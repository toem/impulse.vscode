package de.toem.impulse.samples;

/**
 * Interface for a navigable pointer to samples within a signal.
 *
 * The ISamplePointer interface combines the read-only capabilities of {@link IReadableSample} and 
 * {@link ISampleValueSupply} with navigation functionality, allowing applications to move through
 * a signal's samples while accessing their values and properties. It represents a cursor that 
 * maintains a current position within a signal, with methods to inspect and navigate based on 
 * sample properties.
 * 
 * Key features of this interface include:
 * - Access to the sample value and metadata at the current position
 * - Edge detection capabilities for digital signals
 * - Navigation methods to move between samples and edges
 * - Integration with the pointer pattern via IPointer
 * 
 * The ISamplePointer interface is particularly useful for:
 * - Algorithms that need to scan through signal data
 * - Interactive applications where users navigate through samples
 * - Analysis tools that locate specific features like edges in digital signals
 * - Code that needs to correlate samples across multiple signals
 * 
 * Unlike iterators that create a separate navigation object, a pointer represents a persistent
 * location within the signal itself, maintaining state between operations.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 45
 */
public interface ISamplePointer extends IReadableSample, ISampleValueSupply, IPointer {
    
    /**
     * Determines if an edge of the specified type is present at the current position.
     * 
     * This method checks whether the transition from the previous sample to the current sample
     * constitutes an edge of the specified type using a custom detector. The detector parameter
     * allows for specialized edge detection logic tailored to specific signal characteristics.
     * 
     * Edge types:
     * - 1: Rising edge (transition from low to high, typically STATE_0_BITS to STATE_1_BITS)
     * - -1: Falling edge (transition from high to low, typically STATE_1_BITS to STATE_0_BITS)
     * - 0: Any edge (either rising or falling)
     * 
     * @param edge edge type to detect (1=rising, -1=falling, 0=any)
     * @param detector custom logic detector implementation for specialized detection rules
     * @return true if the specified edge type is detected, false otherwise
     */
    boolean isEdge(int edge, ILogicDetector detector);

    /**
     * Determines if an edge of the specified type is present at the current position.
     * 
     * This method checks whether the transition from the previous sample to the current sample
     * constitutes an edge of the specified type using the default edge detection logic.
     * 
     * Edge types:
     * - 1: Rising edge (transition from low to high, typically STATE_0_BITS to STATE_1_BITS)
     * - -1: Falling edge (transition from high to low, typically STATE_1_BITS to STATE_0_BITS)
     * - 0: Any edge (either rising or falling)
     * 
     * @param edge edge type to detect (1=rising, -1=falling, 0=any)
     * @return true if the specified edge type is detected, false otherwise
     */
    boolean isEdge(int edge);
    
    /**
     * Moves the pointer to the previous edge of the specified type using a custom detector.
     * 
     * This method navigates backward through the signal until it finds an edge (transition)
     * of the specified type. The custom detector allows for specialized edge detection logic
     * tailored to specific signal characteristics or application requirements.
     * 
     * If an edge is found, the pointer is positioned at that sample and the method returns true.
     * If no matching edge is found before the beginning of the signal, the pointer position 
     * remains unchanged and the method returns false.
     * 
     * @param edge edge type to find (1=rising, -1=falling, 0=any)
     * @param detector custom logic detector implementation for specialized detection rules
     * @return true if a matching edge was found and the pointer was moved, false otherwise
     */
    boolean goPrevEdge(int edge, ILogicDetector detector);

    /**
     * Moves the pointer to the previous edge of the specified type.
     * 
     * This method navigates backward through the signal until it finds an edge (transition)
     * of the specified type using the default edge detection logic.
     * 
     * If an edge is found, the pointer is positioned at that sample and the method returns true.
     * If no matching edge is found before the beginning of the signal, the pointer position 
     * remains unchanged and the method returns false.
     * 
     * @param edge edge type to find (1=rising, -1=falling, 0=any)
     * @return true if a matching edge was found and the pointer was moved, false otherwise
     */
    boolean goPrevEdge(int edge);
    
    /**
     * Moves the pointer to the next edge of the specified type using a custom detector.
     * 
     * This method navigates forward through the signal until it finds an edge (transition)
     * of the specified type. The custom detector allows for specialized edge detection logic
     * tailored to specific signal characteristics or application requirements.
     * 
     * If an edge is found, the pointer is positioned at that sample and the method returns true.
     * If no matching edge is found before the end of the signal, the pointer position 
     * remains unchanged and the method returns false.
     * 
     * @param edge edge type to find (1=rising, -1=falling, 0=any)
     * @param detector custom logic detector implementation for specialized detection rules
     * @return true if a matching edge was found and the pointer was moved, false otherwise
     */
    boolean goNextEdge(int edge, ILogicDetector detector);
    
    /**
     * Moves the pointer to the next edge of the specified type.
     * 
     * This method navigates forward through the signal until it finds an edge (transition)
     * of the specified type using the default edge detection logic.
     * 
     * If an edge is found, the pointer is positioned at that sample and the method returns true.
     * If no matching edge is found before the end of the signal, the pointer position 
     * remains unchanged and the method returns false.
     * 
     * @param edge edge type to find (1=rising, -1=falling, 0=any)
     * @return true if a matching edge was found and the pointer was moved, false otherwise
     */
    boolean goNextEdge(int edge);

}
