package de.toem.impulse.samples;

import de.toem.impulse.cells.record.IRecord;
import de.toem.impulse.samples.domain.IDomainBase;
import de.toem.toolkits.pattern.element.ICell;

/**
 * Interface for single domain record production in the impulse framework.
 *
 * This specialized interface extends IRecordProducer to provide functionality specific to 
 * records with a single domain base. It simplifies signal creation and management by applying
 * a consistent domain base across all signals in the record, making it particularly useful
 * for cases where all signals share the same time or frequency domain.
 *
 * Key features of this interface include:
 * - Simplified initialization with a single domain base for the entire record
 * - Streamlined signal creation without requiring domain base specification for each signal
 * - Unified writer opening and closing operations for all signals
 * - Support for both discrete and continuous process modes
 *
 * This interface is ideal for scenarios where data consistency across a single domain
 * is essential, such as in time-synchronized measurements, simulation results with a uniform
 * time base, or frequency-domain analysis with a consistent frequency resolution.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 55
 */

public interface ISingleDomainRecordProducer extends IRecordProducer {

    /**
     * Initializes the record with the specified name and domain base.
     * 
     * This method sets the name of the root record and establishes a single domain base
     * for all signals that will be added to the record. The domain base defines the 
     * fundamental unit of measurement along the signal's domain axis, such as nanoseconds
     * for time-domain signals or hertz for frequency-domain signals.
     * 
     * Using a single domain base for all signals ensures consistency in domain units
     * and simplifies signal correlation and analysis across the record.
     * 
     * @param name The name to be assigned to the root record, providing identity to the entire record structure
     * @param domainBase The domain base that will be applied to all signals in this record (e.g., TimeBase.ms, FreqBase.hz)
     */
    void initRecord(String name, IDomainBase domainBase);

    /**
     * Adds a new signal to the specified container with automatic writer creation.
     * 
     * This method creates a new signal with the specified properties and adds it to the
     * given container. It automatically applies the record's domain base to the signal
     * and creates a writer for the signal to facilitate data entry.
     * 
     * The signal's properties include its name, description, tags, signal type, scale,
     * and format. These properties define the nature and behavior of the signal
     * within the record structure. Unlike the parent interface method, this method
     * doesn't require specifying a domain base since it uses the one defined for the
     * entire record.
     * 
     * @param container Parent cell to which the new signal will be added, or null to add to the base cell
     * @param name Name to assign to the new signal, serving as a unique identifier within its container
     * @param description Additional text providing context or information about the signal's purpose
     * @param tags Metadata that categorizes the signal (e.g., "default", "state", "event", "transaction")
     * @param signalType The type of signal data from ISample constants (Logic, Float, Integer, etc.)
     * @param scale Defines the dimension of the signal, such as bit width for logic signals or dimensions for arrays
     * @param format Defines how signal values are represented textually from ISample constants (e.g., "binary", "hex", "decimal")
     * @return The newly created and added IRecord.Signal instance
     */
    IRecord.Signal addSignal(ICell container, String name, String description, String tags, int signalType, int scale, String format);

    /**
     * Opens all writers at the specified position in discrete process mode.
     * 
     * This method opens all signal writers associated with this record producer at the 
     * specified domain position. In discrete process mode, samples can occur at any position
     * along the domain, with the restriction that positions must be non-decreasing.
     * 
     * Opening writers is a necessary step before writing any sample data. This method
     * provides a convenient way to open all writers simultaneously at the same position,
     * ensuring synchronization of signals at the beginning of the data generation process.
     * 
     * @param start Domain position as a multiple of the record's domain base
     *                (e.g., with domain base=1ms, multiple=100 represents a position at 100ms)
     * @return True if all writers were successfully opened, false otherwise
     */
    boolean open(long start);

    /**
     * Opens all writers at the specified position and rate in continuous process mode.
     * 
     * This method opens all signal writers associated with this record producer at the 
     * specified domain position and with the specified rate. In continuous process mode,
     * samples occur at regular intervals defined by the rate parameter.
     * 
     * The rate parameter specifies the interval between consecutive samples and is 
     * expressed as a multiple of the record's domain base. This is particularly useful
     * for signals that represent regularly sampled data, such as measurements from
     * data acquisition systems or simulation outputs with fixed time steps.
     * 
     * @param multiple Domain position as a multiple of the record's domain base
     *                (e.g., with domain base=1ms, multiple=100 represents a position at 100ms)
     * @param rate Domain distance between consecutive samples as a multiple of the record's domain base
     *            (e.g., with domain base=1ms, rate=5 represents samples every 5ms)
     * @return True if all writers were successfully opened, false otherwise
     */
    boolean open(long start, long rate);

    /**
     * Opens all writers with advanced fragment management options (for internal use only).
     * 
     * This method extends the standard open functionality with additional parameters for
     * controlling how samples are organized into fragments and managing memory usage
     * through fragment limitations. It is primarily intended for internal framework use
     * and advanced optimization scenarios.
     * 
     * @param start Domain position as a multiple of the record's domain base
     * @param rate Domain distance between consecutive samples as a multiple of the record's domain base
     * @param blocksPerFragment
     *            Samples per fragment as multiple of 256. Default value 0 will interpreted as 16 (16*256==4096 samples per fragment).a maximum of
     *            65536 samples per fragment is allowed (0<=samples256PerFragment<=256).
     * @return True if all writers were successfully opened, false otherwise
     */
    boolean open(long start, long rate, int blocksPerFragment);

    /**
     * Closes all writers at the specified position.
     * 
     * This method closes all signal writers associated with this record producer at the 
     * specified domain position. Closing writers is necessary to finalize the signal data
     * and ensure proper visualization and analysis. The closing position typically represents
     * the end of the signal's domain range.
     * 
     * The closing position is important for determining the visible range of signals in
     * visualization tools. It should be set to a value that encompasses all sample data
     * that has been written to the signals.
     * 
     * @param multendiple Domain position as a multiple of the record's domain base
     *                (e.g., with domain base=1ms, multiple=1000 represents a position at 1000ms)
     * @return True if all writers were successfully closed, false otherwise
     */
    boolean close(long end);
}
