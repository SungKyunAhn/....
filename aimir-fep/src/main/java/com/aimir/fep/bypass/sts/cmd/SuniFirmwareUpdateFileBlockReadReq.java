package com.aimir.fep.bypass.sts.cmd;

import java.io.ByteArrayOutputStream;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSDataReq;
import com.aimir.fep.util.DataUtil;

public class SuniFirmwareUpdateFileBlockReadReq extends STSDataFrame {
    
    public SuniFirmwareUpdateFileBlockReadReq(int number, int length) throws Exception {
        ByteArrayOutputStream out = null;
        
        try {
            out = new ByteArrayOutputStream();
            
            out.write(DataUtil.get2ByteToInt(number));
            out.write(DataUtil.get2ByteToInt(length));
            
            req = new STSDataReq(CMD.FirmwareUpdateBlockRead, out.toByteArray());
        }
        finally {
            if (out != null) out.close();
        }
    }
}
