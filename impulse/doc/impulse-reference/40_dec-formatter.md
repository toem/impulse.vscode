<!---
title: "Dec Formatter"
author: "Thomas Haber"
keywords: [impulse, formatter, decimal, samples, format]
description: "Formatter that displays numeric sample values in decimal notation with configurable formatting options."
category: "impulse-reference"
tags:
  - reference
  - formatter
docID: xxx
--->

# Dec Formatter (dec)


The Dec Formatter displays numeric sample values in decimal representation. It is intended for integer, float, and logic signals where a standard, radix-10 view of values is preferred for inspection, debugging, or reporting.

## Supporting

This formatter supports:
- FORMAT_VALUE: Enables formatting of individual sample values.
- INLINE_CONFIGURATION: Permits inline configuration options (e.g., dec/###.###) directly within the formatter string for custom display behavior.

## Supported signal types

The Dec Formatter is intended for all number, float, byte and logic signals.

## Inline Configuration

The Dec Formatter supports inline configuration using pattern strings to control number formatting. 
To use the Dec Formatter with a custom pattern, specify `dec/pattern` (e.g., `dec/###.###`).
The following symbols are supported:

- `0` A digit, always displayed (shows 0 if number has fewer digits)
- `#` A digit, leading zeroes omitted
- `.` Marks decimal separator
- `,` Marks grouping separator (e.g., thousands)
- `E` Separates mantissa and exponent for exponential formats
- `;` Separates positive and negative formats
- `-` Marks the negative number prefix
- `%` Multiplies by 100 and shows number as percentage
- `?` Multiplies by 1000 and shows number as per mille
- `¤` Currency sign (replaced by locale currency symbol); `¤¤` uses international monetary symbols
- `X` Marks a character to be used in number prefix or suffix
- `'` Quotes special characters in prefix or suffix

**Examples:**

| Pattern         | Number      | Output        |
|----------------|------------|--------------|
| ###.###        | 123.456    | 123.456      |
| ###.#          | 123.456    | 123.5        |
| ###,###.##     | 123456.789 | 123,456.79   |
| 000.###        | 9.95       | 009.95       |
| ##0.###        | 0.95       | 0.95         |


## Known Limitations
No known limitations.
