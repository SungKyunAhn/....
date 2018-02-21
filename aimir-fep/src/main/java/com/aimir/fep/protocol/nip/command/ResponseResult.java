package com.aimir.fep.protocol.nip.command;

public class ResponseResult {
    public enum Status {
        Success (new byte[]{(byte)0x00, (byte)0x00}),
        FormatError (new byte[]{(byte)0x10, (byte)0x01}),
        ParameterError (new byte[]{(byte)0x10, (byte)0x02}),
        ValueOverflow (new byte[]{(byte)0x10, (byte)0x03}),
        InvalidAttrId (new byte[]{(byte)0x10, (byte)0x04}),
        AuthorizationError (new byte[]{(byte)0x10, (byte)0x05}),
        NoDataError (new byte[]{(byte)0x10, (byte)0x06}),
        MeteringBusy (new byte[]{(byte)0x20, (byte)0x00}),
        Unknown (new byte[]{(byte)0xFF, (byte)0x00});
        
        private byte[] code;
        
        Status(byte[] code) {
            this.code = code;
        }
        
        public byte[] getCode() {
            return this.code;
        }
    }
    
    public enum ObisStatus {
        Success ((byte)0x00),
        Data_Error ((byte)0x01),
        Data_Overlap ((byte)0x02);
        
        private byte code;
        
        ObisStatus(byte code) {
            this.code = code;
        }
        
        public byte getCode() {
            return this.code;
        }
    }
    
    public Status status;
    public ObisStatus obiusStatus;
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public ObisStatus getObiusStatus() {
		return obiusStatus;
	}
	public void setObiusStatus(ObisStatus obiusStatus) {
		this.obiusStatus = obiusStatus;
	}
    
}
