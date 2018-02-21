package com.aimir.fep.protocol.fmp.processor;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.fep.meter.parser.plc.PLCData;
import com.aimir.fep.protocol.fmp.log.NDLogger;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.CommLog;

/**
 * Partial Frame Data Processor
 *
 * @author J.S Park (elevas@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2009-05-13 15:59:15 +0900 $,
 */
public class PLCProcessor extends Processor
{
    @Autowired
    private NDLogger ndLogger = null;

    /**
     * constructor
     *
     * @throws Exception
     */
    public PLCProcessor() throws Exception
    {
        // ndLogger = (NDLogger)ctx.getBean("ndLogger");
        // ndLogger.init();
    }

    private boolean checkMcuId(String mcuId) {
        if (mcuId != null) {
            mcuId=mcuId.trim();
            for(int i = 0; i < mcuId.length(); i++){
                if(mcuId.charAt(i) < '0' || mcuId.charAt(i) > '9'){
                    return false;
                }
            }
        }
		else {
			return false;
		}
        return true;
    }


    // send notification
    private void savePLCData(PLCData pd)
    {
        try {
            /* TODO
             * 저장로직 추가해야함. 2010.05.07
            getHDM().savePLCData(pd);
            */
            log.debug("saveMeasurementData");
        }catch(Exception ex)
        {
            log.error("saveMeasurementData failed",ex);
            ndLogger.writeObject(pd);
        }
    }

    /**
     * processing Measurement Data Service Data
     *
     * @param sdata
     *            <code>Object</code> ServiceData
     */
    public int processing(Object data) throws Exception {
        if (!(data instanceof PLCData)) {
            log.debug(data.getClass().getName());
            return 0;
        }
        PLCData pdData = (PLCData) data;
        savePLCData(pdData);
        
        return 1;
    }

	/**
	 * processing Measurement Data Service Data
	 *
	 * @param sdata
	 *            <code>Object</code> ServiceData
	 */
	public void processing(Object data, CommLog commLog) throws Exception {
		if (!(data instanceof PLCData)) {
			log.debug(data.getClass().getName());
			return;
		}
		PLCData pdData = (PLCData) data;
		savePLCData(pdData);
	}


    private static DATA_TYPE getDataType(int type)
    {
        for (int i = 0; i < DATA_TYPE.values().length; i++) {
            if (DATA_TYPE.values()[i].getType() == type) {
				return DATA_TYPE.values()[i];
			}
        }
        return DATA_TYPE.UNKNOWN;
    }

    enum DATA_TYPE {
        GENERIC(0),
        MEASUREMENT(1),
        SYSTEM(2),
        APPLICATION(3),
        CONFIG(4),
        UNKNOWN(255);

        private int type;

        DATA_TYPE(int type) {
            this.type = type;
        }

        public int getType()
        {
            return type;
        }

        public void setType(int type)
        {
            this.type = type;
        }
    }

    @Override
    public void restore() throws Exception {

        ndLogger.init();

        File dir = new File(FMPProperty.getProperty("protocol.slidewindow.dir"));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File[] files = dir.listFiles();
        for (File file : files) {
            try {
                log.info(file.getAbsoluteFile());
                //readMeteringData(file.getAbsolutePath());
            }
            catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }
}
