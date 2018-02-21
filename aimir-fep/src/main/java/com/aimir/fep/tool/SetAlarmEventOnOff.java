package com.aimir.fep.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
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

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.EventAlertDao;
import com.aimir.dao.device.EventAlertLogDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.NIAttributeId;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.EventAlert;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.system.Supplier;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;

import net.sf.json.JSONObject;


@Service
public class SetAlarmEventOnOff
{
    private static Log log = LogFactory.getLog(SetAlarmEventOnOff.class);
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    MeterDao meterDao;

    @Autowired
    SupplierDao supplierDao;

    @Autowired
	EventAlertDao eventAlertDao;
    
    @Autowired
    EventAlertLogDao eventAlertLogDao;
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;

    private String 		supplierName;
    int		supplierId;     
    private int			_beforeTime = 24;    
    private String 		_filepath = "";
    private int			_threshold = 3;
    private int  		_maxThreadWorker = 10;
    private long 		_timeout = 3600;
    private String		_commands = "";
    private int			_count =0;
    private String		_version = "";
    private int			_testMode=0;
    
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"/config/spring-fep-schedule.xml"}); 
        DataUtil.setApplicationContext(ctx);
        SetAlarmEventOnOff task = ctx.getBean(SetAlarmEventOnOff.class);
        log.info("======================== SetAlarmEventOnOff start. ========================");
        task.execute(args);
        log.info("======================== SetAlarmEventOnOff end. ========================");
        System.exit(0);
    }    

    private List<Object> getTargetList(String fromDate, String toDate)
	{
        TransactionStatus txstatus = null;	
		List<Object> list = new ArrayList<Object>();
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

			
			Set<Condition> condition1 = new HashSet<Condition>();
			condition1.add(new Condition("name", new Object[] {"Power Alarm"}, null, Restriction.EQ));
			List<EventAlert> evList = eventAlertDao.findByConditions(condition1);
			if (evList.isEmpty()) {
				log.error("Can not find event alert ID.");
				return null;
			}
			EventAlert alert = evList.get(0);
			
			Set<Condition> condition3 = new HashSet<Condition>();
			condition3.add(new Condition("supplier.id", new Object[] {supplierId}, null, Restriction.EQ));
			condition3.add(new Condition("eventAlertId", new Object[] {alert.getId()}, null, Restriction.EQ));
			condition3.add(new Condition("activatorType", new Object[] {TargetClass.EnergyMeter}, null, Restriction.EQ));			
			condition3.add(new Condition("openTime", new Object[] { fromDate+"000000", toDate+"235959" }, null, Restriction.BETWEEN));

			log.debug("Conditions [supplier.id = " + supplierId + 
					"], [eventAlertId = " + alert.getId() + 
					"], [activatorType = EnergyMeter" +
					"], [openTime between (" + fromDate+"000000" + "," + toDate+"235959" + ")]" );
			List<EventAlertLog> listEventAlertLog = eventAlertLogDao.findByConditions(condition3);

			Map<String, Object> map = new HashMap<String, Object>();
			int count;
			String meterId;
			
			for(EventAlertLog eventLog : listEventAlertLog) {
				meterId = eventLog.getActivatorId();
				if(!map.containsKey(meterId)){
					map.put(meterId, 1);
					if (_testMode == 1) {
						log.debug("MeterID:" + meterId + "Count:1");
					}
				}
				else {
					count = (int)map.get(meterId);
					count = count + 1;
					map.replace(meterId, count);
					if (_testMode == 1) {
						log.debug("MeterID:" + meterId + "Count:" + count);
					}
					if (count == _threshold) {
						if (_testMode == 1) {
							log.debug("MeterID:" + meterId + " event count is equal to the threshold.");
						}
						// Check SW Version
						if (!_version.isEmpty()) {
							Meter meter = meterDao.get(meterId);
							if (meter == null) {
								log.debug("MeterID[" + meterId + "] is get error. continue.");
								continue;
							}
							if (_version.equals(meter.getSwVersion())){
								HashMap<String, String> resultMap = new HashMap<String, String>();
								resultMap.put("mdsId", meterId);
								list.add(resultMap);								
							}
							else {
								log.debug("MeterID[" + meterId + "] SW Version[" + meter.getSwVersion() + "]. SW Version is not target version." );
							}
						}
						else {
							log.debug("All version is target.");
							HashMap<String, String> resultMap = new HashMap<String, String>();
							resultMap.put("mdsId", meterId);
							list.add(resultMap);							
						}						
					}
				}				
			}
						
	    	txmanager.commit(txstatus);
    	}
    	catch  (Exception e) {
    		log.error("RecollectThread.run error - " + e, e);
    		if (txstatus != null&& !txstatus.isCompleted())
    			txmanager.rollback(txstatus);
    	}
    	return list;
	}    
    
    public class McuDeviceList {
    	Integer mcuId;
    	List<String> deviceIdList;
    	String	meterMdsId;
    	Map deviceMap = new HashMap<String, String>();
    	
    	public boolean equals(Object obj){
    		McuDeviceList t = (McuDeviceList)obj;
    		if (this.mcuId == t.mcuId) return true;
    		else return false;
    	}
    }
        
    private List<McuDeviceList> getMcuList(List<Object> dataList, int deviceMeter) {
    	/*
    	 * Output List<McuMeterList> is list of McuMeterList. 
    	 * Output meters is list of meter which doesn't have a mcu.
    	 */
    	try{
    		log.debug("getMcuList(dataList[" + dataList.size() + "])");

    		List<Object> orgList = new ArrayList<Object>();
	    	List<McuDeviceList> mcuList = new ArrayList<McuDeviceList>();
	    	orgList.addAll(dataList);
	    	dataList.clear();
	    	
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
							if (modem.getDeviceSerial() != null) {
								mcuList.get(i).deviceMap.put(mdsId, modem.getDeviceSerial());
							}
						} else {	// modem
							mcuList.get(i).deviceIdList.add(modem.getDeviceSerial());
							if (mdsId != null) {
								mcuList.get(i).deviceMap.put(modem.getDeviceSerial(), mdsId);
							}
						}
						continue;
					}
					McuDeviceList mml = new McuDeviceList();
					mml.mcuId = mcu.getId();
					mml.deviceIdList = new ArrayList<String>();
					if ( deviceMeter==1 ){	// meter
						mml.deviceIdList.add(mdsId);
						if (modem.getDeviceSerial() != null) {
							mml.deviceMap.put(mdsId, modem.getDeviceSerial());
						}
					}else{	// modem
						mml.deviceIdList.add(modem.getDeviceSerial());
						if (mdsId != null) {
							mml.deviceMap.put(modem.getDeviceSerial(), mdsId);
						}
					}
					mml.meterMdsId = mdsId;
					mcuList.add(mml);
				} else {
					dataList.add(obj); 
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
							if (modem.getDeviceSerial() != null) {
								mcuList.get(i).deviceMap.put(meter.getMdsId(), modem.getDeviceSerial());
							}
						} else {	// modem
							mcuList.get(i).deviceIdList.add(modem.getDeviceSerial());
							if (meter.getMdsId() != null) {
								mcuList.get(i).deviceMap.put(modem.getDeviceSerial(), meter.getMdsId());
							}
						}
						continue;
					}
					McuDeviceList mml = new McuDeviceList();
					mml.mcuId = mcu.getId();
					mml.deviceIdList = new ArrayList<String>();
					if ( deviceMeter==1 ){	// meter
						mml.deviceIdList.add(meter.getMdsId());
						if (modem.getDeviceSerial() != null) {
							mml.deviceMap.put(meter.getMdsId(), modem.getDeviceSerial());
						}
					}else{	// modem
						mml.deviceIdList.add(modem.getDeviceSerial());
						if (meter.getMdsId() != null) {
							mml.deviceMap.put(modem.getDeviceSerial(), meter.getMdsId());
						}
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
		log.info("ARG_0[" + args[0] + "] ARG_1[" + args[1] + "] ARG_2[" + args[2] + "] ARG_3[" + args[3] + 
				"] ARG_4[" + args[4] +"] ARG_5[" + args[5] +"] ARG_6[" + args[6] + "] ARG_7[" + args[7] + "]");
    	        
        // COUNT
        if (args[0].length() > 0) {
        	_count = Integer.parseInt(args[0]);   
        }
        // COMMAND(delimiter:"|")
        if (args[1].length() > 0) {
        	_commands = args[1];   
        }
        // VERSION
        if (args[2].length() > 0) {
        	_version = args[2];     
        }        
        // THRESHOLD
        if (args[3].length() > 0) {
        	_threshold = Integer.parseInt(args[3]);     
        }
        // BEFORETIME
        if (args[4].length() > 0) {
            _beforeTime = Integer.parseInt(args[4]);
        }
        // FILE
        if (args[5].length() > 0) {
        	_filepath = args[5];
        }
        // MAX_THREAD_WORKER
        if (args[6].length() > 0) {
            _maxThreadWorker = Integer.parseInt(args[6]);        	
        }
        // TIMEOUT
        if (args[7].length() > 0) {
            _timeout = Integer.parseInt(args[7]);        	
        }
        // TESTMODE
        if (args[8].length() > 0) {
        	_testMode = Integer.parseInt(args[8]);        	
        }
        
        log.info("Start Set AlarmEventOnOff maxThreadWorker[" + _maxThreadWorker + "]");

        
        try {
            String		fromDate;
            String		toDate;
            
			Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, (-24-(_beforeTime-24)));
			String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");	
			fromDate = TFDate.substring(0,8);
			TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
			TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");		
			toDate = TFDate.substring(0,8);

			List<McuDeviceList> mculist = new ArrayList<McuDeviceList>();
			if (_filepath.length() > 0) {
    			mculist = getMcuListfromFile(0);
			}
			else {
				List<Object> targetList = getTargetList(fromDate, toDate);
	    		log.info("Total Meter to need set ["+ targetList.size() + "]");
	    		if(targetList != null && targetList.size() > 0){
	    			mculist = getMcuList(targetList, 0);
	    		}
			}    			
    					
			if (mculist.size() == 0) {
				log.info("Target meter is none.");
				return;
			}

			log.info("Total MCU to need set ["+ mculist.size() + "]");

			if (_testMode == 1) {
				log.info("######################## Test Mode ########################");
	        	for (McuDeviceList mcu : mculist) {
	        		int cnt = 0;
	        		log.info(cnt++ + ": MCU[" + mcu.mcuId + "] Set ");
		            for(String modemSerial : mcu.deviceIdList){
		            	log.info("###Target :" + modemSerial + "###");
		            	Modem modem = modemDao.get(modemSerial);
		            	Set meterSet = modem.getMeter();
		            	if (meterSet != null && meterSet.size() > 0) {
							Meter meter = (Meter) meterSet.toArray()[0];
			            	log.info("###MeterID :" + meter.getMdsId() + "###");
			            	log.info("###SW Version :" + meter.getSwVersion() + "###");		            	
						}
		            	else {
		            		log.info("Meter does not exists.");
		            	}
		            }
	        	}
				log.debug("###########################################################");
				return;
			}
			
			// Set Attribute parameters
            String 		attrParam;
	        StringTokenizer tokenizer = new  StringTokenizer(_commands, "|");
	        List<String> cmdList = new ArrayList<String>();
	        while (tokenizer.hasMoreTokens()){
	        	cmdList.add(tokenizer.nextToken());
	        }
	        
	        if (_count != cmdList.size()) {
				log.info("Count and Command parameters is invalid.");
				return;
	        }
	        
	        byte[] byteCount = new byte[1];
	        byteCount[0] = DataUtil.getByteToInt(_count);
        	attrParam = Hex.decode(byteCount);
        	for (String cmd : cmdList) {
        		attrParam = attrParam + cmd;      	
        	}
			
        	// Execute Command
            long time = System.currentTimeMillis();         	
			SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = dayTime.format(new Date(time));
			String resultStr = "--------------------------------------------------------------------------------------------\n"
					+ "Execute date: " + date 
					+ "\nCOUNT: " + _count 
					+ "\nCOMMAND: " + _commands 					
					+ "\nVERSION: " + _version 					
					+ "\nTHRESHOLD: " + _threshold 					
					+ "\nFROM: " + fromDate+"000000"  					
					+ "\nTO: " + toDate+"235959" 					
					+ "\n--------------------------------------------------------------------------------------------";	        	
        	
			makeResultFile(resultStr);
        	
            ExecutorService pool = Executors.newFixedThreadPool(_maxThreadWorker);
            SetAlarmEventOnOffThread threads[] = new SetAlarmEventOnOffThread[mculist.size()];
        	int	i = 0;
    		int cnt = 0;
        
        	for (McuDeviceList mcu : mculist) {
        		log.info(cnt++ + ": MCU[" + mcu.mcuId + "] Set ");

        		threads[i] = new SetAlarmEventOnOffThread(
        				mcu,
        				Hex.decode(NIAttributeId.Alarm_EventCommandON_OFF.getCode()),
        				attrParam);
        		pool.execute(threads[i]);
        		i++;
        	}

            log.info("ExecutorService for mcu shutdown.");
            pool.shutdown();
            log.info("ExecutorService for mcu awaitTermination. [" + _timeout + "]sec");
            pool.awaitTermination(_timeout, TimeUnit.SECONDS);
            
            makeResultFile("\n--------------------------------------------------------------------------------------------\n\n");
        }
        catch (Exception ee) {
        	log.debug(ee.getMessage());
        }
        finally {
        }
    }
    
    protected class SetAlarmEventOnOffThread extends Thread {
    	McuDeviceList mcuDevices;
    	String attrId;
    	String attrParam;
    	
    	CommandGW commandGw = DataUtil.getBean(CommandGW.class);
    	
    	public SetAlarmEventOnOffThread(McuDeviceList deviceList, String id, String param) {
    		try {
	    		this.mcuDevices = deviceList;
	    		this.attrId = id;
	    		this.attrParam = param;
            }
            catch (Exception ee) {}
    	}
    	public void run() {
        	log.info("ThreadID[" + Thread.currentThread().getId() + "] SetAlarmEventOnOff thread start. MCU[" + mcuDevices.mcuId + "]");

			Map<String, Object> result = new HashMap<String, Object>();
			List<String> deviceList = mcuDevices.deviceIdList;    			

			try {

				result=commandGw.cmdExecDmdNiCommandMulti(
						"SET", 
						Hex.decode(NIAttributeId.Alarm_EventCommandON_OFF.getCode()), 
						attrParam, 
						(String[])deviceList.toArray(new String[0])
						);

	    		result.get("RESULT");
	    		result.get("cmdDesc");
	    		
	            for(String modemSerial : deviceList){
	            	log.info("ThreadID[" + Thread.currentThread().getId() + "] ###Target :" + modemSerial + "###");
	            	Map map = new HashMap<String, String>();
	            	map = (Map)result.get(modemSerial);
	            	
	            	if(map.containsKey("AttributeData#1")){
	    				JSONObject jo = JSONObject.fromObject(map.get("AttributeData#1"));
	    				log.info("ThreadID[" + Thread.currentThread().getId() + "]" + jo.toString());
	    				String meterID = "";
	    				if (mcuDevices.deviceMap.get(modemSerial) != null) {
	    					meterID = (String)mcuDevices.deviceMap.get(modemSerial);
	    				}
	    				makeResultFile("\n" + modemSerial + "," + meterID );
	    			}else{
	    				log.info("ThreadID[" + Thread.currentThread().getId() + "]" + "[Fail][" + modemSerial + "] communication error");
	    			}        	
	            }            			    			
   	
        	} catch (Exception e){
        		log.info("ThreadID[" + Thread.currentThread().getId() + "] SetAlarmEventOnOff thread end. MCU[" + mcuDevices.mcuId + "] is failed.");
        	}	
    	}
    }        

	synchronized private void makeResultFile(String resultStr){
		String fileName = "./log/SetAlarmEventOnOffResult.txt";
        try{
            File file = new File(fileName) ;
            FileWriter fw = new FileWriter(file, true) ;
            fw.write(resultStr);
            fw.flush();
            fw.close(); 
        }catch(Exception e){
            log.error(e);
        }
	}    
}