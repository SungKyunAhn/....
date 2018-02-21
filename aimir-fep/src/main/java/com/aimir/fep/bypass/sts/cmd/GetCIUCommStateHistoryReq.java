package com.aimir.fep.bypass.sts.cmd;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSDataReq;

public class GetCIUCommStateHistoryReq extends STSDataFrame {
    
    public GetCIUCommStateHistoryReq() throws Exception {
        req = new STSDataReq(CMD.GetCIUCommStateHistory, new byte[]{});
    }
}
