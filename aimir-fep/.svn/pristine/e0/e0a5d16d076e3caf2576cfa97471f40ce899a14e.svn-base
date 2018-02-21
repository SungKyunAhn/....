package com.aimir.fep.meter.parser.actarisVCTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.vc.VCEventLogData;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;
import com.aimir.util.DateTimeUtil;

public class VM_EVENTLOG
{
    private static Log log = LogFactory.getLog(VM_EVENTLOG.class);

    private byte[] data;

    public static final int LEN_JEVT  = 6;

    protected int OFS_DATE = 0;
    protected int OFS_HOUR = 2;
    protected int OFS_EVMT = 5;

    protected int LEN_DATE = 3;//2.5
    protected int LEN_HOUR = 3;//2.5
    protected int LEN_EVMT = 1;//1

    private VCEventLogData[] eventLogData = null;

    public VM_EVENTLOG(byte[] data) {
        this.data = data;
        try{
            parseData();
            log.debug(toString());
        }catch(Exception e){
            eventLogData = null;
            log.error(e,e);
        }
    }
    
    public void parseData() throws Exception {
        int dataCount = data.length / LEN_JEVT;
        int pos = 0;
        if(data[pos] == 0x00 && 
                data[pos+1] == 0x00 && 
                data[pos+2] == 0x00 && 
                data[pos+3]== 0x00){
                 return;
             }
        eventLogData = new VCEventLogData[dataCount];
        for(int i=0;i<dataCount;i++) {
            
            byte[] jevt = new byte[LEN_JEVT];
            jevt = DataFormat.select(data,i*LEN_JEVT,LEN_JEVT);
            String date = getDate(jevt,OFS_DATE,LEN_DATE);
            String time = getTime(jevt,OFS_HOUR,LEN_HOUR);
            int Pmoy    = getEVMT(jevt,OFS_EVMT,LEN_EVMT);            
            
            eventLogData[i] = new VCEventLogData();

            eventLogData[i].setDate(date+time);
            pos += LEN_DATE+LEN_HOUR;
            eventLogData[i].setEventCode(Pmoy);
            pos += LEN_EVMT;
            
            eventLogData[i].setEventStatus(0);
            eventLogData[i].setEventValue(0d);
            //eventLogData[i].setEventStatus(DataUtil.getIntToByte(data[pos]));
            //pos += LEN_EVENT_STATUS;
            ///eventLogData[i].setEventValue((double) DataFormat.hex2float32(DataUtil.select(data, pos, LEN_EVENT_VALUE)));
            //pos += LEN_EVENT_VALUE;
            //pos += LEN_RESOLVED;
        }
    }
    


    public VCEventLogData[] getEventLogData()
    {
        return eventLogData;
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        if(eventLogData != null && eventLogData.length > 0){
            for(int i = 0; i < eventLogData.length; i++)
            {
                sb.append(eventLogData[i].toString());
            }
        }
        return sb.toString();
    }
    
    

    private int getEVMT(byte[] b, int start, int len)
                    throws Exception {

        int val = DataFormat.nibble(b[start]) & 0xFF;
        return val;
    }

    private String getDate(byte[] b, int start, int len)
                    throws Exception {

        int val = DataFormat.hex2dec(DataFormat.nibble(DataFormat.select(b,start,len)));

        val = val & 0x0FFFFF;
        String temp = new String();
        temp = Util.frontAppendNStr('0',Integer.toString(val),6);

        String yy = temp.substring(0,2);
        String mm = temp.substring(2,4);
        String dd = temp.substring(4,6);

        String yymmdd = yy+mm+dd;

        return getYyyymmdd(yymmdd,false);
    }

    private String getTime(byte[] b, int start, int len)
                    throws Exception {
        int val = DataFormat.hex2dec(DataFormat.nibble(DataFormat.select(b,start,len)));

        val = val >> 4;

        String temp = new String();
        temp = Util.frontAppendNStr('0',Integer.toString(val),6);

        String hhmmss = temp.substring(0,6);

        return hhmmss;
    }
    
    public static String getYyyymmdd(String s,boolean formchk)
    throws Exception {

        StringBuffer b = new StringBuffer();

        try {
            int curryy = (Integer.parseInt(DateTimeUtil
                    .getCurrentDateTimeByFormat("yyyy"))/100)*100;
            int year   = Integer.parseInt(s.substring( 0,2));
            int month  = Integer.parseInt(s.substring( 2,4));
            int day   = Integer.parseInt(s.substring( 4,6));
            if(month != 0){
                year   = Integer.parseInt(s.substring( 0,2))+curryy;
            }

            if(formchk){
                if(month < 1 || month > 12)
                    throw new Exception("Month Format Error");
                if(day < 1 || day >  31)
                    throw new Exception("Day Format Error");

            }

            b.append(Util.frontAppendNStr('0',Integer.toString(year),4));
            b.append(Util.frontAppendNStr('0',Integer.toString(month),2));
            b.append(Util.frontAppendNStr('0',Integer.toString(day),2));
            return b.toString();
        }catch(Exception e) {
            throw new Exception("PacketUtil.getYyyymmdd -> "+e.getMessage());
        }
    }
}