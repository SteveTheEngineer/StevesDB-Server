package me.ste.stevesdbserver.database;

import me.ste.stevesdbserver.StevesDBServer;
import me.ste.stevesdbserver.util.DataReader;
import me.ste.stevesdbserver.util.DataWriter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

public class DatabaseManager {
    private static DatabaseManager instance;
    private final Path dataDirectory = Paths.get("data");

    private final Map<String, Map<String, Table>> cachedTables = new HashMap<>();

    private final boolean cacheTablesInMemory;

    private DatabaseManager(boolean cacheTablesInMemory) {
        this.cacheTablesInMemory = cacheTablesInMemory;

        if(!Files.isDirectory(this.dataDirectory)) {
            try {
                Files.createDirectory(this.dataDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(!this.getDatabases().contains("stevesdb")) {
            this.createDatabase("stevesdb");
            this.createTable("stevesdb", "users");
            this.createTable("stevesdb", "groups");
            this.createTable("stevesdb", "global_permissions");
            this.createTable("stevesdb", "database_permissions");
            this.createTable("stevesdb", "table_permissions");
            this.doAction("stevesdb", "users", true, table -> {
                table.addColumn(TableColumnType.STRING, "username");
                table.addColumn(TableColumnType.STRING, "password");
                table.addColumn(TableColumnType.STRING, "password_salt");
            });
            this.doAction("stevesdb", "groups", true, table -> {
                table.addColumn(TableColumnType.STRING, "username");
                table.addColumn(TableColumnType.STRING, "group");

                Map<Integer, Object> entry = new HashMap<>();
                entry.put(0, "stevesdb");
                entry.put(1, "admin");
                table.addEntry(entry);
            });
            this.doAction("stevesdb", "global_permissions", true, table -> {
                table.addColumn(TableColumnType.STRING, "group");
                table.addColumn(TableColumnType.BOOLEAN, "list_databases");
                table.addColumn(TableColumnType.BOOLEAN, "create_database");
                table.addColumn(TableColumnType.BOOLEAN, "rename_database");
                table.addColumn(TableColumnType.BOOLEAN, "delete_database");
                table.addColumn(TableColumnType.BOOLEAN, "list_tables");
                table.addColumn(TableColumnType.BOOLEAN, "create_table");
                table.addColumn(TableColumnType.BOOLEAN, "rename_table");
                table.addColumn(TableColumnType.BOOLEAN, "delete_table");
                table.addColumn(TableColumnType.BOOLEAN, "list_columns");
                table.addColumn(TableColumnType.BOOLEAN, "add_column");
                table.addColumn(TableColumnType.BOOLEAN, "rename_column");
                table.addColumn(TableColumnType.BOOLEAN, "remove_column");
                table.addColumn(TableColumnType.BOOLEAN, "list_entries");
                table.addColumn(TableColumnType.BOOLEAN, "add_entry");
                table.addColumn(TableColumnType.BOOLEAN, "modify_entry");
                table.addColumn(TableColumnType.BOOLEAN, "remove_entry");

                Map<Integer, Object> entry = new HashMap<>();
                entry.put(0, "admin");
                for(int i = 1; i < 17; i++) {
                    entry.put(i, true);
                }
                table.addEntry(entry);
            });
            this.doAction("stevesdb", "database_permissions", true, table -> {
                table.addColumn(TableColumnType.STRING, "group");
                table.addColumn(TableColumnType.STRING, "database");
                table.addColumn(TableColumnType.BOOLEAN, "rename_database");
                table.addColumn(TableColumnType.BOOLEAN, "delete_database");
                table.addColumn(TableColumnType.BOOLEAN, "list_tables");
                table.addColumn(TableColumnType.BOOLEAN, "create_table");
                table.addColumn(TableColumnType.BOOLEAN, "rename_table");
                table.addColumn(TableColumnType.BOOLEAN, "delete_table");
                table.addColumn(TableColumnType.BOOLEAN, "list_columns");
                table.addColumn(TableColumnType.BOOLEAN, "add_column");
                table.addColumn(TableColumnType.BOOLEAN, "rename_column");
                table.addColumn(TableColumnType.BOOLEAN, "remove_column");
                table.addColumn(TableColumnType.BOOLEAN, "list_entries");
                table.addColumn(TableColumnType.BOOLEAN, "add_entry");
                table.addColumn(TableColumnType.BOOLEAN, "modify_entry");
                table.addColumn(TableColumnType.BOOLEAN, "remove_entry");
            });
            this.doAction("stevesdb", "table_permissions", true, table -> {
                table.addColumn(TableColumnType.STRING, "group");
                table.addColumn(TableColumnType.STRING, "database");
                table.addColumn(TableColumnType.STRING, "table");
                table.addColumn(TableColumnType.BOOLEAN, "rename_table");
                table.addColumn(TableColumnType.BOOLEAN, "delete_table");
                table.addColumn(TableColumnType.BOOLEAN, "list_columns");
                table.addColumn(TableColumnType.BOOLEAN, "add_column");
                table.addColumn(TableColumnType.BOOLEAN, "rename_column");
                table.addColumn(TableColumnType.BOOLEAN, "remove_column");
                table.addColumn(TableColumnType.BOOLEAN, "list_entries");
                table.addColumn(TableColumnType.BOOLEAN, "add_entry");
                table.addColumn(TableColumnType.BOOLEAN, "modify_entry");
                table.addColumn(TableColumnType.BOOLEAN, "remove_entry");
            });
            this.addUser("stevesdb", "password");
        }
    }

    public static void replaceInstance(boolean cacheTablesInMemory) {
        DatabaseManager.instance = new DatabaseManager(cacheTablesInMemory);
    }
    public static DatabaseManager getInstance() {
        return DatabaseManager.instance;
    }

    public boolean createDatabase(String name) {
        if(!this.getDatabases().contains(name)) {
            try {
                Files.createDirectory(this.dataDirectory.resolve(name));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean renameDatabase(String name, String newName) {
        if(this.getDatabases().contains(name) && !this.getDatabases().contains(newName)) {
            try {
                Files.move(this.dataDirectory.resolve(name), this.dataDirectory.resolve(newName));
            } catch (IOException ignored) {}
        }
        return false;
    }

    public boolean deleteDatabase(String name) {
        try {
            for(String table : this.getDatabaseTables(name)) {
                this.deleteTable(name, table);
            }
            Files.delete(this.dataDirectory.resolve(name));
            this.cachedTables.remove(name);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public Collection<String> getDatabases() {
        Collection<String> databases = new HashSet<>();
        try {
            Files.list(this.dataDirectory).filter(Files::isDirectory).forEach(path -> databases.add(path.getFileName().toString()));
        } catch (IOException ignored) {}
        return databases;
    }

    public Collection<String> getDatabaseTables(String database) {
        Collection<String> tables = new HashSet<>();
        try {
            Files.list(this.dataDirectory.resolve(database)).filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".sdb")).forEach(path -> {
                String name = path.getFileName().toString();
                name = name.substring(0, name.length() - 4);
                tables.add(name);
            });
        } catch (IOException ignored) {}
        return tables;
    }

    public boolean createTable(String database, String name) {
        if (this.getDatabases().contains(database) && !this.getDatabaseTables(database).contains(name)) {
            Table obj = new Table();
            this.saveTable(database, name, obj);
            return true;
        } else {
            return false;
        }
    }

    public boolean renameTable(String database, String name, String newName) {
        Collection<String> tables = this.getDatabaseTables(database);
        if(this.getDatabases().contains(database) && tables.contains(name) && !tables.contains(newName)) {
            try {
                Files.move(this.dataDirectory.resolve(database).resolve(name + ".sdb"), this.dataDirectory.resolve(database).resolve(newName + ".sdb"));
            } catch (IOException ignored) {}
            return true;
        }
        return false;
    }

    public boolean deleteTable(String database, String table) {
        try {
            Files.delete(this.dataDirectory.resolve(database).resolve(table + ".sdb"));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private Table loadTable(String database, String table) {
        Table obj = new Table();
        try {
            obj.load(new DataReader(Files.newInputStream(this.dataDirectory.resolve(database).resolve(table + ".sdb"), StandardOpenOption.READ)));
        } catch (IOException e) {
            return null;
        }
        return obj;
    }

    private void saveTable(String database, String table, Table obj) {
        try {
            if(!Files.isRegularFile(this.dataDirectory.resolve(database).resolve(table + ".sdb"))) {
                Files.createFile(this.dataDirectory.resolve(database).resolve(table + ".sdb"));
            }
            obj.save(new DataWriter(Files.newOutputStream(this.dataDirectory.resolve(database).resolve(table + ".sdb"), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T> T doAction(String database, String table, boolean write, Function<Table, T> action) {
        Table obj = this.getCachedTable(database, table);
        if(obj == null) {
            obj = this.loadTable(database, table);
            if(this.cacheTablesInMemory) {
                this.setCachedTable(database, table, obj);
            }
        }
        T returnValue = action.apply(obj);
        if(write && obj != null) {
            this.saveTable(database, table, obj);
        }
        return returnValue;
    }

    public void doAction(String database, String table, boolean write, Consumer<Table> action) {
        this.doAction(database, table, write, obj -> {
            action.accept(obj);
            return null;
        });
    }

    public Collection<String> getUserGroups(String username) {
        return this.doAction("stevesdb", "groups", false, table -> {
            Collection<String> groups = new HashSet<>();
            for(Map<Integer, Object> entry : table.getEntries().values()) {
                if(entry.get(0).equals(username)) {
                    groups.add((String) entry.get(1));
                }
            }
            return groups;
        });
    }

    public boolean hasPermission(String username, String permission, String database, String table) {
        if(username == null) {
            return false;
        }
        for(String group : this.getUserGroups(username)) {
            AtomicBoolean value2 = new AtomicBoolean(false);
            this.doAction("stevesdb", "global_permissions", false, obj -> {
                for(Map<Integer, Object> entry : obj.getEntries().values()) {
                    if(entry.get(0).equals("*") || entry.get(0).equals(group)) {
                        int cid = obj.getColumnId(permission);
                        if(cid >= 1) {
                            value2.set((boolean) entry.get(cid));
                        }
                    }
                }
            });
            if(database != null) {
                this.doAction("stevesdb", "database_permissions", false, obj -> {
                    for(Map<Integer, Object> entry : obj.getEntries().values()) {
                        if((entry.get(0).equals("*") || entry.get(0).equals(group)) && entry.get(1).equals(database)) {
                            int cid = obj.getColumnId(permission);
                            if(cid >= 1) {
                                value2.set((boolean) entry.get(cid));
                            }
                        }
                    }
                });
                if(table != null) {
                    this.doAction("stevesdb", "table_permissions", false, obj -> {
                        for(Map<Integer, Object> entry : obj.getEntries().values()) {
                            if((entry.get(0).equals("*") || entry.get(0).equals(group)) && entry.get(1).equals(database) && entry.get(2).equals(table)) {
                                int cid = obj.getColumnId(permission);
                                if(cid >= 1) {
                                    value2.set((boolean) entry.get(cid));
                                }
                            }
                        }
                    });
                }
            }
            if(value2.get()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPermission(String username, String permission, String database) {
        return this.hasPermission(username, permission, database, null);
    }

    public boolean hasPermission(String username, String permission) {
        return this.hasPermission(username, permission, null);
    }

    public void addUser(String username, String password) {
        try {
            String salt = UUID.randomUUID().toString();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(password.getBytes(StandardCharsets.UTF_8));
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest();
            StringBuilder builder = new StringBuilder();
            for(byte b : digest) {
                String str = Integer.toHexString(Byte.toUnsignedInt(b));
                if(str.length() == 1) {
                    str = "0" + str;
                }
                builder.append(str);
            }
            this.doAction("stevesdb", "users", true, table -> {
                for(Map<Integer, Object> entry : table.getEntries().values()) {
                    if(entry.get(0).equals(username)) {
                        return;
                    }
                }
                Map<Integer, Object> entry = new HashMap<>();
                entry.put(0, username);
                entry.put(1, builder.toString());
                entry.put(2, salt);
                table.addEntry(entry);
            });
        } catch (NoSuchAlgorithmException ignored) {}
    }

    public void setCachedTable(String database, String table, Table obj) {
        if(obj != null) {
            if(!this.cachedTables.containsKey(database)) {
                this.cachedTables.put(database, new HashMap<>());
            }
            this.cachedTables.get(database).put(table, obj);
        } else {
            if(this.cachedTables.containsKey(database)) {
                this.cachedTables.get(database).remove(table, obj);
                if(this.cachedTables.get(database).isEmpty()) {
                    this.cachedTables.remove(database);
                }
            }
        }
    }

    public Table getCachedTable(String database, String table) {
        if(this.cachedTables.containsKey(database) && this.cachedTables.get(database).containsKey(table)) {
            return this.cachedTables.get(database).get(table);
        } else {
            return null;
        }
    }

    public UserData getUserData(String username) {
        return this.doAction("stevesdb", "users", false, table -> {
            for(Map<Integer, Object> entry : table.getEntries().values()) {
                if(entry.get(0).equals(username)) {
                    return new UserData(username, (String) entry.get(1), (String) entry.get(2));
                }
            }
            return null;
        });
    }
}