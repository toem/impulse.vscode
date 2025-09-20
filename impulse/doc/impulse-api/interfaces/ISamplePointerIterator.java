package de.toem.impulse.samples;

import java.util.Iterator;

import de.toem.impulse.samples.domain.DomainLongValue;
import de.toem.impulse.samples.domain.DomainValue;
import de.toem.impulse.samples.domain.IDomainBase;

/**
 * Interface for bi-directional iteration over multiple signals within a specified domain range.
 *
 * This interface extends Java's standard Iterator interface to provide specialized functionality
 * for navigating through multiple signal samples in the impulse framework. Unlike standard iterators that
 * only support forward traversal, ISamplesIterator enables both forward and backward movement 
 * through a sequence of samples across multiple signals, with support for domain-aware positioning.
 * 
 * The iterator operates across multiple signals (accessible via ISamplePointer objects) that must share
 * the same domain base. It iterates to the next or previous domain position that contains a sample change 
 * in any of the source signals. An important characteristic is that a single domain position may contain
 * changes in multiple signals simultaneously.
 * 
 * When the iterator moves to a domain position, that position may represent multiple simultaneous changes
 * across different signals. This makes it suitable for synchronized analysis of related signals.
 * 
 * Key features of this interface include:
 * - Bidirectional traversal (next/previous) across multiple signals
 * - Domain-aware positioning using time units or domain values
 * - Direct sample access for specific signals via ISamplePointer
 * - Edge detection capabilities for digital signals
 * - Support for domain conversion using target writers
 * 
 * The ISamplesIterator interface is designed for efficient traversal and processing of samples
 * across multiple related signals. It's particularly useful for operations like:
 * - Synchronized analysis of multiple signals that requires back-and-forth navigation
 * - Processing samples that meet specific criteria across signal groups
 * - Detecting and working with signal edges and transitions
 * - Converting positions between different domain bases
 * - Analyzing temporal relationships between different signals
 * 
 * This iterator operates on domain positions rather than sample indices, making it suitable
 * for working with both continuous and discrete signals with varying sample densities.
 * 
 * After positioning the iterator with prev() or next(), you can access sample values and metadata
 * from the monitored signals using methods like val(), hasTag(), intValue(), etc. on the respective
 * ISamplePointer objects. These access methods allow you to:
 * - Retrieve raw values via val() or typed values via doubleValue(), intValue(), booleanValue(), etc.
 * - Access metadata through hasTag() and getTag() methods to examine signal-specific properties
 * - Obtain signal characteristics via getCharacteristic() for analysis of signal attributes
 * - Query value-specific information such as getUnit(), getLabel(), and getDescription()
 * - Check state information like isInterpolated() to understand data quality
 * 
 * This integrated access pattern makes it possible to analyze multiple signals synchronously at
 * each domain position without having to manage separate state for each signal. When no more samples 
 * are available, prev() returns Long.MIN_VALUE and next() returns Long.MAX_VALUE.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 47
 */
public interface ISamplePointerIterator extends Iterator<Long> {

    // ========================================================================================================================
    // Traversal
    // ========================================================================================================================
    
    /**
     * Determines if there is a previous sample change available in the iteration sequence.
     * 
     * This method checks whether backward movement in the sample sequence is possible
     * across any of the signals being iterated. It returns true if there is at least 
     * one sample change before the current position within the iteration range in any of 
     * the monitored signals. A domain position may contain changes in multiple signals,
     * and this method will check for any such domain position with changes.
     * 
     * This is the backward-movement counterpart to the standard {@link Iterator#hasNext()} method.
     *
     * @return true if a previous sample change exists in any signal, false if at the beginning of the iteration range
     */
    boolean hasPrev();

    /**
     * Moves to the previous sample change and returns its position.
     * 
     * This method moves the iterator's position backward to the preceding domain position
     * that contains a sample change in any of the signals being monitored. It returns the
     * domain position as a multiple of the domain base. Note that a single domain position
     * may contain changes in multiple signals.
     * 
     * The iterator moves to the most recent domain position that contains at least one sample
     * change across all monitored signals.
     * 
     * If there is no previous sample change (i.e., the iterator is at the beginning of the
     * iteration range), this method returns Long.MIN_VALUE.
     * 
     * It's recommended to check {@link #hasPrev()} before calling this method.
     *
     * @return position of the previous sample change as a long value, or Long.MIN_VALUE if no previous sample change exists
     */
    long prev();
    
