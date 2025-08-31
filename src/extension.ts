import * as path from 'path';
import * as fs from 'fs';
import * as vscode from 'vscode';
import * as WebSocket from 'ws';
import { EditPartProvider } from './editorPart';
import { ViewPartProvider } from './viewPart';
import { ElementFS } from './elementFs';
import { Assist } from './assist';
import { Endpoint, AbstractPart } from './abstractPart';
import { Connection } from './connection';
import { LmDocTool,LmPartTool,LmChat } from './lmTools';

var backendProcess: any;
export var ideClient: IdeClient;
var extensionName: string;
var extensionPath: string;
var extensionVersion:string;
var extensionAbout:string;
export var debug: number;

export function activate(context: vscode.ExtensionContext) {

	debug =3;  // 0 no: 1:front: 2 back 3: front & back  ; debug need to be set to 0 if its not.
	console.log('activate');

	// name
	extensionName = context.extension.packageJSON.name;
	extensionPath = context.extensionPath;

	var extensionAbout = fs.readFileSync( path.join(context.extensionPath, "LICENSE.html"), 'utf8');
	
	var platformVersion =  context.extension.packageJSON.veresultrsion;
	var platfromImprint = fs.readFileSync( path.join(context.extensionPath, "IMPRINT.html"), 'utf8');
	var platformReadme = fs.readFileSync( path.join(context.extensionPath, "README.html"), 'utf8');
	var platformChangelog = fs.readFileSync( path.join(context.extensionPath, "CHANGELOG.html"), 'utf8');
	
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
	context.subscriptions.push(ElementFS.register(context, extensionName + "Fs"));
	ElementFS.addDirectory('global', 'prefs://global/de.toem.impulse.base');
	ElementFS.addDirectory('local', 'prefs://local/de.toem.impulse.base');
	ElementFS.addDirectory('ws', 'prefs://ws/de.toem.impulse.base');	

	// Scripting
	//Scripting.registerFor(context, "java,(uri) => uri.scheme == extensionName + "Fs" || uri.path.toLowerCase().endsWith(".recJs"));
	Assist.registerFor(context,"java", (uri) => uri.scheme == extensionName + "Fs" && uri.path.toLowerCase().endsWith(".java") );
	Assist.registerFor(context,"jx", (uri) => uri.path.toLowerCase().endsWith(".jx") || uri.path.toLowerCase().endsWith(".recjx"));

	// lm tools
	LmPartTool.register(context, "impulse_fetchSampleTable","samples","de.toem.impulse.parts.samples-");
	//LmTools.register(context, "ask_sampleInspector","sample","de.toem.impulse.parts.sample");
	//LmTools.register(context, "ask_viewer","viewer","de.toem.impulse.editor.records");
	LmDocTool.register(context, extensionName + "_getHelp","query",extensionName+"/documentation");
	LmDocTool.register(context, extensionName + "_getHelpContent","docIDs",extensionName+"/documentation");

	//LmChat.register(context, "chat_impulse","impulse","impulse/documentation");

	// Configuation
	const config = vscode.workspace.getConfiguration(extensionName);

	vscode.workspace.onDidChangeConfiguration(event => {
		let affected = event.affectsConfiguration(extensionName);
		if (affected) {
			console.log('affected');

		}
	})

	
	// retrieve values
	const cjava = config.get('java');
	const cport = config.get('port');
	const cxdebugger = config.get('xdebugger');

	const options = [
		"User",
		"Workspace",
		"Workspace Folder"
	];
	const cPreferenceLocationSerializer: any = options.indexOf(config.get('preferenceLocation.serializers') || "");
	const cPreferenceLocationAdaptors: any = options.indexOf(config.get('preferenceLocation.adaptors') || "");
	const cPreferenceLocationProducers: any = options.indexOf(config.get('preferenceLocation.producers') || "");
	const cPreferenceLocationViews: any = options.indexOf(config.get('preferenceLocation.views') || "");
	const cPreferenceLocationProcessors: any = options.indexOf(config.get('preferenceLocation.processors') || "");
	const cPreferenceLocationDiagrams: any = options.indexOf(config.get('preferenceLocation.diagrams') || "");
	const cPreferenceLocationSearch: any = options.indexOf(config.get('preferenceLocation.search') || "");
	const cPreferenceLocationFormatters: any = options.indexOf(config.get('preferenceLocation.formatters') || "");

	const cPreferenceLocationTemplates: any = options.indexOf(config.get('preferenceLocation.templates') || "");
	const cPreferenceLocationLicenses: any = options.indexOf(config.get('preferenceLocation.licenses') || "");
	const cPreferenceLocationColors: any = options.indexOf(config.get('preferenceLocation.colors') || "");
	const cPreferenceLocationParts: any = options.indexOf(config.get('preferenceLocation.parts') || "");
	const cPreferenceLocationCommands: any = options.indexOf(config.get('preferenceLocation.commands') || "");
	const preferenceScopes = {
		"de.toem.impulse.base/serializers": cPreferenceLocationSerializer, 
		"de.toem.impulse.base/adaptors": cPreferenceLocationAdaptors,
		"de.toem.impulse.base/producers": cPreferenceLocationProducers,
		"de.toem.impulse.base/views": cPreferenceLocationViews, 
		"de.toem.impulse.base/processors": cPreferenceLocationProcessors,
		"de.toem.impulse.base/diagrams": cPreferenceLocationDiagrams, 
		"de.toem.impulse.base/search": cPreferenceLocationSearch,
		"de.toem.impulse.base/formatters": cPreferenceLocationFormatters,

		"de.toem.impulse.base/templates": cPreferenceLocationTemplates, 
		"de.toem.impulse.base/licenses": cPreferenceLocationLicenses, 
		"de.toem.impulse.base/colors": cPreferenceLocationColors, 
		"de.toem.impulse.base/parts": cPreferenceLocationParts, 
		"de.toem.impulse.base/commands": cPreferenceLocationCommands
	}

	// get host
	var host: string = 'localhost';

	// get free port
	var port: number = (typeof cport === "string") ? parseInt(cport) : 0;

	// start server
	if ((debug & 2) == 0) {

		// collect bundles
		var bundles: string = "";
		var index = 1;
		for (let bundle of context.extension.packageJSON[extensionName + "Bundles"]) {
			if (typeof bundle === 'string') {
				const bundlePath = vscode.Uri.file(path.join(context.extension.extensionPath, bundle));
				bundles += ' -D' + extensionName + '.bundle.' + index + '="' + bundlePath.toString() + '"';
				index = index + 1;
			}
		}
		for (let extension of vscode.extensions.all) {
			if (extension.packageJSON && extension.packageJSON[extensionName + "Bundles"] && extensionName != extension.packageJSON.name) {
				for (let bundle of extension.packageJSON[extensionName + "Bundles"]) {
					if (typeof bundle === 'string') {
						const bundlePath = vscode.Uri.file(path.join(extension.extensionPath, bundle));
						bundles += ' -D' + extensionName + '.bundle.' + index + '="' + bundlePath.toString() + '"';
						index = index + 1;
					}
				}
			}
			if (extension.packageJSON && extension.packageJSON["contributes"] && extension.packageJSON["contributes"]["languageModelTools"]) {
				console.log(extension.packageJSON["contributes"]["languageModelTools"]);
			}
		}

		// -agentlib:jdwp=transport=dt_socket,address=127.0.0.1:8888,server=y,suspend=n

		// command
		const serverPath = vscode.Uri.file(path.join(context.extensionPath, extensionName));
		const osgi = vscode.Uri.file(
			path.join(context.extensionPath, extensionName, "plugins", 'org.eclipse.osgi_3.16.200.v20210226-1447.jar'));
		const cmd = cjava + (cxdebugger == true ? ' -agentlib:jdwp=transport=dt_socket,address=127.0.0.1:8888,server=y,suspend=n' : '') + (port > 0 ? ' -Dport=' + port : '') + bundles + ' -jar  "' + osgi.fsPath + '"';
		console.log('cmd: ' + cmd);

		// process	
		const cp = require('child_process')
		let gotPort = false;
		backendProcess = cp.exec(cmd, {
			cdw: serverPath.fsPath,maxBuffer: undefined
		}, (error: Error, stdout: any, stderr: any) => {
			if (error) {
				vscode.window.showErrorMessage("Could not start " + extensionName + " server:" + error.message, "Open Preferences").then((ret) => {
					if (ret) vscode.commands.executeCommand('workbench.action.openSettings', extensionName);
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
	var globalStorage: string = context.globalStorageUri ? context.globalStorageUri.toString() + '/preferences/' : "";
	var localStorage: string = context.storageUri ? context.storageUri.toString() + '/preferences/' : "";
	var workspaceStorage: string = vscode.workspace.workspaceFolders ? vscode.workspace.workspaceFolders[0].uri.toString() + '/.impulse/' : "";
	ideClient.send({ id: 0, op: 'init', x0: preferenceScopes, s1: globalStorage, s2: localStorage, s3: workspaceStorage ,s4:extensionAbout,s5:platfromImprint,s6:platformReadme,s7:platformChangelog});

	// commands

	// merge
	/*
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
*/

	// preference Fs open
	context.subscriptions.push(vscode.commands.registerCommand("de.toem.impulse.commands.preferenceFs.open", (focus: any, selection: vscode.Uri[]) => {

		vscode.workspace.updateWorkspaceFolders(0, 0, { uri: vscode.Uri.parse(ElementFS.scheme + ':/'), name: ElementFS.scheme });
		//vscode.commands.executeCommand('workbench.action.reloadWindow');

	}));

	// open preferences editor 
	context.subscriptions.push(vscode.commands.registerCommand("de.toem.impulse.commands.preferences", (focus: any, selection: vscode.Uri[]) => {

		vscode.commands.executeCommand('vscode.openWith', vscode.Uri.parse('diff:' + extensionName + ' Preferences?' + 'any'), 'de.toem.impulse.editor.preferences');

	}));

	// execute expressions 
	context.subscriptions.push(vscode.commands.registerCommand("de.toem.impulse.commands.expression", (focus: any, selection: vscode.Uri[]) => {
		var label: string = 'Expression';
		var expr: string = '';
		var pwd: string = '';

		if (selection && selection.length > 0) {
			const uri = selection[0];
			try {
				const fileData = fs.readFileSync(uri.fsPath, 'utf8');
				expr = fileData;
				label = path.basename(uri.fsPath);
				pwd = path.dirname(uri.fsPath);
			} catch (err) {
				if (err instanceof Error) {
					vscode.window.showErrorMessage(`Failed to read file: ${err.message}`);
				} else {
					vscode.window.showErrorMessage('Failed to read file: Unknown error');
				}
			}			
		}else {		
			const activeEditor = vscode.window.activeTextEditor;
			if (activeEditor) {
				label = path.basename(activeEditor.document.fileName);
				expr = activeEditor.document.getText(activeEditor.selection.isEmpty ? undefined : activeEditor.selection);
				pwd = path.dirname(activeEditor.document.fileName);
			}
		}	
		ideClient.send({ id: 0, op: 'execute', s0: label,s1:expr, s2:pwd, i3:1});

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
	private inputValidator = new Map<number, any>();
	private cbText: string = "";

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
		setInterval(() => {

			vscode.env.clipboard.readText().then((value: string) => {
				if (value != this.cbText) {
					this.cbText = value != null ? value : "";
					this.cbText.substring(0, Math.min(this.cbText.length, 0x10000));
					this.send({ id: -11, op: "CbText", s0: value });
				}
			});

		}, 1000);
	}

	protected handle(message: any) {

		super.handle(message);
		if (message.id == -11 && message.op != "#")
			this.handleIde(this, message);

		if (message.id == ElementFS.ENDPOINT_ID && message.op != "#")
			ElementFS.instance.handle(this, message);
	}

	public handleIde(endpoint: Endpoint, message: any) {

		switch (message.id) {
			case -11: {
				switch (message.op) {
					case "Information": {
						const title: string = message.s1 + ": " + message.s2;
						if (message.x3)
						vscode.window.showInformationMessage(title, ...(message.x2)).then((ret) => {
							endpoint.send({ id: message.id, op: message.op, i0: message.i0, i1: message.x3.indexOf(ret) });
						});
						else vscode.window.showInformationMessage(title);
					}
						break;
					case "Warning": {
						const title: string = message.s1 + ": " + message.s2;
						if (message.x3)
						vscode.window.showWarningMessage(title, ...(message.x3)).then((ret) => {
							endpoint.send({ id: message.id, op: message.op, i0: message.i0, i1: message.x3.indexOf(ret) });
						});
						else vscode.window.showWarningMessage(title);
					}
						break;
					case "Error": {
						const title: string = message.s1 + ": " + message.s2;
						if (message.x3)
						vscode.window.showErrorMessage(title, ...(message.x3)).then((ret) => {
							endpoint.send({ id: message.id, op: message.op, i0: message.i0, i1: message.x3.indexOf(ret) });
						});
						else vscode.window.showErrorMessage(title);
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
						vscode.window.showInputBox({
							title: message.s1, prompt: message.s2, value: message.s3,
							validateInput: message.t4 ? (value) => {
								const thenable = new Promise<string>((resolve, reject) => {
									this.inputValidator.set(message.id, { resolve: resolve, reject: reject });
									//console.log("set thenable for id", message.id);
								})
								endpoint.send({ id: message.id, op: "InputValidation", i0: message.i0, s1: value });
								return thenable;
							} : undefined
						}).then((ret) => {
							endpoint.send({ id: message.id, op: message.op, i0: message.i0, s1: ret });
						});
					}
						break;

					case "InputValidation": {
						const thenable = this.inputValidator.get(message.id);
						//console.log("got response for  thenable ", message.id, message.s1, thenable, thenable.resolve);
						if (thenable && thenable.resolve)
							thenable.resolve(message.s1);
						this.inputValidator.delete(message.id);
					}
						break;

					case "File": {
						vscode.window.showOpenDialog({ title: message.s1, canSelectFolders: false, canSelectFiles: true }).then((ret) => {
							var path: string | null = ret ? ret[0].path : null;
							endpoint.send({ id: message.id, op: message.op, i0: message.i0, s1: path });
						});
					}
						break;

					case "Folder": {
						vscode.window.showOpenDialog({ title: message.s1, canSelectFolders: true, canSelectFiles: false }).then((ret) => {
							var path: string | null = ret ? ret[0].path : null;
							endpoint.send({ id: message.id, op: message.op, i0: message.i0, s1: path });
						});
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

						var uri: vscode.Uri = vscode.Uri.parse(message.s0);
						if (uri && uri.scheme != 'file')
							uri = ElementFS.mapToFsUri(message.s0);
						if (uri)
							vscode.window.showTextDocument(uri, {}).then((ret) => {
								ret
							});
					}
						break;

					case "Preferences": {

						vscode.commands.executeCommand('vscode.openWith', vscode.Uri.parse('pref:' + extensionName + ' Preferences?' + 'any'), 'de.toem.' + extensionName + '.editor.preferences');
					}
						break;

					case "Browser": {
						if (message.s0 == "de.toem.help") {
							let url = message.s3;
							if (url && url.startsWith('help://')) {
								// Replace help:// with local path to documentation folder
								const docPath = path.join(extensionPath, 'impulse', 'documentation');
								const localPath = url.replace('help://', `file://${docPath}/`);
								
								// Use markdown preview for .md files
								if (localPath.toLowerCase().endsWith('.md')) {
									const uri = vscode.Uri.parse(localPath);
									vscode.commands.executeCommand('markdown.showLockedPreviewToSide', uri);
								} else {
									vscode.commands.executeCommand("simpleBrowser.show", localPath);
								}
							} else {
								// Use markdown preview for external .md files too
								if (url.toLowerCase().endsWith('.md')) {
									const uri = vscode.Uri.parse(url);
									vscode.commands.executeCommand('markdown.showPreview', uri);
								} else {
									vscode.commands.executeCommand("simpleBrowser.show", url);
								}
							}
						}
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
					case "CbText": {
						vscode.env.clipboard.writeText(this.cbText = message.s0);
					}
						break;					
				}
			}

		}
	}
}


