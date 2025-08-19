---
title: "Iterating Samples"
author: "Thomas Haber"
keywords: [pointers, iterators, navigation, sample traversal, cursor positioning, edge detection, bidirectional iteration, domain values, signal analysis, sample access, group iteration, performance optimization, logic detection, sample positioning, synchronized analysis]
description: "Comprehensive exploration of the impulse framework's sample iteration capabilities, covering pointer-based navigation with IPointer, ISamplePointer, and IGroupPointer interfaces, and bidirectional iteration with ISamplePointerIterator. Details methods for sample positioning, traversal, edge detection, and data access across both discrete and continuous signals with practical examples and performance considerations."
category: "impulse-api"
tags:
  - api
  - tutorial
  - programming
docID: 996
---
![](images/hd_iterating_samples.png) 
# Iterating Samples

This guide provides a comprehensive overview of iterating through signal data within the impulse framework. The framework offers a rich set of interfaces designed for efficient and flexible navigation through signal data, from simple positioning to complex traversal patterns.

## Introduction to Sample Iteration

Iteration is a core concept in the impulse framework, enabling flexible navigation through signal data streams. The framework provides two main paradigms for traversing samples: pointers and iterators.

Pointers act as persistent cursors that maintain a position within a signal, allowing for interactive navigation and direct access to samples at specific positions. Iterators, on the other hand, facilitate systematic traversal through a specified range of samples, supporting both forward and backward movement.

These complementary approaches provide developers with the tools needed for efficient and flexible signal analysis, whether for interactive visualization applications or automated processing algorithms.

## Core Iterating Interfaces

- **IPointer**: Provides navigable cursor functionality for samples.
- **ISamplePointer**: Combines reading capabilities with navigation functionality, acting as a cursor that maintains a position within a signal. Unlike iterators, pointers represent a persistent location, making them ideal for interactive navigation and algorithms that need to scan through data.
- **IGroupPointer**: Extends the pointer concept to logical groups, allowing navigation through groups of related samples while accessing their combined properties and values.
- **ISamplePointerIterator**: Provides bidirectional iteration over samples within a domain range, supporting both forward and backward traversal.

## Internal Iterating Interfaces

- **ISampleIterator**: Facilitates iteration through samples in a specified range.
- **IGroupIterator**: Facilitates iteration through logical groups in a specified range.

## Navigation with Pointers

Pointers in the impulse framework serve as persistent cursors that maintain a position within a signal, providing both navigation capabilities and direct access to sample data at the current position. They combine the functionality of readers (accessing data) with navigators (moving through data), making them ideal for interactive applications and algorithms that need to scan through data.

### Creating and Positioning Pointers

Pointers provide cursor-like navigation through samples:

```java
// Create a pointer for a signal
IPointer pointer = new SamplePointer(samples);

// Position the pointer
pointer.goPoint(10);  // by index
pointer.setPosition(1000);  // by domain position
pointer.setPosition(DomainValue.valueOf("10ms"));  // with explicit domain value

// Basic navigation
pointer.goMin();  // Go to first sample
pointer.goMax();  // Go to last sample
pointer.goNext();  // Move to next sample
pointer.goPrev();  // Move to previous sample

// Check navigation options
boolean canGoForward = pointer.hasNext();
boolean canGoBack = pointer.hasPrev();
```

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| `goPoint(int)` | Positions pointer at specified sample index | `int` - Sample index | `void` | May throw IndexOutOfBoundsException for invalid indices |
| `setPosition(long)` | Positions pointer at specified domain multiple | `long` - Domain position as multiple | `void` | Sets pointer to nearest sample with appropriate delta if not exact |
| `setPosition(DomainValue)` | Positions pointer using domain value | `DomainValue` - Domain position | `void` | Sets pointer to nearest sample with appropriate delta if not exact |
| `goMin()` | Moves to first sample | None | `boolean` | Returns true if position changed, false if already at first sample |
| `goMax()` | Moves to last sample | None | `boolean` | Returns true if position changed, false if already at last sample |
| `goNext()` | Moves to next sample | None | `boolean` | Returns false if already at last sample |
| `goPrev()` | Moves to previous sample | None | `boolean` | Returns false if already at first sample |
| `hasNext()` | Checks if next sample exists | None | `boolean` | Returns true if there is a next sample |
| `hasPrev()` | Checks if previous sample exists | None | `boolean` | Returns true if there is a previous sample |

