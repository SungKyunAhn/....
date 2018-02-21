
package com.aimir.cms.ws.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.aimir.cms.ws.client package. 
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
public class ObjectFactory {

    private final static QName _SearchReq_QNAME = new QName("http://server.ws.cms.aimir.com/", "SearchReq");
    private final static QName _SearchResp_QNAME = new QName("http://server.ws.cms.aimir.com/", "SearchResp");
    private final static QName _SaveAllReq_QNAME = new QName("http://server.ws.cms.aimir.com/", "SaveAllReq");
    private final static QName _SaveAllResp_QNAME = new QName("http://server.ws.cms.aimir.com/", "SaveAllResp");
    private final static QName _DataLoadReq_QNAME = new QName("http://server.ws.cms.aimir.com/", "DataLoadReq");
    private final static QName _DataLoadResp_QNAME = new QName("http://server.ws.cms.aimir.com/", "DataLoadResp");
    private final static QName _MeterCheckReq_QNAME = new QName("http://server.ws.cms.aimir.com/", "MeterCheckReq");
    private final static QName _MeterCheckResp_QNAME = new QName("http://server.ws.cms.aimir.com/", "MeterCheckResp");    
    private final static QName _MeterImportReq_QNAME = new QName("http://server.ws.cms.aimir.com/", "MeterImportReq");
    private final static QName _MeterImportResp_QNAME = new QName("http://server.ws.cms.aimir.com/", "MeterImportResp");    
    private final static QName _GetDebtReq_QNAME = new QName("http://server.ws.cms.aimir.com/", "GetDebtReq");
    private final static QName _GetDebtResp_QNAME = new QName("http://server.ws.cms.aimir.com/", "GetDebtResp");    
    private final static QName _AddDebtReq_QNAME = new QName("http://server.ws.cms.aimir.com/", "AddDebtReq");
    private final static QName _AddDebtResp_QNAME = new QName("http://server.ws.cms.aimir.com/", "AddDebtResp");    
    private final static QName _UpdateDebtReq_QNAME = new QName("http://server.ws.cms.aimir.com/", "UpdateDebtReq");
    private final static QName _UpdateDebtResp_QNAME = new QName("http://server.ws.cms.aimir.com/", "UpdateDebtResp");
    
    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.aimir.cms.ws.client
     * 
     */
    public ObjectFactory() {
    }
    
    /**
     * Create an instance of {@link SearchReq }
     * 
     */
    public SearchReq createSearchReq() {
        return new SearchReq();
    }    
    
    /**
     * Create an instance of {@link SearchResp }
     * 
     */
    public SearchResp createSearchResp() {
        return new SearchResp();
    }

    /**
     * Create an instance of {@link SaveAllReq }
     * 
     */
    public SaveAllReq createSaveAllReq() {
        return new SaveAllReq();
    }    
    
    /**
     * Create an instance of {@link SearchResp }
     * 
     */
    public SaveAllResp createSaveAllResp() {
        return new SaveAllResp();
    }

    /**
     * Create an instance of {@link DataLoadReq }
     * 
     */
    public DataLoadReq createDataLoadReq() {
        return new DataLoadReq();
    }    
    
    /**
     * Create an instance of {@link DataLoadResp }
     * 
     */
    public DataLoadResp createDataLoadResp() {
        return new DataLoadResp();
    }
    

    /**
     * Create an instance of {@link MeterCheckReq }
     * 
     */
    public MeterCheckReq createMeterCheckReq() {
        return new MeterCheckReq();
    }    
    
    /**
     * Create an instance of {@link MeterCheckResp }
     * 
     */
    public MeterCheckResp createMeterCheckResp() {
        return new MeterCheckResp();
    }
    
    /**
     * Create an instance of {@link MeterCheckReq }
     * 
     */
    public MeterImportReq createMeterImportReq() {
        return new MeterImportReq();
    }    
    
    /**
     * Create an instance of {@link MeterImportResp }
     * 
     */
    public MeterImportResp createMeterImportResp() {
        return new MeterImportResp();
    }
    
    
    /**
     * Create an instance of {@link GetDebtReq }
     * 
     */
    public GetDebtReq createGetDebtReq() {
        return new GetDebtReq();
    }    
    
    /**
     * Create an instance of {@link GetDebtResp }
     * 
     */
    public GetDebtResp createGetDebtResp() {
        return new GetDebtResp();
    }
    
    /**
     * Create an instance of {@link AddDebtReq }
     * 
     */
    public AddDebtReq createAddDebtReq() {
        return new AddDebtReq();
    }    
    
    /**
     * Create an instance of {@link AddDebtResp }
     * 
     */
    public AddDebtResp createAddDebtResp() {
        return new AddDebtResp();
    }    
    
    /**
     * Create an instance of {@link UpdateDebtReq }
     * 
     */
    public UpdateDebtReq createUpdateDebtReq() {
        return new UpdateDebtReq();
    }    
    
