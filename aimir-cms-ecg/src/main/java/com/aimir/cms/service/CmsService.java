package com.aimir.cms.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.cms.constants.CMSConstants.ErrorType;
import com.aimir.cms.constants.CMSConstants.SearchType;
import com.aimir.cms.dao.CustEntDao;
import com.aimir.cms.dao.DebtEntDao;
import com.aimir.cms.dao.MeterEntDao;
import com.aimir.cms.dao.ServPointDao;
import com.aimir.cms.dao.TariffEntDao;
import com.aimir.cms.exception.CMSException;
import com.aimir.cms.model.AuthCred;
import com.aimir.cms.model.CMSEnt;
import com.aimir.cms.model.CustEnt;
import com.aimir.cms.model.DebtEnt;
import com.aimir.cms.model.MeterEnt;
import com.aimir.cms.model.ServPoint;
import com.aimir.cms.model.TariffEnt;
import com.aimir.cms.util.CMSProperty;
import com.aimir.cms.validator.AddDebtReqParameterValidator;
import com.aimir.cms.validator.DataLoadReqParameterValidator;
import com.aimir.cms.validator.GetDebtReqParameterValidator;
import com.aimir.cms.validator.MeterCheckReqParameterValidator;
import com.aimir.cms.validator.MeterImportReqParameterValidator;
import com.aimir.cms.validator.SaveAllReqParameterValidator;
import com.aimir.cms.validator.UpdateDebtReqParameterValidator;
import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.dao.device.EnergyMeterDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractChangeLogDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Customer;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.TariffType;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;

@Service
@Transactional(value = "transactionManager")
public class CmsService {

    private static Log log = LogFactory.getLog(CmsService.class);
    
    @Autowired
    protected MeterDao meterDao;
    
    @Autowired
    protected CustomerDao customerDao;

    @Autowired
    protected ContractDao contractDao;

    @Autowired
    protected ContractChangeLogDao contractChangeLogDao;
    
    @Autowired
    protected DeviceModelDao deviceModelDao;
    
    @Autowired
    protected EnergyMeterDao energyMeterDao;

    @Autowired
    protected SupplierDao supplierDao;
    
    @Autowired
    protected TariffTypeDao tariffTypeDao;

    @Autowired
    protected PrepaymentLogDao prepaymentLogDao;

    @Autowired
    protected CodeDao codeDao;
    
    @Autowired
    protected CustEntDao custEntDao;
    
    @Autowired
    protected ServPointDao servPointDao;
    
    @Autowired
    protected DebtEntDao debtEntDao;
    
    @Autowired
    protected MeterEntDao meterEntDao;
    
    @Autowired
    protected TariffEntDao tariffEntDao;
    
    @Autowired
    CmsUserAuthentication cmsAuth;
    
    @Autowired
    LocationDao locDao;
    
    public List<CMSEnt> search( AuthCred authCred, String searchType, CMSEnt cmsEnt)
                     throws com.aimir.cms.exception.CMSException {

        cmsAuth.userAuthentication(authCred);
        // SearchReqParameterValidator.validator(authCred, searchType, cmsEnt);
        
        Restriction restriction = Restriction.EQ;

        if(SearchType.getSearchType(searchType).equals(SearchType.EXACT)){
            restriction = Restriction.EQ;
        }
        else if(SearchType.getSearchType(searchType).equals(SearchType.LIKE)){
            restriction = Restriction.LIKE;
        }
        
        List<CMSEnt> response = new ArrayList<CMSEnt>();
        
        if(cmsEnt.getCustomer() != null && cmsEnt.getCustomer().getCustomerId() != null 
                && !"".equals(cmsEnt.getCustomer().getCustomerId())) {
            String customerNo = cmsEnt.getCustomer().getCustomerId();
            Set<Condition> condition = new HashSet<Condition>();
            condition.add(new Condition("customerNo", new Object[]{customerNo}, null, restriction));
            List<Customer> customers = customerDao.findByConditions(condition);
            
            if(customers == null || customers.size() == 0){
                throw new CMSException(ErrorType.Error.getIntValue(), "Customer Information is empty in Prepayment System");
            }
            
            response.add(cmsEnt);
        }
        else if (cmsEnt.getSerivcePoint() != null && cmsEnt.getSerivcePoint().getServPointId() != null 
                && !"".equals(cmsEnt.getSerivcePoint().getServPointId())) {
            String servicePointId = cmsEnt.getSerivcePoint().getServPointId();
            Set<Condition> condition = new HashSet<Condition>();
            condition.add(new Condition("servicePointId", new Object[]{servicePointId}, null, restriction));
            List<Contract> contract = contractDao.findByConditions(condition);
            
            if(contract == null || contract.size() == 0){
                throw new CMSException(ErrorType.Error.getIntValue(), "Service Point Information is empty in Prepayment System");
            }
            response.add(cmsEnt);
        }
        return response;
    }
    
    private Location getLocation(String geocode) {
        Location loc = locDao.findByCondition("geocode", geocode);
        if (loc == null) {
            loc = locDao.findByCondition("geocode", geocode.substring(0, 2));
            
            if (loc == null)
                loc = locDao.findByCondition("name", "Accra");
            if (loc == null)
                loc = locDao.getAll().get(0);
        }
        return loc;
    }
    
