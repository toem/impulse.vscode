package de.toem.impulse.samples;

import java.util.List;

import de.toem.impulse.i18n.I18n;
import de.toem.toolkits.pattern.general.ISupports;
import de.toem.toolkits.pattern.properties.PropertyModel;
import de.toem.toolkits.pattern.threading.ICancelable;
import de.toem.toolkits.pattern.threading.IProgress;

/**
 * Interface for processing and transforming signal samples in the impulse framework.
 *
 * This interface defines the core functionality for sample processors that can read from multiple input sources,
 * perform transformations or computations on the sample data, and produce output samples. Sample processors
 * serve as the foundation for implementing complex signal processing operations, filters, and data transformations
 * within the impulse ecosystem.
 *
 * Key features of this interface include:
 * - Support for multiple input sources through configurable source management
 * - Main and slave production modes for handling different processing scenarios
 * - Hierarchical processing capabilities for complex nested data structures
 * - Configurable property models for runtime parameter customization
 * - Asynchronous production support with progress monitoring and cancellation
 * - Update mechanisms for dynamic reconfiguration of processing parameters
 *
 * The processor supports various operation modes:
 * - Main production: Primary processing mode for generating the main output signal
 * - Slave productions: Secondary processing modes for generating derived or auxiliary signals
 * - Homogeneous processing: Optimized mode for processing signals with uniform characteristics
 *
 * Sample processors can be configured with different support flags to indicate their capabilities:
 * - Multiple inputs: Ability to process data from multiple source signals simultaneously
 * - Main production: Support for primary signal generation
 * - Slave productions: Support for generating secondary or derived signals
 * - Homogeneous optimization: Specialized handling for signals with consistent data patterns
 *
 * The interface extends `IReadableSamples` and `IHierarchicalSamples` to provide full access to the processed
 * output data, while implementing `ICancelable` to support interruption of long-running processing operations.
 * Through `ISupports.Static`, processors can declare their capabilities and supported features at runtime.
 *
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 75
 */
public interface ISamplesProcessor extends IReadableSamples,IHierarchicalSamples, ICancelable,ISupports.Static{

    // ========================================================================================================================
    // Support and Mode Constants  
    // ========================================================================================================================

    /** Support flag indicating the processor can handle multiple input sources simultaneously */
    static final int SUPPORT_MULTIPLE_INPUTS = 0x1;
    
    /** Support flag indicating the processor can perform main production operations */
    static final int SUPPORT_MAIN_PROCESSING = 0x2;
    
    /** Support flag indicating the processor can perform slave production operations */
    static final int SUPPORT_SLAVE_PROCESSING = 0x4;
    
    /** Support flag indicating the processor can optimize for homogeneous signal processing */
    static final int SUPPORT_HOMOGENEOUS = 0x8;
    
    /** Processing mode: No specific processing mode active */
    static final int MODE_NONE = 0;
    
    /** Processing mode: Main production mode for generating primary output signals */
    static final int MODE_MAIN_PROCESSING = 1;
    
    /** Processing mode: Slave production mode for generating secondary or derived signals */
    static final int MODE_SLAVE_PROCESSING = 2; 
    
    /** Processing mode: Combined main and slave production modes */
    static final int MODE_MAIN_SLAVE_PROCESSING = MODE_MAIN_PROCESSING | MODE_SLAVE_PROCESSING;

    // ========================================================================================================================
    // Slave Processor Interfaces
    // ========================================================================================================================

    /**
     * Interface for slave processors that provide read-only access to processed samples.
     * 
     * Slave processors are secondary processing units that generate derived or auxiliary
     * signals based on the main processor's operations. They extend the basic readable
     * samples interface to provide access to their processed output data.
     */
    public interface ISlaveProcessor extends IReadableSamples{

    }
    
    /**
     * Interface for hierarchical slave processors that support nested data structures.
     * 
     * This interface extends the basic slave processor to handle complex hierarchical
     * data relationships, enabling processing of structured or grouped sample data
     * with parent-child relationships and nested organization.
     */
    public interface IHierarchicalSlaveProcessor extends ISlaveProcessor{

    }
    
    /**
     * Interface for writable slave processors that can generate output samples.
     * 
     * This interface extends the slave processor to include write capabilities,
     * allowing the processor to generate and output processed sample data through
     * a dedicated samples writer interface.
     */
    public interface IWritableSlaveProcessor extends ISlaveProcessor {
        /**
         * Returns the samples writer used for generating output data.
         * 
         * @return The samples writer interface for this slave processor
         */
        ISamplesWriter getWriter();
    }

