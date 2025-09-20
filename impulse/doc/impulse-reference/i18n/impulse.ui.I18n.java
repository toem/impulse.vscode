package de.toem.impulse.ui.i18n;

public class I18n extends de.toem.impulse.i18n.I18n{
    
    public static String Part_PartConfigurationDialog="Part Configuration";
    public static String Part_PartConfigurationDialog_IconId="ri-list-settings-line";
    public static String Part_PartConfigurationDialog_Description="Dialog for configuring the visualization and behavior of a part (such as an editor or view). Allows users to adjust display options, layout, and interactive features.";
    public static String Part_PartConfigurationDialog_HelpURL = null;
    
    public static String Part_PartPreferencesDialog_Configuration="Configuration";
    public static String Part_PartPreferencesDialog_Configuration_Description="Configuration options for customizing the visualization and behavior of a part (such as an editor or view). Enables selection and adjustment of available settings.";
    public static String Part_PartPreferencesDialog_Configuration_NoConfig="No Configuration selected";
    
    public static String Part_SampleItem_Index = "Index(@)";
    public static String Part_SampleItem_Index_Description = "Unique index of a sample within a signal, starting from 0. Used for referencing and accessing individual samples.";
    public static String Part_SampleItem_Index_IconId = "codicon-location";
    public static String Part_SampleItem_Position = "Position";
    public static String Part_SampleItem_Position_Description = "Position of a sample in the signal domain, such as time or index. Indicates where the sample is located in the signal timeline or structure.";
    public static String Part_SampleItem_Position_IconId = "ri-map-pin-time-fill";
    public static String Part_SampleItem_End = "End";
    public static String Part_SampleItem_End_Description = "Last position or endpoint of a sample group. Marks where a group of related samples ends.";
    public static String Part_SampleItem_End_IconId = "ri-map-pin-fill";
    public static String Part_SampleItem_Value = "Value";
    public static String Part_SampleItem_Value_Description = "Value of a sample, according to its signal type (logic, float, integer, text, etc.).";
    public static String Part_SampleItem_Value_IconId = "codicon-symbol-value";
    public static String Part_SampleItem_Signal = "Signal";
    public static String Part_SampleItem_Signal_Description = "Signal to which the sample belongs. Provides context for the sample's origin.";
    public static String Part_SampleItem_Signal_IconId = "ti-signal-any";
    public static String Part_SampleItem_Tag = "Tag";
    public static String Part_SampleItem_Tag_Description = "Tags or metadata for a sample or group. Used for categorization, filtering, and visualization.";
    public static String Part_SampleItem_Tag_IconId = "ri-price-tag-3-line";
    public static String Part_SampleItem_Group = "Group(#)";
    public static String Part_SampleItem_Group_Description = "Group index for grouped samples. Useful for representing transactions or hierarchical data.";
    public static String Part_SampleItem_Group_IconId = "ri-group-2-line";
    public static String Part_SampleItem_Order = "Order";
    public static String Part_SampleItem_Order_Description = "Order of a sample within its group (first, intermediate, last). Indicates sequence within a group.";
    public static String Part_SampleItem_Order_IconId = "ri-list-ordered";
    public static String Part_SampleItem_Layer = "Layer";
    public static String Part_SampleItem_Layer_Description = "Layer of a sample within a group. Used for hierarchical or multi-layered data structures.";
    public static String Part_SampleItem_Layer_IconId = "ri-list-ordered";
    public static String Part_SampleItem_Samples = "Samples(@)";
    public static String Part_SampleItem_Samples_Description = "Indices of all samples within a group. Useful for grouped or related data.";
    public static String Part_SampleItem_Samples_IconId = "codicon-tasklist";
    public static String Part_SampleItem_Relation = "Relation";
    public static String Part_SampleItem_Relation_Description = "Relations or links between samples, such as dependencies or annotations.";
    public static String Part_SampleItem_Relation_IconId = "ri-arrow-right-down-line";
    public static String Part_SampleItem_Label = "Labels";
    public static String Part_SampleItem_Label_Description = "Labels or annotations attached to samples. Used for marking events or adding notes.";
    public static String Part_SampleItem_Label_IconId = "codicon-symbol-text";
    public static String Part_SampleItem_Member_IconId = "codicon-symbol-enum-member";
    
    public static String Part_SamplesView="Sample Table";
    public static String Part_SamplesView_IconId="ri-table-fill/#9DA700";
    public static String Part_SamplesView_Description="The Sample Table provides a synchronized, tabular representation of signal data, enabling users to monitor, debug, and analyze system behavior in real time. It supports advanced features such as signal and position synchronization with the active viewer, real-time data refreshing for streaming signals, and powerful filtering options for refining results. Users can combine multiple signals for comprehensive analysis, and configure columns and value formats to tailor the table to specific needs. The Sample Table is essential for precise inspection, efficient troubleshooting, and gaining deeper insights into the structure and dynamics of data streams.";
    public static String Part_SamplesView_HelpURL = "impulse-manual/6_complementary";

