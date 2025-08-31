package de.toem.impulse.samples.domain;

import java.math.BigDecimal;

import de.toem.toolkits.core.Utils;

/**
 * Abstract base class representing domain values in the impulse framework.
 * 
 * This class provides a foundation for working with domain values that represent positions
 * or measurements within a specific domain space, such as time or frequency. A domain value
 * consists of a numeric multiple and a domain base which defines the unit and context.
 * 
 * The DomainValue class supports multiple numeric representations (Long, Double, BigDecimal)
 * through its concrete subclasses, facilitating precise handling of values across different
 * magnitudes and precisions. It also provides a rich set of operations for converting between
 * different domain bases, performing arithmetic operations, and comparing domain values.
 * 
 * Key features include:
 * - Creating domain values through parsing of string representations
 * - Converting between different domain bases while preserving the semantic value
 * - Performing arithmetic operations (add, subtract, multiply, divide)
 * - Comparing domain values even across different but compatible domain bases
 * - Converting to various numeric representations (long, double, etc.)
 * 
 * DomainValue is particularly useful for representing positions in signals, durations between
 * events, measurement values with units, and any other values that exist within a defined
 * domain space.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 1
 */
public abstract class DomainValue extends Number{

    public IDomainBase base;

    public static final DomainValue NULL = new DomainLongValue(UnknownBase.Unknown, 0);

    // ========================================================================================================================
    // Construct
    // ========================================================================================================================

    /**
     * Creates a new domain value with the specified base.
     * 
     * @param base The domain base defining the unit system of this value. If null, uses the Unknown domain base.
     */
    public DomainValue(IDomainBase base) {
        super();
        this.base = base != null ? base : DomainBases.Unknown;
    }

    /**
     * Parses a domain value from string components with specified parsing flags.
     * 
     * @param domainClass The class of domain (e.g., "Time", "Frequency")
     * @param value The value as a string (e.g., "10ns", "2.5MHz")
     * @param flags Parsing option flags controlling the interpretation
     * @return A domain value object representing the parsed value
     */
    public static DomainValue parse(String domainClass, String value, int flags) {
        return DomainBases.parseDomainValue(domainClass, value, flags);
    }

    /**
     * Parses a domain value from string components using default parsing flags.
     * 
     * @param domainClass The class of domain (e.g., "Time", "Frequency")
     * @param value The value as a string (e.g., "10ns", "2.5MHz")
     * @return A domain value object representing the parsed value
     */
    public static DomainValue parse(String domainClass, String value) {
        return DomainBases.parseDomainValue(domainClass, value, IDomainBase.PARSE_ANY);
    }
    
    /**
     * Parses a domain value from a single string with specified parsing flags.
     * The domain class is inferred from the string.
     * 
     * @param value The value as a string (e.g., "10ns", "2.5MHz")
     * @param flags Parsing option flags controlling the interpretation
     * @return A domain value object representing the parsed value
     */
    public static DomainValue parse(String value, int flags) {
        return DomainBases.parseDomainValue(value, flags);
    }

    /**
     * Parses a domain value from a single string using default parsing flags.
     * The domain class is inferred from the string.
     * 
     * @param value The value as a string (e.g., "10ns", "2.5MHz")
     * @return A domain value object representing the parsed value
     */
    public static DomainValue parse(String value) {
        return DomainBases.parseDomainValue(value,IDomainBase.PARSE_ANY);
    }
    
    /**
     * Creates a domain value from a domain base and a numeric multiple.
     * The method automatically selects the appropriate concrete implementation
     * based on the type of the numeric value provided.
     * 
     * @param domainBase The domain base for the value
     * @param multiple The numeric multiple of the base unit
     * @return A domain value representing the specified value, or null if the numeric type is not supported
     */
    public static DomainValue valueOf(IDomainBase domainBase, Number multiple) {
        if (multiple instanceof Long)
            return new DomainLongValue(domainBase, multiple);
        if (multiple instanceof Double)
            return new DomainDoubleValue(domainBase, multiple);
        if (multiple instanceof BigDecimal)
            return new DomainBigValue(domainBase, multiple);
        return null;
    }
    // ========================================================================================================================
    // Content
    // ========================================================================================================================

