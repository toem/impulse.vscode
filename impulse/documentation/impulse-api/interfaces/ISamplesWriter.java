package de.toem.impulse.samples;

import de.toem.impulse.cells.record.IRecord;
import de.toem.impulse.samples.domain.IDomainBase;
import de.toem.impulse.samples.raw.Enumeration;
import de.toem.toolkits.pattern.pageable.Pageable;

/**
 * Interface for type-independent sample writing in the impulse framework.
 *
 * This interface provides the foundation for writing signal data across different data types and structures.
 * It defines the core operations for creating, opening, and manipulating signal data streams, enabling
 * efficient and consistent data generation for various signal types.
 *
 * Key features of this interface include:
 * - Signal creation and initialization with specified domain bases and characteristics
 * - Stream management through open/close operations and state tracking
 * - Group and transaction handling for complex hierarchical data
 * - Attachment support for adding metadata and relationships to samples
 * - Efficient writing operations with position control and tagging capabilities
 *
 * The `ISamplesWriter` interface serves as the common base for all specialized writers in the impulse framework,
 * establishing patterns and conventions that are shared across different signal data types such as logic,
 * numeric, text, event, and structured data.
 *
 * Implementations of this interface are designed to accommodate high-performance data generation scenarios,
 * allowing for both real-time streaming and batch processing applications. The interface supports various
 * writing modes, including continuous and discrete signals, with appropriate position handling for each.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 53
 */
public interface ISamplesWriter extends IPackedSamples {

    public final static long NEXT_POSITION = Long.MIN_VALUE; // for continuous
                                                          // writer: use the
                                                          // next domain value

    // ========================================================================================================================
    // Groups
    // ========================================================================================================================

    /**
     * Gets the number of groups in this signal.
     * 
     * Samples in impulse can be organized into logical groups (e.g., transactions, packets, events with multiple stages).
     * A group can consist of a starting sample, one or more intermediate samples, and an ending sample. All samples 
     * in a group share the same group index and order descriptor (first, intermediate, last).
     * 
     * @return The total number of groups in this signal
     * @see ISample#GO_INITIAL
     * @see ISample#GO_INTER
     * @see ISample#GO_FINAL
     * @see ISample#GO_SINGLE
     */
    int getGroups();

    // ========================================================================================================================
    // Open/Close
    // ========================================================================================================================
    
    /**
     * Returns true if writer has been opened.
     */
    boolean isOpen();

    /**
     * Open the writer, starting at position 'start'.
     * 
     * @param start
     *            Domain start position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms).
     * 
     */
    default boolean open(long start) {
        return open(start, start, 0,0, null);
    }

    /**
     * Open the writer, starting at position 'start'.
     * 
     * @param start
     *            Domain start position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms).
     * @param rate
     *            Domain rate distance as a multiple of its domain base.
     * 
     */
    default boolean open(long start, long rate) {
        return open(start, start,rate,0, null);
    }
    
    /**
     * Open the writer, starting at position 'start'.
     * 
     * @param start
     *            Domain start position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms).
     * @param max
     *            Domain max position for this open session as a multiple of its domain base.
     * @param rate
     *            Domain rate distance as a multiple of its domain base.
     * 
     */
    default boolean open(long start, long max, long rate) {
        return open(start, max,rate, 0, null);
    }
    

    /**
     * Open the writer, starting at position 'start' and rate 'rate' (continuous process) and mode (mode/limitation) using 'samples' to store the
     * data. Internal use only!
     * 
     * @param start
     *            Domain start position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms).
     * @param rate
     *            Domain rate distance as a multiple of its domain base.
     * @param blocksPerFragment
     *            Samples per fragment as multiple of 256. Default value 0 will interpreted as 16 (16*256==4096 samples per fragment).a maximum of
     *            65536 samples per fragment is allowed (0<=samples256PerFragment<=256).
     * @param samples
     *            Samples container.
     * 
     */
    default boolean open(long start, long rate, int blocksPerFragment, Pageable<byte[]> samples) {
        return open(start, start,rate, blocksPerFragment, samples);
    }

