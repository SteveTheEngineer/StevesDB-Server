package me.ste.stevesdbserver.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class DataWriter {
    private final DataOutputStream stream;

    public DataWriter(DataOutputStream stream) {
        this.stream = stream;
    }

    public DataWriter(OutputStream stream) {
        this.stream = new DataOutputStream(stream);
    }

    public DataWriter() {
        this(new ByteArrayOutputStream());
    }

    public DataWriter writeBoolean(boolean value) {
        try {
            this.stream.writeBoolean(value);
        } catch (IOException ignored) {}
        return this;
    }

    public DataWriter writeByte(byte value) {
        try {
            this.stream.writeByte(value);
        } catch (IOException ignored) {}
        return this;
    }

    public DataWriter writeUnsignedByte(int value) {
        try {
            this.stream.writeByte((byte) Math.max(0, Math.min(255, value)));
        } catch (IOException ignored) {}
        return this;
    }

    public DataWriter writeShort(short value) {
        try {
            this.stream.writeShort(value);
        } catch (IOException ignored) {}
        return this;
    }

    public DataWriter writeUnsignedShort(int value) {
        try {
            this.stream.writeShort((short) Math.max(0, Math.min(65535, value)));
        } catch (IOException ignored) {}
        return this;
    }

    public DataWriter writeInt(int value) {
        try {
            this.stream.writeInt(value);
        } catch (IOException ignored) {}
        return this;
    }

    public DataWriter writeLong(long value) {
        try {
            this.stream.writeLong(value);
        } catch(IOException ignored) {}
        return this;
    }

    public DataWriter writeFloat(float value) {
        try {
            this.stream.writeFloat(value);
        } catch(IOException ignored) {}
        return this;
    }

    public DataWriter writeDouble(double value) {
        try {
            this.stream.writeDouble(value);
        } catch(IOException ignored) {}
        return this;
    }

    public DataWriter writeString(String value) {
        try {
            byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
            this.stream.writeInt(bytes.length);
            this.stream.write(bytes);
        } catch(IOException ignored) {}
        return this;
    }

    public DataWriter writeBytes(byte[] value) {
        try {
            this.stream.write(value);
        } catch (IOException ignored) {}
        return this;
    }

    public DataWriter writeUnsignedBytes(int[] value) {
        for(int b : value) {
            this.writeUnsignedByte(b);
        }
        return this;
    }

    public DataWriter writeByteArray(byte[] value) {
        this.writeInt(value.length);
        this.writeBytes(value);
        return this;
    }

    public DataWriter writeStringArray(String[] value) {
        this.writeInt(value.length);
        for(String string : value) {
            this.writeString(string);
        }
        return this;
    }

    public DataWriter writeUnsignedByteArray(int[] value) {
        this.writeInt(value.length);
        this.writeUnsignedBytes(value);
        return this;
    }

    public OutputStream getOutputStream() {
        return this.stream;
    }
}