package com.aimir.fep.protocol.nip.frame.payload;

public class Ack_CRC extends PayloadFrame {
    private byte[] crc = new byte[2]; 
    
    public Ack_CRC(byte[] crc) {
        this.crc = crc;
    }
    
    @Override
    public void decode(byte[] bx) {
    }
    
    @Override
    public byte[] encode() throws Exception {
        return crc;
    }
    @Override
    public void setCommandFlow(byte code){ }
    @Override
    public void setCommandType(byte code){ }
    @Override
    public byte[] getFrameTid(){ return null;}
    @Override
    public void setFrameTid(byte[] code){}
}