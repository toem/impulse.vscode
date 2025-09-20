package de.toem.impulse.samples;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import de.toem.impulse.samples.attachments.IAttachment;
import de.toem.impulse.samples.base.SamplesStat;
import de.toem.impulse.samples.domain.DomainLongValue;
import de.toem.impulse.samples.domain.DomainValue;
import de.toem.impulse.samples.raw.Enumeration;
import de.toem.impulse.samples.raw.Logic;
import de.toem.impulse.samples.raw.Struct;

/**
 * Interface for readable samples in the impulse framework.
 *
 * This interface provides a comprehensive API for accessing and interacting with signal samples. 
 * Samples represent the fundamental data points in a signal, and this interface allows users to 
 * query, analyze, and manipulate these samples in a structured and efficient manner.
 *
 * Key features of this interface include:
 * - Access to sample metadata, such as position, value, and tags.
 * - Support for both discrete and continuous signals, enabling flexible handling of various signal types.
 * - Methods for retrieving attachments, compound data, and grouped samples.
 * - Iterators for traversing samples and groups within a specified range.
 *
 * The `IReadableSamples` interface is designed to be used in scenarios where signal data needs to be 
 * read and analyzed, such as in visualization tools, data processing pipelines, or simulation frameworks. 
 * It ensures that the data is settled (fully loaded and decoded) before access, providing a reliable 
 * and consistent view of the signal's state.
 *
 * Implementations of this interface are expected to handle large datasets efficiently, supporting up to 
 * 2^31 samples per signal. The interface also provides mechanisms for working with domain-specific 
 * attributes, such as domain base, scale, and format specifiers, making it suitable for a wide range 
 * of applications, including digital signal processing, system simulations, and data visualization.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 33
 */
public interface IReadableSamples extends ISamples {


    // ========================================================================================================================
    // Size of samples/groups
    // ========================================================================================================================

    /**
     * Samples may be organized in groups (e.g transactions). Returns the group count.
     * 
     */
    int getGroups();

    // ========================================================================================================================
    // Settlement
    // ========================================================================================================================

    /**
     * Returns true if the samples are settled, meaning data has been fetched and decoded.
     *
     * Settling ensures that the sample data is fully loaded and ready for processing. This is particularly useful when working with large datasets
     * or when the data is being fetched from an external source, such as a file or a remote server. By checking if the samples are settled, 
     * you can avoid accessing incomplete or partially loaded data, which could lead to errors or inconsistencies.
     *
     * @return true if settled, false otherwise
     */
    boolean isSettled();

    /**
     * Ensure settlement of samples data.
     *
     * This method is used to explicitly trigger the settlement process for sample data. It ensures that all necessary data is fetched and decoded
     * before further operations are performed. This is particularly useful in scenarios where the data source is asynchronous or when you need to 
     * guarantee that the data is ready for analysis or visualization.
     *
     * @return true if settled
     */
    // boolean ensureSettled(IProgress p);

    /**
     * Returns true if the settling process is ongoing, such as during loading or calculation.
     *
     * This method allows you to check if the settlement process is still in progress. This is useful for monitoring the state of data loading 
     * or processing, especially in applications with a user interface where you might want to display a loading indicator or prevent certain 
     * actions until the data is fully settled.
     *
     * @return true if settling is in progress, false otherwise
     */
    // boolean isSettling();

    // ========================================================================================================================
    // Index <> position
    // ========================================================================================================================

    /**
     * Returns the sample index at the given domain position.
     *
     * The domain position is specified as a multiple of its domain base. For example, if the domain base is 1ms and the multiple is 100, 
     * the domain value is 100ms. This method is essential for mapping a specific position in the signal's domain to its corresponding sample index.
     * It returns the sample index of the sample that is immediatly before or at the given position. If the position is not found, it returns -1.
     *
     * @param position the domain position as a multiple of its domain base
     * @return the sample index at the given position, or -1 if not found
     */
    int indexAt(long position);

    /**
     * Returns the sample index at the given domain range. 
     * 
     * The domain position is specified as a multiple of its domain base. For example, if the domain base is 1ms and the multiple is 100, 
     * the domain value is 100ms. This method is essential for mapping a specific position in the signal's domain to its corresponding sample index.
     * It returns any sample index of the sample that is immediatly before or inside the give range from position to position + range. 
     * This option is useful in optimizing the response time of the indexAt() method, as it allows for a more efficient search within a specified range.
     * 
     * @param position the domain position as a multiple of its domain base
     * @param range the domain range as a multiple of its domain base
     * @return the sample index at the given position and frame, or -1 if not found
     */
    int indexAt(long position, long range);

    /**
     * Returns the sample index at the given domain position.
     *
     * The position is provided as a DomainValue object, which combines domain base and factor. This method is particularly useful when working 
     * with domain-specific values that require additional context. By using a DomainValue object, you can 
     * ensure that the position is interpreted correctly within the signal's domain.
     * It returns the sample index of the sample that is immediatly before or at the given position. If the position is not found, it returns -1.
     *
     * @param position the domain position as a DomainValue object
     * @return the sample index at the given position, or -1 if not found
     */
    int indexAt(DomainValue position);


