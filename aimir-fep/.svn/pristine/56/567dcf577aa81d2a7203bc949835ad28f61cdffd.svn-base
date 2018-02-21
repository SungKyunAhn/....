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
 * @author Yeon Kyoung Park
 * @version $Rev: 1 $, $Date: 2008-01-05 15:59:15 +0900 $,
 */
public class KV2C_RequestDataFrame extends RequestDataFrame
{
    @SuppressWarnings("unused")
    private static Log log = LogFactory.getLog(KV2C_RequestDataFrame.class);
    public byte kind;
    public byte meter_cmd;
    public byte before_txd_control;
    public int len;
    public int meter_length;
    public OCTET msg = null;
    public int cksofs = -1;
    public boolean write = false;
    

    public int send_count;
    public int retry_flag;
    public TIMESTAMP time_of_transmission = new TIMESTAMP();
    
    private int length;

    /**
     * constructor
     */
    public KV2C_RequestDataFrame()
    {
    }
    
    public KV2C_RequestDataFrame(byte before_txd_control, byte kind, byte meter_cmd, int len, int meter_length)
    {
        this.kind = kind;
        this.meter_cmd = meter_cmd;
        this.len = len;
        this.meter_length = meter_length;
        this.before_txd_control = before_txd_control;
    }
    
    public KV2C_RequestDataFrame(byte before_txd_control, byte kind, byte meter_cmd, OCTET msg, int len, int meter_length)
    {
        this.kind = kind;
        this.meter_cmd = meter_cmd;
        this.msg = msg;
        this.len = len;
        this.meter_length = meter_length;
        this.before_txd_control = before_txd_control;
    }
        
    public KV2C_RequestDataFrame(byte before_txd_control, byte kind, byte meter_cmd, OCTET msg,
                                 int cksofs, int len, boolean write, int meter_length)
    {
        this.kind = kind;
        this.meter_cmd = meter_cmd;
        this.msg = msg;
        this.len = len;
        this.meter_length = meter_length;
        this.write = write;
        this.cksofs = cksofs;
        this.before_txd_control = before_txd_control;
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

  
    public byte[] encode() throws Exception
    {
        byte[] cmd = new byte[len];

        int i = 0;
        byte ctl_data;
        
        if(this.meter_cmd == ANSI.IDENT || this.meter_cmd == ANSI.LOGON){
            ctl_data = 0x00;
        }else{
            if(this.before_txd_control == 0x20) 
                ctl_data = 0x00;
            else 
                ctl_data = 0x20;
        }
        
        if(kind == 1) 
            cmd[i++] = ANSI.ACK;
        
        cmd[i++] = ANSI.STP;
        cmd[i++] = ANSI.REV_OPTICAL;
        cmd[i++] = ctl_data;    
        cmd[i++] = 0x00;
        cmd[i++] = (byte)(this.meter_length >> 8);
        cmd[i++] = (byte)(this.meter_length);
        cmd[i++] = this.meter_cmd;
        
        if(this.msg != null)
        {
            System.arraycopy(this.msg.getValue(),0,cmd,i,msg.getValue().length);
            i += msg.getValue().length;
            if(write){
                cmd[i++] = ANSI.chkSum(msg.getValue(),this.cksofs,msg.getValue().length-this.cksofs);
            }
        }

        char crc = ANSI.crc(len - (2 + kind),kind,cmd);
        cmd[i++] = (byte)(crc >> 8);
        cmd[i++] = (byte)(crc);
        
        this.before_txd_control = ctl_data;

        return cmd;
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
        strbuf.append("destination: " + Hex.decode(destination.getValue()) + "\n");        
        strbuf.append("source : " + source + "\n");
        strbuf.append("control : " + control + "\n");
        strbuf.append("send_data_buffer: " + Hex.decode(send_data_buffer.getValue()) + "\n");
        strbuf.append("send_count : " + send_count + "\n");
        strbuf.append("retry_flag : " + retry_flag + "\n");
        strbuf.append("time_of_transmission : " + time_of_transmission + "\n");

        return strbuf.toString();
    }
    
    public byte getTXDControl(){
        return this.before_txd_control;
    }
}
