package com.aimir.fep.iot.saver;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.w3c.dom.NodeList;

import com.aimir.dao.iot.CustDao;
import com.aimir.dao.iot.DcuDao;
import com.aimir.dao.iot.ElectrictLogDao;
import com.aimir.dao.iot.ElectrictSensorDao;
import com.aimir.dao.iot.EventDao;
import com.aimir.dao.iot.HeartBeatDao;
import com.aimir.dao.iot.IotOperationLogDao;
import com.aimir.dao.iot.LocDao;
import com.aimir.dao.iot.PamperDao;
import com.aimir.dao.iot.SensorDao;
import com.aimir.dao.mvm.MeasurementHistoryDao;
import com.aimir.fep.iot.domain.resources.ContentInstance;
import com.aimir.fep.iot.utils.CommonCode.DEVICE_TYPE;
import com.aimir.fep.iot.utils.CommonCode.EVENT_TYPE;
import com.aimir.fep.iot.utils.CommonCode.HRM_STATUS_C1002;
import com.aimir.fep.iot.utils.CommonCode.NETWORK_PROTOCOL;
import com.aimir.fep.iot.utils.CommonCode.SENSOR_KIND;
import com.aimir.fep.iot.utils.CommonCode.SENSOR_TYPE;
import com.aimir.fep.iot.utils.DateUtil;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.iot.CustTbl;
import com.aimir.model.iot.DcuTbl;
import com.aimir.model.iot.ElectrictLog;
import com.aimir.model.iot.ElectrictSensor;
import com.aimir.model.iot.EventTbl;
import com.aimir.model.iot.HeartBeatTbl;
import com.aimir.model.iot.IotOperationLog;
import com.aimir.model.iot.LocTbl;
import com.aimir.model.iot.PamperTbl;
import com.aimir.model.iot.SensorTbl;

@Service
public class ContentInstanceDataSaver {
	
	private static final Log logger = LogFactory.getLog(ContentInstanceDataSaver.class);
	
	private boolean isSos = false;
	private String eventMsg = "";
	private String sosMsg = "";
	private String currentTime = DateUtil.getCurrentDateTime();
	
    @Autowired
    DcuDao dcuDao;
    
    @Autowired
    SensorDao sensorDao;
    
    @Autowired
    LocDao locDao;
    
    @Autowired
    EventDao eventDao;
    
    @Autowired
    CustDao custDao;
    
    @Autowired
    HeartBeatDao heartBeatDao;
    
    @Autowired
    PamperDao pamperDao;
    
	@Autowired
	MeasurementHistoryDao measurementHistoryDao;
	
	@Autowired
	ElectrictSensorDao electrictSensorDao;
	
	@Autowired
	ElectrictLogDao electrictLogDao;
	
	@Autowired
	ProcessorHandler processorHandler;
	
	@Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
	@Autowired
	IotOperationLogDao iotOperationLogDao;

	
	public void CiBleInfoDataSave(ContentInstance cntIns, String[] bleInfo, String[] msgInfo, String[] pairId) {
		
		TransactionStatus txstatus = null;
	   	try {	
	   		txstatus = txmanager.getTransaction(null);
	   		String pairIdTemp = "";
	   		if(pairId[0].length() != 14){
		    	pairIdTemp ="0000" + pairId[0];
		    }else{
		    	pairIdTemp = pairId[0];
		    }

	   		SensorTbl sensor = new SensorTbl();
	   		if (bleInfo[0] != null){
	   			sensor.setSid(bleInfo[0]);
	   			sensor.setDid(bleInfo[0]);
	   			//sensor.setTypecd(SENSOR_TYPE.WEARABLE.getValue());
		    	//sensor.setKindcd(SENSOR_KIND.SUBGIGA.getValue());
	   			
	   			sensor.setMsgtype(msgInfo[0]);
	   			sensor.setPairid(pairIdTemp);
	   			sensor.setLastcommdt(cntIns.getCreationTime());
		   		
	   			sensorDao.saveOrUpdate(sensor);
	   			sensorDao.flushAndClear();
	   		}
	   		
			txmanager.commit(txstatus);
		} catch (Exception e) {
			logger.error("[Exception] BleInfoDataSave is error :"+ e.getMessage());
			if (txstatus != null) txmanager.rollback(txstatus);
        	throw e;
		}
	}
	
