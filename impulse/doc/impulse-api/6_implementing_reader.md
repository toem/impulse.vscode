---
title: "Implementing Readers"
author: "Thomas Haber"
keywords: [reader implementation, InputStream, record creation, parsing, signal writing, file formats, AbstractSingleDomainRecordReader, compression, decompression, ZLIB, GZIP, LZ4, binary formats, error handling, custom readers, domain management, buffering, progress reporting]
description: "Comprehensive guide to implementing custom readers in the impulse framework, covering reader architecture, lifecycle, input stream processing, record creation, signal population, and error handling. Includes implementation patterns, best practices, and examples for different data formats including text, binary, and compressed formats."
category: "impulse-api"
tags:
  - api
  - development
  - integration
docID: 1005
recommended: 998,1015,301,303,305,307
---
![](images/hd_implementing_reader.png) 
# Implementing Readers

This guide provides a comprehensive overview of implementing custom readers in the impulse framework. Readers are the primary mechanism for importing data from external sources, transforming it into the impulse record structure, and making it available for analysis, visualization, and processing.

## Introduction to Readers in impulse

Readers are a fundamental component in the impulse framework's data pipeline, serving as the bridge between external data formats and the structured record format used for analysis and visualization. Whether you're working with text logs, binary files, measurement data, simulation results, or custom formats, implementing a custom reader allows you to bring that data into the impulse ecosystem.

A well-implemented reader handles all aspects of the data import process:
- Reading and parsing the source data
- Creating appropriate record structures and signals
- Converting raw data into properly typed samples
- Managing domain contexts (time, frequency, etc.)
- Reporting progress and handling errors
- Providing configuration options for customization

The impulse framework provides robust abstract classes and interfaces to simplify reader implementation, allowing you to focus on the format-specific parsing logic while inheriting common functionality like error handling, progress reporting, and record management.

### Key Concepts

Before diving into implementation details, let's understand the core concepts behind impulse readers:

**Reader Types**: The framework supports different types of readers for different scenarios:
- **Record Readers**: Create complete record structures with signals and scopes
- **Single-Domain Record Readers**: Simplified readers for formats where all signals share the same domain base
- **Parsing Readers**: Readers that process input data incrementally, reporting changes as they occur

**Reader Lifecycle**: All readers follow a common lifecycle:
1. **Initialization**: Setting up resources and checking format compatibility
2. **Record Creation**: Building the record structure with signals and scopes
3. **Parsing**: Reading the input data and converting it to samples
4. **Writing**: Populating signals with sample data
5. **Finalization**: Cleaning up resources and applying final changes
6. **Error Handling**: Managing exceptions and reporting issues

**Input Processing**: Readers typically receive data through an InputStream, which they must process efficiently, often with buffering and incremental parsing.

**Signal Population**: After parsing, readers use writers to populate signals with typed sample data, handling conversions and domain positioning.

With these concepts in mind, let's explore how to implement custom readers for different scenarios.

## The Reader Interface Hierarchy

The impulse framework provides a comprehensive hierarchy of reader interfaces and abstract classes, each designed for specific use cases and data formats.

### Core Reader Interfaces

- **IRecordReader**: The foundational interface for all readers, providing methods for resource management, progress tracking, and error handling.
- **IParsingRecordReader**: Extends the base reader with methods for parsing input data incrementally and reporting changes during the process.

### Abstract Base Classes

- **AbstractRecordReader**: A base implementation that provides common functionality like property handling and record management.
- **AbstractSingleDomainRecordReader**: A specialized base class for formats where all signals share the same domain base, simplifying record creation and signal management.

Most custom readers will extend one of these abstract classes, inheriting common functionality while implementing format-specific parsing and data conversion logic.

```java
// Typical reader class declaration
public class MyCustomReader extends AbstractSingleDomainRecordReader {
    // Format-specific fields and methods
    // ...
}
```

## Implementing a Basic Reader

Let's explore the key components of a reader implementation, starting with a simple example for a text-based file format.

### Reader Structure

A typical reader implementation includes these core components:

