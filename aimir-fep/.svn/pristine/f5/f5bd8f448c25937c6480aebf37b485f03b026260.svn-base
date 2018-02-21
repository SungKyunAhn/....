package com.aimir.fep.bypass.sts.cmd;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSDataReq;
import com.aimir.fep.util.DataUtil;

public class GetTariffReq extends STSDataFrame {
    
    public GetTariffReq(int tariffMode) throws Exception {
        req = new STSDataReq(CMD.GetTariff, new byte[]{DataUtil.getByteToInt(tariffMode)});
    }
}
