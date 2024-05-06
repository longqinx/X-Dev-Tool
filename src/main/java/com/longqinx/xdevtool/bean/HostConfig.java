package com.longqinx.xdevtool.bean;

import java.util.Vector;

/**
 * @author xiaos
 * @since 2023/10/23
 */
public class HostConfig {
    private String ip;
    private String port;
    private String username;
    private String password;

    public HostConfig() {
    }

    public HostConfig(String ip, String port, String username, String password) {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public HostConfig(Vector<String> vector) {
        this.ip = vector.get(0);
        this.port = vector.get(1);
        this.username = vector.get(2);
        this.password = vector.get(3);
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public String toString() {
        return username + "@" + ip;
    }
}
