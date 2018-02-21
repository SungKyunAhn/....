package com.aimir.fep.bypass.sts.cmd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSException;
import com.aimir.fep.util.DataUtil;

public class GetRFSetupRes extends STSDataFrame {
    private static Log log = LogFactory.getLog(GetRemainingCreditRes.class);
    
    private Integer channel = null;
    private Integer panId = null;
    
    public GetRFSetupRes(byte[] bx) throws Exception {
        super.decode(bx);
        parse();
    }
    
    private void parse() throws Exception {
        if (res.getResult() == 0x00) {
            int pos = 0;
            byte[] b = new byte[1];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            channel = DataUtil.getIntToBytes(b);
            log.debug("CHANNEL[" + channel + "]");
            
            b = new byte[2];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            panId = DataUtil.getIntTo2Byte(b);
            log.debug("PAN_ID[" + panId + "]");
        }
        else
            throw new STSException(res.getRdata());
    }

	public Integer getChannel() {
		return channel;
	}

	public Integer getPanId() {
		return panId;
	}
    
    
    
}
