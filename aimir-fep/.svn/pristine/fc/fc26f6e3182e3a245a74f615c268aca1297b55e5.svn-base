/** 
 * @(#)MeterConnector.java       2008-01-05 *
 * Copyright (c) 2008-2009 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */

package com.aimir.fep.protocol.mrp;

import java.net.Socket;
import java.util.HashMap;

import org.apache.mina.core.session.IoSession;

import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.protocol.mrp.client.MRPClientProtocolHandler;
import com.aimir.fep.protocol.mrp.exception.MRPException;
import com.aimir.fep.util.ByteArray;

/** 
 * Abstract MeterConnector Class. 
 * 
 * @version     1.0 1  
 * @author      Park YeonKyoung    
 */

public abstract class MeterProtocolHandler 
{
	protected MRPClientProtocolHandler handler = null;
	/**
	 * Constructor <p>
	 */
    protected MeterProtocolHandler() { 

    }      
	public abstract void setModemNumber(String modemNumber);
	public abstract void setModemId(String modemId);
	public abstract void setGroupNumber(String groupNumber);
	public abstract void setMemberNumber(String memberNumber);
	public abstract void setMcuSwVersion(String mcuSwVersion);
    /**
     * execute communication with meter
     * @param session
     * @param command
     * @return
     * @throws MRPException
     */
    public abstract CommandData execute(MRPClientProtocolHandler handler,
                                   IoSession session, 
                                   CommandData command) throws MRPException;
    
    public abstract CommandData execute(Socket socket, 
            CommandData command) throws MRPException;

    public abstract ByteArray configureRead(IoSession session) throws MRPException;
    
	/**
	 * Read Billing Data.<p>
	 * @return
	 */
	public abstract ByteArray billRead(IoSession session) throws MRPException;
	
	
	/**
	 * Read Cumulative Data.<p>
	 * @return
	 * @throws MeterException
	 */
	public abstract ByteArray currbillRead(IoSession session) throws MRPException;	
	
	
	/**
	 * LP Read.<p>
	 * @param startday - start day to collect lp.
	 * @param endday   - end   day to collect lp. 
	 * @return
	 * @throws MeterException
	 */
	public abstract ByteArray lpRead(IoSession session, 
                                      String startday, String endday, int lpCycle) throws MRPException;
	
	
	/**
	 * Read Event Log.<p>
	 * @return
	 * @throws MeterException
	 */
	public abstract ByteArray eventlogRead(IoSession session) throws MRPException;
	
	
	/**
	 * Read PQ Data.<p>
	 * @return
	 * @throws MeterException
	 */
	public abstract ByteArray pqRead(IoSession session) throws MRPException;
	
	/**
	 * Read Instrument Data.<p>
	 * @return
	 * @throws MeterException
	 */
	public abstract ByteArray instRead(IoSession session) throws MRPException;
	
	
	public abstract HashMap timeSync(IoSession session, int timethreshold) throws MRPException; 
	
	public abstract HashMap timeCheck(IoSession session) throws MRPException;
	
	/**
	 * Init Process.<p>
	 * @throws MeterException
	 */
	public abstract void initProcess(IoSession session) throws MRPException;
	
	
	/**
	 * Quit Meter connection.<p>
	 * @throws MeterException
	 */
	public abstract void quit() throws MRPException;


	/**
	 * Check CRC.<p>
	 * @param src
	 * @return
	 * @throws MeterException
	 */
	public abstract boolean checkCRC(byte[] src) throws MRPException;
    
    
    public abstract long getSendBytes() throws MRPException;

}
