package com.aimir.fep.meter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterCommand;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.fep.BaseTestCase;
import com.aimir.fep.meter.saver.OmniMDSaver;
import com.aimir.fep.util.CmdUtil;
import com.aimir.model.device.OperationList;
import com.aimir.model.system.DeviceModel;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;

public class CmdOffsetCountTest {
    private static Log log = LogFactory.getLog(CmdOffsetCountTest.class);
    
    @Test
    @Transactional
    @Rollback(false)
    public void test() {
        try {

            int[] i = convertOffsetCount(30, ModemType.ZRU, "20141022000000", "20141022000000");
            System.out.println(i[0]+","+i[1]);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    
    public static int[] convertOffsetCount(int lpInterval, ModemType modemType, String fromDate, String toDate)
    throws Exception
    {
    	int nOffset = 0;
        int nCount = 0;

        OperationList op = null;
        int paramType = 2;

        
        if (!"".equals(fromDate) && !"".equals(toDate)) {
            Calendar today = Calendar.getInstance();
            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();
            
            int amount = 1;
            int field = Calendar.DAY_OF_YEAR;
            
            // Ondemand paramType = 0 인 경우 일 수로 계산한다.
            if (paramType == 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                today.setTime(sdf.parse(DateTimeUtil.getDateString(new Date()).substring(0,8)));
                from.setTime(sdf.parse(fromDate.substring(0,8)));
                to.setTime(sdf.parse(toDate.substring(0,8)));
    
                /*
                if (today.before(to)) {
                    throw new Exception("request toDate[" + toDate + "] must not be after today");
                }
    
                if (!today.after(from)) {
                    throw new Exception("request fromDate[" + fromDate + "] must be before today");
                }
                */
                log.debug("today: "+today);
                log.debug("from: "+from);
                log.debug("to: "+to);
            }
            // paramType == 1인 경우 한시간 주기로 시작일의 offset 과 종료일과의 개수를 계산한다.
            else if (paramType == 1) {
                amount = 1; // lpInterval; 주기단위가 없다. 시간단위
                field = Calendar.HOUR;
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
                today.setTime(sdf.parse(DateTimeUtil.getDateString(new Date()).substring(0,12)));
                today.add(field, -amount);
                
                if (fromDate.length() < 12) {
                    fromDate = StringUtil.endAppendNStr('0', fromDate, 12);
                }
                from.setTime(sdf.parse(fromDate.substring(0, 12)));
                
                if ((toDate.length() == 14 && toDate.substring(8, 14).equals("000000")) ||
                        (toDate.length() == 12 && toDate.substring(8, 12).equals("0000"))) {
                    toDate = toDate.substring(0, 8);
                }
                
                if (toDate.length() == 8) {
                    toDate += "2359";
                }
                else if (toDate.length() == 10) {
                    toDate += "59";
                }
                to.setTime(sdf.parse(toDate.substring(0, 12)));
                
                log.debug("today: "+today);
                log.debug("from: "+from);
                log.debug("to: "+to);
            }
            // paramType == 2인 경우 lpInterval 주기로 시작일의 offset 과 종료일과의 개수를 계산한다.
            // paramType == 2인 경우는 fromDate와 toDate가 분까지 있는 것으로 만들어야 한다.
            else if (paramType == 2) {
                amount = lpInterval; 
                field = Calendar.MINUTE;
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
                today.setTime(sdf.parse(DateTimeUtil.getDateString(new Date()).substring(0,12)));
                today.add(field, -amount);
                
                if (fromDate.length() < 12) {
                    fromDate = StringUtil.endAppendNStr('0', fromDate, 12);
                }
                from.setTime(sdf.parse(fromDate.substring(0, 12)));
                
                if ((toDate.length() == 14 && toDate.substring(8, 14).equals("000000")) ||
                        (toDate.length() == 12 && toDate.substring(8, 12).equals("0000"))) {
                    toDate = toDate.substring(0, 8);
                }
                
                if (toDate.length() == 8) {
                    toDate += "2359";
                }
                else if (toDate.length() == 10) {
                    toDate += "59";
                }
                to.setTime(sdf.parse(toDate.substring(0, 12)));
                
                log.debug("today: "+today);
                log.debug("from: "+from);
                log.debug("to: "+to);
            }
            
            while (today.after(from))  {
                from.add(field, amount);
                nOffset++;
            }

            while (today.after(to)) {
                to.add(field, amount);
                nCount++;
            }
            nCount = nOffset - nCount + 1;
        }
        
        log.debug("nOffset[" + nOffset + "] nCount[" + nCount + "]");
        return new int[] {nOffset, nCount};
    }

}
