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


    public static byte[] createOfferMessage() throws IOException {
        return null;
    }
    public static byte[] createAckMessage() throws IOException {
        return null;
    }
    public static String setIp(){
        return null;
    }
    public static byte[] handle(DataInputStream din){
        return null;
    }

}