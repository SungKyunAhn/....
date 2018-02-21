package com.aimir.fep.meter.saver;

import java.util.List;

import org.springframework.stereotype.Service;

import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.EnvData;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.EnvSensor;

@Service
public class EnvSensorMDSaver extends AbstractMDSaver {

    @Override
    protected boolean save(IMeasurementData md) throws Exception
    {
        EnvSensor parser = (EnvSensor)md.getMeterDataParser();
        
        List<EnvData> lpData = parser.getLpData();
        
        saveEnvData(parser.getMeter().getModem(),lpData);        
        
        return true;
    }

}
