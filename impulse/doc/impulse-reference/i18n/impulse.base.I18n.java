package de.toem.impulse.i18n;

public class I18n extends de.toem.toolkits.general.i18n.I18n {

    // ========================================================================================================================
    // Samples
    // ========================================================================================================================

    public static String Samples_Analysis = "Analysis";
    public static String Samples_AttachmentAtStart = "Attachment at start";

    public static String Samples_CouldNotAllocate = "Could not allocate reader memory!";

    public static String Samples_DomainBase = "Domain base";
    public static String Samples_DomainBase_Description = "The minimum distance between two samples, typically measured in units like nanoseconds (ns) or picoseconds (ps). Defines the granularity of the signal's domain.";
    public static String Samples_DomainClass = "Domain class";
    public static String Samples_DomainClass_Description = "The domain class specifies the type of domain for the signal, such as time, frequency, index, or other units. It determines how sample positions are interpreted.";
    public static String Samples_DomainClassBase = "Domain Class/Base";
    public static String Samples_DomainEnd = "End";
    public static String Samples_DomainEnd_Description = "The ending point or position of the signal in its domain, defining the range or duration covered by the signal.";
    public static String Samples_DomainRate = "Rate";
    public static String Samples_DomainRate_Description = "The rate at which the signal progresses or samples occur. For continuous signals, this is a fixed interval; for discrete signals, the rate is zero.";
    public static String Samples_DomainStart = "Start";
    public static String Samples_DomainStart_Description = "The starting point or position of the signal in its domain, establishing its initial location or time.";
    public static String Samples_DomainUnit = "Domain unit";

    public static String Samples_DomainClass_Time = "Time";
    public static String Samples_DomainClass_Frequency = "Frequency";
    public static String Samples_DomainClass_Amps = "Amps";
    public static String Samples_DomainClass_Volts = "Volts";
    public static String Samples_DomainClass_Index = "Index";
    public static String Samples_DomainClass_Pixel = "Pixel";
    public static String Samples_DomainClass_Float = "Float";
    public static String Samples_DomainClass_Date = "Date";
    public static String Samples_DomainClass_Unknown = "Unknown";

    public static String Samples_ExceptionAt = "SampleParseException at";
    public static String Samples_ExtractMember = "Extract Member";
    public static String Samples_FilterMember = "Filter Member";

    public static String Samples_Formats = "Format/Info/Member";
    public static String Samples_IEEE754 = "IEEE 754";
    public static String Samples_Image = "Image";
    public static String Samples_Interpretation = "Interpretation";
    public static String Samples_Labels = "Labels";
    public static String Samples_LinearFormula = "f(x)=mx+b";
    public static String Samples_LinearTransform = "Linear Transformation (Float)";

    public static String Samples_Member = "Member";
    public static String Samples_Members = "Members";
    public static String Samples_Relations = "Relations";
    public static String Samples_SignalClass = "Class";
    public static String Samples_SignalScale = "Scale";
    public static String Samples_SignalScale_Description = "Defines the dimension or width of the signal, such as the number of bits in a logic signal or the size of an array. Scale helps describe the structure and capacity of the signal.";
    public static String Samples_SignalFormat = "Format";
    public static String Samples_SignalFormat_Description = "Specifies how the signal's values are represented textually, such as binary, decimal, hexadecimal, ASCII, label, or boolean. Format ensures clarity and consistency in visualization and analysis.";
    public static String Samples_SignalCollectionFormat = "Collection Format";
    public static String Samples_SignalCollectionFormat_Description = "Defines how collections of values (arrays or structs) are displayed, such as showing only values or key-value pairs. This helps present structured data in a readable format.";
    public static String Samples_SignalTag = "Tag";
    public static String Samples_Signed = "Signed";
    public static String Samples_Transaction = "Transaction";
    public static String Samples_Unsigned = "Unsigned";
    public static String Samples_Value = "Value";

    public static String Samples_SignalType = "Signal type";
    public static String Samples_SignalType_Description = "The data type of the signal, such as logic, float, integer, event, text, structure, or binary. Determines how the signal's samples are interpreted and processed.";
    public static String Samples_SignalType_Unknown = "Unknown";
    public static String Samples_SignalType_Struct = "Struct";
    public static String Samples_SignalType_Text = "Text";
    public static String Samples_SignalType_TextArray = "Text Array";
    public static String Samples_SignalType_Logic = "Logic";
    public static String Samples_SignalType_Float = "Float";
    public static String Samples_SignalType_FloatArray = "Float Array";
    public static String Samples_SignalType_Event = "Event";
    public static String Samples_SignalType_Enumeration = "Enumeration";
    public static String Samples_SignalType_EventArray = "Event Array";
    public static String Samples_SignalType_Binary = "Bytes";
    public static String Samples_SignalType_Integer = "Integer";
    public static String Samples_SignalType_IntegerArray = "Integer Array";

    // ========================================================================================================================
    // Colors
    // ========================================================================================================================

    public static String Color_Colors = "Colors";
    public static String Color_Colors_Description = "";
    public static String Color_Colors_IconId = "codicon-symbol-color";
    public static String Color_Colors_HelpURL = null;
    public static String Color_CurrentColorSet = "Current Color Set";
    public static String Color_CurrentColorSet_Description = "Current Color Set";

    public static String Color_ColorSet = "Color Set";
    public static String Color_ColorSet_Description = "Color Set";
    public static String Color_ColorSet_IconId = "codicon-color-mode";
    public static String Color_ColorSet_HelpURL = null;

    public static String Color_Color = "Color";
    public static String Color_Color_Description = "Color";
    public static String Color_Color_IconId = "codicon-circle-large-filled";
    public static String Color_Color_HelpURL = null;

    // ========================================================================================================================
    // Diagrams
    // ========================================================================================================================

    public static String Diagram_Diagrams = "Diagrams";
    public static String Diagram_Diagrams_Description = "A set of available diagram types and configurations for visualizing signal data. Diagrams enable advanced graphical representation and analysis of signals.";
    public static String Diagram_Diagrams_IconId = "ri-organization-chart";
    public static String Diagram_Diagrams_HelpURL = "impulse-manual/12_preferences#diagrams";

    public static String Diagram_Diagram = "Diagram";
    public static String Diagram_Diagram_Description = "A functional block for visualizing signals using a specific diagram type, such as line, bar, pie, or custom charts.";
    public static String Diagram_Diagram_IconId = "ri-organization-chart";
    public static String Diagram_Diagram_HelpURL = "impulse-manual/11_functional-blocks#diagram";

    public static String Diagram_Properties = "Diagram Properties";
    public static String Diagram_Properties_Description = "A set of properties and settings for a diagram, such as type, axes, colors, and display options.";
    public static String Diagram_Properties_IconId = "ri-download-2-line";
    public static String Diagram_Properties_HelpURL = "impulse-manual/11_functional-blocks#diagram";

    public static String Diagram_DiagramConfiguration = "Configuration";
    public static String Diagram_DiagramConfiguration_Description = "A specific set of properties and settings for a diagram, defining how signals are visualized. Configurations can be selected or edited in the Preferences dialog.";
    public static String Diagram_DiagramConfiguration_HelpURL = "impulse-manual/11_functional-blocks#user-configurations";

    public static String Diagram_DiagramConfigurations = "Configurations";
    public static String Diagram_DiagramConfigurations_Description = "A collection of user-defined or default configurations for diagrams, allowing customization for specific visualization tasks. Configurations can be saved, reused, and shared.";
    public static String Diagram_DiagramConfigurations_HelpURL = "impulse-manual/11_functional-blocks#user-configurations";

    public static String Diagram_DefaultDiagramConfiguration = "Diagram Configuration";
    public static String Diagram_DefaultDiagramConfiguration_Description = "The default configuration used by a diagram when no user configuration is selected.";
    public static String Diagram_DefaultDiagramConfiguration_IconId = "ri-list-settings-line";
    public static String Diagram_DefaultDiagramConfiguration_HelpURL = "impulse-manual/11_functional-blocks#user-configurations";

    public static String Diagram_Annotated = "Annotated";
    public static String Diagram_Annotated_Description = "Highlights or marks specific data points or regions in the diagram for enhanced analysis or presentation.";
    public static String Diagram_Interpolated = "Interpolated";
    public static String Diagram_Interpolated_Description = "Displays interpolated values between samples to provide smoother visualization of signal data.";
    public static String Diagram_DrawAxis = "Draw Axis";
    public static String Diagram_DrawAxis_Description = "Enables or disables the drawing of axes in the diagram for better orientation and scaling.";
    public static String Diagram_Include = "Include";
    public static String Diagram_Include_Description = "Specifies which signals or data elements to include in the diagram, supporting focused visualization.";
    public static String Diagram_Exclude = "Exclude";
    public static String Diagram_Exclude_Description = "Specifies which signals or data elements to exclude from the diagram, allowing for filtered views.";
    public static String Diagram_Groups = "Enable Groups";
    public static String Diagram_Groups_Description = "Enables grouping of samples or members, useful for representing transactions or multi-stage events in diagrams.";
    public static String Diagram_Scale = "Scale";
    public static String Diagram_Scale_Type = "Scale Type";
    public static String Diagram_Scale_Description = "Defines the dimension or width of the signal or diagram element, such as the number of bits in a logic signal or the size of an array. Scale helps describe the structure and capacity of the data being visualized.";
    public static String[] Diagram_Scale_Options = { "Linear", "Log10" };
    public static String Diagram_Relation = "Show Relations";
    public static String Diagram_Relation_Description = "Shows relations or links between samples, signals, or members, visualizing dependencies or interactions.";
    public static String Diagram_Labels = "Show Labels";
    public static String Diagram_Labels_Description = "Displays labels or annotations for samples or members, providing descriptive information or highlighting events.";
    public static String Diagram_Multicolor = "Multi-color";
    public static String Diagram_Multicolor_Description = "Uses multiple colors to distinguish relations, groups, or categories for improved clarity in visualization.";
    public static String Diagram_Colors = "Colors";
    public static String Diagram_Colors_Description = "Defines the color scheme for relations, groups, or other diagram elements.";
    public static String Diagram_Stacked = "Stacked";
    public static String Diagram_Stacked_Description = "Displays data in a stacked format, useful for comparing multiple signals or categories in a single diagram.";
    public static String Diagram_Transparent = "Transparent";
    public static String Diagram_Transparent_Description = "Enables transparency in diagram elements for layered or overlapping visualizations.";
    public static String Diagram_ValueFormat = "Value Format";
    public static String Diagram_ValueFormat_Description = "Specifies how values are represented textually in the diagram, such as binary, decimal, hexadecimal, ASCII, label, or boolean. Format ensures clarity and consistency in visualization.";
    public static String Diagram_ColumnValueFormat = "Value Column Format";
    public static String Diagram_ColumnValueFormat_Description = "Defines how values in columns are displayed, supporting formats like binary, decimal, hexadecimal, or label for clear tabular visualization.";

