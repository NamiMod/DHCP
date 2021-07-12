package com.company;

/**
 * Server Class
 *
 *
 *
 *
 * in this class we create offer and ack message and set ip to clients
 * server configs : configs.json
 *
 *
 * networks project
 * simple DHCP Client - Server
 * @author Seyed Nami Modarressi
 * @since Summer 2021
 */

import java.io.*;
import java.net.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class Server {

    private static String mode;
    private static String start;
    private static String end;
    private static long lease_time;
    private static ArrayList<Server_Data> clients = new ArrayList();

    /**
     * main method of server
     * @throws Exception cant send or received message
     */
    public static void main(String args[]) throws Exception {
        setup();
        System.out.println("setup completed");
        DatagramSocket serverSocket = new DatagramSocket(9876);
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        while (true) {
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

    /**
     * create offer message for client
     * @param mac client mac
     * @param ip client ip
     * @return message
     * @throws IOException cant create message
     */
    public static byte[] createOfferMessage(int[] mac, String ip) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(out);

        outStream.writeByte(0x02); // op

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

        String[] ip_parts = ip.split("\\.", 4);
        int[] numbers = new int[4];
        for (int i = 0; i < 4; i++) {
            numbers[i] = Integer.parseInt(ip_parts[i]);
        }

        for (int i = 0; i < 4; i++) {
            outStream.writeInt(numbers[i]); // YIADDR // Client ip
        }

        outStream.writeByte(0x7F); // SIADDR
        outStream.writeByte(0x00); // SIADDR
        outStream.writeByte(0x00); // SIADDR
        outStream.writeByte(0x01); // SIADDR

        outStream.writeByte(0x00); // GIADDR
        outStream.writeByte(0x00); // GIADDR
        outStream.writeByte(0x00); // GIADDR
        outStream.writeByte(0x00); // GIADDR

        for (int i = 0; i < 6; i++) {
            outStream.writeInt(mac[i]);
        }

        outStream.writeByte(0x02); // OPTION

        return out.toByteArray();
    }

    /**
     * create ack message for client
     * @param mac client mac
     * @param ip client ip
     * @return message
     * @throws IOException cant create message
     */
    public static byte[] createAckMessage(int[] mac, String ip) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(out);

        outStream.writeByte(0x02); // op

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

        String[] ip_parts = ip.split("\\.", 4);
        int[] numbers = new int[4];
        for (int i = 0; i < 4; i++) {
            numbers[i] = Integer.parseInt(ip_parts[i]);
        }

        for (int i = 0; i < 4; i++) {
            outStream.writeInt(numbers[i]); // YIADDR // Client ip
        }

        outStream.writeByte(0x7F); // SIADDR
        outStream.writeByte(0x00); // SIADDR
        outStream.writeByte(0x00); // SIADDR
        outStream.writeByte(0x01); // SIADDR

        outStream.writeByte(0x00); // GIADDR
        outStream.writeByte(0x00); // GIADDR
        outStream.writeByte(0x00); // GIADDR
        outStream.writeByte(0x00); // GIADDR

        for (int i = 0; i < 6; i++) {
            outStream.writeInt(mac[i]);
        }

        outStream.writeByte(0x05); // OPTION

        return out.toByteArray();
    }

    /**
     * handle client message
     * @param din received packet
     * @return message
     * @throws IOException cant read message
     */
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

        int[] chaddr = new int[6];
        String mac = "";
        int[] mac_byte = new int[6];
        for (int i = 0; i < 6; i++) {
            chaddr[i] = din.readInt();
            if (i != 0) {
                mac = mac + ":" + chaddr[i];
            } else {
                mac = mac + chaddr[i];
            }
            mac_byte[i] = chaddr[i];
        }

        byte option = din.readByte();
        String ip = setIp(mac);


        if (option == 1) {
            // discovery message
            System.out.println("offer message");
            message = createOfferMessage(mac_byte, ip);

        } else if (option == 3) {
            // request message
            System.out.println("request message");
            message = createAckMessage(mac_byte, ip);
        }

        return message;
    }

    /**
     * setup server
     */
    public static void setup() {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("configs.json"));
            JSONObject jsonObject = (JSONObject) obj;

            String name = (String) jsonObject.get("pool_mode");
            mode = name;

            Map range = ((Map) jsonObject.get("range"));
            // iterating address Map
            Iterator<Map.Entry> itr1 = range.entrySet().iterator();
            int counter = 0;
            while (itr1.hasNext()) {
                Map.Entry pair = itr1.next();
                if (mode.equals("range")) {
                    if (counter == 0) {
                        start = (String) pair.getValue();
                        counter++;
                    } else {
                        end = (String) pair.getValue();
                    }

                }
            }

            counter = 0;

            Map subnet = ((Map) jsonObject.get("subnet"));
            // iterating address Map
            Iterator<Map.Entry> itr2 = subnet.entrySet().iterator();
            while (itr2.hasNext()) {
                Map.Entry pair = itr2.next();
                if (mode.equals("subnet")) {
                    if (counter == 0) {
                        end = (String) pair.getValue();
                        counter++;
                    } else {
                        start = (String) pair.getValue();
                    }
                }
            }

            Long lease = (Long) jsonObject.get("lease_time");
            lease_time = lease;

            Map reservation_list = ((Map) jsonObject.get("reservation_list"));
            // iterating address Map
            Iterator<Map.Entry> itr3 = reservation_list.entrySet().iterator();
            while (itr3.hasNext()) {
                Map.Entry pair = itr3.next();
                clients.add(new Server_Data((String) pair.getKey(), (String) pair.getValue(), -1, 0));
            }

            JSONArray black_list = (JSONArray) jsonObject.get("black_list");
            Iterator iterator4 = black_list.iterator();
            while (iterator4.hasNext()) {
                clients.add(new Server_Data((String) iterator4.next(), "", -2, 0));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * set ip to client
     * @param mac mac of client
     * @return ip
     */
    public static String setIp(String mac) {

        update_ip();

        String ip = start;
        if (mode.equals("subnet")) {
            ip = ip_plus(ip);
        }

        for (int i = 0; i < clients.size(); i++) {

            if (mac.equals(clients.get(i).getMac()) && clients.get(i).getLease_time() == -1) {
                return clients.get(i).getIp();
            }

            if (clients.get(i).getLease_time() == -2 && clients.get(i).getMac().equals(mac)) {
                return "0.0.0.0";
            }
            if (clients.get(i).getMac().equals(mac)) {

                if (lease_time * 1000 > System.currentTimeMillis() - clients.get(i).getStart()) {
                    clients.get(i).setStart(System.currentTimeMillis());
                    return clients.get(i).getIp();
                } else {
                    clients.remove(i);
                    break;
                }

            }
        }
        while (!is_ip_ok(ip)) {
            ip = ip_plus(ip);
        }

        if (ip_bigger(ip)) {
            return "0.0.0.0";
        } else {
            clients.add(new Server_Data(mac, ip, lease_time, System.currentTimeMillis()));
        }
        System.out.println("******************************");
        System.out.println("Clients :");
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getLease_time() == -2) {
                System.out.println("Blocked : " + clients.get(i).getMac());
            }
            if (clients.get(i).getLease_time() == -1) {
                System.out.println("Reserved : " + clients.get(i).getMac() + " - " + clients.get(i).getIp());
            }

            if (clients.get(i).getLease_time() != -2 && clients.get(i).getLease_time() != -1) {
                System.out.println(clients.get(i).getMac() + " : " + clients.get(i).getIp() + " *** Time to expire(in ms) : " + (clients.get(i).getLease_time() * 1000 - System.currentTimeMillis() + clients.get(i).getStart()));
            }
        }
        System.out.println("******************************");
        return ip;
    }

    /**
     * check if the ip is ok or not
     * @param ip client ip
     * @return true or false
     */
    public static boolean is_ip_ok(String ip) {
        for (int i = 0; i < clients.size(); i++) {
            if (ip.equals(clients.get(i).getIp())) {
                return false;
            }
        }
        return true;
    }

    /**
     * next ip
     * @param ip last ip
     * @return new ip
     */
    public static String ip_plus(String ip) {

        String[] numbers = ip.split("\\.", 4);
        int[] int_numbers = new int[4];
        int_numbers[0] = Integer.parseInt(numbers[0]);
        int_numbers[1] = Integer.parseInt(numbers[1]);
        int_numbers[2] = Integer.parseInt(numbers[2]);
        int_numbers[3] = Integer.parseInt(numbers[3]);
        int_numbers[3]++;
        if (int_numbers[3] == 1000) {
            int_numbers[3]--;
            int_numbers[2]++;
        }
        if (int_numbers[2] == 1000) {
            int_numbers[2]--;
            int_numbers[1]++;
        }
        if (int_numbers[1] == 1000) {
            int_numbers[1]--;
            int_numbers[0]++;
        }
        if (int_numbers[0] == 1000) {
            int_numbers[0]--;
        }

        return int_numbers[0] + "." + int_numbers[1] + "." + int_numbers[2] + "." + int_numbers[3];


    }

    /**
     * check if the ip is bigger than max or not
     * @param ip client ip
     * @return true or false
     */
    public static boolean ip_bigger(String ip) {
        String[] numbers = ip.split("\\.", 4);
        String temp = numbers[0] + numbers[1] + numbers[2] + numbers[3];

        String[] max = end.split("\\.", 4);
        String temp2 = max[0] + max[1] + max[2] + max[3];

        long num1 = Long.parseLong(temp);
        long num2 = Long.parseLong(temp2);

        if (num1 <= num2) {
            return false;
        }
        return true;

    }

    /**
     * update clients
     */
    public static void update_ip() {

        long time = System.currentTimeMillis();

        ListIterator<Server_Data> iter = clients.listIterator();
        while (iter.hasNext()) {
            Server_Data temp = iter.next();
            if (time - temp.getStart() >= lease_time * 1000 && temp.getLease_time() >= 0) {
                System.out.println("removed : " + temp.getMac() + " - " + temp.getIp());
                iter.remove();
            }
        }

    }

}