    /**
     * Moves to the previous sample change and returns its position converted to the target's domain.
     * 
     * This method moves the iterator's position backward to the preceding domain position
     * that contains a sample change in any of the monitored signals. Note that a single
     * domain position may contain changes in multiple signals.
     * 
     * The target parameter is used to convert the position value to be compatible 
     * with the target's domain base and process type.
     * 
     * This is useful when you need to get a position value that's directly usable with 
     * a specific writer that may have a different domain base.
     * 
     * If there is no previous sample change (i.e., the iterator is at the beginning of the
     * iteration range), this method returns Long.MIN_VALUE.
     * 
     * @param target the writer whose domain base and process type are used for position conversion
     * @return position of the previous sample change as a long value, converted to the target's domain base, or Long.MIN_VALUE if no previous sample change exists
     */
    long prev(ISamplesWriter target);
    
    /**
     * Moves to the next sample change and returns its position.
     * 
     * This method overrides the standard {@link Iterator#next()} method to provide 
     * domain-specific functionality. It moves the iterator's position forward to the 
     * next domain position that contains a sample change in any of the monitored signals
     * and returns that domain position as a multiple of the domain base. Note that a single 
     * domain position may contain changes in multiple signals.
     * 
     * The iterator moves to the next domain position that contains at least one sample
     * change across all signals being monitored.
     * 
     * If there is no next sample change (i.e., the iterator is at the end of the
     * iteration range), this method returns Long.MAX_VALUE.
     * 
     * 
     * @return position of the next sample change as a Long value, or Long.MAX_VALUE if no next sample change exists
     */
    Long next();
    
    /**
     * Moves to the next sample change and returns its position converted to the target's domain.
     * 
     * This method moves the iterator's position forward to the next domain position
     * that contains a sample change in any of the monitored signals. Note that a single
     * domain position may contain changes in multiple signals.
     * 
     * The target parameter is used to convert the position value to be compatible 
     * with the target's domain base and process type.
     * 
     * This is useful when you need to get a position value that's directly usable with 
     * a specific writer that may have a different domain base.
     * 
     * If there is no next sample change (i.e., the iterator is at the end of the
     * iteration range), this method returns Long.MAX_VALUE.

     * @param target the writer whose domain base and process type are used for position conversion
     * @return position of the next sample change as a long value, converted to the target's domain base, or Long.MAX_VALUE if no next sample change exists
     */
    long next(ISamplesWriter target);

    /**
     * Returns the domain position of the current sample change.
     * 
     * This method provides the position of the current sample change across all monitored signals,
     * expressed as a multiple of the common domain base. This domain position may contain
     * changes in multiple signals being monitored. The position is typically measured in
     * units such as nanoseconds, microseconds, or domain-specific units.
     * 
     * @return the current sample change position as a long value
     */
    long current();
    
    /**
     * Returns the current position as a DomainLongValue.
     * 
     * This method provides a more detailed representation of the current position,
     * including domain-specific context such as units or scaling factors. The 
     * DomainLongValue combines both the position value and the domain base information.
     * 
     * @return the current position as a DomainLongValue object
     */
    DomainLongValue getPosition();
    
    /**
     * Returns the domain position of the current sample change converted to the target's domain.
     * 
     * This method provides the position of the sample change at the iterator's current location.
     * This domain position may contain changes in multiple signals being monitored.
     * 
     * The target parameter is used to convert the position value to be compatible with 
     * the target's domain base and process type.
     * 
     * This is useful for obtaining the current position in a domain base that matches
     * a specific writer without moving the iterator.
     * 
     * @param target the writer whose domain base and process type are used for position conversion
     * @return the current sample change position as a long value, converted to the target's domain base
     */
    long current(ISamplesWriter target);
    
