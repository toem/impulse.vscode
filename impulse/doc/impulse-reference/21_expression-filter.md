<!---
title: "Expression Filter Processor"
author: "Thomas Haber"
keywords: [impulse, expression filter, signal processor, filtering, expressions, sample selection, digital, extension, analysis, debugging]
description: "The Expression Filter processor for impulse lets users filter samples in a signal using a custom expression, enabling advanced data selection and transformation."
category: "impulse-reference"
tags:
  - reference
  - signal processor
docID: xxx
--->

# Expression Filter Processor

The Expression Filter processor allows users to filter samples in a signal using a custom expression. Only samples that match the given expression are retained; all others are removed. This enables advanced filtering, selection, and analysis of signal data using flexible criteria.

For details and examples, see the "Expression Filter Processor" section in the impulse expression language documentation ([Expressions](../manual/10_expressions.md)). Expressions can use variables, operators, and functions as described in the impulse expression language. Expressions support numeric, boolean, string, time, and logic values, as well as complex conditions and signal manipulations.

## Supporting

This processor supports:
- HOMOGENEOUS: Processes homogeneous signals
- MAIN_PROCESSING: Designed for main signal processing (resulting in 1 main output signal)
- MULTIPLE_SOURCES: Can combine multiple input sources/signals
- PROPERTIES: Provides options to customize processing behavior, filtering, and output attributes for signal processors.
- CONFIGURATION: Allows users to create, save, and select named configurations for different combinations and processing setups of input signals.

## In/Out

- **Input Signal:** Accepts any signal type containing samples to be filtered.
- **Output Signal:** Produces a signal containing only the samples that match the specified expression. The output signal retains relevant metadata and can be visualized or processed further in impulse.

## Properties

The Expression Filter processor provides the following configuration options:

- **Expression**: Enter the expression used to filter samples. The expression can reference sample values, signal properties, and use operators, functions, and variables as described in the impulse expression language. Only samples for which the expression evaluates to true are kept.
- **Ignore None**: Ignore 'None' samples of the input signals.
- **Hide Identical**: Check to hide sequence of identical samples.
- **Scale**: Configure the scale of the output signal. Scale defines the dimension of the array, such as the number of elements or the size of each element (e.g., dim=4 for a 4-element array). Proper scale settings ensure accurate representation and visualization of array structure.
- **Format**: Set the format for the output signal. The format specifier determines how array values are displayed, such as binary, decimal, hexadecimal, ASCII, label, or boolean. For arrays, format also controls whether values are shown as key-value pairs or just values (e.g., format=keyValues, format=values).
- **Domain**: Set the domain base for the output signal. The domain base indicates the minimum distance between two samples, typically measured in units like nanoseconds (ns), microseconds (us), or picoseconds (ps). It defines the granularity and time resolution of the array signal.
- **Start**: Set the start position or time for the output signal. The start value marks the initial position or timestamp of the array signal in its domain, establishing when the signal begins.
- **End**: Set the end position or time for the output signal. The end value denotes the final position or timestamp, defining the range or duration covered by the array signal.
- **Rate**: Set the sample rate for the output signal. The rate specifies how frequently samples occur; for continuous signals, this is a fixed interval, while for discrete signals, the rate is zero. Proper rate settings are crucial for accurate timing and analysis of array data.

These options allow users to customize how samples are filtered and displayed in impulse.

## Known Limitation
No known limitations.