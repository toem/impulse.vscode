import * as path from 'path';
import * as vscode from 'vscode';
import * as WebSocket from 'ws';
import { EditPartProvider } from './editorPart';
import { ViewPartProvider } from './viewPart';
import { ElementFS, elementFSInstance } from './elementFsProvider';

export var impulseIdeUri:vscode.Uri;
export var impulsePartsUri:vscode.Uri;
var process: any;
var client: WebSocket;
var impulseIdeChannel: any;

export function activate(context: vscode.ExtensionContext) {

	// Register providers
	context.subscriptions.push(EditPartProvider.register(context, 'de.toem.impulse.editor.records'));
	//context.subscriptions.push(EditPartProvider.register(context, 'de.toem.impulse.editor.wallet'));
	context.subscriptions.push(ViewPartProvider.register(context, 'de.toem.impulse.parts.samples'));
	context.subscriptions.push(ViewPartProvider.register(context, 'de.toem.impulse.parts.samples.2'));
	context.subscriptions.push(ViewPartProvider.register(context, 'de.toem.impulse.parts.samples.3'));
	context.subscriptions.push(ViewPartProvider.register(context, 'de.toem.impulse.parts.samples.4'));
	context.subscriptions.push(ElementFS.register(context, 'impulse.Preferences'));

	// Configuation
	const config = vscode.workspace.getConfiguration('impulse');

	// retrieve values
	const cjava = config.get('java');
	const cport = config.get('port');

	// get host
	var host: string = 'localhost';

	// get free port
	var port: number = (typeof cport === "string") ? parseInt(cport) : 8001;

	// create uris
	impulseIdeUri = 	vscode.Uri.parse('ws://' + host + ':' + port + '/ide');
	impulsePartsUri =  vscode.Uri.parse('ws://' + host + ':' + port + '/parts'); 


	// start server
	if (true) { 

		// command
		const osgi = vscode.Uri.file(
			path.join(context.extensionPath, 'impulse', 'org.eclipse.osgi_3.16.200.v20210226-1447.jar'));
		const impulsePath = vscode.Uri.file(path.join(context.extensionPath, 'impulse'));
		const cmd = cjava + ' -Dport=' + port + ' -jar ' + osgi.fsPath;
		console.log('cmd: ' + cmd);

		// process	
		const cp = require('child_process')
		process = cp.exec(cmd, {
			cdw: impulsePath.fsPath
		}, (error: Error, stdout: any, stderr: any) => {
			if (error) {
				vscode.window.showErrorMessage("Could not start impulse server:" + error.message, "Open Preferences").then((ret) => {
					if (ret) vscode.commands.executeCommand('workbench.action.openSettings', 'impulse');
				});
			}
		});
		process.stdout.on('data', (data: any) => {
			console.log(data);

			if (!client && (typeof data === "string") && data.includes("Server started")) {
				connect(context);
			}
		});
		process.stderr.on('data', (data: any) => {
			console.log(data);
		});
	}
	else
		connect(context);

}


function connect(context: vscode.ExtensionContext) {
	// json connection
	client = new WebSocket(impulseIdeUri.toString());
	client.on('open', () => {
		console.log('TCP connection established with the server.');
		client.send(JSON.stringify([{ id: 0, op: 'init', s0: context.storageUri?.toString(), s1: context.globalStorageUri.toString() }]));
		setInterval(() => { client.send("ping") }, 1000);
	});
	impulseIdeChannel = { postMessage: function (m: any) { client.send(JSON.stringify([m])); } };
	client.on('message', (message: string) => {
		const obj = JSON.parse(message);
		for (let i = 0; i < obj.length; i++) {
			onMessage(impulseIdeChannel, obj[i]);
		}

	});
}
export function deactivate() {

	if (client) client.close();
	//if (process)
	//process.

}


var nextRequestId = 1;
var requests = new Map<number, (response: any) => void>();
var outputChannels = new Map<string, vscode.OutputChannel>();
var progressMessages = new Map<number, any>();

export function postMessage(receiver: any | null, message: any, listener: ((response: any) => void) | null) {
	if (!receiver)
		receiver = impulseIdeChannel;
	var requestId = 0;
	if (listener) {
		requestId = nextRequestId++;
		requests.set(requestId, listener);
		message.i0 = requestId;
	}
	receiver.postMessage(message);
}

export function onMessage(sender: any, message: any) {

	if (message.op == "Response") {
		const listener = requests.get(message.i0);
		if (listener != null) {
			requests.get(message.i0);
			listener(message);
		}
	}
	else switch (message.id) {
		case -11: {
			switch (message.op) {
				case "Information": {
					const title: string = message.s1 + ": " + message.s2;
					vscode.window.showInformationMessage(title, ...(message.x2)).then((ret) => {
						sender.postMessage({ id: message.id, op: message.op, i0: message.i0, i1: message.x3.indexOf(ret) });
					});

				}
					break;
				case "Warning": {
					const title: string = message.s1 + ": " + message.s2;
					vscode.window.showWarningMessage(title, ...(message.x3)).then((ret) => {
						sender.postMessage({ id: message.id, op: message.op, i0: message.i0, i1: message.x3.indexOf(ret) });
					});
				}
					break;
				case "Error": {
					const title: string = message.s1 + ": " + message.s2;
					vscode.window.showErrorMessage(title, ...(message.x3)).then((ret) => {
						sender.postMessage({ id: message.id, op: message.op, i0: message.i0, i1: message.x3.indexOf(ret) });
					});
				}
					break;
				case "Question": {
					const title: string = message.s1 + ": " + message.s2;
					vscode.window.showInformationMessage(title, { modal: true }, ...(message.x3)).then((ret) => {
						sender.postMessage({ id: message.id, op: message.op, i0: message.i0, i1: message.x3.indexOf(ret) });
					});

				}
					break;

				case "Progress": {

					// start progress
					if (message.i1 == 0) {

						progressMessages.set(message.i0, message);

						const progressId: number = message.i0;
						const label = message.s2;
						vscode.window.withProgress({
							location: vscode.ProgressLocation.Notification,
							cancellable: true,
							title: label
						}, async (progress) => {
							var increments = 0;
							while (progressMessages.has(progressId)) {
								const m = progressMessages.get(progressId);
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
							sender.postMessage({ id: message.id, op: message.op, i0: message.i0 });
						});
					}
					// update progress
					else if (message.i1 == 1) {
						progressMessages.set(message.i0, message);
					}
					// end progress
					else if (message.i1 == -1) {
						progressMessages.delete(message.i0);
					}
				}
					break;

				case "TextEditor": {

					var uri: vscode.Uri = elementFSInstance.assertFile(message.s0, message.s1);
					vscode.window.showTextDocument(uri, {}).then((ret) => {
						ret
					});
				}
					break;
				case "View": {
					vscode.commands.executeCommand(message.s0 + '.focus');
				}
					break;
				case "Console": {

					outputChannels.set(message.s0, vscode.window.createOutputChannel(message.s1));
				}
					break;
				case "ConsoleShow": {

					if (outputChannels.get(message.s0))
						outputChannels.get(message.s0)?.show(true);
				}
					break;
				case "ConsoleStream": {

					if (outputChannels.get(message.s0))
						outputChannels.get(message.s0)?.appendLine(message.s1);
				}
					break;
			}
		}

	}
}