package me.ste.stevesdbserver.network.packet;

import me.ste.stevesdbserver.network.Connection;
import me.ste.stevesdbserver.util.DataReader;

public abstract class PacketIn {
    public abstract void deserialize(DataReader reader);
    public abstract void handle(Connection connection);
}