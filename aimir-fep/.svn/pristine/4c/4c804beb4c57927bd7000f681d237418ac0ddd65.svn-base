package com.aimir.fep.protocol.fmp.datatype;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * represent FMP Protocol Variable
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 
* <pre>
 * &lt;complexType name="fmpVariable">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fmpVariable")
@XmlSeeAlso({
    OID.class,
    BYTE.class,
    SHORT.class,
    CHAR.class,
    INT.class,
    UINT.class,
    FMPNonFixedVariable.class,
    SMIValue.class,
    IPADDR.class,
    WORD.class,
    BOOL.class
})
public abstract class FMPVariable implements java.io.Serializable
{
    /**
     * encode
     *
     * @return result <code>byte[]</code>
     */
    public abstract byte[] encode();
    /**
     * decode
     *
     * @param buff <code>IoBuffer</code>
     * @param size <code>int</code>
     */
    public abstract void decode(String ns,IoBuffer buff,int size);

    public abstract int decode(String ns,byte[] buff,int pos);
    public abstract int decode(String ns,byte[] buff,int pos,int size);

    /**
     * get syntax
     *
     * @return syntax <code>int</code>
     */
    public abstract int getSyntax();

    /**
     * get java syntax
     *
     * @return syntax <code>String</code>
     */
    public abstract String getJavaSyntax();

    public abstract String getMIBName();

    /**
     * get syntax string
     *
     * @return syntax <code>int</code>
     */
    public final String getSyntaxString()
    {
        String clsname = getClass().getName();
        int idx = clsname.lastIndexOf(".");
        return clsname.substring(idx+1,clsname.length());
    }

    /**
     * get String
     *
     * @return result <code>String</code>
     */
    public abstract String toString();


    /**
     * get FMP Variable Object according to specified data type
     *
     * @param  type <code>String</code> data type
     * @return variable <code>FMPVariable</code>
     */
    public final static FMPVariable getFMPVariableObject(String type)
    {
        String pre = "com.aimir.fep.protocol.fmp.datatype.";
        if(type.toUpperCase().equals("STRING")
                || type.toUpperCase().equals("STREAM"))
        {
            type="OCTET";
        }
        try {
        Class cls = Class.forName(pre+type.toUpperCase());
        return (FMPVariable)cls.newInstance();
        }catch(Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * get FMP Variable Object according to specified data type
     *
     * @param  type <code>int</code> data type
     * @return variable <code>FMPVariable</code>
     */
    public final FMPVariable getFMPVariableObject(int type)
    { 
        if(type == DataType.BOOL) 
            return new BOOL();
        else if(type == DataType.BYTE)
            return new BYTE();
        else if(type == DataType.WORD)
            return new WORD();
        else if(type == DataType.UINT)
            return new UINT();
        else if(type == DataType.CHAR)
            return new CHAR();
        else if(type == DataType.SHORT)
            return new SHORT();
        else if(type == DataType.INT)
            return new INT();
        else if(type == DataType.BCD)
            return new BCD();
        else if(type == DataType.VER)
            return new VER();
        else if(type == DataType.HEX)
            return new HEX();
        else if(type == DataType.STRING)
            return new OCTET();
        else if(type == DataType.STREAM)
            return new OCTET();
        else if(type == DataType.OPAQUE)
            return new OPAQUE();
        else if(type == DataType.OID)
            return new OID();
        else if(type == DataType.IPADDR)
            return new IPADDR();
        else if(type == DataType.SMIVALUE)
            return new SMIValue();
        else if(type == DataType.TIMESTAMP)
            return new TIMESTAMP();
        else if(type == DataType.TIMEDATE)
            return new TIMEDATE();

        return null;
    }

}
