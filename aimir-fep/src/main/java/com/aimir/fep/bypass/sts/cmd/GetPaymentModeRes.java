package com.aimir.fep.bypass.sts.cmd;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSException;
import com.aimir.fep.util.DataUtil;

public class GetPaymentModeRes extends STSDataFrame {

    public GetPaymentModeRes(byte[] bx) throws Exception {
        super.decode(bx);
    }
    
    public int getMode() throws Exception {
        if (res.getResult() == 0x00)
            return DataUtil.getIntToBytes(res.getRdata());
        else
            throw new STSException(res.getRdata());
    }
}
