---
title: "Properties"
author: "Thomas Haber"
keywords: [property model, configuration, serializers, validation, typed properties, UI integration, options, conditional properties, lambda validation, functional blocks]
description: "A comprehensive guide to the impulse property system for configuring framework components. Learn how to create property models with typed values, implement validation rules, define option lists, and integrate with UI frameworks. Covers property creation, validation methods, accessing property values in functional blocks, and best practices for robust configuration management in impulse components."
category: "impulse-api"
tags:
  - api
  - tutorial
  - architecture
docID: 1047
---
![](images/hd_properties.png) 
# Properties

**Properties** are a fundamental mechanism in the impulse framework for configuring functional blocks such as serializers, signal processors, producers, and other components. The property system provides a consistent way to define, validate, and access configuration parameters while enabling user-friendly interfaces for configuration.

This guide explores the property system in detail, focusing on creating and using property models while also covering related aspects such as validation, typed properties, and user interface integration.

---

## Understanding Properties in impulse

Properties in the impulse framework serve as the primary mechanism for making components configurable and adaptable to different usage scenarios. Whether you're reading data from files, processing signals, or producing visualizations, properties provide the flexibility needed to customize behavior without modifying code.

### Property System Overview

At the core of the impulse property system is the `IPropertyModel` interface, which represents a collection of named properties with associated metadata. Each property has:

- A unique identifier (key)
- A current value
- A default value
- An optional type (beyond string)
- Optional validation rules
- Metadata like descriptive labels and help text
- Optional predefined choices (options)

Property models are used throughout the framework to:

1. **Configure components**: Adjust the behavior of serializers, processors, producers, etc.
2. **Store user preferences**: Maintain user-specific settings
3. **Define extension parameters**: Allow extensions to expose configurable options
4. **Validate inputs**: Ensure values meet requirements before they're applied
5. **Generate user interfaces**: Automatically create configuration dialogs and forms

### Key Concepts

Before diving into implementation details, it's important to understand these core concepts:

**Property Types**: While all properties can be represented as strings for persistence, the property system supports typed properties including booleans, integers, doubles, enumerations, and custom types.

**Validation**: Properties can have validation rules to ensure they contain valid values. Validation can range from simple type checking to complex domain-specific rules.

**Options**: Properties can define a set of predefined choices, displayed as dropdown menus or radio buttons in UIs.

**Default Values**: Every property has a default value that can be used to reset configuration or as a starting point.

**Control Providers**: The property system can be extended with custom UI control providers for specialized editing experiences.

---

## The IPropertyModel Interface

The `IPropertyModel` interface is the foundation of the property system, defining methods for property management, access, and validation.

### Core Capabilities

The interface provides methods for:

```java
// Get or set string values
String getVal(String id);
void setVal(String id, String value);

// Access typed values
Object getTyped(String id);
<T> T getTyped(String id, Class<T> cs);
void setTyped(String id, Object value);

// Manage defaults
String getDefault(String id);
void reset(String id);
boolean isDefault(String id);

// Work with options
String[] getOptions(String id);

// Get property metadata
Class<?> getType(String id);
boolean isEnabled(String id);
```
---

## Creating Property Models

There are several approaches to creating property models, depending on your needs and the complexity of your component.

### Using the Factory Pattern

The most common approach is to use the factory methods, which provides a fluent API for defining properties:

```java
// Create a basic property model
PropertyModel properties = new PropertyModel()
    // String property with validation
    .add("filename", "data.log", null, null, "File Name", null, "The input file to process")
    
    // Boolean property
    .add("verbose", true, null, "Verbose Output", null, "Enable detailed logging")
    
    // Integer property with options
    .add("mode", 0, 
         new String[]{"Fast", "Normal", "Accurate"}, 
         new Object[]{0, 1, 2}, 
         null, 
         "Processing Mode", null, "Select processing accuracy/speed tradeoff")
    
    // Double property with range validation
    .add("threshold", 0.5, null, null, 
         "Threshold", null, "Detection threshold (0.0-1.0)");
```

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| `PropertyModel()` | Creates a new empty property model | None | `PropertyModel` | Starting point for building a property collection |
| `add(String, Object, ...)` | Adds a property with specified characteristics | Various overloads including ID, default value, validation, etc. | `PropertyModel` (for chaining) | Core method for defining properties |
| `add(String, String, ...)` | Adds a string property | String ID, default value, and metadata | `PropertyModel` (for chaining) | String-specific version of add |
| `add(String, boolean, ...)` | Adds a boolean property | String ID, default value, and metadata | `PropertyModel` (for chaining) | Boolean-specific version of add |
| `add(String, int, ...)` | Adds an integer property | String ID, default value, and metadata | `PropertyModel` (for chaining) | Integer-specific version of add |
| `add(String, double, ...)` | Adds a double property | String ID, default value, and metadata | `PropertyModel` (for chaining) | Double-specific version of add |

