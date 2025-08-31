package de.toem.impulse.serializer;

import java.nio.charset.Charset;

import de.toem.impulse.i18n.I18n;
import de.toem.impulse.samples.domain.DomainBases;
import de.toem.impulse.samples.domain.TimeBase;
import de.toem.toolkits.pattern.properties.PropertyModel;

/**
 * Interface for record readers that parse input data into records.
 * 
 * This interface defines methods for parsing input data into record structures,
 * reporting changes during the parsing process, and providing property models
 * for configuration. Implementations handle various file formats and data sources,
 * converting them into the impulse record format.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 71
 */
public interface IParsingRecordReader extends IRecordReader  {

    // ========================================================================================================================
    // Property Model
    // ========================================================================================================================

    /**
     * Property model flags for selecting which properties to include
     */
    /** No properties selected */
    public static final int PROP_NONE = 0;
    
    /** Include the 'empty' property to control handling of empty values */
    public static final int PROP_EMPTY = 1<<0;
    
    /** Include properties for inclusion/exclusion patterns */
    public static final int PROP_INCLUDE = 1<<1;
    
    /** Include properties for range specification (start/end) */
    public static final int PROP_RANGE = 1<<2;
    
    /** Include properties for transformations (delay/scale) */
    public static final int PROP_TRANSFORM = 1<<3;
    
    /** Include property for domain base selection */
    public static final int PROP_DOMAIN_BASE = 1<<4;
    
    /** Include property for character set selection */
    public static final int PROP_CHARSET = 1<<5;
    
    /** Include property for character set selection */
    public static final int PROP_LAZY= 1<<6;
    
    /** Include property for hierarchy resolver */
    public static final int PROP_HIERARCHY= 1<<7;
    
    /** Include property for vector resolver */
    public static final int PROP_VECTOR= 1<<8;
    
    /**
     * Creates a property model with the specified options.
     * 
     * This method builds a property model containing only the properties
     * specified by the options parameter. Each bit in the options parameter
     * corresponds to a PROP_* constant and determines whether that property
     * should be included in the model.
     *
     * @param options Bit flags specifying which properties to include (combination of PROP_* constants)
     * @return A property model containing the selected properties
     */
    static public PropertyModel getPropertyModel(int options) {
        return new PropertyModel()
                
                .addIf((options & PROP_INCLUDE) != 0, "include", null, null, null, I18n.Serializer_Include, null, I18n.Serializer_Include_Description)
                .addIf((options & PROP_INCLUDE) != 0, "exclude", null, null, null, I18n.Serializer_Exclude, null, I18n.Serializer_Exclude_Description)
                .addIf((options & PROP_RANGE) != 0, "start", null, null, null, I18n.Serializer_Start, null, I18n.Serializer_Start_Description)
                .addIf((options & PROP_RANGE) != 0, "end", null, null, null, I18n.Serializer_End, null, I18n.Serializer_End_Description)
                .addIf((options & PROP_TRANSFORM) != 0, "delay", null, null, null, I18n.Serializer_Delay, null, I18n.Serializer_Delay_Description)
                .addIf((options & PROP_TRANSFORM) != 0, "dilate", null, null, null, I18n.Serializer_Dilate, null, I18n.Serializer_Dilate_Description)   
  
               .addIf((options & PROP_DOMAIN_BASE) != 0, "domainBase", TimeBase.us.toString(),String.class, DomainBases.ALL_LABELS_ARRAY,DomainBases.ALL_OPTIONS_ARRAY, null,  I18n.Samples_DomainBase, null, I18n.Samples_DomainBase_Description)
                
               .addIf((options & PROP_CHARSET) != 0,"charSet", "", Charset.availableCharsets().keySet().toArray(new String[Charset.availableCharsets().keySet().size()]), null,
                        I18n.General_CharSet, null, I18n.General_CharSet_Description)
               .addIf((options & PROP_LAZY) != 0, "lazy", true, null, I18n.Serializer_Lazy, null, I18n.Serializer_Lazy_Description)
               .addIf((options & PROP_EMPTY) != 0, "empty", true, null, I18n.Serializer_Empty, null, I18n.Serializer_Empty_Description)
               .addIf((options & PROP_HIERARCHY) != 0, "hierarchy", null, null, null, I18n.Serializer_HierarchyResolver, null, I18n.Serializer_HierarchyResolver_Description)
               .addIf((options & PROP_VECTOR) != 0, "vector", true, null, I18n.Serializer_VectorResolver, null, I18n.Serializer_VectorResolver_Description);
    }

    // ========================================================================================================================
    // Change Management
    // ========================================================================================================================

    /**
     * Notifies listeners about changes in the record with position information.
     * 
     * This method should be called when the record structure or content changes
     * during the parsing process, providing both the type of change and the current
     * position within the record.
     * 
     * @param changed Change information (IRecordReader.CHANGED_*)
     * @param units Current end position as a multiple of its domain base 
     *              (e.g. domain base=1ms; units = 100; -> domain value = 100ms)
     */
    public void changed(int changed, long units);

    /**
     * Notifies listeners about changes in the record without position information.
     * 
     * This method should be called when the record structure or content changes
     * during the parsing process, when the specific position is not relevant or unknown.
     * 
     * @param changed Change information (IRecordReader.CHANGED_*)
     */
    public void changed(int changed);

    /**
     * Returns the current position in the parsing process.
     * 
     * This method provides the current position within the record, expressed as
     * a multiple of the record's domain base unit.
     * 
     * @return The current position in domain base units
     */
    long current();
}
