package com.aimir.fep.protocol.fmp.gateway.if9;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.aimir.fep.protocol.fmp.frame.service.AlarmData;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * IF9 Gate way
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
public class IF9Gateway
{
    private Log log = LogFactory.getLog(IF9Gateway.class);
    private static IF9Gateway instance= null;
    private ExecutorService pool = null;

    /**
     * get IF9 Gateway instance(singletone)
     */
    public static IF9Gateway getInstance()
    {
        if(instance == null)
        {
            instance = new IF9Gateway();
        }

        return instance;
    }

    /**
     * constructor
     */
    private IF9Gateway()
    {
        pool = Executors.newFixedThreadPool(20);
    }

    /**
     * sent task into Alarm External Gateway Queue
     * @param alarm - AU AlarmData
     */
    public boolean processing(AlarmData alarm)
    {
        try
        {
            // so.putOperationTask(task);
            pool.execute(getTargetTask(alarm));
            return true;
        }catch(Exception ex)
        {
            log.error("IF9Gateway failed ", ex);
        }
        return false;
    }

    private Runnable getTargetTask(AlarmData alarm) 
        throws Exception
    {
        /*
         * vendor 1 : safecon
         * vendor 2 : menix
         * other : unknown
         */
        int vendor = alarm.getVendor().getValue();

        if (vendor == 1)
        {
            return new SafeConExportTask(alarm);
        }
        else if (vendor == 2)
        {
            return new MenixExportTask(alarm);
        }
        else
        {
            log.debug("unknown vendor type");
            throw new Exception("unknown vendor type");
        }
    }
}
