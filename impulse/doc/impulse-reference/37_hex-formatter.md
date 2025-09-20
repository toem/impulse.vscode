<!-- filepath: /home/thomas/Workspaces/impulse/de.toem.impulse.documentation/impulse-reference/37_hex-formatter.md -->
<!---
title: "Hex Formatter"
author: "Thomas Haber"
keywords: [impulse, formatter, hex, samples, format]
description: "Formatter that displays numeric sample values in hexadecimal notation with configurable width and casing."
category: "impulse-reference"
tags:
  - reference
  - formatter
docID: xxx
--->

# Hex Formatter (hex)

The Hex Formatter displays numeric sample values in hexadecimal representation. It is intended for integer and logic signals where a compact, radix-16 view of values is preferred for inspection, debugging, or protocol analysis.

## Supporting

This formatter supports:
- PROPERTIES: Provides options to customize formatting behavior, filtering, and output attributes.
- FORMAT_VALUE: Enables formatting of individual sample values.

## Supported signal types

The Hex Formatter is intended for all number, byte and logic signals.

## Properties

- **Prefix**: Show or hide the "0x" prefix before hex values.
- **Lowercase**: If enabled, renders hex letters in lowercase (a..f); if disabled, uses uppercase (A..F).

## Known Limitations
No known limitations.