    public static String Diagram_Tags = "Show Tags";
    public static String Diagram_Tags_Description = "Displays tags associated with signals or samples, providing metadata for categorization, filtering, or presentation in diagrams.";

    public static String Diagram_Title = "Show Title";
    public static String Diagram_Title_Description = "Shows the diagram's title, which can be set to none, name, or description to provide context for the visualization.";
    public static String[] Diagram_Title_Options = { "No", "Name", "Description" };
    public static String Diagram_Transposed = "Show Transposed";
    public static String Diagram_Transposed_Description = "Displays the diagram in a transposed format, swapping axes or orientation for alternative views and analysis.";
    public static String Diagram_Explode = "Show Explode";
    public static String Diagram_Explode_Description = "Separates or highlights individual components or categories in the diagram for detailed inspection and analysis.";
    public static String Diagram_Cutout = "Cutout";
    public static String Diagram_Cutout_Description = "Focuses on a specific region or subset of the diagram, allowing for targeted visualization of selected data.";
    public static String Diagram_Filled = "Filled";
    public static String Diagram_Filled_Description = "Fills diagram elements for solid visualization, improving readability and emphasis of data regions.";
    public static String Diagram_Legend = "Show Legend";
    public static String Diagram_Legend_Description = "Shows the legend for the diagram, explaining colors, symbols, or categories used in the visualization for clarity.";

    public static String Diagram_MaxSeries = "Max Series";
    public static String Diagram_MaxSeries_Description = "Max Series";
    public static String Diagram_MaxCategory = "Max Categories";
    public static String Diagram_MaxCategory_Description = "Max Categories";
    public static String Diagram_Members = "Members";
    public static String Diagram_Members_Description = "Identify the structure or array elements you want to display. Use a comma-separated list of all members, e.g. 'X,Y'. Members represent individual components of arrays or structs in signals.";
    public static String Diagram_Samples = "Samples";
    public static String Diagram_Samples_Description = "Identify the samples you want to display. Use a comma-separated list of all sample indices, for example, '0,1,5-8'. Samples represent individual data points or events in a signal.";
    public static String Diagram_Categorize = "Categorize";
    public static String Diagram_Categorize_Description = "Choose if you want to categorize the members or the samples. Categorization helps organize and group data for more meaningful visualization and analysis.";
    public static String[] Diagram_Categorize_Options = { "Members", "Samples" };

    // ========================================================================================================================

    public static String Diagram_Logic = "Logic Diagram";
    public static String Diagram_Logic_IconId = "diagram-logic";
        public static String Diagram_Logic_Description = "Visualizes digital logic signals, showing bit states and transitions for debugging and protocol analysis.";
    public static String Diagram_Logic_HelpURL = "impulse-reference/26_logic-diagram";

    public static String Diagram_Vector = "Vector Diagram";
    public static String Diagram_Vector_IconId = "diagram-vector";
        public static String Diagram_Vector_Description = "Displays vector signals, representing multi-bit or multi-channel data for analysis and visualization.";
    public static String Diagram_Vector_HelpURL = "impulse-reference/27_vector-diagram";

    public static String Diagram_Image = "Image Diagram";
    public static String Diagram_Image_IconId = "diagram-image";
        public static String Diagram_Image_Description = "Renders image signals, enabling visualization of pixel or bitmap data within signal traces.";
    public static String Diagram_Image_HelpURL = "impulse-reference/28_image-diagram";

    public static String Diagram_Event = "Event Diagram";
    public static String Diagram_Event_IconId = "diagram-event";
        public static String Diagram_Event_Description = "Shows event signals, highlighting discrete events, triggers, or markers in signal data.";
    public static String Diagram_Event_HelpURL = "impulse-reference/29_event-diagram";

    public static String Diagram_Gantt = "Gantt Diagram";
    public static String Diagram_Gantt_IconId = "diagram-gantt";
        public static String Diagram_Gantt_Description = "Visualizes timing and scheduling of events or tasks, useful for protocol and transaction analysis.";
    public static String Diagram_Gantt_HelpURL = "impulse-reference/30_gantt-diagram";

    public static String Diagram_Line = "Line Diagram";
    public static String Diagram_Line_IconId = "diagram-line";
        public static String Diagram_Line_Description = "Displays continuous or sampled data as line plots, ideal for analog and time-series signals.";
    public static String Diagram_Line_HelpURL = "impulse-reference/31_line-diagram";

    public static String Diagram_Area = "Area Diagram";
    public static String Diagram_Area_IconId = "diagram-area";
        public static String Diagram_Area_Description = "Shows data as filled areas under curves, emphasizing magnitude and trends in signal data.";
    public static String Diagram_Area_HelpURL = "impulse-reference/32_table-diagram";

    public static String Diagram_Table = "Table Diagram";
    public static String Diagram_Table_IconId = "diagram-table";
        public static String Diagram_Table_Description = "Displays signal data in tabular format, supporting structured analysis and comparison of samples.";
    public static String Diagram_Table_HelpURL = "impulse-reference/32_table-diagram";

    public static String Diagram_BarChart = "BarChart Diagram";
    public static String Diagram_BarChart_IconId = "ri-bar-chart-2-line";
        public static String Diagram_BarChart_Description = "Visualizes categorical or grouped data as bars, useful for comparing values and distributions.";
    public static String Diagram_BarChart_HelpURL = "impulse-reference/33_barchart-diagram";

    public static String Diagram_PieChart = "PieChart Diagram";
    public static String Diagram_PieChart_IconId = "ri-pie-chart-2-line";
        public static String Diagram_PieChart_Description = "Displays proportions of categories as pie slices, ideal for visualizing relative distributions.";
    public static String Diagram_PieChart_HelpURL = "impulse-reference/34_piechart-diagram";

    public static String Diagram_LineChart = "LineChart Diagram";
    public static String Diagram_LineChart_IconId = "ri-line-chart-line";
        public static String Diagram_LineChart_Description = "Plots data points connected by lines, suitable for trends, time-series, and continuous signals.";
    public static String Diagram_LineChart_HelpURL = "impulse-reference/35_linechart-diagram";

    public static String Diagram_RadarChart = "RadarChart Diagram";
    public static String Diagram_RadarChart_IconId = "ri-pie-chart-box-line";
        public static String Diagram_RadarChart_Description = "Displays multivariate data on axes from a central point, useful for comparing multiple variables.";
    public static String Diagram_RadarChart_HelpURL = "impulse-reference/36_radarchart-diagram";

    // ========================================================================================================================

    public static String Diagram_ScriptedDiagram = "Scripted Diagram";
    public static String Diagram_ScriptedDiagram_Description = "";
    public static String Diagram_ScriptedDiagram_IconId = "codicon-code";
    public static String Diagram_ScriptedDiagram_HelpURL = null;

    public static String Diagram_ScriptedDiagramConfiguration = "Scripted Diagram Configuration";
    public static String Diagram_ScriptedDiagramConfiguration_Description = "To create a Scripted Reader, open the preferences and go to \"impulse->Diagram-> Scripted Reader\", open the configuration dialog and add a new Scripted Reader configuration. ";
    public static String Diagram_ScriptedDiagramConfiguration_IconId = "ri-list-settings-line";

    // ========================================================================================================================
    // ...
    // ========================================================================================================================

    public static String Flux_ScriptException = "Flux configuration script exception!";
    public static String Folder_Accordion = "Accordion";
    public static String Folder_DedicatedAxis = "Dedicated Axis";
    public static String Folder_FolderType = "Folder type";
    public static String Folder_Normal = "Normal";
    public static String FormatsPage_Comments = "The format preference page lets you enter custom decimal formats as well as preferred domain units.\nYou can define up to eight different decimal formats. ";
    public static String FormatsPage_DecimalUserFormats = "User defined decimal formats";
    public static String FormatsPage_EG = "e.g. us,s,Hz";
    public static String FormatsPage_PreferredDomainUnits = "Preferred domain units";
    public static String GotoDialog_Goto = "Goto";
    public static String ImpulsePage_Comments = " Below you find preferences pages for the different aspects of impulse (views ,serializers, ports, templates, ...).\nPreferences can be easily exchanged (exported and imported) using the preference wallet editor.";
    public static String ImpulsePage_CreateNewWallet = "Create a new preference wallet";
    public static String ImpulsePage_ImportExport = "Preference exchange (import/export)";
    public static String ImpulsePage_ToImportExportPreferences = "To import/ export preferences,\n create an impulse preference wallet and open the wallet editor!";

    public static String Marker_AddMarker = "Add Marker";
    public static String Marker_ApplyMarkers = "Apply markers";
    public static String Marker_Markers = "Signal Markers";

    public static String Part_Parts = "Parts";
    public static String Part_Parts_Description = "Part";
    public static String Part_Parts_IconId = "ri-windows-line";
    public static String Part_Parts_HelpURL = null;

    public static String Part_Part = "Part";
    public static String Part_Part_Description = "Part";
    public static String Part_Part_IconId = "ri-window-2-line";
    public static String Part_Part_HelpURL = null;

    public static String Part_Properties = "Part Properties";
    public static String Part_Properties_Description = "Properties";
    public static String Part_Properties_IconId = "ri-download-2-line";
    public static String Part_Properties_HelpURL = null;

    public static String Part_PartConfiguration = "Configuration";
    public static String Part_PartConfiguration_Description = "Configurations";
    public static String Part_PartConfiguration_HelpURL = null;

    public static String Part_PartConfigurations = "Configurations";
    public static String Part_PartConfigurations_Description = "Configurations";
    public static String Part_PartConfigurations_HelpURL = null;

