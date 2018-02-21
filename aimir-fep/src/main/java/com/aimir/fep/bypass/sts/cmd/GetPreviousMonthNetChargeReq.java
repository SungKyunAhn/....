package com.aimir.fep.bypass.sts.cmd;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSDataReq;

public class GetPreviousMonthNetChargeReq extends STSDataFrame {
    
    public GetPreviousMonthNetChargeReq() throws Exception {
        req = new STSDataReq(CMD.GetPreviousMonthNetCharge, new byte[]{});
    }
}
