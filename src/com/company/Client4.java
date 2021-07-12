package com.company;

import java.io.*;
import java.net.*;
import java.util.Random;
// reserved client
public class Client4 {
    private static int TimeOut = 10; // 10s
    private static int initial_interval = 10; // 10s
    private static int backoff_cutoff = 120; // 120s
    public static void main(String[] args) throws Exception {

        long start;
        long end;
        String ip = "";
        int time = initial_interval;

        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("localhost");
        byte[] sendData;
        byte[] receiveData = new byte[1024];

        while (true) {
            while (true) {
                sendData = createDiscoveryMessage();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
                clientSocket.send(sendPacket);
                System.out.println("discovery sent");
                start = System.currentTimeMillis();
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                System.out.println("offer received");
                DataInputStream din = new DataInputStream(new ByteArrayInputStream(receiveData));
                end = System.currentTimeMillis();
                if (timeForGetOffer(start, end, time)) {
                    ip = getIP(din);
                    System.out.println(ip);
                    if (ip != null) {
                        System.out.println("we get ip in time :)");
                        break;
                    }
                } else {
                    System.out.println("out of time :(");
                    time = updateTime(time);
                }
            }
            if (ip.equals("0.0.0.0")){
                break;
            }
            sendData = createRequestMessage(ip);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
            clientSocket.send(sendPacket);
            System.out.println("Request sent");
            start = System.currentTimeMillis();
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            System.out.println("we got ack packet");
            DataInputStream din = new DataInputStream(new ByteArrayInputStream(receiveData));
            end = System.currentTimeMillis();
            if (end-start <= TimeOut * 1000L && getAck(din)) {
                System.out.println("Ack");
                break;
            }
            System.out.println("Ack didn't get");
        }
        if (!ip.equals("0.0.0.0")) {
            System.out.println("IP : " + ip);
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

        outStream.writeInt(0x00); // CHADDR 1
        outStream.writeInt(0x00); // CHADDR 2
        outStream.writeInt(0x00); // CHADDR 3
        outStream.writeInt(0x00); // CHADDR 4
        outStream.writeInt(0x00); // CHADDR 5
        outStream.writeInt(0x04); // CHADDR 6

        outStream.writeByte(0x01); // OPTION

        return out.toByteArray();
    }

    public static byte[] createRequestMessage(String ip) throws IOException {
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

        outStream.writeByte(0x7F); // SIADDR
        outStream.writeByte(0x00); // SIADDR
        outStream.writeByte(0x00); // SIADDR
        outStream.writeByte(0x01); // SIADDR

        outStream.writeByte(0x00); // GIADDR
        outStream.writeByte(0x00); // GIADDR
        outStream.writeByte(0x00); // GIADDR
        outStream.writeByte(0x00); // GIADDR

        outStream.writeInt(0x00); // CHADDR 1
        outStream.writeInt(0x00); // CHADDR 2
        outStream.writeInt(0x00); // CHADDR 3
        outStream.writeInt(0x00); // CHADDR 4
        outStream.writeInt(0x00); // CHADDR 5
        outStream.writeInt(0x04); // CHADDR 6

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

        int[] chaddr = new int[6];
        for (int i = 0 ; i < 6 ; i++){
            chaddr[i] = din.readInt();
        }

        byte option = din.readByte();

        ip = yiaddr1+"."+yiaddr2+"."+yiaddr3+"."+yiaddr4;
        return ip;

    }

    public static boolean getAck(DataInputStream din) throws IOException {
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

        int[] chaddr = new int[6];
        for (int i = 0 ; i < 6 ; i++){
            chaddr[i] = din.readInt();
        }


        int option = din.readByte();

        System.out.println("option : "+option);

        if (option == 5){
            return true;
        }

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