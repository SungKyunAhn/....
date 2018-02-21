/** 
 * @(#)ANSI.java       1.0 04/09/30 *
 * Copyright (c) 2004-2005 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.protocol.mrp.protocol;

/**
 * @author Park YeonKyoung  yeonkyoung@hanmail.net
 */
public class ANSI {
    
    public static final byte OFS_STP     = 1;
    public static final byte OFS_IDENTI  = 2;
    public static final byte OFS_CTRL    = 3;
    public static final byte OFS_SEQ_NBR = 4;
    public static final byte OFS_LENGTH  = 5;
    
    public static final int  LEN_HEADER  = 7;
    public static final int  LEN_CRC     = 2;
    
    public static final byte STP = (byte)0xee;
    public static final byte REV_OPTICAL = 0x00;//ANSI 12.18
    public static final byte REV_MODEM   = 0x40;//ANSI 12.21
    public static final byte ACK = 0x06;
    public static final byte NAK = 0x15;

    public static final byte OK    = 0x00;
    public static final byte ERR   = 0x01;
    public static final byte SNS   = 0x02;
    public static final byte ISC   = 0x03;
    public static final byte ONP   = 0x04;
    public static final byte IAR   = 0x05;
    public static final byte BSY   = 0x06;
    public static final byte DNR   = 0x07;
    public static final byte DLK   = 0x08;
    public static final byte RNO   = 0x09;
    public static final byte ISSS  = 0x0a;
    
    public static final byte IDENT  = 0x20;
    public static final byte WRITE  = 0x40;
    public static final byte NEGO   = 0x60;       // negotiate default baud rate
    public static final byte NEGO_B1= 0x61;
    public static final byte LOGON  = 0x50;       // log on
    public static final byte AUTH   = 0x53;       // authenticate
    public static final byte READ   = 0x30;       // read
    public static final byte PREAD  = 0x3f;       // partial read offset
    public static final byte WAIT   = 0x70;       // wait
    public static final byte LOGOFF = 0x52;       // log off
    public static final byte TERMI  = 0x21;       // terminate
    public static final byte DISCON = 0x22;       // disconnect
    public static final byte RR     = (byte)0xFF; // reading continue
    
    public static final byte BAUD_EXT   = 0x00; // externally defined
    public static final byte BAUD_300   = 0x01; // 300 baud
    public static final byte BAUD_600   = 0x02; // 600 baud
    public static final byte BAUD_1200  = 0x03; // 1200 baud
    public static final byte BAUD_2400  = 0x04; // 2400 baud
    public static final byte BAUD_4800  = 0x05; // 4800 baud
    public static final byte BAUD_9600  = 0x06; // 9600 baud
    public static final byte BAUD_14400 = 0x07; // 14400 baud
    public static final byte BAUD_19200 = 0x08; // 19200 baud
    public static final byte BAUD_28800 = 0x09; // 28800 baud
    public static final byte BAUD_57600 = 0x0A; // 57600 baud

    public static final byte OPT_AUTH = 0x51;       // security
    
    public static final int CHANNEL_TRAFFIC_TIME_OUT = 6000;//milliseconds
    public static final int INTER_CHARACTER_TIME_OUT = 500;//milliseconds
    public static final int RESPONSE_TIME_OUT = 2000;//milliseconds
    public static final int TURN_AROUND_DELAY = 175;//milliseconds
    
