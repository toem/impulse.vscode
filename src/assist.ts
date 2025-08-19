import * as path from 'path';
import * as querystring from 'querystring';
import { TextDecoder, TextEncoder } from 'util';
import * as vscode from 'vscode';
import { ideClient } from './extension';
import { ElementFS } from './elementFs';

export class Assist {

    public static ENDPOINT_ID = -16;
    static registerFor(context: vscode.ExtensionContext, language: string, isvalid: (uri: vscode.Uri) => boolean) {
        const collection = vscode.languages.createDiagnosticCollection(language);
        var timeout: any;
        if (vscode.window.activeTextEditor) {
            Assist.updateDiagnostics(vscode.window.activeTextEditor.document, language, collection);
        }

        context.subscriptions.push(vscode.languages.registerCompletionItemProvider(language, {
            provideCompletionItems(document: vscode.TextDocument, position: vscode.Position, token: vscode.CancellationToken):
                Thenable<vscode.CompletionItem[]> {
                if (isvalid(document.uri))
                    return new Promise<vscode.CompletionItem[]>((resolve, reject) => {

                        if (ideClient)
                            ideClient.request({ id: Assist.ENDPOINT_ID, op: "Completions", s1: ElementFS.mapFromFsUri(document.uri), i2: document.offsetAt(position), s3: document.getText(),s5:document.uri,s6:language }, (response) => {

                                if (response.x1) {

                                    var result: vscode.CompletionItem[] = [];

                                    for (var i in response.x1) {
                                        var x = response.x1[i];
                                        var p = new vscode.CompletionItem(x.la);
                                        if (x.de)
                                            p.documentation = Assist.createMarkdownString(x.de);
                                        switch (x.ki) {
                                            case "Field": p.kind = vscode.CompletionItemKind.Field; break;
                                            case "Method": p.kind = vscode.CompletionItemKind.Method; break;
                                            case "Class": p.kind = vscode.CompletionItemKind.Class; break;
                                            case "Enumeration": p.kind = vscode.CompletionItemKind.Enum; break;
                                        }
                                        p.insertText = x.te;
                                        //p.sortText = 'A' + x.la;
                                        //p.preselect = true;
                                        result.push(p)
                                    }
                                    resolve(result);
                                }
                                else reject();

                            });
                        else reject();

                    });
                return Promise.reject();
            }
        }, '.'));

        // Add definition provider (for F12 navigation)
        context.subscriptions.push(vscode.languages.registerDefinitionProvider(language, {
            provideDefinition(document: vscode.TextDocument, position: vscode.Position, token: vscode.CancellationToken): 
                Thenable<vscode.Location | vscode.Location[] | vscode.LocationLink[]> {
                if (isvalid(document.uri))
                    return new Promise<vscode.Location | vscode.Location[] | vscode.LocationLink[]>((resolve, reject) => {
                        if (ideClient) {
                            ideClient.request({
                                id: Assist.ENDPOINT_ID, 
                                op: "Definition", 
                                s1: ElementFS.mapFromFsUri(document.uri), 
                                i2: document.offsetAt(position), 
                                s3: document.getText(),
                                s5:document.uri,
                                s6:language 
                            }, (response) => {
                                if (response.i1 !== undefined) {
                                    // Since we only support definitions in the current document,
                                    // we'll use the current document URI and the returned position
                                    const position = response.i1;
                                    const pos = document.positionAt(position);
                                    resolve(new vscode.Location(document.uri, pos));
                                } else {
                                     reject();
                                }
                            });
                        } else {
                            reject();
                        }
                    });
                return Promise.reject();
            }
        }));

        // Add references provider (for finding usages with Shift+F12)
        context.subscriptions.push(vscode.languages.registerReferenceProvider(language, {
            provideReferences(document: vscode.TextDocument, position: vscode.Position, context: vscode.ReferenceContext, 
                token: vscode.CancellationToken): Thenable<vscode.Location[]> {
                if (isvalid(document.uri))
                    return new Promise<vscode.Location[]>((resolve, reject) => {
                        if (ideClient) {
                            ideClient.request({
                                id: Assist.ENDPOINT_ID,
                                op: "References",
                                s1: ElementFS.mapFromFsUri(document.uri),
                                i2: document.offsetAt(position),
                                s3: document.getText(),
                                b1: context.includeDeclaration, // Whether to include the declaration in results
                                s5:document.uri,
                                s6:language 
                            }, (response) => {
                                if (response.x1) {
                                    // x1 should be an array of positions
                                    const locations: vscode.Location[] = [];
                                    
                                    // Convert each position to a Location object
                                    for (const pos of response.x1) {
                                        const position = document.positionAt( pos.offset);
                                        locations.push(new vscode.Location(document.uri, position));
                                    }
                                    
                                    resolve(locations);
                                } else {
                                    // If no references found, return empty array
                                    reject();
                                }
                            });
                        } else {
                            reject();
                        }
                    });
                return Promise.reject();
            }
        }));

        // Add hover provider
        context.subscriptions.push(vscode.languages.registerHoverProvider(language, {
            provideHover(document: vscode.TextDocument, position: vscode.Position, token: vscode.CancellationToken):
                Thenable<vscode.Hover> {
                if (isvalid(document.uri))
                    return new Promise<vscode.Hover>((resolve, reject) => {

                        ideClient.request({ id: Assist.ENDPOINT_ID, op: "Hover", s1: ElementFS.mapFromFsUri(document.uri), i2: document.offsetAt(position), s3: document.getText(), s5: document.uri, s6: language }, (response) => {

                            if (response.s1) {
                                let md = Assist.createMarkdownString(response.s1);
                                resolve(new vscode.Hover(md));
                            }
                            else reject();
                        });


                    });
                return Promise.reject();
            }
        }));

        // Add document formatting provider
        context.subscriptions.push(vscode.languages.registerDocumentFormattingEditProvider(language, {
            provideDocumentFormattingEdits(document: vscode.TextDocument): Thenable<vscode.TextEdit[]> {
                if (isvalid(document.uri)) {
                    return new Promise<vscode.TextEdit[]>((resolve, reject) => {
                        if (ideClient) {
                            ideClient.request({
                                id: Assist.ENDPOINT_ID,
                                op: "Format",
                                s1: ElementFS.mapFromFsUri(document.uri),
                                s3: document.getText(),
                                s5: document.uri,
                                s6: language
                            }, (response) => {
                                if (response.s1) {
                                    // Create a text edit that replaces the entire document
                                    const fullRange = new vscode.Range(
                                        new vscode.Position(0, 0),
                                        document.lineAt(document.lineCount - 1).range.end
                                    );
                                    resolve([vscode.TextEdit.replace(fullRange, response.s1)]);
                                } else {
                                    // If no formatting changes, return empty array
                                    resolve([]);
                                }
                            });
                        } else {
                            reject();
                        }
                    });
                }
                return Promise.reject();
            }
        }));

        // Register code action provider for organize imports
        context.subscriptions.push(vscode.languages.registerCodeActionsProvider(language, {
            provideCodeActions(document: vscode.TextDocument, range: vscode.Range, context: vscode.CodeActionContext, token: vscode.CancellationToken): vscode.ProviderResult<vscode.CodeAction[]> {
                // Create a code action for organize imports
                const organizeImportsAction = new vscode.CodeAction('Organize Imports', vscode.CodeActionKind.SourceOrganizeImports);
                
                // Set the command that will be executed when this code action is selected
                organizeImportsAction.command = {
                    title: 'Organize Imports',
                    command: `${language}.organizeImports`,
                    // Don't pass the editor directly - it will be retrieved when the command runs
                    arguments: []
                };
                
                return [organizeImportsAction];
            }
        }, {
            providedCodeActionKinds: [vscode.CodeActionKind.SourceOrganizeImports]
        }));

        // Register the organize imports command
        context.subscriptions.push(vscode.commands.registerCommand(`${language}.organizeImports`, () => {
            const editor = vscode.window.activeTextEditor;
            if (editor && isvalid(editor.document.uri)) {
                // Show progress indicator
                vscode.window.withProgress({
                    location: vscode.ProgressLocation.Notification,
                    title: "Organizing imports...",
                    cancellable: false
                }, async (progress) => {
                    return new Promise<void>((resolve) => {
                        ideClient.request({
                            id: Assist.ENDPOINT_ID,
                            op: "OrganizeImports",
                            s1: ElementFS.mapFromFsUri(editor.document.uri),
                            s3: editor.document.getText(),
                            i1: 0x03 ,// Combine options: remove unused (0x01) + add missing (0x02)
                            s5: editor.document.uri,
                            s6: language
                        }, (response) => {
                            if (response.s1) {
                                // Replace the entire document with the organized imports version
                                editor.edit(editBuilder => {
                                    const fullRange = new vscode.Range(
                                        new vscode.Position(0, 0),
                                        editor.document.lineAt(editor.document.lineCount - 1).range.end
                                    );
                                    editBuilder.replace(fullRange, response.s1);
                                }).then(() => resolve());
                            } else {
                                resolve();
                            }
                        });
                    });
                });
            }
        }));

        context.subscriptions.push(vscode.window.onDidChangeActiveTextEditor(editor => {
            if (editor && editor.document && isvalid(editor.document.uri)) {
                Assist.updateDiagnostics(editor.document, language, collection);
            }
        }));

        context.subscriptions.push(
            vscode.workspace.onDidChangeTextDocument(editor => {
                if (editor && editor.document && isvalid(editor.document.uri)) {
                    if (timeout)
                        clearTimeout(timeout);
                    timeout = setTimeout(() => { timeout = undefined; Assist.updateDiagnostics(editor.document, language, collection); }, 2000);
                }
            }));

    }


