import * as path from 'path';
import * as querystring from 'querystring';
import { TextDecoder, TextEncoder } from 'util';
import * as vscode from 'vscode';
import { ideClient } from './extension';
import { Endpoint } from './abstractPart';

// ========================================================================================================================
// Files and Directories
// ========================================================================================================================

export type Entry = File | Directory;

const NONE = 0;
const DIRECTORY = 1;
const FIELD = 2;
const FILE = 3;

export class AbstractFile implements vscode.FileStat {

    type: vscode.FileType;
    ctime: number;
    mtime: number;
    size: number;
    permissions: vscode.FilePermission | undefined;

    parent: Directory | undefined;
    name: string;
    hasstat: boolean;

    constructor(name: string, type: vscode.FileType, parent: Directory | undefined, readOnly: boolean) {

        this.type = type;
        this.ctime = Date.now();
        this.mtime = Date.now();
        this.size = 0;
        this.name = name;
        this.parent = parent;
        this.hasstat = false;
        if (readOnly)
            this.permissions = vscode.FilePermission.Readonly;

        // add to parent directory
        if (parent && (this instanceof File || this instanceof Directory)) {
            parent.entries.set(name, this);
            parent.mtime = Date.now();
            parent.size += 1;
        }
    }

    public move(name: string, parent: Directory) {
        if (this.parent && (this instanceof File || this instanceof Directory)) {

            // move to differnt parent
            if (parent != this.parent) {

                // remove from parent directory
                this.parent.entries.delete(this.name);
                this.parent.mtime = Date.now();
                this.parent.size -= 1;

                // move
                this.parent = parent;
                this.name = name;

                // add to parent directory
                this.parent.entries.set(this.name, this);
                this.parent.mtime = Date.now();
                this.parent.size += 1;

            }
            // just change name
            else if (name != this.name) {

                // remove from parent directory
                this.parent.entries.delete(this.name);
                this.parent.mtime = Date.now();

                // move
                this.name = name;

                // add to parent directory
                this.parent.entries.set(this.name, this);
            }
        }
    }

    public isStatic(): boolean {
        return !this.parent;
    }

    public hasStat() {
        return this.hasstat;
    }

    public dispose() {

        if (this.parent) {

            // remove from parent directory
            this.parent.entries.delete(this.name);
            this.parent.mtime = Date.now();
            this.parent.size -= 1;
        }
    }
}
export class File extends AbstractFile {

    data?: Uint8Array;

    constructor(name: string, parent: Directory) {
        super(name, vscode.FileType.File, parent, false);
    }

    public set(content: Uint8Array) {
        this.mtime = Date.now();
        this.size = content.byteLength;
        this.data = content;
    }

    public stat(type: number, size: number) {
        // Always bump mtime so VS Code knows to re-read even if size didn't change
        const now = Date.now();
        if (this.size !== size) {
            this.size = size;
        }
        if (type == FILE)
            this.permissions = vscode.FilePermission.Readonly;
        
        // Content may have changed without size change; invalidate cached data
        this.data = undefined;
        this.mtime = now;
        this.hasstat = true;
    }
}

export class Directory extends AbstractFile {

    directory: string[] | undefined;
    entries: Map<string, File | Directory>;

    constructor(name: string, parent: Directory | undefined) {
        super(name, vscode.FileType.Directory, parent, true);
        this.entries = new Map();
    }

    public stat(directory: string[]) {
        if (!this.directory || JSON.stringify(this.directory) !== JSON.stringify(directory)) {
            this.size = directory.length;
            this.directory = directory;
            this.mtime = Date.now();
        }
        this.hasstat = true;
    }

    public getDirectory(): [string, vscode.FileType][] {
        var d: [string, vscode.FileType][] = [];

        if (this.isStatic()) {
            for (const e of this.entries)
                d.push([e[0], e[1].type]);
        }
        else if (this.directory && this.hasstat)
            for (const name of this.directory) {
                const t = parseInt(name[0]);
                let mname = (t == FIELD ? this.name + '.' : "") + name.substring(1);
                let type = (t == FIELD || t == FILE) ? vscode.FileType.File : vscode.FileType.Directory;
                d.push([mname, type]);
            }
        return d;
    }
}


