/**
 * (@)# NICommandActinHandlerAdaptor.java
 *
 * 2016. 10. 6.
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
package com.aimir.fep.protocol.nip.client.actions;

import java.net.InetSocketAddress;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author simhanger
 *
 */
public class NICommandActionHandlerAdaptor {
	private static Logger logger = LoggerFactory.getLogger(NICommandActionHandlerAdaptor.class);

	private String handlerName = "DEFAULT_HANDLER";
	private final String NI_COMMAND_ACTION_KEY = "NI_COMMAND_ACTION_KEY";
	private NICommandAction commandAction;

	public String getHandlerName() {
		return handlerName;
	}

	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}

	public NICommandAction setCommandAction(IoSession session, String nameSpce, String commandActionName) throws Exception {
		if (session == null || nameSpce == null || nameSpce.equals("") || commandActionName == null || commandActionName.equals("")) {
			throw new Exception("Unknown CommandAction");
		}

		/*
		 *	TCP/TLS의 경우 IoSession에 넣는다.
		 */
		NICommandAction commandAction = (NICommandAction) Class.forName("com.aimir.fep.protocol.nip.client.actions.NI_" + commandActionName + "_Action_" + nameSpce).newInstance();
		((IoSession) session).setAttribute(NI_COMMAND_ACTION_KEY, commandAction);

		return (NICommandAction) session.getAttribute(NI_COMMAND_ACTION_KEY);
	}

	public NICommandAction setCommandAction(InetSocketAddress session, String nameSpce, String commandActionName) throws Exception {
		if (session == null || nameSpce == null || nameSpce.equals("") || commandActionName == null || commandActionName.equals("")) {
			throw new Exception("Unknown CommandAction");
		}

		/*
		 *	DTLS
		 */
		commandAction = (NICommandAction) Class.forName("com.aimir.fep.protocol.nip.client.actions.NI_" + commandActionName + "_Action_" + nameSpce).newInstance();
		return this.commandAction;
	}

	/**
	 * for TLS
	 * 
	 * @param session
	 *            - IoSession
	 * @return
	 */
	public NICommandAction getNICommandAction(IoSession session) {
		//Session Print
		if (session.getAttribute("NI_COMMAND_ACTION_KEY") != null) {
			((NICommandAction) session.getAttribute("NI_COMMAND_ACTION_KEY")).printMultiSessionRemoteAddress();
		}

		return (NICommandAction) session.getAttribute("NI_COMMAND_ACTION_KEY");
	}

	/**
	 * for DTLS
	 * 
	 * @param peer
	 *            - InetSocketAddress
	 * @return
	 */
	public NICommandAction getNICommandAction(InetSocketAddress peer) {
		//Session Print
		if (commandAction != null) {
			commandAction.printMultiSessionRemoteAddress();
		}

		return commandAction;
	}

	/**
	 * 
	 * @param session
	 *            - IoSession
	 */
	public void closeMultiSession(IoSession session) {
		logger.debug("A [" + handlerName + "]call closeMultiSession1 - {}", session.toString());
		this.getNICommandAction(session).deleteMultiSession(session);
		logger.debug("A [" + handlerName + "]call closeMultiSession2 - {}", session.toString());
	}

	/**
	 * 
	 * @param session
	 *            - InetSocketAddress
	 */
	public void closeMultiSession(InetSocketAddress session) {
		logger.debug("B [" + handlerName + "]call closeMultiSession1 - {}", session.toString());
		this.getNICommandAction(session).deleteMultiSession(session);
		logger.debug("B [" + handlerName + "]call closeMultiSession2 - {}", session.toString());
	}

}
