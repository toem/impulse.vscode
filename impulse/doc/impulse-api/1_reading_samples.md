---
title: "Reading Samples"
author: "Thomas Haber"
keywords: [IReadableSamples, signal data, domain value, process types, signal types, sample structure, sample access, formatting, logic states, structured data, attachments, enumerations, discrete, continuous]
description: "Comprehensive guide to reading signal data in the impulse framework, covering interfaces hierarchy, signal characteristics, domain values, sample access methods, type-specific value handling, working with structured data, attachments, and enumerations. Includes examples for accessing, formatting, and analyzing signal samples with proper domain context."
category: "impulse-api"
tags:
  - api
  - tutorial
  - reference
docID: 994
---
![](images/hd_reading_samples.png) 
# Reading Samples

This guide provides a comprehensive overview of reading signal data within the impulse framework. The framework offers a rich hierarchy of interfaces designed for efficient and flexible data access, ranging from simple value retrieval to complex structural analysis.

## Introduction to Signals in impulse

Signals are a core concept in the impulse framework, representing data streams that can be analyzed, visualized, and processed. In the context of the impulse Viewer, signals exist within records, which are hierarchical structures that organize related signals and provide context for their interpretation.

A record primarily consists of signals and scopes (containers that organize signals into a structured hierarchy), but can also include other elements like proxies (references to signals), relations (connections between elements), and active record elements such as includes, analysis components, and interfaces to external sources.

When working with signals, understanding their structure, metadata, and the methods for accessing their data is essential for effective analysis and processing. Whether you're examining waveform files, log files, trace files, or combined data sources, the interfaces described in this guide provide the foundation for extracting meaningful information from signal data.

### Core Reading Interfaces

- **IReadableSamples**: The primary interface for accessing signal data, providing methods to retrieve samples, navigate through the signal, and access metadata.
- **IReadableValue**: Provides access to a single value with formatting and type conversion capabilities.
- **IReadableSample**: Represents a complete sample with both value and metadata.
- **IReadableGroup**: Represents a group of related samples as a single logical unit.
- **IReadableMembers**: Provides access to structured data with named or indexed members.

### Sample Definition Interfaces

- **ISample**: Defines the fundamental constants, data types, and encoding formats for samples. This interface provides the core specifications for sample representation, including byte-level format details, data type definitions, and constants for flags and masks.
- **ISamples**: Serves as the foundation for all signal sample collections, integrating various aspects of sample management like characteristics, validation, and events. This interface acts as a common base that is extended by both reading and writing interfaces.

### Characteristic Interfaces

- **ISampleCharacteristic**: Defines the essential properties of an individual sample, including its type, format, scale, and tags. This interface focuses on metadata that describes how sample data should be interpreted.
- **ISamplesCharacteristic**: Provides metadata for an entire collection of samples, including domain information, continuity, and release status. This interface is useful for understanding the nature of a signal without accessing individual values.


## Understanding Signal and Sample Characteristics

Before diving into the details of accessing and manipulating samples, it's important to understand the fundamental characteristics of signals and samples in the impulse framework.

Signals are much more than simple data arrays—they are rich data structures with metadata, contextual information, and complex behaviors that enable sophisticated analysis and visualization. A signal in impulse represents a sequence of samples organized along a domain (typically time), with each sample containing a value of a specific type plus optional metadata such as tags and attachments.

### Signal Structure

A signal in impulse can contain up to 2^31 samples, with the following key attributes:

- **Name**: A unique identifier for distinguishing the signal from others.
- **Description**: Detailed comments or notes that provide additional context about the signal.
- **Process Type**: Defines whether the signal is discrete or continuous.
- **Signal Type**: Categorizes the signal based on its data type.
- **Tags**: Provides metadata for context and categorization, such as "default", "state", "event", "transaction", "log", "chart", or "image".
- **Scale**: Represents the dimension of the signal (bit width, array size, etc.).
- **Format Specifier**: Defines how the signal's values are represented textually, such as binary, hexadecimal, or decimal.
- **Domain Base**: Defines the minimum distance between samples (e.g., nanoseconds, picoseconds).
- **Start and End**: Marks the beginning and end positions of the signal.
- **Rate**: Defines the sampling rate (for continuous signals).
- **Attachments**: Additional context like relations (links between samples in different signals) or labels (textual annotations).

Through the Signal dialog in the impulse Viewer, users can view and modify signal properties such as name, description, tags, and more, while also examining the signal's technical properties and raw sample data.

### Process Types

The process type of a signal is a fundamental characteristic that determines how samples are distributed along the domain and how position information is stored and interpreted. The impulse framework supports two types of signals:

- **Discrete Signals**: Have samples at irregular, event-based intervals. Each sample contains its own position information as a delta from its predecessor. Samples can occur at any domain position, with the restriction that positions must be non-decreasing. This process type is particularly useful for event-driven data where samples occur at significant moments rather than at regular intervals.

```java
// Check if a signal is discrete
boolean isDiscrete = !samples.isContinuous();

// For discrete signals, get the position of each sample individually
DomainLongValue pos = samples.positionAt(index);
```

- **Continuous Signals**: Have samples at regular intervals based on a fixed sampling rate. The position of each sample can be calculated from the start position and rate.