    /**
     * Open the writer, starting at position 'start' and rate 'rate' (continuous process) and mode (mode/limitation) using 'samples' to store the
     * data. Internal use only!
     * 
     * @param start
     *            Domain start position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms).
     * @param max
     *            Domain max position for this open session as a multiple of its domain base.
     * @param rate
     *            Domain rate distance as a multiple of its domain base.
     * @param blocksPerFragment
     *            Samples per fragment as multiple of 256. Default value 0 will interpreted as 16 (16*256==4096 samples per fragment).a maximum of
     *            65536 samples per fragment is allowed (0<=samples256PerFragment<=256).
     * @param samples
     *            Samples container.
     * 
     */
    boolean open(long start, long max, long rate, int blocksPerFragment, Pageable<byte[]> samples);

    /**
     * Returns the domain max position for this open session as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value =
     * 100ms).
     */
    long getMaxAsMultiple();

    /**
     * Close the writer latest write position.
     * 
     * @param multiple
     *            Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls
     *            need to pass a value greater or equal.
     */
    void close();

    /**
     * Close the writer at position 'end'.
     * 
     * @param end
     *            Domain end position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls
     *            need to pass a value greater or equal.
     */
    void close(long end);

    // ========================================================================================================================
    // Flush
    // ========================================================================================================================
    
    /**
     * Flush current writer data into its packed samples. Internal use only!
     * 
     * @param multiple
     *            Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls
     *            need to pass a value greater or equal.
     */
    void flush(long multiple);

    /**
     * Flush current writer data into its packed samples. Internal use only!
     */
    void flush();

    /**
     * Flush current writer data into its packed samples and update the given reader. Internal use only!
     */
    ISamplesReader flushAndUpdateReader(ISamplesReader reader); 
    
    /**
     * Flush current writer data into its packed samples and update the given signal. Internal use only!
     */
    int flushAndUpdateSignal(IRecord.Signal signal);
    
    // ========================================================================================================================
    // Members
    // ========================================================================================================================
    
    /**
     * Adds a named element to an array or struct in the writer's legend.
     * 
     * Members are essential for structured data types in impulse, providing metadata and formatting information.
     * Each member has a name, tags for categorization, and a format specifier for display.
     * 
     * This method is typically required for arrays, as struct writers automatically create their legend content.
     * 
     * @param label
     *            Name of the member. This identifies the member in the legend and will be displayed in the UI.
     * @param tags
     *            Content description or metadata tags for the member (e.g., "chart", "log", "state"), or null.
     *            Tags provide additional context about the member's purpose or how it should be visualized.
     * @param format
     *            Format specifier defining how to render the value (e.g., ISample.FORMAT_DECIMAL, FORMAT_HEXADECIMAL).
     *            This controls the text representation of the member's values in plots and tables.
     * @return The descriptor for the newly created member, containing its index and metadata.
     */
    IMemberDescriptor addMember(String label, String tags, String format);
    
    /**
     * Adds a named element to an array/struct in the writer's legend with a specified ID.
     * 
     * It is usually only required for arrays as struct writers automatically create their legend content.
     * 
     * @param nid
     *            Index/ID of the member.
     * @param label
     *            Name of the member. This identifies the member in the legend and will be displayed in the UI.
     * @param tags
     *            Content description or metadata tags for the member, or null.
     *            Tags provide additional context about the member's purpose or how it should be visualized.
     * @param format
     *            Format specifier defining how to render the value (e.g., ISample.FORMAT_DECIMAL).
     *            This controls the text representation of the member's values in plots and tables.
     * @return The descriptor for the created member, containing its index and metadata.
     */
    default IMemberDescriptor setMember(int nid, String label, String tags, String format) {
        return setMember(nid, -1, label, null, null, tags, ISample.DATA_TYPE_UNKNOWN, -1, format);
    }

    /**
     * Adds a detailed member element to an array/struct in the writer's legend with extended attributes.
     * 
     * This method provides more control over the member's metadata, including description, icon,
     * data type, and scale. This is particularly useful for creating rich, self-documenting
     * signal structures that can be better visualized and analyzed.
     * 
     * @param nid
     *            Index/ID of the member.
     * @param label
     *            Name of the member. This identifies the member in the legend and will be displayed in the UI.
     * @param description
     *            Descriptive text that provides additional information about this member's purpose or meaning.
     * @param iconId
     *            ID reference to an icon that will be used to visually represent this member in the UI.
     * @param tags
     *            Content description or metadata tags for the member, or null.
     *            Tags provide additional context about the member's purpose or how it should be visualized.
     * @param type
     *            Data type (struct only) of the member. Use ISample constants like DATA_TYPE_INTEGER, DATA_TYPE_FLOAT, etc.
     * @param scale
     *            Dimension information for the member, such as bit width for integers or array dimensions.
     * @param format
     *            Format specifier defining how to render the value (e.g., ISample.FORMAT_DECIMAL).
     *            This controls the text representation of the member's values in plots and tables.
     * @return The descriptor for the created member, containing its index and metadata.
     */
    default IMemberDescriptor setMember(int nid, String label, String description, String iconId, String tags, int type, int scale, String format) {
        return setMember(nid, -1, label, description, iconId, tags, type, scale, format);
    }
    
