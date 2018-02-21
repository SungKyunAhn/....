/**
 * @(#)ReadNodeSearchCommand.java       1.0 2009-02-20 *
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
 * ReadNodeSearchCommand Class.
 *
 * @version     1.0 20 Feb 2009
 * @author		Park YeonKyoung yeonkyoung@hanmail.net
 */


public class ReadNodeSearchCommand extends CommandIEIU {
    private static Log log = LogFactory.getLog(ReadNodeSearchCommand.class);
    
    public static final byte CMD_READ_REG = 0x52;

    public ReadNodeSearchCommand (byte cmd) {
        super(cmd,"");
    }

    public byte[] makeCommand() {

		StringBuffer buffer = new StringBuffer();
        try {
        	buffer.append(header);
        	buffer.append((char)section);
        	buffer.append((char)cmd);
        	buffer.append((char)0x00);
        	buffer.append((char)getCheckSum(buffer.toString()));
        	buffer.append((char)end);

        	return buffer.toString().getBytes();

        } catch(Exception e) {
            log.error(e);
        }

        return null;
    }

    public byte[] makeSingleCommand(){ return null;}

}
