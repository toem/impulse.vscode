package de.toem.impulse.samples;

import java.util.ArrayList;
import java.util.List;

import de.toem.impulse.cells.record.AbstractSignalProviderCell;
import de.toem.impulse.cells.record.IRecord;
import de.toem.impulse.cells.record.RecordProxy;
import de.toem.impulse.cells.record.RecordScope;
import de.toem.impulse.cells.record.RecordSignal;
import de.toem.impulse.samples.base.PackedSamples;
import de.toem.impulse.samples.domain.IDomainBase;
import de.toem.impulse.samples.writer.DivergingSamplesWriter;
import de.toem.impulse.samples.writer.IConvergingSamplesWriter;
import de.toem.impulse.samples.writer.IDivergingSamplesWriter;
import de.toem.toolkits.pattern.element.ICell;
import de.toem.toolkits.pattern.element.IElement;
import de.toem.toolkits.pattern.element.producer.ICellProducer;

/**
 * Interface for record production in the impulse framework.
 *
 * This interface provides a comprehensive API for creating and managing records, scopes, signals, and writers.
 * Records are hierarchical containers for signal data, and this interface allows users to build, manipulate,
 * and manage these structures in a systematic and efficient manner.
 *
 * Key features of this interface include:
 * - Creation and management of record hierarchies with scopes and signals
 * - Generation of signal writers for different signal types
 * - Support for different domain bases and signal characteristics
 * - Creation of proxies to reference existing signals
 * - Management of writers and readers for signal data
 *
 * The `IRecordProducer` interface is designed to be used in scenarios where signal data needs to be
 * generated, organized and stored, such as in simulation environments, data acquisition systems,
 * or test frameworks. It provides a structured approach to building record hierarchies that can
 * be used to store and access signal data.
 *
 * Implementations of this interface are expected to handle the creation and management of complex
 * record structures, supporting various signal types and domain bases. The interface also provides
 * mechanisms for applying changes to signals and managing the associated writers.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 37
 */
public interface IRecordProducer extends ICellProducer {

    public final static int CHANGED_VALUE = CHANGED_STATE;  // current value has changed (e.g. for the value column)
    public final static int CHANGED_CURRENT = CHANGED_RANGE;  // signal has changed in domain (e.g. signal is longer)
    public final static int CHANGED_SIGNALS = CHANGED_FIELDS;  // signal has changed in value (e.g. added samples)
    public final static int CHANGED_RECORD = CHANGED_CELLS;  // record has changed (e.g. added signals)
    
    public final static String PRODUCER = "producer";
    public final static String WRITER = "writer";

    /**
     * Returns the root record of this producer.
     *
     * The root record is the top-level container that holds all scopes and signals
     * managed by this producer. It serves as the entry point for traversing the entire
     * record hierarchy and provides access to all contained elements. The root record
     * establishes the global context for all signals and scopes within this producer's
     * domain and typically defines common properties that apply to all contained elements.
     *
     * @return the root record of this producer, which is the top-level container for all signals and scopes
     */
    IRecord.Record getRoot();

    /**
     * Returns the base cell for this record producer.
     * 
     * The base cell serves as the fundamental structural element for the record hierarchy.
     * It provides the core functionality needed to locate, traverse, and manipulate
     * the record structure. All operations that involve locating elements by path
     * or manipulating the hierarchy start from this base cell.
     * 
     * @return the base cell that serves as the foundation for the record structure
     */
    ICell getBase();

    
    default boolean isLazy() {
        return false;
    }
    
    // ========================================================================================================================
    // Record
    // ========================================================================================================================

    /**
     * Initializes the record with the specified name.
     * 
     * This method sets the name of the root record, effectively naming the entire
     * record structure. The name serves as an identifier for the record and is typically
     * used for display purposes or when referencing the record in various contexts.
     * 
     * If the root record does not exist, this method will have no effect. It's recommended
     * to call this method early in the initialization process before adding any scopes
     * or signals to establish the identity of the record structure.
     * 
     * @param name the name to be assigned to the root record, providing identity to the entire record structure
     */
    default void initRecord(String name) {
        IRecord.Record record = getRoot();
        if (record != null)
            record.setName(name);
    }

