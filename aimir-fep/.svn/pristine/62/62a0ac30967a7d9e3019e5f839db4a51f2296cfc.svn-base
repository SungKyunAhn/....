package com.aimir.fep.meter.entry;


import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.parser.MeterDataParser;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.model.system.DeviceConfig;
import com.aimir.model.system.ModemConfig;

public class AMUMeasurementData implements IMeasurementData
{
    private static Log log = LogFactory.getLog(MeasurementData.class);

    private byte meterType =  (byte)0x01;
    private byte[] parserCode = new byte[2];

    private byte[] length = new byte[4];
    public byte[] timeStamp = new byte[7];
    private Meter meter = null;

    private MeterDataParser parser = null;

    public AMUMeasurementData(Meter meter)
    {
        this.meter = meter;
    }

    public int getLength()
    {
        return DataFormat.getIntTo4Byte(length);
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

    public int decode(byte[] data,int position)
    {
        int pos = position;
        log.debug("decode:: pos["+pos+"]");

        System.arraycopy(data,pos,length,0,length.length);
        pos+=length.length;

        meterType = data[pos++];

        System.arraycopy(data,pos,parserCode,0,parserCode.length);
        pos+=parserCode.length;

        log.debug("decode:: length["+getLength()+"]");
        log.debug("decode:: meterType["+meterType+"]");
        log.debug("decode:: parserCode["+DataUtil.getIntTo2Byte(parserCode)+"]");
        // 파서 코드를 이용해서 파서를 찾지 않기 때문에 필요 없음.

        byte[] bx = new byte[(getLength()-3)]; //meterType, parserCode 길이 뺌.

        try {
            if(bx.length > 0)
            {
                System.arraycopy(data,pos,bx,0,bx.length);
                pos+=bx.length;
                //TODO amu의 경우 검침 데이터의 헤더 부분에 meter serial이 존재하지 않고
                // 검침 데이터 부분에 미터 시리얼이 존재하기 때문에 meter->modem을 이용해 파서를 가져오는 것이 용이하지 않음
                // 따라서 임시로 계량기 종류에 따라 파서를 코드상에 지정해준다.
                if(meter==null){
                	int parserInt=DataUtil.getIntTo2Byte(parserCode);
	                switch (parserInt) {
					case 0://Pulse INterface
						this.parser = (MeterDataParser)Class.forName("com.aimir.fep.meter.parser.AMUPULSE").newInstance();
						break;
					case 2816://LS RW Series
						this.parser = (MeterDataParser)Class.forName("com.aimir.fep.meter.parser.AMULSRW_RS232").newInstance();
						break;
					case 3072://GE Kv2c
						log.error("GE Kv2c Parser is Unknown!");
						break;
					case 3328://한전 구 프로토콜 : v2.5.0
						this.parser = (MeterDataParser)Class.forName("com.aimir.fep.meter.parser.AMUKEPCO_2_5_0").newInstance();
						break;
					case 3329://한전 신 프로토콜
						log.error("New Kepco Parser is Unknown!");
						break;
					case 3330://한전 DLMS 프로토콜
						this.parser = (MeterDataParser)Class.forName("com.aimir.fep.meter.parser.AMUKEPCO_DLMS").newInstance();
						break;
					case 3584://ABB 프로토콜
						log.error("ABB Parser is Unknown!");
						break;
					case 3840://구 LG 프로토콜
						log.error("Old LG Parser is Unknown!");
						break;
					case 4096://일진 전기 (AMR Tech) 프로토콜
						log.error("ILJIN Electronic Parser is Unknown!");
						break;
					default:
						break;
					}

	                log.debug("Parser Instance Created..");
	                if (parser == null) {
	                    throw new Exception("parser is null, check meterId[" + meter.getMdsId() +
	                            "], check deviceModel["+meter.getModel()+"]");
	                }

	                this.parser.setMeter(meter);
	                parser.parse(bx);
                }else {
	                log.debug("meter          [" + meter.getId() + "]");
	                log.debug("modem of meter [" + meter.getModem().getId() + "]");
	                log.debug("model of modem [" + meter.getModem().getModel() + "]");
	                if (meter.getModem().getModel() == null)
	                    throw new Exception("model is null, check modemId[" + meter.getModem().getDeviceSerial() + "]");

	                DeviceConfig deviceConfig = meter.getModem().getModel().getDeviceConfig();
	                ModemConfig modemConfig = (ModemConfig)deviceConfig;
	                // this.parser = CmdUtil.getParser(meter);
	                log.debug("Parser[" + modemConfig.getParserName() + "]");
	                this.parser = (MeterDataParser)Class.forName(modemConfig.getParserName()).newInstance();
	                log.debug("Parser Instance Created..");
	                if (parser == null) {
	                    throw new Exception("parser is null, check meterId[" + meter.getMdsId() +
	                            "], check deviceModel["+meter.getModel()+"]");
	                }

	                this.parser.setMeter(meter);
	                parser.parse(bx);
                }
            }
        } catch(Exception ex) {
            log.error("pos["+pos+"] mdDataLen["+data.length+"] "
                    +" datalen["+bx.length+"]");
            log.error(ex,ex);
        }

        // +2 is length data
        return getLength()+2;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("AMUMeasurementData[")
        .append("(length=").append(getLength()).append("),")
        .append("(parser=").append(parser).append(')')
        .append(']');

        return sb.toString();
    }
}