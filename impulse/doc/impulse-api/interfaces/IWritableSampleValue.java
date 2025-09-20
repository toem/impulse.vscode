package de.toem.impulse.samples;

import java.util.List;

import de.toem.impulse.samples.attachments.IAttachment;

/**
 * Interface providing write access to sample values in the impulse framework.
 * 
 * This interface extends both {@link ISampleValue} for value access and {@link IWritableSampleMeta}
 * for writeable metadata access, creating a complete API for modifying both the value and 
 * metadata of a sample. It defines methods for setting sample values and associated attachments.
 * 
 * Key features of this interface include:
 * - Methods to modify a sample's core value data
 * - Capabilities to update or replace a sample's attachments
 * - Combined access to both value and metadata modification
 * 
 * The IWritableSampleValue interface is primarily used in scenarios where samples need to be
 * dynamically created or modified, such as when:
 * - Building signals programmatically
 * - Implementing transformation algorithms that produce modified samples
 * - Constructing test data or simulations
 * - Processing samples from external data sources
 * 
 * This interface follows the pattern of separating read and write operations in the impulse
 * framework, allowing for clear distinction between read-only and mutable sample access.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 63
 */
public interface IWritableSampleValue extends ISampleValue, IWritableSampleMeta {

    // ========================================================================================================================
    // Value
    // ========================================================================================================================

    /**
     * Sets the value of this sample.
     * 
     * This method provides the primary mechanism for changing a sample's value data.
     * The value object type should be appropriate for the signal type:
     * 
     * - For analog signals: Number (Double, Integer, etc.)
     * - For digital signals: Logic
     * - For string signals: String
     * - For enum signals: Enumeration
     * - For structured signals: Struct
     * - For binary signals: byte[]
     * 
     * When setting a value of a different type than the sample's native type,
     * automatic type conversion may be attempted, but this depends on the implementation.
     * For most reliable results, provide a value of the expected type for the signal.
     * 
     * @param value the new value object to set for this sample
     * @throws IllegalArgumentException if the provided value cannot be converted to the expected type
     */
    void setValue(Object value);

    /**
     * Sets all attachments for this sample.
     * 
     * This method replaces any existing attachments with the provided list. Attachments are
     * additional pieces of information linked to a sample, such as:
     * 
     * - Relations: Links between this sample and other samples/signals
     * - Labels: Textual annotations or markers providing additional context
     * 
     * Attachments are particularly useful for:
     * - Representing dependencies between signals or samples
     * - Adding explanatory notes or descriptions to specific data points
     * - Creating visual annotations for analysis or presentation
     * - Building complex data structures with cross-references
     * 
     * If you want to remove all attachments, call this method with an empty list.
     * If you want to add attachments while preserving existing ones, you should first
     * retrieve the current attachments, add to the list, and then set the updated list.
     *
     * @param attachments the new list of attachments to associate with this sample, can be empty
     */
    void setAttachments(List<IAttachment> attachments);

}
