package com.aimir.fep.protocol.fmp.frame;

import java.lang.reflect.Field;

import com.aimir.fep.protocol.fmp.datatype.BYTE;

/**
 * MCU Error Constants
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-07 15:59:15 +0900 $,
 */
public class ErrorCode
{
    static public final int IF4ERR_NOERROR              = 0;
    static public final int IF4ERR_INVALID_ID           = 1;
    static public final int IF4ERR_INVALID_OID          = 2;
    static public final int IF4ERR_INVALID_INDEX        = 3;
    static public final int IF4ERR_INVALID_VALUE        = 4;
    static public final int IF4ERR_INVALID_ACCOUNT      = 5;
    static public final int IF4ERR_INVALID_PASSWORD     = 6;
    static public final int IF4ERR_INVALID_LENGTH       = 7;
    static public final int IF4ERR_INVALID_TYPE         = 8;
    static public final int IF4ERR_INVALID_COUNT        = 9;
    static public final int IF4ERR_INVALID_PARAM        = 10;
    static public final int IF4ERR_INVALID_TARGET       = 11;
    static public final int IF4ERR_INVALID_HOST         = 12;
    static public final int IF4ERR_INVALID_HANDLE       = 13;
    static public final int IF4ERR_INVALID_FORMAT       = 14;
    static public final int IF4ERR_INVALID_ADDRESS      = 15;
    static public final int IF4ERR_INVALID_PORT         = 16;
    static public final int IF4ERR_INVALID_BUFFER       = 17;
    static public final int IF4ERR_INVALID_PROTOCOL     = 18;
    static public final int IF4ERR_INVALID_GROUP        = 19;
    static public final int IF4ERR_INVALID_NAME         = 20;
    static public final int IF4ERR_INVALID_FILENAME     = 21;
    static public final int IF4ERR_INVALID_COMMAND      = 22;
    static public final int IF4ERR_INVALID_CHANNEL      = 23;
    static public final int IF4ERR_INVALID_PANID        = 24;
    static public final int IF4ERR_INVALID_SINK         = 25;
    static public final int IF4ERR_INVALID_INVOKE       = 26;
    static public final int IF4ERR_INVALID_EVENT        = 27;
    static public final int IF4ERR_INVALID_MEMBER       = 28;
    static public final int IF4ERR_INVALID_APN          = 29;
    static public final int IF4ERR_INVALID_URL          = 30;
    static public final int IF4ERR_INVALID_VERSION      = 31;
    static public final int IF4ERR_INVALID_TRIGGERID    = 32;
    static public final int IF4ERR_INVALID_OTA_TYPE     = 33;
    static public final int IF4ERR_INVALID_MODEL        = 34;
    static public final int IF4ERR_INVALID_TRANSFER_TYPE= 35;
    static public final int IF4ERR_INVALID_OTA_STEP     = 36;
    static public final int IF4ERR_INVALID_BIN_URL      = 37;
    static public final int IF4ERR_INVALID_BIN_CHECKSUM = 38;
    static public final int IF4ERR_INVALID_DIFF_URL     = 39;
    static public final int IF4ERR_INVALID_DIFF_CHECKSUM= 40;
    static public final int IF4ERR_INVALID_TRANSACTION_ID = 41;
    static public final int IF4ERR_CANNOT_ACCEPT        = 50;
    static public final int IF4ERR_BUSY_METERING        = 60;
    static public final int IF4ERR_BUSY_RECOVERY        = 61;
    static public final int IF4ERR_BUSY_TIMESYNC        = 62;
    static public final int IF4ERR_OUT_OF_BINDING       = 70;
    static public final int IF4ERR_INTERNAL_ERROR       = 71;
    static public final int IF4ERR_DELIVERY_ERROR       = 72;
    static public final int IF4ERR_ZIGBEE_NETWORK_ERROR = 73;
    static public final int IF4ERR_NO_ENTRY             = 100;
    static public final int IF4ERR_CANNOT_DELETE        = 101;
    static public final int IF4ERR_OUT_OF_RANGE         = 102;
    static public final int IF4ERR_EXCEPTION            = 103;
    static public final int IF4ERR_NOT_READY            = 104;
    static public final int IF4ERR_CANNOT_CONNECT       = 105;
    static public final int IF4ERR_METER_READING_ERROR  = 106;
    static public final int IF4ERR_TIMEOUT              = 107;
    static public final int IF4ERR_CRC_ERROR            = 108;
    static public final int IF4ERR_DUPLICATE            = 109;
    static public final int IF4ERR_CANNOT_CHANGE        = 110;
    static public final int IF4ERR_SAME_VERSION         = 111;    
    
