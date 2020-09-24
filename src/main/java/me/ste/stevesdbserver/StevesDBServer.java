package me.ste.stevesdbserver;

import me.ste.stevesdbserver.database.DatabaseManager;
import me.ste.stevesdbserver.database.Table;
import me.ste.stevesdbserver.database.TableColumnType;
import me.ste.stevesdbserver.network.NetworkManager;
import me.ste.stevesdbserver.network.packet.in.*;
import me.ste.stevesdbserver.network.packet.out.*;
import me.ste.stevesdbserver.util.DataWriter;

import java.io.IOException;
import java.nio.file.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StevesDBServer {
    public static final int PROTOCOL_VERSION = 3;
    public static final String SERVER_VERSION = "2.0.2";

    private final static StevesDBServer instance = new StevesDBServer();

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private KeyPair keyPair;
    private NetworkThread networkThread;

    private String boundIp;
    private int port;
    private boolean cacheTablesInMemory;

    public StevesDBServer() {
        this.logger.setLevel(Level.ALL);

        Path configFile = Paths.get("config.xml");
        Properties config = new Properties();

        if(Files.isRegularFile(configFile)) {
            try {
                config.loadFromXML(Files.newInputStream(configFile, StandardOpenOption.READ));
            } catch (IOException e) {
                this.logger.log(Level.SEVERE, "Unable to load the configuration file, aborting");
                e.printStackTrace();
                return;
            }
        } else {
            try {
                Files.createFile(configFile);

                config.setProperty("bound-ip", "0.0.0.0");
                config.setProperty("port", "2540");
                config.setProperty("cache-tables-in-memory", "false");

                config.storeToXML(Files.newOutputStream(configFile, StandardOpenOption.WRITE), "StevesDB Server Configuration File");
            } catch (IOException e) {
                this.logger.log(Level.SEVERE, "Unable to read the configuration file, aborting");
                e.printStackTrace();
                return;
            }
        }

        this.boundIp = config.getProperty("bound-ip");
        this.port = Integer.parseInt(config.getProperty("port"));
        this.cacheTablesInMemory = Boolean.parseBoolean(config.getProperty("cache-tables-in-memory"));

        this.logger.log(Level.INFO, "Loading database manager...");
        DatabaseManager.replaceInstance(this.cacheTablesInMemory);

        this.logger.log(Level.INFO, "Generating key pair... ");

        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);
            this.keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        }

        this.logger.log(Level.INFO, "Starting the socket server...");

        NetworkManager.getInstance().registerPacketsIn(
                AddColumnPacket.class,
                AddEntryPacket.class,
                CreateDatabasePacket.class,
                CreateTablePacket.class,
                DeleteDatabasePacket.class,
                DeleteTablePacket.class,
                EncryptionEnabledPacket.class,
                EncryptionPacket.class,
                HelloPacket.class,
                ListColumnsPacket.class,
                ListDatabasesPacket.class,
                ListEntriesPacket.class,
                ListTablesPacket.class,
                LoginPacket.class,
                LogoutPacket.class,
                ModifyEntryPacket.class,
                RemoveColumnPacket.class,
                RemoveEntryPacket.class,
                RenameColumnPacket.class,
                RenameDatabasePacket.class,
                RenameTablePacket.class
        );

        NetworkManager.getInstance().registerPacketsOut(
                AddColumnResponsePacket.class,
                AddEntryResponsePacket.class,
                CreateDatabaseResponsePacket.class,
                CreateTableResponsePacket.class,
                DeleteDatabaseResponsePacket.class,
                DeleteTableResponsePacket.class,
                EncryptionResponsePacket.class,
                HelloResponsePacket.class,
                ListColumnsResponsePacket.class,
                ListDatabasesResponsePacket.class,
                ListEntriesResponsePacket.class,
                ListTablesResponsePacket.class,
                LoginResponsePacket.class,
                LogoutResponsePacket.class,
                ModifyEntryResponsePacket.class,
                RemoveColumnResponsePacket.class,
                RemoveEntryResponsePacket.class,
                RenameColumnResponsePacket.class,
                RenameDatabaseResponsePacket.class,
                RenameTableResponsePacket.class
        );

        this.networkThread = new NetworkThread(this.boundIp, this.port);
        this.networkThread.start();

        this.logger.log(Level.INFO, "Server is listening on " + this.boundIp + ":" + this.port);

        this.logger.log(Level.INFO, "Started StevesDB server version " + StevesDBServer.SERVER_VERSION);
    }

    public static StevesDBServer getInstance() {
        return StevesDBServer.instance;
    }

    public KeyPair getKeyPair() {
        return this.keyPair;
    }

    public static void main(String[] args) {}
}