    // ========================================================================================================================
    // Property Model
    // ========================================================================================================================

    /**
     * Property model flags for selecting which properties to include
     */
    /** No properties selected */
    public static final int PROP_NONE = 0;
    
    /** Include property for tag handling (keepTags) */
    public static final int PROP_KEEP_TAGS = 1<<0;
    
    /** Include property for ignoring none values */
    public static final int PROP_IGNORE_NONE = 1<<1;
    
    /** Include property for hiding identical values */
    public static final int PROP_HIDE_IDENTICAL = 1<<2;
    
    /** Include property for sample type selection */
    public static final int PROP_SAMPLE_TYPE = 1<<3;
    
    /** Include property for scale configuration */
    public static final int PROP_SCALE = 1<<4;
    
    /** Include property for format configuration */
    public static final int PROP_FORMAT = 1<<5;
    
    /** Include properties for domain configuration */
    public static final int PROP_DOMAIN = 1<<6;
    
    /** Include all available properties */
    public static final int PROP_ALL = PROP_KEEP_TAGS | PROP_IGNORE_NONE | PROP_HIDE_IDENTICAL | PROP_SAMPLE_TYPE | PROP_SCALE | PROP_FORMAT | PROP_DOMAIN;

    /**
     * Creates a property model with the specified options.
     * 
     * This method builds a property model containing only the properties
     * specified by the options parameter. Each bit in the options parameter
     * corresponds to a PROP_* constant and determines whether that property
     * should be included in the model.
     *
     * @param options Bit flags specifying which properties to include (combination of PROP_* constants)
     * @return A property model containing the selected properties
     */
    static public PropertyModel getPropertyModel(int options) {
        PropertyModel model = new PropertyModel();

        //model.add("tags", null, null, null, I18n.General_Tags, null, I18n.General_Tags_Description);

        model.addIf((options & PROP_KEEP_TAGS) != 0, "keepTags", true, null, I18n.SamplesProcessor_KeepTags, null, I18n.SamplesProcessor_KeepTagsComment);
        model.addIf((options & PROP_IGNORE_NONE) != 0, "ignoreNone", true, null, I18n.SamplesProcessor_IgnoreNone, null, I18n.SamplesProcessor_IgnoreNoneComment);
        model.addIf((options & PROP_HIDE_IDENTICAL) != 0, "hideIdentical", true, null, I18n.SamplesProcessor_HideIdentical, null, I18n.SamplesProcessor_HideIdenticalComment);

        model.addIf((options & PROP_SAMPLE_TYPE) != 0, "sampleType", ISample.DATA_TYPE_UNKNOWN, ISample.getDataTypeLabels(false), ISample.getDataTypeOptions(false), null,
                I18n.Samples_SignalType, null, I18n.Samples_SignalType_Description);

        model.addIf((options & PROP_SCALE) != 0, "scale", ISample.SCALE_DEFAULT, null, null, null,I18n.Samples_SignalScale, null, I18n.Samples_SignalScale_Description);

        model.addIf((options & PROP_FORMAT) != 0, "format", null, null,
                null, I18n.Diagram_ValueFormat, null, I18n.Diagram_ValueFormat_Description);
      
        model.addIf((options & PROP_DOMAIN) != 0, "domainBase", null, null, null, I18n.Samples_DomainBase, null, I18n.Samples_DomainBase_Description);
        model.addIf((options & PROP_DOMAIN) != 0, "domainStart", null, null, null, I18n.Samples_DomainStart, null, I18n.Samples_DomainStart_Description);
        model.addIf((options & PROP_DOMAIN) != 0, "domainEnd", null, null, null, I18n.Samples_DomainEnd, null, I18n.Samples_DomainEnd_Description);
        model.addIf((options & PROP_DOMAIN) != 0, "domainRate", null, null, null, I18n.Samples_DomainRate, null, I18n.Samples_DomainRate_Description);
        return model;
    }

    // ========================================================================================================================
    // Source and Mode Management
    // ========================================================================================================================

    /**
     * Returns the input sources for this processor.
     * 
     * This method provides access to the list of readable samples that serve as input
     * sources for the processing operations. The processor reads data from these sources,
     * applies transformations or computations, and generates output samples.
     * 
     * @return List of readable samples serving as input sources for this processor
     */
    List<IReadableSamples> getSources();

