package me.ste.stevesdbserver.network.packet.in;

import me.ste.stevesdbserver.database.DatabaseManager;
import me.ste.stevesdbserver.network.Connection;
import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketIn;
import me.ste.stevesdbserver.network.packet.out.CreateDatabaseResponsePacket;
import me.ste.stevesdbserver.network.packet.out.DeleteDatabaseResponsePacket;
import me.ste.stevesdbserver.util.DataReader;

@PacketId(8)
public class DeleteDatabasePacket extends PacketIn {
    private String database;

    @Override
    public void deserialize(DataReader reader) {
        this.database = reader.readString();
    }

    @Override
    public void handle(Connection connection) {
        if(DatabaseManager.getInstance().hasPermission(connection.getUsername(), "delete_database", this.database)) {
            connection.sendPacket(new DeleteDatabaseResponsePacket(DatabaseManager.getInstance().deleteDatabase(this.database)));
        } else {
            connection.sendPacket(new DeleteDatabaseResponsePacket(false));
        }
    }
}