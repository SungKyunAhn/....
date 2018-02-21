/* 
 * @(#)DIF.java       1.0 2008-06-02 *
 * 
 * Data Information
 * Copyright (c) 2008-2009 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
/**
 * @author YK.Park
 */
package com.aimir.fep.meter.parser.kdhTable;

public class DIF {    

    public static final int EXTENSION_BIT_CODE = 0x80;
    public static final int FUNCTION_UNKNOWN    = -1;
    public static final int FUNCTION_CUMM       = 0;
    public static final int FUNCTION_INSTANT    = 1;
    public static final int FUNCTION_MAX        = 2;
    public static final int FUNCTION_MIN        = 3;   
    public static final int FUNCTION_AVG        = 4;
    public static final int FUNCTION_MAXINSTANT = 5;
    
    public static final int DATA_UNKNOWN       = -1;
    public static final int DATA_NODATA        = 0;
    public static final int DATA_8BIT_INTEGER  = 1;
    public static final int DATA_16BIT_INTEGER = 2;
    public static final int DATA_24BIT_INTEGER = 3;   
    public static final int DATA_32BIT_INTEGER = 4;
    public static final int DATA_2DIGIT_BCD    = 9;
    public static final int DATA_4DIGIT_BCD    = 10;
    public static final int DATA_6DIGIT_BCD    = 11;
    public static final int DATA_8DIGIT_BCD    = 12;

    public static final String[] DIF_FUNCTION_TABLE = {
        "Cummulative",
        "Instant", 
        "Max",
        "Min",
        "Avg",
        "Max Instant",
        "",
        "",
        "",
        ""
    };
    
    public static final String[] DIF_DATA_TABLE = 
    {
         "No data",
         "8 Bit Integer",
         "16 Bit Integer",
         "24 Bit Integer",
         "32 Bit Integer",
         "",
         "",
         "",
         "",
         "2 digit BCD",
         "4 digit BCD",
         "6 digit BCD",
         "8 digit BCD"         
    };
	
	private byte rawData = 0x00;

	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public DIF(byte rawData) {
        this.rawData = rawData;
	}
    
    public boolean isDIFE()
    {
        if((rawData & EXTENSION_BIT_CODE) > 0){
            return true;
        }
        return false;
    }
    
    public int getFunctionField()
    {
        int dif = (rawData & 0x7F) >> 4;
        
        switch(dif){
            case FUNCTION_CUMM       : 
            case FUNCTION_INSTANT    : 
            case FUNCTION_MAX        : 
            case FUNCTION_MIN        : 
            case FUNCTION_AVG        : 
            case FUNCTION_MAXINSTANT : 
                return dif;
        }

        return FUNCTION_UNKNOWN;        
    }
    
    public int getDataField()
    {
        int dif = rawData & 0x0F;
        
        switch(dif){
        case DATA_NODATA        :
        case DATA_8BIT_INTEGER  :
        case DATA_16BIT_INTEGER :
        case DATA_24BIT_INTEGER :   
        case DATA_32BIT_INTEGER :
        case DATA_2DIGIT_BCD    :
        case DATA_4DIGIT_BCD    :
        case DATA_6DIGIT_BCD    :
        case DATA_8DIGIT_BCD    :
                return dif;
        }

        return DATA_UNKNOWN;        
    }
    
    public int getDataFieldLength()
    {
        int dif = rawData & 0x0F;
        
        switch(dif){
        case DATA_NODATA        : return 4;
        case DATA_8BIT_INTEGER  : return 1;
        case DATA_16BIT_INTEGER : return 2;
        case DATA_24BIT_INTEGER : return 3;
        case DATA_32BIT_INTEGER : return 4;
        case DATA_2DIGIT_BCD    : return 1;
        case DATA_4DIGIT_BCD    : return 2;
        case DATA_6DIGIT_BCD    : return 3;
        case DATA_8DIGIT_BCD    : return 4;
        }

        return 4;        
    }

    public String toString()
    {
        int function_field = getFunctionField();
        int data_field = getDataField();
        String str = new String();
        
        switch(function_field){
            case FUNCTION_UNKNOWN    : str = "unknown function field["+function_field+"]\n";
            break;
            case FUNCTION_CUMM       : str = DIF_FUNCTION_TABLE[FUNCTION_CUMM]+"\n";
            break;
            case FUNCTION_INSTANT    : str = DIF_FUNCTION_TABLE[FUNCTION_INSTANT]+"\n";
            break;
            case FUNCTION_MAX        : str = DIF_FUNCTION_TABLE[FUNCTION_MAX]+"\n";
            break;
            case FUNCTION_MIN        : str = DIF_FUNCTION_TABLE[FUNCTION_MIN]+"\n";
            break;
            case FUNCTION_AVG        : str = DIF_FUNCTION_TABLE[FUNCTION_AVG]+"\n";
            break;
            case FUNCTION_MAXINSTANT : str = DIF_FUNCTION_TABLE[FUNCTION_MAXINSTANT]+"\n";
            break;
        }
        
        switch(data_field){
            case DATA_UNKNOWN       : str += "unknown data field["+data_field+"]"+"\n";
            break;            
            case DATA_NODATA        : str += DIF_DATA_TABLE[DATA_NODATA]+"\n";
            break;            
            case DATA_8BIT_INTEGER  : str += DIF_DATA_TABLE[DATA_8BIT_INTEGER]+"\n";
            break;            
            case DATA_16BIT_INTEGER : str += DIF_DATA_TABLE[DATA_16BIT_INTEGER]+"\n";
            break;            
            case DATA_24BIT_INTEGER : str += DIF_DATA_TABLE[DATA_24BIT_INTEGER]+"\n"; 
            break;              
            case DATA_32BIT_INTEGER : str += DIF_DATA_TABLE[DATA_32BIT_INTEGER]+"\n";
            break;            
            case DATA_2DIGIT_BCD    : str += DIF_DATA_TABLE[DATA_2DIGIT_BCD]+"\n";
            break;            
            case DATA_4DIGIT_BCD    : str += DIF_DATA_TABLE[DATA_4DIGIT_BCD]+"\n";
            break;            
            case DATA_6DIGIT_BCD    : str += DIF_DATA_TABLE[DATA_6DIGIT_BCD]+"\n";
            break;            
            case DATA_8DIGIT_BCD    : str += DIF_DATA_TABLE[DATA_8DIGIT_BCD]+"\n";
            break;            
        }
        if(isDIFE()){
            str += "DIFE flag exist\n";
        }else{
            str += "DIFE flag not exist\n";
        }
        
        return str;
    }
}
