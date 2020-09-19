package me.ste.stevesdbserver.network.packet.in;

import me.ste.stevesdbserver.network.Connection;
import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketIn;
import me.ste.stevesdbserver.network.packet.out.LogoutResponsePacket;
import me.ste.stevesdbserver.util.DataReader;

@PacketId(4)
public class LogoutPacket extends PacketIn {
    @Override
    public void deserialize(DataReader reader) {

    }

    @Override
    public void handle(Connection connection) {
        if(connection.getUsername() != null) {
            connection.setUsername(null);
            connection.sendPacket(new LogoutResponsePacket(true));
        } else {
            connection.sendPacket(new LogoutResponsePacket(false));
        }
    }
}