package com.aimir.fep.protocol.fmp.datatype;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.mina.core.buffer.IoBuffer;

import com.aimir.fep.util.Bcd;

/**
 * represent BCD Data Type
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "bcd", propOrder = {
})
public class BCD extends FMPNonFixedVariable
{
    /**
     * constructor
     */
    public BCD() 
    {
    }

    /**
     * constructor
     *
     * @param len <code>int</code> length of byte array
     */
    public BCD(int len)
    {
        this.value = new byte[len];
        this.len = len;
        this.isFixed = true;
    }

    /**
     * constructor
     *
     * @param value <code>String</code> BCD String
     */
    public BCD(String value)
    {
        this.value = Bcd.encode(value);
        this.len = this.value.length;
        this.isFixed = true;
    }

    /**
     * constructor 
     *
     * @param value <code>byte[]</code> BCD byte array
     */
    public BCD(byte[] value)
    {
        this.value = value;
        this.len  = value.length;
        this.isFixed = true;
    }

    /**
     * get BCD String
     *
     * @return value <code>String</code> BCD String
     */
    public String getValue()
    {
        return Bcd.decode(this.value);
    }

    /**
     * set BCD Value String
     *
     * @param value <code>String</code> BCD String
     */
    public void setValue(String value)
    {
        if(isFixed)
        {
            byte[] bx = Bcd.encode(value);
            if(bx.length >= this.len)
                System.arraycopy(bx,0,this.value,0,this.len);
            else
                System.arraycopy(bx,0,this.value,0,bx.length);
        } else {
            this.value = Bcd.encode(value);
            this.len = this.value.length;
        }
    }

    /**
     * set BCD Value Byte array
     *
     * @param value <code>byte[]</code> BCD value byte array
     */
    public void setValue(byte[] value)
    {
        if(isFixed)
        {
            if(value.length >= this.len)
                System.arraycopy(value,0,this.value,0,this.len);
            else
                System.arraycopy(value,0,this.value,0,value.length);
        } else {
            this.value = value;
        }
    }

    /**
     * encode BCD Value
     *
     * @return value <code>byte[]</code> encoded byte array
     */
    public byte[] encode()
    { 
        return this.value;
    }

    /**
     * encode BCD Value
     *
     * @param iscompact <code>boolean</code>
     * @return value <code>byte[]</code> encoded byte array
     */
    public byte[] encode(boolean iscompact)
    {
        return encode();
    }

    /**
     * decode BCD Value
     *
     * @param buff <code>IoBuffer</code> input bytebuffer
     * @param size <code>int</code> BCD Value length
     */
    public void decode(String ns, IoBuffer buff,int size)
    {
        byte[] bx = new byte[size];
        buff.get(bx,0,bx.length);
        setValue(bx);
    }

    /**
     * decode BCD Value
     *
     * @param buff <code>ByteBuffer</code> input bytebuffer
     */
    public void decode(String ns, IoBuffer buff)
    {
        if(isFixed && (this.value != null) && (this.value.length > 0))
            buff.get(this.value,0,this.value.length);
    }

    public int decode(String ns, byte[] buff,int pos)
    {
        System.arraycopy(buff,pos,this.value,0,this.value.length);
        return this.value.length;
    }

    public int decode(String ns, byte[] buff,int pos,int size)
    {
        this.value = new byte[size];
        System.arraycopy(buff,pos,this.value,0,this.value.length); 
        return size;
    }

    /**
     * get syntax(data type)
     *
     * @return syntax <code>int</code> syntax
     */
    public int getSyntax()
    {
        return DataType.BCD;
    }
    /**
     * get java syntax
     *
     *@returnsyntax<code>String</code>
     */
    public String getJavaSyntax()
    {
        return String.class.getName();
    }

    public String getMIBName() { return "bcdEntry"; }


    /**
     * get BCD String Value
     *
     * @return value <code>String</code>
     */
    public String toString()
    {
        return Bcd.decode(this.value);
    }
}