    public static String Part_SamplesView_DomainFilterInfo = "<p><strong>Domain Filter:</strong> Filter samples by domain position using expressions. For example, '10ns < 20ms' shows samples within the specified range. Supports operators for less than, greater than, equal to, and more, allowing precise selection of samples based on their position in the signal timeline.</p>";
    public static String Part_SamplesView_GroupFilterInfo = "<p><strong>Group Filter:</strong> Filter samples by group index using expressions. For example, '20 < 30' shows samples in groups within the specified range. Enables users to focus on specific transactions, multi-stage events, or hierarchical data by selecting relevant groups.</p>";
    public static String Part_SamplesView_UnitMode = "UnitMode";
    public static String Part_SamplesView_ShowUncontionally = "Show unconditionally";
    public static String Part_SamplesView_ValueFilterInfo = "<p><strong>Value Filter:</strong> Filter samples by value using numeric expressions. For example, '0.4 < v < 2.0' matches samples with values in the specified range. Supports filtering by float, integer, or hexadecimal values, enabling targeted analysis of signal data.</p>";

    public static String Part_SamplesView_Configuration="Samples View Configuration";
    public static String Part_SamplesView_Configuration_IconId="ri-list-settings-line";
    public static String Part_SamplesView_Configuration_Description="Configuration options for the Sample Table allow users to customize the visualization and behavior of the table. Users can enable, disable, and reorder columns, add new columns for specific data or expressions, and adjust value formats to best represent the data. This flexibility ensures the Sample Table can be tailored for monitoring, debugging, and analysis across different contexts and data types.";
    public static String Part_SamplesView_Configuration_HelpURL = null;
    
    public static String Part_SamplesView_Column="Samples View Column";
    public static String Part_SamplesView_Column_IconId="codicon-diff";
    public static String Part_SamplesView_Column_Description="Represents a configurable column in the Sample Table, which can display various types of sample information such as values, indices, tags, or computed expressions. Columns can be enabled, disabled, reordered, or added to provide a customized view of the signal data, supporting detailed inspection and analysis.";
    public static String Part_SamplesView_Column_HelpURL = null;
    
    public static String Part_SamplesView_Ask_Info="info";
    public static String Part_SamplesView_Reply_Info="info";
    
    public static String Part_SampleView="Sample Inspector";
    public static String Part_SampleView_IconId="ri-table-2/#9DA700";
    public static String Part_SampleView_Description="The Sample Inspector is a complementary view designed to provide detailed insights into individual samples within signal data. It allows users to inspect specific sample values, analyze data streams, and monitor system performance with precision. Integrated with the active viewer, the Sample Inspector supports real-time updates, synchronization with signal selection and cursor position, and advanced filtering options. This tool is essential for debugging, understanding complex signal behaviors, and gaining deeper insights into the structure and dynamics of data streams.";
    public static String Part_SampleView_HelpURL = "impulse-manual/6_complementary";

    public static String Part_SampleView_Configuration="Sample Inspector Configuration";
    public static String Part_SampleView_Configuration_IconId="ri-list-settings-line";
    public static String Part_SampleView_Configuration_Description="Configuration options for the Sample Inspector allow users to customize the visualization and behavior of the inspector. Users can select which sample attributes to display, adjust value formats, and enable features such as synchronization and filtering. This flexibility ensures the Sample Inspector can be tailored for precise inspection, efficient troubleshooting, and analysis across different contexts and data types.";
    public static String Part_SampleView_Configuration_HelpURL = null;
    
    public static String Part_SamplesView_Row="Sample Inspector Row";
    public static String Part_SamplesView_Row_IconId="codicon-diff";
    public static String Part_SamplesView_Row_Description="Represents a row in the Sample Inspector, displaying detailed information about an individual sample. Rows can show values, indices, tags, relations, and other attributes, supporting in-depth inspection and analysis of signal data.";
    public static String Part_SamplesView_Row_HelpURL = null;
    

    public static String Part_RecordViewer="Record Viewer";
    public static String Part_RecordViewer_IconId="ri-zoom-in-fill/#9DA700";
    public static String Part_RecordViewer_Description="The Record Viewer is the central workspace for visualizing and analyzing signal data in impulse. It provides a flexible interface for displaying signals, traces, simulation outputs, and logs, supporting advanced features such as zooming, scrolling, and multi-domain axes. Users can organize signals into hierarchical views, switch between diagrams, and use cursors for precise measurement and navigation. The Record Viewer integrates seamlessly with the Record Area and complementary views, enabling efficient workflows for filtering, customizing, and exploring complex data. Its powerful visualization capabilities make it essential for engineers and developers seeking deep insights into system behavior and performance.";
    public static String Part_RecordViewer_HelpURL = "impulse-manual/3_ataglance";
  
