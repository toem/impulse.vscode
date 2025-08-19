import * as vscode from 'vscode';
import * as fs from 'fs';
import * as path from 'path';
import { ideClient } from './extension';
import { AbstractPart, Endpoint } from './abstractPart';


// ========================================================================================================================
// Part Tools
// ========================================================================================================================

export class LmPartTool
    implements vscode.LanguageModelTool<any> {
    extensionName: string;


    public static register(context: vscode.ExtensionContext, id: string, name: string, part: string): vscode.Disposable {
        return vscode.lm.registerTool(
            id,
            new LmPartTool(context, id, name, part));
    }

    constructor(
        protected readonly context: vscode.ExtensionContext,
        public readonly id: string,
        public readonly name: string,
        public readonly part: string
    ) {
        this.context = context;
        this.id = id;
        this.name = name;
        this.part = part;
        this.extensionName  = context.extension.packageJSON.name;
    }


    async invoke(
        options: vscode.LanguageModelToolInvocationOptions<any>,
        _token: vscode.CancellationToken
    ) {
        const params = options.input;
        return this.handleTool(params);
    }

    /*
    async prepareInvocation(
        options: vscode.LanguageModelToolInvocationPrepareOptions<any>,
        _token: vscode.CancellationToken
    ) {
        const confirmationMessages = {
            title: 'Count the number of open tabs',
            message: new vscode.MarkdownString(
                `Count the number of open tabs?` +
                (options.input.tabGroup !== undefined
                    ? ` in tab group ${options.input.tabGroup}`
                    : '')
            ),
        };

        return {
            invocationMessage: 'Counting the number of tabs',
            confirmationMessages,
        };
    }
*/

    private async handleTool(params: any): Promise<vscode.LanguageModelToolResult> {

        var result = "";
        var partId = this.part;
        if (params.pindex)
            partId += params.pindex;
        else if (partId.endsWith("-"))
            partId += 1;
        const part = partId != "ideClient" ? AbstractPart.get(partId) : ideClient;
        const promise = part?.promiseRequest({ id: 0, op: "Ask", s1: this.name, x2: params }, 5000).then(r => {
            result = r.s1;
        });

        try {
            await promise;
        } catch (e) {
            return new vscode.LanguageModelToolResult([new vscode.LanguageModelTextPart((e as Error).message)]);
        }
        return new vscode.LanguageModelToolResult([new vscode.LanguageModelTextPart(result)]);
    }
}


// ========================================================================================================================
// Doc Tool
// ========================================================================================================================