    /**
     * Adds a hierarchical member element to an array/struct in the writer's legend with complete attributes.
     * 
     * This is the most comprehensive setMember method, supporting all member attributes including
     * parent-child relationships. Use this method to create complex hierarchical structures where
     * members can have sub-members, enabling tree-like organization of signal data.
     * 
     * @param nid
     *            Index/ID of the member.
     * @param parentId
     *            Index/ID of the parent member, or -1 if this is a top-level member. This creates a
     *            hierarchical structure of members.
     * @param label
     *            Name of the member. This identifies the member in the legend and will be displayed in the UI.
     * @param description
     *            Descriptive text that provides additional information about this member's purpose or meaning.
     * @param iconId
     *            ID reference to an icon that will be used to visually represent this member in the UI.
     * @param tags
     *            Content description or metadata tags for the member, or null.
     *            Tags provide additional context about the member's purpose or how it should be visualized.
     * @param type
     *            Data type (struct only) of the member. Use ISample constants like DATA_TYPE_INTEGER, DATA_TYPE_FLOAT, etc.
     * @param scale
     *            Dimension information for the member, such as bit width for integers or array dimensions.
     * @param format
     *            Format specifier defining how to render the value (e.g., ISample.FORMAT_DECIMAL).
     *            This controls the text representation of the member's values in plots and tables.
     * @return The descriptor for the created member, containing its index and metadata.
     */
    IMemberDescriptor setMember(int nid, int parentId, String label, String description, String iconId, String tags, int type, int scale, String format);

    /**
     * Adds an enumeration value to the writer's legend.
     * 
     * Enumerations provide human-readable names for integer values, allowing for clearer
     * representation of states, categories, or modes in signals. Event writers use enumerations
     * extensively to represent different event types or states.
     * 
     * @param enumerationGroup
     *            Enumeration group identifier, use 0 for global enumeration. Groups allow
     *            organizing related enumerations together.
     * @param value
     *            Integer value of the enumeration. This is the raw value that will be written in samples.
     * @param label
     *            String representation of the enumeration. This text will be displayed in the UI
     *            when this enumeration value is encountered in the signal.
     * @return The enumeration object created, which can be used for reference in writing operations.
     */
    Enumeration setEnum(int enumerationGroup, int value, String label);

    /**
     * Adds an enumeration value to a specific member in the writer's legend.
     * 
     * This method associates enumeration values with a specific member, which is useful for
     * creating member-specific value mappings. For example, a "state" member might have
     * enumeration values like "IDLE", "ACTIVE", "ERROR".
     * 
     * @param memberIdOrIdentifier
     *            The ID or descriptor of the member to associate this enumeration with.
     *            This can be an Integer ID or an IMemberDescriptor object.
     * @param value
     *            Integer value of the enumeration. This is the raw value that will be written in samples.
     * @param label
     *            String representation of the enumeration. This text will be displayed in the UI
     *            when this enumeration value is encountered in the signal.
     * @return The enumeration object created, which can be used for reference in writing operations.
     */
    Enumeration setMemberEnum(Object memberIdOrIdentifier, int value, String label);

    /**
     * Adds all enumerations and members from another legend to this writer's legend.
     * 
     * This method provides a convenient way to copy the entire structure and metadata
     * from one legend to another. This is useful for:
     * - Creating multiple signals with identical structure
     * - Cloning an existing signal's structure for a new recording
     * - Sharing common structures across different signal types
     * 
     * @param legend
     *            The source legend containing members and enumerations to be added to this writer.
     *            All elements from this legend will be copied to the current writer's legend.
     */
    void addAll(ISamplesLegend legend);

    // ========================================================================================================================
    // Attachments
    // ========================================================================================================================
    
