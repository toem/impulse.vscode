import * as path from 'path';
import * as vscode from 'vscode';
import WebSocket = require('ws');
import { AbstractPartProvider } from './abstractPart';
import { getNonce } from './util';


export class ViewPartProvider extends AbstractPartProvider implements vscode.WebviewViewProvider {

	public static register(context: vscode.ExtensionContext, id: string): vscode.Disposable {

		return vscode.window.registerWebviewViewProvider(
			id,
			new ViewPartProvider(context, id), {
			webviewOptions: {
				retainContextWhenHidden: true,
			}
		}
		);
	}

	private _view?: vscode.WebviewView;

	public resolveWebviewView(
		webviewView: vscode.WebviewView,
		context: vscode.WebviewViewResolveContext,
		_token: vscode.CancellationToken,
	) {
		this._view = webviewView;

		webviewView.webview.options = {
			// Allow scripts in the webview
			enableScripts: true,

			localResourceRoots: [
				this._context.extensionUri
			]
		};

		// setup html
		webviewView.webview.html = this.getHtmlForWebview(webviewView.webview, vscode.Uri.file(''));

		// ready and connect state
		var ready: boolean = false;
		var client: WebSocket | undefined;

		// wait for webview ready 
		var readyListener: vscode.Disposable = webviewView.webview.onDidReceiveMessage(message => {
			readyListener.dispose();
			ready = true;
			if (client && ready)
				this.initPart(webviewView, client);
		})

		// create connection to impulse server
		AbstractPartProvider.connect(webviewView.webview, (result) => {
			client = result;
			if (client && ready)
				this.initPart(webviewView, client);
		});

	}

	private initPart(webviewView: vscode.WebviewView, client: WebSocket) {

		// request the part
		AbstractPartProvider.send(client, { id: 0, op: "init", s0: this.id, s1: "" });

			
		// close connection on dispose
		webviewView.onDidDispose(e => {
			AbstractPartProvider.disconnect(client);
		});

		// notify panel status
		const panel: any = webviewView;
		panel.impulse = { visible: webviewView.visible };
		webviewView.onDidChangeVisibility(e => {
			if (panel.impulse.visible != webviewView.visible) {
				AbstractPartProvider.send(client, { id: 0, op: "Visibility", t0: webviewView.visible });
				panel.impulse.visible = webviewView.visible;
			}
		});
		AbstractPartProvider.send(client, { id: 0, op: "Visibility", t0: webviewView.visible });

	}

}