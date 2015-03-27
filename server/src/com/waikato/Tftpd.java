package com.waikato;

import java.net.DatagramSocket;

/**
 * Created by lucas on 24/03/15.
 */
public class Tftpd {
    
    public static void main(String[] args){

        int port = 69;
        if (args.length >0){
            try{
                port=Integer.parseInt(args[0]);
            }catch (NumberFormatException e){
                System.out.println("Invalid port number: "+args[0]);
                System.out.println("Usage: java Tftpd [ port ]");
                System.exit(1);
            }
            if (port>65535) {
                System.out.println("Invalid port number: "+port);
                System.out.println("Usage: java Tftpd [ port ]");
                System.exit(1);
            }
        }

        try {
            DatagramSocket sock = new DatagramSocket(port);
            System.out.println("Server Ready.  Port:  "+sock.getLocalPort());

            // Requests

            while (true) {
                Packet in = Packet.receive(sock);
                //Only respond read requests

                if (in instanceof Read) {
                    System.out.println("Received a RRQ");
                    System.out.println("Request from "+in.getAddress());
                    Transfer transfer = new Transfer((Read)in);
                }

                if (in instanceof Write) {
                    System.out.println("Received a WRQ");
                    System.out.println("Request from "+in.getAddress());
                    String e = "error code 2: access violation";
                    Error ePak = new Error(1,e);

                    try {
                        ePak.send(in.getAddress(),in.getPort(),sock);
                    } catch (Exception f) {
                        System.out.println(f.getMessage());
                    }
                    System.out.println(ePak.message());
                }
                if (in instanceof Error){
                    try {
                        in.send(in.getAddress(),in.getPort(),sock);
                    } catch (Exception f) {
                        System.out.println(f.getMessage());
                    }
                    System.out.println(((Error) in).message());
                }

            }
        }
        catch(Exception e) {
            System.out.println(e.toString());
            System.out.println("Server terminated"); }
    }


}
