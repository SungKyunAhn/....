package com.aimir.fep.meter.entry;

import java.util.HashMap;
import java.util.Map;
import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.Util;
import com.aimir.model.device.Meter;

public class AMUMeasurementDataEntryList
{
    private static Log log = LogFactory.getLog(MeasurementDataEntryList.class);

    public int emDataCnt = 0;
    private Map<String, AMUMeasurementDataEntry> mdes = new HashMap<String, AMUMeasurementDataEntry>();
    private String sourceAddr = null;
    private String destAddr = null;
    private Meter meter = null;

    public final int infoFrameLength =5;

    public AMUMeasurementDataEntryList()
    {
    }

    public String getSourceAddr() {
        return sourceAddr;
    }

    public void setSourceAddr(String sourceAddr) {
        this.sourceAddr = sourceAddr;
    }

    public String getDestAddr() {
        return destAddr;
    }

    public void setDestAddr(String destAddr) {
        this.destAddr = destAddr;
    }

    public int getEmDataCnt()
    {
        // return EMUtil.getIntToByte(this.emDataCnt);
        return emDataCnt;
    }

    public void setEmDataCnt(int data)
    {
        this.emDataCnt = data;
    }

    public Meter getMeter() {
        return meter;
    }

    public void setMeter(Meter meter) {
        this.meter = meter;
    }

    public void append(AMUMeasurementDataEntry mde)
    {
        if (mdes.containsKey(mde.getSourceAddr())) {
        	log.debug("mdes contains ::"+mde.getSourceAddr());
        	AMUMeasurementDataEntry _mde = mdes.get(mde.getSourceAddr());
         //   if (_mde.getMeasurementData()[0].getTimeStamp().compareTo(mde.getMeasurementData()[0].getTimeStamp()) < 0)
                mdes.put(mde.getSourceAddr(), mde);
        }
        else {
        	log.debug("mdes doesn't contains ::"+mde.getSourceAddr());
            mdes.put(mde.getSourceAddr(), mde);
        }
    }

    public AMUMeasurementDataEntry[] getMeasurementDataEntry()
    {
        return mdes.values().toArray(
                new AMUMeasurementDataEntry[0]);
    }

    public byte[] encode()
    {
        setEmDataCnt(mdes.size());
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
    /*    byte[] bx = null;
        AMUMeasurementDataEntry[] mdes = AMUMeasurementDataEntry();
        for(int i = 0 ; i < mdes.length ; i++)
        {
            bx = mdes[i].encode();
            bao.write(bx,0,bx.length);
        }
*/
        return bao.toByteArray();
    }

    public void decode(byte[] data)
    throws Exception
    {
        int pos = 0;
        AMUMeasurementDataEntry mde = null;
        int cnt = getEmDataCnt();
        log.debug("cnt=["+cnt+"]");

        int offset_info = 0;
        int offset_data = infoFrameLength*cnt;

        //info frame identity 부터 시작
        log.debug("offset_data :: "+ offset_data);
        log.debug("data.length :: "+ data.length);

        if(data!=null && data.length>0)
        	log.debug("data raw data ["+Util.getHexString(data)+"]");


        for(int i = 0 ; i < cnt ; i++)
        {
        	log.debug("cnt["+i+"] decode start!");
        	offset_info = i*infoFrameLength;
            mde = new AMUMeasurementDataEntry();
            offset_data = mde.decode(data,offset_info, offset_data, sourceAddr, destAddr, meter);

            log.debug("offset_data :: "+ offset_data);
            append(mde);

            if (data.length - offset_data < 4)
                break;
            pos = offset_data;

        }
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("AMUMeasurementDataEntryList[");
        AMUMeasurementDataEntry[] mdes = getMeasurementDataEntry();
        for(int i = 0 ; i < mdes.length ; i++)
        {
            sb.append(mdes[i].toString());
        }
        sb.append("]\n");

        return sb.toString();
    }
}
