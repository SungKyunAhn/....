package com.aimir.fep.meter.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.meter.parser.MeterDataParser;

/**
 * Energy Measurement Data Class
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 * <pre>
 * &lt;complexType name="meterData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="map">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="entry" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *                             &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="meterId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serviceType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="time" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="vendor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "meterData", propOrder = {
    "map",
    "meterId",
    "serviceType",
    "time",
    "type",
    "vendor"
})
public class MeterData implements java.io.Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -1864000508600404379L;
	private String meterId = null;
    private String type = null;
    private String vendor = null;
    private String serviceType = null;
    private String time = null;
    @XmlTransient
    private MeterDataParser parser = null;
    @XmlElement(required = true)
    private Map map = null;

    /**
     * Constructor
     */
    public MeterData()
    {
        map = new Map();
    }

    /**
     * Constructor
     *
     * @param meterId - meter identifier
     * @param time -  time(yymmddhhmmss)
     * @param type -  meter data type()
     * @param parser -  meter data Parser
     */
    public MeterData(String meterId, String type, String vendor, 
            String serviceType, String time, MeterDataParser parser)
    {
        this.meterId = meterId;
        this.type = type;
        this.vendor = vendor;
        this.serviceType = serviceType;
        this.time = time;
        this.parser = parser;
        map = new Map();
    }

    /**
     * get Sensor Id
     * @return meterId - meter identifier(default : meter number) 
     */
    public String getMeterId()
    {
        return this.meterId;
    }
    /**
     * set Sensor Id
     * @param meterId - meter identifier(default : meter number) 
     */
    public void setMeterId(String meterId)
    {
        this.meterId = meterId;
    }

    /**
     * get time
     * @return time - meter measurement time(mcu time : yymmddhhmmss)
     */
    public String getTime()
    {
        return this.time;
    }
    /**
     * set time
     * @param time - meter measurement time(mcu time : yymmddhhmmss)
     */
    public void setTime(String time)
    {
        this.time = time;
    }
    
    /**
     * get Meter Data Type
     * @return type - meter data type
     */
    public String getType()
    {
        return this.type;
    }
    /**
     * set type
     * @param type - meter data type
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * get Meter Vendor
     * @return vendor - meter vendor
     */
    public String getVendor()
    {
        return this.vendor;
    }
    /**
     * set vendor
     * @param vendor - meter vendor
     */
    public void setVendor(String vendor)
    {
        this.vendor = vendor;
    }

    /**
     * get Meter Service Type
     * @return serviceType - meter serviceType
     */
    public String getServiceType()
    {
        return this.serviceType;
    }
    /**
     * set serviceType
     * @param serviceType - meter serviceType
     */
    public void setServiceType(String serviceType)
    {
        this.serviceType = serviceType;
    }

    /**
     * get parser 
     * @return parser- Meter Data Parser
     */
    @XmlTransient
    public MeterDataParser getParser()
    {
        return this.parser;
    }

    /**
     * set parser
     * @param parser - Meter Data Parser
     */
    public void setParser(MeterDataParser parser)
    {
        this.parser = parser;
        java.util.Map _map = this.parser.getData();
        for (Iterator i = _map.keySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = new Map.Entry();
            entry.setKey(i.next());
            entry.setValue(_map.get(entry.getKey()));
            this.map.getEntry().add(entry);
        }
    }

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}
	
    @Override
    public String toString() {
        return "MeterData [meterId=" + meterId + ", type=" + type + ", vendor="
                + vendor + ", serviceType=" + serviceType + ", time=" + time
                + ", parser=" + parser + ", map=" + map + "]";
    }
    
    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="entry" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
     *                   &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "entry"
    })
    public static class Map {

        protected List<MeterData.Map.Entry> entry;

        /**
         * Gets the value of the entry property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the entry property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEntry().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link MeterData.Map.Entry }
         * 
         * 
         */
        public List<MeterData.Map.Entry> getEntry() {
            if (entry == null) {
                entry = new ArrayList<MeterData.Map.Entry>();
            }
            return this.entry;
        }

        @Override
        public String toString() {
            return "Map [entry=" + entry + "]";
        }

        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
         *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "key",
            "value"
        })
        public static class Entry {

            protected Object key;
            protected Object value;

            /**
             * Gets the value of the key property.
             * 
             * @return
             *     possible object is
             *     {@link Object }
             *     
             */
            public Object getKey() {
                return key;
            }

            /**
             * Sets the value of the key property.
             * 
             * @param value
             *     allowed object is
             *     {@link Object }
             *     
             */
            public void setKey(Object value) {
                this.key = value;
            }

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link Object }
             *     
             */
            public Object getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link Object }
             *     
             */
            public void setValue(Object value) {
                this.value = value;
            }

            @Override
            public String toString() {
                return "Entry [key=" + key + ", value=" + value + "]";
            }

        }
    }
}
