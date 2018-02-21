package com.aimir.fep.bypass.sts.cmd;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSDataReq;
import com.aimir.fep.util.DataUtil;

public class GetSpecificMonthNetChargeReq extends STSDataFrame {
    private static Log log = LogFactory.getLog(GetSpecificMonthNetChargeReq.class);

    public GetSpecificMonthNetChargeReq(String yyyymm) throws Exception {
        // yyyymm을 이용하여 개월수를 계산한다. 이전달은 1, 두달전은 2
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        Calendar cal = Calendar.getInstance();
        int idx = 1;
        for (; idx < 13; idx++) {
            cal.add(Calendar.MONTH, -1);
            if (yyyymm.equals(sdf.format(cal.getTime())))
                break;
        }
        log.debug("MONTH_IDX[" + idx + "]");
        
        req = new STSDataReq(CMD.GetSpecificMonthNetCharge, new byte[]{DataUtil.getByteToInt(idx)});
    }
}
