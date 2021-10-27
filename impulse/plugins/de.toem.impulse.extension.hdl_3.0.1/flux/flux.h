/*******************************************************************************
 * Copyright (c) 2012-2019 Thomas Haber
 * All rights reserved. This source code and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/

#ifdef __cplusplus
extern "C" {
#endif

// ######################################################################################################################
// options
//
// FLX_COMPRESS
// FLX_STDIO
// FLX_CONTROL
// FLX_CUSTOM_TYPES

// ######################################################################################################################
// type definitions

// used types in flux:
// flxbptr:  stream byte buffer pointer
// flxbint: stream byte buffer index and length
// flxid: item, trace and control id
// flxbyte: stream byte and byte parameters
// flxuint: parameter and iterators
// flxsint: parameter and iterators
// flxbool: bool parameter
// flxresult: result values
// flxtext: const c text parameter
// flxdomain: domain value
// flxdelta: domain value delta

#ifndef FLX_CUSTOM_TYPES
#if defined (__STDC_VERSION__) && (__STDC_VERSION__ >= 199901L)   /* C99 */
# include <stdint.h>
typedef unsigned char* flxbptr; // buffer pointer
typedef unsigned int flxbint;// buffer int
typedef unsigned int flxid;// id int
typedef unsigned char flxbyte;// byte
typedef unsigned int flxuint;// unsigned int
typedef int flxsint;// signed int
typedef int flxbool;// bool
typedef int flxresult;// result
typedef const char* flxtext;// text
typedef char flxchar;// char
typedef int64_t flxdomain;// domain
typedef uint32_t flxdelta;// domain increment
typedef int32_t flxsdelta;// domain signed delta
#else
typedef unsigned char* flxbptr; // buffer pointer
typedef unsigned int flxbint; // buffer int
typedef unsigned int flxid; // id int
typedef unsigned char flxbyte; // byte
typedef unsigned int flxuint; // unsigned int
typedef int flxsint; // signed int
typedef int flxbool; // bool
typedef int flxresult; // result
typedef const char* flxtext; // text
typedef char flxchar; // char
typedef long long flxdomain; // domain
typedef unsigned long flxdelta; // domain increment
typedef long flxsdelta; // domain signed delta
#endif
#else
#include "flxtypes.h"
#endif

// ######################################################################################################################
// Error codes
// ######################################################################################################################

#define FLX_MODE_HEAD_NORMAL 0x00  /* Normal mode */
#define FLX_MODE_HEAD_SYNC   0x01  /* Sync mode - may ignore all further definitions and open */

#define FLX_OK  0
#define FLX_ERROR_BUFFER_UNKNOWN_COMMAND -1
#define FLX_ERROR_BUFFER_OVERFLOW  -1
#define FLX_ERROR_BUFFER_NOT_AVAIL  -2
#define FLX_ERROR_INVALID_VALUE  -3
#define FLX_ERROR_INVALID_DATA_SIZE  -4
#define FLX_ERROR_INVALID_ID  -5
#define FLX_ERROR_NO_BUFFER  -6
#define FLX_ERROR_INVALID_OPEN_CLOSE  -7
#define FLX_ERROR_ITEM_ALLREADY_DEFINED  -8
#define FLX_ERROR_ITEM_NOT_DEFINED  -9
#define FLX_ERROR_PARENT_NOT_DEFINED  -10
#define FLX_ERROR_ALLREADY_OPEN  -11
#define FLX_ERROR_CHILDREN_ALLREADY_OPEN  -12
#define FLX_ERROR_NOT_OPEN  -13
#define FLX_ERROR_POSITION_LESSTHAN_CURRENT  -14
#define FLX_ERROR_READ_ERROR  -15
#define FLX_ERROR_COMMAND_PARSE_ERROR  -16
#define FLX_ERROR_COMMAND_PARSE_NEED_MORE_DATA  -17
#define FLX_ERROR_INVALID_PACK_MODE  -18
#define FLX_ERROR_INSUFFICIENT_INPUT  -19
#define FLX_ERROR_BUFFER_ALLREADY_USED  -20

#define FLX_TYPE_UNKNOWN  0
#define FLX_TYPE_EVENT  1
#define FLX_TYPE_INTEGER  2
#define FLX_TYPE_LOGIC  3
#define FLX_TYPE_FLOAT  4
#define FLX_TYPE_TEXT  5
#define FLX_TYPE_BINARY  6
#define FLX_TYPE_STRUCT  7
#define FLX_TYPE_EVENT_ARRAY  8
#define FLX_TYPE_INTEGER_ARRAY  9
#define FLX_TYPE_FLOAT_ARRAY  10
#define FLX_TYPE_TEXT_ARRAY  11

#define FLX_STRUCTTYPE_UNKNOWN  0
#define FLX_STRUCTTYPE_TEXT  1
#define FLX_STRUCTTYPE_ENUM  2
#define FLX_STRUCTTYPE_INTEGER  3
#define FLX_STRUCTTYPE_FLOAT  4
#define FLX_STRUCTTYPE_BINARY  6
#define FLX_STRUCTTYPE_LOCAL_ENUM  7
#define FLX_STRUCTTYPE_MERGE_ENUM  8
#define FLX_STRUCTTYPE_MASK_BASE  0x0f
#define FLX_STRUCTTYPE_MOD_HIDDEN  0x80

