package com.aimir.fep.protocol.nip.command;

public class AuthorizationInfo {
	public String nodeEui;
    public AuthorizationStatus _authorizationStatus;
    
    public enum AuthorizationStatus {
        HESPermitNoSecuNo((byte)0x00),
        HESPermitOkSecuNo((byte)0x01),
        HESPermitOkSecuOk((byte)0x02),
        HESNoNode((byte)0xFF);
        private byte code;
        AuthorizationStatus(byte code) {
            this.code = code;
        }
        public byte getCode() {
            return this.code;
        }
    }
    public void setAuthorizationStatus(byte code) {
        for (AuthorizationStatus c : AuthorizationStatus.values()) {
            if (c.getCode() == code) {
            	_authorizationStatus = c;
                break;
            }
        }
    }
    
	public String getNodeEui() {
		return nodeEui;
	}

	public void setNodeEui(String nodeEui) {
		this.nodeEui = nodeEui;
	}

}