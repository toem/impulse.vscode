package de.toem.impulse.samples;

import de.toem.impulse.samples.domain.DomainLongValue;
import de.toem.impulse.samples.domain.IDomainBase;
import de.toem.toolkits.pattern.pageable.Release;

/**
 * Interface defining the characteristic properties of a collection of samples in the impulse framework.
 *
 * This interface provides essential metadata and properties that describe a set of signal samples as a whole,
 * including information about their domain, range, continuity, and release status. It serves as a foundation
 * for working with sample collections by establishing their fundamental characteristics without requiring
 * access to the individual sample values.
 *
 * Key features of this interface include:
 * - Domain information (base unit, start, end, and rate)
 * - Sample collection properties (count, emptiness, continuity)
 * - Release and version management capabilities
 * - Monotonicity and consistency indicators
 *
 * The interface distinguishes between continuous signals (with regular sample intervals) and discrete signals
 * (with irregular or event-based sampling), allowing for proper interpretation and processing of the data.
 * It also provides methods for release management that help track changes to the sample collection over time.
 *
 * This interface is typically used when you need high-level information about a signal's characteristics
 * without needing to access or process the individual sample values. It's particularly useful for signal
 * selection, filtering, and organization tasks where understanding the nature of the signal is more important
 * than examining its specific values.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 51
 */
public interface ISamplesCharacteristic extends ISampleCharacteristic {

    // ========================================================================================================================
    // Domain range
    // ========================================================================================================================

    /**
     * Returns the domain base of the signal.
     * 
     * The domain base defines the fundamental unit of measurement for the signal's independent
     * variable, such as time, frequency, or spatial position. It establishes the context for
     * interpreting all domain-related values throughout the signal.
     * 
     * Common domain bases include:
     * - TimeBase.ps (picoseconds)
     * - TimeBase.ns (nanoseconds)
     * - TimeBase.us (microseconds)
     * - TimeBase.ms (milliseconds)
     * - FrequencyBase.Hz (hertz)
     * - IndexBase.idx (dimensionless indices)
     * 
     * The domain base is essential for proper interpretation and display of signal data, as it
     * determines how position values are interpreted and displayed in viewers and analysis tools.
     *
     * @return The domain base of this signal
     */
    IDomainBase getDomainBase();

    /**
     * Returns the domain class name of the signal.
     * 
     * The domain class represents the type of the independent variable for the signal, 
     * such as "Time", "Frequency", or "Index". This provides a high-level categorization
     * of the signal's domain without the specificity of the domain base.
     * 
     * This method is useful for determining the general nature of a signal without needing
     * to analyze the specific domain base details.
     *
     * @return The domain class name, or null if no domain base is defined
     */
    default String getDomainClass() {
        return getDomainBase() != null ? getDomainBase().getClazz() : null;
    }

    /**
     * Returns the position of the first sample as a multiple of its domain base.
     * 
     * This value represents the starting point of the signal's data along its domain axis.
     * It is expressed as a multiple of the domain base unit to provide a normalized
     * representation independent of the specific unit.
     * 
     * For example, if the domain base is 1ms and this method returns 100, the first sample
     * is positioned at 100ms in the domain.
     * 
     * This method allows for efficient storage and calculation of positions while preserving
     * the ability to interpret them in their appropriate units.
     *
     * @return The position of the first sample as a multiple of the domain base
     */
    long getStartAsMultiple();

    /**
     * Returns the position of the last sample as a multiple of its domain base.
     * 
     * This value represents the ending point of the signal's data along its domain axis.
     * It is expressed as a multiple of the domain base unit to provide a normalized
     * representation independent of the specific unit.
     * 
     * For example, if the domain base is 1ms and this method returns 500, the last sample
     * is positioned at 500ms in the domain.
     * 
     * This method, combined with getStartAsMultiple(), defines the overall span of the
     * signal in the domain.
     *
     * @return The position of the last sample as a multiple of the domain base
     */
    long getEndAsMultiple();

    /**
     * Returns the distance between consecutive samples as a multiple of the domain base.
     * 
     * This value defines the sampling interval for continuous signals. For discrete signals
     * (event-based or irregularly sampled), this method returns a non-positive value.
     * 
     * For example, if the domain base is 1ms and this method returns 10, samples are
     * positioned at regular 10ms intervals.
     * 
     * The rate is a key characteristic that determines how the signal should be processed,
     * displayed, and analyzed. It affects aliasing considerations, Nyquist limits, and other
     * aspects of signal processing.
     *
     * @return The sampling rate as a multiple of the domain base, or a non-positive value for discrete signals
     */
    long getRateAsMultiple();

