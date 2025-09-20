package de.toem.impulse.samples;

import java.math.BigDecimal;

/**
 * Interface for writing floating-point samples in the impulse framework.
 *
 * This interface provides specialized methods for writing floating-point numerical data to signals.
 * It extends the INumberSamplesWriter interface with float-specific functionality, enabling
 * efficient and type-safe representation of decimal values, analog measurements, and continuous quantities.
 *
 * Key features of this interface include:
 * - Support for various floating-point formats including double, float, and BigDecimal
 * - Methods for writing individual values at specific positions with appropriate precision
 * - Specialized handling of NaN, Infinity, and other special floating-point values
 * - Helper methods optimized for different scripting environments
 * - Optional tagging support for highlighting special values or regions
 *
 * The `IFloatSamplesWriter` interface is particularly useful for representing analog signals,
 * sensor data, measured values, scientific calculations, and any other data that requires
 * decimal precision. It provides a clear and type-safe API for generating complex floating-point
 * signal data while maintaining the positioning and tagging capabilities of the base writer interface.
 *
 * This interface can be used in various scenarios such as scientific data visualization,
 * analog simulation, measurement systems, and financial applications where precise
 * representation of decimal values is essential.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 13
 */
public interface IFloatSamplesWriter extends INumberSamplesWriter {

    /**
     * Writes a double sample.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
     * Example (Java)
     * 
     *  writer.write(1000L,4.0d);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean write(long position, double value);
    /**
     * Writes a float sample.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
     * Example (Java)
     * 
     *  float value = 3.0f; 
     *  writer.write(1000L, false, value);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, float value);
    /**
     * Writes a double sample.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
     * Example (Java)
     * 
     *  double value = 3.0f; 
     *  writer.write(1000L, false, value);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, double value);
    /**
     * Writes a big decimal sample.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
     * Example (Java)
     * 
     *  BigDecimal value = new BigDecimal(3.0f); 
     *  writer.write(1000L, false, value);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, BigDecimal value);

    /**
     * Writes a number sample.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
     * Example (Java)
     * 
     *  Number value = new BigDecimal(3.0f); 
     *  writer.write(1000L, false, value);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, Number value);
    
    /**
     * Writes a float array (e.g. X/Y value).
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
     * Example (Java)
     * 
     *  float[] value = new float[]{10.0,20.0}; 
     *  writer.write(1000L, false, value);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, float[] value);
    
    /**
     * Writes a double array (e.g. X/Y value).
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
     * Example (Java)
     * 
     *  double[] value = new double[]{10.0,20.0}; 
     *  writer.write(1000L, false, value);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, double[] value);
    
    /**
     * Writes a BigDecimal array (e.g. X/Y value).
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * <pre>
   
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean write(long position, boolean tag, BigDecimal[] value);
    /**
     * Writes a float sample.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, float value).
     * Defined for scripting purpose.
     * <pre>
     * Example (JavaScript)
     * 
     *  var value = 3.0; 
     *  out.writeFloat(1000, false, value);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    
    boolean writeFloat(long position, boolean tag, float value);
    /**
     * Writes a double sample.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, double value).
     * Defined for scripting purpose.
     * <pre>
     * Example (JavaScript)
     * 
     *  var value = 3.0; 
     *  out.writeDouble(1000, false, value);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean writeDouble(long position, boolean tag, double value);
    /**
     * Writes a big decimal sample.
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, BigDecimal value).
     * Defined for scripting purpose.
     * <pre>
     * Example (JavaScript)
     * 
     *  var value = new java.math.BigDecimal(12); 
     *  out.writeBig(1000, false, value);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean writeBig(long position, boolean tag, BigDecimal value);

    /**
     * Writes a float array (e.g. X/Y value).
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, float[] value).
     * Defined for scripting purpose.
     * <pre>
     * Example (JavaScript)
     * 
     *  var value = java.lang.reflect.Array.newInstance(java.lang.Float.TYPE, 2);  // a float array 
     *  value[0] = 5.0; value[1] = 3.0;
     *  out.writeFloatArray(1000,false,value);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean writeFloatArray(long position, boolean tag, float[] value);
    /**
     * Writes a float array (e.g. X/Y value).
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, float[] value).
     * Defined for scripting purpose.
     * <pre>
     * Example (JavaScript)
     * 
     *  out.writeFloatArgs(1000,false,3,5.0);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean writeFloatArgs(long position, boolean tag, float... value);    
    /**
     * Writes a double array (e.g. X/Y value).
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, double[] value).
     * Defined for scripting purpose.
     * <pre>
     * Example (JavaScript)
     * 
     *  var value = java.lang.reflect.Array.newInstance(java.lang.Double.TYPE, 2);  // a float array 
     *  value[0] = 5.0; value[1] = 3.0;
     *  out.writeDoubleArray(1000,false,value);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean writeDoubleArray(long position, boolean tag, double[] value);
    /**
     * Writes a double array (e.g. X/Y value).
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, double[] value).
     * Defined for scripting purpose.
     * <pre>
     * Example (JavaScript)
     * 
     *  out.writeDoubleArgs(1000,false,3,5.0);     
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean writeDoubleArgs(long position, boolean tag, double... value);

    /**
     * Writes a BigDecimal array (e.g. X/Y value).
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, BigDecimal[] value).
     * Defined for scripting purpose.
     * <pre>  
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean writeBigArray(long position, boolean tag, BigDecimal[] value);
    /**
     * Writes a BigDecimal array (e.g. X/Y value).
     * Before using this method, the writer must have been opened (done by system when using script producers).
     * Same as write(long position, boolean tag, BigDecimal[] value).
     * Defined for scripting purpose.
     * <pre>
  
     * </pre>
     * @param position Domain position as a multiple of its domain base (e.g. domain base=1ms; multiple = 100; -> domain value = 100ms). Consecutive calls need to pass a value greater or equal.
     * @param tag If set to true, impulse will use tag color (usually red) to paint the sample. Meaning of "tag is use-case depended.
     * @param value Value to be inserted.
     * @return Returns true if succeeded.
     */
    boolean writeBigArgs(long position, boolean tag, BigDecimal... value);
    
 
 
    default float[] createFloatArray(int length) {
        return new float[length];
    }
    
    default double[] createDoubleArray(int length) {
        return new double[length];
    }
    
    default BigDecimal[] createBigArray(int length) {
        return new BigDecimal[length];
    }
 }