    static updateDiagnostics(document: vscode.TextDocument, language: string, collection: vscode.DiagnosticCollection): void {

        if (ideClient)
            ideClient.request({ id: Assist.ENDPOINT_ID, op: "Diagnostics", s1: ElementFS.mapFromFsUri(document.uri), s3: document.getText() , s5: document.uri, s6: language }, (response) => {

                if (response.x1) {

                    var result: vscode.Diagnostic[] = [];

                    for (var i in response.x1) {
                        var x = response.x1[i];
                        result.push({
                            message: x.me,
                            range: new vscode.Range(new vscode.Position(x.li, x.cm), new vscode.Position(x.li, x.cm + x.ln)),
                            severity: x.se == 4 ? vscode.DiagnosticSeverity.Error : x.se == 2 ? vscode.DiagnosticSeverity.Warning : vscode.DiagnosticSeverity.Information,

                        });
                    }
                    collection.set(document.uri, result);
                }
                else collection.set(document.uri, []);

            });

    }


    static createMarkdownString(text: string): vscode.MarkdownString {

        //<a href='http://docs.oracle.com/javase/8/docs/api/java/io/BufferedReader.html#read-char:A-int-int-'>java.io.BufferedReader</a>
        //[GitHub Pages](https://pages.github.com/)

        const regex = /<a href='([^']*)'>([^<]*)<\/a>/g;
        let result;
        while (result = regex.exec(text)) {
            const commandUri = vscode.Uri.parse('command:simpleBrowser.show?' + encodeURIComponent(JSON.stringify(result[1])));
            text = text.replace(result[0], '[' + result[2] + '](' + commandUri.toString() + ')');
        }
        let md = new vscode.MarkdownString(text, true);
        md.supportHtml = true;
        md.isTrusted = true;
        return md;
    }
}