// ========================================================================================================================
// ElementFS
// ========================================================================================================================

export class ElementDocFS implements vscode.FileSystemProvider {

    public static ENDPOINT_ID = -15;
    public static CONTENT_PREF = "pref";
    public static CONTENT_DOC = "doc";
    public static instance: ElementDocFS;
    public static scheme: string;
    private static contents: [string, string, string][] = [];  // impulse uri, vscode uri, content
    private root = new Directory('', undefined);


    static register(context: vscode.ExtensionContext, scheme: string): { dispose(): any; } {
        ElementDocFS.scheme = scheme;
        return vscode.workspace.registerFileSystemProvider(
            scheme,
            ElementDocFS.instance = new ElementDocFS()
        );
    }

    static addDirectory(name: string, impulseUri: string, vscodeUri: string, content:string) {
        this.contents.push([impulseUri, this.scheme + vscodeUri, content]);
        new Directory(name, ElementDocFS.instance.root);
    }

    public constructor(

    ) {
    }




    // ========================================================================================================================
    // Mapping 
    // ========================================================================================================================

    static mapToFsUri(uri: string): vscode.Uri {

        for (const content of ElementDocFS.contents) {
            if (uri.startsWith(content[0])) {

                var uri1: vscode.Uri = vscode.Uri.parse(content[1] + uri.substring(content[0].length));

                // doc does not need element query/fragment 
                if (content[2] == ElementDocFS.CONTENT_DOC) {
                    console.log("mapToFsUri", uri.toString(), uri1);
                    return uri1;
                }

                const fragment = uri1.fragment ? uri1.fragment.split("/") : undefined;
                const uri2 = uri1.with({ query: '', fragment: '', path: path.posix.join(uri1.path, uri1.query, fragment ? path.basename(uri1.query) + '.' + fragment[0] + '.' + fragment[1] : "") });
                console.log("mapToFsUri", uri, uri2.toString());
                return uri2;
            }
        }
        return vscode.Uri.parse("");
    }

