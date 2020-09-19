package me.ste.stevesdbserver;

import me.ste.stevesdbserver.network.ClientHandlerThread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkThread extends Thread {
    private final String boundIp;
    private final int port;
    private ServerSocket serverSocket;

    public NetworkThread(String boundIp, int port) {
        this.setName("Network Thread");
        this.boundIp = boundIp;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            this.serverSocket = new ServerSocket();
            this.serverSocket.bind(new InetSocketAddress(this.boundIp, this.port));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        while(!this.serverSocket.isClosed()) {
            try {
                Socket sock = this.serverSocket.accept();
                new ClientHandlerThread(sock).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}