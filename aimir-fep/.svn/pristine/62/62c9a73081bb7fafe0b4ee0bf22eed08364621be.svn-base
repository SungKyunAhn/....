/**
 * @(#)ControlRegCommand.java       1.0 03/09/01 *
 * Copyright (c) 2003-2004 NuriTelecom, Inc.
 * All rights reserved. *
 * This software is the confidential and proprietary information of
 * Nuritelcom, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Nuritelecom.
 */

package com.aimir.fep.protocol.mrp.command.frame.mmiu;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.mrp.command.frame.Command;
/**
 * ControlRegCommand Class.
 *
 * @version     1.0 1 Sep 2003
 * @author		Park YeonKyoung yeonkyoung@hanmail.net
 */


public class ControlRegCommand extends CommandMMIU {
    private static Log log = LogFactory.getLog(ControlRegCommand.class);
    
    /**
     * Member Variable Description.               <p> *
     * CMD_IP          0x10  IP Address           <p> * 
     * CMD_METER_PD    0x12  Metering Period Time <p> *
     * CMD_HW_RESET    0x13  Hardware Reset Time  <p> *
     * CMD_SSNKEY      0x14  Session Key          <p> *
     * CMD_METER_CODE  0x15  Meter Kind           <p> *
     * CMD_METER_ID    0x16  Meter ID             <p> *
     * CMD_MODEMRATE   0x17  Modem Rate           <p> *
     * CMD_PORT        0x18  PORT Number          <p> *
     */

    public static final byte CMD_IP         = 0x10;
    public static final byte CMD_METER_PD   = 0x12;
    public static final byte CMD_HW_RESET   = 0x13;
    public static final byte CMD_SSNKEY     = 0x14;
    public static final byte CMD_METER_CODE = 0x15;
    public static final byte CMD_METER_ID   = 0x16;
    public static final byte CMD_MODEMRATE  = 0x17;
    public static final byte CMD_PORT       = 0x18;

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
            temp[i++] = len;

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
            log.error(e);
        }

        return null;
    }

    public byte[] makeSingleCommand(){ return null;}
    
    /*
     * Test Class Method
     */
    public static void main(String[] argv) {

        try {
            byte[] ssnkey = new byte[16];
            String meter_id = "METER_ ID";
            byte[] meterid  = new byte[meter_id.length()];
            meterid    = meter_id.getBytes();

            for(int i = 0; i < 16; i++)
              ssnkey[i] = 0x01;

            Command c1 = new ControlRegCommand(ControlRegCommand.CMD_IP,"187.1.10.2");
            log.debug("\ncommand : " +c1.makeCommand());

            Command c2 = new ControlRegCommand(ControlRegCommand.CMD_PORT,"9999");
            log.debug("\ncommand : " +c2.makeCommand());

            Command c3 = new ControlRegCommand(ControlRegCommand.CMD_METER_PD,(byte)0x05);
            log.debug("\ncommand : " +c3.makeCommand());

            Command c4 = new ControlRegCommand(ControlRegCommand.CMD_HW_RESET,(byte)0x10);
            log.debug("\ncommand : " +c4.makeCommand());

            Command c5 = new ControlRegCommand(ControlRegCommand.CMD_SSNKEY,ssnkey);
            log.debug("\ncommand : " +c5.makeCommand());

            Command c6 = new ControlRegCommand(ControlRegCommand.CMD_METER_CODE,(byte)0x40);
            log.debug("\ncommand : " +c6.makeCommand());

            Command c7 = new ControlRegCommand(ControlRegCommand.CMD_METER_ID,"00");
            log.debug("\ncommand : " +c7.makeCommand());

        } catch (Exception e) {
            log.error(e);
        }
    }
}
