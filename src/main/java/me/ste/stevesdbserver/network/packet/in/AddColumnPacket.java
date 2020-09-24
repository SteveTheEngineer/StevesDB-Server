package me.ste.stevesdbserver.network.packet.in;

import me.ste.stevesdbserver.database.DatabaseManager;
import me.ste.stevesdbserver.database.TableColumnType;
import me.ste.stevesdbserver.network.Connection;
import me.ste.stevesdbserver.network.NetworkManager;
import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketIn;
import me.ste.stevesdbserver.network.packet.out.AddColumnResponsePacket;
import me.ste.stevesdbserver.util.DataReader;

@PacketId(14)
public class AddColumnPacket extends PacketIn {
    private String database;
    private String table;
    private TableColumnType type;
    private String name;

    @Override
    public void deserialize(DataReader reader) {
        this.database = reader.readString();
        this.table = reader.readString();
        this.type = TableColumnType.values()[Math.max(0, Math.min(TableColumnType.values().length - 1, reader.readUnsignedByte()))];
        this.name = reader.readString();
    }

    @Override
    public void handle(Connection connection) {
        if(DatabaseManager.getInstance().hasPermission(connection.getUsername(), "add_column", this.database, this.table)) {
            connection.sendPacket(DatabaseManager.getInstance().doAction(this.database, this.table, true, table -> {
                int cid = table.addColumn(this.type, this.name);
                return new AddColumnResponsePacket(cid != -1);
            }));
        } else {
            connection.sendPacket(new AddColumnResponsePacket(false));
        }
    }
}