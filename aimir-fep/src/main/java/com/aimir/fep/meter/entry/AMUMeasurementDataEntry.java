package com.aimir.fep.meter.entry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.io.ByteArrayOutputStream;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.Meter;

public class AMUMeasurementDataEntry implements IMeasurementDataEntry
{
    private static Log log = LogFactory.getLog(AMUMeasurementDataEntry.class);
    
    public final int dataFrameLengthField = 4;
    
    public byte[] meterId = new byte[20];
    public byte modemType = (byte)0x01;
    public byte svcType = (byte)0x01;
    public byte vendor = (byte)0x01;
    public int model = (byte)0x01;
    static List<String> EUI64_PREFIX = null;
    static List<Integer> EUI64_EXCEPT = null;
    private String sourceAddr = null;
    private String destAddr = null;

    static {
        EUI64_PREFIX = new ArrayList<String>();
        EUI64_EXCEPT = new ArrayList<Integer>();
        String eui64candi = FMPProperty.getProperty("eui64.prefix.candidate");
        StringTokenizer st = new StringTokenizer(eui64candi, ",");
        while (st.hasMoreTokens()) {
			EUI64_PREFIX.add(st.nextToken().trim());
		}

        String eui64except = FMPProperty.getProperty("eui64.except.sensorType");
        st = new StringTokenizer(eui64except, ",");
        while (st.hasMoreTokens()) {
			EUI64_EXCEPT.add(Integer.parseInt(st.nextToken().trim()));
		}
    }

    public int dataCnt = 1;
    private ArrayList<AMUMeasurementData> mds = new ArrayList<AMUMeasurementData>();

    public AMUMeasurementDataEntry()
    {
    }

    public String getMeterId()
    {
        return (new String(this.meterId)).trim();
    }
    public void setMeterId(String data)
    {
        byte[] bx = data.getBytes();
        int len = bx.length;
        if(len > 20) {
			len = 20;
		}
        System.arraycopy(bx,0,this.meterId,0,len);
    }

    public ModemType getModemType()
    {
        return CommonConstants.getModemType(DataUtil.getIntToByte(this.modemType));
    }
    public void setModemType(int data)
    {
        this.modemType = (byte)data;
    }

    public int getSvcType()
    {
        return DataUtil.getIntToByte(this.svcType);
    }
    public void setSvcType(int data)
    {
        this.svcType = (byte)data;
    }

    public int getVendor()
    {
        return DataUtil.getIntToByte(this.vendor);
    }
    public void setVendor(int data)
    {
        this.vendor = (byte)data;
    }
    public int getModel()
    {
        return this.model;
    }
    public void setModel(int data)
    {
        this.model = data;
    }
    public int getDataCnt()
    {
        return this.dataCnt;
    }
    public void setDataCnt(int dataCnt)
    {
        this.dataCnt = dataCnt;
    }
    public String getSourceAddr()
    {
        return this.sourceAddr;
    }
    public String getDestAddr()
    {
        return this.destAddr;
    }
    
    public void append(AMUMeasurementData data)
    {
        mds.add(data);
    }

    public AMUMeasurementData[] getMeasurementData()
    {
        return mds.toArray(new AMUMeasurementData[0]);
    }

    public IMeasurementData[] getSortedMeasurementData()
    {
        Collections.sort(mds,MDComparator.TIMESTAMP_ORDER);
        return mds.toArray(new AMUMeasurementData[0]);
    }


    public byte[] encode()
    {
  //      setDataCnt(1);
   //     EMUtil.convertEndian(this.dataCnt);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
   /*     bao.write(sensorId,0,sensorId.length);
        bao.write(sensorType);
        bao.write(svcType);
        bao.write(vendor);
        bao.write(dataCnt,0,dataCnt.length);
        AMUMeasurementData[] mds = getMeasurementData();
        byte[] bx = null;
        for(int i = 0 ; i < mds.length ; i++)
        {
            bx = mds[i].encode();
            bao.write(bx,0,bx.length);
        }
*/
        return bao.toByteArray();
    }
    
    //return next data_position
    public int decode(byte[] data, int info_position, int data_position,
            String sourceAddr, String destAddr, Meter meter)
    throws Exception
    {
        this.sourceAddr = sourceAddr;
        this.destAddr = destAddr;
        
        log.debug("AMUMeasurementDataEntry decode:: position["+info_position+"]");
        log.debug("AMUMeasurementDataEntry decode:: position["+data_position+"]");
        int info_pos = info_position;
        int data_pos = data_position;
        
        // sensorId[(sensorId.length-1)] = data[info_pos++];
        info_pos++;
        svcType = data[info_pos++];
        modemType = data[info_pos++];
        vendor = data[info_pos++];
        model =  data[info_pos++] & 0xff;
        
        log.debug("decode:: modemType["
                +getModemType().name()+"]");
        log.debug("decode:: svcType["
                +getSvcType()+"]");
        log.debug("decode:: vendor["
                +getVendor()+"]");
        log.debug("decode:: model["
                +getModel()+"]");
        int cnt = getDataCnt();
        log.debug("decode:: cnt["+cnt+"]");
        AMUMeasurementData md = null;

        md = new AMUMeasurementData(meter);
        log.debug("info_pos["+info_pos+"]");
        
    	data_position = md.decode(data, data_position);
        
        try{ 
            setMeterId(md.getMeterDataParser().getMDevId());
        	log.debug("meterId : "+getMeterId());
        }
        catch(Exception e){
        	log.error(e,e);
        }
        
        log.debug("data_position["+data_position+"] md["+md.toString()+"]");
        append(md);

        return data_position;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("AMUMeasurementDataEntry[")
        .append("(modemType=").append(getModemType()).append("),")
        .append("(svcType=").append(getSvcType()).append("),")
        .append("(vendor=").append(getVendor()).append("),")
        .append("(dataCnt=").append(getDataCnt()).append(")\n");

        AMUMeasurementData[] mds = getMeasurementData();
        for(int i = 0 ; i < mds.length ; i++) {
			sb.append(mds[i].toString());
		}

        return sb.toString();
    }
}