    public static String Part_DefaultPartConfiguration = "Part Configuration";
    public static String Part_DefaultPartConfiguration_Description = "Part Configuration";
    public static String Part_DefaultPartConfiguration_IconId = "ri-list-settings-line";
    public static String Part_DefaultPartConfiguration_HelpURL = null;

    public static String Part_ScriptedPart = "Scripted Part";
    public static String Part_ScriptedPart_Description = "";
    public static String Part_ScriptedPart_IconId = "codicon-code";
    public static String Part_ScriptedPart_HelpURL = null;

    public static String Part_ScriptedPartConfiguration = "Scripted Part Configuration";
    public static String Part_ScriptedPartConfiguration_Description = "To create a Scripted Reader, open the preferences and go to \"impulse->Part-> Scripted Reader\", open the configuration dialog and add a new Scripted Reader configuration. ";
    public static String Part_ScriptedPartConfiguration_IconId = "ri-list-settings-line";

    public static String PartsPage_Comments = "Manage the settings for all impulse parts like sample or ports view.";
    public static String PloDialogt_GantStyle = "Gantt Format/Style";
    public static String PlotDialog_AddAditionalSource = "Add additional source";
    public static String PlotDialog_AddTemplate = "Add current plot as new template";
    public static String PlotDialog_AdditionalSources = "Additional (s1..)";
    public static String PlotDialog_AreaStyle = "Area Style";
    public static String PlotDialog_B = "b";
    public static String PlotDialog_ChartStyle = "Chart Style";
    public static String PlotDialog_DefaultValueColumnStyle = "Default Value Column Style";
    public static String PlotDialog_EditPrimarySource = "Edit primary source";
    public static String PlotDialog_EventStyle = "Event Format/Style";
    public static String PlotDialog_LineStyle = "Line Style";
    public static String PlotDialog_LogStyle = "Log Style";
    public static String PlotDialog_LogicStyle = "Logic Format/Style";
    public static String PlotDialog_M = "m";
    public static String PlotDialog_SignalPlot = "Signal/Plot";
    public static String PlotDialog_TakeTemplate = "Take settings from selected template";
    public static String PlotDialog_TransactionStyle = "Transaction Style";
    public static String PlotDialog_VectorStyle = "Vector Format/Style";
    public static String PlotTemplateDialog_DescriptionPattern_ = "Description pattern";
    public static String PlotTemplateDialog_DoNotUpdate = "Do not update with latest version. ";
    public static String PlotTemplateDialog_NamePattern = "Name pattern";
    public static String PlotTemplateDialog_ShowContextMenu = "Show in add/insert context menu";
    public static String PlotTemplateDialog_SignalDescriptorPattern = "Signal descriptor pattern";
    public static String PlotTemplateDialog_SignalPattern = "Signal pattern";
    public static String PlotTemplateDialog_UsePatternAutomaticInstantiation = "Use pattern for automatic instantiation";

    public static String PortsPageShow_ImpulseSignalPorts = "Show impulse Signal Ports view";
    public static String PortsPage_Comments = "Ports are means to read signal data from external devices and interfaces.\nSimilar to charts, you can configure multiple ports of a given set of providers\n(e.g. add two tcp ports, one for port 4000, the other for port 4101). ";

    // ========================================================================================================================
    // Signal Processors
    // ========================================================================================================================

    public static String SamplesProcessor_Producers = "Signal Processors";
    public static String SamplesProcessor_Producers_Description = "A set of available signal processors. Signal processors are functional blocks for deriving new signals from existing ones or generating them from scratch. They combine signals, extract patterns, and decode data using protocol parsers, making them essential for advanced data analysis.";
    public static String SamplesProcessor_Producers_IconId = "codicon-circuit-board";
    public static String SamplesProcessor_Producers_HelpURL = "impulse-manual/12_preferences#processors";

    public static String SamplesProcessor_Producer = "Signal Processor";
    public static String SamplesProcessor_Producer_Description = "A functional block for deriving new signals from existing ones or generating them from scratch. Signal processors combine, extract, and decode data, supporting advanced analysis and protocol parsing.";
    public static String SamplesProcessor_Producer_IconId = "ri-download-2-line";
    public static String SamplesProcessor_Producer_HelpURL = "impulse-manual/11_functional-blocks#signal-processor";

    public static String SamplesProcessor_Properties = "Processor Properties";
    public static String SamplesProcessor_Properties_Description = "A set of properties and settings for a signal processor, allowing customization for specific analysis or transformation tasks.";
    public static String SamplesProcessor_Properties_IconId = "ri-download-2-line";
    public static String SamplesProcessor_Properties_HelpURL = "impulse-manual/11_functional-blocks#signal-processor";

    public static String SamplesProcessor_ProducerConfiguration = "Configuration";
    public static String SamplesProcessor_ProducerConfiguration_Description = "A specific set of properties and settings for a signal processor, defining how signals are processed or derived. Configurations can be selected or edited in the Preferences dialog.";
    public static String SamplesProcessor_ProducerConfiguration_HelpURL = "impulse-manual/11_functional-blocks#user-configurations";

    public static String SamplesProcessor_ProducerConfigurations = "Processor Configurations";
    public static String SamplesProcessor_ProducerConfigurations_Description = "A collection of user-defined or default configurations for signal processors, allowing customization for specific workflows. Configurations can be saved, reused, and shared.";
    public static String SamplesProcessor_ProducerConfigurations_HelpURL = "impulse-manual/11_functional-blocks#user-configurations";

    public static String SamplesProcessor_DefaultProducerConfiguration = "Default Processor Configuration";
    public static String SamplesProcessor_DefaultProducerConfiguration_Description = "The default configuration used by a signal processor when no user configuration is selected.";
    public static String SamplesProcessor_DefaultProducerConfiguration_IconId = "ri-list-settings-line";
    public static String SamplesProcessor_DefaultProducerConfiguration_HelpURL = "impulse-manual/11_functional-blocks#user-configurations";

    public static String SamplesProcessor_ArrayCombiner_InvalidType = "Invalid type! Must be an array type.";
    public static String SamplesProcessor_ArraysNotSupported = "Arrays type not supported!";
    public static String SamplesProcessor_CouldNotBeExecuted = "Production could not be executed!";
    public static String SamplesProcessor_CouldNotBeInstatiated = "Production could not be instantiated!";
    public static String SamplesProcessor_CouldNotBeRead = "Production could not be read!";
    public static String SamplesProcessor_Diff_AppendOriginalInputs = "Append both original inputs (\u2780+\u2781) below diff(\u2248) or delta(\u0394) signal";
    public static String SamplesProcessor_Diff_HideIdenticalContent = "Hide identical content";
    public static String SamplesProcessor_Diff_IgnoreLonger = "Ignore if 2nd input (\u2781) is longer";
    public static String SamplesProcessor_Diff_IgnoreShorter = "Ignore if 2nd input (\u2781) is shorter";
    public static String SamplesProcessor_Diff_PrepareDeltaFloat = "Prepare delta (\u0394) for Float signals";
    public static String SamplesProcessor_Diff_PrepareDeltaInteger = "Prepare delta (\u0394) for Integer signals";
    public static String SamplesProcessor_Extract = "Extract";
    public static String SamplesProcessor_HideIdentical = "Hide identical";
    public static String SamplesProcessor_HideIdenticalComment = "Check to hide sequence of identical samples";
    public static String SamplesProcessor_IgnoreNone = "Ignore None";
    public static String SamplesProcessor_IgnoreNoneComment = "Ignore 'None' samples of the input signals.";
    public static String SamplesProcessor_InvalidParameters = "Invalid parameters!";
    public static String SamplesProcessor_KeepAttachments = "Keep attachments";
    public static String SamplesProcessor_KeepAttachmentsComment = "Check to keep labels and relations of each input sample";
    public static String SamplesProcessor_KeepGroups = "Keep Groups";
    public static String SamplesProcessor_KeepGroupsComment = "Check to keep the group information (e.g. transactions) of each input sample";
    public static String SamplesProcessor_KeepTags = "Keep tags";
    public static String SamplesProcessor_KeepTagsComment = "The output samples shall keep the tag information of each input sample";
    public static String SamplesProcessor_Killed = "Killed!";
    public static String SamplesProcessor_LogicExtract_InvalidType = "Invalid type! Must be a logic type.";
    public static String SamplesProcessor_LogicExtract_BitPos = "Bit pos";
    public static String SamplesProcessor_LogicExtract_Count = "Count";
    public static String SamplesProcessor_LogicExtract_From = "from";
    public static String SamplesProcessor_LogicExtract_InvertBits = "Invert bits";
    public static String SamplesProcessor_LogicExtract_Swap = "Swap";
    public static String SamplesProcessor_LogicExtract_SwapBits = "Swap bits";
    public static String SamplesProcessor_LogicCombine_InvalidType = "Invalid type! Must be a logic type.";
    public static String SamplesProcessor_MemberUndefined = "Member undefined!";
    public static String SamplesProcessor_SourceErrors = "Source Error(s)";
    public static String SamplesProcessor_Splitter_Grouped = "Grouped";
    public static String SamplesProcessor_Splitter_Max = "Max";
    public static String SamplesProcessor_StructNotSupported = "Struct type not supported!";

    // ========================================================================================================================

    public static String SamplesProcessor_Java = "Java Signal Processor";
    public static String SamplesProcessor_Java_Description = "A functional block for processing signals using Java. The Java integration type enables developers to implement a signal processor as a single Java class directly within the environment for rapid development and advanced customization.";
    public static String SamplesProcessor_Java_IconId = "codicon-code";
    public static String SamplesProcessor_Java_HelpURL = null;

    public static String SamplesProcessor_JavaConfiguration = "Java Processor Configuration";
    public static String SamplesProcessor_JavaConfiguration_Description = "A specific set of properties and settings for a Java signal processor, defining how signals are processed or derived.";
    public static String SamplesProcessor_JavaConfiguration_IconId = "ri-list-settings-line";
    public static String SamplesProcessor_JavaConfiguration_HelpURL = null;

    public static String SamplesProcessor_ArrayCombine = "Array Combine";
    public static String SamplesProcessor_ArrayCombine_IconId = "ri-git-branch-line/#9ac0cc";
    public static String SamplesProcessor_ArrayCombine_Description = "Combines multiple signals of the same type into a single array signal, enabling advanced analysis and visualization of grouped data. See documentation for supported types, scale, format, and domain options.";
    public static String SamplesProcessor_ArrayCombine_HelpURL = "impulse-reference/18_array-combine";

