package com.waikato;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by lucas on 26/03/15.
 */
public class Data extends Packet {

    // Ctors

    protected Data() {}

    public Data(int blockNumber, FileInputStream in) throws IOException {
        this.message=new byte[maxTftpPakLen];

        this.put(opOffset,tftpDATA);
        this.put(blkOffset,(short)blockNumber);
        length=in.read(message,dataOffset,maxTftpData)+4;
    }

    // Accessors

    public int blockNumber() {
        return this.get(blkOffset);
    }

    public void data(byte[] buffer) {
        buffer = new byte[length-4];

        for (int i=0; i<length-4; i++) buffer[i]=message[i+dataOffset];
    }

    // Direct File IO

    public int write(FileOutputStream out) throws IOException {
        out.write(message,dataOffset,length-4);

        return(length-4);
    }
}