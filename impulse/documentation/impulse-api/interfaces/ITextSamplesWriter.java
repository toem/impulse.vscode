package de.toem.impulse.samples;


/**
 * Interface for writing text samples in the impulse framework.
 *
 * This interface provides specialized methods for writing string and text array data to signals.
 * It extends the base ISamplesWriter interface with text-specific functionality, allowing for
 * efficient and type-safe text data generation.
 *
 * Key features of this interface include:
 * - Methods for writing individual text strings at specific positions
 * - Support for text arrays to represent structured text data
 * - Helper methods optimized for different scripting environments
 * - Convenience methods for creating and manipulating text sample arrays
 *
 * The `ITextSamplesWriter` interface is particularly useful for logging textual events, storing
 * messages, representing symbolic data, and working with any signal that contains human-readable
 * information. It provides a clear and concise API for text data generation while maintaining
 * the positioning and tagging capabilities of the base writer interface.
 *
 * This interface can be used in various scenarios such as protocol analyzers, log viewers,
 * messaging systems, and any application where textual representation of signal data is required.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 59
 */
public interface ITextSamplesWriter extends ISamplesWriter {

    /**
     * Write a text sample.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
     * Example (Java)
     * 
     *  String value = "this is a text"; 
     *  writer.write(1000L, false, value);     

     * Example (JavaScript)
     * 
     *  var value = "this is a text"; 
     *  out.write(1000, false, value);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, String value);
    
    /**
     * Writes a text sample with String array data.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
     * Example (Java)
     * 
     *  String[] value = new String[]{"4","5"};  // a String array value
     *  writer.write(1000L,false,value);     
     * </pre>
     * @param position  Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag  If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value  Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, String[] value);
    
    /**
     * Writes a text sample with String data.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, String value).
     * Defined for scripting purpose.
     * <pre>
     * Example (JavaScript)
     * 
     *  var value = "this is a text"; 
     *  out.writeString(1000,false,value);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    
    boolean writeString(long position, boolean tag, String value);
    
    /**
     * Writes a text sample with String array data.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, String[] value).
     * Defined for scripting purpose.
     * <pre>
     * Example (JavaScript)
     * 
     *  var value = java.lang.reflect.Array.newInstance(java.lang.String.TYPE, 2);  // an integer array value
     *  value[0] = "5"; value[1] = "3";
     *  out.writeStringArray(1000,false,value);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean writeStringArray(long position, boolean tag, String[] value);
    /**
     * Writes a text sample with String array data.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, String... value).
     * Defined for scripting purpose.
     * <pre>
     * Example (JavaScript)
     * 
     *  out.writeStringArgs(1000,false,"2","4");     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean writeStringArgs(long position, boolean tag, String... value);
    
    default String[] createStringArray(int length) {
        return new String[length];
    }
}
