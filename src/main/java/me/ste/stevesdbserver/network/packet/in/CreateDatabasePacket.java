package me.ste.stevesdbserver.network.packet.in;

import me.ste.stevesdbserver.database.DatabaseManager;
import me.ste.stevesdbserver.network.Connection;
import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketIn;
import me.ste.stevesdbserver.network.packet.out.CreateDatabaseResponsePacket;
import me.ste.stevesdbserver.util.DataReader;

@PacketId(7)
public class CreateDatabasePacket extends PacketIn {
    private String name;

    @Override
    public void deserialize(DataReader reader) {
        this.name = reader.readString();
    }

    @Override
    public void handle(Connection connection) {
        if(DatabaseManager.getInstance().hasPermission(connection.getUsername(), "create_database")) {
            connection.sendPacket(new CreateDatabaseResponsePacket(DatabaseManager.getInstance().createDatabase(this.name)));
        } else {
            connection.sendPacket(new CreateDatabaseResponsePacket(false));
        }
    }
}