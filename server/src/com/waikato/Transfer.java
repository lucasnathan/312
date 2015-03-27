package com.waikato;

import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by lucas on 26/03/15.
 */

class Transfer extends Thread {

    protected DatagramSocket socket;
    protected InetAddress address;
    protected int port;
    protected FileInputStream source;

    public Transfer(Read request) {
        try {
            socket = new DatagramSocket();
            address = request.getAddress();
            port = request.getPort();

            if (!isRequest(request.fileName())){

                throw new Exception("error code 1: file not found");
            }
            String f = "src/com/waikato/tftpd/"+request.fileName();

            File srcFile = new File(f);


            if (srcFile.exists() && srcFile.isFile() && srcFile.canRead()) {

                source = new FileInputStream(srcFile);
                this.start();
            } else
                throw new Exception("error code 1: file not found");

        } catch (Exception e) {
            Error ePak = new Error(1, e.getMessage());

            try {
                ePak.send(address, port, socket);
            } catch (Exception f) {
            }

            System.out.println(e.getMessage());
        }
    }

    public void run() {
        int bytesRead = Packet.maxTftpPakLen;

        try {
            for (int blkNum=0; bytesRead==Packet.maxTftpPakLen; blkNum++) {

                Data outPak=new Data(blkNum,source);
                bytesRead=outPak.getLength();
                outPak.send(address,port, socket);

                Packet ack= Packet.receive(socket);
                if (!(ack instanceof Ack)) break;
            }
        }
        catch (Exception e) {
            Error ePak = new Error(1,e.getMessage());

            try {
                ePak.send(address,port, socket);
            } catch (Exception f) {
                System.out.println(f.getMessage());
            }

            System.out.println("Client failed:  "+e.getMessage());
        }
    }

    public static boolean isRequest(String str){

        if(str.matches(".*[^a-zA-Z.\\s].*"))
            return false;
        if(str.matches("^/\\..*"))
            return false;
        if(str.matches("^\\..*"))
            return false;
        if(str.matches(".*/\\..*"))
            return false;
        if(str.matches(".*\\s\\..*"))
            return false;
        return true;
    }
}