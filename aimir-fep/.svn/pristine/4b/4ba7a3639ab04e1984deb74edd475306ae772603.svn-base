package com.aimir.fep.bypass.sts;

public class STSDataRes {
    private byte[] cmd = new byte[1];
    private byte[] result = new byte[1];
    private byte[] rdata = null;
    
    public void decode(byte[] bx) throws Exception {
        int pos = 0;
        System.arraycopy(bx, pos, cmd, 0, cmd.length);
        pos += cmd.length;
        
        System.arraycopy(bx, pos, result, 0, result.length);
        pos += result.length;
        
        rdata = new byte[bx.length - 2];
        System.arraycopy(bx, pos, rdata, 0, rdata.length);
    }
    
    public byte getResult() {
        return result[0];
    }
    
    public byte[] getRdata() {
        return rdata;
    }
}