    /**
     * Returns the position of the first sample as a DomainLongValue.
     * 
     * This method provides a more convenient and context-rich representation of the start position
     * by combining the domain base and position value into a single object. The returned DomainLongValue
     * encapsulates both the numeric position and its unit of measurement.
     * 
     * This representation is particularly useful for display purposes or when working with APIs that
     * require domain-aware position values.
     *
     * @return The position of the first sample as a DomainLongValue
     */
    default DomainLongValue getStart() {
        return new DomainLongValue(getDomainBase(), getStartAsMultiple());
    }

    /**
     * Returns the position of the last sample as a DomainLongValue.
     * 
     * This method provides a more convenient and context-rich representation of the end position
     * by combining the domain base and position value into a single object. The returned DomainLongValue
     * encapsulates both the numeric position and its unit of measurement.
     * 
     * This representation is particularly useful for display purposes or when working with APIs that
     * require domain-aware position values.
     *
     * @return The position of the last sample as a DomainLongValue
     */
    default DomainLongValue getEnd() {
        return new DomainLongValue(getDomainBase(), getEndAsMultiple());
    }

    /**
     * Returns the sampling rate as a DomainLongValue.
     * 
     * This method provides a more convenient and context-rich representation of the sampling rate
     * by combining the domain base and rate value into a single object. The returned DomainLongValue
     * encapsulates both the numeric rate and its unit of measurement.
     * 
     * This representation is particularly useful for display purposes or when working with APIs that
     * require domain-aware rate values.
     *
     * @return The sampling rate as a DomainLongValue
     */
    default DomainLongValue getRate() {
        return new DomainLongValue(getDomainBase(), getRateAsMultiple());
    }

    /**
     * Determines if the signal has discrete (irregularly spaced) samples.
     * 
     * A discrete signal is one where samples occur at irregular intervals or are event-based,
     * rather than being sampled at a fixed rate. Examples include protocol transactions,
     * event markers, or any signal where the timing of samples is determined by external factors
     * rather than a fixed sampling clock.
     * 
     * This method returns true if the rate is zero or negative, indicating that samples
     * are not evenly spaced in the domain.
     *
     * @return true if the signal has discrete samples, false if samples occur at regular intervals
     */
    default boolean isDiscrete() {
        return getRateAsMultiple() <= 0L;
    }
    
    /**
     * Determines if the signal has continuous (regularly spaced) samples.
     * 
     * A continuous signal is one where samples occur at a fixed rate, with equal spacing between
     * consecutive samples. Examples include waveforms captured at a specific sampling rate,
     * like audio or sensor data.
     * 
     * This method returns true if the rate is positive, indicating that samples are evenly
     * spaced in the domain.
     *
     * @return true if the signal has continuous samples, false if samples occur at irregular intervals
     */
    default boolean isContinuous() {
        return getRateAsMultiple() > 0L;
    }
    
    // ========================================================================================================================
    // No of samples
    // ========================================================================================================================
    
    /**
     * Determines if the signal contains no samples.
     * 
     * This is a convenience method that quickly checks if the signal has any data points.
     * An empty signal might represent a newly created channel, a signal that failed to
     * capture data, or the result of filtering operations that removed all samples.
     *
     * @return true if the signal contains no samples, false otherwise
     */
    default boolean isEmpty() {
        return getCount() <= 0;
    }

    /**
     * Returns the total number of samples in the signal.
     * 
     * This method provides the exact count of individual data points contained in the signal.
     * The count is essential for properly iterating through samples, allocating resources for
     * processing, and determining the density of the signal.
     * 
     * For large signals, this operation is designed to be efficient, as it's a frequently used
     * property. The implementation supports up to 2^31-1 samples per signal.
     *
     * @return The number of samples in the signal, or 0 if the signal is empty
     */
    int getCount();

    // ========================================================================================================================
    // Release
    // ========================================================================================================================

    /**
     * Returns the release identifier of the signal.
     * 
     * The release identifier is a composite value that encodes information about the signal's
     * version and modification status. 
     * A release value of 0 indicates that the signal has not yet been released. A release value of < 0 is final and will not be changed. 
     *
     * @return The release identifier encoding version and modification information
     */
    long getRelease();

