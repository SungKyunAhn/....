package com.aimir.fep.util;

import com.aimir.fep.protocol.fmp.datatype.OID;

/**
 * Command MIB Node
 * 
 * @author Y.S Kim (sorimo@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class CommandMIBNode extends MIBNode
{
    private String desc = null;

    /**
     * constructor
     */
    public CommandMIBNode()
    {
        setNodeType(MIBNodeConstants.DATA);
    }
    
    /**
     * constructor
     *
     * @param name <code>String</code> name
     * @param oid <code>OID</code> oid
     * @param desc <code>OID</code> description
     */
    public CommandMIBNode(String name, OID oid, String desc)
    {
        setNodeType(MIBNodeConstants.DATA);

        this.name = name;
        this.oid = oid;
        this.desc = desc;
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
     * get string
     *
     * @return string <code>String</code>
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append('[');
        sb.append("name="+name);
        sb.append(',');
        sb.append("oid="+oid);
        sb.append(',');
        sb.append("desc="+desc);
        sb.append(']');

        return sb.toString();
    }
}
