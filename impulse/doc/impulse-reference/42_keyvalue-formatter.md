<!---
title: "KeyValue Formatter"
author: "Thomas Haber"
keywords: [impulse, formatter, keyvalue, struct, format]
description: "Formatter that displays structured values as key:value pairs for inspection and reporting."
category: "impulse-reference"
tags:
  - reference
  - formatter
docID: xxx
--->

# KeyValue Formatter (keyValues)

The KeyValue Formatter displays structured values as key:value pairs, showing member names and their corresponding values. It is intended for struct and array signals where clarity and detail are important for inspection or reporting.

## Supporting

This formatter supports:
- FORMAT_VALUE: Enables formatting of individual member values.
- INLINE_CONFIGURATION: Permits inline configuration options (e.g., keyValues/A,B) directly within the formatter string for custom display behavior.

## Supported signal types

The KeyValue Formatter is intended for all struct and array signals.

## Inline Configuration
You can provide a comma-separated list keys that shall be included in the formatted value. 
Specify these after the formatter identifier using the format `keyValues/A,B,...`.

## Known Limitations
No known limitations.
