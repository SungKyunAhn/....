package com.aimir.fep.protocol.fmp.command;

import java.util.Vector;

import com.aimir.fep.meter.parser.plc.PLCDataFrame;
import com.aimir.fep.protocol.fmp.common.Target;

/**
 * Command Proxy MBean which execute to MCU
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public interface CommandProxyMBean
{
    /**
     * start CommandProxy
     */
    public void start() throws Exception;

    /**
     * Execute Command
     */
    public Object execute(Target target, String cmdName,
            Vector attrs, Vector values) throws Exception;

    /**
     * Execute Command
     */
    public Object execute(Target target, String cmdName,
            Vector datas) throws Exception;

    /**
     * Execute Firmware Upload Command
     */
    public Object executeFirmwareUpdate(Target target, String filename,
            Integer type, String rsvTime) throws Exception;

    /**
     * notify Firmware Upload Command
     */
    public Object notifyFirmwareUpdate(Target target, String serverIp) throws Exception;

    /**
     * Execute File Upload Command
     */
    public Object executePutFile(Target target, String filename)
            throws Exception;

    /**
     * Execute File Download Command
     */
    public Object executeGetFile(Target target, String filename)
            throws Exception;

    /**
     * get CommandProxy ObjectName String
     */
    public String getName();

    /**
     * stop CommandProxy
     */
    public void stop();

}
