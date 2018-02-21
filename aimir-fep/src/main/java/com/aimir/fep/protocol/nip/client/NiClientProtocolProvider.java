package com.aimir.fep.protocol.nip.client;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * {@link ProtocolProvider} implementation of FEP FMP(AiMiR and MCU Protocol).
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class NiClientProtocolProvider implements ProtocolCodecFactory{
	 @Override
	 public ProtocolEncoder getEncoder(IoSession session) throws Exception{
		 return new NiClientEncoder();
	 }
	 @Override   
     public ProtocolDecoder getDecoder(IoSession session) throws Exception{
    	 return new NiClientDecoder();
     }
}
