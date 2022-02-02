import * as path from 'path';
import * as querystring from 'querystring';
import { TextDecoder, TextEncoder } from 'util';
import * as vscode from 'vscode';
import { ideClient } from './extension';


export var elementFSInstance: ElementFS;

export class File implements vscode.FileStat {

    type: vscode.FileType;
    ctime: number;
    mtime: number;
    size: number;

    name: string;
    data?: Uint8Array;

    constructor(name: string) {
        this.type = vscode.FileType.File;
        this.ctime = Date.now();
        this.mtime = Date.now();
        this.size = 0;
        this.name = name;
    }
}

export class Directory implements vscode.FileStat {

    type: vscode.FileType;
    ctime: number;
    mtime: number;
    size: number;

    name: string;
    entries: Map<string, File | Directory>;

    constructor(name: string) {
        this.type = vscode.FileType.Directory;
        this.ctime = Date.now();
        this.mtime = Date.now();
        this.size = 0;
        this.name = name;
        this.entries = new Map();
    }
}

export type Entry = File | Directory;

export class ElementFS implements vscode.FileSystemProvider {

    static register(context: vscode.ExtensionContext, scheme: string): { dispose(): any; } {
        return vscode.workspace.registerFileSystemProvider(
            scheme,
            elementFSInstance = new ElementFS()
        );
    }

    root = new Directory('');

    link2DirectoryUri(link: string): vscode.Uri {

        var uri: vscode.Uri = vscode.Uri.parse(link);
        return uri.with({ scheme: 'elementFs' + uri.scheme, query: '' });
    }

    link2Uri(link: string): vscode.Uri {

        var uri: vscode.Uri = vscode.Uri.parse(link);
        const duri = uri.with({ scheme: 'elementFs' + uri.scheme, query: '' });
        const query: querystring.ParsedUrlQuery = querystring.parse(uri.query);
        if (query.type == 'python')
            query.type = 'py';
        const newpath = path.posix.join(duri.path, path.basename(duri.path) + '.' + query.field + '.' + query.type);
        const furi = duri.with({ path: newpath })

        return furi;
    }

    uri2link(uri: vscode.Uri): string | undefined {

        const scheme: string = uri.scheme.replace('elementFs', '');
        const dirname = path.posix.dirname(uri.path);
        const basename = path.posix.basename(uri.path);
        const tidx = basename.lastIndexOf('.');
        const fidx = basename.lastIndexOf('.', tidx - 1);
        if (tidx && fidx && tidx != fidx) {
            const field = basename.substring(fidx + 1, tidx);
            const type = basename.substring(tidx + 1);
            return scheme + ':' + dirname + '?field=' + field + '&type=' + type;
        }
    }


    // --- manage file metadata

    stat(uri: vscode.Uri): Thenable<vscode.FileStat> {
        return new Promise<vscode.FileStat>((resolve, reject) => {

			ideClient.request({ id: 0, op: "Stat",  s1: this.uri2link(uri) }, (response) => {
				if (response.t1){
                    
                    // allready existing
                    let result = this._lookup(uri, false);
                    if (result){
                        resolve(result);
                        return;
                    }

                    // parent
                    const duri = uri.with({ path: path.posix.dirname(uri.path) });
                    let parent = this._lookup(duri, true);
                    if (!parent)
                        this.createDirectoryPath(duri);
                    parent = this._lookupAsDirectory(duri, false);
                    if (!parent)
                        throw vscode.FileSystemError.FileNotADirectory(uri);
                    if (parent instanceof File) 
                        throw vscode.FileSystemError.FileIsADirectory(uri);

                    // file
                    const basename = path.posix.basename(uri.path);
                    let entry = parent.entries.get(basename);
                    if (!entry){
                        entry = new File(basename);
                        parent.entries.set(basename, entry);
                    }
                    if (entry instanceof Directory) 
                        throw vscode.FileSystemError.FileIsADirectory(uri);

					resolve(this._lookup(uri, false));

                }else
					throw vscode.FileSystemError.FileNotFound(uri);
			});
		});
    }

