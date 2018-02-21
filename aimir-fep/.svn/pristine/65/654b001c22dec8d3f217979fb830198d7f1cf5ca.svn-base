package com.aimir.fep.protocol.fmp.processor;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.constants.CommonConstants;
import com.aimir.fep.meter.MeterDataSaverWS;
import com.aimir.fep.meter.data.MDHistoryData;
import com.aimir.fep.protocol.fmp.frame.service.MDData;
import com.aimir.fep.protocol.fmp.frame.service.RMDData;
import com.aimir.fep.protocol.fmp.log.MDLogger;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.CommLog;

/**
 * Measurement Service Data Processor
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class MDProcessor extends Processor
{
    @Autowired
    private MDLogger mdLogger;
    
    /**
     * constructor
     *
     * @throws Exception
     */
    public MDProcessor() throws Exception
    {
        // mdLogger = (MDLogger)ctx.getBean("mdLogger");
        // mdLogger.init();
    }

    // send notification
    private void saveMeasurementData(MDData data) throws Exception
    {
    	log.debug("saveMeasurementData Start....");
        // try {
            log.debug("MCU_ID[" + data.getMcuId() + "] IP_ADDR[" + data.getIpAddr() + "] PROTOCOL_TYPE[" + data.getProtocolType() + "]");
            MeterDataSaverWS mds = DataUtil.getBean(MeterDataSaverWS.class);
            mds.save(data.getMcuId(), data.getCnt().getValue(), data.getMdData(), data.getIpAddr(), data.getProtocolType());
            log.debug("saveMeasurementData");
        // }
        // catch(Exception ex)
        // {
        //    log.error("saveMeasurementData failed",ex);
            // 2010.08.10 백업 데이타를 MDHistoryData에서 MDData로 변경.
        //    mdLogger.writeObject(data);
        //}
    }
    
    // send notification
    private void saveMeasurementData(RMDData data)
    {
        log.debug("saveMeasurementData Start....");
        try {
            MeterDataSaverWS mds = DataUtil.getBean(MeterDataSaverWS.class);
            
            mds.saveRData(data.getMcuId(), data.getCnt().getValue(), data.getrData());
            
            log.debug("saveMeasurementData");
        }
        catch(Exception ex)
        {
            log.error("saveMeasurementData failed",ex);
            // 2010.08.10 백업 데이타를 MDHistoryData에서 MDData로 변경.
            mdLogger.writeObject(data);
        }
    }

    /**
     * processing Measurement Data Service Data
     *
     * @param sdata <code>Object</code> ServiceData
     */
    public int processing(Object sdata) throws Exception
    {
        // 2017.03.24 SP-629
        if (sdata instanceof String) {
            log.debug("MDData_Filename[" + (String)sdata + "]");
            MDLogger log = new MDLogger();
            sdata = log.readLog((String)sdata);
        }
        
        if(sdata instanceof MDData)
        {
            MDData mdData = (MDData)sdata;
            log.debug("MCU_ID[" + mdData.getMcuId() + "] CNT[" + mdData.getCnt().getValue() + "]");
            saveMeasurementData(mdData);
            return mdData.getCnt().getValue();
        }
        else {
            log.warn("processing data is not MDData ");
            return 0;
        }
        
    }
    
    /**
     * processing Measurement Data Service Data
     *
     * @param sdata <code>Object</code> ServiceData
     */
    public void processing(Object sdata, CommLog commLog) throws Exception
    {
        commLog.setSvcTypeCode(CommonConstants.getHeaderSvc("S"));
        commLog.setOperationCode(ProcessorHandler.SERVICE_MEASUREMENTDATA);
        commLog.setTotalMeasumentDataCnt(((MDData)sdata).getCnt().getValue());
        
        processing(sdata);
    }

    @Override
    public void restore() throws Exception {
        // mdLogger = (MDLogger)ctx.getBean("mdLogger");
        // mdLogger.init();
        try {
            if(mdLogger.isReadableObject())
            {
                Serializable[] mdhds = mdLogger.readObject();
                for(int i = 0 ; i < mdhds.length ; i++)
                {
                    if (mdhds[i] instanceof MDData)
                    {
                        MDData data = (MDData)mdhds[i];
                            
                        try {
                            saveMeasurementData(data);
                        }
                        catch(Exception ex)
                        {
                            log.error("saveMeasurementData failed",ex);
                            // 2010.08.10 백업 데이타를 MDHistoryData에서 MDData로 변경.
                            mdLogger.backupObject(data);
                        }
                    }
                    else if (mdhds[i] instanceof MDHistoryData)
                    {
                        log.info("try MD History Data restored");
                        MDHistoryData mdhd = (MDHistoryData)mdhds[i];
                        try {
                            MeterDataSaverWS mds = DataUtil.getBean(MeterDataSaverWS.class);
                            mds.save(mdhd.getMcuId(), mdhd.getEntryCount(), mdhd.getMdData(), mdhd.getIpAddr(), mdhd.getProtocolType());
                        }
                        catch (Exception e)
                        {
                            log.error(e);
                            mdLogger.backupObject(mdhd);
                        }
                    }
                    else if (mdhds[i] instanceof RMDData) {
                        log.info("try RMD");
                        RMDData data = (RMDData)mdhds[i];
                        try {
                            MeterDataSaverWS mds = DataUtil.getBean(MeterDataSaverWS.class);
                            mds.saveRData(data.getMcuId(), data.getCnt().getValue(), data.getrData());
                        }
                        catch (Exception e)
                        {
                            log.error(e);
                            mdLogger.backupObject(data);
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
