package com.waikato;

import java.net.DatagramSocket;

/**
 * Created by lucas on 24/03/15.
 */
public class Tftp {
    
    public static void main(String[] args){
        if (isRequest(args[0])){
            System.out.println("passed");
        }else
            System.out.println("File not found.");
        try {
            DatagramSocket sock = new DatagramSocket(Packet.tftpPort);
            System.out.println("Server Ready.  Port:  "+sock.getLocalPort());

            // Listen for requests

            while (1==1) {
                Packet in= Packet.receive(sock);

                // This server will only respond to RRQ

                if (in instanceof Read) {
                    System.out.println("Request from "+in.getAddress());
                    Transfer t = new Transfer((Read)in);
                }
            }
        }
        catch(Exception e) { System.out.println("Server terminated"); }
    }
    public static boolean isRequest(String str){

        if(str.matches(".*[^a-zA-Z./\\s].*"))
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
