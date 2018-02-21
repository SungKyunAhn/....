package com.aimir.fep.bypass.sts.cmd;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSException;

public class SetMessageRes extends STSDataFrame {
    
    public SetMessageRes(byte[] bx) throws Exception {
        super.decode(bx);
    }
    
    public byte getResult() throws Exception {
        if (res.getResult() == 0x00)
            return res.getResult();
        else
            throw new STSException(res.getRdata());
    }
}
