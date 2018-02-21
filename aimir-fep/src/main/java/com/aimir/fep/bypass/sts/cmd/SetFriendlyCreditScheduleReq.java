package com.aimir.fep.bypass.sts.cmd;

import java.io.ByteArrayOutputStream;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSDataReq;
import com.aimir.fep.util.DataUtil;

public class SetFriendlyCreditScheduleReq extends STSDataFrame {
    
    public SetFriendlyCreditScheduleReq(String date, int[] dayType, String[] fromTime, String[] endTime) throws Exception {
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            
            // E_DT
            out.write(DataUtil.get2ByteToInt(Integer.parseInt(date.substring(0,4))));
            out.write(DataUtil.getByteToInt(Integer.parseInt(date.substring(4, 6))));
            out.write(DataUtil.getByteToInt(Integer.parseInt(date.substring(6, 8))));
            
            // COUNT
            out.write(DataUtil.getByteToInt(dayType.length));
            
            // DAYTYPE and FROMTIME, ENDTIME
            for (int i = 0; i < dayType.length; i++) {
                out.write(DataUtil.getByteToInt(dayType[i]));
                out.write(DataUtil.getByteToInt(Integer.parseInt(fromTime[i].substring(0, 2))));
                out.write(DataUtil.getByteToInt(Integer.parseInt(fromTime[i].substring(2, 4))));
                out.write(DataUtil.getByteToInt(Integer.parseInt(endTime[i].substring(0, 2))));
                out.write(DataUtil.getByteToInt(Integer.parseInt(endTime[i].substring(2, 4))));
            }
            
            byte[] b = out.toByteArray();
            
            out = new ByteArrayOutputStream();
            // LEN
            // out.write(DataUtil.getByteToInt(b.length));
            out.write(b);
            req = new STSDataReq(CMD.SetFriendlyCredit, out.toByteArray());
        }
        finally {
            if (out != null) out.close();
        }
    }
}
