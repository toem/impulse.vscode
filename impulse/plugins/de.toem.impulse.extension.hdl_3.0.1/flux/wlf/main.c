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
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include "flux.h"
#include "wlf_api.h"

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
WlfFileId wlfFile;
WlfSymbolId wlfTop;
WlfPackId wlfPack;
unsigned maxSignals;
unsigned maxScopes;

// ######################################################################################################################
// counts signals and scopes
// ######################################################################################################################

static void countItems(WlfSymbolId topId)
{
    WlfIterId iter;
    WlfSymbolId symbolId;
    int subCnt;

    /* create an iterator to retrieve children of top */
    iter = wlfSymChildren64(topId, wlfSelAll);
    if(iter == NULL) return;

    /* iterate through the children */
    while ((symbolId = wlfIterate(iter)) != NULL)
    {

        //printf("%s %s symtype:%d subcnt:%d typId:%d typtyp:%d left:%d right:%d length:%d arch:%d\n",wlfSymPropString(symbolId, WLF_PROP_SYMBOL_NAME),wlfSymPropString(symbolId, WLF_PROP_SYMBOL_ABSOLUTE_PATH),wlfSymPropSymbolSel64(symbolId, WLF_PROP_SYMBOL_TYPE), wlfSymPropInt(symbolId, WLF_PROP_SUBELEMENT_COUNT),wlfSymPropTypeId(symbolId, WLF_PROP_TYPE_ID),wlfTypePropDataType(wlfSymPropTypeId(symbolId, WLF_PROP_TYPE_ID), WLF_TYPE_TYPE),wlfTypePropInt(wlfSymPropTypeId(symbolId, WLF_PROP_TYPE_ID), WLF_TYPE_ARRAY_LEFT),wlfTypePropInt(wlfSymPropTypeId(symbolId, WLF_PROP_TYPE_ID), WLF_TYPE_ARRAY_RIGHT),wlfTypePropInt(wlfSymPropTypeId(symbolId, WLF_PROP_TYPE_ID), WLF_TYPE_ARRAY_LENGTH), wlfSymPropInt(symbolId, WLF_PROP_ARCHIVE_NUMBER));

        subCnt = wlfSymPropInt(symbolId, WLF_PROP_SUBELEMENT_COUNT);
        WlfSymbolSel64 symbolTyp = wlfSymPropSymbolSel64(symbolId, WLF_PROP_SYMBOL_TYPE);

        if(symbolTyp & (wlfSelVhdlScopes | wlfSelVlogScopes))
        {
            maxScopes++;
        }

        /* recurse through the children, but block out bitblasted children */
        if(subCnt<=0)
        {
            countItems(symbolId);
        }
    }

    wlfIteratorDestroy(iter);
}

// ######################################################################################################################
// trace signals and scopes
// ######################################################################################################################

static flxid traceScope(WlfSymbolId scopeId,flxid currentScope)
{

    // scope type
    flxtext description = 0;
    flxtext name;
    WlfSymbolSel64 symbolTyp = wlfSymPropSymbolSel64(scopeId, WLF_PROP_SYMBOL_TYPE);
    switch(symbolTyp)
    {
        /* wlfSelVhdlScopes */
        case wlfSelArchitecture: description = "architecture"; break;
        case wlfSelBlock: description = "block"; break;
        case wlfSelGenerate: description = "generate"; break;
        case wlfSelPackage: description = "package"; break;
        case wlfSelSubprogram: description = "subprogram"; break;
        case wlfSelForeign: description = "foreign"; break;
        /* wlfSelVlogScopes */
        case wlfSelModule: description = "module"; break;
        case wlfSelTask: description = "task"; break;
        /* case wlfSelBlock: (same as VHDL's) */
        case wlfSelFunction: description = "function"; break;
        case wlfSelStatement: description = "statement"; break;
        case wlfSelSVCovergroup: description = "covergroup"; break;
        case wlfSelSVCoverpoint: description = "coverpoint"; break;
        case wlfSelSVCross: description = "cross"; break;
        case wlfSelSVClass: description = "class"; break;
        case wlfSelSVParamClass: description = "paramclass"; break;
        case wlfSelSVInterface: description = "interface"; break;
        case wlfSelVlPackage: description = "package"; break;
        case wlfSelVlGenerateBlock: description = "generate"; break;
        case wlfSelAssertionScope: description = "assertionscope"; break;
        case wlfSelClockingBlock: description = "clockingblock"; break;
        case wlfSelVlTypedef: description = "typedef"; break;
    }

    name = wlfSymPropString(scopeId, WLF_PROP_SYMBOL_NAME);
    flxid nextScope = maxSignals + maxScopes;
    flxAddScope(trace, nextScope, currentScope, name, description);
    maxScopes++;
    return nextScope;
}

