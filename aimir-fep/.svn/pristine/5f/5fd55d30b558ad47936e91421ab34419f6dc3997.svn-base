package com.aimir.fep.bypass.sts.cmd;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSDataReq;
import com.aimir.fep.util.DataUtil;

public class GetSTSTokenReq extends STSDataFrame {

    public GetSTSTokenReq(int count) throws Exception {
        req = new STSDataReq(CMD.GetToken, new byte[]{DataUtil.getByteToInt(count)});
    }
}
