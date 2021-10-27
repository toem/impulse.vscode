/*******************************************************************************
 * Copyright (c) 2012-2019 Thomas Haber
 * All rights reserved. This source code and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/

#ifdef __cplusplus
extern "C"
{
#endif
#ifdef FLX_COMPRESS
#include "lz4.h"
#include "fastlz.h"
#endif
#ifdef FLX_STDIO
#include <stdio.h>
#endif
#include "flux.h"

#ifndef FLX_ENDIANESS
#define FLX_ENDIANESS  1 /* little*/
#endif
// ######################################################################################################################

const flxbyte FLX_VERSION = 4;

// ######################################################################################################################
// Buffer
// ######################################################################################################################

// ######################################################################################################################
// Simple buffer

flxresult flxSimpleBufferAccess(flxbyte command, flxBuffer buffer, flxbint* len,
		flxbyte**bytes) {
	switch (command) {
	case FLX_BUFFER_GET: {
		if (bytes)
			*bytes = buffer->bytes;
		if (len)
			*len = buffer->pos;
		return FLX_OK;
	}
	case FLX_BUFFER_CLEAR: {
		buffer->pos = 0;
		return FLX_OK;
	}
	case FLX_BUFFER_FLUSH:
	case FLX_BUFFER_DEEPFLUSH: {

		if (buffer->u.linear.handle) {
			flxuint n;
			flxbint len = buffer->pos;
			//printf("FLX_BUFFER_FLUSH start %i %i\n",len,buffer->pos);
			flxresult result = buffer->u.linear.handle(command, buffer, &len,
					buffer->bytes, buffer->u.linear.user);
			if (len > 0) {
				//printf("FLX_BUFFER_FLUSH ok %i %i\n",len,buffer->pos);
				// all data handled
				if (len == buffer->pos)
					buffer->pos = 0;
				// partly used
				else if (len > 0 && len < buffer->pos) {
					for (n = len; n < buffer->pos; n++)
						buffer->bytes[n - len] = buffer->bytes[n];
					buffer->pos -= len;
				}
				//printf("FLX_BUFFER_FLUSH finsih %i %i\n",len,buffer->pos);
			}
			return result;
		} else
			return FLX_OK;
	}
	case FLX_BUFFER_REQUEST: {
		//printf("R: %u %u %u\n", buffer->pos, buffer->len, len);
		// need to flush ?
		if (buffer->u.linear.handle && buffer->pos + *len > buffer->len) {
			flxSimpleBufferAccess(FLX_BUFFER_FLUSH, buffer, 0, 0);
		}
		// enough space ?
		if (buffer->pos + *len <= buffer->len) {
			*bytes = buffer->bytes + buffer->pos;
			return FLX_OK;
		}
		*bytes = 0;
		return FLX_ERROR_BUFFER_NOT_AVAIL;
	}
	case FLX_BUFFER_AVAIL: {
		*len = buffer->len - buffer->pos;
		*bytes = buffer->bytes + buffer->pos;
		return FLX_OK;
	}
	case FLX_BUFFER_COMMIT: {
		if (buffer->pos + *len <= buffer->len) {
			buffer->pos += *len;
			//printf("C: %u %u %u\n", buffer->pos, buffer->len, len);
			return FLX_OK;
		}
		return FLX_ERROR_BUFFER_OVERFLOW;
	}
	default:
		return FLX_ERROR_BUFFER_UNKNOWN_COMMAND;
	}
}

flxBuffer flxCreateSimpleBuffer(flxbptr bytes, flxbint len,
		flxBufferHandle handle, void *user) {
	((flxBuffer) bytes)->len = len - sizeof(struct flxBufferStruct);
	((flxBuffer) bytes)->pos = 0;
	((flxBuffer) bytes)->access = (flxBufferAccess) flxSimpleBufferAccess;
	((flxBuffer) bytes)->u.linear.handle = (flxBufferHandle) handle;
	((flxBuffer) bytes)->u.linear.user = user;
	((flxBuffer) bytes)->trace = 0;
	return (flxBuffer) bytes;
}

// ######################################################################################################################
// Linear buffer

flxresult flxLinearBufferAccess(flxbyte command, flxBuffer buffer, flxbint* len,
		flxbyte**bytes) {
	switch (command) {
	case FLX_BUFFER_GET: {
		if (bytes)
			*bytes = buffer->bytes;
		if (len)
			*len = buffer->pos;
		return FLX_OK;
	}
	case FLX_BUFFER_CLEAR: {
		buffer->pos = 0;
		return FLX_OK;
	}
	case FLX_BUFFER_FLUSH:
	case FLX_BUFFER_DEEPFLUSH: {

		if (buffer->u.linear.handle) {
			flxuint n;
			flxbint len = buffer->pos;
			//printf("FLX_BUFFER_FLUSH start %i %i\n",len,buffer->pos);
			flxresult result = buffer->u.linear.handle(command, buffer, &len,
					buffer->bytes, buffer->u.linear.user);
			if (len > 0) {
				//printf("FLX_BUFFER_FLUSH ok %i %i\n",len,buffer->pos);
				// all data handled
				if (len == buffer->pos)
					buffer->pos = 0;
				// partly used
				else if (len > 0 && len < buffer->pos) {
					for (n = len; n < buffer->pos; n++)
						buffer->bytes[n - len] = buffer->bytes[n];
					buffer->pos -= len;
				}
				//printf("FLX_BUFFER_FLUSH finsih %i %i\n",len,buffer->pos);
			}
			return result;
		} else
			return FLX_OK;
	}
	case FLX_BUFFER_REQUEST: {
		//printf("R: %u %u %u\n", buffer->pos, buffer->len, len);
		// need to flush ?
		if (buffer->u.linear.handle && buffer->pos + *len > buffer->len) {
			flxLinearBufferAccess(FLX_BUFFER_FLUSH, buffer, 0, 0);
		}
		// enough space ?
		if (buffer->pos + *len <= buffer->len) {
			*bytes = buffer->bytes + buffer->pos;
			return FLX_OK;
		}
		*bytes = 0;
		return FLX_ERROR_BUFFER_NOT_AVAIL;
	}
	case FLX_BUFFER_AVAIL: {
		*len = buffer->len - buffer->pos;
		*bytes = buffer->bytes + buffer->pos;
		return FLX_OK;
	}
	case FLX_BUFFER_COMMIT: {
		if (buffer->pos + *len <= buffer->len) {
			buffer->pos += *len;
			//printf("C: %u %u %u\n", buffer->pos, buffer->len, len);
			return FLX_OK;
		}
		return FLX_ERROR_BUFFER_OVERFLOW;
	}
	default:
		return FLX_ERROR_BUFFER_UNKNOWN_COMMAND;
	}
}

flxBuffer flxCreateLinearBuffer(flxbptr bytes, flxbint len,
		flxBufferHandle handle, void *user) {
	((flxBuffer) bytes)->len = len - sizeof(struct flxBufferStruct);
	((flxBuffer) bytes)->pos = 0;
	((flxBuffer) bytes)->access = (flxBufferAccess) flxLinearBufferAccess;
	((flxBuffer) bytes)->u.linear.handle = (flxBufferHandle) handle;
	((flxBuffer) bytes)->u.linear.user = user;
	((flxBuffer) bytes)->trace = 0;
	return (flxBuffer) bytes;
}

// Deprecated
flxBuffer flxCreateFixedBuffer(flxbptr bytes, flxbint len,
		flxBufferHandle handle, void *user) {
	return flxCreateLinearBuffer(bytes, len, handle, user);
}

// ######################################################################################################################
// Ring buffer

// section continuation counter
// section	empty	half	full	overf	o15		o16
// 0		0		1		1		2		15		1
// 1		0		1		1		2		15		1
// 2		0		1		1		1		15		1
// 3		0		1		1		1		15		15
// 4		0		1		1		1		15		15
// 5		0		1		1		1		15		15
// 6		0		1		1		1		15		15
// 7		0		0		1		1		15		15
// 8		0		0		1		1		15		15

#define SECTION_HEADER_SIZE 7
flxresult flxRingBufferAccess(flxbyte command, flxBuffer buffer, flxbint* len,
		flxbyte**bytes) {

	switch (command) {
	case FLX_BUFFER_GET: {
		if (bytes)
			*bytes = buffer->bytes;
		if (len) {
			if (buffer->u.ring.first == -1)
				*len = buffer->pos;
			else
				*len = buffer->len;
		}
		return FLX_OK;
	}
	case FLX_BUFFER_CLEAR: {
		buffer->pos = 0;
		buffer->u.ring.first = -1;
		return FLX_OK;
	}
	case FLX_BUFFER_REQUEST: {

		// enough space ?
		if (buffer->u.ring.first == -1) {
			if (buffer->pos + *len <= buffer->len) {
				*bytes = buffer->bytes + buffer->pos;
				return FLX_OK;
			}
		} else {
			// check if available in current section
			if (buffer->u.ring.pos + *len <= buffer->u.ring.len) {
				*bytes = buffer->bytes
						+ (buffer->u.ring.current + SECTION_HEADER_SIZE
								+ buffer->u.ring.pos);
				return FLX_OK;
			} else {
				// no, get to next section
				buffer->u.ring.current += (SECTION_HEADER_SIZE
						+ buffer->bytes[buffer->u.ring.current + 3]
						+ (((int) (buffer->bytes[buffer->u.ring.current + 4]))
								<< 8));
				if (buffer->u.ring.current >= buffer->len)
					buffer->u.ring.current = buffer->u.ring.first;
				// inc section counter
				flxbyte counter = buffer->bytes[buffer->u.ring.current + 2]
						& 0xf;
				counter++;
				if (counter >= 16)
					counter = 1;
				buffer->bytes[buffer->u.ring.current + 2] = counter
						| (buffer->bytes[buffer->u.ring.current + 2] & 0x80);

				// set len,pos
				buffer->u.ring.len = buffer->bytes[buffer->u.ring.current + 3]
						+ (((int) (buffer->bytes[buffer->u.ring.current + 4]))
								<< 8);
				buffer->u.ring.pos = 0;
				// initialize
				if (buffer->u.ring.init)
					buffer->u.ring.init(command, buffer, buffer->trace);

				// printf("%u %u %u %u\n",buffer->u.ring.first,buffer->u.ring.current,buffer->u.ring.pos,buffer->u.ring.len);
				// check if now available in current section
				if (buffer->u.ring.pos + *len <= buffer->u.ring.len) {
					*bytes = buffer->bytes
							+ (buffer->u.ring.current + SECTION_HEADER_SIZE
									+ buffer->u.ring.pos);
					return FLX_OK;
				}
			}
		}
		*bytes = 0;
		return FLX_ERROR_BUFFER_NOT_AVAIL;
	}
	case FLX_BUFFER_AVAIL: {
		if (buffer->u.ring.first == -1) {
			*len = buffer->len - buffer->pos;
			*bytes = buffer->bytes + buffer->pos;
		} else {
			*len = buffer->u.ring.len - buffer->u.ring.pos;
			*bytes = buffer->bytes + buffer->u.ring.pos;
		}
		return FLX_OK;
	}
	case FLX_BUFFER_COMMIT: {
		if (buffer->u.ring.first == -1) {
			if (buffer->pos + *len <= buffer->len) {
				buffer->pos += *len;
				return FLX_OK;
			}
		} else {
			if (buffer->u.ring.pos + *len <= buffer->u.ring.len) {
				buffer->u.ring.pos += *len;
				buffer->bytes[buffer->u.ring.current + 5] = buffer->u.ring.pos
						& 0xff;
				buffer->bytes[buffer->u.ring.current + 6] = (buffer->u.ring.pos
						>> 8) & 0xff;
				return FLX_OK;
			}
		}
		return FLX_ERROR_BUFFER_OVERFLOW;
	}
	case FLX_BUFFER_SECCOMMIT: { // commit of ring sections
		if (buffer->u.ring.first == -1) {
			buffer->u.ring.first = buffer->u.ring.current = buffer->pos;
			buffer->pos = buffer->len;
			buffer->bytes[buffer->u.ring.current + 2]++;
			buffer->u.ring.len =
					buffer->bytes[buffer->u.ring.current + 3]
							+ (((int) (buffer->bytes[buffer->u.ring.current + 4]))
									<< 8);
			buffer->u.ring.pos = 0;
			return FLX_OK;
		}
		return FLX_ERROR_BUFFER_OVERFLOW;
	}
	default:
		return FLX_ERROR_BUFFER_UNKNOWN_COMMAND;
	}
}

