package de.toem.impulse.samples;

import de.toem.toolkits.pattern.general.ITagged;
import de.toem.toolkits.pattern.information.IInformation;

/**
 * Interface defining the fundamental characteristics of a sample in the impulse framework.
 *
 * This interface provides essential metadata and properties that describe an individual sample's type,
 * format, and scale. It serves as a foundational interface for all sample-based data access in the
 * impulse framework, establishing the core attributes that define how sample data should be interpreted.
 *
 * In impulse, a signal can contain up to 2^31 samples, and each sample has distinct characteristics
 * that determine how it should be processed, displayed, and analyzed. These characteristics include:
 *
 * - Sample type: Categorizes the sample as logic, float, integer, enum, text, array, struct, or binary
 * - Format specifier: Defines how the sample's value should be displayed (binary, hex, decimal, etc.)
 * - Scale: Specifies the dimension of the sample, such as bit width for logic signals or precision for numbers
 * - Tags: Provides additional context or categorization for specialized handling and visualization
 *
 * This interface is designed to be lightweight and focused solely on the descriptive aspects of samples,
 * without including methods for data access or manipulation. It establishes a common foundation for more
 * specialized interfaces that handle specific aspects of sample processing.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 41
 */
public interface ISampleCharacteristic extends ISample,IInformation, ITagged.SimpleTags {

    /**
     * Returns the signal type of the sample.
     * 
     * The sample type identifies the fundamental data representation used for the sample values.
     * It determines how the raw data should be interpreted and processed. The returned value
     * is one of the DATA_TYPE_* constants defined in ISample.
     * 
     * Supported sample types include:
     * - ISample.DATA_TYPE_LOGIC (0x5): Digital signals with 1 to N bits, which can be stored as 
     *   2-state (0,1), 4-state (0,1,Z,X), or 16-state data. Ideal for binary states in digital circuits.
     * - ISample.DATA_TYPE_FLOAT (0x3): Floating-point numbers (typically 32-bit or 64-bit), used for 
     *   continuous data like sensor readings or mathematical computations.
     * - ISample.DATA_TYPE_INTEGER (0x2): Whole numbers of any length, useful for counters, IDs, or 
     *   any data requiring precise integer values.
     * - ISample.DATA_TYPE_ENUM (0x1): Represents a predefined set of named values, ideal for states 
     *   or categories (e.g., ON, OFF, IDLE).
     * - ISample.DATA_TYPE_TEXT (0x4): Textual data, useful for labels, descriptions, or any 
     *   string-based information.
     * - ISample.DATA_TYPE_STRUCT (0x7): Complex data types with unlimited members, used for logs, 
     *   transactions, charts, Gantt events, and more.
     * - ISample.DATA_TYPE_BINARY (0x6): Binary data, which can include images, files, or other 
     *   non-textual data.
     * - Various array types (0x8-0xB): Collections of elements of a specific type (enum, integer, 
     *   float, or text).
     * 
     * The sample type is essential for proper data access and conversion operations, as it
     * determines which value retrieval methods are applicable and how data should be
     * formatted for display.
     *
     * @return The sample type as defined by ISample.DATA_TYPE_* constants
     * @see ISample for all available DATA_TYPE_* constants
     * @see #getSampleMainType() for retrieving just the main type component
     */
    int getSampleType();

    /**
     * Returns the main signal type without any modifiers or extensions.
     * 
     * This convenience method extracts just the core type information from the full sample type,
     * filtering out any additional flags or modifiers that might be present. It uses the
     * ISample.DATA_TYPE_MASK_MAIN mask to isolate the fundamental type.
     * 
     * This is useful when you're only concerned with the basic category of the sample
     * (e.g., whether it's a float, integer, or logic) and not with more specific details
     * about its representation.
     *
     * @return The main sample type, masked with ISample.DATA_TYPE_MASK_MAIN
     * @see #getSampleType() for the full, unmasked sample type
     */
    default int getSampleMainType() {
        return getSampleType() & ISample.DATA_TYPE_MASK_MAIN;
    };

    /**
     * Returns a human-readable label for the sample type.
     * 
     * This convenience method converts the numeric sample type to a user-friendly string
     * representation. The labels are defined in ISample and typically represent the simple
     * name of the data type, such as "Integer", "Float", "Logic", etc.
     * 
     * This is particularly useful for display purposes, debugging, or when generating
     * human-readable reports about signal characteristics.
     *
     * @return A string label representing the sample type, or null if the type is unknown
     * @see ISample#getDataTypeLabel(int) for the underlying label lookup implementation
     */
    default String getSampleTypeLabel() {
        return ISample.getDataTypeLabel(getSampleType());
    }

    /**
     * Returns the scale factor for the sample values.
     * 
     * The scale defines the dimension of the signal or sample, with its interpretation depending 
     * on the sample type:
     * 
     * - For logic signals: Represents the bit width (e.g., 8 for an 8-bit logic signal)
     * - For array signals: Represents the array dimensions (e.g., 2 for a 2-dimensional array)
     * - For numeric signals: Can indicate precision, exponent, or unit scaling
     * 
     * Examples:
     * - Scale of 16 for a logic signal indicates a 16-bit vector
     * - Scale of 2 for an array represents a 2-dimensional array
     * 
     * A value of ISample.SCALE_DEFAULT (-1) indicates that the default scaling for the
     * sample type should be used.
     *
     * @return The scale factor for the sample values, or ISample.SCALE_DEFAULT for default scaling
     */
    int getScale();

    /**
     * Returns the format specifier for the sample values.
     * 
     * The format specifier defines how sample values should be converted to string representations
     * for display or export. It provides guidance on the preferred textual representation
     * of the sample data.
     * 
     * Common format specifiers defined in ISample include:
     * - ISample.FORMAT_DEFAULT (null): Use default value based on signal type
     * - ISample.FORMAT_NONE ("none"): No textual representation
     * - ISample.FORMAT_BINARY ("bin"): Binary representation (e.g., "1010")
     * - ISample.FORMAT_OCTAL ("oct"): Octal representation (e.g., "52")
     * - ISample.FORMAT_HEXADECIMAL ("hex"): Hexadecimal representation (e.g., "2A")
     * - ISample.FORMAT_ASCII ("ascii"): ASCII character representation
     * - ISample.FORMAT_DECIMAL ("dec"): Decimal representation (e.g., "42")
     * - ISample.FORMAT_LABEL ("label"): Textual enumeration label
     * - ISample.FORMAT_BYTES ("bytes"): Byte-by-byte hexadecimal representation
     * - ISample.FORMAT_BOOLEAN ("bool"): Boolean representation ("true"/"false")
     * 
     * For arrays and structs, special format specifiers control how multiple values are displayed:
     * - ISample.FORMAT_COLLECTION_VALUES_ONLY ("values"): Show values only
     * - ISample.FORMAT_COLLECTION_KEY_VALUES ("keyvalues"): Show key-value pairs
     * 
     * A null value indicates that the default format for the sample type should be used.
     *
     * @return The format specifier string, or null for default formatting
     * @see ISample for all available format specifier constants
     */
    String getFormat();
}