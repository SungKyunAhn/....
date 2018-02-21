package com.aimir.fep.bypass.sts.cmd;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSException;

public class SetSTSTokenRes extends STSDataFrame {
    
    public SetSTSTokenRes(byte[] bx) throws Exception {
        super.decode(bx);
    }
    
    public String getToken() throws Exception {
        if (res.getResult() == 0x00) {
            return new String(res.getRdata());
        }
        else 
            throw new STSException(res.getRdata());
    }
}
