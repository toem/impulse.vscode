package de.toem.impulse.samples;

/**
 * Interface for writing numeric samples in the impulse framework.
 *
 * This interface provides the common foundation for all numeric sample writers, serving as
 * a base interface for more specialized numeric types such as integers and floating-point values.
 * It extends the base ISamplesWriter interface with fundamental numeric functionality, enabling
 * consistent handling of all numerical signal data.
 *
 * Key features of this interface include:
 * - A generic method for writing any Number-based value to a signal
 * - Consistent positioning and tagging capabilities across numeric types
 * - Type-agnostic numeric handling for polymorphic code
 *
 * The `INumberSamplesWriter` interface establishes a unified approach to numeric sample writing,
 * allowing code to work with numbers generically when the specific numeric type is not important.
 * This promotes code reuse and enables easier handling of mixed numeric signals.
 *
 * This interface is typically not used directly but through its more specialized child interfaces
 * like IIntegerSamplesWriter and IFloatSamplesWriter, which provide additional type-specific
 * methods and optimizations for their respective number types.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 23
 */
public interface INumberSamplesWriter extends ISamplesWriter{

   boolean write(long position, boolean tag, Number value);
}
