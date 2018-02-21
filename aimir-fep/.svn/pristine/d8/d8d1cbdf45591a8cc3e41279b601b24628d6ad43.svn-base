package com.aimir.fep.protocol.security.frame;

import java.util.Hashtable;

import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.model.system.Code;

public enum StatusCode {
	Success((byte)0x00), // Success or Authentication complete
	ServerCertificateUnverified((byte)0x01),
	ClientCertificateUnverified((byte)0x02),
	ClientCertificateExpired((byte)0x03),
	SessionKeyNotDecrypted((byte)0x04),
	LocalError((byte)0x05),
	RenewalDecriptError((byte)0x06);	// UPDATE 2016.11.21 SP-318

	private byte code;

	StatusCode(byte code) {
		this.code = code;
	}

	public byte getCode() {
		return this.code;
	}
	
    public static StatusCode getByName(String name){
        for(StatusCode m : StatusCode.values()){
            if(m.name().equals(name))
                return m;
        }
        return null;
    }

    public static StatusCode getByCode(byte code) {

        for(StatusCode m : StatusCode.values()){
            if(m.getCode() == code)
                return m;
        }
        return null;
    }
}
