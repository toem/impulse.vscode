<!---
title: "Label Formatter"
author: "Thomas Haber"
keywords: [impulse, formatter, label, enumeration, format]
description: "Formatter that uses defined labels for enumerations or mapped values instead of raw numeric values."
category: "impulse-reference"
tags:
  - reference
  - formatter
docID: xxx
--->

# Label Formatter (label)

The Label Formatter uses defined labels for enumerations or mapped values, displaying the label instead of the raw numeric value. 

## Supporting

This formatter supports:
- FORMAT_VALUE: Enables formatting of individual sample values using labels.
- INLINE_CONFIGURATION: Permits inline configuration options (e.g., label/0:hi) directly within the formatter string for custom display behavior.

## Supported signal types

The Label Formatter is intended for all enumeration, number and logic signals.

## Inline Configuration
You can provide a comma-separated list of value:label pairs to define custom labels for values. 
Specify these after the formatter identifier using the format `label/val1:label1,val2:label2,...`.

**Example:**

- `label/1:first,2:second` — value 1 will be displayed as "first", value 2 as "second".

## Known Limitations
No known limitations.