static void traceVar(WlfSymbolId varId,flxid currentScope)
{
    flxtext description = 0;
    WlfSymbolSel64 symbolTyp = wlfSymPropSymbolSel64(varId, WLF_PROP_SYMBOL_TYPE);
    int arch = wlfSymPropInt(varId, WLF_PROP_ARCHIVE_NUMBER);
    if (arch <1)
    return;
    flxtext name = wlfSymPropString(varId, WLF_PROP_SYMBOL_NAME);

    switch(symbolTyp)
    {
        case wlfSelParameter: description = "parameter"; break;
        case wlfSelReg: description = "reg"; break;
        case wlfSelInteger: description = "integer"; break;
        case wlfSelTime: description = "time"; break;
        case wlfSelReal: description = "real"; break;
        case wlfSelSpecparam:description = "specparam"; break;
        case wlfSelMemory: description = "memory"; break;
        case wlfSelNamedEvent: description = "event"; break;
        case wlfSelSignal:description = "signal"; break;
        case wlfSelNet: description = "net"; break;
        case wlfSelVariable:description = "var"; break;
        case wlfSelConstant:description = "constant"; break;
        case wlfSelGeneric:description = "generic"; break;
        case wlfSelAlias:description = "alias"; break;

        default: break;
    }

    WlfTypeId typeId = wlfSymPropTypeId(varId, WLF_PROP_TYPE_ID);
    WlfDataType dataType = wlfTypePropDataType(typeId, WLF_TYPE_TYPE);
    flxbyte type = FLX_TYPE_LOGIC;
    switch(dataType)
    {

        case wlfTypeScalar :
        case wlfTypeArray :
        case wlfTypeRecord :
        case wlfTypeEnum :
        case wlfTypeInteger :
        case wlfTypePhysical :

        case wlfTypeAccess :
        case wlfTypeFile :
        case wlfTypeTime :

        case wlfTypeReg :
        case wlfTypeNet :
        case wlfTypeBit :
        case wlfTypeMemElem :
        case wlfTypeMemBits :
        case wlfTypeUnknown :
        case wlfTypeEvent :
        case wlfTypeClassRef :
        break;
        case wlfTypeVlogReal :
        case wlfTypeReal :
        type = FLX_TYPE_FLOAT;
        break;

        case wlfTypeString :
        type = FLX_TYPE_TEXT;
        break;

    }

    int rgh = wlfTypePropInt(typeId, WLF_TYPE_ARRAY_RIGHT);
    int lft = wlfTypePropInt(typeId, WLF_TYPE_ARRAY_LEFT);
    int len = wlfTypePropInt(typeId, WLF_TYPE_ARRAY_LENGTH);

// bit num from fsdb
    int scale = len, from = 0, to = -1;

// bit from/to from name

    unsigned nameLength = strlen(name);
    char varname[nameLength];
    strncpy(varname, name, nameLength);
    varname[nameLength]=0;

    flxtext pos, posa=0, posb, posc;
    pos = (char*) varname;
    while ((pos = strchr(pos + 1, '[')) != 0)
    posa = pos;// last [
    if (posa)
    {
        posb = strchr(posa, ']');
        posc = strchr(posa, ':');
    }
    if (posa && posb)
    {
        from = to = atoi(posa + 1);
        if (posc)
        from = atoi(posc + 1);
    }

// add item
    flxid itemId = arch;
    if (type == FLX_TYPE_LOGIC && to >= from && (to + 1 - from) == scale)
    {
// strip [..] & trim
        ((char*)posa)[0] = 0;
        posa--;
        if (*posa ==' ')
        ((char*)posa)[0] = 0;
        posa--;
        if (*posa ==' ')
        ((char*)posa)[0] = 0;

// scattered
        if (flxAddScatteredSignal(trace, itemId, currentScope, varname, description, type, 0, from,
                        to) == FLX_ERROR_ITEM_ALLREADY_DEFINED)
        {
            flxAddScatteredSignalReference(trace, itemId, currentScope, varname, description, from, to);
        }
    }
    else
    {
        flxtext descriptor = "";
        if (type == FLX_TYPE_LOGIC && scale > 1)
        {
            char txt[32];
            sprintf(txt, "default<bits=%u>", scale);
            descriptor = txt;
        }

        if (flxAddSignal(trace, itemId, currentScope, varname, description, type,
                        descriptor) == FLX_ERROR_ITEM_ALLREADY_DEFINED)
        {
            flxAddSignalReference(trace, itemId, currentScope, varname, description);
        }
    }

// remember signal type
    trace->items[itemId - 1].signalType = type;
    trace->items[itemId - 1].signalScale = len;
    trace->items[itemId - 1].varId = varId;
}
static void traceHierarchy(WlfSymbolId topId,flxid currentScope)
{
    WlfIterId iter;
    WlfSymbolId symbolId;
    int subCnt;

    /* create an iterator to retrieve children of top */
    iter = wlfSymChildren64(topId, wlfSelAll);
    if(iter == NULL) return;

    /* iterate through the children */
    while ((symbolId = wlfIterate(iter)) != NULL)
    {
        flxid nextScope=currentScope;
        subCnt = wlfSymPropInt(symbolId, WLF_PROP_SUBELEMENT_COUNT);
        WlfSymbolSel64 symbolTyp = wlfSymPropSymbolSel64(symbolId, WLF_PROP_SYMBOL_TYPE);

        if(symbolTyp & (wlfSelVhdlScopes | wlfSelVlogScopes))
        {
            nextScope = traceScope(symbolId,currentScope);
        }
        else
        {
            traceVar(symbolId,currentScope);
        }

        /* recurse through the children, but block out bitblasted children */
        if(subCnt<=0)
        {
            traceHierarchy(symbolId,nextScope);
        }
    }

    wlfIteratorDestroy(iter);
}

