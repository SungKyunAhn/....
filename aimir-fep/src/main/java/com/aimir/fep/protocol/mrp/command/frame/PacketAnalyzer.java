/** 
 * @(#)PacketAnalyzer.java       1.0 03/09/01 *
 * Copyright (c) 2003-2004 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */

package com.aimir.fep.protocol.mrp.command.frame;


/** 
 * PacketAnalyzer Class. 
 * 
 * @version     1.0 1 Sep 2003 
 * @author		Park YeonKyoung yeonkyoung@hanmail.net   
 */

public class PacketAnalyzer {

    public static int REG_READDATA_LENGTH = 88; 
    public static int REG_READ_HEADER     = 8;


    /*
     * Contructer.<p>
     */
    public PacketAnalyzer() {

    }

    /*
     * Analyze Modem Register Information
     */
    public Modem readRegModem(String sequence) {

        Modem modem = new Modem();

        modem.setScuid     (pktStr(sequence, 0 , 16));
        modem.setSsnkey    (pktStr(sequence, 16, 16));
        modem.setServerip  (pktStr(sequence, 32, 16));
        modem.setPort      (pktStr(sequence, 48, 8 ));
        modem.setPeriod    (pktInt(sequence, 56, 4 ));
        modem.setReset     (pktInt(sequence, 60, 4 ));
        modem.setVersion   (pktStr(sequence, 64, 4 ));
        modem.setMetertype (pktInt(sequence, 68, 4 ));
        modem.setMeternum  (pktStr(sequence, 72, 12));
        modem.setRcvlevel  (pktInt(sequence, 84, 4 ));
        modem.setSpeed     (pktInt(sequence, 92, 4 ));
        
        return modem;
    }


    private String pktStr(String data, int startpoint, int length) {
        
        StringBuffer b = new StringBuffer();
        b.append(data.substring(REG_READ_HEADER+startpoint,
        						REG_READ_HEADER+startpoint+length));

        return b.toString();
    }

    private String pktInt(String data, int startpoint, int length) {

        int ret = 0;
        ret = pktStr(data,startpoint,length).hashCode();

        return String.valueOf(ret); 
    }


    @SuppressWarnings("unused")
	private int getLength(String data) {

        int ret = 0;

        try {
            byte[] b = data.getBytes();

            for(int i =0; b[i] != 0x3B; i++) 
                ret = i-REG_READ_HEADER;
        } catch(Exception e) {
        }
        return ret;
    } 
}
