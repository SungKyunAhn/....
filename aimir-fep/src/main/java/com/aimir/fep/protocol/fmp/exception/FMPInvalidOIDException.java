package com.aimir.fep.protocol.fmp.exception;

/**
 * Invalid OID Exception
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class FMPInvalidOIDException extends FMPException
{
    /**
     * constructor
     */
    public FMPInvalidOIDException()
    {
        super();
    }

    /**
     * constructor
     *
     * @param msg <code>String</code> message
     */
    public FMPInvalidOIDException(String msg)
    {
        super(msg);
    }
}
