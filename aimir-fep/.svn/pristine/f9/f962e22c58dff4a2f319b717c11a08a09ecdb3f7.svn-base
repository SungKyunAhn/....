package com.aimir.fep.bypass.sts.cmd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSException;
import com.aimir.fep.util.DataUtil;

public class GetPreviousMonthNetChargeRes extends STSDataFrame {
    private static Log log = LogFactory.getLog(GetPreviousMonthNetChargeRes.class);
    
    private int m_consumption = 0;
    private double m_cost = 0.0;

    public GetPreviousMonthNetChargeRes(byte[] bx) throws Exception {
        super.decode(bx);
        parse();
    }

    private void parse() throws Exception {
        if (res.getResult() == 0x00) {
            int pos = 0;
            
            // M_CONSUMPTION
            byte[] b = new byte[2];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            m_consumption = DataUtil.getIntTo2Byte(b);
            log.debug("M_CONSUMPTION[" + m_consumption + "]");
            
            // M_COST
            b = new byte[4];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            m_cost = (double)DataUtil.getLongToBytes(b);
            
            b = new byte[1];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            m_cost /= Math.pow(10, DataUtil.getIntToBytes(b));
            log.debug("M_COST[" + m_cost + "]");
        }
        else throw new STSException(res.getRdata());
    }

    public int getM_consumption() {
        return m_consumption;
    }

    public double getM_cost() {
        return m_cost;
    }

    
}
