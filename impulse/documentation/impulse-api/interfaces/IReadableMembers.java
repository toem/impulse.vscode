package de.toem.impulse.samples;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import de.toem.impulse.samples.raw.Enumeration;
import de.toem.impulse.samples.raw.Logic;
import de.toem.impulse.samples.raw.Struct;

/**
 * Interface for accessing structured data with named or indexed members in the impulse framework.
 *
 * This interface provides a comprehensive API for accessing individual members within complex data 
 * structures such as structs or arrays. It allows for flexible member identification and offers
 * a rich set of conversion methods to retrieve member values in various formats.
 * 
 * Key features of this interface include:
 * - Flexible member identification through names, IDs, or indices
 * - Type conversion methods for retrieving member values in specific data types
 * - Formatting options for string representation of member values
 * - Support for both array-like indexed access and struct-like named field access

 * 
 * The interface is particularly useful for:
 * - Analyzing protocol data with multiple fields
 * - Working with complex measurement data structures
 * - Processing hierarchical or nested data representations
 * - Providing a unified access pattern to heterogeneous data types
 *
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 29
 * 
 * @see ISample for common sample interfaces
 * @see IMemberAccess for member access methods
 */
public interface IReadableMembers extends ISample, IMemberAccess {

	
    // ========================================================================================================================
    // Format
    // ========================================================================================================================


    /**
     * Format value of member 'memberIdentifier' using the specified format.
     * 
     * This method provides flexible formatting options for converting a member's value to a string
     * representation based on predefined format specifiers. The format parameter accepts
     * standard format strings as defined in ISample, such as:
     * - ISample.FORMAT_BINARY ("bin")
     * - ISample.FORMAT_OCTAL ("oct")
     * - ISample.FORMAT_HEXADECIMAL ("hex")
     * - ISample.FORMAT_ASCII ("ascii")
     * - ISample.FORMAT_DECIMAL ("dec")
     * - ISample.FORMAT_LABEL ("label")
     * 
     * If the specified format is not applicable to the member's type, the method falls back
     * to a reasonable default format determined by the member's data type.
     * 
     * @param memberIdentifier Member identifier, which can be:
     *   - String (name): The member's simple name (case-sensitive, e.g., "field1")
     *   - String (path): The member's full path including parent structures (e.g., "parent.field1")
     *   - Integer (member ID): The member's numeric ID in a struct
     *   - Integer (index): The member's index in an array or in the list returned by getMemberDescriptors()
     * @param format Format specifier as defined in ISample.FORMAT_.. (e.g. ISample.FORMAT_HEXADECIMAL)
     * @return formatted string representation of the member's value
     * @see ISample for all available format specifiers
     */
    String formatOf(Object memberIdentifier, String format);

    /**
     * Returns the default format specifier for the specified member.
     * 
     * The default format is determined based on the member's data type and content.
     * For example:
     * - Logic members typically default to binary format
     * - Numeric members typically default to decimal format
     * - Enum members typically default to label format
     * 
     * This method is particularly useful when you need to display a member's value
     * but don't have specific formatting requirements.
     * 
     * The parentFormat parameter allows for context-aware format determination,
     * where a member's format might be influenced by its parent structure's format.
     * 
     * @param memberIdentifier Member identifier, which can be:
     *   - String name: The member's name or path (e.g., "fieldName" or "header.type")
     *   - Integer memberId: The member's numeric ID or index
     * @param parentFormat Format of the parent structure, or null if not applicable
     * @return default format specifier for the member
     * @see ISample for all available format specifiers
     */
    String defaultFormatOf(Object memberIdentifier, String parentFormat);
    
    // ========================================================================================================================
    // Format Shortcut
    // ========================================================================================================================

    
    /**
     * Format the specified member's value using hexadecimal format.
     * 
     * This is a convenience method equivalent to calling {@link #formatOf(Object, String)}
     * with format parameter set to ISample.FORMAT_HEXADECIMAL ("hex"). The output 
     * typically includes a "0x" prefix for numeric values, and represents each 
     * digit using characters 0-9 and A-F.
     * 
     * For multi-byte values, the representation follows the platform's endianness 
     * unless otherwise specified by the implementation.
     * 
     * @param memberIdentifier Member identifier, which can be:
     *   - String name: The member's name or path (e.g., "fieldName" or "header.type")
     *   - Integer memberId: The member's numeric ID or index
     * @return hexadecimal string representation of the member's value
     */
    String fhexOf(Object memberIdentifier);

