---
title: "Searching for Samples"
author: "Thomas Haber"
keywords: [search, samples, criteria, signals, search engine, configuration, wrap search]
description: "This document provides a detailed guide on searching for samples within data using a search dialog. It explains the search criteria, signals table, and search results sections of the dialog. Users can define search engines, configure properties, and enable wrap search. The document also describes action buttons for navigating matches and clearing results, making it a comprehensive resource for efficient data searching."
category: "impulse-manual"
tags:
  - manual
  - user interface
  - search
docID: 885
---
![](images/hd_search.png) 
# Searching for Samples

This page explains how to search for samples within your data. The search functionality helps you locate positions where selected samples meet specific criteria. Note that this page does not cover searching for signals; for that, please refer to the viewer documentation.

Searches can yield one or multiple positions. You can search in both forward and reverse directions, with an option to wrap around when reaching the end of the data.

To start a search, select one or multiple sequences in the viewer and press `Ctrl + F`. This opens the search dialog, which is organized into the following sections:

### Search Criteria 
At the top of the dialog, you can define the search criteria by:
   - Selecting a **Search Engine** (e.g., "Expression Search").
   - Choosing a **Configuration** or using the search properties below.
   - Specifying **Properties** for the selected search engine, such as the search expression.
   - Using the **Wrap Search** checkbox to enable or disable wrapping. When enabled, the search will continue from the beginning of the data once the end is reached, or vice versa for reverse searches.

### Signals Table
Below the search criteria, a table lists the signals selected before opening the dialog. This table includes:
   - **Name**: The name of each signal.
   - **Location**: The hierarchical location of each signal in the data.

### Search Results
At the bottom of the dialog, the results of the search are displayed after execution. This includes:
   - **Search**: The search term or criteria used.
   - **Position**: The position(s) in the data where matches were found.

### Action Buttons
At the very bottom, the dialog provides buttons to control the search process:
   - **Find Previous**: Navigate to the previous match.
   - **Find All**: Display all matches.
   - **Clear Search Results**: Remove the current search results.
   - **Find Next**: Navigate to the next match.






