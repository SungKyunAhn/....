/**
 * @(#)ControlRegCommand.java       1.0 2009-02-20 *
 * Copyright (c) 2009-2010 NuriTelecom, Inc.
 * All rights reserved. *
 * This software is the confidential and proprietary information of
 * Nuritelcom, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Nuritelecom.
 */

package com.aimir.fep.protocol.mrp.command.frame.ieiu;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ControlRegCommand Class.
 *
 * @version     1.0 20 Feb 2009
 * @author		Park YeonKyoung yeonkyoung@hanmail.net
 */


public class ControlRegCommand extends CommandIEIU {
    private static Log log = LogFactory.getLog(ControlRegCommand.class);
    
    public static final byte CMD_IP             = (byte)0xA1;
    public static final byte CMD_PORT           = (byte)0xA2;
    public static final byte CMD_METER_INTERVAL = (byte)0xA3;
    public static final byte CMD_RESET_INTERVAL = (byte)0xA4;
    public static final byte CMD_METERINSTALL   = (byte)0xA5;
    public static final byte CMD_COMMSPEED      = (byte)0xA7;
    public static final byte CMD_ZIGBEE_ID      = (byte)0xA9;

    byte[] dat;

    /**
     *  Constructor.                                    <p>
     *  @param cmd  : packet session byte type command. <p>
     *  @param data : packet data for register setting. <p>
     */
    public ControlRegCommand(byte cmd, String data) {
        this.cmd = cmd;
        this.dat = data.getBytes();
        setLen(dat);
    }


    /**
     *  Constructor.                                          <p>
     *  @param cmd  : packet session byte type command.       <p>
     *  @param data : packet 1byte data for register setting. <p>
     */
    public ControlRegCommand(byte cmd, byte data) {
        this.cmd = cmd;
        this.dat = new byte[1];
        this.dat[0] = data;
        setLen(dat);
    }


    /**
     *  Constructor.                                               <p>
     *  @param cmd  : packet session byte type command.            <p>
     *  @param data : packet bytes type data for register setting. <p>
     */
    public ControlRegCommand(byte cmd, byte[] data) {
        this.cmd = cmd;
        this.dat = data;
        setLen(dat);
    }
   

    /**
     *  make register control command. <p>
     */ 
    public byte[] makeCommand() {

        try {
            byte[] temp = new byte[1024];
            byte[] head = header.getBytes();

            int i = 0;

            for(; i < header.length(); i++)
                temp[i] = head[i];

            temp[i++] = section;
            temp[i++] = cmd;
            temp[i++] = 0x00;

            try {
                for(int x = 0; ; x++)
                    temp[i++] = dat[x];
            } catch(ArrayIndexOutOfBoundsException e) {
            }
            i = i-1;

            temp[i++] = getChecksum(temp);
            temp[i]   = end;

            byte[] command = new byte[i+1];

            for(int k=0; k<i+1; k++)
                command[k] = temp[k];

            return command;

        } catch(ArrayIndexOutOfBoundsException e) {
            log.debug("makecommand : "+e);
            e.printStackTrace();
        }

        return null;
    }

    public byte[] makeSingleCommand(){ return null;}
    
}
