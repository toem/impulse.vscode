/*******************************************************************************
 * Copyright (c) 2012-2017 toem
 *
 *
 *******************************************************************************/

#ifdef NOVAS_FSDB
#undef NOVAS_FSDB
#endif

#include "ffrAPI.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <stdint.h>
#include <unistd.h>
#include <stdarg.h>
#include <errno.h>
#include <map>
#include <time.h>
#include "flux.h"

#ifdef _WIN32
#include <io.h>
#include <fcntl.h>
#endif

// geometry
#define MAX_ENTRY_SIZE 4096 * 16
#define MAX_TRACE_REQUEST_ITEMS (4096*2) // max no of items to request
#define VERSION 1

// trace object
ffrObject * fsdbObj;

flxTrace trace;
unsigned maxSignals;
unsigned maxScopes;
flxid currentScope;

int64_t timespecDiff(struct timespec *timeA_p, struct timespec *timeB_p) {
	return ((timeA_p->tv_sec * 1000000000) + timeA_p->tv_nsec) - ((timeB_p->tv_sec * 1000000000) + timeB_p->tv_nsec);
}

// ######################################################################################################################
// trace signals and scopes
// ######################################################################################################################

static void traceScopeBase(flxtext name, flxtext description) {
	flxid nextScope = maxSignals + maxScopes;
	flxAddScope(trace, nextScope, currentScope, name, description);
	currentScope = nextScope;
	maxScopes++;
}

static void traceStruct(fsdbTreeCBDataStructBegin* structure, void *user) {
	traceScopeBase(structure->name, "struct");
}
//static void traceArray(fsdbTreeCBDataArrayBegin* array, void *user) {
//}

static void traceScope(fsdbTreeCBDataScope* scope, void *user) {
	flxtext description;

	switch (scope->type) {
	case FSDB_ST_VCD_MODULE:
		description = (flxtext) "module";
		break;

	case FSDB_ST_VCD_TASK:
		description = (flxtext) "task";
		break;

	case FSDB_ST_VCD_FUNCTION:
		description = (flxtext) "function";
		break;

	case FSDB_ST_VCD_BEGIN:
		description = (flxtext) "begin";
		break;

	case FSDB_ST_VCD_FORK:
		description = (flxtext) "fork";
		break;

	case FSDB_ST_VHDL_ARCHITECTURE:
		description = (flxtext) "vhdl_architecture";
		break;

	case FSDB_ST_VHDL_PROCEDURE:
		description = (flxtext) "vhdl_procedure";
		break;

	case FSDB_ST_VHDL_FUNCTION:
		description = (flxtext) "vhdl_function";
		break;

	case FSDB_ST_VHDL_RECORD:
		description = (flxtext) "vhdl_record";
		break;

	case FSDB_ST_VHDL_PROCESS:
		description = (flxtext) "vhdl_process";
		break;

	case FSDB_ST_VHDL_BLOCK:
		description = (flxtext) "vhdl_block";
		break;

	case FSDB_ST_VHDL_FOR_GENERATE:
		description = (flxtext) "vhdl_for_generate";
		break;

	case FSDB_ST_VHDL_IF_GENERATE:
		description = (flxtext) "vhdl_if_generate";
		break;

	default:
		description = (flxtext) "unknown_scope_type";
		break;
	}

	traceScopeBase(scope->name, description);
}

