<!---
title: "impulse for Cursor"
author: "Thomas Haber"
keywords: [impulse, cursor, extension, signal analysis, visualization, log, trace, wave, IDE, integration, data, automation, preferences]
description: "Cursor extension for the impulse framework, providing advanced signal analysis, visualization, and integration with diverse data sources. Enables engineers to analyze logs, traces, and simulation data directly in Cursor with a powerful, extensible platform."
category: "impulse-platforms"
tags: [cursor, extension, impulse, signal, analysis, visualization, integration, platform ]
docID: 
hash: 16f55e29b3484541dd32cc7cca8550c8
--->
# impulse for Cursor

[**impulse**](https://toem.io/products/impulse) is a powerful visualization and analysis workbench designed to help engineers understand, analyze, and debug complex semiconductor and multi-core software systems. It provides a seamless integration into IDE frameworks and supports a growing set of extensions, such as IoT, language IDEs, and reporting tools. 

With its client-server architecture, **impulse** divides workloads between signal-providing tasks (on a simulation or application server) and local analysis tools, ensuring efficient performance. Engineers can use their favorite web IDE to analyze logs, traces, and simulation data without transferring large amounts of data or relying on local tools.

**impulse**’s open extension mechanisms allow users to adapt it to their specific needs. Whether defining custom data formats, implementing acquisition interfaces, creating specialized diagrams, or integrating a 3D chart engine, **impulse** offers unparalleled flexibility. Its lightweight frontend and powerful Java backend enable the creation of high-performance, complex analysis applications.

# VS Code vs Cursor

**Cursor** is built on top of VS Code with significant modifications and enhancements, particularly in the area of AI and language model integration. While both platforms share a common foundation, Cursor introduces its own API layers and behavioral customizations tailored to enhance productivity through AI-driven features. This underlying relationship means that the impulse extension benefits from VS Code compatibility while adapting to Cursor's extended capabilities.

The **impulse** extension is designed to work seamlessly with both **VS Code** and **Cursor**. The core functionality, data handling, and visualization capabilities remain consistent across both platforms. However, there are differences in AI-related features due to the distinct APIs provided by each editor.

- **Single Codebase**: The same extension source code powers both VS Code and Cursor installations.
- **Platform Detection**: The extension automatically detects the running platform and adapts accordingly.
- **AI Feature Abstraction**: AI capabilities (such as inline suggestions, completions, and context-aware assistance) are abstracted to work with each platform's specific API:
  - **VS Code**: Uses VS Code's built-in AI/language model APIs.
  - **Cursor**: Leverages Cursor's enhanced AI integration and proprietary APIs.
- **VSX Marketplace**: Cursor primarily uses the VSX marketplace for extensions. The actual version of impulse for Cursor is available on the [VSX marketplace](https://open-vsx.org/), ensuring you get the latest updates and compatibility optimizations specific to Cursor.


> ## Beta Notice
> - This extension is a beta release with core features.
> - It may be unstable and contain defects.
> - Not intended for production use.
> - Back up important data and use with caution.
> - Do not rely on correct functioning or performance.

## Scope and Add-ons

- Includes core features and Cursor integration only.
- Domain-specific use cases (e.g., EDA, RTOS) requiring additional readers, adaptors, processors, or diagrams are not included.
- These capabilities are delivered as separate companion extensions (available now or coming soon).

### Add more features
- Open the Extensions view in Cursor and search for "impulse".
- Install the desired companion extensions (for example, a VCD reader).
- Reload the window if the backend is already running.

![](images/ss_cursor_platform1.png)

## Features

impulse offers a wide range of data formats and external interfaces through signal adaptors, providing a unified view of various data sources. Whether reading signals from log, wave, or trace files, or accessing live data streams, users can seamlessly integrate data from multiple channels. Signal adaptors allow direct connections to sources such as TCP streams, pipes, applications, external networks, serial interfaces, and data acquisition units. When dealing with multiple signal sources—such as log data from a serial interface and trace data over TCP—impulse can merge and synchronize them, ensuring accurate, comprehensive analysis across diverse systems and interfaces.

### Data Integration
- Supports log, wave, trace files, and live data streams.
- Enables connections to diverse data sources via signal adaptors including TCP streams, pipes, serial interfaces, and hardware.
- Synchronizes multiple signal sources for unified analysis.

### Customizable Views
- Create tailored views with structured navigation.
- Switch between views for efficient signal interpretation.
- Use diagrams to visualize signals across domains like time and frequency.

### Signal Processing
- Combine signals using mathematical operations.
- Decode data with protocol parsers.
- Generate reference signals and extract statistical insights.

### Sample Tables
- Tabular representation synchronized with viewers.
- Real-time updates for streaming data.
- Configurable columns for custom value representation.

### Automation
- Background processing of large datasets.
- Automatic conflict detection and pattern identification.
- Statistical extraction for performance insights.
d
![](images/ss_cursor_platform2.png)

## Quick Start

* Select a file to view.
* Use the context menus 'Open with' and select the impulse Viewer (You may select the impulse Viewer as the default option at this point).
* On activation, it may take a while (a few secs) to load the backend java server. The OS may ask you for approval.
* Accept the license.
* Create a new view.
* DnD signals into the view. 
* Have fun !

## License

**Our guiding principle for this and all subsequent versions is:**

* Non-commercial use is free.
* Commercial use requires a commercial license (including a free plan).
* see [LICENSE.md](LICENSE.md)
* see [Plans](https://toem.io/pricing)


## Documentation
 
Enter [https://toem.io/resources/](https://toem.io/resources/) for more information about impulse. 

## Preferences

The **Preferences** section allows users to configure various aspects of the impulse extension to suit their needs. These include server settings, debugging options, and preference storage locations. Users can modify these settings through the Cursor settings interface or by directly editing the `settings.json` file. Below are some key preferences:

- **`impulse.java`**: Command to start the Java OSGi backend. Default: `"java -Xms128m -Xmx2048m"`.
- **`impulse.port`**: Communication port for the backend. Default: `"0"` (random port).
- **`impulse.xdebugger`**: Enables Java extension debugging. Default: `false`.
- **`impulse.preferences`**: Format for storing preferences. Default: `"XML"`.

### Preference location and format

impulse provides flexibility in storing preferences by allowing users to choose between different storage locations. These locations can be configured for various preference categories, such as serializers, adaptors, views, and more. The available storage options are:

- **User**: Stores preferences globally for all workspaces.
- **Workspace**: Stores preferences specific to the current workspace.
- **Workspace Folder**: Stores preferences in a folder named `./impulse` within the workspace.

### Default Locations for Preference Categories

- **Serializers**: Default: `User`
- **Adaptors**: Default: `User`
- **Producers**: Default: `User`
- **Views**: Default: `Workspace`
- **Processors**: Default: `User`
- **Diagrams**: Default: `User`
- **Search**: Default: `Workspace`
- **Formatters**: Default: `User`
- **Templates**: Default: `User`
- **Licenses**: Default: `User`
- **Colors**: Default: `User`
- **Parts**: Default: `Workspace`
- **Commands**: Default: `Workspace`

To modify these preferences, use the [impulse Preference editor](command:de.toem.impulse.commands.preferences). Changes to some settings, such as the backend server configuration, may require a restart to take effect.

## Sources

Find the source code of the vscode extension at [https://github.com/toem/impulse.vscode](https://github.com/toem/impulse.vscode). 

## Issues and Proposals

Use [https://github.com/toem/impulse.vscode/issues](https://github.com/toem/impulse.vscode/issues) for issues and proposals.

You may use [support@toem.de](mailto:support@toem.de) for further information.