    // ========================================================================================================================
    // Scopes
    // ========================================================================================================================
    /**
     * Adds a new scope to the specified container.
     * 
     * A scope is a hierarchical grouping mechanism for organizing signals within a record.
     * Scopes can be nested to create a tree structure that helps categorize and manage signals
     * in a logical manner. This method creates a new scope with the given name and adds it
     * to the specified container.
     * 
     * If the provided container is null, the scope will be added to the base cell of this producer.
     * The method ensures that the scope has a unique name within its container by automatically
     * adjusting the name if needed.
     * 
     * @param container Parent cell to which the new scope will be added, or null to add to the base cell
     * @param name Name to assign to the new scope
     * @return The newly created and added IRecord.Scope instance
     */
    default IRecord.Scope addScope(ICell container, String name) {
        if (container == null)
            container = getBase();
        RecordScope child = new RecordScope();
        container.addChild(child);
        child.setName(container.uniqueChildName(name));
        return child;
    }

    /**
     * Adds a new scope to the specified container with a description.
     * 
     * This method extends the basic scope creation functionality by allowing the association
     * of a descriptive text with the scope. The description provides additional context or 
     * information about the purpose of the scope, helping to document the record structure.
     * 
     * If the provided container is null, the scope will be added to the base cell of this producer.
     * The method ensures that the scope has a unique name within its container by automatically
     * adjusting the name if needed. The description is optional and can be null.
     * 
     * @param container Parent cell to which the new scope will be added, or null to add to the base cell
     * @param name Name to assign to the new scope
     * @param description Additional text describing the purpose or content of the scope
     * @param tags Optional tags associated with the signal for categorization or filtering
     * @return The newly created and added IRecord.Scope instance
     */
    default IRecord.Scope addScope(ICell container, String name, String description, String tags) {
        if (container == null)
            container = getBase();
        RecordScope child = new RecordScope();
        container.addChild(child);
        child.setName(container.uniqueChildName(name));
        child.description = description != null ? description.intern() : null;
        child.tags =tags;
        return child;
    }

    /**
     * Retrieves a scope from the record structure using the specified URI path.
     * 
     * This method navigates the record structure to find and return the scope identified
     * by the given URI. The URI path follows the hierarchical structure of the record,
     * with path segments separated by backslashes (\).
     * 
     * If the base cell is null or if no scope is found at the specified path, 
     * this method returns null.
     * 
     * @param uri The URI path identifying the location of the scope within the record structure
     * @return The scope found at the specified path, or null if not found or if the base cell is null
     */
    default IRecord.Scope getScope(String uri) {
        ICell base = getBase();
        return  base != null ? base.get(uri,IRecord.Scope.class) : null;
    }

    /**
     * Removes empty scopes from the record structure.
     * 
     * This method traverses the record structure starting from the specified container
     * and removes any scope that does not contain any children (signals or other scopes).
     * This is useful for cleaning up the record structure after operations that might
     * leave orphaned or empty scopes.
     * 
     * The method recursively processes all child scopes before determining if the current
     * scope should be removed. If the 'included' parameter is true, the container itself
     * may be removed if it is a IRecord.Scope, has no children, and has a parent container.
     * 
     * @param container The starting container for the removal process
     * @param included Flag indicating whether the container itself should be considered for removal
     */
    default void removeEmptyScopes(ICell container, boolean included) {
        for (IRecord.Scope scope : container.getChildren(IRecord.Scope.class)) {
            removeEmptyScopes(scope, true);
        }
        if (container instanceof IRecord.Scope && included && !container.hasChildren() && container.getCellContainer() != null)
            container.getCellContainer().removeChild(container);
    }
    
