package com.aimir.fep.command.conf;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

public class DLMSMeta {

	
    /**
     * <p>Java class for protocol.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * <p>
     * <pre>
     * &lt;simpleType name="LOAD_CONTROL_STATUS">
     *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *     &lt;enumeration value="OPEN"/>
     *     &lt;enumeration value="CLOSE"/>
     *   &lt;/restriction>
     * &lt;/simpleType>
     * </pre>
     * 
     */
    @XmlType(name = "LOAD_CONTROL_STATUS")
    @XmlEnum
    public enum LOAD_CONTROL_STATUS {
        @XmlEnumValue("OPEN")
        OPEN(0),
        @XmlEnumValue("CLOSE")
        CLOSE(1);
        
        int code;
        
        LOAD_CONTROL_STATUS(int code) {
            this.code = code;
        }
        
        public int getCode() {
            return this.code;
        }
        
        public static LOAD_CONTROL_STATUS getValue(int code) {
            for (LOAD_CONTROL_STATUS a : LOAD_CONTROL_STATUS.values()) {
                if (a.getCode() == code)
                    return a;
            }
            return null;
        }
    }
    
    /**
     * <p>Java class for protocol.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * <p>
     * <pre>
     * &lt;simpleType name="CONTROL_STATE">
     *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *     &lt;enumeration value="Disconnected"/>
     *     &lt;enumeration value="Connected"/>
     *     &lt;enumeration value="ReadyForReconnection"/>
     *   &lt;/restriction>
     * &lt;/simpleType>
     * </pre>
     * 
     */
    @XmlType(name = "CONTROL_STATE")
    @XmlEnum
    public enum CONTROL_STATE {
        @XmlEnumValue("Disconnected")
        Disconnected(0),
        @XmlEnumValue("Connected")
        Connected(1),
        @XmlEnumValue("ReadyForReconnection")
        ReadyForReconnection(2);
        
        int code;
        
        CONTROL_STATE(int code) {
            this.code = code;
        }
        
        public int getCode() {
            return this.code;
        }
        
        public static CONTROL_STATE getValue(int code) {
            for (CONTROL_STATE a : CONTROL_STATE.values()) {
                if (a.getCode() == code)
                    return a;
            }
            return null;
        }
    }
}
