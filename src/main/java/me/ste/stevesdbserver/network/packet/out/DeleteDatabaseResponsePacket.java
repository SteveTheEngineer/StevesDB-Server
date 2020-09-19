package me.ste.stevesdbserver.network.packet.out;

import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketOut;
import me.ste.stevesdbserver.util.DataWriter;

@PacketId(8)
public class DeleteDatabaseResponsePacket extends PacketOut {
    private final boolean success;

    public DeleteDatabaseResponsePacket(boolean success) {
        this.success = success;
    }

    @Override
    public void serialize(DataWriter writer) {
        writer.writeBoolean(this.success);
    }
}