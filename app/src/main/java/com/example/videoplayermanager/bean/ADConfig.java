package com.example.videoplayermanager.bean;

public class ADConfig {
    private String IP="在此更改IP";
    private String port="在此更改端口";
    private String account="在此更改账号";
    private String password="在此更改密码";


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
