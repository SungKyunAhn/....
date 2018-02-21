package com.aimir.fep.protocol.fmp.exception;

/**
 * ACK timeout Exception
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class FMPACKTimeoutException extends FMPException
{
    /**
     * constructor
     */
    public FMPACKTimeoutException()
    {
        super();
    }

    /**
     * constructor
     *
     * @param msg <code>String</code> message
     */
    public FMPACKTimeoutException(String msg)
    {
        super(msg);
    }
}
