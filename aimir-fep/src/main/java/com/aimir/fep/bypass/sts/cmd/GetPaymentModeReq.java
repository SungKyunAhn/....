package com.aimir.fep.bypass.sts.cmd;

import com.aimir.fep.bypass.sts.STSDataReq;
import com.aimir.fep.bypass.sts.STSDataFrame;

public class GetPaymentModeReq extends STSDataFrame {
    public GetPaymentModeReq() throws Exception {
        req = new STSDataReq(CMD.GetPaymentMode, new byte[]{});
    }
}
