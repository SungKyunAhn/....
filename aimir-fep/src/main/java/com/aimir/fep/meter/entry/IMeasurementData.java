package com.aimir.fep.meter.entry;

import com.aimir.fep.meter.parser.MeterDataParser;

public interface IMeasurementData
{
    public MeterDataParser getMeterDataParser();
    
    public String getTimeStamp();
}