1. **Fields**: For tracking parsing state and storing references to created signals
2. **Constructors**: For initialization with different parameter sets
3. **Support Interface**: Static method defining the capabilities of the reader
4. **Property Model**: For configuration options
5. **Format Detection**: For determining if the reader can handle a given input
6. **Record Building**: For creating the record structure with signals and scopes
7. **Parsing Logic**: For processing the input data and extracting samples
8. **Writing**: For populating signals with sample data
9. **Error Handling**: For managing exceptions and cleanup


### Example: Line-Based Text Reader

Here's a simplified example of a reader that processes a text file line by line, creating a signal where each line becomes a text sample:

```java
public class LineReader extends AbstractSingleDomainRecordReader {
    private int linesProcessed;
    private IRecord.Signal signal;
    private long started;
    
    // Constructor and property model methods
    // ...
    
    @Override
    protected int isApplicable(String name, String contentType) {
        // This reader doesn't auto-detect, so always return NOT_APPLICABLE
        return NOT_APPLICABLE;
    }
    
    @Override
    protected void parse(IProgress progress, InputStream in) throws ParseException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        long current = 0;
        
        try {
            // console
            IConsoleStream console = new ConfiguredConsoleStream(Ide.DEFAULT_CONSOLE, ConfiguredConsoleStream.logging(getProperties()));
            started = Utils.millies();
                     
            // create record
            initRecord("Lines", TimeBase.us);
            signal = addSignal(base, "Lines", null,null, ISample.DATA_TYPE_TEXT, -1,ISamples.FORMAT_DEFAULT);
            changed(CHANGED_RECORD);

            // open
            // feed lines
            linesProcessed = 1;
            open(current);
            changed(CHANGED_CURRENT, current);

            // read, parse, add samples
            String line;
            int n=0;
            while ((line = reader.readLine()) != null && (progress == null || !progress.isCanceled())) {
                if (line.trim().isEmpty())
                    continue;  // Skip empty lines
                current = (Utils.millies() - started)*1000;  // Convert elapsed milliseconds to microseconds
                ((ITextSamplesWriter) getWriter(signal)).write(current, false, line);
                linesProcessed++;
                changed(CHANGED_SIGNALS, current);
                
                // flush
                if ((n++ % 100) == 0)
                    flushAndSetProgress(progress);  // Update progress every 100 lines
            }
            
        } catch (IOException e) {
        } catch (Throwable e) {
            throw new ParseException(linesProcessed, e.getMessage(), e);
        } finally {

            // close
            close(current + 1);
            try {
                reader.close();
            } catch (IOException e) {
                // Ignore exceptions on close as the stream might already be closed
            }
        }

    }
```

This example demonstrates the core structures and patterns used in impulse readers:
- Extending `AbstractSingleDomainRecordReader` to inherit common functionality
- Implementing `isApplicable()` to check format compatibility
- Creating the record structure and signals within the `parse()` method
- Reading input data line by line and converting to samples
- Managing timing and positions for samples
- Reporting progress and changes during parsing

## Reader Lifecycle Management

Understanding the complete lifecycle of a reader is essential for proper implementation. Let's explore the key phases and how to implement them correctly.

### Initialization

Readers are initialized with input parameters that typically include:
- **Descriptor**: Provides context about the serialization process
- **Configuration**: Identifies specific settings to use
- **Properties**: Key-value pairs for additional configuration
- **Input Stream**: The source data to be read

The initialization phase sets up resources, validates parameters, and prepares for parsing:

```java
public MyReader(ISerializerDescriptor descriptor, 
                String configuration,
                String[][] properties, 
                InputStream in) {
    // Initialize the reader with base class
    super(descriptor, configuration, properties, 
          getPropertyModel(descriptor, null), in);
    
    // Format-specific initialization
    // ...
}
```

### Support Interface

The `supports` method is a static method that determines whether the reader supports specific functionality requested by the framework. This method is critical for reader discovery and capability negotiation within the impulse system:

