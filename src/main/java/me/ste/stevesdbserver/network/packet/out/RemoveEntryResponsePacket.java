package me.ste.stevesdbserver.network.packet.out;

import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketOut;
import me.ste.stevesdbserver.util.DataWriter;

@PacketId(19)
public class RemoveEntryResponsePacket extends PacketOut {
    private boolean success;
    private int removed;

    public RemoveEntryResponsePacket(boolean success, int removed) {
        this.success = success;
        this.removed = removed;
    }

    @Override
    public void serialize(DataWriter writer) {
        writer.writeBoolean(this.success);
        writer.writeInt(this.removed);
    }
}