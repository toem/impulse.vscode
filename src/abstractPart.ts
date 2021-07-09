import * as path from 'path';
import * as vscode from 'vscode';
import { getNonce } from './util';
import { onMessage } from './extension';
import { host } from './extension';
import { port } from './extension';

export class AbstractPartProvider {

    constructor(
		protected readonly _context: vscode.ExtensionContext,
		protected  readonly id : string
	) { }

	//#endregion

	/**
	 * Get the static HTML used for in our editor's webviews.
	 */
     protected getHtmlForWebview(webview: vscode.Webview, uri: vscode.Uri): string {
		
        //const ext = '_dev';
        const ext = '';

        
		// Local path to script and css for the webview
		const configUri = webview.asWebviewUri(vscode.Uri.file(
			path.join(this._context.extensionPath, 'media', 'impulse'+ext+'_config.js')
		)); 
		const scriptUri = webview.asWebviewUri(vscode.Uri.file(
			path.join(this._context.extensionPath, 'media', 'impulse'+ext+'.js')
		)); 
		const styleResetUri = webview.asWebviewUri(vscode.Uri.file(
			path.join(this._context.extensionPath, 'media', 'reset.css')
		));
		const styleVSCodeUri = webview.asWebviewUri(vscode.Uri.file(
			path.join(this._context.extensionPath, 'media', 'vscode.css')
		));
		const styleMainUri = webview.asWebviewUri(vscode.Uri.file(
			path.join(this._context.extensionPath, 'media', 'impulse.css')
		));

		// Use a nonce to whitelist which scripts can be run
		const nonce = getNonce();

		return /* html */`
			<!DOCTYPE html>
			<html lang="en">
			<head>
				<meta charset="UTF-8">

				<!--
				Use a content security policy to only allow loading images from https or from our extension directory,
				and only allow scripts that have a specific nonce.
				-->
				<meta http-equiv="Content-Security-Policy" content="default-src 'self' ; img-src 'self' data:; style-src ${webview.cspSource};font-src ${webview.cspSource}; script-src 'nonce-${nonce}';connect-src ws:;">

				<meta name="viewport" content="width=device-width, initial-scale=1.0">

				<link href="${styleResetUri}" rel="stylesheet" />
				<link href="${styleVSCodeUri}" rel="stylesheet" />
				<link href="${styleMainUri}" rel="stylesheet" />

				<title>Paw Draw</title>
			</head>
			<body>
				<script nonce="${nonce}">
				    var impulseHost = "${host}";
					var impulsePort = "${port}";
					var idePartRequest = "${this.id}";
					var ideUriRequest = "${uri}";	
					const vscode = acquireVsCodeApi();
					var sendMessageToIde = vscode.postMessage;	
					var receiveMessagesFromIde = function(listener){
						window.addEventListener("message",listener);
					};
				</script> 
			    <script nonce="${nonce}" src="${configUri}"></script>				
				<script nonce="${nonce}" src="${scriptUri}"></script>
			</body>
			</html>`;
	}

	protected onMessage(sender :any, message: any) {
		
		onMessage(sender,message);
		switch (message.id) {
			/*
			case -11:{
				switch (message.op) {
					case "Information":{
		
						vscode.window.showInformationMessage(message.s0 + ": "+message.s1);
		
		
						
					}
					case "YesNoQuestion":{
		
						vscode.window.showInformationMessage(message.s0 + ": "+message.s1,{modal:true}, "Yes","No").then((ret)=>{
							webview.postMessage({id:message.id,op:message.op,i0:message.i2,s1:ret});
						});
					
		
						
					}
				}
			}
*/
		}
	}

}