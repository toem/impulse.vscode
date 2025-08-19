---
title: "How-To Guides"
author: "Thomas Haber"
keywords: [how-to, guides, instructions, tutorials, impulse, signal file, configuration, visualization, troubleshooting, workflows, examples]
description: "A collection of practical step-by-step instructions for common tasks in impulse. Organized by functionality, these guides help users accomplish specific goals efficiently, from basic operations to advanced signal analysis techniques."
category: "impulse-manual"
tags:
  - manual
  - tutorial
  - examples
docID: 18
---

# How-To Guides

This section provides practical step-by-step instructions for common tasks in impulse. These guides are organized by functionality and are designed to help you accomplish specific goals efficiently.

## Getting Started

### How to open a signal file in impulse
1. Click on the "Open File" button in the toolbar or use the menu option File > Open.
2. Select the signal file (wave, trace, or log) that you want to open.
3. impulse will automatically detect the file type and select the appropriate reader.
4. The file content will be loaded into the Record Area.

### How to configure file reader settings
1. After opening a file, locate the Source Settings section in the Record Area.
2. Click the edit button (pencil icon) next to the reader name.
3. In the configuration dialog, adjust settings such as:
   - Signal filtering options
   - Time range restrictions
   - Reader-specific parameters
4. Click "Apply" to update the file view with your new settings.

### How to navigate the workspace efficiently
1. Use the Record Area to manage and filter signals.
2. Use the View Area to visualize and analyze signals.
3. Toggle the visibility of components using toolbar buttons:
   - Show/Hide Record Area
   - Show/Hide Cursor Details
   - Show/Hide Axis, Grid, or Value Column
4. Enable "Fit Vertical" to automatically adjust signal height to fit the viewer.

## Working with Signals

### How to filter signals in the Record Area
1. Locate the Filter field in the Signal Table section of the Record Area.
2. Enter your filter criteria using one of these methods:
   - **Text fragments**: Enter `abc` to find signals containing "abc"
   - **Regular expressions**: Enter patterns like `ab[0-9]n?` for more complex matching
   - **Numeric expressions**: Use conditions like `0.4 < v < 2.0` or `v == 1000`
3. The Signal Table will update to show only matching signals.
4. To apply filters to all children in a hierarchy, check the "All Children" box.

### How to add signals to a view
1. Filter signals in the Record Area to find the ones you want to visualize.
2. Use one of these methods to add signals to the View Area:
   - Drag and drop signals from the Signal Table to the View Tree
   - Right-click on signals and select "Add to View"
   - Select multiple signals and use toolbar buttons to add them
3. The signals will appear in the View Tree and corresponding diagrams will be shown.

### How to create derived signals
1. In the View Area, right-click and select "New Signal" or use the "Add Signal" button.
2. In the signal configuration:
   - Select source signals from the record
   - Choose a signal processor to combine or transform them
   - Configure the processor parameters
3. Name your new signal and click "Create".
4. The derived signal will appear in the View Tree with a corresponding diagram.

### How to customize signal appearance
1. Right-click on a signal in the View Tree and select "Properties".
2. In the properties dialog, you can modify:
   - Signal name and description
   - Diagram type (line, step, bar, etc.)
   - Color and style
   - Value format (decimal, hex, binary, etc.)
3. Switch between the Property Table tab for comprehensive options or the UI Fields tab for common settings.
4. Click "Apply" to update the signal appearance.

## Working with Views

### How to create a new view
1. Click the "Add New View" button or use menu View > New View.
2. In the View Dialog, enter a name for your view.
3. Configure basic view properties such as domain axis and default diagram type.
4. Click "Create" to add the view to the workspace.

### How to organize signals in the View Tree
1. Right-click in the View Tree and select "New Folder".
2. Name the folder to categorize related signals.
3. Drag and drop signals into the folder.
4. Use the folder checkboxes to show/hide groups of signals at once.
5. Rearrange signals and folders by dragging them to new positions in the tree.

### How to synchronize multiple views
1. Create multiple views for different aspects of your analysis.
2. Select "View > Link Views" or use the equivalent toolbar button.
3. Choose which aspects to synchronize:
   - Domain position (e.g., time alignment)
   - Zoom level
   - Cursor positions
4. Navigate in any linked view, and the others will update accordingly.

### How to export view data
1. Right-click in the View Area and select "Export" or use File > Export.
2. Choose from export options:
   - Export visible data as CSV
   - Export as image (PNG, JPG, SVG)
   - Copy current view to clipboard
3. Configure export parameters such as resolution or data range.
4. Select destination and complete the export.

## Working with Cursors

### How to add and position cursors
1. Right-click on the domain axis and select "Add Cursor" or use the cursor toolbar button.
2. Click in the diagram area to position the cursor.
3. Fine-tune cursor position by:
   - Dragging the cursor line
   - Entering an exact value in the Cursor Details panel
   - Using keyboard shortcuts for precise movements

### How to measure between cursors
1. Place at least two cursors at points of interest in your signals.
2. View the Cursor Details panel to see measurements between cursors:
   - Delta values (difference between cursor positions)
   - Time or frequency measurements
   - Rate calculations (for time domain signals)
3. Right-click on the delta value for additional measurement options.

### How to create cursor markers
1. Position a cursor at a point of interest.
2. Right-click on the cursor and select "Add Marker" or "Create Annotation".
3. Enter a descriptive name or note for the marker.
4. The marker will appear on the cursor line and in the Cursor Details panel.
5. Use markers to highlight important events or save positions for later reference.

## Using Sample Tables

