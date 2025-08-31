package de.toem.impulse.samples;

import de.toem.impulse.samples.domain.IDomainBase;

/**
 * Core interface providing metadata access for both samples and groups in the impulse framework.
 *
 * This interface defines the common metadata properties and behaviors shared between individual samples
 * and sample groups. It serves as a foundation in the sample hierarchy, providing essential context
 * about a sample or group's position, organization, and classification within a signal.
 *
 * Key features of this interface include:
 * - Access to sample/group position information in the signal's domain
 * - Group and layer organization capabilities for representing complex relationships
 * - Tagging mechanism for highlighting or categorizing samples and groups
 * - Type identification methods to distinguish between samples and groups
 *
 * The `IAbstractMeta` interface is designed to provide a unified way to access metadata regardless of
 * whether you're working with an individual sample or a logical group of samples. This consistency
 * simplifies the development of algorithms that can operate on both types interchangeably when only
 * metadata access is required.
 *
 * This interface is extended by more specialized interfaces like `ISampleMeta` for individual samples
 * and `IGroupMeta` for groups of samples, which add type-specific capabilities while maintaining
 * compatibility with code that works with the common metadata properties.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 3
 */
public interface IAbstractMeta extends ISample {

    /**
     * Returns the parent samples collection containing this sample or group.
     * 
     * This method provides access to the parent collection, which contains all samples
     * in the signal, including this one. This allows for navigating from an individual
     * sample or group back to its containing signal.
     * 
     * This relationship is essential for operations that need context beyond the
     * current sample, such as examining adjacent samples or accessing signal-level properties.
     *
     * @return the parent samples collection
     */
    ISamples getSamples();
    
    
    /**
     * Determines if this object represents an individual sample.
     * 
     * This method allows for type checking without casting or using instanceof.
     * In conjunction with {@link #isGroup()}, it enables polymorphic handling of
     * samples and groups through a common interface.
     *
     * @return true if this object is an individual sample, false if it is a group
     * @see #isGroup() for the complementary type check
     */
    boolean isSample();
    
    
    /**
     * Determines if this object represents a group of samples.
     * 
     * This method allows for type checking without casting or using instanceof.
     * Groups represent logical collections of samples, such as transactions or events
     * that span multiple data points.
     *
     * @return true if this object is a group, false if it is an individual sample
     * @see #isSample() for the complementary type check
     */
    boolean isGroup();
    
    /**
     * Returns either the sample index or group index, depending on the object type.
     * 
     * This method provides a unified way to access the identifying index of this object,
     * regardless of whether it's a sample or a group. This simplifies code that needs
     * to handle both types through the same interface.
     * 
     * For samples, this returns the sample index within the signal.
     * For groups, this returns the group identifier.
     *
     * @return the index of a sample or the identifier of a group
     */
    int getIndexOrGroup();
    
    /**
     * Returns the domain base associated with this sample or group.
     * 
     * The domain base defines the fundamental unit and characteristics of the domain
     * in which this sample or group exists. For time-based signals, this typically
     * represents the time unit (e.g., nanoseconds or microseconds).
     * 
     * The domain base is essential for interpreting position values correctly and for
     * performing calculations that involve domain measurements.
     *
     * @return the domain base object containing unit information
     */
    IDomainBase getDomainBase();
    
    /**
     * Returns the group identifier of this sample or group.
     * 
     * In the impulse framework, samples can be organized into logical groups such as
     * transactions, packets, or other related collections of data points. Each group
     * has a unique identifier that links its member samples together.
     * 
     * The group identifier is a sequential number from 0 to getGroups()-1. A value of
     * ISample.GROUP_NONE (-1) indicates that this sample doesn't belong to any group.
     * 
     * For group objects, this returns the group's own identifier.
     * For sample objects, this returns the identifier of the group this sample belongs to.
     *
     * @return the group identifier (0..getGroups()-1), or ISample.GROUP_NONE (-1) if not grouped
     */
    int getGroup();

    /**
     * Returns the layer of the sample or group.
     * 
     * Layers provide an organizational dimension that allows multiple independent sample
     * sequences to exist in parallel without conflicts, even when they overlap in the
     * time/position domain.
     * 
     * In the impulse framework, layers and groups are closely related concepts:
     * - All samples in the same group belong to the same layer
     * - Groups are formed by sequences of samples in the same layer
     * 
     * Common uses for layers include:
     * - Protocol stack layers (physical, data link, network, etc.)
     * - Parallel transaction streams
     * - Hierarchical representations of data
     * 
     * A return value of 0 indicates the sample or group is not in any specific layer.
     *
     * @return layer identifier (1..getLayers()), or 0 if not layered
     */
    int getLayer();

    /**
     * Returns the position of this sample or group as a multiple of the domain base.
     * 
     * The position represents the location of this sample or the start of this group
     * in the signal's domain. It is expressed as a multiple of the domain base unit.
     * For example, if the domain base is 1ns, a position value of 1000 represents 1000ns.
     * 
     * This raw position value is particularly useful for efficient calculations and
     * comparisons. For human-readable positions with units, you may need to combine
     * this value with the domain base information.
     *
     * @return the position as a long value (to be interpreted using the domain base)
     */
    long getPositionAsMultiple();

    
    /**
     * Determines if this sample or group is tagged.
     * 
     * Tagged samples or groups are marked for various use-case specific purposes, such as
     * highlighting important events, marking anomalies, or indicating specific regions
     * of interest in the signal. The exact meaning of a tag depends on the application
     * context and may be determined by the tag level (see {@link #getTag()}).
     *
     * @return true if this sample or group has been tagged, false otherwise
     * @see #getTag() for retrieving the specific tag level
     */
    boolean isTagged();

    /**
     * Returns the tag level of this sample or group.
     * 
     * Tag levels range from 1 to 15, with 1 being the highest priority. A tag level of 0
     * indicates that the sample or group is not tagged. Tag levels can be used to categorize
     * or prioritize elements based on their significance or to implement different visual
     * representations in user interfaces.
     * 
     * Common uses for tag levels include:
     * - Marking severity levels of anomalies (1 for critical, 2 for warning, etc.)
     * - Categorizing different types of events or points of interest
     * - Implementing multi-level filtering or highlighting in visualizations
     *
     * @return tag level (1..15) if tagged, 0 if not tagged
     * @see #isTagged() for checking if an element is tagged
     */
    int getTag();
}
