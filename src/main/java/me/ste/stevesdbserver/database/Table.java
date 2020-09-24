package me.ste.stevesdbserver.database;

import me.ste.stevesdbserver.util.DataReader;
import me.ste.stevesdbserver.util.DataWriter;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class Table {
    private Map<Integer, TableColumn> columns = new TreeMap<>(Comparator.comparingInt(a -> a));
    private Map<Integer, Map<Integer, Object>> entries = new TreeMap<>(Comparator.comparingInt(a -> a));

    public void load(DataReader reader) {
        int columnAmount = reader.readInt();
        for(int i = 0; i < columnAmount; i++) {
            this.columns.put(i, new TableColumn(TableColumnType.values()[reader.readUnsignedByte()], reader.readString()));
        }
        int index = 0;
        while(reader.available() > 0) {
            Map<Integer, Object> values = new TreeMap<>(Comparator.comparingInt(a -> a));
            for(int i = 0; i < columnAmount; i++) {
                Object value = null;
                TableColumnType type = this.columns.get(i).getType();
                if(type == TableColumnType.BOOLEAN) {
                    value = reader.readBoolean();
                } else if(type == TableColumnType.DOUBLE) {
                    value = reader.readDouble();
                } else if(type == TableColumnType.INTEGER) {
                    value = reader.readInt();
                } else if(type == TableColumnType.LONG) {
                    value = reader.readLong();
                } else if(type == TableColumnType.STRING) {
                    value = reader.readString();
                }
                values.put(i, value);
            }
            this.entries.put(index, values);
            index++;
        }
    }

    public void save(DataWriter writer) {
        writer.writeInt(this.columns.size());
        for(TableColumn column : this.columns.values()) {
            writer.writeUnsignedByte(column.getType().ordinal());
            writer.writeString(column.getName());
        }
        for(Map<Integer, Object> entry : this.entries.values()) {
            for(int key : this.columns.keySet()) {
                TableColumn column = this.columns.get(key);
                if(column.getType() == TableColumnType.BOOLEAN) {
                    writer.writeBoolean((boolean) entry.getOrDefault(key, false));
                } else if(column.getType() == TableColumnType.INTEGER) {
                    writer.writeInt((int) entry.getOrDefault(key, 0));
                } else if(column.getType() == TableColumnType.LONG) {
                    writer.writeLong((long) entry.getOrDefault(key, 0));
                } else if(column.getType() == TableColumnType.DOUBLE) {
                    writer.writeDouble((double) entry.getOrDefault(key, 0));
                } else if(column.getType() == TableColumnType.STRING) {
                    writer.writeString((String) entry.getOrDefault(key, ""));
                }
            }
        }
    }

    public Map<Integer, Map<Integer, Object>> getEntries() {
        return this.entries;
    }

    public Map<Integer, Map<Integer, Object>> getClonedEntries() {
        Map<Integer, Map<Integer, Object>> cloned = new TreeMap<>(Comparator.comparingInt(a -> a));
        for(Map.Entry<Integer, Map<Integer, Object>> entry : entries.entrySet()) {
            Map<Integer, Object> values = new TreeMap<>(Comparator.comparingInt(a -> a));
            for(Map.Entry<Integer, Object> entry2 : entry.getValue().entrySet()) {
                values.put(entry2.getKey(), entry2.getValue());
            }
            cloned.put(entry.getKey(), values);
        }
        return cloned;
    }

    public Map<Integer, TableColumn> getColumns() {
        return this.columns;
    }

    public void addEntry(Map<Integer, Object> entry) {
        this.entries.put(this.entries.size(), entry);
    }

    public void removeEntry(int index) {
        LinkedHashMap<Integer, Map<Integer, Object>> oldEntries = new LinkedHashMap<>(this.entries);
        oldEntries.remove(index);
        this.entries.clear();
        int newIndex = 0;
        for(Map.Entry<Integer, Map<Integer, Object>> s : oldEntries.entrySet()) {
            this.entries.put(newIndex, s.getValue());
            newIndex++;
        }
    }

    public Map<Integer, Object> getEntry(int index) {
        return this.entries.get(index);
    }

    public int getColumnId(String name) {
        for(int cid : this.columns.keySet()) {
            if(this.columns.get(cid).getName().equals(name)) {
                return cid;
            }
        }
        return -1;
    }

    public int addColumn(TableColumnType type, String name) {
        for(TableColumn col : this.columns.values()) {
            if(col.getName().equals(name)) {
                return -1;
            }
        }
        int cid = this.columns.size();
        this.columns.put(cid, new TableColumn(type, name));
        for(int index : this.entries.keySet()) {
            Object value = null;
            if(type == TableColumnType.BOOLEAN) {
                value = false;
            } else if(type == TableColumnType.INTEGER) {
                value = 0;
            } else if(type == TableColumnType.DOUBLE) {
                value = 0;
            } else if(type == TableColumnType.LONG) {
                value = 0;
            } else if(type == TableColumnType.STRING) {
                value = "";
            }
            this.entries.get(index).put(cid, value);
        }
        return cid;
    }

    public void removeColumn(int cid) {
        if(this.columns.containsKey(cid)) {
            Map<Integer, TableColumn> newColumns = new LinkedHashMap<>(this.columns);
            newColumns.remove(cid);
            this.columns.clear();
            int newCid = 0;
            for(TableColumn tc : newColumns.values()) {
                this.columns.put(newCid, tc);
                newCid++;
            }
            for(int index : this.entries.keySet()) {
                Map<Integer, Object> newValues = new LinkedHashMap<>(this.entries.get(index));
                newValues.remove(cid);
                int newCid2 = 0;
                for(Object value : newValues.values()) {
                    this.entries.get(index).put(newCid2, value);
                    newCid2++;
                }
            }
        }
    }
}