#define FLX_ENUM_GLOBAL  0
#define FLX_ENUM_RELATION_TARGET  1
#define FLX_ENUM_RELATION_STYLE  2
#define FLX_ENUM_LABEL_STYLE  3
#define FLX_ENUM_MEMBER_0  8

#define FLX_STATE_LEVEL_UNKNOWN 0
#define FLX_STATE_LEVEL_2 1
#define FLX_STATE_LEVEL_4 2
#define FLX_STATE_LEVEL_16 3
#define FLX_STATE_0_BITS  0
#define FLX_STATE_1_BITS  1
#define FLX_STATE_Z_BITS  2
#define FLX_STATE_X_BITS  3
#define FLX_STATE_L_BITS  4
#define FLX_STATE_H_BITS  5
#define FLX_STATE_U_BITS  6
#define FLX_STATE_W_BITS  7
#define FLX_STATE_D_BITS  8
#define FLX_STATE_J_BITS  9
#define FLX_STATE_K_BITS  10
#define FLX_STATE_M_BITS  11
#define FLX_STATE_N_BITS  12
#define FLX_STATE_O_BITS  13
#define FLX_STATE_P_BITS  14
#define FLX_STATE_UNKNOWN_BITS 15

#define FLX_PACK_LZ4  0
#define FLX_PACK_FLZ  1

// ######################################################################################################################
// # Buffer creation and handling
// ######################################################################################################################

#define FLX_BUFFER_REQUEST  0  // request amount of free buffer - if not available flush
#define FLX_BUFFER_AVAIL  1    // get available free buffer
#define FLX_BUFFER_COMMIT 2    // commit new data in buffer
#define FLX_BUFFER_SECCOMMIT 3 // commit all data in buffer for sections
#define FLX_BUFFER_GET  4      // get data
#define FLX_BUFFER_CLEAR 5     // clear data
#define FLX_BUFFER_FLUSH 6     // flush data -> call handle
#define FLX_BUFFER_DEEPFLUSH 7 // flush data -> call handle + flush
struct flxTraceStruct;
typedef flxresult (*flxBufferAccess)(flxbyte command, void* buffer,
		flxbint* len, flxbyte** bytes);
typedef flxresult (*flxBufferHandle)(flxbyte command, void* buffer,
		flxbint* len, flxbyte* bytes, void *user);
typedef flxresult (*flxBufferInit)(flxbyte command, void* buffer,
		struct flxTraceStruct *trace);

typedef struct flxBufferStruct {
	flxBufferAccess access;
	flxbint len;
	flxbint pos;
	union{
		struct{
			flxBufferHandle handle;
			void *user;
		}linear;
		struct{
			flxBufferInit init;
			flxbint first; // pos of first section
			flxbint current; // pos of current section
			flxbint len; // size of current section
			flxbint pos; // pos in current section, relative to current
		}ring;
	}u;

	struct flxTraceStruct *trace;
	flxbyte bytes[1];

}* flxBuffer;

/**
 * Calculates the required memory for a buffer with given resulting bufferSize.
 * @param bufferSize : The resulting buffer size
 * @return Returns the required memory size
 */
#define  FLX_BUFFER_BYTES(bufferSize) (sizeof(struct flxBufferStruct) +  bufferSize)
;
/**
 * Creates a new 'fixed' buffer.
 * @param bytes : Memory pointer for the new buffer
 * @param len : Size of the memory
 * @param handle : Buffer handler
 * @param user : User parameter
 * @return Returns the newly created buffer
 */
flxBuffer flxCreateLinearBuffer(flxbptr bytes, flxbint len,
		flxBufferHandle handle, void *user);
flxBuffer flxCreateFixedBuffer(flxbptr bytes, flxbint len,
		flxBufferHandle handle, void *user);

flxBuffer flxCreateRingBuffer(flxbptr bytes, flxbint len,flxBufferInit init);

/**
 * Clear the buffer.
 * @param buffer : Buffer to be cleared
 */
void flxClearBuffer(flxBuffer buffer);

/**
 * Returns the the number of bytes in the buffer.
 * @param buffer : Buffer to be used
 * @return Returns the the number of bytes
 */
flxbint flxGetBufferBytes(flxBuffer buffer);

/**
 * Flushes the buffer.
 * @param buffer : Buffer to be flushed
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxFlushBuffer(flxBuffer buffer);

// ######################################################################################################################
// # Trace creation and handling
// ######################################################################################################################

#define FLX_ITEM_TYPE_UNDEFINED  0
#define FLX_ITEM_TYPE_SCOPE  1
#define FLX_ITEM_TYPE_SIGNAL  2

#define FLX_ITEM_OPEN_NONE  0
#define FLX_ITEM_OPEN_LOCAL  1
#define FLX_ITEM_OPEN_CONTAINER  2

#ifndef FLX_ITEM_EXTENSION
#define FLX_ITEM_EXTENSION
#endif

typedef struct flxTraceItemStruct {
	flxbyte type;
	flxbyte open;
	flxid parentId;
	union {
		flxdomain current;
		flxid openId;
	} u;
	FLX_ITEM_EXTENSION
}* flxTraceItem;

typedef struct flxTraceStruct {
	flxid id;
	flxbyte mode;
	flxid maxItemId;
	flxbint maxEntrySize;

	flxBuffer buffer;

	// item 0
	flxbyte open;
	flxdomain current;

	// items 1..itemCount (itemCount+1 .. maxItemId may not have an item struct)
	flxTraceItem items;
	flxuint itemCount;

}* flxTrace;

/**
 * Calculates the required memory for a trace with given multi-open flag and item count.
 * @param multiOpen : Set to true, if you wan to allow sequence opens on items != 0
 * @param maxItemId : The maximal item id to be used
 * @return Returns the required memory size.
 */
