package me.ste.stevesdbserver.network.packet.in;

import me.ste.stevesdbserver.database.DatabaseManager;
import me.ste.stevesdbserver.database.UserData;
import me.ste.stevesdbserver.network.Connection;
import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketIn;
import me.ste.stevesdbserver.network.packet.out.LoginResponsePacket;
import me.ste.stevesdbserver.util.DataReader;

@PacketId(3)
public class LoginPacket extends PacketIn {
    private String username;
    private String password;

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public void deserialize(DataReader reader) {
        this.username = reader.readString();
        this.password = reader.readString();
    }

    @Override
    public void handle(Connection connection) {
        UserData userData = DatabaseManager.getInstance().getUserData(this.username);
        boolean success = userData != null && userData.verifyPassword(this.password);
        if(success) {
            connection.setUsername(this.username);
        }
        connection.sendPacket(new LoginResponsePacket(success));
    }
}