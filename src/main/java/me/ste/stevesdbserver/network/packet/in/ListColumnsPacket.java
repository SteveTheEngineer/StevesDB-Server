package me.ste.stevesdbserver.network.packet.in;

import me.ste.stevesdbserver.database.DatabaseManager;
import me.ste.stevesdbserver.database.TableColumn;
import me.ste.stevesdbserver.network.Connection;
import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketIn;
import me.ste.stevesdbserver.network.packet.out.ListColumnsResponsePacket;
import me.ste.stevesdbserver.util.DataReader;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@PacketId(13)
public class ListColumnsPacket extends PacketIn {
    private String database;
    private String table;

    @Override
    public void deserialize(DataReader reader) {
        this.database = reader.readString();
        this.table = reader.readString();
    }

    @Override
    public void handle(Connection connection) {
        if(DatabaseManager.getInstance().hasPermission(connection.getUsername(), "list_columns", this.database, this.table)) {
            connection.sendPacket(DatabaseManager.getInstance().doAction(this.database, this.table, false, table -> {
                if(table == null) {
                    return new ListColumnsResponsePacket(false, Collections.emptyMap());
                }
                return new ListColumnsResponsePacket(true, table.getColumns());
            }));
        } else {
            connection.sendPacket(new ListColumnsResponsePacket(false, Collections.emptyMap()));
        }
    }
}