#define  FLX_TRACE_BYTES(multiOpen,maxItemId) (sizeof(struct flxTraceStruct) +  (multiOpen ? sizeof(struct flxTraceItemStruct) * (maxItemId):0))
;
/**
 * Creates a trace object
 * @param traceId : Id of the new trace object
 * @param maxItemId : The maximal item id to be used
 * @param maxEntrySize : The maximal allowed entry size (e.g. 4096).
 * @param traceBytes : Required memory for this trace structure
 * @param traceBytesLen : Size of the given memory
 * @param buffer : Initial buffe to be used for this trace
 * @return Returns the newly created trace object
 */
flxTrace flxCreateTrace(flxid traceId, flxid maxItemId, flxbint maxEntrySize,
		flxbptr traceBytes, flxbint traceBytesLen, flxBuffer buffer);
/**
 * Sets a new buffer for the given trace.
 * @param trace : The tarce object
 * @param buffer : Buffer to be used as output
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxSetBuffer(flxTrace trace, flxBuffer buffer);

// ######################################################################################################################
// # Adding trace entries
// ######################################################################################################################

typedef struct flxMemberValueStruct {
	flxid memberId;
	flxbyte type;
	flxtext label;
	flxtext descriptor;
	flxbyte format;
	void* value;
	flxuint size;
	flxuint option; // FLX_STRUCTTYPE_INTEGER: signed,
	flxbool valid;
}* flxMemberValue;

// ----------------------------------------------------------------------------------------------------------------------
// ## Items
//

/**
 * Writes a head entry. The head entry contains information data about the trace and is also used as file identification.
 * @param trace : The trace object
 * @param name : The name of the item
 * @param description : Descriptive text for this item or 0
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxAddHead(flxTrace trace, flxtext name, flxtext description);

/**
 * Writes a head entry. The head entry contains information data about the trace and is also used as file identification.
 * @param trace : The trace object
 * @param name : The name of the item
 * @param description : Descriptive text for this item or 0
 * @param mode : Mode parameter 0:normal 1: sync
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxAddModeHead(flxTrace trace, flxtext name, flxtext description, flxbyte mode);

/**
 * Writes a head entry for a derived format. The head entry contains information data about the trace and is also used as file identification.
 * @param trace : The trace object
 * @param format4 : Format identification (4 characters)
 * @param name : The name of the item
 * @param description : Descriptive text for this item or 0
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */

flxresult flxAddHeadDerived(flxTrace trace, flxtext format4, flxtext name,
		flxtext description);

/**
 * Writes a head entry for a derived format. The head entry contains information data about the trace and is also used as file identification.
 * @param trace : The trace object
 * @param noOfSections : No of sections to be inserted into buffer (ring-buffer)
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxAddSections(flxTrace trace,  flxuint noOfSections);

/**
 * Writes a signal item entry.
 * @param trace : The trace object
 * @param itemId : The item id for this new item. The id must be unique for this trace and in the range of 1..maxItemId
 * @param parentId : Defines the parent of this new item (or 0 for the root item) : Defines the parent of this new item (or 0 for the root item)
 * @param name : The name of the item
 * @param description : Descriptive text for this item or 0
 * @param signalType : The type of this new signal (FLX_TYPE_...)
 * @param signalDescriptor : Extended definition of the signal type, usually set to 0 for default
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxAddSignal(flxTrace trace, flxid itemId, flxid parentId,
		flxtext name, flxtext description, flxbyte signalType,
		flxtext signalDescriptor);

/**
 * Writes an item entry for scattered signals.
 * @param trace : The trace object
 * @param itemId : The item id for this new item. The id must be unique for this trace and in the range of 1..maxItemId
 * @param parentId : Defines the parent of this new item (or 0 for the root item)
 * @param name : The name of the item
 * @param description : Descriptive text for this item or 0
 * @param signalType : The type of this new signal (FLX_TYPE_...)
 * @param signalDescriptor : Extended definition of the signal type, usually set to 0 for default
 * @param scatteredFrom : Scattered from (e.g. bit position 0)
 * @param scatteredTo : Scattered to (e.g. bit position 4)
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxAddScatteredSignal(flxTrace trace, flxid itemId, flxid parentId,
		flxtext name, flxtext description, flxbyte signalType,
		flxtext signalDescriptor, flxuint scatteredFrom, flxuint scatteredTo);
/**
 * Writes an item entry for multiple signals.
 * @param trace : The trace object
 * @param itemIdFrom : The first item id for this new item set. The id must be unique for this trace and in the range of 1..maxItemId
 * @param itemIdTo : The last item id for this new item set. The id must be unique for this trace and in the range of 1..maxItemId
 * @param parentId : Defines the parent of this new item (or 0 for the root item)
 * @param name : The name of the item
 * @param description : Descriptive text for this item or 0
 * @param signalType : The type of this new signal (FLX_TYPE_...)
 * @param signalDescriptor : Extended definition of the signal type, usually set to 0 for default
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxAddSignals(flxTrace trace, flxid itemIdFrom, flxid itemIdTo,
		flxid parentId, flxtext name, flxtext description, flxbyte signalType,
		flxtext signalDescriptor);
/**
 * Writes an item entry for a signal reference.
 * @param trace : The trace object
 * @param referenceId
 * @param parentId : Defines the parent of this new item (or 0 for the root item)
 * @param name : The name of the item
 * @param description : Descriptive text for this item or 0
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxAddSignalReference(flxTrace trace, flxid referenceId,
		flxid parentId, flxtext name, flxtext description);
/**
 * Writes an item entry for a scattered signal reference.
 * @param trace : The trace object
 * @param referenceId
 * @param parentId : Defines the parent of this new item (or 0 for the root item)
 * @param name : The name of the item
 * @param description : Descriptive text for this item or 0
 * @param scatteredFrom : Scattered from (e.g. bit position 0)
 * @param scatteredTo : Scattered to (e.g. bit position 4)
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxAddScatteredSignalReference(flxTrace trace, flxid referenceId,
		flxid parentId, flxtext name, flxtext description,
		flxuint scatteredFrom, flxuint scatteredTo);
/**
 * Writes an item entry for a scope.
 * @param trace : The trace object
 * @param itemId : The item id for this new item. The id must be unique for this trace and in the range of 1..maxItemId
 * @param parentId : Defines the parent of this new item (or 0 for the root item)
 * @param name : The name of the item
 * @param description : Descriptive text for this item or 0
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxAddScope(flxTrace trace, flxid itemId, flxid parentId,
		flxtext name, flxtext description);
/**
 * Tests the item type.
 * @param trace : The trace object
 * @param itemId : The item id of the referenced item. The id must be unique for this trace and in the range of 1..maxItemId
 * @return Returns true if the item is a signal
 */