```java
public static boolean supports(Object request, Object context) {
    int ir = request instanceof Integer ? ((Integer) request).intValue() : -1;
    
    // Check if the reader supports a specific configuration type
    if (SUPPORT_CONFIGURATION == ir && DefaultSerializerConfiguration.TYPE.equals(context))
        return true;
    
    // Check for property support
    return ir == (ir & SUPPORT_PROPERTIES);
}
```

The method handles two main types of requests:
1. **Configuration Support**: Checks if the reader works with a specific configuration type
2. **Property Support**: Verifies if the reader supports requested property features

This method is important because it:
- Allows the framework to query reader capabilities at runtime without instantiation
- Controls which readers are available for specific tasks
- Determines whether the reader appears in menus and selection dialogs
- Enables configuration validation before reader instantiation

When implementing this method, ensure you check for the configuration types and property support flags that your reader actually implements.

### Property Model Interface

The `getPropertyModel` static method is a crucial part of reader implementation that defines the configurable aspects of your reader. This method enables the framework to discover and present configuration options to users without instantiating the reader:

```java
static public IPropertyModel getPropertyModel(ISerializerDescriptor object, Object context) {
    // Start with standard properties from the base reader
    return IParsingRecordReader.getPropertyModel(PROP_DOMAIN_BASE | PROP_CHARSET)
        // Add custom properties specific to this reader
        .add("blockSize", 16, Integer.class, null, "Block Size", null, "Size in bytes of each block to read")
        // Add console logging configuration
        .add(ConfiguredConsoleStream.getPropertyModel());
}
```

This static method serves several important purposes:
1. **Defining Configuration Parameters**: Specifies what aspects of the reader can be configured
2. **Setting Default Values**: Provides sensible defaults for each property
3. **Providing Type Information**: Indicates the data type expected for each property
4. **Adding Validation Rules**: Enforces constraints on property values
5. **Creating UI Metadata**: Supplies labels and descriptions for generating user interfaces
6. **Combining Property Sets**: Allows composition of property sets from different sources

#### Standard Property Flags

The `IParsingRecordReader.getPropertyModel(int options)` method accepts bit flags that determine which standard properties to include:

| Flag Constant | Value | Description | Common Use Case |
|---------------|-------|-------------|----------------|
| `PROP_NONE` | 0 | No standard properties | For readers that need minimal configuration |
| `PROP_EMPTY` | 1 | Control for handling empty values | When empty values need special handling |
| `PROP_INCLUDE` | 2 | Inclusion/exclusion patterns | To filter what elements are read |
| `PROP_RANGE` | 4 | Start/end range specification | To limit data read to a specific range |
| `PROP_TRANSFORM` | 8 | Delay/scale transformations | For modifying sample timing |
| `PROP_DOMAIN_BASE` | 16 | Domain base selection (e.g., ns, μs, ms) | To control time unit representation |
| `PROP_CHARSET` | 32 | Character set selection | For text-based readers |

You can combine these flags using bitwise OR (`|`) to include multiple standard property sets:

```java
// Include domain base and character set properties
IParsingRecordReader.getPropertyModel(PROP_DOMAIN_BASE | PROP_CHARSET)
```

#### Different Reader Implementation Strategies

Reader implementations typically follow one of these property model patterns:

- **Simple readers** like `LineReader` might use minimal configuration:
  ```java
  static public IPropertyModel getPropertyModel(ISerializerDescriptor object, Object context) {
      return IParsingRecordReader.getPropertyModel(PROP_NONE)
              .add(ConfiguredConsoleStream.getPropertyModel());
  }
  ```

- **Standard readers** like `ByteBlockReader` add a few custom properties:
  ```java
  static public IPropertyModel getPropertyModel(ISerializerDescriptor object, Object context) {
      return IParsingRecordReader.getPropertyModel(PROP_NONE)
          .add("size", 16, null, null, null, "Block size", "Block size to be used")
          .add(ConfiguredConsoleStream.getPropertyModel());
  }
  ```