    readDirectory(uri: vscode.Uri): [string, vscode.FileType][] {
        const entry = this._lookupAsDirectory(uri, false);
        const result: [string, vscode.FileType][] = [];
        for (const [name, child] of entry.entries) {
            result.push([name, child.type]);
        }
        return result;
    }

    // --- manage file contents

    readFile(uri: vscode.Uri): Thenable<Uint8Array> {
        return new Promise<Uint8Array>((resolve, reject) => {
			ideClient.request({ id: 0, op: "Get",  s1: this.uri2link(uri) }, (response) => {
				if (response.s1){

                    // parent
                    const duri = uri.with({ path: path.posix.dirname(uri.path) });
                    let parent = this._lookup(duri, true);
                    if (!parent)
                        this.createDirectoryPath(duri);
                    parent = this._lookupAsDirectory(duri, false);
                    if (!parent)
                        throw vscode.FileSystemError.FileNotADirectory(uri);
                    if (parent instanceof File) 
                        throw vscode.FileSystemError.FileIsADirectory(uri);

                    // file
                    const basename = path.posix.basename(uri.path);
                    let entry = parent.entries.get(basename);
                    if (!entry){
                        entry = new File(basename);
                        parent.entries.set(basename, entry);
                    }
                    if (entry instanceof Directory) 
                        throw vscode.FileSystemError.FileIsADirectory(uri);

                    entry.data = new TextEncoder().encode(response.s1);  
					resolve(entry.data);

                }else
					throw vscode.FileSystemError.FileNotFound(uri);
			});
		});

        //throw vscode.FileSystemError.FileNotFound();
    }

    writeFile(uri: vscode.Uri, content: Uint8Array, options: { create: boolean, overwrite: boolean}): void {
        const basename = path.posix.basename(uri.path);
        const parent = this._lookupParentDirectory(uri);
        let entry = parent.entries.get(basename);
        if (entry instanceof Directory) {
            throw vscode.FileSystemError.FileIsADirectory(uri);
        }
        if (!entry && !options.create) {
            throw vscode.FileSystemError.FileNotFound(uri);
        }
        if (entry && options.create && !options.overwrite) {
            throw vscode.FileSystemError.FileExists(uri);
        }
        if (!entry) {
            entry = new File(basename);
            parent.entries.set(basename, entry);
            this._fireSoon({ type: vscode.FileChangeType.Created, uri });
        }
        entry.mtime = Date.now();
        entry.size = content.byteLength;
        entry.data = content;

        this._fireSoon({ type: vscode.FileChangeType.Changed, uri });

        ideClient.send( { id: 0, op: 'Set', s0: this.uri2link(uri), s1: new TextDecoder('utf-8').decode(content) });
    }

    // --- manage files/folders

    rename(oldUri: vscode.Uri, newUri: vscode.Uri, options: { overwrite: boolean }): void {

        if (!options.overwrite && this._lookup(newUri, true)) {
            throw vscode.FileSystemError.FileExists(newUri);
        }

        const entry = this._lookup(oldUri, false);
        const oldParent = this._lookupParentDirectory(oldUri);

        const newParent = this._lookupParentDirectory(newUri);
        const newName = path.posix.basename(newUri.path);

        oldParent.entries.delete(entry.name);
        entry.name = newName;
        newParent.entries.set(newName, entry);

        this._fireSoon(
            { type: vscode.FileChangeType.Deleted, uri: oldUri },
            { type: vscode.FileChangeType.Created, uri: newUri }
        );
    }

    delete(uri: vscode.Uri): void {
        const dirname = uri.with({ path: path.posix.dirname(uri.path) });
        const basename = path.posix.basename(uri.path);
        const parent = this._lookupAsDirectory(dirname, false);
        if (!parent.entries.has(basename)) {
            throw vscode.FileSystemError.FileNotFound(uri);
        }
        parent.entries.delete(basename);
        parent.mtime = Date.now();
        parent.size -= 1;
        this._fireSoon({ type: vscode.FileChangeType.Changed, uri: dirname }, { uri, type: vscode.FileChangeType.Deleted });
    }

