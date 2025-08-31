package de.toem.toolkits.pattern.element.serializer;

import java.util.List;

import de.toem.toolkits.pattern.element.ICell;
import de.toem.toolkits.pattern.element.ICover;
import de.toem.toolkits.pattern.element.producer.ICellProducer;
import de.toem.toolkits.pattern.threading.IProgress;
import de.toem.toolkits.pattern.validation.IValidationResult;

/**
 * Base interface for cell serializers that handle reading and writing operations.
 * 
 * This interface defines the common contract for all serializers that can convert 
 * between cell representations and serialized formats. Implementations typically
 * include readers (deserializers) that convert from external formats to cells,
 * and writers (serializers) that convert cells to external formats.
 * 
 * Serializers can be queried for applicability to specific content types and 
 * cell types, and expose their capabilities through support flags.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 67
 */
public interface ICellSerializer extends ISerializerDescriptor.DescriptedObject{
    
    // ========================================================================================================================
    // Support (extends ISupports defines)
    // ========================================================================================================================
    

    
    // ========================================================================================================================
    // Used and attempted serializers
    // ========================================================================================================================
    
    static final String WRITER_USED = "writer.used";
    static final String WRITER_ATEMPT = "writer.atempt";
    static final String READER_ATEMPT = "reader.atempt";
    static final String READER_ATEMPTED = "reader.atempted";
    static final String READER_USED = "reader.used";

    // ========================================================================================================================
    // Applicable
    // ========================================================================================================================
    
    /**
     * Indicates that a serializer is definitely applicable for the given parameters.
     */
    public final static int APPLICABLE = 1;
    
    /**
     * Indicates that a serializer might be applicable for the given parameters.
     */
    public final static int MAY_APPLICABLE = 0;
    
    /**
     * Indicates that a serializer is not applicable for the given parameters.
     */
    public final static int NOT_APPLICABLE = -1;
    
    /**
     * Combines two applicability values to determine overall applicability.
     * 
     * @param a1 First applicability value
     * @param a2 Second applicability value
     * @return Combined applicability value
     */
    public static int APPLICABLE(int a1,int a2) {
        if (a1 == NOT_APPLICABLE || a2 == NOT_APPLICABLE)
            return NOT_APPLICABLE;
        if (a1 == APPLICABLE || a2 == APPLICABLE)
            return APPLICABLE;
        return MAY_APPLICABLE;
    }
    
    /**
     * Determines if this serializer is applicable for the given context.
     * 
     * @param name The name of the serializer or context
     * @param contentType The content type to be serialized/deserialized
     * @param cellType The cell type to be serialized/deserialized
     * @return An applicability value (APPLICABLE, MAY_APPLICABLE, or NOT_APPLICABLE)
     */
    int isApplicable(String name, String contentType, String cellType);
    
    // ========================================================================================================================
    // Messages
    // ========================================================================================================================
    
    /**
     * Interface for messages produced during serialization operations.
     * Messages can represent both cell data and validation results.
     */
    interface Message extends ICell, IValidationResult{
        
    }
      
    // ========================================================================================================================
    // Input Request
    // ========================================================================================================================
    
    /**
     * Interface for requesting input during deserialization operations.
     * Provides methods to read text or binary data in various formats.
     */
    public interface IInputRequest {

        /**
         * Reads text with the specified maximum size using the default character set.
         * 
         * @param size Maximum number of characters to read
         * @return The read text
         */
        String text(int size);

        /**
         * Reads text with the specified maximum size using the given character set.
         * 
         * @param size Maximum number of characters to read
         * @param charSet Character set to use for decoding
         * @return The read text
         */
        String text(int size, String charSet);

        /**
         * Reads binary data with the specified maximum size.
         *
         * @param size Maximum number of bytes to read
         * @return The read bytes
         */
        byte[] bytes(int size);

        /**
         * Gets the character set used by this input request.
         * 
         * @return The character set identifier
         */
        String charSet();
    }
    
    // ========================================================================================================================
    // Reader
    // ========================================================================================================================
    
    /**
     * Interface for cell readers (deserializers) that convert external formats into cell structures.
     * Cell readers implement the producer pattern to generate cells from input sources.
     */
    public static interface ICellReader extends ICellSerializer, ICellProducer {

        /**
         * Extended applicability check that takes into account the actual input data.
         * This allows for more precise determination of whether this reader can handle the input.
         * 
         * @param name The name of the serializer or context
         * @param contentType The content type to be deserialized
         * @param cellType The target cell type
         * @param inputRequest The input data source to be examined
         * @return An applicability value (APPLICABLE, MAY_APPLICABLE, or NOT_APPLICABLE)
         */
        int isApplicable(String name, String contentType, String cellType, IInputRequest inputRequest); 
    }
    
    // ========================================================================================================================
    // Writer
    // ========================================================================================================================
    
    /**
     * Interface for cell writers (serializers) that convert cell structures into external formats.
     * Cell writers provide different methods to serialize cells with various contexts and options.
     */
    public static interface ICellWriter extends ICellSerializer {

        /**
         * Writes the content of a cover to the target destination.
         * 
         * @param progress Progress monitor for tracking the serialization operation
         * @param cover The cover containing the cells to be serialized
         * @param opts Options controlling the serialization process
         */
        public void write(IProgress progress, ICover cover, int opts);

        /**
         * Writes a cell with the context of a cover to the target destination.
         * 
         * @param progress Progress monitor for tracking the serialization operation
         * @param cover The cover providing context for serialization
         * @param baseCell The cell to be serialized
         * @param opts Options controlling the serialization process
         */
        public void write(IProgress progress, ICover cover, ICell baseCell, int opts);

        /**
         * Writes multiple cells with the context of a cover and a base cell to the target destination.
         * 
         * @param progress Progress monitor for tracking the serialization operation
         * @param cover The cover providing context for serialization
         * @param baseCell The base cell providing additional context
         * @param list The list of cells to be serialized
         * @param opts Options controlling the serialization process
         */
        public void write(IProgress progress, ICover cover, ICell baseCell, List<ICell> list, int opts);
    }

}