    static public final int IF4ERR_UNKNOWN_NAMESPACE	= 112;	//Namespace name not found 	v1.2
    static public final int IF4ERR_INVALID_WINDOW_SIZE	= 113;	//Invalid window size 	v1.2
    static public final int IF4ERR_INVALID_WINDOW_COUNT	= 114;	//Invalid window count 	v1.2
    
    static public final int IF4ERR_GROUP_DB_OPEN_FAIL   = 150;
    static public final int IF4ERR_GROUP_INVALID_MEMBER = 151;
    static public final int IF4ERR_GROUP_NAME_DUPLICATE = 152;
    static public final int IF4ERR_GROUP_STORE_ERROR    = 153;
    static public final int IF4ERR_GROUP_NAME_NOT_EXIST = 154;
    static public final int IF4ERR_GROUP_DELETE_ERROR   = 155;
    static public final int IF4ERR_GROUP_ERROR          = 156;
    static public final int IF4ERR_GROUP_UPDATE_ERROR   = 157;
    static public final int IF4ERR_GROUP_KEY_ERROR      = 158; 
    static public final int IF4ERR_GROUP_MEMBEREXIST_ERROR = 159; 
    static public final int IF4ERR_GROUP_RECOVERY_ERROR	= 160;
    static public final int IF4ERR_GROUP_MEMBER_RECOVER_ERROR = 161;

    static public final int IF4ERR_ALREADY_INITIALIZED  = 200;
    static public final int IF4ERR_BUSY                 = 201;
    static public final int IF4ERR_SYSTEM_ERROR         = 202;
    static public final int IF4ERR_IO_ERROR             = 203;
    static public final int IF4ERR_MEMORY_ERROR         = 204;
    static public final int IF4ERR_DO_NOT_SUPPORT       = 205;
    static public final int IF4ERR_CANNOT_GET           = 206;
    static public final int IF4ERR_CANNOT_SET           = 207;
    static public final int IF4ERR_CANNOT_GETLIST       = 208;
    static public final int IF4ERR_CANNOT_SUPPORT_MULTI = 209;
    static public final int IF4ERR_NEED_MORE_VALUE      = 210;
    static public final int IF4ERR_NO_MORE_WORKER       = 211;
    static public final int IF4ERR_DUPLICATE_TRIGGERID  = 212;
    static public final int IF4ERR_TRIGGERID_NOT_FOUND  = 213;
    static public final int IF4ERR_USER_MODIFY          = 220;
    static public final int IF4ERR_TRANSACTION_CREATE_FAIL = 221;
    static public final int IF4ERR_TRANSACTION_UPDATE_FAIL = 222;
    static public final int IF4ERR_TRANSACTION_DELETE_FAIL = 223;
    static public final int IF4ERR_DEPRECATED_FUNTION   = 240;
    static public final int IF4ERR_UNKNOWN_ERROR        = 255;
    static public final int IF4ERR_RETURN_DATA_EMPTY    = 999;
    
    static public final int MCU_CANNOT_CONNECT       = 260;



    /**
     * constructor
     */
    public ErrorCode()
    {
    }

    public static BYTE getByte(int code)
    {
        BYTE b = new BYTE(code);
        return b;
    }

    public static String getCodeString(int code) throws Exception
    {
        Class<ErrorCode> clazz = ErrorCode.class;
        Field[] fs = clazz.getFields();
        //log.debug("Field length ["+fs.length+"]");
        for (int i = 0; i < fs.length; i++)
        {
            String name = fs[i].getName();
            int value = fs[i].getInt(name);
            //log.debug("CodeName="+name+", CodeValue="+value);
            if (code == value)
            {
                return name;
            }
        }
        return "UKNOWN ERROR CODE";
    }

