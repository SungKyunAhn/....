package com.aimir.fep.bypass.sts.cmd;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSDataReq;

public class SetPaymentModeReq extends STSDataFrame {
    public SetPaymentModeReq(int mode) throws Exception {
        req = new STSDataReq(CMD.SetPaymentMode, new byte[]{(byte)mode});
    }
}