    public static String SamplesProcessor_LogicCombine = "Logic Combine";
    public static String SamplesProcessor_LogicCombine_IconId = "ri-git-branch-line/#9da700";
    public static String SamplesProcessor_LogicCombine_Description = "Combines multiple logic signals (single-bit or vectors) into a single logic vector signal containing all bits. Useful for digital analysis, protocol decoding, and visualization of grouped logic signals.";
    public static String SamplesProcessor_LogicCombine_HelpURL = "impulse-reference/17_logic-combine";

    public static String SamplesProcessor_LogicExtract = "Logic Extract";
    public static String SamplesProcessor_LogicExtract_IconId = "ri-file-shred-line/#9da700";
    public static String SamplesProcessor_LogicExtract_Description = "Lets users extract, swap, and invert bits from logic signals for advanced digital analysis, protocol decoding, and signal transformation.";
    public static String SamplesProcessor_LogicExtract_HelpURL = "impulse-reference/19_logic-extract";

    public static String SamplesProcessor_MemberExtract = "Member Extract";
    public static String SamplesProcessor_MemberExtract_IconId = "ri-file-shred-line/#647687";
    public static String SamplesProcessor_MemberExtract_Description = "Extracts a member from a struct or array signal by name or index and creates a new signal of the specified type. See documentation for supported types and configuration options.";
    public static String SamplesProcessor_MemberExtract_HelpURL = "impulse-reference/20_member-extract";

    public static String SamplesProcessor_ExpressionFilter = "Expression Filter";
    public static String SamplesProcessor_ExpressionFilter_IconId = "ri-filter-line";
    public static String SamplesProcessor_ExpressionFilter_Description = "Filters all samples that don't match a given expression. Supports text, regular, and numeric expressions. See the Expression Filter Processor section for details and examples.";
    public static String SamplesProcessor_ExpressionFilter_HelpURL = "impulse-reference/21_expression-filter";

    public static String SamplesProcessor_Expression = "Expression";
    public static String SamplesProcessor_Expression_IconId = "ri-calculator-line";
    public static String SamplesProcessor_Expression_Description = "Computes the value of a user-defined expression for each sample, supporting multiple input signals and advanced data transformation. See documentation for syntax and examples.";
    public static String SamplesProcessor_Expression_HelpURL = "impulse-reference/25_expression";

    public static String SamplesProcessor_LogicSplitter = "Logic Splitter";
    public static String SamplesProcessor_LogicSplitter_IconId = "ri-git-merge-line/#9da700";
    public static String SamplesProcessor_LogicSplitter_Description = "Splits a logic signal into its individual components (bits or groups of bits), supporting child processing for deduce operations. See documentation for configuration options.";
    public static String SamplesProcessor_LogicSplitter_HelpURL = "impulse-reference/22_logic-splitter";

    public static String SamplesProcessor_StructSplitter = "Struct Splitter";
    public static String SamplesProcessor_StructSplitter_IconId = "ri-git-merge-line/#647687";
    public static String SamplesProcessor_StructSplitter_Description = "Splits a struct signal into its individual members, supporting child processing for deduce operations. See documentation for configuration options.";
    public static String SamplesProcessor_StructSplitter_HelpURL = "impulse-reference/23_struct-splitter";

    public static String SamplesProcessor_ArraySplitter = "Array Splitter";
    public static String SamplesProcessor_ArraySplitter_IconId = "ri-git-merge-line/#9ac0cc";
    public static String SamplesProcessor_ArraySplitter_Description = "Splits an array signal into its individual elements, supporting child processing for deduce operations. See documentation for configuration options.";
    public static String SamplesProcessor_ArraySplitter_HelpURL = "impulse-reference/24_array-splitter";

    // ========================================================================================================================

    public static String SamplesProcessor_Diff = "Diff";
    public static String SamplesProcessor_Diff_IconId = "codicon-diff";
    public static String SamplesProcessor_Diff_Description = "Creates a \"diff\" signal from 2 input signals. The diff signal is identical with the input if both input are equal. Regions with non equal signal data are tagged.";
    public static String SamplesProcessor_Diff_HelpURL = null;

    public static String SamplesProcessor_ScriptProducer = "Signal Script";
    public static String SamplesProcessor_ScriptProducer_IconId = "codicon-code";
    public static String SamplesProcessor_ScriptProducer_Description = "Signal scripts allow the users to analyse and interpret signals in many ways. Combine signals using mathematical operations, generate references, implement protocol parsers, extract statistical informations or search for conflicts automatically.";
    public static String SamplesProcessor_ScriptProducer_HelpURL = null;

    public static String SamplesProcessor_ScriptedProducer = "Scripted Signal Processor";
    public static String SamplesProcessor_ScriptedProducer_Description = "A functional block for processing signals using scripts. Scripted signal processors allow users to define custom processing logic for advanced analysis and transformation.";
    public static String SamplesProcessor_ScriptedProducer_IconId = "codicon-code";
    public static String SamplesProcessor_ScriptedProducer_HelpURL = null;

    public static String SamplesProcessor_ScriptedProducerConfiguration = "Scripted Processor Configuration";
    public static String SamplesProcessor_ScriptedProducerConfiguration_Description = "A specific set of properties and settings for a scripted signal processor, defining how signals are processed or derived.";
    public static String SamplesProcessor_ScriptedProducerConfiguration_IconId = "ri-list-settings-line";

    // ========================================================================================================================
    // Sample Search
    // ========================================================================================================================

    public static String SamplesSearch_Searches = "Search";
    public static String SamplesSearch_Searches_Description = "A set of available search engines and configurations for finding samples within signals. Search engines help locate specific samples, events, or patterns within signals, even in large datasets.";
    public static String SamplesSearch_Searches_IconId = "codicon-search";
    public static String SamplesSearch_Searches_HelpURL = "impulse-manual/12_preferences#search";

    public static String SamplesSearch_Search = "Search Engine";
    public static String SamplesSearch_Search_Description = "A functional block for searching samples within signals based on defined criteria. Search engines can be configured to locate events, values, or patterns efficiently.";
    public static String SamplesSearch_Search_IconId = "codicon-search";
    public static String SamplesSearch_Search_HelpURL = "impulse-manual/11_functional-blocks#search";
    public static String SamplesSearch_NoSearch = "No search configured";
    public static String SamplesSearch_NoConfig = "No configuration selected - uses search properties";

    public static String SamplesSearch_Properties = "Search Properties";
    public static String SamplesSearch_Properties_Description = "A set of properties and settings for a search engine, such as search expressions, criteria, and options like wrap search.";
    public static String SamplesSearch_Properties_IconId = "ri-download-2-line";
    public static String SamplesSearch_Properties_HelpURL = "impulse-manual/11_functional-blocks#search";

    public static String SamplesSearch_SearchConfiguration = "Search Configuration";
    public static String SamplesSearch_SearchConfiguration_Description = "A specific set of properties and settings for a search engine, defining how samples are searched. Configurations can be selected or edited in the Preferences dialog.";
    public static String SamplesSearch_SearchConfiguration_HelpURL = "impulse-manual/11_functional-blocks#user-configurations";

    public static String SamplesSearch_SearchConfigurations = "Search Configurations";
    public static String SamplesSearch_SearchConfigurations_Description = "A collection of user-defined or default configurations for search engines, allowing customization for specific search tasks. Configurations can be saved, reused, and shared.";
    public static String SamplesSearch_SearchConfigurations_HelpURL = "impulse-manual/11_functional-blocks#user-configurations";

    public static String SamplesSearch_Source = "Source";
    public static String SamplesSearch_Source_Description = "The signal or data source to be searched. Sources are selected before opening the search dialog and define the scope of the search operation.";
    public static String SamplesSearch_Source_IconId = "ri-open-source-line";
    public static String SamplesSearch_Source_HelpURL = "impulse-manual/11_functional-blocks#search";

    public static String SamplesSearch_Source_Source = "Source";
    public static String SamplesSearch_Source_Source_Description = "The specific signal or data element being searched.";
    public static String SamplesSearch_Source_Source_HelpURL = null;

    public static String SamplesSearch_DefaultSearchConfiguration = "Search Configuration";
    public static String SamplesSearch_DefaultSearchConfiguration_Description = "The default configuration used by a search engine when no user configuration is selected.";
    public static String SamplesSearch_DefaultSearchConfiguration_IconId = "ri-file-search-line";
    public static String SamplesSearch_DefaultSearchConfiguration_HelpURL = "impulse-manual/11_functional-blocks#user-configurations";
    public static String SamplesSearch_DefaultSearchConfiguration_Sources = "Sources";
    public static String SamplesSearch_DefaultSearchConfiguration_Source_Description = "Search Configuration";
    public static String SamplesSearch_DefaultSearchConfiguration_New = "MySearch";

    public static String SamplesSearch_ExpressionSearch = "Expression Search";
    public static String SamplesSearch_ExpressionSearch_IconId = "ri-calculator-line"; 
    public static String SamplesSearch_ExpressionSearch_Description = "Searches for samples matching a user-defined expression across selected signals. Supports multiple sources, flexible criteria, and wrap search. Refer to the documentation for syntax and usage.";
    public static String SamplesSearch_ExpressionSearch_HelpURL = "impulse-reference/47_expression-search";

    public static String SamplesSearch_InvalidExpression = "Invalid expression";
    public static String SamplesSearch_NoActiveCursor = "No active Cursor";
    public static String SamplesSearch_NoBooleanExpression = "No boolean expression";

    public static String SamplesSearch_GenerateExpression = "Generate";
    public static String SamplesSearch_GenerateExpression_IconId = "ri-newspaper-line";
    public static String SamplesSearch_GenerateExpression_Description = "Generate new expression";

    public static String SamplesSearch_NotFound = "Not found";
    public static String SamplesSearch_Found = "Found '%s' at %s";
    public static String SamplesSearch_FoundN = "Found '%d' positions for %s";
    public static String SamplesSearch_Canceled = "Canceled";
    public static String SamplesSearch_Error = "Got error: %s";

