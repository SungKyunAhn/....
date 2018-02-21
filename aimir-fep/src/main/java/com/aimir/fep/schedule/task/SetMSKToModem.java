package com.aimir.fep.schedule.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.MCU;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;

/**
 * 모뎀 정보를 가져온다. 나중에 버전 정보가 없는 모뎀만 조회하여 스캐닝하는 기능 추가해야 함.
 * @author elevas
 *
 */
@Service
public class SetMSKToModem 
{
    private static Log log = LogFactory.getLog(SetMSKToModem.class);
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    MeterDao meterDao;

    @Autowired
    SupplierDao supplierDao;
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;

    private String 		supplierName;
    int		supplierId;    
    private int			_beforeTime = 168;    
    private String		fromDate;
    private String		toDate;
    private CommonConstants.DateType dateType;
    private String		meterId;
    private String _filepath = "SetMSKToModemList.txt";

    
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"/config/spring-fep-schedule.xml"}); 
        DataUtil.setApplicationContext(ctx);
        SetMSKToModem task = ctx.getBean(SetMSKToModem.class);
        log.info("======================== SetMSKToModem start. ========================");
        task.execute(args);
        log.info("======================== SetMSKToModem end. ========================");
        System.exit(0);
    }

	private List<Object> getGaps(String fromDate, String toDate)
	{
        TransactionStatus txstatus = null;	
		List<Object> allGaps = new ArrayList<Object>();
    	try {
        	txstatus = txmanager.getTransaction(null);	
    	
			if ( supplierName != null && !"".equals(supplierName)){
				Supplier supplier = supplierDao.getSupplierByName(supplierName);
				if ( supplier == null ){
					throw new Exception("Supplier:" + supplierName + " is not exist.");
				}
				supplierId = supplier.getId();
			}
			else {
				Supplier supplier = supplierDao.getAll().get(0);
				supplierId = supplier.getId();
				log.info("Default Supplier={" + supplier.getName() + "}");
			}
			// calc 24 hour before
	        Calendar calendar = Calendar.getInstance();
	        calendar.setTime(new Date());
	        calendar.add(Calendar.HOUR, (_beforeTime*-1));	        
	        Date before24Date = calendar.getTime();
	        
	    	MeterType[] meterTypes = {MeterType.EnergyMeter};
	    	for ( MeterType meterType : meterTypes){
	    		Map<String, Object> params = new HashMap<String, Object>();  
	    		params.put("channel", 1);
	    		params.put("searchStartDate",fromDate);
	    		params.put("searchEndDate", toDate);
	    		params.put("dateType", CommonConstants.DateType.HOURLY.getCode());
	    		params.put("meterType", meterType.name());
	    		params.put("supplierId", supplierId);
	    		if ( meterId != null && !"".equals(meterId)){
	    			params.put("mdsId", meterId);
	    		}
	    		List<Object> gaps  = null;
	    		if ( dateType == CommonConstants.DateType.DAILY ){
		    		log.debug("getMissingMeters:params={" + params.toString() + "}");
	    			gaps = meterDao.getMissingMeters(params);
	    		}
//	    		else {
//	    			log.debug("getMissingMetersForRecollectByHour:params={" + params.toString() + "}");
//	    			gaps = meterDao.getMissingMetersForRecollectByHour(params);
//	    		}
	    		StringBuffer sbuf = new StringBuffer();
	    		StringBuffer sbuf2 = new StringBuffer();
    			for (Object obj : gaps) {
    				boolean link = false;
    				HashMap<String,Object> resultMap = (HashMap<String, Object>) obj;
    				Meter meter = meterDao.get((String)resultMap.get("mdsId"));
    				if ( meter != null ){
    					Modem modem = meter.getModem();
    					String lastLinkTime = null;
    					if ( modem != null && (lastLinkTime = modem.getLastLinkTime()) != null){
    						Date lastLinkDate = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(lastLinkTime);
    						if ( before24Date.before(lastLinkDate)){
    							allGaps.add(obj);
    		    				sbuf.append("'" + (String) resultMap.get("mdsId") + "',");
    		    				link = true;
    						}
    					}
    				}
    				if ( link == false){
    					sbuf2.append("'" + (String) resultMap.get("mdsId") + "',");
    				}
    			}
    			log.debug("Missing Meters({}):{" + meterType.name() + " ," + sbuf.toString() + "}");
    			log.debug("Missing but Last Link Date is before {" +
       					new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(before24Date) +
	    				"} ({" + meterType.name() +
	    				"}):{" + sbuf2.toString() + "}");	
	    	}
	    	txmanager.commit(txstatus);
    	}
    	catch  (Exception e) {
    		log.error("RecollectThread.run error - " + e, e);
    		if (txstatus != null&& !txstatus.isCompleted())
    			txmanager.rollback(txstatus);
    	}
    	return allGaps;
	}
	
    public class McuDeviceList {
    	Integer mcuId;
    	List<String> deviceIdList;
    	String	meterMdsId;
    	
    	public boolean equals(Object obj){
    		McuDeviceList t = (McuDeviceList)obj;
    		if (this.mcuId == t.mcuId) return true;
    		else return false;
    	}
    }
        
    private List<McuDeviceList> getMcuList(List<Object> gaps, int deviceMeter) {
    	/*
    	 * Output List<McuMeterList> is list of McuMeterList. 
    	 * Output meters is list of meter which doesn't have a mcu.
    	 */
    	try{
    		log.debug("getMcuList(gaps[" + gaps.size() + "])");

    		List<Object> orgList = new ArrayList<Object>();
	    	List<McuDeviceList> mcuList = new ArrayList<McuDeviceList>();
	    	orgList.addAll(gaps);
	    	gaps.clear();
	    	
	        for (Object obj : orgList) {
				HashMap<String,Object> resultMap = (HashMap<String, Object>) obj;
				String mdsId = (String) resultMap.get("mdsId");
				Meter meter = meterDao.get(mdsId);
				Modem modem = meter.getModem();
				if (modem == null) {
					log.debug("Meter[" + mdsId + "] . Modem is null .");
	        		continue;
	        	}
				MCU mcu = modem.getMcu();

				if (mcu != null) {
					log.debug("MCU[" + mcu.getId() + "] . Meter[" + meter.getMdsId() + "] . Modem[" + modem.getDeviceSerial() + "] .");
					int i;
					for (i = 0; i < mcuList.size(); i++) {	// Search the same mcu.
						if ( mcuList.get(i).mcuId.compareTo(mcu.getId())==0 ) { 
							break;
						}
					}
					if (i < mcuList.size()) {	// find!
						log.debug("MCU[" + mcu.getId() + "] has been already listed. Meter[" + mdsId + "],Modem[" + modem.getDeviceSerial() + " is added.");
						if ( deviceMeter==1 ){	// meter
							mcuList.get(i).deviceIdList.add(mdsId);
						} else {	// modem
							mcuList.get(i).deviceIdList.add(modem.getDeviceSerial());
						}
						continue;
					}
					McuDeviceList mml = new McuDeviceList();
					mml.mcuId = mcu.getId();
					mml.deviceIdList = new ArrayList<String>();
					if ( deviceMeter==1 ){	// meter
						mml.deviceIdList.add(mdsId);
					}else{	// modem
						mml.deviceIdList.add(modem.getDeviceSerial());
					}
					mml.meterMdsId = mdsId;
					mcuList.add(mml);
				} else {
					gaps.add(obj); 
				}
	        }
	        return mcuList;
    	}
        catch (Exception e) {
        	log.debug(e.getMessage());
        	return null;
        }   	
    }
    
    private List<McuDeviceList> getMcuList_KOREANENV_TEST(List<Object> gaps, int deviceMeter) {
    	/*
    	 * Output List<McuMeterList> is list of McuMeterList. 
    	 * Output meters is list of meter which doesn't have a mcu.
    	 */
    	try{
	    	log.debug("getMcuList2(gaps[" + gaps.size() + "])");
	    	List<McuDeviceList> mcuList = new ArrayList<McuDeviceList>();
	    	gaps.clear();

	    	int idx = 0;
	        for (idx=0; idx<3; idx++) {
				String mdsId = "";
	        	if (idx==0){
	        		mdsId = "6970631400021313";
	        	} else if (idx==1){
	        		mdsId = "6970631400021344";
	        	} else if (idx==2){
	        		mdsId = "6970631400021351";
	        	}
				Meter meter = meterDao.get(mdsId);
				Modem modem = meter.getModem();
				if (modem == null) {
					log.debug("Meter[" + mdsId + "] . Modem is null .");
	        		continue;
	        	}
				MCU mcu = modem.getMcu();

				if (mcu != null) {
					log.debug("MCU[" + mcu.getId() + "] . Meter[" + meter.getMdsId() + "] . Modem[" + modem.getDeviceSerial() + "] .");
					int i;
					for (i = 0; i < mcuList.size(); i++) {	// Search the same mcu.
						if ( mcuList.get(i).mcuId.compareTo(mcu.getId())==0 ) { 
							break;
						}
					}
					if (i < mcuList.size()) {	// find!
						log.debug("MCU[" + mcu.getId() + "] has been already listed. Meter[" + mdsId + "],Modem[" + modem.getDeviceSerial() + " is added.");
						if ( deviceMeter==1 ){	// meter
							mcuList.get(i).deviceIdList.add(mdsId);
						} else {	// modem
							mcuList.get(i).deviceIdList.add(modem.getDeviceSerial());
						}
						continue;
					}
					McuDeviceList mml = new McuDeviceList();
					mml.mcuId = mcu.getId();
					mml.deviceIdList = new ArrayList<String>();
					if ( deviceMeter==1 ){	// meter
						mml.deviceIdList.add(mdsId);
					}else{	// modem
						mml.deviceIdList.add(modem.getDeviceSerial());
					}
					mml.meterMdsId = mdsId;
					mcuList.add(mml);
				}
	        }
	        return mcuList;
    	}
        catch (Exception e) {
        	log.debug(e.getMessage());
        	return null;
        }   	
    }       

    private List<McuDeviceList> getMcuListfromFile(int deviceMeter) {
    	/*
    	 * Output List<McuMeterList> is list of McuMeterList. 
    	 * Output meters is list of meter which doesn't have a mcu.
    	 */
    	try{
    		List<String > mdsIDList = new ArrayList<String>();
    	    FileReader fr = null;
    	    BufferedReader br = null;
    	    
    	    log.debug("current dir" + new File(".").getAbsolutePath());
    	    
	        try {
		        fr = new FileReader(_filepath);
		        
	    	    br = new BufferedReader(fr);
	    	    
		        String line;
		        while ((line = br.readLine()) != null) {
		        	mdsIDList.add(line);
		        }    		    		
   
	        } catch (Exception e) {
	        	log.debug(e.getMessage());
	        } finally {
	            try {
	                br.close();
	                fr.close();
	            } catch (Exception e) {
	            	log.debug(e.getMessage());
	            }
	        }	        
	        
	    	log.debug("getMcuListfromFile(mdsIDList[" + mdsIDList.size() + "])");
	    	List<McuDeviceList> mcuList = new ArrayList<McuDeviceList>();

	    	int idx = 0;
	        for (idx=0; idx < mdsIDList.size(); idx++) {
				Meter meter = meterDao.get(mdsIDList.get(idx));
				Modem modem = meter.getModem();
				if (modem == null) {
					log.debug("Meter[" + mdsIDList.get(idx) + "] . Modem is null .");
	        		continue;
	        	}
				MCU mcu = modem.getMcu();

				if (mcu != null) {
					log.debug("MCU[" + mcu.getId() + "] . Meter[" + meter.getMdsId() + "] . Modem[" + modem.getDeviceSerial() + "] .");
					int i;
					for (i = 0; i < mcuList.size(); i++) {	// Search the same mcu.
						if ( mcuList.get(i).mcuId.compareTo(mcu.getId())==0 ) { 
							break;
						}
					}
					if (i < mcuList.size()) {	// find!
						log.debug("MCU[" + mcu.getId() + "] has been already listed. Meter[" + meter.getMdsId() + "],Modem[" + modem.getDeviceSerial() + " is added.");
						if ( deviceMeter==1 ){	// meter
							mcuList.get(i).deviceIdList.add(meter.getMdsId());
						} else {	// modem
							mcuList.get(i).deviceIdList.add(modem.getDeviceSerial());
						}
						continue;
					}
					McuDeviceList mml = new McuDeviceList();
					mml.mcuId = mcu.getId();
					mml.deviceIdList = new ArrayList<String>();
					if ( deviceMeter==1 ){	// meter
						mml.deviceIdList.add(meter.getMdsId());
					}else{	// modem
						mml.deviceIdList.add(modem.getDeviceSerial());
					}
					mml.meterMdsId = meter.getMdsId();
					mcuList.add(mml);
				}
	        }
	        return mcuList;
    	}
        catch (Exception e) {
        	log.debug(e.getMessage());
        	return null;
        }   	
    }       
    
    
    public void execute(String[] args) {
        int  _maxThreadWorker = 10;
        long _timeout = 3600;
        
        _maxThreadWorker = Integer.parseInt(FMPProperty.getProperty("soria.setmsk.maxworker", "10"));
        _timeout = Integer.parseInt(FMPProperty.getProperty("soria.setmsk.timeout", "3600"));
        _beforeTime = Integer.parseInt(FMPProperty.getProperty("soria.setmsk.beforetime", "168"));
        _filepath = FMPProperty.getProperty("soria.setmsk.file", "");
        
        log.info("Start Set MSK maxThreadWorker[" + _maxThreadWorker + "]");

        
        try {
			Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
			String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");	
			dateType = CommonConstants.DateType.DAILY;
			fromDate = TFDate.substring(0,8);
			toDate = TFDate.substring(0,8);

			List<McuDeviceList> mculist = new ArrayList<McuDeviceList>();
			if (_filepath.length() > 0) {
    			mculist = getMcuListfromFile(1);
				
			}
			else {
				List<Object> gaps = getGaps(fromDate, toDate);
	    		log.info("Total Meter to need set MSK ["+ gaps.size() + "]");
	    		if(gaps != null && gaps.size() > 0){
	    			mculist = getMcuList(gaps, 1);
	    		}
			}    			
    			
			if (mculist.size() == 0) {
				log.info("Target meter is none.");
				return;
			}
			
        	log.info("Total MCU to need set MSK ["+ mculist.size() + "]");

            ExecutorService pool = Executors.newFixedThreadPool(_maxThreadWorker);
            setMSKThread threads[] = new setMSKThread[mculist.size()];
        	int	i = 0;
    		int cnt = 0;
        
        	for (McuDeviceList mcu : mculist) {
        		log.info(cnt++ + ": MCU[" + mcu.mcuId + "] Set MSK");

        		threads[i] = new setMSKThread(mcu);
        		pool.execute(threads[i]);
        		i++;
        	}

            log.info("ExecutorService for mcu shutdown.");
            pool.shutdown();
            log.info("ExecutorService for mcu awaitTermination. [" + _timeout + "]sec");
            pool.awaitTermination(_timeout, TimeUnit.SECONDS);                
        }
        catch (Exception ee) {}
        finally {
        }
    }
    
    protected class setMSKThread extends Thread {
    	McuDeviceList mcuDevices;
    	CommandGW commandGw = DataUtil.getBean(CommandGW.class);
    	
    	public setMSKThread(McuDeviceList deviceList) {
    		try {
	    		this.mcuDevices = deviceList;
            }
            catch (Exception ee) {}
    	}
    	public void run() {
        	log.info("ThreadID[" + Thread.currentThread().getId() + "] setMSK thread start. MCU[" + mcuDevices.mcuId + "]");

        	// setMsk is need set to modem one by one
        	try {
    			List<String> deviceList = mcuDevices.deviceIdList;
        		
        		for (int i = 0; i < deviceList.size() ; i++) {
        			log.debug("setMSK Meter[" + deviceList.get(i) +"]");
        			commandGw.cmdDmdNiSetMSKToModem(deviceList.get(i));
        		}                      	
        	} catch (Exception e){
        		log.info("ThreadID[" + Thread.currentThread().getId() + "] setMSK thread end. MCU[" + mcuDevices.mcuId + "] is failed.");
        	}	
    	}
    }    
}
