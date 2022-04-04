import * as path from 'path';
import * as vscode from 'vscode';
import * as WebSocket from 'ws';
import { EditPartProvider } from './editorPart';
import { ViewPartProvider } from './viewPart';
import { ElementFS } from './elementFs';
import { Scripting } from './scripting';
import { Endpoint, AbstractPart } from './abstractPart';
import { Connection } from './connection';

var backendProcess: any;
export var ideClient: IdeClient;

export function activate(context: vscode.ExtensionContext) {

	console.log('activate');

	// name
	const name = context.extension.packageJSON.name;

	// editors	
	if (context.extension.packageJSON.contributes && context.extension.packageJSON.contributes.customEditors)
		for (let customEditor of context.extension.packageJSON.contributes.customEditors)
			if (customEditor.viewType)
				context.subscriptions.push(EditPartProvider.register(context, customEditor.viewType));

	// views
	if (context.extension.packageJSON.contributes && context.extension.packageJSON.contributes.views)
		for (let panel in context.extension.packageJSON.contributes.views)
			for (let view of context.extension.packageJSON.contributes.views[panel])
				if (view.id)
					context.subscriptions.push(ViewPartProvider.register(context, view.id));

	// element fs
	context.subscriptions.push(ElementFS.register(context, name+"Fs"));
	ElementFS.addDirectory('Preferences','Preferences://de.toem.impulse.base');


	// Scripting
	Scripting.register(context, (uri)=> uri.scheme == name+"Fs" || uri.path.toLowerCase().endsWith(".recjs"));

	// Configuation
	const config = vscode.workspace.getConfiguration(name);

	// retrieve values
	const cjava = config.get('java');
	const cport = config.get('port');
	const cxdebugger  = config.get('xdebugger');
	const cpreferences= config.get('preferences');

	// get host
	var host: string = 'localhost';

	// get free port
	var port: number = (typeof cport === "string") ? parseInt(cport) : 0;

	// start server
	if (true) {

		// collect bundles
		var bundles: string = "";
		var index = 1;
		for (let extension of vscode.extensions.all) {
			if (extension.packageJSON && extension.packageJSON[name + "Bundles"]) {
				for (let bundle of extension.packageJSON[name + "Bundles"]) {
					if (typeof bundle === 'string') {
						const bundlePath = vscode.Uri.file(path.join(extension.extensionPath, bundle));
						bundles += ' -D' + name + '.bundle.' + index + '="' + bundlePath.toString() + '"';
						index = index + 1;
					}
				}
			}
		}

		// -agentlib:jdwp=transport=dt_socket,address=127.0.0.1:8888,server=y,suspend=n

		// command
		const serverPath = vscode.Uri.file(path.join(context.extensionPath, name));
		const osgi = vscode.Uri.file(
			path.join(context.extensionPath, name, 'org.eclipse.osgi_3.16.200.v20210226-1447.jar'));
		const cmd = cjava + (cxdebugger == true ? ' -agentlib:jdwp=transport=dt_socket,address=127.0.0.1:8888,server=y,suspend=n' : '') +  (port > 0 ? ' -Dport=' + port : '') + bundles + ' -jar  "' + osgi.fsPath + '"';
		console.log('cmd: ' + cmd);

		// process	
		const cp = require('child_process')
		let gotPort = false;
		backendProcess = cp.exec(cmd, {
			cdw: serverPath.fsPath
		}, (error: Error, stdout: any, stderr: any) => {
			if (error) {
				vscode.window.showErrorMessage("Could not start " + name + " server:" + error.message, "Open Preferences").then((ret) => {
					if (ret) vscode.commands.executeCommand('workbench.action.openSettings', name);
				});
			}
		});
		backendProcess.stdout.on('data', (data: string) => {
			if (!gotPort) {
				let index = data.indexOf('Server started at:');
				if (index >= 0) {
					let port = parseInt(data.substring(index + 18));

					Connection.openAll(host, port);
					gotPort = true;
				}
			}
			console.log(data);
		});
		backendProcess.stderr.on('data', (data: any) => {
			console.log(data);
		});
	} else {
		port = 5055;
		Connection.openAll(host, port);
	}

	// start IDE connection
	ideClient = new IdeClient(new Connection('ide'));
	ideClient.send({ id: 0, op: 'init', s0: (cpreferences=='Workspace' ? context.storageUri?.toString():context.globalStorageUri.toString())+'/preferences/', s1: context.globalStorageUri.toString()+"/globalPreferences/" });

	// commands

	// merge
	context.subscriptions.push(vscode.commands.registerCommand("de.toem.impulse.commands.merge", (focus: any, selection: vscode.Uri[]) => {

		var name: string = '';
		selection.forEach((uri, n) => {
				name += (n > 0 ? '+' : '') + path.basename(uri.path);
		});
		vscode.commands.executeCommand('vscode.openWith', vscode.Uri.parse('merge:' + name + '?' + selection), 'de.toem.impulse.editor.records');
	}));

	// diff
	context.subscriptions.push(vscode.commands.registerCommand("de.toem.impulse.commands.diff", (focus: any, selection: vscode.Uri[]) => {

		var name: string = '';
		selection.forEach((uri, n) => {
			if (n < 2)
				name += (n > 0 ? '<>' : '') + path.basename(uri.path);
		});
		vscode.commands.executeCommand('vscode.openWith', vscode.Uri.parse('diff:' + name + '?' + selection), 'de.toem.impulse.editor.records');
	}));


		// preference Fs open
		context.subscriptions.push(vscode.commands.registerCommand("de.toem.impulse.commands.preferenceFs.open", (focus: any, selection: vscode.Uri[]) => {

			vscode.workspace.updateWorkspaceFolders(0, 0, { uri: vscode.Uri.parse(ElementFS.scheme+':/'), name: ElementFS.scheme });
			//vscode.commands.executeCommand('workbench.action.reloadWindow');

		}));

/*
		context.subscriptions.push(vs.commands.registerCommand("_dart.reloadExtension", (_) => {
			deactivate();
			for (const sub of context.subscriptions) {
				try {
					sub.dispose();
				} catch (e) {
					console.error(e);
				}
			}
			activate(context);
		}));
		*/

		console.log('activated');
}


