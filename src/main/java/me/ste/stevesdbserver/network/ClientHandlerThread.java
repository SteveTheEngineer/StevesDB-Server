package me.ste.stevesdbserver.network;

import me.ste.stevesdbserver.network.packet.PacketIn;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Logger;

public class ClientHandlerThread extends Thread {
    private final Socket socket;
    private final Logger logger;

    public ClientHandlerThread(Socket socket) {
        this.socket = socket;
        this.setName(this.getClass().getName());
        this.logger = Logger.getLogger(this.getName());
    }

    @Override
    public void run() {
        int connectionId = NetworkManager.getInstance().newConnectionId();
        Connection connection = new Connection(connectionId, this.socket);
        NetworkManager.getInstance().getConnections().put(connectionId, connection);
        InputStream inputStream;
        try {
            inputStream = this.socket.getInputStream();
        } catch (IOException e) {
            return;
        }
        byte[] buffer = new byte[65535];
        while(!this.socket.isClosed()) {
            try {
                int size = inputStream.read(buffer);
                if(size == -1) {
                    break;
                }
                byte[] data = Arrays.copyOfRange(buffer, 0, size);
                for(PacketIn packet : NetworkManager.getInstance().deserializePacketsIn(data, connection.getEncryptionSecret())) {
                    packet.handle(connection);
                }
            } catch (IOException ignored) {}
        }
        NetworkManager.getInstance().getConnections().remove(connectionId);
    }
}