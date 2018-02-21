/**
 * 
 */
package com.aimir.fep.protocol.nip.client.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.protocol.nip.client.multisession.MultiSession;
import com.aimir.fep.protocol.nip.client.multisession.MultiSessionAdaptor;
import com.aimir.fep.protocol.nip.frame.GeneralFrame;

/**
 * @author simhanger
 *
 */
public abstract class NICommandAction extends MultiSessionAdaptor {
	private static Logger logger = LoggerFactory.getLogger(BypassCommandAction.class);

	private boolean useAck = false; // ACK를 사용할지 여부.

	public boolean isUseAck() {
		return useAck;
	}

	public void setUseAck(boolean useAck) {
		this.useAck = useAck;
	}

	public abstract String getActionTitle();

	/**
	 * This method used for first action with Initialization
	 * 
	 * @param session
	 * @param frame
	 * @return
	 * @throws Exception
	 */
	public abstract Object executeStart(MultiSession session, GeneralFrame generalFrame) throws Exception;

	/**
	 * This method used for command transaction.
	 * 
	 * @param session
	 * @param frame
	 * @return
	 * @throws Exception
	 */
	public abstract Object executeTransaction(MultiSession session, GeneralFrame generalFrame) throws Exception;

	/**
	 * This method used for last action with destroy resources
	 * 
	 * @param session
	 * @throws Exception
	 */
	public abstract void executeStop(MultiSession session) throws Exception;

	/**
	 * This method used for receive ACK Command
	 * 
	 * @param session
	 * @throws Exception
	 */
	public abstract void executeAck(MultiSession session, GeneralFrame generalFrame) throws Exception;
	
	/**
	 * This method used for receive RESPONSE Command
	 * 
	 * @param session
	 * @throws Exception
	 */
	public abstract void executeResponse(MultiSession session, GeneralFrame generalFrame) throws Exception;

}