    /**
     * Sets the current position to the specified domain units.
     * 
     * This method positions the iterator at the specified domain location, allowing
     * direct navigation to specific points across all signals. The iterationStart parameter
     * controls whether the next call to prev() or next() will return the sample change at
     * the newly set position (if true) or move to an adjacent sample change (if false).
     * 
     * @param multiple domain position as a multiple of the domain base
     * @param iterationStart if true, next prev() or next() will return the current sample change position
     */
    void move(long multiple, boolean iterationStart);

    /**
     * Sets the current position using a DomainValue object.
     * 
     * This method is similar to {@link #move(long, boolean)} but accepts a
     * DomainValue object that combines domain base and position information. This is
     * particularly useful when working with domain-specific values that have their
     * own units or scaling.
     * 
     * @param position the domain position as a DomainValue object
     * @param iterationStart if true, next prev() or next() will return the current sample change position
     */
    void move(DomainValue position, boolean iterationStart);

    // ========================================================================================================================
    // Traversal on single pointers
    // ========================================================================================================================
    
    /**
     * Moves to the previous sample for the specified pointer and returns the position.
     * 
     * This method supports signal-specific traversal by moving the iterator to the
     * previous sample change of only the specific signal referenced by the given pointer. Unlike 
     * the standard prev() method which considers all signals, this method only looks at the
     * specified signal and returns the domain position of the previous change.
     * 
     * If no previous sample exists for the specified pointer, this method returns Long.MIN_VALUE.
     * 
     * @param samples the sample pointer identifying the specific signal to traverse
     * @return position of the previous sample change as a long value, or Long.MIN_VALUE if no previous sample change exists
     */
    long prev(ISamplePointer samples);

    /**
     * Moves to the next sample change for the specified pointer and returns the position.
     * 
     * This method supports signal-specific traversal by moving the iterator to the
     * next sample change of only the specific signal referenced by the given pointer. Unlike 
     * the standard next() method which considers all signals, this method only looks at the
     * specified signal and returns the domain position of the next change.
     * 
     * If no next sample exists for the specified pointer, this method returns Long.MAX_VALUE.
     * 
     * @param samples the sample pointer identifying the specific signal to traverse
     * @return position of the next sample change as a long value, or Long.MAX_VALUE if no next sample change exists
     */
    long next(ISamplePointer samples);

    // ========================================================================================================================
    // Traversal to edges
    // ========================================================================================================================
    
    /**
     * Moves to the previous edge of the specified type for a digital signal and returns the position.
     * 
     * This method provides specialized navigation for digital signals by locating
     * the previous transition (edge) of the specified type in the specific signal
     * referenced by the given pointer. This method only examines the signal specified
     * by the samples parameter, not all signals being iterated.
     * 
     * Edge types include:
     * - 1: Rising edge (transition from low to high)
     * - -1: Falling edge (transition from high to low)
     * - 0: Any edge (either rising or falling)
     * 
     * This method uses the default edge detection logic for the signal.
     * 
     * If no previous edge of the specified type is found, this method returns Long.MIN_VALUE.
     * 
     * @param samples the sample pointer identifying the specific digital signal to analyze
     * @param edge edge type to detect (1=rising, -1=falling, 0=any)
     * @return position of the previous edge as a long value, or Long.MIN_VALUE if no previous edge found
     */
    long prevEdge(ISamplePointer samples, int edge);
    
    /**
     * Moves to the previous edge of the specified type using a custom detector and returns the position.
     * 
     * This method extends {@link #prevEdge(ISamplePointer, int)} by allowing
     * custom edge detection logic through the detector parameter. This is useful
     * for signals with non-standard voltage levels or specialized detection criteria.
     * 
     * If no previous edge of the specified type is found, this method returns Long.MIN_VALUE.
     * 
     * @param samples the sample pointer identifying the signal to traverse
     * @param edge edge type to detect (1=rising, -1=falling, 0=any)
     * @param detector custom logic detector implementation for specialized detection rules
     * @return position of the previous edge as a long value, or Long.MIN_VALUE if no previous edge found
     */
    long prevEdge(ISamplePointer samples, int edge, ILogicDetector detector);

