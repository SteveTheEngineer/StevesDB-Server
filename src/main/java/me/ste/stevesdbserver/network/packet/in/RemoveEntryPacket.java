package me.ste.stevesdbserver.network.packet.in;

import me.ste.stevesdbserver.database.DatabaseManager;
import me.ste.stevesdbserver.database.TableColumn;
import me.ste.stevesdbserver.network.Connection;
import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketIn;
import me.ste.stevesdbserver.network.packet.out.RemoveEntryResponsePacket;
import me.ste.stevesdbserver.util.ComparatorOperation;
import me.ste.stevesdbserver.util.DataReader;
import me.ste.stevesdbserver.util.EntryFilter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@PacketId(20)
public class RemoveEntryPacket extends PacketIn {
    private String database;
    private String table;
    private Map<String, EntryFilter> filters = new HashMap<>();
    private int startIndex;
    private int endIndex;

    @Override
    public void deserialize(DataReader reader) {
        this.database = reader.readString();
        this.table = reader.readString();

        int length = reader.readInt();
        for(int i = 0; i < length; i++) {
            this.filters.put(reader.readString(), new EntryFilter(ComparatorOperation.values()[Math.max(0, Math.min(ComparatorOperation.values().length - 1, reader.readUnsignedByte()))], reader.readString()));
        }

        this.startIndex = reader.readInt();
        this.endIndex = reader.readInt();
    }

    @Override
    public void handle(Connection connection) {
        if(DatabaseManager.getInstance().hasPermission(connection.getUsername(), "remove_entry", this.database, this.table)) {
            connection.sendPacket(DatabaseManager.getInstance().doAction(this.database, this.table, true, table -> {
                if(table != null) {
                    Map<Integer, EntryFilter> cidFilters = new HashMap<>();
                    for(Map.Entry<String, EntryFilter> filter : this.filters.entrySet()) {
                        for(Map.Entry<Integer, TableColumn> column : table.getColumns().entrySet()) {
                            if(column.getValue().getName().equals(filter.getKey())) {
                                cidFilters.put(column.getKey(), filter.getValue());
                            }
                        }
                    }
                    AtomicInteger i = new AtomicInteger(0);
                    AtomicInteger modified = new AtomicInteger(0);
                    table.getEntries().entrySet().removeIf(entry -> {
                        if(EntryFilter.entryMatchesFilters(entry.getValue(), cidFilters)) {
                            int index = i.getAndIncrement();
                            if(index >= this.startIndex && index <= this.endIndex) {
                                modified.getAndIncrement();
                                return true;
                            }
                        }
                        return false;
                    });
                    return new RemoveEntryResponsePacket(true, modified.get());
                } else {
                    return new RemoveEntryResponsePacket(false, 0);
                }
            }));
        } else {
            connection.sendPacket(new RemoveEntryResponsePacket(false, 0));
        }
    }
}