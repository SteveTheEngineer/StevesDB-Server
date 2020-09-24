package me.ste.stevesdbserver.network.packet.out;

import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketOut;
import me.ste.stevesdbserver.util.DataWriter;

@PacketId(17)
public class AddEntryResponsePacket extends PacketOut {
    private final boolean success;

    public AddEntryResponsePacket(boolean success) {
        this.success = success;
    }

    @Override
    public void serialize(DataWriter writer) {
        writer.writeBoolean(this.success);
    }
}