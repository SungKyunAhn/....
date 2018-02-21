package com.aimir.fep.protocol.mrp.protocol;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;

import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.mrp.command.frame.RequestDataFrame;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.KepcoCRC16;


/**
 * RequestDataFrame
 * 
 * @author Yeon Kyoung Park
 * @version $Rev: 1 $, $Date: 2008-01-05 15:59:15 +0900 $,
 */
public class LK3410CP_005_RequestDataFrame extends RequestDataFrame
{
    @SuppressWarnings("unused")
    private static Log log = LogFactory.getLog(LK3410CP_005_RequestDataFrame.class);
    public BYTE service = new BYTE();
    public OCTET destination = new OCTET(6);
    public BYTE source = new BYTE();
    public OCTET send_data_buffer = null;
    public BYTE control = new BYTE();
    public int send_count;
    public int retry_flag;
    public TIMESTAMP time_of_transmission = new TIMESTAMP();
    
    private int length;

    /**
     * constructor
     */
    public LK3410CP_005_RequestDataFrame()
    {
    }
    
    public LK3410CP_005_RequestDataFrame(BYTE service, OCTET destination, BYTE source, BYTE control,
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

    /**
     * decode
     *
     * @param bytebuffer <code>IoBuffer</code> bytebuffer
     * @return frame <code>GeneralDataFrame</code> frame
     */
    public static LK3410CP_005_RequestDataFrame decode(IoBuffer bytebuffer) 
        throws Exception
    {
        LK3410CP_005_RequestDataFrame frame = null;
        return frame;
    }
    
    public byte[] encode() throws Exception
    {
        //log.debug("RequestDataFrame/"+toString());
        this.length = LK3410CP_005_DataConstants.SOH.getValue().length
                    + 1
                    + destination.getValue().length
                    + 1
                    + 1
                    + send_data_buffer.getValue().length
                    + 2;

        if(time_of_transmission != null){
            this.length += time_of_transmission.getValue().length();
        }
        byte[] buf = new byte[this.length];
        
        System.arraycopy(LK3410CP_005_DataConstants.SOH.getValue(), 0, buf, 0, 2);
        buf[2] = (byte) (this.length - 5);
        System.arraycopy(destination.getValue(), 0, buf, 3, destination.getValue().length);
        buf[3+destination.getValue().length] = (byte)source.getValue();
        buf[3+destination.getValue().length+1] = (byte)control.getValue();

        if(send_data_buffer != null && send_data_buffer.getValue().length > 0){
            System.arraycopy(send_data_buffer.getValue(), 0, buf, 3+destination.getValue().length+1+1, send_data_buffer.getValue().length);
        }
        byte[] crc16 = KepcoCRC16.getCRC16(buf, this.length-2);
        System.arraycopy( crc16, 0, buf, 3+destination.getValue().length+1+1+send_data_buffer.getValue().length, crc16.length);      
        
        return buf;
    }

    public IoBuffer getIoBuffer() throws Exception
    {
        /*
        this.length = RequestDataConstants.SOH.encode().length
                    + 1
                    + destination.getLen()
                    + 1
                    + 1
                    + send_data_buffer.getLen();
        if(time_of_transmission != null){
            this.length += time_of_transmission.getLen();
        }
        IoBuffer buf = IoBuffer.allocate(this.length);
        buf.put(RequestDataConstants.SOH.encode());
        buf.put(0,(byte)(this.length-5));
        buf.put(destination.encode());
        buf.put((byte)source.getValue());
        buf.put((byte)control.getValue());
        if(send_data_buffer != null && send_data_buffer.getLen() > 0){
            buf.put(send_data_buffer.encode());
        }
        byte[] crc16 = FrameUtil.getCRC(buf, 0, this.length-2);
        buf.put(crc16);   
        buf.flip();
        */
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
        strbuf.append("destination: " + Hex.decode(destination.getValue()) + "\n");        
        strbuf.append("source : " + source + "\n");
        strbuf.append("control : " + control + "\n");
        strbuf.append("send_data_buffer: " + Hex.decode(send_data_buffer.getValue()) + "\n");
        strbuf.append("send_count : " + send_count + "\n");
        strbuf.append("retry_flag : " + retry_flag + "\n");
        strbuf.append("time_of_transmission : " + time_of_transmission + "\n");

        return strbuf.toString();
    }
}