The `add()` method has several overloads to support different property types and configurations. The parameters typically include:

1. Property key (identifier)
2. Default value
3. Option labels (for dropdown lists)
4. Option values (corresponding to labels)
5. Validation
6. Information (label, icon, description, help URL)

### Conditional Property Creation

The factory also supports conditional property creation using `addIf()`, which is useful for creating properties only when certain conditions are met:

```java
// Only add this property if advanced features are enabled
boolean advancedMode = true;
properties.addIf(advancedMode, "optimizationLevel", 2, 
                new String[]{"Basic", "Advanced", "Experimental"},
                new Object[]{1, 2, 3},
                null, 
                "Optimization Level", null, "Algorithm optimization level");
```

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| `addIf(boolean, String, Object, ...)` | Conditionally adds a property | Condition flag followed by normal add parameters | `PropertyModel` (for chaining) | Property is only added if condition is true |
| `addIf(boolean, String, String, ...)` | Conditionally adds a string property | Condition flag followed by string property parameters | `PropertyModel` (for chaining) | String-specific conditional version |
| `addIf(boolean, String, boolean, ...)` | Conditionally adds a boolean property | Condition flag followed by boolean property parameters | `PropertyModel` (for chaining) | Boolean-specific conditional version |
| `addIf(boolean, String, int, ...)` | Conditionally adds an integer property | Condition flag followed by integer property parameters | `PropertyModel` (for chaining) | Integer-specific conditional version |
| `addIf(boolean, String, double, ...)` | Conditionally adds a double property | Condition flag followed by double property parameters | `PropertyModel` (for chaining) | Double-specific conditional version |

---

## Working with Typed Properties

While all properties can be accessed as strings, the typed property system provides type safety and convenience when working with non-string values.

### Defining Typed Properties

When creating a property, you can specify its type:

```java
// Integer property
properties.add("count", 100, null, null, "Count", null, "Number of items to process");

// Double property
properties.add("scale", 1.5, null, null, "Scale Factor", null, "Scaling factor for values");

// Boolean property
properties.add("enabled", true, null, "Enabled", null, "Enable this feature");

```

### Accessing Typed Values

Typed properties can be accessed using type-specific methods:

```java
// Get typed values
int count = properties.getTyped("count", Integer.class);
double scale = properties.getTyped("scale", Double.class);
boolean enabled = properties.getTyped("enabled", Boolean.class);

// Set typed values
properties.setTyped("count", 200);
properties.setTyped("scale", 2.0);
properties.setTyped("enabled", false);
```
---

## Property Validation

Validation ensures that property values meet specific requirements before they're applied, preventing invalid configurations.

### Built-in Validation Results

The impulse framework provides standard validation results in the `ValidationResult` class:

```java
// Standard validation results
ValidationResult.OK           // Validation passed
ValidationResult.UNCHANGED    // Validation passed, value unchanged
ValidationResult.UNKNOWN_ERROR // Generic error result
ValidationResult.NUMBER_FORMAT_ERROR // Invalid number format
ValidationResult.BOOLEAN_FORMAT_ERROR // Invalid boolean format
```

**Method Descriptions:**

| Constant | Description | Value | Use Case |
|----------|-------------|-------|----------|
| `ValidationResult.OK` | Indicates successful validation | `IValidationResult.STATUS_OK` | Return when value passes all validation rules |
| `ValidationResult.UNCHANGED` | Indicates value is valid but unchanged | `IValidationResult.STATUS_UNCHANGED` | Return when validation doesn't modify the value |
| `ValidationResult.UNKNOWN_ERROR` | Generic error for unspecified issues | `IValidationResult.STATUS_ERROR` | Default error when specific error not available |
| `ValidationResult.NUMBER_FORMAT_ERROR` | Error for invalid numeric formats | `IValidationResult.STATUS_ERROR` | When string can't be parsed as a number |
| `ValidationResult.BOOLEAN_FORMAT_ERROR` | Error for invalid boolean formats | `IValidationResult.STATUS_ERROR` | When string can't be parsed as boolean |

