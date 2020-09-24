package me.ste.stevesdbserver.network.packet.in;

import me.ste.stevesdbserver.database.DatabaseManager;
import me.ste.stevesdbserver.network.Connection;
import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketIn;
import me.ste.stevesdbserver.network.packet.out.ListTablesResponsePacket;
import me.ste.stevesdbserver.util.DataReader;

import java.util.Collections;

@PacketId(6)
public class ListTablesPacket extends PacketIn {
    private String database;

    @Override
    public void deserialize(DataReader reader) {
        this.database = reader.readString();
    }

    @Override
    public void handle(Connection connection) {
        if(DatabaseManager.getInstance().hasPermission(connection.getUsername(), "list_tables", this.database)) {
            connection.sendPacket(new ListTablesResponsePacket(true, DatabaseManager.getInstance().getDatabaseTables(this.database)));
        } else {
            connection.sendPacket(new ListTablesResponsePacket(false, Collections.emptySet()));
        }
    }
}