package com.company;

import java.io.*;
import java.net.*;
import java.util.Random;

public class Client {
    private static int TimeOut = 10; // 10s
    private static int initial_interval = 10; // 10s
    private static int backoff_cutoff = 120; // 120s
    public static void main(String[] args) throws Exception {

        long start;
        long end;
        String ip = "";
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
                    DataInputStream din = new DataInputStream(new ByteArrayInputStream(receiveData));
                    end = System.currentTimeMillis();
                    if (timeForGetOffer(start, end , time)) {
                        ip = getIP(din);
                        if (ip != null) {
                            break;
                        }
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
                DataInputStream din = new DataInputStream(new ByteArrayInputStream(receiveData));
                end = System.currentTimeMillis();
                if (end-start <= TimeOut * 1000L && getAck(din)) {
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

        outStream.writeByte(0x00); // CHADDR 1
        outStream.writeByte(0x05); // CHADDR 1
        outStream.writeByte(0x3C); // CHADDR 1
        outStream.writeByte(0x04); // CHADDR 1

        outStream.writeByte(0x8D); // CHADDR 2
        outStream.writeByte(0x59); // CHADDR 2
        outStream.writeByte(0x00); // CHADDR 2
        outStream.writeByte(0x00); // CHADDR 2

        outStream.writeByte(0x00); // CHADDR 3
        outStream.writeByte(0x00); // CHADDR 3
        outStream.writeByte(0x00); // CHADDR 3
        outStream.writeByte(0x00); // CHADDR 3

        outStream.writeByte(0x00); // CHADDR 4
        outStream.writeByte(0x00); // CHADDR 4
        outStream.writeByte(0x00); // CHADDR 4
        outStream.writeByte(0x00); // CHADDR 4

        outStream.writeByte(0x01); // OPTION

        return out.toByteArray();
    }

    public static byte[] createRequestMessage() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(out);

        outStream.writeByte(0x01); // op

        outStream.writeByte(0x01); // HTYPE

        outStream.writeByte(0x06); // HLEN

        outStream.writeByte(0x00); // HOPS

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

        outStream.writeByte(0x00); // CHADDR 1
        outStream.writeByte(0x05); // CHADDR 1
        outStream.writeByte(0x3C); // CHADDR 1
        outStream.writeByte(0x04); // CHADDR 1

        outStream.writeByte(0x8D); // CHADDR 2
        outStream.writeByte(0x59); // CHADDR 2
        outStream.writeByte(0x00); // CHADDR 2
        outStream.writeByte(0x00); // CHADDR 2

        outStream.writeByte(0x00); // CHADDR 3
        outStream.writeByte(0x00); // CHADDR 3
        outStream.writeByte(0x00); // CHADDR 3
        outStream.writeByte(0x00); // CHADDR 3

        outStream.writeByte(0x00); // CHADDR 4
        outStream.writeByte(0x00); // CHADDR 4
        outStream.writeByte(0x00); // CHADDR 4
        outStream.writeByte(0x00); // CHADDR 4

        outStream.writeByte(0x03); // OPTION

        return out.toByteArray();
    }

    public static String getIP(DataInputStream din) throws IOException {

        String ip = "";
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

        int yiaddr1 = din.readInt();
        int yiaddr2 = din.readInt();
        int yiaddr3 = din.readInt();
        int yiaddr4 = din.readInt();

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
        byte[] mac_byte = new byte[16];
        for (int i = 0 ; i < 16 ; i++){
            chaddr[i] = din.readByte();
            mac = mac + chaddr[i];
            mac_byte[i]=chaddr[i];
        }

        byte option = din.readByte();

        ip = yiaddr1+"."+yiaddr2+"."+yiaddr3+"."+yiaddr4;
        return ip;

    }

    public static boolean getAck(DataInputStream din){
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