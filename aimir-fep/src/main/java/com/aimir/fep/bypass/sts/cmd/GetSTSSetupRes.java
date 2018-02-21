package com.aimir.fep.bypass.sts.cmd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSException;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class GetSTSSetupRes extends STSDataFrame {
    private static Log log = LogFactory.getLog(GetRemainingCreditRes.class);
    
    private String date = null;
    private String stsNumber = null;
    
    public GetSTSSetupRes(byte[] bx) throws Exception {
        super.decode(bx);
        parse();
    }
    
    private void parse() throws Exception {
        if (res.getResult() == 0x00) {
            int pos = 0;
            byte[] b = new byte[6];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            date = String.format("%4d%02d%02d%02d%02d",
                    DataUtil.getIntTo2Byte(new byte[]{b[0], b[1]}),
                    DataUtil.getIntToByte(b[2]),
                    DataUtil.getIntToByte(b[3]),
                    DataUtil.getIntToByte(b[4]),
                    DataUtil.getIntToByte(b[5]));
            
            log.debug(Hex.decode(b) + " DATE[" + date + "]");
            
            b = new byte[11];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            stsNumber = new String(b);
            log.debug("STS_NUMBER[" + stsNumber + "]");
        }
        else
            throw new STSException(res.getRdata());
    }

	public String getDate() {
		return date;
	}

	public String getStsNumber() {
		return stsNumber;
	}
    
}
