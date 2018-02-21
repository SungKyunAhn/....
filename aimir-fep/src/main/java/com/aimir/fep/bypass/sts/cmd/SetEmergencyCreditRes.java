package com.aimir.fep.bypass.sts.cmd;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSException;

public class SetEmergencyCreditRes extends STSDataFrame {
    
    public SetEmergencyCreditRes(byte[] bx) throws Exception {
        super.decode(bx);
    }
    
    public int getResult() throws Exception {
        if (res.getResult() != 0x00) throw new STSException(res.getRdata());
        else return res.getResult();
    }
}
