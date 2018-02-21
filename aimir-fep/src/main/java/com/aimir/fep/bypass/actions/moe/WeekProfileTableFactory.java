//
// 이 파일은 JAXB(JavaTM Architecture for XML Binding) 참조 구현 2.2.8-b130911.1802 버전을 통해 생성되었습니다. 
// <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>를 참조하십시오. 
// 이 파일을 수정하면 소스 스키마를 재컴파일할 때 수정 사항이 손실됩니다. 
// 생성 날짜: 2016.05.11 시간 05:36:04 PM KST 
//


package com.aimir.fep.bypass.actions.moe;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the data package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class WeekProfileTableFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: data
     * 
     */
    public WeekProfileTableFactory() {
    }

    /**
     * Create an instance of {@link WeekProfileTable }
     * 
     */
    public WeekProfileTable createCaptureObjects() {
        return new WeekProfileTable();
    }

    /**
     * Create an instance of {@link WeekProfileTable.RecordList }
     * 
     */
    public WeekProfileTable.RecordList createCaptureObjectsRecordList() {
        return new WeekProfileTable.RecordList();
    }

}
