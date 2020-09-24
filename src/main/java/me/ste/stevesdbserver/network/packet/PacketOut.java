package me.ste.stevesdbserver.network.packet;

import me.ste.stevesdbserver.util.DataWriter;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class PacketOut {
    public abstract void serialize(DataWriter writer);
}