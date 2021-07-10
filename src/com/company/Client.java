package com.company;

import java.net.*;

public class Client {

    private static String ip;

    public static void main(String[] args) throws Exception {

        int counter = 1;
        long start;
        long end;

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
                    if (timeForGetOffer(start, end) && getIP(receiveData) != null) {
                        break;
                    } else {
                        counter += 1;
                        updateTime(counter);
                    }
                }
                sendData = createRequestMessage();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
                clientSocket.send(sendPacket);
                start = System.currentTimeMillis();
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                end = System.currentTimeMillis();
                if (getAck(receiveData)) { // and time is ok
                    break;
                }
            }
            System.out.println("IP : "+ ip);
        }
    }

    public static byte[] createDiscoveryMessage(){
        return null;
    }
    public static byte[] createRequestMessage(){
        return null;
    }
    public static String getIP(byte[] offer){

        return null;
    }
    public static boolean getAck(byte[] offer){
        return false;
    }
    public static boolean timeForGetOffer(long start , long end){
        // true if it's not expired
        return false;
    }
    public static void updateTime(int counter){

    }


}