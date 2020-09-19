package me.ste.stevesdbserver.network.packet.out;

import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketOut;
import me.ste.stevesdbserver.util.DataWriter;

@PacketId(6)
public class CreateDatabaseResponsePacket extends PacketOut {
    private final boolean success;

    public CreateDatabaseResponsePacket(boolean success) {
        this.success = success;
    }

    @Override
    public void serialize(DataWriter writer) {
        writer.writeBoolean(this.success);
    }
}