### Accessing Data Through Pointers

Pointers combine navigation with data access:

```java
// Get the current position
int index = pointer.getPoint();
long position = pointer.getPositionAsMultiple();
DomainLongValue domainPosition = pointer.getPosition();

// Check if pointer is between samples
boolean isBetweenSamples = pointer.hasDelta();
long offset = pointer.getDelta();

// Access the current value
Object value = pointer.val();
double numValue = pointer.doubleValue();
String textValue = pointer.stringValue();

// Format the current value
String formatted = pointer.format("hex");
String defaultFormatted = pointer.format(pointer.defaultFormat());
```

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| `getPoint()` | Gets current sample index | None | `int` | 0-based index of current sample |
| `getPositionAsMultiple()` | Gets position as raw domain multiple | None | `long` | Raw numeric position value |
| `getPosition()` | Gets position as domain value | None | `DomainLongValue` | Full domain value with unit information |
| `hasDelta()` | Checks if pointer has offset from sample position | None | `boolean` | True if positioned between samples |
| `getDelta()` | Gets offset from current sample position | None | `long` | Domain units offset value |
| `val()` | Gets raw sample value | None | `Object` | Type depends on signal type |
| `doubleValue()` | Gets value as double | None | `double` | For numeric signal types |
| `stringValue()` | Gets value as string | None | `String` | For all signal types |
| `format(String)` | Formats value using specified format | `String` - Format specifier | `String` | Format depends on signal type |
| `defaultFormat()` | Gets default format for signal | None | `String` | Signal's preferred format |

### Advanced Sample Pointer Operations

The ISamplePointer interface extends the basic IPointer functionality with sample-specific operations, including edge detection for digital signals:

```java
// Create a sample pointer
ISamplePointer samplePointer = new SamplePointer(samples);

// Edge detection (for logic signals)
boolean isRisingEdge = samplePointer.isEdge(1);  // check for rising edge
boolean isFallingEdge = samplePointer.isEdge(-1);  // check for falling edge
boolean isAnyEdge = samplePointer.isEdge(0);  // check for any edge

// Navigate to edges
boolean foundRising = samplePointer.goNextEdge(1);  // go to next rising edge
boolean foundFalling = samplePointer.goPrevEdge(-1);  // go to previous falling edge

```

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| `isEdge(int)` | Checks if current position is an edge | `int` - Edge type (1=rising, -1=falling, 0=any) | `boolean` - Edge status | For logic signals only |
| `isEdge(int, ILogicDetector)` | Checks for edge using custom detector | `int` - Edge type, `ILogicDetector` - Custom detector | `boolean` - Edge status | For custom edge detection logic |
| `goNextEdge(int)` | Moves to next edge of specified type | `int` - Edge type | `boolean` - Success status | Returns false if no matching edge found |
| `goPrevEdge(int)` | Moves to previous edge of specified type | `int` - Edge type | `boolean` - Success status | Returns false if no matching edge found |


## Iterating with ISamplePointerIterator

While pointers are ideal for interactive navigation and random access, the ISamplePointerIterator interface provides a more systematic approach to traversing samples within a specific domain range. This bidirectional iterator enables both forward and backward movement through multiple signals simultaneously, making it particularly useful for synchronized analysis.

### Understanding ISamplePointerIterator

The ISamplePointerIterator interface extends Java's standard Iterator interface to provide specialized functionality for bidirectional iteration across multiple signals within a specified domain range. Key characteristics include:

- It operates on domain positions rather than sample indices, making it suitable for signals with varying sample densities.
- It iterates to the next or previous domain position that contains a sample change in any of the source signals.
- A single domain position may contain changes in multiple signals simultaneously.
- It supports both forward (next) and backward (prev) movement, unlike standard Java iterators.
- The signals being traversed must share the same domain base.

