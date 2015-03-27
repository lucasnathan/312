package com.waikato;

/**
 * Created by lucas on 26/03/15.
 */
public class Ack extends Packet {

    // Ctors

    protected Ack() {}

    public Ack(int blockNumber) {
        length=4;
        this.message = new byte[length];

        put(opOffset, ACK);
        put(blkOffset,(short)blockNumber);
    }

    // Accessors

    public int blockNumber() {
        return this.get(blkOffset);
    }
}