export class LmDocTool
    implements vscode.LanguageModelTool<any> {
        
    allMetadata: string;
    docIdToMetadata: Map<number, any>;
    docIdToContent: Map<number, string>;
    extensionName: string;

    public static register(context: vscode.ExtensionContext, id: string, name: string, docFolder: string): vscode.Disposable {
        return vscode.lm.registerTool(
            id,
            new LmDocTool(context, id, name, docFolder));
    }

    constructor(
        protected readonly context: vscode.ExtensionContext,
        public readonly id: string,
        public readonly name: string,
        public readonly docFolder: string
    ) {
        this.context = context;
        this.id = id;
        this.name = name;
        this.docFolder = docFolder;
        this.docIdToMetadata = new Map<number, any>();
        this.docIdToContent = new Map<number, string>();
        this.allMetadata = this.createIndex();
        this.extensionName  = context.extension.packageJSON.name;
    }

    createIndex(): string {
        
        let index = '';
        const fullDocFolder = path.join(this.context.extensionPath, this.docFolder);
        
        // Function to scan for markdown and Java files in a directory and its subdirectories
        const scanFiles = (docFolder: string): string[] => {
            const files: string[] = [];
            try {
                const entries = fs.readdirSync(docFolder, { withFileTypes: true });

                for (const entry of entries) {
                    const fullPath = path.join(docFolder, entry.name);
                    if (entry.isDirectory()) {
                        files.push(...scanFiles(fullPath));
                    } else if (entry.isFile() && (entry.name.endsWith('.md') || entry.name.endsWith('.java'))) {
                        files.push(fullPath);
                    }
                }
            } catch (error) {
            }
            return files;
        };

        // Scan for all markdown and Java files
        const allFiles = scanFiles(fullDocFolder);

        // Process each markdown file to extract metadata
        for (const filePath of allFiles) {
            try {
                const content = fs.readFileSync(filePath, 'utf8');

                // Process based on file type
                if (filePath.endsWith('.md')) {
                    // Extract YAML metadata block enclosed in horizontal lines (---)
                    const metadataMatch = content.match(/^---\r?\n([\s\S]*?)\r?\n---/);
                    
                    if (metadataMatch) {
                        const metadataBlock = metadataMatch[0]; // Complete block including the --- delimiters
                        
                        // Only extract docID with regex
                        const docIdMatch = metadataBlock.match(/docID:\s*(\d+)/);
                        const docId = docIdMatch ? parseInt(docIdMatch[1]) : 0;
                        
                        // Calculate word count for size metadata
                        const contentWithoutMetadata = content.replace(metadataBlock, '').trim();
                        const wordCount = contentWithoutMetadata.split(/\s+/).length;
                        
                        // Calculate relative path for storing in maps
                        const relativePath = path.relative(fullDocFolder, filePath).replace(/\\/g, '/');
                        
                        // Store in docID maps if docId exists
                        if (docId) {
                            // Store content without metadata
                            this.docIdToMetadata.set(docId, { path: relativePath });
                            this.docIdToContent.set(docId, contentWithoutMetadata);
                        }
                        
                        // Add the complete metadata block and size to the index
                        index += metadataBlock.replace(/---\s*$/, `size: ${wordCount}\n---`) + '\n\n';
                    }
                } else if (filePath.endsWith('.java')) {
                    // Process Java file
                    // Extract class/interface name
                    const classMatch = content.match(/public (class|interface|enum) ([A-Za-z0-9_]+)/);
                    
                    if (classMatch) {
                        const className = classMatch[2].trim();
                        const type = classMatch[1].trim(); // class, interface, or enum
                        
                        // Extract base class and implemented interfaces for keywords
                        let superClasses = [];
                        const extendsMatch = content.match(/extends ([A-Za-z0-9_,<>\s]+)(\{|implements)/);
                        if (extendsMatch) {
                            superClasses.push(extendsMatch[1].trim());
                        }
                        
                        const implementsMatch = content.match(/implements ([A-Za-z0-9_,<>\s]+)\{/);
                        if (implementsMatch) {
                            superClasses.push(implementsMatch[1].trim());
                        }
                        
                        // Extract method names for keywords
                        const methodNames = [];
                        if (type === 'interface') {
                            // For interfaces, match methods without implementation
                            const interfaceMethodMatches = content.matchAll(/\s+(?:\w+\s+)*\s*(\w+)\s*\([^)]*\)\s*;/g);
                            const interfaceMethods = Array.from(interfaceMethodMatches, m => m[1]);
                            methodNames.push(...interfaceMethods);
                        } else {
                            // For classes and enums, match methods with implementation blocks
                            const classMethodMatches = content.matchAll(/public\s+(?!class|interface|enum)[\w<>\[\],\s]+\s+(\w+)\s*\(/g);
                            const classMethods = Array.from(classMethodMatches, m => m[1]);
                            methodNames.push(...classMethods);
                        }
                        
                        // Remove duplicate method names (for overloaded methods)
                        const uniqueMethodNames = [...new Set(methodNames)];
                        
                        // Combine superclasses and method names for keywords
                        const keywords = [...superClasses, ...uniqueMethodNames].join(', ');
                        
                        // Extract javadoc description (first two paragraphs)
                        let description = '';
                        let docId = 0;
                        const javadocMatch = content.match(/\/\*\*([\s\S]*?)\*\//);
                        if (javadocMatch) {
                            const javadoc = javadocMatch[1];
                            
                            // Look for docID in javadoc
                            const docIdMatch = javadoc.match(/\s*\*\s*docID:\s*(\d+)/);
                            if (docIdMatch) {
                                docId = parseInt(docIdMatch[1], 10);
                            }
                            
                            // Split into paragraphs (separated by empty lines) and take first two
                            const paragraphs = javadoc
                                .split(/\n\s*\*\s*\n/)
                                .map(para => para.split('\n')
                                    .map(line => line.trim().replace(/^\s*\*\s*/, '').trim())
                                    .join(' ')
                                    .trim())
                                .filter(para => para.length > 0 && !para.includes('docID:')) // Filter out the docID line
                                .slice(0, 2);
                            
                            // Join paragraphs with a space instead of newlines
                            description = paragraphs.join(' ');
                        }
                        
                        // Calculate word count for size metadata
                        const wordCount = content.split(/\s+/).length;
                        
                        // Create tags array
                        const tags = ["api"];
                        
                        // Check if file is in an interface folder
                        if (filePath.includes("/interface/") || filePath.includes("\\interface\\")) {
                            tags.push("interface");
                        }
                        
                        // Check if file is in examples folder
                        if (filePath.includes("/examples/") || filePath.includes("\\examples\\")) {
                            tags.push("examples");
                            
                            // Get the subfolder name in examples
                            const examplesPathMatch = filePath.match(/examples[/\\]([^/\\]+)/);
                            if (examplesPathMatch && examplesPathMatch[1]) {
                                tags.push(examplesPathMatch[1]);
                            }
                        }
                        
                        // Create metadata block in YAML format
                        const metadataBlock = `---
title: "${className}"
keywords: [${keywords}]
description: "${description.replace(/"/g, '\\"')}"
category: ${this.extensionName}+"-api"
${docId > 0 ? `docID: ${docId}` : ''}
size: ${wordCount}
tags:
${tags.map(tag => `  - ${tag}`).join('\n')}
---`;
                        
                        // Calculate relative path from documentation path
                        const relativePath = path.relative(fullDocFolder, filePath).replace(/\\/g, '/');
                        
                        // Store in docID maps if docId exists
                        if (docId > 0) {
                            // Store content without metadata (which is the whole Java file in this case)
                            this.docIdToMetadata.set(docId, { path: relativePath });
                            this.docIdToContent.set(docId, content);
                        }
                        
                        // Add the metadata block to the index
                        index += metadataBlock + '\n\n';
                    }
                }
            } catch (error) {
                console.error(`Error processing file ${filePath}:`, error);
            }
        }
        return index;
    }

    async invoke(
        options: vscode.LanguageModelToolInvocationOptions<any>,
        _token: vscode.CancellationToken
    ) {
        const params = options.input;
        return this.handleTool(params);
    }

    /*
    async prepareInvocation(
        options: vscode.LanguageModelToolInvocationPrepareOptions<any>,
        _token: vscode.CancellationToken
    ) {

        const confirmationMessages = {
            title: '',
            message: new vscode.MarkdownString(
                `Do manual search ?`
            ),
        };

        return {
             '',
            confirmationMessages,
        };
    }
*/


    private async handleTool(params: any): Promise<vscode.LanguageModelToolResult> {
        const fullDocFolder = path.join(this.context.extensionPath, this.docFolder);

        // Case 1: Direct docID access
        if (params.docIds) {
            const docIds = Array.isArray(params.docIds) 
                ? params.docIds 
                : params.docIds.toString().split(',').map((id: string) => parseInt(id.trim()));
            
            const resultParts: vscode.LanguageModelTextPart[] = [];
            const notFoundIds: number[] = [];
            
            // Collect content for each requested docId
            for (const docId of docIds) {
                if (this.docIdToContent.has(docId)) {
                    const content = this.docIdToContent.get(docId);
                    if (content)
                    resultParts.push(new vscode.LanguageModelTextPart(content));
                } else {
                    notFoundIds.push(docId);
                }
            }
            
            // Return content if found
            if (resultParts.length > 0) {
                return new vscode.LanguageModelToolResult(resultParts);
            }
            
            // Return error if docIds were specified but none found
            if (notFoundIds.length > 0) {
                return new vscode.LanguageModelToolResult([
                    new vscode.LanguageModelTextPart(`Error: Content not found for docID(s): ${notFoundIds.join(', ')}`)
                ]);
            }
        }

        // Case 2: Query-based search
        if (params.query) {
            const messages = [
                vscode.LanguageModelChatMessage.User(`You shall find relevant documentation content to answer a given user query. This content is then (in a 2nd step) used to answer the query.`),
                vscode.LanguageModelChatMessage.User('Here now the documentation metadata index as md yaml metadata :\n'+this.allMetadata+"\n"),
                vscode.LanguageModelChatMessage.User(`
When analyzing the metadata blocks, pay attention to these key fields:
- title: The main topic name of the document
- author: Creator of the content
- keywords: Important terms that indicate document topics and relevance
- description: Brief summary of the document's contents and purpose
- category: The main section this document belongs to (e.g., "impulse-api")
- tags: Additional categorization labels
- docID: Unique identifier for the document
- size: Word/token count, use this to balance your selections
- recommended: Related document IDs that might also be relevant

For example, when you see:
\`\`\`
---
title: "Implementing Readers"
author: "Thomas Haber"
keywords: [reader implementation, InputStream, record creation, parsing, signal writing]
description: "Comprehensive guide to implementing custom readers in the impulse framework..."
category: "impulse-api"
tags:
  - api
  - development
docID: 1005
recommended: 301,303,305,307
---
\`\`\`

This indicates a document about implementing readers in the impulse framework with docID 1005, which also recommends checking documents 301, 303, 305, and 307.
                `),
                vscode.LanguageModelChatMessage.User('Here now the user request query : "'+params.query+'"'),
                vscode.LanguageModelChatMessage.User('ONLY return the docIDs of the selected content as a comma separated list. If nothing is found, return "NONE" ')
            ];

            try {
                const [model] = await vscode.lm.selectChatModels({ vendor: 'copilot', family: 'gpt-4o' });
                const chatResponse = await model.sendRequest(messages, {}, params.token);
                let selection = '';
                for await (const fragment of chatResponse.text) {
                    selection += fragment;
                }

                // Process selected docIDs
                if (selection.trim() === "NONE") {
                    return new vscode.LanguageModelToolResult([
                        new vscode.LanguageModelTextPart("I couldn't find any relevant documentation to answer your question.")
                    ]);
                }

                const docIds = selection.split(',').map(id => parseInt(id.trim()));
                const resultParts: vscode.LanguageModelTextPart[] = [];
                const notFoundIds: number[] = [];
                
                // Collect content for each selected docId
                for (const docId of docIds) {
                    if (this.docIdToContent.has(docId)) {
                        const content = this.docIdToContent.get(docId);
                        if (content)
                            resultParts.push(new vscode.LanguageModelTextPart(content));
                    } else {
                        notFoundIds.push(docId);
                    }
                }
                
                // Return content if found
                if (resultParts.length > 0) {
                    return new vscode.LanguageModelToolResult(resultParts);
                }
                
                // Return error if selected docIds were not found
                if (notFoundIds.length > 0) {
                    return new vscode.LanguageModelToolResult([
                        new vscode.LanguageModelTextPart(`Error: Content not found for docID(s): ${notFoundIds.join(', ')}`)
                    ]);
                }
            } catch (err) {
                console.error(`Error: ${err}`);
                return new vscode.LanguageModelToolResult([
                    new vscode.LanguageModelTextPart(`Error processing documentation request: ${err}`)
                ]);
            }
        }

        // Case 3: No query or docIds - return the metadata index
        return new vscode.LanguageModelToolResult([
            new vscode.LanguageModelTextPart(this.allMetadata)
        ]);
    }

}

// ========================================================================================================================
// Chat Tool
// ========================================================================================================================

/*
        "chatParticipants": [
            {
                "id": "chat_impulse",
                "fullName": "impulse",
                "name": "impulse",
                "description": "Read impulse manual and API documentation",
                "isSticky": true,
                "commands": [
                    {
                        "name": "fetch",
                        "description": "Fetch a specific content from documentation"
                    }
                ],
                "disambiguation": [
                    {
                        "category": "impulse",
                        "description": "The user wants to learn about impulse and its API",
                        "examples": [
                            "What is a signal in impulse",
                            "How do i implement a serializer"
                        ]
                    }
                ]
            }],
*/

interface IChatResult extends vscode.ChatResult {
    metadata: {
        command: string;
    }
}
export class LmChat {
    /** 
     * Flag to determine if this is a documentation tool based on its ID.
     * Currently coupled to the extension name pattern "ask_<extension-name>".
     * For extension-independent use, this detection should be made more generic.
     */
    isDocumentationTool: boolean;
    participant: vscode.ChatParticipant;

    /**
 * Registers a new language model tool with VS Code.
 * 
 * @param context - The extension context
 * @param id - Unique identifier for the tool
 * @param name - Display name of the tool
 * @param part - Either a part ID to communicate with backend or a folder path for documentation tools
 * @returns A disposable that unregisters the tool when disposed
 */
    public static register(context: vscode.ExtensionContext, id: string, name: string, partOrFolder: string) {
        new LmChat(context, id, name, partOrFolder);
    }


    /**
     * Creates a new LmChat instance.
     * 
     * @param context - The extension context used to resolve paths and access extension information
     * @param id - Unique identifier for the tool
     * @param name - Display name of the tool
     * @param partOrFolder - Either a part ID for backend communication or a folder path for documentation tools
     */
    constructor(
        protected readonly context: vscode.ExtensionContext,
        public readonly id: string,
        public readonly name: string,
        public readonly partOrFolder: string
    ) {
        this.context = context;
        this.id = id;
        this.name = name;
        this.partOrFolder = partOrFolder;
        this.isDocumentationTool = id == "chat_" + context.extension.packageJSON.name;

        const handler: vscode.ChatRequestHandler = async (request: vscode.ChatRequest, context: vscode.ChatContext, stream: vscode.ChatResponseStream, token: vscode.CancellationToken): Promise<IChatResult> => {

            // Define the base path for documentation using the partOrFolder
            const documentationPath = path.join(this.context.extensionPath, this.partOrFolder);

            let index = '';

            // Get all subdirectories in the documentation path
            const directories = fs.readdirSync(documentationPath, { withFileTypes: true })
                .filter(dirent => dirent.isDirectory())
                .map(dirent => dirent.name);

            // Look for index.md files in each directory
            for (const dir of directories) {
                const indexPath = path.join(documentationPath, dir, 'index.md');
                if (fs.existsSync(indexPath)) {
                    index += fs.readFileSync(indexPath, 'utf8') + '\n\n';
                }
            }

            try {
                const messages = [
                    vscode.LanguageModelChatMessage.User(`This request contains a user request and an documentation index with titles, file paths, keywords, and descriptions for each content.  CAREFULLY analyze the keywords and descriptions to identify ALL relevant content for the given user request  and give each a rate from 0-10 (no reply). ONLY return the file pathes that have a rate > 5  (a maximum of 5) as a comma separated list. If nothing is found, return "NONE" `),
                    vscode.LanguageModelChatMessage.Assistant(index),
                    vscode.LanguageModelChatMessage.User(request.prompt)
                ];

                const chatResponse = await request.model.sendRequest(messages, {}, token);
                let selection = '';
                for await (const fragment of chatResponse.text) {
                    selection
                        += fragment;
                }
                // Check if any relevant files were found
                if (selection.trim() === "NONE") {
                    await stream.markdown("I couldn't find any relevant documentation to answer your question.");
                    return { metadata: { command: '' } };
                }

                await stream.markdown("Found relevant documentation. Retrieving content " + selection);

                // Process content parameter which may contain multiple comma-separated paths
                const contentPaths = selection.split(',').map((path: string) => path.trim());
                let concatenatedContent = '';
                let notFoundPaths: string[] = [];

                // Process each requested path
                for (const contentPath of contentPaths) {
                    const requestedFile = path.join(documentationPath, contentPath);
                    if (fs.existsSync(requestedFile)) {
                        // Read the file and add its content with a separator
                        const fileContent = fs.readFileSync(requestedFile, 'utf8');
                        concatenatedContent += fileContent + '\n\n';
                    } else {
                        notFoundPaths.push(contentPath);
                    }
                }
                //stream.push(new vscode.ChatResponseMarkdownPart(concatenatedContent));

                const messages2 = [
                    vscode.LanguageModelChatMessage.User(`This request contains a user request and documentation to answer.  CAREFULLY read the content and the request and return an answer.`),
                    vscode.LanguageModelChatMessage.Assistant(concatenatedContent),
                    vscode.LanguageModelChatMessage.User(request.prompt)
                ];

                const chatResponse2 = await request.model.sendRequest(messages2, {}, token);
                for await (const fragment of chatResponse2.text) {
                    stream.markdown(fragment);
                }


            } catch (err) {
                console.error(`Error: ${err}`);
            }

            return { metadata: { command: '' } };
        }
        this.participant = vscode.chat.createChatParticipant(id, handler);


    }

}



