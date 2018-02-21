/**
 * @(#)ReadRegCommand.java       1.0 03/09/01 *
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
 * ReadRegCommand Class.
 *
 * @version     1.0 1 Sep 2003
 * @author		Park YeonKyoung yeonkyoung@hanmail.net
 */


public class ReadRegCommand extends CommandMMIU {
    private static Log log = LogFactory.getLog(ReadRegCommand.class);
    public static final byte CMD_READ_REG = 0x20;

    public ReadRegCommand (byte cmd) {
        super(cmd,"");
    }

    public byte[] makeCommand() {

		StringBuffer buffer = new StringBuffer();
        try {
        	buffer.append(header);
        	buffer.append((char)section);
        	buffer.append((char)cmd);
        	buffer.append((char)len);
        	buffer.append((char)getCheckSum(buffer.toString()));
        	buffer.append((char)end);

        	return buffer.toString().getBytes();

        } catch(Exception e) {
            log.error(e);
        }

        return null;
    }

    public byte[] makeSingleCommand(){ return null;}
    
    
	/*
	 * Test Class
	 */
    public static void main(String[] argv) {

        try {

            Command c = new ReadRegCommand(ReadRegCommand.CMD_READ_REG);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