### How to open and configure Sample Tables
1. Click on "Tools > Sample Table" or use the Sample Table toolbar button.
2. Select the input signal(s) for the table:
   - Choose a single signal for detailed sample inspection
   - Select multiple signals to view them together
3. Configure the table columns:
   - Enable or disable columns through the Part Configuration menu
   - Reorder columns by dragging them
   - Add custom columns for specialized data

### How to filter samples in Sample Tables
1. Click the filter icon in the Sample Table toolbar.
2. Enter filter criteria:
   - Text fragments to match sample values
   - Regular expressions for pattern matching
   - Numeric expressions to isolate value ranges
3. The table will update to show only samples matching your filter.
4. Use multiple filters to refine results further.

### How to use the Sample Inspector with tables
1. Open a Sample Table with your signals of interest.
2. Click on any sample in the table to select it.
3. Open the Sample Inspector (Tools > Sample Inspector).
4. The inspector will display detailed information about the selected sample:
   - Full value representation
   - Metadata and attachments
   - Related samples and contextual information
5. Navigate through samples in the table to update the inspector view.

## Signal Analysis Techniques

### How to perform basic signal measurements
1. Add signals to the View Area.
2. Place cursors at points of interest.
3. Use the Cursor Details panel to measure:
   - Maximum and minimum values
   - Peak-to-peak amplitude
   - Time intervals
   - Frequency (for periodic signals)
4. Right-click on signals to access quick measurement options.

### How to decode protocol data
1. Identify the signal(s) containing protocol data.
2. Right-click and select "Apply Processor" or add a new signal with processing.
3. Choose the appropriate protocol decoder:
   - I2C, SPI, UART for common interfaces
   - Custom protocols as available
4. Configure decoder parameters (bit rate, polarity, etc.).
5. View the decoded data in the new signal or in a Sample Table.

### How to correlate signals from different sources
1. Load data from multiple sources (files, interfaces).
2. Ensure timing alignment through one of these methods:
   - Use common timestamps across sources
   - Align on known synchronization events
   - Apply time offset adjustment
3. Add signals from different sources to the same view.
4. Use cursors to measure relationships between signals.
5. Create derived signals that combine data across sources.

## Customizing the Workspace

### How to save and restore workspace layouts
1. Configure your workspace with preferred views, signal arrangements, and visible components.
2. Go to File > Save Workspace or use the equivalent toolbar button.
3. Enter a name for your workspace configuration.
4. To restore: Go to File > Open Workspace and select your saved configuration.
5. The complete workspace state will be restored, including open files, views, and cursor positions.

### How to customize keyboard shortcuts
1. Go to Edit > Preferences > Keyboard.
2. Browse or search for commands you want to customize.
3. Click on the shortcut field for a command and press the desired key combination.
4. Resolve any conflicts if your chosen shortcut is already assigned.
5. Click "Apply" to save your customized shortcuts.

### How to configure display preferences
1. Go to Edit > Preferences > Display.
2. Adjust settings for:
   - Color themes and contrast
   - Font sizes and families
   - Signal rendering quality
   - Performance options for large datasets
3. Preview changes when available.
4. Click "Apply" to save your preferences.

## Advanced Features

### How to create and use expression-based filters
1. In any filter field, click the expression editor button.
2. Create expressions using the following elements:
   - Variables (signal names, attributes)
   - Operators (arithmetic, comparison, logical)
   - Functions (min, max, avg, etc.)
   - Constants (numbers, strings, boolean values)
3. Examples:
   - `amplitude > 2.5 && frequency < 100`
   - `name.contains("sensor") && value.isChanged()`
   - `timestamp.between(cursor1, cursor2)`
4. Apply the expression to filter signals or samples.

### How to create custom functional blocks
1. Identify the type of functional block you need:
   - Signal processor for data transformation
   - Diagram for visualization
   - Reader for data import
2. Go to Tools > Extensions > Create New Component.
3. Select the appropriate template for your block type.
4. Implement the required interface methods.
5. Configure metadata (name, description, parameters).
6. Test and deploy your custom block.

### How to automate repetitive tasks
1. Identify tasks you perform frequently.
2. Create a script using one of these methods:
   - Record actions through Tools > Record Script
   - Write a script manually using the API
   - Modify an existing script template
3. Configure script parameters for flexibility.
4. Add the script to your toolbar or assign a keyboard shortcut.
5. Run the script to automate the task sequence.

## Troubleshooting

### How to diagnose performance issues
1. Monitor resource usage through Help > System Information.
2. Identify performance bottlenecks:
   - Large file loading times
   - Complex signal processing
   - Heavy rendering workload
3. Apply optimizations:
   - Limit time range or signal count
   - Disable real-time updates when not needed
   - Reduce rendering quality for large datasets
   - Use more efficient filtering methods
4. Consider breaking analysis into smaller, focused sessions.

### How to recover from application errors
1. If the application becomes unresponsive:
   - Wait for background processing to complete
   - Use Help > Thread Dump to identify blocking operations
2. If a view becomes corrupted:
   - Close and reopen the view
   - Restore from a previously saved workspace
3. For persistent issues:
   - Clear application cache (Help > Reset Workspace)
   - Check for software updates
   - Consult the log files for error details

### How to report issues effectively
1. Gather information about the issue:
   - Steps to reproduce
   - Error messages
   - System information (OS, version, etc.)
   - Relevant data files (if shareable)
2. Create a detailed description including:
   - Expected behavior
   - Actual behavior
   - Screenshots when applicable
3. Submit through the appropriate channel:
   - Help > Report Issue
   - Support portal
   - Developer community forum