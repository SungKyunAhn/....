/**
 * @(#)ByPassModeCommandIEIU.java       1.0 03/09/01 *
 * Copyright (c) 2003-2004 NuriTelecom, Inc.
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

import com.aimir.fep.protocol.mrp.command.frame.Command;
/**
 * ByPassModeCommandIEIU Class.
 *
 * @author		Kang, Soyi
 */


public class ByPassModeCommandIEIU extends CommandIEIU {
    private static Log log = LogFactory.getLog(ByPassModeCommandIEIU.class);
    
    public static final byte CMD_BYPASS_MODE = 0x06;

    public static byte grpid = 0x00;
    public static byte memid = 0x00;

    /**
     * Contructor.<p>
     * @param type byte
     */
    public ByPassModeCommandIEIU(byte cmd, byte grpid, byte memid) {
        super(cmd,"");
        this.grpid = grpid;
        this.memid = memid;
    }
    
    /**
     * make command.<p>
     * @return type String
     */
    public byte[]  makeCommand() {

        try {
            byte[] temp = new byte[1024];
            byte[] head = header.getBytes();

            int i = 0;

            for(; i < header.length(); i++)
                temp[i] = head[i];

            temp[i++] = section;
            temp[i++] = cmd;
            temp[i++] = 0x02;
            temp[i++] = 0x00;
            temp[i++] = grpid;
            temp[i++] = memid;
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
    

    /**
     * Class test method 
     */
    public static void main(String[] argv) {

        try {
            Command c = new ByPassModeCommandIEIU(ByPassModeCommandIEIU.CMD_BYPASS_MODE, grpid, memid);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