```java
// Check if a signal is continuous
boolean isContinuous = samples.isContinuous();

// For continuous signals, get the sampling rate
DomainLongValue samplingRate = samples.getRate();
```

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| `isContinuous()` | Checks if signal has regular sample intervals | None | `boolean` - true for continuous signals | Continuous signals have fixed rate between samples |
| `positionAt(int)` | Gets the domain position of a sample | `int` - Sample index | `DomainLongValue` - Position with domain context | Important for discrete signals where positions can vary |
| `getRate()` | Gets the sampling rate of the signal | None | `DomainLongValue` - Rate with domain context | Only meaningful for continuous signals |

### Signal Types

The signal type is a critical characteristic that defines the nature of the data represented by the signal and determines how samples should be interpreted and processed. The impulse framework supports several signal types to represent different kinds of data:

- **Logic**: Digital signals with 1 to N bits, representing binary states (0, 1, Z, X, etc.). These can be stored as 2-state, 4-state, or 16-state data, making them suitable for representing binary states in digital circuits.

- **Float**: Floating-point values, typically used for analog measurements, sensor readings, or mathematical computations. These can be 32-bit or 64-bit precision values.

- **Integer**: Whole numbers of any length, used for counters, IDs, or any data requiring precise integer values without fractional components.

- **Enumeration**: Named values with associated numeric representations, ideal for states or categories (e.g., "ON", "OFF", "IDLE"). Enumerations provide human-readable labels for numeric values.

- **Text**: String data, useful for labels, descriptions, or any text-based information that needs to be recorded in the signal.

- **Array**: Collections of elements of a specific type, such as Text, Binary, Integer, Float, or Enumeration. Arrays are useful for structured data like matrices or lists.

- **Structure**: Complex data types with named members of various types. Each member can be of type Text, Binary, Integer, Float, Enumeration, or Arrays. Structures are particularly useful for logs, transactions, charts, Gantt events, and other complex data representations.

- **Binary**: Raw binary data like images, files, or other non-textual data. This type is useful for handling data that doesn't fit into the other categories.

```java
// Get the signal type
int type = samples.getSampleType();

// Check for specific types
if (type == ISample.TYPE_LOGIC) {
    // Logic signal handling
} else if (type == ISample.TYPE_FLOAT) {
    // Float signal handling
} else if (type == ISample.TYPE_STRUCT) {
    // Structured data handling
}
```

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| `getSampleType()` | Gets the data type of samples in the signal | None | `int` - Type constant | Constants defined in ISample (TYPE_LOGIC, TYPE_FLOAT, etc.) |

### Signal Scale

The scale attribute has different meanings depending on the signal type:

- For **Logic Signals**: Represents the bit width (e.g., 1 for a single bit, 8 for a byte).
- For **Numeric Signals**: Can represent precision.
- For **Arrays**: Indicates dimensions.

```java
// Get the scale of a signal
int scale = samples.getScale();
```

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| `getScale()` | Gets the scale attribute of the signal | None | `int` - Scale value | Meaning depends on signal type (bit width, array dimensions, etc.) |

### Signal Domain

Every signal has a domain that defines the independent variable, typically time. The domain base establishes the unit of measurement and context for positions within the signal:

```java
// Get the domain base
IDomainBase domain = samples.getDomainBase();

// Get domain range
DomainLongValue start = samples.getStart();
DomainLongValue end = samples.getEnd();
```

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| `getDomainBase()` | Gets the domain base of the signal | None | `IDomainBase` - Domain base object | Defines the unit system (e.g., TimeBase, FreqBase) |
| `getStart()` | Gets the start position of the signal | None | `DomainLongValue` - Start position with domain context | Beginning of the signal's domain range |
| `getEnd()` | Gets the end position of the signal | None | `DomainLongValue` - End position with domain context | End of the signal's domain range |

#### Understanding getStart() vs getStartAsMultiple() (and the same for getEnd, getRate)

The impulse framework provides multiple methods for accessing domain positions:

```java
// Get the start position as a DomainValue object
DomainLongValue start = samples.getStart();
System.out.println("Start position: " + start.toString());  // e.g., "10 ns"

// Get the start position as a raw numeric multiple
long startMultiple = samples.getStartAsMultiple();
System.out.println("Start position multiple: " + startMultiple);  // e.g., 10

// The difference:
// - getStart() returns a full DomainValue object with unit context
// - getStartAsMultiple() returns just the numeric value without unit context
```

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| `getStart()` | Gets the start position as a domain value | None | `DomainLongValue` - Start with domain context | Complete object with unit information |
| `getStartAsMultiple()` | Gets the start position as raw multiple | None | `long` - Raw position multiple | Just the numeric component of the position |

The `getStart()` method returns a complete `DomainValue` object that knows about its domain base and can perform unit conversions, while `getStartAsMultiple()` returns just the raw numeric value (the "multiple" of the base unit).

#### Working with DomainValue

The `DomainValue` class is crucial for handling positions and intervals in signal processing. It provides rich functionality for unit conversion, mathematical operations, and formatting:

```java
// Creating domain values
DomainValue timeValue = DomainValue.parse("Time", "10ns");  // From domain class and string
DomainValue freqValue = DomainValue.parse("2.5MHz");        // From string with auto-detection
DomainValue customValue = DomainValue.valueOf(domain, 42);  // From domain base and multiple

// Converting between units
DomainValue microseconds = timeValue.convertTo(TimeBase.us);
System.out.println(timeValue + " = " + microseconds);  // "10 ns = 0.01 μs"

// Accessing the numeric value in different forms
long longVal = timeValue.longMultiple();       // As long
double doubleVal = timeValue.doubleMultiple(); // As double
BigDecimal bigVal = timeValue.bigMultiple();   // As BigDecimal

// Mathematical operations
DomainValue sum = timeValue.add(DomainValue.parse("5ns"));        // 15 ns
DomainValue diff = timeValue.sub(DomainValue.parse("2ns"));       // 8 ns
DomainValue doubled = timeValue.mul(2);                           // 20 ns
DomainValue scaled = timeValue.div(4);                            // 2.5 ns
Number ratio = timeValue.div(DomainValue.parse("2ns"), false);    // 5.0 (unitless)

// Comparing domain values
boolean isLess = timeValue.lt(DomainValue.parse("20ns"));         // true
boolean isGreater = timeValue.gt(DomainValue.parse("5ns"));       // true
boolean isEqual = timeValue.eq(DomainValue.parse("10ns"));        // true

// Formatting options
String defaultFormat = timeValue.toString();                     // "10 ns"
String withStyle = timeValue.toString(IDomainBase.STYLE_AUTO_UNIT);  // "10 ns"

```

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| `lt(DomainValue)` | Compares if this DomainValue is less than the specified value | `DomainValue` - The value to compare against | `boolean` - true if this value is less than parameter | Automatically converts units if bases differ |
| `gt(DomainValue)` | Compares if this DomainValue is greater than the specified value | `DomainValue` - The value to compare against | `boolean` - true if this value is greater than parameter | Automatically converts units if bases differ |
| `eq(DomainValue)` | Compares if this DomainValue is equal to the specified value | `DomainValue` - The value to compare against | `boolean` - true if values are equal | Performs precise comparison considering domain base |
| `toString()` | Converts the DomainValue to a string representation | None | `String` - The formatted string representation | Uses default formatting style |
| `toString(int)` | Converts the DomainValue to a string with specified style | `int` - Style constant from IDomainBase | `String` - The formatted string representation | Available styles include AUTO_UNIT, FIXED_UNIT, etc. |
|--------|-------------|------------|-------------|-------|
| `parse(String, String)` | Creates domain value from class and value strings | `String` - Domain class, `String` - Value with unit | `DomainValue` - The parsed domain value | For explicit domain class specification |
| `parse(String)` | Creates domain value from string with unit | `String` - Value with unit | `DomainValue` - The parsed domain value | Auto-detects domain class from unit |
| `valueOf(IDomainBase, long)` | Creates domain value from base and multiple | `IDomainBase` - Domain base, `long` - Multiple | `DomainValue` - The created domain value | Programmatic creation with explicit components |
| `convertTo(IDomainBase)` | Converts to different domain base | `IDomainBase` - Target base | `DomainValue` - Converted value | Maintains equivalent real-world quantity |
| `longMultiple()` | Gets the numeric multiple as long | None | `long` - The numeric multiple | Raw value without domain context |
| `doubleMultiple()` | Gets the numeric multiple as double | None | `double` - The numeric multiple | For fractional multiples |
| `bigMultiple()` | Gets the numeric multiple as BigDecimal | None | `BigDecimal` - The numeric multiple | For high-precision values |
|--------|-------------|------------|-------------|-------|
| `add(DomainValue)` | Adds another domain value | `DomainValue` - Value to add | `DomainValue` - Sum result | Handles unit conversion automatically |
| `sub(DomainValue)` | Subtracts another domain value | `DomainValue` - Value to subtract | `DomainValue` - Difference result | Handles unit conversion automatically |
| `mul(Number)` | Multiplies by a numeric value | `Number` - Multiplier | `DomainValue` - Product result | Scales the domain value |
| `div(Number)` | Divides by a numeric value | `Number` - Divisor | `DomainValue` - Quotient result | Scales the domain value |
| `div(DomainValue, boolean)` | Divides by another domain value | `DomainValue` - Divisor, `boolean` - Unit handling flag | `Number` - Unitless ratio | When false parameter, results in unitless ratio |

#### Unit Conversion with DomainValue

The `DomainValue` class provides several methods for unit conversion:

```java
// Converting an entire DomainValue to another base
DomainValue nsValue = DomainValue.parse("1μs");
DomainValue psValue = nsValue.convertTo(TimeBase.ps);
System.out.println(nsValue + " = " + psValue);  // "1 μs = 1000000 ps"

// Converting just the numeric component between bases
Number psNumber = nsValue.convertMultipleTo(TimeBase.ps, IDomainBase.CONVERT_ANY);
System.out.println("In picoseconds: " + psNumber);  // 1000000

// Conversion with explicit result type
double psDouble = nsValue.convertMultipleToDouble(TimeBase.ps, IDomainBase.CONVERT_ANY, 0.0);
long psLong = nsValue.convertMultipleToLong(TimeBase.ps, IDomainBase.CONVERT_ANY, 0);

```

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| `parse(String)` | Creates a DomainValue by parsing a string representation | `String` - Text representation including value and unit | `DomainValue` - The parsed domain value | Auto-detects domain class and base from unit |
| `convertTo(IDomainBase)` | Converts this DomainValue to an equivalent value with a different base | `IDomainBase` - Target domain base | `DomainValue` - New domain value with equivalent value but different base | Maintains the same real-world quantity |
| `convertMultipleTo(IDomainBase, int)` | Converts just the numeric part to a different base | `IDomainBase` - Target domain base, `int` - Conversion flags | `Number` - The converted numeric value | Flags control conversion behavior |
| `convertMultipleToDouble(IDomainBase, int, double)` | Converts numeric part to double with specific base | `IDomainBase` - Target base, `int` - Flags, `double` - Default value | `double` - Converted value as double | Returns default if conversion fails |
| `convertMultipleToLong(IDomainBase, int, long)` | Converts numeric part to long with specific base | `IDomainBase` - Target base, `int` - Flags, `long` - Default value | `long` - Converted value as long | Returns default if conversion fails |