flxbool flxIsSignal(flxTrace trace, flxid itemId);
/**
 * Tests the item type.
 * @param trace : The trace object
 * @param itemId : The item id of the referenced item. The id must be unique for this trace and in the range of 1..maxItemId
 * @return Returns true if the item is a scope
 */
flxbool flxIsScope(flxTrace trace, flxid itemId);

// ----------------------------------------------------------------------------------------------------------------------
// ## Sequences - Open & Close
//
/**
 * Opens a new sequence. This opens the sequence for the references item and all items below (children,...).
 * @param trace : The trace object
 * @param itemId : The item id of the referenced item to be opened (1..maxItemId or 0 for the root item)
 * @param domainBase : Domain base (e.g. ns, us, Hz,..), or 0 for default.
 * @param start : Domain position as a multiple of its domain base (e.g. domain base=1ms, units = 100, -> domain value = 100ms)
 * @param rate : Domain rate as a multiple of its domain base
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxOpen(flxTrace trace, flxid itemId, flxtext domainBase, flxdomain start,
		flxdelta rate);

/**
 * Sets the default domain. This is used when using the flxOpen with domain=0
 * @param trace : The trace object
 * @param domainBase : Domain base (e.g. ns, us, Hz,..), or 0 for default.
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxSetDefaultOpenDomain(flxTrace trace, flxtext domainBase);

/**
 * Checks the open state of an item.
 * @param trace : The trace object
 * @param itemId : The item id of the referenced item to be opened (1..maxItemId or 0 for the root item)
 * @return Returns true if a sequence has been opened for the given item.
 */
flxbool flxIsOpen(flxTrace trace, flxid itemId);

/**
 * Returns the currentdomain position.
 * @param trace : The trace object
 * @param itemId : The item id of the referenced item to be opened (1..maxItemId or 0 for the root item)
 * @return Returns the current domain position, or 0 if not open
 */
flxdomain flxGetCurrent(flxTrace trace, flxid itemId);

