package com.aimir.fep.protocol.fmp.processor;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.fep.meter.MeterDataSaverMain;
import com.aimir.fep.meter.data.AMUMDHistoryData;
import com.aimir.fep.protocol.fmp.frame.amu.MeterPooling;
import com.aimir.fep.protocol.fmp.log.AMUMDLogger;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.CommLog;

/**
 * AMU Protocol Metering Frame Data Processor
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 24. 오후 5:08:42$
 */
public class AMUMDProcessor extends Processor{

    @Autowired
    private AMUMDLogger amuMdLogger;
    
    @Autowired
    private MeterDataSaverMain mds;
    
    /**
     * constructor
     *
     * @throws Exception
     */
    public AMUMDProcessor() throws Exception
    {
    	// spring-listener.xml에 등록된 Log Name
    	// amuMdLogger.init();
    }
    
    /**
     * checkMcuId
     * 
     * @param mcuId
     * @return
     */
    private boolean checkMcuId(String mcuId) {
        if (mcuId != null) {
            mcuId= mcuId.trim();
            for(int i = 0; i < mcuId.length(); i++){
                if(mcuId.charAt(i) < '0' || mcuId.charAt(i) > '9'){
                    return false;
                }
            }
        }
        else
            return false;
        return true;
    }
    
    /**
     * processing AMU MD Data
     *
     * @param sdata <code>Object</code> AMUGeneralDataFrame
     */
    public int processing(Object sdata) throws Exception
    {
         if(!(sdata instanceof MeterPooling))
         {
             log.debug("AMUMDProcessor sdata["+sdata+"] is not MeterPooling");
             return 0;
         }
         MeterPooling amuMDData = (MeterPooling)sdata;
         
        /* ********************* Save Metering Data *************************** */
        saveAmuMeasurementData(amuMDData);
        
        return 1;
    }
    
    /**
     * processing AMU MD Data
     *
     * @param sdata <code>Object</code> AMUGeneralDataFrame
     */
    public void processing(Object sdata, CommLog commLog) throws Exception
    {
    	 if(!(sdata instanceof MeterPooling))
         {
             log.debug("AMUMDProcessor sdata["+sdata+"] is not MeterPooling");
             return;
         }
    	 MeterPooling amuMDData = (MeterPooling)sdata;
         
        /* ********************* Save Metering Data *************************** */
        saveAmuMeasurementData(amuMDData);
    }
    
    
    /**
     * send notification
     * 
     * @param agdf	AMUGeneralDataFrame
     */
    private void saveAmuMeasurementData(MeterPooling meterPoolData)
    {
    	AMUMDHistoryData amuMdHistoryData = new AMUMDHistoryData();
        // String mcuId = Hex.decode(meterPoolData.getMcuId());
        // log.debug(" MCU ID " + Hex.decode(meterPoolData.getMcuId()));
        // if (checkMcuId(mcuId)) {
        	
        	//saveMeasurementData
            try {
            	log.debug("MeterPool Total ["+meterPoolData.encode().length+"] : " + Hex.decode(meterPoolData.encode()));
            	log.debug("saveAmuMeasurementData Source_Addr [" +meterPoolData.getSourceAddr()+ 
            	        "] Dest_Addr[" + meterPoolData.getDestAddr() + "]");
            	amuMdHistoryData.setSourceAddr(meterPoolData.getSourceAddr());
            	amuMdHistoryData.setDestAddr(meterPoolData.getDestAddr());
            	log.debug("saveAmuMeasurementData MertInfoCount [" +meterPoolData.getMeterInfoCnt()+ "]");
            	amuMdHistoryData.setEntryCount(meterPoolData.getMeterInfoCnt());	// meter Info Count
            	log.debug("MeterPoolData  ["+meterPoolData.getMeterPoolData().length+"] : " + Hex.decode(meterPoolData.getMeterPoolData()));
                amuMdHistoryData.setMdData(meterPoolData.getMeterPoolData());
                mds.save(amuMdHistoryData);
                log.debug("############ saveAmuMeasurementData Complete ############");
            }catch(Exception ex)
            {
                log.error("saveMeasurementData failed",ex);
                amuMdLogger.writeObject(amuMdHistoryData);
            }
        // }
        // else {
        //    log.error("MCUID[" + mcuId + "] invalid!");
        // }
    }

    
    @Override
    public void restore() throws Exception {

    	amuMdLogger.init();
    	     
         try {
             if(amuMdLogger.isReadableObject())
             {
                 Serializable[] mdhds = amuMdLogger.readObject();
                 for(int i = 0 ; i < mdhds.length ; i++)
                 {
                     if (mdhds[i] instanceof AMUMDHistoryData)
                     {
                         try { 
                             mds.save((AMUMDHistoryData)mdhds[i]);
                         }
                         catch(Exception exx) {
                             log.error("AMUMDProcessor failed",exx);
                             amuMdLogger.writeObject(mdhds[i]);
                         }
                     }
                     else
                     {
                         log.warn("Log type is [" + mdhds[i].getClass().getName() + "]");
                     }
                 }
             }
         }
         catch (Exception e) {
             log.warn(e.getMessage() + " try next");
         }
    }
}