    // ========================================================================================================================
    // Sample Formatter
    // ========================================================================================================================

    public static String SamplesFormatter_SamplesFormatters = "Formatters";
    public static String SamplesFormatter_SamplesFormatters_Description = "A set of available sample formatters. Formatters define how sample values are displayed, such as hexadecimal, binary, decimal, or custom formats, enabling clear and meaningful visualization of signal data.";
    public static String SamplesFormatter_SamplesFormatters_IconId = "ri-file-text-fill";
    public static String SamplesFormatter_SamplesFormatters_HelpURL = "impulse-manual/12_preferences#formatters";

    public static String SamplesFormatter_SamplesFormatter = "Formatter";
    public static String SamplesFormatter_SamplesFormatter_Description = "A functional block for formatting and displaying sample values. Formatters control the representation of data, supporting various numeric bases, labels, and custom display options.";
    public static String SamplesFormatter_SamplesFormatter_IconId = "ri-text-spacing";
    public static String SamplesFormatter_SamplesFormatter_HelpURL = "impulse-manual/11_functional-blocks#formatter";

    public static String SamplesFormatter_HexFormatter = "Hex";
    public static String SamplesFormatter_HexFormatter_Description = "Displays numeric sample values in hexadecimal notation. Supports configurable width and casing. Prefix '0x' and lowercase/uppercase options available.";
    public static String SamplesFormatter_HexFormatter_IconId = "ri-text-spacing";
    public static String SamplesFormatter_HexFormatter_HelpURL = "impulse-reference/37_hex-formatter";

    public static String SamplesFormatter_BinFormatter = "Bin";
    public static String SamplesFormatter_BinFormatter_Description = "Displays numeric sample values in binary notation. Supports configurable width and grouping. Prefix '0b' option available.";
    public static String SamplesFormatter_BinFormatter_IconId = "ri-text-spacing";
    public static String SamplesFormatter_BinFormatter_HelpURL = "impulse-reference/38_bin-formatter";

    public static String SamplesFormatter_OctFormatter = "Oct";
    public static String SamplesFormatter_OctFormatter_Description = "Displays numeric sample values in octal notation. Supports configurable width and prefix. Prefix '0o' option available.";
    public static String SamplesFormatter_OctFormatter_IconId = "ri-text-spacing";
    public static String SamplesFormatter_OctFormatter_HelpURL = "impulse-reference/39_oct-formatter";

    public static String SamplesFormatter_DecFormatter = "Dec";
    public static String SamplesFormatter_DecFormatter_Description = "Displays numeric sample values in decimal notation. Supports inline configuration for custom formatting patterns (e.g., ###.###, 000.###, etc.).";
    public static String SamplesFormatter_DecFormatter_IconId = "ri-text-spacing";
    public static String SamplesFormatter_DecFormatter_HelpURL = "impulse-reference/40_dec-formatter";

    public static String SamplesFormatter_DomainFormatter = "Domain";
    public static String SamplesFormatter_DomainFormatter_Description = "Displays domain (time/index) values using preferred units and formatting. Supports inline configuration for unit and formatting style (preferred, auto, raw).";
    public static String SamplesFormatter_DomainFormatter_IconId = "ri-text-spacing";
    public static String SamplesFormatter_DomainFormatter_HelpURL = "impulse-reference/41_domain-formatter";

    public static String SamplesFormatter_KeyValueFormatter = "KeyValue";
    public static String SamplesFormatter_KeyValueFormatter_Description = "Displays structured values as key:value pairs, showing member names and their corresponding values. Intended for struct and array signals.";
    public static String SamplesFormatter_KeyValueFormatter_IconId = "ri-text-spacing";
    public static String SamplesFormatter_KeyValueFormatter_HelpURL = "impulse-reference/42_keyvalue-formatter";

    public static String SamplesFormatter_ValuesOnlyFormatter = "ValuesOnly";
    public static String SamplesFormatter_ValuesOnlyFormatter_Description = "Displays only raw values, omitting keys or labels. Intended for struct and array signals.";
    public static String SamplesFormatter_ValuesOnlyFormatter_IconId = "ri-text-spacing";
    public static String SamplesFormatter_ValuesOnlyFormatter_HelpURL = "impulse-reference/43_valuesonly-formatter";

    public static String SamplesFormatter_HtmlFormatter = "Html";
    public static String SamplesFormatter_HtmlFormatter_Description = "Renders sample content as HTML. Supports formatting of associated metadata.";
    public static String SamplesFormatter_HtmlFormatter_IconId = "ri-text-spacing";
    public static String SamplesFormatter_HtmlFormatter_HelpURL = "impulse-reference/44_html-formatter";

    public static String SamplesFormatter_LabelFormatter = "Label";
    public static String SamplesFormatter_LabelFormatter_Description = "Uses defined labels for enumerations or mapped values. Supports inline configuration for custom value:label pairs (e.g., label/1:first,2:second).";
    public static String SamplesFormatter_LabelFormatter_IconId = "ri-text-spacing";
    public static String SamplesFormatter_LabelFormatter_HelpURL = "impulse-reference/45_label-formatter";

    public static String SamplesFormatter_BytesFormatter = "Bytes";
    public static String SamplesFormatter_BytesFormatter_Description = "Displays binary data as hexadecimal byte sequences or selectable encodings. Supports inline configuration for number of bytes to display (e.g., bytes/16, bytes/all).";
    public static String SamplesFormatter_BytesFormatter_IconId = "ri-text-spacing";
    public static String SamplesFormatter_BytesFormatter_HelpURL = "impulse-reference/46_bytes-formatter copy";
    // ========================================================================================================================
    // Producer
    // ========================================================================================================================
    /*
     * 
     * public static String CellProducer_ScriptedRecordProducer = "Scripted Record Producer"; public static String
     * CellProducer_ScriptedRecordProducer_Description =
     * "If you are reading signal and trace data from a TCP, file, serial or other port input and with a format unknown to the impulse tool, you can get on the right track with a scripted Producer. Using a scripted Producer is an easy way to read data from any format."
     * ; public static String CellProducer_ScriptedRecordProducer_IconId = "codicon-code"; public static String
     * CellProducer_ScriptedRecordProducer_HelpURL = "210-r005-scripted-Producer"; public static String
     * CellProducer_ScriptedRecordProducerConfiguration = "Scripted Producer Configuration"; public static String
     * CellProducer_ScriptedRecordProducerConfiguration_Description =
     * "To create a Scripted Producer, open the preferences and go to \"impulse->Serializer-> Scripted Producer\", open the configuration dialog and add a new Scripted Producer configuration. "
     * ; public static String CellProducer_ScriptedRecordProducerConfiguration_IconId = "ri-list-settings-line";
     */
    public static String CellProducer_Diff = "Record Diff";
    public static String CellProducer_Diff_IconId = "codicon-diff";
    public static String CellProducer_Diff_Description = "Creates a \"diff\" signal from 2 input signals. The diff signal is identical with the input if both input are equal. Regions with non equal signal data are tagged.";
    public static String CellProducer_Diff_HelpURL = null;

    public static String CellProducer_Diff_AppendOriginalInputs = "Append both original inputs (\u2780+\u2781) below diff(\u2248) or delta(\u0394) signal";
    public static String CellProducer_Diff_HideIdenticalContent = "Hide identical content";
    public static String CellProducer_Diff_IgnoreLonger = "Ignore if 2nd input (\u2781) is longer";
    public static String CellProducer_Diff_IgnoreShorter = "Ignore if 2nd input (\u2781) is shorter";
    public static String CellProducer_Diff_PrepareDeltaFloat = "Prepare delta (\u0394) for Float signals";
    public static String CellProducer_Diff_PrepareDeltaInteger = "Prepare delta (\u0394) for Integer signals";
    public static String CellProducer_Diff_PrepareDiff = "Prepare diff";
    public static String CellProducer_Diff_PrepareInputs = "Prepare inputs";

    public static String CellProducer_TransactionMetrics = "Transaction Metrics";
    public static String CellProducer_TransactionMetrics_IconId = "codicon-graph";
    public static String CellProducer_TransactionMetrics_Description = "Aggregates and analyzes statistical metrics from multiple transaction analyzers (e.g., TLM, AXI) across many signals to provide combined insights and performance data.";
    public static String CellProducer_TransactionMetrics_HelpURL = null;

    public static String CellPort_ScriptedRecordPort = "Scripted Record Port";
    public static String CellPort_ScriptedRecordPort_Description = "If you are reading signal and trace data from a TCP, file, serial or other port input and with a format unknown to the impulse tool, you can get on the right track with a scripted Port. Using a scripted Port is an easy way to read data from any format.";
    public static String CellPort_ScriptedRecordPort_IconId = "codicon-code";
    public static String CellPort_ScriptedRecordPort_HelpURL = null;
    public static String CellPort_ScriptedRecordPortConfiguration = "Scripted Port Configuration";
    public static String CellPort_ScriptedRecordPortConfiguration_Description = "To create a Scripted Port, open the preferences and go to \"impulse->Serializer-> Scripted Port\", open the configuration dialog and add a new Scripted Port configuration. ";
    public static String CellPort_ScriptedRecordPortConfiguration_IconId = "ri-list-settings-line";

    public static String CellPort_Tcp = "Tcp Port";
    public static String CellPort_Tcp_IconId = "codicon-diff";
    public static String CellPort_Tcp_Description = "Creates a \"diff\" signal from 2 input signals. The diff signal is identical with the input if both input are equal. Regions with non equal signal data are tagged.";
    public static String CellPort_Tcp_HelpURL = null;
    public static String CellPort_Tcp_Mode = "Mode";
    public static String CellPort_Tcp_Mode0 = "Client";
    public static String CellPort_Tcp_Mode1 = "Client - waiting for server";
    public static String CellPort_Tcp_Mode2 = "Server";
    public static String CellPort_Tcp_Mode_Description = "Mode";
    public static String CellPort_Tcp_Socket = "Socket";
    public static String CellPort_Tcp_Socket_Description = "Socket";
    public static String CellPort_Tcp_Server = "Server";
    public static String CellPort_Tcp_Server_Description = "Server";
    public static String CellPort_Tcp_LogPath = "LogPath";
    public static String CellPort_Tcp_LogPath_Description = "LogPath";
    public static String CellPort_Tcp_EnableStimulation = "EnableStimulation";
    public static String CellPort_Tcp_EnableStimulation_Description = "EnableStimulation";

