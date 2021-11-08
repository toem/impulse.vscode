import * as path from 'path';
import * as querystring from 'querystring';
import { TextDecoder, TextEncoder } from 'util';
import * as vscode from 'vscode';
import { ideClient } from './extension';


export class Scripting {

    static register(context: vscode.ExtensionContext, scheme: string) {
        context.subscriptions.push(vscode.languages.registerCompletionItemProvider('javascript', {
            provideCompletionItems(
                document: vscode.TextDocument, position: vscode.Position, token: vscode.CancellationToken):
                Thenable<vscode.CompletionItem[]> {
                    if (document.uri.scheme == scheme) 
                return new Promise<vscode.CompletionItem[]>((resolve, reject) => {

                   ideClient.request({ id: 0, op: "Complete",  s1: document.getText(),i2:position.character }, (response) => {
                        if (response.t1){
                            
                        }
                        resolve([new vscode.CompletionItem('huhu*')]);
                   });
                       
                });
                return Promise.reject();
            }
        }));
    }
}
