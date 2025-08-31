package de.toem.impulse.samples;

import java.math.BigDecimal;
import java.math.BigInteger;

import de.toem.impulse.samples.domain.DomainLongValue;
import de.toem.impulse.samples.raw.Enumeration;
import de.toem.impulse.samples.raw.Logic;
import de.toem.impulse.samples.raw.Struct;

/**
 * Interface for readable values in the impulse framework.
 *
 * This interface provides a comprehensive API for accessing and manipulating 
 * individual values within the impulse signal processing framework. It represents
 * a single sample or group value, allowing interaction with its position, raw data,
 * and formatted representations.
 *
 * Key features of this interface include:
 * - Access to the position information
 * - Flexible formatting options for data representation
 * - Type conversion methods to various data types (logic, numeric, string, etc.)
 * - Utility methods for logic state detection and analysis
 *
 * The `IReadableValue` interface is designed to be used in scenarios where direct
 * access to a single sample or group value is needed, providing a rich set of methods
 * for interpreting and manipulating the underlying data regardless of its original type.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 35
 */
public interface IReadableValue extends IReadableMembers, IAbstractValue {

  

    
    // ========================================================================================================================
    // Position
    // ========================================================================================================================


    /**
     * Returns the position/start of this sample/group.
     * 
     * This method provides access to the domain position of the value, represented as 
     * a DomainLongValue object that combines domain base and factor. The position 
     * represents the exact location of this sample in its signal's domain (e.g., time,
     * frequency, or any other domain used by the signal).
     * 
     * For group values, this represents the starting position of the group.
     * @return the domain position as a DomainLongValue
     */
    DomainLongValue getPosition();
    
    
    // ========================================================================================================================
    // Format
    // ========================================================================================================================

    /**
     * Formats this sample/group using format 'format'.
     * 
     * This method provides flexible formatting options for converting the value to 
     * a string representation based on predefined format specifiers. The format parameter
     * accepts standard format strings as defined in ISample:
     * 
     * - ISample.FORMAT_BINARY ("bin"): Binary representation (e.g., "1010")
     * - ISample.FORMAT_OCTAL ("oct"): Octal representation (e.g., "52")
     * - ISample.FORMAT_HEXADECIMAL ("hex"): Hexadecimal representation (e.g., "2A")
     * - ISample.FORMAT_ASCII ("ascii"): ASCII character representation
     * - ISample.FORMAT_DECIMAL ("dec"): Decimal representation (e.g., "42")
     * - ISample.FORMAT_LABEL ("label"): Label from enumeration if available
     * - ISample.FORMAT_BYTES ("bytes"): Byte-by-byte hexadecimal representation
     * - ISample.FORMAT_BOOLEAN ("bool"): Boolean representation ("true"/"false")
     * 
     * If the specified format is not applicable to the value type, the method falls back
     * to a reasonable default format.
     *
     * @param format Format specifier as defined in ISample.FORMAT_.. (e.g. ISample.FORMAT_HEXADECIMAL)
     * @return formatted string representation of the value
     * @see ISample for all available format specifiers
     */
    String format(String format);

    /**
     * Returns the default format for this value.
     * 
     * The default format is determined based on the value's data type and content.
     * For example:
     * - Logic values typically default to binary format
     * - Numeric values typically default to decimal format
     * - Enum values typically default to label format
     * 
     * This is useful when you need to display a value but don't have specific
     * formatting requirements, as it will select an appropriate format for the 
     * data type.
     *
     * @return format specifier as defined in ISample.FORMAT_.. (e.g. ISample.FORMAT_HEXADECIMAL)
     * @see ISample for all available format specifiers
     */
    String defaultFormat();


    // ========================================================================================================================
    // Format
    // ========================================================================================================================
   
    /**
     * Format the current sample using hexadecimal format.
     * 
     * This is a convenience method equivalent to calling {@link #format(String)}
     * with format parameter set to ISample.FORMAT_HEXADECIMAL ("hex"). The output 
     * typically includes a "0x" prefix for numeric values, and represents each 
     * digit using characters 0-9 and A-F.
     * 
     * For multi-byte values, the representation follows the platform's endianness 
     * unless otherwise specified by the implementation.
     *
     * @return hexadecimal string representation of the value
     */
    String fhex();

    /**
     * Format the current sample using decimal format.
     * 
     * This is a convenience method equivalent to calling {@link #format(String)}
     * with format parameter set to ISample.FORMAT_DECIMAL ("dec"). For numeric values,
     * this produces a standard base-10 representation.
     * 
     * For non-numeric values, the implementation attempts to provide a meaningful
     * decimal interpretation when possible.
     *
     * @return decimal string representation of the value
     */
    String fdec();

