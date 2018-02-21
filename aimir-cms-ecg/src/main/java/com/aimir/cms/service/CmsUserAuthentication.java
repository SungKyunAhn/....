package com.aimir.cms.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncryptionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.cms.constants.CMSConstants.ErrorType;
import com.aimir.cms.exception.CMSException;
import com.aimir.cms.model.AuthCred;
import com.aimir.dao.system.OperatorDao;
import com.aimir.model.system.User;

@Service
@Transactional(value = "transactionManager")
public class CmsUserAuthentication {

    private static Log log = LogFactory.getLog(CmsService.class);
    
    @Autowired
    OperatorDao operatorDao;
	
	public void userAuthentication(AuthCred authCred) throws CMSException{
		
		log.info("CMSInterface Authentication..");
    	if(authCred == null){
    		throw new CMSException(ErrorType.Error.getIntValue(), "AuthCred is empty");
    	}

    	if(authCred.getUserName() == null ||  "".equals(authCred.getUserName())){
    		throw new CMSException(ErrorType.Error.getIntValue(), "UserName is empty");
    	}
    	
		log.info("User Name = "+authCred.getUserName());
    	if(authCred.getPassword() == null || "".equals(authCred.getPassword())){
    		throw new CMSException(ErrorType.Error.getIntValue(), "Password is empty");
    	}   
    	
		log.info("Password = "+authCred.getPassword());
    	
    	verifyAccount(authCred);
		log.info("CMSInterface Authentication End..");
	}
	
	
	public boolean verifyAccount(AuthCred authCred) throws CMSException {

		User loginUser = null;
		try{
		    loginUser = operatorDao.getOperatorByLoginId(authCred.getUserName());
		}catch(Exception e){
			throw new CMSException(ErrorType.Error.getIntValue(), "Internal Service Error = "+e.getMessage());
		}
		if(loginUser == null) {
		   	throw new CMSException(ErrorType.Error.getIntValue(), "UserName is invalid");
		}
		if(loginUser.getPassword().equals(authCred.getPassword())){
		 	return true;
		} else
			try {
				if(loginUser.getPassword().equals(hashPassword(authCred.getPassword(), authCred.getUserName()))){
				   	return true;
				}else{
				   	throw new CMSException(ErrorType.Error.getIntValue(), "Password is invalid");
				}
			} catch (EncryptionException e) {
				throw new CMSException(ErrorType.Error.getIntValue(), "Password Encription Failed");
			}
	}
    
    public String hashPassword(String password, String accountName) throws EncryptionException {
        String salt = accountName.toLowerCase();
        return ESAPI.encryptor().hash(password, salt);
    }

}
