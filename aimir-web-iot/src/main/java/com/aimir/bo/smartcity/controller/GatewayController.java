package com.aimir.bo.smartcity.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.dao.iot.DcuDao;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.iot.DcuTbl;

@Controller
public class GatewayController {

	private static Log logger = LogFactory.getLog(GatewayController.class);

	@Autowired
	DcuDao dcuDao;
	
    static public AimirUser getAimirUser(HttpServletResponse response ,HttpServletRequest request){
    	ESAPI.httpUtilities().setCurrentHTTP(request, response);
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
		AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
		AimirUser user = (AimirUser)instance.getUserFromSession();          
		return user;
    }
    
    @RequestMapping(value="/gadget/gateway/getMainParameter")
    public ModelAndView getMainParameter(HttpServletResponse response ,HttpServletRequest request) {
    	ModelAndView mav = new ModelAndView("/gadget/device/gatewayMaxGadget");
    	return mav;
    }    
    
    @RequestMapping(value="/gadget/gateway/getGatewayGrid")
    public ModelAndView getGatewayGrid(HttpServletResponse response ,HttpServletRequest request,
    		@RequestParam(value = "pageNum", defaultValue= "0") String pageNum, @RequestParam(value = "gwId", defaultValue="") String gwId,
    		@RequestParam(value = "searchStartTime", defaultValue="") String searchStartTime, @RequestParam(value = "searchEndTime", defaultValue="") String searchEndTime) {
    	ModelAndView mav = new ModelAndView("jsonView");

    	//gatewayMaxGadget.jsp의 데이터 표현 요소
    	//기존 사회안전망 리턴 값 확인 필요
    	/* 
		<th>Gateway Id</th>
		<th>nCube Ver</th>
		<th>Server IP</th>
		<th>Server Port</th>
		<th>Last Comm. Date</th>
		<th>Installation Date</th>
    	*/
    	logger.debug("HHH - pageNum:"+pageNum+"|gwId:"+gwId+"|searchStartTime:"+searchStartTime+"|searchEndTime:"+searchEndTime);
    	
    	//천체 게이트웨이 개수
    	Map<String, String> conditionMap = new HashMap<String, String>();

    	conditionMap.put("gwId", gwId);
    	conditionMap.put("sLastcommStartDate", searchStartTime);
    	conditionMap.put("sLastcommEndDate", searchEndTime);
    	List<DcuTbl> dcuList = dcuDao.getDcuByCondition(conditionMap);
    	
    	conditionMap.clear();
    	conditionMap.put("page", pageNum);
    	conditionMap.put("gwId", gwId);
    	conditionMap.put("sLastcommStartDate", searchStartTime);
    	conditionMap.put("sLastcommEndDate", searchEndTime);
    	List<Object> dcuGridList = dcuDao.getDcuBySearchGrid(conditionMap);
    	
    	mav.addObject("draw", 0);
    	mav.addObject("recordsTotal", dcuList.size());
    	mav.addObject("recordsFiltered", dcuList.size());
    	mav.addObject("data", dcuGridList);
    	
    	/*
    	String[] array = {"20171124101610", "20171124101510",
    			"20171123101610", "20171123101510",
    			"20171122101610", "20171122101510",
    			"20171121101610", "20171121101510",
    			"20171120101610", "20171120101510",
    	};
    	
    	try {
        	List<Meter> meters = new ArrayList<Meter>();
        	for(int i=0; i<10; i++) {
        		Meter meter = new Meter();
        		meter.setMdsId(BasicEntityIdGenerator.getInstance().generateLongId().substring(0, 10));
        		meter.setInstallDate("20171101101610");
        		meter.setLastReadDate(array[i]);
        		meter.setSwVersion("1.0");
        		
        		meters.add(meter);
        	}
        	mav.addObject("draw", 0);
        	mav.addObject("recordsTotal", dcuList.size());
        	mav.addObject("recordsFiltered", dcuList.size());
        	mav.addObject("data", meters);
        	
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	*/
    	
    	return mav;
    }
    
    @RequestMapping(value="/gadget/gateway/getAllGateway")
    public ModelAndView getAllGateway(HttpServletResponse response ,HttpServletRequest request) {
    	String rtnStr = "";
    	ResultStatus status = ResultStatus.FAIL;
    	ModelAndView mav = new ModelAndView("jsonView");
    	
    	try {
    		Map<String, String> conditionMap = new HashMap<String, String>();
        	List<DcuTbl> dcuList = dcuDao.getDcuByCondition(conditionMap);
        	
        	mav.addObject("dcuList", dcuList);
        	status = ResultStatus.SUCCESS;
    	}catch(Exception e) {
    		rtnStr = e.getMessage();
			status = ResultStatus.FAIL;
    		logger.error(e, e);
    	}
    	
    	mav.addObject("rtnStr", rtnStr);
    	mav.addObject("status", status.name());
    	return mav;
    }
}
