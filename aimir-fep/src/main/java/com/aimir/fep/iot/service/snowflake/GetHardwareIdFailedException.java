package com.aimir.fep.iot.service.snowflake;

public class GetHardwareIdFailedException extends Exception {
    GetHardwareIdFailedException(String reason){
        super(reason);
    }
    GetHardwareIdFailedException(Exception e){
        super(e);
    }
}