    /**
     * Returns the position at the given sample index, as a multiple of the domain base.
     *
     * This method is the inverse of `indexAt(long position)`. It allows you to determine the position in the signal's domain for a given sample index.
     * This is useful for tasks such as plotting or analyzing the temporal or spatial distribution of samples within a signal.
     *
     * @param idx the sample index (0..getCount()-1)
     * @return the position as a long value (to be multiplied with the domain base)
     */
    long multPosAt(int idx);

    /**
     * Returns the domain position at the given sample index as a DomainLongValue.
     *
     * This method provides a more detailed representation of the position, including domain-specific context.
     * It is particularly useful when working with signals that have complex domain characteristics or when you need to display or process 
     * the position in a human-readable format.
     *
     * @param idx the sample index (0..getCount()-1)
     * @return the domain position as a DomainLongValue
     */
    DomainLongValue positionAt(int idx);

    // ========================================================================================================================
    // Value/Status at index
    // ========================================================================================================================

    /**
     * Determines if the sample at the given index is a 'none' sample, indicating no signal value is available.
     * 
     * None samples typically represent gaps in the signal data or sections where no meaningful measurement was recorded.
     *
     * @param idx the sample index (0..getCount()-1)
     * @return true if the sample is a 'none' sample, false if the sample contains valid data
     */
    boolean isNoneAt(int idx);

    /**
     * Determines if the sample at the given index is tagged, indicating special significance.
     * 
     * Tagged samples are marked for various use-case specific purposes, such as highlighting important events,
     * marking anomalies, or indicating specific regions of interest in the signal. The exact meaning of a tag
     * depends on the application context and may be determined by the tag level (see {@link #getTagAt(int)}).
     *
     * @param idx the sample index (0..getCount()-1)
     * @return true if the sample has been tagged, false otherwise
     * @see #getTagAt(int) for retrieving the specific tag level
     */
    boolean isTaggedAt(int idx);

    /**
     * Retrieves the tag level of the sample at the given index.
     * 
     * Tag levels range from 1 to 15, with 1 being the highest priority. A tag level of 0 indicates
     * that the sample is not tagged. Tag levels can be used to categorize or prioritize samples
     * based on their significance or to implement different visual representations in user interfaces.
     * 
     * Common uses for tag levels include:
     * - Marking severity levels of anomalies (1 for critical, 2 for warning, etc.)
     * - Categorizing different types of events or points of interest
     * - Implementing multi-level filtering or highlighting in visualizations
     *
     * @param idx the sample index (0..getCount()-1)
     * @return tag level (1..15) if tagged, 0 if the sample is not tagged
     * @see #isTaggedAt(int) for checking if a sample is tagged
     */
    int getTagAt(int idx);

    /**
     * Retrieves the raw value of the sample at the given index without any interpretation or conversion.
     * 
     * The returned object type depends on the signal type:
     * - For analog signals: Number (Double, Integer, etc.)
     * - For digital signals: Logic
     * - For string signals: String
     * - For enum signals: Enumeration
     * - For structured signals: Struct
     * - For binary signals: byte[]
     * 
     * This method provides direct access to the underlying sample data in its native format.
     *
     * @param idx the sample index (0..getCount()-1)
     * @return the raw sample value as its native object type
     * @see #valueAt(int, int) for retrieving values with additional processing options
     */
    Object valueAt(int idx);

    /**
     * Retrieves the raw value of the sample at the given index with additional processing options.
     * 
     * The flags parameter controls how values are retrieved and processed. Available flag values include:
     * 
     * - ISample.VALUE_DEFAULT (0x0): Default behavior
     * - ISample.VALUE_MASK (0x3): Mask for value flags
     * - ISample.VALUE_NO_ENUMS (0x1): Prevent enumeration resolution
     * - ISample.VALUE_WRITABLE (0x2): Return a writable representation
     * 
     * These flags can be combined using bitwise OR operations to include multiple features.
     * For example, use (VALUE_NO_ENUMS | AT_LABEL) to get a raw value (not resolved to 
     * enumerations) with label attachments.
     * 
     * Example (Java):
     *   // Get the raw value without enum resolution
     *   Object rawValue = signal.valueAt(42, ISample.VALUE_NO_ENUMS);
     *   System.out.println("Raw value: " + rawValue);
     * 
     * @param idx the sample index (0..getCount()-1)
     * @param flags bit flags that control retrieval behavior
     * @return the raw sample value, potentially modified according to the flags
     */
    Object valueAt(int idx, int flags);

    /**
     * Retrieves all attachments associated with the sample at the given index.
     * 
     * Attachments are additional pieces of information linked to a sample, such as relations and labels. 
     * They provide context or supplementary data beyond
     * the primary sample value itself.
     * 
     * If no attachments exist for the sample, an empty list is returned.
     *
     * @param idx the sample index (0..getCount()-1)
     * @return list of all attachments for the sample, never null
     * @see #attachmentsAt(int, int) for retrieving attachments filtered by type
     */
    List<IAttachment> attachmentsAt(int idx);