    /**
     * Format the specified member's value using decimal format.
     * 
     * This is a convenience method equivalent to calling {@link #formatOf(Object, String)}
     * with format parameter set to ISample.FORMAT_DECIMAL ("dec"). For numeric values,
     * this produces a standard base-10 representation.
     * 
     * For non-numeric values, the implementation attempts to provide a meaningful
     * decimal interpretation when possible.
     * 
     * @param memberIdentifier Member identifier, which can be:
     *   - String name: The member's name or path (e.g., "fieldName" or "header.type")
     *   - Integer memberId: The member's numeric ID or index
     * @return decimal string representation of the member's value
     */
    String fdecOf(Object memberIdentifier);

    /**
     * Format the specified member's value using octal format.
     * 
     * This is a convenience method equivalent to calling {@link #formatOf(Object, String)}
     * with format parameter set to ISample.FORMAT_OCTAL ("oct"). The output represents
     * the value using base-8 notation (digits 0-7).
     * 
     * For non-numeric values, the implementation attempts to provide a meaningful
     * octal interpretation when possible.
     * 
     * @param memberIdentifier Member identifier, which can be:
     *   - String name: The member's name or path (e.g., "fieldName" or "header.type")
     *   - Integer memberId: The member's numeric ID or index
     * @return octal string representation of the member's value
     */
    String foctOf(Object memberIdentifier);

    /**
     * Format the specified member's value using binary format.
     * 
     * This is a convenience method equivalent to calling {@link #formatOf(Object, String)}
     * with format parameter set to ISample.FORMAT_BINARY ("bin"). The output represents
     * the value using base-2 notation (digits 0 and 1).
     * 
     * For logic values, this shows the direct bit values. For numeric types, this
     * shows the binary representation of the underlying value. The representation may 
     * include leading zeros or grouping based on the implementation.
     * 
     * @param memberIdentifier Member identifier, which can be:
     *   - String name: The member's name or path (e.g., "fieldName" or "header.type")
     *   - Integer memberId: The member's numeric ID or index
     * @return binary string representation of the member's value
     */
    String fbinOf(Object memberIdentifier);

    /**
     * Format the specified member's value using ASCII format.
     * 
     * This is a convenience method equivalent to calling {@link #formatOf(Object, String)}
     * with format parameter set to ISample.FORMAT_ASCII ("ascii"). For byte or character
     * values, this attempts to represent the value as printable ASCII characters.
     * 
     * Non-printable characters are typically represented using escape sequences or
     * placeholder characters, depending on the implementation.
     * 
     * @param memberIdentifier Member identifier, which can be:
     *   - String name: The member's name or path (e.g., "fieldName" or "header.type")
     *   - Integer memberId: The member's numeric ID or index
     * @return ASCII string representation of the member's value
     */
    String fasciiOf(Object memberIdentifier);
    
    // ========================================================================================================================
    // Conversion
    // ========================================================================================================================
  
    // logic
    /**
     * Returns the specified member's value as a Logic object.
     * 
     * This method is specifically designed for accessing logic-type members and provides 
     * a convenient way to retrieve the value as a Logic object. For non-logic members, 
     * the implementation will attempt reasonable type conversion or return a default value.
     * 
     * The Logic class encapsulates various states of a digital signal, including:
     * - 0 (STATE_0_BITS): Strong drive, logic zero
     * - 1 (STATE_1_BITS): Strong drive, logic one
     * - Z (STATE_Z_BITS): High impedance
     * - X (STATE_X_BITS): Strong drive, unknown logic value
     * - And other specialized states
     * 
     * @param memberIdentifier Member identifier, which can be:
     *   - String name: The member's name or path (e.g., "fieldName" or "header.type")
     *   - Integer memberId: The member's numeric ID or index
     * @return Logic value representation of the member
     */
    Logic logicValueOf(Object memberIdentifier);

    /**
     * Returns the specified member's value as an integer representing a logic state.
     * 
     * This method retrieves the logic state of a member as an integer code according 
     * to the implementation's encoding scheme. Common values include:
     * - ISample.STATE_0_BITS (0): Strong drive, logic zero
     * - ISample.STATE_1_BITS (1): Strong drive, logic one
     * - ISample.STATE_Z_BITS (2): High impedance
     * - ISample.STATE_X_BITS (3): Strong drive, unknown logic value
     * 
     * @param memberIdentifier Member identifier, which can be:
     *   - String name: The member's name or path (e.g., "fieldName" or "header.type")
     *   - Integer memberId: The member's numeric ID or index
     * @return integer code representing the logic state
     */
    int logicStateOf(Object memberIdentifier);