    /**
     * Returns the domain base of this value.
     * 
     * @return The domain base defining the unit system
     */
    public IDomainBase base() {
        return base;
    }

    /**
     * Returns the domain class identifier of this value's base.
     * 
     * @return The domain class string (e.g., "time", "frequency")
     */
    public String getDomainClass() {
        return base != null ? base.getClazz():UnknownBase.DOMAIN_CLASS;
    }
    
    /**
     * Returns the numeric multiple of this domain value.
     * 
     * @return The multiple as a Number object
     */
    public abstract Number multiple();

    /**
     * Returns the numeric multiple as a long value.
     * 
     * @return The multiple as a long
     */
    public abstract long longMultiple();

    /**
     * Returns the numeric multiple as a double value.
     * 
     * @return The multiple as a double
     */
    public abstract double doubleMultiple();

    /**
     * Returns the numeric multiple as a BigDecimal value.
     * 
     * @return The multiple as a BigDecimal
     */
    public abstract BigDecimal bigMultiple();

    /**
     * Converts this value to common base multiple defined by the domain base.
     * This facilitates comparison of values across different but compatible bases.
     * 
     * @return The value expressed in common base multiple as a double
     */
    public double commonBaseMultiple() {
        return base.toCommonBase(multiple());
    }

    // ========================================================================================================================
    // 2String
    // ========================================================================================================================

    /**
     * Returns a string representation of this domain value.
     * Includes both the numeric value and unit, formatted according to the domain base.
     * 
     * @return A formatted string representation (e.g., "10 ns", "2.5 MHz")
     */
    public String toString() {
        return base != null ? base.toString(multiple()) : String.valueOf(multiple());
    }

    /**
     * Returns a string representation with a specific formatting style.
     * 
     * @param style The formatting style to use
     * @return A formatted string representation
     */
    public String toString(int style) {
        return base != null ? base.toString(multiple(), style) : String.valueOf(multiple());
    }
  
    // ========================================================================================================================
    // Convert
    // ========================================================================================================================

    /**
     * Converts this domain value to an equivalent value in a different domain base.
     * 
     * @param base The target domain base to convert to
     * @return A new DomainValue representing the same quantity in the new base
     */
    public abstract DomainValue convertTo(IDomainBase base);

    /**
     * Converts this domain value to an equivalent value in a different domain base 
     * using the specified conversion flags.
     * 
     * If the current base and target base are the same, returns this value unchanged.
     * If the bases are compatible (e.g., both are time bases), performs the conversion.
     * Otherwise, returns this value unchanged.
     * 
     * @param domainBase The target domain base to convert to
     * @param flags Conversion option flags controlling the conversion behavior
     * @return A new DomainValue representing the same quantity in the new base, or this instance if conversion is not possible
     */
    public DomainValue convertTo(IDomainBase domainBase, int flags) {
        if (this.base == domainBase)
            return this;
        else if (this.base.isCompatible(domainBase)) {
            Number multiple = this.base.convertTo(domainBase, multiple(), flags);
            return DomainValue.valueOf(domainBase, multiple);
        }
        return this;
    }

    /**
     * Converts this value's multiple to the specified domain base and returns the numeric value.
     * 
     * Unlike convertTo() which returns a complete DomainValue object, this method returns just
     * the numeric component of the converted value.
     * 
     * @param domainBase The target domain base to convert to
     * @param flags Conversion option flags controlling the conversion behavior
     * @return The numeric multiple in the target domain base, or null if conversion is not possible
     */
    public Number convertMultipleTo(IDomainBase domainBase, int flags) {
        if (this.base == domainBase)
            return multiple();
        else if (this.base.isCompatible(domainBase)) {
            return this.base.convertTo(domainBase, multiple(), flags);
        }
        return null;
    }

