package me.ste.stevesdbserver.network.packet.in;

import me.ste.stevesdbserver.database.DatabaseManager;
import me.ste.stevesdbserver.network.Connection;
import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketIn;
import me.ste.stevesdbserver.network.packet.out.RenameTableResponsePacket;
import me.ste.stevesdbserver.util.DataReader;

@PacketId(11)
public class RenameTablePacket extends PacketIn {
    private String database;
    private String table;
    private String newName;

    @Override
    public void deserialize(DataReader reader) {
        this.database = reader.readString();
        this.table = reader.readString();
        this.newName = reader.readString();
    }

    @Override
    public void handle(Connection connection) {
        if (DatabaseManager.getInstance().hasPermission(connection.getUsername(), "rename_table", this.database, this.table)) {
            connection.sendPacket(new RenameTableResponsePacket(DatabaseManager.getInstance().renameTable(this.database, this.table, this.newName)));
        } else {
            connection.sendPacket(new RenameTableResponsePacket(false));
        }
    }
}