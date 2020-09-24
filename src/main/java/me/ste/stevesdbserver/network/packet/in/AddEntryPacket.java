package me.ste.stevesdbserver.network.packet.in;

import me.ste.stevesdbserver.database.DatabaseManager;
import me.ste.stevesdbserver.database.Table;
import me.ste.stevesdbserver.database.TableColumn;
import me.ste.stevesdbserver.database.TableColumnType;
import me.ste.stevesdbserver.network.Connection;
import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketIn;
import me.ste.stevesdbserver.network.packet.out.AddEntryResponsePacket;
import me.ste.stevesdbserver.util.DataReader;

import java.util.HashMap;
import java.util.Map;

@PacketId(18)
public class AddEntryPacket extends PacketIn {
    private String database;
    private String table;
    private Map<String, String> values = new HashMap<>();

    @Override
    public void deserialize(DataReader reader) {
        this.database = reader.readString();
        this.table = reader.readString();

        int length = reader.readInt();
        for(int i = 0; i < length; i++) {
            this.values.put(reader.readString(), reader.readString());
        }
    }

    @Override
    public void handle(Connection connection) {
        if(DatabaseManager.getInstance().hasPermission(connection.getUsername(), "add_entry", this.database, this.table)) {
            connection.sendPacket(new AddEntryResponsePacket(DatabaseManager.getInstance().doAction(this.database, this.table, true, table -> {
                if(table != null) {
                    Map<Integer, Object> entry = new HashMap<>();
                    for(Map.Entry<Integer, TableColumn> column : table.getColumns().entrySet()) {
                        String strValue = this.values.get(column.getValue().getName());
                        Object value = null;
                        TableColumnType type = column.getValue().getType();
                        if(strValue != null) {
                            if(type == TableColumnType.BOOLEAN) {
                                value = strValue.equals("true");
                            } else if(type == TableColumnType.DOUBLE) {
                                try {
                                    value = Double.parseDouble(strValue);
                                } catch(NumberFormatException e) {
                                    value = 0D;
                                }
                            } else if(type == TableColumnType.INTEGER) {
                                try {
                                    value = Integer.parseInt(strValue);
                                } catch(NumberFormatException e) {
                                    value = 0;
                                }
                            } else if(type == TableColumnType.LONG) {
                                try {
                                    value = Long.parseLong(strValue);
                                } catch(NumberFormatException e) {
                                    value = 0L;
                                }
                            } else if(type == TableColumnType.STRING) {
                                value = strValue;
                            }
                        } else {
                            if(type == TableColumnType.BOOLEAN) {
                                value = false;
                            } else if(type == TableColumnType.DOUBLE) {
                                value = 0D;
                            } else if(type == TableColumnType.INTEGER) {
                                value = 0;
                            } else if(type == TableColumnType.LONG) {
                                value = 0L;
                            } else if(type == TableColumnType.STRING) {
                                value = "";
                            }
                        }
                        entry.put(column.getKey(), value);
                    }
                    table.addEntry(entry);
                    return true;
                } else {
                    return false;
                }
            })));
        } else {
            connection.sendPacket(new AddEntryResponsePacket(false));
        }
    }
}