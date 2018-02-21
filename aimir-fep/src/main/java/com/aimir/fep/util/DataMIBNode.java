package com.aimir.fep.util;

import com.aimir.fep.protocol.fmp.datatype.OID;

/**
 * Data MIB Node
 * 
 * @author Y.S Kim (sorimo@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class DataMIBNode extends MIBNode
{
    private String type = null;
    private int len = 0;
    private String desc = null;

    /**
     * constructor
     */
    public DataMIBNode()
    {
        setNodeType(MIBNodeConstants.DATA);
    }
    
    /**
     * constructor
     *
     * @param name <code>String</code> name
     * @param type <code>String</code> type
     * @param len <code>String</code> length
     * @param oid <code>OID</code> oid
     * @param desc <code>String</code> description
     */
    public DataMIBNode(String name, String type, String len, 
            OID oid, String desc)
    {
        setNodeType(MIBNodeConstants.DATA);

        this.name = name;
        this.type = type;
        if (len != null && !len.equals("")) 
            this.len = Integer.parseInt(len);
        this.oid = oid;
        this.desc = desc;
    }

    /**
     * get type
     *
     * @return type <code>String</code>
     */
    public String getType()
    {
        return type;
    }

    /**
     * set type
     *
     * @param type <code>String</code> type
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * get length
     *
     * @return length <code>int</code>
     */
    public int getLen()
    {
        return len;
    }

    /**
     * set length
     *
     * @param length <code>int</code> length
     */
    public void setLen(int len)
    {
        this.len = len;
    }

    /**
     * get Description
     *
     * @return desc <code>String</code>
     */
    public String getDesc()
    {
        return desc;
    }

    /**
     * set Description
     *
     * @param desc <code>String</code> description
     */
    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    /**
     * get String
     *
     * @return string <code>String</code>
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append('[');
        sb.append("name="+name);
        sb.append(',');
        sb.append("type="+type);
        sb.append(',');
        sb.append("len="+len);
        sb.append(',');
        sb.append("oid="+oid);
        sb.append(',');
        sb.append("desc="+desc);
        sb.append(']');

        return sb.toString();
    }
}
