package com.aimir.fep.schedule.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
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

/**
 * 모뎀 정보를 가져온다. 나중에 버전 정보가 없는 모뎀만 조회하여 스캐닝하는 기능 추가해야 함.
 * @author elevas
 *
 */
@Service
public class SetCmdMeterOBIS 
{
    private static Log log = LogFactory.getLog(SetCmdMeterOBIS.class);
    
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

    int		supplierId;    
    private String _filepath = "TargetMeterList.txt";
    private String		_obisaction = "set";
    private String		_obisparam;

    
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"/config/spring-fep-schedule.xml"}); 
        DataUtil.setApplicationContext(ctx);
        SetCmdMeterOBIS task = ctx.getBean(SetCmdMeterOBIS.class);
        log.info("======================== SetCmdMeterOBIS start. ========================");
		writeResult("start--------------------------------------------------");
        task.execute(args);
        log.info("======================== SetCmdMeterOBIS end. ========================");
		writeResult("end  --------------------------------------------------");
        System.exit(0);
    }

	public static void writeResult(String resultStr){
		String fileName = "./log/SetMeterOBIS_rlt.txt";
		
        try{
            File file = new File(fileName) ;
            FileWriter fw = new FileWriter(file, true) ;
            fw.write(resultStr +"\n");
            fw.flush();
            fw.close(); 
        }catch(Exception e){
            log.error(e);
        }
	}

    public class McuDeviceList {
    	Integer mcuId;
    	List<String> deviceIdList;
    	List<String> 	meterMdsIdList;
    	
    	public boolean equals(Object obj){
    		McuDeviceList t = (McuDeviceList)obj;
    		if (this.mcuId == t.mcuId) return true;
    		else return false;
    	}
    }

    private List<McuDeviceList> getMcuListfromFile() {
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
						mcuList.get(i).meterMdsIdList.add(mdsIDList.get(idx));
						mcuList.get(i).deviceIdList.add(modem.getDeviceSerial());
						continue;
					}
					McuDeviceList mml = new McuDeviceList();
					mml.mcuId = mcu.getId();
					mml.deviceIdList = new ArrayList<String>();
					mml.deviceIdList.add(modem.getDeviceSerial());
					mml.meterMdsIdList = new ArrayList<String>();
					mml.meterMdsIdList.add(mdsIDList.get(idx));
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
        
        _maxThreadWorker = Integer.parseInt(FMPProperty.getProperty("soria.cmd.obis.maxworker", "10"));
        _timeout = Integer.parseInt(FMPProperty.getProperty("soria.cmd.obis.timeout", "3600"));
        _filepath = FMPProperty.getProperty("soria.cmd.obis.target.file", "TargetMeterList.txt");
        _obisaction = FMPProperty.getProperty("soria.cmd.obis", "set");
        _obisparam = FMPProperty.getProperty("soria.cmd.obis.param", "");
        
        log.info("start:" + args.toString());
        log.info("Start Set Meter OBIS maxThreadWorker[" + _maxThreadWorker + "]" );

        log.debug("soria.cmd.obis.maxworker => " + _maxThreadWorker);
        log.debug("soria.cmd.obis.timeout => " + _timeout);
        log.debug("soria.cmd.obis.target.file => " + _filepath);
        log.debug("soria.cmd.obis => " + _obisaction);
        log.debug("soria.cmd.obis.param => " + _obisparam);
        try {
        	if ( _obisparam.isEmpty() ){
        		log.info("parameter is nothing. soria.cmd.obis.param=****");
				return;
        	}
			List<McuDeviceList> mculist = new ArrayList<McuDeviceList>();
			mculist = getMcuListfromFile();
    			
			if (mculist.size() == 0) {
				log.info("Target meter is none.");
				return;
			}
			
        	log.info("Total MCU to need time sync ["+ mculist.size() + "]");

            ExecutorService pool = Executors.newFixedThreadPool(_maxThreadWorker);
            execMeterOBISThread threads[] = new execMeterOBISThread[mculist.size()];
        	int	i = 0;
    		int cnt = 0;
    		
        
        	for (McuDeviceList mcu : mculist) {
        		log.info(cnt++ + ": MCU[" + mcu.mcuId + "] Set setMeterOBIS");

        		threads[i] = new execMeterOBISThread(mcu, _obisparam);
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
    

    protected class execMeterOBISThread extends Thread {
    	McuDeviceList mcuDevices;
    	String	parameter;
    	CommandGW commandGw = DataUtil.getBean(CommandGW.class);
    	
    	public execMeterOBISThread(McuDeviceList deviceList, String param) {
    		try {
	    		this.mcuDevices = deviceList;
	    		this.parameter = param;
            }
            catch (Exception ee) {}
    	}
    	public void run() {
        	log.info("ThreadID[" + Thread.currentThread().getId() + "] execMeterOBISThread thread start. MCU[" + mcuDevices.mcuId + "]");

        	// setMeterOBIS is need set to modem one by one
        	try {
    			List<String> modemList = mcuDevices.deviceIdList;
    			List<String> meterMdsList = mcuDevices.meterMdsIdList;
        		
        		for (int i = 0; i < modemList.size() ; i++) {
        			log.debug("ThreadID[" + Thread.currentThread().getId() + "] execMeterOBISThread Meter["+meterMdsList.get(i) + "],[" + modemList.get(i) +"]");
        			Map<String,Object> result = new HashMap<String,Object>();
        			if ( _obisaction.equals("set") ){
        				result = commandGw.cmdMeterParamSet(modemList.get(i), parameter);
            			log.debug("ThreadID[" + Thread.currentThread().getId() + "] [cmdMeterParamSet] Result = Meter["+meterMdsList.get(i) + "],[" + modemList.get(i) + "]="+result.toString());
        			}else{
        				result = commandGw.cmdMeterParamGet(modemList.get(i), parameter);
            			log.debug("ThreadID[" + Thread.currentThread().getId() + "] [cmdMeterParamGet] Result = Meter["+meterMdsList.get(i) + "],[" + modemList.get(i) + "]="+result.toString());
        			}
        			Object obj = null;
        			int ret = 0;
        			obj = result.get("RESULT_VALUE");
        			if (obj instanceof String) {
        				writeResult("Meter["+meterMdsList.get(i)+"]["+modemList.get(i)+"] ret="+result.get("RESULT_VALUE").toString());
        			}else{
        				writeResult("Meter["+meterMdsList.get(i)+"]["+modemList.get(i)+"] ret=Fail");
        			}
        		}
        	} catch (Exception e){
        		log.info("ThreadID[" + Thread.currentThread().getId() + "] execMeterOBISThread thread end. MCU[" + mcuDevices.mcuId + "] is failed.");
        	}
    	}
    } 
}
