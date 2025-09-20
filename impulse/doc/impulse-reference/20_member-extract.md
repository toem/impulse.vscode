<!---
title: "Member Extract Processor"
author: "Thomas Haber"
keywords: [impulse, member extract, signal processor, struct, array, signal transformation, digital, extension, analysis, debugging]
description: "The Member Extract processor for impulse lets users extract a member from a struct or array signal, enabling advanced digital analysis and signal transformation."
category: "impulse-reference"
tags:
  - reference
  - signal processor
docID: xxx
--->

# Member Extract Processor

The Member Extract processor allows users to extract a member from a struct or array signal by specifying the member's name or index. The extracted member is used to create a new signal of the desired type (text, binary, integer, float, enumeration, or array). The source must be a struct or array signal.

## Supporting

This processor supports:
- HOMOGENEOUS: Processes homogeneous signals
- MAIN_PROCESSING: Designed for main signal processing (resulting in 1 main output signal)
- PROPERTIES: Provides options to customize processing behavior, filtering, and output attributes for signal processors.
- CONFIGURATION: Allows users to create, save, and select named configurations for different combinations and processing setups of input signals.

## In/Out

- **Input Signal:** Accepts struct or array signals containing multiple members or elements.
- **Output Signal:** Produces a signal of the specified type based on the extracted member. The output signal inherits relevant metadata and can be visualized or processed further in impulse.

## Properties

The Member Extract processor provides the following configuration options:

- **Member**: Select the member to extract from the source signal. The member can be identified by name or index. This property allows you to create a new signal of the specified type from the chosen member.
- **Keep Tags**: The output samples will keep the tag information of each input sample.
- **Ignore None**: Ignore 'None' samples of the input signals.
- **Hide Identical**: Check to hide sequence of identical samples.
- **Scale**: Configure the scale of the output signal. Scale defines the dimension of the array, such as the number of elements or the size of each element (e.g., dim=4 for a 4-element array). Proper scale settings ensure accurate representation and visualization of array structure.
- **Format**: Set the format for the output signal. The format specifier determines how array values are displayed, such as binary, decimal, hexadecimal, ASCII, label, or boolean. For arrays, format also controls whether values are shown as key-value pairs or just values (e.g., format=keyValues, format=values).
- **Domain**: Set the domain base for the output signal. The domain base indicates the minimum distance between two samples, typically measured in units like nanoseconds (ns), microseconds (us), or picoseconds (ps). It defines the granularity and time resolution of the array signal.
- **Start**: Set the start position or time for the output signal. The start value marks the initial position or timestamp of the array signal in its domain, establishing when the signal begins.
- **End**: Set the end position or time for the output signal. The end value denotes the final position or timestamp, defining the range or duration covered by the array signal.
- **Rate**: Set the sample rate for the output signal. The rate specifies how frequently samples occur; for continuous signals, this is a fixed interval, while for discrete signals, the rate is zero. Proper rate settings are crucial for accurate timing and analysis of array data.

These options allow users to customize how members are extracted and displayed in impulse.

## Known Limitation
No known limitations.