	public void CiSensorInfoDataSave(ContentInstance cntIns, String[] sensorInfo, String[] vibInfo, String statusInfo, String[] degreeInfo, String[] noiseInfo, String[] tempInfo, String[] humInfo, String[] e_periodInfo) {

		TransactionStatus txstatus = null;
	   	try {	
	   		txstatus = txmanager.getTransaction(null);
	   		
	   		ElectrictSensor electrictSensor = new ElectrictSensor();
	   		if (sensorInfo[0] != null) {
		   		electrictSensor.setSid(sensorInfo[0]);
		   		electrictSensor.setVib(Double.valueOf(vibInfo[1]));
		   		electrictSensor.setStatus(statusInfo);
		   		electrictSensor.setDegreex(Double.valueOf(degreeInfo[1]));
		   		electrictSensor.setDegreey(Double.valueOf(degreeInfo[2]));
		   		electrictSensor.setNoise(Integer.parseInt(noiseInfo[1]));
		   		electrictSensor.setTempin(Integer.parseInt(tempInfo[1]));
		   		electrictSensor.setTempout(Integer.parseInt(tempInfo[2]));
		   		electrictSensor.setTempstate(Integer.parseInt(tempInfo[1]) - Integer.parseInt(tempInfo[2]));
		   		electrictSensor.setHum(Integer.parseInt(humInfo[1]));
		   		electrictSensor.setTempperiod(Integer.parseInt(e_periodInfo[0]));
		   		electrictSensor.setVibperiod(Integer.parseInt(e_periodInfo[1]));
		   		electrictSensor.setDegreeperiod(Integer.parseInt(e_periodInfo[2]));
		   		electrictSensor.setNoiseperiod(Integer.parseInt(e_periodInfo[3]));
		   		electrictSensor.setHumperiod(Integer.parseInt(e_periodInfo[4]));
		   		electrictSensor.setCreatedt(cntIns.getCreationTime());
		   		electrictSensor.setModifydt(currentTime);
		   		
		   		electrictSensorDao.saveOrUpdate(electrictSensor);
		   		electrictSensorDao.flushAndClear();
	   		}
	   		
	   		ElectrictLog electrictLog = new ElectrictLog();
	   		if (sensorInfo[0] != null) {
	   			electrictLog.setSid(sensorInfo[0]);
	   			electrictLog.setDegreex(Double.valueOf(degreeInfo[1]));
	   			electrictLog.setDegreey(Double.valueOf(degreeInfo[2]));
	   			electrictLog.setTempin(Integer.parseInt(tempInfo[1]));
	   			electrictLog.setTempout(Integer.parseInt(tempInfo[2]));
	   			electrictLog.setVib(Double.valueOf(vibInfo[1]));
	   			electrictLog.setNoise(Integer.parseInt(noiseInfo[1]));
	   			electrictLog.setCreatedt(cntIns.getCreationTime());
	   			electrictLog.setModifydt(currentTime);
	   			electrictLog.setHum(Integer.parseInt(humInfo[1]));
	   			
	   			electrictLogDao.saveOrUpdate(electrictLog);
		   		electrictLogDao.flushAndClear();
	   		}
			txmanager.commit(txstatus);
		} catch (Exception e) {
			logger.error("[Exception] CiSensorInfoDataSave is error :"+ e.getMessage());
			if (txstatus != null) txmanager.rollback(txstatus);
        	throw e;
		}
	}
	
