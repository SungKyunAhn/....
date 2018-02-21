package com.aimir.fep.protocol.nip.frame.payload;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class PayloadFrame {
    protected Log log = LogFactory.getLog(getClass());
    
    public abstract byte[] encode() throws Exception;
    public abstract void decode(byte[] bx);
    
    public abstract void setCommandFlow(byte code);
    public abstract void setCommandType(byte code);
    
    public abstract byte[] getFrameTid();
    public abstract void setFrameTid(byte[] code);

}