    /**
     * Retrieves attachments of a specific type associated with the sample at the given index.
     * 
     * Attachments are categorized by type for efficient filtering and processing. The standard
     * attachment types include:
     * 
     * - ISample.AT_RELATION (1 << 2): Relations to other samples or signals
     * - ISample.AT_LABEL (2 << 2): Text labels or annotations
     * 
     * Passing -1 as the type parameter is equivalent to calling {@link #attachmentsAt(int)},
     * returning all attachments regardless of type.
     * 
     * If no attachments of the specified type exist, an empty list is returned.
     * 
     * Example (Java):
     *   // Get all label attachments for a sample
     *   List<IAttachment> labels = signal.attachmentsAt(sampleIndex, ISample.AT_LABEL);
     *   for (IAttachment label : labels) {
     *     System.out.println("Label: " + label.toString());
     *   }
     *
     * @param idx the sample index (0..getCount()-1)
     * @param type attachment type identifier (AT_RELATION, AT_LABEL), or -1 for all types
     * @return list of attachments of the specified type, never null
     */
    List<IAttachment> attachmentsAt(int idx, int type);

    /**
     * Creates a comprehensive compound object containing the sample value and all related information.
     * 
     * The returned compound object encapsulates:
     * - The sample value
     * - Sample position/timestamp
     * - Group information (if applicable)
     * - Order information (if part of a sequence)
     * - Layer information (if multi-layered)
     * - Original sample index
     * - Attachments
     * 
     * This method is useful when you need a complete, self-contained representation of a sample
     * that includes all relevant context.
     *
     * @param idx the sample index (0..getCount()-1)
     * @return a compound object containing the sample and all its metadata
     * @see #compoundAt(int, int) for creating a compound with custom options
     */
    IReadableSample compoundAt(int idx);

    /**
     * Creates a customized compound object containing the sample value and selected information.
     * 
     * The flags parameter controls which additional information is included in the compound object.
     * Available flag values include:
     * 
     * - ISample.VALUE_DEFAULT (0x0): Default behavior
     * - ISample.VALUE_MASK (0x3): Mask for value flags
     * - ISample.VALUE_NO_ENUMS (0x1): Prevent enumeration resolution
     * - ISample.VALUE_WRITABLE (0x2): Return a writable representation
     * - ISample.AT_RELATION (1 << 2): Include relation attachments
     * - ISample.AT_LABEL (2 << 2): Include label attachments
     * 
     * These flags can be combined using bitwise OR operations to include multiple features.
     * For example, use (VALUE_NO_ENUMS | AT_LABEL) to get a compound with raw values 
     * (not resolved to enumerations) and label attachments.
     * 
     * Example (Java):
     *   // Get a compound with raw values and label attachments
     *   int flags = ISample.VALUE_NO_ENUMS | ISample.AT_LABEL;
     *   IReadableSample sample = signal.compoundAt(42, flags);
     *   
     *   // Process the sample
     *   System.out.println("Position: " + sample.getPosition());
     *   System.out.println("Raw value: " + sample.valueAt(0));
     *
     * @param idx the sample index (0..getCount()-1)
     * @param flags bit flags that control what information to include
     * @return a compound object containing the sample and selected metadata
     */
    IReadableSample compoundAt(int idx, int flags);

    /**
     * Returns a packed representation of the sample at the given index for internal use.
     * 
     * This is a low-level method primarily intended for internal usage by the framework itself
     * or by performance-critical applications. The packed format provides a more efficient
     * representation of the sample data for operations that need to process samples rapidly.
     *
     * @param idx the sample index (0..getCount()-1)
     * @param flags additional flags for packing (implementation-specific)
     * @return a packed sample object optimized for internal processing
     */
    ISamplePack packedAt(int idx, int flags);

    // logic
    /**
     * Retrieves the sample at the given index as a Logic value for digital signals.
     * 
     * This method is specifically designed for digital/logic signals and provides a convenient
     * way to access the value as a Logic object. For non-digital signals, the implementation
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
     * @param idx the sample index (0..getCount()-1)
     * @return logic value representation of the sample
     */
    Logic logicValueAt(int idx);

    /**
     * Retrieves the sample at the given index as an integer representing a logic state.
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
     * @param idx the sample index (0..getCount()-1)
     * @return integer code representing the logic state
     */
    int logicStateAt(int idx);

    /**
     * Determines if an edge of the specified type is present at the given sample index using a custom detector.
     * 
     * This method checks whether the transition from the previous sample to the current sample
     * constitutes an edge of the specified type. The detector parameter allows for customized
     * edge detection logic for specific application needs.
     * 
     * Edge types:
     * - 1: Rising edge (transition from low to high, typically STATE_0_BITS to STATE_1_BITS)
     * - -1: Falling edge (transition from high to low, typically STATE_1_BITS to STATE_0_BITS)
     * - 0: Any edge (either rising or falling)
     *
     * @param idx the sample index (0..getCount()-1)
     * @param edge edge type to detect (1=rising, -1=falling, 0=any)
     * @param detector custom logic detector implementation for specialized detection rules
     * @return true if the specified edge type is detected, false otherwise
     */
    boolean isEdgeAt(int idx, int edge, ILogicDetector detector);

    /**
     * Determines if an edge of the specified type is present at the given sample index.
     * 
     * This method uses the default edge detection logic to check whether the transition
     * from the previous sample to the current sample constitutes an edge of the specified type.
     * 
     * Edge types:
     * - 1: Rising edge (transition from low to high, typically STATE_0_BITS to STATE_1_BITS)
     * - -1: Falling edge (transition from high to low, typically STATE_1_BITS to STATE_0_BITS)
     * - 0: Any edge (either rising or falling)
     * 
     * For the first sample in the signal (idx=0), this will typically return false since
     * there is no previous sample to compare with.
     *
     * @param idx the sample index (0..getCount()-1)
     * @param edge edge type to detect (1=rising, -1=falling, 0=any)
     * @return true if the specified edge type is detected, false otherwise
     */
    boolean isEdgeAt(int idx, int edge);

