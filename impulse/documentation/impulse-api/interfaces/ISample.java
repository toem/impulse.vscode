package de.toem.impulse.samples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.toem.impulse.i18n.I18n;
import de.toem.toolkits.core.Utils;

/**
 * Interface defining the sample format, data types, and constants for the Impulse signal processing system.
 * This interface provides detailed specifications for sample encoding, formatting, and interpretation across
 * various data types including logic, float, integer, text, enums, and binary data.
 * 
 * It defines:
 * - Byte-level format specifications for samples and attachments
 * - Constants for masks, shifts, and flags used in sample formatting
 * - Data type definitions and operations
 * - Encoding and decoding methods for different data representations
 * - Group and layer management structures
 * - Attachment types and relation specifications
 * 
 * This interface serves as the core specification for sample data representation in the Impulse system.
 *
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved. 
 * docID: 39
 */
public interface ISample {

    // SAMPLE FORMAT
    //
    // FORMAT0 1 byte
    // FORMAT1 1 byte
    // Optional SIZE and GROUP bytes
    // DATA 0 - N
    // UNITS 0,1,2,4,8 bytes delta (Discrete only)
    //
    // ------- |7|6|5|4|3|2|1|0|
    // FORMAT0 |DF-|X|XDF|GO |T|
    // FORMAT1 |UF---|+|S0-----|
    // TAG | | if T & X
    // {Sn ..} |+|Sn-----------| if +
    // GL |GL-------------| if GO == GO_START
    // {Gn. .} |+|Gn-----------| if GO != GO_NONE
    //
    // DF: Data Format (2 bit)
    // XDF: Extended data format (3 bit)
    // GO: Group Order    // X: X flag (1 bit) for attachment and tag extension
    // UF: Domain format (3 bit) Discrete only

    // S0: Size lower bits (4 bit)
    // Sn: Size upper bits (7 bit)
    // GL: Group Layer (8 bit)
    // Gn: Group Number (7 bit)

    // ATTACHMENT FORMAT (have no unit information)
    //
    // FORMAT0 1 byte
    // FORMAT1 1 byte
    // DATA 0 - N
    //
    // ------- |7|6|5|4|3|2|1|0|
    // FORMAT0 |R|1|AT |0|
    // FORMAT1 |R |+|S0-----|
    //
    // ATTACHMENT MULT AT = 0 (was block stat)
    // ------- |7|6|5|4|3|2|1|0|
    // FORMAT0 | |1|0|0|0|0|0|0|
    //
    // ATTACHMENT ASSOC AT = 4
    // ------- |7|6|5|4|3|2|1|0|
    // FORMAT0 | |1|0|0|1|0|0|0|
    //
    // ATTACHMENT LABEL AT = 8
    // ------- |7|6|5|4|3|2|1|0|
    // FORMAT0 | |1|0|1|0|0|0|0|

    // FORMAT 0
    public static final int MASK_FORMAT_0_DF = 0xC0; // 2 bits
    public static final int SHIFT_FORMAT_0_DF = 6;
    public static final int MASK_FORMAT_0_X = 0x20; // 1 bit
    public static final int MASK_FORMAT_0_XDF = 0x18; // 2 bits
    public static final int SHIFT_FORMAT_0_XDF = 3;
    public static final int MASK_FORMAT_0_RES = 0x02; // 1 bit
    public static final int SHIFT_FORMAT_0_RES = 1;
    public static final int MASK_FORMAT_0_GROUP = 0x02; // 1 bit
    public static final int MASK_FORMAT_0_T = 0x01; // 1 bit

    // Tag & Level
    public static final int MASK_FORMAT_0_TL = 0x21; // 2 bit - same as MASK_FORMAT_0_A
    public static final int VALUE_FORMAT_0_T = 0x01; // only tagged
    public static final int VALUE_FORMAT_0_TL = 0x21; // tagged & level

    // attachment
    public static final int MASK_FORMAT_0_A = 0x21; // is attachments if ((format0 & MASK_FORMAT_0_A) == VALUE_FORMAT_0_A)
    public static final int VALUE_FORMAT_0_A = 0x20; // is attachments
    public static final int MASK_FORMAT_0_AT = 0x1e; // 4 bits

    // FORMAT 1
    public static final int MASK_FORMAT_1_TF = 0xe0; // 3 bits
    public static final int SHIFT_FORMAT_1_TF = 5;
    public static final int MASK_FORMAT_1_S0_PLUS = 0x10; // 1 bit
    public static final int MASK_FORMAT_1_S0 = 0x0f; // 4 bits