flxBuffer flxCreateRingBuffer(flxbptr bytes, flxbint len, flxBufferInit init) {
	((flxBuffer) bytes)->len = len - sizeof(struct flxBufferStruct);
	((flxBuffer) bytes)->pos = 0;
	((flxBuffer) bytes)->access = (flxBufferAccess) flxRingBufferAccess;
	((flxBuffer) bytes)->u.ring.init = init;
	((flxBuffer) bytes)->u.ring.first = -1;
	((flxBuffer) bytes)->u.ring.current = -1;
	((flxBuffer) bytes)->trace = 0;
	return (flxBuffer) bytes;
}

// ######################################################################################################################
// General purpose

void flxClearBuffer(flxBuffer buffer) {
	buffer->access(FLX_BUFFER_CLEAR, buffer, 0, 0);
}

flxbint flxGetBufferBytes(flxBuffer buffer) {
	flxbint len;
	buffer->access(FLX_BUFFER_GET, buffer, &len, 0);
	return len;
}

flxresult flxFlushBuffer(flxBuffer buffer) {
	return buffer->access(FLX_BUFFER_FLUSH, buffer, 0, 0);
}

flxresult flxDeepFlushBuffer(flxBuffer buffer) {
	return buffer->access(FLX_BUFFER_DEEPFLUSH, buffer, 0, 0);
}

// ######################################################################################################################
// Basic Library
// ######################################################################################################################

// read write of positive integers
// PLUS data
const flxbyte MASK_PLUS = 0x80;     // 1 bit
const flxbyte MASK_PLUS_DATA = 0x7f;     // 7 bits
const flxbyte DEFAULT_PLUS_LENGTH = 0x7;     // 7 bits

static flxuint _deltalen(flxdelta val) {
	int len = 1;
	while (1) {
		if (val <= MASK_PLUS_DATA) {
			return len;
		}
		val >>= DEFAULT_PLUS_LENGTH;
		len++;
	}
	return 0;
}

static flxuint _pluslen(flxuint val) {
	int len = 1;
	while (1) {
		if (val <= MASK_PLUS_DATA) {
			return len;
		}
		val >>= DEFAULT_PLUS_LENGTH;
		len++;
	}
	return 0;
}

static flxuint _textlen(flxtext text, flxbool incLen) {
	if (!text)
		return 0;
	int len = incLen ? 1 : 0;
	while (*text != '\0') {
		text++;
		len++;
	};
	if (incLen)
		len += _pluslen(len);
	return len;
}

static void _arraycopy(flxbptr source, flxbint sPos, flxbptr target,
		flxbint tPos, flxbint len) {
	int n;
	for (n = 0; n < len; n++)
		target[tPos + n] = source[sPos + n];
}

static flxbint _intlen(int x) {
	flxbint n = sizeof(int) * 8;
	if (x == 0)
		return 0;
	while (1) {
		if (x < 0)
			break;
		n--;
		x <<= 1;
	}
	return n;
}

// ######################################################################################################################
// Low level data writing
// ######################################################################################################################

const flxbyte FLX_SZDF_NONE = 0xff;     // 0 size; >0 (size<<4|val)
const flxbyte FLX_SZDF_SIZEONLY = 0x0;
const flxbyte FLX_DF_DEFAULT = 1;
const flxbyte FLX_DF_LOGIC_2 = 1;
const flxbyte FLX_DF_LOGIC_4 = 2;
const flxbyte FLX_DF_LOGIC_16 = 3;
const flxbyte FLX_DF_ENUM_EVENT = 2;
const flxbyte FLX_XDF_LOGIC_PACK_0 = 0;
const flxbyte FLX_XDF_LOGIC_PACK_1 = 4;
const flxbyte FLX_XDF_LOGIC_PACK_RIGHT_ALLIGNED = 8;
const flxbyte FLX_XDF_INTEGER_32 = 4;
const flxbyte FLX_XDF_INTEGER_64 = 8;
const flxbyte FLX_XDF_FLOAT_32 = 4;
const flxbyte FLX_XDF_FLOAT_64 = 8;
const flxbyte FLX_DF_NONE = 0;

const flxbyte FLX_LOGIC_L2_BYTE_FILL[] = { 0x00, 0xff, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0 };
const flxbyte FLX_LOGIC_L4_BYTE_FILL[] = { 0x00, 0x55, 0xaa, 0xff, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0 };
const flxbyte FLX_LOGIC_L16_BYTE_FILL[] = { 0x00, 0x11, 0x22, 0x33, 0x44, 0x55,
		0x66, 0x77, 0x88, 0x99, 0xaa, 0xbb, 0xcc, 0xdd, 0xee, 0xff };
const flxbyte FLX_STATE_LC_DIGITS[] = { '0', '1', 'z', 'x', 'l', 'h', 'u', 'w',
		'-', 'j', 'k', 'm', 'n', 'o', 'p', '#' };
const flxbyte FLX_STATE_UC_DIGITS[] = { '0', '1', 'Z', 'X', 'L', 'H', 'U', 'W',
		'-', 'J', 'K', 'M', 'N', 'O', 'P', '#' };

static flxuint _deltawrite(flxdelta val, flxbptr bytes, flxbint pos) {
	flxuint written = 1;
	while (1) {
		if (val <= MASK_PLUS_DATA) {
			bytes[pos++] = (flxbyte) (val & MASK_PLUS_DATA);
			return written;
		}
		bytes[pos++] = (flxbyte) ((val & MASK_PLUS_DATA) | MASK_PLUS);
		val >>= DEFAULT_PLUS_LENGTH;
		written++;
	}
	return 0;
}

static flxbint _pluswrite(flxuint val, flxbptr bytes, flxbint pos) {
	flxbint written = 1;
	while (1) {
		if (val <= MASK_PLUS_DATA) {
			bytes[pos++] = (flxbyte) (val & MASK_PLUS_DATA);
			return written;
		}
		bytes[pos++] = (flxbyte) ((val & MASK_PLUS_DATA) | MASK_PLUS);
		val >>= DEFAULT_PLUS_LENGTH;
		written++;
	}
	return 0;
}
static flxbint _pluswritefixed(flxuint val, flxbptr bytes, flxbint pos,
		flxbint size) {
	flxbint written = 1;
	while (1) {
		if (written == size) {
			bytes[pos++] = (flxbyte) (val & MASK_PLUS_DATA);
			return written;
		}
		bytes[pos++] = (flxbyte) ((val & MASK_PLUS_DATA) | MASK_PLUS);
		val >>= DEFAULT_PLUS_LENGTH;
		written++;
	}
	return 0;
}

static flxbint _textwrite(flxtext value, flxbyte szDf, flxbptr bytes,
		flxbint pos) {
	flxuint size = _textlen(value, 0);
	flxbint written = _pluswrite(size, bytes, pos);
	_arraycopy((flxbptr) value, 0, bytes, pos + written, size);
	written += size;
	return written;
}

static flxbint _textnwrite(flxtext value, flxuint size, flxbyte szDf,
		flxbptr bytes, flxbint pos) {
	flxbint written =
			szDf != FLX_SZDF_NONE ?
					_pluswrite(szDf ? ((size << 4) | (szDf & 0x0f)) : size,
							bytes, pos) :
					0;
	_arraycopy((flxbptr) value, 0, bytes, pos + written, size);
	written += size;
	return written;
}

static flxbint _binwrite(flxbptr value, flxuint size, flxbyte szDf,
		flxbptr bytes, flxbint pos) {
	flxbint written =
			szDf != FLX_SZDF_NONE ?
					_pluswrite(szDf ? ((size << 4) | (szDf & 0x0f)) : size,
							bytes, pos) :
					0;
	_arraycopy(value, 0, bytes, pos + written, size);
	written += size;
	return written;
}

static flxbint _intwrite(void* value, flxbyte size, flxbool signd, flxbyte szDf,
		flxbptr bytes, flxbint pos) {

	flxbint written = 0;
	flxbptr vbytes = (flxbptr) value;
	flxsint n = 1;
	flxuint rsize = size;

#if FLX_ENDIANESS == 1 /*little*/
		signd &= size > 0 && (vbytes[size - 1] & 0x80) != 0;
		for (n = size - 1; n >= 0; n--) {
			if (!signd && !vbytes[n] && (n == 0 || (vbytes[n - 1] & 0x80) == 0))
				rsize--;
			else if (signd && n > 0 && vbytes[n] == 0xff
					&& (vbytes[n - 1] & 0x80) != 0)
				rsize--;
			else
				break;
		}
		written +=
				szDf != FLX_SZDF_NONE ?
						_pluswrite(
								szDf ? ((rsize << 4) | (szDf & 0x0f)) : rsize,
								bytes, pos) :
						0;
		for (n = 0; n < rsize; n++) {
			bytes[pos + written++] = vbytes[n];
		}
#else
		signd &= (vbytes[0] & 0x80) != 0;
		for (n = 0; n < size; n++) {
			if (!signd && !vbytes[n]
					&& (n < (size - 1) || (vbytes[n + 1] & 0x80) == 0))
				rsize--;
			else if (signd && n < (size - 1) && vbytes[n] == 0xff
					&& (vbytes[n + 1] & 0x80) != 0)
				rsize--;
			else
				break;
		}
		written +=
				szDf != FLX_SZDF_NONE ?
						_pluswrite(
								szDf ? ((rsize << 4) | (szDf & 0x0f)) : rsize,
								bytes, pos) :
						0;
		for (n = size - 1; n >= size - rsize; n--) {
			bytes[pos + written++] = vbytes[n];
		}
#endif
	return written;
}

static flxbint _intarraywrite(void *value, flxbyte intsize, flxbool signd,
		flxuint count, flxbyte szDf, flxbptr bytes, flxbint pos) {

	flxbint written = 0;
	flxuint n = 0;
	flxbint sizeBits, sizeBytes, size;

// space for size
	if (szDf != FLX_SZDF_NONE) {
		sizeBits = _intlen((1 + intsize) * count);
		sizeBytes = (sizeBits + (szDf ? 4 : 0) + 6) / 7;
		if (!sizeBytes)
			sizeBytes = 1;
		written += sizeBytes;
	}

// write all
	for (n = 0; n < count; n++) {
		written += _intwrite(((flxbptr) value) + n * intsize, intsize, signd,
				FLX_SZDF_SIZEONLY, bytes, pos + written);

	}

// write size
	if (szDf != FLX_SZDF_NONE) {
		size = written - sizeBytes;
		_pluswritefixed(szDf ? ((size << 4) | (szDf & 0x0f)) : size, bytes, pos,
				sizeBytes);
	}
	return written;
}

static flxbint _floatwrite(void* value, flxbyte size, flxbyte szDf,
		flxbptr bytes, flxbint pos) {

	flxbint written = 0;
	flxbptr vbytes = (flxbptr) value;
	flxsint n = 1;

	if (size != 4 && size != 8)
		return 0;

	written +=
			szDf != FLX_SZDF_NONE ?
					_pluswrite(szDf ? ((size << 4) | (szDf & 0x0f)) : size,
							bytes, pos) :
					0;
#if FLX_ENDIANESS == 1 /*little*/
		for (n = 0; n < size; n++) {
			bytes[pos + written++] = vbytes[n];
		}
#else
		for (n = size - 1; n >= 0; n--) {
			bytes[pos + written++] = vbytes[n];
		}
#endif
	return written;
}

static flxbint _floatarraywrite(void *value, flxbyte floatsize, flxuint count,
		flxbyte szDf, flxbptr bytes, flxbint pos) {

	flxbint written = 0;
	flxuint n = 0;
	flxbint size = floatsize * count;

	written +=
			szDf != FLX_SZDF_NONE ?
					_pluswrite(szDf ? ((size << 4) | (szDf & 0x0f)) : size,
							bytes, pos) :
					0;
	for (n = 0; n < count; n++) {
		written += _floatwrite(((flxbptr) value) + n * floatsize, floatsize,
				FLX_SZDF_NONE, bytes, pos + written);

	}
	return written;
}