    /**
     * Determines if the sample at the given index represents a high logic state using a custom detector.
     * 
     * The detector parameter allows for customized logic level detection rules, which can be
     * useful for signals with non-standard voltage levels or specialized interpretation criteria.
     * 
     * By default, a high state typically corresponds to STATE_1_BITS (1) or STATE_H_BITS (5),
     * but the detector can apply different rules based on the application context.
     *
     * @param idx the sample index (0..getCount()-1)
     * @param detector custom logic detector implementation for specialized detection rules
     * @return true if the sample represents a high logic state, false otherwise
     */
    boolean isHighAt(int idx, ILogicDetector detector);

    /**
     * Determines if the sample at the given index represents a high logic state.
     * 
     * This method uses the default logic level detection rules to determine whether
     * the sample represents a high state. For digital signals, this typically corresponds
     * to logic '1' (STATE_1_BITS) or 'H' (STATE_H_BITS), but the exact interpretation may 
     * depend on the signal's characteristics.
     *
     * @param idx the sample index (0..getCount()-1)
     * @return true if the sample represents a high logic state, false otherwise
     */
    boolean isHighAt(int idx);

    /**
     * Determines if the sample at the given index represents a low logic state using a custom detector.
     * 
     * The detector parameter allows for customized logic level detection rules, which can be
     * useful for signals with non-standard voltage levels or specialized interpretation criteria.
     * 
     * By default, a low state typically corresponds to STATE_0_BITS (0) or STATE_L_BITS (4),
     * but the detector can apply different rules based on the application context.
     *
     * @param idx the sample index (0..getCount()-1)
     * @param detector custom logic detector implementation for specialized detection rules
     * @return true if the sample represents a low logic state, false otherwise
     */
    boolean isLowAt(int idx, ILogicDetector detector);

    /**
     * Determines if the sample at the given index represents a low logic state.
     * 
     * This method uses the default logic level detection rules to determine whether
     * the sample represents a low state. For digital signals, this typically corresponds
     * to logic '0' (STATE_0_BITS) or 'L' (STATE_L_BITS), but the exact interpretation may 
     * depend on the signal's characteristics.
     *
     * @param idx the sample index (0..getCount()-1)
     * @return true if the sample represents a low logic state, false otherwise
     */
    boolean isLowAt(int idx);

    // numbers
    /**
     * Retrieves the sample at the given index as a Number value.
     * 
     * This method provides a generic numeric representation of the sample value, suitable for
     * any signal that can be reasonably interpreted as a number. The returned Number object
     * could be any of Java's numeric types (Integer, Double, etc.) depending on the
     * signal's native representation.
     * 
     * For non-numeric signals, the implementation should attempt reasonable type conversion
     * or throw an exception if conversion is not possible.
     *
     * @param idx the sample index (0..getCount()-1)
     * @return numeric representation of the sample value
     */
    Number numberValueAt(int idx);

    /**
     * Retrieves the sample at the given index as a float value.
     * 
     * This method provides the sample value converted to a float, suitable for
     * processing signals with single-precision floating-point values. For integer signals,
     * the value is converted to float format. For double-precision signals, precision may be lost.
     *
     * @param idx the sample index (0..getCount()-1)
     * @return float representation of the sample value
     */
    float floatValueAt(int idx);

    /**
     * Retrieves the sample at the given index as a double value.
     * 
     * This method provides the sample value converted to a double, suitable for
     * processing signals with double-precision floating-point values or for calculations
     * that require higher precision than float.
     *
     * @param idx the sample index (0..getCount()-1)
     * @return double representation of the sample value
     */
    double doubleValueAt(int idx);

    /**
     * Retrieves the sample at the given index as a BigDecimal value.
     * 
     * This method provides the sample value as a BigDecimal, suitable for applications
     * that require arbitrary precision or exact decimal representation. 
     *
     * @param idx the sample index (0..getCount()-1)
     * @return BigDecimal representation of the sample value
     */
    BigDecimal bigDecimalValueAt(int idx);

    /**
     * Retrieves the sample at the given index as a long value.
     * 
     * This method provides the sample value converted to a long, suitable for
     * processing signals with integer values that may exceed the range of a 32-bit integer.
     * For floating-point signals, the value is truncated to an integer.
     *
     * @param idx the sample index (0..getCount()-1)
     * @return long representation of the sample value
     */
    long longValueAt(int idx);

    /**
     * Retrieves the sample at the given index as an int value.
     * 
     * This method provides the sample value converted to an int, suitable for
     * processing signals with 32-bit integer values. For floating-point signals,
     * the value is truncated to an integer.
     *
     * @param idx the sample index (0..getCount()-1)
     * @return int representation of the sample value
     */
    int intValueAt(int idx);

