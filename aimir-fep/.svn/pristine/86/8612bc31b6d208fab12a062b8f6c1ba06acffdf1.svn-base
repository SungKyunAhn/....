package com.aimir.fep.bypass.sts.cmd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSException;
import com.aimir.fep.util.DataUtil;

public class GetFriendlyCreditScheduleRes extends STSDataFrame {
    private static Log log = LogFactory.getLog(GetFriendlyCreditScheduleRes.class);
    
    private int fcMode = 0;
    private String date = null;
    private int count = 0;
    private int[] dayType = null;
    private String[] fromTime = null;
    private String[] endTime = null;
    
    public GetFriendlyCreditScheduleRes(byte[] bx) throws Exception {
        super.decode(bx);
        parse();
    }
    
    public void parse() throws Exception {
        if (res.getResult() == 0x00) {
            int pos = 0;
            
            byte[] b = new byte[1];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            fcMode = DataUtil.getIntToBytes(b);
            log.debug("FC_MODE[" + fcMode + "]");
            
            b = new byte[4];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            date = String.format("%4d%02d%02d",
                    DataUtil.getIntTo2Byte(new byte[]{b[0], b[1]}),
                    DataUtil.getIntToByte(b[2]),
                    DataUtil.getIntToByte(b[3])
                    );
            log.debug("E_DT[" + date + "]");
            
            b = new byte[1];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            count = DataUtil.getIntToBytes(b);
            log.debug("COUNT[" + count + "]");
            
            dayType = new int[count];
            fromTime = new String[count];
            endTime = new String[count];
            for (int i = 0; i < count; i++) {
                b = new byte[1];
                System.arraycopy(res.getRdata(), pos, b, 0, b.length);
                pos += b.length;
                dayType[i] = DataUtil.getIntToBytes(b);
                
                b = new byte[2];
                System.arraycopy(res.getRdata(), pos, b, 0, b.length);
                pos += b.length;
                fromTime[i] = String.format("%02d%02d", DataUtil.getIntToByte(b[0]), DataUtil.getIntToByte(b[1]));
                
                b = new byte[2];
                System.arraycopy(res.getRdata(), pos, b, 0, b.length);
                pos += b.length;
                endTime[i] = String.format("%02d%02d", DataUtil.getIntToByte(b[0]), DataUtil.getIntToByte(b[1]));
                log.debug(i+">>DAYTYPE[" + dayType[i] +"], FROMTIME[" + fromTime[i] + "], ENDTIME[" + endTime[i] +"]");
            }
        }
        else
            throw new STSException(res.getRdata());
    }

    public int getFcMode() {
        return fcMode;
    }

    public String getDate() {
        return date;
    }

    public int getCount() {
        return count;
    }

    public int[] getDayType() {
        return dayType;
    }

    public String[] getFromTime() {
        return fromTime;
    }

    public String[] getEndTime() {
        return endTime;
    }
}