- **Complex readers** add multiple custom properties with validation:
  ```java
  static public IPropertyModel getPropertyModel(ISerializerDescriptor object, Object context) {
      return IParsingRecordReader.getPropertyModel(PROP_DOMAIN_BASE | PROP_CHARSET)
          .add("blockSize", 4096, Integer.class, val -> (Integer)val > 0, 
               "Block Size", null, "Size of blocks to read in bytes")
          .add("skipEmptyBlocks", true, Boolean.class, null,
               "Skip Empty Blocks", null, "Whether to skip blocks containing only zeros")
          .add("timeScale", "microseconds", String.class, 
               Arrays.asList("nanoseconds", "microseconds", "milliseconds"), 
               "Time Scale", null, "Time unit for sample timestamps")
          .add(ConfiguredConsoleStream.getPropertyModel());
  }
  ```

#### Accessing Properties During Runtime

During reader operation, properties can be accessed using the methods provided by the base reader classes for configuration-driven behavior. These methods offer both type-safe and generic approaches to property access:

```java
@Override
protected void parse(IProgress progress, InputStream in) throws ParseException {
    try {
        // Get property values with appropriate types
        int blockSize = getTypedProperty("blockSize", Integer.class);
        boolean skipEmpty = getTypedProperty("skipEmptyBlocks", Boolean.class);
        String timeScale = getTypedProperty("timeScale", String.class);
        
        // Log property values for debugging
        IConsoleStream console = new ConfiguredConsoleStream(Ide.DEFAULT_CONSOLE, 
                                         ConfiguredConsoleStream.logging(getProperties()));
        console.info("Using block size: " + blockSize);
        
        // Apply property values to control parsing behavior
        if (skipEmpty) {
            // Skip empty blocks logic...
        }
        
        // Create record with appropriate configuration
        // ...
    } catch (Exception e) {
        // Error handling
    }
}
```

The primary methods for working with properties during runtime include:

- `getTypedProperty(String, Class<T>)`: Gets a property value with type safety
- `getTypedProperty(String)`: Gets a property value as a generic Object
- `getProperty(String)`: Gets a property value as a String
- `isPropertyDefault(String)`: Checks if a property is at its default value
- `getPropertyModel()`: Gets the complete property model

The property model approach ensures that:
- Configuration options are discoverable by the framework
- UIs can be automatically generated for configuring the reader
- Properties are validated before use
- Default values are available when needed
- Reader behavior can adapt to different user requirements

### Format Detection

The `isApplicable()` method is a critical part of the reader implementation as it determines whether the reader can handle a given input format. This method is called by the framework during the reader selection process to identify the most appropriate reader for a given file or data stream.

The impulse framework defines three possible return values from `isApplicable()`:
- `APPLICABLE` (1): The reader is definitely capable of handling the input
- `MAY_APPLICABLE` (0): The reader might be capable of handling the input
- `NOT_APPLICABLE` (-1): The reader cannot handle the input

Readers implementing the `ICellReader` interface provide an extended version of this method that allows examining the actual input data:

```java
@Override
public int isApplicable(String name, String contentType, String cellType, IInputRequest inputRequest) {
    // Example for GIF format detection (checking the signature "GIF")
    return inputRequest.text(3).contains("GIF") ? APPLICABLE : NOT_APPLICABLE;
}
```

The `IInputRequest` parameter provides methods to check the initial content of the input:

| Method | Description | Use Case |
|--------|-------------|----------|
| `text(int size)` | Gets the first N characters as text | For text formats or ASCII signatures |
| `text(int size, String charSet)` | Gets the first N characters using specific encoding | For text formats with known encoding |
| `bytes(int size)` | Gets the first N bytes as raw data | For binary formats with specific byte signatures |
| `charSet()` | Gets the character set used | For checking encoding compatibility |

For readers that don't perform auto-detection, you can simply return `NOT_APPLICABLE`:

```java
@Override
protected int isApplicable(String name, String contentType) {
    return NOT_APPLICABLE;
}
```

#### Format Detection Strategies

There are several strategies for implementing effective format detection:

1. **File Extension Checking**: Simple but less reliable
   ```java
   return name.toLowerCase().endsWith(".gif") ? APPLICABLE : NOT_APPLICABLE;
   ```

