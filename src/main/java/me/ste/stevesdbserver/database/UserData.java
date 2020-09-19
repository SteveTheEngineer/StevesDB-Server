package me.ste.stevesdbserver.database;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserData {
    private final String username;
    private final String password;
    private final String passwordSalt;

    public UserData(String username, String password, String passwordSalt) {
        this.username = username;
        this.password = password;
        this.passwordSalt = passwordSalt;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getPasswordSalt() {
        return this.passwordSalt;
    }

    public boolean verifyPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(password.getBytes(StandardCharsets.UTF_8));
            md.update(this.passwordSalt.getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest();
            StringBuilder builder = new StringBuilder();
            for(byte b : digest) {
                String str = Integer.toHexString(Byte.toUnsignedInt(b));
                if(str.length() == 1) {
                    str = "0" + str;
                }
                builder.append(str);
            }
            return this.password.equals(builder.toString());
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }
}