    // PLUS data
    public static final int MASK_PLUS = 0x80; // 1 bit
    public static final int MASK_PLUS_DATA = 0x7f; // 7 bits
    public static final int DEFAULT_PLUS_LENGTH = 0x7; // 7 bits

    public static final int MAX_DATA = 1 << 12; // ?
    public static final int MAX_SAMPLE_SIZE = 3 + 512 + 8; // ?

    // UNITS FORMAT (time/frequency/...)
    public static final int UF_DELTA_0 = 7; // same unit as previous (0 bytes)
    public static final int UF_DELTA_1 = 0; // delta 1 bytes
    public static final int UF_DELTA_2 = 1; // delta 2 bytes
    public static final int UF_DELTA_4 = 2;// delta 4 bytes
    public static final int UF_DELTA_8 = 3;// delta 8 bytes
    public static final int UF_DELTA_PREVIOUS = 4; // previous delta (0 bytes)
    public static final int UF_ABSOLUTE_8 = 5; //

    public static final int GO_NONE = -1; //
    public static final int GO_INITIAL = 0x00; //
    public static final int GO_INTER = 0x01; //
    public static final int GO_FINAL = 0x02;//
    public static final int GO_SINGLE = 0x3;//
    public static final int GO_MASK = 0x03; //
    public static final String[] GROUP_ORDER_LABELS = new String[] { "Initial", "Intermediate", "Final", "Single"};
    public static final int LAYERS_MAX = Short.MAX_VALUE;
    public static final int LAYER_NONE = -1;
    public static final int GROUP_NONE = -1;
    
//    public static final int AT_MULT = 0; // Continuous only

    public static final int AT_RELATION = 1 << 2;
    public static final int AT_LABEL = 2 << 2;
    public static final int AT_ANY = MASK_FORMAT_0_AT;

    public static final int AT_RELATION_REV_FLAG = 0x01; // reverse relation
    public static final int AT_RELATION_ABS_FLAG = 0x02; // relation with absolute target position - using target domain base if available
    public static final int AT_RELATION_CONTENT_FLAG = 0x04; // relation with additional sample content (idx, group,...) information
    public static final int AT_RELATION_NOPOS_FLAG = 0x10; // relation with absolute target position - using target domain base if available

    public static final int AT_RELATION_DELTA_POS = 0x0; // relation with delta position - using this domain base
    public static final int AT_RELATION_DELTA_POS_REV = 0x1; // reverse relation with delta position
    public static final int AT_RELATION_ABS_POS = 0x2; // relation with absolute target position - using target domain base if available
    public static final int AT_RELATION_ABS_POS_REV = 0x3;// reverse relation with absolute target position
    public static final int AT_RELATION_NO_POS = 0x10; // relation with delta position - using this domain base
    public static final int AT_RELATION_NO_POS_REV = 0x11; // reverse relation with delta position
    
    public static final int VALUE_DEFAULT = 0x0;
    public static final int VALUE_MASK = 0x3;
    public static final int VALUE_NO_ENUMS = 0x1;
    public static final int VALUE_WRITABLE = 0x2;

    public static final int ATTACHMENTS_MASK = AT_ANY;
    public static final int ATTACHMENTS_DEFAULT = AT_ANY;

    public static final int COMPOUND_MASK = VALUE_MASK | ATTACHMENTS_MASK;
    public static final int COMPOUND_DEFAULT = VALUE_DEFAULT | ATTACHMENTS_DEFAULT;

    public static final int PACKED_DEFAULT = 0;

    public static final int SCALE_DEFAULT = -1;
    public static final int MAX_SCALE = 0x10000;

    // ========================================================================================================================
    // DataType
    // ========================================================================================================================

    /**
     * Unknown member type.
     */
    public static final int DATA_TYPE_UNKNOWN = 0x0; // ISample.Unknown

    /**
     * Global enumeration type. Enumeration values are used for all members of one signal.
     */
    public static final int DATA_TYPE_ENUM = 0x1; // ISample.Event
    /**
     * Integer type.
     */
    public static final int DATA_TYPE_INTEGER = 0x2; // ISample.Integer
    /**
     * Float type.
     */
    public static final int DATA_TYPE_FLOAT = 0x3; // ISample.Float
    /**
     * Text type.
     */
    public static final int DATA_TYPE_TEXT = 0x4; // ISample.Text

