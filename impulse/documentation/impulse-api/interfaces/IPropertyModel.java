package de.toem.toolkits.pattern.properties;

import java.util.List;
import java.util.Map;

import de.toem.toolkits.pattern.information.IInformation;
import de.toem.toolkits.pattern.information.IInformations;
import de.toem.toolkits.pattern.properties.IPropertyModel.IProperty;
import de.toem.toolkits.pattern.validation.IContextValidation;
import de.toem.toolkits.pattern.validation.IValidation;
import de.toem.toolkits.pattern.validation.IValidationResult;
import de.toem.toolkits.ui.tlk.ITlkControlProvider;

/**
 * A comprehensive property management system for configurable components.
 * 
 * The IPropertyModel interface provides a robust framework for defining, managing, 
 * validating, and accessing properties in a structured manner. It allows for:
 * 
 * - Type-safe property definitions with strong validation
 * - Default values and reset capabilities
 * - Value conversion between string and typed representations
 * - Support for options/enumerated values
 * - UI integration through metadata and control providers
 * - Validation with detailed error reporting
 * - Hierarchical organization of properties
 * 
 * This interface is central to the configuration system and is used throughout
 * the framework to provide consistent property handling.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 75
 */
public interface IPropertyModel extends IInformations<IProperty>, IContextValidation.Single<String, String>, IValidation.Multi<String> {

    /** String representation of Boolean.TRUE */
    static final String TRUE = Boolean.TRUE.toString();
    
    /** String representation of Boolean.FALSE */
    static final String FALSE = Boolean.FALSE.toString();;

    // ========================================================================================================================
    // Factory
    // ========================================================================================================================
    /**
     * Factory interface for building property models with a fluent API.
     * Provides methods to add properties of various types with different configurations.
     */
    interface Factory extends IPropertyModel{
        /**
         * Adds a string property to the model.
         * 
         * @param key Property identifier
         * @param value Default value
         * @param optionLabels Array of option labels for dropdown selection
         * @param validation Validation for the property value
         * @param informations Property metadata in order: label, iconId, description, helpURL
         * @return The property model for method chaining
         */
        public Factory add(String key, String value, String[] optionLabels, IValidation.Single<String> validation, String... informations);

        /**
         * Adds a boolean property to the model.
         * 
         * @param key Property identifier
         * @param value Default value
         * @param validation Validation for the property value
         * @param informations Property metadata in order: label, iconId, description, helpURL
         * @return The property model for method chaining
         */
        public Factory add(String key, boolean value, IValidation.Single<String> validation, String... informations);

        /**
         * Adds an integer property to the model.
         * 
         * @param key Property identifier
         * @param value Default value
         * @param optionLabels Array of option labels for dropdown selection
         * @param optionValues Array of option values corresponding to labels
         * @param validation Validation for the property value
         * @param informations Property metadata in order: label, iconId, description, helpURL
         * @return The property model for method chaining
         */
        public Factory add(String key, int value, String[] optionLabels, Object[] optionValues, IValidation.Single<String> validation, String... informations);

        /**
         * Adds a double property to the model.
         * 
         * @param key Property identifier
         * @param value Default value
         * @param optionLabels Array of option labels for dropdown selection
         * @param optionValues Array of option values corresponding to labels
         * @param validation Validation for the property value
         * @param informations Property metadata in order: label, iconId, description, helpURL
         * @return The property model for method chaining
         */
        public Factory add(String key, double value, String[] optionLabels, Object[] optionValues, IValidation.Single<String> validation, String... informations);

        /**
         * Adds a typed property to the model.
         * 
         * @param key Property identifier
         * @param defaultValue Default value
         * @param type The class type of the property
         * @param optionLabels Array of option labels for dropdown selection
         * @param optionValues Array of option values corresponding to labels
         * @param validation Validation for the property value
         * @param informations Property metadata in order: label, iconId, description, helpURL
         * @return The property model for method chaining
         */
        public Factory add(String key, String defaultValue, Class type, String[] optionLabels, Object[] optionValues, IValidation.Single<String> validation,
                String... informations);

        /**
         * Adds a custom control provider to the model.
         * 
         * @param controls The control provider class
         * @return The property model for method chaining
         */
        public Factory add(Class<? extends ITlkControlProvider> controls);
        
        /**
         * Conditionally adds a string property to the model.
         * 
         * @param condition Whether to add the property
         * @param key Property identifier
         * @param value Default value
         * @param optionLabels Array of option labels for dropdown selection
         * @param validation Validation for the property value
         * @param informations Property metadata in order: label, iconId, description, helpURL
         * @return The property model for method chaining
         */
        public Factory addIf(boolean condition, String key, String value, String[] optionLabels, IValidation.Single<String> validation,
                String... informations);

