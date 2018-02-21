package com.aimir.fep.command.ws.client;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for CmdMultiFirmwareOTA complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="cmdMultiFirmwareOTA">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;element name="OTATargetType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;element name="DeviceList" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;element name="Take_over" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;element name="UseNullBypass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;element name="FirmwareId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;element name="OptVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;element name="OptModel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;element name="OptTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdMultiFirmwareOTA", propOrder = { "otaTargetType", "deviceList", "take_over", "useNullBypass", "firmwareId", "optVersion", "optModel", "optTime" })
public class CmdMultiFirmwareOTA {
	@XmlElement(name = "OTATargetType")
	protected String otaTargetType;

	@XmlElement(name = "DeviceList")
	protected List<String> deviceList;

	@XmlElement(name = "Take_over")
	protected String take_over;

	@XmlElement(name = "UseNullBypass")
	protected boolean useNullBypass;

	@XmlElement(name = "FirmwareId")
	protected String firmwareId;

	// INSERT START SP-681
	@XmlElement(name = "OptVersion")
	protected String optVersion;
	
	@XmlElement(name = "OptModel")
	protected String optModel;
	
	@XmlElement(name = "OptTime")
	protected String optTime;
	// INSERT END SP-681
	
	/**
	 * Gets the value of the otaTargetType property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getOtaTargetType() {
		return otaTargetType;
	}

	/**
	 * Gets the value of the deviceList property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public List<String> getDeviceList() {
		if (deviceList == null) {
			deviceList = new ArrayList<String>();
		}
		return this.deviceList;
	}

	/**
	 * Gets the value of the take_over property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getTake_over() {
		return take_over;
	}

	/**
	 * Gets the value of the useNullBypass property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public boolean isUseNullBypass() {
		return useNullBypass;
	}

	/**
	 * Gets the value of the firmwareId property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getFirmwareId() {
		return firmwareId;
	}
		
	// INSERT START SP-681
	/**
	 * Gets the value of the optVersion property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getOptVersion() {
		return optVersion;
	}
	
	/**
	 * Gets the value of the optModel property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getOptModel() {
		return optModel;
	}

	/**
	 * Gets the value of the optTime property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getOptTime() {
		return optTime;
	}
	
	// INSERT END SP-681	
}
