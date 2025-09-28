<!---
title: "ValuesOnly Formatter"
author: "Thomas Haber"
keywords: [impulse, formatter, valuesonly, compact, format]
description: "Formatter that displays only raw values without keys or labels for compact output."
category: "impulse-reference"
tags:
  - reference
  - formatter
docID: xxx
--->

# ValuesOnly Formatter (values)

The ValuesOnly Formatter displays only raw values, omitting labels.

## Supporting

This formatter supports:
- FORMAT_VALUE: Enables formatting of individual sample values.
- INLINE_CONFIGURATION: Permits inline configuration options (e.g., values/A,B) directly within the formatter string for custom display behavior.

## Supported signal types

The ValuesOnly Formatter is intended for struct and array signals.

## Inline Configuration
You can provide a comma-separated list keys that shall be included in the formatted value. 
Specify these after the formatter identifier using the format `values/A,B,...`.


## Known Limitations
No known limitations.
