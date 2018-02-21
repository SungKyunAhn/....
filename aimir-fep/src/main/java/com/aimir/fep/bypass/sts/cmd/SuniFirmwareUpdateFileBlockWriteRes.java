package com.aimir.fep.bypass.sts.cmd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSException;
import com.aimir.fep.util.DataUtil;

public class SuniFirmwareUpdateFileBlockWriteRes extends STSDataFrame {
    private static Log log = LogFactory.getLog(SuniFirmwareUpdateFileBlockWriteRes.class);
    
    private int number = 0;
    private int length = 0;
    private byte[] data = null;
    
    public SuniFirmwareUpdateFileBlockWriteRes(byte[] bx) throws Exception {
        super.decode(bx);
        parse();
    }
    
    private void parse() throws Exception {
        if (res.getResult() == 0x00) {
            int pos = 0;
            
            byte[] b = new byte[2];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            number = DataUtil.getIntTo2Byte(b);
            log.debug("NUMBER[" + number + "]");
            
            b = new byte[2];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            length = DataUtil.getIntTo2Byte(b);
            log.debug("LENGTH[" + length + "]");
            
            data = new byte[length];
            System.arraycopy(res.getRdata(), pos, data, 0, b.length);
            pos += b.length;
        }
        else throw new STSException(res.getRdata());
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