    /**
     * Float type.
     */
    public static final int DATA_TYPE_LOGIC = 0x5; // ISample.Logic
    /**
     * Binary type.
     */
    public static final int DATA_TYPE_BINARY = 0x6; // ISample.Binary
    /**
     * Inner type.
     */
    public static final int DATA_TYPE_STRUCT = 0x7; // ISample.Struct
    /**
     * Enumeration array type.
     */
    public static final int DATA_TYPE_ENUM_ARRAY = 0x8; // ISample.EventArray
    /**
     * Integer array type.
     */
    public static final int DATA_TYPE_INTEGER_ARRAY = 0x9; // ISample.IntegerArray
    /**
     * Float array type.
     */
    public static final int DATA_TYPE_FLOAT_ARRAY = 0xa; // ISample.FloatArray
    /**
     * Text array type.
     */
    public static final int DATA_TYPE_TEXT_ARRAY = 0xb; // ISample.TextArray

    public static final int DATA_TYPE_MAX = 0xb;
    public static final int DATA_TYPE_MASK_MAIN = 0x0f;

    static boolean isSimpleDataType(int type) {
        return type == ISample.DATA_TYPE_ENUM || type == ISample.DATA_TYPE_INTEGER || type == ISample.DATA_TYPE_FLOAT
                || type == ISample.DATA_TYPE_TEXT || type == ISample.DATA_TYPE_LOGIC;
    }

    /**
     * Returns true if signal type is an array type.
     * 
     */
    static boolean isArrayDataType(int type) {
        return type == ISample.DATA_TYPE_ENUM_ARRAY || type == ISample.DATA_TYPE_INTEGER_ARRAY || type == ISample.DATA_TYPE_FLOAT_ARRAY
                || type == ISample.DATA_TYPE_TEXT_ARRAY;
    }

    /**
     * Returns true if signal type is an array or struct type.
     * 
     */
    static boolean isArrayOrStructDataType(int type) {
        return type == ISample.DATA_TYPE_ENUM_ARRAY || type == ISample.DATA_TYPE_INTEGER_ARRAY || type == ISample.DATA_TYPE_FLOAT_ARRAY
                || type == ISample.DATA_TYPE_TEXT_ARRAY || type == ISample.DATA_TYPE_STRUCT;
    }

    static final String[] DATA_TYPE_LABELS = new String[] { I18n.Samples_SignalType_Unknown, I18n.Samples_SignalType_Event,
            I18n.Samples_SignalType_Integer, I18n.Samples_SignalType_Float, I18n.Samples_SignalType_Text, I18n.Samples_SignalType_Logic,
            I18n.Samples_SignalType_Binary, I18n.Samples_SignalType_Struct, I18n.Samples_SignalType_EventArray, I18n.Samples_SignalType_IntegerArray,
            I18n.Samples_SignalType_FloatArray, I18n.Samples_SignalType_TextArray };
    static final Object[] DATA_TYPE_OPTIONS = new Object[] { DATA_TYPE_UNKNOWN, DATA_TYPE_ENUM, DATA_TYPE_INTEGER, DATA_TYPE_FLOAT, DATA_TYPE_TEXT,
            DATA_TYPE_LOGIC, DATA_TYPE_BINARY, DATA_TYPE_STRUCT, DATA_TYPE_ENUM_ARRAY, DATA_TYPE_INTEGER_ARRAY, DATA_TYPE_FLOAT_ARRAY,
            DATA_TYPE_TEXT_ARRAY };


    static String[] getDataTypeLabels(boolean includeUnknown) {
        List<String> list = new ArrayList<>();
        for (int n=includeUnknown?0:1;n<DATA_TYPE_MAX;n++)
                list.add(DATA_TYPE_LABELS[n]);
        return list.toArray(new String[list.size()]);
    }

    static Object[] getDataTypeOptions(boolean includeUnknown) {
        List<Object> list = new ArrayList<>();
        for (int n=includeUnknown?0:1;n<DATA_TYPE_MAX;n++)
                list.add(n);
        return list.toArray(new Object[list.size()]);
    }
    
    static String getDataTypeLabel(int type) {
        type &= DATA_TYPE_MASK_MAIN;
        return type >= 0 && type < DATA_TYPE_LABELS.length ? DATA_TYPE_LABELS[type]:null;
    }

