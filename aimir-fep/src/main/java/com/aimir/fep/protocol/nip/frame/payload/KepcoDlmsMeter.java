package com.aimir.fep.protocol.nip.frame.payload;

public class KepcoDlmsMeter{
    
	public enum MeterType{
		KepcoStd((byte)0x00),
		EType_1_0((byte)0x01),
		EType_1_1((byte)0x02),
		GType((byte)0x03);
		
		private byte code;
		MeterType(byte code) {
            this.code = code;
        }
        public byte getCode() {
            return this.code;
        }
	}
	
	private MeterType _meterType;
	private KepcoDlmsMeteringData[] _meteringData;
	
	public MeterType get_meterType() {
		return _meterType;
	}

	public void set_meterType(MeterType _meterType) {
		this._meterType = _meterType;
	}
	
	public void setMeterType(byte code) {
    	for (MeterType f : MeterType.values()) {
            if (f.getCode()== code){
            	_meterType = f;
                break;
            }
        }
    }
	
	public KepcoDlmsMeteringData[] newMeteringData(int cnt) {
		_meteringData = new KepcoDlmsMeteringData[cnt];
		return _meteringData;
	}
	
	public KepcoDlmsMeteringData[] get_meteringData() {
		return _meteringData;
	}

	public void set_meteringData(KepcoDlmsMeteringData[] _meteringData) {
		this._meteringData = _meteringData;
	}
}