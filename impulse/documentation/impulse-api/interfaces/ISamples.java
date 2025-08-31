package de.toem.impulse.samples;

import de.toem.toolkits.pattern.element.exploits.Markers;
import de.toem.toolkits.pattern.general.ITransientData;
import de.toem.toolkits.pattern.information.IInformation;
import de.toem.toolkits.pattern.validation.IValidationResult;

/**
 * Core interface defining the fundamental capabilities for sample collections in the impulse framework.
 *
 * This interface serves as the foundation for all signal sample representations in the impulse ecosystem, 
 * providing essential infrastructure for managing and accessing collections of signal samples. It integrates
 * multiple aspects of sample management through inheritance from several key interfaces.
 *
 * Key features of this interface include:
 * - Sample characteristic information via {@link ISamplesCharacteristic}
 * - Core sample type definitions through {@link ISample}
 * - Transient data handling for optimized memory usage
 * - Validation support for ensuring data integrity
 * - Information management for metadata and descriptive content
 * - Reference management for tracking relationships between sample collections
 * - Event notification through listeners
 * 
 * The `ISamples` interface establishes a common foundation that is extended by more specialized interfaces
 * in the hierarchy, such as `IReadableSamples` for read operations and `IWritableSamples` for modification
 * operations. This separation of concerns allows for a clean API design while maintaining a consistent
 * underlying data model.
 *
 * Implementations of this interface typically represent complete signal datasets, such as waveforms,
 * protocol traces, log sequences, or other time-series data.
 *
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 49
 */
public interface ISamples extends ISample, ISamplesCharacteristic, ITransientData, IValidationResult, IInformation {

    
    /**
     * Standard tag for identifying transaction data structures.
     * Transactions typically represent multi-sample logical units with start, middle, and end components.
     */
    public static final String TAG_TRANSACTION = "transaction";
    
    /**
     * Standard tag for identifying log entry data structures.
     * Log entries are typically discrete events with timestamp and message information.
     */
    public static final String TAG_LOG = "log";
    
    /**
     * Standard tag for identifying Gantt chart data structures.
     * Gantt representations typically include task name, duration, and dependencies.
     */
    public static final String TAG_GANTT = "gantt";
    
    /**
     * Standard tag for identifying chart data structures.
     * Chart data typically contains plotable values with axes information.
     */
    public static final String TAG_CHART = "chart";
    
    /**
     * Standard tag for identifying image data structures.
     * Image data typically contains encoded visual information.
     */
    public static final String TAG_IMAGE = "image";
    
    /**
     * Standard tag for identifying label data structures.
     * Labels typically provide textual annotations for points or regions.
     */
    public static final String TAG_LABEL = "label";
    
    /**
     * Standard tag for identifying event data structures.
     * Events typically represent discrete occurrences at specific time points.
     */
    public static final String TAG_EVENT = "event";
    
    /**
     * Standard tag for identifying state data structures.
     * States typically represent continuous conditions over spans of time.
     */
    public static final String TAG_STATE = "state";
    
    // ========================================================================================================================
    // Information
    // ========================================================================================================================

    /**
     * Returns the icon identifier for this sample collection.
     * 
     * This method provides a way to associate an icon with a sample collection for visual
     * representation in user interfaces. The returned identifier can be used to retrieve
     * the appropriate icon resource.
     * 
     * The default implementation returns null, indicating no specific icon is associated
     * with the sample collection.
     *
     * @return The icon identifier string, or null if no icon is specified
     */
    default String getIconId() {
        return null;
    };


    // ========================================================================================================================
    // Referenced
    // ========================================================================================================================

    /**
     * Determines if this sample collection is referenced by another sample collection.
     * 
     * References are used to establish relationships between different sample collections,
     * such as when one signal is derived from another, or when samples are shared across
     * multiple views or analysis contexts.
     * 
     * This method checks for the presence of a reference marker in the sample collection's
     * data to determine if it is being referenced by another collection.
     *
     * @return true if this collection is referenced by another collection, false otherwise
     */
    default boolean isReferenced() {
        return getData("REF") != null;
    }

    /**
     * Returns the sample collection that references this collection, if any.
     * 
     * When a sample collection is referenced by another collection, this method provides
     * access to the referencer. This allows navigation through reference chains and enables
     * operations that need to consider the complete relationship context.
     * 
     * If no reference exists, this method returns null.
     *
     * @return The referencing sample collection, or null if this collection is not referenced
     */
    default ISamples getReferencer() {
        Object data = getData("REF");
        return data instanceof ISamples ? (ISamples) data : null;
    }

    // ========================================================================================================================
    // Listener
    // ========================================================================================================================

    /**
     * Interface for receiving notifications when a sample collection's release state changes.
     * 
     * Release state changes typically occur when data is loaded, unloaded, or when the sample collection
     * is modified in ways that affect its temporal boundaries or content. This listener interface allows
     * components to react to these changes and update accordingly.
     */
    public interface ISamplesReleasedListener {
        
        /**
         * Called when the release state of a sample collection changes.
         * 
         * This method is invoked when the release boundaries of a sample collection are modified.
         * Release boundaries define the portions of the signal data that are currently loaded and
         * available for access. Changes in these boundaries typically indicate that data has been
         * loaded, unloaded, or the collection has been modified.
         * 
         * @param samples The sample collection whose release state has changed
         * @param fromRelease The previous release identifier
         * @param toRelease The new release identifier
         */
        void released(ISamples samples, long fromRelease, long toRelease);
    }

    /**
     * Registers a listener to receive notifications when the sample collection's release state changes.
     * 
     * @param listener The listener to register
     */
    void addListener(ISamplesReleasedListener listener);

    /**
     * Unregisters a previously registered release state change listener.
     * 
     * @param listener The listener to unregister
     */
    void removeListener(ISamplesReleasedListener listener);

 
    // ========================================================================================================================
    // Internal
    // ========================================================================================================================
    
    /**
     * Returns the legend associated with this sample collection.
     * 
     * A legend contains metadata about member descriptors and enumeration values defined for the samples
     * in this collection. This information is essential for interpreting structured data, enumerations,
     * and complex value types.
     * 
     * This method is primarily intended for internal use within the framework, but can be useful for
     * advanced applications that need direct access to the complete legend information.
     * 
     * @return The legend object containing member and enumeration information
     */
    ISamplesLegend getLegend();

    /**
     * Returns the markers associated with this sample collection.
     * 
     * Markers provide additional annotations and highlights for specific regions or points within
     * the sample collection. They can be used to mark areas of interest, flag important events,
     * or provide visual cues for navigation and analysis.
     * 
     * @return The markers object containing all markers defined for this sample collection
     */
    Markers getMarkers();

}