    private void addCustomer(CMSEnt cmsEnt) throws CMSException {
        List<Customer> customers = customerDao.getCustomersByCustomerNo(new String[]{cmsEnt.getCustomer().getCustomerId()});
        
        if (customers.size() == 1) return;
        
        Customer customer = new Customer();
        customer.setCustomerNo(cmsEnt.getCustomer().getCustomerId());
        customer.setName(cmsEnt.getCustomer().getOtherNames() + " " + cmsEnt.getCustomer().getSurname());
        customer.setAliasName(cmsEnt.getCustomer().getOtherNames());
        customer.setAddress1(cmsEnt.getCustomer().getAddress1());
        customer.setAddress2(cmsEnt.getCustomer().getAddress2());
        customer.setAddress3(cmsEnt.getCustomer().getAddress3());
        customer.setEmail(cmsEnt.getCustomer().getEmail());
        customer.setTelNo(cmsEnt.getCustomer().getTelephone3());
        customer.setWorkTelephone(cmsEnt.getCustomer().getTelephone2());
        customer.setMobileNo(cmsEnt.getCustomer().getTelephone1());
        customer.setVatNo(cmsEnt.getCustomer().getTaxRefNo());
        Supplier supplier = supplierDao.getSupplierByName(CMSProperty.getProperty("default.supplier.name"));
        customer.setSupplier(supplier);
        customer.setSupplierId(supplier.getId());
        customer.setPucNumber(cmsEnt.getCustomer().getIdNo());
        customer.setIdentityOrCompanyRegNo(cmsEnt.getCustomer().getIdType());
        customer.setTelephoneNo(cmsEnt.getCustomer().getFax());
        customer.setSmsYn(1);
        customer.setLocation(getLocation("Accra"));
        
        
        //customer.setLocation(defaultLocation);
        if(!cmsEnt.getCustomer().isExist()){
            CustEnt _logCustEnt = cmsEnt.getCustomer();
            _logCustEnt.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat(""));
            custEntDao.saveOrUpdate(_logCustEnt);
            customerDao.add(customer); 
        }
    }
    
    private void modCustomer(CMSEnt cmsEnt) throws CMSException {
        List<Customer> customers = customerDao.getCustomersByCustomerNo(new String[]{cmsEnt.getCustomer().getCustomerId()});
        if(customers != null && customers.size() == 1){
            Customer customer = customers.get(0);
            customer.setCustomerNo(cmsEnt.getCustomer().getCustomerId());
            customer.setName(cmsEnt.getCustomer().getOtherNames() + " " + cmsEnt.getCustomer().getSurname());
            customer.setAddress1(cmsEnt.getCustomer().getAddress1());
            customer.setAddress2(cmsEnt.getCustomer().getAddress2());
            customer.setAddress3(cmsEnt.getCustomer().getAddress3());
            customer.setEmail(cmsEnt.getCustomer().getEmail());
            customer.setTelNo(cmsEnt.getCustomer().getTelephone3());
            customer.setWorkTelephone(cmsEnt.getCustomer().getTelephone2());
            customer.setMobileNo(cmsEnt.getCustomer().getTelephone1());
            customer.setVatNo(cmsEnt.getCustomer().getTaxRefNo());              
            customer.setPucNumber(cmsEnt.getCustomer().getIdNo());
            customer.setSmsYn(1);
            customer.setLocation(getLocation("Accra"));
            customerDao.update(customer);
            
            CustEnt custEnt = cmsEnt.getCustomer();
            custEnt.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat(""));
            
            if(cmsEnt.getCustomer().isExist()) {
                CustEnt _custEnt = custEntDao.findByCondition("customerId", custEnt.getCustomerId());
                if (_custEnt != null) {
                    _custEnt.setAddress1(custEnt.getAddress1());
                    _custEnt.setAddress2(custEnt.getAddress2());
                    _custEnt.setAddress3(custEnt.getAddress3());
                    _custEnt.setEmail(custEnt.getEmail());
                    _custEnt.setExist(custEnt.isExist());
                    _custEnt.setFax(custEnt.getFax());
                    _custEnt.setIdNo(custEnt.getIdNo());
                    _custEnt.setIdType(custEnt.getIdType());
                    _custEnt.setOtherNames(custEnt.getOtherNames());
                    _custEnt.setSurname(custEnt.getSurname());
                    _custEnt.setTaxRefNo(custEnt.getTaxRefNo());
                    _custEnt.setTelephone1(custEnt.getTelephone1());
                    _custEnt.setTelephone2(custEnt.getTelephone2());
                    _custEnt.setTelephone3(custEnt.getTelephone3());
                    
                    custEntDao.update(_custEnt);
                }
            }
        }
    }
    
    private void addServPoint(CMSEnt cmsEnt) throws CMSException {
        ServPoint servPoint = cmsEnt.getSerivcePoint();
        String servicePointId = servPoint.getServPointId();
        Set<Condition> condition = new HashSet<Condition>();
        condition.add(new Condition("servicePointId", new Object[]{servicePointId}, null, Restriction.EQ));
        List<Contract> contracts = contractDao.findByConditions(condition);
        
        if(contracts.size() == 0 && !servPoint.isExist()){
            // GEO-Code가 있는지 확인한다. 있으면 중복 에러 발생
            String geocode = servPoint.getGeoCode().replaceAll("-", "");
            condition = new HashSet<Condition>();
            condition.add(new Condition("contractNumber", new Object[]{"%"+geocode+"%"}, null, Restriction.LIKE));
            contracts = contractDao.findByConditions(condition);
            
            if (contracts != null && contracts.size() > 0) {
                geocode += contracts.size();
                // throw new CMSException(ErrorType.Error.getIntValue(), "Geo-code provided already in the system");
            }
            
            Contract contract = new Contract();
            contract.setContractNumber(geocode);
            Code serviceTypeCode = codeDao.findByCondition("code", "3.1");
            contract.setServiceTypeCode(serviceTypeCode);
            Code creditType = codeDao.findByCondition("code", "2.2.1");
            contract.setCreditType(creditType);
            Code creditStatus = codeDao.findByCondition("code", "2.2.1.2.0");
            contract.setCreditStatus(creditStatus);
            Supplier supplier = supplierDao.getSupplierByName(CMSProperty.getProperty("default.supplier.name"));
            contract.setSupplier(supplier);
            contract.setSupplierId(supplier.getId());
            contract.setChargeAvailable(servPoint.getBlockFlag()? false:true);
            contract.setAddress1(servPoint.getAddress1());
            contract.setAddress2(servPoint.getAddress2());
            contract.setAddress3(servPoint.getAddress3());
            contract.setContractDate(servPoint.getWriteDate());
            contract.setPrepayStartTime(servPoint.getWriteDate());
            contract.setServicePointId(servicePointId);
            contract.setCurrentCredit(10.0);
            contract.setCurrentArrears(10.0);
            //contract.setArrearsContractCount(1);
            if(geocode.length() < 4){
                contract.setLocation(getLocation(geocode));
            }else{
                contract.setLocation(getLocation(geocode.substring(0, 4)));
            }
            contract.setStatus(codeDao.getCodeIdByCodeObject(CommonConstants.ContractStatus.NORMAL.getCode()));
            
            if(servPoint.getMeter() != null) {
                String mdsId = servPoint.getMeter().getMeterSerialNo();
                if (mdsId.startsWith("P"))
                    mdsId = mdsId.substring(1);
                log.debug("METER_NO[" + mdsId + "]");
                Meter meter = meterDao.get(mdsId);
                
                if (meter == null) {
                    meter = new EnergyMeter();
                    meter.setMdsId(mdsId);
                    meter.setContract(contract);
                    meter.setInstallDate(servPoint.getMeter().getWriteDate());
                    meter.setSupplier(contract.getSupplier());
                    meter.setInstallProperty(servPoint.getMeter().getBatchNo());
                    meter.setMeterType(CommonConstants.getMeterTypeByName(MeterType.EnergyMeter.name()));
                    meter.setLocation(contract.getLocation());
                    
                    Code status = codeDao.findByCondition("name", "NewRegistered");
                    if (status != null)
                        meter.setMeterStatus(status);
                    
                    if(servPoint.getMeter().getModel() != null && !"".equals(servPoint.getMeter().getModel())){
                        DeviceModel model = deviceModelDao.findByCondition("name", getMeterModel(servPoint.getMeter().getModel()));
                        if (model != null)
                            meter.setModel(model);
                    }
                    
                    meterDao.add(meter);
                }
                else {
                    // contract의 geo-code가 다르면 이전 contract의 미터를 널로 처리한다.
                    if (meter.getContract() != null && !meter.getContract().getContractNumber().equals(geocode)) {
                        meter.getContract().setMeter(null);
                        contractDao.update(meter.getContract());
                    }
                }
                meterEntDao.saveOrUpdate(servPoint.getMeter());
                contract.setMeter(meter);
            }
            
            if(servPoint.getTariff() != null && !"".equals(servPoint.getTariff().getTariffCode())){
                TariffType tariff = null;
                Code sic = null;
                if (servPoint.getTariff().getTariffCode().equals("E11")) {
                    tariff = tariffTypeDao.findByCondition("name", "Residential");
                    // home 14.2
                    sic = codeDao.findByCondition("code", "14.2");
                }
                else if (servPoint.getTariff().getTariffCode().equals("E12")) {
                    tariff = tariffTypeDao.findByCondition("name", "Non Residential");
                    sic = codeDao.findByCondition("code", "14.E4AL");
                }
                else if(servPoint.getTariff().getTariffCode().equals("PRE-RESIDENTIAL")) { //for iraq
                    tariff = tariffTypeDao.findByCondition("name", "Residential");
                } else if(servPoint.getTariff().getTariffCode().equals("PRE-INDUSTRIAL")) { //for iraq
                    tariff = tariffTypeDao.findByCondition("name", "Industrial");
                } else if(servPoint.getTariff().getTariffCode().equals("PRE-COMMERCIAL")) { //for iraq
                    tariff = tariffTypeDao.findByCondition("name", "Commercial");
                } else if(servPoint.getTariff().getTariffCode().equals("PRE-GOVERNMENT")) { //for iraq
                    tariff = tariffTypeDao.findByCondition("name", "Government");
                } else if(servPoint.getTariff().getTariffCode().equals("PRE-AGRICULTURE")) { //for iraq
                    tariff = tariffTypeDao.findByCondition("name", "Agriculture");
                }
                if(tariff != null){
                    contract.setTariffIndex(tariff);
                    if (sic != null)
                        contract.setSic(sic);
                }

                condition = new HashSet<Condition>();
                condition.add(new Condition("id.tariffCode", new Object[]{servPoint.getTariff().getTariffCode()}, null, Restriction.EQ));
                condition.add(new Condition("id.tariffGroup", new Object[]{servPoint.getTariff().getTariffGroup()}, null, Restriction.EQ));
                List<TariffEnt> dbTariffEnts = tariffEntDao.findByConditions(condition);
                
                if(dbTariffEnts == null || (dbTariffEnts != null && dbTariffEnts.size() == 0)){
                    TariffEnt _logTariffEnt = servPoint.getTariff();
                    _logTariffEnt.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat(""));
                    tariffEntDao.add(_logTariffEnt);
                }
            }
            
            servPointDao.saveOrUpdate(servPoint);
            contractDao.add(contract);
        }
    }
    
    private void modServPoint(CMSEnt cmsEnt) throws CMSException {
        ServPoint servPoint = cmsEnt.getSerivcePoint();
        String servicePointId = servPoint.getServPointId();
        Set<Condition> condition = new HashSet<Condition>();
        condition.add(new Condition("servicePointId", new Object[]{servicePointId}, null, Restriction.EQ));
        List<Contract> contracts = contractDao.findByConditions(condition);
        
        if(contracts.size() == 1 && servPoint.isExist()){
            // Tariff검사
            condition = new HashSet<Condition>();
            condition.add(new Condition("servPointId", new Object[]{servicePointId}, null, Restriction.EQ));
            List<ServPoint> servpoints = servPointDao.findByConditions(condition);
            if (servpoints != null && servpoints.size() == 1) {
                ServPoint _servPoint = servpoints.get(0);
                
                if (servPoint.getTariff() != null) {
                    condition = new HashSet<Condition>();
                    condition.add(new Condition("id.tariffCode", 
                            new Object[]{servPoint.getTariff().getTariffCode()}, 
                            null, Restriction.EQ));
                    condition.add(new Condition("id.tariffGroup", 
                            new Object[]{servPoint.getTariff().getTariffGroup()}, 
                            null, Restriction.EQ));
                    List<TariffEnt> tariffs = tariffEntDao.findByConditions(condition);
                    if (tariffs == null || (tariffs != null && tariffs.size() == 0)) {
                        TariffEnt _logTariffEnt = servPoint.getTariff();
                        _logTariffEnt.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat(""));
                        tariffEntDao.add(_logTariffEnt);
                    }
                }
                _servPoint.setAddress1(servPoint.getAddress1());
                _servPoint.setAddress2(servPoint.getAddress2());
                _servPoint.setAddress3(servPoint.getAddress3());
                _servPoint.setBlockFlag(servPoint.getBlockFlag());
                _servPoint.setBlockReason(servPoint.getBlockReason());
                _servPoint.setExist(servPoint.isExist());
                _servPoint.setGeoCode(servPoint.getGeoCode());
                _servPoint.setTariff(servPoint.getTariff());
                _servPoint.setWriteDate(servPoint.getWriteDate());
                
                servPointDao.update(_servPoint);
            }
            else
                servPointDao.add(servPoint);
            
            String geocode = servPoint.getGeoCode().replaceAll("-", "");
            
            // 한개만 있어야 한다.
            condition = new HashSet<Condition>();
            condition.add(new Condition("servicePointId", new Object[]{servPoint.getServPointId()}, null, Restriction.EQ));
            contracts = contractDao.findByConditions(condition);
            
            Contract contract = contracts.get(0);
            if (contract.getContractNumber().contains(geocode)) {
                contract.setChargeAvailable(servPoint.getBlockFlag()? false:true);
                contract.setAddress1(servPoint.getAddress1());
                contract.setAddress2(servPoint.getAddress2());
                contract.setAddress3(servPoint.getAddress3());
                if(geocode.length() < 4){
                    contract.setLocation(getLocation(geocode));
                }else{
                    contract.setLocation(getLocation(geocode.substring(0, 4)));
                }
                
                // contract.setPrepayStartTime(servPoint.getWriteDate());
                
                if(servPoint.getTariff() != null){
                    TariffType tariff = null;
                    if (servPoint.getTariff().getTariffCode().equals("E11"))
                        tariff = tariffTypeDao.findByCondition("name", "Residential");
                    else if (servPoint.getTariff().getTariffCode().equals("E12"))
                        tariff = tariffTypeDao.findByCondition("name", "Non Residential");
                    else if(servPoint.getTariff().getTariffCode().equals("PRE-RESIDENTIAL")) //for iraq
                        tariff = tariffTypeDao.findByCondition("name", "Residential");
                    else if(servPoint.getTariff().getTariffCode().equals("PRE-INDUSTRIAL")) //for iraq
                        tariff = tariffTypeDao.findByCondition("name", "Industrial");
                    else if(servPoint.getTariff().getTariffCode().equals("PRE-COMMERCIAL")) //for iraq
                        tariff = tariffTypeDao.findByCondition("name", "Commercial");
                    else if(servPoint.getTariff().getTariffCode().equals("PRE-GOVERNMENT")) //for iraq
                        tariff = tariffTypeDao.findByCondition("name", "Government");
                    else if(servPoint.getTariff().getTariffCode().equals("PRE-AGRICULTURE")) //for iraq
                        tariff = tariffTypeDao.findByCondition("name", "Agriculture");
                    if(tariff != null){
                        contract.setTariffIndex(tariff);
                    }
                }
                contractDao.update(contract);
            }
            else {
                condition = new HashSet<Condition>();
                condition.add(new Condition("contractNumber", new Object[]{"%"+geocode+"%"}, null, Restriction.LIKE));
                contracts = contractDao.findByConditions(condition);
                
                if (contracts != null && contracts.size() > 0) {
                    geocode += contracts.size();
                    // throw new CMSException(ErrorType.Error.getIntValue(), "Geo-code provided already in the system");
                }
                
                contract.setContractNumber(geocode);
                contract.setChargeAvailable(servPoint.getBlockFlag()? false:true);
                contract.setAddress1(servPoint.getAddress1());
                contract.setAddress2(servPoint.getAddress2());
                contract.setAddress3(servPoint.getAddress3());
                
                if(geocode.length() < 4){
                    contract.setLocation(getLocation(geocode));
                }else{
                    contract.setLocation(getLocation(geocode.substring(0, 4)));
                }
                
                // contract.setPrepayStartTime(servPoint.getWriteDate());
                
                if(servPoint.getTariff() != null){
                    TariffType tariff = null;
                    if (servPoint.getTariff().getTariffCode().equals("E11"))
                        tariff = tariffTypeDao.findByCondition("name", "Residential");
                    else if (servPoint.getTariff().getTariffCode().equals("E12"))
                        tariff = tariffTypeDao.findByCondition("name", "Non Residential");
                    else if(servPoint.getTariff().getTariffCode().equals("PRE-RESIDENTIAL")) //for iraq
                        tariff = tariffTypeDao.findByCondition("name", "Residential");
                    else if(servPoint.getTariff().getTariffCode().equals("PRE-INDUSTRIAL")) //for iraq
                        tariff = tariffTypeDao.findByCondition("name", "Industrial");
                    else if(servPoint.getTariff().getTariffCode().equals("PRE-COMMERCIAL")) //for iraq
                        tariff = tariffTypeDao.findByCondition("name", "Commercial");
                    else if(servPoint.getTariff().getTariffCode().equals("PRE-GOVERNMENT")) //for iraq
                        tariff = tariffTypeDao.findByCondition("name", "Government");
                    else if(servPoint.getTariff().getTariffCode().equals("PRE-AGRICULTURE")) //for iraq
                        tariff = tariffTypeDao.findByCondition("name", "Agriculture");
                    if(tariff != null){
                        contract.setTariffIndex(tariff);
                    }
                }
                
                contractDao.update(contract);
                /*
                if (contract.getCustomer() != null) {
                    contract.setContractNumber(geocode);
                    contract.setChargeAvailable(servPoint.getBlockFlag()? false:true);
                    contract.setAddress1(servPoint.getAddress1());
                    contract.setAddress2(servPoint.getAddress2());
                    contract.setAddress3(servPoint.getAddress3());
                    // contract.setPrepayStartTime(servPoint.getWriteDate());
                    contractDao.update(contract);
                }
                else {
                    contract.setServicePointId(null);
                    contractDao.update(contract);
                    
                    contract = new Contract();
                    contract.setContractNumber(geocode);
                    Code serviceTypeCode = codeDao.findByCondition("code", "3.1");
                    contract.setServiceTypeCode(serviceTypeCode);
                    Code creditType = codeDao.findByCondition("code", "2.2.1");
                    contract.setCreditType(creditType);
                    Code creditStatus = codeDao.findByCondition("code", "2.2.1.2.0");
                    contract.setCreditStatus(creditStatus);
                    Supplier supplier = supplierDao.getSupplierByName(CMSProperty.getProperty("default.supplier.name"));
                    contract.setSupplier(supplier);
                    contract.setSupplierId(supplier.getId());
                    contract.setChargeAvailable(servPoint.getBlockFlag()? false:true);
                    contract.setAddress1(servPoint.getAddress1());
                    contract.setAddress2(servPoint.getAddress2());
                    contract.setAddress3(servPoint.getAddress3());
                    contract.setPrepayStartTime(servPoint.getWriteDate());
                    contract.setServicePointId(servicePointId);
                    contractDao.add(contract);
                }
                */
            }
        }
    }
    
    private void installMeter(CMSEnt cmsEnt) throws CMSException {
        ServPoint servPoint = cmsEnt.getSerivcePoint();
        MeterEnt meterEnt = servPoint.getMeter();
        String mdsId = meterEnt.getMeterSerialNo();
        if (mdsId.startsWith("P"))
            mdsId = mdsId.substring(1);
        
        Set<Condition> condition = new HashSet<Condition>();
        condition.add(new Condition("servicePointId", new Object[]{"%"+servPoint.getServPointId()+"%"}, null, Restriction.LIKE));
        condition.add(new Condition("contractNumber", new Object[]{"%"+servPoint.getGeoCode().replaceAll("-", "")+"%"}, null, Restriction.LIKE));
        List<Contract> contracts = contractDao.findByConditions(condition);
        
        condition = new HashSet<Condition>();
        condition.add(new Condition("mdsId", new Object[]{mdsId}, null, Restriction.EQ));
        List<Meter> meters = meterDao.findByConditions(condition);
        
        if (contracts != null && contracts.size() == 1 && meters != null && meters.size() == 1) {
            Contract contract = contracts.get(0);
            Meter meter = meters.get(0);
           
            // contract의 geo-code가 다르면 이전 contract의 미터를 널로 처리한다.
            if (meter.getContract() != null && !meter.getContract().getContractNumber().equals(contract.getContractNumber())) {
                meter.getContract().setMeter(null);
                contractDao.update(meter.getContract());
            }
        
            contract.setMeter(meter);
            meter.setLocation(contract.getLocation());
            meter.setContract(contract);
            
            contractDao.update(contract);
            meterDao.update(meter);
            
            condition = new HashSet<Condition>();
            condition.add(new Condition("servPointId", new Object[]{servPoint.getServPointId()}, null, Restriction.EQ));
            List<ServPoint> servPoints = servPointDao.findByConditions(condition);
            if (servPoints != null && servPoints.size() == 1) {
                ServPoint _servPoint = servPoints.get(0);
                _servPoint.setMeter(servPoint.getMeter());
                servPointDao.saveOrUpdate(_servPoint);
            }
        }
    }
    
    private void removeMeter(CMSEnt cmsEnt) throws CMSException {
        ServPoint servPoint = cmsEnt.getSerivcePoint();
        String servicePointId = servPoint.getServPointId();
        Set<Condition> condition = new HashSet<Condition>();
        condition.add(new Condition("servicePointId", new Object[]{"%"+servicePointId+"%"}, null, Restriction.LIKE));
        condition.add(new Condition("contractNumber", new Object[]{"%"+servPoint.getGeoCode().replaceAll("-", "")+"%"}, null, Restriction.LIKE));
        List<Contract> contracts = contractDao.findByConditions(condition);
        
        if(contracts.size() == 1 && servPoint.isExist()){
            Contract contract = contracts.get(0);
            Meter meter = contract.getMeter();
            
            if (meter != null) {
                Code deleteCode = codeDao.findByCondition("code", "1.3.3.9");
                if (deleteCode != null) {
                    meter.setMeterStatus(deleteCode);
                    meterDao.update(meter);
                }
            }
            contract.setMeter(null);
            contractDao.update(contract);
            
            condition = new HashSet<Condition>();
            condition.add(new Condition("servPointId", new Object[]{servicePointId}, null, Restriction.EQ));
            List<ServPoint> servpoints = servPointDao.findByConditions(condition);
            
            if (servpoints != null && servpoints.size() == 1) {
                ServPoint _servPoint = servpoints.get(0);
                _servPoint.setMeter(null);
                servPointDao.update(_servPoint);
            }
        }
    }
    
    private void enroll(CMSEnt cmsEnt) throws CMSException {
        CustEnt custEnt = cmsEnt.getCustomer();
        ServPoint servPoint = cmsEnt.getSerivcePoint();
        
        Set<Condition> condition = new HashSet<Condition>();
        condition.add(new Condition("customerNo", new Object[]{custEnt.getCustomerId()}, null, Restriction.EQ));
        List<Customer> customers = customerDao.findByConditions(condition);
        
        if(customers == null || customers.size() == 0){
            throw new CMSException(ErrorType.Error.getIntValue(), "Customer Information is empty in Prepayment System");
        }
        
        Customer customer = customers.get(0);
        
        condition = new HashSet<Condition>();
        condition.add(new Condition("servicePointId", new Object[]{servPoint.getServPointId()}, null, Restriction.EQ));
        List<Contract> contracts = contractDao.findByConditions(condition);
        
        if(contracts == null || contracts.size() == 0){
            throw new CMSException(ErrorType.Error.getIntValue(), "Service Point Information is empty in Prepayment System");
        }
        Contract contract = contracts.get(0);
        
        customer.setLocation(contract.getLocation());
        customerDao.update(customer);
        
        contract.setCustomer(customer);
        contractDao.saveOrUpdate(contract);
        
        condition = new HashSet<Condition>();
        condition.add(new Condition("servPointId", new Object[]{servPoint.getServPointId()}, null, Restriction.EQ));
        List<ServPoint> servPoints = servPointDao.findByConditions(condition);
        
        servPoint = servPoints.get(0);
        
        condition = new HashSet<Condition>();
        condition.add(new Condition("customerId", new Object[]{custEnt.getCustomerId()}, null, Restriction.EQ));
        List<CustEnt> custents = custEntDao.findByConditions(condition);
        
        custEnt = custents.get(0);
        
        custEnt.setServPoint(servPoint);
        custEntDao.saveOrUpdate(custEnt);
        
        servPoint.setCustEnt(custEnt);
        servPointDao.saveOrUpdate(servPoint);
    }
    
    private void deenroll(CMSEnt cmsEnt) throws CMSException {
        ServPoint servPoint = cmsEnt.getSerivcePoint();
        
        Set<Condition> condition = new HashSet<Condition>();
        condition.add(new Condition("servicePointId", new Object[]{servPoint.getServPointId()}, null, Restriction.EQ));
        List<Contract> contracts = contractDao.findByConditions(condition);
        
        Contract contract = contracts.get(0);
        
        contract.setCustomer(null);
        contractDao.saveOrUpdate(contract);
        
        condition = new HashSet<Condition>();
        condition.add(new Condition("servPointId", new Object[]{servPoint.getServPointId()}, null, Restriction.EQ));
        List<ServPoint> servPoints = servPointDao.findByConditions(condition);
        
        servPoint = servPoints.get(0);
        
        CustEnt custEnt = servPoint.getCustEnt();
        if (custEnt != null) {
            custEnt.setServPoint(null);
            custEntDao.saveOrUpdate(custEnt);
        }
        
        servPoint.setCustEnt(null);
        servPointDao.saveOrUpdate(servPoint);
    }
    
    public CMSEnt saveAll( AuthCred authCred, CMSEnt cmsEnt, String saveAllType)
            throws com.aimir.cms.exception.CMSException
    {
        log.debug("SaveAllType[" + saveAllType + "]");
        
        cmsAuth.userAuthentication(authCred);
        SaveAllReqParameterValidator.validator(authCred, cmsEnt, saveAllType);
        
        //Set<Location> locs = supplierDao.getSupplierByName("ECG").getLocations();
        //Location defaultLocation = locs.iterator().next();        
        
        if (saveAllType != null && "f_add_customer".equals(saveAllType) && cmsEnt.getCustomer() != null) {
            addCustomer(cmsEnt);
        }
        else if (saveAllType != null && "f_mod_customer".equals(saveAllType) && cmsEnt.getCustomer() != null) {
            modCustomer(cmsEnt);
        }
        else if (saveAllType != null && "f_add_serv_point".equals(saveAllType) && cmsEnt.getSerivcePoint() != null) {
            addServPoint(cmsEnt);
        }
        else if (saveAllType != null && 
                ("f_mod_serv_point".equals(saveAllType) || 
                        "f_block_serv_point".equals(saveAllType) || 
                        "f_unblock_serv_point".equals(saveAllType)) && cmsEnt.getSerivcePoint() != null) {
            modServPoint(cmsEnt);
        }
        else if (saveAllType != null && "f_install_meter".equals(saveAllType) && cmsEnt.getSerivcePoint() != null) {
            installMeter(cmsEnt);
        }
        else if (saveAllType != null && "f_remove_meter".equals(saveAllType) && cmsEnt.getSerivcePoint() != null) {
            removeMeter(cmsEnt);
        }
        else if (saveAllType != null && "f_enroll".equals(saveAllType)) {
            enroll(cmsEnt);
        }
        else if (saveAllType != null && "f_deenroll".equals(saveAllType)) {
            deenroll(cmsEnt);
        }
        
        CMSEnt response = new CMSEnt();

        if(cmsEnt.getCustomer() != null){
            CustEnt custEnt = new CustEnt();
            custEnt.setCustomerId(cmsEnt.getCustomer().getCustomerId());
            response.setCustomer(custEnt);
        }
        if(cmsEnt.getSerivcePoint() != null){
            ServPoint servicePoint = new ServPoint();
            servicePoint.setServPointId(cmsEnt.getSerivcePoint().getServPointId());
            response.setSerivcePoint(servicePoint);
        }       

        return response;
    }

    public CMSEnt dataLoad( AuthCred authCred, CMSEnt cmsEnt)
            throws com.aimir.cms.exception.CMSException {
        
        cmsAuth.userAuthentication(authCred);
        DataLoadReqParameterValidator.validator(authCred, cmsEnt);
        
        CustEnt custEnt = cmsEnt.getCustomer();
        ServPoint servPoint = cmsEnt.getSerivcePoint();
        
        if (custEnt != null && custEnt.getCustomerId() != null && !"".equals(custEnt.getCustomerId())) {
            Set<Condition> condition = new HashSet<Condition>();
            condition.add(new Condition("customerId", new Object[]{custEnt.getCustomerId()}, null, Restriction.EQ));
            List<CustEnt> customers = custEntDao.findByConditions(condition);
            
            custEnt = null;
            
            if (customers != null && customers.size() == 1) {
                custEnt = customers.get(0);
                
                if (custEnt.getServPoint() != null) {
                    servPoint = new ServPoint();
                    servPoint.setAddress1(custEnt.getServPoint().getAddress1());
                    servPoint.setAddress2(custEnt.getServPoint().getAddress2());
                    servPoint.setAddress3(custEnt.getServPoint().getAddress3());
                    servPoint.setBlockFlag(custEnt.getServPoint().getBlockFlag());
                    servPoint.setBlockReason(custEnt.getServPoint().getBlockReason());
                    servPoint.setExist(custEnt.getServPoint().isExist());
                    servPoint.setGeoCode(custEnt.getServPoint().getGeoCode());
                    servPoint.setServPointId(custEnt.getServPoint().getServPointId());
                    servPoint.setWriteDate(custEnt.getServPoint().getWriteDate());
                    servPoint.setTariff(custEnt.getServPoint().getTariff());
                    servPoint.setMeterSerialNo(custEnt.getServPoint().getMeterSerialNo());
                    servPoint.setMeter(custEnt.getServPoint().getMeter());
                }
            }
        }
        else if (servPoint != null && servPoint.getServPointId() != null && !"".equals(servPoint.getServPointId())) {
            Set<Condition> condition = new HashSet<Condition>();
            condition.add(new Condition("servPointId", new Object[]{servPoint.getServPointId()}, null, Restriction.EQ));
            List<ServPoint> servPoints = servPointDao.findByConditions(condition);
            
            servPoint = null;
            
            if (servPoints != null && servPoints.size() == 1) {
                servPoint = servPoints.get(0);
                
                if (servPoint.getCustEnt() != null) {
                    custEnt = new CustEnt();
                    custEnt.setAddress1(servPoint.getCustEnt().getAddress1());
                    custEnt.setAddress2(servPoint.getCustEnt().getAddress2());
                    custEnt.setAddress3(servPoint.getCustEnt().getAddress3());
                    custEnt.setCustomerId(servPoint.getCustEnt().getCustomerId());
                    custEnt.setEmail(servPoint.getCustEnt().getEmail());
                    custEnt.setExist(servPoint.getCustEnt().isExist());
                    custEnt.setFax(servPoint.getCustEnt().getFax());
                    custEnt.setIdNo(servPoint.getCustEnt().getIdNo());
                    custEnt.setIdType(servPoint.getCustEnt().getIdType());
                    custEnt.setOtherNames(servPoint.getCustEnt().getOtherNames());
                    custEnt.setSurname(servPoint.getCustEnt().getSurname());
                    custEnt.setTaxRefNo(servPoint.getCustEnt().getTaxRefNo());
                    custEnt.setTelephone1(servPoint.getCustEnt().getTelephone1());
                    custEnt.setTelephone2(servPoint.getCustEnt().getTelephone2());
                    custEnt.setTelephone3(servPoint.getCustEnt().getTelephone3());
                    custEnt.setWriteDate(servPoint.getCustEnt().getWriteDate());
                }
            }
        }

        // Customer와 ServPoint 둘 다 없으면 에러
        if (custEnt == null && servPoint == null)
            throw new CMSException(ErrorType.Error.getIntValue(), "Customer Or ServPoint Information is empty in Prepayment System");
            
        if (servPoint!= null) {
            if (servPoint.getTariff() != null) {
                TariffEnt tariffEnt = new TariffEnt();
                tariffEnt.setTariffCode(servPoint.getTariff().getTariffCode());
                tariffEnt.setTariffGroup(servPoint.getTariff().getTariffGroup());
                
                log.debug("Tariff_code[" + tariffEnt.getTariffCode() + 
                        "] Tariff_group[" + tariffEnt.getTariffGroup() + "]");
                
                servPoint.setTariff(tariffEnt);
            }
            
            if (servPoint.getMeterSerialNo() != null && !"".equals(servPoint.getMeterSerialNo())) {
                MeterEnt _meterEnt = meterEntDao.findByCondition("id.meterSerialNo", servPoint.getMeterSerialNo());
                
                if (_meterEnt != null) {
                    MeterEnt meterEnt = new MeterEnt();
                    meterEnt.setBatchNo(_meterEnt.getBatchNo());
                    meterEnt.setMake(_meterEnt.getMake());
                    meterEnt.setMeterSerialNo(_meterEnt.getMeterSerialNo());
                    meterEnt.setModel(_meterEnt.getModel());
                    meterEnt.setWriteDate(_meterEnt.getWriteDate());
                    
                    servPoint.setMeter(meterEnt);
                }
            }
            
            // servPoint.getTariff();
            // servPoint.getMeter();
        }
        
        CMSEnt response = new CMSEnt();
        response.setCustomer(custEnt);
        response.setSerivcePoint(servPoint);
        
        return response;
    }

    public MeterEnt meterCheck( AuthCred authCred, MeterEnt meterEnt)
            throws com.aimir.cms.exception.CMSException {
        
        cmsAuth.userAuthentication(authCred);
        MeterCheckReqParameterValidator.validator(authCred, meterEnt);
        
        Set<Condition> condition = new HashSet<Condition>();
        condition.add(new Condition("id.meterSerialNo", new Object[]{meterEnt.getMeterSerialNo()}, null, Restriction.EQ));
        condition.add(new Condition("id.make", new Object[]{meterEnt.getMake()}, null, Restriction.EQ));
        List<MeterEnt> meters = meterEntDao.findByConditions(condition);
        
        MeterEnt response = null;
        
        if (meters != null && meters.size() == 1) {
            MeterEnt _meterEnt = meters.get(0);
            response = new MeterEnt();
            response.setModel(_meterEnt.getModel());
            response.setMake(_meterEnt.getMake());
            response.setBatchNo(_meterEnt.getBatchNo());
            response.setMeterSerialNo(_meterEnt.getMeterSerialNo());
        }
        else{
            throw new CMSException(ErrorType.Error.getIntValue(), "Meter is empty");
        }
        
        /*
        Meter meter = null;
        meter = meterDao.get(meterEnt.getMeterSerialNo());
        if(meter != null){
            response = new MeterEnt();
            response.setMeterSerialNo(meter.getMdsId());
            response.setWriteDate(meter.getInstallDate());
            response.setBatchNo(meter.getInstallProperty());
            
            Set<Condition> condition = new HashSet<Condition>();
            condition.add(new Condition("meterSerialNo", new Object[]{meter.getMdsId()}, null, Restriction.EQ));
            List<MeterEnt> meters = meterEntDao.findByConditions(condition);
            
            if (meters != null && meters.size() == 1) {
                MeterEnt _meterEnt = meters.get(0);
                response.setModel(_meterEnt.getModel());
                response.setMake(_meterEnt.getMake());
            }
          
        }else{
            throw new CMSException(ErrorType.Error.getIntValue(), "Meter is empty");
        }
        */
        return response;
    }

    private String getMeterModel(String cmsMeterModel) {
        String modelName = null;
        if (cmsMeterModel.equals("ML001")) {
            modelName = "K162M";
        }
        else if (cmsMeterModel.equals("ML002")) {
            modelName = "K382M X1";
        }
        else if (cmsMeterModel.equals("ML058")) {
            modelName = "K382M AB1";
        }
        else if (cmsMeterModel.equals("ML053")) {
            modelName = "K351C";
        }
        else if (cmsMeterModel.equals("ML046")) {
            modelName = "OmniPower P1";
        }
        else if (cmsMeterModel.equals("ML059")) {
            modelName = "OmniPower P3";
        }
        else modelName = cmsMeterModel;
        
        return modelName;
    }
    
    public void meterImport( AuthCred authCred, MeterEnt meterEnt)
            throws com.aimir.cms.exception.CMSException {
        
        cmsAuth.userAuthentication(authCred);
        MeterImportReqParameterValidator.validator(authCred, meterEnt);

        String modelName = getMeterModel(meterEnt.getModel());
        DeviceModel model = deviceModelDao.findByCondition("name", modelName);
        EnergyMeter meter = null;
        String mdsId = meterEnt.getMeterSerialNo();
        if (mdsId.startsWith("P"))
            mdsId = mdsId.substring(1);
        
        meter = energyMeterDao.findByCondition("mdsId", mdsId);
        if(meter != null){
            meter.setModel(model);
            // meter.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat(""));
            meter.setPrepaymentMeter(Boolean.TRUE);
            // Supplier supplier = supplierDao.getSupplierByName(CMSProperty.getProperty("default.supplier.name"));
            // meter.setSupplier(supplier);
            // meter.setSupplierId(supplier.getId());
            // meter.setMeterType(CommonConstants.getMeterTypeByName(MeterType.EnergyMeter.name()));
            meter.setInstallProperty(meterEnt.getBatchNo());
            // meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.NewRegistered.name()));
            //meter.setLocation(location);
            try{
                MeterEnt _logMeterEnt = meterEnt;
                _logMeterEnt.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat(""));
                meterEntDao.saveOrUpdate(_logMeterEnt);
                energyMeterDao.update(meter);
            }catch(Exception e){
                throw new CMSException("MeterImport Update Data Error", e);
            }
            
        }else{
            if (!Pattern.matches(model.getMdsIdPattern(), mdsId))
                throw new CMSException("MeterImport Add Data Error, MeterSerialNo is invalid");
            
            if (mdsId.startsWith("192"))
                throw new CMSException("MeterImport Add Data Error, MeterSerialNo is invalid");
            
            meter = new EnergyMeter();
            meter.setMdsId(mdsId);
            meter.setMeterType(CommonConstants.getMeterTypeByName(MeterType.EnergyMeter.name()));
            meter.setModel(model);
            meter.setWriteDate(DateTimeUtil.getDateString(new Date()));
            meter.setInstallDate(DateTimeUtil.getDateString(new Date()));
            meter.setPrepaymentMeter(Boolean.TRUE);
            meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.NewRegistered.name()));
            Supplier supplier = supplierDao.getSupplierByName(CMSProperty.getProperty("default.supplier.name"));
            meter.setSupplier(supplier);
            meter.setSupplierId(supplier.getId());
            meter.setInstallProperty(meterEnt.getBatchNo());
            meter.setLocation(getLocation("Accra"));
            
            try{
                MeterEnt _logMeterEnt = meterEnt;
                _logMeterEnt.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat(""));
                meterEntDao.saveOrUpdate(_logMeterEnt);
                // energyMeterDao.update(meter);
                energyMeterDao.add(meter);
            }catch(Exception e){
                throw new CMSException("MeterImport Add Data Error", e);
            }           
        }
    }

    public DebtEnt addDebt( AuthCred authCred, DebtEnt debtEnt)
            throws com.aimir.cms.exception.CMSException {
        
        cmsAuth.userAuthentication(authCred);
        AddDebtReqParameterValidator.validator(authCred, debtEnt);

        List<Customer> customers = customerDao.getCustomersByCustomerNo(new String[]{debtEnt.getCustomerId()});
        if(customers == null || customers.size() == 0){
            //throw new CMSException("Customer Information is empty in Prepayment System!");
            throw new CMSException(ErrorType.Error.getIntValue(), "Customer Information is empty in Prepayment System");
        }
        
        Set<Contract> contracts = customers.get(0).getContracts();
        if(contracts == null || contracts.size() == 0){
            //throw new CMSException("ServicePoint Information is empty in Prepayment System!");
            throw new CMSException(ErrorType.Error.getIntValue(), "Service Point Information is empty in Prepayment System");
        }
        
        // Contract contract = contracts.iterator().next();
        // contract.setCurrentArrears(debtEnt.getDebtAmount());
        // contract.setCreditType(codeDao.getCodeByName("prepay"));//postpay->prepayment

        //contract.setCreditStatus(creditStatus);
        //debtEnt.getDebtStatus();
        //debtEnt.getDebtType();
        //debtEnt.getInstallmentNo();
        
        try{
            DebtEnt _logDebtEnt = debtEnt;
            _logDebtEnt.setWriteDate(DateTimeUtil.getDateString(new Date()));
            debtEntDao.saveOrUpdate(_logDebtEnt);
            // contractDao.update(contract);
        }catch(Exception e){
            throw new CMSException("AddDebt Processing Error!",e);
        }

        DebtEnt response = debtEnt;
        return response;
    }

    public DebtEnt updateDebt( AuthCred authCred, DebtEnt debtEnt)
            throws com.aimir.cms.exception.CMSException {
        
        cmsAuth.userAuthentication(authCred);
        UpdateDebtReqParameterValidator.validator(authCred, debtEnt);

        /*
        List<Customer> customers = customerDao.getCustomersByCustomerNo(new String[]{debtEnt.getCustomerId()});
        if(customers == null || customers.size() == 0){
            //throw new CMSException("Customer Information is empty in Prepayment System!");
            throw new CMSException(ErrorType.Error.getIntValue(), "Customer Information is empty in Prepayment System");
        }
        
        Set<Contract> contracts = customers.get(0).getContracts();
        if(contracts == null || contracts.size() == 0){
            //throw new CMSException("ServicePoint Information is empty in Prepayment System!");
            throw new CMSException(ErrorType.Error.getIntValue(), "Customer Information is empty in Prepayment System");
        }
        
        Contract contract = contracts.iterator().next();
        if (debtEnt.getDebtAmount() != null && debtEnt.getDebtAmount() > 0)
            contract.setCurrentArrears(debtEnt.getDebtAmount());
        // contract.setCreditType(codeDao.getCodeByName("prepay"));//postpay->prepayment
        //debtEnt.getDebtType();
        */
        
        try{
            DebtEnt _logDebtEnt = debtEnt;
            _logDebtEnt.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat(""));
            debtEntDao.saveOrUpdate(_logDebtEnt);
            // contractDao.update(contract);
        }catch(Exception e){
            throw new CMSException("UpdateDebt Processing Error!",e);
        }

        DebtEnt response = debtEnt;
        return response;
    }

    public List<DebtEnt> getDebt( AuthCred authCred, DebtEnt debtEnt)
            throws com.aimir.cms.exception.CMSException {
        
        cmsAuth.userAuthentication(authCred);
        GetDebtReqParameterValidator.validator(authCred, debtEnt);
        
        String customerNo = debtEnt.getCustomerId();
        /*
        List<Customer> customers = customerDao.getCustomersByCustomerNo(new String[]{customerNo});
        if(customers == null || customers.size() == 0){
            throw new CMSException(ErrorType.Error.getIntValue(), "Customer Information is empty in Prepayment System");
        }
        */
        
        Set<Condition> condition = new HashSet<Condition>();
        condition.add(new Condition("id.customerId", new Object[]{customerNo}, null, Restriction.EQ));
        List<DebtEnt> dbEnts = debtEntDao.findByConditions(condition);
        
        return dbEnts;
        
        /*
        if (dbEnts == null || (dbEnts != null && dbEnts.size() == 0))
            return dbEnts; // throw new CMSException(ErrorType.Error.getIntValue(), "No debt");
        
        Set<Contract> contracts = customers.get(0).getContracts();

        if(contracts == null || contracts.size() == 0){
            throw new CMSException(ErrorType.Error.getIntValue(), "Customer has to enroll in Prepyament System");
        }

        if(contracts != null && contracts.size() > 0){
            for(Contract contract : contracts){
                
                DebtEnt entity = new DebtEnt();
                
                entity.setCustomerId(debtEnt.getCustomerId());
                entity.setDebtAmount(contract.getCurrentArrears());
                entity.setDebtStatus(dbEnt.getDebtStatus());
                entity.setDebtType(dbEnt.getDebtType());
                entity.setInstallmentDueDate(dbEnt.getInstallmentDueDate());
                entity.setDebtRef(dbEnt.getDebtRef());
                response.add(entity); 
            }
        }
        return response;
        */
    }    
}