    static mapFromFsUri(uri: vscode.Uri): string | undefined {
        console.log("mapFromFsUri", uri.toString());
        for (const content of ElementDocFS.contents) {
            if (uri.toString().startsWith(content[1].substring(0,content[1].length-1))) {

                // fs uri has no fragment query
                uri = uri.with({fragment:'', query:''});

                // doc does not need element query/fragment 
                if (content[2] == ElementDocFS.CONTENT_DOC) {
                    var res = content[0] + uri.toString().substring(content[1].length);
                    console.log("mapFromFsUri", uri.toString(), res);
                    return res;
                }

                var uri1: vscode.Uri = vscode.Uri.parse(content[0] + uri.toString().substring(content[1].length));

                // field and lang name - fragment
                var fragment;
                var fname = path.basename(uri1.path);
                var langname = path.extname(fname);
                fname = fname.substring(0, fname.length - langname.length);
                var fieldname = path.extname(fname);
                fname = fname.substring(0, fname.length - fieldname.length);
                var dname = path.dirname(uri1.path);
                var cname = path.basename(dname);
                if (fname != "" && fieldname.length > 1 && langname.length > 1 && cname == fname) {
                    fragment = fieldname.substring(1) + "/" + langname.substring(1)
                }

                // element path
                var splitted = dname.substring(1).split('/');
                if (splitted.length <2)
                    return uri1.toString();
                var elementname = splitted[1];
                var elementpath = path.posix.join("/", splitted[0], splitted[1]);

                // cell path - query
                var query = fragment != null ? dname.substring(elementpath.length + 1): uri1.path.substring(elementpath.length + 1);

                var uri2 = uri1.with({ query: query, fragment: fragment, path: elementpath });

                console.log("mapFromFsUri", uri.toString(), uri2.toString());
                return uri2.toString();

            }
        }
        return "";
    }
    /*
        static link2Uri(link: string): vscode.Uri {
    
            for (const content of ElementFS.contents) {
                if (link.startsWith(content[0])) {
                    var uri: vscode.Uri = vscode.Uri.parse(this.scheme + ':/' + content[1] + link.substring(content[0].length));
                    const duri = uri.with({ query: '' });
                    const query: querystring.ParsedUrlQuery = querystring.parse(uri.query);
                    if (query.lang && query.field) {
                        const newpath = path.posix.join(duri.path, path.basename(duri.path) + '.' + query.field + '.' + query.lang);
                        const furi = duri.with({ path: newpath })
                        return furi;
                    }
                    return duri;
                }
            }
            return vscode.Uri.parse("");
        }
    
        static uri2link(uri: vscode.Uri): string | undefined {
    
    
            let uripath = uri.path;
            for (const content of ElementFS.contents) {
                if (uripath.startsWith('/' + content[1])) {
                    uripath = uripath.substring(content[1].length + 1);
                    let dirname = path.posix.dirname(uripath);
                    const basename = path.posix.basename(uripath);
                    const dirbasename = path.posix.basename(dirname);
                    if (dirbasename != "" && basename.startsWith(dirbasename)) {
                        const tidx = basename.lastIndexOf('.');
                        const fidx = basename.lastIndexOf('.', tidx - 1);
                        if (tidx && fidx && tidx != fidx) {
                            const field = basename.substring(fidx + 1, tidx);
                            const language = basename.substring(tidx + 1);
                            const link = content[0] + dirname + '?field=' + field + '&lang=' + language;
                            return link;
                        }
                    }
                    return content[0] + uripath;
                }
            }
            return "";
        }
    */

    // ========================================================================================================================
    // Stat
    // ========================================================================================================================

    stat(uri: vscode.Uri): vscode.FileStat | Thenable<vscode.FileStat> {
        console.log("stat", uri);

        // static
        let result = this.lookup(uri, true);
        if (result && result.isStatic())
            return result;

        return new Promise<vscode.FileStat>((resolve, reject) => {
            console.log("ideClient", uri, ElementDocFS.mapFromFsUri(uri));
            ideClient.request({ id: ElementDocFS.ENDPOINT_ID, op: "Stat", s1: ElementDocFS.mapFromFsUri(uri) }, (response) => {

                if (response.i1 && response.i1 != NONE) {

                    // allready existing
                    let result = this.lookup(uri, true);
                    if (result instanceof File && (response.i1 != FILE && response.i1 != FIELD))
                        result.dispose();
                    if (result instanceof Directory && response.i1 != DIRECTORY)
                        result.dispose();

                    // not existing
                    if (!result) {

                        // directories
                        const duri = (response.i1 == DIRECTORY) ? uri : uri.with({ path: path.posix.dirname(uri.path) });
                        let directory = this.lookup(duri, true);
                        if (!directory)
                            directory = this.createDirectoryPath(duri);
                        if (!directory) {
                            reject(vscode.FileSystemError.FileNotADirectory(uri));
                            return;
                        }
                        if (directory instanceof File) {
                            reject(vscode.FileSystemError.FileIsADirectory(uri));
                            return;
                        }
                        // file
                        if (response.i1 == FILE || response.i1 == FIELD) {
                            const basename = path.posix.basename(uri.path);
                            let file = directory.entries.get(basename);
                            if (!file) {
                                file = new File(basename, directory);
                            }
                            if (file instanceof Directory) {
                                reject(vscode.FileSystemError.FileIsADirectory(uri));
                                return;
                            }
                            result = file;
                        } else
                            result = directory;
                    }

                    // file
                    if (result instanceof File) {
                        result.stat(response.i1, response.i2)
                        resolve(result);
                        return;
                    }
                    // directory
                    else if (result instanceof Directory) {
                        result.stat(response.x2)
                        resolve(result);
                        return;
                    }
                } else {

                    // existing
                    let result = this.lookup(uri, false);
                    if (result) {
                        result.dispose();
                    }
                    reject(vscode.FileSystemError.FileNotFound(uri));
                }
            });
        });
    }

