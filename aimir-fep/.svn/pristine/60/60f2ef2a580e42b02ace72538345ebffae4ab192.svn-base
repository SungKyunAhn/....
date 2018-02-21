package com.aimir.fep.protocol.fmp.processor;

import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.fep.meter.data.NDHistoryData;
import com.aimir.fep.protocol.fmp.frame.service.NDData;
import com.aimir.fep.protocol.fmp.log.NDLogger;
import com.aimir.model.device.CommLog;

/**
 * Measurement Service Data Processor
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class NDProcessor extends Processor
{
    @Autowired
    private NDLogger ndLogger = null;
    
    /**
     * constructor
     *
     * @throws Exception
     */
    public NDProcessor() throws Exception
    {
        // ndLogger = (NDLogger)ctx.getBean("ndLogger");
        // ndLogger.init();
    }

    // send notification
    private void saveMeasurementData(NDData data)
    {
        NDHistoryData ndHistoryData = new NDHistoryData();
        ndHistoryData.setMcuId(data.getMcuId());
        ndHistoryData.setEntryCount(data.getCnt().getValue());
        ndHistoryData.setMdData(data.getMdData());
        try {
            /* TODO
             * 저장로직 추가해야함. 2010.05.07
            getHDM().saveMeasurementData(ndHistoryData);
            log.debug("saveMeasurementData");
            */
        }catch(Exception ex) 
        {
            log.error(ex);
            ndLogger.writeObject(ndHistoryData);
        }
    }

    /**
     * processing Measurement Data Service Data
     *
     * @param sdata <code>Object</code> ServiceData
     */
    public int processing(Object sdata) throws Exception
    {
        if(!(sdata instanceof NDData))
        {
            log.debug("processing data sdata does not NDData ");
            return 0;
        }
        NDData ndData = (NDData)sdata;
        saveMeasurementData(ndData);
        
        return 1;
    }
    
    /**
     * processing Measurement Data Service Data
     *
     * @param sdata <code>Object</code> ServiceData
     */
    public void processing(Object sdata, CommLog commLog) throws Exception
    {
        if(!(sdata instanceof NDData))
        {
            log.debug("processing data sdata does not NDData ");
            return;
        }
        NDData ndData = (NDData)sdata;
        saveMeasurementData(ndData);
    }

    @Override
    public void restore() throws Exception {
        // ndLogger = (NDLogger)ctx.getBean("ndLogger");
        ndLogger.init();
        
        try {
            if(ndLogger.isReadableObject())
            {
                NDHistoryData[] mdhds = (NDHistoryData[])ndLogger.readObject();
                log.info("ND list[" + mdhds.length+"]");
                for(int i = 0 ; i < mdhds.length ; i++)
                {
                    try {
                        /* TODO
                         * 저장 로직 추가해야함.
                        getHDM().saveMeasurementData(mdhds[i]);
                        */ 
                    }catch(Exception exx) {
                        log.error("NDProcessor failed",exx);
                        ndLogger.backupObject(mdhds[i]);
                    }
                }
            }
        }
        catch (Exception e) {
            log.warn(e.getMessage() + " try next");
        }
    }
}
