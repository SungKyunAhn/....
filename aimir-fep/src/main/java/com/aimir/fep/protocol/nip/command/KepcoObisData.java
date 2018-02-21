package com.aimir.fep.protocol.nip.command;

public class KepcoObisData{
	public int obisCnt;	
	public ObisCodeId obisCodeId;
	public String schedule;
	public MeterType meterType;
	public int meterCount;
	public String[] meterPortAddress;
	public ObisCodes obisCode;
	public int selectiveAccessLength;
	public String selectiveAccessData;
	public KepcoMeteringSchedule meteringSchedule; 

	public ObisCodes getObisCode() {
		return obisCode;
	}

	public void setObisCode(ObisCodes obisCode) {
		this.obisCode = obisCode;
	}

	public KepcoMeteringSchedule getMeteringSchedule() {
		return meteringSchedule;
	}

	public void setMeteringSchedule(KepcoMeteringSchedule meteringSchedule) {
		this.meteringSchedule = meteringSchedule;
	}

	public enum ObisCodeId {
        LP((new byte[]{(byte)0x00,(byte)0x01})),
        CurrRead((new byte[]{(byte)0x00,(byte)0x02})),
        RegularRead((new byte[]{(byte)0x00,(byte)0x04})),
        JeongBokJean((new byte[]{(byte)0x00,(byte)0x08})),
        PeakLoad((new byte[]{(byte)0x00,(byte)0x10})),
        CurrMaxDemand((new byte[]{(byte)0x00,(byte)0x20})),
        GtypeVoltAmpere((new byte[]{(byte)0x00,(byte)0x40})),
        MeterInfo((new byte[]{(byte)0x00,(byte)0x80}));
        private byte[] code;
        
        ObisCodeId(byte[] code) {
            this.code = code;
        }
        
        public byte[] getCode() {
            return this.code;
        }
    }
	
	public void setObisCodeId(ObisCodeId obisCodeId) {
		this.obisCodeId = obisCodeId;
	}
	
	public ObisCodeId getObisCodeId() {
        return obisCodeId;
    }
	
	
	public int getObisCnt() {
		return obisCnt;
	}

	public void setObisCnt(int obisCnt) {
		this.obisCnt = obisCnt;
	}

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public int getMeterCount() {
		return meterCount;
	}

	public void setMeterCount(int meterCount) {
		this.meterCount = meterCount;
	}

	public String[] getMeterPortAddress() {
		return meterPortAddress;
	}

	public void setMeterPortAddress(String[] meterPortAddress) {
		this.meterPortAddress = meterPortAddress;
	}

	public enum MeterType {
        KepcoStd((byte)0x01),
        EType_1_0((byte)0x02),
        EType_1_1((byte)0x04),
        GType((byte)0x08);
        
        private byte code;
        
        MeterType(byte code) {
            this.code = code;
        }
        
        public byte getCode() {
            return this.code;
        }
    }
	
	public void setMeterType(MeterType meterType) {
		this.meterType = meterType;
	}
	
	public MeterType getMeterType() {
        return meterType;
    }
	
	public int getSelectiveAccessLength() {
		return selectiveAccessLength;
	}
	public void setSelectiveAccessLength(int selectiveAccessLength) {
		this.selectiveAccessLength = selectiveAccessLength;
	}
	public String getSelectiveAccessData() {
		return selectiveAccessData;
	}
	public void setSelectiveAccessData(String selectiveAccessData) {
		this.selectiveAccessData = selectiveAccessData;
	}
	
	public class ObisCodes{
		String serviceTypes;
		String classId;
		String obis;
		int attribute;
		
		public String getServiceTypes() {
			return serviceTypes;
		}
		public void setServiceTypes(String serviceTypes) {
			this.serviceTypes = serviceTypes;
		}
		public String getClassId() {
			return classId;
		}
		public void setClassId(String classId) {
			this.classId = classId;
		}
		public String getObis() {
			return obis;
		}
		public void setObis(String obis) {
			this.obis = obis;
		}
		public int getAttribute() {
			return attribute;
		}
		public void setAttribute(int attribute) {
			this.attribute = attribute;
		}
	}

}