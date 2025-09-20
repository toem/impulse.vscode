<!---
title: "Bytes Formatter"
author: "Thomas Haber"
keywords: [impulse, formatter, bytes, binary, encoding, format]
description: "Formatter that displays binary data as hexadecimal byte sequences or selectable encodings for inspection."
category: "impulse-reference"
tags:
  - reference
  - formatter
docID: xxx
--->

# Bytes Formatter (bytes)

The Bytes Formatter displays binary data as hexadecimal byte sequences.

## Supporting

This formatter supports:
- FORMAT_VALUE: Enables formatting of individual bytes or byte sequences.
- INLINE_CONFIGURATION: Permits inline configuration options (e.g., encoding=UTF-8) directly within the formatter string for custom display behavior.

## Supported signal types

The Bytes Formatter is intended for all byte signals.

## Inline Configuration

You can specify the number of bytes to display by adding it after the formatter identifier (e.g., `bytes/16` to show 16 bytes). Use `bytes/all` to display all bytes in the signal.

**Examples:**

- `bytes/16` — displays the first 16 bytes.
- `bytes/all` — displays all bytes.

## Known Limitations
No known limitations.
