package com.aimir.fep.bypass.sts.cmd;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSDataReq;

public class GetRemainingCreditReq extends STSDataFrame {

    public GetRemainingCreditReq() throws Exception {
        req = new STSDataReq(CMD.GetRemainingCredit, new byte[]{});
    }
}