/**
 * Closes a sequence. This closes the sequence for the references item and all items below (children,...).
 * @param trace : The trace object
 * @param itemId : The item id of the referenced item to be opened (1..maxItemId or 0 for the root item)
 * @param end : Domain position as a multiple of its domain base (e.g. domain base=1ms , units = 100, -> domain value = 100ms).
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxClose(flxTrace trace, flxid itemId, flxdomain end);

// ----------------------------------------------------------------------------------------------------------------------
// ## Enums & Members
//
/**
 * Writes an entry for a enumeration.
 * @param trace : The trace object
 * @param itemId : The item id of the referenced item. The id must be unique for this trace and in the range of 1..maxItemId
 * @param enumeration : Define the enumeration domain (e.g. FLX_ENUM_GLOBAL, FLX_ENUM_MEMBER_0, ..)
 * @param label : The textual representation of the enum.
 * @param value : The value : The integer value of the enum. This value must be unique for one enumeration domain.
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxWriteEnumDef(flxTrace trace, flxid itemId, flxuint enumeration,
		flxtext label, flxuint value);
/**
 * Writes an entry for an array definition.
 * @param trace : The trace object
 * @param itemId : The item id of the referenced item. The id must be unique for this trace and in the range of 1..maxItemId
 * @param index : Index of the array member (0..size-1).
 * @param label : Label of the array member.
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxWriteArrayDef(flxTrace trace, flxid itemId, flxuint index,
		flxtext label);
/**
 * Initializes a member structure.
 * @param member : Member structure of type flxMemberValue
 * @param memberId : Id of the member (0..N). This id need to be unique for one signal item.
 * @param label : Label of the struct member.
 * @param memberType : Data type of this member (FLX_STRUCTTYPE_TEXT, FLX_STRUCTTYPE_ENUM,...)
 * @param memberDescriptor : Type descriptor or 0 for default.
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxInitMember(flxMemberValue member, flxuint memberId, flxtext label,
		flxbyte memberType, flxtext memberDescriptor);
/**
 * Writes an entry for a member definition.
 * @param trace : The trace object
 * @param itemId : The item id of the referenced item. The id must be unique for this trace and in the range of 1..maxItemId
 * @param memberId : Id of the member (0..N). This id need to be unique for one signal item.
 * @param label : Label of the struct member.
 * @param memberType : Data type of this member (FLX_STRUCTTYPE_TEXT, FLX_STRUCTTYPE_ENUM,...)
 * @param memberDescriptor : Type descriptor or 0 for default.
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxWriteMemberDef(flxTrace trace, flxid itemId, flxid memberId, flxtext label,
		flxbyte memberType, flxtext memberDescriptor);
/**
 * Writes multiple entries for member definition.
 * @param trace : The trace object
 * @param itemId : The item id of the referenced item. The id must be unique for this trace and in the range of 1..maxItemId
 * @param member : Member structure of type flxMemberValue
 * @param count : No of members in the member array.
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxWriteMemberDefs(flxTrace trace, flxid itemId,
		flxMemberValue member, flxuint count);

// ----------------------------------------------------------------------------------------------------------------------
// ## Writing samples
//
/**
 * Sets the current domain position.
 * @param trace : The trace object
 * @param itemId : The item id of the referenced item. The id must be unique for this trace and in the range of 1..maxItemId
 * @param domainPosition : Domain position as a multiple of its domain base (e.g. domain base=1ms; units = 100; -> domain value = 100ms).
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxWriteCurrent(flxTrace trace, flxid itemId, flxdomain domainPosition);
/**
 * Writes a 'none' samples.
 * @param trace : The trace object
 * @param itemId : The item id of the referenced item. The id must be unique for this trace and in the range of 1..maxItemId
 * @param conflict : Marks the new sample as a 'conflict' one. In impulse conflict samples are painted in red
 * @param domainPosition : Domain position as a multiple of its domain base (e.g. domain base=1ms; units = 100; -> domain value = 100ms).
 * @param isDelta : If set to true, domain will be taken as positive relative value (0 to keep the domain position)
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxWriteNoneAt(flxTrace trace, flxid itemId, flxbool conflict,
		flxdomain domainPosition, flxbool isDelta);
/**
 * Writes an integer sample.
 * @param trace : The trace object
 * @param itemId : The item id of the referenced item. The id must be unique for this trace and in the range of 1..maxItemId
 * @param conflict : Marks the new sample as a 'conflict' one. In impulse conflict samples are painted in red
 * @param domainPosition : Domain position as a multiple of its domain base (e.g. domain base=1ms; units = 100; -> domain value = 100ms).
 * @param isDelta : If set to true, domain will be taken as positive relative value (0 to keep the domain position)
 * @param value : The value
 * @param size : Size of the value in bytes
 * @param signd : 1 if value is a signed integer
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxWriteIntAt(flxTrace trace, flxid itemId, flxbool conflict,
		flxdomain domainPosition, flxbool isDelta, void *value, flxbyte size,
		flxbool signd);
/**
 * Writes an integer array sample.
 * @param trace : The trace object
 * @param itemId : The item id of the referenced item. The id must be unique for this trace and in the range of 1..maxItemId
 * @param conflict : Marks the new sample as a 'conflict' one. In impulse conflict samples are painted in red
 * @param domainPosition : Domain position as a multiple of its domain base (e.g. domain base=1ms; units = 100; -> domain value = 100ms).
 * @param isDelta : If set to true, domain will be taken as positive relative value (0 to keep the domain position)
 * @param value : The value
 * @param intsize : Size of the int values (4 or 8) in bytes
 * @param signd : 1 if value is a signed integer
 * @param count : No of array members
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxWriteIntArrayAt(flxTrace trace, flxid itemId, flxbool conflict,
		flxdomain domainPosition, flxbool isDelta, void *value, flxbyte intsize,
		flxbool signd, flxuint count);
/**
 * Writes a float sample.
 * @param trace : The trace object
 * @param itemId : The item id of the referenced item. The id must be unique for this trace and in the range of 1..maxItemId
 * @param domainPosition : Marks the new sample as a 'conflict' one. In impulse conflict samples are painted in red
 * @param current : Domain position as a multiple of its domain base (e.g. domain base=1ms; units = 100; -> domain value = 100ms).
 * @param isDelta : If set to true, domain will be taken as positive relative value (0 to keep the domain position)
 * @param value : The value
 * @param size : Size of the value in bytes (4 or 8)
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxWriteFloatAt(flxTrace trace, flxid itemId, flxbool conflict,
		flxdomain domainPosition, flxbool isDelta, void *value, flxbyte size);
/**
 * Writes a float array sample.
 * @param trace : The trace object
 * @param itemId : The item id of the referenced item. The id must be unique for this trace and in the range of 1..maxItemId
 * @param conflict : Marks the new sample as a 'conflict' one. In impulse conflict samples are painted in red
 * @param domainPosition : Domain position as a multiple of its domain base (e.g. domain base=1ms; units = 100; -> domain value = 100ms).
 * @param isDelta : If set to true, domain will be taken as positive relative value (0 to keep the domain position)
 * @param value : The value
 * @param floatsize : Size of the value in bytes (4 or 8)
 * @param count : No of array members
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxWriteFloatArrayAt(flxTrace trace, flxid itemId, flxbool conflict,
		flxdomain domainPosition, flxbool isDelta, void *value, flxbyte floatsize,
		flxuint count);
/**
 * Writes an event sample.
 * @param trace : The trace object
 * @param itemId : The item id of the referenced item. The id must be unique for this trace and in the range of 1..maxItemId
 * @param conflict : Marks the new sample as a 'conflict' one. In impulse conflict samples are painted in red
 * @param domainPosition : Domain position as a multiple of its domain base (e.g. domain base=1ms; units = 100; -> domain value = 100ms).
 * @param isDelta : If set to true, domain will be taken as positive relative value (0 to keep the domain position)
 * @param value : The value
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxWriteEventAt(flxTrace trace, flxid itemId, flxbool conflict,
		flxdomain domainPosition, flxbool isDelta, flxuint value);
/**
 * Writes an event array sample.
 * @param trace : The trace object
 * @param itemId : The item id of the referenced item. The id must be unique for this trace and in the range of 1..maxItemId
 * @param conflict : Marks the new sample as a 'conflict' one. In impulse conflict samples are painted in red
 * @param domainPosition : Domain position as a multiple of its domain base (e.g. domain base=1ms; units = 100; -> domain value = 100ms).
 * @param isDelta : If set to true, domain will be taken as positive relative value (0 to keep the domain position)
 * @param value : The value
 * @param count : No of array members
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxWriteEventArrayAt(flxTrace trace, flxid itemId, flxbool conflict,
		flxdomain domainPosition, flxbool isDelta, flxuint *value, flxuint count);
/**
 * Writes a text sample.
 * @param trace : The trace object
 * @param itemId : The item id of the referenced item. The id must be unique for this trace and in the range of 1..maxItemId
 * @param conflict : Marks the new sample as a 'conflict' one. In impulse conflict samples are painted in red
 * @param domainPosition : Domain position as a multiple of its domain base (e.g. domain base=1ms; units = 100; -> domain value = 100ms).
 * @param isDelta : If set to true, domain will be taken as positive relative value (0 to keep the domain position)
 * @param value : The value
 * @param size : Size of the value in characters
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxWriteTextAt(flxTrace trace, flxid itemId, flxbool conflict,
		flxdomain domainPosition, flxbool isDelta, flxtext value, flxuint size);
/**
 * Writes a binary sample.
 * @param trace : The trace object
 * @param itemId : The item id of the referenced item. The id must be unique for this trace and in the range of 1..maxItemId
 * @param conflict : Marks the new sample as a 'conflict' one. In impulse conflict samples are painted in red
 * @param domainPosition : Domain position as a multiple of its domain base (e.g. domain base=1ms; units = 100; -> domain value = 100ms).
 * @param isDelta : If set to true, domain will be taken as positive relative value (0 to keep the domain position)
 * @param value : The value
 * @param size : Size of the value in bytes
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxWriteBinaryAt(flxTrace trace, flxid itemId, flxbool conflict,
		flxdomain domainPosition, flxbool isDelta, flxbptr value, flxuint size);
/**
 * Writes a logic sample using an array of states.
 * @param trace : The trace object
 * @param itemId : The item id of the referenced item. The id must be unique for this trace and in the range of 1..maxItemId
 * @param conflict : Marks the new sample as a 'conflict' one. In impulse conflict samples are painted in red
 * @param domainPosition : Domain position as a multiple of its domain base (e.g. domain base=1ms; units = 100; -> domain value = 100ms).
 * @param isDelta : If set to true, domain will be taken as positive relative value (0 to keep the domain position)
 * @param precedingStates : If the given no of bits less than the defined one, the preceding states will be filled to the left
 * @param value : The value
 * @param size : Size of the value in states
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxWriteLogicStatesAt(flxTrace trace, flxid itemId, flxbool conflict,
		flxdomain domainPosition, flxbool isDelta, flxbyte precedingStates,
		flxbptr value, flxuint size);
/**
 * Writes a logic sample using a text.
 * @param trace : The trace object
 * @param itemId : The item id of the referenced item. The id must be unique for this trace and in the range of 1..maxItemId
 * @param conflict : Marks the new sample as a 'conflict' one. In impulse conflict samples are painted in red
 * @param domainPosition : Domain position as a multiple of its domain base (e.g. domain base=1ms; units = 100; -> domain value = 100ms).
 * @param isDelta : If set to true, domain will be taken as positive relative value (0 to keep the domain position)
 * @param precedingStates : If the given no of bits less than the defined one, the preceding states will be filled to the left
 * @param value : The value
 * @param size : Size of the value in characters
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxWriteLogicTextAt(flxTrace trace, flxid itemId, flxbool conflict,
		flxdomain domainPosition, flxbool isDelta, flxbyte precedingStates,
		flxtext value, flxuint size);
/**
 * Sets the member values of a struct signal member.
 * @param member : Member structure of type flxMemberValue
 * @param value : The value
 * @param size : Size of the value in bytes
 * @param option : integer->signed flag
 * @param valid : Marks this member as valid. In-valid members are not written.
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxSetMember(flxMemberValue member, void* value, flxuint size,
		flxuint option, flxbool valid);
/**
 * Writes a struct sample.
 * @param trace : The trace object
 * @param itemId : The item id of the referenced item. The id must be unique for this trace and in the range of 1..maxItemId
 * @param conflict : Marks the new sample as a 'conflict' one. In impulse conflict samples are painted in red
 * @param domainPosition : Domain position as a multiple of its domain base (e.g. domain base=1ms; units = 100; -> domain value = 100ms).
 * @param isDelta : If set to true, domain will be taken as positive relative value (0 to keep the domain position)
 * @param value : The value
 * @param count : No of array members
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxWriteMembersAt(flxTrace trace, flxid itemId, flxbool conflict,
		flxdomain domainPosition, flxbool isDelta, flxMemberValue value, flxuint count);

// ----------------------------------------------------------------------------------------------------------------------
// ## Relation & Labels
//
/**
 * Writes an relation entry. An relation connects the previously written sample with any other item (path of the item) at a relative position.
 * @param trace : The trace object
 * @param itemId : The item id of the referenced item. The id must be unique for this trace and in the range of 1..maxItemId
 * @param target : Path to the target signal (e.g. "\\scope\\signal")
 * @param style : Enumeration id of the style description.
 * @param delta : Delta position
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxWriteRelation(flxTrace trace, flxid itemId, flxuint target,
		flxuint style, flxsdelta delta);
/**
 * Writes a label entry. The label is added to the previously written sample.
 * @param trace : The trace object
 * @param itemId : The item id of the referenced item. The id must be unique for this trace and in the range of 1..maxItemId
 * @param style : Enumeration id of the style description.
 * @param x
 * @param y
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxWriteLabel(flxTrace trace, flxid itemId, flxuint style, flxsint x,
		flxsint y);

// ----------------------------------------------------------------------------------------------------------------------
// ## Control entries
//

/**
 * Writes a control request entry into a buffer.
 * @param buffer : Target of this write entry
 * @param controlId : Identifies a control function
 * @param messageId : Identifies a message (may be used to identify the result message)
 * @param value : The value (set of members)
 * @param count : No of array members
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxWriteControlReqEntry(flxBuffer buffer, flxid controlId,
		flxid messageId, flxMemberValue value, flxuint count);
/**
 * Writes a control result entry into a buffer.
 * @param buffer : Target of this write entry
 * @param controlId : Identifies a control function
 * @param messageId : Identifies a message (may be used to identify the result message)
 * @param value : The value (set of members)
 * @param count : No of array members
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxWriteControlResEntry(flxBuffer buffer, flxid controlId,
		flxid messageId, flxMemberValue value, flxuint count);
/**
 * Writes a control request entry into a trace.
 * @param buffer : Target of this write entry
 * @param controlId : Identifies a control function
 * @param messageId : Identifies a message (may be used to identify the result message)
 * @param value : The value (set of members)
 * @param count : No of array members
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxWriteControlRequest(flxTrace trace, flxid controlId,
		flxid messageId, flxMemberValue value, flxuint count);
/**
 * Writes a control result entry into a trace.
 * @param buffer : Target of this write entry
 * @param controlId : Identifies a control function
 * @param messageId : Identifies a message (may be used to identify the result message)
 * @param value : The value (set of members)
 * @param count : No of array members
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxWriteControlResult(flxTrace trace, flxid controlId,
		flxid messageId, flxMemberValue value, flxuint count);

/**
 * Flushes the trace buffer.
 * @return Returns FLX_OK is succeeded, or FLX_ERROR_ in the error case
 */