    // ========================================================================================================================
    // SignalType == any
    //
    public static final int DF_NONE = 0x00;

    // XDF
    public static final int XDF_NONE = 0 << SHIFT_FORMAT_0_XDF;

    // ========================================================================================================================
    // SignalType == DATA_TYPE_LOGIC
    //

    // SignalType == Logic
    //
    // LOGIC STATES
    // Level 2 - 1 Bit
    // 0: '0' strong drive, logic zero
    // 1: '1' strong drive, logic one
    // Level 4 - 2 Bits
    // 2: 'Z' high impedance
    // 3: 'X' strong drive, unknown logic value
    // Level 16 - 4 Bits
    // 4: 'L' weak drive, logic zero
    // 5: 'H' weak drive, logic one
    // 6: 'U' uninitialized
    // 7: 'W' weak drive, unknown logic value
    // 8: '-' (STATE_D) don't care
    // 9 - 14: 'J', 'K', 'M', 'N', 'O', 'P' to be defined by the user

    public final int STATE_LEVEL_NONE = 0x0;// 0 Bit
    public final int STATE_LEVEL_2 = 0x1;// 1 Bit
    public final int STATE_LEVEL_4 = 0x2;// 2 Bits
    public final int STATE_LEVEL_16 = 0x3;// 4 Bits

    public final int STATE_0_BITS = 0x0;// L2
    public final int STATE_1_BITS = 0x1;// L2
    public final int STATE_Z_BITS = 0x2;// L4
    public final int STATE_X_BITS = 0x3;// L4
    public final int STATE_L_BITS = 0x4;// L16
    public final int STATE_H_BITS = 0x5;// L16
    public final int STATE_U_BITS = 0x6;// L16
    public final int STATE_W_BITS = 0x7;// L16
    public final int STATE_D_BITS = 0x8;// L16

    public final int STATE_J_BITS = 0x9;// L16
    public final int STATE_K_BITS = 0xa;// L16
    public final int STATE_M_BITS = 0xb;// L16
    public final int STATE_N_BITS = 0xc;// L16
    public final int STATE_O_BITS = 0xd;// L16
    public final int STATE_P_BITS = 0xe;// L16
    public final int STATE_UNKNOWN_BITS = 0xf;// L16

    public final char[] STATE_LC_DIGITS = { '0', '1', 'z', 'x', 'l', 'h', 'u', 'w', '-', 'j', 'k', 'm', 'n', 'o', 'p', '#' };
    public final char[] STATE_UC_DIGITS = { '0', '1', 'Z', 'X', 'L', 'H', 'U', 'W', '-', 'J', 'K', 'M', 'N', 'O', 'P', '#' };

    public static final int LOGIC_PACK_0 = STATE_0_BITS; // 0
    public static final int LOGIC_PACK_1 = STATE_1_BITS; // 1
    public static final int LOGIC_PACK_RIGHT_ALLIGNED = 2; // fill with msb

