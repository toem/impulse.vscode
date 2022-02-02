import * as path from 'path';
import * as vscode from 'vscode';
import * as WebSocket from 'ws';

export class Connection {

    protected static connections: Connection[] = [];
    protected static host: string | null = null;
    protected static port: number = 0;
    protected socket: WebSocket | null = null;
    protected outbuffer: string[] = [];
    protected inbuffer: string[] = [];
    protected listener: ((message: string) => void) | null = null;

    public constructor(
        protected protocol: string
    ) {
        Connection.connections.push(this);
        if (Connection.host)
            this.open(Connection.host, Connection.port);
    }

    public static openAll(host: string, port: number) {
        Connection.host = host;
        Connection.port = port;
        Connection.connections.forEach((c) => {
            if (!c.socket)
                c.open(host, port);
        });
    }

    public open(host: string, port: number) {

        // uri
        const uri = vscode.Uri.parse('ws://' + host + ':' + port + '/' + this.protocol);

        // socket
        var socket = new WebSocket(uri.toString());
        socket.on('open', () => {

            // message and ping
            console.log('Connection ' + this.protocol + ' established with host ' + host + ' on port ' + port + '.');
            setInterval(() => { socket.send("ping") }, 1000);

            // clear buffer
            this.outbuffer.forEach((m) => {
                socket.send(m);
            });
            this.outbuffer = [];

            // react on messages
            socket.on('message', (message: string) => {
                if (this.listener)
                    this.listener.call(this, message);
                else
                    this.inbuffer.push(message);
            });

            // set the socket
            this.socket = socket;
        });
        socket.on('close', () => {
            this.socket = null;
        });
        socket.on('error', (err) => {
            console.log('WebSocket error:' + err);
        })
    }
    public send(message: string) {
        if (this.socket)
            this.socket.send(message);
        else
            this.outbuffer.push(message);
    }

    public onMessage(listener: (message: string) => void) {
        this.listener = listener;

        // clear in buffer
        if (listener) {
            this.inbuffer.forEach((m) => {
                listener(m);
            });
            this.inbuffer = [];
        }
    }

    public close() {
        if (this.socket) { }
        if (this.socket)
            this.socket.close;
        this.socket = null;
        const index = Connection.connections.indexOf(this);
        if (index > -1) {
            Connection.connections.splice(index, 1);
        }

    }
}