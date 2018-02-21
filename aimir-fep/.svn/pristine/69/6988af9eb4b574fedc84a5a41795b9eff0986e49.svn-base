package com.aimir.fep.meter.parser.SM110Table;

public class GE_ANSI_CONSTANT {

	public enum MeterMode{
		DemandOnly(0),
		DemandLp(1),
		TOU(2);
		
		private int code;
		
		MeterMode(int code){
			this.code = code;
		}
		
		public int getCode(){
			return this.code;
		}
	}

    public static MeterMode getMeterMode(int meterMode){
    	for(MeterMode m : MeterMode.values()){
    		if(m.getCode() == meterMode)
    			return m;
    	}
    	return null;
    }
}