    /**
     * Retrieves the sample at the given index as a BigInteger value.
     * 
     * This method provides the sample value as a BigInteger, suitable for applications
     * that require arbitrary precision integer calculations. This is particularly useful
     * for handling very large integer values that exceed the range of built-in numeric types.
     *
     * @param idx the sample index (0..getCount()-1)
     * @return BigInteger representation of the sample value
     */
    BigInteger bigIntValueAt(int idx);

    // other

    /**
     * Retrieves the sample at the given index as a Struct value for structured signals.
     * 
     * This method is specifically designed for signals with structured data and provides access
     * to samples that contain multiple fields or hierarchical data. The returned Struct object
     * allows you to access individual members or fields within the structured data.
     * 
     * Structs are particularly useful for complex data types such as protocol packets, 
     * multi-dimensional measurements, or any data that has multiple related components.
     *
     * @param idx the sample index (0..getCount()-1)
     * @return struct value of the sample, or null if the sample cannot be interpreted as a struct
     */
    Struct structValueAt(int idx);

    /**
     * Retrieves the sample at the given index as a String value.
     * 
     * This method is primarily intended for text-based signals but will attempt to convert
     * other signal types to string representations. For text signals, it returns the original
     * string data. For other types, it typically returns a human-readable string representation
     * of the value.
     * 
     * This is useful for displaying signal values in user interfaces or log files where
     * a textual representation is required.
     *
     * @param idx the sample index (0..getCount()-1)
     * @return string value of the sample, or a string representation if not a text signal
     */
    String stringValueAt(int idx);

    /**
     * Retrieves the sample at the given index as an Enumeration value.
     * 
     * This method is specifically designed for signals that use enumerated values, where
     * each sample represents one of a predefined set of possible values. The returned
     * Enumeration object contains both the numeric value and its associated label.
     * 
     * Enumerations are commonly used for state machines, status indicators, or any signal
     * that has a limited set of discrete states with meaningful names.
     *
     * @param idx the sample index (0..getCount()-1)
     * @return enumeration value of the sample, or null if the sample cannot be interpreted as an enumeration
     */
    Enumeration enumValueAt(int idx);

    /**
     * Retrieves the sample at the given index as a byte array.
     * 
     * This method is primarily intended for binary signals or signals that contain raw binary data.
     * It provides access to the underlying bytes that constitute the sample value. This is useful
     * for working with protocol data, file contents, or any other binary information stored in signals.
     * 
     * For non-binary signals, this method may attempt to serialize the value into a byte array
     * representation, but the specific behavior depends on the signal type.
     *
     * @param idx the sample index (0..getCount()-1)
     * @return byte array value of the sample, or null if the sample cannot be represented as bytes
     */
    byte[] bytesValueAt(int idx);

    // ========================================================================================================================
    // Signal value to text
    // ========================================================================================================================

    /**
     * Formats the sample at the given index using the specified format.
     * 
     * This method provides flexible formatting options for converting sample values to string
     * representations based on predefined format specifiers. The format parameter accepts
     * standard format strings as defined in ISample:
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
     * If the specified format is not applicable to the sample type, the method falls back
     * to a reasonable default format.
     *
     * @param idx the sample index (0..getCount()-1)
     * @param format format specifier string as defined in ISample (e.g., "hex", "dec")
     * @return formatted string representation of the sample value
     * @see ISample for all available format specifiers
     */
    String formatAt(int idx, String format);

    /**
     * Returns the default format specifier for the sample at the given index.
     * 
     * The default format is determined based on the sample's data type and content.
     * For example:
     * - Logic samples typically default to binary format
     * - Numeric samples typically default to decimal format
     * - Enum samples typically default to label format
     * 
     * This is useful when you need to display a sample value but don't have specific
     * formatting requirements.
     *
     * @param idx the sample index (0..getCount()-1)
     * @return default format specifier string as defined in ISample
     */
    String defaultFormatAt(int idx);

    /**
     * Formats the sample at the given index using hexadecimal format.
     * 
     * This is a convenience method equivalent to calling {@link #formatAt(int, String)}
     * with format parameter set to ISample.FORMAT_HEXADECIMAL ("hex"). The output 
     * typically includes a "0x" prefix for numeric values, and represents each 
     * digit using characters 0-9 and A-F.
     * 
     * For multi-byte values, the representation follows the platform's endianness 
     * unless otherwise specified by the implementation.
     *
     * @param idx the sample index (0..getCount()-1)
     * @return hexadecimal string representation of the sample value
     */
    String fhexAt(int idx);

    /**
     * Formats the sample at the given index using decimal format.
     * 
     * This is a convenience method equivalent to calling {@link #formatAt(int, String)}
     * with format parameter set to ISample.FORMAT_DECIMAL ("dec"). For numeric samples,
     * this produces a standard base-10 representation.
     * 
     * For non-numeric samples, the implementation attempts to provide a meaningful
     * decimal interpretation when possible.
     *
     * @param idx the sample index (0..getCount()-1)
     * @return decimal string representation of the sample value
     */
    String fdecAt(int idx);

    /**
     * Formats the sample at the given index using octal format.
     * 
     * This is a convenience method equivalent to calling {@link #formatAt(int, String)}
     * with format parameter set to ISample.FORMAT_OCTAL ("oct"). The output represents
     * the value using base-8 notation (digits 0-7).
     * 
     * For non-numeric samples, the implementation attempts to provide a meaningful
     * octal interpretation when possible.
     *
     * @param idx the sample index (0..getCount()-1)
     * @return octal string representation of the sample value
     */
    String foctAt(int idx);

