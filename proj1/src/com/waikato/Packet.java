package com.waikato;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by lucas on 20/03/15.
 */
public class Packet {

    // TFTP constants

    public int serverPort = 69;
    public static int maxTftpPakLen=516;
    public static int maxTftpData=512;

    // Tftpd opcodes (RFC 1350)

    protected static final short RRQ =1;
    protected static final short WRQ =2;
    protected static final short DATA =3;
    protected static final short ACK =4;
    protected static final short ERROR =5;

    // Packet Offsets

    protected static final int opOffset=0;

    protected static final int fileOffset=2;

    protected static final int blkOffset=2;
    protected static final int dataOffset=4;

    protected static final int numOffset=2;
    protected static final int msgOffset=4;

    // The packet

    protected int length;
    protected byte [] message;

    // Reply Address

    protected InetAddress host;
    protected int port;


    public Packet() {
        message=new byte[maxTftpPakLen];
        length=maxTftpPakLen;
    }

    public static Packet receive(DatagramSocket sock) throws Exception {
        Packet in = new Packet();
        Packet typePack = null;
        DatagramPacket datagramPacket = new DatagramPacket(in.message,in.length);

        sock.receive(datagramPacket);
        switch (in.get(0)) {
            case RRQ:

                if (in.getMode().compareTo("octet")==0){
                    typePack=new Read();
                    typePack.message=in.message;
                    typePack.length=datagramPacket.getLength();
                }else {
                    String e = "error code 2: Illegal TFTP operation";
                    typePack = new Error(4,e);
                }
                break;
            case WRQ:
                typePack=new Write();
                typePack.message=in.message;
                typePack.length=datagramPacket.getLength();
                break;
            case DATA:
                typePack=new Data();
                typePack.message=in.message;
                typePack.length=datagramPacket.getLength();
                break;
            case ACK:
                typePack=new Ack();
                typePack.message=in.message;
                typePack.length=datagramPacket.getLength();
                break;
            case ERROR:
                typePack=new Error();
                typePack.message=in.message;
                typePack.length=datagramPacket.getLength();
                break;
        }



        typePack.host=datagramPacket.getAddress();
        typePack.port=datagramPacket.getPort();

        return typePack;
    }

    public void send(InetAddress ip, DatagramSocket sock) throws IOException {
        sock.send(new DatagramPacket(message,length,ip, serverPort));
    }

    public void send(InetAddress ip, int port, DatagramSocket s) throws IOException {
        s.send(new DatagramPacket(message,length,ip,port));
    }

    // DatagramPacket like methods

    public InetAddress getAddress() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void setServerPort(int port) {
        this.serverPort = port;
    }

    public int getLength() {
        return length;
    }

    // Utility methods to manipulate the packets

    protected void put(int at, short value) {
        message[at++] = (byte)(value >>> 8);
        message[at] = (byte)(value % 256);
    }

    protected void put(int at, String value, byte del) {
        value.getBytes(0, value.length(), message, at);
        message[at + value.length()] = del;
    }

    protected int get(int at) {
        return (message[at] & 0xff) << 8 | message[at+1] & 0xff;
    }

    protected String get (int at, byte del) {
        StringBuffer result = new StringBuffer();

        while (message[at] != del) {
            result.append((char)message[at++]);
        }

        return result.toString();
    }
    protected String getMode (){
        int i =fileOffset;
        while (message[i]!=0){
            i++;
        }
        i++;
        StringBuffer result=new StringBuffer();
        while (message[i]!=0){
            result.append((char) message[i]);
            i++;
        }
        return result.toString();
    }
    public static int defautPort(){
        return 69;
    }
}