    // meter_read_class
    public static final char READ_ST_01 = 0x0001;  //manufactorer 
    public static final char READ_ST_03 = 0x0003;  //meter status (error & warning)     
    public static final char READ_ST_05 = 0x0005;  //meter serial number
    public static final char READ_ST_07 = 0x0007;  //procedure initiate table
    public static final char READ_ST_08 = 0x0008;  //procedure response table
    public static final char READ_ST_11 = 0x000B;  //Actual sources limiting table
    public static final char READ_ST_15 = 0x000f;  //meter constant, VT ratio, CT ratio
    public static final char READ_ST_22 = 0x0016;  //meter data selection table
    public static final char READ_ST_21 = 0x0015;  //meter actual register table
    public static final char READ_ST_55 = 0x0037;  //date & time of meter 
    public static final char READ_ST_25 = 0x0019;  //previous billing
    public static final char READ_ST_23 = 0x0017;  //current billing
    public static final char READ_ST_60 = 0x003c;  //Dimension Profile Table
    public static final char READ_ST_61 = 0x003d;  //load profile actual configuration
    public static final char READ_ST_62 = 0x003e;  //load profile control
    public static final char READ_ST_63 = 0x003f;  //load profile status
    public static final char READ_ST_64 = 0x0040;  //load profile data
    public static final char READ_ST_71 = 0x0047;  //event log Actual Dimension Log Table
    public static final char READ_ST_72 = 0x0048;  //event identification table
    public static final char READ_ST_75 = 0x004b;  //event log control table
    public static final char READ_ST_76 = 0x004c;  //event log table
    public static final char READ_ST_51 = 0x0033;  // actual time tou
    public static final char READ_ST_54 = 0x0036;  // calendar table
    
    public static final int LEN_ST_05 = 33;  
    public static final int LEN_ST_15 = 163; 
    public static final int LEN_ST_22 = 23;//max 23  
    public static final int LEN_ST_21 = 23;

    public static final int LEN_ST_03 = 30;
    //public static final int LEN_ST_76 = ;
    public static final int LEN_ST_55 = 22;
    public static final int LEN_ST_25 = 320;
    public static final int LEN_ST_23 = 314;
    public static final int LEN_ST_61 = 26;
    public static final int LEN_ST_63 = 26;
    public static final int LEN_ST_64 = 513;
    public static final int LEN_ST_71 = 22;

    
    public static final byte[] PERM1 = {
                57, 49, 41, 33, 25, 17,  9,  1, 58, 50, 42, 34, 26, 18, 10,
                 2, 59, 51, 43, 35, 27, 19, 11,  3, 60, 52, 44, 36, 63, 55,
                47, 39, 31, 23, 15,  7, 62, 54, 46, 38, 30, 22, 14,  6, 61,
                53, 45, 37, 29, 21, 13,  5, 28, 20, 12,  4  
                };

    public static final byte[] PERM2 = {
                 2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15, 16,
                17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28,  1, 30, 31,
                32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46,
                47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 29
               };

    public static final byte[] PERM3 = {    
                14, 17, 11, 24,  1,  5,  3, 28, 15,  6, 21, 10, 23, 19, 12, 
                 4, 26,  8, 16,  7, 27, 20, 13,  2, 41, 52, 31, 37, 47, 55,
                30, 40, 51, 45, 33, 48, 44, 49, 39, 56, 34, 53, 46, 42, 50,
                36, 29, 32
               };

    public static final byte[] PERM4 = {
                58, 50, 42, 34, 26, 18, 10,  2, 60, 52, 44, 36, 28, 20, 12, 
                 4, 62, 54, 46, 38, 30, 22, 14,  6, 64, 56, 48, 40, 32, 24, 
                16,  8, 57, 49, 41, 33, 25, 17,  9,  1, 59, 51, 43, 35, 27,
                19, 11,  3, 61, 53, 45, 37, 29, 21, 13,  5, 63, 55, 47, 39,
                31, 23, 15, 7
               };

    public static final byte[] PERM5 = {
                32,  1,  2,  3,  4,  5,  4,  5,  6,  7,  8,  9,  8,  9, 10,
                11, 12, 13, 12, 13, 14, 15, 16, 17, 16, 17, 18, 19, 20, 21, 
                20, 21, 22, 23, 24, 25, 24, 25, 26, 27, 28, 29, 28, 29, 30,
                31, 32, 1
               };

    public static final byte[] PERM6 = {
                16,  7, 20, 21, 29, 12, 28, 17,  1, 15, 23, 26,  5, 18, 31,
                10,  2,  8, 24, 14, 32, 27,  3,  9, 19, 13, 30,  6, 22, 11,
                 4, 25
               };

