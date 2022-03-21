import * as path from 'path';
import * as querystring from 'querystring';
import { TextDecoder, TextEncoder } from 'util';
import * as vscode from 'vscode';
import { ideClient } from './extension';
import { ElementFS } from './elementFs';

export class Scripting {

    public static ENDPOINT_ID = -16;

    static register(context: vscode.ExtensionContext, scheme: string) {

        context.subscriptions.push(vscode.languages.registerCompletionItemProvider('javascript', {
            provideCompletionItems(document: vscode.TextDocument, position: vscode.Position, token: vscode.CancellationToken):
                Thenable<vscode.CompletionItem[]> {
                if (document.uri.scheme == scheme)
                    return new Promise<vscode.CompletionItem[]>((resolve, reject) => {


                        ideClient.request({ id: Scripting.ENDPOINT_ID, op: "Complete", s1: ElementFS.uri2link(document.uri), i2: document.offsetAt(position), s3: document.getText() }, (response) => {

                            if (response.x1) {

                                var result: vscode.CompletionItem[] = [];

                                for (var i in response.x1) {
                                    var x = response.x1[i];
                                    var p = new vscode.CompletionItem(x.la);
                                    if (x.de) 
                                        p.documentation = Scripting.createMarkdownString(x.de);
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


                    });
                return Promise.reject();
            }
        },'.'));


        context.subscriptions.push(vscode.languages.registerHoverProvider('javascript', {
            provideHover(document: vscode.TextDocument, position: vscode.Position, token: vscode.CancellationToken):
                Thenable<vscode.Hover> {
                if (document.uri.scheme == scheme)
                    return new Promise<vscode.Hover>((resolve, reject) => {

                        ideClient.request({ id: Scripting.ENDPOINT_ID, op: "Info", s1: ElementFS.uri2link(document.uri), i2: document.offsetAt(position), s3: document.getText() }, (response) => {

                            if (response.s1) {
                                let md  = Scripting.createMarkdownString(response.s1);
                                resolve(new vscode.Hover(md));
                            }
                            else reject();
                        });


                    });
                return Promise.reject();
            }
        }));
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
