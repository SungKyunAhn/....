package com.aimir.fep.bypass.sts.cmd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSException;
import com.aimir.fep.util.DataUtil;

public class GetEmergencyCreditRes extends STSDataFrame {
    private static Log log = LogFactory.getLog(GetEmergencyCreditRes.class);
    
    private int mode = 0;
    private int day = 0;
    
    public GetEmergencyCreditRes(byte[] bx) throws Exception {
        super.decode(bx);
        parse();
    }
    
    private void parse() throws Exception {
        if (res.getResult() == 0x00) {
            int pos = 0;
            byte[] b = new byte[1];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            mode = DataUtil.getIntToBytes(b);
            
            log.debug("MODE[" + mode + "]");
            
            b = new byte[2];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            day = DataUtil.getIntTo2Byte(b);
            log.debug("DAY[" + day + "]");
        }
        else throw new STSException(res.getRdata());
    }
    
    public int getMode() {
        return this.mode;
    }
    
    public int getDay() {
        return this.day;
    }
}