    public static final byte[] PERM7 = {
                40,  8, 48, 16, 56, 24, 64, 32, 39,  7, 47, 15, 55, 23, 63,
                31, 38,  6, 46, 14, 54, 22, 62, 30, 37,  5, 45, 13, 53, 21, 
                61, 29, 36,  4, 44, 12, 52, 20, 60, 28, 35,  3, 43, 11, 51,
                19, 59, 27, 34,  2, 42, 10, 50, 18, 58, 26, 33,  1, 41,  9,
                49, 17, 57, 25
               };

    public static final byte[][] SBOXES = {
               {14,  4, 13,  1,  2, 15, 11,  8,  3, 10,  6, 12,  5,  9,  0, 
                 7,  0, 15,  7,  4, 14,  2, 13,  1, 10,  6, 12, 11,  9,  5, 
                 3,  8,  4,  1, 14,  8, 13,  6,  2, 11, 15, 12,  9,  7,  3,
                10,  5,  0, 15, 12,  8,  2,  4,  9,  1,  7,  5, 11,  3, 14,
                10,  0,  6, 13, },
               {15,  1,  8, 14,  6, 11,  3,  4,  9,  7,  2, 13, 12,  0,  5,
                10,  3, 13,  4,  7, 15,  2,  8, 14, 12,  0,  1, 10,  6,  9,
                11,  5,  0, 14,  7, 11, 10,  4, 13,  1,  5,  8, 12,  6,  9, 
                 3,  2, 15, 13,  8, 10,  1,  3, 15,  4,  2, 11,  6,  7, 12, 
                 0,  5, 14,  9, },
               {10,  0,  9, 14,  6,  3, 15,  5,  1, 13, 12,  7, 11,  4,  2, 
                 8, 13,  7,  0,  9,  3,  4,  6, 10,  2,  8,  5, 14, 12, 11,
                15,  1, 13,  6,  4,  9,  8, 15,  3,  0, 11,  1,  2, 12,  5,
                10, 14,  7,  1, 10, 13,  0,  6,  9,  8,  7,  4, 15, 14,  3,
                11,  5,  2, 12, },
               { 7, 13, 14,  3,  0,  6,  9, 10,  1,  2,  8,  5, 11, 12,  4, 
                15, 13,  8, 11,  5,  6, 15,  0,  3,  4,  7,  2, 12,  1, 10,
                14,  9, 10,  6,  9,  0, 12, 11,  7, 13, 15,  1,  3, 14,  5, 
                 2,  8,  4,  3, 15,  0,  6, 10,  1, 13,  8,  9,  4,  5, 11,
                12,  7,  2, 14, },
               { 2, 12,  4,  1,  7, 10, 11,  6,  8,  5,  3, 15, 13,  0, 14, 
                 9, 14, 11,  2, 12,  4,  7, 13,  1,  5,  0, 15, 10,  3,  9, 
                 8,  6,  4,  2,  1, 11, 10, 13,  7,  8, 15,  9, 12,  5,  6, 
                 3,  0, 14, 11,  8, 12,  7,  1, 14,  2, 13,  6, 15,  0,  9,
                10,  4,  5,  3, },
               {12,  1, 10, 15,  9,  2,  6,  8,  0, 13,  3,  4, 14,  7,  5,
                11, 10, 15,  4,  2,  7, 12,  9,  5,  6,  1, 13, 14,  0, 11, 
                 3,  8,  9, 14, 15,  5,  2,  8, 12,  3,  7,  0,  4, 10,  1,
                13, 11,  6,  4,  3,  2, 12,  9,  5, 15, 10, 11, 14,  1,  7, 
                 6,  0,  8, 13, },
               { 4, 11,  2, 14, 15,  0,  8, 13,  3, 12,  9,  7,  5, 10,  6, 
                 1, 13,  0, 11,  7,  4,  9,  1, 10, 14,  3,  5, 12,  2, 15, 
                 8,  6,  1,  4, 11, 13, 12,  3,  7, 14, 10, 15,  6,  8,  0, 
                 5,  9,  2,  6, 11, 13,  8,  1,  4, 10,  7,  9,  5,  0, 15,
                14,  2,  3, 12, },
               {13,  2,  8,  4,  6, 15, 11,  1, 10,  9,  3, 14,  5,  0, 12, 
                 7,  1, 15, 13,  8, 10,  3,  7,  4, 12,  5,  6, 11,  0, 14, 
                 9,  2,  7, 11,  4,  1,  9, 12, 14,  2,  0,  6, 10, 13, 15, 
                 3,  5,  8,  2,  1, 14,  7,  4, 10,  8, 13, 15, 12,  9,  0, 
                 3,  5,  6, 11, }
              };

    
    public static boolean crc_check(byte[] buf, int len, byte meter_cmd) {
    
        byte kind;
        char crc;

        char crch = (char)((buf[len - 2] << 8));
        char crcl = (char)(buf[len - 1] & 0xFF);

        crc = (char)(crch | crcl);

        if(meter_cmd == RR)
            kind = 0;
        else 
            kind = 1;
    
        if(crc == (crc(len - (2 + kind), kind, buf))) 
            return true;        
        return false;
    }
    
