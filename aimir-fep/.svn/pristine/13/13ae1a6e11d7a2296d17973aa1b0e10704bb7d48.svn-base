/**
 * (@)# NIBypassClientEncoder.java
 *
 * 2016. 6. 1.
 *
 * Copyright (c) 2013 NURITELECOM, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * NURITELECOM, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with NURITELECOM, Inc.
 *
 * For more information on this product, please see
 * http://www.nuritelecom.co.kr
 *
 */
package com.aimir.fep.protocol.nip.client.bypass;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.aimir.fep.util.Hex;

/**
 * @author simhanger
 *
 */
public class BypassClientEncoder extends ProtocolEncoderAdapter {

	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		byte[] bx = (byte[]) message;
		IoBuffer buffer = IoBuffer.allocate(bx.length);
		buffer.put(bx);
		buffer.flip();
		out.write(buffer);
		
		System.out.println("====================>>>>> 쐈다 : " + Hex.decode(bx));
	}

}
