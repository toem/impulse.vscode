import * as path from 'path';
import * as vscode from 'vscode';
import * as WebSocket from 'ws';
import { getNonce } from './util';
import { backendPartsUri } from './extension';
import { ideClient } from './extension';

export class Endpoint {

	public constructor(
		protected readonly socket: WebSocket
	) {
	}

	public send(message: any) {
		if (this.socket)
			if (Array.isArray(message))
				this.socket.send(JSON.stringify(message));
			else
				this.socket.send("[" + JSON.stringify(message) + "]")
	}

	private nextRequestId = 1;
	private requests : Map<number, (response: any) => void> = new Map();

	protected request(message: any, listener: ((response: any) => void) | null) {

		var requestId = 0;
		if (listener) {
			requestId = this.nextRequestId++;
			this.requests.set(requestId, listener);
			message.i0 = requestId;
		}
		this.send(message);
	}

	protected handle(message: any) {

		if (message.op == "Response") {
			const listener = this.requests.get(message.i0);
			if (listener != null) {
				this.requests.delete(message.i0);
				listener(message);
			}
		}
	}

	public disconnect() {
		this.socket.close;
	}
}

export class AbstractPart  extends Endpoint {

	public static parts: AbstractPart[] = [];

	public constructor(
		private readonly provider: AbstractPartProvider,
		private readonly webview: vscode.Webview,
		socket: WebSocket
	) {
		super(socket);

		webview.onDidReceiveMessage(message => {
			socket.send("[" + JSON.stringify(message) + "]")
		})
		socket.on('message', (message: string) => {
			const jsonArray = JSON.parse(message);
			for (let i = 0; i < jsonArray.length; i++) {
				var jsonObject = jsonArray[i];
				if (jsonObject.id == -11 || jsonObject.id == 0)
					this.handle(jsonObject);
			}
			webview.postMessage(jsonArray);
		});
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
			if (message.id == -11 && message.op != "Response")
				ideClient.handleIde(this, message);
	}
}

export class AbstractPartProvider {

	constructor(
		protected readonly context: vscode.ExtensionContext,
		public readonly id: string
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

	protected connect(webview: vscode.Webview, listener: ((client: WebSocket) => void)) {

		// json connection
		var client = new WebSocket(backendPartsUri.toString());
		var established = false;
		client.on('open', () => {
			established = true;
			console.log('Part connection established with the server.');
			setInterval(() => { client.send("ping") }, 1000);
			listener(client);
		});
		client.on('error', () => {
			if (!established) {
				console.log('Not found - trying again');
				this.connect(webview, listener);
			} else
				console.log('WebSocket error:');
		})
	}
}