By understanding these domain value operations, you can effectively work with signal positions, calculate intervals, and perform meaningful comparisons across different units and scales.

### Sample Structure

Each sample in a signal has several key attributes:

- **Value**: The actual data of the sample, which can be of any supported signal type.
- **Position**: The location of the sample in the signal's domain.
- **Tag**: Optional metadata that can mark samples as special or provide additional context.

```java
// Check if a sample is tagged
boolean isTagged = samples.isTaggedAt(index);

// Get the position of a sample
DomainLongValue position = samples.positionAt(index);

// Get the value of a sample (generic object)
Object value = samples.valueAt(index);
```

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| `isTaggedAt(int)` | Checks if a sample has a tag applied | `int` - Sample index (0-based) | `boolean` - true if the sample is tagged | Tags can mark special or important samples |
| `positionAt(int)` | Gets the domain position of a sample | `int` - Sample index (0-based) | `DomainLongValue` - Position with domain context | Provides complete position with unit information |
| `valueAt(int)` | Gets the actual data value of a sample | `int` - Sample index (0-based) | `Object` - The sample value in its native type | Return type depends on signal type (Logic, Number, String, etc.) |

### Sample Groups and Layers

Samples can be organized into logical groups and layers, which is particularly useful for representing transactions or multi-stage processes:

```java
// Check if a signal has groups
boolean hasGroups = samples.getGroups() > 0;

// Get the group a sample belongs to
int groupIndex = samples.groupAt(sampleIndex);

// Get the layer of a sample
int layer = samples.layerAt(sampleIndex);

// Get sample order within its group
int order = samples.orderAt(sampleIndex);
if (order == ISample.GO_INITIAL) {
    // First sample in a group
} else if (order == ISample.GO_FINAL) {
    // Last sample in a group
} else if (order == ISample.GO_INTER) {
    // Intermediate sample in a group
} else if (order == ISample.GO_SINGLE) {
    // Single-sample group
}
```

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| `getGroups()` | Gets the number of distinct groups in the signal | None | `int` - Count of groups in the signal | Returns 0 if no groups are defined |
| `groupAt(int)` | Gets the group ID that a sample belongs to | `int` - Sample index (0-based) | `int` - Group identifier | Samples in the same logical group have the same group ID |
| `layerAt(int)` | Gets the layer number of a sample | `int` - Sample index (0-based) | `int` - Layer number | Layers can represent hierarchy levels within groups |
| `orderAt(int)` | Gets the ordering position of a sample within its group | `int` - Sample index (0-based) | `int` - Order value (GO_INITIAL, GO_FINAL, GO_INTER, GO_SINGLE) | Indicates the position/role of the sample in its group sequence |

### Sample Attachments

Samples can have attachments that provide additional context:

- **Relations**: Create links between samples in different signals.
- **Labels**: Add textual or symbolic annotations to samples.

```java
// Get attachments for a sample
List<IAttachment> attachments = samples.attachmentsAt(sampleIndex);

// Get specific types of attachments
List<IAttachment> relations = samples.attachmentsAt(sampleIndex, ISample.AT_RELATION);
List<IAttachment> labels = samples.attachmentsAt(sampleIndex, ISample.AT_LABEL);

// Get attachments for a group
List<IAttachment> groupAttachments = samples.attachmentsAtGroup(groupIdx);
```

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| `attachmentsAt(int)` | Gets all attachments for a sample | `int` - Sample index | `List<IAttachment>` - All attachments | Returns empty list if none exist |
| `attachmentsAt(int, int)` | Gets attachments of specific type | `int` - Sample index, `int` - Attachment type | `List<IAttachment>` - Filtered attachments | Common types: AT_RELATION, AT_LABEL |
| `attachmentsAtGroup(int)` | Gets attachments for a group | `int` - Group index | `List<IAttachment>` - Group attachments | Group-level metadata and relationships |

## Accessing Signal Samples

The impulse framework provides a rich set of methods for accessing and navigating through signal data. Whether you need to retrieve individual samples, find samples at specific positions, or work with collections of related samples, the framework offers intuitive interfaces for these operations.

When working with signal data, you often need to:
- Navigate through samples sequentially
- Find samples at particular domain positions
- Extract values in appropriate types
- Handle different signal types consistently

The `IReadableSamples` interface serves as the foundation for these operations, providing methods that abstract away the complexity of the underlying data formats and access patterns. This makes it possible to write code that works seamlessly across different signal types and sources.