        /**
         * Conditionally adds a boolean property to the model.
         * 
         * @param condition Whether to add the property
         * @param key Property identifier
         * @param value Default value
         * @param validation Validation for the property value
         * @param informations Property metadata in order: label, iconId, description, helpURL
         * @return The property model for method chaining
         */
        public Factory addIf(boolean condition, String key, boolean value, IValidation.Single<String> validation, String... informations);

        /**
         * Conditionally adds an integer property to the model.
         * 
         * @param condition Whether to add the property
         * @param key Property identifier
         * @param defaultValue Default value
         * @param optionLabels Array of option labels for dropdown selection
         * @param optionValues Array of option values corresponding to labels
         * @param validation Validation for the property value
         * @param informations Property metadata in order: label, iconId, description, helpURL
         * @return The property model for method chaining
         */
        public Factory addIf(boolean condition, String key, int defaultValue, String[] optionLabels, Object[] optionValues, IValidation.Single<String> validation,
                String... informations);

        /**
         * Conditionally adds a double property to the model.
         * 
         * @param condition Whether to add the property
         * @param key Property identifier
         * @param value Default value
         * @param optionLabels Array of option labels for dropdown selection
         * @param optionValues Array of option values corresponding to labels
         * @param validation Validation for the property value
         * @param informations Property metadata in order: label, iconId, description, helpURL
         * @return The property model for method chaining
         */
        public Factory addIf(boolean condition, String key, double value, String[] optionLabels, Object[] optionValues, IValidation.Single<String> validation,
                String... informations);

        /**
         * Conditionally adds a typed property to the model.
         * 
         * @param condition Whether to add the property
         * @param key Property identifier
         * @param defaultValue Default value
         * @param type The class type of the property
         * @param optionLabels Array of option labels for dropdown selection
         * @param optionValues Array of option values corresponding to labels
         * @param validation Validation for the property value
         * @param informations Property metadata in order: label, iconId, description, helpURL
         * @return The property model for method chaining
         */
        public Factory addIf(boolean condition, String key, String defaultValue, Class type, String[] optionLabels, Object[] optionValues,
                IValidation.Single<String> validation, String... informations);

        public Factory addIf(boolean condition, Class<? extends ITlkControlProvider> controls);
    }
    
    // ========================================================================================================================
    // IProperty
    // ========================================================================================================================
    /**
     * Interface representing a single property within a property model.
     * Each property has a key, value, type, and optional metadata.
     */
    public interface IProperty extends IInformation, IValidation.Single<String> {
        /**
         * Gets the default value of this property as a string.
         * 
         * @return The default string value
         */
        String getDefaultVal();

        /**
         * Sets the default value of this property.
         * 
         * @param val The new default value as a string
         */
        void setDefaultVal(String val);

        /**
         * Gets the current value of this property as a string.
         * 
         * @return The current string value
         */
        String getVal();

        /**
         * Sets the current value of this property.
         * 
         * @param val The new value as a string
         */
        void setVal(String val);

        /**
         * Gets the type of this property.
         * 
         * @return The class type of the property
         */
        Class<?> getType();

        /**
         * Checks if this property has a specific type.
         * 
         * @return true if the property has a type other than string, false otherwise
         */
        boolean isTyped();

        /**
         * Checks if this property is enabled.
         * 
         * @return true if the property is enabled, false otherwise
         */
        boolean isEnabled();

        /**
         * Checks if this property has its default value.
         * 
         * @return true if the property has its default value, false otherwise
         */
        boolean isDefault();

        /**
         * Resets this property to its default value.
         */
        void reset();

        /**
         * Checks if this property has an indeterminate value.
         * This typically happens when multiple selections have different values.
         * 
         * @return true if the property value is indeterminate, false otherwise
         */
        boolean isIndeterminate();

        /**
         * Gets the typed value of this property.
         * 
         * @return The value converted to its native type
         */
        Object getTyped();

        /**
         * Gets the typed value of this property cast to the specified class.
         * 
         * @param <T> The target type
         * @param cs The class to cast to
         * @return The value converted to the requested type
         */
        <T> T getTyped(Class<T> cs);

        /**
         * Validates a typed value for this property.
         * 
         * @param value The value to validate
         * @param flags Validation flags
         * @return The validation result
         */
        IValidationResult validateTyped(Object value, int flags);

        /**
         * Sets the value of this property using a typed object.
         * 
         * @param value The new typed value
         */
        void setTyped(Object value);

        /**
         * Gets the available option labels for this property.
         * 
         * @return Array of option labels, or null if no options are defined
         */
        String[] getOptionLabels();
        
        /**
         * Gets the available option values for this property.
         * 
         * @return Array of option values, or null if no options are defined
         */
        Object[] getOptionValues();

        /**
         * Checks if this property has predefined options.
         * 
         * @return true if the property has options, false otherwise
         */
        boolean hasOptions();
        
        /**
         * Gets the current option label.
         * 
         * @return The selected option label
         */
        String getOption();

        /**
         * Sets the value of this property by option label.
         * 
         * @param option The option label to select
         */
        void setOption(String option);
        
