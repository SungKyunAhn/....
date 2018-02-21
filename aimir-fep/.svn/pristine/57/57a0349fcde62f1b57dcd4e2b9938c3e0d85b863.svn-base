package com.aimir.fep.bypass.sts.cmd;

import java.io.ByteArrayOutputStream;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSDataReq;
import com.aimir.fep.util.DataUtil;

public class SuniFirmwareUpdateFileBlockWriteReq extends STSDataFrame {
    
    public SuniFirmwareUpdateFileBlockWriteReq(int number, byte[] data) throws Exception {
        ByteArrayOutputStream out = null;
        
        try {
            out = new ByteArrayOutputStream();
            
            out.write(DataUtil.get2ByteToInt(number));
            out.write(DataUtil.get2ByteToInt(data.length));
            out.write(data);
            
            req = new STSDataReq(CMD.FirmwareUpdateBlockWrite, out.toByteArray());
        }
        finally {
            if (out != null) out.close();
        }
    }
}
