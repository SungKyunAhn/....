package com.aimir.fep.protocol.nip.exception;

public class NiException extends Exception{
	public NiException(){
		super();
	}
	public NiException(String message){
		super(message);
	}
	
	public NiException(Throwable t){
		super(t);
	}
	
	public NiException(String message, Throwable t){
        super(message, t);
    }
}