    /**
     * Style pattern format for attachents depends on the attachment type:
     * 
     * For {@link IAttachedRelation}: {#}message{/color{/lineStyle{/arrowStyle{/symbol}{/sourceLabel}{/targetLabel}}}}
     * For {@link IAttachedLabel}: {#}message{/color{/symbol}}
     * 
     * Style elements explanation:
     * 
     * # (optional prefix): When present, suppresses the display of the message
     * color: An integer index (0..max) referencing a color in the tag palette
     * lineStyle (for relations): Available values are straight (default), dashdot, dot, dash
     * arrowStyle (for relations): Available values are normal (default), dot
     * symbol: Visual symbol identifier used in the attachment representation
     * sourceLabel, targetLabel (for relations): Additional labels at source and target points
     */

    /**
     * Adds a relation attachment to the previous sample with full control over all parameters.
     *
     * This is the core implementation used by all other attachRelation overloads. It provides full control over all aspects of the relation,
     * including type, target, style, position, domain base, content type, and target index.
     * 
     * @param type
     *            The type of relation to create (see constants in {@link ISample})
     * @param target
     *            Path to associated signal (e.g. "/signal/reset").
     * @param style
     *            Style definition in the form '{#}message{/color{/lineStyle{/arrowStyle{/symbol}{/sourceLabel}{/targetLabel}}}}'.
     * @param targetPosition
     *            Absolute or relative position as a multiple of domain base, depending on the relation type
     * @param targetBase
     *            The domain base to use for the target position calculation, or null to use this signal's domain base.
     * @param targetContent
     *            The content type to target (from ISample constants like CONTENT_SAMPLE, CONTENT_GROUP).
     * @param targetIdx
     *            The specific index within the target content to link to.
     * @return true if the relation was successfully attached, false otherwise
     */
    boolean attachRelation(int type, String target, String style, long targetPosition, IDomainBase targetBase, int targetContent, int targetIdx);

    default boolean attachRelation(String target, String style, long deltaTargetPosition) {
        return attachRelation( ISample.AT_RELATION_DELTA_POS, target, style, deltaTargetPosition, null, ISample.CONTENT_SAMPLE, 0);
    }

    default boolean attachRelation(int type, String target, String style, long targetPosition) {
        return attachRelation(type, target, style, targetPosition, null, ISample.CONTENT_SAMPLE, 0);
    }

    default boolean attachRelation(int type, String target, String style, long targetPosition, IDomainBase targetBase) {
        return attachRelation(type, target, style, targetPosition, targetBase, ISample.CONTENT_SAMPLE, 0);
    }

    /**
     * Inserts a relation attachment of specified type with custom domain base at a specific sample index.
     *
     * This is the core implementation used by all other attachRelation overloads. It provides full control over all aspects of the relation,
     * including type, target, style, position, domain base, content type, and target index.
     *
     * @param type
     *            The type of relation to create (see constants in {@link ISample})
     * @param target
     *            Path to associated signal (e.g. "/signal/reset").
     * @param style
     *            Style definition in the form '{#}message{/color{/lineStyle{/arrowStyle{/symbol}{/sourceLabel}{/targetLabel}}}}'.
     * @param targetPosition
     *            Absolute or relative position as a multiple of domain base, depending on the relation type
     * @param targetBase
     *            The domain base to use for the target position calculation, or null to use this signal's domain base.
     * @param targetContent
     *            The content type to target (from ISample constants like CONTENT_SAMPLE, CONTENT_GROUP).
     * @param targetIdx
     *            The specific index within the target content to link to.
     * @return true if the relation was successfully attached, false otherwise
     */
    boolean insertRelationAt(int idx, int type, String target, String style, long targetPosition, IDomainBase targetBase, int targetContent,
            int targetIdx);

    default boolean insertRelationAt(int idx, String target, String style, long deltaTargetPosition) {
        return insertRelationAt( idx,AT_RELATION_DELTA_POS, target, style, deltaTargetPosition, null, ISample.CONTENT_SAMPLE, 0);
    }

    default boolean insertRelationAt(int idx, int type, String target, String style, long targetPosition) {
        return insertRelationAt(idx, type, target, style, targetPosition, null, ISample.CONTENT_SAMPLE, 0);
    }

