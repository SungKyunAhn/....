package com.aimir.fep.meter.entry;

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.parser.MeterDataParser;
import com.aimir.fep.meter.parser.ZEUPLS;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;


public class NewMeasurementData implements IMeasurementData
{
    private static Log log = LogFactory.getLog(NewMeasurementData.class);

    private byte[] length = new byte[2];
    public byte[] timeStamp = new byte[7];
    public  String mcuId = null;
    public  int mdGroup = 0;
    public  int mdMember = 0;
    private MeterDataParser parser = null;
    private String meterId = null;

    public NewMeasurementData()
    {
    }

    public NewMeasurementData(NewMeasurementDataEntry mde)
    {
        this.mcuId = mde.getMcuId();
        this.mdGroup = mde.getMdGroup();
        this.mdMember = mde.getMdMember();
    }

    public String getTimeStamp()
    {
        return DataFormat.decodeTime(this.timeStamp);
    }
    public void setTimeStamp(String data)
    {
        this.timeStamp = DataFormat.encodeTime(data);
    }

    public int getLength()
    {
        return DataUtil.getIntTo2Byte(length);
    }
    public void setLength(int data)
    {
        this.length = DataUtil.get2ByteToInt(data);
    }

    public MeterDataParser getMeterDataParser()
    {
        return this.parser;
    }
    public void setEMDataParser(MeterDataParser data)
    {
        this.parser = data;
    }

    public String getMeterId()
    {
        return meterId;
    }

    public void setMeterId(String meterId)
    {
        this.meterId = meterId;
    }

    public byte[] encode()
    {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        if(parser != null)
        {
            bao.write(timeStamp,0,timeStamp.length);
            byte[] bx = parser.getRawData();
            setLength(bx.length);
            DataUtil.convertEndian(length);
            bao.write(length,0,length.length);
            bao.write(bx,0,bx.length);
        } else {
            bao.write(timeStamp,0,timeStamp.length);
            setLength(0);
            DataUtil.convertEndian(length);
            bao.write(length,0,length.length);
        }
        return bao.toByteArray();
    }

    public int decode(byte[] data,int position)
    {
        log.debug("NewMeasurementData decode:: start mcuId["+mcuId+"] "
                + " mdGroup["+mdGroup+"] mdMember["+mdMember+"]");
        int pos = position;
        log.debug("NewMeasurementData decode:: pos["+pos+"]");


        System.arraycopy(data,pos,timeStamp,0,timeStamp.length);
        pos+=timeStamp.length;
        log.debug("NewMeasurementData decode:: length["+getLength()+"]");
        log.debug("NewMeasurementData decode:: timeStamp["
                +getTimeStamp()+"]");

        this.length[0] = data[pos]; 
        this.length[1] = data[pos+1]; 
        DataUtil.convertEndian(length);
        pos+=length.length;
        byte[] bx = new byte[getLength()];

        try {
            System.arraycopy(data,pos,bx,0,bx.length);
            pos+=bx.length;
            this.parser = null; // new ZEUPLS(getTimeStamp()); // EMUtil.getEMDataParser(meterMO.getPropertyValueString("id"));
            parser.parse(bx);
        } catch(Exception ex) {
            log.error("pos["+pos+"] mdDataLen["+data.length+"] "
                    +" datalen["+bx.length+"]");
            log.error("EMDataParser parse failed",ex);
        }

        return getLength()+2+7;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("NewMeasurementData[")
        .append("(timeStamp=").append(getTimeStamp()).append("),")
        .append("(length=").append(getLength()).append("),")
        .append("(parser=").append(parser).append(')')
        .append(']');

        return sb.toString();
    }
}
