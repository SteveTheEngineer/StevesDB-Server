package me.ste.stevesdbserver.util;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class DataReader {
    private final DataInputStream stream;

    public DataReader(DataInputStream stream) {
        this.stream = stream;
    }

    public DataReader(InputStream stream) {
        this.stream = new DataInputStream(stream);
    }
    
    public boolean readBoolean() {
        try {
            return this.stream.readBoolean();
        } catch (IOException e) {
            return false;
        }
    }

    public byte readByte() {
        try {
            return this.stream.readByte();
        } catch (IOException e) {
            return 0;
        }
    }

    public int readUnsignedByte() {
        try {
            return this.stream.readUnsignedByte();
        } catch (IOException e) {
            return 0;
        }
    }

    public short readShort() {
        try {
            return this.stream.readShort();
        } catch (IOException e) {
            return 0;
        }
    }

    public int readUnsignedShort() {
        try {
            return this.stream.readUnsignedShort();
        } catch (IOException e) {
            return 0;
        }
    }

    public int readInt() {
        try {
            return this.stream.readInt();
        } catch (IOException e) {
            return 0;
        }
    }

    public long readLong() {
        try {
            return this.stream.readLong();
        } catch(IOException e) {
            return 0;
        }
    }

    public float readFloat() {
        try {
            return this.stream.readFloat();
        } catch(IOException e) {
            return 0;
        }
    }

    public double readDouble() {
        try {
            return this.stream.readDouble();
        } catch(IOException e) {
            return 0;
        }
    }

    public String readString() {
        return new String(this.readByteArray(), StandardCharsets.UTF_8);
    }

    public byte[] readBytes(int length) {
        byte[] bytes = new byte[length];
        for(int i = 0; i < length; i++) {
            bytes[i] = this.readByte();
        }
        return bytes;
    }

    public int[] readUnsignedBytes(int length) {
        int[] bytes = new int[length];
        for(int i = 0; i < length; i++) {
            bytes[i] = this.readUnsignedByte();
        }
        return bytes;
    }

    public byte[] readByteArray() {
        return this.readBytes(this.readInt());
    }

    public String[] readStringArray() {
        int length = this.readInt();
        String[] array = new String[length];
        for(int i = 0; i < length; i++) {
            array[i] = this.readString();
        }
        return array;
    }

    public int[] readUnsignedByteArray() {
        return this.readUnsignedBytes(this.readInt());
    }

    public int available() {
        try {
            return this.stream.available();
        } catch (IOException e) {
            return -1;
        }
    }
}