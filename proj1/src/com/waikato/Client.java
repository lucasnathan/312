package com.waikato;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by lucas on 26/03/15.
 */

class UseException extends Exception {
    public UseException() { super(); }
    public UseException(String s) { super(s); }
}

public class Client {

    public static void main(String argv[]) throws TftpException, UseException {
        String host="";

        try {

            // Process command line

            if (argv.length==0)
                throw new UseException("usage:  tftpClient [host] file");

            if (argv.length==1)
                host="localhost";
            else
                host=argv[0];

            String fileName=argv[argv.length-1];

            // Create socket and open output file

            InetAddress server = InetAddress.getByName(host);
            DatagramSocket sock = new DatagramSocket();

            FileOutputStream outFile = new FileOutputStream(fileName);

            // Send request to server

            Read reqPak = new Read(fileName);
            reqPak.send(server,sock);

            int pakLen= Packet.maxTftpPakLen;

            // Process the transfer

            for (int pakCount=0, bytesOut=512; bytesOut==512; pakCount++) {
                Packet inPak = Packet.receive(sock);

                if (inPak instanceof Error)  {
                    Error p=(Error)inPak;
                    throw new TftpException(p.message());
                }
                else if (inPak instanceof Data) {
                    Data p=(Data)inPak;

                    int blockNum=p.blockNumber();
                    bytesOut=p.write(outFile);

                    Ack ack=new Ack(blockNum);
                    ack.send(p.getAddress(),p.getPort(),sock);
                }
                else
                    throw new TftpException("Unexpected response from server");
            };

            outFile.close();
            sock.close();
        }

        catch (UnknownHostException e) { System.out.println("Unknown host "+host); }
        catch (IOException e) { System.out.println("IO error, transfer aborted"); }
        catch (TftpException e) { System.out.println(e.getMessage()); }
        catch (UseException e) { System.out.println(e.getMessage()); }
    }
}