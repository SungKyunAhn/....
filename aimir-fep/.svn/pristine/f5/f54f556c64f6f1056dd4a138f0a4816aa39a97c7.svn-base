package com.aimir.fep.util;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.aimir.fep.protocol.fmp.datatype.OID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Command MIB Node
 * 
 * @author Y.S Kim (sorimo@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class MIBUtil
{
    private static Log log = LogFactory.getLog(MIBUtil.class);
    private String dfname = "/mibs/data.mib";
    private String cfname = "/mibs/command.mib";
    private String efname = "/mibs/event.mib";

    private static MIBUtil mibutil = null;
    private static Hashtable<String, MIBNode> oidH = new Hashtable<String, MIBNode>();
    private static Hashtable<String, MIBNode> nameH = new Hashtable<String, MIBNode>();
    private static Hashtable<DataMIBNode, Vector<DataMIBNode>> dataMibs = new Hashtable<DataMIBNode, Vector<DataMIBNode>>();

    private static Map<String, NameSpaceMIBUtil> nsmibutil = new HashMap<String, NameSpaceMIBUtil>();
    private String ns = null;
    
    /**
     *  constructor
     */
    private MIBUtil(String ns)
    {
        this.ns = ns;
        
        // log.info("NameSpace[" + this.ns + "]");
        if (mibutil == null)
            init();
        
        if (this.ns != null && nsmibutil.get(this.ns) == null) {
            nsmibutil.put(this.ns, new NameSpaceMIBUtil(this.ns));
        }
    }

    public synchronized static MIBUtil getInstance()
    {
        return getInstance(null);
    }
    
    /**
     *  get MIBUtil Instance
     *
     * @return mibutil <code>MIBUtil</code>
     */
    public synchronized static MIBUtil getInstance(String ns)
    {
        mibutil = new MIBUtil(ns);
        return mibutil;
    }

    private void init()
    {
        loadDataMIB();
        loadCommandMIB();
        loadEventMIB();
    }

    private void loadDataMIB()
    {
        String line = null;
        BufferedReader file = null;
        String[] items = null;
        MIBNode md = null;
        Vector<MIBNode> mds = new Vector<MIBNode>();
        OID oid = null;
        try
        {
            log.debug("data mib file ready ["+dfname+"]");
            //file = new BufferedReader(new FileReader(dfname));
            file = new BufferedReader(new InputStreamReader(
                        MIBUtil.class.getResourceAsStream(dfname), "EUC-KR"));
            log.debug("data mib file open ["+dfname+"]");
            while ((line = file.readLine()) != null)
            {
                items = line.split(",");
                if (items == null || items.length != 5)
                {
                    log.info("data mib file format is incorrect... ["+line+"]");
                    return;
                }
                oid = new OID(makeThreeCiphers(items[3]));
                md = new DataMIBNode(items[0], DataUtil.convType(items[1]), items[2], oid, items[4]);
                oidH.put(md.getOid().getValue(), (MIBNode) md);
                nameH.put(md.getName(), (MIBNode) md);
                mds.add(md);
            }

            Collections.sort(mds, new KeyComparator());
            file.close();

            //log.debug("mds ["+mds+"]");

            makeDataMIBs(mds);
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

    private void loadCommandMIB()
    {
        String line = null;
        BufferedReader file = null;
        String[] items = null;
        MIBNode md = null;
        OID oid = null;
        try
        {
            log.debug("command mib file ready ["+cfname+"]");
            //file = new BufferedReader(new FileReader(cfname));
            file = new BufferedReader(new InputStreamReader(
                        MIBUtil.class.getResourceAsStream(cfname), "EUC-KR"));
            log.debug("command mib file open ["+cfname+"]");
            while ((line = file.readLine()) != null)
            {
                items = line.split(",");
                if (items == null || items.length != 3)
                {
                    log.info("command mib file format is incorrect... ["+line+"]");
                    return;
                }
                oid = new OID(makeThreeCiphers(items[1]));
                md = new CommandMIBNode(items[0], oid, items[2]);
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

    private void loadEventMIB()
    {
        String line = null;
        BufferedReader file = null;
        String[] items = null;
        MIBNode md = null;
        OID oid = null;
        OID[] oids = null;
        String[] tmpoids = null;
        try
        {
            log.debug("event mib file ready ["+efname+"]");
          //file = new BufferedReader(new FileReader(efname));
            file = new BufferedReader(new InputStreamReader(
                        MIBUtil.class.getResourceAsStream(efname), "EUC-KR"));
            log.debug("event mib file open ["+efname+"]");
            
            while ((line = file.readLine()) != null)
            {
                items = line.split(",");
                if (items == null || items.length != 5)
                {
                    log.info("event mib file format is incorrect... ["+line+"]");
                    return;
                }
                oid = new OID(makeThreeCiphers(items[3]));

                if (items[1] != null && !items[1].equals(""))
                {
                    tmpoids = items[1].split("\\|");
                    oids = new OID[tmpoids.length];

                    for (int i = 0; i < tmpoids.length; i++)
                    {
                        oids[i] = new OID(makeThreeCiphers(tmpoids[i]));
                    }
                }
                else
                    oids = null;
                md = new EventMIBNode(items[0], oids, items[2], oid, items[4]);
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
    private void makeDataMIBs(Collection<MIBNode> col)
    {
        DataMIBNode md = null;
        DataMIBNode key_md = null;
        Vector<DataMIBNode> mds = null;
        //String[] ids = null;
        String oidValue = null;
        String lastIndex = null;

        for(Iterator<MIBNode> it = col.iterator(); it.hasNext(); )
        {
            md = (DataMIBNode) it.next();

            oidValue = ((OID) md.getOid()).getValue();
            lastIndex = 
                oidValue.substring(oidValue.lastIndexOf(".")+1, 
                        oidValue.length());

            //ids = ((OID) md.getOid()).getValue().split("\\.");

            //log.debug("oid value : "+((OID) md.getOid()).getValue());

            if (lastIndex != null && !lastIndex.equals(""))
            {
                if ("0".equals(lastIndex))
                {
                    if (key_md != null)
                    {
                        dataMibs.put(key_md, mds);
                    }
    
                    key_md = md;
                    mds = new Vector<DataMIBNode>();
                }
                else
                {
                    mds.add(md);
                }
            }
        }

        if (key_md != null)
        {
            dataMibs.put(key_md, mds);
        }
    }

    /**
     *  generate Command Proxy Java code
    public void makeCommandProxyCode()
    {
        try
        {
            CreateClassCode c = CreateClassCode.getInstance();
            c.createCommandProxyCode();
        }
        catch (Exception e)
        {
            log.error(e);
        }
    }
    */

    /**
     *  get Data MIBs
     *
     * @param oid <code>String</code> oid
     * @return data mibs <code>Collection</code>
     */
    public Collection<?> getDataMIBsByOid(String oid)
    {
        MIBNode md = getMIBNodeByOid(oid);

        return (Vector<?>) dataMibs.get(md);
    }

    /**
     *  get Data MIBs
     *
     * @param name <code>String</code> name
     * @return data mibs <code>Collection</code>
     */
    public Collection<?> getDataMIBsByName(String name)
    {
        MIBNode md = getMIBNodeByName(name);

        return (Vector<?>) dataMibs.get(md);
    }

    /**
     *  get MIBNode
     *
     * @param oid <code>String</code> oid
     * @return mib node <code>MIBNode</code>
     */
    public MIBNode getMIBNodeByOid(String oid)
    {
    	if (ns != null && !"null".equals(ns) && !"".equals(ns))
    		return (MIBNode) nsmibutil.get(ns).getMIBNodeByOid(oid);
    	else
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
    	if (ns != null && !"null".equals(ns) && !"".equals(ns))
			return (MIBNode) nsmibutil.get(ns).getMIBNodeByName(name);
    	else
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
        if (ns != null && !"null".equals(ns) && !"".equals(ns))
            return nsmibutil.get(ns).getOid(name);
        else {
            MIBNode md = (MIBNode) nameH.get(name);
            
            if (md == null)
            {
                return null;
            }
            return (OID) md.getOid();
        }
    }

    /**
     *  get name
     *
     * @param oid <code>String</code> oid
     * @return name <code>String</code>
     */
    public String getName(String oid)
    {
        if (ns != null && !"null".equals(ns) && !"".equals(ns))
            return nsmibutil.get(ns).getName(oid);
        else {
            MIBNode md = (MIBNode) oidH.get(oid);
            if (md == null)
            {
                return null;
            }
            return (String) md.getName();
        }
    }

    private String makeThreeCiphers(String oid)
    {
        String[] str = oid.split("\\.");

        if (str.length < 3 && str.length > 0)
        {
            return makeThreeCiphers(oid+".0");
        }
        else if (str.length == 3)
        {
            return oid;
        }
        else if (str.length > 3)
        {
            return str[0]+"."+str[1]+"."+str[2];
        }
        else
        {
            return "0.0.0";
        }
    }

    public Hashtable<String, MIBNode> getOidHash()
    {
        if (ns != null && !"null".equals(ns) && !"".equals(ns))
            return nsmibutil.get(ns).getOidHash();
        else
            return oidH;
    }

    public Hashtable<String, MIBNode> getNameHash()
    {
        if (ns != null && !"null".equals(ns) && !"".equals(ns))
            return nsmibutil.get(ns).getNameHash();
        else
            return nameH;
    }
}
