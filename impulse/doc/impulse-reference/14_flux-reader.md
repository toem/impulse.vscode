
<!---
title: "flux Trace Reader"
author: "Thomas Haber"
keywords: [flux, trace, impulse, binary, serializer, reader, simulation, measurement, debugging, compression, buffer, handler, multi-core, semiconductor, signal, scope, hierarchical, extension]
description: "The flux Trace Reader extension for impulse enables efficient import and analysis of binary flux trace files, supporting scalable compression, hierarchical signal and scope organization, and integration with impulse's visualization and processing tools. Designed for semiconductor and multi-core embedded system use-cases."
category: "impulse-reference"
tags:
  - reference
  - serializer
docID: xxx
--->

# flux Trace Reader

The flux reader supports the open, binary flux trace format, designed for semiconductor and multi-core embedded system use-cases. Flux traces efficiently store signals and scopes in a hierarchical structure, using scalable compression and chunked binary entries for both definitions and value changes. The format enables fast, low-memory access to large datasets, supporting features like multiple traces, flexible buffers, and custom handlers for output and compression. With impulse, flux traces can be visualized, analyzed, and processed, making them ideal for simulation, measurement, and debugging workflows.

## Supporting

This serializer supports:
- PROPERTIES: Provides options to customize serialisation behavior, filtering, and output attributes for serializers.
- CONFIGURATION: The serializer supports configuration management, allowing users to add and select configurations to override default name patterns and properties. 

## Properties

**Logging and Diagnostics Properties**
The parser integrates with impulse's console logging system, providing configurable verbosity levels for diagnostic output during the import process. Console properties control the level of detail in parsing progress reports, timing statistics, and error information.

## Format

The format description is under construction.

## Known Limitation
No known limitations.