    // ========================================================================================================================
    // Read/Create Directory
    // ========================================================================================================================

    readDirectory(uri: vscode.Uri): [string, vscode.FileType][] | Thenable<[string, vscode.FileType][]> {
        console.log("readDirectory", uri);
        const entry = this.lookupAsDirectory(uri, false);
        if (!entry.isStatic() && !entry.hasStat())
            return new Promise<[string, vscode.FileType][]>((resolve, reject) => {

                ideClient.request({ id: ElementDocFS.ENDPOINT_ID, op: "Stat", s1: ElementDocFS.mapFromFsUri(uri) }, (response) => {

                    if (response.i1 && response.i1 != NONE) {

                        // allready existing
                        let result = this.lookup(uri, true);

                        // directory
                        if (result instanceof Directory) {
                            result.stat(response.x2)
                            resolve(result.getDirectory());
                            return;
                        }
                    }
                    reject(vscode.FileSystemError.FileNotFound(uri));
                });
            });
        return entry.getDirectory();
    }

    createDirectory(uri: vscode.Uri): void {

        throw vscode.FileSystemError.NoPermissions(uri);
    }

    private createDirectoryPath(uri: vscode.Uri): Directory | undefined {
        const parts = uri.path.split('/');
        let entry: Entry = this.root;
        for (const part of parts) {
            if (!part) {
                continue;
            }
            let child: Entry | undefined;
            if (entry instanceof Directory) {
                child = entry.entries.get(part);
                if (!child)
                    entry = new Directory(part, entry);
                else
                    entry = child;
            }
        }
        return (entry instanceof Directory) ? entry : undefined;
    }
    // ========================================================================================================================
    // Read/Write File
    // ========================================================================================================================


    readFile(uri: vscode.Uri): Thenable<Uint8Array> {
        console.log("readFile", uri);
        return new Promise<Uint8Array>((resolve, reject) => {
            ideClient.request({ id: ElementDocFS.ENDPOINT_ID, op: "Read", s1: ElementDocFS.mapFromFsUri(uri) }, (response) => {
                if (response.i1 != NONE) {

                    // parent
                    const duri = uri.with({ path: path.posix.dirname(uri.path) });
                    let parent = this.lookup(duri, true);
                    if (!parent)
                        parent = this.createDirectoryPath(duri);
                    if (!parent) {
                        reject(vscode.FileSystemError.FileNotADirectory(uri));
                        return;
                    }
                    if (parent instanceof File) {
                        reject(vscode.FileSystemError.FileIsADirectory(uri));
                        return;
                    }

                    // file
                    const basename = path.posix.basename(uri.path);
                    let entry = parent.entries.get(basename);
                    if (!entry)
                        entry = new File(basename, parent);
                    if (entry instanceof Directory) {
                        reject(vscode.FileSystemError.FileIsADirectory(uri));
                        return;
                    }
                    let data = new TextEncoder().encode(response.s2);
                    entry.set(data);
                    resolve(data);

                } else
                    reject(vscode.FileSystemError.FileNotFound(uri));
            });
        });

        //throw vscode.FileSystemError.FileNotFound();
    }

    writeFile(uri: vscode.Uri, content: Uint8Array, options: { create: boolean, overwrite: boolean }): void {
        console.log("writeFile", uri);
        const basename = path.posix.basename(uri.path);
        const parent = this.lookupParentDirectory(uri, false);
        let file = parent.entries.get(basename);
        if (file instanceof Directory) {
            throw vscode.FileSystemError.FileIsADirectory(uri);
        }
        if (!file && !options.create) {
            throw vscode.FileSystemError.FileNotFound(uri);
        }
        if (file && options.create && !options.overwrite) {
            throw vscode.FileSystemError.FileExists(uri);
        }
        if (!file) {
            file = new File(basename, parent);
            this._fireSoon({ type: vscode.FileChangeType.Created, uri });
        }

        file.set(content);
        ideClient.send({ id: ElementDocFS.ENDPOINT_ID, op: 'Write', s1: ElementDocFS.mapFromFsUri(uri), s2: new TextDecoder('utf-8').decode(content) });
        this._fireSoon({ type: vscode.FileChangeType.Changed, uri });
    }