### Reading Individual Samples

The `IReadableSamples` interface provides several methods to access individual samples:

```java
// Get the number of samples in the signal
int count = samples.getCount();

// Access sample value at a specific index
Object value = samples.valueAt(10);

// Check if a sample is tagged
boolean isTagged = samples.isTaggedAt(5);

// Get a complete sample representation
IReadableSample sample = samples.compoundAt(3);
```

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| `getCount()` | Gets the total number of samples in the signal | None | `int` - Number of samples | Returns 0 for empty signals |
| `valueAt(int)` | Gets the value of a sample at a specific index | `int` - Sample index (0-based) | `Object` - The sample value | Return type depends on signal type |
| `isTaggedAt(int)` | Checks if a sample has a tag | `int` - Sample index (0-based) | `boolean` - true if tagged | Tagged samples are often highlighted in UI |
| `compoundAt(int)` | Gets a complete sample representation with all metadata | `int` - Sample index (0-based) | `IReadableSample` - Sample with value and metadata | Provides access to both value and contextual information |

### Finding Samples by Position

To locate samples at specific domain positions:

```java
// Find the sample index at a specific position (in multiple of the domain base)
int index = samples.indexAt(1000); 

// Get the position of a sample
DomainLongValue position = samples.positionAt(index);
String formatted = position.toString();  // e.g., "1.0 ms"

// Get the position as a raw multiple (without domain base information)
long positionMultiple = samples.multPosAt(index);
```

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| `indexAt(long)` | Finds sample index at a specific domain position | `long` - Position as multiple of domain base | `int` - Sample index or -1 if not found | Returns index of sample at or before the position |
| `positionAt(int)` | Gets the domain position of a sample | `int` - Sample index (0-based) | `DomainLongValue` - Position with domain context | Position includes both value and unit information |
| `multPosAt(int)` | Gets raw position multiple of a sample | `int` - Sample index (0-based) | `long` - Position as multiple of domain base | Position without domain base information |

## Working with Sample Values

Signal values can represent a wide variety of data types, from simple binary states to complex structured data. The impulse framework provides type-specific methods that allow you to work with each data type in its natural form, without having to worry about the underlying representation or conversion details.

This approach simplifies application code by providing direct access to values in their appropriate data types. Whether you're working with digital waveforms, analog measurements, text logs, or complex data structures, you can use consistent patterns to access and manipulate the data.

The framework also provides comprehensive formatting capabilities that enable you to convert values into human-readable string representations suitable for UI display, logging, or analysis.

### Type-Specific Value Access

The framework provides methods for accessing values in their appropriate types:

```java
// For digital/logic signals
Logic logic = samples.logicValueAt(index);
boolean isHigh = samples.isHighAt(index);
boolean isLow = samples.isLowAt(index);

// For numeric signals
double doubleVal = samples.doubleValueAt(index);
long longVal = samples.longValueAt(index);
int intVal = samples.intValueAt(index);

// For text signals
String textVal = samples.stringValueAt(index);

// For enum signals
Enumeration enumVal = samples.enumValueAt(index);

// For binary data
byte[] binaryData = samples.bytesValueAt(index);

// For structured data
Struct structData = samples.structValueAt(index);
```

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| `logicValueAt(int)` | Gets digital/logic value of a sample | `int` - Sample index | `Logic` - Logic state representation | For digital signals with states like 0, 1, Z, X |
| `isHighAt(int)` | Checks if a sample has logic high state | `int` - Sample index | `boolean` - true if logic high (1) | Convenience method for digital signals |
| `isLowAt(int)` | Checks if a sample has logic low state | `int` - Sample index | `boolean` - true if logic low (0) | Convenience method for digital signals |
| `doubleValueAt(int)` | Gets value as double-precision float | `int` - Sample index | `double` - Sample value as double | For numeric signals or type conversion |
| `longValueAt(int)` | Gets value as long integer | `int` - Sample index | `long` - Sample value as long | For integer signals or truncated conversion |
| `intValueAt(int)` | Gets value as 32-bit integer | `int` - Sample index | `int` - Sample value as int | May lose precision for large values |
| `stringValueAt(int)` | Gets value as text string | `int` - Sample index | `String` - Sample value as string | Native for text signals, formatted for others |
| `enumValueAt(int)` | Gets value as enumeration | `int` - Sample index | `Enumeration` - Named value | For enumeration type signals |
| `bytesValueAt(int)` | Gets value as byte array | `int` - Sample index | `byte[]` - Binary data | For binary data signals |
| `structValueAt(int)` | Gets value as structured data | `int` - Sample index | `Struct` - Complex data with fields | For structure type signals |

### Formatting Values

For formatted string representations of values:

```java
// Format using a specific format
String hexString = samples.formatAt(index, "hex");
String binaryString = samples.formatAt(index, "bin");
String decimalString = samples.formatAt(index, "dec");

// Convenience formatting methods
String hex = samples.fhexAt(index);
String dec = samples.fdecAt(index);
String bin = samples.fbinAt(index);
String ascii = samples.fasciiAt(index);
String oct = samples.foctAt(index);

// Get the default format for a sample
String defaultFormat = samples.defaultFormatAt(index);
```

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| `formatAt(int, String)` | Formats a sample value using the specified format | `int` - Sample index, `String` - Format name | `String` - Formatted representation | Common formats: "hex", "bin", "dec", etc. |
| `fhexAt(int)` | Formats a sample value in hexadecimal | `int` - Sample index | `String` - Hex representation | Convenience shortcut for `formatAt(index, "hex")` |
| `fdecAt(int)` | Formats a sample value in decimal | `int` - Sample index | `String` - Decimal representation | Convenience shortcut for `formatAt(index, "dec")` |
| `fbinAt(int)` | Formats a sample value in binary | `int` - Sample index | `String` - Binary representation | Convenience shortcut for `formatAt(index, "bin")` |
| `fasciiAt(int)` | Formats a sample value as ASCII | `int` - Sample index | `String` - ASCII representation | Useful for text representation of binary data |
| `foctAt(int)` | Formats a sample value in octal | `int` - Sample index | `String` - Octal representation | Convenience shortcut for `formatAt(index, "oct")` |
| `defaultFormatAt(int)` | Gets the default format for a sample | `int` - Sample index | `String` - Default format string | Based on signal type and metadata |

### Working with Logic States

For digital/logic signals, specialized methods are available:

```java
// Check for edges (transitions)
boolean isRisingEdge = samples.isEdgeAt(index, 1);
boolean isFallingEdge = samples.isEdgeAt(index, -1);
boolean isAnyEdge = samples.isEdgeAt(index, 0);

// Get detailed logic state
int logicState = samples.logicStateAt(index);
switch (logicState) {
    case ISample.STATE_0_BITS:  // Logic 0
        // Handle logic 0
        break;
    case ISample.STATE_1_BITS:  // Logic 1
        // Handle logic 1
        break;
    case ISample.STATE_Z_BITS:  // High impedance
        // Handle high-Z state
        break;
    // Other states: X, L, H, U, W, etc.
}

```

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| `isEdgeAt(int, int)` | Checks if a sample has a logic transition (edge) | `int` - Sample index, `int` - Edge direction | `boolean` - true if edge detected | Edge direction: 1 (rising), -1 (falling), 0 (any) |
| `logicStateAt(int)` | Gets the detailed logic state of a digital sample | `int` - Sample index | `int` - Logic state constant | Common states: STATE_0_BITS (0), STATE_1_BITS (1), STATE_Z_BITS (Z), etc. |

## Working with Structured Data

Structured data represents complex information models with named fields or members, similar to structs in C/C++ or objects in higher-level languages. In the impulse framework, structured signals can represent logs, transactions, events, and other complex data types that go beyond simple scalar values.

The key advantage of structured data is the ability to maintain relationships between related pieces of information while preserving their individual semantics. For example, a transaction event might include fields for a transaction ID, timestamp, status code, and message text. With structured data, all these fields remain accessible individually while still being treated as a cohesive unit.

The framework provides interfaces like `IReadableMembers` that allow you to:
- Access individual members by name or index
- Retrieve type-appropriate values for each member
- Format members according to their specific requirements
- Explore the structure of complex data types
- Apply consistent operations across different structured data models

Working with structured data effectively is especially important for analyzing complex systems, where the relationships between different pieces of information are as important as the individual values themselves.

### Accessing Struct Members

For structured data types, you can access individual members:

```java
// Get a structured sample
IReadableSample sample = samples.compoundAt(index);

// Using IReadableMembers interface
double numericField = sample.doubleValueOf("fieldName");
String textField = sample.stringValueOf("anotherField");
boolean boolField = sample.booleanValueOf("flagField");
```

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| `compoundAt(int)` | Gets a complete sample with all metadata | `int` - Sample index | `IReadableSample` - Complete sample object | Access point for structured data |
| `doubleValueOf(String)` | Gets a numeric member's value as double | `String` - Member name | `double` - Member value | For numeric fields in structures |
| `stringValueOf(String)` | Gets a text member's value as string | `String` - Member name | `String` - Member value | For text fields in structures |
| `booleanValueOf(String)` | Gets a boolean member's value | `String` - Member name | `boolean` - Member value | For flag fields in structures |

### Formatting Struct Members

Format individual members for display:

```java
// Format a member using a specific format
String formattedValue = sample.formatOf("address", "hex");

// Convenience formatting methods for members
String hexField = sample.fhexOf("address");
String decField = sample.fdecOf("count");
String binField = sample.fbinOf("flags");
```

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| `formatOf(String, String)` | Formats a struct member using specified format | `String` - Member name, `String` - Format name | `String` - Formatted representation | Applies specific formatting to a member |
| `fhexOf(String)` | Formats a struct member in hexadecimal | `String` - Member name | `String` - Hex representation | Shortcut for `formatOf(member, "hex")` |
| `fdecOf(String)` | Formats a struct member in decimal | `String` - Member name | `String` - Decimal representation | Shortcut for `formatOf(member, "dec")` |
| `fbinOf(String)` | Formats a struct member in binary | `String` - Member name | `String` - Binary representation | Shortcut for `formatOf(member, "bin")` |

### Working with Member Descriptors

Explore the structure of complex data types:

```java
// Get all member descriptors for a signal
List<IMemberDescriptor> allMembers = samples.getMemberDescriptors();

// Get a specific member descriptor
IMemberDescriptor memberDesc = samples.getMemberDescriptor("fieldName");
// or by ID
IMemberDescriptor memberById = samples.getMemberDescriptor(42);

// Get information about the member
String name = memberDesc.getName();
String path = memberDesc.getPath();
int type = memberDesc.getType();
String format = memberDesc.getFormat();

// Find members with specific tags
List<IMemberDescriptor> criticalMembers = samples.getMembersForTag("critical");
```

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| `getMemberDescriptors()` | Gets all available member descriptors | None | `List<IMemberDescriptor>` - All member descriptors | For exploring structure layout |
| `getMemberDescriptor(String)` | Gets descriptor for specific member by name | `String` - Member name | `IMemberDescriptor` - Member descriptor | Provides metadata about the member |
| `getMemberDescriptor(int)` | Gets descriptor for specific member by ID | `int` - Member ID | `IMemberDescriptor` - Member descriptor | Alternative access by numeric identifier |
| `getName()` | Gets the name of the member | None | `String` - Member name | From an IMemberDescriptor object |
| `getPath()` | Gets the full path of the member | None | `String` - Member path | Path may include parent structures |
| `getType()` | Gets the data type of the member | None | `int` - Type constant | Type constants defined in ISample |
| `getFormat()` | Gets the default format of the member | None | `String` - Format string | Default presentation format |
| `getMembersForTag(String)` | Finds members with a specific tag | `String` - Tag name | `List<IMemberDescriptor>` - Tagged members | For finding members with certain properties |

## Working with Attachments

Attachments in the impulse framework provide a powerful mechanism for adding context, metadata, and relationships to signal samples. Unlike the core sample data that represents the primary measurements or states, attachments represent secondary information that enhances the interpretation and analysis of the signal data.

There are two primary types of attachments supported by the framework:

**Relations**: Create links between samples in different signals, allowing you to establish cause-and-effect relationships, hierarchical connections, or any other association between signals. Relations are particularly valuable for:
   - Tracing the flow of data through a system
   - Connecting requests with their corresponding responses
   - Establishing parent-child relationships between events
   - Creating visual connections between related signals in the UI

**Labels**: Add textual or symbolic annotations to specific samples, providing additional descriptive information, commentary, or categorization. Labels are useful for:
   - Marking significant events or anomalies
   - Adding human-readable explanations to complex patterns
   - Creating markers for navigation or reference
   - Highlighting samples that meet specific criteria

Attachments can be associated with individual samples or with entire groups of samples, making them extremely flexible for different analysis scenarios. The ability to attach contextual information to signals without modifying the underlying data makes it possible to layer multiple interpretations onto the same signal data.

### Retrieving Attachments

Attachments provide additional context for samples and groups:

```java
// Get all attachments for a sample
List<IAttachment> attachments = samples.attachmentsAt(sampleIndex);

// Get attachments of a specific type
List<IAttachment> relations = samples.attachmentsAt(sampleIndex, ISample.AT_RELATION);
List<IAttachment> labels = samples.attachmentsAt(sampleIndex, ISample.AT_LABEL);

// Get attachments for a group
List<IAttachment> groupAttachments = samples.attachmentsAtGroup(groupIdx);
```

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| `attachmentsAt(int)` | Gets all attachments for a sample | `int` - Sample index | `List<IAttachment>` - All attachments | Returns empty list if none exist |
| `attachmentsAt(int, int)` | Gets attachments of specific type | `int` - Sample index, `int` - Attachment type | `List<IAttachment>` - Filtered attachments | Common types: AT_RELATION, AT_LABEL |
| `attachmentsAtGroup(int)` | Gets attachments for a group | `int` - Group index | `List<IAttachment>` - Group attachments | Group-level metadata and relationships |

### Working with Specific Attachment Types

Different attachment types serve different purposes and provide different information:

```java
// Process relations (links between samples/signals)
for (IAttachment attachment : relations) {
    if (attachment instanceof IAttachment.IAttachedRelation) {
        IAttachment.IAttachedRelation relation = (IAttachment.IAttachedRelation) attachment;
        
        // Access relationship information
        String targetId = relation.getTargetId();
        String style = relation.getStyle();
        
        // Get positions
        DomainValue sourcePos = relation.getSourcePosition();
        DomainValue targetPos = relation.getTargetPosition();
        DomainValue absoluteTargetPos = relation.getAbsoluteTargetPosition();
        
        // Check relationship properties
        boolean isReverse = relation.isReverse();
        boolean isDelta = relation.isDelta();
       
    }
}

// Process labels (textual annotations)
for (IAttachment attachment : labels) {
    if (attachment instanceof IAttachment.IAttachedLabel) {
        IAttachment.IAttachedLabel label = (IAttachment.IAttachedLabel) attachment;
        
        // Access label information
        String message = label.getMessage();
        String style = label.getStyle();
        
        // Get label position
        DomainValue position = label.getSourcePosition();       
    }
}
```

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| **For Relations:** |||||
| `getTargetId()` | Gets the ID of the related target | None | `String` - Target identifier | Identifies the related signal or entity |
| `getStyle()` | Gets the style of the relationship | None | `String` - Style identifier | Defines visual appearance and semantic meaning |
| `getSourcePosition()` | Gets position of source | None | `DomainValue` - Source position | Where the relationship starts |
| `getTargetPosition()` | Gets position of target relative to source | None | `DomainValue` - Relative target position | May be delta or absolute depending on type |
| `getAbsoluteTargetPosition()` | Gets absolute position of target | None | `DomainValue` - Absolute target position | Always gives absolute position regardless of type |
| `isReverse()` | Checks if this is a reverse relationship | None | `boolean` - true if reverse | Reverse relations point from target to source |
| `isDelta()` | Checks if target position is relative | None | `boolean` - true if delta | When true, position is relative to source |
| **For Labels:** |||||
| `getMessage()` | Gets the text of the label | None | `String` - Label text | The annotation message |
| `getStyle()` | Gets the style of the label | None | `String` - Style identifier | Defines visual appearance |
| `getSourcePosition()` | Gets position of the label | None | `DomainValue` - Label position | Where the label is placed |