    /**
     * Moves to the next edge of the specified type for a digital signal and returns the position.
     * 
     * This method provides specialized navigation for digital signals by locating
     * the next transition (edge) of the specified type in the specific signal
     * referenced by the given pointer. Edge types include:
     * - 1: Rising edge (transition from low to high)
     * - -1: Falling edge (transition from high to low)
     * - 0: Any edge (either rising or falling)
     * 
     * This method uses the default edge detection logic for the signal and only examines 
     * the specific signal referenced by the pointer, not all signals being iterated.
     * 
     * If no next edge of the specified type is found, this method returns Long.MAX_VALUE.
     * 
     * @param samples the sample pointer identifying the specific digital signal to analyze
     * @param edge edge type to detect (1=rising, -1=falling, 0=any)
     * @return position of the next edge as a long value, or Long.MAX_VALUE if no next edge found
     */
    long nextEdge(ISamplePointer samples, int edge);
    
    /**
     * Moves to the next edge of the specified type using a custom detector and returns the position.
     * 
     * This method extends {@link #nextEdge(ISamplePointer, int)} by allowing
     * custom edge detection logic through the detector parameter. This is useful
     * for signals with non-standard voltage levels or specialized detection criteria.
     * The method examines only the specific signal referenced by the given pointer,
     * not all signals being iterated.
     * 
     * If no next edge of the specified type is found, this method returns Long.MAX_VALUE.
     * 
     * @param samples the sample pointer identifying the specific digital signal to analyze
     * @param edge edge type to detect (1=rising, -1=falling, 0=any)
     * @param detector custom logic detector implementation for specialized detection rules
     * @return position of the next edge as a long value, or Long.MAX_VALUE if no next edge found
     */
    long nextEdge(ISamplePointer samples, int edge, ILogicDetector detector);

    
    // ========================================================================================================================
    // Changes
    // ========================================================================================================================
    
    /**
     * Determines if the specified sample pointer is at the current position.
     * 
     * This method checks whether the given sample pointer refers to the same
     * domain position as the iterator's current position. This is useful for
     * coordinating multiple iterators or pointers within the same signal domain.
     * 
     * @param samples the sample pointer to check
     * @return true if the sample pointer is at the same position as this iterator
     */
    boolean hasChange(ISamplePointer samples);
    
    // ========================================================================================================================
    // Domain Base and Range
    // ========================================================================================================================
    
    /**
     * Returns the domain base associated with this iterator.
     * 
     * The domain base defines the fundamental unit and characteristics of the domain
     * in which the iterator operates. For time-based signals, this typically represents
     * the time unit (e.g., nanoseconds or microseconds). For spatial signals, it might
     * represent distance units. All signals monitored by this iterator must share the same
     * domain base.
     * 
     * @return the domain base object containing unit information
     */
    IDomainBase getDomainBase();

    /**
     * Returns the start boundary of the iterator's range as domain units.
     * 
     * This method provides the beginning position of the range covered by this iterator,
     * expressed as a multiple of the domain base. The iterator will not move before this
     * position during traversal across any of the monitored signals.
     * 
     * @return the start position as a long value
     */
    long getStartAsMutliple();

    /**
     * Returns the end boundary of the iterator's range as domain units.
     * 
     * This method provides the ending position of the range covered by this iterator,
     * expressed as a multiple of the domain base. The iterator will not move beyond this
     * position during traversal across any of the monitored signals.
     * 
     * @return the end position as a long value
     */
    long getEndAsMultiple();
    
    /**
     * Returns the start boundary of the iterator's range as a DomainLongValue.
     * 
     * This method provides a more detailed representation of the start position,
     * including domain-specific context such as units or scaling factors. The 
     * DomainLongValue combines both the position value and the domain base information.
     * 
     * @return the start position as a DomainLongValue object
     */
    DomainLongValue getStart();

    /**
     * Returns the end boundary of the iterator's range as a DomainLongValue.
     * 
     * This method provides a more detailed representation of the end position,
     * including domain-specific context such as units or scaling factors. The 
     * DomainLongValue combines both the position value and the domain base information.
     * 
     * @return the end position as a DomainLongValue object
     */
    DomainLongValue getEnd();

}