static flxbint _logicstateswrite(flxbyte stateLevel, flxbyte precedingStates,
		flxbptr value, flxuint size, flxbptr bytes, flxbint pos) {
	flxbint written = 0;
	flxuint n, i;
	flxbint start = 0;
	flxbyte fill;
	flxsint from, to;

// compute level and check  preceding
	if (stateLevel == 0) {
		flxbool crop = 1;
		flxuint totalLen = size;
		flxuint maxState = precedingStates;
		for (n = 0; n < totalLen; n++) {
			if (crop && precedingStates == value[n]) {
				start++;
				size--;
			} else
				crop = 0;
			if (value[n] > maxState)
				maxState = value[n];
		}
		stateLevel =
				maxState >= FLX_STATE_Z_BITS ?
						(maxState >= FLX_STATE_L_BITS ?
						FLX_STATE_LEVEL_16 :
														FLX_STATE_LEVEL_4) :
						FLX_STATE_LEVEL_2;
	}

// size / df
	if (size == 0 && precedingStates == FLX_STATE_0_BITS) {
		written = _pluswrite( FLX_STATE_LEVEL_2 | FLX_XDF_LOGIC_PACK_0, bytes,
				pos);
	} else if (size == 0 && precedingStates == FLX_STATE_1_BITS) {
		written = _pluswrite( FLX_STATE_LEVEL_2 | FLX_XDF_LOGIC_PACK_1, bytes,
				pos);
	} else {

		// calc data length
		flxuint statesPerByte = 8 >> (stateLevel - 1);
		flxuint dlength = (size + statesPerByte) / statesPerByte;
		written = _pluswrite(
				(dlength << 4) | stateLevel | FLX_XDF_LOGIC_PACK_RIGHT_ALLIGNED,
				bytes, pos);

		// write data
		switch (stateLevel) {
		case FLX_STATE_LEVEL_2:
			fill = (flxbyte) FLX_LOGIC_L2_BYTE_FILL[precedingStates];
			to = ((flxsint) size) - dlength * 8;
			for (n = 0; n < dlength; n++) {
				flxbyte d = fill;
				from = to;
				to += 8;
				if (from < 0) {
					from = 0;
				}
				for (i = from; i < to; i++) {
					d = (flxbyte) ((d << 1) | value[start + i]);
				}
				bytes[pos + written++] = (d);
			}
			break;
		case FLX_STATE_LEVEL_4:
			fill = FLX_LOGIC_L4_BYTE_FILL[precedingStates];
			to = ((flxsint) size) - dlength * 4;
			for (n = 0; n < dlength; n++) {
				flxbyte d = fill;
				from = to;
				to += 4;
				if (from < 0) {
					from = 0;
				}
				for (i = from; i < to; i++) {
					d = (flxbyte) ((d << 2) | value[start + i]);
				}
				bytes[pos + written++] = (d);
			}
			break;
		case FLX_STATE_LEVEL_16:
			fill = FLX_LOGIC_L16_BYTE_FILL[precedingStates];
			to = ((flxsint) size) - dlength * 2;
			for (n = 0; n < dlength; n++) {
				flxbyte d = fill;
				from = to;
				to += 2;
				if (from < 0) {
					from = 0;
				}
				for (i = from; i < to; i++) {
					d = (flxbyte) ((d << 4) | value[start + i]);
				}
				bytes[pos + written++] = (d);
			}
			break;
		}

	}
	return written;
}

static flxbint _logictextwrite(flxbyte precedingStates, flxtext value,
		flxuint size, flxbptr bytes, flxbint pos) {

	static flxbyte char2State[256];
	static flxbool init = 0;
	flxuint n = 0;
	flxbyte states[256];

	if (size > 256)
		return 0;

// init conversion
	if (!init) {
		for (n = 0; n < 256; n++) {
			char2State[n] = FLX_STATE_UNKNOWN_BITS;
		}
		for (n = 0; n < 16; n++) {
			char2State[FLX_STATE_LC_DIGITS[n]] = n;
			char2State[FLX_STATE_UC_DIGITS[n]] = n;
		}
		init = 1;
	}

// convert into states
	for (n = 0; n < size; n++)
		states[n] = char2State[(int) value[n]];

// write
	return _logicstateswrite(0, char2State[precedingStates], states, size,
			bytes, pos);
}

static flxbint _memberwrite(flxMemberValue value, flxuint count, flxbyte szDf,
		flxbptr bytes, flxbint pos) {

	flxbint written = 0;
	flxuint n = 0;
	flxbint maxSize, sizeBits, sizeBytes, size;
	flxbyte type;

// space for size
	if (szDf != FLX_SZDF_NONE) {
		maxSize = 0;
		for (n = 0; n < count; n++)
			maxSize +=
					(value + n) != 0 && (value + n)->valid ?
							_pluslen((value + n)->memberId) + 1
									+ _pluslen((value + n)->size)
									+ (value + n)->size :
							0;
		sizeBits = _intlen(maxSize);
		sizeBytes = (sizeBits + (szDf ? 4 : 0) + 6) / 7;
		if (!sizeBytes)
			sizeBytes = 1;
		written += sizeBytes;
	}

// write all
	for (n = 0; n < count; n++) {
		if ((value + n) != 0 && (value + n)->valid) {
			written += _pluswrite((value + n)->memberId, bytes, pos + written);
			type = (value + n)->type;
			bytes[pos + written++] = type;
			type = type & FLX_STRUCTTYPE_MASK_BASE;
			switch (type) {
			case FLX_STRUCTTYPE_ENUM:
			case FLX_STRUCTTYPE_LOCAL_ENUM:
			case FLX_STRUCTTYPE_MERGE_ENUM:
			case FLX_STRUCTTYPE_INTEGER:
				written += _intwrite((value + n)->value, (value + n)->size,
						(value + n)->option, FLX_SZDF_SIZEONLY, bytes,
						pos + written);
				break;
			case FLX_STRUCTTYPE_FLOAT:
				written += _floatwrite((value + n)->value, (value + n)->size,
						FLX_SZDF_SIZEONLY, bytes, pos + written);
				break;
			case FLX_STRUCTTYPE_TEXT:
				written += _textnwrite((flxtext) (value + n)->value,
						(value + n)->size, FLX_SZDF_SIZEONLY, bytes,
						pos + written);
				break;
			case FLX_STRUCTTYPE_BINARY:
				written += _binwrite((flxbptr) (value + n)->value,
						(value + n)->size, FLX_SZDF_SIZEONLY, bytes,
						pos + written);
				break;

			}
		}
	}
// write size
	if (szDf != FLX_SZDF_NONE) {
		size = written - sizeBytes;
		_pluswritefixed(szDf ? ((size << 4) | (szDf & 0x0f)) : size, bytes, pos,
				sizeBytes);
	}
	return written;
}

#ifdef FLX_CONTROL
flxbint _plusread(flxuint* val, flxbptr bytes, flxbptr end) {
	flxuint shift = 0;
	flxbptr pos = bytes;

	*val = 0;
	while (pos <= end) {
		flxbyte sn = *pos++;
		*val = *val | ((sn & MASK_PLUS_DATA) << shift);
		if ((sn & MASK_PLUS) == 0)
			return (flxbint) (pos - bytes);
		shift += DEFAULT_PLUS_LENGTH;
	}
	return 0;
}

static flxbint _intread(void* value, flxbyte size, flxbool signd, flxbptr bytes,
		flxbptr end) {

	flxuint rsize = 0;
	flxbptr pos = bytes;
	flxsint n = 1;
	flxbool little = ((char*) &n)[0] == 1;
	flxbptr vbytes = (flxbptr) value;

	pos += _plusread(&rsize, pos, end);
	if (bytes == pos || rsize > size || (pos + rsize - 1) > end)
		return 0;

	signd &= rsize > 0 && (pos[rsize - 1] & 0x80) != 0;

	if (little) {
		for (n = 0; n < size; n++) {
			if (n < rsize)
				vbytes[n] = pos[n];
			else
				vbytes[n] = signd ? -1 : 0;
		}
	} else {
		for (n = 0; n < size; n++) {
			if (n < rsize)
				vbytes[size - n] = pos[n];
			else
				vbytes[size - n] = signd ? -1 : 0;
		}
	}
	return (flxbint) (pos - bytes);
}

static flxbint _floatread(void* value, flxbyte size, flxbptr bytes, flxbptr end) {

	flxuint rsize = 0;
	flxbptr pos = bytes;
	flxsint n = 1;
	flxbool little = ((char*) &n)[0] == 1;
	flxbptr vbytes = (flxbptr) value;

	pos += _plusread(&rsize, pos, end);
	if (bytes == pos || rsize != size || (pos + rsize - 1) > end)
		return 0;

	if (little) {
		for (n = 0; n < size; n++) {
			vbytes[n] = pos[n];
		}
	} else {
		for (n = 0; n < size; n++) {
			vbytes[size - n] = pos[n];
		}
	}
	return (flxbint) (pos - bytes);
}

static flxbint _binread(flxbptr value, flxuint* size, flxbptr bytes,
		flxbptr end) {
	flxuint rsize = 0;
	flxbptr pos = bytes;

	pos += _plusread(&rsize, pos, end);
	if (bytes == pos || rsize > *size || (pos + rsize - 1) > end)
		return 0;
	_arraycopy((flxbptr) pos, 0, (flxbptr) value, 0, rsize);
	*size = rsize;
	pos += rsize;
	return (flxbint) (pos - bytes);
}

static flxbint _textread(flxtext value, flxuint* size, flxbptr bytes,
		flxbptr end) {
	return _binread((flxbptr) value, size, bytes, end);
}

#endif

// ######################################################################################################################
// entry writing
// ######################################################################################################################

#define REQ1(t) (sizeof(t)+1)

// structure
#define FLX_ENTRY_HEAD 0x01 /* Head */
#define FLX_ENTRY_SWTH 0x04 /* Switch Trace */
#define FLX_ENTRY_PBLK  0x05 /* Packed Block */
#define FLX_ENTRY_SECT  0x06 /* Section Block */

// content definitions
#define FLX_ENTRY_SCPD  0x10 /* Scope Definition*/
#define FLX_ENTRY_SIGD  0x11 /* Signal Definition*/
#define FLX_ENTRY_MSGD  0x12 /* Multi Signals Definition*/
#define FLX_ENTRY_SIRD  0x13 /* Signal Reference Definition*/
#define FLX_ENTRY_SSGD  0x14 /* Scattered Signal Definition*/
#define FLX_ENTRY_SSRD  0x15 /* Scattered Signal Reference Definition*/

// open/close/domain
#define FLX_ENTRY_OPEN  0x20 /* Open */
#define FLX_ENTRY_CLOS  0x21 /* Close */
#define FLX_ENTRY_DOMD  0x22 /* Default Open Domain */
#define FLX_ENTRY_CURR  0x23 /* Current Domain Value */

// legend definitions
#define FLX_ENTRY_ENMD  0x30 /* Enum Definition*/
#define FLX_ENTRY_MEMD  0x31 /* Member Definition*/

// attachments
#define FLX_ENTRY_ATRE  0x40 /* Relation */
#define FLX_ENTRY_ATLA  0x41 /* Label */

// control
#define FLX_ENTRY_CREQ  0x80 /* Control Request */
#define FLX_ENTRY_CRES  0x81 /* Control Result */

// ######################################################################################################################
// structure

