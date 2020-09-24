package me.ste.stevesdbserver.network.packet.in;

import me.ste.stevesdbserver.StevesDBServer;
import me.ste.stevesdbserver.network.Connection;
import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketIn;
import me.ste.stevesdbserver.util.DataReader;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@PacketId(2)
public class EncryptionEnabledPacket extends PacketIn {
    private byte[] encryptionSecret;

    @Override
    public void deserialize(DataReader reader) {
        this.encryptionSecret = reader.readByteArray();

    }

    @Override
    public void handle(Connection connection) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");

            cipher.init(Cipher.DECRYPT_MODE, StevesDBServer.getInstance().getKeyPair().getPrivate());
            connection.setEncryptionSecret(cipher.doFinal(this.encryptionSecret));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }
}