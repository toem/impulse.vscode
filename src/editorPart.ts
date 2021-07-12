import * as path from 'path';
import * as vscode from 'vscode';
import { Disposable, disposeAll } from './dispose';
import { getNonce } from './util';
import { AbstractPartProvider } from './abstractPart';

/**

 */
class Document extends Disposable implements vscode.CustomDocument {

	static async create(
		uri: vscode.Uri,
		backupId: string | undefined,
	): Promise<Document | PromiseLike<Document>> {
		// If we have a backup, read that. Otherwise read the resource from the workspace
		const dataFile = typeof backupId === 'string' ? vscode.Uri.parse(backupId) : uri;
		return new Document(uri);
	}

	private readonly _uri: vscode.Uri;

	private constructor(
		uri: vscode.Uri,
	) {
		super();
		this._uri = uri;
	}

	public get uri() { return this._uri; }

	/**
	 * Fired when the document is disposed of.
	 */
	private readonly _onDidDispose = this._register(new vscode.EventEmitter<void>());
	public readonly onDidDispose = this._onDidDispose.event;

	/**
	 * Fired to notify webviews that the document has changed.
	 */
	private readonly _onDidChangeDocument = this._register(new vscode.EventEmitter<{
		readonly content?: Uint8Array;
		//readonly edits: readonly PawDrawEdit[];
	}>());
	public readonly onDidChangeContent = this._onDidChangeDocument.event;

	/**
	 * Fired to tell VS Code that an edit has occured in the document.
	 * 
	 * This updates the document's dirty indicator.
	 */
	private readonly _onDidChange = this._register(new vscode.EventEmitter<{
		readonly label: string,
		undo(): void,
		redo(): void,
	}>());
	public readonly onDidChange = this._onDidChange.event;

	/**
	 * Called by VS Code when there are no more references to the document.
	 * 
	 * This happens when all editors for it have been closed.
	 */
	dispose(): void {
		this._onDidDispose.fire();
		super.dispose();
	}


	/**
	 * Called by VS Code when the user saves the document.
	 */
	async save(cancellation: vscode.CancellationToken): Promise<void> {
		await this.saveAs(this.uri, cancellation);
		// this._savedEdits = Array.from(this._edits);
	}

	/**
	 * Called by VS Code when the user saves the document to a new location.
	 */
	async saveAs(targetResource: vscode.Uri, cancellation: vscode.CancellationToken): Promise<void> {
		// const fileData = await this._delegate.getFileData();
		// if (cancellation.isCancellationRequested) {
		// 	return;
		// }
		// await vscode.workspace.fs.writeFile(targetResource, fileData);
	}

	/**
	 * Called by VS Code when the user calls `revert` on a document.
	 */
	async revert(_cancellation: vscode.CancellationToken): Promise<void> {
		// const diskContent = await RecordDocument.readFile(this.uri);
		// this._documentData = diskContent;
		// this._edits = this._savedEdits;
		// this._onDidChangeDocument.fire({
		// 	content: diskContent,
		// 	edits: this._edits,
		// });
	}

	/**
	 * Called by VS Code to backup the edited document.
	 * 
	 * These backups are used to implement hot exit.
	 */
	async backup(destination: vscode.Uri, cancellation: vscode.CancellationToken): Promise<vscode.CustomDocumentBackup> {
		//await this.saveAs(destination, cancellation);

		return {
			id: destination.toString(),
			delete: async () => {
				try {
					//await vscode.workspace.fs.delete(destination);
				} catch {
					// noop
				}
			}
		};
	}
}

/**
 * 
 * - How to implement a custom editor for binary files.
 * - Setting up the initial webview for a custom editor.
 * - Loading scripts and styles in a custom editor.
 * - Communication between VS Code and the custom editor.
 * - Using CustomDocuments to store information that is shared between multiple custom editors.
 * - Implementing save, undo, redo, and revert.
 * - Backing up a custom editor.
 */
export class EditPartProvider extends AbstractPartProvider implements vscode.CustomEditorProvider<Document> {

