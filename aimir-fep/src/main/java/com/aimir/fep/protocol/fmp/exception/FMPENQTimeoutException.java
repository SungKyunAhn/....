package com.aimir.fep.protocol.fmp.exception;


/**
 * ENQ timeout Exception
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class FMPENQTimeoutException extends FMPException
{
    /**
     * constructor
     */
    public FMPENQTimeoutException()
    {
        super();
    }

    /**
     * constructor
     *
     * @param msg <code>String</code> message
     */
    public FMPENQTimeoutException(String msg)
    {
        super(msg);
    }
}
