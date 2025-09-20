package de.toem.impulse.samples;

import de.toem.impulse.samples.domain.DomainLongValue;

/**
 * Interface representing a group of samples in the impulse framework.
 * 
 * This interface combines the capabilities of {@link IReadableValue} and {@link IGroupValue}, providing
 * a comprehensive representation of a logical group of samples. Groups represent higher-level entities
 * like protocol transactions, waveform bursts, or any other collection of related samples that form
 * a meaningful unit within a signal.
 * 
 * A group typically consists of multiple samples arranged in sequence, with a defined start and end
 * position. The group may span across a range in the domain (time, frequency, etc.) and contain values
 * that collectively represent a more complex data structure than individual samples alone.
 * 
 * Key features of this interface include:
 * - Access to the group's start and end positions
 * - Methods for retrieving and formatting the group's aggregated value
 * - Type conversion methods for interpreting the group data in various formats
 * - Access to group-specific metadata such as layer information
 * 
 * The IReadableGroup interface is particularly useful for:
 * - Protocol analysis where multiple samples form a transaction or packet
 * - Processing multi-sample events as cohesive units
 * - Working with hierarchical data where groups represent logical containers
 * - Analyzing temporal or spatial patterns that span multiple data points
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 27
 * 
 * @see IReadableValue for methods related to value access and formatting
 * @see IGroupValue for group-specific metadata and properties
 * @see IReadableSamples#compoundAtGroup(int) for obtaining an IReadableGroup from a signal
 */
public interface IReadableGroup extends IReadableValue, IGroupValue {


    // ========================================================================================================================
    // Position
    // ========================================================================================================================


    /**
     * Returns the position of the last sample in the group.
     * 
     * While {@link IReadableValue#getPosition()} returns the starting position of the group,
     * this method provides the ending position. Together, these two methods define the complete
     * span or range occupied by the group in the domain (time, frequency, etc.).
     * 
     * This information is particularly useful for:
     * - Calculating the duration or size of the group
     * - Determining overlaps between different groups
     * - Planning visualization or analysis windows that need to encompass the entire group
     * 
     * For single-sample groups, the start and last positions will typically be identical.
     * For multi-sample groups, the last position corresponds to the domain position of the
     * sample with order specifier GO_FINAL.
     * 
     * @return the domain position of the last sample in the group as a DomainLongValue
     * @see IReadableValue#getPosition() for retrieving the starting position of the group
     */
    DomainLongValue getLastPosition();




}
