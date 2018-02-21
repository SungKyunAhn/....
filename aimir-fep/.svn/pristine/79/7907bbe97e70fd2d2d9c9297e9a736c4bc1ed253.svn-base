package com.aimir.fep.bypass.sts.cmd;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSDataReq;
import com.aimir.fep.util.DataUtil;
import com.aimir.util.DateTimeUtil;

public class SetMessageReq extends STSDataFrame {

    public SetMessageReq(String msg) throws Exception {
        ByteArrayOutputStream out = null;
        
        try {
            out = new ByteArrayOutputStream();
            String date = DateTimeUtil.getDateString(new Date()).substring(0, 12);

            out.write(DataUtil.get2ByteToInt(Integer.parseInt(date.substring(0, 4))));
            out.write(DataUtil.getByteToInt(Integer.parseInt(date.substring(4, 6))));
            out.write(DataUtil.getByteToInt(Integer.parseInt(date.substring(6, 8))));
            out.write(DataUtil.getByteToInt(Integer.parseInt(date.substring(8, 10))));
            out.write(DataUtil.getByteToInt(Integer.parseInt(date.substring(10, 12))));
            out.write(msg.getBytes().length);
            out.write(msg.getBytes());
            req = new STSDataReq(CMD.Message, out.toByteArray());
        }
        finally {
            if (out != null) out.close();
        }
    }
}
