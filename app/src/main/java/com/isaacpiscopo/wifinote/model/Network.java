package com.isaacpiscopo.wifinote.model;

import java.io.Serializable;

/**
 * Represents a saved WiFi network credential.
 * Stored in the local SQLite database via {@link com.isaacpiscopo.wifinote.data.DbHelper}.
 */
public class Network implements Serializable {

    private long id;
    private String ssid;
    private String password;
    private String security;
    private long createdAt;

    /** Constructs a new Network with all fields. */
    public Network(long id, String ssid, String password, String security, long createdAt) {
        this.id = id;
        this.ssid = ssid;
        this.password = password;
        this.security = security;
        this.createdAt = createdAt;
    }

    /** Constructs a new Network without an id (for insert operations). */
    public Network(String ssid, String password, String security) {
        this.id = -1;
        this.ssid = ssid;
        this.password = password;
        this.security = security;
        this.createdAt = System.currentTimeMillis();
    }

    /** Returns the database row id, or -1 if not yet persisted. */
    public long getId() { return id; }

    /** Sets the database row id after a successful insert. */
    public void setId(long id) { this.id = id; }

    /** Returns the WiFi network name (SSID). */
    public String getSsid() { return ssid; }

    /** Sets the WiFi network name (SSID). */
    public void setSsid(String ssid) { this.ssid = ssid; }

    /** Returns the network password, or an empty string for open networks. */
    public String getPassword() { return password; }

    /** Sets the network password. */
    public void setPassword(String password) { this.password = password; }

    /**
     * Returns the security type.
     * Valid values: {@code "WPA2"}, {@code "WPA3"}, {@code "OPEN"}.
     */
    public String getSecurity() { return security; }

    /** Sets the security type. */
    public void setSecurity(String security) { this.security = security; }

    /** Returns the Unix timestamp (ms) when this record was created. */
    public long getCreatedAt() { return createdAt; }

    /** Sets the creation timestamp. */
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
