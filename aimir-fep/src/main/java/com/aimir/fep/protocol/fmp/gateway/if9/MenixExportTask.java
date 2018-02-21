package com.aimir.fep.protocol.fmp.gateway.if9;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.frame.service.AlarmData;

/**
 * safecon export
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
public class MenixExportTask implements Runnable
{
    private Log log = LogFactory.getLog(MenixExportTask.class);
    private AlarmData alarm = null;
    private Object result = null;

    /**
     * constructor
     *
     * @param alarm - AU AlarmData
     */
    public MenixExportTask(AlarmData alarm)
    {
        this.alarm = alarm;
    }

    /**
     * execute task
     */
    public void run()
    { 
        try
        {
            log.debug("IF9Gateway[Menix] AlarmData : " + alarm.toString());
        }
        catch(Exception ex)
        {
            log.error("MenixExporTask failed : " ,ex);
        }
    }

    /**
     * get result
     * @return data - result object
     */
    public Object getResult()
    {
        return this.result;
    }
}