    /**
     * Formats the sample at the given index using binary format.
     * 
     * This is a convenience method equivalent to calling {@link #formatAt(int, String)}
     * with format parameter set to ISample.FORMAT_BINARY ("bin"). The output represents
     * the value using base-2 notation (digits 0 and 1).
     * 
     * For logic samples, this shows the direct bit values. For numeric types, this
     * shows the binary representation of the underlying value. The representation may 
     * include leading zeros or grouping based on the implementation.
     *
     * @param idx the sample index (0..getCount()-1)
     * @return binary string representation of the sample value
     */
    String fbinAt(int idx);

    /**
     * Formats the sample at the given index using ASCII format.
     * 
     * This is a convenience method equivalent to calling {@link #formatAt(int, String)}
     * with format parameter set to ISample.FORMAT_ASCII ("ascii"). For byte or character
     * values, this attempts to represent the value as printable ASCII characters.
     * 
     * Non-printable characters are typically represented using escape sequences or
     * placeholder characters, depending on the implementation.
     *
     * @param idx the sample index (0..getCount()-1)
     * @return ASCII string representation of the sample value
     */
    String fasciiAt(int idx);

    // ========================================================================================================================
    // Grouped values
    // ========================================================================================================================

    /**
     * Returns the group index for the sample at the given index.
     * 
     * In impulse, samples can be organized into logical groups like transactions, packets, or any 
     * other related collection of data points. Each group has a unique index that links its 
     * member samples together.
     * 
     * The group index is simply a sequential number from 0 to getGroups()-1, identifying each
     * group in the signal. A return value of ISample.GROUP_NONE (-1) indicates the sample 
     * doesn't belong to any group.
     *
     * @param idx the sample index (0..getCount()-1)
     * @return the group index (0..getGroups()-1), or ISample.GROUP_NONE (-1) if not grouped
     * @see #getGroups() for the total number of groups
     * @see #orderAt(int) for determining the sample's position within its group
     */
    int groupAt(int idx);

    /**
     * Returns the order specifier of the sample within its group.
     * 
     * The order specifier indicates the sample's position within its logical group:
     * 
     * - ISample.GO_INITIAL (0): First sample in a multi-sample group
     * - ISample.GO_INTER (1): Intermediate sample within a group
     * - ISample.GO_FINAL (2): Last sample in a multi-sample group
     * - ISample.GO_SINGLE (3): Single sample that constitutes its own group
     * - ISample.GO_NONE (-1): Not part of any group
     * 
     * This information is useful for processing groups in sequence, identifying boundaries 
     * between groups, or finding specific positions within a group.
     *
     * @param idx the sample index (0..getCount()-1)
     * @return order specifier as defined in ISample.GO_* constants
     * @see ISample#GO_INITIAL
     * @see ISample#GO_INTER
     * @see ISample#GO_FINAL
     * @see ISample#GO_SINGLE
     * @see ISample#GO_NONE
     */
    int orderAt(int idx);

    /**
     * Returns the layer identifier for the sample at the given index.
     * 
     * In the impulse framework, layers and groups are closely related concepts:
     * - All samples in the same group belong to the same layer
     * - Groups are formed by sequences of samples in the same layer
     * - A group starts with a sample having order GO_INITIAL and ends with GO_FINAL
     * 
     * Layers provide an organizational dimension that allows multiple independent sample
     * sequences to exist in parallel without conflicts, even when they overlap in the
     * time/position domain.
     * 
     * Common uses for layers include:
     * - Protocol stack layers (physical, data link, network, etc.)
     * - Parallel transaction streams
     * - Hierarchical representations of data
     * 
     * A return value of 0 indicates the sample is not in any specific layer.
     *
     * @param idx the sample index (0..getCount()-1)
     * @return layer identifier (1..getLayers()), or 0 if not layered
     * @see #getLayers() for the total number of layers
     * @see #groupAt(int) for determining the group a sample belongs to
     * @see #orderAt(int) for determining the sample's position within its group
     */
    int layerAt(int idx);

    /**
     * Retrieves the aggregated value(s) for all samples in the specified group.
     * 
     * Instead of accessing individual samples that belong to a group, this method 
     * provides a more convenient way to retrieve a consolidated representation of 
     * the entire group. The structure and content of the returned object depend on
     * the signal type.
     *
     * @param group the group index (0..getGroups()-1)
     * @return aggregated value(s) for all samples in the group
     * @see #valuesAtGroup(int, int) for retrieving values with additional options
     */
    Object valuesAtGroup(int group);

    /**
     * Retrieves the aggregated value(s) for all samples in the specified group with additional processing options.
     * 
     * The flags parameter controls how values are retrieved and processed, similar to 
     * the {@link #valueAt(int, int)} method. Common flag values are defined in ISample:
     * 
     * - VALUE_DEFAULT (0x0): Default behavior
     * - VALUE_NO_ENUMS (0x1): Prevent enumeration resolution
     * - VALUE_WRITABLE (0x2): Return a writable representation
     * - AT_RELATION (1 << 2): Include relation attachments
     * - AT_LABEL (2 << 2): Include label attachments
     * 
     * @param group the group index (0..getGroups()-1)
     * @param flags bit flags that control retrieval behavior
     * @return aggregated value(s) for the group, potentially modified according to the flags
     */
    Object valuesAtGroup(int group, int flags);

