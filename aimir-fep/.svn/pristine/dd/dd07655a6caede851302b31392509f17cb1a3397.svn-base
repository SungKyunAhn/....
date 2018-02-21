package com.aimir.fep.protocol.security.frame;

public enum FrameType {
		CinfoRequest((byte)0x00),
		CinfoResponse((byte)0x01),
		CertificateRequest((byte)0x02),
		CertificateSUCCESSReseponse((byte)0x03),
		CertificateRenewalResponse((byte)0x04),
		AckNack((byte)0x05);
		private byte code;

		FrameType(byte code) {
			this.code = code;
		}

		public byte getCode() {
			return this.code;
		}
		
	    public static FrameType getByName(String name){
	        for(FrameType m : FrameType.values()){
	            if(m.name().equals(name))
	                return m;
	        }
	        return null;
	    }

	    public static FrameType getByCode(byte code) {

	        for(FrameType m : FrameType.values()){
	            if(m.getCode() == code)
	                return m;
	        }
	        return null;
	    }
}
