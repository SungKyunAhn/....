package com.aimir.fep.protocol.fmp.datatype;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.mina.core.buffer.IoBuffer;

import com.aimir.fep.util.Hex;

/**
 * represent MACADDR Data Type
 * 
 * @author goodjob (goodjob@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2014-09-10 10:00:00 +0900 $,
 * <pre>
 * &lt;complexType name="macaddr">
 *   &lt;complexContent>
 *     &lt;extension base="{http://server.ws.command.fep.aimir.com/}fmpVariable">
 *       &lt;sequence>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "macaddr", propOrder = {
    "value"
})
public class MACADDR extends FMPVariable
{
    private String value = null;

    /**
     * constructor
     */
    public MACADDR()
    {
    }

    /**
     * constructor
     *
     * @param value <code>String</code> value
     */
    public MACADDR(String value)
    {
        this.value = value;
    }

    /**
     * constructor
     *
     * @param value <code>byte[]</code> value
     */
    public MACADDR(byte[] value)
    {
        setValue(value);
    }

    /**
     * get value
     *
     * @return result <code>String</code>
     */
    public String getValue()
    {
        return  this.value;
    }

    /**
     * set value
     *
     * @param value <code>int</code>
     */
    public void setValue(String value)
    {
        this.value = value;
    }
    /**
     * set value
     *
     * @param value <code>byte[]</code>
     */
    public void setValue(byte[] value)
    {
    	String hexValue = Hex.decode(value);
        this.value = hexValue.substring(0, 2)
        		+ ":" + hexValue.substring(2, 4) 
        		+ ":" + hexValue.substring(4, 6) 
        		+ ":" + hexValue.substring(6, 8) 
        		+ ":" + hexValue.substring(8, 10) 
        		+ ":" + hexValue.substring(10, 12);
    }

    /**
     * encode IP6ADDR Value
     *
     * @return value <code>byte[]</code> encoded byte array
     */
    public byte[] encode()
    {
        if(value == null)
            return new byte[0];
        return Hex.encode(this.value.replace(":", ""));
    }

    /**
     * decode MACADDR Value
     *
     * @param buff <code>IoBuffer</code> input bytebuffer
     * @param size <code>int</code> Value length
     */
    public void decode(String ns, IoBuffer buff,int size)
    {
        if(size < 6)
        {
            byte[] bx = new byte[6];
            buff.get(bx,0,size);
            setValue(bx);
        }
        else if(size == 6)
        {
            byte[] bx = new byte[6];
            buff.get(bx,0,bx.length);
            setValue(bx);
        } else if(size > 6)
        {
            byte[] temp = new byte[size];
            buff.get(temp,0,temp.length);
            byte[] bx = new byte[6];
            System.arraycopy(temp,0,bx,0,bx.length);
            setValue(bx);
        }
    }

    public int decode(String ns, byte[] buff,int pos)
    {
        byte[] bx = new byte[6];
        System.arraycopy(buff,pos,bx,0,bx.length);
        setValue(bx);
        return bx.length;
    }

    public int decode(String ns, byte[] buff,int pos,int size)
    {
        byte[] bx = new byte[6];
        System.arraycopy(buff,pos,bx,0,bx.length);
        setValue(bx);
        return size;
    }

    /**
     * get syntax(data type)
     *
     * @return syntax <code>int</code> syntax
     */
    public int getSyntax()
    {
        return DataType.MACADDR;
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
    public String getMIBName() { return "macEntry"; }

    /**
     * get IPADDR String Value
     *
     * @return value <code>String</code>
     */
    public String toString()
    {
        return this.value;
    }
}