    /**
     * Retrieves all attachments associated with the specified group.
     * 
     * Group attachments apply to the entire group rather than individual samples.
     * They can provide metadata, relationships, or annotations for the group as a whole.
     * 
     * If no attachments exist for the group, an empty list is returned.
     *
     * @param group the group index (0..getGroups()-1)
     * @return list of all attachments for the group, never null
     * @see #attachmentsAtGroup(int, int) for retrieving attachments filtered by type
     */
    List<IAttachment> attachmentsAtGroup(int group);

    /**
     * Retrieves attachments of a specific type associated with the specified group.
     * 
     * This method filters group attachments by their type, returning only those that match
     * the specified type identifier. Common attachment types include:
     * 
     * - ISample.AT_RELATION (1 << 2): Relations to other groups or signals
     * - ISample.AT_LABEL (2 << 2): Text labels or annotations
     * 
     * Passing -1 as the type parameter returns all attachments regardless of type.
     * If no attachments of the specified type exist, an empty list is returned.
     * 
     * @param group the group index (0..getGroups()-1)
     * @param type attachment type identifier, or -1 for all types
     * @return list of attachments of the specified type, never null
     */
    List<IAttachment> attachmentsAtGroup(int group, int type);

    /**
     * Creates a comprehensive compound object containing group values and all related information.
     * 
     * The returned compound object encapsulates:
     * - Aggregated values for all samples in the group
     * - Group position/timespan information
     * - Layer information
     * - Group index
     * - Attachments
     * 
     * This method is useful when you need a complete, self-contained representation of a group
     * that includes all relevant context for processing or analysis.
     *
     * @param group the group index (0..getGroups()-1)
     * @return a compound object containing the group data and all its metadata
     * @see #compoundAtGroup(int, int) for creating a compound with custom options
     */
    IReadableGroup compoundAtGroup(int group);

    /**
     * Creates a customized compound object containing group values and selected information.
     * 
     * The flags parameter controls which additional information is included in the compound object,
     * similar to the {@link #compoundAt(int, int)} method. The same bit flags apply.
     *
     * @param group the group index (0..getGroups()-1)
     * @param flags bit flags that control what information to include
     * @return a compound object containing the group data and selected metadata
     */
    IReadableGroup compoundAtGroup(int group, int flags);

    /**
     * Returns the sample index of the first sample in the specified group.
     * 
     * This method provides an efficient way to locate the beginning of a group,
     * which is particularly useful when processing groups sequentially or when
     * navigating between groups.
     * 
     * For groups containing multiple samples, this returns the index of the sample
     * with order specifier GO_INITIAL or GO_SINGLE.
     *
     * @param group the group index (0..getGroups()-1)
     * @return sample index of the first sample in the group
     */
    int indexAtGroup(int group);

    /**
     * Returns the number of layers in the samples.
     * 
     * Layers provide an additional dimension for organizing samples beyond grouping.
     * Samples in different layers can occupy the same position in the domain without conflict.
     * 
     * A return value of 0 indicates there are no layers defined in the signal.
     *
     * @return the total number of layers, or 0 if not layered
     * @see #layerAt(int) for determining the layer of a specific sample
     */
    int getLayers();

    // ========================================================================================================================
    // Members and Enums
    // ========================================================================================================================

    /**
     * Returns all member descriptors for the signal.
     * 
     * Member descriptors define the structure of complex signals, particularly for structured data types.
     * Each member descriptor represents a field or component within the signal's structure, providing
     * metadata such as name, data type, and hierarchical relationships.
     * 
     * For simple signals (like primitive types), this typically returns an empty list.
     * For struct signals and array, this returns descriptors for each field in the structure.
     *
     * @return list of member descriptors, never null
     * @see IMemberDescriptor for the available properties of member descriptors
     */
    List<IMemberDescriptor> getMemberDescriptors();

    /**
     * Returns all enumerations of the given type.
     * 
     * Enumerations map numeric values to meaningful string labels, providing symbolic 
     * representations for discrete states or categories within a signal. Different
     * enumeration types serve different purposes in the impulse framework:
     * 
     * - ISample.ENUM_GLOBAL (0): Global enumerations used across the entire signal
     * - ISample.ENUM_RELATION_TARGET (1): Enumerations for relation targets
     * - ISample.ENUM_RELATION_STYLE (2): Enumerations for relation display styles
     * - ISample.ENUM_LABEL_STYLE (3): Enumerations for label styles
     *
     * @param enumerationType enumeration type as defined in ISample (ENUM_*)
     * @return list of enumerations for the specified type, never null
     * @see ISample#ENUM_GLOBAL
     * @see ISample#ENUM_RELATION_TARGET
     * @see ISample#ENUM_RELATION_STYLE
     */

    List<Enumeration> getEnums(int enumerationType);

