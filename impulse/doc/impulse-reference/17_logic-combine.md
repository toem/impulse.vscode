<!---
title: "Logic Combine Processor"
author: "Thomas Haber"
keywords: [impulse, logic combine, signal processor, logic vector, combine, bitwise, digital, extension, analysis, debugging]
description: "The Logic Combine processor for impulse combines multiple logic signals (single-bit or vectors) into a single logic vector signal, enabling advanced digital analysis and visualization."
category: "impulse-reference"
tags:
  - reference
  - signal processor
docID: xxx
--->

# Logic Combine Processor

The Logic Combine processor is a signal processor extension for impulse that enables users to combine multiple logic signals—whether single-bit or logic vectors—into a single logic vector signal containing all bits. This is especially useful for digital analysis, protocol decoding, and visualization of grouped logic signals.

## Supporting

This processor supports:
- HOMOGENEOUS: Processes homogeneous signals
- MAIN_PROCESSING: Designed for main signal processing (resulting in 1 main output signal)
- MULTIPLE_SOURCES: Can combine multiple input sources/signals
- PROPERTIES: Provides options to customize processing behavior, filtering, and output attributes for signal processors.
- CONFIGURATION: Allows users to create, save, and select named configurations for different combinations and processing setups of input signals.

## In/Out

- **Input Signals:** Accepts multiple logic signals, including single-bit (1-dimensional) and multi-bit (vector) logic signals.
- **Output Signal:** Produces a single logic vector signal containing all bits from the input signals, combined in the specified order.

## Properties

The Logic Combine processor provides the following configuration options:

- **Swap**: Swap the order of bits in the combined logic vector.
- **Invert**: Invert the bits of the combined logic vector.
- **Keep Tags**: The output samples will keep the tag information of each input sample.
- **Ignore None**: Ignore 'None' samples of the input signals.
- **Hide Identical**: Check to hide sequence of identical samples.
- **Scale**: Configure the scale of the output signal. The scale defines the dimension of the signal, such as the number of bits in a logic vector or the size of an array (e.g., bits=16 for a 16-bit signal, dim=2 for a 2-dimensional array). Proper scale settings ensure accurate representation and visualization of signal structure.
- **Format**: Set the format for the output signal. The format specifier determines how signal values are displayed, such as binary, decimal, hexadecimal, ASCII, label, or boolean. For arrays and structs, format also controls whether values are shown as key-value pairs or just values (e.g., format=hex, format=label, format=keyValues).
- **Domain**: Set the domain base for the output signal. The domain base indicates the minimum distance between two samples, typically measured in units like nanoseconds (ns), microseconds (us), or picoseconds (ps). It defines the granularity and time resolution of the signal.
- **Start**: Set the start position or time for the output signal. The start value marks the initial position or timestamp of the signal in its domain, establishing when the signal begins.
- **End**: Set the end position or time for the output signal. The end value denotes the final position or timestamp, defining the range or duration covered by the signal.
- **Rate**: Set the sample rate for the output signal. The rate specifies how frequently samples occur; for continuous signals, this is a fixed interval, while for discrete signals, the rate is zero. Proper rate settings are crucial for accurate timing and analysis.

These options allow users to customize how multiple logic signals are combined, displayed, and processed in impulse.

## Known Limitation
No known limitations.