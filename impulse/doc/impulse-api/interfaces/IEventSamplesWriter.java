package de.toem.impulse.samples;

import java.math.BigInteger;

import de.toem.impulse.samples.raw.Enumeration;

/**
 * Interface for writing event-based samples in the impulse framework.
 *
 * This interface provides specialized methods for writing event data to signals.
 * It extends the base ISamplesWriter interface with event-specific functionality, allowing
 * for efficient representation of state transitions, triggers, flags, and discrete events.
 *
 * Key features of this interface include:
 * - Methods for writing event markers with or without associated data
 * - Support for both integer and string-based enumeration values
 * - Array-based event data for complex state/transition combinations
 * - Helper methods optimized for different scripting environments
 * - Convenience methods for creating and manipulating event sample arrays
 *
 * The `IEventSamplesWriter` interface is particularly useful for capturing state changes,
 * protocol events, triggers, and other discrete occurrences in a system. It provides a
 * rich set of methods for representing events with different levels of detail and context.
 *
 * This interface can be used in various scenarios such as state machine visualization,
 * protocol analyzers, event-based debugging, and system monitoring where the presence
 * of events and their associated data is more important than continuous signal values.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 11
 */
public interface IEventSamplesWriter extends ISamplesWriter {

    /**
     * Writes an event sample without data.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
     * Example (Java)
     * 
     *  writer.write(1000L,false);     
     * </pre>
     * @param position  Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag  If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag);

    /**
     * Writes an event sample with integer enum data.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
     * Example (Java)
     * 
     *  int value = 4; // an integer value of an enumeration
     *  writer.write(1000L,false,value);    
     * </pre>
     * @param position  Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag  If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value  Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, int value);

    /**
     * Writes an event sample with String enum data.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
     * Example (Java)
     * 
     *  String value = "Running"; // a string value of an enumeration
     *  writer.write(1000L,false,value);     
     * </pre>
     * @param position  Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag  If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value  Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, String value);
    
    /**
     * Writes an event sample with integer enum data.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
     * Example (Java)
     * 
     *  Enumeration value = new Enumeration(Enumeration.ENUM_GLOBAL,4,"Running"); 
     *  writer.write(1000L,false,value);    
     * </pre>
     * @param position  Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag  If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value  Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, Enumeration value);
    
    /**
     * Writes an event sample with integer enum array data (e.g. a State/Transition-combination).
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
     * Example (Java)
     * 
     *  int[] value = new int[]{4,5};  // an integer array value of enumerations
     *  writer.write(1000L,false,value);     
     * </pre>
     * @param position  Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag  If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value  Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, int[] value);

    /**
     * Writes an event sample with String enum array data (e.g. a State/Transition-combination).
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
     * Example (Java)
     * 
     *  String[] value = new String[]{"Running","Started"};  // a string array value of enumerations
     *  writer.write(1000L,false,value);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, String[] value);
    boolean write(long position, boolean tag, Object[] value);

    /**
     * Writes an event sample with Enumeration enum array data (e.g. a State/Transition-combination).
     * Before using this method, the writer must have been opened (done by system when using script producers).

     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, Enumeration[] value);
    
    /**
     * Writes an event sample with integer enum data.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, int value).
     * Defined for scripting purpose.
     * <pre>
     * Example (JavaScript)
     * 
     *  var value = 4; // an integer value of an enumeration
     *  out.writeInt(1000,false,value);    
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean writeInt(long position, boolean tag, int value);

    /**
    * Writes an event sample with String enum data.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, String value).
     * Defined for scripting purpose.
     * <pre>
     * Example (JavaScript)
     * 
     *  var value = "Running"; // a string value of an enumeration
     *  out.writeString(1000,false,value);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean writeString(long position, boolean tag, String value);
    
    /**
    * Writes an event sample with Enumeration enum data.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, Enumeration value).
     * Defined for scripting purpose.
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean writeEnum(long position, boolean tag, Enumeration value);
    
    /**
     * Writes an event sample with integer enum array data (e.g. a State/Transition-combination).
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, int[] value).
     * Defined for scripting purpose.
     * <pre>
     * Example (JavaScript)
     * 
     *  var value = java.lang.reflect.Array.newInstance(java.lang.Integer.TYPE, 2);  // an integer array value of enumerations
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
     * Writes an event sample with integer enum array data (e.g. a State/Transition-combination).
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
     * Writes an event sample with String enum array data (e.g. a State/Transition-combination).
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, String[] value).
     * Defined for scripting purpose.
     * <pre>
     * Example (JavaScript)
     * 
     *  var value = java.lang.reflect.Array.newInstance(java.lang.String.class, 2);  // a string array value of enumerations
     *  value[0] = "Yes"; value[1] = "Asking";
     *  out.writeStringArray(1000,false,value);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean writeStringArray(long position, boolean tag, String[] value);
    /**
     * Writes an event sample with String enum array data (e.g. a State/Transition-combination).
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, String... value).
     * Defined for scripting purpose.
     * <pre>
     * Example (JavaScript)
     * 
     *  out.writeIntArgs(1000,false,"Yes","Asking");     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean writeStringArgs(long position, boolean tag, String... value);


    /**
     * Writes an event sample with Enumeration enum array data (e.g. a State/Transition-combination).
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, Enumeration[] value).
     * Defined for scripting purpose.
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean writeEnumArray(long position, boolean tag, Enumeration[] value);
    /**
     * Writes an event sample with Enumeration enum array data (e.g. a State/Transition-combination).
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, Enumeration... value).
     * Defined for scripting purpose.

     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean writeEnumArgs(long position, boolean tag, Enumeration... value);

    
    default int[] createIntArray(int length) {
        return new int[length];
    }
    
    default String[] createStringArray(int length) {
        return new String[length];
    }
    
    default Enumeration[] createEnumerationArray(int length) {
        return new Enumeration[length];
    }
}
