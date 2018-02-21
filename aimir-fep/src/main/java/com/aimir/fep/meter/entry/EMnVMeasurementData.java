package com.aimir.fep.meter.entry;

import java.io.ByteArrayOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.fep.meter.parser.DLMSEMnVEType_1_0;
import com.aimir.fep.meter.parser.DLMSEMnVGtype;
import com.aimir.fep.meter.parser.EMnVCompensator;
import com.aimir.fep.meter.parser.MeterDataParser;
import com.aimir.fep.meter.parser.ModbusEMnVDefault;
import com.aimir.fep.meter.parser.ModbusEMnVHyundai;
import com.aimir.fep.meter.parser.ModbusEMnVLS;
import com.aimir.fep.meter.parser.ModbusEMnVRockwell;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.EMnVMeterType;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.EMnVMeteringDataType;
import com.aimir.fep.util.DataFormat;
import com.aimir.model.device.Meter;
import com.aimir.model.system.DeviceConfig;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.MeterConfig;

public class EMnVMeasurementData implements IMeasurementData
{
    private static Logger log = LoggerFactory.getLogger(EMnVMeasurementData.class);

    private byte[] length = new byte[2];
    public byte[] timeStamp = new byte[7];
    private Meter meter = null;
    //ondemand 여부
    private boolean isOnDemand = false;

    
    private MeterDataParser parser = null;