This iterator is particularly useful for:
- Synchronized analysis of multiple signals that requires back-and-forth navigation
- Processing samples that meet specific criteria across signal groups
- Detecting and working with signal edges and transitions
- Converting positions between different domain bases
- Analyzing temporal relationships between different signals

After positioning the iterator with `prev()` or `next()`, you can access sample values and metadata from the monitored signals using methods like `val()`, `hasTag()`, `intValue()`, etc. on the respective ISamplePointer objects. This integrated access pattern makes it possible to analyze multiple signals synchronously at each domain position without having to manage separate state for each signal.

When no more samples are available, `prev()` returns `Long.MIN_VALUE` and `next()` returns `Long.MAX_VALUE`.

### Creating and Configuring the Iterator

```java
// Create sample pointers for the signals to iterate
ISamplePointer pointer1 = new SamplePointer(samples1);
ISamplePointer pointer2 = new SamplePointer(samples2);
ISamplePointer[] pointers = { pointer1, pointer2 };

// Create the iterator with domain range
ISamplePointerIterator iterator = new DefaultSamplePointerIterator(pointers);

// Alternative: Create with a writer target
ISamplesWriter writer = /* Use appropriate ISamplesWriter implementation */;
ISamplePointerIterator targetIterator = new DefaultSamplePointerIterator(writer, pointers);

// Alternative: Create with a list of pointers
List<ISamplePointer> pointerList = Arrays.asList(pointer1, pointer2);
ISamplePointerIterator listIterator = new DefaultSamplePointerIterator(pointerList);
```

**Constructor Descriptions:**

| Constructor | Description | Parameters | Notes |
|-------------|-------------|------------|-------|
| `DefaultSamplePointerIterator(ISamplePointer...)` | Creates iterator with variable number of pointers | `ISamplePointer...` - Signal pointers | Uses full range of signals |
| `DefaultSamplePointerIterator(List<ISamplePointer>)` | Creates iterator with a list of pointers | `List<ISamplePointer>` - List of signal pointers | Uses full range of signals |
| `DefaultSamplePointerIterator(ISamplesWriter, ISamplePointer...)` | Creates iterator with writer target and pointers | `ISamplesWriter` - Target writer, `ISamplePointer...` - Signal pointers | For position conversion to target domain |
| `DefaultSamplePointerIterator(ISamplesWriter, List<ISamplePointer>)` | Creates iterator with writer target and list of pointers | `ISamplesWriter` - Target writer, `List<ISamplePointer>` - List of signal pointers | For position conversion to target domain |

### Bidirectional Iteration

The ISamplePointerIterator interface supports both forward and backward traversal:

```java
// Forward iteration
while (iterator.hasNext()) {
    long position = iterator.next();
    
    // Access values for each signal at this position
    Object value1 = pointer1.val();
    Object value2 = pointer2.val();
    
    // Process the values
    System.out.println("Position: " + position + ", Value1: " + value1 + ", Value2: " + value2);
}

// Backward iteration
while (iterator.hasPrev()) {
    long position = iterator.prev();
    
    // Access values for each signal at this position
    Object value1 = pointer1.val();
    Object value2 = pointer2.val();
    
    // Process the values
    System.out.println("Position: " + position + ", Value1: " + value1 + ", Value2: " + value2);
}
```

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| `hasNext()` | Checks if forward movement is possible | None | `boolean` - Availability | Standard Iterator method |
| `next()` | Moves to next position and returns it | None | `long` - Domain position as multiple | Returns Long.MAX_VALUE if no more samples |
| `hasPrev()` | Checks if backward movement is possible | None | `boolean` - Availability | Bidirectional extension to Iterator |
| `prev()` | Moves to previous position and returns it | None | `long` - Domain position as multiple | Returns Long.MIN_VALUE if no more samples |

### Edge-Based Iteration

