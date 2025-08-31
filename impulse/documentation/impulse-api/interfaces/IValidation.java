package de.toem.toolkits.pattern.validation;

import java.util.List;

/**
 * Generic interface for validation operations.
 * 
 * This interface defines a contract for validators that can validate input values
 * of type F and produce validation results of type E. The validation can be controlled
 * using flags to modify the validation behavior.
 * 
 * Two specialized sub-interfaces are provided:
 * - Single: For validators that produce a single validation result
 * - Multi: For validators that can produce multiple validation results
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 73
 */
public interface IValidation<E, F> {

    /**
     * Validates the provided value according to the validation rules.
     * 
     * @param value The value to validate
     * @param flags Control flags to modify the validation behavior
     * @return Validation result(s) of type E
     */
    E validate(F value, int flags);

    /**
     * Specialization of IValidation for validators that produce a single validation result.
     * 
     * This interface is for validators that perform a single validation check
     * and return a single IValidationResult.
     * 
     * @param <T> The type of value being validated
     */
    public interface Single<T> extends IValidation<IValidationResult, T> {

    }

    /**
     * Specialization of IValidation for validators that produce multiple validation results.
     * 
     * This interface is for validators that perform multiple validation checks
     * and return a list of IValidationResult objects.
     * 
     * @param <T> The type of value being validated
     */
    public interface Multi<T> extends IValidation<List<IValidationResult>, T> {

    }
       
}
