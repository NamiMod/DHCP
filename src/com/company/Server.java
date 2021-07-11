package com.company;

import java.io.*;
import java.net.*;

public class Server {
    public static void main(String args[]) throws Exception {
        DatagramSocket serverSocket = new DatagramSocket(9876);
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        while(true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            DataInputStream din = new DataInputStream(new ByteArrayInputStream(receiveData));
            System.out.println("- Packet Received From Client");
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            sendData = handle(din);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            serverSocket.send(sendPacket);
            System.out.println("- Packet Sent To Client");
        }
    }


    public static byte[] createOfferMessage(String mac , String ip) throws IOException {
        return null;
    }
    public static byte[] createAckMessage(String mac , String ip) throws IOException {
        return null;
    }
    public static String setIp(String mac){
        return null;
    }

    public static byte[] handle(DataInputStream din) throws IOException {

        byte[] message = new byte[1024];

        byte op = din.readByte();

        byte htype = din.readByte();

        byte hlen = din.readByte();

        byte hops = din.readByte();

        byte xid1 = din.readByte();
        byte xid2 = din.readByte();
        byte xid3 = din.readByte();
        byte xid4 = din.readByte();

        byte secs1 = din.readByte();
        byte secs2 = din.readByte();

        byte flag1 = din.readByte();
        byte flag2 = din.readByte();

        byte ciaddr1 = din.readByte();
        byte ciaddr2 = din.readByte();
        byte ciaddr3 = din.readByte();
        byte ciaddr4 = din.readByte();

        byte yiaddr1 = din.readByte();
        byte yiaddr2 = din.readByte();
        byte yiaddr3 = din.readByte();
        byte yiaddr4 = din.readByte();

        byte siaddr1 = din.readByte();
        byte siaddr2 = din.readByte();
        byte siaddr3 = din.readByte();
        byte siaddr4 = din.readByte();

        byte giaddr1 = din.readByte();
        byte giaddr2 = din.readByte();
        byte giaddr3 = din.readByte();
        byte giaddr4 = din.readByte();

        byte[] chaddr = new byte[16];
        String mac = "";
        for (int i = 0 ; i < 16 ; i++){
            chaddr[i] = din.readByte();
            mac = mac + chaddr[i];
        }

        byte option = din.readByte();

        if (option == 1){
            // discovery message
            String ip = setIp(mac);
            message = createOfferMessage(mac , ip);

        }else if (option == 3){
            // request message
            String ip = setIp(mac);
            message = createAckMessage(mac , ip);
        }
        return message;
    }

}