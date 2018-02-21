package com.aimir.fep.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.nip.command.ModemEventLog;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.NIAttributeId;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;

import net.sf.json.JSONObject;


@Service
public class GetLogCommandMulti {
    private static Log log = LogFactory.getLog(GetLogCommandMulti.class);
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    MeterDao meterDao;

    @Autowired
    SupplierDao supplierDao;
    
    private String 		_filepath = "";
    private int  		_maxThreadWorker = 10;
    private long 		_timeout = 3600;
    private int			_count =0;
    private int			_offset=-1;
    private int			_testMode=0;
    
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"/config/spring-fep-schedule.xml"}); 
        DataUtil.setApplicationContext(ctx);
        GetLogCommandMulti task = ctx.getBean(GetLogCommandMulti.class);
        log.info("======================== GetLogCommandMulti start. ========================");
        task.execute(args);
        log.info("======================== GetLogCommandMulti end. ========================");
        System.exit(0);
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

    private List<McuDeviceList> getMcuListfromModemFile(int deviceMeter) {
    	/*
    	 * Output List<McuMeterList> is list of McuMeterList. 
    	 * Output meters is list of meter which doesn't have a mcu.
    	 */
    	try{
    		List<String > inputList = new ArrayList<String>();
    	    FileReader fr = null;
    	    BufferedReader br = null;
    	    
    	    log.debug("current dir" + new File(".").getAbsolutePath());
    	    
	        try {
		        fr = new FileReader(_filepath);
		        
	    	    br = new BufferedReader(fr);
	    	    
		        String line;
		        while ((line = br.readLine()) != null) {
		        	inputList.add(line);
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
	        
	    	log.debug("getMcuListfromModemFile(Modem Serial List[" + inputList.size() + "])");
	    	List<McuDeviceList> mcuList = new ArrayList<McuDeviceList>();

	    	int idx = 0;
	        for (idx=0; idx < inputList.size(); idx++) {
            	Modem modem = modemDao.get(inputList.get(idx));
				if (modem == null) {
					log.debug("Modem[" + inputList.get(idx) + "] . Modem is null .");
	        		continue;
	        	}

				Set meterSet = modem.getMeter();
				Meter meter = null;
				if (meterSet != null && meterSet.size() > 0) {
					meter = (Meter) meterSet.toArray()[0];
            	}
				else {
					log.debug("Modem[" + inputList.get(idx) + "] . Meter is null .");
					if ( deviceMeter==1 )
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
		log.info("ARG_0[" + args[0] + "] ARG_1[" + args[1] + "] ARG_2[" + args[2] + "] ARG_3[" + args[3] + 
				"] ARG_4[" + args[4] +"] ARG_5[" + args[5] + "]");
    	        
        // COUNT
        if (args[0].length() > 0) {
        	_count = Integer.parseInt(args[0]);   
        }
        // OFFSET(for FW 1.2)
        if (args[1].length() > 0) {
        	_offset = Integer.parseInt(args[1]);  
        }
        // FILE
        if (args[2].length() > 0) {
        	_filepath = args[2];
        }
        // MAX_THREAD_WORKER
        if (args[3].length() > 0) {
            _maxThreadWorker = Integer.parseInt(args[3]);        	
        }
        // TIMEOUT
        if (args[4].length() > 0) {
            _timeout = Integer.parseInt(args[4]);        	
        }
        // TESTMODE
        if (args[5].length() > 0) {
        	_testMode = Integer.parseInt(args[5]);        	
        }
        
        log.info("Start GetLogCommandMulti maxThreadWorker[" + _maxThreadWorker + "]");

        
        try {
			List<McuDeviceList> mculist = new ArrayList<McuDeviceList>();
			if (_filepath.length() > 0) {
    			mculist = getMcuListfromModemFile(0);
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
	        String attrParam="";
	        
	        if (_offset < 0) {
	        	attrParam = Hex.decode(DataUtil.get2ByteToInt(_count));
	        }
	        else {
	        	// For FW Version is 1.2
	        	attrParam = Hex.decode(DataUtil.get2ByteToInt(_count)) +
	        			Hex.decode(DataUtil.get2ByteToInt(_offset));        	
	        }			

        	// Execute Command
            ExecutorService pool = Executors.newFixedThreadPool(_maxThreadWorker);
            GetLogCommandThread threads[] = new GetLogCommandThread[mculist.size()];
        	int	i = 0;
    		int cnt = 0;
        
        	for (McuDeviceList mcu : mculist) {
        		log.info(cnt++ + ": MCU[" + mcu.mcuId + "] Set ");

        		threads[i] = new GetLogCommandThread(
        				mcu,
        				Hex.decode(NIAttributeId.ModemEventLog.getCode()),
        				attrParam);
        		pool.execute(threads[i]);
        		i++;
        	}

            log.info("ExecutorService for mcu shutdown.");
            pool.shutdown();
            log.info("ExecutorService for mcu awaitTermination. [" + _timeout + "]sec");
            pool.awaitTermination(_timeout, TimeUnit.SECONDS);                
        }
        catch (Exception ee) {
        	log.debug(ee.getMessage());
        }
        finally {
        }
    }
    
    protected class GetLogCommandThread extends Thread {
    	McuDeviceList mcuDevices;
    	String attrId;
    	String attrParam;
    	
    	CommandGW commandGw = DataUtil.getBean(CommandGW.class);
    	
    	public GetLogCommandThread(McuDeviceList deviceList, String id, String param) {
    		try {
	    		this.mcuDevices = deviceList;
	    		this.attrId = id;
	    		this.attrParam = param;
            }
            catch (Exception ee) {}
    	}
    	public void run() {
        	log.info("ThreadID[" + Thread.currentThread().getId() + "] GetLogCommandThread thread start. MCU[" + mcuDevices.mcuId + "]");

			Map<String, Object> result = new HashMap<String, Object>();
			int i =0;
			
			List<String> deviceList = mcuDevices.deviceIdList;
	        long time = System.currentTimeMillis(); 
			SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = dayTime.format(new Date(time));
			String resultStr = "--------------------------------------------------------------------------------------------\n"
					+ "Execute date: " + date 
					+ "\n--------------------------------------------------------------------------------------------";			

			try {
				result=commandGw.cmdExecDmdNiCommandMulti( 
						"GET", 
						attrId,
						attrParam,
						(String[])deviceList.toArray(new String[0])
						);
				
				result.get("RESULT");
				result.get("cmdDesc");
				
		        for(String modemSerial : deviceList){
		        	resultStr += "\n### Target : " +modemSerial + " ### \n";
		        	log.info("ThreadID[" + Thread.currentThread().getId() + "] ###Target :" + modemSerial + "###");

		        	Map map = new HashMap<String, String>();
		        	map = (Map)result.get(modemSerial);
		        	
		        	if(map.containsKey("AttributeData#1")){
						ModemEventLog modemEventLog = new ModemEventLog();
						JSONObject jo = JSONObject.fromObject(map.get("AttributeData#1"));
						byte[] bx = Hex.encode(String.valueOf(jo.get("Value")));
						modemEventLog.decode(bx);											
						resultStr += modemEventLog.toString() + "\n";
						log.info("ThreadID[" + Thread.currentThread().getId() + "] " + modemEventLog.toString());
					}else{
						resultStr += "[Fail] communication error \n";
						log.info("[Fail][" + modemSerial + "] communication error");
					}        	

		        	i++;
		        }        
		        makeResultFile(resultStr+"--------------------------------------------------------------------------------------------\n\n");		
   	
        	} catch (Exception e){
        		log.info("ThreadID[" + Thread.currentThread().getId() + "] GetLogCommandThread thread end. MCU[" + mcuDevices.mcuId + "] is failed.");
        	}	
    	}
    }        	
	
	synchronized private void makeResultFile(String resultStr){
		String fileName = "./log/GetLogResult.txt";
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
	
//	private void setTargetList() {
//        InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream(fileName);
//        if (fileInputStream != null) {
//            targetList = new LinkedList<String>();
//
//            @SuppressWarnings("resource")
//            Scanner scanner = new Scanner(fileInputStream);
//            while (scanner.hasNextLine()) {
//                String target = scanner.nextLine().trim();
//                if(!target.equals("")){
//                    targetList.add(target);                 
//                }
//            }
//            log.info("Target List:" + "("+targetList.size()+")" + targetList.toString());
//        } else {
//            log.info("File not found");
//        }
//    }
	

}