2. **Content Type Checking**: Reliable if MIME types are accurate
   ```java
   return "image/gif".equals(contentType) ? APPLICABLE : NOT_APPLICABLE;
   ```

3. **Magic Number/Signature Detection**: Most reliable for binary formats
   ```java
   byte[] header = inputRequest.bytes(6);
   return header[0] == 'G' && header[1] == 'I' && header[2] == 'F' ? APPLICABLE : NOT_APPLICABLE;
   ```

4. **Pattern Matching**: For formats with specific patterns
   ```java
   String start = inputRequest.text(100);
   return start.contains("<vcd") && start.contains("$timescale") ? APPLICABLE : NOT_APPLICABLE;
   ```

5. **Combined Approach**: For highest reliability
   ```java
   if ("image/gif".equals(contentType) || name.toLowerCase().endsWith(".gif")) {
       return inputRequest.text(3).contains("GIF") ? APPLICABLE : NOT_APPLICABLE;
   }
   return NOT_APPLICABLE;
   ```

This method should be efficient and conclusive, as it may be called for many readers as part of format detection during file import.

### Record Creation Within Parse

The record structure is created within the `parse(IProgress progress, InputStream in)` method, which is the main entry point for processing the input stream:

```java
@Override
protected void parse(IProgress progress, InputStream in) throws ParseException {
    try {
        // Initialize the record with a name and domain base
        initRecord("My Data Format", TimeBase.ns);
        
        // Create hierarchical structure with scopes
        IRecord.Scope rootScope = addScope(null, "Main");
        IRecord.Scope subScope = addScope(rootScope, "Signals");
        
        // Add signals with appropriate metadata
        IRecord.Signal mySignal = addSignal(subScope, "Signal1", "Description", "state",
                ISample.DATA_TYPE_INTEGER, 32, 
                ISample.FORMAT_DECIMAL);
        
        // Notify about record structure creation
        changed(CHANGED_RECORD);
        
        // Open writers at initial position
        open(0);
        changed(CHANGED_CURRENT, 0);
        
        // Get writer for the signal and start adding samples
        IIntegerSamplesWriter writer = (IIntegerSamplesWriter) getWriter(mySignal);
        
        // Process input data (format-specific)
        // ...
    } catch (Exception e) {
        // Error handling
    }
}
```

The record structure should reflect the logical organization of data in the source format, making it intuitive for users to navigate and analyze.

### Parsing and Writing

The `parse(IProgress progress, InputStream in)` method does the actual work of reading the input data and converting it to samples:

```java
@Override
protected void parse(IProgress progress, InputStream in) throws ParseException {
    try {
        // Set up input and log
        IConsoleStream console = new ConfiguredConsoleStream(Ide.DEFAULT_CONSOLE, 
                ConfiguredConsoleStream.logging(getProperties()));
        
        // Create record structure
        initRecord("My Format", TimeBase.us);
        IRecord.Signal signal1 = addSignal(null, "Signal1", "Description", null,
                ISample.DATA_TYPE_INTEGER, 32, ISample.FORMAT_DECIMAL);
        changed(CHANGED_RECORD);
        
        // Open the record for writing
        open(0);
        changed(CHANGED_CURRENT, 0);
        
        // Get writer for the signal
        IIntegerSamplesWriter writer = (IIntegerSamplesWriter) getWriter(signal1);
        
        // Process input data (format-specific)
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        long position = 0;
        
        while ((line = reader.readLine()) != null && (progress == null || !progress.isCanceled())) {
            // Parse data from the line
            int value = Integer.parseInt(line.trim());
            
            // Write samples at appropriate positions
            writer.write(position, false, value);
            position += 1000; // 1000 microseconds between samples
            
            // Report progress and changes
            if (position % 100000 == 0) {
                flushAndSetProgress(progress);
                changed(CHANGED_SIGNALS, position);
            }
        }
        
        // Close record with final position
        close(position);
        
    } catch (Exception e) {
        // Handle exceptions
        console.error("Error parsing data", e);
        throw new ParseException("Failed to parse: " + e.getMessage(), e);
    } finally {
        // Clean up resources
        try {
            if (in != null) in.close();
        } catch (IOException e) {
            // Ignore exceptions on close
        }
    }
}
```

