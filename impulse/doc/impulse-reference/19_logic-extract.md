<!---
title: "Logic Extract Processor"
author: "Thomas Haber"
keywords: [impulse, logic extract, signal processor, bit manipulation, logic vector, extract, swap, invert, digital, extension, analysis, debugging]
description: "The Logic Extract processor for impulse lets users extract, swap, and invert bits from logic signals, enabling advanced digital analysis and signal transformation."
category: "impulse-reference"
tags:
  - reference
  - signal processor
docID: xxx
--->

# Logic Extract Processor

The Logic Extract processor is a signal processor extension for impulse that lets users extract, swap, and invert bits from logic signals. It is ideal for advanced digital analysis, protocol decoding, and signal transformation workflows.

## Supporting

This processor supports:
- HOMOGENEOUS: Processes homogeneous signals
- MAIN_PROCESSING: Designed for main signal processing (resulting in 1 main output signal)
- PROPERTIES: Provides options to customize processing behavior, filtering, and output attributes for signal processors.
- CONFIGURATION: Allows users to create, save, and select named configurations for different combinations and processing setups of input signals.

## In/Out

- **Input Signal:** Accepts logic signals (single-bit or vector)
- **Output Signal:** Produces a logic signal or logic vector with extracted, swapped, or inverted bits as specified.

## Properties

The Logic Extract processor provides the following configuration options:

- **Extract**: Enable or disable bit extraction from the input signal.
- **Bit Position**: Specify the starting bit position for extraction.
- **Count**: Set the number of bits to extract.
- **Swap**: Swap the order of extracted bits.
- **Invert**: Invert the extracted bits.
- **Keep Tags**: The output samples will keep the tag information of each input sample.
- **Ignore None**: Ignore 'None' samples of the input signals.
- **Hide Identical**: Check to hide sequence of identical samples.
- **Scale**: Configure the scale of the output signal. Scale defines the dimension of the array, such as the number of elements or the size of each element (e.g., dim=4 for a 4-element array). Proper scale settings ensure accurate representation and visualization of array structure.
- **Format**: Set the format for the output signal. The format specifier determines how array values are displayed, such as binary, decimal, hexadecimal, ASCII, label, or boolean. For arrays, format also controls whether values are shown as key-value pairs or just values (e.g., format=keyValues, format=values).
- **Domain**: Set the domain base for the output signal. The domain base indicates the minimum distance between two samples, typically measured in units like nanoseconds (ns), microseconds (us), or picoseconds (ps). It defines the granularity and time resolution of the array signal.
- **Start**: Set the start position or time for the output signal. The start value marks the initial position or timestamp of the array signal in its domain, establishing when the signal begins.
- **End**: Set the end position or time for the output signal. The end value denotes the final position or timestamp, defining the range or duration covered by the array signal.
- **Rate**: Set the sample rate for the output signal. The rate specifies how frequently samples occur; for continuous signals, this is a fixed interval, while for discrete signals, the rate is zero. Proper rate settings are crucial for accurate timing and analysis of array data.

These options allow users to customize how bits are extracted, manipulated, and displayed in impulse.

## Known Limitation
No known limitations.