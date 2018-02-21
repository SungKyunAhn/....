package com.aimir.bo.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.fep.iot.utils.CommonUtil;
import com.aimir.model.system.Customer;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Role;
import com.aimir.model.system.User;
import com.aimir.service.system.CustomerManager;
import com.aimir.service.system.OperatorManager;
import com.aimir.util.TimeLocaleUtil;


@Controller
public class CommonController {
	
	@Autowired
	OperatorManager operatorManager;
	
	@Autowired
    CustomerManager customerManager;
	
    private static Log logger = LogFactory.getLog(CommonController.class);
    
    static public AimirUser getAimirUser(HttpServletResponse response ,HttpServletRequest request){
    	ESAPI.httpUtilities().setCurrentHTTP(request, response);
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
		AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
		AimirUser user = (AimirUser)instance.getUserFromSession();          
		return user;
    }
    
    @RequestMapping(value="/gadget/index", method = RequestMethod.GET)
    public ModelAndView getIndex(HttpServletResponse response ,HttpServletRequest request) {
    	AimirUser aimirUser = getAimirUser(response,request);
    	ModelAndView mav = null;
    	
    	if(CommonUtil.isEmpty(aimirUser)) {
    		mav = new ModelAndView("/admin/login");
    	} else {
    		mav = new ModelAndView("/gadget/index");
    	}
    	
    	return mav;
    }
    
    
	@RequestMapping(value="/common/getUserInfo")
	public ModelAndView getUserInfo(HttpServletResponse response ,HttpServletRequest request) {
		AimirUser aimirUser = getAimirUser(response,request);   
		
		long loginUserId = 0;		
		int roleId = 0;
		int customerRole = 0;
		int supplierId = 0;
		int maxMeters =0;
		
		String loginId ="";//Main 화면에서 로그인중인 사용자를 보여주기 위해 추가
		String supplierName = ""; //공급사 이름으로 화면을 편집하기 위해 추가
		User user = null;
		boolean isAdmin = false;
		if(aimirUser!=null && !aimirUser.isAnonymous()){                 
		    loginUserId = aimirUser.getAccountId();
			roleId = aimirUser.getRoleData().getId();
            customerRole = (aimirUser.getRoleData().getCustomerRole() != null && aimirUser.getRoleData().getCustomerRole() == true) ? 1 : 0;
            
            isAdmin = aimirUser.getRoleData().getName().equals("admin");
            
			supplierId = aimirUser.getRoleData().getSupplier().getId();
			supplierName = aimirUser.getRoleData().getSupplier().getName();
			loginId= aimirUser.getLoginId();//Main 화면에서 로그인중인 사용자를 보여주기 위해 추가
			
			User loginUser = null;

	        loginUser = operatorManager.getOperatorByLoginId(loginId);
	        if (loginUser == null)
	            loginUser = customerManager.getCustomersByLoginId(loginId);
	        
	        if(loginUser instanceof Operator)
	            user = operatorManager.getOperator(new Long(loginUserId).intValue());
	        else if(loginUser instanceof Customer)
	            user = customerManager.getCustomer(new Long(loginUserId).intValue());
		}
		
		// 날짜 포맷을 사용자에 맞도록 변경
		String country = aimirUser.getSupplier().getCountry().getCode_2letter();
		String lang    = aimirUser.getSupplier().getLang().getCode_2letter();
		SimpleDateFormat locDateType = (SimpleDateFormat)DateFormat.getDateInstance(DateFormat.SHORT,  new Locale(lang, country));

		SimpleDateFormat formatter = null;
		if(locDateType != null){
			formatter = (SimpleDateFormat)DateFormat.getDateInstance(DateFormat.LONG,  new Locale(lang, country));
		}else{
			formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		}
		
		String currDateTime = formatter.format(Calendar.getInstance( new Locale(lang, country)).getTime());
		
		SimpleDateFormat currentformatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String currTime = TimeLocaleUtil.getLocaleDate(currentformatter.format(Calendar.getInstance().getTime()), lang, country);
		String dbDateTime = currentformatter.format(Calendar.getInstance().getTime());
		ModelAndView mav = new ModelAndView("jsonView");
		
		mav.addObject("operatorId", loginUserId==0?"":Long.toString(loginUserId));
		mav.addObject("roleId"    , roleId    ==0?"":Integer.toString(roleId)   );
		mav.addObject("customerRole",customerRole);
		mav.addObject("supplierId", supplierId==0?"":Integer.toString(supplierId));		
		mav.addObject("supplierName", supplierName);
		mav.addObject("dbDateTime", dbDateTime);
		mav.addObject("currDateTime", currDateTime);
		mav.addObject("currTime", currTime);//전기,가스,수도 모니터링에서 사용
		mav.addObject("loginId", loginId);//Main 화면에서 로그인중인 사용자를 보여주기 위해 추가
		mav.addObject("locDateFormat", locDateType.toPattern());
		mav.addObject("currencySymbol", getCurrencySymbol(new Locale(lang, country)));
		mav.addObject("isFirstLogin", (user != null && user.getIsFirstLogin() == null) ? true : user.getIsFirstLogin());
		mav.addObject("isAdmin", isAdmin);
		if(user != null){
			Integer locationId = user.getLocationId();
			if(locationId !=null){
				mav.addObject("locationId", locationId.toString());
			}
		}
		if(user != null){
			Role role = user.getRole();
			mav.addObject("maxMeters", role.getMaxMeters());
		}
		return mav;
	}
	
	public Map<String, Object> getDateFormat() {
    	
    	Map<String, Object> result = new HashMap<String, Object>();    	
		result.put("dateFormat6", TimeLocaleUtil.getDateFormat(6, TimeLocaleUtil.getLocale().getLanguage(), TimeLocaleUtil.getLocale().getCountry()));
		logger.info("dateFormat: "+TimeLocaleUtil.getDateFormat(6, TimeLocaleUtil.getLocale().getLanguage(), TimeLocaleUtil.getLocale().getCountry()));
		
		result.put("dateFormat8", TimeLocaleUtil.getDateFormat(8, TimeLocaleUtil.getLocale().getLanguage(), TimeLocaleUtil.getLocale().getCountry()));
		logger.info("dateFormat: "+TimeLocaleUtil.getDateFormat(8, TimeLocaleUtil.getLocale().getLanguage(), TimeLocaleUtil.getLocale().getCountry()));
		
		result.put("dateFormat14", TimeLocaleUtil.getDateFormat(14, TimeLocaleUtil.getLocale().getLanguage(), TimeLocaleUtil.getLocale().getCountry()));
    	logger.info("dateFormat: "+TimeLocaleUtil.getDateFormat(14, TimeLocaleUtil.getLocale().getLanguage(), TimeLocaleUtil.getLocale().getCountry()));
    	
    	
    	result.put("numberFormatGroup", TimeLocaleUtil.getDecimalFomrat("group"));
    	logger.info("numberFormat Group: "+ TimeLocaleUtil.getDecimalFomrat("group"));
    	
    	result.put("numberFormatDecimal", TimeLocaleUtil.getDecimalFomrat("decimal"));
    	logger.info("numberFormat Decimal: "+ TimeLocaleUtil.getDecimalFomrat("decimal"));
    	
		return result;       	
    }
	
	public String getCurrencySymbol(Locale locale){
		Currency currency = Currency.getInstance(locale);
		return currency.getSymbol();
	}
}
