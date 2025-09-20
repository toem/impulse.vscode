package de.toem.impulse.serializer.templates;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import de.toem.impulse.cells.record.IRecord;
import de.toem.impulse.samples.IBinarySamplesWriter;
import de.toem.impulse.samples.ISample;
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
import de.toem.toolkits.pattern.threading.IProgress;
import de.toem.toolkits.utils.serializer.ParseException;

/**
 * Example implementation of a record reader for processing GIF animations.
 *
 * The GifReader demonstrates how to create a custom record reader that processes
 * GIF animation files and transforms each frame into a binary sample within the impulse framework.
 * This implementation serves as a template for developing readers that need to handle
 * image or animation data.
 *
 * Key features demonstrated by this example:
 * - Extending AbstractSingleDomainRecordReader to inherit common reader functionality
 * - Implementing GIF signature detection in the isApplicable method
 * - Converting individual animation frames to binary data (PNG format)
 * - Creating a time-based signal with animation frames as binary samples
 * - Using Java's ImageIO API for image processing
 * - Setting up timing for frame sequences
 * - Error handling with proper resource cleanup
 *
 * This class can be used as a starting point for developing readers that process image
 * or animation formats, and serves as an educational example of implementing readers for
 * more complex binary formats within the impulse framework.
 *
 * A typical use case for this reader would be analyzing animation timing, frame rates,
 * or visualizing frame sequences as signal data for comparison with other time-series data.
 * 
 * Implementation Notes:
 * - Each frame in the GIF becomes a binary sample in the signal
 * - Frames are converted to PNG format for storage in samples
 * - The timestamp for each frame is spaced at regular intervals (10ms per frame)
 * - Format detection checks for the "GIF" signature in the file header (first 3 bytes)
 * - The reader creates a single binary signal named "Images" to store all frames
 * - The time base is set to milliseconds (TimeBase.ms) for intuitive frame timing
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 305
 */
public class GifReader extends AbstractSingleDomainRecordReader {

    // ========================================================================================================================
    // Content
    // ========================================================================================================================

    // ========================================================================================================================
    // Construct
    // ========================================================================================================================

    /**
     * Default constructor for the GifReader.
     * 
     * This constructor creates an instance without initializing input parameters.
     * It's typically used when the reader is instantiated by a factory and configured
     * later, or when created for property model inspection.
     */
    public GifReader() {
        super();
    }

    /**
     * Fully parameterized constructor for the GifReader.
     * 
     * This constructor creates and initializes an instance with all the necessary
     * parameters for immediate operation. It passes the parameters to the parent
     * class and sets up the property model for configuration.
     * 
     * @param descriptor The serializer descriptor providing contextual information
     * @param contentName The name of the content being processed (e.g., file name)
     * @param contentType The MIME type or other format descriptor of the content (e.g., "image/gif")
     * @param cellType The type of cell that will be produced
     * @param configuration Configuration name for specialized settings
     * @param properties Additional properties as key-value pairs
     * @param in The input stream containing the GIF animation data to be read
     */
    public GifReader(ISerializerDescriptor descriptor, String contentName, String contentType, String cellType, String configuration,
            String[][] properties, InputStream in) {
        super(descriptor, configuration, properties, getPropertyModel(descriptor, null), in);
    }

    // ========================================================================================================================
    // Property Model
    // ========================================================================================================================