For optimal performance and resource management, consider these practices:
- Use buffered reading for efficient I/O
- Report progress periodically, not for every sample
- Check for cancellation regularly to allow users to abort long operations
- Close resources properly in finally blocks or try-with-resources

### Error Handling

Proper error handling ensures that users get meaningful feedback and resources are cleaned up:

```java
try {
    // Parsing operations
    // ...
} catch (IOException e) {
    // Log detailed error information
    IConsoleStream console = 
        new ConfiguredConsoleStream(Ide.getConsole());
    console.println("Error processing file at line " + 
                   linesProcessed + ": " + e.getMessage());
    
    // Throw a ParseException with context
    throw new ParseException("I/O error at line " + 
                           linesProcessed, e);
} finally {
    // Clean up resources
    Utils.close(inputStream);
    // Other cleanup...
}
```

Good error handling includes:
- Contextual information about where the error occurred
- Underlying exception details for debugging
- Resource cleanup in finally blocks
- Appropriate logging for diagnostic purposes

## Specialized Reader Types

The impulse framework supports different types of readers for specific use cases. Let's explore some specialized reader implementations.

### Binary Data Readers

Binary formats require different parsing approaches, often involving:
- Reading fixed-size blocks or structured records
- Managing binary endianness and data alignment
- Processing headers and footers
- Handling binary-encoded fields and values

The `ByteBlockReader` example demonstrates a simple approach to reading binary data in blocks:

```java
@Override
public void parse(IProgress progress, InputStream in) throws ParseException {
    int bytesProcessed = 0;
    long started = 0;
    long current = 0;
    try {
        // console
        IConsoleStream console = new ConfiguredConsoleStream(Ide.DEFAULT_CONSOLE, ConfiguredConsoleStream.logging(getProperties()));
        started = Utils.millies();

        // properties
        int size = getTypedProperty("size", Integer.class);
        console.info(this.getId(), "size", size);

        // create record
        initRecord("Bytes", TimeBase.us);
        IRecord.Signal bytesSignal = addSignal(base, "Bytes", null, null, ISample.DATA_TYPE_BINARY, -1, ISamples.FORMAT_DEFAULT);
        IBinarySamplesWriter bytesWriter = ((IBinarySamplesWriter) getWriter(bytesSignal));
        changed(CHANGED_RECORD);

        // open
        open(current);
        changed(CHANGED_CURRENT, current);

        // read
        byte[] bytes = new byte[size];
        int read, n = 0;
        while ((read = in.read(bytes)) >= 0 && (progress == null || !progress.isCanceled())) {
            if (read <= 0)
                continue;
            if (read < bytes.length) {
                byte[] buffer = new byte[read];
                System.arraycopy(bytes, 0, buffer, 0, read);
                bytes = buffer;
            }
            current = (Utils.millies() - started) * 1000;  // Convert elapsed milliseconds to microseconds
            bytesWriter.write(current, false, bytes);
            bytesProcessed += read;
            changed(CHANGED_SIGNALS, current);

            // flush
            if ((n++ % 100) == 0)
                flushAndSetProgress(progress);  // Periodically flush data and update progress
        }
    } catch (IOException e) {
        // For this example reader, we simply ignore IO exceptions
        // In a production reader, you would typically log and handle these appropriately
    } catch (Throwable e) {
        throw new ParseException(bytesProcessed, e.getMessage(), e);
    } finally {
        // close
        close(current + 1);
        try {
            in.close();
        } catch (IOException e) {
            // Ignore exceptions on close
        }
    }
}
```

For more complex binary formats, consider using structured parsing approaches like:
- State machines for protocol parsing
- Binary deserializers for structured data
- Memory-mapped files for large datasets

### Compressed Data Readers

Many file formats store data in compressed form to reduce storage requirements, especially for large datasets. The impulse framework allows you to implement readers that can transparently handle decompression as part of the parsing process. This is particularly important when working with formats like trace files, logs, or binary dumps that may use various compression algorithms.