	public void CiDcuConfDataSave(String seq, String[] cnts, String ct) {
		
		TransactionStatus txstatus = null;
	   	try {	
	   		txstatus = txmanager.getTransaction(null);

	   		DcuTbl dcu = dcuDao.get(cnts[0]);
	   		if (dcu != null){
		   		dcu.setDcuId(cnts[0]);
		   		dcu.setSeverIp(cnts[1]);
		   		dcu.setServerPort(Integer.parseInt(cnts[2]));
		   		dcu.setFwVer(cnts[3]);
		   		dcu.setLocalIp(cnts[4]);
		   		dcu.setLastCommDt(currentTime);
		   		dcu.setModifyDt(currentTime);
		   		dcu.setCreateDt(ct);
		   		
				dcuDao.update(dcu);
				dcuDao.flushAndClear();
	   		} else {
	   			logger.error("["+seq+"] CiDcuConfDataSave dcu is null ~~");
	   		}
	   		
			txmanager.commit(txstatus);
		} catch (Exception e) {
			logger.error("[Exception] DcuConfDataSave error :"+ e.getMessage());
			if (txstatus != null) txmanager.rollback(txstatus);
        	throw e;
		}
	}
	
	
	public PamperTbl CiPamperDataSave(PamperTbl pamper, ContentInstance cntIns, String sensorInfo[], Double hrmRate, Double hrmMin, Double hrmMax, String hrmTm, String statusInfo) {
		
		TransactionStatus txstatus = null;
	   	try {	
	   		txstatus = txmanager.getTransaction(null);

	   		pamper.setYear(cntIns.getCreationTime().substring(0, 4));
	   		pamper.setMonth(cntIns.getCreationTime().substring(4, 6));
	   		pamper.setDay(cntIns.getCreationTime().substring(6, 8));
	   		pamper.setHhmmss(cntIns.getCreationTime().substring(8, 14));
	   		pamper.setYyyymmddhhmmss(cntIns.getCreationTime());
	   		pamper.setCreatedt(cntIns.getCreationTime());
	   		pamper.setDeviceno(sensorInfo[0]); // 웨어러블 정보
	   		pamper.setRate(hrmRate);
	   		pamper.setParentdevicetype(DEVICE_TYPE.DCU.getValue());
			
	   		pamper.setParentdeviceid(cntIns.getParentID());
	   		pamper.setMinthreshold(hrmMin);
	   		pamper.setMaxthreshold(hrmMax);
	   		pamper.setHrmtm(hrmTm);
	   		pamper.setEventcd(statusInfo);
			
			pamperDao.add(pamper);
			locDao.flushAndClear();
			
			txmanager.commit(txstatus);
		} catch (Exception e) {
			logger.error("[Exception] PamperDataSave is error :"+ e.getMessage());
			if (txstatus != null) txmanager.rollback(txstatus);
        	throw e;
		}
	return pamper;
	}
	
	public EventTbl CiPamperEventDataSave(ContentInstance cntIns, PamperTbl pamper, EventTbl event, NodeList nodes, String sensorInfo[], Double hrmRate, Double hrmMin, Double hrmMax, String hrmTm) {
		
		TransactionStatus txstatus = null;
	   	try {
	   		txstatus = txmanager.getTransaction(null);
	   		
	   		event.setYear(cntIns.getCreationTime().substring(0, 4));
			event.setMonth(cntIns.getCreationTime().substring(4, 6));
			event.setDay(cntIns.getCreationTime().substring(6, 8));
			event.setHhmmss(cntIns.getCreationTime().substring(8, 14));
			event.setYyyymmddhhmmss(cntIns.getCreationTime());
			event.setCreatedt(cntIns.getCreationTime());
			event.setDeviceid(sensorInfo[0]);
			event.setParentdeviceid(cntIns.getParentID());
			event.setRestoreflag("C");
			
			//비정상 또는 정지일 경우 분기하기 위한  변수
			boolean isabnomal=false;
			//심박수 0인 경우 분기
			if(hrmRate==0.0){
				pamper.setStatus(HRM_STATUS_C1002.STOP_HRM.getValue());
				event.setEventcd(EVENT_TYPE.UNKNOWN.getCode());
				isabnomal=false;
			}else{
				int retMinVal = Double.compare(hrmMin, hrmRate);
				int retMaxVal = Double.compare(hrmRate, hrmMax);
				
				if(retMinVal > 0){
					pamper.setStatus(HRM_STATUS_C1002.LOW_HRM.getValue());//2
					event.setEventcd(EVENT_TYPE.ABN_HRM.getCode());  //심박이상 시 eventcode셋팅
					isabnomal=true;
					//저심박 SOS 차병준
					this.isSos = true;
					this.sosMsg = "SOS 심박이상호출";
				}else if(retMaxVal > 0){
					pamper.setStatus(HRM_STATUS_C1002.HIGH_HRM.getValue());//3
					event.setEventcd(EVENT_TYPE.ABN_HRM.getCode());  //심박이상 시 eventcode셋팅
					isabnomal=true;
					//고심박 SOS 차병준
					this.isSos = true;
				}else{
					pamper.setStatus(HRM_STATUS_C1002.NORMAL.getValue());//1
					//2016 12 13 차병준 추가
					event.setEventcd(EVENT_TYPE.UNKNOWN.getCode());
				}
			}
			eventDao.add(event);
			locDao.flushAndClear();
			
			txmanager.commit(txstatus);
		} catch (Exception e) {
			logger.error("[Exception] HeartBeatEventDataSave is error :"+ e.getMessage());
			if (txstatus != null) txmanager.rollback(txstatus);
        	throw e;
		}
	return event;
	}
	