    /**
     * Assert that a scope path exists, creating any missing scopes in the hierarchy.
     * 
     * @param path The path with '/' separators
     * @return The scope at the given path
     */
    default IRecord.Scope assertScope(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        
        // Split the path into components
        String[] components = path.split(IElement.PATH_SEPERATOR);
        IRecord.Scope currentScope = null;
        StringBuilder currentPath = new StringBuilder();
        
        // Create or retrieve each scope in the hierarchy
        for (int i = 0; i < components.length; i++) {
            String component = components[i];
            
            // Skip empty components (e.g., if path starts or ends with '/')
            if (component.isEmpty()) {
                continue;
            }
            
            // Build the path up to this component
            if (currentPath.length() > 0) {
                currentPath.append(IElement.PATH_SEPERATOR);
            }
            currentPath.append(component);
            
            // Check if the scope exists
            IRecord.Scope existingScope = getScope(currentPath.toString());
            
            if (existingScope != null) {
                // Use existing scope
                currentScope = existingScope;
            } else {
                // Create new scope
                IRecord.Scope parentScope = currentScope; // may be null for root level
                currentScope = addScope(parentScope, component);
            }
        }
        
        return currentScope;
    }
    
    default IRecord.Scope upScope(IRecord.Scope scope) {
        return scope != null ? scope.getContainer(IRecord.Scope.class):null;
    }
    
    default IRecord.Scope resolveHierarchies(ICell container, String regEx, String hname){

        try {
            RecordScope hierarchy = new RecordScope();
            hierarchy.setName(hname != null ? hname:"Hierarchy");
            // Process all signals and create hierarchy
            for (ICell cell : container.getTribe(false, AbstractSignalProviderCell.class)) {
                if (cell.getData(PRODUCER) != this)
                    continue;
                RecordSignal signal = null;
                if (cell instanceof RecordSignal)
                    signal = (RecordSignal) cell;
                else if (cell instanceof RecordProxy)
                    signal = ((RecordProxy) cell).getSignal();
                String name = signal.getName();
                String description = signal.description;
                // Split hierarchical name and create nested scopes
                String[] splitted = name.split(regEx);
                RecordScope scope = hierarchy;
                for (int n = 0; n < splitted.length - 1; n++) {
                    if (scope.getChild(splitted[n]) != null)
                        scope = (RecordScope) scope.getChild(splitted[n]);
                    else {
                        RecordScope next = new RecordScope();
                        next.setName(splitted[n]);
                        scope.addChild(next);
                        scope = next;
                    }
                }
                // Create proxy for the signal
                RecordProxy proxy = new RecordProxy();
                proxy.setName(splitted[splitted.length - 1]);
                proxy.description = description;
                proxy.signal = signal.getCellUri(container.getCellRoot(), null);
                scope.addChild(proxy);
            }
            if (hierarchy.getNoOfChildren() > 0)
                container.insertChild(hierarchy, 0);
            return hierarchy;
        } catch (Throwable e) {
        }
        return null;
    }
    // ========================================================================================================================
    // Signals
    // ========================================================================================================================

    /**
     * Adds a new signal to the specified container with automatic writer creation.
     * 
     * This method creates a new signal with the specified properties and adds it to the
     * given container. It automatically creates a writer for the signal to facilitate
     * data entry. This is a convenience method that delegates to the more detailed
     * addSignal method with createWriter set to true.
     * 
     * The signal's properties include its name, description, tags, signal type, scale,
     * format, and domain base. These properties define the nature and behavior of the signal
     * within the record structure.
     * 
     * @param container Parent cell to which the new signal will be added, or null to add to the base cell
     * @param name Name to assign to the new signal
     * @param description Additional text describing the purpose or content of the signal
     * @param tags Optional tags associated with the signal for categorization or filtering
     * @param signalType The type of signal data (one of the ISample constants)
     * @param signalType The type of signal data from ISample constants (Logic, Float, Integer, etc.)
     * @param scale Defines the dimension of the signal, such as bit width for logic signals or dimensions for arrays
     * @param format Defines how signal values are represented textually from ISample constants (e.g., "binary", "hex", "decimal")
     * @param domainBase Indicates the minimum distance between two samples, typically measured in units like nanoseconds (ns) or picoseconds (ps)
     * @return The newly created and added IRecord.Signal instance
     */
    default IRecord.Signal addSignal(ICell container, String name, String description,  String tags,
            int signalType, int scale, String format, IDomainBase domainBase) {
        return addSignal(container, name, description,  null, signalType, scale, format, domainBase, true);
    }

