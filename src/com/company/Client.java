package com.company;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Random;

public class Client {
    private static int TimeOut = 10; // 10s
    private static int initial_interval = 10; // 10s
    private static int backoff_cutoff = 120; // 120s
    public static void main(String[] args) throws Exception {

        long start;
        long end;
        String ip = null;
        int time = initial_interval;

        while(true) {
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName("localhost");
            byte[] sendData;
            byte[] receiveData = new byte[1024];

            while(true) {
                while (true) {
                    sendData = createDiscoveryMessage();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
                    clientSocket.send(sendPacket);
                    start = System.currentTimeMillis();
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    clientSocket.receive(receivePacket);
                    end = System.currentTimeMillis();
                    ip = getIP(receiveData);
                    if (timeForGetOffer(start, end , time) && ip != null) {
                        break;
                    } else {
                        time = updateTime(time);
                    }
                }
                sendData = createRequestMessage();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
                clientSocket.send(sendPacket);
                start = System.currentTimeMillis();
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                end = System.currentTimeMillis();
                if (getAck(receiveData) && end-time <= TimeOut * 1000L) {
                    break;
                }
            }
            System.out.println("IP : "+ ip);
        }
    }

    public static byte[] createDiscoveryMessage() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(out);

        outStream.writeByte(0x01); // op

        outStream.writeByte(0x01); // HTYPE

        outStream.writeByte(0x06); // HLEN

        outStream.writeByte(0x00); // HOPS
        //ToDo : random XID
        outStream.writeByte(0x01); // XID
        outStream.writeByte(0x02); // XID
        outStream.writeByte(0x03); // XID
        outStream.writeByte(0x04); // XID

        outStream.writeByte(0x00); // SECS
        outStream.writeByte(0x00); // SECS

        outStream.writeByte(0x00); // FLAGS
        outStream.writeByte(0x00); // FLAGS

        outStream.writeByte(0x00); // CIADDR
        outStream.writeByte(0x00); // CIADDR
        outStream.writeByte(0x00); // CIADDR
        outStream.writeByte(0x00); // CIADDR

        outStream.writeByte(0x00); // YIADDR
        outStream.writeByte(0x00); // YIADDR
        outStream.writeByte(0x00); // YIADDR
        outStream.writeByte(0x00); // YIADDR

        outStream.writeByte(0x00); // SIADDR
        outStream.writeByte(0x00); // SIADDR
        outStream.writeByte(0x00); // SIADDR
        outStream.writeByte(0x00); // SIADDR

        outStream.writeByte(0x00); // GIADDR
        outStream.writeByte(0x00); // GIADDR
        outStream.writeByte(0x00); // GIADDR
        outStream.writeByte(0x00); // GIADDR

        outStream.writeByte(0x00); // GIADDR
        outStream.writeByte(0x00); // GIADDR
        outStream.writeByte(0x00); // GIADDR
        outStream.writeByte(0x00); // GIADDR



        return out.toByteArray();
    }
    public static byte[] createRequestMessage(){
        return null;
    }

    public static String getIP(byte[] offer){ return null; }
    public static boolean getAck(byte[] offer){
        return false;
    }





    public static boolean timeForGetOffer(long start , long end , int time){
        long temp = end - start ;
        if (temp < time*1000L){
            return true;
        }
        return false;
    }
    public static int updateTime(int time){
        Random rand = new Random();
        int temp = (int)(time * 2 * rand.nextDouble());
        return Math.min(temp, backoff_cutoff);
    }


}