    // ========================================================================================================================
    // Serializer
    // ========================================================================================================================

    public static String Serializer_FluxReader = "flux Trace Reader";
    public static String Serializer_FluxReader_IconId = null;
    public static String Serializer_FluxReader_Description = "flux trace format is an open waveform/trace format targeting semiconductor and multi-core embedded system use-cases. The trace data is packed into a binary format and allows scalable compression.";
    public static String Serializer_FluxReader_HelpURL = "impulse-reference/14_flux-reader";

    public static String Serializer_RecJxReader = "Signal Expression Reader";
    public static String Serializer_RecJxReader_IconId = null;
    public static String Serializer_RecJxReader_Description = "RecJx files are wave files based on java expression (jx). You can create signal references, define test vectors for your design, or script a custom reader. Everything is based on the same simple API used in signal scripts and serializers.";
    public static String Serializer_RecJxReader_HelpURL = "impulse-reference/15_recJx-reader";

    public static String Serializer_LineReader = "Test Line Reader";
    public static String Serializer_LineReader_IconId = null;
    public static String Serializer_LineReader_Description = "A test serializer to help users get their content into impulse. The Test Line Reader creates a text signal containing all lines from the input, with the domain base set to the current time from the start of reading.";
    public static String Serializer_LineReader_HelpURL = "impulse-reference/16_test-reader";

    public static String Serializer_ByteBlockReader = "Test Byte Block Reader";
    public static String Serializer_ByteBlockReader_IconId = null;
    public static String Serializer_ByteBlockReader_Description = "A test serializer to help users get their content into impulse. The Test Byte Block Reader creates a binary signal with binary blocks from the input, with the domain base set to the current time from the start of reading.";
    public static String Serializer_ByteBlockReader_HelpURL = "impulse-reference/16_test-reader";

    public static String Serializer_Include = "Include";
    public static String Serializer_Include_Description = "Regular expression pattern to include specific signals during import. Only signals matching this pattern will be imported into the waveform viewer.";
    public static String Serializer_Exclude = "Exclude";
    public static String Serializer_Exclude_Description = "Regular expression pattern to exclude specific signals during import. Signals matching this pattern will be filtered out and not imported.";
    public static String Serializer_Start = "Start";
    public static String Serializer_Start_Description = "Start time position for importing samples. Only value changes at or after this time will be imported (specified in domain units like ns, us, ms).";
    public static String Serializer_End = "End";
    public static String Serializer_End_Description = "End time position for importing samples. Only value changes before or at this time will be imported (specified in domain units like ns, us, ms).";
    public static String Serializer_Delay = "Delay";
    public static String Serializer_Delay_Description = "Time offset to shift all timestamps during import. Positive values delay the waveform, negative values advance it (specified in domain units). Applied before dilation.";
    public static String Serializer_Dilate = "Dilate";
    public static String Serializer_Dilate_Description = "Time scaling factor to stretch or compress the temporal dimension of the waveform. Values > 1.0 slow down time, values < 1.0 speed up time. Applied after delay transformation using formula: (time + delay) * dilate.";
    public static String Serializer_Lazy = "Lazy";
    public static String Serializer_Lazy_Description = "Enable lazy loading mode where signal data is read from file only when actually requested by the viewer, reducing initial memory usage for large files.";
    public static String Serializer_Empty = "Keep empty scopes";
    public static String Serializer_Empty_Description = "Preserve empty hierarchical scopes in the signal tree structure even when they contain no actual signals or variables.";
    public static String Serializer_HierarchyResolver = "Resolve Hierarchy";
    public static String Serializer_HierarchyResolver_Description = "Enable hierarchical signal organization by creating nested scope structures based on signal names. Enter the name split regex to enable.";
    public static String Serializer_VectorResolver = "Resolve Vectors";
    public static String Serializer_VectorResolver_Description = "Enable automatic grouping and resolution of multi-bit vector signals based on bit indices and signal naming conventions.";

    public static String Serializer_NeedToReload1 = "The reader has been changed. Please reload !";
    public static String Serializer_NeedToReload2 = "The reader configuration has been changed. Please reload !";
    public static String Serializer_EmptyFile = "Empty file";
    public static String Serializer_InvalidCharacter = "Invalid character";
    public static String Serializer_InvalidDomainBase = "Invalid domain base";
    public static String Serializer_InvalidEntry = "Invalid entry";
    public static String Serializer_InvalidId = "Invalid id";
    public static String Serializer_InvalidLogicVector = "Invalid logic vector";
    public static String Serializer_InvalidNoColumns = "Invalid no of columns";
    public static String Serializer_InvalidParameterCount = "Invalid parameter count";
    public static String Serializer_InvalidParameterValues = "Invalid parameter values";
    public static String Serializer_InvalidProcessType = "Invalid process type";
    public static String Serializer_InvalidScale = "Invalid scale";
    public static String Serializer_InvalidScope = "Invalid scope";
    public static String Serializer_InvalidSignalType = "Invalid signal type";
    public static String Serializer_InvalidVersion = "Invalid version";
    public static String Serializer_LockedSerializer = "Locked serializer";
    public static String Serializer_Must3Lines = "Must be at least 3 lines";
    public static String Serializer_NoCommandFound = "No command found";
    public static String Serializer_NoValidHeader = "No valid header";
    public static String Serializer_NotInitialized = "Not Initialized";
    public static String Serializer_ParseError = "Parse Error";
    public static String Serializer_RealTypeHasVector = "Real type has vector";
    public static String Serializer_SharedIdsDifferentScale = "Shared ids but different scale";
    public static String Serializer_StringTypeHasVector = "String type has vector";
    public static String Serializer_SyntaxError = "Syntax Error";
    public static String Serializer_TheSelectedSerializerLocked = "The selected serializer is locked!";
    public static String Serializer_WriterNotFound = "Writer not found";

    public static String Serializer_ScriptedRecordReader = "Scripted Record Reader";
    public static String Serializer_ScriptedRecordReader_Description = "";
    public static String Serializer_ScriptedRecordReader_IconId = "codicon-code";
    public static String Serializer_ScriptedRecordReader_HelpURL = null;
    public static String Serializer_ScriptedRecordReaderConfiguration = "Scripted Reader Configuration";
    public static String Serializer_ScriptedRecordReaderConfiguration_Description = "To create a Scripted Reader, open the preferences and go to \"impulse->Serializer-> Scripted Reader\", open the configuration dialog and add a new Scripted Reader configuration. ";
    public static String Serializer_ScriptedRecordReaderConfiguration_IconId = "ri-list-settings-line";

    // ========================================================================================================================
    // Record
    // ========================================================================================================================

    public static String Record_Record = "Record";
    public static String Record_Record_Description = "A record is the top-level organizational structure in impulse, used to collect, manage, and analyze sets of signals and related elements. Records provide a hierarchical framework for organizing signals, scopes, proxies, and relations, enabling efficient handling of complex datasets from various sources such as simulations, log files, or measurements.";
    public static String Record_Record_IconId = "ri-record-circle-fill";
    public static String Record_Record_HelpURL = "impulse-reference/3_record";

    public static String Record_NativeType = "Native";
    public static String Record_NativeType_Description = "The original or underlying data type as represented in the source or native format, before any conversion or abstraction.";
    public static String Record_Content_Group_Description = "The 'Content' section of a record or scope provides a comprehensive, tabular overview of all its contained elements, including signals, sub-scopes, proxies, and relations. This section enables users to explore, filter, and analyze the hierarchical structure and properties of all sub-elements, making it easier to manage and understand complex records. Reviewing this section helps ensure that all relevant data and organizational elements are visible and accessible for further analysis or configuration.";

    public static String Record_Scope = "Scope";
    public static String Record_Scope_Description = "A scope is an organizational element used to group and structure signals and other record elements within a hierarchical tree. Scopes help manage complex datasets by providing logical containers, making navigation, analysis, and visualization more intuitive. They can contain signals, sub-scopes, proxies, and relations, reflecting the logical or physical structure of the system being analyzed.";
    public static String Record_Scope_IconId = "codicon-folder";
    public static String Record_Scope_HelpURL = "impulse-reference/2_scope";

    public static String Record_Active_Embedded = "Show";
    public static String Record_Active_Embedded_Description = "Show embedded controls in viewer";
    public static String Record_Active_SaveContent = "Save Content";
    public static String Record_Active_SaveContent_Description = "SaveContent";

    public static String Record_Include = "Include";
    public static String Record_Include_Description = "An include is an active record element that allows you to import external files or data sources into a record. This enables the reuse of data and configurations across multiple records, supporting flexible integration and consistent analysis workflows. Includes can bring in signals, scopes, and other elements, and offer options to display configuration sections and save imported content with the main record.";
    public static String Record_Include_IconId = "ri-download-2-line";
    public static String Record_Include_HelpURL = "impulse-reference/5_include";
    public static String Record_Include_Source = "Source";
    public static String Record_Include_Source_Description = "The file or data source to be included in the record. You can select a file using the dialog or manually enter the path. The source determines what content is imported and integrated into the current record structure.";
    public static String Record_Include_CouldNotResolveElement = "Could not resolve element:";
    public static String Record_Include_CouldNotCreateReader = "Could not create reader:";
    public static String Record_Include_Source_Group_Description = "The 'Source' section specifies the file or data source to be imported into the record. Here, you can select or enter the path to the external file or resource. The chosen source determines what content is brought into the record, enabling integration of signals, scopes, and other elements from various formats or locations.";

    public static String Record_Analysis = "Analysis";
    public static String Record_Analysis_Description = "An analysis is an active record element that processes existing signals or data within a record to generate new signals or results. It enables advanced processing, transformation, and interpretation of data, supporting workflows such as statistical calculations, mathematical transformations, and protocol decoding. Analysis elements can operate in the background and update record content dynamically as new data becomes available.";
    public static String Record_Analysis_IconId = "codicon-server-process";
    public static String Record_Analysis_HelpURL = "impulse-reference/6_analysis";
    public static String Record_Analysis_Source_Group_Description = "The 'Sources' section of the Analysis dialog specifies the input signals, scopes, or other elements to be analyzed or transformed. Here, you can select or enter the paths to the data elements that will serve as inputs for the analysis. Multiple sources can be managed, enabling flexible and powerful data processing workflows.";