    /**
     * Determines if the specified member represents a high logic state using a custom detector.
     * 
     * The detector parameter allows for customized logic level detection rules, which can be
     * useful for members with non-standard voltage levels or specialized interpretation criteria.
     * 
     * By default, a high state typically corresponds to STATE_1_BITS (1) or STATE_H_BITS (5),
     * but the detector can apply different rules based on the application context.
     *  
     * @param memberIdentifier Member identifier, which can be:
     *   - String name: The member's name or path (e.g., "fieldName" or "header.type")
     *   - Integer memberId: The member's numeric ID or index
     * @param detector Custom logic detector implementation for specialized detection rules
     * @return true if the member represents a high logic state, false otherwise
     */
    boolean isHighOf(Object memberIdentifier, ILogicDetector detector);

    /**
     * Determines if the specified member represents a low logic state using a custom detector.
     * 
     * The detector parameter allows for customized logic level detection rules, which can be
     * useful for members with non-standard voltage levels or specialized interpretation criteria.
     * 
     * By default, a low state typically corresponds to STATE_0_BITS (0) or STATE_L_BITS (4),
     * but the detector can apply different rules based on the application context.
     * 
     * @param memberIdentifier Member identifier, which can be:
     *   - String name: The member's name or path (e.g., "fieldName" or "header.type")
     *   - Integer memberId: The member's numeric ID or index
     * @param detector Custom logic detector implementation for specialized detection rules
     * @return true if the member represents a low logic state, false otherwise
     */
    boolean isLowOf(Object memberIdentifier, ILogicDetector detector);

    // numbers
    /**
     * Returns the specified member's value as a boolean.
     * 
     * This method attempts to convert the member's value to a boolean representation.
     * Typically, numeric values of 0 are interpreted as false, and non-zero values as true.
     * String values might be parsed according to common boolean string representations
     * (e.g., "true", "false", "yes", "no").
     * 
     * @param memberIdentifier Member identifier, which can be:
     *   - String name: The member's name or path (e.g., "fieldName" or "header.type")
     *   - Integer memberId: The member's numeric ID or index
     * @return boolean representation of the member's value
     */
    boolean booleanValueOf(Object memberIdentifier);
    
    // numbers
    /**
     * Returns the specified member's value as a Number object.
     * 
     * This method provides a generic numeric representation of the member's value, suitable for
     * any member that can be reasonably interpreted as a number. The returned Number object
     * could be any of Java's numeric types (Integer, Double, etc.) depending on the
     * member's native representation.
     * 
     * For non-numeric members, the implementation should attempt reasonable type conversion
     * or throw an exception if conversion is not possible.
     * 
     * @param memberIdentifier Member identifier, which can be:
     *   - String name: The member's name or path (e.g., "fieldName" or "header.type")
     *   - Integer memberId: The member's numeric ID or index
     * @return numeric representation of the member's value
     */
    Number numberValueOf(Object memberIdentifier);

    /**
     * Returns the specified member's value as a float.
     * 
     * This method provides the member's value converted to a float, suitable for
     * processing numeric members with single-precision floating-point values. For integer
     * members, the value is converted to float format. For double-precision members,
     * precision may be lost.
     * 
     * @param memberIdentifier Member identifier, which can be:
     *   - String name: The member's name or path (e.g., "fieldName" or "header.type")
     *   - Integer memberId: The member's numeric ID or index
     * @return float representation of the member's value
     */
    float floatValueOf(Object memberIdentifier);

    /**
     * Returns the specified member's value as a double.
     * 
     * This method provides the member's value converted to a double, suitable for
     * processing numeric members with double-precision floating-point values or for
     * calculations that require higher precision than float.
     * 
     * @param memberIdentifier Member identifier, which can be:
     *   - String name: The member's name or path (e.g., "fieldName" or "header.type")
     *   - Integer memberId: The member's numeric ID or index
     * @return double representation of the member's value
     */
    double doubleValueOf(Object memberIdentifier);

    /**
     * Returns the specified member's value as a BigDecimal.
     * 
     * This method provides the member's value as a BigDecimal, suitable for applications
     * that require arbitrary precision or exact decimal representation. This is particularly
     * useful for financial calculations or when handling very large or very precise decimal values.
     * 
     * @param memberIdentifier Member identifier, which can be:
     *   - String name: The member's name or path (e.g., "fieldName" or "header.type")
     *   - Integer memberId: The member's numeric ID or index
     * @return BigDecimal representation of the member's value
     */
    BigDecimal bigDecimalValueOf(Object memberIdentifier);

