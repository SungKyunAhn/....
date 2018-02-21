package com.aimir.fep.command.conf;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Hashtable;

import java.net.URL;

import org.apache.xerces.parsers.DOMParser;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.InputSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Default Configuration
 * 
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class DefaultConf
{                                         
    private static DefaultConf defaultConf = null;

    private static Log log = LogFactory.getLog(DefaultConf.class);

    private static Document doc = null;

    private DefaultConf()
    {
        loadDefaultConf();
    }

    public static DefaultConf getInstance()
    {
        if (defaultConf == null)
        {
            defaultConf = new DefaultConf();
        }
        return defaultConf;
    }

    /**
     * Initialize
     *
     */
    public static void loadDefaultConf()
    {
        String filename = "/config/default.conf.xml";
        URL url = DefaultConf.class.getResource(filename);
        log.debug(DefaultConf.class.getResource(filename).getPath());
        if (url == null)
        {
            log.error("can not find command.conf.default file["+
                    filename+"]");
            return;
        }

        try
        {
            doc = parseURL(url);
        }
        catch (Exception e)
        {
            log.error("load default configuration file exception : "+e);
        }
    }

    /**
     * Xpath Document element node
     *
     * @return node <code>Node</node> element node
     * @param xQuery <code>String</code> Xpath
     * @throws <code>Exception</code> 
    */
    @SuppressWarnings("unused")
    private Node selectNode(String xQuery) 
        throws Exception 
    {
        if(doc == null)
        {
            log.debug("document is null");
            return null;
        }

        XPath xpath = XPathFactory.newInstance().newXPath();
        Node n = null;
        NodeList nodeList = (NodeList)xpath.evaluate(xQuery, doc,XPathConstants.NODESET);

        for(int i = 0; i < nodeList.getLength() && (n=nodeList.item(i)) != null; i++){
            short type = n.getNodeType();
            if(type == Node.ELEMENT_NODE)
            break;
        }

        return n;
    }

    /**
     * Xpath Document element
     *
     * @return nodes <code>Node</node> element node
     * @param xQuery <code>String</code> Xpath
     * @throws <code>Exception</code>
    */
    private Node[] selectNodes(String xQuery) 
        throws Exception 
    {
        if(doc == null)
        {
            log.debug("document is null");
            return null;
        }

        Collection<Node> nodes = new ArrayList<Node>();

        XPath xpath = XPathFactory.newInstance().newXPath();
        Node n = null;
        NodeList nodeList = (NodeList)xpath.evaluate(xQuery, doc,XPathConstants.NODESET);

        for(int i = 0; i < nodeList.getLength(); i++)
        {
            n = nodeList.item(i);
            short type = n.getNodeType();
            if (type == Node.ELEMENT_NODE)
            {
                nodes.add(n);
            }
        }

        return nodes.toArray(new Node[0]);
    }

    private static Document parseURL(URL url) throws Exception 
    {

        DOMParser parser = new DOMParser();
        parser.setFeature("http://xml.org/sax/features/validation", true);
        parser.parse(new InputSource(url.openStream()));
        Document d = parser.getDocument();

        return d;
    }

    private String getNodeValue(Node node)
    {
        NodeList leafNodes = node.getChildNodes();
        String nodeValue = null;
        for (int i = 0; i < leafNodes.getLength(); i++)
        {
            Node leaf = leafNodes.item(i);
            if (leaf.getNodeType() == Node.TEXT_NODE)
            {
                nodeValue = leaf.getNodeValue();
                if (nodeValue != null)
                {
                    nodeValue = nodeValue.trim();
                }
                return nodeValue;
            }
        }
        return nodeValue;
    }

    private String getClassName(Node node)
    {
        NamedNodeMap attributes = node.getAttributes();

        for (int i = 0; i < attributes.getLength(); i++)
        {
            Node current = attributes.item(i);
            if (current.getNodeName().toLowerCase().equals("class"))
            {
                return current.getNodeValue();
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public Hashtable getDefaultProperties(String className)
        throws Exception
    {
        Node[] nodes = selectNodes("//configuration");

        if(nodes==null)
            return null;
        
        String _className = null;
        
        Hashtable propList = null;
        log.debug("NodeList Length"+nodes.length);
        for (int i = 0; i < nodes.length; i++)
        {
            _className = getClassName(nodes[i]);
            
            if (className.equals(_className))
            {
                propList = getProperties(nodes[i]);
            }
        }
        
        return propList;
    }

    private Hashtable getProperties(Node node) throws Exception
    {
        String className = getClassName(node);
        NodeList childs = node.getChildNodes();

        if (doc == null)
        {
            log.debug("default configuration file is not load");
            return null;
        }

        String propName = null;
        String propType = null;
        String propValue = null;
        Hashtable<String, String> propList = new Hashtable<String, String>();
        
        for (int i = 0; i < childs.getLength(); i++)
        {
            Node child = childs.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE)
            {
                if (child.getNodeName().equals("property"))
                {
                    NamedNodeMap attributes = child.getAttributes();
                    propValue = getNodeValue(child);                    
                    for (int j = 0; j < attributes.getLength(); j++)
                    {
                        Node current = attributes.item(j);
                        if (current.getNodeName().equals("name"))
                        {
                            propName = current.getNodeValue();                            
                        }
                        else if (current.getNodeName().equals("type"))
                        {
                            propType = current.getNodeValue();                            
                        }
                        propList.put(propName, (propValue!=null ? propValue:""));
                    }
                }
            }
        }
        
        return propList;
    }
}
