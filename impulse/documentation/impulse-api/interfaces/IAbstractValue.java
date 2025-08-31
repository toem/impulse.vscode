package de.toem.impulse.samples;

import java.util.List;

import de.toem.impulse.samples.attachments.IAttachment;

/**
 * Interface that provides access to sample/group values and attachments in the impulse framework.
 *
 * This interface extends {@link IAbstractMeta} to add value access capabilities, forming a core part
 * of the impulse signal data access hierarchy. It provides the fundamental methods needed to retrieve
 * raw values and associated attachments from both individual samples and logical groups of samples.
 *
 * Key features of this interface include:
 * - Access to raw sample or group values without type conversion
 * - Retrieval of attachments such as relations and labels that provide context
 * - Support for both individual samples and grouped data structures
 *
 * The `IAbstractValue` interface serves as a foundation for more specialized interfaces that provide
 * type-specific value access or additional processing capabilities. It establishes the minimal common
 * functionality needed for accessing the core data content of samples and groups.
 *
 * This interface is designed to be used in scenarios where basic value access is required without the
 * need for the extensive conversion and processing capabilities provided by more specialized interfaces
 * like {@link IReadableValue} or {@link IReadableSample}.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 5
 */
public interface IAbstractValue extends IAbstractMeta {

    /**
     * Returns the current raw (no interpretation or conversion) sample/group value.
     * 
     * This method provides direct access to the underlying value in its native format without
     * any type conversion or interpretation. The returned object type depends on the signal type:
     * 
     * - For analog signals: Number (Double, Integer, etc.)
     * - For digital signals: Logic
     * - For string signals: String
     * - For enum signals: Enumeration
     * - For structured signals: Struct
     * - For binary signals: byte[]
     * 
     * While this method offers the most direct access to the value, it requires the caller to handle
     * type checking and casting. For type-specific access, more specialized interfaces like IReadableValue
     * provide methods such as doubleValue(), stringValue(), etc., which perform automatic type conversion.
     *
     * @return the raw sample or group value as its native object type
     */
    Object val();

    /**
     * Returns all attachments associated with this sample or group.
     * 
     * Attachments are additional pieces of information linked to a sample or group, providing
     * supplementary context beyond the primary value itself. The two primary types of attachments are:
     * 
     * - Relations: Links between samples/groups and other samples/signals
     * - Labels: Textual annotations or markers
     * 
     * Attachments are particularly useful for:
     * - Representing dependencies between signals or samples
     * - Adding explanatory notes or descriptions to specific data points
     * - Creating visual annotations for analysis or presentation
     * - Building complex data structures with cross-references
     * 
     * If no attachments exist, an empty list is returned.
     *
     * @return list of all attachments for this sample or group, never null
     */
    List<IAttachment> attachments();
}