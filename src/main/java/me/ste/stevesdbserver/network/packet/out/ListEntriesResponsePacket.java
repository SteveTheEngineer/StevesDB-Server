package me.ste.stevesdbserver.network.packet.out;

import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketOut;
import me.ste.stevesdbserver.util.DataWriter;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

@PacketId(16)
public class ListEntriesResponsePacket extends PacketOut {
    private final boolean success;
    private final int total;
    private final Map<Integer, Map<Integer, Object>> entries;

    public ListEntriesResponsePacket(boolean success, Map<Integer, Map<Integer, Object>> entries, int min, int max) {
        this.success = success;
        this.total = entries.size();
        this.entries = new TreeMap<>(Comparator.comparingInt(a -> a));
        for(Map.Entry<Integer, Map<Integer, Object>> entry : entries.entrySet()) {
            if(entry.getKey() >= min && entry.getKey() <= max) {
                Map<Integer, Object> values = new TreeMap<>(Comparator.comparingInt(a -> a));
                for(Map.Entry<Integer, Object> entry2 : entry.getValue().entrySet()) {
                    values.put(entry2.getKey(), entry2.getValue());
                }
                this.entries.put(entry.getKey(), values);
            }
        }
    }

    public ListEntriesResponsePacket(boolean success, Map<Integer, Map<Integer, Object>> entries) {
        this(success, entries, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public void serialize(DataWriter writer) {
        writer.writeBoolean(this.success);
        writer.writeInt(this.total);
        writer.writeInt(this.entries.size());
        for(Map.Entry<Integer, Map<Integer, Object>> entry : this.entries.entrySet()) {
            writer.writeInt(entry.getValue().size());
            for(Object value : entry.getValue().values()) {
                writer.writeString(String.valueOf(value));
            }
        }
    }
}