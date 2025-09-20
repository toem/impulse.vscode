package de.toem.impulse.samples;

import de.toem.impulse.samples.domain.DomainLongValue;
import de.toem.impulse.samples.domain.DomainValue;

/**
 * Interface for navigable pointers in the impulse framework's domain-oriented space.
 *
 * IPointer provides comprehensive positioning and navigation capabilities within a signal's domain space,
 * combining read capabilities of IReadableSamples, IReadableValue, and IAbstractValueSupply with navigation
 * functionality. The interface implements a dual positioning system:
 * 
 * 1. Sample/Group Index (Point): An integer index (0-based) directly referencing a discrete data point
 *    in the sequence. Used to access the actual sample/group data.
 * 
 * 2. Domain Position: A precise domain-space position that may fall between samples/groups. Composed of:
 *    - The referenced sample/group position (determined by the index)
 *    - A delta value representing an offset from that sample/group position
 * 
 * Example: In a time-domain signal with samples at 0ms, 10ms, 20ms:
 *    - Index (point) = 1 (references the sample at 10ms)
 *    - Domain position = 15ms (with a delta of 5ms from the referenced sample)
 * 
 * This design allows for both:
 *    - Discrete navigation through actual data points (samples/groups)
 *    - Precise positioning anywhere in the continuous domain space
 * 
 * Key features:
 * - Bidirectional navigation (next/previous)
 * - Direct access by index or domain position
 * - Relative and absolute position manipulation
 * - Domain-aware positioning with comprehensive position information
 * - Value access and formatting capabilities
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 25
 */
public interface IPointer extends IReadableSamples, IReadableValue, IAbstractValueSupply {


    // ========================================================================================================================
    // Samples
    // ========================================================================================================================

    /**
     * Returns the underlying samples object that this pointer references.
     * 
     * This method provides access to the complete signal data (samples or groups) that the pointer
     * is navigating. This is useful when operations need to be performed on the
     * entire signal rather than just at the current pointer position.
     *
     * @return the referenced samples object
     */
    ISamples getReference();
    
    // ========================================================================================================================
    // Traversal
    // ========================================================================================================================

    /**
     * Moves the pointer to the previous sample or group and returns success status.
     * 
     * This method navigates to the sample or group immediately before the current position
     * in the signal. If successful, the pointer's position is updated and the method
     * returns true. If there is no previous element (i.e., the pointer is at the first
     * element in the signal), the position remains unchanged and the method returns false.
     *
     * @return true if successfully moved to the previous element, false if already at the first element
     */
    boolean goPrev();

    /**
     * Moves the pointer to the next sample or group and returns success status.
     * 
     * This method navigates to the sample or group immediately after the current position
     * in the signal. If successful, the pointer's position is updated and the method
     * returns true. If there is no next element (i.e., the pointer is at the last
     * element in the signal), the position remains unchanged and the method returns false.
     *
     * @return true if successfully moved to the next element, false if already at the last element
     */
    boolean goNext();

    /**
     * Determines if there is a previous sample or group available from the current position.
     * 
     * This method checks whether backward movement in the sample or group sequence is possible
     * without actually changing the pointer's position. It's typically used before
     * calling {@link #goPrev()} to avoid attempting to move before the first element.
     *
     * @return true if a previous element exists, false if at the first element
     */
    boolean hasPrev();

    /**
     * Determines if there is a next sample or group available from the current position.
     * 
     * This method checks whether forward movement in the sample or group sequence is possible
     * without actually changing the pointer's position. It's typically used before
     * calling {@link #goNext()} to avoid attempting to move past the last element.
     *
     * @return true if a next element exists, false if at the last element
     */
    boolean hasNext();

    /**
     * Moves the pointer to the first sample or group in the signal.
     * 
     * This method provides a convenient way to reset the pointer to the beginning
     * of the signal, regardless of its current position. If the pointer was already
     * at the first element, the method returns false; otherwise, it returns true.
     *
     * @return true if the position changed, false if already at the first element
     */
    boolean goMin();

    /**
     * Moves the pointer to the last sample or group in the signal.
     * 
     * This method provides a convenient way to position the pointer at the end
     * of the signal, regardless of its current position. If the pointer was already
     * at the last element, the method returns false; otherwise, it returns true.
     *
     * @return true if the position changed, false if already at the last element
     */
    boolean goMax();
    
