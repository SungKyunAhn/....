package com.aimir.cms.ws.server;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.cms.constants.CMSConstants.ErrorType;
import com.aimir.cms.model.AuthCred;
import com.aimir.cms.model.CMSEnt;
import com.aimir.cms.model.CustEnt;
import com.aimir.cms.model.DebtEnt;
import com.aimir.cms.model.ErrorParam;
import com.aimir.cms.model.MeterEnt;
import com.aimir.cms.model.ServPoint;
import com.aimir.cms.service.CmsService;
import com.aimir.cms.ws.client.AddDebtResp;
import com.aimir.cms.ws.client.DataLoadResp;
import com.aimir.cms.ws.client.GetDebtResp;
import com.aimir.cms.ws.client.MeterCheckResp;
import com.aimir.cms.ws.client.MeterImportResp;
import com.aimir.cms.ws.client.SaveAllResp;
import com.aimir.cms.ws.client.SearchResp;
import com.aimir.cms.ws.client.UpdateDebtResp;

/**
 * CMS WebService
 *
 * @author goodjob
 * @version $Rev: 1 $, $Date: 2014-06-16 13:00:00 +0900 $,
 */
@WebService(serviceName="CmsWS")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
@Service(value="cmsWS")
public class CmsWS
{
    protected static Log log = LogFactory.getLog(CmsWS.class);
    
    @Autowired
    protected CmsService cmsService;
    