    // ========================================================================================================================
    // Rename / Delete
    // ========================================================================================================================

    rename(oldUri: vscode.Uri, newUri: vscode.Uri, options: { overwrite: boolean }): void {

        throw vscode.FileSystemError.NoPermissions(oldUri);
    }

    delete(uri: vscode.Uri): void {

        throw vscode.FileSystemError.NoPermissions(uri);
    }

    // ========================================================================================================================
    // Lookup
    // ========================================================================================================================

    private lookup(uri: vscode.Uri, silent: false): Entry;
    private lookup(uri: vscode.Uri, silent: boolean): Entry | undefined;
    private lookup(uri: vscode.Uri, silent: boolean): Entry | undefined {
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

    private lookupAsDirectory(uri: vscode.Uri, silent: boolean): Directory {
        const entry = this.lookup(uri, silent);
        if (entry instanceof Directory) {
            return entry;
        }
        throw vscode.FileSystemError.FileNotADirectory(uri);
    }

    private lookupAsFile(uri: vscode.Uri, silent: boolean): File {
        const entry = this.lookup(uri, silent);
        if (entry instanceof File) {
            return entry;
        }
        throw vscode.FileSystemError.FileIsADirectory(uri);
    }

    private lookupParentDirectory(uri: vscode.Uri, silent: boolean): Directory {
        const dirname = uri.with({ path: path.posix.dirname(uri.path) });
        return this.lookupAsDirectory(dirname, silent);
    }

    // ========================================================================================================================
    // Events
    // ========================================================================================================================

    private _emitter = new vscode.EventEmitter<vscode.FileChangeEvent[]>();
    private _bufferedEvents: vscode.FileChangeEvent[] = [];
    private _fireSoonHandle?: NodeJS.Timeout;

    readonly onDidChangeFile: vscode.Event<vscode.FileChangeEvent[]> = this._emitter.event;

    // Align with current API: include options parameter
    watch(_resource: vscode.Uri, _options: { recursive: boolean; excludes: string[] }): vscode.Disposable {
        // ignore, fires for all changes...
        return new vscode.Disposable(() => { });
    }

    private _fireSoon(...events: vscode.FileChangeEvent[]): void {
        this._bufferedEvents.push(...events);

        if (this._fireSoonHandle) {
            clearTimeout(this._fireSoonHandle);
        }

        this._fireSoonHandle = setTimeout(() => {
            // Important: don't mutate the same array that listeners receive
            const toFire = this._bufferedEvents.slice();
            this._bufferedEvents.length = 0;
            this._emitter.fire(toFire);
        }, 5);
    }

    // ========================================================================================================================
    // MEssages
    // ========================================================================================================================

    public handle(endpoint: Endpoint, message: any) {

        switch (message.op) {
            case "Notify": {
                const change: string = message.s0;
                const uri: vscode.Uri = ElementDocFS.mapToFsUri(message.s1);
                if (!uri || !uri.scheme) break;

                if (change === 'changed') {
                    // Invalidate cache and bump mtime immediately, so subsequent stat shows a change
                    const entry = this.lookup(uri, true);
                    if (entry instanceof File) {
                        entry.data = undefined;
                        entry.mtime = Date.now();
                    }
                    this._fireSoon({ type: vscode.FileChangeType.Changed, uri });
                } else if (change === 'created') {
                    this._fireSoon({ type: vscode.FileChangeType.Created, uri });
                } else if (change === 'deleted') {
                    this._fireSoon({ type: vscode.FileChangeType.Deleted, uri });
                }
            }
                break;
        }
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
