package com.aimir.fep.bypass.sts.cmd;

import java.io.ByteArrayOutputStream;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSDataReq;
import com.aimir.fep.util.DataUtil;

public class SetRFSetupReq extends STSDataFrame {
    
    public SetRFSetupReq(Integer channel, Integer panId) throws Exception {
    	ByteArrayOutputStream out = null;
        
        try {
            out = new ByteArrayOutputStream();
            
            // E_DT
            out.write(DataUtil.getByteToInt(channel));
            
            out.write(DataUtil.get2ByteToInt(panId));
            
            byte[] b = out.toByteArray();
            out = new ByteArrayOutputStream();
            // out.write(DataUtil.getByteToInt(b.length));
            out.write(b);
            
            req = new STSDataReq(CMD.SetRFSetup, out.toByteArray());
        }
        finally {
            if (out != null) out.close();
        }
    }
}
