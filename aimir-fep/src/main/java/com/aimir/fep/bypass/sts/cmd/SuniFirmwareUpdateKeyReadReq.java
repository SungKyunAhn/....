package com.aimir.fep.bypass.sts.cmd;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSDataReq;
import com.aimir.fep.util.DataUtil;

public class SuniFirmwareUpdateKeyReadReq extends STSDataFrame {

    public SuniFirmwareUpdateKeyReadReq(int number) throws Exception {
        req = new STSDataReq(CMD.FirmwareUpdateKeyRead, new byte[]{DataUtil.getByteToInt(number)});
    }
}