    public EMnVMeasurementData(Meter meter)
    {
        this.meter = meter;
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

//    public int decode(byte[] data,int position) throws Exception
//    {
//        int pos = position;
//        log.debug("decode:: pos["+pos+"] data.total_len[" + data.length + "]");
//        this.length[0] = data[pos];
//        this.length[1] = data[pos+1];
//        DataFormat.convertEndian(length);
//        pos+=length.length;
//        System.arraycopy(data,pos,timeStamp,0,timeStamp.length);
//        pos+=timeStamp.length;
//        log.debug("decode:: length["+getLength()+"]");
//        log.debug("decode:: timeStamp["
//                +getTimeStamp()+"]");
//        byte[] bx = new byte[(getLength()-timeStamp.length)];
//        try {
//            if(bx.length > 0)
//            {
//                System.arraycopy(data,pos,bx,0,bx.length);
//                pos+=bx.length;
//                log.debug("meter          [" + meter.getMdsId() + "]");
//                log.debug("modem of meter [" + meter.getModem().getDeviceSerial() + "]");
//                log.debug("model of modem [" + meter.getModem().getModel() + "]");
//
//                // 파서와 저장 클래스명을 미터 설정 정보에서 먼저 가져온다.
//                // 없으면 모뎀 설정 정보에서 가져온다. by elevas, 2012.06.12
//                DeviceConfig deviceConfig = null;
//                if (meter.getModel() != null) {
//                    log.debug("model of meter[" + meter.getModel() + "]");
//                    deviceConfig = meter.getModel().getDeviceConfig();
//                    if (deviceConfig != null) {
//                        log.debug("DeviceConfig[" + deviceConfig + "]");
//                    }
//                }
//                
//                if ((deviceConfig == null || 
//                        (deviceConfig != null && 
//                        (deviceConfig.getParserName() == null || "".equals(deviceConfig.getParserName())))) &&
//                        meter.getModem().getModel() != null)
//                    deviceConfig = meter.getModem().getModel().getDeviceConfig();
//                
//                if (deviceConfig == null)
//                    throw new Exception("register modem[" + meter.getModem().getDeviceSerial() + "] or meter[" + meter.getMdsId() + "] config");
//                else {
//                    if (deviceConfig instanceof MeterConfig) {
//                        MeterConfig meterconfig = (MeterConfig)deviceConfig;
//                        if (meterconfig.getChannels() == null || meterconfig.getChannels().size() == 0) {
//                            throw new Exception("set meter channel config for meter model[" + meter.getModel().getName() + "]");
//                        }
//                    }
//                }
//                
//                log.debug("Parser[" + deviceConfig.getParserName() + "]");
//                this.parser = (MeterDataParser)Class.forName(deviceConfig.getParserName()).newInstance();
//                log.debug("Parser Instance Created..");
//                if (parser == null) {
//					throw new Exception("parser is null, check meterId[" + meter.getMdsId() +
//					        "], check deviceModel["+meter.getModel()+"]");
//				}
//
//                //ondemand 상태를 추가한다.
//                this.parser.setOnDemand(this.isOnDemand());
//                this.parser.setMeter(meter);
//                this.parser.setMeteringTime(getTimeStamp());
//                this.parser.parse(bx);
//            }
//        } catch(Exception ex) {
//            log.error("pos["+pos+"] mdDataLen["+data.length+"] "
//                    +" datalen["+bx.length+"]");
//            log.error(ex);
//            throw ex;
//        }
//
//        // +2 is length data
//        return getLength()+2;
//    }

	public void prcMeterParsing(DeviceModel model, EMnVMeterType meterType
			, EMnVMeteringDataType meteringDataType, byte[] mdData
			, int supplierId, String typeCheck) throws Exception {
		DeviceConfig deviceConfig = null;
		
		log.debug("PrcMeter Parsing  ==> [Type={}][MeteringDataType={}][Length={}][Supplier={}]", new Object[]{meterType.name(), meteringDataType.name(), mdData.length, supplierId});
	     
		if(model != null){
			deviceConfig = model.getDeviceConfig();
		    
	        if (deviceConfig == null){
	            throw new Exception("register modem or meter config");
	        }else {
	            if (deviceConfig instanceof MeterConfig) {
	                MeterConfig meterconfig = (MeterConfig)deviceConfig;
	                if ( (typeCheck.equals(MeterType.EnergyMeter.name())) && (meterconfig.getChannels() == null || meterconfig.getChannels().size() == 0) ) {
	                    throw new Exception("set meter channel config for meter model[" + model.getName() + "]");
	                }
	            }
	        }
		      
	        log.debug("### Parser ==> [{}]", deviceConfig.getParserName());
	        
	        parser = (MeterDataParser)Class.forName(deviceConfig.getParserName()).newInstance();
	        
	        log.debug("Parser Instance Created..");
	        if (parser == null) {
				throw new Exception("parser is null, check deviceModel[" + model + "]");
			}

	        parser.setFlag(meteringDataType.getValue());  // Billing or Load profile
	        parser.setMeter(meter);
	        parser.setMeteringTime(getTimeStamp());
	        parser.parse(mdData);
	        
	        // 미터로부터 TimeStamp값이 넘어오지 않기 때문에 OBSI코드에 있는 일자/시간 값을 넣어줌.
	        // Inverter는 일자/시간도 없어서 값을 넣지 않음.
	        String tempMeterTime = parser.getMeterTime();	        
	        setTimeStamp(tempMeterTime.length() == 12 ? tempMeterTime + "00" : tempMeterTime);  
	        parser.setMeteringTime(getTimeStamp());
		}else {
			log.debug("### DeviceModel is null.");
		}
		
	}
	
	public void postMeterParsing(){
		if(parser instanceof DLMSEMnVGtype){
			((DLMSEMnVGtype)parser).postParse();
		}else if(parser instanceof DLMSEMnVEType_1_0){
			((DLMSEMnVEType_1_0)parser).postParse();
		}else if(parser instanceof ModbusEMnVDefault){
			((ModbusEMnVDefault)parser).postParse();
			log.debug("####### ModbusEMnV Default Parser ####");
		}else if(parser instanceof ModbusEMnVLS){
			((ModbusEMnVLS)parser).postParse();
			log.debug("####### ModbusEMnV LS Parser ####");
		}else if(parser instanceof ModbusEMnVHyundai){
			((ModbusEMnVHyundai)parser).postParse();
			log.debug("####### ModbusEMnV Hyundai Parser ####");
		}else if(parser instanceof ModbusEMnVRockwell){
			((ModbusEMnVRockwell)parser).postParse();
			log.debug("####### ModbusEMnV Rockwell Parser ####");
		}else if(parser instanceof EMnVCompensator){
			((EMnVCompensator)parser).postParse();
			log.debug("####### Compensator Parser ####");
		}else{			
			log.debug("####### 이상한 파서야~!!! ####");
		}
		
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
