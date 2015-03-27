package com.waikato;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by lucas on 24/03/15.
 */

public class Tftp {

    public static void main(String argv[]){
        String host="";
         /*if (isRequest(args[0])){
            System.out.println("passed");
        }else
            System.out.println("File not found.");*/
        try {

            // Process command line

            if (argv.length==0)
                throw new Exception("usage:  Tftp server[:port] file [ local-file ]");

            if (argv.length==1)
                host="localhost";
            else
                host=argv[0];

            String fileName=argv[argv.length-1];

            // Create socket and open output file

            InetAddress server = InetAddress.getByName(host);
            DatagramSocket sock = new DatagramSocket();

            FileOutputStream outFile = new FileOutputStream("alomocada");

            // Send request to server

            Read reqPak = new Read(fileName);
            reqPak.send(server,sock);

            int pakLen= Packet.maxTftpPakLen;

            // Process the transfer

            for (int pakCount=0, bytesOut=512; bytesOut==512; pakCount++) {
                Packet inPak = Packet.receive(sock);

                if (inPak instanceof Error)  {
                    Error p=(Error)inPak;
                    throw new Exception(p.message());
                }
                else if (inPak instanceof Data) {
                    Data p=(Data)inPak;

                    int blockNum=p.blockNumber();
                    bytesOut=p.write(outFile);

                    Ack ack=new Ack(blockNum);
                    ack.send(p.getAddress(),p.getPort(),sock);
                }
                else
                    throw new Exception("Unexpected response from server");
            }
            outFile.close();
            sock.close();
        }
        catch (IOException e) {
            System.out.println("IO error, transfer aborted");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}