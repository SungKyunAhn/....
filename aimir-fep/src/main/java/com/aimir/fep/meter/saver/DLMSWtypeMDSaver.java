package com.aimir.fep.meter.saver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.DLMSWtype;

@Service
public class DLMSWtypeMDSaver extends AbstractMDSaver {
    private static Log log = LogFactory.getLog(DLMSWtypeMDSaver.class);
    
    enum STATUS {
        Q4OverFlow((byte)0x80, "Q4 Over Flow"),
        ReverseFlow((byte)0x40, "Reverse Flow"),
        Leak((byte)0x20, "Leak"),
        BatteryLow((byte)0x04, "Battery Low"),
        Freeze((byte)0x02, "Freeze");
        
        byte code;
        String descr;
        
        STATUS(byte code, String descr) {
            this.code = code;
            this.descr = descr;
        }
        
        public byte getCode() {
            return this.code;
        }
        
        public String getDescr() {
            return this.descr;
        }
    }
	
    @Override
    public boolean save(IMeasurementData md) throws Exception {
        try {
            DLMSWtype parser = (DLMSWtype) md.getMeterDataParser();
            
            // log.debug("active pulse constant:" +
            // parser.getActivePulseConstant());
            // log.debug("currentDemand:" + currentDemand);
            byte dif = parser.getDIF();
            byte status = parser.getStatus();
            
            // 계량기 구경을 저장해야 될 것 같은데...
            dif &= 0xF0;
            int idif = 1;
            switch (dif) {
            case 0x10 : idif = 15; break;
            case 0x20 : idif = 20; break;
            case 0x30 : idif = 25; break;
            case 0x40 : idif = 32; break;
            case 0x50 : idif = 40; break;
            case 0x60 : idif = 50; break;
            case 0x70 : idif = 80; break;
            case (byte)0x80 : idif = 100; break;
            case (byte)0x90 : idif = 150; break;
            case (byte)0xA0 : idif = 200; break;
            case (byte)0xB0 : idif = 250; break;
            case (byte)0xC0 : idif = 300; break;
            }
            
            saveMeteringData(MeteringType.Normal, md.getTimeStamp().substring(0,8),
                    md.getTimeStamp().substring(8, 14), parser.getMeteringValue(),
                    parser.getMeter(), DeviceType.Modem, parser.getMeter().getModem().getDeviceSerial(),
                    DeviceType.Meter, parser.getMeterID(), parser.getMeterTime());
            
            // 상태에 대한 이벤트를 발생한다.
            for (STATUS s : STATUS.values()) {
                if ((status & s.getCode()) != 0x00) {
                    // 이벤트 발생
                }
            }
            log.info(parser.getMDevId() + " Metering END......!!!!");	
        }
        catch (Exception e) {
            log.error(e, e);
            throw e;
        }
        return true;
    }
	
    @Override
    public String relayValveOn(String mcuId, String meterId) {
        return null;
    }

    @Override
    public String relayValveOff(String mcuId, String meterId) {
        return null;
    }

    @Override
    public String relayValveStatus(String mcuId, String meterId) {
        return null;
    }

    @Override
    public String syncTime(String mcuId, String meterId) {
        return null;
    }
}
