import * as path from 'path';
import * as vscode from 'vscode';
import { AbstractPartProvider } from './abstractPart';
import { getNonce } from './util';


export class ViewPartProvider extends AbstractPartProvider implements vscode.WebviewViewProvider {

    public static register(context: vscode.ExtensionContext,id: string): vscode.Disposable {

                return vscode.window.registerWebviewViewProvider(
                    id,
                    new ViewPartProvider(context,id),{
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

		webviewView.webview.html = this.getHtmlForWebview(webviewView.webview,vscode.Uri.file(''));


		webviewView.webview.onDidReceiveMessage(message => {

			switch (message.id) {

				case 0: {
					switch (message.op) {
						case "init": {

							webviewView.webview.postMessage({ id: 0, op: "Visibility", t0: webviewView.visible });
						}
					}
				}
			}

			this.onMessage(webviewView.webview, message);
		});	
			
			// activation / visiblity
			const panel: any = webviewView;
			panel.impulse = { visible: webviewView.visible };
			webviewView.onDidChangeVisibility(e => {
				if (panel.impulse.visible != webviewView.visible) {
					webviewView.webview.postMessage({ id: 0, op: "Visibility", t0: webviewView.visible });
					panel.impulse.visible = webviewView.visible;
				}
			});	
	}

}