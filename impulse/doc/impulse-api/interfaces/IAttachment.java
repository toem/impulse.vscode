package de.toem.impulse.samples.attachments;

import de.toem.impulse.samples.domain.DomainValue;
import de.toem.toolkits.pattern.json.IJsonBase;

/**
 * Interface for signal data attachments in the impulse framework.
 *
 * Attachments provide additional metadata and context to signal samples, enabling richer
 * data representation and visualization. They allow users to add annotations, relations,
 * and visual cues to samples, enhancing the interpretability and usability of signal data.
 *
 * Key features of this interface include:
 * - Support for different attachment types, including labels and relations
 * - Methods for accessing visual styling attributes such as colors and symbols
 * - Access to source and target positions for relational attachments
 * - Configuration options for attachment display and representation
 *
 * The `IAttachment` interface serves as the foundation for all attachment types in the
 * impulse framework, providing common properties and behaviors. It is extended by
 * specialized interfaces like `IAttachedLabel` and `IAttachedRelation` to support
 * different attachment use cases.
 *
 * Attachments can be used in various scenarios, such as:
 * - Adding descriptive labels to specific samples of interest
 * - Creating relationships between samples in the same or different signals
 * - Visually highlighting important events or patterns in signal data
 * - Providing additional context for understanding complex signal behaviors
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 7
 */
public interface IAttachment extends IJsonBase {

    /**
     * Gets the visual style identifier for this attachment.
     * 
     * Style pattern format depends on the attachment type:
     * 
     * For {@link IAttachedRelation}: {#}message{/color{/lineStyle{/arrowStyle{/symbol}{/sourceLabel}{/targetLabel}}}}
     * For {@link IAttachedLabel}: {#}message{/color{/symbol}}
     * 
     * Style elements explanation:
     * 
     * # (optional prefix): When present, suppresses the display of the message
     * color: An integer index (0..max) referencing a color in the tag palette
     * lineStyle (for relations): Available values are straight (default), dashdot, dot, dash
     * arrowStyle (for relations): Available values are normal (default), dot
     * symbol: Visual symbol identifier used in the attachment representation
     * sourceLabel, targetLabel (for relations): Additional labels at source and target points
     * 
     * @return The style identifier string used for rendering the attachment
     */
    String getStyle();
    
    /**
     * Determines whether the attachment message should be displayed.
     * 
     * @return true if the message should be shown, false otherwise
     */
    boolean showMessage();

    /**
     * Gets the descriptive message associated with this attachment.
     * 
     * @return The attachment message text
     */
    String getMessage();

    /**
     * Gets the color index used for visual representation of this attachment.
     * 
     * @return An integer representing the color index in the color palette
     */
    int getColorIdx();

    /**
     * Gets the symbol used to visually represent this attachment.
     * 
     * @return The symbol string identifier
     */
    String getSymbol();
    
    /**
     * Formats the attachment information according to the specified format.
     * 
     * @param format The format specifier to use
     * @return A formatted string representation of the attachment
     */
    String format(int format);
    
    /**
     * Gets the domain position where this attachment is anchored in the source signal.
     * 
     * @return The domain value representing the source position
     */
    DomainValue getSourcePosition();  

    /**
     * Gets the index of the sample in the source signal where this attachment is anchored.
     * 
     * @return The index of the source sample
     */
    int getSourceIdx();
    
    /**
     * Gets the content identifier of the source attachment point.
     * 
     * @return The content identifier
     */
    int getSourceContent();
    
    /**
     * Interface for label attachments in the impulse framework.
     * 
     * Label attachments provide textual annotations for specific samples in a signal.
     * They are used to add contextual information, highlight important events, or
     * provide human-readable descriptions of signal features.
     * 
     * Labels inherit all properties from the base IAttachment interface and can be
     * styled, colored, and positioned like other attachments.
     */
    public interface IAttachedLabel extends IAttachment {

    }

    /**
     * Interface for relational attachments in the impulse framework.
     * 
     * Relation attachments create connections between samples, either within the same signal
     * or across different signals. They are useful for showing causal relationships,
     * dependencies, or logical connections between events represented by samples.
     * 
     * Relations include properties for specifying source and target positions, visual styling
     * of connection lines and arrows, and formatted display options.
     * 
     */
    public interface IAttachedRelation extends IAttachment {

        /**
         * Format constant indicating the default formatting approach should be used.
         */
        public static final int FORMAT_DEFAULT = -1;
        
        /**
         * Format constant indicating only the target information should be displayed.
         */
        public static final int FORMAT_TARGET = 1;
        
        /**
         * Format constant indicating both the message and target information should be displayed.
         */
        public static final int FORMAT_MESSAGE_TARGET = 2;
        
        /**
         * Format constant indicating the source, message, and target information should all be displayed.
         */
        public static final int FORMAT_SOURCE_MESSAGE_TARGET = 3;
        
        /**
         * Gets the type identifier for this relation.
         * 
         * @return An integer representing the relation type
         */
        int getType();
        
        /**
         * Determines if this is a reverse relation (target to source rather than source to target).
         * 
         * @return true if the relation is reversed, false otherwise
         */
        boolean isReverse();
        
        /**
         * Checks if this relation has a defined target position.
         * 
         * @return true if a target position is defined, false otherwise
         */
        boolean hasTargetPos(); 
        
        /**
         * Determines if this relation represents a delta (difference) between positions.
         * 
         * @return true if the relation is a delta, false otherwise
         */
        boolean isDelta(); 

        /**
         * Checks if this relation has associated target content.
         * 
         * @return true if target content is defined, false otherwise
         */
        boolean hasTargetContent();
        
        /**
         * Gets the line style used for rendering the relation.
         * 
         * @return The line style identifier
         */
        String getLineStyle();

        /**
         * Gets the arrow style used for rendering the relation endpoint.
         * 
         * @return The arrow style identifier
         */
        String getArrowStyle();
        
        /**
         * Gets the identifier of the target signal or element.
         * 
         * @return The target ID string
         */
        String getTargetId();

        /**
         * Gets the domain position of the target attachment point.
         * 
         * @return The domain value representing the target position
         */
        DomainValue getTargetPosition();
        
        /**
         * Gets the absolute domain position of the target attachment point, 
         * resolving any relative positioning.
         * 
         * @return The absolute domain value of the target position
         */
        DomainValue getAbsoluteTargetPosition();
        
        /**
         * Gets the content identifier of the target attachment point.
         * 
         * @return The target content identifier
         */
        int getTargetContent();
        
        /**
         * Gets the index of the sample in the target signal where this attachment is anchored.
         * 
         * @return The index of the target sample
         */
        int getTargetIdx();

        /**
         * Gets the URI associated with this relation, if any.
         * 
         * @return The URI string, or null if not defined
         */
        String getUri();


    }


}
