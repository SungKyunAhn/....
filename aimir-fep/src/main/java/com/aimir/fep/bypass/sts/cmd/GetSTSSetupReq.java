package com.aimir.fep.bypass.sts.cmd;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSDataReq;

public class GetSTSSetupReq extends STSDataFrame {
    
    public GetSTSSetupReq() throws Exception {
    	req = new STSDataReq(CMD.GetSTSSetup, new byte[]{});
    }
}