export function deactivate() {

	console.log('deactivate');
	
	if (ideClient)
		ideClient.disconnect();

	console.log('deactivated');
}

class IdeClient extends Endpoint {

	private outputChannels = new Map<string, vscode.OutputChannel>();
	private progressMessages = new Map<number, any>();

	public constructor(
		connection: Connection
	) {
		super(connection);
		connection.onMessage((message: string) => {
			const obj = JSON.parse(message);
			for (let i = 0; i < obj.length; i++) {
				this.handle(obj[i]);
			}

		});
	}

	protected handle(message: any) {

		super.handle(message);
		if (message.id == -11 && message.op != "#")
			this.handleIde(this, message);
		if (message.id == -12 && message.op != "#")
		this.handleClipboard(this, message);
		if (message.id == ElementFS.ENDPOINT_ID && message.op != "#")
			ElementFS.instance.handle(this, message);
	}

	public handleIde(endpoint: Endpoint, message: any) {

		switch (message.id) {
			case -11: {
				switch (message.op) {
					case "Information": {
						const title: string = message.s1 + ": " + message.s2;
						vscode.window.showInformationMessage(title, ...(message.x2)).then((ret) => {
							endpoint.send({ id: message.id, op: message.op, i0: message.i0, i1: message.x3.indexOf(ret) });
						});

					}
						break;
					case "Warning": {
						const title: string = message.s1 + ": " + message.s2;
						vscode.window.showWarningMessage(title, ...(message.x3)).then((ret) => {
							endpoint.send({ id: message.id, op: message.op, i0: message.i0, i1: message.x3.indexOf(ret) });
						});
					}
						break;
					case "Error": {
						const title: string = message.s1 + ": " + message.s2;
						vscode.window.showErrorMessage(title, ...(message.x3)).then((ret) => {
							endpoint.send({ id: message.id, op: message.op, i0: message.i0, i1: message.x3.indexOf(ret) });
						});
					}
						break;
					case "Question": {
						setTimeout(() => {
							const title: string = message.s1 + ": " + message.s2;
							vscode.window.showInformationMessage(title, { modal: true }, ...(message.x3)).then((ret) => {
								endpoint.send({ id: message.id, op: message.op, i0: message.i0, i1: message.x3.indexOf(ret) });
							});
						}, 500);

					}
						break;

					case "Input": {
						setTimeout(() => {
							vscode.window.showInputBox({ title: message.s1, prompt: message.s2, value: message.s3/*,validateInput:(value)=>{} */ }).then((ret) => {
								endpoint.send({ id: message.id, op: message.op, i0: message.i0, s1: ret });
							});
						}, 500);
					}
						break;

					case "Progress": {

						// start progress
						if (message.i1 == 0) {

							this.progressMessages.set(message.i0, message);

							const progressId: number = message.i0;
							const label = message.s2;
							vscode.window.withProgress({
								location: vscode.ProgressLocation.Notification,
								cancellable: true,
								title: label
							}, async (progress, token) => {
								var increments = 0;
								while (this.progressMessages.has(progressId) && !token.isCancellationRequested) {
									const m = this.progressMessages.get(progressId);
									if (m) {
										//console.log(m.s3,m.d4 * 100 - increments);
										if (m.d4 > 0) {
											progress.report({
												message: m.s3,
												increment: m.d4 * 100 - increments,
											});
											increments = m.d4 * 100;
										} else {
											progress.report({
												message: m.s3
											});
										}
									}
									await new Promise(r => setTimeout(r, 500));
								}
							}).then((ret) => {
								endpoint.send({ id: message.id, op: message.op, i0: message.i0 });
							});
						}
						// update progress
						else if (message.i1 == 1) {
							this.progressMessages.set(message.i0, message);
						}
						// end progress
						else if (message.i1 == -1) {
							this.progressMessages.delete(message.i0);
						}
					}
						break;

					case "TextEditor": {

						var uri: vscode.Uri = ElementFS.link2Uri(message.s0);
						vscode.window.showTextDocument(uri, {}).then((ret) => {
							ret
						});
					}
						break;

					case "Browser": {
						vscode.commands.executeCommand("simpleBrowser.show", message.s3);
					}
						break;

					case "View": {
						vscode.commands.executeCommand(message.s0 + '.focus');
					}
						break;
					case "Console": {

						this.outputChannels.set(message.s0, vscode.window.createOutputChannel(message.s1));
					}
						break;
					case "ConsoleShow": {

						if (this.outputChannels.get(message.s0))
							this.outputChannels.get(message.s0)?.show(true);
					}
						break;
					case "ConsoleStream": {

						if (this.outputChannels.get(message.s0))
							this.outputChannels.get(message.s0)?.appendLine(message.s1);
					}
						break;
				}
			}

		}
	}
	public handleClipboard(endpoint: Endpoint, message: any) {

		switch (message.id) {
			case -12: {
				switch (message.op) {
					case "Text": {
						vscode.env.clipboard.writeText(message.s0);
					}
						break;
				}
			}
		}
	}
}


