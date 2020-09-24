package me.ste.stevesdbserver.network.packet.in;

import me.ste.stevesdbserver.StevesDBServer;
import me.ste.stevesdbserver.network.Connection;
import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketIn;
import me.ste.stevesdbserver.network.packet.out.HelloResponsePacket;
import me.ste.stevesdbserver.util.DataReader;

@PacketId(0)
public class HelloPacket extends PacketIn {
    private int protocolVersion;
    private String clientVersion;

    @Override
    public void deserialize(DataReader reader) {
        this.protocolVersion = reader.readInt();
        this.clientVersion = reader.readString();
    }

    @Override
    public void handle(Connection connection) {
        connection.sendPacket(new HelloResponsePacket(StevesDBServer.PROTOCOL_VERSION, StevesDBServer.SERVER_VERSION));
    }
}