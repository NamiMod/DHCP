package com.company;

public class Server_Data {

    private String mac;
    private String ip;
    private long lease_time;

    public Server_Data(String mac , String ip , int lease_time){
        this.mac = mac;
        this.ip = ip;
        this.lease_time = lease_time;
    }

    public String getMac() {
        return mac;
    }

    public String getIp() {
        return ip;
    }

    public long getLease_time() {
        return lease_time;
    }

}
