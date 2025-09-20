<!---
title: "Expression Search Engine"
author: "Thomas Haber"
keywords: [search, samples, criteria, signals, search engine, expression, configuration, wrap search]
description: "The Expression Search Engine enables users to locate samples matching custom expressions within selected signals. Supports flexible criteria, configuration, and wrap search for efficient data analysis."
category: "impulse-reference"
tags:
  - reference
  - search
  - engine
docID: xxx
--->

# Expression Search Engine

The Expression Search Engine allows users to search for samples that match a user-defined expression within selected signals. This engine is ideal for locating specific values, patterns, or conditions in large datasets, supporting both simple and complex search criteria.

For a comprehensive guide to the expression language, see the [Expressions Manual](../impulse-manual/10_expressions.md). For details on using the search dialog and search workflow, see the [Search Manual](../impulse-manual/9_search.md).

In search expressions, selected sources are referenced as `s0`, `s1`, etc. For example, `*s0 > 5 && *s1 < 10` compares the first and second selected signals.

## Supporting

This search engine supports:

This search engine supports:
- **MULTIPLE_SOURCES**: Allows searching across multiple signals or data sources simultaneously.
- **PROPERTIES**: Provides options to customize search criteria, filtering, and output attributes for search operations.
- **CONFIGURATION**: Enables users to create, save, and select named configurations for different search setups and criteria.


## Configuration & Properties

- **Search Expression**: Enter a valid impulse expression to define the search criteria (see [Expressions Manual](../impulse-manual/10_expressions.md)).
- **Logging and Diagnostics Properties**: The parser integrates with impulse's console logging system, providing configurable verbosity levels for diagnostic output during the import process. Console properties control the level of detail in parsing progress reports, timing statistics, and error information.


## Known Limitations
No known limitations.
