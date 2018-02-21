package com.aimir.fep.meter.entry;

import java.util.ArrayList;
import java.util.Collections;
import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;

public class NewMeasurementDataEntry implements IMeasurementDataEntry
{
    private static Log log = LogFactory.getLog(MeasurementDataEntry.class);
    public String mcuId = null;
    public byte mdGroup = (byte)0x01;
    public byte mdMember = (byte)0x01;
    public byte[] dataCnt = new byte[2];
    private ArrayList mds = new ArrayList();

    public NewMeasurementDataEntry()
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

    public int getMdGroup()
    {
        return DataUtil.getIntToByte(this.mdGroup);
    }
    public void setMdGroup(int data)
    {
        this.mdGroup = (byte)data;
    }

    public int getMdMember()
    {
        return DataUtil.getIntToByte(this.mdMember);
    }
    public void setMdMember(int data)
    {
        this.mdMember = (byte)data;
    }

    public int getDataCnt()
    {
        return DataUtil.getIntTo2Byte(this.dataCnt);
    }
    public void setDataCnt(int data)
    {
        this.dataCnt = DataUtil.get2ByteToInt(data);
    }

    public void append(NewMeasurementData data)
    {
        mds.add(data);
    }

    public NewMeasurementData[] getMeasurementData()
    {
        return (NewMeasurementData[])mds.toArray(new NewMeasurementData[0]);
    }

    public IMeasurementData[] getSortedMeasurementData()
    {
        Collections.sort(mds,NewMDComparator.TIMESTAMP_ORDER);
        return (NewMeasurementData[])mds.toArray(new NewMeasurementData[0]);
    }


    public byte[] encode()
    {
        setDataCnt(mds.size());
        DataUtil.convertEndian(this.dataCnt);

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bao.write(mdGroup);
        bao.write(mdMember);
        bao.write(dataCnt,0,dataCnt.length);

        NewMeasurementData[] mds = getMeasurementData();
        byte[] bx = null;
        for(int i = 0 ; i < mds.length ; i++)
        {
            bx = mds[i].encode();
            bao.write(bx,0,bx.length);
        }

        return bao.toByteArray();
    }

    public int decode(byte[] data, int position)
    {
        log.debug("NewMeasurementDataEntry decode:: position["+position+"]");
        int pos = position;

        mdGroup = data[pos++];
        mdMember = data[pos++];
        log.debug("NewMeasurementDataEntry decode:: mdGroup["
                +getMdGroup()+"]");
        log.debug("NewMeasurementDataEntry decode:: mdMember["+
                getMdMember()+"]");

        System.arraycopy(data,pos,dataCnt,0,dataCnt.length);
        DataUtil.convertEndian(this.dataCnt);
        pos+=dataCnt.length;
        log.debug("NewMeasurementDataEntry decode:: dataCnt["
                +getDataCnt()+"]");

        int cnt = getDataCnt();
        log.debug("NewMeasurementDataEntry decode:: cnt["+cnt+"]");

        NewMeasurementData md = null;

        for(int i = 0 ; i < cnt ; i++)
        {
            md = new NewMeasurementData(this);
            log.debug("pos["+pos+"]");
            pos+=md.decode(data,pos);
            log.debug("pos["+pos+"] md["+md.toString()+"]");
            append(md);
        }

        return (pos - position);
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("MeasurementDataEntry[")
        .append("(mdGroup=").append(getMdGroup()).append("),")
        .append("(mdMember=").append(getMdMember()).append("),")
        .append("(dataCnt=").append(getDataCnt()).append(")\n");

        NewMeasurementData[] mds = getMeasurementData();
        for(int i = 0 ; i < mds.length ; i++)
            sb.append(mds[i].toString());
        
        return sb.toString();
    }
}
