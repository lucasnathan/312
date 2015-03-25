package com.waikato;

import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by lucas on 26/03/15.
 */
class TftpException extends Exception {
    TftpException() { super(); }
    TftpException(String s) { super(s); }
}

class Transfer extends Thread {

    protected DatagramSocket sock;
    protected InetAddress host;
    protected int port;
    protected FileInputStream source;

    public Transfer(Read request) throws TftpException {
        try {
            sock = new DatagramSocket();

            host = request.getAddress();
            port = request.getPort();

            File srcFile = new File(request.fileName());

            if (srcFile.exists() && srcFile.isFile() && srcFile.canRead()) {
                source = new FileInputStream(srcFile);
                this.start();
            } else
                throw new TftpException("access violation");

        } catch (Exception e) {
            Error ePak = new Error(1, e.getMessage());

            try {
                ePak.send(host, port, sock);
            } catch (Exception f) {
            }

            System.out.println("Client start failed:  " + e.getMessage());
        }
    }

    public void run() {
        int bytesRead = Packet.maxTftpPakLen;

        try {
            for (int blkNum=0; bytesRead== Packet.maxTftpPakLen; blkNum++) {
                Data outPak=new Data(blkNum,source);
                bytesRead=outPak.getLength();
                outPak.send(host,port,sock);

                Packet ack= Packet.receive(sock);
                if (!(ack instanceof Ack)) break;
            }
        }
        catch (Exception e) {
            Error ePak = new Error(1,e.getMessage());

            try { ePak.send(host,port,sock); } catch (Exception f) {}

            System.out.println("Client failed:  "+e.getMessage());
        }

        System.out.println("Client terminated");
    }
}