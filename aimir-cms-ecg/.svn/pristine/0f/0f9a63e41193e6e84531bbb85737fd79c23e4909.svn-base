package com.aimir.cms.constants;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

public class CMSConstants {

    /**
     * <p>Java class for searchType
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * <p>
     * <pre>
     * &lt;simpleType name="SearchType">
     *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *     &lt;enumeration value="EXACT"/>
     *     &lt;enumeration value="LIKE"/>
     *   &lt;/restriction>
     * &lt;/simpleType>
     * </pre>
     * 
     */
    @XmlType(name = "SearchType")
    @XmlEnum
	public enum SearchType {
        @XmlEnumValue("EXACT")
		EXACT("EXACT"),
        @XmlEnumValue("LIKE")
		LIKE("LIKE");
		
		private String value;

		SearchType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
		
        public static SearchType getSearchType(String searchType) {
            for (SearchType type : SearchType.values()) {
                if (type.getValue().equals(searchType))
                    return type;
            }

            return null;
        }
	}    
    
    public enum DebtType {
    	Type1("VA120");
    	
		private String value;

		DebtType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
		
        public static DebtType getDebtType(String debtType) {
            for (DebtType type : DebtType.values()) {
                if (type.getValue().equals(debtType))
                    return type;
            }

            return null;
        }
    }
	
	
	public enum ErrorType {
		NoError(0),
		Error(-1);
		
		private Integer value;
		
		ErrorType(int value){
			this.value = value;
		}
		
		public Integer getValue() {
			return value;
		}
        
        public int getIntValue() {
            return this.value.intValue();
        }
        
        public static ErrorType getErrorType(int code) {
            for (ErrorType type : ErrorType.values()) {
                if (type.getIntValue() == code)
                    return type;
            }

            return ErrorType.NoError;
        }
	}
	
	public enum CO_SISTEMA {
		Smartcash("SG101"),
		Electrocash("SG102"),
		Aimir("SG103"),
		PNS("SG104");
		
		private String value;

		CO_SISTEMA(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
		
        public static CO_SISTEMA getCO_SISTEMA(String searchType) {
            for (CO_SISTEMA type : CO_SISTEMA.values()) {
                if (type.getValue().equals(searchType))
                    return type;
            }

            return CO_SISTEMA.Aimir;
        }
	}
}
