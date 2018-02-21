package com.aimir.fep.bypass.sts.cmd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSException;
import com.aimir.fep.util.DataUtil;

public class GetCIUCommStateHistoryRes extends STSDataFrame {
    private static Log log = LogFactory.getLog(GetCIUCommStateHistoryRes.class);
    
    private int count = 0;
    private String[] dts = null;
    private int[] flags = null;
    
    public GetCIUCommStateHistoryRes(byte[] bx) throws Exception {
        super.decode(bx);
        parse();
    }
    
    private void parse() throws Exception {
        if (res.getResult() == 0x00) {
            int pos = 0;
            byte[] b = new byte[1];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            count = DataUtil.getIntToBytes(b);
            
            log.debug("COUNT[" + count + "]");
            
            dts = new String[count];
            flags = new int[count];
            
            for (int i = 0; i < count; i++) {
                b = new byte[6];
                System.arraycopy(res.getRdata(), pos, b, 0, b.length);
                pos += b.length;
                
                dts[i] = String.format("%4d%02d%02d%02d%02d", 
                        DataUtil.getIntTo2Byte(new byte[]{b[0],b[1]}),
                        DataUtil.getIntToByte(b[2]),
                        DataUtil.getIntToByte(b[3]),
                        DataUtil.getIntToByte(b[4]),
                        DataUtil.getIntToByte(b[5]));
                
                b = new byte[1];
                System.arraycopy(res.getRdata(), pos, b, 0, b.length);
                pos += b.length;
                flags[i] = DataUtil.getIntToBytes(b);
                
                log.debug("DATE_TIME[" + dts[i] + "] FLAG[" + flags[i] + "]");
            }
        }
        else throw new STSException(res.getRdata());
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String[] getDts() {
        return dts;
    }

    public void setDts(String[] dts) {
        this.dts = dts;
    }

    public int[] getFlags() {
        return flags;
    }

    public void setFlags(int[] flags) {
        this.flags = flags;
    }
    
}