    /**
     * Moves the pointer to the sample or group at the specified index.
     * 
     * This method provides direct access to samples or groups by their ordinal position in the signal.
     * Valid indices range from 0 to getCount()-1. If an invalid index is provided,
     * the behavior is implementation-dependent and may throw an exception.
     *
     * @param index the sample or group index (0..getCount()-1)
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    void goPoint(int index);

    /**
     * Returns the current sample or group index.
     * 
     * This method provides the ordinal position (index) of the current sample or group within
     * the signal. The index is a zero-based value ranging from 0 to getCount()-1.
     *
     * @return the current sample or group index (0..getCount()-1)
     */
    int getPoint();

    // ========================================================================================================================
    // Range
    // ========================================================================================================================

    /**
     * Returns the maximum valid sample or group index in the signal.
     * 
     * This method provides the highest valid index that can be used with {@link #goPoint(int)}.
     * Typically, this is equivalent to getCount()-1, representing the last sample or group in the signal.
     *
     * @return the maximum valid sample or group index
     */
    int getMaxPoint();
    
    /**
     * Returns the minimum valid sample or group index in the signal.
     * 
     * This method provides the lowest valid index that can be used with {@link #goPoint(int)}.
     * Typically, this is 0, representing the first sample or group in the signal.
     *
     * @return the minimum valid sample or group index (usually 0)
     */
    int getMinPoint();
    
    // ========================================================================================================================
    // Position
    // ========================================================================================================================

    /**
     * Returns the domain position of the pointer as a multiple of the domain base.
     * 
     * This method provides the current position of the pointer in the signal's domain,
     * expressed as a multiple of the domain base. The position is equivalent to calling
     * 
     * Note that the pointer's position may not necessarily align exactly with a sample or group;
     * it can be positioned between elements. To get the position of the current sample or group,
     * use {@link #getPositionAsMultiple(boolean)} with a true parameter.
     *
     * @return the position as a long value (to be multiplied with the domain base)
     * @see #getPositionAsMultiple(boolean) for more control over the position returned
     */
    long getPositionAsMultiple();

    /**
     * Returns the domain position as a multiple of the domain base.
     * 
     * This method provides either the pointer's current position or the position of the
     * current sample or group, depending on the atSample parameter. When atSample is true, the
     * method returns the exact position of the sample or group at the current index. When atSample
     * is false, it returns the pointer's position, which may be between elements.

     *
     * @param atSample if true, returns the position of the indexed sample or group; if false, returns the pointer's position
     * @return the position as a long value (to be multiplied with the domain base)
     */
    long getPositionAsMultiple(boolean atSample);
    
    /**
     * Returns the domain position of the pointer as a DomainLongValue.
     * 
     * This method provides the current position of the pointer in the signal's domain
     * as a DomainLongValue object, which combines the position value with domain base
     * information. It is equivalent to calling {@link #getPosition(boolean)} with a
     * false parameter.
     * 
     * The DomainLongValue provides a more complete representation of the position,
     * including units and formatting capabilities.
     * 
     * @return the position as a DomainLongValue object
     * @see #getPosition(boolean) for more control over the position returned
     */
    DomainLongValue getPosition();

    /**
     * Returns the domain position as a DomainValue.
     * 
     * This method provides either the pointer's current position or the position of the
     * current sample or group as a DomainValue object, depending on the atSample parameter. When
     * atSample is true, the method returns the exact position of the sample or group at the current
     * index. When atSample is false, it returns the pointer's position, which may be
     * between elements.
     * 
     * The returned DomainValue includes both the position value and domain base information,
     * providing a complete representation with units and formatting capabilities.
     * 
     * @param atSample if true, returns the position of the indexed sample or group; if false, returns the pointer's position
     * @return the position as a DomainValue object
     */
    DomainValue getPosition(boolean atSample);
    
    /**
     * Sets the delta between the pointer's position and the current sample's or group's position.
     * 
     * This method is primarily for internal use. It adjusts the offset between the
     * pointer's position and the position of the sample or group at the current index. This
     * allows the pointer to be positioned precisely, even between elements.
     * 
     * The delta is expressed as a multiple of the domain base units.
     *
     * @param delta the position offset as a multiple of the domain base
     */
    void setDelta(long delta);

    /**
     * Returns the delta between the pointer's position and the current sample's or group's position.
     * 
     * This method provides the offset between the pointer's position and the position
     * of the sample or group at the current index. A non-zero delta indicates that the pointer
     * is positioned between elements.
     * 
     * The delta is expressed as a multiple of the domain base units.
     *
     * @return the position offset as a multiple of the domain base
     */
    long getDelta();