    /**
     * Format the current sample using octal format.
     * 
     * This is a convenience method equivalent to calling {@link #format(String)}
     * with format parameter set to ISample.FORMAT_OCTAL ("oct"). The output represents
     * the value using base-8 notation (digits 0-7).
     * 
     * For non-numeric values, the implementation attempts to provide a meaningful
     * octal interpretation when possible.
     *
     * @return octal string representation of the value
     */
    String foct();

    /**
     * Format the current sample using binary format.
     * 
     * This is a convenience method equivalent to calling {@link #format(String)}
     * with format parameter set to ISample.FORMAT_BINARY ("bin"). The output represents
     * the value using base-2 notation (digits 0 and 1).
     * 
     * For logic values, this shows the direct bit values. For numeric types, this
     * shows the binary representation of the underlying value. The representation may 
     * include leading zeros or grouping based on the implementation.
     *
     * @return binary string representation of the value
     */
    String fbin();

    /**
     * Format the current sample using ASCII format.
     * 
     * This is a convenience method equivalent to calling {@link #format(String)}
     * with format parameter set to ISample.FORMAT_ASCII ("ascii"). For byte or character
     * values, this attempts to represent the value as printable ASCII characters.
     * 
     * Non-printable characters are typically represented using escape sequences or
     * placeholder characters, depending on the implementation.
     *
     * @return ASCII string representation of the value
     */
    String fascii();

    // ========================================================================================================================
    // Conversion
    // ========================================================================================================================
   
    // logic
    /**
     * Return current sample as Logic value.
     * 
     * This method is specifically designed for digital/logic values and provides a convenient
     * way to access the value as a Logic object. For non-digital values, the implementation
     * should attempt reasonable type conversion or return a default value.
     * 
     * The Logic class encapsulates various states of a digital signal, such as:
     * - 0 (STATE_0_BITS): Strong drive, logic zero
     * - 1 (STATE_1_BITS): Strong drive, logic one
     * - Z (STATE_Z_BITS): High impedance
     * - X (STATE_X_BITS): Strong drive, unknown logic value
     * - L (STATE_L_BITS): Weak drive, logic zero
     * - H (STATE_H_BITS): Weak drive, logic one
     * - U (STATE_U_BITS): Uninitialized
     * - And other specialized states
     *
     * @return logic value representation of the sample
     */
    Logic logicValue();

    /**
     * Return current sample as int value representing a logic state.
     * 
     * Returns an integer value that represents the logic state according to the implementation's
     * encoding scheme. Common values include:
     * - ISample.STATE_0_BITS (0): Strong drive, logic zero
     * - ISample.STATE_1_BITS (1): Strong drive, logic one
     * - ISample.STATE_Z_BITS (2): High impedance
     * - ISample.STATE_X_BITS (3): Strong drive, unknown logic value
     * - ISample.STATE_L_BITS (4): Weak drive, logic zero
     * - ISample.STATE_H_BITS (5): Weak drive, logic one
     * - ISample.STATE_U_BITS (6): Uninitialized
     * - ISample.STATE_W_BITS (7): Weak drive, unknown logic value
     * - ISample.STATE_D_BITS (8): Don't care
     * 
     * The specific encoding follows the standard logic state conventions used in
     * digital and hardware description systems.
     *
     * @return integer code representing the logic state
     */
    int logicState();

    /**
     * Tries to detect the logic state high at current position using a custom detector.
     * 
     * The detector parameter allows for customized logic level detection rules, which can be
     * useful for signals with non-standard voltage levels or specialized interpretation criteria.
     * 
     * By default, a high state typically corresponds to STATE_1_BITS (1) or STATE_H_BITS (5),
     * but the detector can apply different rules based on the application context.
     * 
     * @param detector Custom logic detector implementation for specialized detection rules
     * @return True if high logic state detected
     */
    boolean isHigh(ILogicDetector detector);

    /**
     * Tries to detect the logic state high at current position.
     * 
     * This method uses the default logic level detection rules to determine whether
     * the value represents a high state. For digital signals, this typically corresponds
     * to logic '1' (STATE_1_BITS) or 'H' (STATE_H_BITS), but the exact interpretation may 
     * depend on the signal's characteristics.
     * 
     * @return True if high logic state detected
     */
    boolean isHigh();
    
    /**
     * Tries to detect the logic state low at current position using a custom detector.
     * 
     * The detector parameter allows for customized logic level detection rules, which can be
     * useful for signals with non-standard voltage levels or specialized interpretation criteria.
     * 
     * By default, a low state typically corresponds to STATE_0_BITS (0) or STATE_L_BITS (4),
     * but the detector can apply different rules based on the application context.
     * 
     * @param detector Custom logic detector implementation for specialized detection rules
     * @return True if low logic state detected
     */
    boolean isLow(ILogicDetector detector);
    