### Inline Validation with Lambda Expressions

For simple validation scenarios, you can use lambda expressions directly within the `add()` method, which provides a concise way to define validation logic:

```java
// Using lambda for required field validation
properties.add("name", "", 
    (value, flags) -> {
        if (value == null || value.trim().isEmpty()) {
            return new ValidationResult(IValidationResult.STATUS_ERROR, "Name is required");
        }
        return ValidationResult.OK;
    },
    "Name", null, "User's name");
```

**Method Descriptions:**

| Method/Parameter | Description | Parameters | Return Value | Notes |
|------------------|-------------|------------|-------------|-------|
| `add()` with validator | Adds property with inline validation | Various including lambda validator | `PropertyModel` (for chaining) | Fluent API for adding validated properties |
| Lambda validator | Anonymous function for validation | `value` - The value to validate, `flags` - Validation context flags | `IValidationResult` - Validation outcome | Compact syntax for simple validation rules |
| `ValidationResult constructor` | Creates validation result | `status` - Result status code, `message` - Error message | `ValidationResult` | Used to return custom validation results |

This approach is particularly useful for:
- Simple validation rules that don't need to be reused
- Quick prototyping
- Self-contained components with specialized validation
- Reducing boilerplate code for simple validation cases

For more complex validation or rules that need to be shared across multiple properties, the full validator implementation approach shown in the next section provides better organization and reusability.

### Implementing Validators

To create validators, implement the `IValidation.Single<String>` interface:

```java
// Basic required value validator
IValidation.Single<String> requiredValidator = new IValidation.Single<String>() {
    @Override
    public IValidationResult validate(String value, int flags) {
        if (value == null || value.trim().isEmpty()) {
            return new ValidationResult(IValidationResult.STATUS_ERROR, "Value is required");
        }
        return ValidationResult.OK;
    }
};

// Range validator for numeric values
IValidation.Single<String> rangeValidator = new IValidation.Single<String>() {
    private final double min = 0;
    private final double max = 100;
    
    @Override
    public IValidationResult validate(String value, int flags) {
        try {
            double numValue = Double.parseDouble(value);
            if (numValue < min || numValue > max) {
                return new ValidationResult(IValidationResult.STATUS_ERROR, 
                    "Value must be between " + min + " and " + max);
            }
            return ValidationResult.OK;
        } catch (NumberFormatException e) {
            return ValidationResult.NUMBER_FORMAT_ERROR;
        }
    }
};

// Pattern validator for string format
IValidation.Single<String> patternValidator = new IValidation.Single<String>() {
    private final String pattern = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
    
    @Override
    public IValidationResult validate(String value, int flags) {
        if (value != null && !value.matches(pattern)) {
            return new ValidationResult(IValidationResult.STATUS_ERROR, 
                "Value must match pattern: " + pattern);
        }
        return ValidationResult.OK;
    }
};
```
---

## Properties with Options

Properties with predefined choices provide a better user experience by limiting selection to valid values and making configuration more intuitive.

### Defining Options

Options are defined as parallel arrays of labels (displayed to users) and values (used internally):

```java
// Simple options (string values same as labels)
properties.add("logLevel", "INFO", 
               new String[]{"DEBUG", "INFO", "WARN", "ERROR"}, 
               null,
               "Log Level", null, "Logging verbosity");

// Options with different internal values
properties.add("quality", 1, 
               new String[]{"Low", "Medium", "High", "Ultra"}, 
               new Object[]{0, 1, 2, 3},
               null,
               "Quality", null, "Rendering quality");
```

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| `add()` with options | Adds property with predefined choices | `String` - Property ID, `Object` - Default value, `String[]` - Option labels, `Object[]` - Option values, ... | `PropertyModel` (for chaining) | Labels shown to users; Values stored internally |
| `getOptions(String)` | Gets available options for a property | `String` - Property ID | `String[]` - Array of option labels | For displaying available choices in UI |
| `getTyped(String)` | Gets the internal value of selected option | `String` - Property ID | `Object` - Selected option's value | Returns the value object, not the label |
| `getVal(String)` | Gets the label of selected option | `String` - Property ID | `String` - Selected option's label | Returns the display text of the selection |