    /**
     * Create an instance of {@link UpdateDebtResp }
     * 
     */
    public UpdateDebtResp createUpdateDebtResp() {
        return new UpdateDebtResp();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchReq }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.cms.aimir.com/", name = "SearchReq")
    public JAXBElement<SearchReq> createcreateSearchReq(SearchReq value) {
        return new JAXBElement<SearchReq>(_SearchReq_QNAME, SearchReq.class, null, value);
    }    
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchResp }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.cms.aimir.com/", name = "SearchResp")
    public JAXBElement<SearchResp> createSearchResp(SearchResp value) {
        return new JAXBElement<SearchResp>(_SearchResp_QNAME, SearchResp.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SaveAllReq }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.cms.aimir.com/", name = "SaveAllReq")
    public JAXBElement<SaveAllReq> createSaveAllReq(SaveAllReq value) {
        return new JAXBElement<SaveAllReq>(_SaveAllReq_QNAME, SaveAllReq.class, null, value);
    }    
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchResp }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.cms.aimir.com/", name = "SaveAllResp")
    public JAXBElement<SaveAllResp> createSaveAllResp(SaveAllResp value) {
        return new JAXBElement<SaveAllResp>(_SaveAllResp_QNAME, SaveAllResp.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataLoadReq }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.cms.aimir.com/", name = "DataLoadReq")
    public JAXBElement<DataLoadReq> createDataLoadReq(DataLoadReq value) {
        return new JAXBElement<DataLoadReq>(_DataLoadReq_QNAME, DataLoadReq.class, null, value);
    }    
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataLoadResp }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.cms.aimir.com/", name = "DataLoadResp")
    public JAXBElement<DataLoadResp> createDataLoadResp(DataLoadResp value) {
        return new JAXBElement<DataLoadResp>(_DataLoadResp_QNAME, DataLoadResp.class, null, value);
    }    

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MeterCheckReq }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.cms.aimir.com/", name = "MeterCheckReq")
    public JAXBElement<MeterCheckReq> createMeterCheckReq(MeterCheckReq value) {
        return new JAXBElement<MeterCheckReq>(_MeterCheckReq_QNAME, MeterCheckReq.class, null, value);
    }    
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MeterCheckResp }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.cms.aimir.com/", name = "MeterCheckResp")
    public JAXBElement<MeterCheckResp> createMeterCheckResp(MeterCheckResp value) {
        return new JAXBElement<MeterCheckResp>(_MeterCheckResp_QNAME, MeterCheckResp.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MeterCheckReq }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.cms.aimir.com/", name = "MeterImportReq")
    public JAXBElement<MeterImportReq> createMeterImportReq(MeterImportReq value) {
        return new JAXBElement<MeterImportReq>(_MeterImportReq_QNAME, MeterImportReq.class, null, value);
    }    
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MeterImportResp }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.cms.aimir.com/", name = "MeterImportResp")
    public JAXBElement<MeterImportResp> createMeterImportResp(MeterImportResp value) {
        return new JAXBElement<MeterImportResp>(_MeterImportResp_QNAME, MeterImportResp.class, null, value);
    }    
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDebtReq }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.cms.aimir.com/", name = "GetDebtReq")
    public JAXBElement<GetDebtReq> createGetDebtReq(GetDebtReq value) {
        return new JAXBElement<GetDebtReq>(_GetDebtReq_QNAME, GetDebtReq.class, null, value);
    }    
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDebtResp }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.cms.aimir.com/", name = "GetDebtResp")
    public JAXBElement<GetDebtResp> createGetDebtResp(GetDebtResp value) {
        return new JAXBElement<GetDebtResp>(_GetDebtResp_QNAME, GetDebtResp.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddDebtReq }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.cms.aimir.com/", name = "AddDebtReq")
    public JAXBElement<AddDebtReq> createAddDebtReq(AddDebtReq value) {
        return new JAXBElement<AddDebtReq>(_AddDebtReq_QNAME, AddDebtReq.class, null, value);
    }    
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddDebtResp }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.cms.aimir.com/", name = "AddDebtResp")
    public JAXBElement<AddDebtResp> createAddDebtResp(AddDebtResp value) {
        return new JAXBElement<AddDebtResp>(_AddDebtResp_QNAME, AddDebtResp.class, null, value);
    }    
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateDebtReq }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.cms.aimir.com/", name = "UpdateDebtReq")
    public JAXBElement<UpdateDebtReq> createUpdateDebtReq(UpdateDebtReq value) {
        return new JAXBElement<UpdateDebtReq>(_UpdateDebtReq_QNAME, UpdateDebtReq.class, null, value);
    }    
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateDebtResp }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.cms.aimir.com/", name = "UpdateDebtResp")
    public JAXBElement<UpdateDebtResp> createUpdateDebtResp(UpdateDebtResp value) {
        return new JAXBElement<UpdateDebtResp>(_UpdateDebtResp_QNAME, UpdateDebtResp.class, null, value);
    }
}
