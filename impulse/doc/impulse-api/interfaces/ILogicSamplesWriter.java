package de.toem.impulse.samples;

import de.toem.impulse.samples.raw.Logic;

/**
 * Interface for writing logic samples in the impulse framework.
 *
 * This interface provides specialized methods for writing digital logic data to signals.
 * It extends the base ISamplesWriter interface with logic-specific functionality, enabling
 * efficient representation of binary states, multi-bit values, and digital transitions.
 *
 * Key features of this interface include:
 * - Support for multiple logic states (0, 1, X, Z, etc.) representing digital signal values
 * - Methods for writing individual bits, bytes, or multi-bit values
 * - Automatic state level detection and state reduction
 * - Flexible representation of edges, pulses, and complex waveforms
 * - Helper methods optimized for different scripting environments
 *
 * The `ILogicSamplesWriter` interface is particularly useful for creating digital waveforms,
 * bus transactions, control signals, and any data that follows digital logic patterns. It
 * provides a clear and type-safe API for generating complex digital signal data while
 * maintaining the positioning and tagging capabilities of the base writer interface.
 *
 * This interface can be used in various scenarios such as digital circuit simulation,
 * protocol analysis, hardware debugging, and system modeling where digital signals play
 * a central role in understanding system behavior.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 21
 */
public interface ILogicSamplesWriter extends ISamplesWriter{
   
