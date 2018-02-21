package com.aimir.fep.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.datatype.OID;

/**
 * 5자리 이상의 긴 SNMP MIB를 처리하기 위한 MIB Util
 * asn.1로 작성된 mib의 parser가 아닙니다
 * @author HanSejin
 */
public class SnmpMibUtil {
    private static Log log = LogFactory.getLog(SnmpMibUtil.class);
    private String dir = "/mibs/";
    private String efname = "/event.mib";

    private static SnmpMibUtil snmputil = null;
    private static Hashtable<String, MIBNode> oidH = new Hashtable<String, MIBNode>();
    private static Hashtable<String, MIBNode> nameH = new Hashtable<String, MIBNode>();


    /**
     *  constructor
     */
    private SnmpMibUtil(String nameSpace)
    {
        efname = dir+nameSpace+"/event.mib";
        init();
    }

    public synchronized static SnmpMibUtil getInstance(String ns)
    {
        snmputil = new SnmpMibUtil(ns);
        return snmputil;
    }

    private void init()
    {
        loadEventMIB();
    }

    private void loadEventMIB()
    {
        String line = null;
        BufferedReader file = null;
        String[] items = null;
        MIBNode md = null;
        OID oid = null;
        try
        {
            log.debug("SNMP mib file ready ["+efname+"]");
            //file = new BufferedReader(new FileReader(efname));
            file = new BufferedReader(new InputStreamReader(
                    NameSpaceMIBUtil.class.getResourceAsStream(efname), "EUC-KR"));
            log.debug("SNMP mib file open ["+efname+"]");

            while ((line = file.readLine()) != null)
            {
                items = line.split(",");
                if (items == null || items.length != 3)
                {
                    log.info("snmp mib file format is incorrect... ["+line+"]");
                    return;
                }
                oid = new OID((items[1].trim()));
                // 동일한 형태로 작성되는 command mib를 사용합니다(name,oid,desc)
                md = new CommandMIBNode(items[0].trim(), oid, items[2].trim());
                // oid와 name을 각각 key로 지정한 해시테이블
                oidH.put(md.getOid().getValue(), (MIBNode) md);
                nameH.put(md.getName(), (MIBNode) md);
            }
            file.close();

        }
        catch (Exception e)
        {
            log.error(e);
        }
        finally
        {
            try
            {
                if (file != null)
                {
                    file.close();
                }
            }
            catch (Exception e)
            {
                log.error(e);
            }
        }
    }


    /**
     *  get MIBNode
     *
     * @param oid <code>String</code> oid
     * @return mib node <code>MIBNode</code>
     */
    public MIBNode getMIBNodeByOid(String oid)
    {
        return (MIBNode) oidH.get(oid);
    }

    /**
     *  get MIBNode
     *
     * @param name <code>String</code> name
     * @return mib node <code>MIBNode</code>
     */
    public MIBNode getMIBNodeByName(String name)
    {
        return (MIBNode) nameH.get(name);
    }

    /**
     *  get OID
     *
     * @param name <code>String</code> name
     * @return oid <code>OID</code>
     */
    public OID getOid(String name)
    {
        MIBNode md = (MIBNode) nameH.get(name);
        if (md == null)
        {
            return null;
        }
        return (OID) md.getOid();
    }

    /**
     *  get name
     *
     * @param oid <code>String</code> oid
     * @return name <code>String</code>
     */
    public String getName(String oid)
    {
        MIBNode md = (MIBNode) oidH.get(oid);
        if (md == null)
        {
            return null;
        }
        return (String) md.getName();
    }

    public Hashtable<String, MIBNode> getOidHash()
    {
        return oidH;
    }

    public Hashtable<String, MIBNode> getNameHash()
    {
        return nameH;
    }

}
