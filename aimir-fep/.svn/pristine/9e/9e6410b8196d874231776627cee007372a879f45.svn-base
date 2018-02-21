package com.aimir.fep.meter.entry;

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.fep.meter.parser.MeterDataParser;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.Meter;
import com.aimir.model.system.DeviceConfig;
import com.aimir.model.system.MeterConfig;

public class MeasurementData implements IMeasurementData
{
    private static Log log = LogFactory.getLog(MeasurementData.class);

    private byte[] length = new byte[2];
    public byte[] timeStamp = new byte[7];
    private Meter meter = null;
    // SP-687
    private String mcuId = null;
    //ondemand 여부
    private boolean isOnDemand = false;

    private MeterDataParser parser = null;

    public MeasurementData(Meter meter, String mcuId)
    {
        this.meter = meter;
        this.mcuId = mcuId;
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

	public int getLength()
    {
        return DataFormat.getIntTo2Byte(length);
    }
    public void setLength(int data)
    {
        this.length = DataFormat.get2ByteToInt(data);
    }

    public String getTimeStamp()
    {
        return DataFormat.decodeTime(this.timeStamp);
    }
    public void setTimeStamp(String data)
    {
        this.timeStamp = DataFormat.encodeTime(data);
    }

    public MeterDataParser getMeterDataParser()
    {
        return this.parser;
    }

    public void setMeterDataParser(MeterDataParser data)
    {
        this.parser = data;
    }

    public byte[] encode()
    {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        if(parser != null)
        {
            byte[] bx = parser.getRawData();
            setLength(timeStamp.length+bx.length);
            DataFormat.convertEndian(length);
            bao.write(length,0,length.length);
            bao.write(timeStamp,0,timeStamp.length);
            bao.write(bx,0,bx.length);
        } else {
            setLength(timeStamp.length);
            DataFormat.convertEndian(length);
            bao.write(length,0,length.length);
            bao.write(timeStamp,0,timeStamp.length);
        }
        return bao.toByteArray();
    }

    public int decode(byte[] data,int position) throws Exception
    {
        int pos = position;
        log.debug("decode:: pos["+pos+"] data.total_len[" + data.length + "]");
        this.length[0] = data[pos];
        this.length[1] = data[pos+1];
        DataFormat.convertEndian(length);
        pos+=length.length;
        System.arraycopy(data,pos,timeStamp,0,timeStamp.length);
        pos+=timeStamp.length;
        log.debug("decode:: length["+getLength()+"]");
        log.debug("decode:: timeStamp["
                +getTimeStamp()+"]");
        byte[] bx = new byte[(getLength()-timeStamp.length)];
        try {
            //if(data.length >= (pos + getLength())) 
        	if(data.length >= (pos + bx.length))
            {
                System.arraycopy(data,pos,bx,0,bx.length);
                pos+=bx.length;
                log.debug("meter          [" + meter.getMdsId() + "]");
                log.debug("modem of meter [" + meter.getModem().getDeviceSerial() + "]");
                log.debug("model of modem [" + meter.getModem().getModel() + "]");
                log.debug("namespace of modem [" + meter.getModem().getNameSpace() + "]");

                // 파서와 저장 클래스명을 미터 설정 정보에서 먼저 가져온다.
                // 없으면 모뎀 설정 정보에서 가져온다. by elevas, 2012.06.12
                DeviceConfig deviceConfig = null;
                if (meter.getModel() != null) {
                    log.debug("model of meter[" + meter.getModel() + "]");
                    deviceConfig = meter.getModel().getDeviceConfig();
                    if (deviceConfig != null) {
                        log.debug("DeviceConfig[" + deviceConfig + "]");
                    }
                }
                
                if ((deviceConfig == null || 
                        (deviceConfig != null && 
                        (deviceConfig.getParserName() == null || "".equals(deviceConfig.getParserName())))) &&
                        meter.getModem().getModel() != null)
                    deviceConfig = meter.getModem().getModel().getDeviceConfig();
                
                if (deviceConfig == null) {
                    log.warn("register modem[" + meter.getModem().getDeviceSerial() + "] or meter[" + meter.getMdsId() + "] config");
                    return getLength() + 2;
                }
                else {
                    if (deviceConfig instanceof MeterConfig) {
                        MeterConfig meterconfig = (MeterConfig)deviceConfig;
                        if (meterconfig.getChannels() == null || meterconfig.getChannels().size() == 0) {
                            log.warn("set meter channel config for meter model[" + meter.getModel().getName() + "]");
                            return getLength() + 2;
                        }
                    }
                }
                
                log.debug("Parser[" + deviceConfig.getParserName() + "]");
                if (deviceConfig.getParserName() == null || deviceConfig.getParserName().equals("")) {
                    log.warn("parser is null, check meterId[" + meter.getMdsId() +
                            "], check deviceModel["+meter.getModel()+"]");
                    return getLength()+2;
                }
                this.parser = (MeterDataParser)Class.forName(deviceConfig.getParserName()).newInstance();
                log.debug("Parser Instance Created..");

                // SP-687
                this.parser.setDeviceId(this.mcuId);
                //ondemand 상태를 추가한다.
                this.parser.setOnDemand(this.isOnDemand());
                this.parser.setMeter(meter);
                this.parser.setMeteringTime(getTimeStamp());
                this.parser.parse(bx);
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            log.error("pos["+pos+"] mdDataLen["+data.length+"] "
                    +" datalen["+bx.length+"]");
            log.error(ex, ex);
            
            EventUtil.sendEvent("Invalid Frame",
                    TargetClass.valueOf(meter.getMeterType().getName()),
                    meter.getMdsId(),
                    new String[][] {{"message", 
                        "MCU[" + meter.getModem().getMcu().getSysID() + 
                        "] Modem[" + meter.getModem().getDeviceSerial() + "] Frame Error "}});
        } catch(Exception ex) {
            log.error("pos["+pos+"] mdDataLen["+data.length+"] "
                    +" datalen["+bx.length+"]");
            log.error(ex);
        }
        // +2 is length data
        return getLength()+2;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("MeasurementData[")
        .append("(length=").append(getLength()).append("),")
        .append("(timeStamp=").append(getTimeStamp()).append("),")
        .append("(parser=").append(this.parser != null? this.parser.getClass().getName():"").append(')')
        .append(']');

        return sb.toString();
    }
}
