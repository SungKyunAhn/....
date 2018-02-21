package com.aimir.fep.meter.entry;

import java.util.ArrayList;
import java.util.List;
import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MeasurementDataEntryList
{
    private static Log log = LogFactory.getLog(MeasurementDataEntryList.class);
    
    public int emDataCnt = 0;
    // private Map<String, MeasurementDataEntry> mdes = new HashMap<String, MeasurementDataEntry>();
    private List<MeasurementDataEntry> mdes = new ArrayList<MeasurementDataEntry>();
    private String mcuId = null;

    //ondemand 여부
    private boolean isOnDemand = false;

    public MeasurementDataEntryList()
    {
    }

    /**
	 * @param isOnDemand the isOnDemand to set
	 */
	public void setOnDemand(boolean isOnDemand) {
		this.isOnDemand = isOnDemand;
	}

	/**
	 * @return the isOnDemand
	 */
	public boolean isOnDemand() {
		return isOnDemand;
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

    public void setMcuId(String mcuId)
    {
        this.mcuId = mcuId;
    }
    
    public void append(MeasurementDataEntry mde)
    {
        /*
        if (mdes.containsKey(mde.getModemId())) {
            MeasurementDataEntry _mde = (MeasurementDataEntry)mdes.get(mde.getModemId());
            if (_mde.getMeasurementData()[0].getTimeStamp().compareTo(mde.getMeasurementData()[0].getTimeStamp()) < 0)
                mdes.put(mde.getModemId(), mde);
        }
        else {
            mdes.put(mde.getModemId(), mde);
        }
        */
        mdes.add(mde);
    }

    public MeasurementDataEntry[] getMeasurementDataEntry()
    {
        /*
        return (MeasurementDataEntry[])mdes.values().toArray(
                new MeasurementDataEntry[0]);
                */
        return (MeasurementDataEntry[])mdes.toArray(new MeasurementDataEntry[0]);
    }

    public byte[] encode()
    {
        setEmDataCnt(mdes.size());
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        byte[] bx = null;
        MeasurementDataEntry[] mdes = getMeasurementDataEntry();
        for(int i = 0 ; i < mdes.length ; i++)
        {
            bx = mdes[i].encode();
            bao.write(bx,0,bx.length);
        }

        return bao.toByteArray();
    }

    public void decode(byte[] data, String ns, String ipAddr, String protocolType)
    {
        int pos = 0;
        MeasurementDataEntry mde = null;
        int cnt = getEmDataCnt();
        log.debug("ns[" + ns + "] ipAddr[" + ipAddr + "] cnt=["+cnt+"]"); // raw[" + Hex.decode(data) + "]");
        int offset = 0;
        for(int i = 0 ; i < cnt ; i++)
        {
        	// 원래 try catch가 없었는데 엔트리 리스트에서 다른 미터 타입의 검침데이터가 올때 중간에 하나 실패하면 나머지가 아예 처리 안되므로
        	//테스트를 위해 try catch로 해서 잘못된 프레임은 처리안하기로 함
        	try{
                mde = new MeasurementDataEntry();
                mde.setOnDemand(this.isOnDemand());
                offset = mde.decode(data,pos,mcuId, ns, ipAddr, protocolType);
                log.debug("OFFSET[" + offset + "]");
                if (offset == -1 || offset == 0)
                    break;
                pos+=offset;
                append(mde);
        	} catch(Exception e){
        		log.error(e, e);
        	}

        }
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("MeasurementDataEntryList[");
        MeasurementDataEntry[] mdes = getMeasurementDataEntry();
        for(int i = 0 ; i < mdes.length ; i++)
        {
            sb.append(mdes[i].toString());
        }
        sb.append("]\n");

        return sb.toString();
    }
}
