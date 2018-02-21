/**
 * @(#)AuthPassCommandIEIU.java       1.0 03/09/01 *
 * Copyright (c) 2003-2004 NuriTelecom, Inc.
 * All rights reserved. *
 * This software is the confidential and proprietary information of
 * Nuritelcom, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Nuritelecom.
 */

package com.aimir.fep.protocol.mrp.command.frame.ieiu;

import com.aimir.fep.protocol.mrp.command.frame.Command;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * AuthPassCommandIEIU Class.
 *
 * @author 		Kang, Soyi
 */


public class AuthPassCommandIEIU extends CommandIEIU {

    private Log log = LogFactory.getLog(getClass());
    public static final byte CMD_AUTH_PASS     = 0x00;
    public static final char CMD_USERAUTH_PASS = 0x01;

    char cmmd;
	byte[] sessionkey = new byte[16];
	
    /**
     * Contructor.<p>
     * @param  cmd
     * @param data
     */
    public AuthPassCommandIEIU(byte cmd,byte[] sessionkey,String scuid) {
         super(cmd, scuid);
         this.sessionkey = sessionkey;
    }


    /**
     * Constructor.<p>
     * @param  command
     */
    public AuthPassCommandIEIU(char cmmd) {
        this.cmmd = cmmd;
    }


    /**
     *  make command.<p>
     *  @return command (type String)
     */
    public byte[] makeCommand() {
       
        try { 
            byte[] temp = new byte[1024];
            byte[] head = header.getBytes();
            byte[] encode = encodingtoXOR();

            int i = 0;

            for(; i < header.length(); i++)
                temp[i] = head[i];

            temp[i++] = section;
            temp[i++] = cmd;
            temp[i++] = 0x10;

            for(int x = 0; x < 16; x++)
                temp[i++] = encode[x];

            temp[i++] = getChecksum(temp);
            temp[i]   = end;

            byte[] command = new byte[i+1];

            for(int k=0; k<i+1; k++) 
                command[k] = temp[k]; 

			return command;

        } catch(Exception e) {
            log.warn("makecommand : "+e);
        }

        return null;
    } 

    
    /**
     * make command.<p>
     * @return type String
     */ 
    public byte[] makeSingleCommand() {
      
        try { 
            byte[] temp = new byte[1024];
            byte[] head = header.getBytes();

            int i = 0;

            for(; i < header.length(); i++)
                temp[i] = head[i];

            temp[i++] = section;
            temp[i++] = (byte)cmmd;
            temp[i++] = 0x00;
            temp[i++] = 0x00;
            
            temp[i++] = getChecksum(temp);
            temp[i]   = end;

            byte[] command = new byte[i+1];

			System.arraycopy(temp,0,command,0,i+1);
            return command;

        } catch(ArrayIndexOutOfBoundsException e) {
            log.error(e);
        }

        return null;
    }


	/**
	 *  encoding by xor operation. <p>
	 *  @return  byte array.       <p>
	 */ 
	public byte[] encodingtoXOR () throws Exception {

		byte[] b     = new byte[16];
		byte[] nbyte = new byte[16];
	
		try {

			if(data != null && data != ""){
				byte[] temp = data.getBytes();
				System.arraycopy(temp,0,b,0,temp.length);
			}

			for(int i = 0; i < 16; i++) {
				nbyte[i]=(byte)(b[i]^sessionkey[i]);
			}

		} catch (Exception e) {
			throw new Exception("XOR Encoding Exception!");
		}

		return nbyte;

	}


    /**
     * class test method.<p>
     */ 
    public static void main(String[] args) {

        try {
        	
        	String sessionkey = new String("2031551670507099");
            Command c 
                = new AuthPassCommandIEIU(AuthPassCommandIEIU.CMD_AUTH_PASS,sessionkey.getBytes(),
                                      "01192951370");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
