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
public class LGRW3410_RequestDataFrame extends RequestDataFrame
{
    @SuppressWarnings("unused")
    private static Log log = LogFactory.getLog(LGRW3410_RequestDataFrame.class);

    public OCTET send_data_buffer = null;
    public BYTE control = new BYTE();
    public OCTET send_data_length = null;
    public int send_count;
    public int retry_flag;
    public TIMESTAMP time_of_transmission = new TIMESTAMP();
    public boolean ack;
    private int length;

    /**
     * constructor
     */
    public LGRW3410_RequestDataFrame()
    {
    }
    
    public LGRW3410_RequestDataFrame(BYTE service, BYTE control, OCTET send_data_length,
                            OCTET send_data_buffer, boolean ack, int send_count,
                            int retry_flag, TIMESTAMP time_of_transmission){
        this.ack = ack;
        this.control = control;
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
    public static LGRW3410_RequestDataFrame decode(IoBuffer bytebuffer) 
        throws Exception
    {
    	LGRW3410_RequestDataFrame frame = null;
        return frame;
    }
    
    public byte[] encode() throws Exception
    {
        //log.debug("RequestDataFrame/"+toString());
        this.length = 1 //SOH
                    + 1 //RESERVED
                    + 1 //CTRL
                    + 1 //SEQ_NO
                    + send_data_length.getValue().length //LENGTH
                    + send_data_buffer.getValue().length //DATA
                    +2; //CRC
        
        byte[] buf = new byte[this.length];
        
        buf[0] = (byte) LGRW3410_DataConstants.SOH;
        buf[1] = (byte) LGRW3410_DataConstants.RESERVED;
        buf[2] = (byte) control.getValue();
        buf[3] = (byte) LGRW3410_DataConstants.SEQ_NBR;
        if(send_data_length != null && send_data_length.getValue().length > 0){
            System.arraycopy(send_data_length.getValue(), 0, buf, 4, send_data_length.getValue().length);
        }
        if(send_data_buffer != null && send_data_buffer.getValue().length > 0){
            System.arraycopy(send_data_buffer.getValue(), 0, buf, 4+send_data_length.getValue().length, send_data_buffer.getValue().length);
        }
        char crc16 = LGRW3410_DataConstants.KH_CRC16(buf, 0, this.length-2);
        buf[4+send_data_length.getValue().length+send_data_buffer.getValue().length]	=	(byte)(crc16 >> 8);
        buf[5+send_data_length.getValue().length+send_data_buffer.getValue().length]	=	(byte)(crc16);
     //   System.arraycopy( crc16, 0, buf, 4+send_data_length.getValue().length+send_data_buffer.getValue().length, crc16.length);      
       
        if(ack){
        	byte[] buf2 = new byte[buf.length+1];
        	buf2[0] = LGRW3410_DataConstants.DATA_CTRL_R_ACK;
        	System.arraycopy( buf, 0, buf2, 1, buf.length);
        	return buf2;
        }else{
        	return buf;
        }
        
    //    return buf;
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
    
    public IoBuffer getIoBuffer(boolean a) throws Exception
    {
        byte[] b = encode();
        if(b[0] == LGRW3410_DataConstants.DATA_CTRL_R_ACK)
        	b[0] = LGRW3410_DataConstants.DATA_CTRL_R_NACK;
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
