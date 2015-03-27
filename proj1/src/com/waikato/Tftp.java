package com.waikato;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by lucas on 24/03/15.
 */

public class Tftp {

    public static void main(String argv[]){
        String hostName = "";
        String localFile = "";
        int port;

        try {

            if (argv.length<2){
                throw new Exception("usage:  Tftp server[:port] file [ local-file ]");
            }
            if (argv[0].contains(":")){
                String[] serverName = argv[0].split(":");
                hostName=serverName[0];
                port = Integer.parseInt(serverName[1]);
            }else {
                hostName = argv[0];
                port = Packet.defautPort();
            }
            String fileName=argv[1];
            if (argv.length>2){
                localFile = argv[2];
            }else {
                localFile = fileName;
            }

            InetAddress server = InetAddress.getByName(hostName);
            DatagramSocket sock = new DatagramSocket();
            FileOutputStream outFile = new FileOutputStream(localFile);

            Read request = new Read(fileName);
            request.setServerPort(port);
            request.send(server, sock);

            int byteM =512;
             do {

                Packet packReceived = Packet.receive(sock);

                if (packReceived instanceof Error)  {
                    Error p=(Error)packReceived;
                    throw new Exception(p.message());
                }
                else if (packReceived instanceof Data) {
                    Data p=(Data)packReceived;

                    int blockNum=p.blockNumber();
                    byteM = p.write(outFile);

                    //send Ack
                    Ack ack=new Ack(blockNum);
                    ack.send(p.getAddress(),p.getPort(),sock);
                }
                else
                    throw new Exception("Unexpected response from server");
            }while(byteM==512);
            outFile.close();
            sock.close();
            throw new Exception("transfer done");
        }
        catch (IOException e) {
            System.out.println("transfer aborted");
            System.exit(1);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

}