package de.toem.impulse.samples;

/**
 * Interface representing a single, readable sample in the impulse framework.
 * 
 * This interface combines the capabilities of {@link IReadableValue} and {@link ISampleValue}, 
 * providing a comprehensive representation of an individual sample with all its metadata and 
 * value access methods. It represents a complete, self-contained sample that has been extracted 
 * from a signal, typically obtained through methods like {@link IReadableSamples#compoundAt(int)}.
 * 
 * The IReadableSample interface is particularly useful for:
 * - Processing individual samples with their complete context
 * - Analyzing samples that have been extracted from their original signal
 * - Working with sample data in a portable, self-contained format
 * - Passing complete sample information between components or applications
 * 
 * Unlike raw sample values, an IReadableSample includes position information, group affiliations,
 * attachments, and other metadata alongside the actual value. This makes it suitable for 
 * scenarios where the full context of a sample is required for proper analysis or visualization.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 31
 * 
 * @see IReadableValue for methods related to value access and formatting
 * @see ISampleValue for sample-specific metadata and properties
 */
public interface IReadableSample extends IReadableValue, ISampleValue {
    
    
}
