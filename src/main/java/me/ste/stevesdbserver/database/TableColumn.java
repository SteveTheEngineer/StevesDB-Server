package me.ste.stevesdbserver.database;

public class TableColumn {
    private final TableColumnType type;
    private String name;

    public TableColumn(TableColumnType type, String name) {
        this.type = type;
        this.name = name;
    }

    public TableColumnType getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}