    /**
     * Returns the member descriptor for the specified member identifier.
     * 
     * This method provides a flexible way to look up a member descriptor using different
     * types of identifiers:
     * - String (name): The member's simple name (case-sensitive, e.g., "field1")
     * - String (path): The member's full path including parent structures (e.g., "parent.field1")
     * - Integer (member ID): The member's numeric ID in a struct
     * - Integer (index): The member's index in an array or in the list returned by getMemberDescriptors()
     * 
     * Both structured data (structs) and arrays can have member descriptors, though they are
     * optional for arrays. For structs, every field has its own member descriptor with a unique ID.
     * 
     * This flexible lookup mechanism makes it easy to navigate complex data structures regardless
     * of how you reference their components.
     * 
     * @param memberIdentifier member identifier (String name/path, Integer ID/index)
     * @return member descriptor for the specified identifier, or null if not found
     * @see #getMemberDescriptors() for retrieving all available member descriptors
     * @see IMemberDescriptor#getPath() for understanding the path notation used for nested members
     */
    IMemberDescriptor getMemberDescriptor(Object memberIdentifier);

    /**
     * Returns all enumerations for the specified member.
     * 
     * For members that use enumerated values, this method provides access to all
     * valid enumeration values that can be associated with the member. This is useful
     * for discovering the possible states or categories that a particular field can take.
     * 
     * For example, a "state" field might have enumerations for "running", "paused", and "stopped",
     * each mapped to a numeric value.
     *
     * @param memberIdentifier member identifier (String name, Integer id, or index)
     * @return list of enumerations for the specified member, never null
     */
    List<Enumeration> getMemberEnums(Object memberIdentifier);

    /**
     * Returns the enumeration for the specified member and label.
     * 
     * This method allows you to look up an enumeration value by its string label.
     * It's useful when you have a readable label (like "running") and need to find
     * the corresponding enumeration object with its numeric value and metadata.
     * 
     * The lookup is specific to the identified member, so the same label could map
     * to different enumerations in different members.
     *
     * @param memberIdentifier member identifier (String name, Integer id, or index)
     * @param label enumeration label to find (case-sensitive)
     * @return enumeration for the specified label, or null if not found
     */
    Enumeration getMemberEnum(Object memberIdentifier, String label);

    /**
     * Returns the enumeration for the specified member and numeric value.
     * 
     * This method allows you to look up an enumeration by its numeric value.
     * It's useful when you have a raw value (like 2) and need to find the 
     * corresponding enumeration object with its string label and metadata.
     * 
     * The lookup is specific to the identified member, so the same numeric value
     * could map to different enumerations in different members.
     *
     * @param memberIdentifier member identifier (String name, Integer id, or index)
     * @param value enumeration numeric value to find
     * @return enumeration for the specified value, or null if not found
     */
    Enumeration getMemberEnum(Object memberIdentifier, int value);

    /**
     * Returns all member descriptors that contain the specified tag content.
     * 
     * This method allows for querying members based on tag content, which can be used
     * for categorization, filtering, or specialized processing. For example, you might
     * tag certain members as "critical" or "deprecated" and then use this method to
     * find all such members.
     * 
     * The search looks for any tag containing the specified content as a substring,
     * making it a flexible way to group related members across a complex structure.
     *
     * @param content tag content to search for (case-sensitive)
     * @return list of member descriptors containing the specified tag, never null
     */
    List<IMemberDescriptor> getMembersForTag(String content);

    // ========================================================================================================================
    // Iterators (internal use only)
    // ========================================================================================================================

    /**
     * Creates an iterator for traversing samples within a specified domain position range.
     * 
     * This method provides an efficient way to iterate through a subset of samples without
     * having to manually track indices or positions. The returned iterator encapsulates the
     * logic for navigating through the samples sequentially, allowing for cleaner code when
     * processing ranges of samples.
     * 
     * @param start start position as a multiple of the domain base
     * @param end end position as a multiple of the domain base
     * @return an iterator that traverses all samples between the start and end positions
     * @see ISampleIterator for the methods available on the returned iterator
     */
    ISampleIterator sampleIterator(long start, long end);

    /**
     * Creates an iterator for traversing groups within a specified domain position range and layer.
     * 
     * This method allows for efficient iteration through logical groups of samples rather than
     * individual samples. Groups represent higher-level entities like protocol transactions, 
     * waveform bursts, or any other logical grouping of samples.
     * 
     * The layer parameter allows focusing on groups from a specific layer, which is useful when
     * working with multi-layered signals (e.g., protocol stacks). Specifying -1 for the layer
     * parameter will include groups from all layers.
     * 
     * @param start start position as a multiple of the domain base
     * @param end end position as a multiple of the domain base
     * @param layer layer to include (1..getLayers()), or -1 for all layers
     * @return an iterator that traverses all groups between the start and end positions in the specified layer(s)
     * @see IGroupIterator for the methods available on the returned iterator
     * @see #getLayers() for determining the number of available layers
     */
    IGroupIterator groupIterator(long start, long end, int layer);

    
    // ========================================================================================================================
    // Samples statistics (internal use only)
    // ========================================================================================================================

    /**
     * Returns a statistics object for all samples in the specified index range.
     *
     * Intended for internal usage.
     *
     * @param idx0 first sample index (0..getCount()-1)
     * @param idxN last sample index (0..getCount()-1)
     * @param content stat information to be extracted (implementation-specific)
     * @return statistics object for the specified sample range
     */
    SamplesStat getStat(int idx0, int idxN, int content);

}
