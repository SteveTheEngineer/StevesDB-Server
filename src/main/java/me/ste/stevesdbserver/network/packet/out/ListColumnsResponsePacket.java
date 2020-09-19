package me.ste.stevesdbserver.network.packet.out;

import me.ste.stevesdbserver.database.TableColumn;
import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketOut;
import me.ste.stevesdbserver.util.DataWriter;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

@PacketId(12)
public class ListColumnsResponsePacket extends PacketOut {
    private final boolean success;
    private final Map<Integer, TableColumn> columns;

    public ListColumnsResponsePacket(boolean success, Map<Integer, TableColumn> columns) {
        this.success = success;
        this.columns = new TreeMap<>(Comparator.comparingInt(a -> a));
        for(Map.Entry<Integer, TableColumn> entry : columns.entrySet()) {
            this.columns.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void serialize(DataWriter writer) {
        writer.writeBoolean(this.success);
        writer.writeInt(this.columns.size());
        for(TableColumn tc : this.columns.values()) {
            writer.writeUnsignedByte(tc.getType().ordinal());
            writer.writeString(tc.getName());
        }
    }
}