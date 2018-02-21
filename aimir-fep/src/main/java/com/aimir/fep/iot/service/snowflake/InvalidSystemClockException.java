package com.aimir.fep.iot.service.snowflake;

public class InvalidSystemClockException extends Exception {
    public InvalidSystemClockException(String message){
        super(message);
    }
}