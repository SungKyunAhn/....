package com.aimir.fep.protocol.fmp.datatype;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.mina.core.buffer.IoBuffer;

import com.aimir.fep.util.DataUtil;

/**
 * represent GMTTIME Data Type
 * 
 * @author 
 * @version $Rev: 1 $, $Date: 2007-10-05 15:59:15 +0900 $,
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "gmtTime", propOrder = {
})
public class GMTTIME extends FMPNonFixedVariable
{
    /**
     * constructor
     */
    public GMTTIME() 
    {
    }

    /**
     * constructor
     *
     * @param len <code>int</code> length of byte array
     */
    public GMTTIME(int len)
    {
        this.value = new byte[len];
        this.len = len;
        this.isFixed = true;
    }

    /**
     * constructor
     *
     * @param value <code>String</code> GMTTIME String
     */
    public GMTTIME(String value)
    {
        this.value = DataUtil.encodeGMT(value);
        this.len = this.value.length;
        this.isFixed = true;
    }

    /**
     * constructor 
     *
     * @param value <code>byte[]</code> GMTTIME byte array
     */
    public GMTTIME(byte[] value)
    {
        this.value = value;
        this.len  = value.length;
        this.isFixed = true;
    }

    /**
     * get GMTTIME String
     *
     * @return value <code>String</code> GMTTIME String
     */
    public String getValue()
    {
        return DataUtil.decodeGMT(value);        
    }

    /**
     * set GMTTIME Value String
     *
     * @param value <code>String</code> GMTTIME String
     */
    public void setValue(String value)
    {
        if(isFixed)
        {
            byte[] bx = DataUtil.encodeGMT(value);
            if(bx.length >= this.len)
                System.arraycopy(bx,0,this.value,0,this.len);
            else
                System.arraycopy(bx,0,this.value,0,bx.length);
        } else {
            this.value = DataUtil.encodeGMT(value);
            this.len = this.value.length;
        }
    }

    /**
     * set GMTTIME Value Byte array
     *
     * @param value <code>byte[]</code> GMTTIME value byte array
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
     * encode GMTTIME Value
     *
     * @return value <code>byte[]</code> encoded byte array
     */
    public byte[] encode()
    { 
        return this.value;
    }

    /**
     * encode GMTTIME Value
     *
     * @param iscompact <code>boolean</code>
     * @return value <code>byte[]</code> encoded byte array
     */
    public byte[] encode(boolean iscompact)
    {
        return encode();
    }

    /**
     * decode GMTTIME Value
     *
     * @param buff <code>IoBuffer</code> input bytebuffer
     * @param size <code>int</code> GMTTIME Value length
     */
    public void decode(String ns, IoBuffer buff,int size)
    {
        byte[] bx = new byte[size];
        buff.get(bx,0,bx.length);
        setValue(bx);
    }

    /**
     * decode GMTTIME Value
     *
     * @param buff <code>IoBuffer</code> input bytebuffer
     */
    public void decode(String ns, IoBuffer buff)
    {
        this.value = new byte[11];
        isFixed = true;
        if(isFixed && (this.value != null) && (this.value.length > 0))
            buff.get(this.value,0,this.value.length);
    }

    public int decode(String ns, byte[] buff,int pos)
    {
        this.value = new byte[11];
        System.arraycopy(buff,pos,this.value,0,this.value.length);
        return this.value.length;
    }

    public int decode(String ns, byte[] buff,int pos,int size)
    {
        this.value = new byte[11];
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
        return DataType.GMTTIME;
    }
    /**
     * get java syntax
     *
     *@returnsyntax<code>String</code>
     */
    public String getJavaSyntax()
    {
        return GMTTIME.class.getName();
    }


    /**
     * get GMTTIME String Value
     *
     * @return value <code>String</code>
     */
    public String toString()
    {
        return DataUtil.decodeGMT(value);
    }

    public String getMIBName() { return "gmtEntry"; }
}