	public HeartBeatTbl CiHeartBeatDataSave(HeartBeatTbl heartBeat, ContentInstance cntIns, String sensorInfo[], Double hrmRate, Double hrmMin, Double hrmMax, String hrmTm, String statusInfo) {
		
		TransactionStatus txstatus = null;
	   	try {	
	   		txstatus = txmanager.getTransaction(null);
	   		
	   		hrmMin = 40.0;//MIN_THRESHOLD
	   		hrmMax = 150.0;//MAX_THRESHOLD
	   		
	   		heartBeat.setYear(cntIns.getCreationTime().substring(0, 4));
	   		heartBeat.setMonth(cntIns.getCreationTime().substring(4, 6));
	   		heartBeat.setDay(cntIns.getCreationTime().substring(6, 8));
	   		heartBeat.setHhmmss(cntIns.getCreationTime().substring(8, 14));
	   		heartBeat.setYyyymmddhhmmss(cntIns.getCreationTime());
	   		heartBeat.setCreatedt(cntIns.getCreationTime());
	   		heartBeat.setDeviceno(sensorInfo[0]); // 웨어러블 정보
	   		heartBeat.setRate(hrmRate);
	   		heartBeat.setParentdevicetype(DEVICE_TYPE.DCU.getValue());
			
	   		heartBeat.setParentdeviceid(cntIns.getParentID());
	   		heartBeat.setMinthreshold(hrmMin);
	   		heartBeat.setMaxthreshold(hrmMax);
	   		heartBeat.setHrmtm(hrmTm);
	   		heartBeat.setEventcd(statusInfo);
			
			heartBeatDao.add(heartBeat);
			heartBeatDao.flushAndClear();
			
			txmanager.commit(txstatus);
		} catch (Exception e) {
			logger.error("[Exception] HeartBeatDataSave is error :"+ e.getMessage());
			if (txstatus != null) txmanager.rollback(txstatus);
        	throw e;
		}
	return heartBeat;
	}
	