    default boolean insertRelationAt(int idx, int type, String target, String style, long targetPosition, IDomainBase targetBase) {
        return insertRelationAt( idx,type, target, style, targetPosition, targetBase, ISample.CONTENT_SAMPLE, 0);
    }
    /**
     * Adds a relation attachment to the previous sample with full control over all parameters.
     * 
     * This is the core implementation of insertRelationAt used by all other overloads. It provides
     * full control over all aspects of the relation, including target content type and target index.
     * 
     * @param type
     *            The type of relation to create (from ISample constants like AT_RELATION_DELTA_POS).
     * @param targetId
     *            ID reference to the target signal in the writer's legend.
     * @param styleId
     *            ID reference to the style definition in the writer's legend.
     * @param targetPosition
     *            Absolute or relative position as a multiple of target domain base, depending on the relation type.
     * @param targetBaseId
     *            ID reference to the domain base to use for the target position calculation.
     * @param targetContent
     *            The content type to target (from ISample constants like CONTENT_SAMPLE, CONTENT_GROUP).
     * @param targetIdx
     *            The specific index within the target content to link to.
     * @return true if the relation was successfully inserted, false otherwise
     */

    boolean attachRelation(int type, int targetId, int styleId, long targetPosition, int targetBaseId, int targetContent, int targetIdx);

    default boolean attachRelation(int targetId, int styleId, long deltaTargetPosition) {
        return attachRelation(AT_RELATION_DELTA_POS, targetId, styleId, deltaTargetPosition, 0, ISample.CONTENT_NONE, 0);
    }

    default boolean attachRelation(int type, int targetId, int styleId, long targetPosition) {
        return attachRelation(type, targetId, styleId, targetPosition, 0, ISample.CONTENT_NONE, 0);
    }

    default boolean attachRelation(int type, int targetId, int styleId, long targetPosition, int targetBaseId) {
        return attachRelation(type, targetId, styleId, targetPosition, targetBaseId, ISample.CONTENT_NONE, 0);
    }

    /**
     * Inserts a relation attachment with complete control over all parameters.
     * 
     * This is the core implementation of insertRelationAt used by all other overloads. It provides
     * full control over all aspects of the relation, including target content type and target index.
     * 
     * @param idx
     *            The index of the sample to which the relation should be attached.
     * @param type
     *            The type of relation to create (from ISample constants like AT_RELATION_DELTA_POS).
     * @param targetId
     *            ID reference to the target signal in the writer's legend.
     * @param styleId
     *            ID reference to the style definition in the writer's legend.
     * @param targetPosition
     *            Absolute or relative position as a multiple of target domain base, depending on the relation type.
     * @param targetBaseId
     *            ID reference to the domain base to use for the target position calculation.
     * @param targetContent
     *            The content type to target (from ISample constants like CONTENT_SAMPLE, CONTENT_GROUP).
     * @param targetIdx
     *            The specific index within the target content to link to.
     * @return true if the relation was successfully inserted, false otherwise
     */
    boolean insertRelationAt(int idx, int type, int targetId, int styleId, long targetPosition, int targetBaseId, int targetContent, int targetIdx);

    default boolean insertRelationAt(int idx, int targetId, int styleId, long deltaTargetPosition) {
        return insertRelationAt(idx, AT_RELATION_DELTA_POS, targetId, styleId, deltaTargetPosition, 0, ISample.CONTENT_NONE, 0);
    }

    default boolean insertRelationAt(int idx, int type, int targetId, int styleId, long targetPosition) {
        return insertRelationAt(idx, type, targetId, styleId, targetPosition, 0, 0, 0);
    }

    default boolean insertRelationAt(int idx, int type, int targetId, int styleId, long targetPosition, int targetBaseId) {
        return insertRelationAt(idx, type, targetId, styleId, targetPosition, targetBaseId, ISample.CONTENT_NONE, 0);
    }
    /**
     * Adds a label attachment to the previous sample. The style string will be added to the legend.
     * 
     * Labels add textual or symbolic annotations to a sample. These annotations can:
     * - Provide descriptive information about the sample
     * - Highlight specific events or states within the signal
     * - Serve as markers for debugging or analysis purposes
     * 
     * @param style
     *            Style definition in the form '{#}message{/color{/symbol}}'.
     * @return true if the label was successfully attached, false otherwise
     */
    boolean attachLabel(String style);

    /**
     * Adds a label attachment to the previous sample using a predefined style ID.
     * 
     * Labels are useful for adding contextual information to samples, such as:
     * - Adding explanatory notes to specific data points
     * - Creating visual annotations for analysis or presentation
     * - Marking important events or transitions in the signal
     * 
     * @param styleId
     *            An integer referencing a previously defined style in the form '{#}message{/color{/symbol}}'.
     * @return true if the label was successfully attached, false otherwise
     */
    boolean attachLabel(int styleId);

