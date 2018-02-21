package com.aimir.fep.bypass.sts.cmd;

import java.io.ByteArrayOutputStream;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSDataReq;
import com.aimir.fep.util.DataUtil;

public class SuniFirmwareUpdateKeyWriteReq extends STSDataFrame {

    public SuniFirmwareUpdateKeyWriteReq(int number, byte[] key) throws Exception {
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            out.write(DataUtil.getByteToInt(number));
            out.write(key);
            req = new STSDataReq(CMD.FirmwareUpdateKeyWrite, out.toByteArray());
        }
        finally {
            if (out != null) out.close();
        }
    }
}
