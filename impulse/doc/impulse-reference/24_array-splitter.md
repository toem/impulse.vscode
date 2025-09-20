<!---
title: "Array Splitter Processor"
author: "Thomas Haber"
keywords: [impulse, array splitter, signal processor, array, deduce, child processing, extension, analysis, debugging]
description: "The Array Splitter processor for impulse splits an array signal into its individual elements, supporting child processing for deduce operations."
category: "impulse-reference"
tags:
	- reference
	- signal processor
docID: xxx
--->

# Array Splitter Processor

The Array Splitter processor splits an array signal into its individual elements. Each element is output as a separate child signal. This processor is used for deduce operations and supports only child processing.

## Supporting

This processor supports:
- HOMOGENEOUS: Processes homogeneous signals
- CHILD_PROCESSING: Designed for child signal processing (resulting in N child output signals)
- PROPERTIES: Provides options to customize processing behavior, filtering, and output attributes for signal processors.
- CONFIGURATION: Allows users to create, save, and select named configurations for different combinations and processing setups of input signals.

## In/Out

- **Input Signal:** Accepts an array signal to be split into elements.
- **Output Signals:** Produces one child signal for each element of the input array signal. Each child signal can be visualized or processed further in impulse.

## Properties

The Array Splitter processor provides the following configuration options:

- **Max**: Set the maximum number of elements to split from the input signal (default: 64).
- **Keep Tags**: The output samples will keep the tag information of each input sample.
- **Ignore None**: Ignore 'None' samples of the input signals.
- **Hide Identical**: Check to hide sequence of identical samples.


## Known Limitation
No known limitations.
