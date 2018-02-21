package com.aimir.fep.bypass.sts;

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.bypass.sts.STSDataFrame.CMD;
import com.aimir.fep.util.Hex;

public class STSDataReq {
    private static Log log = LogFactory.getLog(STSDataReq.class);
    
    private byte cmd = 0x00;
    private byte[] para = null;
    
    public STSDataReq(CMD cmd, byte[] para) {
        this.cmd = cmd.getCmd();
        this.para = para;
    }
    
    public byte[] encode() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(cmd);
        out.write(para);
        
        byte[] b = out.toByteArray();
        log.debug(Hex.decode(b));
        return b;
    }
}