    public static char crc(int size, byte kind, byte[] buf){
        int i = 0;
        char crc;
        
        crc = (char) ((~buf[1+kind] << 8) | (~buf[0+kind] & 0xFF));
        
        for(i = 2; i < size; i++)
            crc = crc16(buf[i+kind],crc);
        
        crc = crc16((byte) 0x00,crc);
        crc = crc16((byte) 0x00,crc);
        crc = (char) ~crc;
        crc = (char) (crc >> 8 | crc << 8);
        
        return crc;
    }
    
    
    public static char crc16(byte octet, char o_crc){
        
        char d_crc = o_crc;
        for(int i = 8; i > 0; i--){
            if((d_crc & 0x0001) != 0){
                d_crc >>= 1;
                if((octet & 0x01) != 0)
                    d_crc |= 0x8000;
                
                d_crc = (char) (d_crc ^ 0x8408);
                octet >>= 1;
                
            }else{
                d_crc >>= 1;
                if((octet & 0x01) != 0)
                    d_crc |= 0x8000;
                octet >>= 1;
            }
        }
        return d_crc;
        
    }

    public static byte chkSum(byte[] buf, int nPos, int nLength){

        byte bytCheck = 0;

        if( nLength <= 0 )  return -1;

        for( int x = 0; x < nLength; x++ ) {
            bytCheck +=(( buf[x+nPos]^0xFF ) + 1);
        }
        return bytCheck;
    }   

    public static boolean check_response(byte b) throws Exception {
        
        switch(b){
            case 0x00:
                return true;
            case 0x01:
                throw new Exception("Error-Rejection of the received service request");
            case 0x02:
                throw new Exception("Service Not Supported");
            case 0x03:
                throw new Exception("Insufficient Security Clearance");
            case 0x04:
                throw new Exception("Operation Not Possible");
            case 0x05:
                throw new Exception("Inappropriate Action Requested");
            case 0x06:
                throw new Exception("Device Busy");
            case 0x07:
                throw new Exception("Data Not Ready");
            case 0x08:
                throw new Exception("Data Locked");
            case 0x09:
                throw new Exception("Renegotiate Request");
            case 0x0A:
                throw new Exception("Invalid Service Sequence");
            default :
                if(b >= 0x0b && b <= 0x1F){
                    throw new Exception("Codes are currently undefined");
                }
                if(b >= 0x20 && b <= 0x7F){
                    throw new Exception("Codes shall not be used to avoid confusion with request codes");
                }
                if(b >= 0x80 && b <= 0xFF){
                    throw new Exception("Codes shall be reserved for protocol extensions");
                }
        }
        return false;
    }
    
}