package me.ste.stevesdbserver.network.packet.in;

import me.ste.stevesdbserver.database.DatabaseManager;
import me.ste.stevesdbserver.network.Connection;
import me.ste.stevesdbserver.network.packet.PacketId;
import me.ste.stevesdbserver.network.packet.PacketIn;
import me.ste.stevesdbserver.network.packet.out.CreateTableResponsePacket;
import me.ste.stevesdbserver.util.DataReader;

@PacketId(10)
public class CreateTablePacket extends PacketIn {
    private String database;
    private String name;

    @Override
    public void deserialize(DataReader reader) {
        this.database = reader.readString();
        this.name = reader.readString();
    }

    @Override
    public void handle(Connection connection) {
        if(DatabaseManager.getInstance().hasPermission(connection.getUsername(), "create_table", this.database)) {
            connection.sendPacket(new CreateTableResponsePacket(DatabaseManager.getInstance().createTable(this.database, this.name)));
        } else {
            connection.sendPacket(new CreateTableResponsePacket(false));
        }
    }
}