// ######################################################################################################################
// trace value changes
// ######################################################################################################################

static WlfCallbackResponse traceChange(void* user, WlfCallbackReason reason)
{
    flxid itemId = (flxid)user;
	flxbyte type= trace->items[itemId - 1].signalType;
	flxuint scale = trace->items[itemId - 1].signalScale;
    WlfValueId valId = (WlfValueId)trace->items[itemId - 1].value;

    if (reason == WLF_ENDLOG || valId == 0)
    return (WLF_CONTINUE_SCAN);

    // tine
    WlfTime64 time;
    wlfPackTime(wlfPack, &time);

    //flxWriteLogicTextAt(trace, itemId, 0, time, 0, FLX_STATE_0_BITS, "1", 1);

    // value
    char * value = wlfValueToString(valId, WLF_RADIX_BINARY, 0);
    unsigned  len = value != 0?strlen(value):0;   //// ??????????????????????
    switch(type)
    {
        case FLX_TYPE_LOGIC:
        {
            int n,conflict = 0;
            for (n =0;n<len;n++)
            if (value[n] == 'X' || value[n] == 'x')
            conflict = 1;
            flxWriteLogicTextAt(trace, itemId, conflict, time, 0, scale > len || len<1?FLX_STATE_0_BITS:value[0], value, len);
        }break;
        case FLX_TYPE_FLOAT:
        {
            double v = atof(value);
            flxWriteFloatAt(trace, itemId, 0, time, 0, &v,8);
        }
        break;
        case FLX_TYPE_TEXT:
        flxWriteTextAt(trace, itemId, 0, time, 0,value, len);
        break;
    }

    return (WLF_CONTINUE_SCAN);
}

