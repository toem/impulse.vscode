package de.toem.toolkits.pattern.element.producer;

import java.util.LinkedHashMap;
import java.util.Map;

import de.toem.toolkits.pattern.element.ICell;
import de.toem.toolkits.pattern.element.ICover;
import de.toem.toolkits.pattern.threading.IProgress;
import de.toem.toolkits.pattern.threading.PartialProgress;
import de.toem.toolkits.pattern.threading.Progress;

/**
 * An interface for components that generate or produce ICell objects.
 * 
 * ICellProducer represents a generic producer of cell objects, which can be used
 * for various purposes such as reading or generating cells from different sources
 * (files, databases, calculations, etc.).
 * 
 * The producer can operate with different modes and flags to control how cells
 * are created, and it supports progress tracking and notification mechanisms
 * for long-running operations.
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 * docID: 69
 */
public interface ICellProducer extends ICellProducerDescriptor.DescriptedObject {

    // ========================================================================================================================
    // Root and Base
    // ========================================================================================================================

    /**
     * Returns the root cell produced by this producer.
     * 
     * @return The root cell, which serves as the entry point to the cell hierarchy
     */
    ICell getRoot();

    /**
     * Returns the base cell used by this producer.
     * 
     * @return The base cell, which provides context for production operations
     */
    ICell getBase();

    // ========================================================================================================================
    // Run
    // ========================================================================================================================
    
    /**
     * Executes the producer operation and returns a cover containing the produced cells.
     * 
     * @param progress A progress monitor for tracking the operation
     * @return A cover containing the produced cells
     */
    ICover run(IProgress progress);

    /**
     * Flag indicating that no insertion should be performed.
     */
    public final static int INSERT_NONE = 0;
    
    /**
     * Flag indicating that the root cell should be inserted.
     */
    public final static int INSERT_ROOT = 1;
    
    /**
     * Flag indicating that child cells should be inserted.
     */
    public final static int INSERT_CHILDREN = 2;

    /**
     * Executes the producer operation with a specified base cell and insertion flags.
     * 
     * @param progress A progress monitor for tracking the operation
     * @param base The base cell to use as context for the operation
     * @param flags Flags controlling how cells should be inserted (INSERT_NONE, INSERT_ROOT, INSERT_CHILDREN)
     * @return A cover containing the produced cells
     */
    ICover run(IProgress progress, ICell base, int flags);

    /**
     * Indicates that no changes have occurred.
     */
    public final static int CHANGED_NONE = 0;
    
    /**
     * Indicates that the current state has changed (e.g., for the value column).
     */
    public final static int CHANGED_STATE = 1;
    
    /**
     * Indicates that the range has changed (e.g., signal is longer).
     */
    public final static int CHANGED_RANGE = 2;
    
    /**
     * Indicates that data has changed (e.g., added samples).
     */
    public final static int CHANGED_FIELDS = 3;
    
    /**
     * Indicates that cells have changed (e.g., added signals).
     */
    public final static int CHANGED_CELLS = 4;
    
    /**
     * Indicates that a new Cover has been created.
     */
    public final static int CHANGED_COVER = 5;

    // ========================================================================================================================
    // Progess
    // ========================================================================================================================

    interface IFlushListener {
        void flushed(ICellProducer producer, ICover cover, int changed);
    }

    class ProducerProgress extends Progress implements IFlushListener {

        private IFlushListener listener;

        public ProducerProgress(String label, IFlushListener listener) {
            super(label);
            this.listener = listener;
        }

        @Override
        public void flushed(ICellProducer generator, ICover cover, int changed) {
            if (listener != null)
                listener.flushed(generator, cover, changed);
        }

        Map<PartialProgress, String> doing = new LinkedHashMap<>();

        public void doing(String doing, PartialProgress partial) {
            this.doing.put(partial, doing);
            String all = null;
            for (String v : this.doing.values())
                if (v != null)
                    all = (all == null ? "" : (all + "+")) + v;
            super.doing(all);
        }

    }

    class PartialProducerProgress extends PartialProgress implements IFlushListener {
        public PartialProducerProgress(ProducerProgress progress, double partial) {
            super(progress, partial);
        }

        @Override
        public void flushed(ICellProducer generator, ICover cover, int changed) {
            ((IFlushListener) getReference()).flushed(generator, cover, changed);
        }

        @Override
        public void doing(String doing) {
            if (getReference() instanceof ProducerProgress)
                ((ProducerProgress) getReference()).doing(doing, this);
            else
                super.doing(doing);
        }

        public boolean start() {
            return true;
        }

        public void end() {
            if (getReference() instanceof ProducerProgress)
                ((ProducerProgress) getReference()).doing(null, this);
        }
    }

}
