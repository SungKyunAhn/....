package com.aimir.fep.protocol.mrp.protocol;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;

import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.mrp.command.frame.RequestDataFrame;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * RequestDataFrame
 * 
 * @author goodjob@nuritelecom.com
 */
public class EDMI_Mk10_RequestDataFrame extends RequestDataFrame
{
    private static Log log = LogFactory.getLog(EDMI_Mk10_RequestDataFrame.class);
	
    public BYTE service = new BYTE();
    public OCTET send_data_buffer = new OCTET();
    public OCTET command = new OCTET();
    public int send_count;
    public int retry_flag;
    public TIMESTAMP time_of_transmission = new TIMESTAMP();

    /**
     * constructor
     */
    public EDMI_Mk10_RequestDataFrame()
    {
    }
    
    public EDMI_Mk10_RequestDataFrame(BYTE service, OCTET command,
                            OCTET send_data_buffer, int send_count,
                            int retry_flag, TIMESTAMP time_of_transmission){
        this.service = service;
        this.command = command;
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

    /**
     * decode
     *
     * @param bytebuffer <code>IoBuffer</code> bytebuffer
     * @return frame <code>GeneralDataFrame</code> frame
     */
    public static EDMI_Mk10_RequestDataFrame decode(IoBuffer bytebuffer) 
        throws Exception
    {
    	EDMI_Mk10_RequestDataFrame frame = null;
        return frame;
    }
    
    public byte[] encode() throws Exception
    {
        byte[] commandValue = command.getValue();
        byte[] data = send_data_buffer.getValue();
        
        int dataLength = 2; //CRC
        if(commandValue!=null && commandValue.length>0)
        	dataLength += commandValue.length; //COMMAND
        else{
        	return new byte[]{EDMI_Mk10_DataConstants.ESC,EDMI_Mk10_DataConstants.STX, EDMI_Mk10_DataConstants.ETX};
        }
        
        if(data!=null && data.length>0)
        	dataLength += data.length; //DATA
        
        
        char crc = 0;
        byte[] buf = null;
        if(data!=null && data.length>0){
        	buf = new byte[(dataLength+2)*2];	
        }else{
        	buf = new byte[]{EDMI_Mk10_DataConstants.STX, EDMI_Mk10_DataConstants.ETX};
        	return buf;
        }
   //     log.debug("dataLength : "+dataLength);
   //     log.debug("commandValue : "+commandValue.length);
   //     log.debug("data : "+data.length);
        
        /*
        * Add the STX and start the CRC calc.
        */
        int idx =0;
        buf[idx++]= EDMI_Mk10_DataConstants.STX;
        crc = EDMI_Mk10_DataConstants.CalculateCharacterCRC16(crc,buf[0]);
        
        /*
        * Send the data, computing CRC as we go.
        */
        for (int i=0; i<commandValue.length; i++) {
        	
	        byte[] dled = send_byte(commandValue[i]);
	        buf[idx++]= dled[0];
	        if(dled.length>1){
	        	buf[idx++]= dled[1];
	        }
	        crc = EDMI_Mk10_DataConstants.CalculateCharacterCRC16(crc,commandValue[i]);
        }
        for (int i=0; i<data.length; i++) {
        	
	        byte[] dled = send_byte(data[i]);
	        buf[idx++]= dled[0];
	        if(dled.length>1){
	        	buf[idx++]= dled[1];
	        }
	        crc = EDMI_Mk10_DataConstants.CalculateCharacterCRC16(crc,data[i]);
        }
        
        /*
        * Add the CRC
        */
        byte[] dleCRC = send_byte((byte)(crc>>8));
        buf[idx++]= dleCRC[0];
        if(dleCRC.length>1){
        	buf[idx++]= dleCRC[1];
        }
        dleCRC = send_byte((byte)crc);
        buf[idx++]= dleCRC[0];
        if(dleCRC.length>1){
        	buf[idx++]= dleCRC[1];
        }
        
        byte[] dataFinal = new byte[idx+1];
        System.arraycopy(buf, 0, dataFinal, 0, idx);
        
        /*
        * Add the ETX
        */
        dataFinal[idx] = EDMI_Mk10_DataConstants.ETX;
       
    //    log.debug("dataFinal ::: "+Hex.decode(dataFinal));
        
        return dataFinal;
    }

    /*
    * DLE stuff a single byte
    */
    public byte[] send_byte(byte d)
    {
    	byte[] b = null;
	    switch(d) {
		    case EDMI_Mk10_DataConstants.STX:
		    case EDMI_Mk10_DataConstants.ETX:
		    case EDMI_Mk10_DataConstants.DLE:
		    case EDMI_Mk10_DataConstants.XON:
		    case EDMI_Mk10_DataConstants.XOFF:
		    	b = new byte[]{EDMI_Mk10_DataConstants.DLE, (byte)(d|0x40)};
		    break;
		    default:
		    	b = new byte[]{d};
	    }
	    return b ;
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
        strbuf.append("command: " + command + "\n");
        strbuf.append("send_data_buffer: " + Hex.decode(send_data_buffer.getValue()) + "\n");
        strbuf.append("send_count : " + send_count + "\n");
        strbuf.append("retry_flag : " + retry_flag + "\n");
        strbuf.append("time_of_transmission : " + time_of_transmission + "\n");

        return strbuf.toString();
    }

}