    public static final byte LOGIC_L2_BYTE_FILL[] = new byte[] { (byte) 0x00, (byte) 0xff, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    public static final byte LOGIC_L4_BYTE_FILL[] = new byte[] { (byte) 0x00, (byte) 0x55, (byte) 0xaa, (byte) 0xff, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0 };
    public static final byte LOGIC_L16_BYTE_FILL[] = new byte[] { (byte) 0x00, (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55,
            (byte) 0x66, (byte) 0x77, (byte) 0x88, (byte) 0x99, (byte) 0xaa, (byte) 0xbb, (byte) 0xcc, (byte) 0xdd, (byte) 0xee, (byte) 0xff };

    // DF
    public static final int DF_LOGIC_2 = STATE_LEVEL_2 << SHIFT_FORMAT_0_DF; // 0x40;
    public static final int DF_LOGIC_4 = STATE_LEVEL_4 << SHIFT_FORMAT_0_DF; // 0x80;
    public static final int DF_LOGIC_16 = STATE_LEVEL_16 << SHIFT_FORMAT_0_DF; // 0xc0;

    // XDF
    public static final int XDF_LOGIC_PACK_0 = LOGIC_PACK_0 << SHIFT_FORMAT_0_XDF;
    public static final int XDF_LOGIC_PACK_1 = LOGIC_PACK_1 << SHIFT_FORMAT_0_XDF;
    public static final int XDF_LOGIC_PACK_RIGHT_ALLIGNED = LOGIC_PACK_RIGHT_ALLIGNED << SHIFT_FORMAT_0_XDF;

    // DATA_BYTES in Little Endian (minor byte first)

    // ========================================================================================================================
    // SignalType == Float
    //

    public static final int FLOAT_ACCURACY_DEFAULT = 0x00;
    public static final int FLOAT_ACCURACY_32 = 0x01; // 4 bytes
    public static final int FLOAT_ACCURACY_64 = 0x02; // 8 bytes
    public static final int FLOAT_ACCURACY_BIG = 0x03; // n bytes

    // DF
    public static final int DF_FLOAT = 1 << SHIFT_FORMAT_0_DF;
    public static final int DF_FLOAT_ARRAY = 1 << SHIFT_FORMAT_0_DF;
    public static final int DF_FLOAT_N_ARRAY = 3 << SHIFT_FORMAT_0_DF; // has length per sample

    // XDF
    public static final int XDF_FLOAT_DEFAULT = FLOAT_ACCURACY_DEFAULT << SHIFT_FORMAT_0_XDF;
    public static final int XDF_FLOAT_32 = FLOAT_ACCURACY_32 << SHIFT_FORMAT_0_XDF;
    public static final int XDF_FLOAT_64 = FLOAT_ACCURACY_64 << SHIFT_FORMAT_0_XDF;
    public static final int XDF_FLOAT_BIG = FLOAT_ACCURACY_BIG << SHIFT_FORMAT_0_XDF;

    // ========================================================================================================================
    // SignalType == Event
    //

    // DF
    public static final int DF_EVENT = 1 << SHIFT_FORMAT_0_DF;
    public static final int DF_ENUM_EVENT = 2 << SHIFT_FORMAT_0_DF;
    public static final int DF_ENUM_EVENT_ARRAY = 2 << SHIFT_FORMAT_0_DF;
    public static final int DF_ENUM_EVENT_N_ARRAY = 3 << SHIFT_FORMAT_0_DF;

    // ========================================================================================================================
    // SignalType == Integer
    //

    public static final int INTEGER_ACCURACY_DEFAULT = 0x00;
    public static final int INTEGER_ACCURACY_32 = 0x01; // 4 bytes
    public static final int INTEGER_ACCURACY_64 = 0x02; // 8 bytes
    public static final int INTEGER_ACCURACY_BIG = 0x03; // n bytes

    // DF
    public static final int DF_INTEGER = 1 << SHIFT_FORMAT_0_DF;
    public static final int DF_INTEGER_ARRAY = 1 << SHIFT_FORMAT_0_DF;
    public static final int DF_INTEGER_N_ARRAY = 3 << SHIFT_FORMAT_0_DF;

    // XDF
    public static final int XDF_INTEGER_DEFAULT = INTEGER_ACCURACY_DEFAULT << SHIFT_FORMAT_0_XDF;
    public static final int XDF_INTEGER_32 = INTEGER_ACCURACY_32 << SHIFT_FORMAT_0_XDF;
    public static final int XDF_INTEGER_64 = INTEGER_ACCURACY_64 << SHIFT_FORMAT_0_XDF;
    public static final int XDF_INTEGER_BIG = INTEGER_ACCURACY_BIG << SHIFT_FORMAT_0_XDF;

    // DATA_BYTES in Little Endian (minor byte first)

    // ========================================================================================================================
    // SignalType == Text
    //

    // DF
    public static final int DF_TEXT = 1 << SHIFT_FORMAT_0_DF;
    public static final int DF_TEXT_ARRAY = 1 << SHIFT_FORMAT_0_DF;
    public static final int DF_TEXT_N_ARRAY = 3 << SHIFT_FORMAT_0_DF;

    // ========================================================================================================================
    // SignalType == Struct
    //

    /**
     * Global enumeration struct member type. Enumeration values are used for all members of one signal.
     */
    public static final int DATA_TYPE_GLOBAL_ENUM = DATA_TYPE_ENUM; // ISample.Event
    /**
     * Merging enumeration struct member type. Enumeration values are used just for this member.
     */
    public static final int DATA_TYPE_MERGE_ENUM = 0xd; // ISample.Event

    public static final int STRUCT_MOD_VALID_UNTIL_CHANGE = 0x40;
    public static final int STRUCT_MOD_HIDDEN = 0x80;

    public static final int STRUCT_MASK_XDF = 0x30; // used for arrays
    public static final int STRUCT_MASK_MOD = 0xc0;

    // DF
    public static final int DF_STRUCT = 1 << SHIFT_FORMAT_0_DF;

    // ========================================================================================================================
    // SignalType == ...Array
    //

    // ========================================================================================================================
    // SignalType == Binary
    //

    // DF
    public static final int DF_BINARY = 1 << SHIFT_FORMAT_0_DF;

    // data
    //
    // EVENT_ID (+7) {+data...} singulär with a transaction/relation
    // Memebers
    // ID (+7)
    // Type (8)
    // Data
    // TR_PROP_TYPE_BINARY: length (+7) , Bytes
    // TR_PROP_TYPE_STRING: length (+7) , UTF-8 Bytes
    // TR_PROP_TYPE_ENUM: val (+7)
    // TR_PROP_TYPE_INTEGER: length (+7), Bytes
    // TR_PROP_TYPE_RELATION: relation (+7) , id (+7) // id while or before

    // ========================================================================================================================
    // ========================================================================================================================

    public static final int ACCURACY_DEFAULT = -1;

    // format
    public static final String FORMAT_DEFAULT = null;
//    public static final String COUNT_FORMATS = 0x14;


    public static final String FORMAT_NONE = "none";

    // numbers /logic / event
    public static final String FORMAT_BINARY = "bin";
    public static final String FORMAT_OCTAL = "oct";
    public static final String FORMAT_HEXADECIMAL = "hex";
    public static final String FORMAT_ASCII ="ascii";
    public static final String FORMAT_DECIMAL = "dec";

    public static final String FORMAT_LABEL ="label";
    public static final String FORMAT_BYTES ="bytes";
    
    public static final String FORMAT_EVENT = "event";
    
    public static final String FORMAT_BOOLEAN ="bool";


    // multi value format
    public static final String FORMAT_COLLECTION_VALUES_ONLY = "values";
    public static final String FORMAT_COLLECTION_KEY_VALUES = "keyvalues";
    public static final String FORMAT_COLLECTION_HTML = "html";


    // ========================================================================================================================
    // Enums
    // ========================================================================================================================

    public static final int ENUM_MIN = 0;
    public static final int ENUM_GLOBAL = 0;
    public static final int ENUM_RELATION_TARGET = 1;
    public static final int ENUM_RELATION_STYLE = 2;
    public static final int ENUM_LABEL_STYLE = 3;
    public static final int ENUM_RELATION_DOMAINBASE = 4;
    public static final int ENUM_TAG = 5;
    public static final int ENUM_MEMBER_0 = 8;
    public static final int ENUM_MAX = Short.MAX_VALUE;

    // ========================================================================================================================
    // Content
    // ========================================================================================================================

    public static final int CONTENT_NONE = 0x0;
    public static final int CONTENT_SAMPLE = 0x1;
    public static final int CONTENT_GROUP = 0x2;
    public static final int CONTENT_OTHER = 0x4;
    
    public static String content2String(int content, int idx) {return content == CONTENT_SAMPLE ? "@"+idx:content == CONTENT_GROUP ? "#"+idx:""+idx; }
    public static int parseContent(String text) {return  text != null && text.startsWith("@") ? CONTENT_SAMPLE :  text != null && text.startsWith("#") ? CONTENT_GROUP:text != null ? CONTENT_OTHER:CONTENT_NONE;}
    public static int parseContentIdx(String text) {return  text != null && (text.startsWith("@") || text.startsWith("#"))  ? Utils.parseInt(text.substring(1), -1) : Utils.parseInt(text,-1) ;}
    
    // ========================================================================================================================
    // Stat
    // ========================================================================================================================

    public final static int STAT_VAL = (1 << 0);
    public final static int STAT_TAG = (1 << 1);
    public final static int STAT_CHANGE = (1 << 2);
    public final static int STAT_NUM_MINMAX = (1 << 3);
    public final static int STAT_NUM_MEDIAN = (1 << 4);

    public final static int STAT_HAS_TAG = (1 << 0);
    public final static int STAT_HAS_NON_TAG = (1 << 1);
    public final static int STAT_HAS_CHANGE = (1 << 2);
    public final static int STAT_HAS_NONE = (1 << 3);
    public final static int STAT_HAS_VAL = (1 << 4);
    public final static int STAT_HAS_NUM = (1 << 5);
}
