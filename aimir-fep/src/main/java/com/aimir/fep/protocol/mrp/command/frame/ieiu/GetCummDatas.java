/**
 * @(#)GetCummDatas.java       1.0 2009-03-06 *
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
 * GetBasePulse Class.
 *
 * @author		Kang, Soyi
 */


public class GetCummDatas extends CommandIEIU {
    private static Log log = LogFactory.getLog(GetCummDatas.class);
    
    public static final byte CMD_GET_CUMM_DATA = (byte)0xC1;

    public static byte grpid = 0x00;
    public static byte memid = 0x00;
    public static byte[] date;

    /**
     * Contructor.<p>
     * @param type byte
     */
    public GetCummDatas(byte cmd, byte grpid, byte memid, byte[] date ) {
        super(cmd,"");
        this.grpid = grpid;
        this.memid = memid;
        this.date = date;
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
            temp[i++] = 0x06;
            temp[i++] = 0x00;
            temp[i++] = grpid;
            temp[i++] = memid;
            System.arraycopy(date, 0, temp, i, 4);
            i+= 4;
            
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

            Command c = new GetCummDatas(GetCummDatas.CMD_GET_CUMM_DATA, grpid, memid, null);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
