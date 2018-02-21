/**
 * @(#)ResetModemCommand.java       1.0 2009-02-20 *
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
 * ResetModemCommand Class.
 *
 * @version     1.0 20 Feb 2009
 * @author		Park YeonKyoung yeonkyoung@hanmail.net
 */


public class ResetModemCommand extends CommandIEIU {
    private static Log log = LogFactory.getLog(ResetModemCommand.class);
    
    public static final byte CMD_RESET = 0x02;

    public ResetModemCommand (byte cmd) {
        super(cmd,"");
    }

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

}
