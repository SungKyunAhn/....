package com.aimir.fep.bypass.sts.cmd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSException;
import com.aimir.fep.util.DataUtil;

public class SuniFirmwareUpdateKeyReadRes extends STSDataFrame {
    private static Log log = LogFactory.getLog(SuniFirmwareUpdateKeyReadRes.class);
    
    private int number = 0;
    private byte[] data = new byte[32];
    
    public SuniFirmwareUpdateKeyReadRes(byte[] bx) throws Exception {
        super.decode(bx);
        parse();
    }
    
    private void parse() throws STSException {
        if (res.getResult() != 0x00) throw new STSException(res.getRdata());
        else {
            int pos = 0;
            byte[] b = new byte[2];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            number = DataUtil.getIntTo2Byte(b);
            log.debug("NUMBER[" + number + "]");
            
            System.arraycopy(res.getRdata(), pos, data, 0, data.length);
            pos += data.length;
        }
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    
}
