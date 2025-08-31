package de.toem.impulse.samples;

import java.util.Iterator;

/**
 * An iterator interface for traversing through individual samples within the impulse framework.
 * 
 * Samples represent the fundamental data points in signal processing, representing discrete measurements
 * or events in a time series or other domain-based data collection. This interface provides
 * specialized capabilities for navigating through these samples with awareness of their position,
 * ordering, and contextual information.
 * 
 * By extending both the standard Java Iterator interface and the ISampleValue interface,
 * ISampleIterator combines the familiar iteration pattern with impulse-specific functionality
 * for accessing sample values and metadata. This dual inheritance allows clients to use
 * standard iteration methods like hasNext() and next() while also accessing domain-specific
 * properties of each sample.
 * 
 * Use cases for ISampleIterator include:
 * - Sequential processing of individual signal data points
 * - Bounded traversal of samples within a specific domain range
 * - Position-aware navigation through signal data
 * - Analysis of sample properties like position, group membership, and layering
 * 
 * When iterating through samples, each call to next() advances to the next individual data point,
 * providing access to both its value and contextual information through the methods defined
 * in this interface.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 43
 */
public interface ISampleIterator extends Iterator<Integer>, ISampleValue {

    /**
     * Checks if there is another sample available in the iteration before the specified position.
     * 
     * This method enables bounded traversal of samples within a dataset, which is particularly useful
     * when processing a specific range or window of samples. By checking if samples exist before a
     * certain position, you can implement efficient early-stopping mechanisms or position-aware
     * processing algorithms.
     * 
     * The position parameter represents a location in the domain space (such as time or distance),
     * expressed in the native units of the measurement. This allows for domain-aware iteration
     * that respects the physical or conceptual space being measured.
     *
     * @param position The position limit in domain units to check against
     * @return true if there is a next sample before the specified position, false otherwise
     */
    boolean hasNextBefore(long position);

    /**
     * Returns the index of the next sample in the iteration without advancing the iterator.
     * 
     * This method provides a peek ahead capability, allowing inspection of the next sample's
     * index without changing the iterator's state. The index represents the sequential position
     * of the sample within the entire dataset, typically ranging from 0 to the total count minus one.
     * 
     * This is valuable when implementing custom navigation logic, conditional processing based
     * on sample indices, or when you need to determine the relationship between the current
     * iterator position and the overall sample collection.
     *
     * @return The index of the next sample in the iteration
     */
    int getNextIndex();

    /**
     * Returns the position of the next sample in the iteration, specified in domain units.
     * 
     * The position represents the location of the sample in the domain dimension (such as time,
     * frequency, or distance) and is expressed in the native units of the measurement. This
     * method allows examination of where the next sample exists in the physical or conceptual
     * space being measured, without advancing the iterator.
     * 
     * This information is essential for correlating samples with external events or markers,
     * implementing custom domain-aware traversal algorithms, or for analyzing the spatial or
     * temporal distribution of samples within a signal.
     *
     * @return The position of the next sample in domain units
     */
    long getNextPosition();

    /**
     * Returns sample value in packed form with all surrounding information, like position, 
     * group, order, layer and index.
     * 
     * This method creates a compound object that encapsulates the complete context of a sample,
     * providing a comprehensive view that includes both the value and its metadata. The returned
     * IReadableSample contains not just the sample value but also contextual information that
     * defines how this sample relates to others in the dataset.
     * 
     * The flags parameter controls what information is included in the compound object:
     * - ISample.VALUE_DEFAULT (0x0): Default behavior
     * - ISample.VALUE_NO_ENUMS (0x1): Prevent enumeration resolution
     * - ISample.VALUE_WRITABLE (0x2): Return a writable representation
     * - ISample.VALUE_MASK (0x3): Mask for value flags
     * - ISample.AT_RELATION (1 << 2): Include relation attachments
     * - ISample.AT_LABEL (2 << 2): Include label attachments
     * 
     * This method is primarily intended for internal usage within the framework but can be useful 
     * for advanced applications that need direct access to the complete sample representation.
     * 
     * @param flags Flags that control what information to include in the compound object
     * @return An IReadableSample containing the compound information
     */
    IReadableSample compound(int flags);
    
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
     * @return An IReadableSample containing the compound information with default flags
     * @see #compound(int) for more options using specific flags
     */
    default IReadableSample compound() {
        return compound(COMPOUND_DEFAULT);
    };
}