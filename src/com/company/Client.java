package com.company;

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws Exception {


        while(true) {

            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName("localhost");
            byte[] sendData = new byte[1024];
            byte[] receiveData = new byte[1024];



            String sentence = inFromUser.readLine();
            sendData = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
            clientSocket.send(sendPacket);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            String modifiedSentence = new String(receivePacket.getData());
            System.out.println("FROM SERVER:" + modifiedSentence);
        }


    }

    public byte[] createDiscoveryMessage(){
        return null;
    }
    public byte[] createRequestMessage(){
        return null;
    }
    public String getIP(byte[] offer){
        return null;
    }
    public boolean timeForGetOffer(long start , long end){
        return false;
        // update time if false
    }
    public boolean getAck(byte[] offer){
        return false;
    }

}