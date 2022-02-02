import * as path from 'path';
import * as vscode from 'vscode';
import { AbstractPartProvider } from './abstractPart';
import { AbstractPart } from './abstractPart';
import { getNonce } from './util';
import { Connection } from './connection';

class ViewPart extends AbstractPart {

	public constructor(
		provider: AbstractPartProvider,
		private readonly webviewView: vscode.WebviewView,
		connection: Connection
	) {
		super(provider, webviewView.webview, connection);

		// request the part
		this.send({ id: 0, op: "init", s0: provider.id, s1: "" });


		// close connection on dispose
		webviewView.onDidDispose(e => {
			this.disconnect();
			AbstractPart.remove(this);
		});

		// notify panel status
		const panel: any = webviewView;
		panel.impulse = { visible: webviewView.visible };
		webviewView.onDidChangeVisibility(e => {
			if (panel.impulse.visible != webviewView.visible) {
				this.send({ id: 0, op: "Visibility", t0: webviewView.visible });
				panel.impulse.visible = webviewView.visible;
			}
		});
		this.send({ id: 0, op: "Visibility", t0: webviewView.visible });

		AbstractPart.add(this);
	}

}

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

	public resolveWebviewView(
		webviewView: vscode.WebviewView,
		context: vscode.WebviewViewResolveContext,
		_token: vscode.CancellationToken,
	) {
		const webview: vscode.Webview = webviewView.webview;

		webview.options = {
			// Allow scripts in the webview
			enableScripts: true,

			localResourceRoots: [
				this.context.extensionUri
			]
		};

		// setup html
		webview.html = this.getHtmlForWebview(webview, vscode.Uri.file(''));

		// create part
		new ViewPart(this, webviewView, new Connection("parts"));
	}
}