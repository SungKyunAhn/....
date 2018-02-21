package com.aimir.fep.protocol.fmp.datatype;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.mina.core.buffer.IoBuffer;

import com.aimir.fep.protocol.fmp.frame.service.Entry;
import com.aimir.fep.util.CodecUtil;

/**
 * represent OPAQUE Data Type
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "opaque", propOrder = {
    "value",
    "clsName"
})
public class OPAQUE extends FMPVariable
{
    Object  value = null;
    String  clsName = "";

    /**
     * constructor
     */
    public OPAQUE()
    {
    }

    /**
     * constructor
     *
     * @param clsName <code>String</code> object class name
     */
    public OPAQUE(String clsName)
    {
        this.clsName = clsName;
    }

    /**
     * constructor
     *
     * @param value <code>Entry</code> Entry object
     */
    public OPAQUE(Entry value)
    {
        this.clsName = value.getClass().getName(); 
        this.value = value;
    }

    /**
     * constructor
     *
     * @param bytebuffer <code>IoBuffer</code> byte buffer
     * @param clsName <code>String</code> Entry Class name
     */
    public OPAQUE(String ns, IoBuffer bytebuffer,String clsName)
    { 
        try
        {
            decode(ns,bytebuffer,clsName);
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * get Entry Class name
     *
     * @return clsName <code>String</code> Entry Class name
     */
    public String getClsName()
    {
        return this.clsName;
    } 

    /**
     * set Entry Class name
     *
     * @param clsName <code>String</code> Entry Class name
     */
    public void setClsName(String clsName)
    {
        this.clsName = clsName;
    }

    /**
     * get Entry Object
     *
     * @return object <code>Object</code> Entry Object
     */
    public Object getValue()
    {
        return this.value;
    }

    /**
     * set Entry Object
     *
     * @param object <code>Object</code> Entry Object
     */
    public void setValue(Entry value)
    {
        this.clsName = value.getClass().getName(); 
        this.value = value;
    }

    /**
     * encode OPAQUE Value
     *
     * @return value <code>byte[]</code> encoded byte array
     */
    public byte[] encode() 
    {
        try 
        {
            if(value == null) 
                return new byte[0]; 
            return CodecUtil.pack(this.value);
        }catch(Exception ex)
        {
            ex.printStackTrace();
            return new byte[0];
        }
    }

    /**
     * decode OPAQUE Value
     *
     * @param buff <code>IoBuffer</code> byte buffer
     * @param clsName <code>String</code> Entry Class name
     */
    public void decode(String ns, IoBuffer buff,String clsName) 
    {
        try {
            setValue((Entry)CodecUtil.unpack(ns,clsName,buff));
        }catch(Exception ex)
        {
            ex.printStackTrace();
            return;
        }
    }

    public int decode(String ns, byte[] buff,int pos)
    {
        int size = 0;
        try {
            //Class cls = Class.forName(clsName);
            //Object obj = cls.newInstance();
            //size = CodecUtil.sizeOf(obj);
            //byte[] bx = new byte[size];
            //System.arraycopy(buff,pos,bx,0,size);
            Entry entry = (Entry)CodecUtil.unpack(ns,clsName,buff,pos);
            size = CodecUtil.sizeOf(entry);
            setValue(entry);
            return size;
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return size;
    }

    public int decode(String ns, byte[] buff,int pos,int size)
    {
        //int len = 0;
        try {
            //Class cls = Class.forName(clsName);
            //Object obj = cls.newInstance();
            //len = CodecUtil.sizeOf(obj);
            //byte[] bx = new byte[len];
            //System.arraycopy(buff,pos,bx,0,bx.length);
            setValue((Entry)CodecUtil.unpack(ns,clsName,buff,pos));
            return size;
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return size;
    }


    /**
     * decode OPAQUE Value
     *
     * @param buff <code>ByteBuffer</code> byte buffer
     * @param size <code>int</code> value length
     */
    public void decode(String ns, IoBuffer buff,int size) 
    {
        int pos = buff.position();
        try {
            setValue((Entry)CodecUtil.unpack(ns,clsName,buff));
        }catch(Exception ex)
        {
            ex.printStackTrace();
            buff.position(pos+size);
            return;
        }
    }

    /**
     * get syntax(data type)
     *
     * @return syntax <code>int</code> syntax
     */
    public int getSyntax()
    {
        return DataType.OPAQUE;
    }

    /**
     * get java syntax
     *
     *@returnsyntax<code>String</code>
     */
    public String getJavaSyntax()
    {
        return Object.class.getName();
    }
    public String getMIBName() { return "opaqueEntry"; }

    /**
     * get OPAQUE String Value
     *
     * @return value <code>String</code>
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(this.value);

        return sb.toString();
    }
}