    /**
     * Tries to detect the logic state low at current position.
     * 
     * This method uses the default logic level detection rules to determine whether
     * the value represents a low state. For digital signals, this typically corresponds
     * to logic '0' (STATE_0_BITS) or 'L' (STATE_L_BITS), but the exact interpretation may 
     * depend on the signal's characteristics.
     * 
     * @return True if low logic state detected
     */
    boolean isLow();

    // numbers
    /**
     * Return current sample as Number value.
     * 
     * This method provides a generic numeric representation of the value, suitable for
     * any signal that can be reasonably interpreted as a number. The returned Number object
     * could be any of Java's numeric types (Integer, Double, etc.) depending on the
     * signal's native representation.
     * 
     * For non-numeric signals, the implementation should attempt reasonable type conversion
     * or throw an exception if conversion is not possible.
     * 
     * @return numeric representation of the value
     */
    Number numberValue();

    /**
     * Return current sample as float value.
     * 
     * This method provides the value converted to a float, suitable for
     * processing signals with single-precision floating-point values. For integer signals,
     * the value is converted to float format. For double-precision signals, precision may be lost.
     * 
     * @return float representation of the value
     */
    float floatValue();

    /**
     * Return current sample as double value.
     * 
     * This method provides the value converted to a double, suitable for
     * processing signals with double-precision floating-point values or for calculations
     * that require higher precision than float.
     * 
     * @return double representation of the value
     */
    double doubleValue();

    /**
     * Return current sample as BigDecimal value.
     * 
     * This method provides the value as a BigDecimal, suitable for applications
     * that require arbitrary precision or exact decimal representation. This is
     * particularly useful for financial calculations or when handling very large
     * or very precise decimal values.
     * 
     * @return BigDecimal representation of the value
     */
    BigDecimal bigDecimalValue();

    /**
     * Return current sample as long value.
     * 
     * This method provides the value converted to a long, suitable for
     * processing signals with integer values that may exceed the range of a 32-bit integer.
     * For floating-point signals, the value is truncated to an integer.
     * 
     * @return long representation of the value
     */
    long longValue();

    /**
     * Return current sample as int value.
     * 
     * This method provides the value converted to an int, suitable for
     * processing signals with 32-bit integer values. For floating-point signals,
     * the value is truncated to an integer.
     * 
     * @return int representation of the value
     */
    int intValue();

    /**
     * Return current sample as BigInteger value.
     * 
     * This method provides the value as a BigInteger, suitable for applications
     * that require arbitrary precision integer calculations. This is particularly useful
     * for handling very large integer values that exceed the range of built-in numeric types.
     * 
     * @return BigInteger representation of the value
     */
    BigInteger bigIntValue();

    // other
    /**
     * Return current sample as Struct value.
     * 
     * This method is specifically designed for values with structured data and provides access
     * to samples that contain multiple fields or hierarchical data. The returned Struct object
     * allows you to access individual members or fields within the structured data.
     * 
     * Structs are particularly useful for complex data types such as protocol packets, 
     * multi-dimensional measurements, or any data that has multiple related components.
     * 
     * @return struct value, or null if the value cannot be interpreted as a struct
     */
    Struct structValue();

    /**
     * Return current sample as String value.
     * 
     * This method is primarily intended for text-based values but will attempt to convert
     * other value types to string representations. For text values, it returns the original
     * string data. For other types, it typically returns a human-readable string representation
     * of the value.
     * 
     * This is useful for displaying values in user interfaces or log files where
     * a textual representation is required.
     * 
     * @return string value, or a string representation if not a text value
     */
    String stringValue();

    /**
     * Return current sample as Enumeration value.
     * 
     * This method is specifically designed for values that use enumerated values, where
     * each value represents one of a predefined set of possible values. The returned
     * Enumeration object contains both the numeric value and its associated label.
     * 
     * Enumerations are commonly used for state machines, status indicators, or any signal
     * that has a limited set of discrete states with meaningful names.
     * 
     * @return enumeration value, or null if the value cannot be interpreted as an enumeration
     */
    Enumeration enumValue();
    
    /**
     * Return current sample as byte array.
     * 
     * This method is primarily intended for binary values or values that contain raw binary data.
     * It provides access to the underlying bytes that constitute the value. This is useful
     * for working with protocol data, file contents, or any other binary information.
     * 
     * For non-binary values, this method may attempt to serialize the value into a byte array
     * representation, but the specific behavior depends on the value type.
     * 
     * @return byte array value, or null if the value cannot be represented as bytes
     */
    byte[] bytesValue();
    

}
