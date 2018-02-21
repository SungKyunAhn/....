package com.aimir.fep.bypass.sts.cmd;

import java.io.ByteArrayOutputStream;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSDataReq;
import com.aimir.fep.util.DataUtil;

public class SetSTSSetupReq extends STSDataFrame {
    
    public SetSTSSetupReq(String date, String tokenKey1, String tokenKey2, String stsNumber) throws Exception {
    	ByteArrayOutputStream out = null;
        
        try {
            out = new ByteArrayOutputStream();
            
            // E_DT
            out.write(DataUtil.get2ByteToInt(Integer.parseInt(date.substring(0, 4))));
            out.write(DataUtil.getByteToInt(Integer.parseInt(date.substring(4, 6))));
            out.write(DataUtil.getByteToInt(Integer.parseInt(date.substring(6, 8))));
            out.write(DataUtil.getByteToInt(Integer.parseInt(date.substring(8, 10))));
            out.write(DataUtil.getByteToInt(Integer.parseInt(date.substring(10, 12))));
            
            out.write(tokenKey1.getBytes());
            out.write(tokenKey2.getBytes());
            out.write(stsNumber.getBytes());
            
            byte[] b = out.toByteArray();
            out = new ByteArrayOutputStream();
            // out.write(DataUtil.getByteToInt(b.length));
            out.write(b);
            
            req = new STSDataReq(CMD.SetSTSSetup, out.toByteArray());
        }
        finally {
            if (out != null) out.close();
        }
    }
}