// ######################################################################################################################
// control handler
// ######################################################################################################################
flxresult handleReqScheme(flxbyte command,flxid controlId, flxid messageId, flxid memberId, flxbyte type, void **value, flxuint *size,
        flxuint *opt)
{

    if (command == FLX_CONTROL_HANDLE_LEAVE_MESSAGE)
    {

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

flxresult handleReqItems(flxbyte command,flxid controlId, flxid messageId, flxid memberId, flxbyte type, void **value, flxuint *size,
        flxuint *opt)
{
    int n;

    if (command == FLX_CONTROL_HANDLE_LEAVE_MESSAGE)
    {
        // read scopes and vars
        maxScopes = 1;
        traceHierarchy(wlfTop,0);

        // domain base
        flxdomain start=0;
        flxdomain end=0;
        int status;
        WlfFileInfo fileInfo;
        status = wlfFileInfo(wlfFile, &fileInfo);
        if(status == WLF_OK)
        {
            start = fileInfo.startTime;
            end = fileInfo.lastTime+fileInfo.lastDelta;
        }
        int resolution;
        status = wlfFileResolution(wlfFile, &resolution);
        flxtext domainBase = 0;
        if(status == WLF_OK)
        switch(resolution)
        {
            case WLF_TIME_RES_1FS: domainBase = "fs"; break;
            case WLF_TIME_RES_10FS: domainBase = "fs10"; break;
            case WLF_TIME_RES_100FS:domainBase = "fs100"; break;
            case WLF_TIME_RES_1PS: domainBase = "ps"; break;
            case WLF_TIME_RES_10PS: domainBase = "ps10"; break;
            case WLF_TIME_RES_100PS:domainBase = "ps100"; break;
            case WLF_TIME_RES_1NS: domainBase = "ns"; break;
            case WLF_TIME_RES_10NS: domainBase = "ns10"; break;
            case WLF_TIME_RES_100NS:domainBase = "ns100"; break;
            case WLF_TIME_RES_1US: domainBase = "us"; break;
            case WLF_TIME_RES_10US: domainBase = "us10"; break;
            case WLF_TIME_RES_100US:domainBase = "us100"; break;
            case WLF_TIME_RES_1MS: domainBase = "ms"; break;
            case WLF_TIME_RES_10MS: domainBase = "ms10"; break;
            case WLF_TIME_RES_100MS:domainBase = "ms100"; break;
            case WLF_TIME_RES_1SEC: domainBase = "s"; break;
            case WLF_TIME_RES_10SEC:domainBase = "s10"; break;
            case WLF_TIME_RES_100SEC:domainBase ="s100"; break;
            default: domainBase = "ns"; break;
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

flxresult handleReqTrace(flxbyte command, flxid controlId,flxid messageId, flxid memberId, flxbyte type, void **value, flxuint *size,
        flxuint *opt)
{

    static flxuint itemIds[MAX_TRACE_REQUEST_ITEMS];
    static flxuint count = 0;
    flxbint pos = 0;
    flxuint val = 0;

    // item ids as binary parameter
    FLX_CONTROL_HANDLE_BINARY_PARAMETER(0, bItemIds, MAX_ENTRY_SIZE)
    FLX_CONTROL_HANDLE_ENUM_PARAMETER(0, moreToCome, 0)

    if (command == FLX_CONTROL_HANDLE_LEAVE_MESSAGE)
    {

        // extract itemIds
        while (pos < bItemIdsSize)
        {
            val = 0;
            pos += _plusread(&val, bItemIds + pos, bItemIds + bItemIdsSize);
            if (val != 0 && count < MAX_TRACE_REQUEST_ITEMS)
            {
                itemIds[count++] = val;
            }
        }
        if (moreToCome)
        return FLX_OK;

        // psoido open
        trace->open = FLX_ITEM_OPEN_LOCAL;
        trace->current = 0;

        /* Create a callback context  */
        wlfPack = wlfPackCreate();
        if(wlfPack != NULL)
        {
            // info
            WlfFileInfo fileInfo;
            int status = wlfFileInfo(wlfFile, &fileInfo);

            // callbacks
            for (pos = 0; pos < count; pos++)
            {
                flxid itemId = itemIds[pos];
                WlfSymbolId varId = (WlfSymbolId) trace->items[itemId - 1].varId;
                if (wlfSymIsSymbolSelect64(varId, wlfSelAllSignals))
                {
                    WlfValueId val = wlfValueCreate(varId);
                    wlfAddSignalEventCB(wlfPack, varId, val, WLF_REQUEST_POSTPONED, traceChange, (void*)itemId);
                    trace->items[itemId - 1].value = val;
                }
            }

            // iterate changes
            if(status == WLF_OK)
            {
                wlfReadDataOverRange(wlfPack, fileInfo.startTime, fileInfo.startDelta, fileInfo.lastTime, fileInfo.lastDelta, NULL, NULL, NULL);
            }
            // free resources
            wlfPackDestroy(wlfPack);
            for (pos = 0; pos < count; pos++)
            {
                flxid itemId = itemIds[pos];
                WlfSymbolId varId = (WlfSymbolId) trace->items[itemId - 1].varId;
                if (wlfSymIsSymbolSelect64(varId, wlfSelAllSignals))
                {
                    WlfValueId val = (WlfValueId)trace->items[itemId - 1].value;
                    wlfValueDestroy(val);
                    trace->items[itemId - 1].value = 0;
                }
            }
        }

        // write result message & flush
        flxWriteControlResult(trace, controlId, messageId, 0, 0);
        flxFlush(trace);

        // reset count
        count = 0;

    }
    return FLX_OK;
}

flxresult handleCommands(flxbyte command, flxid controlId, flxid messageId, flxid memberId, flxbyte type, void **value,
        flxuint *size, flxuint *opt)
{

    switch (controlId)
    {
        case FLX_CONTROL_DB_REQ_SCHEME:
        return handleReqScheme(command, controlId,messageId, memberId, type, value, size, opt);
        case FLX_CONTROL_DB_REQ_ITEMS:
        return handleReqItems(command, controlId,messageId, memberId, type, value, size, opt);
        case FLX_CONTROL_DB_REQ_TRACE:
        return handleReqTrace(command, controlId,messageId, memberId, type, value, size, opt);
    }
    return FLX_ERROR_COMMAND_PARSE_ERROR;
}

// ######################################################################################################################
// main
// ######################################################################################################################

int main(int argc, char **argv)
{

#ifdef _WIN32
    setmode(fileno(stdout),O_BINARY);
    setmode(fileno(stdin),O_BINARY);
#endif

    // ######################################################################################################################
    // open wlf

    /* Initialize the WLF api */
    int status;
    status = wlfInit();
    if (status != WLF_OK)
    {
        fprintf(stderr, "wlf Init failed: %u", status);
        exit(1);
    }

    /* Open the WLF File */
    wlfFile = wlfFileOpen(argv[1], "vsim_wlf");
    if (wlfFile == NULL)
    {
        fprintf(stderr, "Invalid wlf file: %s",argv[1]);
        exit(1);
    }

    /* Check the API version */
    WlfFileInfo fileInfo;
    status = wlfFileInfo(wlfFile, &fileInfo);
    if(status != WLF_OK)
    {
        fprintf(stderr, "wlfFileInfo failed %u", status);
        exit(1);
    }

    /* get top context */
    wlfTop = wlfFileGetTopContext(wlfFile);
    if(wlfTop == NULL)
    {
        fprintf(stderr, "wlfFileGetTopContext failed");
        exit(1);
    }

    // ######################################################################################################################
    // detect geometry

    /* gather symbol information */
    maxSignals = fileInfo.signalCount;
    maxScopes = 1;
    countItems(wlfTop);

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

    flxAddHead(trace, argv[1], "wlf");
    flxFlush(trace);

    //flxSetBuffer(trace, buffer2);

    //parse input
    return flxParseControlInput(stdin, MAX_ENTRY_SIZE, handleCommands);
}

#ifdef __cplusplus
}
#endif