    /**
     * Adds a new signal to the specified container with optional writer creation.
     * 
     * This method creates a new signal with the specified properties and adds it to the
     * given container. It allows control over whether a writer should be automatically
     * created for the signal through the createWriter parameter.
     * 
     * The signal's properties include its name, description, tags, signal type, scale,
     * format, and domain base. These properties define the nature and behavior of the signal
     * within the record structure. If createWriter is true, a writer appropriate for the
     * signal type is created and associated with the signal.
     * 
     * @param container Parent cell to which the new signal will be added, or null to add to the base cell
     * @param name Name to assign to the new signal
     * @param description Additional text describing the purpose or content of the signal
     * @param tags Optional tags associated with the signal for categorization or filtering
     * @param signalType The type of signal data from ISample constants (Logic, Float, Integer, etc.)
     * @param scale Defines the dimension of the signal, such as bit width for logic signals or dimensions for arrays
     * @param format Defines how signal values are represented textually from ISample constants (e.g., "binary", "hex", "decimal")
     * @param domainBase Indicates the minimum distance between two samples, typically measured in units like nanoseconds (ns) or picoseconds (ps)
     * @param createWriter Flag indicating whether a writer should be automatically created for the signal
     * @return The newly created and added IRecord.Signal instance
     */
    default IRecord.Signal addSignal(ICell container, String name, String description,  String tags,
            int signalType, int scale, String format, IDomainBase domainBase, boolean createWriter) {
        if (container == null)
            container = getBase();
        RecordSignal signal = new RecordSignal();
        signal.setData(PRODUCER, this);
        container.addChild(signal);
        signal.setName(container.uniqueChildName(name));
        signal.description = description != null ? description.intern() : null;
        signal.tags = tags != null ? tags.intern() : null;
        signal.sampleType = signalType;
        signal.scale = scale;
        signal.format = format;
        signal.domainBase = domainBase != null ? domainBase.toString() : null;
        if (createWriter) {
            ISamplesWriter writer = PackedSamples.createWriter(signal.getCellUri(), signal.getName(),signal.description,  signal.tags, signalType, scale,format,
                    domainBase, false);
            // writers.add(writer);
            signal.setData(WRITER, writer);
        }
        return signal;
    }

    /**
     * Creates a proxy referencing an existing signal and adds it to the specified container.
     * 
     * Signal proxies provide a way to reference an existing signal from another location in
     * the record structure without duplicating the signal data. This is useful for creating
     * alternate views or organizations of signals while maintaining a single source of truth
     * for the signal data.
     * 
     * The proxy is added to the specified container and given the specified name and description.
     * It references the target signal, allowing access to the signal's data through the proxy.
     * 
     * @param container Parent cell to which the new proxy will be added, or null to add to the base cell
     * @param name Name to assign to the new proxy
     * @param description Additional text describing the purpose or content of the proxy
     * @param signal The target signal that this proxy will reference
     * @return The newly created and added IRecord.Proxy instance
     */
    default IRecord.Proxy addSignalProxy(ICell container, String name, String description, IRecord.Signal signal) {
        if (container == null)
            container = getBase();
        IRecord.Record record = getRoot();
        RecordProxy proxy = new RecordProxy();
        container.addChild(proxy);
        proxy.setName(container.uniqueChildName(name));
        proxy.description = description != null ? description.intern() : null;
        proxy.signal = signal != null ? signal.getCellUri(record, null) : null;
        return proxy;
    }

