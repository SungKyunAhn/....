/**
 * @(#)MeterModeCommand.java       1.0 03/09/01 *
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
 * MeterModeCommand Class.
 *
 * @version     1.0 1 Sep 2003
 * @author		Park YeonKyoung yeonkyoung@hanmail.net
 */


public class MeterModeCommand extends CommandMMIU {
    private static Log log = LogFactory.getLog(MeterModeCommand.class);
    public static final byte CMD_METER_MODE = 0x60;

	/**
	 * Constructor.<p>
	 * @param cmd
	 */
    public MeterModeCommand(byte cmd) {
        super(cmd,"");
    }


	/**
	 * make command.<p>
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

            Command c = new MeterModeCommand(MeterModeCommand.CMD_METER_MODE);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
