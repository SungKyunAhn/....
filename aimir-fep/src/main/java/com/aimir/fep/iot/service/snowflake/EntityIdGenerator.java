package com.aimir.fep.iot.service.snowflake;

public interface EntityIdGenerator {
	String generateLongId() throws InvalidSystemClockException, GetHardwareIdFailedException;
}
