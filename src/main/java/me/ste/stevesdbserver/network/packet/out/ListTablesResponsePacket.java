package me.ste.stevesdbserver.network.packet.out;

import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketOut;
import me.ste.stevesdbserver.util.DataWriter;

import java.util.Collection;

@PacketId(5)
public class ListTablesResponsePacket extends PacketOut {
    private final boolean success;
    private final Collection<String> tables;

    public ListTablesResponsePacket(boolean success, Collection<String> tables) {
        this.success = success;
        this.tables = tables;
    }

    @Override
    public void serialize(DataWriter writer) {
        writer.writeBoolean(this.success);
        writer.writeInt(this.tables.size());
        for(String s : this.tables) {
            writer.writeString(s);
        }
    }
}