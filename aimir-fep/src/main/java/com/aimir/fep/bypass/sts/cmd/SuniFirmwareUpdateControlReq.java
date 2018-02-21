package com.aimir.fep.bypass.sts.cmd;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSDataReq;
import com.aimir.fep.util.DataUtil;

public class SuniFirmwareUpdateControlReq extends STSDataFrame {

    public SuniFirmwareUpdateControlReq(int control) throws Exception {
        req = new STSDataReq(CMD.FirmwareUpdateControl, new byte[]{DataUtil.getByteToInt(control)});
    }
}
