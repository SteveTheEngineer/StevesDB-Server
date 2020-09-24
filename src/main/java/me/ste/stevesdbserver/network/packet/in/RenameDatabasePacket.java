package me.ste.stevesdbserver.network.packet.in;

import me.ste.stevesdbserver.database.DatabaseManager;
import me.ste.stevesdbserver.network.Connection;
import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketIn;
import me.ste.stevesdbserver.network.packet.out.RenameDatabaseResponsePacket;
import me.ste.stevesdbserver.util.DataReader;

@PacketId(9)
public class RenameDatabasePacket extends PacketIn {
    private String database;
    private String newName;

    @Override
    public void deserialize(DataReader reader) {
        this.database = reader.readString();
        this.newName = reader.readString();
    }

    @Override
    public void handle(Connection connection) {
        if(DatabaseManager.getInstance().hasPermission(connection.getUsername(), "rename_database", this.database)) {
            connection.sendPacket(new RenameDatabaseResponsePacket(DatabaseManager.getInstance().renameDatabase(this.database, this.newName)));
        } else {
            connection.sendPacket(new RenameDatabaseResponsePacket(false));
        }
    }
}