    // ========================================================================================================================
    // Primitive writes
    // ========================================================================================================================
    
    /**
     * Writes a none sample (a sample with no value) at the specified domain position.
     * 
     * None samples are useful for:
     * - Marking points in time without associating a specific value
     * - Indicating an event has occurred but without detailed information
     * - Creating temporal breakpoints in a signal
     * 
     * @param position
     *            Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms).
     * @param tag
     *            If set to true or an intetger > 0, impulse will use tag color (usually red) to paint the sample. Meaning of "tag" is use-case dependent.
     * @return true if the sample was successfully written, false otherwise
     */
    boolean write(long position, boolean tag);

    boolean write(long position, int tag);

    /**
     * Writes a none sample (a sample with no value) at the specified domain position.
     * 
     * This method is provided specifically for scripting environments and has the same
     * behavior as write(multiple, tag).
     * 
     * @param position
     *            Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms).
     * @param tag
     *            If set to true or an intetger > 0, impulse will use tag color (usually red) to paint the sample. Meaning of "tag" is use-case dependent.
     * @return Returns true if the sample was successfully written, false otherwise
     */
    boolean writeNone(long position, boolean tag);

    boolean writeNone(long position, int tag);

    /**
     * Writes a sample using a provided value object at the specified domain position.
     * 
     * This method supports writing any value type compatible with the signal's data type,
     * and allows for tagging the sample to influence its visualization or processing.
     * 
     * @param position
     *            Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms).
     * @param tag
     *            If set to true or an intetger > 0, impulse will use tag color (usually red) to paint the sample. Meaning of "tag" is use-case dependent.
     * @param value
     *            The value to write, which must be compatible with the signal's data type.
     * @return true if the sample was successfully written, false otherwise
     */
    boolean writeSample(long position, boolean tag, Object value);

    boolean writeSample(long position, int tag, Object value);

    /**
     * Writes a sample with specified grouping order and layer information.
     * 
     * This advanced method allows writing samples that belong to a specific group with order
     * (first, intermediate, last) and layer information. This is useful for representing
     * complex data like transactions, hierarchical events, or multi-layer structures.
     * 
     * For example, in a transaction representation:
     * - order=GO_INITIAL: Marks the start of a transaction 
     * - order=GO_INTER: Indicates an intermediate stage of a transaction
     * - order=GO_FINAL: Marks the end of a transaction
     * - order=GO_SINGLE: Represents a single-stage transaction
     * 
     * The layer parameter allows organizing data in vertical layers within a signal.
     * 
     * @param position
     *            Domain position as a multiple of its domain base.
     * @param tag
     *            If set to true or an intetger > 0, impulse will use tag color (usually red) to paint the sample. Meaning of "tag" is use-case dependent.
     * @param order
     *            Group order descriptor (e.g., ISample.GO_INITIAL, GO_INTER, GO_FINAL, GO_SINGLE).
     * @param layer
     *            Layer number for multi-layer data organization.
     * @param value
     *            The value to write, which must be compatible with the signal's data type.
     * @return true if the sample was successfully written, false otherwise
     */
    boolean writeSample(long position, boolean tag, int order, int layer, Object value);


    boolean writeSample(long position, int tag,  int order, int layer, Object value);



    // ========================================================================================================================
    // Internal writes
    // ========================================================================================================================

    /**
     * Sample write primitive method. Only for internal usage!
     * 
     * @param position
     * @param format0
     * 
     */
    boolean writeSample(long position, byte format0);

    /**
     * Sample write primitive method. Only for internal usage!
     * 
     * @param position
     * @param format0
     * @param data0
     * 
     */
    boolean writeSample(long position, byte format0, byte data0);

    /**
     * Sample write primitive method. Only for internal usage!
     * 
     * @param position
     * @param format0
     * @param data
     * @param start
     * @param dlength
     * 
     */
    boolean writeSample(long position, byte format0, byte[] data, int start, int dlength);

    /**
     * Sample write primitive method. Only for internal usage!
     * 
     * @param position
     * @param format0
     * @param order TODO
     * @param layer
     * @param data
     * @param start
     * @param dlength
     * @param group
     * 
     */
    boolean writeSample(long position, byte format0,int order, /* int group,*/ int layer, byte[] data, int start, int dlength);

    /**
     * Sample write primitive method. Only for internal usage!
     * 
     * @param packed
     * 
     */
    boolean writeSample(ISamplePack packed);

    boolean writeSample(ISampleValue value);



}
