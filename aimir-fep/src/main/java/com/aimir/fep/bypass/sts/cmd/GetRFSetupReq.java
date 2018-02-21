package com.aimir.fep.bypass.sts.cmd;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSDataReq;

public class GetRFSetupReq extends STSDataFrame {
    
    public GetRFSetupReq() throws Exception {
    	req = new STSDataReq(CMD.GetRFSetup, new byte[]{});
    }
}
