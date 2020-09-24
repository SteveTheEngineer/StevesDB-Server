package me.ste.stevesdbserver.network.packet.in;

import me.ste.stevesdbserver.database.DatabaseManager;
import me.ste.stevesdbserver.database.TableColumn;
import me.ste.stevesdbserver.database.TableColumnType;
import me.ste.stevesdbserver.network.Connection;
import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketIn;
import me.ste.stevesdbserver.network.packet.out.ModifyEntryResponsePacket;
import me.ste.stevesdbserver.util.*;

import java.util.HashMap;
import java.util.Map;

@PacketId(19)
public class ModifyEntryPacket extends PacketIn {
    private String database;
    private String table;
    private Map<String, EntryFilter> filters = new HashMap<>();
    private int startIndex;
    private int endIndex;
    private Map<String, EntryValueModifier> values = new HashMap<>();

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

        int length2 = reader.readInt();
        for(int i = 0; i < length2; i++) {
            this.values.put(reader.readString(), new EntryValueModifier(EntryValueOperation.values()[Math.max(0, Math.min(EntryValueOperation.values().length - 1, reader.readUnsignedByte()))], reader.readString()));
        }
    }

    @Override
    public void handle(Connection connection) {
        if(DatabaseManager.getInstance().hasPermission(connection.getUsername(), "modify_entry", this.database, this.table)) {
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
                    int modified = 0;
                    int i = 0;
                    for(Map.Entry<Integer, Map<Integer, Object>> entry : table.getEntries().entrySet()) {
                        if(EntryFilter.entryMatchesFilters(entry.getValue(), cidFilters)) {
                            int index = i++;
                            if(index >= this.startIndex && index <= this.endIndex) {
                                for(Map.Entry<Integer, TableColumn> column : table.getColumns().entrySet()) {
                                    if(!this.values.containsKey(column.getValue().getName())) {
                                        continue;
                                    }
                                    EntryValueModifier modifier = this.values.get(column.getValue().getName());
                                    Object oldValue = table.getEntries().get(entry.getKey()).get(column.getKey());
                                    Object value = oldValue;
                                    TableColumnType type = column.getValue().getType();
                                    if(type == TableColumnType.BOOLEAN) {
                                        value = modifier.getValue().equals("true");
                                    } else if(type == TableColumnType.DOUBLE) {
                                        try {
                                            double newValue = Double.parseDouble(modifier.getValue());
                                            if(modifier.getOperation() == EntryValueOperation.SET) {
                                                value = newValue;
                                            } else if(modifier.getOperation() == EntryValueOperation.ADD) {
                                                value = ((double) oldValue) + newValue;
                                            } else if(modifier.getOperation() == EntryValueOperation.SUBTRACT) {
                                                value = ((double) oldValue) - newValue;
                                            }
                                        } catch(NumberFormatException ignored) {}
                                    } else if(type == TableColumnType.INTEGER) {
                                        try {
                                            int newValue = Integer.parseInt(modifier.getValue());
                                            if(modifier.getOperation() == EntryValueOperation.SET) {
                                                value = newValue;
                                            } else if(modifier.getOperation() == EntryValueOperation.ADD) {
                                                value = ((int) oldValue) + newValue;
                                            } else if(modifier.getOperation() == EntryValueOperation.SUBTRACT) {
                                                value = ((int) oldValue) - newValue;
                                            }
                                        } catch(NumberFormatException ignored) {}
                                    } else if(type == TableColumnType.LONG) {
                                        try {
                                            long newValue = Long.parseLong(modifier.getValue());
                                            if(modifier.getOperation() == EntryValueOperation.SET) {
                                                value = newValue;
                                            } else if(modifier.getOperation() == EntryValueOperation.ADD) {
                                                value = ((long) oldValue) + newValue;
                                            } else if(modifier.getOperation() == EntryValueOperation.SUBTRACT) {
                                                value = ((long) oldValue) - newValue;
                                            }
                                        } catch(NumberFormatException ignored) {}
                                    } else if(type == TableColumnType.STRING) {
                                        try {
                                            String s = modifier.getValue();
                                            if(modifier.getOperation() == EntryValueOperation.SET) {
                                                value = s;
                                            } else if(modifier.getOperation() == EntryValueOperation.ADD) {
                                                value = oldValue + s;
                                            } else if(modifier.getOperation() == EntryValueOperation.SUBTRACT) {
                                                value = s + oldValue    ;
                                            }
                                        } catch(NumberFormatException ignored) {}
                                    }
                                    table.getEntries().get(entry.getKey()).put(column.getKey(), value);
                                }
                                modified++;
                            }
                        }
                    }
                    return new ModifyEntryResponsePacket(true, modified);
                } else {
                    return new ModifyEntryResponsePacket(true, 0);
                }
            }));
        } else {
            connection.sendPacket(new ModifyEntryResponsePacket(false, 0));
        }
    }
}