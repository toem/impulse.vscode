package de.toem.impulse.samples;

import java.math.BigDecimal;
import java.math.BigInteger;


/**
 * Interface for writing integer samples in the impulse framework.
 *
 * This interface provides specialized methods for writing integer numerical data to signals.
 * It extends the INumberSamplesWriter interface with integer-specific functionality, enabling
 * efficient and type-safe representation of whole numbers, counters, identifiers, and discrete values.
 *
 * Key features of this interface include:
 * - Support for various integer formats including int, long, short, byte, and BigInteger
 * - Methods for writing individual values at specific positions with appropriate size control
 * - Handling of both signed and unsigned integer representations
 * - Helper methods optimized for different scripting environments
 * - Optional tagging support for highlighting significant values
 *
 * The `IIntegerSamplesWriter` interface is particularly useful for representing counters,
 * indices, state identifiers, addresses, and any other data that requires whole number
 * representation. It provides a clear and type-safe API for generating integer signal data
 * while maintaining the positioning and tagging capabilities of the base writer interface.
 *
 * This interface can be used in various scenarios such as address bus traces, counter logs,
 * state transition identifiers, and protocol analyzers where precise integer representation
 * is essential for system understanding.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 19
 */
public interface IIntegerSamplesWriter extends INumberSamplesWriter{
   
    /**
     * Writes an integer sample using an int value.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
     * Example (Java)
     * 
     *  int value = 44; 
     *  writer.write(1000L, false, value);     
      * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, int value);    
    /**
     * Writes an integer sample using a long value.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
     * Example (Java)
     * 
     *  long value = 44; 
     *  writer.write(1000L, false, value);     
      * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, long value); 
    /**
     * Writes an integer sample using a BigInteger value.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
     * Example (Java)
     * 
     *  BigInteger value = new BigInteger(44); 
     *  writer.write(1000L, false, value);     
      * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, BigInteger value);
    
    /**
     * Writes an integer sample using a Number value.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
     * Example (Java)
     * 
     *  Number value = new BigInteger(44); 
     *  writer.write(1000L, false, value);     
      * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, Number value);
    
    /**
     * Writes an integer sample with integer array data.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
     * Example (Java)
     * 
     *  int[] value = new int[]{4,5};  // an integer array value
     *  writer.write(1000L,false,value);     
     * </pre>
     * @param position  Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag  If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value  Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, int[] value);
    
    /**
     * Writes an integer sample with long array data.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
     * Example (Java)
     * 
     *  int[] value = new long[]{4,5};  // a long array value
     *  writer.write(1000L,false,value);     
     * </pre>
     * @param position  Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag  If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value  Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, long[] value);
    
    /**
     * Writes an integer sample with BigInteger array data.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
    
     * </pre>
     * @param position  Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag  If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value  Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, BigInteger[] value);
    
    /**
     * Writes an integer sample using an int value.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, int value).
     * Defined for scripting purpose.
     * <pre>
     * Example (JavaScript)
     * 
     *  var value = 44; 
     *  out.writeInt(1000, false, value);     
      * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean writeInt(long position, boolean tag, int value);    
    /**
     * Writes an integer sample using a long value.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, long value).
     * Defined for scripting purpose.
     * <pre>
     * Example (JavaScript)
     * 
     *  var value = 44; 
     *  out.writeLong(1000, false, value);     
      * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean writeLong(long position, boolean tag, long value); 
    /**
     * Writes an integer sample using a BigInteger value.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, BigInteger value).
     * Defined for scripting purpose.
     * <pre>
     * Example (JavaScript)
     * 
     *  var value = new java.math.BigInteger(12); 
     *  out.writeBig(1000, false, value);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean writeBig(long position, boolean tag, BigInteger value);
    
    /**
     * Writes an integer sample with integer array data.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, int[] value).
     * Defined for scripting purpose.
     * <pre>
     * Example (JavaScript)
     * 
     *  var value = java.lang.reflect.Array.newInstance(java.lang.Integer.TYPE, 2);  // an integer array value
     *  value[0] = 5; value[1] = 3;
     *  out.writeIntArray(1000,false,value);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean writeIntArray(long position, boolean tag, int[] value);
    /**
     * Writes an integer sample with integer array data .
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, int... value).
     * Defined for scripting purpose.
     * <pre>
     * Example (JavaScript)
     * 
     *  out.writeIntArgs(1000,false,2,4);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean writeIntArgs(long position, boolean tag, int... value);
    
    /**
     * Writes an integer sample with long array data.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, long[] value).
     * Defined for scripting purpose.
     * <pre>
     * Example (JavaScript)
     * 
     *  var value = java.lang.reflect.Array.newInstance(java.lang.Long.TYPE, 2);  // a long array value
     *  value[0] = 5; value[1] = 3;
     *  out.writeIntArray(1000,false,value);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean writeLongArray(long position, boolean tag, long[] value);
    /**
     * Writes an integer sample with long array data .
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, long... value).
     * Defined for scripting purpose.
     * <pre>
     * Example (JavaScript)
     * 
     *  out.writeIntArgs(1000,false,2,4);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean writeLongArgs(long position, boolean tag, long... value);
    
    /**
     * Writes an integer sample with BigInteger array data.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, long[] value).
     * Defined for scripting purpose.
     * <pre>

     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean writeBigArray(long position, boolean tag, BigInteger[] value);
    /**
     * Writes an integer sample with BigInteger array data .
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, BigInteger... value).
     * Defined for scripting purpose.
     * <pre>
    
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean writeBigArgs(long position, boolean tag, BigInteger[] value);
    
    
    default int[] createIntArray(int length) {
        return new int[length];
    }
    
    default long[] createLongArray(int length) {
        return new long[length];
    }
    
    default BigInteger[] createBigArray(int length) {
        return new BigInteger[length];
    }
    
}
