package com.aimir.fep.util;

import com.aimir.fep.protocol.fmp.datatype.OID;

/**
 * Event MIB Node
 * 
 * @author Y.S Kim (sorimo@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class EventMIBNode extends MIBNode
{
    private String severity = null;
    private OID[] oids = null;
    private String desc = null;

    /**
     * constructor
     */
    public EventMIBNode()
    {
        setNodeType(MIBNodeConstants.DATA);
    }
    
    /**
     * constructor
     *
     * @param name <code>String</code> name
     * @param oids <code>OID[]</code> name
     * @param severity <code>String</code> name
     * @param oid <code>OID</code> name
     * @param desc <code>String</code> name
     */
    public EventMIBNode(String name, OID[] oids, String severity, 
            OID oid, String desc)
    {
        setNodeType(MIBNodeConstants.EVENT);

        this.name = name;
        this.oids = oids;
        this.severity = severity;
        this.oid = oid;
        this.desc = desc;
    }

    /**
     * get severity
     *
     * @return severity <code>String</code>
     */
    public String getSeverity()
    {
        return severity;
    }

    /**
     * set severity
     *
     * @param severity <code>String</code>
     */
    public void setSeverity(String severity)
    {
        this.severity = severity;
    }

    /**
     * get oids
     *
     * @return oids <code>OID[]</code>
     */
    public OID[] getOids()
    {
        return oids;
    }

    /**
     * set oids
     *
     * @param oids <code>OID[]</code>
     */
    public void setOids(OID[] oids)
    {
        this.oids = oids;
    }

    /**
     * get description
     *
     * @return description <code>String</code>
     */
    public String getDesc()
    {
        return desc;
    }

    /**
     * set description
     *
     * @param desc <code>String</code>
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
        sb.append("severity="+severity);
        sb.append(',');
        sb.append("oids=");
        if (oids != null)
        {
            sb.append('{');
            for (int i = 0; i < oids.length; i++)
            {
                sb.append(oids[i]);
                sb.append(',');
            } 
            sb.append('}');
        }
        sb.append(',');
        sb.append("oid="+oid);
        sb.append(',');
        sb.append("desc="+desc);
        sb.append(']');

        return sb.toString();
    }
}