	public EventTbl CiHeartBeatEventDataSave(ContentInstance cntIns, HeartBeatTbl heartBeat, EventTbl event, NodeList nodes, String sensorInfo[], Double hrmRate, Double hrmMin, Double hrmMax, String hrmTm) {
		
		TransactionStatus txstatus = null;
	   	try {	
	   		txstatus = txmanager.getTransaction(null);
	   		
	   		String custNo = custDao.getCustomerFromDeviceNo(sensorInfo[0]);
			CustTbl customer = custDao.get(custNo);

			event.setYear(cntIns.getCreationTime().substring(0, 4));
			event.setMonth(cntIns.getCreationTime().substring(4, 6));
			event.setDay(cntIns.getCreationTime().substring(6, 8));
			event.setHhmmss(cntIns.getCreationTime().substring(8, 14));
			event.setYyyymmddhhmmss(cntIns.getCreationTime());
			event.setCreatedt(cntIns.getCreationTime());
			event.setDeviceid(sensorInfo[0]);
			event.setParentdeviceid(cntIns.getParentID());
			event.setRestoreflag("C");
			
			//비정상 또는 정지일 경우 분기하기 위한  변수
			boolean isabnomal=false;
			//심박수 0인 경우 분기
			if(hrmRate==0.0){
				heartBeat.setStatus(HRM_STATUS_C1002.STOP_HRM.getValue());
				event.setEventcd(EVENT_TYPE.UNKNOWN.getCode());
				isabnomal=false;
			}else{
				int retMinVal = Double.compare(hrmMin, hrmRate);
				int retMaxVal = Double.compare(hrmRate, hrmMax);
				
				if(retMinVal > 0){
					heartBeat.setStatus(HRM_STATUS_C1002.LOW_HRM.getValue());//2
					event.setEventcd(EVENT_TYPE.ABN_HRM.getCode());  //심박이상 시 eventcode셋팅
					isabnomal=true;
					//저심박 SOS 차병준
					this.isSos = true;
					this.sosMsg = "SOS 심박이상호출";
				}else if(retMaxVal > 0){
					heartBeat.setStatus(HRM_STATUS_C1002.HIGH_HRM.getValue());//3
					event.setEventcd(EVENT_TYPE.ABN_HRM.getCode());  //심박이상 시 eventcode셋팅
					isabnomal=true;
					//고심박 SOS 차병준
					this.isSos = true;
				}else{
					heartBeat.setStatus(HRM_STATUS_C1002.NORMAL.getValue());//1
					//2016 12 13 차병준 추가
					event.setEventcd(EVENT_TYPE.UNKNOWN.getCode());
				}
				
				// 임계치에 의한 정상/비정상 판단
				//2017 03 08 차병준
				//xml 노드체크
				if(nodes.item(0) != null){
					if(FMPProperty.getProperty("is.kepco").equals("false") && FMPProperty.getProperty("is.relay").equals("false")){
						//CustomerVO custVO = custDao.selectCustNo(hbVO.getDEVICE_NO());//차후 쿼리 수정 (by ask)

						 /* 2017 03 15 차병준
						 * cust가 등록 안된 상태에서 cust_no get을 하면 에러 발생 하여 custVO가 NULL이 아닌구간에서 GET 접근 하게 수정*/
						if(customer != null){
							heartBeat.setCustno(customer.getCustno());
							if (!heartBeat.getStatus().equals(HRM_STATUS_C1002.NORMAL.getValue())){
								/*HashMap<String, String> tmp = new HashMap<String, String>();
								tmp.put("cust_no", custVO.getCUST_NO());
								tmp.put("flag", "0");*/
								customer.setCustno(customer.getCustno());
								customer.setStatabnhrm("0");
								custDao.update(customer);
							}
						}
					}
				}
			}
			
			if(isabnomal == true){
				if(FMPProperty.getProperty("is.manage.ip").equals("true")){
					//CustomerVO custVO = custDao.selectCustNo(hbVO.getDEVICE_NO());
					if(customer != null){
						heartBeat.setCustno(customer.getCustno());
						// 이벤트 통보
						if(heartBeat.getStatus().equals("4")){
							//notiService.notification(hbVO.getCUST_NO(), EVENT_TYPE.HRM_STOP.getCode());//by ask
						}else {
							//notiService.notification(hbVO.getCUST_NO(), EVENT_TYPE.ABN_HRM.getCode());//by ask
						}
						
						/*by ask
						 * this.smsKey = customer.getSMS_KEY();
						this.empNo = custVO.getCUST_NO();
						this.recvphone = custVO.getGUARD_CELLPHONE();*/
					}

					/*by ask
					 * this.smsKey = custVO.getSMS_KEY();
					this.empNo = custVO.getCUST_NO();
					this.recvphone = custVO.getGUARD_CELLPHONE();*/
					
				}				
				//eventLogDAO.insertEventLog(eventLogVO);
			}
			
			eventDao.add(event);
			eventDao.flushAndClear();
			
			txmanager.commit(txstatus);
		} catch (Exception e) {
			logger.error("[Exception] HeartBeatEventDataSave is error :"+ e.getMessage());
			if (txstatus != null) txmanager.rollback(txstatus);
        	throw e;
		}
	return event;
	}
	
