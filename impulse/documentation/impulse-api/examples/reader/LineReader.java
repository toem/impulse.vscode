package de.toem.impulse.serializer.templates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.toem.impulse.cells.record.IRecord;
import de.toem.impulse.samples.ISample;
import de.toem.impulse.samples.ISamples;
import de.toem.impulse.samples.ITextSamplesWriter;
import de.toem.impulse.samples.domain.TimeBase;
import de.toem.impulse.serializer.AbstractSingleDomainRecordReader;
import de.toem.impulse.serializer.IParsingRecordReader;
import de.toem.toolkits.core.Utils;
import de.toem.toolkits.pattern.element.serializer.ISerializerDescriptor;
import de.toem.toolkits.pattern.element.serializer.SingletonSerializerPreference.DefaultSerializerConfiguration;
import de.toem.toolkits.pattern.ide.ConfiguredConsoleStream;
import de.toem.toolkits.pattern.ide.IConsoleStream;
import de.toem.toolkits.pattern.ide.Ide;
import de.toem.toolkits.pattern.properties.IPropertyModel;
import de.toem.toolkits.pattern.registry.IRegistryObject;
import de.toem.toolkits.pattern.registry.RegistryAnnotation;
import de.toem.toolkits.pattern.threading.IProgress;
import de.toem.toolkits.utils.serializer.ParseException;

/**
 * Example implementation of a record reader for processing text files line by line.
 *
 * The LineReader demonstrates how to create a custom record reader that processes
 * textual data from an input stream and transforms it into a signal with each line
 * represented as a text sample within the impulse framework.
 *
 * Key features demonstrated by this example:
 * - Extending AbstractSingleDomainRecordReader to inherit common reader functionality
 * - Processing text input line by line for efficient text file handling
 * - Creating a time-based signal with text samples
 * - Implementing proper progress tracking and cancellation support
 * - Handling timing using a microsecond time base
 * - Error handling with appropriate exception details
 *
 * This class can be used as a starting point for developing readers that process text-based
 * formats like log files, configuration files, or any line-oriented text content that
 * needs to be visualized or analyzed as signals.
 *
 * A typical use case for this reader would be importing log files, CSV data, or any
 * text-based format where each line represents a discrete event or data point.
 * 
 * Implementation Notes:
 * - Each non-empty line becomes a text sample in the signal
 * - The timestamp for each sample represents the elapsed processing time in microseconds
 * - Empty lines are automatically filtered out
 * - Progress is reported periodically during reading to support UI feedback
 * - Error handling includes proper resource cleanup and meaningful exception details
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 303
 */
public class LineReader extends AbstractSingleDomainRecordReader {

    // ========================================================================================================================
    // Content
    // ========================================================================================================================
    
    /**
     * Counts the number of lines processed by this reader.
     * Used for progress reporting and error diagnostics.
     */
    private int linesProcessed;
    
    /**
     * Reference to the signal created for storing line data.
     * Stored as a field to enable access from multiple methods.
     */
    private IRecord.Signal signal;
    
    /**
     * Timestamp when the parsing started.
     * Used to calculate elapsed time for each sample.
     */
    private long started;

    // ========================================================================================================================
    // Construct
    // ========================================================================================================================
    
    /**
     * Default constructor for the LineReader.
     * 
     * This constructor creates an instance without initializing input parameters.
     * It's typically used when the reader is instantiated by a factory and configured
     * later, or when created for property model inspection.
     */
    public LineReader() {
        super();
    }

    /**
     * Fully parameterized constructor for the LineReader.
     * 
     * This constructor creates and initializes an instance with all the necessary
     * parameters for immediate operation. It passes the parameters to the parent
     * class and sets up the property model for configuration.
     * 
     * @param descriptor The serializer descriptor providing contextual information
     * @param contentName The name of the content being processed
     * @param contentType The MIME type or other format descriptor of the content
     * @param cellType The type of cell that will be produced
     * @param configuration Configuration name for specialized settings
     * @param properties Additional properties as key-value pairs
     * @param in The input stream containing the data to be read
     */
    public LineReader(ISerializerDescriptor descriptor, String contentName, String contentType, String cellType, String configuration,
            String[][] properties, InputStream in) {
        super(descriptor, configuration, properties, getPropertyModel(descriptor, null), in);
    }

