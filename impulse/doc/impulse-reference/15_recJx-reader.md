<!---
title: "Signal Expression Reader (recJx)"
author: "Thomas Haber"
keywords: [impulse, recJx, signal expression, reader, reference, example, scripting, test vector, record, expression syntax, signal manipulation, filter, processor]
description: "The Signal Expression Reader (recJx) extension for impulse enables users to generate reference or example records using the impulse expression syntax. Supports flexible scripting of signals, test vectors, and custom record structures for analysis and demonstration."
category: "impulse-reference"
tags:
  - reference
  - serializer
docID: xxx
--->

# Signal Expression Reader (recJx)

The Signal Expression Reader (recJx) enables users to generate reference or example records using the powerful impulse expression syntax. This reader interprets expressions as described in the impulse manual, allowing flexible creation of signals, test vectors, and custom record structures. It is ideal for scripting signal references, defining test scenarios, and producing example datasets for analysis and demonstration purposes.

Refer to the [Expressions Manual](../impulse-manual/10_expressions.md) for a comprehensive guide to the expression language and its capabilities.

## Supporting

This serializer supports:
- PROPERTIES: Provides options to customize serialisation behavior, filtering, and output attributes for serializers.
- CONFIGURATION: The serializer supports configuration management, allowing users to add and select configurations to override default name patterns and properties. 

## Properties

**Logging and Diagnostics Properties**
The parser integrates with impulse's console logging system, providing configurable verbosity levels for diagnostic output during the import process. Console properties control the level of detail in parsing progress reports, timing statistics, and error information.

## Format

Records are defined using the recJx format, which consists of signal definitions and associated expressions. Each signal or test vector is described by an expression, which is evaluated to generate the corresponding data. The format supports:

- Mathematical calculations
- Conditional logic
- Time domain operations
- Logic operations
- String manipulation
- Multiple operations and assignments

Below is a real-world example demonstrating how to initialize a record, create scopes and signals, and generate sample data using impulse's expression syntax. This practical code sample shows how to script integer, struct, and float array signals, and write a sine wave pattern to a signal:

```java
// Init the record with name and time base (nanoseconds)
producer.initRecord("Example Record",TimeBase.ns);

// Create a scope and add three signals: integer, struct, and float array
var signals = producer.addScope(null, "External Signals");
var intsig = producer.addSignal(signals, "Sin", null,null, ISample.DATA_TYPE_INTEGER,-1,ISample.FORMAT_DEFAULT);
var struct = producer.addSignal(signals, "Struct", null,null,ISample.DATA_TYPE_STRUCT, -1,ISample.FORMAT_DEFAULT,IndexBase.n);
var struct2 = producer.addSignal(signals, "Struct2", null,null,ISample.DATA_TYPE_STRUCT, -1,ISample.FORMAT_DEFAULT,IndexBase.n);
var struct3 = producer.addSignal(signals, "Struct3", null,null,ISample.DATA_TYPE_STRUCT, -1,ISample.FORMAT_DEFAULT,IndexBase.n);
var floatArray = producer.addSignal(signals, "XY", null,null, ISample.DATA_TYPE_FLOAT_ARRAY, 2,ISample.FORMAT_DEFAULT);

// Start writing samples at 0 ns
var t = 0l; // 0 ns
producer.open(t);

// Write integer signal ("Sin") with a sine wave pattern
var writer  = producer.getWriter(intsig);
writer.writeInt(t, false, 0);
t=14000l;
writer.writeInt(t, false, 0);
while (t< 94000) {
    // Write time as integer value (sine wave)
    writer.writeInt(t, false, (int)(100.0 * Math.sin(1.0 * t/1000.0)));
    t = t + 10;
}
writer.writeInt(t, false, 0);
writer.writeInt(100000l, false, 0);
```


## Known Limitations

- The reader relies on the impulse expression interpreter; unsupported features or syntax may result in errors.
- Complex expressions may require careful type handling and parentheses for correct evaluation.

