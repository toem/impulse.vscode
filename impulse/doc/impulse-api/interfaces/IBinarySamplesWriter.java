package de.toem.impulse.samples;



/**
 * Interface for writing binary data samples in the impulse framework.
 *
 * This interface provides specialized methods for writing raw binary data to signals.
 * It extends the base ISamplesWriter interface with binary-specific functionality, enabling
 * efficient representation of arbitrary byte sequences, files, images, or any raw data.
 *
 * Key features of this interface include:
 * - Methods for writing byte arrays of any length at specific positions
 * - Efficient handling of large binary objects through optimized data storage
 * - Support for both complete binary blocks and fragmented binary data
 * - Helper methods optimized for different scripting environments
 * - Optional tagging support for marking significant binary data
 *
 * The `IBinarySamplesWriter` interface is particularly useful for capturing raw binary
 * content such as file contents, network packets, memory dumps, image data, or any
 * other information that is best represented in its raw binary form. It provides a
 * straightforward API for storing binary data while maintaining the positioning and
 * tagging capabilities of the base writer interface.
 *
 * This interface can be used in various scenarios such as file transfer monitoring,
 * network packet capture, firmware analysis, and any application where raw binary
 * data needs to be represented in signal form.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 9
 */
public interface IBinarySamplesWriter extends ISamplesWriter{
   
    /**
     * Writes a binary sample.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
     * Example (Java)
     * 
     *  byte[] value = new byte[]{1,2,3,4};
     *  writer.write(1000L,false,value); 
     *  
     * Example (JavaScript)  
     * 
     *  var value=java.lang.String.valueOf(123).getBytes();
     *  out.write( 1000, false, value);
     * </pre>
     * @param multiple  Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal. 
     * @param tag  If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value  Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, byte[] value);  
    boolean write(long position, boolean tag, byte[] value, int start, int length);  
    

}