    /**
     * Search web method
     * @param authCred
     * @param searchType
     * @param cmsEnt
     * @return
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="SearchResp") SearchResp search(@WebParam(name="AuthCred") AuthCred authCred,
    							@WebParam(name="SearchType") String searchType,
    							@WebParam(name="CMSEnt") CMSEnt cmsEnt)
            throws com.aimir.cms.exception.CMSException
    {
    	List<CMSEnt> searchResult = new ArrayList<CMSEnt>();
        SearchResp response = new SearchResp();
    	ErrorParam errorParam = new ErrorParam();
    	errorParam.setErrorId((long) ErrorType.NoError.getIntValue());
    	try{
        	searchResult = cmsService.search(authCred, searchType, cmsEnt);
    	}catch(com.aimir.cms.exception.CMSException e){
    		if(e.getFaultInfo() != null && e.getFaultInfo().getErrorParam() != null){
    		    errorParam = e.getFaultInfo().getErrorParam();
    		    searchResult.add(cmsEnt);
    		}else{
    			throw e;
    		}
    	}

    	response.setErrorParam(errorParam);
    	response.setSearchResult(searchResult);
        return response;
    }

    /**
     * SaveAll web method
     * @param authCred
     * @param cmsEnt
     * @return
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="SaveAllResp") SaveAllResp saveAll(@WebParam(name="AuthCred") AuthCred authCred,
			  			  @WebParam(name="CMSEnt") CMSEnt cmsEnt,
			  			  @WebParam(name="SaveAllType") String saveAllType)
            throws com.aimir.cms.exception.CMSException
    {
    	CMSEnt value = new CMSEnt();
    	SaveAllResp response = new SaveAllResp();
    	ErrorParam errorParam = new ErrorParam();
    	errorParam.setErrorId((long) ErrorType.NoError.getIntValue());
    	try{
    	    // TODO 2014.12.18 add savealltype
        	value = cmsService.saveAll(authCred, cmsEnt, saveAllType);
    	}catch(com.aimir.cms.exception.CMSException e){
    		if(e.getFaultInfo() != null && e.getFaultInfo().getErrorParam() != null){
        		errorParam = e.getFaultInfo().getErrorParam();
        		if (saveAllType.contains("customer") || saveAllType.contains("enroll")) {
        		    value.setCustomer(new CustEnt());
        		    value.getCustomer().setCustomerId(cmsEnt.getCustomer().getCustomerId());
        		}
        		if (saveAllType.contains("serv_point") || saveAllType.contains("meter") || saveAllType.contains("roll")) {
        		    value.setSerivcePoint(new ServPoint());
        		    value.getSerivcePoint().setServPointId(cmsEnt.getSerivcePoint().getServPointId());
        		}
    		}else{
    			throw e;
    		}
    	}

    	response.setErrorParam(errorParam);
    	response.setCMSEnt(value);
        return response;
    }
    
    /**
     * DataLoad web method
     * @param authCred
     * @param cmsEnt
     * @return
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="DataLoadResp") DataLoadResp dataLoad(
            @WebParam(name="AuthCred") AuthCred authCred,
			   				@WebParam(name="CMSEnt") CMSEnt cmsEnt)
            throws com.aimir.cms.exception.CMSException
    {
        CMSEnt value = new CMSEnt();
        DataLoadResp response = new DataLoadResp();
    	ErrorParam errorParam = new ErrorParam();
    	errorParam.setErrorId((long) ErrorType.NoError.getIntValue());
    	try{
            value = cmsService.dataLoad(authCred, cmsEnt);
    	}catch(com.aimir.cms.exception.CMSException e){
    		if(e.getFaultInfo() != null && e.getFaultInfo().getErrorParam() != null){
        		errorParam = e.getFaultInfo().getErrorParam();
        		value = cmsEnt;
    		}else{
    			throw e;
    		}
    	}

    	response.setErrorParam(errorParam);
    	response.setCMSEnt(value);
        return response;
    }
    
    /**
     * MeterCheck web method
     * @param authCred
     * @param meterEnt
     * @return
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="MeterCheckResp") MeterCheckResp meterCheck(
            @WebParam(name="AuthCred") AuthCred authCred,
			  				   @WebParam(name="MeterEnt") MeterEnt meterEnt)
            throws com.aimir.cms.exception.CMSException
    {
        MeterEnt value = new MeterEnt();
        MeterCheckResp response = new MeterCheckResp();
    	ErrorParam errorParam = new ErrorParam();
    	errorParam.setErrorId((long) ErrorType.NoError.getIntValue());
    	try{
    		value = cmsService.meterCheck(authCred, meterEnt);
    	}catch(com.aimir.cms.exception.CMSException e){
    		if(e.getFaultInfo() != null && e.getFaultInfo().getErrorParam() != null){
        		errorParam = e.getFaultInfo().getErrorParam();
        		// value = meterEnt;
    		}else{
    			throw e;
    		}
    	}

    	response.setErrorParam(errorParam);
    	response.setMeterEnt(value);
        return response;
    }
    
    /**
     * MeterImport web method
     * @param authCred
     * @param meterEnt
     * @return
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="MeterImportResp") MeterImportResp meterImport(
            @WebParam(name="AuthCred") AuthCred authCred,
    						@WebParam(name="MeterEnt") MeterEnt meterEnt)
            throws com.aimir.cms.exception.CMSException
    {

    	MeterImportResp response = new MeterImportResp();
    	ErrorParam errorParam = new ErrorParam();
    	errorParam.setErrorId((long) ErrorType.NoError.getIntValue());
    	try{
    		cmsService.meterImport(authCred, meterEnt);
    	}catch(com.aimir.cms.exception.CMSException e){
    		if(e.getFaultInfo() != null && e.getFaultInfo().getErrorParam() != null){
        		errorParam = e.getFaultInfo().getErrorParam();
    		}else{
    			throw e;
    		}
    	}

    	response.setErrorParam(errorParam);
        return response;
    }
    
    /**
     * AddDebt web method
     * @param authCred
     * @param debtEnt
     * @return
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="AddDebtResp") AddDebtResp addDebt(
            @WebParam(name="AuthCred") AuthCred authCred,
			  			   @WebParam(name="DebtEnt") DebtEnt debtEnt)
            throws com.aimir.cms.exception.CMSException
    {
    	AddDebtResp response = new AddDebtResp();
    	ErrorParam errorParam = new ErrorParam();
    	errorParam.setErrorId((long) ErrorType.NoError.getIntValue());
    	try{
        	cmsService.addDebt(authCred, debtEnt);
    	}catch(com.aimir.cms.exception.CMSException e){
    		if(e.getFaultInfo() != null && e.getFaultInfo().getErrorParam() != null){
        		errorParam = e.getFaultInfo().getErrorParam();
    		}else{
    			throw e;
    		}
    	}

    	response.setErrorParam(errorParam);
    	response.setDebtEnt(debtEnt);
        return response;
    }
    
    /**
     * UpdateDebt web method
     * @param authCred
     * @param debtEnt
     * @return
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="UpdateDebtResp") UpdateDebtResp updateDebt(
            @WebParam(name="AuthCred") AuthCred authCred,
			   				  @WebParam(name="DebtEnt") DebtEnt debtEnt)
            throws com.aimir.cms.exception.CMSException
    {
    	DebtEnt value = new DebtEnt();
        UpdateDebtResp response = new UpdateDebtResp();
    	ErrorParam errorParam = new ErrorParam();
    	errorParam.setErrorId((long) ErrorType.NoError.getIntValue());
    	try{
    		value = cmsService.updateDebt(authCred, debtEnt);
    	}catch(com.aimir.cms.exception.CMSException e){
    		if(e.getFaultInfo() != null && e.getFaultInfo().getErrorParam() != null){
        		errorParam = e.getFaultInfo().getErrorParam();
        		value = debtEnt;
    		}else{
    			throw e;
    		}
    	}

    	response.setErrorParam(errorParam);
    	response.setDebtEnt(value);
        return response;
    }
    
    /**
     * GetDebt web method
     * @param authCred
     * @param debtEnt
     * @return
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="GetDebtResp") GetDebtResp getDebt(
            @WebParam(name="AuthCred") AuthCred authCred,
				  				 @WebParam(name="DebtEnt") DebtEnt debtEnt)
            throws com.aimir.cms.exception.CMSException
    {
    	List<DebtEnt> debtResult = new ArrayList<DebtEnt>();       
        GetDebtResp response = new GetDebtResp();
    	ErrorParam errorParam = new ErrorParam();
    	errorParam.setErrorId((long) ErrorType.NoError.getIntValue());
    	try{
    		debtResult = cmsService.getDebt(authCred, debtEnt);
    	}catch(com.aimir.cms.exception.CMSException e){
    		if(e.getFaultInfo() != null && e.getFaultInfo().getErrorParam() != null){
        		errorParam = e.getFaultInfo().getErrorParam();
        		if("Customer Information is empty in Prepayment System".equals(e.getFaultInfo().getErrorParam().getErrorMsg())
        				|| "Contract Information is empty in Prepayment System".equals(e.getFaultInfo().getErrorParam().getErrorMsg())
        				|| "Not debt".equals(e.getFaultInfo().getErrorParam().getErrorMsg())) {
        			debtEnt = null;
        			debtResult.add(debtEnt);
        		} 
        		
    		}else{
    			throw e;
    		}
    	}

    	response.setErrorParam(errorParam);
    	response.setDebtResult(debtResult);
        return response;
    }
  
}