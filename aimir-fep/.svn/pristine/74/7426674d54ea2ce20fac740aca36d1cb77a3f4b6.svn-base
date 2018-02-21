
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="OK"/>
 *     &lt;enumeration value="SNS"/>
 *     &lt;enumeration value="ISSS"/>
 *     &lt;enumeration value="IAR"/>
 *     &lt;enumeration value="ISC"/>
 *     &lt;enumeration value="ONP"/>
 *     &lt;enumeration value="BSY"/>
 *     &lt;enumeration value="ERR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "type")
@XmlEnum
public enum Type {

    OK,
    SNS,
    ISSS,
    IAR,
    ISC,
    ONP,
    BSY,
    ERR;

    public String value() {
        return name();
    }

    public static Type fromValue(String v) {
        return valueOf(v);
    }

}
