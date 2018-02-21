package com.aimir.fep.bypass.sts.cmd;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSException;

public class SetPaymentModeRes extends STSDataFrame {
    
    public SetPaymentModeRes(byte[] bx) throws Exception {
        super.decode(bx);
    }

    public int getResult() throws Exception {
        if (res.getResult() == 0x00)
            return 0;
        else
            throw new STSException(res.getRdata());
    }
}