    /**
     * Retrieves a list of all signals produced by this record producer.
     * 
     * This method collects and returns all signal cells within the record structure that
     * were created by this producer. It filters out signals that might have been created
     * by other producers, ensuring that only signals associated with this producer are included.
     * 
     * This is useful for operations that need to process or manipulate all signals produced
     * by this producer, such as bulk updates or data export.
     * 
     * @return A list containing all signal cells created by this producer
     */
    default List<IRecord.Signal> getAllSignals() {
        List<IRecord.Signal> list = new ArrayList<>();
        ICell base = getBase();
        if (base != null)
            for (ICell cell : base.getTribe(false, IRecord.Signal.class)) {
                if (cell.getData(PRODUCER) == this)
                    list.add((IRecord.Signal)cell);
            }
        return list;
    }

    /**
     * Retrieves a signal from the record structure using the specified path.
     * 
     * This method navigates the record structure to find and return the signal identified
     * by the given path. The path follows the hierarchical structure of the record,
     * with path segments typically separated by slashes.
     * 
     * If the base cell is null or if no signal is found at the specified path, 
     * this method returns null.
     * 
     * @param path The path identifying the location of the signal within the record structure
     * @return The signal found at the specified path, or null if not found or if the base cell is null
     */
    default IRecord.Signal getSignal(String path) {
        ICell base = getBase();
        return base != null ? base.get(path, IRecord.Signal.class) : null;
    }

    // ========================================================================================================================
    // Writers
    // ========================================================================================================================

    /**
     * Creates a writer for the specified signal.
     * 
     * This method creates a samples writer appropriate for the given signal and associates
     * it with the signal. The writer is configured based on the signal's properties, such as
     * signal type, scale, and format.
     * 
     * The created writer provides methods for adding and manipulating sample data for the signal.
     * After using the writer to add data, the apply() method should be called to update the
     * signal with the new data.
     * 
     * @param signal The signal for which to create a writer
     * @return The newly created samples writer associated with the signal
     */
    default ISamplesWriter createWriter(IRecord.Signal signal) {
        ISamplesWriter writer = PackedSamples.createWriter(signal, false);
        signal.setData(WRITER, writer);
        return writer;
    }

    /**
     * Creates a writer for the specified signal with custom properties.
     * 
     * This method creates a samples writer for the given signal using the specified
     * properties instead of the signal's own properties. This allows creating writers
     * with different characteristics than the signal's default properties.
     * 
     * The created writer is associated with the signal and provides methods for adding
     * and manipulating sample data. After using the writer to add data, the apply() method
     * should be called to update the signal with the new data.
     * 
     * @param signal The signal for which to create a writer
     * @param signalType The type of signal data from ISample constants (Logic, Float, Integer, etc.)
     * @param scale Defines the dimension of the signal, such as bit width for logic signals or dimensions for arrays
     * @param format Defines how signal values are represented textually from ISample constants (e.g., "binary", "hex", "decimal")
     * @param domainBase Indicates the minimum distance between two samples, typically measured in units like nanoseconds (ns) or picoseconds (ps)
     * @return The newly created samples writer associated with the signal
     */
    default ISamplesWriter createWriter(IRecord.Signal signal,  int signalType, int scale, String format,
            IDomainBase domainBase) {
        ISamplesWriter writer = PackedSamples.createWriter(signal.getCellUri(), signal.getName(), signal.getDescription(),  signal.getTags(), signalType, scale,  format,
                domainBase, false);
        signal.setData(WRITER, writer);
        return writer;
    }