    createDirectory(uri: vscode.Uri): void {
        const basename = path.posix.basename(uri.path);
        const dirname = uri.with({ path: path.posix.dirname(uri.path) });
        const parent = this._lookupAsDirectory(dirname, false);

        const entry = new Directory(basename);
        parent.entries.set(entry.name, entry);
        parent.mtime = Date.now();
        parent.size += 1;
        this._fireSoon({ type: vscode.FileChangeType.Changed, uri: dirname }, { type: vscode.FileChangeType.Created, uri });
    }

    createDirectoryPath(uri: vscode.Uri): void {
        const parts = uri.path.split('/');
        let entry: Entry = this.root;
        for (const part of parts) {
            if (!part) {
                continue;
            }
            let child: Entry | undefined;
            if (entry instanceof Directory) {
                child = entry.entries.get(part);
                if (!child) {
                    const dir = new Directory(part);
                    entry.entries.set(dir.name, dir);
                    entry.mtime = Date.now();
                    entry.size += 1;
                    entry = dir;
                }
            }
        }
    }

    // --- lookup

    private _lookup(uri: vscode.Uri, silent: false): Entry;
    private _lookup(uri: vscode.Uri, silent: boolean): Entry | undefined;
    private _lookup(uri: vscode.Uri, silent: boolean): Entry | undefined {
        const parts = uri.path.split('/');
        let entry: Entry = this.root;
        for (const part of parts) {
            if (!part) {
                continue;
            }
            let child: Entry | undefined;
            if (entry instanceof Directory) {
                child = entry.entries.get(part);
            }
            if (!child) {
                if (!silent) {
                    throw vscode.FileSystemError.FileNotFound(uri);
                } else {
                    return undefined;
                }
            }
            entry = child;
        }
        return entry;
    }

    private _lookupAsDirectory(uri: vscode.Uri, silent: boolean): Directory {
        const entry = this._lookup(uri, silent);
        if (entry instanceof Directory) {
            return entry;
        }
        throw vscode.FileSystemError.FileNotADirectory(uri);
    }

    private _lookupAsFile(uri: vscode.Uri, silent: boolean): File {
        const entry = this._lookup(uri, silent);
        if (entry instanceof File) {
            return entry;
        }
        throw vscode.FileSystemError.FileIsADirectory(uri);
    }

    private _lookupParentDirectory(uri: vscode.Uri): Directory {
        const dirname = uri.with({ path: path.posix.dirname(uri.path) });
        return this._lookupAsDirectory(dirname, false);
    }

    // --- manage file events

    private _emitter = new vscode.EventEmitter<vscode.FileChangeEvent[]>();
    private _bufferedEvents: vscode.FileChangeEvent[] = [];
    private _fireSoonHandle?: NodeJS.Timer;

    readonly onDidChangeFile: vscode.Event<vscode.FileChangeEvent[]> = this._emitter.event;

    watch(_resource: vscode.Uri): vscode.Disposable {
        // ignore, fires for all changes...
        return new vscode.Disposable(() => { });
    }

    private _fireSoon(...events: vscode.FileChangeEvent[]): void {
        this._bufferedEvents.push(...events);

        if (this._fireSoonHandle) {
            clearTimeout(this._fireSoonHandle);
        }

        this._fireSoonHandle = setTimeout(() => {
            this._emitter.fire(this._bufferedEvents);
            this._bufferedEvents.length = 0;
        }, 5);
    }
}








































/*
 * Provider for static element data

export class ElementContentProvider implements vscode.TextDocumentContentProvider {

    static register(context: vscode.ExtensionContext, scheme: string): { dispose(): any; } {
        return vscode.workspace.registerTextDocumentContentProvider(
            scheme,
            new ElementContentProvider()
        );
    }


    // emitter and its event
    onDidChangeEmitter = new vscode.EventEmitter<vscode.Uri>();
    onDidChange = this.onDidChangeEmitter.event;

    async provideTextDocumentContent(uri: vscode.Uri): Promise<string> {

        return new Promise<string>((resolve, reject) => {

            let id = setTimeout(() => {
                clearTimeout(id);
                reject('Timeout')
            }, 5000);

            const link = uri.scheme + ':' + uri.path + '?' + uri.query;
            postMessage(null, { id: 0, op: "Content", s1: link }, (response) => { resolve(response.s1); })


        });
    }
}
 */