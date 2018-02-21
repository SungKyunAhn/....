package com.aimir.fep.meter.parser;

import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants.MeteringFlag;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.protocol.fmp.datatype.IP6ADDR;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;

/**
IPv6 Address	Meter Type	Meter Serial	Current Time	Load Profile
Size(Byte) : 16	1	16	7	48
**/
public class SimpleMeter extends MeterDataParser implements java.io.Serializable {

	private static final long serialVersionUID = 1853335362384983992L;
	private static Log log = LogFactory.getLog(SimpleMeter.class);
	
    private byte[] rawData = null;    
    private Double meteringValue = 0d;
    
    LPData[] lplist = null;
    
    String ip6addr = null;
    String edgeRouterId = null;
    int meterType = 0;
    String meterSerial = null;
    String currentTime = null;

    private int lpInterval = 60;

    
    /**
     * constructor
     */
    public SimpleMeter()
    {
    }

    /**
     * parseing Energy Meter Data of Simple Meter
     * @param data stream of result command
     */
	@Override
    public void parse(byte[] data) throws Exception
    {
		this.rawData = data;
        log.debug("Data : "+Hex.decode(data));
        
        byte[] IPV6ADDR = new byte[16];
        byte[] METERTYPE = new byte[1];
        byte[] METERSERIAL = new byte[16];
        byte[] CURRENTTIME = new byte[7];
        byte[] LP = new byte[48];
        
        int pos = 0;

        System.arraycopy(data, pos, IPV6ADDR, 0, IPV6ADDR.length);
        pos += IPV6ADDR.length;
        
        System.arraycopy(data, pos, METERTYPE, 0, METERTYPE.length);
        pos += METERTYPE.length;
        
        System.arraycopy(data, pos, METERSERIAL, 0, METERSERIAL.length);
        pos += METERSERIAL.length;
        
        System.arraycopy(data, pos, CURRENTTIME, 0, CURRENTTIME.length);
        pos += CURRENTTIME.length;
        
        System.arraycopy(data, pos, LP, 0, LP.length);
        pos += LP.length;        
        
        this.ip6addr = new IP6ADDR(IPV6ADDR).getValue();
        this.meterType = METERTYPE[0] & 0xFF;
        
        this.edgeRouterId = Hex.decode(IPV6ADDR).substring(0,16);
        if(meterType == 0){//dummy modem
            this.meterSerial = Hex.decode(METERSERIAL).substring(16);
        }else{
            this.meterSerial = new String(METERSERIAL).trim();
        }

        this.currentTime = DataFormat.decodeTime(CURRENTTIME);
        
        log.info("ip6addr["+ip6addr+"] meterType["+meterType+"] meterSerial["+meterSerial+"] currentTime["+currentTime+"]");
        
        lplist = new LPData[12];
        int lpCnt = 12;
        byte[] pulse = new byte[4];
        pos = 0;
        String lpTime = currentTime.substring(0,10)+"0000";
		lpTime = Util.addMinYymmdd(lpTime, -lpInterval*lpCnt);
        for(int i = 0; i < lpCnt; i++){
        	
        	System.arraycopy(LP, pos, pulse, 0, pulse.length);
            pos += pulse.length;
            
            int currentPulse = DataUtil.getIntTo4Byte(pulse);
            double lpValue = currentPulse*0.001;
            lpTime = Util.addMinYymmdd(lpTime, lpInterval);
            lplist[i] = new LPData();
            lplist[i].setDatetime(lpTime);
            lplist[i].setBasePulse(0);
            lplist[i].setCh(new Double[]{(double) lpValue});
            lplist[i].setLp((double) lpValue);
            lplist[i].setLpValue((double) lpValue);
            
            if(currentPulse < 0){
            	lplist[i].setFlag(MeteringFlag.NotValid.getFlag());
            }else{
            	lplist[i].setFlag(MeteringFlag.Correct.getFlag());
            }
            
            log.info("lpTime["+lpTime+"] Pulse["+currentPulse+"] lpValue["+lpValue+"]");
        }
    }

	public LPData[] getLplist() {
		return lplist;
	}

	public void setLplist(LPData[] lplist) {
		this.lplist = lplist;
	}

	public String getIp6addr() {
		return ip6addr;
	}

	public void setIp6addr(String ip6addr) {
		this.ip6addr = ip6addr;
	}

	public int getMeterType() {
		return meterType;
	}

	public void setMeterType(int meterType) {
		this.meterType = meterType;
	}

	public String getMeterSerial() {
		return meterSerial;
	}

	public void setMeterSerial(String meterSerial) {
		this.meterSerial = meterSerial;
	}

	public String getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(String currentTime) {
		this.currentTime = currentTime;
	}

	public int getLpInterval() {
		return lpInterval;
	}

	public void setLpInterval(int lpInterval) {
		this.lpInterval = lpInterval;
	}

	public void setMeteringValue(Double meteringValue) {
		this.meteringValue = meteringValue;
	}

	public String getEdgeRouterId() {
		return edgeRouterId;
	}

	public void setEdgeRouterId(String edgeRouterId) {
		this.edgeRouterId = edgeRouterId;
	}

	@Override
	public byte[] getRawData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Double getMeteringValue() {
		return this.meteringValue;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LinkedHashMap<?, ?> getData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getFlag() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setFlag(int flag) {
		// TODO Auto-generated method stub
		
	}

}
