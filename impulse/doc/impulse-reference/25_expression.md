<!---
title: "Expression Processor"
author: "Thomas Haber"
keywords: [impulse, expression processor, signal processor, expressions, sample calculation, multiple sources, digital, extension, analysis, debugging]
description: "The Expression Processor for impulse computes the value of a user-defined expression for each sample, supporting multiple input signals and advanced data transformation."
category: "impulse-reference"
tags:
  - reference
  - signal processor
docID: xxx
--->

# Expression Processor

The Expression Processor computes the value of a user-defined expression for each sample, using one or more input signals. The output signal contains the result of the expression for each sample position. This enables advanced data transformation, calculation, and analysis workflows.

The processor supports multiple input signals. The expression refers to the primary source as `s0` and additional sources as `s1`, `s2`, etc. Expressions can use variables, operators, and functions as described in the impulse expression language (see [Expressions](../manual/10_expressions.md) for details and examples).

## Supporting

This processor supports:
- HOMOGENEOUS: Processes homogeneous signals
- MAIN_PROCESSING: Designed for main signal processing (resulting in 1 main output signal)
- MULTIPLE_SOURCES: Can combine multiple input sources/signals
- PROPERTIES: Provides options to customize processing behavior, filtering, and output attributes for signal processors.
- CONFIGURATION: Allows users to create, save, and select named configurations for different combinations and processing setups of input signals.

## In/Out

- **Input Signals:** Accepts one or more signals as input. The primary source is referenced as `s0`, and additional sources as `s1`, `s2`, etc.
- **Output Signal:** Produces a signal containing the computed value of the expression for each sample position. The output signal can be visualized or processed further in impulse.

## Properties

The Expression Processor provides the following configuration options:

- **Expression**: Enter the expression to compute for each sample. The expression can reference input signals by `s0`, `s1`, `s2`, etc., and use operators, functions, and variables as described in the impulse expression language. The result of the expression is stored in the output signal.
- **Ignore None**: Ignore 'None' samples of the input signals.
- **Hide Identical**: Check to hide sequence of identical samples.
- **Scale**: Configure the scale of the output signal. Scale defines the dimension of the array, such as the number of elements or the size of each element (e.g., dim=4 for a 4-element array). Proper scale settings ensure accurate representation and visualization of array structure.
- **Format**: Set the format for the output signal. The format specifier determines how array values are displayed, such as binary, decimal, hexadecimal, ASCII, label, or boolean. For arrays, format also controls whether values are shown as key-value pairs or just values (e.g., format=keyValues, format=values).
- **Domain**: Set the domain base for the output signal. The domain base indicates the minimum distance between two samples, typically measured in units like nanoseconds (ns), microseconds (us), or picoseconds (ps). It defines the granularity and time resolution of the array signal.
- **Start**: Set the start position or time for the output signal. The start value marks the initial position or timestamp of the array signal in its domain, establishing when the signal begins.
- **End**: Set the end position or time for the output signal. The end value denotes the final position or timestamp, defining the range or duration covered by the array signal.
- **Rate**: Set the sample rate for the output signal. The rate specifies how frequently samples occur; for continuous signals, this is a fixed interval, while for discrete signals, the rate is zero. Proper rate settings are crucial for accurate timing and analysis of array data.

These options allow users to customize how the expression is computed and how the output signal is displayed in impulse.

## Known Limitation
No known limitations.