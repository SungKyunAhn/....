package com.aimir.fep.protocol.fmp.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.MeterModel;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.fep.BaseTestCase;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.meter.MeterDataSaverMain;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.MDHistoryData;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.Mk10;
import com.aimir.fep.meter.saver.Mk10MDSaver;
import com.aimir.fep.protocol.fmp.common.LANTarget;
import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.INT;
import com.aimir.fep.protocol.fmp.datatype.OPAQUE;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.exception.FMPMcuException;

import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.protocol.fmp.frame.service.entry.meterDataEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.meterLPEntry;
import com.aimir.fep.protocol.mrp.client.lan.LANMMIUClient;

import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.MIBUtil;
import com.aimir.util.TimeUtil;


public class Mk10_Test extends BaseTestCase {
	static {
        DataUtil.setApplicationContext(new ClassPathXmlApplicationContext(new String[]{"/config/spring.xml"}));
    }
    private static Log log = LogFactory.getLog(Mk10_Test.class);
    
    private static LANTarget target;
    private static MeterDataSaverMain mdsMain;
    
    @Test
    public void ondemandTest(){
    		CommandGW cgw = new CommandGW();
    		try {
    			cgw.cmdOnDemandMeter("", "211327734", "Nuri-dc175a02", "0", TimeUtil.getCurrentDay()+"000000", TimeUtil.getCurrentDay()+"000000");
    			
    		} catch (FMPMcuException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
    }
}