    /**
     * Determines if the signal's data can still be modified.
     * 
     * Volatile signals can be extended with new samples, have their legend modified,
     * or undergo other changes. This is the normal state for signals that are actively
     * being captured or constructed.
     * 
     * This method is the logical opposite of {@link #isFinal()}.
     *
     * @return true if the signal can still be modified, false if it is finalized
     */
    default boolean isVolatile() {
        return Release.isVolatile(getRelease());
    }

    /**
     * Determines if the signal's data is finalized and cannot be modified.
     * 
     * Final signals are locked against further modifications to their samples or legend.
     * This is typically the case for signals from completed captures, historical data,
     * or signals that have been explicitly marked as final.
     * 
     * This method is the logical opposite of {@link #isVolatile()}.
     *
     * @return true if the signal is finalized, false if it can still be modified
     */
    default boolean isFinal() {
        return Release.isFinal(getRelease());
    }

    /**
     * Determines if the signal has only been extended since its initial release.
     * 
     * A monotonous signal is one where samples have only been added (typically at the end)
     * since its creation, without removing any existing samples or making other structural
     * changes. This property is useful for optimizing certain operations that depend on
     * the stability of existing data.
     * 
     * This is equivalent to calling {@link #isMonotonous(long)} with a sinceRelease value of 0.
     *
     * @return true if the signal has only been extended, false if other changes have occurred
     */
    default boolean isMonotonous() {
        return isMonotonous(0);
    }

    /**
     * Determines if the signal has only been extended since the specified release.
     * 
     * This method checks if all changes to the signal since the given release point
     * have been monotonic (additive only). It is equivalent to calling 
     * {@link #isMonotonous(long, boolean)} with ignoreAttachments set to false.
     *
     * @param sinceRelease The reference release point to compare against
     * @return true if only additions have occurred since the reference point, false otherwise
     */
    default boolean isMonotonous(long sinceRelease) {
        return isMonotonous(sinceRelease, false);
    }

    /**
     * Determines if the signal has only been extended since the specified release,
     * with an option to ignore attachment changes.
     * 
     * This method provides fine-grained control over what constitutes a monotonic change.
     * When ignoreAttachments is true, modifications to sample attachments (like relations or labels)
     * are not considered violations of monotonicity.
     *
     * @param sinceRelease The reference release point to compare against
     * @param ignoreAttachments If true, attachment changes are not considered in the monotonicity check
     * @return true if only allowed additions have occurred since the reference point, false otherwise
     */
    default boolean isMonotonous(long sinceRelease, boolean ignoreAttachments) {
        return isMonotonous(sinceRelease, getRelease(), ignoreAttachments);
    }

    /**
     * Compares two release identifiers to determine if changes between them are monotonic.
     * 
     * This static utility method performs the actual monotonicity check between two arbitrary
     * release identifiers. It examines the mayor change and reduction components, and optionally
     * the minor change (attachment) component, to determine if changes have been strictly additive.
     *
     * @param sinceRelease The earlier release identifier
     * @param toRelease The later release identifier
     * @param ignoreAttachments If true, attachment changes are not considered in the monotonicity check
     * @return true if changes between the releases are monotonic according to the criteria
     */
    static boolean isMonotonous(long sinceRelease, long toRelease, boolean ignoreAttachments) {
        return Release.mayorChanged(sinceRelease) == Release.mayorChanged(toRelease) & Release.reduced(sinceRelease) == Release.reduced(toRelease)
                && (ignoreAttachments || (Release.minorChanged(sinceRelease) == Release.minorChanged(toRelease)));
    }

    /**
     * Determines if the signal has been released with initial content.
     * 
     * A released signal has had at least its first batch of data committed. This status
     * can change from false to true as a signal is populated with data, but it never reverts
     * from true to false.
     * 
     * This method is useful for detecting signals that have no meaningful data yet, even if
     * they have been created and initialized.
     *
     * @return true if the signal has been released with content, false if not yet released
     */
    default boolean isReleased() {
        return Release.isReleased(getRelease());
    }

    /**
     * Determines if the signal has consistent, homogeneous sample data.
     * 
     * A homogeneous signal contains samples that are consistent in their structure and
     * interpretation, requiring no filtering or merging operations to process them uniformly.
     * 
     * The default implementation returns true, indicating that most signal implementations
     * provide homogeneous data by default.
     *
     * @return true if the signal contains homogeneous data, false if filtering or merging is needed
     */
    default boolean isHomogeneous() {
        return true;
    }
}