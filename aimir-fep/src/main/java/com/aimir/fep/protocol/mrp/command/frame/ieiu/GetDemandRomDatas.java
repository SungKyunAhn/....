/**
 * @(#)GetDemandRomDatas.java       1.0 2009-07-29 *
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

/**
 * GetDemandRomDatas
 *
 * @author		YK.Park
 */


public class GetDemandRomDatas extends CommandIEIU {
    private static Log log = LogFactory.getLog(GetDemandRomDatas.class);
    
    public static final byte CMD_GET_DEMANDROM_DATA = (byte)0xC1;
    
    public static final byte[][] ROM_METER_ENABLE = {{0x00,0x00,0x00},
    													{0x00,(byte)0xCC,(byte)0xCC},
    													{0x01,(byte)0x99,(byte)0x98},
    													{0x02,(byte)0x66,(byte)0x64},
    													{0x03,(byte)0x33,(byte)0x30}};
    public static final byte[][] ROM_METER_SERIAL = {{0x00,0x00,0x01},
    													{0x00,(byte)0xCC,(byte)0xCD},
    													{0x01,(byte)0x99,(byte)0x99},
    													{0x02,(byte)0x66,(byte)0x65},
    													{0x03,(byte)0x33,(byte)0x31}};
    public static final byte[][] ROM_METER_EVENT = {{0x00,0x00,(byte)0x11},
    													{0x00,(byte)0xCC,(byte)0xDD},
    													{0x01,(byte)0x99,(byte)0xA9},
    													{0x02,(byte)0x66,(byte)0x75},
    													{0x03,(byte)0x33,(byte)0x41}};
    public static final byte[][] ROM_METER_LPPOINTER = {{0x00,0x00,(byte)0x98},
    													{0x00,(byte)0xCD,(byte)0x64},
    													{0x01,(byte)0x9A,(byte)0x30},
    													{0x02,(byte)0x66,(byte)0xFC},
    													{0x03,(byte)0x33,(byte)0xC8}};
    public static final byte[][] ROM_METER_LPDATA = {{0x00,0x00,(byte)0x9A},
    													{0x00,(byte)0xCD,(byte)0x66},
    													{0x01,(byte)0x9A,(byte)0x32},
    													{0x02,(byte)0x66,(byte)0xFE},
    													{0x03,(byte)0x33,(byte)0xCA}};

    public static byte grpid = 0x00;
    public static byte memid = 0x00;
    public static byte[] date;

    /**
     * Contructor.<p>
     * @param type byte
     */
    public GetDemandRomDatas(byte cmd, byte grpid, byte memid, byte[] date ) {
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
            temp[i++] = 0x07;
            temp[i++] = 0x00;//dlen 2byte
            temp[i++] = this.grpid;
            temp[i++] = this.memid;
            temp[i++] = 0x02;//read request
            

            	
            /*
			temp[i++] = (byte)(840 >> 8);//rom length
            temp[i++] = (byte)(840);//rom length
            int romAddress = 0;
            int n = 0;
            int METER_LPDATA = DataUtil.getIntToBytes(ROM_METER_LPDATA[(int)memid]);
            int METER_LPPOINTER = DataUtil.getIntToBytes(ROM_METER_LPPOINTER[(int)memid]);
            
            romAddress = METER_LPDATA + (((METER_LPPOINTER-(n))+40)%40)*96+(31*n) ;

            
            temp[i++] = (byte)(romAddress >> 16);
            temp[i++] = (byte)(romAddress >> 8);
            temp[i++] = (byte)(romAddress);
                        */
            
            temp[i++] = date[0];
            temp[i++] = date[1];
            temp[i++] = date[2];
            temp[i++] = date[3];
            
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