	public static register(context: vscode.ExtensionContext, id: string): vscode.Disposable {

		/*		vscode.commands.executeCommand('setContext', 'showMyCommand', true);
		
				vscode.commands.registerCommand('de.toem.impulse.commands.geometry.signals', () => {
					vscode.window.showInformationMessage("hoho");
						
				});
		*/
		return vscode.window.registerCustomEditorProvider(
			id,
			new EditPartProvider(context, id),
			{
				// For this demo extension, we enable `retainContextWhenHidden` which keeps the 
				// webview alive even when it is not visible. You should avoid using this setting
				// unless is absolutely required as it does have memory overhead.
				webviewOptions: {
					retainContextWhenHidden: true,
				},
				supportsMultipleEditorsPerDocument: false,
			});
	}



	//#region CustomEditorProvider

	async openCustomDocument(
		uri: vscode.Uri,
		openContext: { backupId?: string },
		_token: vscode.CancellationToken
	): Promise<Document> {
		const document: Document = await Document.create(uri, openContext.backupId);

		const listeners: vscode.Disposable[] = [];

		listeners.push(document.onDidChange(e => {
			// Tell VS Code that the document has been edited by the use.
			this._onDidChangeCustomDocument.fire({
				document,
				...e,
			});
		}));

		document.onDidDispose(() => disposeAll(listeners));

		return document;
	}

	async resolveCustomEditor(
		document: Document,
		webviewPanel: vscode.WebviewPanel,
		_token: vscode.CancellationToken
	): Promise<void> {

		// Setup initial content for the webview
		webviewPanel.webview.options = {
			enableScripts: true,
		};
		webviewPanel.webview.html = this.getHtmlForWebview(webviewPanel.webview, document.uri);

		webviewPanel.webview.onDidReceiveMessage(message => {

			switch (message.id) {

				case 0: {
					switch (message.op) {
						case "init": {

							webviewPanel.webview.postMessage({ id: 0, op: "Activation", t0: webviewPanel.active });
							webviewPanel.webview.postMessage({ id: 0, op: "Visibility", t0: webviewPanel.visible });
						}
					}
				}
			}

			this.onMessage(webviewPanel.webview, message);
		});

		/*
		webviewPanel.onDidDispose(e => {
			webviewPanel.webview.postMessage({ id: 0, op: "dispose" });
		});
*/
		// activation / visiblity
		const panel: any = webviewPanel;
		panel.impulse = { document: document, active: webviewPanel.active, visible: webviewPanel.visible };
		webviewPanel.onDidChangeViewState(e => {
			if (panel.impulse.active != webviewPanel.active) {
				webviewPanel.webview.postMessage({ id: 0, op: "Activation", t0: webviewPanel.active });
				panel.impulse.active = webviewPanel.active;
			}
			if (panel.impulse.visible != webviewPanel.visible) {
				webviewPanel.webview.postMessage({ id: 0, op: "Visibility", t0: webviewPanel.visible });
				panel.impulse.visible = webviewPanel.visible;
			}
		});



	}


	private readonly _onDidChangeCustomDocument = new vscode.EventEmitter<vscode.CustomDocumentEditEvent<Document>>();
	public readonly onDidChangeCustomDocument = this._onDidChangeCustomDocument.event;

	public saveCustomDocument(document: Document, cancellation: vscode.CancellationToken): Thenable<void> {
		return document.save(cancellation);
	}

	public saveCustomDocumentAs(document: Document, destination: vscode.Uri, cancellation: vscode.CancellationToken): Thenable<void> {
		return document.saveAs(destination, cancellation);
	}

	public revertCustomDocument(document: Document, cancellation: vscode.CancellationToken): Thenable<void> {
		return document.revert(cancellation);
	}

	public backupCustomDocument(document: Document, context: vscode.CustomDocumentBackupContext, cancellation: vscode.CancellationToken): Thenable<vscode.CustomDocumentBackup> {
		return document.backup(context.destination, cancellation);
	}

}
