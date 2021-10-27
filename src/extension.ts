import * as path from 'path';
import * as vscode from 'vscode';
import * as WebSocket from 'ws';
import { EditPartProvider } from './editorPart';
import { ViewPartProvider } from './viewPart';
import { ElementFS, elementFSInstance } from './elementFsProvider';
import { Endpoint } from './abstractPart';

var backendIdeUri: vscode.Uri;
export var backendPartsUri: vscode.Uri;
var backendProcess: any;
export var ideClient: IdeClient;

export function activate(context: vscode.ExtensionContext) {

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
	context.subscriptions.push(ElementFS.register(context, name + '.Preferences'));

	// Configuation
	const config = vscode.workspace.getConfiguration(name);

	// retrieve values
	const cjava = config.get('java');
	const cport = config.get('port');

	// get host
	var host: string = 'localhost';

	// get free port
	var port: number = (typeof cport === "string") ? parseInt(cport) : 8001;

	// create uris
	backendIdeUri = vscode.Uri.parse('ws://' + host + ':' + port + '/ide');
	backendPartsUri = vscode.Uri.parse('ws://' + host + ':' + port + '/parts');


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

		// command
		const serverPath = vscode.Uri.file(path.join(context.extensionPath, name));
		const osgi = vscode.Uri.file(
			path.join(context.extensionPath, name, 'org.eclipse.osgi_3.16.200.v20210226-1447.jar'));
		const cmd = cjava + ' -Dport=' + port + bundles + ' -jar  "' + osgi.fsPath + '"';
		console.log('cmd: ' + cmd);

		// process	
		const cp = require('child_process')
		backendProcess = cp.exec(cmd, {
			cdw: serverPath.fsPath
		}, (error: Error, stdout: any, stderr: any) => {
			if (error) {
				vscode.window.showErrorMessage("Could not start " + name + " server:" + error.message, "Open Preferences").then((ret) => {
					if (ret) vscode.commands.executeCommand('workbench.action.openSettings', name);
				});
			}
		});
		backendProcess.stdout.on('data', (data: any) => {
			console.log(data);
		});
		backendProcess.stderr.on('data', (data: any) => {
			console.log(data);
		});
	}

	connect(context);
}

function connect(context: vscode.ExtensionContext) {
	// json connection
	var established = false;
	var socket = new WebSocket(backendIdeUri.toString());
	socket.on('open', () => {
		established = true;
		console.log('IDE connection established with the server.');
		setInterval(() => { socket.send("ping") }, 1000);
		ideClient = new IdeClient(socket);
		ideClient.send({ id: 0, op: 'init', s0: context.storageUri?.toString(), s1: context.globalStorageUri.toString() });
	});
	socket.on('error', () => {
		if (!established) {
			console.log('Not found - trying again');
			connect(context);
		} else
			console.log('WebSocket error:');
	})
}

export function deactivate() {

	if (ideClient) ideClient.disconnect();
	//if (process)
	//process.
}

class IdeClient extends Endpoint {

	private outputChannels = new Map<string, vscode.OutputChannel>();
	private progressMessages = new Map<number, any>();

	public constructor(
		socket: WebSocket
	) {
		super(socket);
		socket.on('message', (message: string) => {
			const obj = JSON.parse(message);
			for (let i = 0; i < obj.length; i++) {
				this.handle(obj[i]);
			}

		});
	}

	protected handle(message: any) {

		super.handle(message);
		if (message.op != "Response")
			this.handleIde(this, message);
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

						var uri: vscode.Uri = message.s1 ? elementFSInstance.assertFile(message.s0, message.s1) : vscode.Uri.parse(message.s0);
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
}


