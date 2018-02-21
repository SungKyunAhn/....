package com.aimir.fep.bypass.sts.cmd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSException;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class GetRemainingCreditRes extends STSDataFrame {
    private static Log log = LogFactory.getLog(GetRemainingCreditRes.class);
    
    private String dt = null;
    private double credit = 0.0;
    
    public GetRemainingCreditRes(byte[] bx) throws Exception {
        super.decode(bx);
        parse();
    }
    
    private void parse() throws Exception {
        if (res.getResult() == 0x00) {
            int pos = 0;
            byte[] b = new byte[6];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            dt = String.format("%4d%02d%02d%02d%02d",
                    DataUtil.getIntTo2Byte(new byte[]{b[0], b[1]}),
                    DataUtil.getIntToByte(b[2]),
                    DataUtil.getIntToByte(b[3]),
                    DataUtil.getIntToByte(b[4]),
                    DataUtil.getIntToByte(b[5]));
            
            log.debug(Hex.decode(b) + " DT[" + dt + "]");
            
            b = new byte[4];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            log.debug(Hex.decode(b));
            long lcredit = 0l;
            if(b[0] < 0) {
            	lcredit = (((((~b[0]) & 0xff) << 8) + (((~b[1]) & 0xff) << 8) + (((~b[2]) & 0xff) << 8) + ((~b[3]) & 0xff)) + 1)*-1;
            } else {
            	lcredit = DataUtil.getIntToBytes(b);
            }

            b = new byte[1];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            log.debug(Hex.decode(b));
            int dec = DataUtil.getIntToBytes(b);
            
            credit = (double)lcredit / Math.pow(10, dec);
            log.debug("Credit[" + credit + "]");
        }
        else
            throw new STSException(res.getRdata());
    }
    
    public String getDt() {
        return dt;
    }
    
    public double getCredit() {
        return credit;
    }
}
