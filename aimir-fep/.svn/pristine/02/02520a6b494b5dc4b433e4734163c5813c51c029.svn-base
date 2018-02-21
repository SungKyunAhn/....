package com.aimir.fep.meter.data;

/**
 * response 정보와 메시지를 담는 VO class
 * @author kskim
 *
 */
public class Response implements java.io.Serializable{

	private static final long serialVersionUID = -505197940215049685L;

	/**
     * ANSI response CODE 정의
     * @author kskim
     *
     */
    public enum Type{
    	OK,
    	SNS,
    	ISSS,
    	IAR,
    	ISC,
    	ONP,
    	BSY,
    	ERR;
    }
	
    Type type;
	String message;
	
	public Response() {}
	
	public Response(Type type, String message){
		this.type = type;
		this.message = message;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String msg) {
		this.message = msg;
	}
	
}