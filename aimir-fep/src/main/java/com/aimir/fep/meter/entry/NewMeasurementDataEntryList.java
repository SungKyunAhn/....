package com.aimir.fep.meter.entry;

import java.util.ArrayList;
import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NewMeasurementDataEntryList
{
    private static Log log = LogFactory.getLog(NewMeasurementDataEntryList.class);
    
    String mcuId = null;
    public int emDataCnt = 0;
    private ArrayList mdes = new ArrayList(); 


    public NewMeasurementDataEntryList()
    {
    }

    public String getMcuId()
    {
        return this.mcuId;
    }
    public void setMcuId(String mcuId)
    {
        this.mcuId = mcuId;
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

    public void append(NewMeasurementDataEntry mde)
    {
        mdes.add(mde);
    }

    public NewMeasurementDataEntry[] getMeasurementDataEntry()
    {
        return (NewMeasurementDataEntry[])mdes.toArray(
                new NewMeasurementDataEntry[0]);
    }

    public byte[] encode()
    {
        setEmDataCnt(mdes.size());
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        byte[] bx = null;
        NewMeasurementDataEntry[] mdes = getMeasurementDataEntry();
        for(int i = 0 ; i < mdes.length ; i++)
        {
            bx = mdes[i].encode();
            bao.write(bx,0,bx.length);
        }

        return bao.toByteArray();
    }

    public void decode(byte[] data)
    {
        int size = data.length;
        int pos = 0;
        NewMeasurementDataEntry mde = null;
        int cnt = getEmDataCnt();
        log.debug("cnt=["+cnt+"]");
        for(int i = 0 ; i < cnt ; i++)
        {
            mde = new NewMeasurementDataEntry();
            mde.setMcuId(this.mcuId);
            pos+=mde.decode(data,pos);
            append(mde);
        }
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("NewMeasurementDataEntryList[");
        NewMeasurementDataEntry[] mdes = getMeasurementDataEntry();
        for(int i = 0 ; i < mdes.length ; i++)
        {
            sb.append(mdes[i].toString());
        }
        sb.append("]\n");

        return sb.toString();
    }
}