## Property UI Integration

The property system integrates with UI frameworks to automatically generate configuration interfaces.

### Control Providers

Control providers customize how properties are presented in the UI.
The control provider class implements `ITlkControlProvider` interface to create specialized UI components for properties.

### Information Metadata

Property metadata helps create better user interfaces:

```java
properties.add("timeout", 30, null, null, 
               "Connection Timeout", "clock-icon", // Label and icon
               "Maximum time to wait for connection in seconds", // Description
               "https://docs.example.com/timeout"); // Help URL
```

This metadata is used by UIs to display:
- Proper labels
- Icons
- Tooltip descriptions
- Help links

> **Note:** This section will be expanded in the future with more detailed information about UI integration options, custom control providers, and advanced layout techniques.

---

## Property Models in Functional Blocks

Functional blocks in the impulse framework, such as serializers, record readers, signal processors, and producers, use property models to define their configuration interfaces. The framework provides specialized factory methods for creating property models tailored to each type of functional block.

### Defining Standard Property Models

When implementing a functional block, you typically define a static `getPropertyModel()` method that creates a property model with standard properties for your component type, plus any custom properties specific to your implementation:

```java
static public IPropertyModel getPropertyModel(ISerializerDescriptor object, Object context) {
    boolean notPref = context != IRegistryObject.Preference.class;
    
    // Start with standard properties for this type of component
    return IParsingRecordReader.getPropertyModel(PROP_INLCLUDE)
        // Add component-specific properties
        .add("blockSize", 1024, null, null, 
             "Block Size", null, "Size of data blocks to process at once")
        // Add standard console logging properties
        .add(ConfiguredConsoleStream.getPropertyModel());
}
```

This approach ensures that:
1. Your component inherits all standard properties required for its functional category
2. You can add custom properties specific to your implementation
3. You can include standard property groups from other subsystems (like logging)
4. The property model is consistent with user expectations for that component type

### Accessing Properties in Functional Blocks

Functional blocks in the impulse framework provide consistent helper methods for accessing property values in implementation code. These methods simplify property access and make your code more readable by eliminating the need to directly interact with the property model in most cases.

```java
// Get a typed property with specific type
int blockSize = getTypedProperty("blockSize", Integer.class);
double threshold = getTypedProperty("threshold", Double.class);
boolean enabled = getTypedProperty("enabled", Boolean.class);

// Get a property as generic Object
Object value = getTypedProperty("complexProperty");

// Get a property as String
String filename = getProperty("filename");

// Check if a property is set to its default value
boolean isUsingDefault = isPropertyDefault("blockSize");
```

These helper methods automatically handle the common tasks of locating the property model and performing null-safety checks, allowing you to focus on the business logic of your component rather than property access mechanics.

**Method Descriptions:**

| Method | Description | Parameters | Return Value | Notes |
|--------|-------------|------------|-------------|-------|
| `getTypedProperty(String, Class<T>)` | Gets a property value with type safety | `String` - Property key, `Class<T>` - Expected type | `T` - Typed property value | Returns null if property or model doesn't exist |
| `getTypedProperty(String)` | Gets a property value as Object | `String` - Property key | `Object` - Property value | Returns null if property or model doesn't exist |
| `getProperty(String)` | Gets a property value as String | `String` - Property key | `String` - String value | Returns null if property or model doesn't exist |
| `isPropertyDefault(String)` | Checks if property is at default value | `String` - Property key | `boolean` - true if default | Returns true if property or model doesn't exist |


### Best Practices for Property Access

When working with properties in functional blocks:

1. **Type Safety**: Always use the typed accessor methods when you know the expected type.
2. **Default Values**: Design your component to work with default values by setting reasonable defaults in your property model.
3. **Validation**: Don't assume properties contain valid values in your access code; the property system's validation should handle this.
4. **Performance**: Cache property values that are used frequently instead of looking them up repeatedly.
5. **Error Handling**: Include graceful fallbacks for missing or invalid properties.

---

## Conclusion

The property system in the impulse framework provides a flexible and powerful way to configure components, validate inputs, and generate user interfaces. By understanding how to create and manage property models, you can develop customizable, user-friendly components that integrate seamlessly with the rest of the framework.
