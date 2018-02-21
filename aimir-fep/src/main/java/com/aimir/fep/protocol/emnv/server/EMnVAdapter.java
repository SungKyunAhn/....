/**
 * (@)# EMnVAdapter.java
 *
 * 2015. 4. 16.
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
package com.aimir.fep.protocol.emnv.server;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.transport.socket.nio.NioProcessor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.protocol.fmp.server.FMPSslContextFactory;
import com.aimir.fep.util.DataUtil;

/**
 * @author simhanger
 */
@Service
public class EMnVAdapter implements EMnVAdapterMBean, MBeanRegistration {
	private static Logger logger = LoggerFactory.getLogger(EMnVAdapter.class);

	public final static String ADAPTER_DOMAIN = "Adapter";
	private ObjectName objectName = null;
	private String name;

	@Autowired
	private EMnVProtocolHandler protocolHandler;

	String[] states = { "Stopped", "Stopping", "Starting", "Started", "Failed", "Destroyed" };
	private final int STOPPED = 0;
	private final int STOPPING = 1;
	private final int STARTING = 2;
	private final int STARTED = 3;
	private final int FAILED = 4;
	private final int DESTROYED = 5;
	private int state = STOPPED;

	private int PORT = 8198;
	private IoAcceptor acceptor = null;

	//private static ProtocolCodecFactory CODEC_FACTORY;

	public EMnVAdapter() {
		String defaultMaxPoolSize = String.valueOf(Runtime.getRuntime().availableProcessors() * 2);

		protocolHandler = DataUtil.getBean(EMnVProtocolHandler.class);

		ExecutorService executor = Executors.newCachedThreadPool();
		acceptor = new NioSocketAcceptor(executor, new NioProcessor(executor));

		logger.debug("load EMnV MIB Completed. ### PORT = " + getPort() + " ###");
	}

	@Override
	public ObjectName preRegister(MBeanServer server, ObjectName obName) throws Exception {
		objectName = obName;
		if (objectName == null) {
			objectName = new ObjectName(server.getDefaultDomain() + ":service=" + this.getClass().getName());
		}

		this.name = objectName.toString();
		return objectName;
	}

	@Override
	public void postRegister(Boolean registrationDone) {
		// TODO Auto-generated method stub

	}

	@Override
	public void preDeregister() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postDeregister() {
		// TODO Auto-generated method stub

	}

	@Override
	public void start() throws Exception {
		logger.debug("{} start", objectName);
		try {
			state = STARTING;

			FMPSslContextFactory.setSslFilter(acceptor);
			acceptor.setDefaultLocalAddress(new InetSocketAddress(PORT));
			acceptor.setHandler(protocolHandler);
			acceptor.getFilterChain().addLast(name, new ProtocolCodecFilter(new ProtocolCodecFactory() {
				public ProtocolDecoder getDecoder(IoSession session) throws Exception {
					return new EMnVDecoder();
				}

				public ProtocolEncoder getEncoder(IoSession session) throws Exception {
					return new EMnVEncoder();
				}
			}));
			acceptor.bind();

			logger.info("EMnVAdapter Listening on port {}", PORT);

			state = STARTED;
		} catch (Exception ex) {
			logger.error("objectName[{}] start failed", objectName);
			state = STOPPED;
			throw ex;
		}
	}

	@Override
	public void stop() {
		logger.debug("{} stop", objectName);
		state = STOPPING;

		acceptor.unbind();

		state = STOPPED;
	}

	@Override
	public String getName() {
		return objectName.toString();
		//return this.objectName.getCanonicalName();
	}

	@Override
	public String getState() {
		return states[state];
	}

	@Override
	public int getPort() {
		return PORT;
	}

	public void setPort(int port) {
		this.PORT = port;
	}

	@Override
	public CommandData cmdExecute(String targetId, CommandData cmdData) throws Exception {
		//        long stime = System.currentTimeMillis();
		//        long ctime = System.currentTimeMillis();
		//        long responseTimeout = 90000;
		//        
		//		CommandData response = null;
		//		IoSession cmdSession = cache.getActiveSession(targetId);
		//        if(cmdSession != null && cmdSession.isConnected()){
		//        	log.info("GetClientSession from =["+targetId+","+cmdSession.getRemoteAddress()+"]");        	
		//        	CommandAction_GD action = new CommandAction_GD();
		//        	action.executeReverseGPRSCommand(cmdSession, command);
		//        	Thread.sleep(500);
		//        	while(((ctime - stime)) < responseTimeout){
		//            	response = (CommandData)cmdSession.getAttribute("response");
		//            	if(response != null){
		//            		cmdSession.removeAttribute("response");
		//            		return response;
		//            	}
		//            	Thread.sleep(500);
		//            	ctime = System.currentTimeMillis();
		//        	}
		//
		//        }else{
		//        	throw new Exception("No session for target=["+targetId+"]"+" from ReverseGPRSAdapter");
		//        }
		//        return response;    
		return null;
	}

}
