import * as path from 'path';
import * as vscode from 'vscode';
import { Disposable, disposeAll } from './dispose';
import { getNonce } from './util';
import { AbstractPartProvider } from './abstractPart';
import { AbstractPart } from './abstractPart';
import { Connection } from './connection';

/**

 */
class Document extends Disposable implements vscode.CustomDocument {

	static async create(
		uri: vscode.Uri,
		backupId: string | undefined,
	): Promise<Document | PromiseLike<Document>> {
		// If we have a backup, read that. Otherwise read the resource from the workspace
		const dataFile: vscode.Uri = typeof backupId === 'string' ? vscode.Uri.parse(backupId) : uri;

		//var fileData;
		//if (uri.scheme != "file") {
		//	fileData = await Document.readFile(uri);
		//}

		return new Document(uri);
	}


	public readonly type: string | undefined;
	public readonly content: vscode.Uri[] | undefined;

	private constructor(
		private readonly _uri: vscode.Uri

	) {
		super();

		if (_uri.scheme == 'merge' || _uri.scheme == 'diff') {
			this.type = _uri.scheme;
			var uris: string[] = _uri.query.split(',');
			this.content = [];
			uris.forEach((uri, n) => {
				this.content?.push(vscode.Uri.parse(uri));
			});
		}
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



	private static async readFile(uri: vscode.Uri): Promise<Uint8Array> {
		if (uri.scheme === 'untitled') {
			return new Uint8Array();
		}
		return vscode.workspace.fs.readFile(uri);
	}
}

class EditorPart extends AbstractPart {

	public constructor(
		private readonly editprovider: EditPartProvider,
		private readonly webviewPanel: vscode.WebviewPanel,
		connection: Connection,
		private readonly document: Document
	) {
		super(editprovider, webviewPanel.webview, connection);

		// request the part
		this.init  = { id: 0, op: "init", s0: editprovider.id };
		if (document.type && document.content) {
			this.init.s1 = document.type;
			document.content.forEach((uri, n) => {
				this.init['s' + (n + 2)] = uri.scheme + ':' + uri.path;
			});
		} else {
			this.init.s2 = document.uri.scheme + ':' + document.uri.path
		}
		this.send(this.init);

		// close connection on dispose
		webviewPanel.onDidDispose(e => {
			this.disconnect();
			AbstractPart.remove(this);
		});

		// notify panel status
		const panel: any = webviewPanel;
		panel.impulse = { document: document, active: webviewPanel.active, visible: webviewPanel.visible };
		webviewPanel.onDidChangeViewState(e => {
			if (panel.impulse.active != webviewPanel.active) {
				this.send({ id: 0, op: "Activation", t0: webviewPanel.active });
				panel.impulse.active = webviewPanel.active;
			}
			if (panel.impulse.visible != webviewPanel.visible) {
				this.send({ id: 0, op: "Visibility", t0: webviewPanel.visible });
				panel.impulse.visible = webviewPanel.visible;
			}
		});
		this.send({ id: 0, op: "Activation", t0: webviewPanel.active });
		this.send({ id: 0, op: "Visibility", t0: webviewPanel.visible });


		AbstractPart.add(this);
	}

	protected handle(message: any) {

		super.handle(message);
		if (message.id == 0 && message.op != "#")
			switch (message.op) {
				case "Dirty": {
					if (message.t0)
						this.editprovider.onDidChangeCustomDocumentEmitter.fire({ document: this.document });
				}
					break;
			}
	}

	public static getFromDocument(document: Document): EditorPart | null {
		var result: EditorPart | null = null;
		AbstractPart.parts.forEach((p, index) => {
			if (p instanceof EditorPart && (p as EditorPart).document == document) {
				result = p as EditorPart;
			}
		});
		return result;
	}


	public save(cancellation: vscode.CancellationToken): Promise<void> {
		return new Promise((resolve, reject) => {
			this.request({ id: 0, op: "Save" }, (response) => {
				if (response.t1)
					resolve();
				else
					reject("Save failed");
			});
		});
	}

	public saveAs(uri: vscode.Uri, cancellation: vscode.CancellationToken): Promise<void> {

		return new Promise((resolve, reject) => {
			this.request({ id: 0, op: "SaveAs", s1: uri.toString() }, (response) => {
				if (response.t1)
					resolve();
				else
					reject("Save failed");
			});
		});
	}

	public revert(_cancellation: vscode.CancellationToken): Promise<void> {
		return new Promise((resolve, reject) => {
			this.request({ id: 0, op: "Revert" }, (response) => {
				if (response.t1)
					resolve();
				else
					reject("Revert failed");
			});
		});
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

export class EditPartProvider extends AbstractPartProvider implements vscode.CustomEditorProvider<Document> {

	public static register(context: vscode.ExtensionContext, id: string): vscode.Disposable {

		return vscode.window.registerCustomEditorProvider(
			id,
			new EditPartProvider(context, id),
			{
				webviewOptions: {
					retainContextWhenHidden: true,
				},
				supportsMultipleEditorsPerDocument: false,
			});
	}


	async openCustomDocument(
		uri: vscode.Uri,
		openContext: { backupId?: string },
		_token: vscode.CancellationToken
	): Promise<Document> {
		const document: Document = await Document.create(uri, openContext.backupId);

		return document;
	}


	async resolveCustomEditor(
		document: Document,
		webviewPanel: vscode.WebviewPanel,
		_token: vscode.CancellationToken
	): Promise<void> {

		const webview: vscode.Webview = webviewPanel.webview;

		// Setup initial content for the webview
		webview.options = {
			enableScripts: true,
		};

		// setup html
		webview.html = this.getHtmlForWebview(webview, document.uri);

		// create part
		new EditorPart(this, webviewPanel, new Connection("parts"), document);
	}


	public readonly onDidChangeCustomDocumentEmitter = new vscode.EventEmitter<vscode.CustomDocumentContentChangeEvent<Document>>();
	public readonly onDidChangeCustomDocument = this.onDidChangeCustomDocumentEmitter.event;

	public saveCustomDocument(document: Document, cancellation: vscode.CancellationToken): Thenable<void> {
		const part = EditorPart.getFromDocument(document);
		if (part)
			return part.save(cancellation);
		return Promise.reject();
	}

	public saveCustomDocumentAs(document: Document, destination: vscode.Uri, cancellation: vscode.CancellationToken): Thenable<void> {
		const part = EditorPart.getFromDocument(document);
		if (part)
			return part.saveAs(destination, cancellation);
		return Promise.reject();
	}

	public revertCustomDocument(document: Document, cancellation: vscode.CancellationToken): Thenable<void> {
		const part = EditorPart.getFromDocument(document);
		if (part)
			return part.revert(cancellation);
		return Promise.reject();
	}

	public backupCustomDocument(document: Document, context: vscode.CustomDocumentBackupContext, cancellation: vscode.CancellationToken): Thenable<vscode.CustomDocumentBackup> {
		const part = EditorPart.getFromDocument(document);
		if (part)
			return part.backup(context.destination, cancellation);
		return Promise.reject();
	}

}