    /**
     * Creates and returns the property model for configuring this reader.
     * 
     * The property model defines the configurable aspects of the reader, with their
     * default values, valid ranges, and descriptions. This implementation inherits
     * properties from the base record reader implementation and adds console logging
     * configuration.
     * 
     * Unlike other readers that might need custom properties, this reader doesn't define
     * any specific properties for GIF handling as the standard implementation is sufficient.
     * 
     * @param object The serializer descriptor, used to provide context
     * @param context Additional context information, particularly for preferences
     * @return The property model containing all configurable properties for this reader
     */
    static public IPropertyModel getPropertyModel(ISerializerDescriptor object, Object context) {
        // notPref is used to determine if this method is being called in a preference context
        // This can be used to adjust available properties based on the current use case
        boolean notPref = context != IRegistryObject.Preference.class;
        return IParsingRecordReader.getPropertyModel(PROP_NONE).add(ConfiguredConsoleStream.getPropertyModel());
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
     * This method allows the framework to query the capabilities of this reader at runtime,
     * enabling proper integration with the impulse platform.
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
    // Applicable
    // ========================================================================================================================

    /**
     * Determines if this reader can process the specified input.
     * 
     * This method examines the beginning of the input stream to determine if the content can be 
     * handled by this reader. For GIF files, the method checks if the first 3 bytes contain 
     * the "GIF" signature, which is the standard header for GIF files (typically "GIF87a" or "GIF89a").
     * 
     * This smart detection logic allows the impulse framework to automatically select this reader 
     * when a GIF file is encountered, enabling a plug-and-play experience for users.
     * 
     * @param name The name of the file or content
     * @param contentType The MIME type or other format descriptor (e.g., "image/gif")
     * @param cellType The expected type of cell to be produced
     * @param inputRequest Interface for examining the beginning of the input
     * @return APPLICABLE if this reader can process the input, NOT_APPLICABLE otherwise
     */
    @Override
    public int isApplicable(String name, String contentType, String cellType, IInputRequest inputRequest) {
        return inputRequest.text(3).contains("GIF") ? APPLICABLE : NOT_APPLICABLE;
    }

    // ========================================================================================================================
    // Parser
    // ========================================================================================================================

    /**
     * Parses the input stream and creates a record with binary signal data for each GIF frame.
     * 
     * This is the core implementation method that processes the GIF animation stream.
     * It demonstrates a typical workflow for binary image-based record readers:
     * 1. Set up logging and initialization
     * 2. Create the record and signal structure
     * 3. Configure the Java ImageIO reader for GIF format
     * 4. Determine the number of frames in the animation
     * 5. Process each frame in sequence:
     *    a. Read the frame as a BufferedImage
     *    b. Convert the frame to PNG format as binary data
     *    c. Write the frame data as a sample with appropriate timestamp
     * 6. Periodically report progress and allow for cancellation
     * 7. Handle errors and ensure proper resource cleanup
     * 
     * The method uses a millisecond time base (TimeBase.ms), with each frame's timestamp 
     * spaced at regular intervals (10ms per frame). This creates a time-synchronized view 
     * of the animation frames that can be visualized in the impulse framework.
     * 
     * @param progress Interface for reporting progress and checking for cancellation
     * @param in The input stream containing the GIF animation data to be read
     * @throws ParseException If an error occurs during parsing
     */
    @Override
    protected void parse(IProgress progress, InputStream in) throws ParseException {

        int imagesProcessed=0;
        long current = 0;
        try {

            // console
            IConsoleStream console = new ConfiguredConsoleStream(Ide.DEFAULT_CONSOLE, ConfiguredConsoleStream.logging(getProperties()));
            
            // properties
            
            // create record
            initRecord("Gif Record", TimeBase.ms);
            IRecord.Signal images = addSignal(null, "Images", "An image signal", null, ISample.DATA_TYPE_BINARY, -1, ISample.FORMAT_DEFAULT);
            changed(CHANGED_RECORD);
                       
            // open
            open(current);
            changed(CHANGED_CURRENT, current);
            
            // images
            IBinarySamplesWriter imageWriter = (IBinarySamplesWriter) getWriter(images);
            ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
            ImageInputStream stream = ImageIO.createImageInputStream(in);
            reader.setInput(stream);

            int count = reader.getNumImages(true);  // Get total number of frames in the GIF
            int n=0;
            for (int index = 0; index < count; index++) {
                BufferedImage frame = reader.read(index);  // Read current frame
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(frame, "png", baos);  // Convert frame to PNG format
                byte[] bytes = baos.toByteArray();
                imageWriter.write(current = index * 10, false, bytes);  // Store with timestamp (10ms per frame)
                imagesProcessed = index;
                changed(CHANGED_SIGNALS, current);
                
                // flush
                if ((n++ % 100) == 0)
                    flushAndSetProgress(progress);  // Update progress every 100 frames
                
            }
        } catch (IOException e) {
            // For this example reader, we simply ignore IO exceptions that might occur during reading
            // In a production reader, you would typically log the exception and potentially rethrow it
            // wrapped in a ParseException with appropriate context information about the failure
        } catch (Throwable e) {
            throw new ParseException(imagesProcessed, e.getMessage(), e);
        } finally {

            // close
            close(current + 1);
            try {
                in.close();
            } catch (IOException e) {
                // Ignore exceptions on close as the stream might already be closed
            }
        }
    }

}