	public LocTbl CiLocationDataSave(LocTbl loc, ContentInstance cntIns, /*String addr,*/ String sensorInfo[], Double locX, Double locY) {
		
		TransactionStatus txstatus = null;
	   	try {	
	   		txstatus = txmanager.getTransaction(null);
	   		
	   		String custNo = custDao.getCustomerFromDeviceNo(sensorInfo[0]);   		
	   		loc.setYear(cntIns.getCreationTime().substring(0, 4));
		  	loc.setMonth(cntIns.getCreationTime().substring(4, 6));
		  	loc.setDay(cntIns.getCreationTime().substring(6, 8));
		  	loc.setHhmmss(cntIns.getCreationTime().substring(8, 14));
		  	loc.setYyyymmddhhmmss(cntIns.getCreationTime());
		  	loc.setCreatedt(cntIns.getCreationTime());
			loc.setDeviceno(sensorInfo[0]); // 웨어러블 정보
			loc.setGpiox(locX);
			loc.setGpioy(locY);
			if (custNo != null)
				loc.setCustno(custNo);//cust_no set 추가 (by ask)
			
			// GPS정보로 주소명 취득
			/*String addr = locDAO.getAddr(locVO);by ask*/
			//loc.setAddr1(addr);
			loc.setParentdevicetype(DEVICE_TYPE.DCU.getValue());
			loc.setParentdeviceid(cntIns.getParentID());
			
			locDao.add(loc);
			locDao.flushAndClear();
			
			txmanager.commit(txstatus);
		} catch (Exception e) {
			logger.error("[Exception] locationDataSave error : "+ e.getMessage());
			if (txstatus != null) txmanager.rollback(txstatus);
        	throw e;
		}
	return loc;
	}
   
	public SensorTbl CiSensorDataSave(SensorTbl sensor, ContentInstance cntIns, /*String addr,*/ String gpiTm, 
			String sensorInfo[], String periodInfo[], String sensorTm, String statusInfo, String threedTm, String threedInfo[], Double locX, Double locY) {
		
		TransactionStatus txstatus = null;
		try {
			txstatus = txmanager.getTransaction(null);
			
			sensor.setGpitm(gpiTm);
			sensor.setModifydt(currentTime);
			sensor.setCreatedt(cntIns.getCreationTime());
	    	sensor.setSid(sensorInfo[0]);
	    	sensor.setDid(sensorInfo[0]);
	    	sensor.setFwver(sensorInfo[1]);
	    	sensor.setHwver(sensorInfo[2]);
	    	sensor.setVendor(sensorInfo[3]);
	    	sensor.setModel(sensorInfo[4]);
	    	sensor.setTypecd(SENSOR_TYPE.WEARABLE.getValue());
	    	sensor.setKindcd(SENSOR_KIND.SUBGIGA.getValue());
	    	sensor.setNetworkcd(NETWORK_PROTOCOL.SUBGIGA.getValue());
	    	sensor.setParentdevicetype(DEVICE_TYPE.DCU.getValue());
	    	sensor.setParentdeviceid(cntIns.getParentID()); 
	    	//sensor.setAddr(addr);
	    	sensor.setWakeupperiod(Integer.parseInt(periodInfo[0]));
	    	sensor.setGpsperiod(Integer.parseInt(periodInfo[1]));
	    	sensor.setHrmperiod(Integer.parseInt(periodInfo[2]));
	    	sensor.setBeaconperiod(Integer.parseInt(periodInfo[3]));
	    	sensor.setLastcommdt(cntIns.getCreationTime());
	    	sensor.setDcudt(sensorTm);
	    	sensor.setStatus(statusInfo);
	    	sensor.setThreedtm(threedTm);
	    	sensor.setThreediox(threedInfo[1]);
	    	sensor.setThreedioy(threedInfo[2]);
	    	sensor.setThreedioz(threedInfo[3]);
	    	sensor.setThreedpace(threedInfo[4]);
	    	sensor.setRssi(0);
	    	sensor.setFlag("0");
	    	sensor.setGpiox(locX);
	    	sensor.setGpioy(locY);
	    	
	    	sensorDao.saveOrUpdate(sensor);
	    	txmanager.commit(txstatus);
		} catch (Exception e) {
			logger.error("[Exception] sensorDataSave is  error : "+ e.getMessage());
			if (txstatus != null) txmanager.rollback(txstatus);
        	throw e;
		}
		return sensor;
	}
	
