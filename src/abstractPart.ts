import * as path from 'path';
import * as vscode from 'vscode';
import * as WebSocket from 'ws';
import { getNonce } from './util';
import { onMessage } from './extension';
import { impulsePartsUri } from './extension';

export class AbstractPartProvider {

	constructor(
		protected readonly _context: vscode.ExtensionContext,
		protected readonly id: string
	) { }

	//#endregion

	/**
	 * Get the static HTML used for in our editor's webviews.
	 */
	protected getHtmlForWebview(webview: vscode.Webview, uri: vscode.Uri): string {

		const ext = '_dev';
		//const ext = '';


		// Local path to script and css for the webview
		const configUri = webview.asWebviewUri(vscode.Uri.file(
			path.join(this._context.extensionPath, 'media', 'impulse' + ext + '_config.js')
		));
		const scriptUri = webview.asWebviewUri(vscode.Uri.file(
			path.join(this._context.extensionPath, 'media', 'impulse' + ext + '.js')
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
		const chartUri = webview.asWebviewUri(vscode.Uri.file(
			path.join(this._context.extensionPath, 'media', 'chart.js')
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
				<meta http-equiv="Content-Security-Policy" content="default-src 'self' ; img-src 'self' data:; style-src ${webview.cspSource};font-src ${webview.cspSource}; script-src 'nonce-${nonce}' 'unsafe-eval';connect-src ws:;">

				<meta name="viewport" content="width=device-width, initial-scale=1.0">

				<link href="${styleResetUri}" rel="stylesheet" />
				<link href="${styleVSCodeUri}" rel="stylesheet" />
				<link href="${styleMainUri}" rel="stylesheet" />

				<title>Paw Draw</title>
			</head>
			<body>
				<script nonce="${nonce}">
					const vscode = acquireVsCodeApi();
					var sendMessageToContainer = vscode.postMessage;	
					var receiveMessagesFromContainer = function(listener){
						window.addEventListener("message",listener);
					};
				</script> 
			    <script nonce="${nonce}" src="${configUri}"></script>				
				<script nonce="${nonce}" src="${scriptUri}"></script>
				<!--<script nonce="${nonce}" src="${chartUri}"></script> -->
			</body>
			</html>`;
	}

	protected onMessage(sender: any, message: any) {

		onMessage(sender, message);
	}

	protected static connect(webview: vscode.Webview,  listener: ((client:WebSocket) => void)) {

		// json connection
		var client = new WebSocket(impulsePartsUri.toString());
		var established = false;
		client.on('open', () => {
			established = true;
			console.log('Part connection established with the server.');
			setInterval(() => { client.send("ping") }, 1000);
			webview.onDidReceiveMessage(message => {
				AbstractPartProvider.send(client,message);
			})
			client.on('message', (message: string) => {
				const jsonArray = JSON.parse(message);
				for (let i = 0; i < jsonArray.length; i++) {
					var jsonObject = jsonArray[i];
					if (jsonObject.id == -11)
						onMessage({ postMessage: function (m: any) { AbstractPartProvider.send(client,m); } }, jsonObject);
				}
				webview.postMessage(jsonArray);
			});
			listener(client);
		});
		client.on('error', () => {
			if (!established) {
				console.log('Not found - trying again');
				this.connect(webview,listener);
			} else
				console.log('WebSocket error:');
		})
	}

	protected static send(client:WebSocket, message:any) {
		client.send("[" + JSON.stringify(message) + "]")
}

	protected static disconnect(client:WebSocket) {
			client.close;
	}
}