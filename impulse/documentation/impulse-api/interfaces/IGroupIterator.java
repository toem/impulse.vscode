package de.toem.impulse.samples;

import java.util.Iterator;

/**
 * An iterator interface for traversing through groups of sample data within the impulse framework.
 * 
 * Groups represent logical collections of samples that belong together, such as transactions,
 * packets, or bursts of signal activity. This interface provides specialized iteration capabilities
 * for navigating through these grouped samples efficiently.
 * 
 * By extending both the standard Java Iterator interface and the IGroupValue interface,
 * IGroupIterator combines the familiar iteration pattern with impulse-specific functionality
 * for accessing group values and metadata. This dual inheritance allows clients to use
 * standard iteration methods like hasNext() and next() while also accessing group-specific
 * properties such as position, identifier, and associated units.
 * 
 * Use cases for IGroupIterator include:
 * - Processing collections of related samples as cohesive units
 * - Analyzing transaction boundaries within a signal
 * - Iterating through logical protocol frames or packets
 * - Performing group-level statistics or analysis
 * 
 * When iterating through groups, each iteration step moves to the next logical group
 * rather than the next individual sample, allowing for more intuitive processing of
 * higher-level signal structures.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 15
 */
public interface IGroupIterator extends Iterator<Integer>, IGroupValue {

    /**
     * Checks if there is another group available in the iteration before the specified position.
     * 
     * This method allows for efficient position-based iteration limiting, which is particularly
     * useful when processing time-series or domain-based data. By checking for groups before a
     * certain position, you can implement bounded traversal or early-stopping mechanisms without
     * having to iterate through all groups.
     *
     * @param position The position to check against, specified in multiple of the domain base
     * @return true if there is a next group before the specified position, false otherwise
     */
    boolean hasNextBefore(long position);

    /**
     * Returns the index of the next group in the iteration without advancing the iterator.
     * 
     * This method provides a peek ahead capability, allowing you to examine the index of the
     * next group without consuming it. The group index is a sequential number identifying
     * the group within the collection of all groups, typically ranging from 0 to getGroups()-1.
     * 
     * This is particularly useful when you need to make decisions about whether to process
     * the next group based on its index, without changing the iterator's state.
     *
     * @return The index of the next group in the iteration
     */
    int getNextIndex();

    /**
     * Returns the group identifier of the next group in the iteration without advancing the iterator.
     * 
     * The group identifier is a value that represents the type or category of the group and is
     * distinct from the group index. While the index specifies the sequential position of a group
     * in the collection, the group identifier categorizes the group semantically.
     * 
     * For example, in a protocol analyzer, different group identifiers might represent different
     * packet types or protocol layers. This method allows you to examine this categorization
     * before deciding whether to process the group.
     *
     * @return The group identifier of the next group
     */
    int getNextGroup();

    /**
     * Returns the position of the next group in the iteration, specified in in multiple of the domain base.
     * 
     * The position represents the location of the group in the domain dimension (such as time
     * or distance) and is expressed in the same units used throughout the impulse framework.
     * This method is essential for understanding where the next group is located in the signal
     * without advancing the iterator.
     * 
     * This information can be used for implementing custom iteration logic based on positions,
     * or for correlating groups with external events or markers that are also positioned in
     * the same domain.
     *
     * @return The position of the next group in multiple of the domain base
     */
    long getNextPosition();

    /**
     * Returns sample value in packed form and all surrounding information, like position, group, order, layer and index.
     * 
     * This method creates a compound object that encapsulates all relevant data about the next group,
     * providing a comprehensive view of both the value and its context. The returned IReadableGroup
     * object contains the aggregated value(s) of the group along with metadata such as position,
     * group identifier, ordering information, layer, and index.
     * 
     * The flags parameter controls what information is included in the compound object:
     * - ISample.VALUE_DEFAULT (0x0): Default behavior
     * - ISample.VALUE_NO_ENUMS (0x1): Prevent enumeration resolution
     * - ISample.VALUE_WRITABLE (0x2): Return a writable representation
     * - ISample.VALUE_MASK (0x3): Mask for value flags
     * - ISample.AT_RELATION (1 << 2): Include relation attachments
     * - ISample.AT_LABEL (2 << 2): Include label attachments
     * 
     * This method is primarily intended for internal usage within the framework, but can be useful 
     * for advanced applications that need direct access to the complete group representation.
     * 
     * @param flags Flags that control what information to include in the compound object
     * @return An IReadableGroup containing the compound information
     */
    IReadableGroup compound(int flags);
    
    /**
     * Returns sample value in packed form with default compound flags.
     * 
     * This is a convenience method that calls compound(int flags) with the default set of flags
     * (COMPOUND_DEFAULT). It simplifies the creation of compound objects when the standard
     * configuration is sufficient, reducing the need for flag specification.
     * 
     * The default compound includes the essential information needed for most processing tasks,
     * striking a balance between completeness and efficiency. For more specific needs or
     * optimized processing, consider using the parameterized version with custom flags.
     * 
     * @return An IReadableGroup containing the compound information with default flags
     * @see #compound(int) for more options using specific flags
     */
    default IReadableGroup compound() {
        return compound(COMPOUND_DEFAULT);
    };
}