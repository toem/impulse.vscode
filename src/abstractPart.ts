import * as path from 'path';
import * as vscode from 'vscode';
import { getNonce } from './util';
import { ideClient } from './extension';
import { Connection } from './connection';

export class Endpoint {

	public constructor(
		protected readonly connection: Connection
	) {
	}

	public send(message: any) {
		if (this.connection)
			if (Array.isArray(message))
				this.connection.send(JSON.stringify(message));
			else
				this.connection.send("[" + JSON.stringify(message) + "]")
	}

	private nextRequestId = 1;
	private requests: Map<number, (response: any) => void> = new Map();

	public request(message: any, listener: ((response: any) => void) | null) {

		var requestId = 0;
		if (listener) {
			requestId = this.nextRequestId++;
			this.requests.set(requestId, listener);
			message.i0 = requestId;
		}
		this.send(message);
	}

	protected handle(message: any) {

		if (message.op == "#") {
			const listener = this.requests.get(message.i0);
			if (listener != null) {
				this.requests.delete(message.i0);
				listener(message);
			}
		}
	}

	public disconnect() {
		this.connection.close;
	}
}

export class AbstractPart extends Endpoint {

	public static parts: AbstractPart[] = [];
	private ready: boolean = false;
	protected init : any;

	public constructor(
		private readonly provider: AbstractPartProvider,
		private readonly webview: vscode.Webview,
		connection: Connection
	) {
		super(connection);

		webview.onDidReceiveMessage(message => {
			if (!this.ready && message.id == 0 && message.op == 'ready') {
				connection.onMessage((message: string) => {
					const jsonArray = JSON.parse(message);
					for (let i = 0; i < jsonArray.length; i++) {
						var jsonObject = jsonArray[i];
						if (jsonObject.id == -11 || jsonObject.id == -12 || jsonObject.id == 0)
							this.handle(jsonObject);
					}
					webview.postMessage(jsonArray);
				});
				this.ready = true;
			} else  if (this.ready && message.id == 0 && message.op == 'ready') {
				this.connection.reopen();
				this.send(this.init);
				this.ready = true;
			} else
				connection.send("[" + JSON.stringify(message) + "]")
		})

	}

	public static add(part: AbstractPart) {
		AbstractPart.parts.push(part);
	}

	public static remove(part: AbstractPart) {

		AbstractPart.parts.forEach((p, index) => {
			if (part == p) {
				AbstractPart.parts.splice(index, 1);
			}
		});
	}

	protected handle(message: any) {

		super.handle(message);
		if (message.id == -11 && message.op != "#")
			ideClient.handleIde(this, message);
		if (message.id == -12 && message.op != "#")
			ideClient.handleClipboard(this, message);
	}

	/*
	public static resetAll() {

		AbstractPart.parts.forEach((p, index) => {
			p.reset();
		});
	}

	public reset() {
		if (this.webview)
		this.webview.postMessage({id:0,op:'reset'});
		if (this.connection)
		this.connection.reset();		
	}
	*/
}

export class AbstractPartProvider {

	constructor(
		protected readonly context: vscode.ExtensionContext,
		public readonly id: string
	) { 

	}

	//#endregion

	/**
	 * Get the static HTML used for in our editor's webviews.
	 */
	protected getHtmlForWebview(webview: vscode.Webview, uri: vscode.Uri): string {

		//const ext = '_dev';
		const ext = '';


		// Local path to script and css for the webview
		const configUri = webview.asWebviewUri(vscode.Uri.file(
			path.join(this.context.extensionPath, 'media', 'impulse' + ext + '_config.js')
		));
		const scriptUri = webview.asWebviewUri(vscode.Uri.file(
			path.join(this.context.extensionPath, 'media', 'impulse' + ext + '.js')
		));
		const styleResetUri = webview.asWebviewUri(vscode.Uri.file(
			path.join(this.context.extensionPath, 'media', 'reset.css')
		));
		const styleVSCodeUri = webview.asWebviewUri(vscode.Uri.file(
			path.join(this.context.extensionPath, 'media', 'vscode.css')
		));
		const styleCodiconUri = webview.asWebviewUri(vscode.Uri.file(
			path.join(this.context.extensionPath, 'media', 'codicon.css')
		));
		const styleRemixiconUri = webview.asWebviewUri(vscode.Uri.file(
			path.join(this.context.extensionPath, 'media', 'remixicon.css')
		));
		const styleMainUri = webview.asWebviewUri(vscode.Uri.file(
			path.join(this.context.extensionPath, 'media', 'impulse.css')
		));
		const chartUri = webview.asWebviewUri(vscode.Uri.file(
			path.join(this.context.extensionPath, 'media', 'chart.js')
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
				<link href="${styleCodiconUri}" rel="stylesheet" />
				<link href="${styleRemixiconUri}" rel="stylesheet" />
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
				<script nonce="${nonce}" src="${chartUri}"></script>
			</body>
			</html>`;
	}
}