    public static String Record_Interface = "Interface";
    public static String Record_Interface_Description = "An interface is an active record element that exposes selected signals, scopes, or other elements from a record for external access or integration. Interfaces enable modular design, controlled data sharing, and interoperability between records or tools by defining which internal elements are made available and how they are presented.";
    public static String Record_Interface_IconId = "codicon-symbol-interface";
    public static String Record_Interface_HelpURL = "impulse-reference/7_interface";

    public static String Record_Signal = "Signal";
    public static String Record_Signal_Description = "A signal in impulse represents a stream of values over a domain (such as time, index, or other units) and is defined by attributes including name, description, process type (discrete or continuous), signal type (logic, float, integer, event, text, structure, binary), tags, scale, format specifier, domain base, start/end, rate, samples, and attachments. These properties enable flexible modeling, visualization, and analysis of digital, analog, or structured data.";
    public static String Record_Signal_IconId = "ti-signal-any";
    public static String Record_Signal_HelpURL = "impulse-reference/1_signal";

    public static String Record_Signal_Signal_Group_Description = "The 'Signal' section of the dialog displays the technical and structural properties of the signal, such as sample type, scale, format, domain base, start/end, rate, and count. These fields provide an overview of how the signal's data is stored, interpreted, and displayed, helping users understand its structure and characteristics. All fields in this section are read-only and reflect the signal's definition at creation or import.";
    public static String Record_Signal_Content_Group_Description = "The 'Content' section of a signal provides a detailed, tabular overview of all its samples, including their positions, values, tags, and group information. This allows users to inspect, compare, and analyze individual data points, especially in signals with many samples or complex event groupings. Reviewing this section helps ensure the signal is accurately defined and ready for analysis or visualization.";

    public static String Record_Signal_SampleType = "Sample type";
    public static String Record_Signal_SampleType_Description = "The data type of the signal's samples, such as Logic, Integer, Float, Text, Structure, or Binary.";
    public static String Record_Signal_Scale = "Scale";
    public static String Record_Signal_Scale_Description = "The dimension or width of the signal, e.g., number of bits or array size.";
    public static String Record_Signal_Format = "Format";
    public static String Record_Signal_Format_Description = "How the signal's values are displayed, e.g., binary, decimal, hexadecimal, ASCII, or label.";
    public static String Record_Signal_Start = "Start";
    public static String Record_Signal_Start_Description = "The starting position or time for the signal in its domain.";
    public static String Record_Signal_End = "End";
    public static String Record_Signal_End_Description = "The ending position or time for the signal in its domain.";
    public static String Record_Signal_Rate = "Rate";
    public static String Record_Signal_Rate_Description = "The interval at which samples occur; fixed for continuous signals, zero for discrete.";
    public static String Record_Signal_Count = "Count";
    public static String Record_Signal_Count_Description = "The total number of samples in the signal.";
    public static String Record_Signal_DomainBase = "Domain base";
    public static String Record_Signal_DomainBase_Description = "The minimum distance between two samples, typically measured in units like nanoseconds (ns), picoseconds (ps), or other domain units.";

    public static String Record_Proxy = "Signal Proxy";
    public static String Record_Proxy2 = "Proxy";
    public static String Record_Proxy_Description = "A signal proxy is a reference element within a record that points to an existing signal, allowing the same signal data to be accessed, reused, or presented in multiple locations without duplication. Proxies support flexible organization, alternative views, and efficient management of complex datasets by enabling dynamic linking to signals throughout the record structure.";
    public static String Record_Proxy_IconId = "ti-signal-proxy";
    public static String Record_Proxy_HelpURL = "impulse-reference/4_proxy";
    public static String Record_SignalReference = "Signal";
    public static String Record_SignalReference_Description = "A reference to another signal within the record, typically used for proxying, linking, or reusing signal data in different contexts. This allows the same signal to be accessed or displayed from multiple locations without duplicating its data, supporting flexible organization and analysis.";
    public static String Record_SignalReference_NoSignalSelected = "No Signal selected";

    public static String Record_Source = "Source";
    public static String Record_Source_Description = "A Source is a record element that represents a signal or scope within the record, used as the input for other elements such as analysis or includes. It enables referencing and integrating existing data structures as sources for calculations, transformations, or hierarchical organization, supporting flexible data integration and analysis workflows.";
    public static String Record_Source_IconId = "ri-external-link-line";
    public static String Record_Source_HelpURL = null;
    public static String Record_Source_Reference = "Reference";
    public static String Record_Source_Reference_Description = "The field refers to a scope, signal, or other record element within the record. It is used to specify the input element for analysis, includes, or other record features, enabling flexible referencing and integration of existing data structures.";

    public static String Record_Relation = "Relation";
    public static String Record_Relation_Description = "A relation is a record element that defines a connection between signals, proxies, scopes, or other elements within a record. Relations enable structured interactions, dependencies, and advanced analysis by linking elements as sources and targets, supporting workflows such as timing analysis, data flow visualization, and system behavior tracing.";
    public static String Record_Relation_IconId = "codicon-link";
    public static String Record_Relation_HelpURL = "impulse-reference/8_relation";
    public static String Record_RelationSource = "Source";
    public static String Record_RelationSource_Description = "The source element of the relation. Select a signal, scope, or other record element to serve as the starting point of the connection.";
    public static String Record_RelationSource_NoSourceSelected = "No source selected";
    public static String Record_RelationTarget = "Target";
    public static String Record_RelationTarget_Description = "The target element of the relation. Select a signal, scope, or other record element to serve as the endpoint of the connection.";
    public static String Record_RelationTarget_NoTargetSelected = "No target selected";

    // ========================================================================================================================
    // View
    // ========================================================================================================================

    public static String View_Views = "Views";
    public static String View_Views_Description = "A collection of customizable views for organizing and visualizing signal data.";
    public static String View_Views_IconId = "ri-layout-masonry-line";
    public static String View_Views_HelpURL = "impulse-manual/12_preferences#views";

    public static String View_View = "View";
    public static String View_View_Description = "A single view for arranging signals, folders, axes, and cursors for tailored analysis.";
    public static String View_View_IconId = "ri-layout-masonry-line";
    public static String View_View_HelpURL = "impulse-reference/9_view";

    public static String View_Empty = "New Empty View";
    public static String View_Empty_Description = "Adds an empty view";
    public static String View_Empty_IconId = View_Views_IconId;
    public static String View_Flat = "New flat View ";
    public static String View_Flat_Description = "Adds a view with a flat list of all signals";
    public static String View_Flat_IconId = "ri-play-list-add-line";
    public static String View_Hierarchy = "New hierarchical View";

    public static String View_Hierarchy_Description = "Adds a new view with a hierarchy of all signals and scopes";
    public static String View_Hierarchy_IconId = "ri-node-tree";

    public static String View_Cursors = "Cursors";
    public static String View_Cursors_Description = "A set of tools for measurement and navigation within signals in a view.";
    public static String View_Cursors_IconId = "ri-input-cursor-move";
    public static String View_Cursors_HelpURL = "impulse-reference/9_view#cursors-section";

    public static String View_Cursor = "Cursor";
    public static String View_Cursor_Description = "A tool for precise measurement and navigation within a signal.";
    public static String View_Cursor_IconId = "ri-input-cursor-move";
    public static String View_Cursor_HelpURL = null;

    public static String View_Axes = "Axes";
    public static String View_Axes_Description = "A set of axes for visualizing signals across different domains in a view.";
    public static String View_Axes_IconId = "ri-input-cursor-move";
    public static String View_Axes_HelpURL = "impulse-reference/9_view#axes-section";

    public static String View_Axis = "Axis";
    public static String View_Axis_Description = "A domain or scale used for displaying signals in a view.";
    public static String View_Axis_IconId = "ri-input-cursor-move";
    public static String View_Axis_HelpURL = null;

    public static String View_Axis_Type = "Type";
    public static String View_Axis_Type_Description = "";

    public static String View_Axis_Areas = "Areas";
    public static String View_Axis_Areas_Description = "";

   
    public static String View_DefaultAxis = "Default Axis";
    public static String View_DefaultAxis_Description = "The default axis used for displaying signals in a view when no specific axis is selected or configured.";
    public static String View_DefaultAxis_IconId = "ri-input-cursor-move";
    public static String View_DefaultAxis_HelpURL = null;

    public static String View_Axis_Primary = "Primary Axis";
    public static String View_Axis_Secondary = "Secondary Axis";

    public static String View_SignalsFolders = "Signals & Folders";
    public static String View_SignalsFolders_Description = "The content of a view, including View Signals and other folders, organized hierarchically.";
    public static String View_SignalsFolders_IconId = "ri-line-chart-line/#808080";
    public static String View_SignalsFolders_HelpFragment = "#signals--folders-section";

    public static String View_Signal = "View Signal";
    public static String View_Signal_Description = "A configurable representation of signal data within a view, supporting processing and visualization options.";
    public static String View_Signal_IconId = "ri-line-chart-line/#808080";
    public static String View_Signal_HelpURL = "impulse-reference/11_view-signal";

    public static String View_Signal_PrimarySource = "Primary Source (s0)";
    public static String View_Signal_PrimarySource_Description = "The main signal or data source used for visualization and processing in the view.";
    public static String View_Signal_PrimarySource_HelpURL = null;

    public static String View_Signal_AdditionalSources = "Additional Sources (s1,...)";
    public static String View_Signal_AdditionalSources_Description = "Additional signals or data sources used in the view for comparison, combination, or advanced analysis.";
    public static String View_Signal_AdditionalSources_HelpURL = null;

    public static String View_Signal_Induce = "Induce";
    public static String View_Signal_Induce_Description = "Defines the input source and processor for generating or transforming the signal.";
    public static String View_Signal_Induce_HelpFragment = "#induce-section";
    public static String View_Signal_InduceNoProcessor = "No processor configured - uses raw primary source";
    public static String View_Signal_InduceNoProcessorConfig = "No configuration selected - uses processor properties";

