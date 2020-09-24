package me.ste.stevesdbserver.network.packet.out;

import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketOut;
import me.ste.stevesdbserver.util.DataWriter;

@PacketId(18)
public class ModifyEntryResponsePacket extends PacketOut {
    private final boolean success;
    private final int modified;

    public ModifyEntryResponsePacket(boolean success, int modified) {
        this.success = success;
        this.modified = modified;
    }

    @Override
    public void serialize(DataWriter writer) {
        writer.writeBoolean(this.success);
        writer.writeInt(this.modified);
    }
}