    // ========================================================================================================================
    // Supports
    // ========================================================================================================================

    /**
     * Determines if this reader supports the specified functionality request.
     * 
     * This method checks if the reader supports a particular functionality identified by the request parameter.
     * Two types of requests are handled:
     * 1. Configuration support (SUPPORT_CONFIGURATION): Checks if the reader works with DefaultSerializerConfiguration
     * 2. Property support: Verifies if the reader supports the requested property features
     * 
     * This method allows the framework to query the capabilities of this reader at runtime.
     * 
     * @param request An Integer identifying the functionality being queried (usually one of the SUPPORT_* constants)
     * @param context Additional context for the request, typically a configuration type string
     * @return true if the reader supports the requested functionality, false otherwise
     */
    public static boolean supports(Object request, Object context) {
        int ir = request instanceof Integer ? ((Integer) request).intValue() : -1;
        if (SUPPORT_CONFIGURATION == ir && DefaultSerializerConfiguration.TYPE.equals(context))
            return true;
        return ir == (ir & SUPPORT_PROPERTIES);
    }

    // ========================================================================================================================
    // Property Model
    // ========================================================================================================================

    /**
     * Creates and returns the property model for configuring this reader.
     * 
     * The property model defines the configurable aspects of the reader, with their
     * default values, valid ranges, and descriptions. This implementation inherits
     * all properties from the base record reader implementation and adds console
     * logging configuration.
     * 
     * Unlike the ByteBlockReader, this reader doesn't define custom properties as it
     * doesn't require additional configuration beyond what's inherited from the base
     * reader implementation.
     * 
     * This static method allows the property model to be inspected without creating
     * a full instance of the reader, which is useful for UI configuration.
     * 
     * @param object The serializer descriptor, used to provide context
     * @param context Additional context information, particularly for preferences
     * @return The property model containing all configurable properties for this reader
     */
    static public IPropertyModel getPropertyModel(ISerializerDescriptor object, Object context) {
        return IParsingRecordReader.getPropertyModel(PROP_NONE)
                .add(ConfiguredConsoleStream.getPropertyModel());
    }
    
    // ========================================================================================================================
    // Applicable
    // ========================================================================================================================
    
    /**
     * Determines if this reader can process the specified input based on the file name and content type.
     * 
     * This method is called by the impulse framework to determine if this reader should be used
     * for the given input. In this example implementation, the method returns NOT_APPLICABLE for all
     * inputs, making it a non-auto-detecting reader.
     * 
     * In a real implementation, you might check the file extension, content type, or examine 
     * the beginning of the file to determine if it's in a format this reader can handle.
     * 
     * @param name The name of the file or content
     * @param contentType The MIME type or other format descriptor of the content
     * @return APPLICABLE if this reader can process the input, NOT_APPLICABLE otherwise
     */
    @Override
    protected int isApplicable(String name, String contentType) {
        return NOT_APPLICABLE;
    }

    // ========================================================================================================================
    // Parser
    // ========================================================================================================================
    
    /**
     * Parses the input stream and creates a record with text signal data.
     * 
     * This is the core implementation method that processes the input stream data line by line.
     * It demonstrates a typical workflow for text-based record readers:
     * 1. Set up a buffered reader for efficient line reading
     * 2. Initialize logging and tracking variables
     * 3. Create the record structure with a text signal
     * 4. Read lines from the input stream in a loop
     * 5. Skip empty lines to reduce noise in the output signal
     * 6. Convert each line into a text sample with timestamp
     * 7. Periodically report progress and check for cancellation
     * 8. Handle errors and ensure proper resource cleanup
     * 
     * The method uses a microsecond time base (TimeBase.us), with each sample's timestamp 
     * representing the elapsed time since processing started (in microseconds). This creates 
     * a time-synchronized view of the text lines in the stream.
     * 
     * @param progress Interface for reporting progress and checking for cancellation
     * @param in The input stream containing the text data to be read
     * @throws ParseException If an error occurs during parsing
     */
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
            // For this example reader, we simply ignore IO exceptions that might occur during reading
            // In a production reader, you would typically log the exception and potentially rethrow it
            // wrapped in a ParseException with appropriate context information about the failure
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

}