When implementing a reader for compressed data, you typically need to:
1. Detect the compression format based on headers or metadata
2. Select the appropriate decompression algorithm
3. Decompress data blocks before parsing them
4. Handle decompression errors gracefully

The following example demonstrates handling multiple compression algorithms within a reader:

```java
import java.io.*;
import java.util.zip.*;
import com.occultusterra.compression.FastLZ;
import kanzi.IndexedByteArray;
import kanzi.function.LZ4Codec;

// Constants for compression modes
static final int ENTRY_PBLK_MODE_NONE = 0;
static final int ENTRY_PBLK_MODE_ZLIB = 1;
static final int ENTRY_PBLK_MODE_GZIP = 2;
static final int ENTRY_PBLK_MODE_LZ4 = 3;
static final int ENTRY_PBLK_MODE_FLZ = 4;

// Decompression method handling multiple algorithms
private byte[] decompressBlock(byte[] compressed, int mode, int originalSize, IConsoleStream console) {
    byte[] decompressed = new byte[originalSize];
    
    try {
        switch (mode) {
            case ENTRY_PBLK_MODE_LZ4:
                if (!new LZ4Codec().inverse(new IndexedByteArray(compressed, 0), new IndexedByteArray(decompressed, 0))) {
                    console.error("LZ4 decompression failed");
                    return null;
                }
                break;
            case ENTRY_PBLK_MODE_FLZ:
                try { FastLZ.decompress(compressed, decompressed); } 
                catch (Exception e) { 
                    console.error("FastLZ decompression failed", e); 
                    return null; 
                }
                break;
            case ENTRY_PBLK_MODE_ZLIB:
                try {
                    Inflater decompresser = new Inflater();
                    decompresser.setInput(compressed);
                    int result = decompresser.inflate(decompressed);
                    decompresser.end();
                    if (result != originalSize) {
                        console.error("ZLib size mismatch: expected " + originalSize + ", got " + result);
                        return null;
                    }
                } catch (Exception e) { 
                    console.error("ZLib decompression failed", e); 
                    return null; 
                }
                break;
            case ENTRY_PBLK_MODE_GZIP:
                try {
                    ByteArrayInputStream bin = new ByteArrayInputStream(compressed);
                    GZIPInputStream gzipper = new GZIPInputStream(bin);
                    int result = 0, chunk = 0;
                    while ((chunk = gzipper.read(decompressed, result, decompressed.length - result)) > 0)
                        result += chunk;
                    gzipper.close();
                    if (result != originalSize) {
                        console.error("GZip size mismatch: expected " + originalSize + ", got " + result);
                        return null;
                    }
                } catch (Exception e) { 
                    console.error("GZip decompression failed", e); 
                    return null; 
                }
                break;
            case ENTRY_PBLK_MODE_NONE:
                System.arraycopy(compressed, 0, decompressed, 0, Math.min(compressed.length, originalSize));
                break;
            default:
                console.error("Unknown compression mode: " + mode);
                return null;
        }
        return decompressed;
    } catch (Exception e) {
        console.error("Decompression failed with exception", e);
        return null;
    }
}
```

When implementing a reader for compressed data, consider these best practices:

- **Memory Management**: Be mindful of memory usage when dealing with large compressed blocks that expand significantly when decompressed.
- **Streaming**: For very large files, consider streaming decompression rather than loading entire compressed blocks into memory.
- **Performance Optimization**: Balance decompression speed with memory usage based on your use case requirements.
- **Detect Corruption**: Implement checksums or other validation to detect corrupted compressed data.

You can integrate decompression into your reader's parsing workflow by decompressing blocks as they're read:

```java
@Override
protected void parse(IProgress progress, InputStream in) throws ParseException {
    try {
        // Quick record setup
        initRecord("Compressed Data", TimeBase.ns);
        IRecord.Signal signal = addSignal(null, "Data", null, null, ISample.DATA_TYPE_BINARY, -1, ISamples.FORMAT_DEFAULT);
        changed(CHANGED_RECORD);
        open(0);
        changed(CHANGED_CURRENT, 0);
        IBinarySamplesWriter writer = (IBinarySamplesWriter) getWriter(signal);
        
        // Read file header and structure info
        // ...
        
        // Process blocks
        long position = 0;
        while (hasMoreBlocks && (progress == null || !progress.isCanceled())) {
            // Read block header
            int compressionMode = readCompressionMode(in);
            int compressedSize = readCompressedSize(in);
            int originalSize = readOriginalSize(in);
            
            // Read and decompress block
            byte[] compressed = new byte[compressedSize];
            if (in.read(compressed) != compressedSize) {
                console.error("Incomplete compressed block");
                throw new ParseException("Failed to read complete compressed block");
            }
            
            byte[] decompressed = decompressBlock(compressed, compressionMode, originalSize, console);
            if (decompressed == null)
                throw new ParseException("Decompression failed - see console for details");
            
            // Store data and update position
            writer.write(position, false, decompressed);
            position += timeBetweenBlocks;
            
            // Report progress periodically
            if (++blocksProcessed % 10 == 0) {
                flushAndSetProgress(progress);
                changed(CHANGED_SIGNALS, position);
            }
        }
        
        close(position);
    } catch (IOException e) {
        throw new ParseException("I/O error: " + e.getMessage(), e);
    } finally {
        // Resource cleanup
    }
}
```

## Advanced Reader Techniques

As you develop more sophisticated readers, consider these advanced techniques for improved functionality and performance.


### Progress Reporting and Cancellation

Proper progress reporting and cancellation handling make your reader responsive and user-friendly, especially when processing large files. The `IParsingRecordReader` interface provides built-in mechanisms to support these features.
Report progress at reasonable intervals rather than for every sample:

```java
// Define reporting interval based on data unit (bytes, lines, records)
long reportInterval = 8192; // Example: report every 8KB for binary data
long lastReportedAmount = 0;

// In processing loop
while ((bytesRead = in.read(buffer)) > 0 && (progress == null || !progress.isCanceled())) {
    bytesProcessed += bytesRead;
    
    // Process data and update signals
    current = calculateTimestamp();
    writer.write(current, false, processedData);
    
    // Report changes to update the UI
    changed(CHANGED_SIGNALS, current);
    
    // Flush periodically using the common helper method
    if (++processedBlocks % 100 == 0) {
        flushAndSetProgress(progress);  // Combined flush and progress update
    }
}
```

#### Checking for Cancellation

Regularly check for user cancellation to make your reader responsive:

```java
// In processing loop, include cancellation check
while ((bytesRead = in.read(buffer)) > 0 && (progress == null || !progress.isCanceled())) {
    // Processing logic...
}

```

#### Using changed() for UI Updates

The `changed()` methods from `IParsingRecordReader` keep the UI updated about parsing progress:

```java
// Notify about record structure changes (call once when record structure is created)
changed(CHANGED_RECORD);

// Notify about current position changes (call when starting at a position)
changed(CHANGED_CURRENT, initialPosition);

// Notify about data changes with position (call during parsing)
changed(CHANGED_SIGNALS, currentPosition);

// Notify about data changes without position (for non-positioned updates)
changed(CHANGED_DATA);
```

#### Best Practices

- **Use reasonable intervals** - Update progress every ~100ms, not on every sample
- **Check cancellation regularly** without excessive overhead
- **Clean up resources** when cancelled, leaving the system in a consistent state
- **Combine with UI updates** through `changed()` calls and `flush()`
- **Include context in cancellation messages** to aid debugging
- **Use try-finally blocks** to ensure resources are always released

## Conclusion

Implementing readers in the impulse framework allows you to bring diverse data formats into a unified environment for analysis, visualization, and processing. By following the patterns and practices outlined in this guide, you can create robust, efficient readers that handle your specific data formats while integrating seamlessly with the impulse ecosystem.

## Additional Resources

For more information about implementing and using readers, refer to:
- Writing: [Writing Samples](../impulse-api/3_writing-samples.md) and [Building Records](../impulse-api/4_building-records.md)
- Configuration [Properties](../impulse-api/5_properties.md)