	public EventTbl CiEventDataSave(EventTbl event, ContentInstance cntIns, String status[], String sensorInfo[], LocTbl loc, SensorTbl sensor) {
		TransactionStatus txstatus = null;

		try {
			txstatus = txmanager.getTransaction(null);
			/*txstatus = txmanager.getTransaction(
	                new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));*/
			
			String custNo = custDao.getCustomerFromDeviceNo(sensorInfo[0]);
			CustTbl custmer = custDao.get(custNo);
			
			boolean isEvent = false;
			event.setYear(loc.getYear());
			event.setMonth(loc.getMonth());
			event.setDay(loc.getDay());
			event.setHhmmss(loc.getHhmmss());
			event.setYyyymmddhhmmss(loc.getYyyymmddhhmmss());
			event.setCreatedt(cntIns.getCreationTime());
			event.setDeviceid(loc.getDeviceno());
			event.setParentdeviceid(loc.getParentdeviceid());
			
			for(String st : status){
				logger.debug("# status : ["+st+"]");
				if(st.equals("SOS")){
					logger.debug("# sos");
					event.setEventcd(EVENT_TYPE.SOS_CALL.getCode());
					event.setRestoreflag("C");
					isEvent = true;
					this.isSos = true;
					this.eventMsg = "sos";
					this.sosMsg = "SOS 긴급호출";
				}else if(st.equals("LB")){
					logger.debug("# LB");
					event.setEventcd(EVENT_TYPE.LOW_BATT.getCode());
					event.setRestoreflag("C");
					isEvent = true;
					this.eventMsg = "lb";
				}else if(st.equals("TO")){
					logger.debug("# TO");
					event.setEventcd(EVENT_TYPE.TAKE_OFF.getCode());
					event.setRestoreflag("C");
					isEvent = true;
					this.eventMsg = "to";
				}else if(st.equals("CH")){
					logger.debug("# CH");
					event.setEventcd(EVENT_TYPE.CHARGING.getCode());
					event.setRestoreflag("C");
					isEvent = true;
					this.eventMsg = "ch";
				}else if(st.equals("BC")){
					logger.debug("# BC");
					event.setEventcd(EVENT_TYPE.BEACON.getCode());
					event.setRestoreflag("C");
					isEvent = true;
					this.eventMsg = "bc";
				} else {
					logger.debug("# OK");//임시 로직
					event.setEventcd(EVENT_TYPE.NOMAL.getCode());
					event.setRestoreflag("C");
					isEvent = true;
					this.eventMsg = "ok";
				}
				
				/* by ask 현재 필요없는 소스*/
				if(isEvent==true){
					// 이 부분은 추후에 상황을 봐서 생략해도 되는 부분인데 여러 대의 수집센서가 이벤트를 받기 때문에,
					// 같은 이벤트를 중복으로 넣는 경우가 있어 이를 방지하기 위한 로직입니다.
					//int cnt= eventLogDAO.getLastEvent(eventLogVO);
					
					//getCustNoFromDeviceNo quary 다시 짜야함  .. by ask
					//int cnt= eventDao.getEventDataCount(event);
					//String custNo = custDao.getCustNoFromDeviceNo(loc.getDeviceno());
					//CustTbl custmer = custDao.get(custNo);

					//CustomerVO custVO = custDao.selectCustNo(locVO.getDEVICE_NO()); //중복이 아닐 경우 고객정보를 조회하고
					if(custmer != null){//고객정보가 null이 아닐 경우에 SMS나 APP PUSH알림으로 Notification을 보냅니다.
						//sos 이벤트 발생시에만
						//HashMap<String, String> tmp = new HashMap<String, String>();
						//tmp.put("cust_no", custmer.getCustno());
						//tmp.put("flag", "0");
						//custDao.updateLocFlag(tmp);
						
						custmer.setStatoutofloc("0");
						custDao.update(custmer);
						logger.debug("# customer is Update ~~");
						
						
						//eventLogVO.setCUST_NO(custVO.getCUST_NO());
						// 이벤트 통보
						/*if(this.eventMsg == "sos"){
							notiService.notification(eventLogVO.getCUST_NO(), EVENT_TYPE.SOS_CALL.getCode());
						}else if(this.eventMsg == "lb"){
							notiService.notification(eventLogVO.getCUST_NO(), EVENT_TYPE.LOW_BATT.getCode());
						}else if(this.eventMsg == "to"){
							notiService.notification(eventLogVO.getCUST_NO(), EVENT_TYPE.TAKE_OFF.getCode());
						}else if(this.eventMsg == "ch"){
							notiService.notification(eventLogVO.getCUST_NO(), EVENT_TYPE.CHARGING.getCode());
						} by ask*/
					}
					//eventLogDAO.insertEventLog(eventLogVO);
				}
			}
			
			if(FMPProperty.getProperty("is.kepco").equals("false") && FMPProperty.getProperty("is.relay").equals("false")){
				if(sensor.getGpiox()!=null){
					
					/*by ask sql 수정해야됨.
					 * String custNo = custDao.getCustNoFromDeviceNo(loc.getDeviceno());
					CustTbl custmer = custDao.get(custNo);*/
					
					if (loc.getCustno() != null){
						if(isOutLoc(loc.getGpiox(), loc.getGpioy(), loc.getDeviceno(), loc.getCustno())){
							//logger.debug("### isOutLoc EVENT_TYPE.OUTOF_LOC [" + event + "]");
							
							event.setEventcd(EVENT_TYPE.OUTOF_LOC.getCode());
							event.setRestoreflag("C");
							
							custmer.setStatoutofloc("0");
							custDao.update(custmer);
							
							/* 필요없는 소스 by ask
							int cnt= eventDao.getEventDataCount(event);
							logger.debug("### EventDataCount cnt [" + cnt + "]");
							//int cnt= eventLogDAO.getLastEvent(eventLogVO);
							if(cnt==0){
								if(custNo != null){
									custmer.setStatoutofloc("0");
									custDao.update(custmer);
									event.setCustno(custNo);
									
									HashMap<String, String> tmp = new HashMap<String, String>();
									tmp.put("cust_no", custVO.getCUST_NO());
									tmp.put("flag", "0");
									custDao.updateLocFlag(tmp);
									eventLogVO.setCUST_NO(custVO.getCUST_NO());
									// 이벤트 통보
									notiService.notification(eventLogVO.getCUST_NO(), EVENT_TYPE.OUTOF_LOC.getCode());
								}
								//eventDao.add(event);
							}*/
						} else{
							custmer.setStatoutofloc("1");
							custDao.update(custmer);
							
							/*by ask 
							 * CustomerVO custVO = custDao.selectCustNo(locVO.getDEVICE_NO());
							if(custVO != null){
								HashMap<String, String> tmp = new HashMap<String, String>();
								tmp.put("cust_no", custVO.getCUST_NO());
								tmp.put("flag", "1");
								custDao.updateLocFlag(tmp);
							}*/
						}
					}
				}
			}
	
	    	eventDao.add(event);
	    	txmanager.commit(txstatus);
		} catch (Exception e) {
			logger.error("[Exception] EventDataSave is error :"+ e.getMessage());
			if (txstatus != null) txmanager.rollback(txstatus);
        	throw e;
		}
		return event;
	}
	
