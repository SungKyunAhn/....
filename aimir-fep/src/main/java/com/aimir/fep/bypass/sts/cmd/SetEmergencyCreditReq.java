package com.aimir.fep.bypass.sts.cmd;

import java.io.ByteArrayOutputStream;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSDataReq;
import com.aimir.fep.util.DataUtil;

public class SetEmergencyCreditReq extends STSDataFrame {
    
    public SetEmergencyCreditReq(int mode, int day) throws Exception {
        ByteArrayOutputStream out = null;
        
        try {
            out = new ByteArrayOutputStream();
            
            out.write(DataUtil.getByteToInt(mode));
            out.write(DataUtil.get2ByteToInt(day));
            
            req = new STSDataReq(CMD.SetEmergencyCredit, out.toByteArray());
        }
        finally {
            if (out != null) out.close();
        }
    }
}