    /**
     * Returns the specified member's value as a long.
     * 
     * This method provides the member's value converted to a long, suitable for
     * processing numeric members with integer values that may exceed the range of a 32-bit integer.
     * For floating-point members, the value is truncated to an integer.
     * 
     * @param memberIdentifier Member identifier, which can be:
     *   - String name: The member's name or path (e.g., "fieldName" or "header.type")
     *   - Integer memberId: The member's numeric ID or index
     * @return long representation of the member's value
     */
    long longValueOf(Object memberIdentifier);

    /**
     * Returns the specified member's value as an int.
     * 
     * This method provides the member's value converted to an int, suitable for
     * processing numeric members with 32-bit integer values. For floating-point members,
     * the value is truncated to an integer.
     * 
     * @param memberIdentifier Member identifier, which can be:
     *   - String name: The member's name or path (e.g., "fieldName" or "header.type")
     *   - Integer memberId: The member's numeric ID or index
     * @return int representation of the member's value
     * @throws ArithmeticException if the value exceeds the range of a 32-bit integer
     */
    int intValueOf(Object memberIdentifier);

    /**
     * Returns the specified member's value as a BigInteger.
     * 
     * This method provides the member's value as a BigInteger, suitable for applications
     * that require arbitrary precision integer calculations. This is particularly useful
     * for handling very large integer values that exceed the range of built-in numeric types.
     * 
     * @param memberIdentifier Member identifier, which can be:
     *   - String name: The member's name or path (e.g., "fieldName" or "header.type")
     *   - Integer memberId: The member's numeric ID or index
     * @return BigInteger representation of the member's value
     */
    BigInteger bigIntValueOf(Object memberIdentifier);

    // other

    /**
     * Returns the specified member's value as a String.
     * 
     * This method is primarily intended for text-based members but will attempt to convert
     * other member types to string representations. For text members, it returns the original
     * string data. For other types, it typically returns a human-readable string representation
     * of the value.
     * 
     * This is useful for displaying member values in user interfaces or log files where
     * a textual representation is required.
     * 
     * @param memberIdentifier Member identifier, which can be:
     *   - String name: The member's name or path (e.g., "fieldName" or "header.type")
     *   - Integer memberId: The member's numeric ID or index
     * @return string value of the member, or a string representation if not a text member
     */
    String stringValueOf(Object memberIdentifier);

    /**
     * Returns the specified member's value as an Enumeration.
     * 
     * This method is specifically designed for members that use enumerated values, where
     * the member represents one of a predefined set of possible values. The returned
     * Enumeration object contains both the numeric value and its associated label.
     * 
     * Enumerations are commonly used for state machines, status indicators, or any signal
     * that has a limited set of discrete states with meaningful names.
     * 
     * @param memberIdentifier Member identifier, which can be:
     *   - String name: The member's name or path (e.g., "fieldName" or "header.type")
     *   - Integer memberId: The member's numeric ID or index
     * @return enumeration value of the member, or null if the member cannot be interpreted as an enumeration
     */
    Enumeration enumValueOf(Object memberIdentifier);
    
    /**
     * Returns the specified member's value as a byte array.
     * 
     * This method is primarily intended for binary members or members that contain raw binary data.
     * It provides access to the underlying bytes that constitute the member's value. This is useful
     * for working with protocol data, file contents, or any other binary information.
     * 
     * For non-binary members, this method may attempt to serialize the value into a byte array
     * representation, but the specific behavior depends on the member type.
     * 
     * @param memberIdentifier Member identifier, which can be:
     *   - String name: The member's name or path (e.g., "fieldName" or "header.type")
     *   - Integer memberId: The member's numeric ID or index
     * @return byte array value of the member, or null if the member cannot be represented as bytes
     */
    byte[] bytesValueOf(Object memberIdentifier);

    /**
     * Returns the specified member's value as a Struct.
     * 
     * This method is specifically designed for struct-type members and provides access
     * to nested structured data. The returned Struct object allows you to access individual
     * fields or components within the nested structure.
     * 
     * Structs are particularly useful for complex data types such as protocol headers,
     * nested records, or any data that has multiple related components organized hierarchically.
     * 
     * @param memberIdentifier Member identifier, which can be:
     *   - String name: The member's name or path (e.g., "fieldName" or "header.type")
     *   - Integer memberId: The member's numeric ID or index
     * @return struct value of the member, or null if the member cannot be interpreted as a struct
     */
    Struct structValueOf(Object memberIdentifier);
    


}