    /**
     * Creates a converging writer for the specified signal with custom properties.
     * 
     * A converging writer is a specialized type of writer that combines multiple data sources
     * into a single signal. This method creates a converging writer for the given signal
     * using the specified properties instead of the signal's own properties.
     * 
     * The created writer is associated with the signal and provides methods for adding
     * and manipulating sample data from multiple sources. After using the writer to add data,
     * the apply() method should be called to update the signal with the new data.
     * 
     * @param signal The signal for which to create a converging writer
     * @param signalType The type of signal data from ISample constants (Logic, Float, Integer, etc.)
     * @param scale Defines the dimension of the signal, such as bit width for logic signals or dimensions for arrays
     * @param format Defines how signal values are represented textually from ISample constants (e.g., "binary", "hex", "decimal")
     * @param domainBase Indicates the minimum distance between two samples, typically measured in units like nanoseconds (ns) or picoseconds (ps)
     * @return The newly created converging samples writer associated with the signal
     */
    default IConvergingSamplesWriter createConvergingWriter(IRecord.Signal signal,  int signalType,
            int scale, String format, IDomainBase domainBase) {
        IConvergingSamplesWriter writer = (IConvergingSamplesWriter) PackedSamples.createWriter(signal.getCellUri(), signal.getName(), 
                signal.getDescription(),  signal.getTags(), signalType,  scale,  format, domainBase, true);
        signal.setData(WRITER, writer);
        return writer;
    }

    /**
     * Retrieves the writer associated with the specified signal.
     * 
     * This method returns the samples writer that was previously created for the given
     * signal, typically through one of the addSignal() or createWriter() methods. The
     * writer provides methods for adding and manipulating sample data for the signal.
     * 
     * If no writer has been associated with the signal, this method returns null.
     * 
     * @param signal The signal whose associated writer is to be retrieved
     * @return The samples writer associated with the signal, or null if no writer is associated
     */
    default ISamplesWriter getWriter(IRecord.Signal signal) {
        return (ISamplesWriter) signal.getData(WRITER);
    }

    // ========================================================================================================================
    // Reader
    // ========================================================================================================================

    /**
     * Creates a reader for the specified signal.
     * 
     * This method creates a samples reader configured for the given signal. The reader
     * provides methods for accessing and examining the sample data associated with the signal.
     * 
     * The reader is created based on the signal's properties, such as signal type and format,
     * ensuring that the data is correctly interpreted. Unlike writers, readers are not
     * typically stored as signal data but are created as needed for data access.
     * 
     * @param signal The signal for which to create a reader
     * @return The newly created samples reader for the signal
     */
    default ISamplesReader createReader(IRecord.Signal signal) {
        return PackedSamples.createReader(signal);
    }

    // ========================================================================================================================
    // Apply to signals
    // ========================================================================================================================

    /**
     * Applies the data from all writers to their respective signals.
     * 
     * This method iterates through all signals created by this producer that have
     * associated writers. For each such signal, it updates the signal's data with
     * the current content of its writer. This effectively commits all changes made
     * through writers to the signal data.
     * 
     * This method should be called after using writers to add or modify signal data
     * and before accessing that data through signals or readers. It ensures that all
     * signal data is up-to-date with the latest changes made through writers.
     */
    default void apply() {
        ICell base = getBase();
        if (base != null)
            for (IRecord.Signal signal : base.getTribe(false, IRecord.Signal.class)) {
                if (signal.getData(PRODUCER) != this)
                    continue;
                ISamplesWriter writer = (ISamplesWriter) signal.getData(WRITER);
                if (writer != null)
                    signal.update(writer);
            }
    }


    
    // ========================================================================================================================
    // Internal use only
    // ========================================================================================================================

    /**
     * Creates a diverging writer with an optional initial target. (Internal use only)
     * 
     * A diverging writer is a specialized type of writer that can distribute signal data
     * to multiple target writers. This method creates a new diverging writer and optionally
     * sets an initial target writer to receive the data.
     * 
     * The diverging writer can be used to send the same signal data to multiple signals
     * without duplicating the data creation logic. Additional targets can be added to the
     * diverging writer after it is created.
     * 
     * @param initialTarget The initial target writer to receive data, or null for no initial target
     * @return The newly created diverging samples writer
     */
    default IDivergingSamplesWriter createDivergingWriter(ISamplesWriter initialTarget) {
        IDivergingSamplesWriter writer = initialTarget != null ? new DivergingSamplesWriter(initialTarget) : new DivergingSamplesWriter();
        return writer;
    }
}
