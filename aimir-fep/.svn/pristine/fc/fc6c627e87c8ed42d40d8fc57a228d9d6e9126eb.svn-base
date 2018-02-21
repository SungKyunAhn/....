package com.aimir.fep.util;

import org.apache.mina.core.buffer.IoBuffer;


/*******************************************************************
 *  Cylcic Redundancy Check (CRC).
 *
 *  1 + x + x^5 + x^12 + x^16 is irreducible polynomial.
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */

public class CRC16 
{ 
    // convert an int into a  String of 0's and 1's

    private short crc = (short) 0xffff;
    /**
     * constructor
     */
    public CRC16()
    {
        crc = (short)0xffff;
    }

    public String toBinaryString() {
        String s = "";
        for (int i = 0; i < 16; i++)
        s = (crc >> i & 1) + s;
        return s;
    }

    /**
     * update
     *
     * @param buffer <code>ByteBuffer</code>
     */
    public void update(IoBuffer buffer)
    {
        int pos = buffer.position();
        while(buffer.hasRemaining())
        {
            byte b = buffer.get();
            for(int i = 0 ; i < 8 ; i++)
            {
                boolean c15 = ((crc >> 15      & 1) == 1); 
                boolean bit = ((b   >> (7 - i) & 1) == 1); 
                crc <<= 1; 
                if (c15 ^ bit) 
                    crc ^= 0x1021; // 0001 0000 0010 0001  (0, 5, 12) 
            }
        }
        buffer.position(pos);
        return;
    }

    /**
     * update
     *
     * @param buffer <code>ByteBuffer</code>
     * @param off <code>int</code> start offset
     * @param len <code>int</code> length
     */
    public void update(IoBuffer buffer,int off, int len)
    {
        int pos = buffer.position();
        buffer.position(off);
        int cnt = 0;
        while(buffer.hasRemaining())
        {
            if(cnt >= len)
                break;
            byte b = buffer.get();
            for(int i = 0 ; i < 8 ; i++)
            {
                boolean c15 = ((crc >> 15      & 1) == 1); 
                boolean bit = ((b   >> (7 - i) & 1) == 1); 
                crc <<= 1; 
                if (c15 ^ bit) 
                    crc ^= 0x1021; 
            }
            cnt++;
        }
        buffer.position(pos);
        return;
    }

    /**
     * update
     *
     * @param buffer <code>byte[]</code>
     */
    public void update(byte[] buffer)
    {
        for(int i = 0;  i < buffer.length ; i++)
        {
            byte b = buffer[i];
            for(int j = 0 ; j < 8 ; j++)
            {
                boolean c15 = ((crc >> 15      & 1) == 1); 
                boolean bit = ((b   >> (7 - j) & 1) == 1); 
                crc <<= 1; 
                if (c15 ^ bit) 
                    crc ^= 0x1021; 
            }
        }
        return;
    }

    /**
     * update
     *
     * @param buffer <code>byte[]</code>
     * @param off <code>int</code> start offset
     * @param len <code>int</code> length
     */
    public void update(byte[]  buffer, int off, int len)
    {
        for(int i = off;  i < (len + off) ; i++)
        {
            byte b = buffer[i];
            for(int j = 0 ; j < 8 ; j++)
            {
                boolean c15 = ((crc >> 15      & 1) == 1); 
                boolean bit = ((b   >> (7 - j) & 1) == 1); 
                crc <<= 1; 
                if (c15 ^ bit) 
                    crc ^= 0x1021; 
            }
        }
        return;
    }

    /**
     * reset
     */
    public void reset()
    {
        crc = (short)0xffff;
    }

    /**
     * get crc value
     *
     * @return crc <code>int</code>
     */
    public int getValue()
    {
        int sum = (crc & 0xffff) << 0;
        return sum;
    }
}