flxresult flxFlush(flxTrace trace);

// ######################################################################################################################
// Buffer Handler
// ######################################################################################################################

flxresult flxCopy(flxbyte command, void* buffer, flxbint* len, flxbyte* bytes,
		void*user);
#ifdef FLX_COMPRESS
flxresult flxCompressLz4(flxbyte command, void* buffer, flxbint* len, flxbyte* bytes, void*user);
flxresult flxCompressFlz(flxbyte command, void* buffer, flxbint* len, flxbyte* bytes, void*user);
#endif

#ifdef FLX_STDIO
flxresult flxWriteToFile(flxbyte command, void* buffer, flxbint* len, flxbyte* bytes, void*user);
#endif

#ifdef FLX_CONTROL

flxbint _plusread(flxuint* val, flxbptr bytes, flxbptr end);

// control parameter declaration
#define FLX_CONTROL_HANDLE_INTEGER_PARAMETER(id,name,ctype,def,sign)\
	static ctype name;\
	if (memberId == id){ if (command == FLX_CONTROL_HANDLE_ENTER_MESSAGE) name = def;\
	else if (command == FLX_CONTROL_HANDLE_PARSE_PARAMETER && FLX_STRUCTTYPE_INTEGER == type) {*value = (void*) &name;*size = sizeof(ctype);*opt = sign; }}