For logic signals, ISamplePointerIterator provides methods to navigate to specific edge types for specific sample pointers:

```java
// Navigate through each position
while (iterator.hasNext()) {
    long position = iterator.next();
    
    // Using sample pointers directly to check for edges at the current position
    boolean signal1HasEdge = pointer1.isEdge(0);  // Check for any edge in signal 1
    boolean signal2HasEdge = pointer2.isEdge(0);  // Check for any edge in signal 2
    
    // Process edge information
    if (signal1HasEdge) {
        boolean isRising = pointer1.isEdge(1);
        System.out.println("Signal1 has " + (isRising ? "rising" : "falling") + " edge at " + position);
    }
    
    if (signal2HasEdge) {
        boolean isRising = pointer2.isEdge(1);
        System.out.println("Signal2 has " + (isRising ? "rising" : "falling") + " edge at " + position);
    }
}

// Or navigate directly to edges for specific signals
while (true) {
    // Find next rising edge in signal 1
    long edgePosition = iterator.nextEdge(pointer1, 1);
    if (edgePosition == Long.MAX_VALUE) {
        break;  // No more rising edges found
    }
    System.out.println("Rising edge found at " + edgePosition + " in signal 1");
}
```

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| `nextEdge(ISamplePointer, int)` | Moves to next edge of specified type for a specific signal | `ISamplePointer` - Signal pointer, `int` - Edge type (1=rising, -1=falling, 0=any) | `long` - Position of the edge | Returns Long.MAX_VALUE if no matching edge found |
| `prevEdge(ISamplePointer, int)` | Moves to previous edge of specified type for a specific signal | `ISamplePointer` - Signal pointer, `int` - Edge type (1=rising, -1=falling, 0=any) | `long` - Position of the edge | Returns Long.MIN_VALUE if no matching edge found |
| `next(ISamplePointer)` | Moves to next sample change for a specific signal | `ISamplePointer` - Signal pointer | `long` - Position of the change | Returns Long.MAX_VALUE if no next sample exists |
| `prev(ISamplePointer)` | Moves to previous sample change for a specific signal | `ISamplePointer` - Signal pointer | `long` - Position of the change | Returns Long.MIN_VALUE if no previous sample exists |


## Practical Examples

### Example 1: Analyzing Signal Transitions

This example demonstrates how to use ISamplePointerIterator to analyze transitions in multiple signals:

```java
// Setup pointers and iterator
ISamplePointer clockPointer = new SamplePointer(clockSignal);
ISamplePointer dataPointer = new SamplePointer(dataSignal);
ISamplePointer[] pointers = { clockPointer, dataPointer };

DomainLongValue start = DomainValue.valueOf("0s");
DomainLongValue end = DomainValue.valueOf("1ms");
ISamplePointerIterator iterator = new SamplePointerIterator(pointers, start, end);

// Analyze rising clock edges and capture data values
iterator.move(iterator.getStartAsMutliple(), true);
System.out.println("Data values at rising clock edges:");
while (iterator.hasNext()) {
    long position = iterator.next();
    
    // Check if the clock signal has a rising edge at this position
    if (clockPointer.isEdge(1)) {
        // Read data value at the clock edge
        Object dataValue = dataPointer.val();
        System.out.println("Clock edge at " + position + ", Data value: " + dataValue);
    }
}
```

### Example 2: Finding Signal Violations

This example shows how to use ISamplePointerIterator to detect timing violations between signals:

