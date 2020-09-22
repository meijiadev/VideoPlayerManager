package com.example.videoplayermanager.bean;

/**
 * 广告机参数配置
 */
public class ADConfig {
    private String IP="192.168.0.96";
    private String port="189";
    private String account="admin_ad1";
    private String password="admin_ad";


    public void setAccount(String account) {
        this.account = account;
    }

    public String getPort() {
        return port;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getAccount() {
        return account;
    }

    public String getIP() {
        return IP;
    }

    public String getPassword() {
        return password;
    }

}