#define FLX_CONTROL_HANDLE_FLOAT_PARAMETER(id,name,ctype,def,sign)\
	static ctype name;\
	if (memberId == id){ if (command == FLX_CONTROL_HANDLE_ENTER_MESSAGE) name = def;\
	else if (command == FLX_CONTROL_HANDLE_PARSE_PARAMETER && FLX_STRUCTTYPE_FLOAT == type) {*value = (void*) &name;*size = sizeof(ctype);*opt = sign; }}

#define FLX_CONTROL_HANDLE_ENUM_PARAMETER(id,name,def)\
	static int name;\
	if (memberId == id){ if (command == FLX_CONTROL_HANDLE_ENTER_MESSAGE) name = def;\
	else if (command == FLX_CONTROL_HANDLE_PARSE_PARAMETER && FLX_STRUCTTYPE_ENUM == type) {*value = (void*) &name;*size = sizeof(int); }}

#define FLX_CONTROL_HANDLE_TEXT_PARAMETER(id,name,dim)\
	static flxchar name[dim];\
	static flxbint name ## Size;\
	if (memberId == id){ if (command == FLX_CONTROL_HANDLE_ENTER_MESSAGE) name ## Size = 0;\
	else if (command == FLX_CONTROL_HANDLE_PARSE_PARAMETER && FLX_STRUCTTYPE_TEXT == type) {*value = (void*) name; name ## Size = *size;*size = dim;}}

#define FLX_CONTROL_HANDLE_BINARY_PARAMETER(id,name,dim)\
	static flxbyte name[dim];\
	static flxbint name ## Size;\
	if (memberId == id){ if (command == FLX_CONTROL_HANDLE_ENTER_MESSAGE) name ## Size = 0;\
	else if (command == FLX_CONTROL_HANDLE_PARSE_PARAMETER && FLX_STRUCTTYPE_BINARY == type) {*value = (void*) name; name ## Size = *size; *size = dim;}}

// parse handler
#define FLX_CONTROL_HANDLE_ENTER_MESSAGE 0
#define FLX_CONTROL_HANDLE_PARSE_PARAMETER 1
#define FLX_CONTROL_HANDLE_LEAVE_MESSAGE 2
typedef flxresult (*flxHandleControlParse)(flxbyte command,flxid controlId, flxid messageId,flxid memberId,flxbyte type,void **value, flxuint *size, flxuint *opt);
#ifdef FLX_STDIO
flxresult flxParseControlInput(FILE* file, flxbint maxEntrySize,flxHandleControlParse parse);
#endif
flxresult flxHandleControl(flxbyte command, void* buffer, flxbint* len, flxbyte* bytes, void*user);
#endif

// Database scheme
#define FLX_CONTROL_DB_SCHEME  (0x00000100)
#define FLX_CONTROL_DB_REQ_SCHEME (FLX_CONTROL_DB_SCHEME )
#define FLX_CONTROL_DB_REQ_ITEMS (FLX_CONTROL_DB_SCHEME +0x01)
#define FLX_CONTROL_DB_REQ_TRACE (FLX_CONTROL_DB_SCHEME +0x02)

// CanBus scheme
#define FLX_CONTROL_CANBUS_SCHEME  (0x00000200)
#define FLX_CONTROL_CANBUS_REQ_SCHEME (FLX_CONTROL_CANBUS_SCHEME )
#define FLX_CONTROL_CANBUS_REQ_AVAIL (FLX_CONTROL_CANBUS_SCHEME +0x01)
#define FLX_CONTROL_CANBUS_RES_AVAIL (FLX_CONTROL_CANBUS_SCHEME +0x02)
#define FLX_CONTROL_CANBUS_REQ_OPEN (FLX_CONTROL_CANBUS_SCHEME +0x03)
#define FLX_CONTROL_CANBUS_REQ_CLOSE (FLX_CONTROL_CANBUS_SCHEME +0x04)
#define FLX_CONTROL_CANBUS_REQ_IDENT (FLX_CONTROL_CANBUS_SCHEME +0x05)
#define FLX_CONTROL_CANBUS_REQ_SEND (FLX_CONTROL_CANBUS_SCHEME +0x06)
#define FLX_CONTROL_CANBUS_REQ_FILTER (FLX_CONTROL_CANBUS_SCHEME +0x07)

#define FLX_CONTROL_CANBUS_ID_CAN 1
#define FLX_CONTROL_CANBUS_ID_STATUS 2
#define FLX_CONTROL_CANBUS_ID_ERROR 3

#define FLX_CONTROL_CANBUS_MESSAGE_STANDARD 0x0
#define FLX_CONTROL_CANBUS_MESSAGE_RTR 0x1
#define FLX_CONTROL_CANBUS_MESSAGE_EXTENDED 0x2
#define FLX_CONTROL_CANBUS_MESSAGE_STATUS 0x80

#ifdef __cplusplus
}
#endif

