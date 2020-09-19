package me.ste.stevesdbserver.util;

public class EntryValueModifier {
    private final EntryValueOperation operation;
    private final String value;

    public EntryValueModifier(EntryValueOperation operation, String value) {
        this.operation = operation;
        this.value = value;
    }

    public EntryValueOperation getOperation() {
        return this.operation;
    }

    public String getValue() {
        return this.value;
    }
}