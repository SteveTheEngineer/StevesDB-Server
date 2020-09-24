package me.ste.stevesdbserver.network.packet.in;

import me.ste.stevesdbserver.database.DatabaseManager;
import me.ste.stevesdbserver.network.Connection;
import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketIn;
import me.ste.stevesdbserver.network.packet.out.RemoveColumnResponsePacket;
import me.ste.stevesdbserver.network.packet.out.RenameColumnResponsePacket;
import me.ste.stevesdbserver.util.DataReader;

@PacketId(16)
public class RemoveColumnPacket extends PacketIn {
    private String database;
    private String table;
    private String column;

    @Override
    public void deserialize(DataReader reader) {
        this.database = reader.readString();
        this.table = reader.readString();
        this.column = reader.readString();
    }

    @Override
    public void handle(Connection connection) {
        if(DatabaseManager.getInstance().hasPermission(connection.getUsername(), "rename_column", this.database, this.table)) {
            connection.sendPacket(new RemoveColumnResponsePacket(DatabaseManager.getInstance().doAction(this.database, this.table, true, table -> {
                if(table != null && table.getColumnId(this.column) != -1) {
                    table.getColumns().remove(table.getColumnId(this.column));
                    return true;
                } else {
                    return false;
                }
            })));
        } else {
            connection.sendPacket(new RemoveColumnResponsePacket(false));
        }
    }
}