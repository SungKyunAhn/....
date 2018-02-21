package com.aimir.fep.protocol.mrp.protocol;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;

import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.mrp.command.frame.RequestDataFrame;
import com.aimir.fep.util.Hex;

/**
 * RequestDataFrame
 * 
 * @author Kang, Soyi
 */
public class A2RL_RequestDataFrame extends RequestDataFrame
{
    @SuppressWarnings("unused")
    private static Log log = LogFactory.getLog(A2RL_RequestDataFrame.class);

    
    public BYTE control = null;
    public BYTE function = null;
    public BYTE pad = null;
    public OCTET send_data_length = null;
    public OCTET send_data_buffer = null;
    
    public int send_count;
    public int retry_flag;
    public TIMESTAMP time_of_transmission = new TIMESTAMP();
    private int length;

    /**
     * constructor
     */
    public A2RL_RequestDataFrame()
    {
    }
   
    public A2RL_RequestDataFrame(BYTE service, BYTE control, 
                            int send_count, int retry_flag, TIMESTAMP time_of_transmission){

        this.control = control;
        this.send_count = send_count;
        this.retry_flag = retry_flag;
        this.time_of_transmission = time_of_transmission;
    }
    
    public A2RL_RequestDataFrame(BYTE service, 
    						BYTE control, BYTE function, 
                            int send_count, int retry_flag, TIMESTAMP time_of_transmission){

        this.control = control;
        this.function = function;
       
        this.send_count = send_count;
        this.retry_flag = retry_flag;
        this.time_of_transmission = time_of_transmission;
    }

    public A2RL_RequestDataFrame(BYTE service, 
    						BYTE control, BYTE function, BYTE pad, OCTET send_data_length, OCTET send_data_buffer, 
                            int send_count, int retry_flag, TIMESTAMP time_of_transmission){

        this.control = control;
        this.function = function;
        this.pad = pad;
        
        this.send_data_length = send_data_length;
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

    /**
     * decode
     *
     * @param bytebuffer <code>IoBuffer</code> bytebuffer
     * @return frame <code>GeneralDataFrame</code> frame
     */
    public static A2RL_RequestDataFrame decode(IoBuffer bytebuffer) 
        throws Exception
    {
    	A2RL_RequestDataFrame frame = null;
        return frame;
    }
    
    public byte[] encode() throws Exception
    {

    	//length
        this.length = 1; //STX
        if(this.control!=null)
        	this.length+= 1; //CTRL
        if(this.function!=null)
        	this.length+= 1; //FUNC
        if(this.pad !=null)
        	this.length+= 1; //PAD
        
        if(send_data_length!=null && send_data_length.getValue().length>0){
        	this.length+= send_data_length.getValue().length; //LENGTH
        	this.length+= send_data_buffer.getValue().length; //DATA
        }
        
        this.length+= 2; //CRC
        
        //make frame
        byte[] buf = new byte[this.length];
        
        int idx =0;
        buf[idx++] = (byte) A2RL_DataConstants.STX;
        buf[idx++] = (byte) control.getValue();
        if(this.function!=null)
        	buf[idx++] = (byte) function.getValue();
        if(this.pad!=null)
        	buf[idx++] = (byte) pad.getValue();
        
        if(send_data_length!=null && send_data_length.getValue().length>0){
        	System.arraycopy(send_data_length.getValue(), 0, buf, idx, send_data_length.getValue().length);
        	idx+=send_data_length.getValue().length;
        }
        if(send_data_buffer != null && send_data_buffer.getValue().length > 0){
            System.arraycopy(send_data_buffer.getValue(), 0, buf, idx, send_data_buffer.getValue().length);
            idx+=send_data_buffer.getValue().length;
        }
        
        buf[idx++]	=	A2RL_DataConstants.getCRCH(buf);
        buf[idx]	=	A2RL_DataConstants.getCRCL(buf);
        
        return buf;
    }

    public IoBuffer getIoBuffer() throws Exception
    {

        byte[] b = encode();
        IoBuffer buf = IoBuffer.allocate(b.length);
        buf.put(b);
        buf.flip();
        return buf;
    }

    /**
     * get string
     */
    public String toString()
    {
        StringBuffer strbuf = new StringBuffer();
        strbuf.append(super.toString());
        strbuf.append("CLASS["+this.getClass().getName()+"]\n");
        strbuf.append("service : " + service + "\n");
        strbuf.append("control : " + control + "\n");
        strbuf.append("function : " + function + "\n");
        strbuf.append("pad : " + pad + "\n");
        strbuf.append("send_data_length: " + Hex.decode(send_data_length.getValue()) + "\n");
        strbuf.append("send_data_buffer: " + Hex.decode(send_data_buffer.getValue()) + "\n");
        strbuf.append("send_count : " + send_count + "\n");
        strbuf.append("retry_flag : " + retry_flag + "\n");
        strbuf.append("time_of_transmission : " + time_of_transmission + "\n");

        return strbuf.toString();
    }
}