## Working with Enumerations

Enumerations are a powerful feature in the impulse framework that provide a mapping between numeric values and meaningful textual labels. This mapping creates a layer of abstraction that makes signals more intuitive to work with and analyze, especially when dealing with state-based or categorical data.

In many digital systems, states and modes are represented internally as numeric values (like 0, 1, 2, etc.), but these values have specific semantic meanings in the context of the system (like "IDLE", "RUNNING", "ERROR"). Enumerations bridge this gap by maintaining both the efficient numeric representation and the human-readable label.

The benefits of enumerations include:

1. **Improved Readability**: Instead of cryptic numeric codes, signals display meaningful state names.
2. **Consistent Interpretation**: Ensures that everyone analyzing the data uses the same terminology.
3. **Simplified Filtering**: Makes it easier to search for specific states or transitions.
4. **Enhanced Visualization**: Enables color-coding and styling based on state values.
5. **Domain Context Preservation**: Maintains the original design terminology from the system being analyzed.

Enumerations can be global (applicable to multiple signals) or local to specific struct members, providing flexibility in how they're applied across a record. They're particularly valuable when analyzing protocols, state machines, or any system with discrete operational modes.

```java
// Get all enumerations of a specific type
```

```java
// Get all enumerations of a specific type
List<Enumeration> globalEnums = samples.getEnums(ISample.ENUM_GLOBAL);
List<Enumeration> relationTargets = samples.getEnums(ISample.ENUM_RELATION_TARGET);
List<Enumeration> relationStyles = samples.getEnums(ISample.ENUM_RELATION_STYLE);

// Get enumerations for a specific member
List<Enumeration> memberEnums = samples.getMemberEnums("stateField");

// Look up an enumeration by label
Enumeration runningEnum = samples.getMemberEnum("stateField", "RUNNING");

// Look up an enumeration by value
Enumeration stateOne = samples.getMemberEnum("stateField", 1);
```

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| `getEnums(int)` | Gets all enumerations of a specific type | `int` - Enumeration type constant | `List<Enumeration>` - All enumerations of that type | Common types: ENUM_GLOBAL, ENUM_RELATION_TARGET, etc. |
| `getMemberEnums(String)` | Gets enumerations for a specific struct member | `String` - Member name | `List<Enumeration>` - Member's enumerations | Lists all possible values for that member |
| `getMemberEnum(String, String)` | Looks up an enumeration by member and label | `String` - Member name, `String` - Enum label | `Enumeration` - The found enumeration | Converts from label to enumeration object |
| `getMemberEnum(String, int)` | Looks up an enumeration by member and value | `String` - Member name, `int` - Enum value | `Enumeration` - The found enumeration | Converts from numeric value to enumeration object |

## Conclusion

The impulse framework provides a comprehensive set of interfaces and methods for working with signal data, enabling developers to efficiently extract, analyze, and visualize information from complex signals. By understanding the concepts and techniques covered in this guide, you can build powerful applications that leverage the full capabilities of the impulse signal processing ecosystem.

Key takeaways from this guide include:

1. **Rich Data Representation**: Signals in impulse are much more than simple arrays of values; they include metadata, domain context, and structural information that enables sophisticated analysis.

2. **Type-Specific Access**: The framework provides specialized methods for working with each signal type, making it easy to handle anything from simple binary logic to complex structured data.

3. **Domain Context**: All position and interval information maintains its connection to the underlying domain (like time or frequency), ensuring accurate calculations and meaningful comparisons.

4. **Structural Analysis**: For complex data types, the framework offers powerful interfaces for exploring and navigating structure layouts and accessing nested information.

5. **Contextual Attachments**: Relations and labels provide additional layers of meaning and connection that enhance the interpretability of signal data.

6. **Flexible Formatting**: Comprehensive formatting capabilities make it easy to convert raw values into human-readable representations suitable for different analysis contexts.

Whether you're building tools for semiconductor debugging, communications protocol analysis, sensor data processing, or any other signal-focused application, the impulse reading interfaces provide a solid foundation for your development efforts. By leveraging the appropriate interfaces and methods for your specific use case, you can create efficient, maintainable code that delivers powerful analysis capabilities.

For more information about working with samples, refer to:
- Reading: [Iterating Samples](../impulse-api/2_iterating-samples.md)
- Writing: [Writing Samples](../impulse-api/3_writing-samples.md)  and [Building Records](../impulse-api/4_building-records.md)