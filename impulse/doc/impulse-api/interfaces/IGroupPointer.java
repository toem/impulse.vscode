package de.toem.impulse.samples;

/**
 * Interface that represents a pointer to a group in the sample collection.
 *
 * A group pointer provides a specialized reference mechanism for accessing and manipulating
 * groups of samples within a signal. It combines the functionality of a readable group with
 * pointer navigation capabilities, allowing for efficient traversal and access to group data.
 *
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 17
 */
public interface IGroupPointer extends IReadableGroup, IGroupValueSupply,IPointer{

  
}
