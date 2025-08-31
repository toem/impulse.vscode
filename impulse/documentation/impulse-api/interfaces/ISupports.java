package de.toem.toolkits.pattern.general;

/**
 *
 * A general interface to expose supported capabilities from functional blocks
 * such as serializers (readers, writers) and other components. Implementing
 * classes can declare which specific functionality they support, allowing
 * for capability discovery at runtime.
 * 
 * This interface allows components to be queried for their capabilities
 * using the {@link #supports(Object, Object)} method with appropriate
 * request parameters.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 65
 */
public interface ISupports {
    /**
     * Constants defining the various types of support that can be provided
     * by implementing classes.
     */
    public interface Static {
        /** Indicates no support for any capability */
        public final static int SUPPORT_NONE = 0;
        
        /** Indicates support for handling configuration */
        public final static int SUPPORT_CONFIGURATION = 0x1000;  
        
        /** Indicates support for having property */
        public final static int SUPPORT_PROPERTIES = 0x2000;
        
        /** Indicates support beeing default functional block for given context */
        public final static int SUPPORT_DEFAULT = 0x4000;

        /** Indicates support for source level access */
        public final static int SUPPORT_SOURCE = 0x8000;
        
        /** Indicates support for native build processes */
        public final static int SUPPORT_NATIVE_BUILD = 0x10000;
        
    }
    
    /**
     * Determines if the implementing class supports the specified request
     * within the given context.
     * 
     * @param request The capability or feature being queried
     * @param context Additional contextual information that may influence 
     *                the support determination
     * @return {@code true} if the request is supported in the given context,
     *         {@code false} otherwise
     */
    boolean supports(Object request, Object context);
}
