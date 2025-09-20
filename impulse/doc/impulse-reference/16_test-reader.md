<!---
title: "Test Readers"
author: "Thomas Haber"
keywords: [impulse, test reader, line reader, byte block reader, serializer, import, binary, text, signal, domain base, debugging, extension]
description: "The Test Readers extension for impulse provides simple serializers to help users import their content into impulse. Includes the Test Line Reader for text signals and the Test Byte Block Reader for binary signals, both using domain base as current time from start of reading."
category: "impulse-reference"
tags:
  - reference
  - serializer
docID: xxx
--->

# Test Readers

The Test Readers extension provides simple serializers to help users get their content into impulse for quick inspection and debugging. It includes two readers:

- **Test Line Reader**: Creates a text signal containing all lines from the input file. Each line is imported as a sample, with the domain base set to the current time from the start of reading. This is useful for quickly visualizing and analyzing textual data in impulse.
- **Test Byte Block Reader**: Creates a binary signal with binary blocks from the input file. Each block is imported as a sample, with the domain base set to the current time from the start of reading. This allows users to inspect and process raw binary data.

Both readers are designed for ease of use and rapid import, making them ideal for testing, debugging, and initial data exploration.
For both readers, the domain base of each sample is set to the current time from the start of reading, providing a simple time-based sequence for visualization.

## Supporting

This serializer does neither support properties and configuratons.

## Format

- **Test Line Reader**: Expects a plain text file. Each line is imported as a sample in a text signal.
- **Test Byte Block Reader**: Expects a binary file. Data is read in blocks and imported as samples in a binary signal.

## Known Limitation
No known limitations.