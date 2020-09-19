package me.ste.stevesdbserver.network.packet.in;

import me.ste.stevesdbserver.database.DatabaseManager;
import me.ste.stevesdbserver.network.Connection;
import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketIn;
import me.ste.stevesdbserver.network.packet.out.CreateDatabaseResponsePacket;
import me.ste.stevesdbserver.network.packet.out.DeleteTableResponsePacket;
import me.ste.stevesdbserver.util.DataReader;

@PacketId(12)
public class DeleteTablePacket extends PacketIn {
    private String database;
    private String table;

    @Override
    public void deserialize(DataReader reader) {
        this.database = reader.readString();
        this.table = reader.readString();
    }

    @Override
    public void handle(Connection connection) {
        if(DatabaseManager.getInstance().hasPermission(connection.getUsername(), "delete_table", this.database, this.table)) {
            connection.sendPacket(new DeleteTableResponsePacket(DatabaseManager.getInstance().deleteTable(this.database, this.table)));
        } else {
            connection.sendPacket(new DeleteTableResponsePacket(false));
        }
    }
}