package me.ste.stevesdbserver.network;

import me.ste.stevesdbserver.network.packet.PacketOut;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Connection {
    private final Socket socket;
    private OutputStream outputStream;
    private int id;
    private byte[] encryptionSecret;
    private String username;

    public Connection(int id, Socket socket) {
        this.id = id;
        this.socket = socket;
        try {
            this.outputStream = this.socket.getOutputStream();
        } catch (IOException ignored) {}
    }

    public boolean sendPacket(PacketOut packet) {
        if(this.socket.isClosed()) {
            return false;
        }
        try {
            this.outputStream.write(NetworkManager.getInstance().serializePacketOut(packet, this.encryptionSecret));
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public byte[] getEncryptionSecret() {
        return this.encryptionSecret;
    }

    public void setEncryptionSecret(byte[] encryptionSecret) {
        this.encryptionSecret = encryptionSecret;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}