    // auto state level and reduction of states (preceding)
    /**
     * Writes a logic sample using a single state value.
     * Does automatic state level detection and state reduction.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
     * Example (Java)
     * 
     *  byte states = ILogicStates.STATE_X_BITS; // all bits to X
     *  writer.write(1000L, true, states);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param states State value for all bits. 
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, byte states);
    /**
     * Writes a logic sample using a state array value.
     * Does automatic state level detection and state reduction.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
     * Example (Java)
     * 
     *  byte precedingStates = ILogicStates.STATE_0_BITS; // all bits to the left
     *  byte[] states = new  byte[]{ILogicStates.STATE_X_BITS,ILogicStates.STATE_1_BITS}; // defined bits
     *  writer.write(1000L, true, precedingStates, states,0,2);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param precedingStates State value for all bits left of the defined.
     * @param states Defined state values.
     * @param start Start position in state array (left-most bit).
     * @param length Length of states in array.
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, byte precedingStates, byte[] states, int start, int length); 
    /**
     * Writes a logic sample using a string value.
     * Does automatic state level detection and state reduction.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
     * Example (Java)
     * 
     *  byte precedingStates = ILogicStates.STATE_0_BITS; // all bits to the left
     *  String states = "X1"; // defined bits
     *  writer.write(1000L, true, precedingStates, states);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param precedingStates State value for all bits left of the defined.
     * @param states Defined state values.
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, byte precedingStates, String states);
    
    /**
     * Writes a logic sample using Logic value.
     * Does automatic state level detection and state reduction.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
     * Example (Java)
     * 
     *  Logic value = Logic.valueOf("uuxx1001");
     *  writer.write(1000L, true, value);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param value Logic value.
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, Logic value);
    @Deprecated
    boolean write(long position, Logic value);
    
    
    // manual state level and reduction of states (preceding)
    /**
     * Writes a logic sample using a single state value.
     * No automatic state level detection and no state reduction !
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
     * Example (Java)
     * 
     *  byte level = ISample.STATE_LEVEL_4; // use 2 bits per sample (0,1,Z,X) 
     *  byte states = ILogicStates.STATE_X_BITS; // all bits to X
     *  writer.write(1000L, true, level, states);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param stateLevel Defines the number of bits per state.
     * @param states State value for all bits. 
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, int stateLevel, byte states);
    /**
     * Writes a logic sample using  a single state value + precedingStates.
     * No automatic state level detection and no state reduction !
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
     * Example (Java)
     * 
     *  byte level = ISample.STATE_LEVEL_4; // use 2 bits per sample (0,1,Z,X) 
     *  byte precedingStates = ILogicStates.STATE_0_BITS; // all bits to the left
     *  byte state = ILogicStates.STATE_1_BITS; // defined bits
     *  writer.write(1000L, true, level, precedingStates, state);      
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param stateLevel Defines the number of bits per state.
     * @param precedingStates State value for all bits left of the defined.
     * @param states Defined state values.
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, int stateLevel, byte precedingStates, byte states);
    /**
     * Writes a logic sample using  a state array value.
     * No automatic state level detection and no state reduction !
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
     * Example (Java)
     * 
     *  byte level = ISample.STATE_LEVEL_4; // use 2 bits per sample (0,1,Z,X) 
     *  byte precedingStates = ILogicStates.STATE_0_BITS; // all bits to the left
     *  byte[] states = new  byte[]{ILogicStates.STATE_X_BITS,ILogicStates.STATE_1_BITS}; // defined bits
     *  writer.write(1000L, true, level, precedingStates, states,0,2);      
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param stateLevel Defines the number of bits per state.
     * @param precedingStates State value for all bits left of the defined.
     * @param states Defined state values.
     * @param start Start position in state array (left-most bit).
     * @param length Length of states in array.
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, int stateLevel, byte precedingStates, byte[] states, int start, int length);       

    
    // writer interfaces to avoid overloading problems from script
    /**
     * Writes a logic sample using a single state value.
     * Does automatic state level detection and state reduction.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, byte states).
     * Defined for scripting purpose.
    * <pre>
     * Example (JavaScript)
     * 
     *  var states = ILogicStates.STATE_X_BITS; // all bits to X
     *  out.writeByte(1000, true, states);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param states State value for all bits. 
     * @return Returns true if succeeded.
     */
    boolean writeByte(long position, boolean tag, byte states);
    /**
     * Same as write(long position, boolean tag, byte precedingStates, byte[] states, int start, int length).
     * Defined for scripting purpose.
     * <pre>
     * Example (JavaScript)
     * 
     *  var precedingStates = ILogicStates.STATE_0_BITS; // all bits to the left
     *  var states = java.lang.reflect.Array.newInstance(java.lang.Byte.TYPE, 2);
     *  states[0] = ILogicStates.STATE_X_BITS;states[1] = ILogicStates.STATE_1_BITS;
     *  out.writeBytesP(1000, true, precedingStates, states,0,2);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param precedingStates State value for all bits left of the defined.
     * @param states Defined state values.
     * @param start Start position in state array (left-most bit).
     * @param length Length of states in array.
     * @return Returns true if succeeded.
     */
    boolean writeBytesP(long position, boolean tag, byte precedingStates, byte[] states, int start, int length); 
    /**
     * Same as write(long position, boolean tag, byte precedingStates, String states).
     * Defined for scripting purpose.
     * <pre>
     * Example (JavaScript)
     * 
     *  var precedingStates = ILogicStates.STATE_0_BITS; // all bits to the left
     *  var states = "X1"; // defined bits
     *  out.writeStringP(1000, true, precedingStates, states);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param precedingStates State value for all bits left of the defined.
     * @param states Defined state values.
     * @return Returns true if succeeded.
     */
    boolean writeStringP(long position, boolean tag, byte precedingStates, String states);
    /**
     * Same as write(long position, boolean tag, int stateLevel, byte states).
     * Defined for scripting purpose.
     * <pre>
     * Example (JavaScript)
     * 
     *  var level = ISample.STATE_LEVEL_4; // use 2 bits per sample (0,1,Z,X) 
     *  var states = ILogicStates.STATE_X_BITS; // all bits to X
     *  out.writeByteS(1000, true, level, states);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param stateLevel Defines the number of bits per state.
     * @param states State value for all bits. 
     * @return Returns true if succeeded.
     */
    boolean writeByteS(long position, boolean tag, int stateLevel, byte states);
    /**
     * Same as write(long position, boolean tag, int stateLevel, byte precedingStates, byte state).
     * Defined for scripting purpose.
     * <pre>
     * Example (JavaScript)
     * 
     *  var level = ISample.STATE_LEVEL_4; // use 2 bits per sample (0,1,Z,X) 
     *  var precedingStates = ILogicStates.STATE_0_BITS; // all bits to the left
     *  var states = ILogicStates.STATE_X_BITS; // all bits to X
     *  out.writeByteSP(1000, true, level,precedingStates, states);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param stateLevel Defines the number of bits per state.
     * @param precedingStates State value for all bits left of the defined.
     * @param states Defined state values.
     * @return Returns true if succeeded.
     */
    boolean writeByteSP(long position, boolean tag, int stateLevel, byte precedingStates, byte states);
    /**
     * Same as write(long position, boolean tag, int stateLevel, byte precedingStates, byte[] states, int start, int length).
     * Defined for scripting purpose.
     * <pre>
     * Example (JavaScript)
     * 
     *  var level = ISample.STATE_LEVEL_4; // use 2 bits per sample (0,1,Z,X) 
     *  var precedingStates = ILogicStates.STATE_0_BITS; // all bits to the left
     *  var states = java.lang.reflect.Array.newInstance(java.lang.Byte.TYPE, 2);
     *  states[0] = ILogicStates.STATE_X_BITS;states[1] = ILogicStates.STATE_1_BITS;
     *  out.writeBytesSP(1000, true, level, precedingStates, states,0,2);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param stateLevel Defines the number of bits per state.
     * @param precedingStates State value for all bits left of the defined.
     * @param states Defined state values.
     * @param start Start position in state array (left-most bit).
     * @param length Length of states in array.
     * @return Returns true if succeeded.
     */
    boolean writeBytesSP(long position, boolean tag, int stateLevel, byte precedingStates, byte[] states, int start, int length);   
    /**
     * Writes a logic sample using Logic value.
     * Does automatic state level detection and state reduction.
     * Same as write(long position, Logic logic).
     * Defined for scripting purpose.
     * <pre>
     * Example (JavaScript)
     * 
     *  var value = Logic.valueOf("uuxx1001");
     *  out.writeLogic(1000, true, value);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param logic Logic value.
     * @return Returns true if succeeded.
     */
    boolean writeLogic(long position, boolean tag, Logic logic);
    @Deprecated
    boolean writeLogic(long position, Logic logic);
}