        /**
         * Validates an option value for this property.
         * 
         * @param value The option value to validate
         * @param flags Validation flags
         * @return The validation result
         */
        IValidationResult validateOption(String value, int flags);

        /**
         * Gets the validation handler for this property.
         * 
         * @return The validation handler
         */
        IValidation.Single<String> getValidation();
    }

    // ========================================================================================================================
    // Clear / Reset
    // ========================================================================================================================

    /**
     * Resets all properties to their default values.
     */
    void reset();

    /**
     * Clears all properties from this model.
     */
    void clear();

    // ========================================================================================================================
    // Property access by id
    // ========================================================================================================================

    /**
     * Gets the available options for a property.
     * 
     * @param id The property identifier
     * @return Array of option labels, or null if no options are defined
     */
    String[] getOptions(String id);

    /**
     * Gets the default value of a property.
     * 
     * @param id The property identifier
     * @return The default value as a string
     */
    String getDefault(String id);

    /**
     * Checks if a property has its default value.
     * 
     * @param key The property identifier
     * @return true if the property has its default value, false otherwise
     */
    boolean isDefault(String key);
    
    /**
     * Gets the current value of a property.
     * 
     * @param id The property identifier
     * @return The current value as a string
     */
    String getVal(String id);

    /**
     * Sets the value of a property.
     * 
     * @param id The property identifier
     * @param value The new value as a string
     */
    void setVal(String id, String value);

    /**
     * Resets a property to its default value.
     * 
     * @param id The property identifier
     */
    void reset(String id);

    /**
     * Checks if a property has an indeterminate value.
     * 
     * @param id The property identifier
     * @return true if the property value is indeterminate, false otherwise
     */
    boolean isIndeterminate(String id);

    /**
     * Sets the default value of a property.
     * 
     * @param id The property identifier
     * @param val The new default value
     * @return This property model for method chaining
     */
    IPropertyModel setDefaultVal(String id, String val);

    // ========================================================================================================================
    // ByPassed
    // ========================================================================================================================

    /**
     * Gets a map of bypassed property values.
     * These are values that were set but didn't match any defined property.
     * 
     * @return Map of property key to value for bypassed properties
     */
    Map<String, String> getByPassed();

    // ========================================================================================================================
    // typed values by id
    // ========================================================================================================================

    /**
     * Gets the typed value of a property.
     * 
     * @param id The property identifier
     * @return The value converted to its native type
     */
    Object getTyped(String id);

    /**
     * Gets the typed value of a property cast to the specified class.
     * 
     * @param <T> The target type
     * @param id The property identifier
     * @param cs The class to cast to
     * @return The value converted to the requested type
     */
    <T> T getTyped(String id, Class<T> cs);

    /**
     * Sets the value of a property using a typed object.
     * 
     * @param id The property identifier
     * @param value The new typed value
     */
    void setTyped(String id, Object value);

    /**
     * Checks if a property has a specific type.
     * 
     * @param id The property identifier
     * @return true if the property has a type other than string, false otherwise
     */
    boolean isTyped(String id);

    /**
     * Gets the type of a property.
     * 
     * @param id The property identifier
     * @return The class type of the property
     */
    Class<?> getType(String id);

    /**
     * Checks if a property is enabled.
     * 
     * @param id The property identifier
     * @return true if the property is enabled, false otherwise
     */
    boolean isEnabled(String id);

    // ========================================================================================================================
    // total value
    // ========================================================================================================================

    /**
     * Sets all properties from an object using reflection.
     * 
     * @param total The object containing property values
     * @return This property model for method chaining
     */
    IPropertyModel setTotal(Object total);

    /**
     * Sets all properties from an IPropertyTotal implementation.
     * 
     * @param total The property total provider
     * @return This property model for method chaining
     */
    IPropertyModel setTotal(IPropertyTotal total);

    /**
     * Adds properties from an object using reflection.
     * 
     * @param total The object containing property values
     * @return This property model for method chaining
     */
    IPropertyModel addTotal(Object total);

    /**
     * Gets all property values as a string array matrix.
     * 
     * @return Array of [key, value] pairs
     */
    String[][] total();

    /**
     * Sets default values for all properties from an object using reflection.
     * 
     * @param total The object containing default values
     * @return This property model for method chaining
     */
    IPropertyModel setDefaults(Object total);

    // ========================================================================================================================
    // controls
    // ========================================================================================================================

    /**
     * Gets the list of control provider classes for this property model.
     * 
     * @return List of control provider classes
     */
    List<Class<? extends ITlkControlProvider>> getControlClasses();

    /**
     * Gets the control provider for this property model.
     * 
     * @return The control provider instance
     */
    ITlkControlProvider getControls();

    // ========================================================================================================================
    // ambiguous/common
    // ========================================================================================================================

    /**
     * Sets common properties for all properties in this model.
     * Used when the model represents multiple selections with common properties.
     * 
     * @param common The property model containing common values
     */
    void setAllCommon(IPropertyModel common);
}