	private boolean isOutLoc(double x, double y, String devId, String custNo){
		
		CustTbl customer = custDao.get(custNo);
		if(customer!=null && customer.getScope()!=null){
			double scope = Double.parseDouble(customer.getScope()); //반경=반지름
			double gpiox = customer.getAddr3();
			double gpioy = customer.getAddr4();
		
			double distance= calDistance( y, x, gpioy, gpiox);
		
			if(scope - distance<=0){  //반지름 - 중심에서 현재위치까지의 거리가 음수이면 반경 이상(r-d<=0) 
				return true;
			}else{
				return false;
			}
		}
		return false;
	}
	
	public double calDistance(double lat1, double lon1, double lat2, double lon2){  
        
        double theta, dist;  
        theta = lon1 - lon2;  
        dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))   
              * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));  
        dist = Math.acos(dist);  
        dist = rad2deg(dist);  
          
        dist = dist * 60 * 1.1515;   
        dist = dist * 1.609344;    // 단위 mile 에서 km 변환.  
        dist = dist * 1000.0;      // 단위  km 에서 m 로 변환  
      
        return dist;  
    }
	
	// 주어진 도(degree) 값을 라디언으로 변환  
    private double deg2rad(double deg){  
    	return (double)(deg * Math.PI / (double)180d);  
    }  
  
    // 주어진 라디언(radian) 값을 도(degree) 값으로 변환  
    private double rad2deg(double rad){  
    	return (double)(rad * (double)180d / Math.PI);  
    }

}
