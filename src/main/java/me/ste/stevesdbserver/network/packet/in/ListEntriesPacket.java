package me.ste.stevesdbserver.network.packet.in;

import me.ste.stevesdbserver.database.DatabaseManager;
import me.ste.stevesdbserver.database.TableColumn;
import me.ste.stevesdbserver.network.Connection;
import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketIn;
import me.ste.stevesdbserver.network.packet.out.ListEntriesResponsePacket;
import me.ste.stevesdbserver.util.ComparatorOperation;
import me.ste.stevesdbserver.util.DataReader;
import me.ste.stevesdbserver.util.EntryFilter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@PacketId(17)
public class ListEntriesPacket extends PacketIn {
    private String database;
    private String table;
    private int start;
    private int end;
    private Map<String, EntryFilter> filters = new HashMap<>();

    @Override
    public void deserialize(DataReader reader) {
        this.database = reader.readString();
        this.table = reader.readString();
        this.start = reader.readInt();
        this.end = reader.readInt();

        int length = reader.readInt();
        for(int i = 0; i < length; i++) {
            this.filters.put(reader.readString(), new EntryFilter(ComparatorOperation.values()[Math.max(0, Math.min(ComparatorOperation.values().length - 1, reader.readUnsignedByte()))], reader.readString()));
        }
    }

    @Override
    public void handle(Connection connection) {
        if(DatabaseManager.getInstance().hasPermission(connection.getUsername(), "list_entries", this.database, this.table)) {
            connection.sendPacket(DatabaseManager.getInstance().doAction(this.database, this.table, false, table -> {
                if(table == null) {
                    return new ListEntriesResponsePacket(false, Collections.emptyMap());
                }
                Map<Integer, EntryFilter> cidFilters = new HashMap<>();
                for(Map.Entry<String, EntryFilter> filter : this.filters.entrySet()) {
                    for(Map.Entry<Integer, TableColumn> column : table.getColumns().entrySet()) {
                        if(column.getValue().getName().equals(filter.getKey())) {
                            cidFilters.put(column.getKey(), filter.getValue());
                        }
                    }
                }
                return new ListEntriesResponsePacket(true, EntryFilter.removeUnmatchedEntries(table.getClonedEntries(), cidFilters), this.start, this.end);
            }));
        } else {
            connection.sendPacket(new ListEntriesResponsePacket(false, Collections.emptyMap()));
        }
    }
}