    /**
     * Returns true if there is a delta between the pointer's position and the current sample's or group's position.
     * 
     * This method checks whether the pointer is positioned exactly at a sample/group position
     * or between samples/groups. It returns true if the pointer has an offset from the
     * current sample/group position (i.e., it's positioned between data points).
     * 
     * This is a convenience method equivalent to checking whether getDelta() != 0.
     *
     * @return true if the pointer has a non-zero delta from the current sample/group position, false otherwise
     */
    boolean hasDelta();

    
    /**
     * Sets the position of the pointer to the specified multiple of the domain base.
     * 
     * This method positions the pointer at the specified location in the signal's domain.
     * If the position doesn't exactly match a sample or group position, the pointer will reference
     * the nearest element with an appropriate delta offset.
     * 
     * The units parameter represents a position as a multiple of the domain base. For
     * example, if the domain base is 1ns, a units value of 1000 represents 1000ns.
     *
     * @param position domain position as a multiple of the domain base
     */
    void setPosition(long position);

    /**
     * Sets the position of the pointer using a DomainValue object.
     * 
     * This method positions the pointer at the specified location in the signal's domain.
     * If the position doesn't exactly match a sample or group position, the pointer will reference
     * the nearest element with an appropriate delta offset.
     * 
     * The DomainValue parameter provides a more expressive way to specify positions,
     * including units and domain-specific context.
     *
     * @param position the domain position as a DomainValue object
     */
    void setPosition(DomainValue position);

    // ========================================================================================================================
    // Relative Pointers
    // ========================================================================================================================

    /**
     * Creates a new pointer positioned relative to this pointer's position.
     * 
     * This method creates a new pointer instance that references the same signal but
     * is positioned at a specified offset from the current pointer. The offset is
     * specified as a string representing a domain value, such as "10ms" or "-5µs".
     * 
     * This is useful for working with multiple related positions within a signal without
     * having to constantly reposition a single pointer.
     *
     * @param delta domain distance as a text describing the domain value (e.g., "10ms")
     * @return a new pointer instance positioned at the relative location
     */
    IPointer relative(String delta);

    /**
     * Creates a new pointer positioned relative to this pointer's position.
     * 
     * This method creates a new pointer instance that references the same signal but
     * is positioned at a specified offset from the current pointer. The offset is
     * specified as a DomainValue object, providing precise control over the relative
     * positioning.
     * 
     * This is useful for working with multiple related positions within a signal without
     * having to constantly reposition a single pointer.
     *
     * @param delta domain distance as a DomainValue object
     * @return a new pointer instance positioned at the relative location
     */
    IPointer relative(DomainValue delta);

    /**
     * Creates a new pointer positioned relative to this pointer's position.
     * 
     * This method creates a new pointer instance that references the same signal but
     * is positioned at a specified offset from the current pointer. The offset is
     * specified as a number of domain base units.
     * 
     * This is useful for working with multiple related positions within a signal without
     * having to constantly reposition a single pointer.
     *
     * @param delta domain distance as a multiple of the domain base
     * @return a new pointer instance positioned at the relative location
     */
    IPointer relative(long delta);
    

    // ========================================================================================================================
    // Formatting
    // ========================================================================================================================

    /**
     * Formats the current sample or group value using the specified format.
     * 
     * This method provides a formatted string representation of the sample or group at the
     * current pointer position. The format parameter specifies how the value should
     * be represented, such as binary, hexadecimal, or decimal.
     * 
     * Format values include:
     * - ISample.FORMAT_BINARY ("bin"): Binary representation
     * - ISample.FORMAT_OCTAL ("oct"): Octal representation
     * - ISample.FORMAT_HEXADECIMAL ("hex"): Hexadecimal representation
     * - ISample.FORMAT_DECIMAL ("dec"): Decimal representation
     * - ISample.FORMAT_LABEL ("label"): Enumeration label if available
     *
     * @param format format specifier string as defined in ISample
     * @return formatted string representation of the current sample or group value
     */
    String format(String format);

    /**
     * Returns the default format specifier for the current sample or group.
     * 
     * This method determines the most appropriate format for the sample or group at the
     * current pointer position based on its data type and content. The returned
     * format specifier can be used with {@link #format(String)} to get a formatted
     * representation of the sample or group value.
     *
     * @return default format specifier string as defined in ISample
     */
    String defaultFormat();    
}
