package me.ste.stevesdbserver.network.packet.in;

import me.ste.stevesdbserver.database.DatabaseManager;
import me.ste.stevesdbserver.network.Connection;
import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketIn;
import me.ste.stevesdbserver.network.packet.out.ListDatabasesResponsePacket;
import me.ste.stevesdbserver.util.DataReader;

import java.util.Collections;

@PacketId(5)
public class ListDatabasesPacket extends PacketIn {
    @Override
    public void deserialize(DataReader reader) {

    }

    @Override
    public void handle(Connection connection) {
        if(DatabaseManager.getInstance().hasPermission(connection.getUsername(), "list_databases")) {
            connection.sendPacket(new ListDatabasesResponsePacket(true, DatabaseManager.getInstance().getDatabases()));
        } else {
            connection.sendPacket(new ListDatabasesResponsePacket(false, Collections.emptySet()));
        }
    }
}