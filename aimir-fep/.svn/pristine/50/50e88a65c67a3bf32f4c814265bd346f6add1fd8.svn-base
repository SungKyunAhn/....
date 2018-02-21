package com.aimir.fep.protocol.mrp.client;

import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.GeneralDataFrame;
import com.aimir.fep.util.Hex;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * Decodes MRP GeneralDataFrame into input stream
 *
 * @author Yeon Kyoung Park
 * @version $Rev: 1 $, $Date: 2007-01-05 15:59:15 +0900 $,
 */
public class WCDMASMSClientDecoder extends ProtocolDecoderAdapter {
	private Log log = LogFactory.getLog(getClass());
	private IoBuffer dataBuff = null;
	private int dataBuffSize = 0;
	private boolean isRemained = false;
	private byte[] lengthField = new byte[GeneralDataConstants.LENGTH_LEN];

	// decode frame
	private String decodeFrame(IoSession session, IoBuffer in) throws Exception {
		GeneralDataFrame frame = null;
		// log.debug("isMultiframe is false ");
		Object ns_obj = session.getAttribute("nameSpace");
        String ns = ns_obj!=null? (String)ns_obj:null;
		frame = GeneralDataFrame.decode(ns,in);
		return in.getHexDump();
	}



	private void release(IoBuffer bytebuffer) {
		log.debug("FREE IoBuffer");
		try {
			bytebuffer.free();
		} catch (Exception ex) {
		}
	}

	private void release(IoBuffer[] bytebuffers) {
		log.debug("FREE IoBuffers");
		for (int i = 0; i < bytebuffers.length; i++) {
			try {
				bytebuffers[i].free();
			} catch (Exception ex) {
			}
		}
	}



	/**
	 * decode input stream
	 *
	 * @param session
	 *            <code>IoSession</code> session
	 * @param in
	 *            <code>IoBuffer</code> input stream
	 * @param out
	 *            <code>ProtocolDecoderOutput</code> save decoding frame
	 * @throws ProtocolViolationException
	 */
	public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws ProtocolDecoderException {
		try {
			// in = in.limit(512);
			String receiveMsg = new String(Hex.encode(in.getHexDump().replaceAll(" ", "")),"euc-kr");
			log.debug("Received [" + session.getRemoteAddress() + "] : " + in.limit() + " :" + in.getHexDump()+" : "+ receiveMsg);
		} catch (Exception ex) {
			log.error("MRPDecoder::decode failed : ", ex);
			throw new ProtocolDecoderException(ex.getMessage());
		}
	}
}