    /**
     * Converts this value's multiple to the specified domain base and returns the result as a double.
     * 
     * @param domainBase The target domain base to convert to
     * @param flags Conversion option flags controlling the conversion behavior
     * @param def Default value to return if conversion is not possible
     * @return The converted value as a double, or the default value if conversion fails
     */
    public double convertMultipleToDouble(IDomainBase domainBase, int flags, double def) {
        Number n = convertMultipleTo(domainBase, flags);
        return n != null ? n.doubleValue() : def;
    }

    /**
     * Converts this value's multiple to the specified domain base and returns the result as a long.
     * 
     * @param domainBase The target domain base to convert to
     * @param flags Conversion option flags controlling the conversion behavior
     * @param def Default value to return if conversion is not possible
     * @return The converted value as a long, or the default value if conversion fails
     */
    public long convertMultipleToLong(IDomainBase domainBase, int flags, long def) {
        Number n = convertMultipleTo(domainBase, flags);
        return n != null ? n.longValue() : def;
    }

    /**
     * Normalizes this domain value 
     * 
     * @return A new DomainValue in normalized form
     */
    public abstract DomainValue normalize();
    
    // ========================================================================================================================
    // Math
    // ========================================================================================================================

    /**
     * Checks if this domain value is equal to another domain value.
     * 
     * Equality is determined by comparing the numeric values after converting to a common base
     * if the domain bases differ but are compatible.
     * 
     * @param that The domain value to compare with
     * @return true if the values are equal, false otherwise
     */
    public boolean eq(DomainValue that) {
        if (that != null) {
            if (this.base == that.base)
                return this.doubleMultiple() == that.doubleMultiple();
            else if (this.base.isCompatible(that.base)) {
                return this.commonBaseMultiple() == that.commonBaseMultiple();
            }
        }
        return false;
    }

    /**
     * Checks if this domain value is less than another domain value.
     * 
     * Comparison is performed after converting to a common base if the domain
     * bases differ but are compatible.
     * 
     * @param that The domain value to compare with
     * @return true if this value is less than the other value, false otherwise
     */
    public boolean lt(DomainValue that) {
        if (that != null) {
            if (this.base == that.base)
                return this.doubleMultiple() < that.doubleMultiple();
            else if (this.base.isCompatible(that.base)) {
                return this.commonBaseMultiple() < that.commonBaseMultiple();
            }
        }
        return false;
    }

    /**
     * Checks if this domain value is less than or equal to another domain value.
     * 
     * Comparison is performed after converting to a common base if the domain
     * bases differ but are compatible.
     * 
     * @param that The domain value to compare with
     * @return true if this value is less than or equal to the other value, false otherwise
     */
    public boolean le(DomainValue that) {
        if (that != null) {
            if (this.base == that.base)
                return this.doubleMultiple() <= that.doubleMultiple();
            else if (this.base.isCompatible(that.base)) {
                return this.commonBaseMultiple() <= that.commonBaseMultiple();
            }
        }
        return false;
    }

    /**
     * Checks if this domain value is greater than another domain value.
     * 
     * This method is implemented in terms of the lt() method for consistency.
     * 
     * @param that The domain value to compare with
     * @return true if this value is greater than the other value, false otherwise
     */
    public boolean gt(DomainValue that) {
        if (that != null)
            return that.lt(this);
        return true;
    }

    /**
     * Checks if this domain value is greater than or equal to another domain value.
     * 
     * This method is implemented in terms of the le() method for consistency.
     * 
     * @param that The domain value to compare with
     * @return true if this value is greater than or equal to the other value, false otherwise
     */
    public boolean ge(DomainValue that) {
        if (that != null)
            return that.le(this);
        return true;
    }
    
    /**
     * Subtracts another domain value from this one.
     * 
     * If the domain bases differ but are compatible, conversion is performed automatically.
     * 
     * @param that The domain value to subtract
     * @return A new domain value representing the difference
     */
    public abstract DomainValue sub(DomainValue that);
    
    /**
     * Adds another domain value to this one.
     * 
     * If the domain bases differ but are compatible, conversion is performed automatically.
     * 
     * @param that The domain value to add
     * @return A new domain value representing the sum
     */
    public abstract DomainValue add(DomainValue that);