```java
// Setup pointers and iterator
ISamplePointer clockPointer = new SamplePointer(clockSignal);
ISamplePointer dataPointer = new SamplePointer(dataSignal);
ISamplePointer[] pointers = { clockPointer, dataPointer };

ISamplePointerIterator iterator = new DefaultSamplePointerIterator();

// Define minimum setup time (in domain units)
long minSetupTime = 5000;  // e.g., 5ns

// Check for setup time violations
while (iterator.hasNext()) {
    long position = iterator.next();
    
    if (clockPointer.isEdge(1)) {  // Check if clock has a rising edge
        // Store the clock edge position
        long clockEdgePosition = position;
        
        // Go back in time to check for data changes within setup window
        iterator.prev();  // Move back from clock edge
        
        // Look backward for data changes within setup window
        boolean setupViolation = false;
        while (iterator.hasPrev() && position >= clockEdgePosition - minSetupTime) {
            position = iterator.prev();
            
            if (dataPointer.isEdge(0)) {  // Data changed
                setupViolation = true;
                System.out.println("Setup time violation at " + clockEdgePosition + 
                                  ", Data changed at " + position + 
                                  ", Setup time: " + (clockEdgePosition - position));
                break;
            }
        }
        
        // Return to the clock edge to continue forward scanning
        iterator.next();  // Move forward to where we were
        while (iterator.next() != clockEdgePosition) {
            // Keep moving until we reach the original clock edge
        }
    }
}
```

### Example 3: Comparing Signal Patterns

This example demonstrates using ISamplePointerIterator to compare patterns between two logic signals:

```java
// Setup pointers and iterator
ISamplePointer patternPointer = new SamplePointer(patternSignal);
ISamplePointer testPointer = new SamplePointer(testSignal);
ISamplePointer[] pointers = { patternPointer, testPointer };

ISamplePointerIterator iterator = new DefaultSamplePointerIterator(pointers);

// Compare patterns
boolean patternsMatch = true;
iterator.move(iterator.getStartAsMutliple(), true);

while (iterator.hasNext()) {
    long position = iterator.next();
    
    // Compare values at each position
    Object patternValue = patternPointer.val();
    Object testValue = testPointer.val();
    
    if (!patternValue.equals(testValue)) {
        patternsMatch = false;
        System.out.println("Pattern mismatch at position " + position + 
                          ": Pattern = " + patternValue + 
                          ", Test = " + testValue);
    }
}

System.out.println("Patterns " + (patternsMatch ? "match" : "do not match"));
```

### Example 4:Combining Pointers and Iterators

The pointer and iterator interfaces in the impulse framework complement each other, and they can be used together to create powerful signal analysis algorithms:

```java
// Create pointers for signals
ISamplePointer clockPointer = new SamplePointer(clockSignal);
ISamplePointer dataPointer = new SamplePointer(dataSignal);
ISamplePointer[] pointers = { clockPointer, dataPointer };

// Create iterator for range-based traversal
ISamplePointerIterator iterator = new DefaultSamplePointerIterator(pointers);

// Create independent pointers for fine-grained analysis
ISamplePointer analyzerPointer = new SamplePointer(dataSignal);

// Use iterator for coarse navigation
while (iterator.hasNext()) {
    long position = iterator.next();
    
    if (clockPointer.isEdge(1)) {  // Check if clock has a rising edge
        // Use independent pointer for detailed analysis around this position
        analyzerPointer.setPosition(position);
        
        // Look backward with the analyzer pointer
        if (analyzerPointer.hasPrev()) {
            analyzerPointer.goPrev();
            // Analyze previous data value
        }
        
        // Return to the clock edge position
        analyzerPointer.setPosition(position);
        
        // Look forward with the analyzer pointer
        if (analyzerPointer.hasNext()) {
            analyzerPointer.goNext();
            // Analyze next data value
        }
    }
}
```

## Conclusion

The impulse framework provides a rich set of interfaces for iterating through signal data, from simple pointers for interactive navigation to sophisticated iterators for synchronized analysis of multiple signals. Whether you're developing visualization tools, analysis algorithms, or automated testing systems, these interfaces offer the flexibility and power needed to effectively work with complex signal data.

By understanding and leveraging the capabilities of IPointer, ISamplePointer, IGroupPointer, and ISamplePointerIterator, developers can create robust and efficient signal processing applications that can handle a wide range of analysis scenarios.

For more information about working with samples, refer to:
- Reading: [Reading Samples](../impulse-api/1_reading-samples.md)
- Writing: [Writing Samples](../impulse-api/3_writing-samples.md)  and [Building Records](../impulse-api/4_building-records.md)