package com.aimir.fep.protocol.fmp.client;

import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.fmp.frame.service.AlarmData;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.protocol.fmp.frame.service.EventData;
import com.aimir.fep.protocol.fmp.frame.service.MDData;
import com.aimir.fep.protocol.fmp.frame.service.RMDData;


/**
 * Client Interface
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public interface Client
{
    /**
     * set Target
     *
     * @param target <code>TcpTarget</code> target
     */
    public void setTarget(Target target) throws Exception;

    /**
     * request command data to Target and response 
     *
     * @param command <code>CommandData</code> request command
     * @return response <code>ServiceData</code> response
     * @throws Exception
     */
    public CommandData sendCommand(CommandData commandData) 
        throws Exception;

    /**
     * send Alarm to Target 
     *
     * @param alarm <code>AlarmData</code> send alarm
     * @throws Exception
     */
    public void sendAlarm(AlarmData alarmData) throws Exception;

    /**
     * send Event to Target 
     *
     * @param event <code>EventData</code> event alarm
     * @throws Exception
     */
    public void sendEvent(EventData eventData) throws Exception;

    /**
     * send MeasurementData to Target 
     *
     * @param mdData <code>MDData</code> Measurement Data
     * @throws Exception
     */
    public void sendMD(MDData mdData) throws Exception;
    
    /**
     * send R MeasurementData to Target 
     *
     * @param rmdData <code>RMDData</code>R Measurement Data
     * @throws Exception
     */
    public void sendRMD(RMDData rmdData) throws Exception;

    /**
     * check whether connected or not
     *
     * @return connected <code>boolean</code>
     */
    public boolean isConnected(); 

    /**
     * close TCPClient session and wait completed
     *
     * @param wait <code>boolean</code> wait
     */
    public void close();
    
    /**
     * close TCPClient session and wait completed
     *
     * @param wait <code>boolean</code> wait
     */
    public void close(boolean wait);
}
