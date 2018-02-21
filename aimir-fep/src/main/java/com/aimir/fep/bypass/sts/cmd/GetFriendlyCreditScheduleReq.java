package com.aimir.fep.bypass.sts.cmd;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSDataReq;
import com.aimir.fep.util.DataUtil;

public class GetFriendlyCreditScheduleReq extends STSDataFrame {

    public GetFriendlyCreditScheduleReq(byte[] bx) throws Exception {
        super.decode(bx);
    }
    
    public GetFriendlyCreditScheduleReq(int mode) throws Exception {
        req = new STSDataReq(CMD.GetFriendlyCredit, new byte[]{DataUtil.getByteToInt(mode)});
    }
}
