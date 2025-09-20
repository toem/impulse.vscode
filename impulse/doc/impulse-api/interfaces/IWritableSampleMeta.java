package de.toem.impulse.samples;

/**
 * Interface providing write access to sample metadata in the impulse framework.
 * 
 * This interface extends {@link ISampleMeta} to add methods for modifying sample metadata properties.
 * While ISampleMeta provides read-only access to sample metadata, IWritableSampleMeta allows applications
 * to change these properties, enabling dynamic updates to samples in a signal.
 * 
 * Key features of this interface include:
 * - Methods to modify the "none" state of a sample
 * - Capabilities to change a sample's order within its group
 * - Functionality to update a sample's group and layer assignments
 * - Controls for modifying position and tag information
 * 
 * The IWritableSampleMeta interface is primarily used in scenarios where samples need to be
 * dynamically created or modified, such as when building signals programmatically, implementing
 * signal processing algorithms, or adapting samples during import/export operations.
 * 
 * This interface follows the pattern of separating read and write operations in the impulse
 * framework, allowing for clear distinction between read-only and mutable sample access.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 61
 */
public interface IWritableSampleMeta extends ISampleMeta{

    // ========================================================================================================================
    // Type / Index / Group
    // ========================================================================================================================

    /**
     * Sets the "none" state of this sample.
     * 
     * The "none" state indicates whether this sample has an actual value or represents 
     * a gap in the signal data. Setting a sample to "none" (true) indicates that no 
     * meaningful value is available at this position.
     * 
     * This is useful for representing:
     * - Signal gaps or dropouts
     * - Intentionally omitted data points
     * - Placeholders for sparse data representations
     * 
     * Note that a "none" sample still maintains its position/timestamp and other metadata,
     * but indicates that no actual value should be rendered or considered in calculations.
     *
     * @param none true to mark this sample as having no value, false to indicate it has a valid value
     */
    void setNone(boolean none);

    /**
     * Sets the order of this sample within its logical group.
     * 
     * The order specifier indicates the sample's position within a group:
     * 
     * - ISample.GO_INITIAL (0): First sample in a multi-sample group
     * - ISample.GO_INTER (1): Intermediate sample within a group
     * - ISample.GO_FINAL (2): Last sample in a multi-sample group
     * - ISample.GO_SINGLE (3): Single sample that constitutes its own group
     * - ISample.GO_NONE (-1): Not part of any group
     * 
     * This method is particularly useful when constructing or modifying grouped samples
     * such as transactions, protocol packets, or other multi-sample logical entities.
     *
     * @param order order specifier as defined in ISample.GO_* constants
     * @see ISample#GO_INITIAL
     * @see ISample#GO_INTER
     * @see ISample#GO_FINAL
     * @see ISample#GO_SINGLE
     * @see ISample#GO_NONE
     */
    void setOrder(int order);

    /**
     * Sets the group identifier for this sample.
     * 
     * In the impulse framework, samples can be organized into logical groups such as
     * transactions, packets, or other related collections of data points. Each group
     * has a unique identifier that links its member samples together.
     * 
     * Samples in the same group typically represent parts of a larger entity, such as:
     * - Different phases of a transaction (begin, data, end)
     * - Fields of a protocol packet
     * - Components of a complex event
     * 
     * The group identifier should be a sequential number from 0 to getGroups()-1, or
     * ISample.GROUP_NONE (-1) to indicate this sample doesn't belong to any group.
     *
     * @param group the group identifier (0..getGroups()-1), or ISample.GROUP_NONE (-1)
     */
    void setGroup(int group);

    /**
     * Sets the layer assignment for this sample.
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
     * In the impulse framework, samples in the same group should belong to the same layer.
     * A layer value of 0 indicates no specific layer assignment.
     *
     * @param layer layer identifier (1..getLayers()), or 0 for no specific layer
     */
    void setLayer(int layer);
    
    // ========================================================================================================================
    // Units / Position
    // ========================================================================================================================

    /**
     * Sets the position of this sample as a multiple of its domain base.
     * 
     * The position represents the location of this sample in the signal's domain. 
     * It is expressed as a multiple of the domain base unit. For example, if the domain
     * base is 1ns, a position value of 1000 represents 1000ns.
     * 
     * This method is essential for placing samples at specific positions when creating
     * or modifying signals. The position should typically follow these rules:
     * - For discrete signals: position(sample(idx)) <= position(sample(idx+1))
     * - For continuous signals: positions are determined by start, end, and rate
     *
     * @param multiple the position as a long value (interpreted using the domain base)
     */
    void setUnits(long multiple);

    // ========================================================================================================================
    // Tag
    // ========================================================================================================================
    
    /**
     * Sets the tagged state of this sample.
     * 
     * Tagged samples are marked for various use-case specific purposes, such as
     * highlighting important events, marking anomalies, or indicating specific regions
     * of interest in the signal.
     * 
     * Setting tag to true marks this sample with the default tag level (typically 1).
     * Setting tag to false removes any existing tag, making this an ordinary untagged sample.
     *
     * @param tag true to mark this sample as tagged, false to remove any tag
     * @see #setTag(int) for setting a specific tag level
     */
    void setTag(boolean tag);

    /**
     * Sets the tag level of this sample.
     * 
     * Tag levels range from 1 to 15, with 1 being the highest priority. A tag level of 0
     * indicates that the sample is not tagged. Tag levels can be used to categorize
     * or prioritize samples based on their significance or to implement different visual
     * representations in user interfaces.
     * 
     * Common uses for tag levels include:
     * - Marking severity levels of anomalies (1 for critical, 2 for warning, etc.)
     * - Categorizing different types of events or points of interest
     * - Implementing multi-level filtering or highlighting in visualizations
     *
     * @param tag tag level (1..15) to apply, or 0 to remove any tag
     */
    void setTag(int tag);


}