static void traceVar(fsdbTreeCBDataVar *var, void *user) {

	flxtext description;
	flxbyte type = FLX_TYPE_LOGIC;

	switch (var->type) {
	case FSDB_VT_VCD_EVENT:
		description = (flxtext) "event";
		break;

	case FSDB_VT_VCD_INTEGER:
		description = (flxtext) "integer";
		break;

	case FSDB_VT_VCD_PARAMETER:
		description = (flxtext) "parameter";
		break;

	case FSDB_VT_VCD_REAL:
		type = FLX_TYPE_FLOAT;
		description = (flxtext) "real";
		break;

	case FSDB_VT_VCD_REG:
		description = (flxtext) "reg";
		break;

	case FSDB_VT_VCD_SUPPLY0:
		description = (flxtext) "supply0";
		break;

	case FSDB_VT_VCD_SUPPLY1:
		description = (flxtext) "supply1";
		break;

	case FSDB_VT_VCD_TIME:
		description = (flxtext) "time";
		break;

	case FSDB_VT_VCD_TRI:
		description = (flxtext) "tri";
		break;

	case FSDB_VT_VCD_TRIAND:
		description = (flxtext) "triand";
		break;

	case FSDB_VT_VCD_TRIOR:
		description = (flxtext) "trior";
		break;

	case FSDB_VT_VCD_TRIREG:
		description = (flxtext) "trireg";
		break;

	case FSDB_VT_VCD_TRI0:
		description = (flxtext) "tri0";
		break;

	case FSDB_VT_VCD_TRI1:
		description = (flxtext) "tri1";
		break;

	case FSDB_VT_VCD_WAND:
		description = (flxtext) "wand";
		break;

	case FSDB_VT_VCD_WIRE:
		description = (flxtext) "wire";
		break;

	case FSDB_VT_VCD_WOR:
		description = (flxtext) "wor";
		break;

	case FSDB_VT_VCD_MEMORY:
		description = (flxtext) "vcd_memory";
		break;

	case FSDB_VT_VCD_MEMORY_DEPTH:
		description = (flxtext) "vcd_memory_depth_or_range";
		break;

	case FSDB_VT_VHDL_SIGNAL:
		description = (flxtext) "vhdl_signal";
		break;

	case FSDB_VT_VHDL_VARIABLE:
		description = (flxtext) "vhdl_variable";
		break;

	case FSDB_VT_VHDL_CONSTANT:
		description = (flxtext) "vhdl_constant";
		break;

	case FSDB_VT_VHDL_FILE:
		description = (flxtext) "vhdl_file";
		break;

	case FSDB_VT_VHDL_MEMORY:
		description = (flxtext) "vhdl_memory";
		break;

	case FSDB_VT_VHDL_MEMORY_DEPTH:
		description = (flxtext) "vhdl_memory_depth";
		break;

	default:
		description = (flxtext) "unknown_var_type";
		break;
	}

	switch (var->vc_dt) {
	case FSDB_VC_DT_BYTE:
	case FSDB_VC_DT_SHORT:
	case FSDB_VC_DT_INT:
	case FSDB_VC_DT_LONG:
		type = FLX_TYPE_INTEGER;
		break;
	case FSDB_VC_DT_FLOAT:
	case FSDB_VC_DT_DOUBLE:
		type = FLX_TYPE_FLOAT;
		break;
	}

	// bit num from fsdb
	int scale = 0, from = 0, to = -1;
	if (var->lbitnum >= var->rbitnum) {
		scale = var->lbitnum - var->rbitnum + 1;
	} else {
		scale = var->rbitnum - var->lbitnum + 1;
	}

	// bit from/to from name
	unsigned nameLength = strlen(var->name);
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

	//char txt2[32];
	//sprintf(txt2, "l%ur%u<bits=%u>",var->lbitnum,var->rbitnum, scale);
	//description = txt2;

	// add item
	flxid itemId = var->u.idcode;
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

static bool_T traceTreeItem(fsdbTreeCBType cbType, void *clientData, void *treeCbData) {
	switch (cbType) {
	case FSDB_TREE_CBT_BEGIN_TREE:
		break;

	case FSDB_TREE_CBT_SCOPE:
		traceScope((fsdbTreeCBDataScope *) treeCbData, 0);
		break;

	case FSDB_TREE_CBT_STRUCT_BEGIN:
		traceStruct((fsdbTreeCBDataStructBegin *) treeCbData, 0);
		break;

	case FSDB_TREE_CBT_ARRAY_BEGIN:
		//traceArray((fsdbTreeCBDataArrayBegin *) treeCbData, 0);
		break;

	case FSDB_TREE_CBT_VAR:
		traceVar((fsdbTreeCBDataVar *) treeCbData, 0);
		break;

	case FSDB_TREE_CBT_UPSCOPE:
	case FSDB_TREE_CBT_STRUCT_END:
		currentScope = trace->items[currentScope - 1].parentId;
		break;

	case FSDB_TREE_CBT_END_TREE:
	case FSDB_TREE_CBT_ARRAY_END:
		break;

	case FSDB_TREE_CBT_FILE_TYPE:
	case FSDB_TREE_CBT_SIMULATOR_VERSION:
	case FSDB_TREE_CBT_SIMULATION_DATE:
	case FSDB_TREE_CBT_X_AXIS_SCALE:
	case FSDB_TREE_CBT_END_ALL_TREE:
	case FSDB_TREE_CBT_RECORD_BEGIN:
	case FSDB_TREE_CBT_RECORD_END:
		break;

	default:
		return 0;
	}
	return 1;
}

static bool_T scopeCountCallback(fsdbTreeCBType cbType, void *clientData, void *treeCbData) {
	printf("%u\n", cbType);
	switch (cbType) {
	case FSDB_TREE_CBT_SCOPE:
	case FSDB_TREE_CBT_STRUCT_BEGIN:
		//case FSDB_TREE_CBT_ARRAY_BEGIN:
		maxScopes++;
		break;
	}
	return 1;
}

// ######################################################################################################################
// trace value changes
// ######################################################################################################################

static void traceValueChange(int itemId, /*ffrVCTrvsHdl*/
ffrTimeBasedVCTrvsHdl vcTrvsHdl, byte_T *vc, flxdomain time) {
	unsigned n;

	fsdbVarType varType = vcTrvsHdl->ffrGetVarType();
	int bs;
	static flxbyte buffer[FSDB_MAX_BIT_SIZE + 1 + 32];
	flxbptr bufferp = buffer;
	int conflict = 0;
	flxbyte type = trace->items[itemId - 1].signalType;
	flxuint scale = trace->items[itemId - 1].signalScale;

	// VERILOG
	if (varType >= FSDB_VT_VCD_EVENT && varType <= FSDB_VT_VCD_REG2) {

		switch (vcTrvsHdl->ffrGetBytesPerBit()) {
		case FSDB_BYTES_PER_BIT_1B:
			bs = vcTrvsHdl->ffrGetBitSize();
			for (n = 0; n < bs; n++) {
				switch (vc[n]) {
				case FSDB_BT_VCD_0:
					bufferp[n] = FLX_STATE_0_BITS;
					break;

				case FSDB_BT_VCD_1:
					bufferp[n] = FLX_STATE_1_BITS;
					break;

				case FSDB_BT_VCD_X:
					bufferp[n] = FLX_STATE_X_BITS;
					conflict = 1;
					break;

				case FSDB_BT_VCD_Z:
					bufferp[n] = FLX_STATE_Z_BITS;
					break;

				default:
					bufferp[n] = FLX_STATE_X_BITS;
					conflict = 1;
					break;
				}
			}
			flxWriteLogicStatesAt(trace, itemId, conflict, time, 0, scale > bs || bs < 1 ? FLX_STATE_0_BITS : buffer[0],
					buffer, bs);
			break;

		case FSDB_BYTES_PER_BIT_2B:

			break;

		case FSDB_BYTES_PER_BIT_4B:

			switch (varType) {
			case FSDB_VT_VCD_MEMORY_DEPTH:
			case FSDB_VT_VHDL_MEMORY_DEPTH:
				break;

			default:
				flxWriteFloatAt(trace, itemId, 0, time, 0, vc, 4);
				break;
			}
			break;

		case FSDB_BYTES_PER_BIT_8B:
			varType = vcTrvsHdl->ffrGetVarType();
			switch (varType) {
			case FSDB_VT_VCD_REAL:
				flxWriteFloatAt(trace, itemId, 0, time, 0, vc, 4);
				break;

			case FSDB_VT_STREAM:
			default:
				break;
			}
			break;

		}
	} else
	// VHDL
	{
		switch (vcTrvsHdl->ffrGetBytesPerBit()) {
		case FSDB_BYTES_PER_BIT_1B:
			bs = vcTrvsHdl->ffrGetBitSize();
			for (n = 0; n < bs; n++) {
				switch (vc[n]) {
				case FSDB_BT_VHDL_STD_ULOGIC_U:
					bufferp[n] = FLX_STATE_U_BITS;

					//conflict = 1; // remove this
					//bufferp[n] = FLX_STATE_X_BITS;
					break;

				case FSDB_BT_VHDL_STD_ULOGIC_X:
					conflict = 1;
					bufferp[n] = FLX_STATE_X_BITS;
					break;

				case FSDB_BT_VHDL_STD_ULOGIC_0:
					bufferp[n] = FLX_STATE_0_BITS;
					break;

				case FSDB_BT_VHDL_STD_ULOGIC_1:
					bufferp[n] = FLX_STATE_1_BITS;
					break;

				case FSDB_BT_VHDL_STD_ULOGIC_Z:
					bufferp[n] = FLX_STATE_Z_BITS;
					break;

				case FSDB_BT_VHDL_STD_ULOGIC_W:
					bufferp[n] = FLX_STATE_W_BITS;
					break;

				case FSDB_BT_VHDL_STD_ULOGIC_L:
					bufferp[n] = FLX_STATE_L_BITS;
					break;

				case FSDB_BT_VHDL_STD_ULOGIC_H:
					bufferp[n] = FLX_STATE_H_BITS;
					break;

				case FSDB_BT_VHDL_STD_ULOGIC_DASH:
					bufferp[n] = FLX_STATE_D_BITS;
					break;

				default:
					bufferp[n] = FLX_STATE_U_BITS;
					conflict = 1;
					break;
				}
			}
			flxWriteLogicStatesAt(trace, itemId, conflict, time, 0, scale > bs || bs < 1 ? FLX_STATE_0_BITS : buffer[0],
					buffer, bs);
			break;

		case FSDB_BYTES_PER_BIT_2B:

			break;

		case FSDB_BYTES_PER_BIT_4B:

			switch (varType) {
			case FSDB_VT_VCD_MEMORY_DEPTH:
			case FSDB_VT_VHDL_MEMORY_DEPTH:
				break;

			default:
				flxWriteFloatAt(trace, itemId, 0, time, 0, vc, 4);
				break;
			}
			break;

		case FSDB_BYTES_PER_BIT_8B:
			varType = vcTrvsHdl->ffrGetVarType();
			switch (varType) {
			case FSDB_VT_VCD_REAL:
				flxWriteFloatAt(trace, itemId, 0, time, 0, vc, 4);
				break;

			case FSDB_VT_STREAM:
			default:
				break;
			}
			break;

		}

	}

	return;
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
		fsdbObj->ffrSetTreeCBFunc(traceTreeItem, 0);
		fsdbObj->ffrReadScopeVarTree();

		// domain base
		char domainBase[16] = "s\0";
		uint_T digit;
		char *unit;
		fsdbRC rc = fsdbObj->ffrExtractScaleUnit(fsdbObj->ffrGetScaleUnit(), digit, unit);
		if (rc == FSDB_RC_SUCCESS) {
			sprintf(domainBase, "%s", unit);
			if (digit > 1)
				sprintf(domainBase + strlen(domainBase), "%u", digit);
		}

		// start / ned
		fsdbTag64 time;
		fsdbObj->ffrGetMinFsdbTag64(&time);
		flxdomain start = (((uint64_t) time.H << 32) | ((uint64_t) time.L));
		fsdbObj->ffrGetMaxFsdbTag64(&time);
		flxdomain end = (((uint64_t) time.H << 32) | ((uint64_t) time.L));

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

		fsdbTag64 time;
		byte_T *vc;
		fsdbVarIdcode sigArray[MAX_TRACE_REQUEST_ITEMS];
		fsdbVarIdcode currentId;
		int readableSignals = 0;

		// psoido open
		trace->open = FLX_ITEM_OPEN_LOCAL;
		trace->current = 0;

		// load all signals
		fsdbObj->ffrUnloadSignals();
		fsdbObj->ffrResetSignalList();
		for (pos = 0; pos < count; pos++) {
			fsdbObj->ffrAddToSignalList(itemIds[pos]);
		}
		fsdbObj->ffrLoadSignals();

		// traverse handler
		for (pos = 0; pos < count; pos++)
			if (flxIsSignal(trace, itemIds[pos]))
				sigArray[readableSignals++] = itemIds[pos];
		ffrTimeBasedVCTrvsHdl vcTrvsHdl = fsdbObj->ffrCreateTimeBasedVCTrvsHdl(readableSignals, sigArray);
		if (vcTrvsHdl && FSDB_RC_SUCCESS == vcTrvsHdl->ffrGetVC(&vc)) {
			vcTrvsHdl->ffrGetXTag((void*) &time);
			vcTrvsHdl->ffrGetVarIdcode(&currentId);
			traceValueChange(currentId, vcTrvsHdl, vc, (((uint64_t) time.H << 32) | ((uint64_t) time.L)));
		}
		while (vcTrvsHdl && FSDB_RC_SUCCESS == vcTrvsHdl->ffrGotoNextVC()) {
			vcTrvsHdl->ffrGetVC(&vc);
			vcTrvsHdl->ffrGetXTag((void*) &time);
			vcTrvsHdl->ffrGetVarIdcode(&currentId);
			traceValueChange(currentId, vcTrvsHdl, vc, (((uint64_t) time.H << 32) | ((uint64_t) time.L)));
		}

		// unload all signals
		fsdbObj->ffrUnloadSignals();

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

	ffrFSDBInfo fsdbInfo;
	fsdbFileType fsdbFileType;
	unsigned fsdbBlkIdx = 0;
	int n;
	struct timespec t1, t2;
	clock_gettime(CLOCK_MONOTONIC, &t1);

#ifdef _WIN32
	setmode(fileno(stdout),O_BINARY);
	setmode(fileno(stdin),O_BINARY);
#endif

// ######################################################################################################################
// open fsdb

	// check if output is fsdb
	if (!ffrObject::ffrIsFSDB(argv[1])) {
		fprintf(stderr, "Input is no FSDB file: %s \n", argv[1]);
		exit(1);
	}

	// check fsdb info type
	ffrObject::ffrGetFSDBInfo(argv[1], fsdbInfo);
	if ((fsdbInfo.file_type != FSDB_FT_VERILOG) && (fsdbInfo.file_type != FSDB_FT_VERILOG_VHDL)
			&& (fsdbInfo.file_type != FSDB_FT_VHDL)) {
		fprintf(stderr, "Invalid fsdb info type : %u \n", fsdbInfo.file_type);
		exit(2);
	}

	// open fsdb
	fsdbObj = ffrObject::ffrOpen3(argv[1]);
	if (!fsdbObj) {
		fprintf(stderr, "Could not open file: %s \n", argv[1]);
		exit(3);
	}

	// check fsdb file type
	fsdbFileType = fsdbObj->ffrGetFileType();
	if ((fsdbFileType != FSDB_FT_VERILOG) && (fsdbFileType != FSDB_FT_VERILOG_VHDL) && (fsdbFileType != FSDB_FT_VHDL)) {
		fsdbObj->ffrClose();
		fprintf(stderr, "Invalid fsdb file type : %u \n", fsdbFileType);
		exit(4);
	}

	fsdbObj->ffrReadDataTypeDefByBlkIdx(fsdbBlkIdx); /* necessary if FSDB file has transaction data ... we don't process this but it prevents possible crashes */

// ######################################################################################################################
// detect fsdb geometry

	maxSignals = fsdbObj->ffrGetMaxVarIdcode();
	maxScopes = 1;
	fsdbObj->ffrSetTreeCBFunc(scopeCountCallback, 0);
	fsdbObj->ffrReadScopeVarTree();

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

	flxAddHead(trace, argv[1], "fsdb");
	flxFlush(trace);

	//flxSetBuffer(trace, buffer2);

	//parse input
	return flxParseControlInput(stdin, MAX_ENTRY_SIZE, handleCommands);
}
