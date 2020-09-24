package me.ste.stevesdbserver.network.packet.in;

import me.ste.stevesdbserver.StevesDBServer;
import me.ste.stevesdbserver.network.Connection;
import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketIn;
import me.ste.stevesdbserver.network.packet.out.EncryptionResponsePacket;
import me.ste.stevesdbserver.util.DataReader;

@PacketId(1)
public class EncryptionPacket extends PacketIn {
    @Override
    public void deserialize(DataReader reader) {

    }

    @Override
    public void handle(Connection connection) {
        connection.sendPacket(new EncryptionResponsePacket(StevesDBServer.getInstance().getKeyPair().getPublic()));
    }
}