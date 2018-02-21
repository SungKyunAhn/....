package com.aimir.fep.util;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * CRC 16
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class SunCRC16 {

    /** value contains the currently computed CRC, set it to 0 initally */ 
    public int value;

    /**
     * constructor
     */
    public SunCRC16() 
    { 
        value = 0; 
    } 


    /** 
     * update CRC with byte b 
     *
     * @param aByte <code>byte</code>
     */
    public void update(byte aByte) 
    { 
        int a, b; 
        a = (int) aByte; 
        for (int count = 7; count >=0; count--) 
        { 
            a = a << 1; 
            b = (a >>> 8) & 1; 
            if ((value & 0x8000) != 0) 
            { 
                value = ((value << 1) + b) ^ 0x1021; 
            } 
            else 
            { 
                value = (value << 1) + b; 
            } 
        } 
        value = value & 0xffff; 
        return; 
    }

    /**
     * update
     *
     * @param buffer <code>byte[]</code>
     * @param off <code>int</code> start offset
     * @param len <code>int</code> length
     */
    public void update(byte[] buff, int off, int len) 
    { 
        for(int i = off ; i < (off + len) ; i++) 
            update(buff[i]); 
    } 

    /**
     * update
     *
     * @param buffer <code>IoBuffer</code>
     */
    public void update(IoBuffer buff) 
    { 
        buff.rewind(); 
        while(buff.hasRemaining()) 
        { 
            update(buff.get()); 
        } 
        buff.rewind(); 
    } 

    /**
     * update
     *
     * @param buffer <code>IoBuffer</code>
     * @param off <code>int</code> start offset
     * @param len <code>int</code> length
     */
    public void update(IoBuffer buff,int off, int len) 
    { 
        int prepos = buff.position(); 
        buff.position(off); 
        int cnt = 0; 
        while(buff.hasRemaining()) 
        { 
            if(cnt >= len) 
                break; 
            update(buff.get()); 
            cnt++; 
        } 
        buff.position(prepos); 
    }

    /** 
     * reset CRC value to 0 
     */ 
    public void reset() 
    { 
        value = 0; 
    } 
}

