package me.ste.stevesdbserver.network.packet.out;

import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketOut;
import me.ste.stevesdbserver.util.DataWriter;

@PacketId(0)
public class HelloResponsePacket extends PacketOut {
    private int protocolVersion;
    private String serverVersion;

    public HelloResponsePacket(int protocolVersion, String serverVersion) {
        this.protocolVersion = protocolVersion;
        this.serverVersion = serverVersion;
    }

    @Override
    public void serialize(DataWriter writer) {
        writer.writeInt(this.protocolVersion);
        writer.writeString(this.serverVersion);
    }
}