package com.aimir.fep.protocol.mrp.command.frame;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;

import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;


/**
 * RequestDataFrame
 * 
 * @author Yeon Kyoung Park
 * @version $Rev: 1 $, $Date: 2008-01-05 15:59:15 +0900 $,
 */
public abstract class RequestDataFrame
{
    @SuppressWarnings("unused")
    private static Log log = LogFactory.getLog(RequestDataFrame.class);
    public BYTE service = new BYTE();
    public OCTET destination = new OCTET(6);
    public BYTE source = new BYTE();
    public OCTET send_data_buffer = null;
    public BYTE control = new BYTE();
    public int send_count;
    public int retry_flag;
    public TIMESTAMP time_of_transmission = new TIMESTAMP();

    /**
     * constructor
     */
    public RequestDataFrame()
    {
    }
    
    public RequestDataFrame(BYTE service, OCTET destination, BYTE source, BYTE control,
                            OCTET send_data_buffer, int send_count,
                            int retry_flag, TIMESTAMP time_of_transmission){
        this.service = service;
        this.destination = destination;
        this.source = source;
        this.control = control;
        this.send_data_buffer = send_data_buffer;
        this.send_count = send_count;
        this.retry_flag = retry_flag;
        this.time_of_transmission = time_of_transmission;
    }

    /**
     * get code
     *
     * @result code <code>byte</code> code
     */
    public BYTE getService()
    {
        return this.service;
    }

    /**
     * set code
     *
     * @param code <code>byte</code> code
     */
    public void setService(BYTE service)
    {
        this.service = service;
    }

    /**
     * get arg
     *
     * @result arg <code>OCTET</code> arg
     */
    public OCTET getDestination()
    {
        return this.destination;
    }

    /**
     * set arg
     *
     * @param arg <code>OCTET</code> arg
     */
    public void setDestination(OCTET destination)
    {
        this.destination = destination;
    }
    
    public void setControl(BYTE control)
    {
        this.control = control;
    }
    
    public abstract byte[] encode() throws Exception;

    public IoBuffer getIoBuffer() throws Exception
    {

        byte[] b = encode();
        IoBuffer buf = IoBuffer.allocate(b.length);
        buf.put(b);
        buf.flip();
        return buf;
    }
    
}