// [head]
// \0\1'recTr3'<&:version><&:id><t/0: name><t/0: description><0/1/2:mode><&:maxItemId>
flxresult flxWriteHeadEntry(flxBuffer buffer, flxtext format4, flxid traceId,
		flxtext name, flxtext description, flxbyte mode, flxid maxItemId,
		flxbint maxEntrySize) {

// request buffer
	flxbint request = 6 + REQ1(flxid) * 2 + REQ1(flxbint) + _textlen(name, 1)
			+ _textlen(description, 1);
	flxbptr bytes = 0;
	flxbint written = 0;

	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {
		bytes[written++] = 0;
		bytes[written++] = FLX_ENTRY_HEAD;
		_arraycopy((const flxbptr) format4, 0, bytes, written, 4);
		written += 4;
		bytes[written++] = FLX_VERSION;
		written += _pluswrite(traceId, bytes, written);
		written += _textwrite(name, FLX_SZDF_SIZEONLY, bytes, written);
		written += _textwrite(description, FLX_SZDF_SIZEONLY, bytes, written);
		bytes[written++] = mode;
		written += _pluswrite(maxItemId, bytes, written);
		written += _pluswrite(maxEntrySize, bytes, written);
		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}

flxresult flxWriteSwitchEntry(flxBuffer buffer, flxid traceId) {

// request buffer
	flxbint request = 2 + REQ1(flxid);
	flxbptr bytes = 0;
	flxbint written = 0;

	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {
		bytes[written++] = 0;
		bytes[written++] = FLX_ENTRY_SWTH;
		written += _pluswrite(traceId, bytes, written);
		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}

#ifdef FLX_COMPRESS

flxresult flxWritePackEntry(flxBuffer buffer, flxbyte mode, flxbptr value,
		flxuint size) {
	flxbint request = 0;
	flxbptr bytes = 0;
	flxbint written = 0;
	flxbint sizeLen = 0;
	flxbint compressed = 0;
	flxbyte packed[size];

// compress
	if (mode == FLX_PACK_LZ4)
		compressed = LZ4_compress((const char*) value, (char*) packed, size);
	else if (mode == FLX_PACK_FLZ)
		compressed = fastlz_compress((const char*) value, size, packed);
	else
		return FLX_ERROR_INVALID_PACK_MODE;

	request = 3 + REQ1(flxbint) * 2 + compressed;
	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {

		bytes[written++] = 0;
		bytes[written++] = FLX_ENTRY_PBLK;
		bytes[written++] = mode;
		written += _pluswrite(size, bytes, written); // original size
		written += _pluswrite(compressed, bytes, written); // compressed size
		_arraycopy(packed, 0, bytes, written, compressed);
		written += compressed;
		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}
#endif

flxresult flxWriteSectionEntries(flxBuffer buffer, flxuint noOfSections) {

// request buffer
	flxbint avail = 0;
	flxbptr bytes = 0;
	flxbint written = 0;
	flxuint n;

	if (buffer->access(FLX_BUFFER_AVAIL, buffer, &avail, &bytes) == FLX_OK) {
		flxbint sectionSize = avail / noOfSections;
		flxbint contentSize = sectionSize - SECTION_HEADER_SIZE;
		flxbint lastContentSize = avail
				- sectionSize * (noOfSections - 1)-SECTION_HEADER_SIZE;
		if (lastContentSize < 16 || lastContentSize > 0xffff)
			return FLX_ERROR_BUFFER_NOT_AVAIL;
		for (n = 0; n < noOfSections; n++) {
			bytes[written++] = 0;
			bytes[written++] = FLX_ENTRY_SECT;
			bytes[written++] = (n == noOfSections - 1) ? 0x80 : 0; // counter
			if (n == noOfSections - 1)
				contentSize = lastContentSize;
			bytes[written++] = contentSize & 0xff; // section size
			bytes[written++] = (contentSize >> 8) & 0xff;
			bytes[written++] = 0; // used
			bytes[written++] = 0;
			written += contentSize;
		}
		return buffer->access(FLX_BUFFER_SECCOMMIT, buffer, &avail, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}

// ######################################################################################################################
// content definitions

flxresult flxWriteScopeDefEntry(flxBuffer buffer, flxid itemId, flxid parentId,
		flxtext name, flxtext description) {

// request buffer
	flxbint request = 2 + REQ1(flxid) * 2 + _textlen(name, 1)
			+ _textlen(description, 1);
	flxbptr bytes = 0;
	flxbint written = 0;

	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {
		bytes[written++] = 0;
		bytes[written++] = FLX_ENTRY_SCPD;
		written += _pluswrite(itemId, bytes, written);
		written += _pluswrite(parentId, bytes, written);
		written += _textwrite(name, FLX_SZDF_SIZEONLY, bytes, written);
		written += _textwrite(description, FLX_SZDF_SIZEONLY, bytes, written);

		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}

flxresult flxWriteSignalDefEntry(flxBuffer buffer, flxid itemId, flxid parentId,
		flxtext name, flxtext description, flxbyte signalType,
		flxtext signalDescriptor) {

// request buffer
	flxbint request = 3 + REQ1(flxid) * 2 + _textlen(name, 1)
			+ _textlen(description, 1) + _textlen(signalDescriptor, 1);
	flxbptr bytes = 0;
	flxbint written = 0;

	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {
		bytes[written++] = 0;
		bytes[written++] = FLX_ENTRY_SIGD;
		written += _pluswrite(itemId, bytes, written);
		written += _pluswrite(parentId, bytes, written);
		written += _textwrite(name, FLX_SZDF_SIZEONLY, bytes, written);
		written += _textwrite(description, FLX_SZDF_SIZEONLY, bytes, written);
		bytes[written++] = signalType & 0xf;
		written += _textwrite(signalDescriptor, FLX_SZDF_SIZEONLY, bytes,
				written);
		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}

flxresult flxWriteMultiSignalDefEntry(flxBuffer buffer, flxid itemIdFrom,
		flxid itemIdTo, flxid parentId, flxtext name, flxtext description,
		flxbyte signalType, flxtext signalDescriptor) {

// request buffer
	flxbint request = 3 + REQ1(flxid) * 3 + _textlen(name, 1)
			+ _textlen(description, 1) + _textlen(signalDescriptor, 1);
	flxbptr bytes = 0;
	flxbint written = 0;

	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {
		bytes[written++] = 0;
		bytes[written++] = FLX_ENTRY_MSGD;
		written += _pluswrite(itemIdFrom, bytes, written);
		written += _pluswrite(itemIdTo, bytes, written);
		written += _pluswrite(parentId, bytes, written);
		written += _textwrite(name, FLX_SZDF_SIZEONLY, bytes, written);
		written += _textwrite(description, FLX_SZDF_SIZEONLY, bytes, written);
		bytes[written++] = signalType & 0xf;
		written += _textwrite(signalDescriptor, FLX_SZDF_SIZEONLY, bytes,
				written);
		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}

flxresult flxWriteSignalReferenceDefEntry(flxBuffer buffer, flxid referenceId,
		flxid parentId, flxtext name, flxtext description) {

// request buffer
	flxbint request = 2 + REQ1(flxid) * 2 + _textlen(name, 1)
			+ _textlen(description, 1);
	flxbptr bytes = 0;
	flxbint written = 0;

	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {
		bytes[written++] = 0;
		bytes[written++] = FLX_ENTRY_SIRD;
		written += _pluswrite(referenceId, bytes, written);
		written += _pluswrite(parentId, bytes, written);
		written += _textwrite(name, FLX_SZDF_SIZEONLY, bytes, written);
		written += _textwrite(description, FLX_SZDF_SIZEONLY, bytes, written);
		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}

flxresult flxWriteScatteredSignalDefEntry(flxBuffer buffer, flxid itemId,
		flxid parentId, flxtext name, flxtext description, flxbyte signalType,
		flxtext signalDescriptor, flxuint scatteredFrom, flxuint scatteredTo) {

// request buffer
	flxbint request = 3 + REQ1(flxid) * 2 + REQ1(flxuint) * 2
			+ _textlen(name, 1) + _textlen(description, 1)
			+ _textlen(signalDescriptor, 1) + _pluslen(scatteredFrom)
			+ _pluslen(scatteredTo);
	flxbptr bytes = 0;
	flxbint written = 0;

	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {
		bytes[written++] = 0;
		bytes[written++] = FLX_ENTRY_SSGD;
		written += _pluswrite(itemId, bytes, written);
		written += _pluswrite(parentId, bytes, written);
		written += _textwrite(name, FLX_SZDF_SIZEONLY, bytes, written);
		written += _textwrite(description, FLX_SZDF_SIZEONLY, bytes, written);
		bytes[written++] = signalType & 0xf;
		written += _textwrite(signalDescriptor, FLX_SZDF_SIZEONLY, bytes,
				written);
		written += _pluswrite(scatteredFrom, bytes, written);
		written += _pluswrite(scatteredTo, bytes, written);
		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}

flxresult flxWriteScatteredSignalReferenceDefEntry(flxBuffer buffer,
		flxid referenceId, flxid parentId, flxtext name, flxtext description,
		flxuint scatteredFrom, flxuint scatteredTo) {

// request buffer
	flxbint request = 2 + REQ1(flxid) * 2 + _textlen(name, 1)
			+ _textlen(description, 1) + REQ1(flxuint) * 2;
	flxbptr bytes = 0;
	flxbint written = 0;

	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {
		bytes[written++] = 0;
		bytes[written++] = FLX_ENTRY_SSRD;
		written += _pluswrite(referenceId, bytes, written);
		written += _pluswrite(parentId, bytes, written);
		written += _textwrite(name, FLX_SZDF_SIZEONLY, bytes, written);
		written += _textwrite(description, FLX_SZDF_SIZEONLY, bytes, written);
		written += _pluswrite(scatteredFrom, bytes, written);
		written += _pluswrite(scatteredTo, bytes, written);
		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}

// ######################################################################################################################
// open/close/domain
// <Open/Close>
// [open: open all/signal/scope]
// \0\20<&:itemId(0==all)><t:domain><si:start units><ui:rate 0: discrete>
// [close: close all]
// \0\21<&:itemId(0==all)><si:end units>

flxresult flxWriteOpenEntry(flxBuffer buffer, flxid itemId, flxtext domain,
		flxdomain start, flxdelta rate) {

// request buffer
	flxbint request = 2 + REQ1(flxid) + _textlen(domain, 1)
	+ REQ1(flxdomain) + REQ1(flxdelta);
	flxbptr bytes = 0;
	flxbint written = 0;

	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {
		bytes[written++] = 0;
		bytes[written++] = FLX_ENTRY_OPEN;
		written += _pluswrite(itemId, bytes, written);
		written += _textwrite(domain, FLX_SZDF_SIZEONLY, bytes, written);
		written += _intwrite(&start, sizeof(flxdomain), 1, FLX_SZDF_SIZEONLY,
				bytes, written);
		written += _intwrite(&rate, sizeof(flxdelta), 0, FLX_SZDF_SIZEONLY,
				bytes, written);
		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}

flxresult flxWriteCloseEntry(flxBuffer buffer, flxid itemId, flxdomain end) {

// request buffer
	flxbint request = 2 + REQ1(flxid) + REQ1(flxdomain);
	flxbptr bytes = 0;
	flxbint written = 0;

	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {
		bytes[written++] = 0;
		bytes[written++] = FLX_ENTRY_CLOS;
		written += _pluswrite(itemId, bytes, written);
		written += _intwrite(&end, sizeof(flxdomain), 1, FLX_SZDF_SIZEONLY,
				bytes, written);
		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}

flxresult flxWriteDefaultOpenDomainEntry(flxBuffer buffer, flxtext domain) {

// request buffer
	flxbint request = 2 + _textlen(domain, 1);
	flxbptr bytes = 0;
	flxbint written = 0;

	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {
		bytes[written++] = 0;
		bytes[written++] = FLX_ENTRY_DOMD;
		written += _textwrite(domain, FLX_SZDF_SIZEONLY, bytes, written);
		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}

flxresult flxflxWriteCurrentEntry(flxBuffer buffer, flxid itemId,
		flxdomain domain) {

	flxbint request;
	flxbptr bytes;
	flxbint written = 0;

// request buffer
	request = 2 + REQ1(flxid) + REQ1(flxdomain);

// write
	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {
		bytes[written++] = 0;
		bytes[written++] = FLX_ENTRY_CURR;
		written += _pluswrite(itemId, bytes, written);
		written += _intwrite(&domain, sizeof(flxdomain), 1, FLX_SZDF_SIZEONLY,
				bytes, written);
		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}
// ######################################################################################################################
// enum/member
//[enmd: enum definition]
//\0\12<&:id><&:type><t: enum>

flxresult flxWriteEnumDefEntry(flxBuffer buffer, flxid itemId,
		flxuint enumeration, flxtext label, flxuint value) {

// request buffer
	flxbint request = 2 + REQ1(flxid) + REQ1(flxuint) * 2 + _textlen(label, 1);
	flxbptr bytes = 0;
	flxbint written = 0;

	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {
		bytes[written++] = 0;
		bytes[written++] = FLX_ENTRY_ENMD;
		written += _pluswrite(itemId, bytes, written);
		written += _pluswrite(enumeration, bytes, written);
		written += _textwrite(label, FLX_SZDF_SIZEONLY, bytes, written);
		written += _pluswrite(value, bytes, written);

		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}

// [memd: member definition]
// \0\13<&:id><&:memberId><t:label><#:type><t:content><#:format>

flxresult flxWriteMemberDefEntry(flxBuffer buffer, flxid itemId,
		flxMemberValue member) {
	if (member == 0)
		return FLX_ERROR_INVALID_VALUE;

// request buffer
	flxbint request = 3 + REQ1(flxid) * 2 + _textlen(member->label, 1)
			+ _textlen(member->descriptor, 1);
	flxbptr bytes = 0;
	flxbint written = 0;

	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {
		bytes[written++] = 0;
		bytes[written++] = FLX_ENTRY_MEMD;
		written += _pluswrite(itemId, bytes, written);
		written += _pluswrite(member->memberId, bytes, written);
		written += _textwrite(member->label, FLX_SZDF_SIZEONLY, bytes, written);
		bytes[written++] = member->type;
		written += _textwrite(member->descriptor, FLX_SZDF_SIZEONLY, bytes,
				written);

		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}

// ######################################################################################################################
// relation/label

flxresult flxWriteRelationEntry(flxBuffer buffer, flxid itemId,
		flxuint target, flxuint style, flxsdelta delta) {

// request buffer
	flxbint request = 2 + REQ1(flxid) + REQ1(flxuint) * 2 + REQ1(flxdelta);
	flxbptr bytes = 0;
	flxbint written = 0;

	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {
		bytes[written++] = 0;
		bytes[written++] = FLX_ENTRY_ATRE;
		written += _pluswrite(itemId, bytes, written);
		written += _pluswrite(target, bytes, written);
		written += _pluswrite(style, bytes, written);
		written += _intwrite(&delta, sizeof(flxsdelta), 1, FLX_SZDF_SIZEONLY,
				bytes, written);

		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}

flxresult flxWriteLabelEntry(flxBuffer buffer, flxid itemId, flxuint style,
		flxsint x, flxsint y) {

// request buffer
	flxbint request = 2 + REQ1(flxid) + REQ1(flxuint) + REQ1(flxsint) * 2;
	flxbptr bytes = 0;
	flxbint written = 0;

	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {
		bytes[written++] = 0;
		bytes[written++] = FLX_ENTRY_ATLA;
		written += _pluswrite(itemId, bytes, written);
		written += _pluswrite(style, bytes, written);
		written += _intwrite(&x, sizeof(flxsint), 1, FLX_SZDF_SIZEONLY, bytes,
				written);
		written += _intwrite(&y, sizeof(flxsint), 1, FLX_SZDF_SIZEONLY, bytes,
				written);

		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}

// ######################################################################################################################
// control

flxresult flxWriteControlEntry(flxBuffer buffer, flxbyte entryTag,
		flxid controlId, flxid messageId, flxMemberValue value, flxuint count) {
	flxbint request;
	flxbptr bytes = 0;
	flxbint written = 0;
	flxuint n = 0;
	flxuint rcount = 0;

// request buffer
	request = 2 + REQ1(flxid) * 2 + REQ1(flxuint);
	for (n = 0; n < count; n++)
		if ((value + n) != 0 && (value + n)->valid) {
			request += REQ1(flxid) + REQ1(flxuint) + (value + n)->size;
			rcount++;
		}
	request += _pluslen(rcount);

// write
	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {
		bytes[written++] = 0;
		bytes[written++] = entryTag;
		written += _pluswrite(controlId, bytes, written);
		written += _pluswrite(messageId, bytes, written);
		written += _pluswrite(rcount, bytes, written);

		written += _memberwrite(value, count, FLX_SZDF_NONE, bytes, written);
		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}

flxresult flxWriteControlReqEntry(flxBuffer buffer, flxid controlId,
		flxid messageId, flxMemberValue value, flxuint count) {
	return flxWriteControlEntry(buffer, FLX_ENTRY_CREQ, controlId, messageId,
			value, count);
}
flxresult flxWriteControlResEntry(flxBuffer buffer, flxid controlId,
		flxid messageId, flxMemberValue value, flxuint count) {
	return flxWriteControlEntry(buffer, FLX_ENTRY_CRES, controlId, messageId,
			value, count);
}

// ######################################################################################################################
// data

// [data]
// <&:id+flags><&:delta><&:count>?{a}<&:len><&:data>   ... <&:len><&:data>?{a;count times}
//  flags:
//    id,1
//    id,r,a,c,0
//  c-> conflict
//  a-> array data
//  r2
//  comments:
//  In multi domain, delta per signal, else, delta per core

flxresult flxWriteNoneDataEntry(flxBuffer buffer, flxid itemId,
		flxbool conflict, flxdelta delta) {

	flxbint request;
	flxbptr bytes;
	flxbint written = 0;

// itemId
	if (itemId == 0)
		return FLX_ERROR_INVALID_ID;
	itemId = (itemId << 3) | (conflict ? 1 : 0) | (delta ? 2 : 0);

// request buffer
	request = REQ1(flxid) + REQ1(flxdelta) + 1;

// write
	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {
		written += _pluswrite(itemId, bytes, written);
		if (delta)
			written += _deltawrite(delta, bytes, written);
		bytes[written++] = FLX_DF_NONE;
		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}

flxresult flxWriteIntDataEntry(flxBuffer buffer, flxid itemId, flxbool conflict,
		flxdelta delta, void *value, flxbyte size, flxbool signd) {

	flxbint request;
	flxbptr bytes;
	flxbint written = 0;

// itemId
	if (itemId == 0)
		return FLX_ERROR_INVALID_ID;
	itemId = (itemId << 3) | (conflict ? 1 : 0) | (delta ? 2 : 0);

// request buffer
	request = REQ1(flxid) + REQ1(flxdelta) + 2 + size;

// write
	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {
		written += _pluswrite(itemId, bytes, written);
		if (delta)
			written += _deltawrite(delta, bytes, written);
		written += _intwrite(value, size, signd, FLX_DF_DEFAULT, bytes,
				written);
		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}

flxresult flxWriteIntArrayDataEntry(flxBuffer buffer, flxid itemId,
		flxbool conflict, flxdelta delta, void *value, flxbyte intsize,
		flxbool signd, flxuint count) {
	flxbint request;
	flxbptr bytes = 0;
	flxbint written = 0;

// itemId
	if (itemId == 0)
		return FLX_ERROR_INVALID_ID;
	if (intsize != 4 && intsize != 8)
		return FLX_ERROR_INVALID_DATA_SIZE;
	itemId = (itemId << 3) | (conflict ? 1 : 0) | (delta ? 2 : 0);

// request buffer
	request = REQ1(flxid) + REQ1(flxdelta) + REQ1(flxuint)
			+ (1 + intsize) * count;

// write
	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {
		written += _pluswrite(itemId, bytes, written);
		if (delta)
			written += _deltawrite(delta, bytes, written);
		written += _intarraywrite(value, intsize, signd, count,
				FLX_DF_DEFAULT
						| (intsize == 4 ?
								FLX_XDF_INTEGER_32 : FLX_XDF_INTEGER_64), bytes,
				written);
		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}

flxresult flxWriteFloatDataEntry(flxBuffer buffer, flxid itemId,
		flxbool conflict, flxdelta delta, void *value, flxbyte size) {

	flxbint request;
	flxbptr bytes;
	flxbint written = 0;

// itemId
	if (itemId == 0)
		return FLX_ERROR_INVALID_ID;
	if (size != 4 && size != 8)
		return FLX_ERROR_INVALID_DATA_SIZE;
	itemId = (itemId << 3) | (conflict ? 1 : 0) | (delta ? 2 : 0);

// request buffer
	request = REQ1(flxid) + REQ1(flxdelta) + 2 + size;

// write
	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {
		written += _pluswrite(itemId, bytes, written);
		if (delta)
			written += _deltawrite(delta, bytes, written);
		written += _floatwrite(value, size,
				FLX_DF_DEFAULT
						| (size == 4 ? FLX_XDF_FLOAT_32 : FLX_XDF_FLOAT_64),
				bytes, written);
		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}

flxresult flxWriteFloatArrayDataEntry(flxBuffer buffer, flxid itemId,
		flxbool conflict, flxdelta delta, void *value, flxbyte floatsize,
		flxuint count) {
	flxbint request;
	flxbptr bytes = 0;
	flxbint written = 0;

// itemId
	if (itemId == 0)
		return FLX_ERROR_INVALID_ID;
	if (floatsize != 4 && floatsize != 8)
		return FLX_ERROR_INVALID_DATA_SIZE;
	itemId = (itemId << 3) | (conflict ? 1 : 0) | (delta ? 2 : 0);

// request buffer
	request = REQ1(flxid) + REQ1(flxdelta) + REQ1(flxuint)
			+ (1 + floatsize) * count;

// write
	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {
		written += _pluswrite(itemId, bytes, written);
		if (delta)
			written += _deltawrite(delta, bytes, written);
		written +=
				_floatarraywrite(value, floatsize, count,
						FLX_DF_DEFAULT
								| (floatsize == 4 ?
										FLX_XDF_FLOAT_32 : FLX_XDF_FLOAT_64),
						bytes, written);
		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}

flxresult flxWriteEventDataEntry(flxBuffer buffer, flxid itemId,
		flxbool conflict, flxdelta delta, flxuint value) {

	flxbint request;
	flxbptr bytes;
	flxbint written = 0;

// itemId
	if (itemId == 0)
		return FLX_ERROR_INVALID_ID;
	itemId = (itemId << 3) | (conflict ? 1 : 0) | (delta ? 2 : 0);

// request buffer
	request = REQ1(flxid) + REQ1(flxdelta) + REQ1(flxuint);

// write
	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {
		written += _pluswrite(itemId, bytes, written);
		if (delta)
			written += _deltawrite(delta, bytes, written);
		written += _intwrite(&value, sizeof(flxuint), 0, FLX_DF_ENUM_EVENT,
				bytes, written);
		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}

flxresult flxWriteEventArrayDataEntry(flxBuffer buffer, flxid itemId,
		flxbool conflict, flxdelta delta, flxuint *value, flxuint count) {
	flxbint request;
	flxbptr bytes = 0;
	flxbint written = 0;

// itemId
	if (itemId == 0)
		return FLX_ERROR_INVALID_ID;
	itemId = (itemId << 3) | (conflict ? 1 : 0) | (delta ? 2 : 0);

// request buffer
	request = REQ1(flxid) + REQ1(flxdelta) + REQ1(flxuint)
			+ REQ1(flxuint) * count;

// write
	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {
		written += _pluswrite(itemId, bytes, written);
		if (delta)
			written += _deltawrite(delta, bytes, written);
		written += _intarraywrite(value, sizeof(flxuint), 0, count,
				FLX_DF_ENUM_EVENT, bytes, written);
		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}

flxresult flxWriteTextDataEntry(flxBuffer buffer, flxid itemId,
		flxbool conflict, flxdelta delta, flxtext value, flxuint size) {

	flxbint request;
	flxbptr bytes;
	flxbint written = 0;

// itemId
	if (itemId == 0)
		return FLX_ERROR_INVALID_ID;
	itemId = (itemId << 3) | (conflict ? 1 : 0) | (delta ? 2 : 0);

// request buffer
	request = REQ1(flxid) + REQ1(flxdelta) + REQ1(flxuint) + size;

// write
	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {
		written += _pluswrite(itemId, bytes, written);
		if (delta)
			written += _deltawrite(delta, bytes, written);
		written += _textnwrite(value, size, FLX_DF_DEFAULT, bytes, written);
		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}

flxresult flxWriteBinaryDataEntry(flxBuffer buffer, flxid itemId,
		flxbool conflict, flxdelta delta, flxbptr value, flxuint size) {

	flxbint request;
	flxbptr bytes;
	flxbint written = 0;

// itemId
	if (itemId == 0)
		return FLX_ERROR_INVALID_ID;
	itemId = (itemId << 3) | (conflict ? 1 : 0) | (delta ? 2 : 0);

// request buffer
	request = REQ1(flxid) + REQ1(flxdelta) + REQ1(flxuint) + size;

// write
	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {
		written += _pluswrite(itemId, bytes, written);
		if (delta)
			written += _deltawrite(delta, bytes, written);
		written += _binwrite(value, size, FLX_DF_DEFAULT, bytes, written);
		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}

flxresult flxWriteLogicStatesDataEntry(flxBuffer buffer, flxid itemId,
		flxbool conflict, flxdelta delta, flxbyte precedingStates,
		flxbptr value, flxuint size) {

	flxbint request;
	flxbptr bytes;
	flxbint written = 0;

// itemId
	if (itemId == 0)
		return FLX_ERROR_INVALID_ID;
	itemId = (itemId << 3) | (conflict ? 1 : 0) | (delta ? 2 : 0);

// request buffer
	request = REQ1(flxid) + REQ1(flxdelta) + REQ1(flxuint) + size;

// write
	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {
		written += _pluswrite(itemId, bytes, written);
		if (delta)
			written += _deltawrite(delta, bytes, written);
		written += _logicstateswrite(FLX_STATE_LEVEL_UNKNOWN, precedingStates,
				value, size, bytes, written);
		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}

flxresult flxWriteLogicTextDataEntry(flxBuffer buffer, flxid itemId,
		flxbool conflict, flxdelta delta, flxbyte precedingStates,
		flxtext value, flxuint size) {

	flxbint request;
	flxbptr bytes;
	flxbint written = 0;

// itemId
	if (itemId == 0)
		return FLX_ERROR_INVALID_ID;
	if (size > 256)
		return FLX_ERROR_INVALID_DATA_SIZE;
	itemId = (itemId << 3) | (conflict ? 1 : 0) | (delta ? 2 : 0);

// request buffer
	request = REQ1(flxid) + REQ1(flxdelta) + REQ1(flxuint) + size;

// write
	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {
		written += _pluswrite(itemId, bytes, written);
		if (delta)
			written += _deltawrite(delta, bytes, written);
		written += _logictextwrite(precedingStates, value, size, bytes,
				written);
		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}

flxresult flxWriteMemberDataEntry(flxBuffer buffer, flxid itemId,
		flxbool conflict, flxdelta delta, flxMemberValue value, flxuint count) {
	flxbint request;
	flxbptr bytes = 0;
	flxbint written = 0;
	flxuint n = 0;

// itemId
	if (itemId == 0)
		return FLX_ERROR_INVALID_ID;
	itemId = (itemId << 3) | (conflict ? 1 : 0) | (delta ? 2 : 0);

// request buffer
	request = REQ1(flxid) + REQ1(flxdelta) + REQ1(flxuint);
	for (n = 0; n < count; n++)
		request += (value + n) != 0 && (value + n)->valid ?
		REQ1(flxid) + 1 + REQ1(flxuint) + (value + n)->size :
															0;

// write
	if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request, &bytes) == FLX_OK) {
		written += _pluswrite(itemId, bytes, written);
		if (delta)
			written += _deltawrite(delta, bytes, written);
		written += _memberwrite(value, count, FLX_DF_DEFAULT, bytes, written);
		return buffer->access(FLX_BUFFER_COMMIT, buffer, &written, 0);
	}
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}

// ######################################################################################################################
// Trace level operations
// ######################################################################################################################

flxTrace flxCreateTrace(flxid traceId, flxid maxItemId, flxbint maxEntrySize,
		flxbptr traceBytes, flxbint traceBytesLen, flxBuffer buffer) {
	flxuint n;

	if (traceBytesLen < sizeof(struct flxTraceStruct) || traceBytes == 0)
		return 0;

	flxTrace trace = (flxTrace) traceBytes;
	trace->items = (flxTraceItem) (traceBytes + sizeof(struct flxTraceStruct));
	flxid maxAvailableItems = ((traceBytesLen - sizeof(struct flxTraceStruct))
			/ sizeof(struct flxTraceItemStruct));
	if (maxAvailableItems < maxItemId)
		trace->items = 0;
	trace->id = traceId;
	trace->mode = 0;
	trace->maxItemId = maxItemId;
	trace->maxEntrySize = maxEntrySize;

// init item 0
	trace->current = 0;
	trace->open = FLX_ITEM_OPEN_NONE;

// init items 1..
	if (trace->items != 0)
		for (n = 0; n < maxItemId; n++) {
			trace->items[n].type = FLX_ITEM_TYPE_UNDEFINED;
			trace->items[n].parentId = 0;
			trace->items[n].open = FLX_ITEM_OPEN_NONE;
			trace->items[n].u.current = 0;
		}

	// buffer
	trace->buffer = 0;
	flxSetBuffer(trace, buffer);

	return trace;
}

flxresult flxSetBuffer(flxTrace trace, flxBuffer buffer) {

	if (buffer != 0 && buffer->trace != 0 && buffer->trace != trace)
		return FLX_ERROR_BUFFER_ALLREADY_USED;
	if (trace->buffer != 0)
		trace->buffer->trace = 0;

	trace->buffer = buffer;
	if (trace->buffer != 0)
		trace->buffer->trace = trace;

	return FLX_OK;
}

flxresult flxAddHead(flxTrace trace, flxtext name, flxtext description) {
	if (!trace->buffer)
		return FLX_ERROR_NO_BUFFER;
	return flxWriteHeadEntry(trace->buffer, "flux", trace->id, name,
			description, FLX_MODE_HEAD_NORMAL, trace->maxItemId, trace->maxEntrySize);
}
flxresult flxAddModeHead(flxTrace trace, flxtext name, flxtext description, flxbyte mode) {
	if (!trace->buffer)
		return FLX_ERROR_NO_BUFFER;
	return flxWriteHeadEntry(trace->buffer, "flux", trace->id, name,
			description, mode, trace->maxItemId, trace->maxEntrySize);
}
flxresult flxAddHeadDerived(flxTrace trace, flxtext format4, flxtext name,
		flxtext description) {
	if (!trace->buffer)
		return FLX_ERROR_NO_BUFFER;
	return flxWriteHeadEntry(trace->buffer, format4, trace->id, name,
			description, FLX_MODE_HEAD_NORMAL, trace->maxItemId, trace->maxEntrySize);
}

flxresult flxAddSections(flxTrace trace, flxuint noOfSections) {
	if (!trace->buffer)
		return FLX_ERROR_NO_BUFFER;
	return flxWriteSectionEntries(trace->buffer, noOfSections);
}

flxresult flxAddScope(flxTrace trace, flxid itemId, flxid parentId,
		flxtext name, flxtext description) {
	if (!trace->buffer)
		return FLX_ERROR_NO_BUFFER;
	if (itemId == 0 || itemId > trace->maxItemId || parentId > trace->maxItemId)
		return FLX_ERROR_INVALID_ID;
	if (trace->items != 0) {
		if (trace->items[itemId - 1].type != FLX_ITEM_TYPE_UNDEFINED)
			return FLX_ERROR_ITEM_ALLREADY_DEFINED;
		if (parentId
				!= 0&& trace->items[parentId - 1].type != FLX_ITEM_TYPE_SCOPE)
			return FLX_ERROR_PARENT_NOT_DEFINED;
		trace->items[itemId - 1].type = FLX_ITEM_TYPE_SCOPE;
		trace->items[itemId - 1].open = FLX_ITEM_OPEN_NONE;
		trace->items[itemId - 1].parentId = parentId;
	}

	return flxWriteScopeDefEntry(trace->buffer, itemId, parentId, name,
			description);
}

flxresult flxAddSignal(flxTrace trace, flxid itemId, flxid parentId,
		flxtext name, flxtext description, flxbyte signalType,
		flxtext signalDescriptor) {
	if (!trace->buffer)
		return FLX_ERROR_NO_BUFFER;
	if (itemId == 0 || itemId > trace->maxItemId || parentId > trace->maxItemId)
		return FLX_ERROR_INVALID_ID;
	if (trace->items != 0) {
		if (trace->items[itemId - 1].type != FLX_ITEM_TYPE_UNDEFINED)
			return FLX_ERROR_ITEM_ALLREADY_DEFINED;
		if (parentId
				!= 0&& trace->items[parentId - 1].type != FLX_ITEM_TYPE_SCOPE)
			return FLX_ERROR_PARENT_NOT_DEFINED;
		trace->items[itemId - 1].type = FLX_ITEM_TYPE_SIGNAL;
		trace->items[itemId - 1].open = FLX_ITEM_OPEN_NONE;
		trace->items[itemId - 1].parentId = parentId;
	}
	return flxWriteSignalDefEntry(trace->buffer, itemId, parentId, name,
			description, signalType, signalDescriptor);
}

flxresult flxAddSignals(flxTrace trace, flxid itemIdFrom, flxid itemIdTo,
		flxid parentId, flxtext name, flxtext description, flxbyte signalType,
		flxtext signalDescriptor) {
	flxuint itemId;
	if (!trace->buffer)
		return FLX_ERROR_NO_BUFFER;
	for (itemId = itemIdFrom; itemId <= itemIdTo; itemId++) {
		if (itemId == 0 || itemId > trace->maxItemId
				|| parentId > trace->maxItemId)
			return FLX_ERROR_INVALID_ID;
		if (trace->items != 0) {
			if (trace->items[itemId - 1].type != FLX_ITEM_TYPE_UNDEFINED)
				return FLX_ERROR_ITEM_ALLREADY_DEFINED;
			if (parentId
					!= 0&& trace->items[parentId - 1].type != FLX_ITEM_TYPE_SCOPE)
				return FLX_ERROR_PARENT_NOT_DEFINED;
			trace->items[itemId - 1].type = FLX_ITEM_TYPE_SIGNAL;
			trace->items[itemId - 1].open = FLX_ITEM_OPEN_NONE;
			trace->items[itemId - 1].parentId = parentId;
		}
	}
	return flxWriteMultiSignalDefEntry(trace->buffer, itemIdFrom, itemIdTo,
			parentId, name, description, signalType, signalDescriptor);
}

flxresult flxAddSignalReference(flxTrace trace, flxid referenceId,
		flxid parentId, flxtext name, flxtext description) {
	if (!trace->buffer)
		return FLX_ERROR_NO_BUFFER;
	if (referenceId == 0 || referenceId > trace->maxItemId
			|| parentId > trace->maxItemId)
		return FLX_ERROR_INVALID_ID;
	if (trace->items != 0) {
		if (trace->items[referenceId - 1].type != FLX_ITEM_TYPE_SIGNAL)
			return FLX_ERROR_ITEM_NOT_DEFINED;
		if (parentId
				!= 0&& trace->items[parentId - 1].type != FLX_ITEM_TYPE_SCOPE)
			return FLX_ERROR_PARENT_NOT_DEFINED;
	}
	return flxWriteSignalReferenceDefEntry(trace->buffer, referenceId, parentId,
			name, description);
}

flxresult flxAddScatteredSignal(flxTrace trace, flxid itemId, flxid parentId,
		flxtext name, flxtext description, flxbyte signalType,
		flxtext signalDescriptor, flxuint scatteredFrom, flxuint scatteredTo) {
	if (!trace->buffer)
		return FLX_ERROR_NO_BUFFER;
	if (itemId == 0 || itemId > trace->maxItemId || parentId > trace->maxItemId)
		return FLX_ERROR_INVALID_ID;
	if (trace->items != 0) {
		if (trace->items[itemId - 1].type != FLX_ITEM_TYPE_UNDEFINED)
			return FLX_ERROR_ITEM_ALLREADY_DEFINED;
		if (parentId
				!= 0&& trace->items[parentId - 1].type != FLX_ITEM_TYPE_SCOPE)
			return FLX_ERROR_PARENT_NOT_DEFINED;
		trace->items[itemId - 1].type = FLX_ITEM_TYPE_SIGNAL;
		trace->items[itemId - 1].open = FLX_ITEM_OPEN_NONE;
		trace->items[itemId - 1].parentId = parentId;
	}
	return flxWriteScatteredSignalDefEntry(trace->buffer, itemId, parentId,
			name, description, signalType, signalDescriptor, scatteredFrom,
			scatteredTo);
}

flxresult flxAddScatteredSignalReference(flxTrace trace, flxid referenceId,
		flxid parentId, flxtext name, flxtext description,
		flxuint scatteredFrom, flxuint scatteredTo) {
	if (!trace->buffer)
		return FLX_ERROR_NO_BUFFER;
	if (referenceId == 0 || referenceId > trace->maxItemId
			|| parentId > trace->maxItemId)
		return FLX_ERROR_INVALID_ID;
	if (trace->items != 0) {
		if (trace->items[referenceId - 1].type != FLX_ITEM_TYPE_SIGNAL)
			return FLX_ERROR_ITEM_NOT_DEFINED;
		if (parentId
				!= 0&& trace->items[parentId - 1].type != FLX_ITEM_TYPE_SCOPE)
			return FLX_ERROR_PARENT_NOT_DEFINED;
	}
	return flxWriteScatteredSignalReferenceDefEntry(trace->buffer, referenceId,
			parentId, name, description, scatteredFrom, scatteredTo);
}

flxbool flxIsSignal(flxTrace trace, flxid itemId) {
	return trace->items != 0
			&& trace->items[itemId - 1].type == FLX_ITEM_TYPE_SIGNAL;
}

flxbool flxIsScope(flxTrace trace, flxid itemId) {
	return trace->items != 0
			&& trace->items[itemId - 1].type == FLX_ITEM_TYPE_SCOPE;
}

flxresult flxOpen(flxTrace trace, flxid itemId, flxtext domainBase,
		flxdomain start, flxdelta rate) {
	flxid n;
	if (!trace->buffer)
		return FLX_ERROR_NO_BUFFER;

// check id
	if (itemId >= trace->maxItemId || (trace->items == 0 && itemId > 0))
		return FLX_ERROR_INVALID_ID;

// check if item is open
	if (itemId == 0) {
		if (trace->open != FLX_ITEM_OPEN_NONE)
			return FLX_ERROR_ALLREADY_OPEN;
	} else {
		if (trace->items[itemId - 1].open != FLX_ITEM_OPEN_NONE)
			return FLX_ERROR_ALLREADY_OPEN;
	}
// check if children are open
	if (trace->items != 0)
		for (n = 1; n < trace->maxItemId; n++) {
			if (trace->items[n - 1].open != FLX_ITEM_OPEN_NONE) {
				flxid p = trace->items[itemId - 1].parentId;
				while (1) {
					if (p == itemId)
						return FLX_ERROR_CHILDREN_ALLREADY_OPEN;
					if (p == 0)
						break;
					p = trace->items[p - 1].parentId;
				}
			}
		}

// open item
	if (itemId == 0) {
		trace->open = FLX_ITEM_OPEN_LOCAL;
		trace->current = start;
	} else {
		trace->items[itemId - 1].open = FLX_ITEM_OPEN_LOCAL;
		trace->items[itemId - 1].u.current = start;
	}
// indicate open in children
	if (trace->items != 0)
		for (n = 1; n < trace->maxItemId; n++) {
			flxid p = trace->items[n - 1].parentId;
			while (1) {
				if (p == itemId) {
					trace->items[n - 1].open = FLX_ITEM_OPEN_CONTAINER;
					trace->items[n - 1].u.openId = itemId;
					break;
				}
				if (p == 0)
					break;
				p = trace->items[p - 1].parentId;
			}
		}
	return flxWriteOpenEntry(trace->buffer, itemId, domainBase, start, rate);
}

flxresult flxClose(flxTrace trace, flxid itemId, flxdomain end) {
	flxid n;
	if (!trace->buffer)
		return FLX_ERROR_NO_BUFFER;

// check id
	if (itemId >= trace->maxItemId || (trace->items == 0 && itemId > 0))
		return FLX_ERROR_INVALID_ID;

// check if item is open
	flxdomain current;
	if (itemId == 0) {
		if (trace->open != FLX_ITEM_OPEN_LOCAL)
			return FLX_ERROR_NOT_OPEN;
		current = trace->current;
	} else {
		if (trace->items[itemId - 1].open != FLX_ITEM_OPEN_LOCAL)
			return FLX_ERROR_NOT_OPEN;
		current = trace->items[itemId - 1].u.current;
	}

// adjust end
	if (end < current)
		end = current + 1;

// remove open indication in children
	if (trace->items != 0)
		for (n = 1; n < trace->maxItemId; n++) {
			flxid p = trace->items[n - 1].parentId;
			while (1) {
				if (p == itemId) {
					trace->items[n - 1].open = FLX_ITEM_OPEN_NONE;
					trace->items[n - 1].u.current = 0;
					break;
				}
				if (p == 0)
					break;
				p = trace->items[p - 1].parentId;
			}
		}
	return flxWriteCloseEntry(trace->buffer, itemId, end);
}

flxresult flxSetDefaultOpenDomain(flxTrace trace, flxtext domainBase) {
	if (!trace->buffer)
		return FLX_ERROR_NO_BUFFER;

	return flxWriteDefaultOpenDomainEntry(trace->buffer, domainBase);
}

flxbool flxIsOpen(flxTrace trace, flxid itemId) {
	return (trace->open == FLX_ITEM_OPEN_LOCAL)
			|| (trace->items != 0
					&& trace->items[itemId - 1].open != FLX_ITEM_OPEN_NONE);
}

flxdomain flxGetCurrent(flxTrace trace, flxid itemId) {
	flxid openId;
	if (trace->open == 0 && trace->items != 0) {
		if (trace->items[itemId - 1].open == FLX_ITEM_OPEN_LOCAL)
			openId = itemId;
		else if (trace->items[itemId - 1].open == FLX_ITEM_OPEN_CONTAINER) {
			openId = trace->items[itemId - 1].u.openId;\
			if (trace->items[openId - 1].open != FLX_ITEM_OPEN_LOCAL)
				return FLX_ERROR_NOT_OPEN;
		} else
			return FLX_ERROR_NOT_OPEN;
		return trace->items[openId - 1].u.current;
	} else {
		if (trace->open != FLX_ITEM_OPEN_LOCAL)
			return FLX_ERROR_NOT_OPEN;
		return trace->current;
	}
	return 0;
}

flxresult flxWriteEnumDef(flxTrace trace, flxid itemId, flxuint enumeration,
		flxtext label, flxuint value) {

	if (!trace->buffer)
		return FLX_ERROR_NO_BUFFER;
	if (!flxIsOpen(trace, itemId))
		return FLX_ERROR_NOT_OPEN;
// write value
	return flxWriteEnumDefEntry(trace->buffer, itemId, enumeration, label,
			value);
}

flxresult flxWriteArrayDef(flxTrace trace, flxid itemId, flxuint index,
		flxtext label) {

	if (!trace->buffer)
		return FLX_ERROR_NO_BUFFER;
	if (!flxIsOpen(trace, itemId))
		return FLX_ERROR_NOT_OPEN;

// write value
	struct flxMemberValueStruct member;
	flxInitMember(&member, index, label, FLX_STRUCTTYPE_UNKNOWN, 0);
	return flxWriteMemberDefEntry(trace->buffer, itemId, &member);
}

flxresult flxInitMember(flxMemberValue member, flxid memberId, flxtext label,
		flxbyte memberType, flxtext memberDescriptor) {

	member->memberId = memberId;
	member->label = label;
	member->type = memberType;
	member->descriptor = memberDescriptor;
	member->value = 0;
	member->valid = 0;
	return FLX_OK;
}

flxresult flxWriteMemberDef(flxTrace trace, flxid itemId, flxid memberId, flxtext label,
		flxbyte memberType, flxtext memberDescriptor) {

	if (!trace->buffer)
		return FLX_ERROR_NO_BUFFER;
	if (!flxIsOpen(trace, itemId))
		return FLX_ERROR_NOT_OPEN;

// write value
	struct flxMemberValueStruct member;
	flxInitMember(&member, memberId, label, memberType, memberDescriptor);
	return flxWriteMemberDefEntry(trace->buffer, itemId, &member);
}

flxresult flxWriteMemberDefs(flxTrace trace, flxid itemId,
		flxMemberValue member, flxuint count) {
	flxuint n;
	flxresult result;

	if (!trace->buffer)
		return FLX_ERROR_NO_BUFFER;
	if (!flxIsOpen(trace, itemId))
		return FLX_ERROR_NOT_OPEN;

	for (n = 0; n < count; n++) {
		result = flxWriteMemberDefEntry(trace->buffer, itemId, member + n);
		if (result != FLX_OK)
			return result;
	}
	return FLX_OK;
}

#define FLX_WRITE_AT(x)\
	flxresult r;\
	flxid openId = 0;\
	flxdomain* current;\
	if (!trace->buffer)\
		return FLX_ERROR_NO_BUFFER;\
	if (trace->open == 0 && trace->items != 0) {\
		if (trace->items[itemId - 1].open == FLX_ITEM_OPEN_LOCAL)\
			openId = itemId;\
		else if (trace->items[itemId - 1].open == FLX_ITEM_OPEN_CONTAINER) {\
			openId = trace->items[itemId - 1].u.openId;\
			if (trace->items[openId - 1].open != FLX_ITEM_OPEN_LOCAL)\
				return FLX_ERROR_NOT_OPEN;\
		} else\
			return FLX_ERROR_NOT_OPEN;\
		current = &(trace->items[openId - 1].u.current);\
	} else {\
		if (trace->open != FLX_ITEM_OPEN_LOCAL)\
			return FLX_ERROR_NOT_OPEN;\
		current = &(trace->current);\
	}\
	flxdomain delta = isDelta ? domainPosition : domainPosition - *current;\
	if (delta < 0)\
		return FLX_ERROR_POSITION_LESSTHAN_CURRENT;\
	r = x ;\
	if (r == FLX_OK){\
		if (isDelta)\
			*current += delta;\
		else\
			*current = domainPosition;\
	}\
	return r;

flxresult flxWriteCurrent(flxTrace trace, flxid itemId,
		flxdomain domainPosition) {
	int isDelta = 0;
	FLX_WRITE_AT(flxflxWriteCurrentEntry(trace->buffer, itemId, domainPosition));
}

flxresult flxWriteNoneAt(flxTrace trace, flxid itemId, flxbool conflict,
		flxdomain domainPosition, flxbool isDelta) {
	FLX_WRITE_AT(flxWriteNoneDataEntry(trace->buffer, itemId, conflict, delta))
}

flxresult flxWriteIntAt(flxTrace trace, flxid itemId, flxbool conflict,
		flxdomain domainPosition, flxbool isDelta, void *value, flxbyte size,
		flxbool signd) {
	FLX_WRITE_AT(
			flxWriteIntDataEntry(trace->buffer, itemId, conflict, delta, value,
					size, signd))
}

flxresult flxWriteIntArrayAt(flxTrace trace, flxid itemId, flxbool conflict,
		flxdomain domainPosition, flxbool isDelta, void *value, flxbyte intsize,
		flxbool signd, flxuint count) {
	FLX_WRITE_AT(
			flxWriteIntArrayDataEntry(trace->buffer, itemId, conflict, delta,
					value, intsize, signd, count))
}

flxresult flxWriteFloatAt(flxTrace trace, flxid itemId, flxbool conflict,
		flxdomain domainPosition, flxbool isDelta, void *value, flxbyte size) {
	FLX_WRITE_AT(
			flxWriteFloatDataEntry(trace->buffer, itemId, conflict, delta,
					value, size))
}

flxresult flxWriteFloatArrayAt(flxTrace trace, flxid itemId, flxbool conflict,
		flxdomain domainPosition, flxbool isDelta, void *value,
		flxbyte floatsize, flxuint count) {
	FLX_WRITE_AT(
			flxWriteFloatArrayDataEntry(trace->buffer, itemId, conflict, delta,
					value, floatsize, count))
}

flxresult flxWriteEventAt(flxTrace trace, flxid itemId, flxbool conflict,
		flxdomain domainPosition, flxbool isDelta, flxuint value) {
	FLX_WRITE_AT(
			flxWriteEventDataEntry(trace->buffer, itemId, conflict, delta,
					value))
}

flxresult flxWriteEventArrayAt(flxTrace trace, flxid itemId, flxbool conflict,
		flxdomain domainPosition, flxbool isDelta, flxuint* value,
		flxuint count) {
	FLX_WRITE_AT(
			flxWriteEventArrayDataEntry(trace->buffer, itemId, conflict, delta,
					value, count))
}

flxresult flxWriteTextAt(flxTrace trace, flxid itemId, flxbool conflict,
		flxdomain domainPosition, flxbool isDelta, flxtext value, flxuint size) {
	FLX_WRITE_AT(
			flxWriteTextDataEntry(trace->buffer, itemId, conflict, delta, value,
					size))
}

flxresult flxWriteBinaryAt(flxTrace trace, flxid itemId, flxbool conflict,
		flxdomain domainPosition, flxbool isDelta, flxbptr value, flxuint size) {
	FLX_WRITE_AT(
			flxWriteBinaryDataEntry(trace->buffer, itemId, conflict, delta,
					value, size))
}

flxresult flxWriteLogicStatesAt(flxTrace trace, flxid itemId, flxbool conflict,
		flxdomain domainPosition, flxbool isDelta, flxbyte precedingStates,
		flxbptr value, flxuint size) {
	FLX_WRITE_AT(
			flxWriteLogicStatesDataEntry(trace->buffer, itemId, conflict, delta,
					precedingStates, value, size))
}
flxresult flxWriteLogicTextAt(flxTrace trace, flxid itemId, flxbool conflict,
		flxdomain domainPosition, flxbool isDelta, flxbyte precedingStates,
		flxtext value, flxuint size) {
	FLX_WRITE_AT(
			flxWriteLogicTextDataEntry(trace->buffer, itemId, conflict, delta,
					precedingStates, value, size))
}

flxresult flxSetMember(flxMemberValue member, void* value, flxuint size,
		flxuint option, flxbool valid) {

	member->value = value;
	member->size = size;
	member->option = option;
	member->valid = valid;
	return FLX_OK;
}

flxresult flxWriteMembersAt(flxTrace trace, flxid itemId, flxbool conflict,
		flxdomain domainPosition, flxbool isDelta, flxMemberValue value,
		flxuint size) {
	FLX_WRITE_AT(
			flxWriteMemberDataEntry(trace->buffer, itemId, conflict, delta,
					value, size))
}

flxresult flxWriteRelation(flxTrace trace, flxid itemId, flxuint target,
		flxuint style, flxsdelta delta) {

	if (!trace->buffer)
		return FLX_ERROR_NO_BUFFER;
	if (!flxIsOpen(trace, itemId))
		return FLX_ERROR_NOT_OPEN;
// write value
	return flxWriteRelationEntry(trace->buffer, itemId, target, style, delta);
}

flxresult flxWriteLabel(flxTrace trace, flxid itemId, flxuint style, flxsint x,
		flxsint y) {

	if (!trace->buffer)
		return FLX_ERROR_NO_BUFFER;
	if (!flxIsOpen(trace, itemId))
		return FLX_ERROR_NOT_OPEN;
// write value
	return flxWriteLabelEntry(trace->buffer, itemId, style, x, y);
}

flxresult flxWriteControlRequest(flxTrace trace, flxid controlId,
		flxid messageId, flxMemberValue value, flxuint count) {

	if (!trace->buffer)
		return FLX_ERROR_NO_BUFFER;

	return flxWriteControlReqEntry(trace->buffer, controlId, messageId, value,
			count);
}
flxresult flxWriteControlResult(flxTrace trace, flxid controlId,
		flxid messageId, flxMemberValue value, flxuint count) {

	if (!trace->buffer)
		return FLX_ERROR_NO_BUFFER;

	return flxWriteControlResEntry(trace->buffer, controlId, messageId, value,
			count);
}
flxresult flxFlush(flxTrace trace) {

	if (!trace->buffer)
		return FLX_ERROR_NO_BUFFER;

	return flxDeepFlushBuffer(trace->buffer);
}
// ######################################################################################################################
// Buffer Handler
// ######################################################################################################################
flxresult flxCopy(flxbyte command, void* buffer, flxbint* len, flxbyte* bytes,
		void*user) {

	flxBuffer buffer2 = (flxBuffer) user;
	flxbint request = *len;
	flxbptr tbytes = 0;
	if (buffer2->access(FLX_BUFFER_REQUEST, buffer2, &request,
			&tbytes) == FLX_OK) {
		_arraycopy(bytes, 0, tbytes, 0, *len);
		return buffer2->access(FLX_BUFFER_COMMIT, buffer2, len, 0);
	}
	if (command == FLX_BUFFER_DEEPFLUSH)
		flxDeepFlushBuffer(buffer2);
	return FLX_ERROR_BUFFER_NOT_AVAIL;
}
#ifdef FLX_COMPRESS
flxresult flxCompressLz4(flxbyte command, void* buffer, flxbint* len,
		flxbyte* bytes, void*user) {

	flxBuffer buffer2 = (flxBuffer) user;
	flxWritePackEntry(buffer2, FLX_PACK_LZ4, bytes, *len);
	if (command == FLX_BUFFER_DEEPFLUSH)
		flxDeepFlushBuffer(buffer2);
	return FLX_OK;
}
flxresult flxCompressFlz(flxbyte command, void* buffer, flxbint* len,
		flxbyte* bytes, void*user) {

	flxBuffer buffer2 = (flxBuffer) user;
	flxWritePackEntry(buffer2, FLX_PACK_FLZ, bytes, *len);
	if (command == FLX_BUFFER_DEEPFLUSH)
		flxDeepFlushBuffer(buffer2);
	return FLX_OK;
}
#endif
#ifdef FLX_STDIO
flxresult flxWriteToFile(flxbyte command, void* buffer, flxbint* len,
		flxbyte* bytes, void*user) {

	fwrite(bytes, *len, 1, (FILE*) user);
	fflush((FILE*) user);
	return FLX_OK;
}
#endif

#ifdef FLX_CONTROL
#ifdef FLX_STDIO
flxresult flxParseControlInput(FILE* file, flxbint maxEntrySize,
		flxHandleControlParse handleControlParse) {

// buffer
	unsigned bufferSize = FLX_BUFFER_BYTES(maxEntrySize);
	unsigned char memoryBuffer[bufferSize];
	flxBuffer buffer = flxCreateFixedBuffer(memoryBuffer, bufferSize,
			flxHandleControl, (void*) handleControlParse);

	while (1) {
		flxbint request;
		flxbptr bytes;
		flxbint read;
		flxresult result;
		flxbool flush;

		request = fgetc(file);
		flush = request & 0x80;
		request = request & 0x7f;
		if (buffer->access(FLX_BUFFER_REQUEST, buffer, &request,
				&bytes) == FLX_OK) {
			read = fread(bytes, 1, request, file);
			if (read == request) {
				result = buffer->access(FLX_BUFFER_COMMIT, buffer, &read, 0);
				if (result != FLX_OK)
					return result;
				if (flush) {
					result = flxFlushBuffer(buffer);
					if (result != FLX_OK)
						return result;
				}
			} else
				return FLX_ERROR_INSUFFICIENT_INPUT;
		} else
			return FLX_ERROR_BUFFER_NOT_AVAIL;
	}
	return FLX_OK;
}
#endif
flxresult flxHandleControl(flxbyte command, void* buffer, flxbint* len,
		flxbyte* bytes, void* user) {
	flxuint n;
	flxbptr pos = bytes;
	flxbptr end = bytes + *len - 1;
	flxuint read;
	flxuint skip;

	flxuint controlId;
	flxuint messageId;
	flxuint count;
	flxid memberId;
	flxbyte type;
	void *value;
	flxuint size;
	flxuint opt;
	flxuint rsize;

//printf("flxHandleControl\n");
	*len = 0;

// empty command
	while (end > pos) {

		if (end - pos < 2)
			return FLX_ERROR_COMMAND_PARSE_NEED_MORE_DATA;
		if (*(pos++) != 0) {
			*len = pos - bytes;
			return FLX_ERROR_COMMAND_PARSE_ERROR;
		}
		if (*pos == 0x80) {
			pos++;
			// read controlId, messageId and parameter count
			read = _plusread(&controlId, pos, end);
			if (read == 0)
				return FLX_ERROR_COMMAND_PARSE_NEED_MORE_DATA;
			pos += read;
			read = _plusread(&messageId, pos, end);
			if (read == 0)
				return FLX_ERROR_COMMAND_PARSE_NEED_MORE_DATA;
			pos += read;
			read = _plusread(&count, pos, end);
			if (read == 0)
				return FLX_ERROR_COMMAND_PARSE_NEED_MORE_DATA;
			pos += read;

			//printf("flxHandleControl %i %i %i\n",controlId,messageId,count);
			if (user) {
				((flxHandleControlParse) user)(
				FLX_CONTROL_HANDLE_ENTER_MESSAGE, controlId, messageId, 0, 0, 0,
						0, 0);
			}

			for (n = 0; n < count; n++) {

				// read id, type and peek length of value
				read = _plusread(&memberId, pos, end);
				if (read == 0)
					return FLX_ERROR_COMMAND_PARSE_NEED_MORE_DATA;
				pos += read;

				type = *(pos++);

				read = _plusread(&size, pos, end);
				if (read == 0)
					return FLX_ERROR_COMMAND_PARSE_NEED_MORE_DATA;
				skip = read + size;

				if (user) {
					value = 0;
					rsize = size;
					if (((flxHandleControlParse) user)(
					FLX_CONTROL_HANDLE_PARSE_PARAMETER, controlId, messageId,
							memberId, type, &value, &rsize, &opt) == FLX_OK) {
						if (value && rsize > 0) {
							type = type & FLX_STRUCTTYPE_MASK_BASE;
							read = 0;
							switch (type) {
							case FLX_STRUCTTYPE_ENUM:
							case FLX_STRUCTTYPE_LOCAL_ENUM:
							case FLX_STRUCTTYPE_MERGE_ENUM:
							case FLX_STRUCTTYPE_INTEGER:
								if (rsize >= size)
									read = _intread((void*) value, rsize, opt,
											pos, end);
								break;
							case FLX_STRUCTTYPE_FLOAT:
								if (rsize == size)
									read = _floatread((void*) value, rsize, pos,
											end);
								break;
							case FLX_STRUCTTYPE_TEXT:
								if (rsize >= size)
									read = _textread((flxtext) value, &rsize,
											pos, end);
								break;
							case FLX_STRUCTTYPE_BINARY:
								if (rsize >= size)
									read = _binread((flxbptr) value, &rsize,
											pos, end);
								break;
							default:
								return FLX_ERROR_COMMAND_PARSE_ERROR;

							}
							if (read == 0)
								return FLX_ERROR_COMMAND_PARSE_NEED_MORE_DATA;
						}
					}
				}
				pos += skip;
			}
			if (user) {
				((flxHandleControlParse) user)(
				FLX_CONTROL_HANDLE_LEAVE_MESSAGE, controlId, messageId, 0, 0, 0,
						0, 0);
			}

		} else if (*pos == 0x81) {
			pos++;
		} else
			return FLX_ERROR_COMMAND_PARSE_ERROR;

		*len = pos - bytes;
	}
	return FLX_OK;
}
#endif

#ifdef __cplusplus
}
#endif

