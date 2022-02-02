/*******************************************************************************
 * Copyright (c) 2012-2019 Thomas Haber
 *
 *
 *******************************************************************************/

#ifdef __cplusplus
extern "C"
{
#endif

#include <pthread.h>
#include <stdio.h>
#include <string.h>
#include <math.h>
#include "flux.h"
#include "fstapi.h"

#ifdef _WIN32
#include <io.h>
#include <fcntl.h>
#endif

// geometry
#define MAX_ENTRY_SIZE 4096 * 16
#define MAX_TRACE_REQUEST_ITEMS (4096*2) // max no of items to request
#define VERSION 1

// trace object
flxTrace trace;
void * fstObject;
unsigned maxSignals;
unsigned maxScopes;
flxid currentScope;

// ######################################################################################################################
// trace signals and scopes
// ######################################################################################################################

static const char *modtypes[] = { "module", "task", "function", "begin", "fork", "generate", "struct", "union", "class",
		"interface", "package", "program", "vhdl_architecture", "vhdl_procedure", "vhdl_function", "vhdl_record",
		"vhdl_process", "vhdl_block", "vhdl_for_generate", "vhdl_if_generate", "vhdl_generate", "vhdl_package" };

static void traceScope(struct fstHierScope* scope) {

	// scope type
	flxtext description = 0;
	if (scope->typ >= FST_ST_MIN && scope->typ <= FST_ST_MAX)
		description = modtypes[scope->typ];

	flxid nextScope = maxSignals + maxScopes;
	flxAddScope(trace, nextScope, currentScope, scope->name, description);
	currentScope = nextScope;
	maxScopes++;
}

static const char *vartypes[] = { "event", "integer", "parameter", "real", "real_parameter", "reg", "supply0",
		"supply1", "time", "tri", "triand", "trior", "trireg", "tri0", "tri1", "wand", "wire", "wor", "port", "sparray",
		"realtime", "string", "bit", "logic", "int", "shortint", "longint", "byte", "enum", "shortreal" };

static void traceVar(struct fstHierVar* var) {
	flxtext description = 0;
	if (var->typ >= FST_VT_MIN && var->typ <= FST_VT_MAX)
		description = vartypes[var->typ];

	flxbyte type = FLX_TYPE_LOGIC;
	switch (var->typ) {
	case FST_VT_VCD_REAL:
	case FST_VT_VCD_REAL_PARAMETER:
		type = FLX_TYPE_FLOAT;
		break;
	case FST_VT_GEN_STRING:
		type = FLX_TYPE_TEXT;
		break;
	}

	// bit num from fsdb
	int scale = var->length, from = 0, to = -1;

	// bit from/to from name
	unsigned nameLength = var->name_length;
	char varname[nameLength];
	strncpy(varname, var->name, nameLength);
	varname[nameLength] = 0;

	flxtext pos, posa = 0, posb, posc;
	pos = (char*) varname;
	while ((pos = strchr(pos + 1, '[')) != 0)
		posa = pos; // last [
	if (posa) {
		posb = strchr(posa, ']');
		posc = strchr(posa, ':');
	}
	if (posa && posb) {
		from = to = atoi(posa + 1);
		if (posc)
			from = atoi(posc + 1);
	}

	// add item
	flxid itemId = var->handle;
	if (type == FLX_TYPE_LOGIC && to >= from && (to + 1 - from) == scale) {
		// strip [..] & trim
		((char*) posa)[0] = 0;
		while (strlen(varname) > 0 && varname[strlen(varname) - 1] == ' ')
			varname[strlen(varname) - 1] = 0;

		// scattered
		if (flxAddScatteredSignal(trace, itemId, currentScope, varname, description, type, 0, from,
				to) == FLX_ERROR_ITEM_ALLREADY_DEFINED) {
			flxAddScatteredSignalReference(trace, itemId, currentScope, varname, description, from, to);
		}
	} else {
		flxtext descriptor = 0;
		if (type == FLX_TYPE_LOGIC && scale > 1) {
			char txt[32];
			sprintf(txt, "<bits=%u>", scale);
			descriptor = txt;
		}
		if (flxAddSignal(trace, itemId, currentScope, varname, description, type,
				descriptor) == FLX_ERROR_ITEM_ALLREADY_DEFINED) {
			flxAddSignalReference(trace, itemId, currentScope, varname, description);
		}
	}

	// remember signal type
	trace->items[itemId - 1].signalType = type;
	trace->items[itemId - 1].signalScale = scale;

}

// ######################################################################################################################
// trace value changes
// ######################################################################################################################

void traceChangeVar(void *user_callback_data_pointer, uint64_t time, fstHandle itemId, const unsigned char *value,
		uint32_t len) {

	flxbyte type= trace->items[itemId - 1].signalType;
	flxuint scale = trace->items[itemId - 1].signalScale;
	switch (type) {
	case FLX_TYPE_LOGIC: {
		int n, conflict = 0;
		for (n = 0; n < len; n++)
			if (value[n] == 'x' || value[n] == 'X') {
				conflict = 1;
				break;
			}
		flxWriteLogicTextAt(trace, itemId, conflict, time, 0, scale > len || len<1?FLX_STATE_0_BITS:value[0], value, len);
	}
		break;
	case FLX_TYPE_FLOAT: {
		double v = atof(value);
		flxWriteFloatAt(trace, itemId, 0, time, 0, &v, 8);
	}
		break;
	case FLX_TYPE_TEXT:
		flxWriteTextAt(trace, itemId, 0, time, 0, value, len);
		break;
	}

	//flxWriteLogicTextAt(trace, facidx, 0, time, 0, FLX_STATE_0_BITS, "11", 2);
}

void traceChange(void *user_callback_data_pointer, uint64_t time, fstHandle facidx, const unsigned char *value) {
	traceChangeVar(user_callback_data_pointer, time, facidx, value, strlen(value));
}

// ######################################################################################################################
// control handler
// ######################################################################################################################

flxresult handleReqScheme(flxbyte command, flxid controlId, flxid messageId, flxid memberId, flxbyte type, void **value,
		flxuint *size, flxuint *opt) {

	if (command == FLX_CONTROL_HANDLE_LEAVE_MESSAGE) {

		// send geometry
		unsigned version = 1;
		unsigned maxTraceItems = MAX_TRACE_REQUEST_ITEMS;
		struct flxMemberValueStruct members[2];
		flxInitMember(members + 0, 0, 0, FLX_STRUCTTYPE_INTEGER, 0);
		flxInitMember(members + 1, 1, 0, FLX_STRUCTTYPE_INTEGER, 0);
		flxSetMember(members + 0, &version, sizeof(unsigned), 0, 1);
		flxSetMember(members + 1, &maxTraceItems, sizeof(unsigned), 0, 1);
		flxWriteControlResult(trace, controlId, messageId, members, 2);
		flxFlush(trace);
	}
	return FLX_OK;
}

flxresult handleReqItems(flxbyte command, flxid controlId, flxid messageId, flxid memberId, flxbyte type, void **value,
		flxuint *size, flxuint *opt) {
	int n;

	if (command == FLX_CONTROL_HANDLE_LEAVE_MESSAGE) {

		// read scopes and vars
		maxScopes = 1;
		currentScope = 0;
		struct fstHier *h;
		fstReaderIterateHierRewind(fstObject);
		while ((h = fstReaderIterateHier(fstObject))) {

			switch (h->htyp) {
			case FST_HT_SCOPE:
				traceScope(&h->u.scope);
				break;
			case FST_HT_UPSCOPE:
				currentScope = trace->items[currentScope - 1].parentId;
				break;
			case FST_HT_VAR:
				traceVar(&h->u.var);
				break;
			}
		}

		// domain base
		flxdomain start = fstReaderGetStartTime(fstObject);
		flxdomain end = fstReaderGetEndTime(fstObject);
		flxtext domainBase = "ns";
		signed char timescale = fstReaderGetTimescale(fstObject);
		switch (timescale) {
		case 2:
			domainBase = "s100";
			break;
		case 1:
			domainBase = "s10";
			break;
		case 0:
			domainBase = "s";
			break;

		case -1:
			domainBase = "ms100";
			break;
		case -2:
			domainBase = "ms10";
			break;
		case -3:
			domainBase = "ms";
			break;

		case -4:
			domainBase = "us100";
			break;
		case -5:
			domainBase = "us10";
			break;
		case -6:
			domainBase = "us";
			break;

		case -7:
			domainBase = "ns100";
			break;
		case -8:
			domainBase = "ns10";
			break;
		case -9:
			domainBase = "ns";
			break;

		case -10:
			domainBase = "ps100";
			break;
		case -11:
			domainBase = "ps10";
			break;
		case -12:
			domainBase = "ps";
			break;

		case -13:
			domainBase = "fs100";
			break;
		case -14:
			domainBase = "fs10";
			break;
		case -15:
			domainBase = "fs";
			break;

		case -16:
			domainBase = "as100";
			break;
		case -17:
			domainBase = "as10";
			break;
		case -18:
			domainBase = "as";
			break;
		}

		// send open and close to notify about domain
		flxOpen(trace, 0, domainBase, start, 0);
		flxClose(trace, 0, end);

		// write result message & flush
		flxWriteControlResult(trace, controlId, messageId, 0, 0);
		flxFlush(trace);
	}

	return FLX_OK;
}

flxresult handleReqTrace(flxbyte command, flxid controlId, flxid messageId, flxid memberId, flxbyte type, void **value,
		flxuint *size, flxuint *opt) {

	static flxuint itemIds[MAX_TRACE_REQUEST_ITEMS];
	static flxuint count = 0;
	flxbint pos = 0;
	flxuint val = 0;

	// item ids as binary parameter
	FLX_CONTROL_HANDLE_BINARY_PARAMETER(0, bItemIds, MAX_ENTRY_SIZE)
	FLX_CONTROL_HANDLE_ENUM_PARAMETER(0, moreToCome, 0)

	if (command == FLX_CONTROL_HANDLE_LEAVE_MESSAGE) {

		// extract itemIds
		while (pos < bItemIdsSize) {
			val = 0;
			pos += _plusread(&val, bItemIds + pos, bItemIds + bItemIdsSize);
			if (val != 0 && count < MAX_TRACE_REQUEST_ITEMS) {
				itemIds[count++] = val;
			}
		}
		if (moreToCome)
			return FLX_OK;

		// psoido open
		trace->open = FLX_ITEM_OPEN_LOCAL;
		trace->current = 0;

		// iterate changes
		fstReaderClrFacProcessMaskAll(fstObject);
		for (pos = 0; pos < count; pos++)
			if (flxIsSignal(trace, itemIds[pos]))
				fstReaderSetFacProcessMask(fstObject, itemIds[pos]);
		fstReaderIterBlocks2(fstObject, traceChange, traceChangeVar, 0, 0);

		// write result message & flush
		flxWriteControlResult(trace, controlId, messageId, 0, 0);
		flxFlush(trace);

		// reset count
		count = 0;

	}
	return FLX_OK;
}

flxresult handleCommands(flxbyte command, flxid controlId, flxid messageId, flxid memberId, flxbyte type, void **value,
		flxuint *size, flxuint *opt) {

	switch (controlId) {
	case FLX_CONTROL_DB_REQ_SCHEME:
		return handleReqScheme(command, controlId, messageId, memberId, type, value, size, opt);
	case FLX_CONTROL_DB_REQ_ITEMS:
		return handleReqItems(command, controlId, messageId, memberId, type, value, size, opt);
	case FLX_CONTROL_DB_REQ_TRACE:
		return handleReqTrace(command, controlId, messageId, memberId, type, value, size, opt);
	}
	return FLX_ERROR_COMMAND_PARSE_ERROR;
}

// ######################################################################################################################
// main
// ######################################################################################################################

int main(int argc, char **argv) {

#ifdef _WIN32
	setmode(fileno(stdout),O_BINARY);
	setmode(fileno(stdin),O_BINARY);
#endif

	// ######################################################################################################################
	// open fst

	fstObject = fstReaderOpen(argv[1]);
	if (!fstObject) {
		fprintf(stderr, "Could not open: %s \n", argv[1]);
		exit(20);
	}

	// ######################################################################################################################
	// detect geometry

	maxSignals = fstReaderGetMaxHandle(fstObject) + 1;
	maxScopes = 1;
	struct fstHier *h;
	while ((h = fstReaderIterateHier(fstObject))) {

		switch (h->htyp) {
		case FST_HT_SCOPE:
			maxScopes++;
			break;
		}
	}

	// ######################################################################################################################
	// trace & buffers

	// calculate required memory for trace and buffers
	unsigned bufferSize = FLX_BUFFER_BYTES(4096 * 16);
	unsigned traceSize = FLX_TRACE_BYTES(1, maxSignals + maxScopes);

	// trace memory
	unsigned char *memoryBuffer=(unsigned char *)malloc(bufferSize);
	unsigned char *memoryTrace=(unsigned char *)malloc(traceSize);

	// buffer
	flxBuffer buffer1 = flxCreateFixedBuffer(memoryBuffer, bufferSize, flxWriteToFile, stdout);
	//flxBuffer buffer2 = flxCreateFixedBuffer(memoryBuffer + bufferSize, bufferSize, flxCompressFlz, buffer1);

	// trace
	trace = flxCreateTrace(0, maxSignals + maxScopes, MAX_ENTRY_SIZE, memoryTrace, traceSize, buffer1);

	// ######################################################################################################################
	// send head and content

	flxAddHead(trace, argv[1], "fst");
	flxFlush(trace);

	//flxSetBuffer(trace, buffer2);

	//parse input
	return flxParseControlInput(stdin, MAX_ENTRY_SIZE, handleCommands);
}

#ifdef __cplusplus
}
#endif

