/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gaza.aid.tracker;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Abstract class representing a system user .
 */
public abstract class User {

    // Data Fields: Store user identification and security information
    private int user_ID;
    private String user_Name;
    private String password; // Stores the hashed password, never plain text
    private String user_Type;

    // Empty Constructor
    public User() {
    }

    // Constructor: Initializes a new user and encrypts the password immediately
    public User(int user_ID, String user_Name, String password, String user_Type) {
        this.user_ID = user_ID;
        this.user_Name = user_Name;
        this.setPassword(password);
        this.user_Type = user_Type;
    }

    // Getter Method: Returns the user ID
    public int getUser_ID() {
        return user_ID;
    }

    // Setter Method: Updates the user ID
    public void setUser_ID(int user_ID) {
        this.user_ID = user_ID;
    }

    // Getter Method: Returns the user name
    public String getUser_Name() {
        return user_Name;
    }

    // Setter Method: Updates the user name
    public void setUser_Name(String user_Name) {
        this.user_Name = user_Name;
    }

    // Getter Method: Returns the stored password hash
    public String getPassword() {
        return password;
    }

    // Setter Method: Encrypts the raw password and stores it as a SHA-256 hash
    public final void setPassword(String rawPassword) {
        this.password = hashPassword(rawPassword);
    }

    // Logic Method: Verifies if the attempted password matches the stored hash
    public boolean checkPassword(String attemptedPassword) {
        if (attemptedPassword == null) {
            return false;
        }
        return this.password.equals(hashPassword(attemptedPassword));
    }

    // Private Helper Method: Performs SHA-256 password hashing
    private static String hashPassword(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawPassword.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("Password hashing failed.", e);
        }
    }

    // Getter Method: Returns the user type (Organization, Volunteer)
    public String getUser_Type() {
        return user_Type;
    }

    // Setter Method: Updates the user type
    public void setUser_Type(String user_Type) {
        this.user_Type = user_Type;
    }

    // Abstract Method: Defines role-based permissions to be implemented by subclasses
    public abstract String getRolePermissions();

    // Method: Returns a string representation of the user
    @Override
    public String toString() {
        return "user_ID : " + user_ID + "user_Name : " + user_Name + "user_Type : " + user_Type;
    }
}