    /**
     * Multiplies this domain value by a scalar.
     * 
     * @param mul The scalar multiplier
     * @return A new domain value representing the product
     */
    public abstract DomainValue mul(Number mul);

    /**
     * Divides this domain value by a scalar.
     * 
     * @param div The scalar divisor
     * @return A new domain value representing the quotient
     */
    public abstract DomainValue div(Number div);

    /**
     * Divides this domain value by another domain value.
     * 
     * This operation returns a scalar representing how many times the other
     * value fits into this value.
     * 
     * @param that The domain value to divide by
     * @param round Whether to round the result or return the exact value
     * @return A scalar representing the quotient
     */
    public abstract Number div(DomainValue that, boolean round);

    /**
     * Returns the absolute value of this domain value.
     * 
     * @return A new domain value representing the absolute value
     */
    public abstract DomainValue abs();
    
    /**
     * Negates this domain value (changes the sign).
     * 
     * @return A new domain value representing the negation
     */
    public abstract DomainValue neg();
    
    /**
     * Rounds this domain value to the specified number of decimal digits.
     * 
     * @param digits The number of decimal digits to round to
     * @return A new domain value with the rounded multiple
     */
    public abstract DomainValue round(int digits);
    
    /**
     * Sets the precision of this domain value to the specified number of decimal digits.
     * Unlike round(), this method may truncate or pad with zeros to achieve the exact
     * number of decimal places.
     * 
     * @param digits The number of decimal digits to maintain
     * @return A new domain value with the specified precision
     */
    public abstract DomainValue precision(int digits);
    
    /**
     * Checks if this domain value represents zero.
     * 
     * @return true if this value is zero, false otherwise
     */
    public abstract boolean isZero();
    
    /**
     * Compares two domain values with a specified sort direction.
     * 
     * @param n1 The first domain value
     * @param n2 The second domain value
     * @param direction The sort direction (Utils.SORT_UP, Utils.SORT_DOWN, or 0 for no sorting)
     * @return -1 if n1 is less than n2, 1 if n1 is greater than n2, and 0 if they are equal, adjusted for sort direction
     */
    public static int compare(DomainValue n1, DomainValue n2, int direction) {
        if (direction == 0)
            return 0;
        if (direction == Utils.SORT_UP)
            direction = -1;
        return (n1.lt(n2) ? 1 : n1.gt(n2) ? -1 : 0) * direction;
    }

    /**
     * Returns the minimum of two domain values.
     * 
     * @param a The first domain value
     * @param b The second domain value
     * @return The smaller of the two domain values
     */
    public static DomainValue min(DomainValue a, DomainValue b) {
        return a.le(b) ? a : b;
    }

    /**
     * Returns the maximum of two domain values.
     * 
     * @param a The first domain value
     * @param b The second domain value
     * @return The larger of the two domain values
     */
    public static DomainValue max(DomainValue a, DomainValue b) {
        return a.lt(b) ? b : a;
    }
    
    // ========================================================================================================================
    // Number
    // ========================================================================================================================
    
    /**
     * Returns the value of this domain value as a double.
     * This method is part of the Number interface implementation.
     * 
     * @return The multiple part of this domain value as a double
     */
    @Override
    public double doubleValue() {
        return doubleMultiple();
    }

    /**
     * Returns the value of this domain value as a float.
     * This method is part of the Number interface implementation.
     * 
     * @return The multiple part of this domain value as a float
     */
    @Override
    public float floatValue() {
        return (float) doubleMultiple();
    }

    /**
     * Returns the value of this domain value as an int.
     * This method is part of the Number interface implementation.
     * 
     * @return The multiple part of this domain value as an int
     */
    @Override
    public int intValue() {
        return (int) longMultiple();
    }

    /**
     * Returns the value of this domain value as a long.
     * This method is part of the Number interface implementation.
     * 
     * @return The multiple part of this domain value as a long
     */
    @Override
    public long longValue() {
        return longMultiple();
    }
}
