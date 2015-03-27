package com.waikato;

/**
 * Created by lucas on 22/03/15.
 */
public class Read extends Packet {

    static final String reqType = "octet";

    protected Read() {}

    public Read(String filename) {

        length=2+filename.length()+1+reqType.length()+1;
        message = new byte[length];
        put(opOffset, RRQ);
        put(fileOffset,filename,(byte)0);
        put(fileOffset+filename.length()+1,reqType,(byte)0);
    }

    public String fileName() {
        return this.get(fileOffset,(byte)0);
    }

    public String requestType() {
        String fname = fileName();
        return this.get(fileOffset+fname.length()+1,(byte)0);
    }
}