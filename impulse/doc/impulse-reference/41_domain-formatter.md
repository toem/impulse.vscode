<!---
title: "Domain Formatter"
author: "Thomas Haber"
keywords: [impulse, formatter, domain, time, index, units, format]
description: "Formatter that displays domain (time/index) values using preferred units and formatting."
category: "impulse-reference"
tags:
  - reference
  - formatter
docID: xxx
--->

# Domain Formatter (domain, ns, ms, .. (any domainBase))

The Domain Formatter displays domain values (such as time or index) using preferred units and formatting options. It is intended for signals where domain representation is important for analysis, visualization, or reporting.

To select the Domain Formatter, use `domain` or any domain base (e.g.,  `ms`, `ns`, ...).

## Supporting

This formatter supports:
- FORMAT_VALUE: Enables formatting of individual domain values.
- INLINE_CONFIGURATION: Permits inline configuration options (e.g., ms/auto) directly within the formatter string for custom display behavior.

## Supported signal types

The Domain Formatter is intended for all domain signals (time, index, etc.).

## Inline Configuration

You can specify the formatting style using the following options after the formatter identifier:

- `preffered`: Use the preferred unit and formatting for the domain value.
- `auto`: Automatically select the most suitable unit and formatting based on the value.
- `raw`: Display the raw domain value without conversion.

**Examples:**

- `domain/preffered` — Formats a domain value (including domain base information) using preferred units (e.g., ms, s).
- `ms/auto` — Assumes numerical value to be in ms and then automatically selects the best unit.
- `ns/raw` — Assumes numerical value to be in ns and formats without conversion.

## Known Limitations
No known limitations.
