package de.toem.impulse.samples;

import de.toem.impulse.samples.raw.StructMember;

/**
 * Interface for writing structured data samples in the impulse framework.
 *
 * This interface provides specialized methods for writing complex structured data to signals.
 * It extends the base ISamplesWriter interface with structure-specific functionality, enabling
 * efficient representation of compound data types with named fields and hierarchical relationships.
 *
 * Key features of this interface include:
 * - Support for composite data structures with multiple fields of varying types
 * - Methods for writing both grouped and non-grouped structured samples
 * - Automatic member descriptor generation for field navigation and access
 * - Hierarchical data representation with parent-child relationships
 * - Flexible formatting options for individual structure members
 * 
 * The `IStructSamplesWriter` interface is particularly useful for representing complex data
 * structures such as protocol messages, database records, configuration objects, and other
 * compound information units. It provides a rich API for creating structured data signals
 * while maintaining the positioning and tagging capabilities of the base writer interface.
 *
 * This interface can be used in various scenarios such as protocol analyzers, database
 * tracing, configuration monitoring, and system debugging where complex data structures
 * need to be captured and visualized in signal form.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 57
 */
public interface IStructSamplesWriter extends ISamplesWriter{

    /**
     * Write non-grouped struct sample (e.g a log with multiple entries).
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag Adds a tag to the sample if tag == true or tag > 0. If tag is an integer value, a tag level will be added (1 {highest}..15), otherwise its tag level is set to 1.
     * @param value Array of struct members containing the structured data to be inserted.
     * @return Returns true if the write operation succeeded, false otherwise.
     */
    boolean write(long position, boolean tag,  StructMember[] value);
    boolean write(long position, int tag,  StructMember[] value);
    
    /**
     * Write grouped struct sample (e.g a transaction).
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * see Extension Toolkit for examples.
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag Adds a tag to the sample if tag == true or tag > 0. If tag is an integer value, a tag level will be added (1 {highest}..15), otherwise its tag level is set to 1.
     * @param order Group order descriptor (e.g., ISample.GO_INITIAL, GO_INTER, GO_FINAL, GO_SINGLE) that defines the sample's role in a group sequence.
     * @param layer Specifies the vertical layer for rendering (0..255), with higher layers appearing above lower layers in visualization.
     * @param value Array of struct members containing the structured data to be inserted.
     * @return Returns true if the write operation succeeded, false otherwise.
     */
    boolean write(long position, boolean tag,  int order,int layer, StructMember[] value);
    boolean write(long position, int tag, int order,int layer, StructMember[] value);

    /**
     * Creates a struct member with a specified parent, allowing for hierarchical data structures.
     * @param parent The parent struct member. Use null for root-level members.
     * @param name Name of the member that will be displayed in the UI.
     * @param description Descriptive text that provides additional information about this member's purpose or meaning.
     * @param iconId ID reference to an icon that will be used to visually represent this member in the UI.
     * @param tags Comma-separated list of tags for filtering and categorization.
     * @param type Data type of the member, using ISample.DATA_TYPE_* constants.
     * @param scale Dimension information for the member, such as bit width for integers or array dimensions.
     * @param format Format specifier defining how to render the value (e.g., ISample.FORMAT_DECIMAL).
     * @return The newly created StructMember object.
     */
    StructMember createMember(StructMember parent, String name,String description,String iconId, String tags, int type, int scale, String format);
    
    /**
     * Creates an array of struct members with the specified size.
     * Primarily intended for use in scripting environments where dynamic array creation is needed.
     * @param size Number of members to allocate in the array.
     * @return A newly allocated array of StructMember objects with the specified size.
     */
    
    StructMember[] createMembers(int size);
    /**
     * Creates and initializes a member at a specific index in a pre-allocated member array.
     * This method is particularly useful in scripting contexts where arrays are built incrementally.
     * @param members The pre-allocated array of struct members.
     * @param idx Zero-based index in the array where the new member should be placed.
     * @param name Name of the member that will be displayed in the UI.
     * @param description Descriptive text that provides additional information about this member's purpose or meaning.
     * @param iconId ID reference to an icon that will be used to visually represent this member in the UI.
     * @param tags Comma-separated list of tags for filtering and categorization.
     * @param type Data type of the member, using ISample.DATA_TYPE_* constants.
     * @param scale Dimension information for the member, such as bit width for integers or array dimensions.
     * @param format Format specifier defining how to render the value (e.g., ISample.FORMAT_DECIMAL).
     * @return The newly created StructMember object, which is also stored in the array at the specified index.
     */
    StructMember createMember(StructMember[] members, int idx, String name,String description,String iconId, String tags, int type, int scale, String format);
    

    /**
     * Gets the next available layer number for rendering.
     * Useful when you need to place samples in a new layer without overlapping existing visualizations.
     * @return An integer representing a free layer number that can be used for sample positioning.
     */
    int getFreeLayer();

}
