package com.aimir.fep.protocol.nip.frame.payload;

public class PulseMeter{
    
	public enum LPPeriod{
		Sixty ((int)1),
		thirtyint ((int)2),
		fiveTeen ((int)4),
		five ((int)12);
		private int code;
		
		LPPeriod(int code) {
            this.code = code;
        }
        
        public int getCode() {
            return this.code;
        }
	}
	
	private LPPeriod _lpperiod;
	private int voltage;
	private int current;
	private int currentPulse;
    
	private int lpPeriod;
	private int meteringDataCount;

    public void setLPPeriod(int code) {
    	for (LPPeriod f : LPPeriod.values()) {
            if (f.getCode() == code) {
            	_lpperiod = f;
                break;
            }
        }
    }
    
    public LPPeriod get_lpperiod() {
		return _lpperiod;
	}
	public void set_lpperiod(LPPeriod _lpperiod) {
		this._lpperiod = _lpperiod;
	}
	public int getVoltage() {
		return voltage;
	}
	public void setVoltage(int voltage) {
		this.voltage = voltage;
	}
	public int getCurrent() {
		return current;
	}
	public void setCurrent(int current) {
		this.current = current;
	}
	public int getCurrentPulse() {
		return currentPulse;
	}
	public void setCurrentPulse(int currentPulse) {
		this.currentPulse = currentPulse;
	}
	public int getLpPeriod() {
		return lpPeriod;
	}
	public void setLpPeriod(int lpPeriod) {
		this.lpPeriod = lpPeriod;
	}
	public int getMeteringDataCount() {
		return meteringDataCount;
	}
	public void setMeteringDataCount(int meteringDataCount) {
		this.meteringDataCount = meteringDataCount;
	}
	
	public MeteringData[] _meteringData;
	
	public MeteringData[] newMeteringData(int cnt) {
		_meteringData = new MeteringData[cnt];
		return _meteringData;
	}
	
	public MeteringData[] get_meteringData() {
		return _meteringData;
	}

	public void set_meteringData(MeteringData[] _meteringData) {
		this._meteringData = _meteringData;
	}
	
	public class MeteringData{
		public int offset;
	    public String baseTime;
	    public int basePulse;
	    public int lpData;
	    
	    public int getOffset() {
			return offset;
		}
		public void setOffset(int offset) {
			this.offset = offset;
		}
		public String getBaseTime() {
			return baseTime;
		}
		public void setBaseTime(String baseTime) {
			this.baseTime = baseTime;
		}
		public int getBasePulse() {
			return basePulse;
		}
		public void setBasePulse(int basePulse) {
			this.basePulse = basePulse;
		}
		public int getLpData() {
			return lpData;
		}
		public void setLpData(int lpData) {
			this.lpData = lpData;
		}
	}
}