    public static String getMessage(int code) throws Exception
    {
        switch(code)
        {
            case IF4ERR_NOERROR              : return "No Error";
            case IF4ERR_INVALID_ID           : return "Invalid ID";
            case IF4ERR_INVALID_OID          : return "Invalid OID";
            case IF4ERR_INVALID_INDEX        : return "Invalid Index";
            case IF4ERR_INVALID_VALUE        : return "Invalid Value";
            case IF4ERR_INVALID_ACCOUNT      : return "Invalid Account";
            case IF4ERR_INVALID_PASSWORD     : return "Invalid Password";
            case IF4ERR_INVALID_LENGTH       : return "Invalid Length";
            case IF4ERR_INVALID_TYPE         : return "Invalid Type";
            case IF4ERR_INVALID_COUNT        : return "Invalid Count";
            case IF4ERR_INVALID_PARAM        : return "Invalid Parameter";
            case IF4ERR_INVALID_TARGET       : return "Invalid Target";
            case IF4ERR_INVALID_HOST         : return "Invalid HOST";
            case IF4ERR_INVALID_HANDLE       : return "Invalid Handel";
            case IF4ERR_INVALID_FORMAT       : return "Invalid Format";
            case IF4ERR_INVALID_ADDRESS      : return "Invalid Address";
            case IF4ERR_INVALID_PORT         : return "Invalid Port";
            case IF4ERR_INVALID_BUFFER       : return "Invalid Buffer";
            case IF4ERR_INVALID_PROTOCOL     : return "Invalid Protocol";
            case IF4ERR_INVALID_GROUP        : return "Invalid AimirGroup";
            case IF4ERR_INVALID_NAME         : return "Invalid Name";
            case IF4ERR_INVALID_FILENAME     : return "Invalid File Name";
            case IF4ERR_INVALID_COMMAND      : return "Invalid Command";
            case IF4ERR_INVALID_CHANNEL      : return "Invalid Channel";
            case IF4ERR_INVALID_PANID        : return "Invalid PAN ID";
            case IF4ERR_INVALID_SINK         : return "Invalid Sink";
            case IF4ERR_INVALID_INVOKE       : return "Invalid Invoke";
            case IF4ERR_INVALID_EVENT        : return "Invalid Event";
            case IF4ERR_INVALID_MEMBER       : return "Invalid Member";
            case IF4ERR_INVALID_APN          : return "Invalid APN";
            case IF4ERR_INVALID_URL          : return "Invalid APN";
            case IF4ERR_INVALID_VERSION      : return "Invalid Version";
            case IF4ERR_INVALID_TRIGGERID    : return "Invalid Trigger ID";
            case IF4ERR_INVALID_OTA_TYPE     : return "Invalid OTA Type";
            case IF4ERR_INVALID_MODEL        : return "Invalid Model";
            case IF4ERR_INVALID_TRANSFER_TYPE: return "Invalid Transfer Type";
            case IF4ERR_INVALID_OTA_STEP     : return "Invalid OTA Step";
            case IF4ERR_INVALID_BIN_URL      : return "Invalid Bin URL";
            case IF4ERR_INVALID_BIN_CHECKSUM : return "Invalid Bin Checksum";
            case IF4ERR_INVALID_DIFF_URL     : return "Invalid Diff URL";
            case IF4ERR_INVALID_DIFF_CHECKSUM: return "Invalid Diff Checksum";
            case IF4ERR_INVALID_TRANSACTION_ID : return "Invalid Transaction ID";
            case IF4ERR_CANNOT_ACCEPT        : return "Mcu cannot Accept";
            case IF4ERR_BUSY_METERING        : return "Busy Metering";
            case IF4ERR_BUSY_RECOVERY        : return "Busy Recovery";
            case IF4ERR_BUSY_TIMESYNC        : return "Busy Timesync";
            case IF4ERR_OUT_OF_BINDING       : return "Out Of Binding";
            case IF4ERR_INTERNAL_ERROR       : return "Internal Error";
            case IF4ERR_DELIVERY_ERROR       : return "Delivery Error";
            case IF4ERR_ZIGBEE_NETWORK_ERROR : return "Zigbee Network Error";
            case IF4ERR_NO_ENTRY             : return "No Entry";
            case IF4ERR_CANNOT_DELETE        : return "Connot Delete";
            case IF4ERR_OUT_OF_RANGE         : return "Out Of Range";
            case IF4ERR_EXCEPTION            : return "Exception Occured";
            case IF4ERR_NOT_READY            : return "Not Ready";
            case IF4ERR_CANNOT_CONNECT       : return "Connot Connect";
            case IF4ERR_METER_READING_ERROR  : return "Meter Reading Error";
            case IF4ERR_TIMEOUT              : return "Time Out";
            case IF4ERR_CRC_ERROR            : return "CRC Error";
            case IF4ERR_DUPLICATE            : return "CRC Error";
            case IF4ERR_CANNOT_CHANGE        : return "Can Not Change";
            case IF4ERR_SAME_VERSION         : return "Same Version";            
            case IF4ERR_GROUP_DB_OPEN_FAIL   : return "Group DB Open Failed";
            case IF4ERR_GROUP_INVALID_MEMBER : return "Group Invalid Member";
            case IF4ERR_GROUP_NAME_DUPLICATE : return "Group Name Duplicate";
            case IF4ERR_GROUP_STORE_ERROR    : return "Group Store Error";
            case IF4ERR_GROUP_NAME_NOT_EXIST : return "Group Name Not Exist";
            case IF4ERR_GROUP_DELETE_ERROR   : return "Group Delete Error";
            case IF4ERR_GROUP_ERROR          : return "Group Error";
            case IF4ERR_GROUP_UPDATE_ERROR   : return "Group Update Error";
            case IF4ERR_GROUP_KEY_ERROR      : return "Group Key Error";   
            case IF4ERR_GROUP_MEMBEREXIST_ERROR : return "Group Member Exist Error";  
            case IF4ERR_GROUP_RECOVERY_ERROR : return "Group Recovery Error"; 
            case IF4ERR_GROUP_MEMBER_RECOVER_ERROR : return "Group Member Recovery Error"; 
            case IF4ERR_ALREADY_INITIALIZED  : return "Already Initialized";
            case IF4ERR_BUSY                 : return "BUSY";
            case IF4ERR_SYSTEM_ERROR         : return "System Error";
            case IF4ERR_IO_ERROR             : return "IO error";
            case IF4ERR_MEMORY_ERROR         : return "Memory Error";
            case IF4ERR_DO_NOT_SUPPORT       : return "Do Not Support";
            case IF4ERR_CANNOT_GET           : return "Read operation failed";
            case IF4ERR_CANNOT_SET           : return "Set operation failed";
            case IF4ERR_CANNOT_GETLIST       : return "Cannot GETLIST Operation";
            case IF4ERR_CANNOT_SUPPORT_MULTI : return "Cannot Support Multi";
            case IF4ERR_NEED_MORE_VALUE      : return "Need More Value";
            case IF4ERR_NO_MORE_WORKER       : return "Do Not WORKER";
            case IF4ERR_DUPLICATE_TRIGGERID  : return "Duplicate Trigger ID";
            case IF4ERR_TRIGGERID_NOT_FOUND  : return "Trigger ID Not Found";
            case IF4ERR_USER_MODIFY          : return "User Modify";
            case IF4ERR_TRANSACTION_CREATE_FAIL : return "Transaction Create Fail";
            case IF4ERR_TRANSACTION_UPDATE_FAIL : return "Transaction Update Fail";
            case IF4ERR_TRANSACTION_DELETE_FAIL : return "Transaction Delete Fail";
            case IF4ERR_DEPRECATED_FUNTION   : return "Mcu don't support a function any more";
            case IF4ERR_UNKNOWN_ERROR        : return "Unknown Error";
            case IF4ERR_RETURN_DATA_EMPTY    : return "Read Time out";
            
            case MCU_CANNOT_CONNECT : return "Can't connect to DCU";
            
            
            default : return "Unknown Error";
        }
    }
}