    public static String View_Signal_Visualize = "Visualize";
    public static String View_Signal_Visualize_Description = "Configures how the signal is displayed, including diagram type, axis, and display options.";
    public static String View_Signal_Visualize_HelpFragment = "#visualize-section";
    public static String View_Signal_VisualizeNoDiagram = "No diagram configured - uses 'default'";
    public static String View_Signal_VisualizeNoDiagramConfig = "No configuration selected- uses diagram properties";

    public static String View_Signal_Deduce = "Deduce";
    public static String View_Signal_Deduce_Description = "Configures further analysis or extraction of derived signals or components.";
    public static String View_Signal_Deduce_HelpFragment = "#deduce-section";
    public static String View_Signal_DeduceNoProcessor = "No processor configured - uses 'default'";
    public static String View_Signal_DeduceNoProcessorConfig = "No configuration selected - uses processor properties";

    public static String View_Signal_DeducedSignals = "Deduced Signals";
    public static String View_Signal_DeducedSignals_Description = "Derived signals are automatically created by the selected processor and do not have any user settings by default (such as color or display options). If you want to view or visualize these signals differently than the default, you can add a \"Deduced Signal\" entry. This is done by double-clicking a deduced signal to edit, which prompts you to create a custom setting. The settings you define for a Deduced Signal will be applied to the visualization of the corresponding derived signal if available. The Deduced Signals table provides an overview of which derived signals have custom settings and allows you to manage them (add, edit, delete, reorder).";
    public static String View_Signal_DeducedSignals_HelpFragment = "#deduce-section";

    public static String View_Signal_Axis = "Axis";
    public static String View_Signal_Axis_Description = "The axis used for displaying the signal in the view.";
    public static String View_Signal_Axis_HelpURL = null;

    public static String View_Signal_Combine = "Combine";
    public static String View_Signal_Combine_Description = "Defines how this signal is combined with adjacent signals for visualization or analysis.";
    public static String View_Signal_Combine_HelpURL = null;

    public static String View_Signal_CombineNone = "Dont Combine";
    public static String View_Signal_CombinePrevious = "Combine with previous";
    public static String View_Signal_CombineNext = "Combine with next";

    public static String View_Signal_Above = "Above";
    public static String View_Signal_Hide = "Hide";

    public static String View_Signal_Within = "Within";

    public static String View_Folder = "Folder";
    public static String View_Folder_Description = "A hierarchical container for organizing View Signals and other folders within a view.";
    public static String View_Folder_IconId = "codicon-folder";
    public static String View_Folder_HelpURL = "impulse-reference/10_folder";

    public static String View_DeducedSignal = "Deduced Signal";
    public static String View_DeducedSignal_Description = "A user-defined customization for a derived signal, allowing override of default visualization and properties.";
    public static String View_DeducedSignal_IconId = "ri-line-chart-line/#808080";
    public static String View_DeducedSignal_HelpURL = "impulse-reference/12_deduced-signal";

    public static String View_Source = "Source";
    public static String View_Source_Description = "";
    public static String View_Source_IconId = "ri-open-source-line";
    public static String View_Source_HelpURL = null;

    public static String View_Source_Source = "Source";
    public static String View_Source_Source_Description = "Source";
    public static String View_Source_Source_HelpURL = null;

    // ========================================================================================================================
    // Wallet
    // ========================================================================================================================

    public static String Wallet_Wallet = "Preference Wallet";
    public static String Wallet_Wallet_Description = "";
    public static String Wallet_Wallet_IconId = "ri-wallet-line";
    public static String Wallet_Wallet_HelpURL = null;

    // ========================================================================================================================
    // Logging UC
    // ========================================================================================================================

    public static String Logging_DomainFloat = "Float value (e.g. 0,033ms, 0.4)";
    public static String Logging_DomainInteger = "Integer value (e.g. 100 us, 50)";
    public static String Logging_DomainDate = "Date (e.g. yyyy-MM-dd HH:mm:ss,SSS)";
    public static String Logging_DomainSame = "Same as previous";
    public static String Logging_DomainSameSignal = "Same as previous (per Signal)";
    public static String Logging_DomainIncrementing = "Incrementing";
    public static String Logging_DomainIncrementingSignal = "Incrementing (per Signal)";
    public static String Logging_DomainReceptionTime = "Reception time";
    public static String Logging_NameSource = "Name from source value";
    public static String Logging_NameHierarchySource = "Hierarchy from source value";
    public static String Logging_NameExplicit = "Explicit name";
    public static String Logging_NameHierarchyExplicit = "Explicit hierarchy";
    public static String Logging_NameExtensionSource = "Name extension from source value";
    public static String Logging_AddFilePosition = "Add log file position (enables synchronisation with log file)";
    public static String Logging_SignalScopeName = "Signal/Scope Name";
    public static String Logging_HierarchySepPrefix = "Hierarchy sep./Prefix:";
    public static String Logging_ExtensionMode = "Extension Mode:";
    public static String Logging_ExtensionSource = "Extension Source:";
    public static String Logging_ParseFromValue = "Parse unit from value";
    public static String Logging_ExtensionUnit = "Extension Unit:";
    public static String Logging_UseRelativeDomainValue = "Use relative (to the first event) domain value";
    public static String Logging_SniffBytes_ = "Sniff no of bytes:";

    public static String Logging_Detect = "Detect with:";

    // ========================================================================================================================
    // ????
    // ========================================================================================================================

    public static String Adapter_CommandIsEmpty = "Command is empty!";
    public static String Adapter_CouldNotCreateInput = "Could not create input!";
    public static String Adapter_CouldNotCreateInput_Description = "Could not create input!";
    public static String Adapter_CouldNotCreateInputReader = "Could not create input/reader!";
    public static String Adapter_CouldNotCreateProcess = "Could not create process!";
    public static String Adapter_CouldNotCreateReader = "Could not create reader!";
    public static String Adapter_CouldNotCreateSocket = "Could not create socket!";
    public static String Adapter_CouldNotCreateSocket_Description = "Could not create socket!";
    public static String Adapter_EnableStimulationScript = "Enable Script";
    public static String Adapter_EnableSync = "Enable Sync";
    public static String Adapter_ErrorsOccuredWhileStarting = "Errors occured while starting the port. Please check console log.";
    public static String Adapter_FileDoesNotExist = "File does not exist!";
    public static String Adapter_InsertAsRoot = "Insert at Root";
    public static String Adapter_NoReaderSelected = "No reader selected!";
    public static String Adapter_ReadUntilPortStopped = "Read until port stopped (no EoF)";
    public static String Adapter_ResourceDoesNotExist = "Resource does not exist!";
    public static String Adapter_ServerIsEmtpy = "Server is empty!";
    public static String Adapter_SocketIsInvalid = "Socket invalid!";
    public static String Adapter_StartingPort = "Starting port";
    public static String Adapter_Stimulation = "Stimulation Script";
    public static String Adapter_WriteInputToFile = "Write input to file";
    public static String Attachment_Label = "Label";
    public static String Attachment_Link = "Link";
    public static String Attachment_Relation = "Relation";
    public static String Axis_AxisType = "Axis type";
    public static String Axis_Dedicated = "Dedicated axis";
    public static String Axis_DedicatedOverridingDomain = "Dedicated axis with overriding domain base";
    public static String Axis_DomainAxis = "Domain Axis";
    public static String Axis_Linear = "Linear";
    public static String Axis_Log10 = "Log10";
    public static String Axis_No = "No dedicated axis";
    public static String Axis_RootDefault = "Use signal domain base";
    public static String Axis_RootOverridingDomain = "Override domain base with ";
    public static String ChartsPage_Comments = " The chart preference page enables the user to define new charts.\nThe basis for each new chart is a so called chart provider.\nimpulse contains providers for Birt charts, Nebula charts and script charts.\nUsers may extend impulse with additional chart providers.";
    public static String CompareDialog_Compare = "Compare";
    public static String CompareDialog_SelectScopes = "Select scopes for comparison";
    public static String CompareDialog_SignalFilter = "Signal filter";

    public static String RecordViewer_CouldNotFindView = "Could not find a suitable view. Do you want to create a new one?";
    public static String RecordViewer_NotViewSet = "No View has been selected. Do you want to create a new one ?";
    public static String RecordViewer_OpenSerializer = "Open Serializer";
    public static String RecordViewer_RecordViewer = "Record Viewer";
    public static String RecordViewer_SelectConfiguration = "Select Configuration";
    public static String RecordViewer_SelectSerializer = "Select Serializer";
    public static String RecordViewer_SerializerConfigurationRequired = "Configuration required";
    public static String RecordViewer_SerializerReportedParseException = "The serializer reported a parse exception. Do you want to open the message?";
    public static String RecordViewer_ViewMissing = "View missing";
    public static String RecordViewer_SourcesScopes = "Sources & Scopes";
    public static String RecordViewer_Graph = "Graph";
    public static String RecordViewer_ActiveView = "Active View";

    public static String SampleDialog_Sample = "Sample";
    public static String SampleView_MemberSettings = "Member settings";
    public static String SampleView_StandardSettings = "Standard settings";
    public static String SampleView_TypeSettings = "Type settings";

    public static String PaintTreeController_Combined = "Combined";
    public static String PaintTreeController_MaxMin = "Max/min";
    public static String PaintTreeController_MaxMinOf = "Max/min of";
    public static String PaintTreeController_SignalsNotFoundInView = "Signals not found in active View";
    public static String PaintTreeController_WantToAddNewPlot = "Do you want to add to active View ?";

    public static String SocketAdapterDialog_Client = "Normal Client (stops if server not available)";
    public static String SocketAdapterDialog_ClientWaitServer = "Waiting Client (waits for server to start)";
    public static String SocketAdapterDialog_Server = "Server (waits for one client to connect)";
    public static String SourceReferenceDialog_ReferenceToPlot = "Path refers a plot";
    public static String SourceReferenceDialog_ReferenceToSignal = "Path refers a signal";
    public static String SourceReferenceDialog_SelectFrom = "Select from";
    public static String ThemePage_ForDetailedThemeSettings = "For detailed theme settings, go to 'General->Appearance->Colors and Fonts'";
    public static String UdpAdapterDialog_ManualFeed = "Manual Feed  ";
    public static String UdpAdapterDialog_Normal = "Normal";

    public static String ViewsPage_Comments = "";
    public static String WalletEditor_ImpulseWalletEditor = "Impulse Wallet Editor";

}