    public static String Part_RecordViewer_Configuration="Record Viewer Configuration";
    public static String Part_RecordViewer_Configuration_IconId="ri-list-settings-line";
    public static String Part_RecordViewer_Configuration_Description="Configuration options for the Record Viewer allow users to customize the visualization and behavior of the workspace. Users can select which signals and diagrams to display, adjust domain axes, enable or disable cursors, and configure appearance settings to optimize the analysis environment. This flexibility ensures the Record Viewer can be tailored for a wide range of workflows, from simple signal inspection to advanced multi-domain analysis.";
    public static String Part_RecordViewer_Configuration_HelpURL = null;
    
    public static String Part_RecordViewer_Column="Signal Column";
    public static String Part_RecordViewer_Column_IconId="codicon-diff";
    public static String Part_RecordViewer_Column_Description="Represents a column in the Record Viewer that displays signal information, such as values, names, or computed results. Columns can be configured to show specific attributes, support filtering and sorting, and provide real-time updates as the viewer interacts with signals and cursors. This enables users to efficiently inspect, compare, and analyze signal data within the main visualization workspace.";
    public static String Part_RecordViewer_Column_HelpURL = null;
    
    public static String Part_WalletEditor="Wallet Editor";
    public static String Part_WalletEditor_IconId="codicon-code";
    public static String Part_WalletEditor_Description="The Wallet Editor in impulse is a specialized tool for managing, sharing, and exchanging preferences and configurations. It provides a collaborative interface where users can transfer elements such as serializers, adaptors, and views between the wallet and the system's preferences. The Wallet Editor features a tree structure for organizing elements, supports saving and loading wallet files, and enables selective application of configurations to streamline collaboration and maintain standardized environments across teams and projects.";
    public static String Part_WalletEditor_HelpURL = "impulse-manual/12_preferences";
  
    public static String Part_PreferencesEditor="impulse Preferences";
    public static String Part_PreferencesEditor_IconId="codicon-code";
    public static String Part_PreferencesEditor_Description="The Preferences Editor in impulse provides a comprehensive interface for customizing and configuring the workbench. Users can manage impulse-specific preferences such as serializers, adaptors, producers, views, processors, diagrams, and formatters, as well as standard preferences like templates, licenses, colors, and UI parts. The editor supports storing, discarding, reloading, and resetting preferences, and features intuitive tree-based navigation and action buttons for efficient management. This tool enables users to tailor impulse to their specific needs, optimize workflows, and ensure consistency across different platforms and environments.";
    public static String Part_PreferencesEditor_HelpURL = "impulse-manual/12_preferences";
    
    public static String Part_FindDialog="Find";
    public static String Part_FindDialog_Description="The Find dialog in impulse provides a powerful interface for searching samples within signal data. Users can define search criteria using various engines and configurations, specify properties such as expressions, and enable wrap search for continuous navigation. The dialog displays selected signals, their hierarchical locations, and presents search results with positions where matches are found. Action buttons allow users to navigate between matches, display all results, and clear search results, making the Find dialog an essential tool for efficient and targeted data analysis.";
    public static String Part_FindDialog_HelpURL = "impulse-manual/9_search";
    public static String Part_FindDialog_FindNext = "Find Next";
    public static String Part_FindDialog_FindAll = "Find All";
    public static String Part_FindDialog_Clear = "Clear";
    public static String Part_FindDialog_FindPrev = "Find Prev";
    public static String Part_FindDialog_Wrap = "Wrap search";
    public static String Part_FindDialog_Signals="Signals";
    public static String Part_FindDialog_Signals_Description="Displays the signals selected for searching, including their names and hierarchical locations. This section helps users understand the context of the search and ensures the correct signals are being analyzed.";
    public static String Part_FindDialog_Results="Results";
    public static String Part_FindDialog_Results_Description="Shows the results of the search, including the criteria used and the positions where matches were found. This section provides a clear overview of search outcomes, supporting efficient navigation and further analysis of the data.";
    
    public static String Part_AboutDialog="About";
    public static String Part_AboutDialog_Description="The About dialog provides users with essential information about impulse, including its capabilities, supported features, and version details. It serves as a reference point for understanding the tool's purpose, its role in signal visualization and analysis, and how it can be customized to fit diverse workflows. This dialog helps users quickly access documentation, support resources, and learn more about the development and evolution of impulse.";
    public static String Part_AboutDialog_HelpURL = null;
    
    public static String Part_ViewSelectDialog="View Selection";
    public static String Part_ViewSelectDialog_Description="The View Selection dialog in impulse enables users to create, select, clone, and manage views for tailored signal visualization and analysis. It presents a list of available views, showing their names and fit to the current record, and provides options to add new views, clone existing ones, or delete unused views. This dialog is central to organizing and customizing the workspace, ensuring users can efficiently switch between different visualizations and optimize their analysis experience.";
    public static String Part_ViewSelectDialog_HelpURL = "impulse-manual/4_views";
}