    /**
     * Returns the current processing mode of this processor.
     * 
     * The processing mode determines how the processor operates and what type of output
     * it generates. Possible modes include main production, slave productions, or a
     * combination of both modes.
     * 
     * @return Current processing mode (MODE_NONE, MODE_MAIN_PRODUCTION, MODE_SLAVE_PRODUCTIONS, 
     *         or MODE_MAIN_SLAVE_PRODUCTIONS)
     */
    int getMode();
    
    /**
     * Returns the settling state of the processor's input sources.
     * 
     * This method checks whether any of the input sources are currently in a settling
     * state, which indicates that the source data is still being loaded, decoded, or
     * processed and may not yet be complete or stable.
     * 
     * @return true if at least one source is in settling state, false otherwise
     */
    // boolean areSourcesSettling();

    // ========================================================================================================================
    // Configuration Updates
    // ========================================================================================================================

    /**
     * Updates the processor with a new configuration and parameters.
     * 
     * This method attempts to reconfigure the processor with new settings, input sources,
     * and processing parameters. It is primarily intended for internal use by the framework
     * to dynamically adjust processor behavior based on changing requirements or user settings.
     * 
     * The update operation can result in different outcomes:
     * - No update required: The new configuration is identical to the current one
     * - Monotonous update: The configuration can be updated without disrupting ongoing operations
     * - Update not possible: The changes are too significant and require processor recreation
     * 
     * @param configuration Configuration string defining processor behavior
     * @param id Unique identifier for this processor instance
     * @param name Human-readable name for this processor
     * @param description Detailed description of the processor's purpose and operation
     * @param tags Comma-separated tags for categorization and filtering
     * @param sources List of input sources for processing
     * @param properties Array of property key-value pairs for processor configuration
     * @return 0 if no update required, 1 if monotonous update performed, -1 if update not possible
     */
    int update(String configuration, String id, String name, String description, String tags, List<IReadableSamples> sources, String[][] properties);

    /**
     * Updates the processor with new input sources.
     * 
     * This method provides a simplified way to update only the input sources for the processor,
     * leaving other configuration parameters unchanged. The sources parameter can be a single
     * source, a collection of sources, or any other object that can be interpreted as input sources.
     * 
     * @param sources New input sources for the processor (format depends on implementation)
     * @return 0 if no update required, 1 if monotonous update performed, -1 if update not possible
     */
    int update(Object sources);

    /**
     * Performs a general update operation on the processor.
     * 
     * This method triggers a general update of the processor, typically used to refresh
     * the processor state or apply pending configuration changes. The specific behavior
     * depends on the processor implementation and current state.
     * 
     * @return 0 if no update required, 1 if monotonous update performed, -1 if update not possible
     */
    int update();

    // ========================================================================================================================
    // Sample Production
    // ========================================================================================================================

    /**
     * Interface for listening to flush events during sample production.
     * 
     * This interface extends the progress interface to provide notifications when
     * the processor flushes processed data. Flush events indicate that a batch of
     * processed samples has been completed and made available for consumption.
     * 
     * Flush listeners are typically used for:
     * - Progress monitoring during long-running processing operations
     * - Coordinating with downstream consumers of processed data
     * - Implementing custom caching or buffering strategies
     * - Performance monitoring and optimization
     */
    interface IFlushListener extends IProgress {
        /**
         * Called when the processor flushes processed samples.
         * 
         * This method is invoked whenever the processor completes a batch of processing
         * and makes the results available. The release parameter indicates the position
         * or timestamp up to which samples have been processed and flushed.
         * 
         * @param processor The processor that performed the flush operation
         * @param release The position or timestamp up to which samples have been flushed
         */
        void flushed(ISamplesProcessor processor, long release);
    }

    /**
     * Initiates the sample production process.
     * 
     * This method starts the core processing operation where the processor reads from
     * its input sources, applies transformations or computations, and generates output
     * samples. The production process can be run synchronously or asynchronously based
     * on the async parameter.
     * 
     * During production, the processor:
     * - Reads data from all configured input sources
     * - Applies the specified processing operations (filtering, transformation, computation)
     * - Generates output samples according to the configured mode (main, slave, or both)
     * - Reports progress through the provided progress interface
     * - Handles cancellation requests through the ICancelable interface
     * 
     * For asynchronous operation, this method returns immediately and processing
     * continues in the background. For synchronous operation, the method blocks
     * until processing is complete or cancelled.
     * 
     * @param p Progress interface for monitoring and controlling the production process
     * @param async If true, production runs asynchronously; if false, runs synchronously
     */
    public void produce(IProgress p, boolean async);
}
