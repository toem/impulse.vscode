<!---
title: "Logic Splitter Processor"
author: "Thomas Haber"
keywords: [impulse, logic splitter, signal processor, logic, bitwise, deduce, child processing, digital, extension, analysis, debugging]
description: "The Logic Splitter processor for impulse splits a logic signal into its individual components, supporting child processing for deduce operations."
category: "impulse-reference"
tags:
  - reference
  - signal processor
docID: xxx
--->

# Logic Splitter Processor

The Logic Splitter processor splits a logic signal into its individual components (single bits or groups of bits). Each component is output as a separate child signal. This processor is used for deduce operations and supports only child processing.

## Supporting

This processor supports:
- HOMOGENEOUS: Processes homogeneous signals
- CHILD_PROCESSING: Designed for child signal processing (resulting in N child output signal)
- PROPERTIES: Provides options to customize processing behavior, filtering, and output attributes for signal processors.
- CONFIGURATION: Allows users to create, save, and select named configurations for different combinations and processing setups of input signals.

## In/Out

- **Input Signal:** Accepts a logic signal to be split into components.
- **Output Signals:** Produces one child signal for each component (bit/channel) of the input logic signal. Each child signal can be visualized or processed further in impulse.

## Properties

The Logic Splitter processor provides the following configuration options:

- **Max**: Set the maximum number of components (bits/channels) to split from the input signal (default: 64).
- **Grouped**: Enable grouping of output signals (e.g., 4 will create groups of 4 bits).
- **Swap**: Swap the order of bits/channels in the output child signals.
- **Invert**: Invert the bits/channels in the output child signals.
- **Keep Tags**: The output samples will keep the tag information of each input sample.
- **Ignore None**: Ignore 'None' samples of the input signals.
- **Hide Identical**: Check to hide sequence of identical samples.

These options allow users to control how logic signals are split and how the resulting child signals are displayed in impulse.

## Known Limitation
No known limitations.