<!---
title: "Array Combine Processor"
author: "Thomas Haber"
keywords: [impulse, array combine, signal processor, array, combine, digital, extension, analysis, debugging]
description: "The Array Combine processor for impulse combines multiple signals into a single array signal, enabling advanced analysis and visualization of grouped data."
category: "impulse-reference"
tags:
  - reference
  - signal processor
docID: xxx
--->

# Array Combine Processor

The Array Combine processor is a signal processor extension for impulse that enables users to combine multiple signals—of the same type—into a single array signal. This is especially useful for grouping related signals, creating composite data structures, and visualizing arrays in impulse.

## Supporting

This processor supports:
- HOMOGENEOUS: Processes homogeneous signals
- MAIN_PROCESSING: Designed for main signal processing (resulting in one main output signal)
- MULTIPLE_SOURCES: Can combine multiple input sources/signals
- PROPERTIES: Provides options to customize processing behavior, filtering, and output attributes for signal processors.
- CONFIGURATION: Allows users to create, save, and select named configurations for different combinations and processing setups of input signals.

## In/Out

- **Input Signals:** Accepts multiple signals of the same type (integer, float, text, enumeration).
- **Output Signal:** Produces a single array signal containing all input signals as array elements, in the specified order.

## Properties

The Array Combine processor provides configuration options that allow users to control how signals are grouped into arrays and how the resulting array signal is displayed and processed. These options include tag handling, filtering, and signal attributes such as scale, format, domain, start, end, and rate.

- **Keep Tags**: The output samples will keep the tag information of each input sample.
- **Ignore None**: Ignore 'None' samples of the input signals.
- **Hide Identical**: Check to hide sequence of identical samples.
- **Signal Type**: Sets the output signal type. Supported types include integer, float, text, enumeration, and array. The signal type determines how array elements are interpreted and visualized (see signal.md for details).
- **Scale**: Configure the scale of the output signal. Scale defines the dimension of the array, such as the number of elements or the size of each element (e.g., dim=4 for a 4-element array). Proper scale settings ensure accurate representation and visualization of array structure.
- **Format**: Set the format for the output signal. The format specifier determines how array values are displayed, such as binary, decimal, hexadecimal, ASCII, label, or boolean. For arrays, format also controls whether values are shown as key-value pairs or just values (e.g., format=keyValues, format=values).
- **Domain**: Set the domain base for the output signal. The domain base indicates the minimum distance between two samples, typically measured in units like nanoseconds (ns), microseconds (us), or picoseconds (ps). It defines the granularity and time resolution of the array signal.
- **Start**: Set the start position or time for the output signal. The start value marks the initial position or timestamp of the array signal in its domain, establishing when the signal begins.
- **End**: Set the end position or time for the output signal. The end value denotes the final position or timestamp, defining the range or duration covered by the array signal.
- **Rate**: Set the sample rate for the output signal. The rate specifies how frequently samples occur; for continuous signals, this is a fixed interval, while for discrete signals, the rate is zero. Proper rate settings are crucial for accurate timing and analysis of array data.

These options allow users to customize how multiple signals are grouped, displayed, and processed as arrays in impulse.

## Known Limitation
No known limitations.