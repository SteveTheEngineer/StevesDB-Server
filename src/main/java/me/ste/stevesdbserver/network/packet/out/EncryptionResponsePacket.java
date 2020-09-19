package me.ste.stevesdbserver.network.packet.out;

import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketOut;
import me.ste.stevesdbserver.util.DataWriter;

import java.security.PublicKey;

@PacketId(1)
public class EncryptionResponsePacket extends PacketOut {
    private PublicKey publicKey;

    public EncryptionResponsePacket(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public void serialize(DataWriter writer) {
        writer.writeByteArray(this.publicKey.getEncoded());
    }
}