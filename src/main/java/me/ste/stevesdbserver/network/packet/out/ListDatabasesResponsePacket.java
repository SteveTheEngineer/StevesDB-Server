package me.ste.stevesdbserver.network.packet.out;

import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketOut;
import me.ste.stevesdbserver.util.DataWriter;

import java.util.Collection;

@PacketId(4)
public class ListDatabasesResponsePacket extends PacketOut {
    private final boolean success;
    private final Collection<String> databases;

    public ListDatabasesResponsePacket(boolean success, Collection<String> databases) {
        this.success = success;
        this.databases = databases;
    }

    @Override
    public void serialize(DataWriter writer) {
        writer.writeBoolean(this.success);
        writer.writeInt(this.databases.size());
        for(String s : this.databases) {
            writer.writeString(s);
        }
    }
}