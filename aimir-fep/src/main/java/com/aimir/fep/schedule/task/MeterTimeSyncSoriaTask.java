package com.aimir.fep.schedule.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.command.ws.server.ResponseMap;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.MCU;	// INSERT SP-556
import com.aimir.model.system.Code;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.Condition.Restriction;

/**
 * 모뎀 정보를 가져온다. 나중에 버전 정보가 없는 모뎀만 조회하여 스캐닝하는 기능 추가해야 함.
 * @author elevas
 *
 */
@Service
public class MeterTimeSyncSoriaTask 
{
    private static Log log = LogFactory.getLog(MeterTimeSyncSoriaTask.class);
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    MeterDao meterDao;
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    String _mode = "DCU";	// INSERT SP-443
    
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"/config/spring-fep-schedule.xml"}); 
        DataUtil.setApplicationContext(ctx);
        MeterTimeSyncSoriaTask task = ctx.getBean(MeterTimeSyncSoriaTask.class);
        log.info("======================== MeterTimeSyncSoriaTask start. ========================");
        task.execute(args);
        log.info("======================== MeterTimeSyncSoriaTask end. ========================");
        System.exit(0);
    }

    // UPDATE START #300
//    private List<Meter> getMeter(long timeDiff) throws Exception {
    private List<Meter> getMeter(long diffmin, long diffmax, long difffactory) throws Exception {
    // UPDATE END #300
//        TransactionStatus txstatus = null;		// DELETE #300
        
        try {
//            txstatus = txmanager.getTransaction(null);	// DELETE #300
            Set<Condition> condition = new HashSet<Condition>();           
            Code comError = CommonConstants.getMeterStatusByName(MeterStatus.CommError.name());
            Code powerDown = CommonConstants.getMeterStatusByName(MeterStatus.PowerDown.name());
            
            // UPDATE START #300
//            condition.add(new Condition("timeDiff", new Object[]{timeDiff}, null, Restriction.GE));
            condition.add(new Condition("timeDiff", new Object[]{diffmin}, null, Restriction.GE));
            condition.add(new Condition("timeDiff", new Object[]{diffmax}, null, Restriction.LE));
            // UPDATE END #300
            condition.add(new Condition("meterStatus", new Object[]{comError.getId()}, null, Restriction.NE));
            condition.add(new Condition("meterStatus", new Object[]{powerDown.getId()}, null, Restriction.NE));
            
            List<Meter> tmplist = meterDao.findByConditions(condition);
//            List<Meter> list = meterDao.findByConditions(condition);
            List<Meter> list = new ArrayList<Meter>();
            list.addAll(tmplist);
        	log.debug("########## " + diffmin + " <= meter.timeDiff <= " + diffmax + " Count[" + tmplist.size() + "] ##########" );
            for (Meter m : tmplist) {
            	log.debug("Meter[" + (m.getMdsId()== null ? "" : m.getMdsId()) + "] difftime[" + m.getTimeDiff() + "]");
            }            
            condition = new HashSet<Condition>();
            // UPDATE START #300
//            condition.add(new Condition("timeDiff", new Object[]{-1*timeDiff}, null, Restriction.LE));
            condition.add(new Condition("timeDiff", new Object[]{-1*diffmin}, null, Restriction.LE));
            condition.add(new Condition("timeDiff", new Object[]{-1*diffmax}, null, Restriction.GE));
             // UPDATE END #300
            condition.add(new Condition("meterStatus", new Object[]{comError.getId()}, null, Restriction.NE));
            condition.add(new Condition("meterStatus", new Object[]{powerDown.getId()}, null, Restriction.NE));
            
            tmplist.clear();
            tmplist = meterDao.findByConditions(condition);
//            list.addAll(meterDao.findByConditions(condition));
            list.addAll(tmplist);
        	log.debug("########## " + -1*diffmax + " <= meter.timeDiff <= " + -1*diffmin + " Count[" + tmplist.size() + "] ##########" );
            for (Meter m : tmplist) {
            	log.debug("Meter[" + (m.getMdsId()== null ? "" : m.getMdsId()) + "] difftime[" + m.getTimeDiff() + "]");
            }            

            // INSERT START #300
            // Factory status
            condition = new HashSet<Condition>();
            condition.add(new Condition("timeDiff", new Object[]{difffactory}, null, Restriction.GE));
            condition.add(new Condition("meterStatus", new Object[]{comError.getId()}, null, Restriction.NE));
            condition.add(new Condition("meterStatus", new Object[]{powerDown.getId()}, null, Restriction.NE));
            
            tmplist.clear();
            tmplist = meterDao.findByConditions(condition);
            list.addAll(tmplist);
        	log.debug("########## " + difffactory + "<= meter.timeDiff" + " Count[" + tmplist.size() + "] ##########" );
            for (Meter m : tmplist) {
            	log.debug("Meter[" + (m.getMdsId()== null ? "" : m.getMdsId()) + "] difftime[" + m.getTimeDiff() + "]");
            }            
            
            condition = new HashSet<Condition>();
            condition.add(new Condition("timeDiff", new Object[]{-1*difffactory}, null, Restriction.LE));
            condition.add(new Condition("meterStatus", new Object[]{comError.getId()}, null, Restriction.NE));
            condition.add(new Condition("meterStatus", new Object[]{powerDown.getId()}, null, Restriction.NE));
            
            tmplist.clear();
            tmplist = meterDao.findByConditions(condition);
            list.addAll(tmplist);
        	log.debug("########## " + "meter.timeDiff <= " + -1*difffactory + " Count[" + tmplist.size() + "] ##########" );
            for (Meter m : tmplist) {
            	log.debug("Meter[" + (m.getMdsId()== null ? "" : m.getMdsId()) + "] difftime[" + m.getTimeDiff() + "]");
            }                     
            // INSERT END #300
            
            return list;
        }
        catch (Exception e) {
        	log.debug(e.getMessage());
        	throw e;
        }
        finally {
//            if (txstatus != null) txmanager.commit(txstatus);	// DELETE #300
        }
    }
    
    // INSERT START SP-429
    private void filterModemLastLinkTime(List<Meter> list,  long lastCommCond) {
    	try{
	    	String current = DateTimeUtil.getDateString(new Date());
	    	String lastCommFilter = DateTimeUtil.getPreHour(current, (int)lastCommCond);
	    	List<Meter> orgList = new ArrayList<Meter>();
	    	orgList.addAll(list);
	    	
	    	log.debug("Current[" + current + "], Condition time[" + lastCommFilter + "]");
	        for (Meter m : orgList) {
	        	Modem modem = m.getModem();
	        	// INSERT START SP-640
	        	if (modem == null) {
	        		log.debug("Delete from list Meter[" + m.getMdsId() + "], Modem is null.");
	        		list.remove(m);
	        		continue;
	        	}
	        	// INSERT END SP-640
	        	
	        	//if "modem.last_link_time < lastcommfilter", delete from list.
	        	log.debug("modem.last_link_time=[" + modem.getLastLinkTime() + "]");
	        	if (modem.getLastLinkTime().compareTo(lastCommFilter)<0) {
	        		log.debug("Delete from list Meter[" + m.getMdsId() + "], Modem[" + modem.getDeviceSerial() + "]");
	        		list.remove(m);
	        	}
	        }
        }
        catch (Exception e) {
        	log.debug(e, e);
        }   	
    }
    // INSERT END SP-429

    // INSERT START SP-556
    public class McuModemList {
    	Integer mcuId;
    	List<Modem> modemList;
    }
        
    private List<McuModemList> getMcuList(List<Meter> meters) {
    	/*
    	 * Output List<McuModemList> is list of McuModemList. 
    	 * Output meters is list of meter which doesn't have a mcu.
    	 */
    	try{
	    	log.debug("getMcuList(meters[" + meters.size() + "])");

    		List<Meter> orgList = new ArrayList<Meter>();
	    	List<McuModemList> mcuList = new ArrayList<McuModemList>();
	    	orgList.addAll(meters);
	    	meters.clear();
	    	
	        for (Meter m : orgList) {

	        	Modem modem = m.getModem();
	        	// INSERT START SP-640
	        	if (modem == null) {
					log.debug("Meter[" + m.getMdsId() + "] . Modem is null .");
	        		continue;
	        	}
	        	// INSERT END SP-640
				MCU mcu = modem.getMcu();

				if (mcu != null) {
					log.debug("MCU[" + mcu.getId() + "] . Meter[" + m.getMdsId() + "] . Modem[" + modem.getDeviceSerial() + "] .");
					int i;
					for (i = 0; i < mcuList.size(); i++) {	// Search the same mcu.
						if ( mcuList.get(i).mcuId.compareTo(mcu.getId())==0 ) { 
							break;
						}
					}
					if (i < mcuList.size()) {	// find!
						log.debug("MCU[" + mcu.getId() + "] has been already listed. Modem[" 
									+ modem.getDeviceSerial() + "] is skip.");
						mcuList.get(i).modemList.add(modem);
						continue;
					}
					McuModemList mml = new McuModemList();
					mml.mcuId = mcu.getId();
					mml.modemList = new ArrayList<Modem>();
					mml.modemList.add(modem);
					mcuList.add(mml);
				} else {
					log.debug("MCU is null. Meter[" + m.getMdsId() + "] . Modem[" + modem.getDeviceSerial() + "] .");					
					meters.add(m); 
				}

	        }
	        return mcuList;
    	}
        catch (Exception e) {
        	log.debug(e, e);

        	return null;
        }   	
    }    
    // INSERT END SP-556
    
    public void execute(String[] args) {
    	// UPDATE START #300
    	//long _timeDiff = 0;
    	long _timeDiffMin = 0;
    	long _timeDiffMax = 0;
    	long _timeDiffFactory = 0;
        int  _maxThreadWorker = 10;
        long _timeout = 3600;
        // UPDATE END #300
        long _lastCommCond = 24;	// INSERT SP-429
        int _maxSynctimeModem = 0;	// INSERT SP-556
        int _maxMcuThreadWorker = 10;	// INSERT SP556
        String _onlyMBB = "false";		// INSERT SP-640
        
        TransactionStatus txstatus = null;	// INSERT #300
        
    	// UPDATE START #300
        //_timeDiff = Long.parseLong(FMPProperty.getProperty("soria.meter.synctime.diff", "7"));
        //log.info("Start Meter Time Sync timeDiff[" + _timeDiff + "]");
        _timeDiffMin = Long.parseLong(FMPProperty.getProperty("soria.meter.synctime.diffmin", "7"));
        _timeDiffMax = Long.parseLong(FMPProperty.getProperty("soria.meter.synctime.diffmax", "3600"));
        _timeDiffFactory = Long.parseLong(FMPProperty.getProperty("soria.meter.synctime.difffactory", "504576000"));        
        _maxThreadWorker = Integer.parseInt(FMPProperty.getProperty("soria.meter.synctime.maxworker", "10"));
        _maxMcuThreadWorker = Integer.parseInt(FMPProperty.getProperty("soria.mcu.synctime.maxworker", "10")); // INSERT SP-556
        _timeout = Integer.parseInt(FMPProperty.getProperty("soria.meter.synctime.timeout", "3600"));
        // UPDATE START SP-556
//        _mode = FMPProperty.getProperty("soria.meter.synctime.mode", "DCU");		// INSERT SP-443
        _mode = FMPProperty.getProperty("soria.meter.synctime.mode", "MODEM");
        _maxSynctimeModem = Integer.parseInt(FMPProperty.getProperty("soria.meter.synctime.modem.max", "15"));
        // UPDATE END SP-556
        
        log.info("Start Meter Time Sync maxThreadWorker[" + _maxThreadWorker + "]");
        // UPDATE END #300
        _lastCommCond = Integer.parseInt(FMPProperty.getProperty("soria.meter.synctime.lastcomm", "24"));	// INSERT SP-429
        _onlyMBB = FMPProperty.getProperty("soria.meter.synctime.onlymbb", "false");	// INSERT SP-640
        
        try {
        	txstatus = txmanager.getTransaction(null);	// INSERT #300
        	String meterid = FMPProperty.getProperty("soria.meter.synctime.meterid", "");
        	
        	if (meterid !=null && !"".equals(meterid)) {
        		log.info("Meter[" + meterid + "] sync time. Property file specified.");
        		syncTime(meterid);
        		return;
        	}
        	
        	// UPDATE START #300
//            List<Meter> meterlist = getMeter(_timeDiff);
            List<Meter> meterlist = getMeter(_timeDiffMin,_timeDiffMax,_timeDiffFactory);
            // UPDATE END #300
            log.info("getMeter() size ["+ meterlist.size() + "]");
            
            // INSERT START SP-429
            // filter Modem LastLinkTime
            filterModemLastLinkTime(meterlist, _lastCommCond);
            // INSERT END SP-429            
            
            // UPDATE START SP-556 
            log.info("##### Execution _mode is [" + _mode + "] #####");
            if (_mode.equals("MODEM")) {
            	List<McuModemList> mculist = getMcuList(meterlist);
            	
            	// INSERT START SP-640
            	if (!_onlyMBB.equals("true")) { 
            	// INSERT END SP-640
            	
            	log.info("Total MCU to need time sync ["+ mculist.size() + "]");
            	int cnt = 0;

                ExecutorService pool = Executors.newFixedThreadPool(_maxMcuThreadWorker);
            	mcuSyncTimeThread threads[] = new mcuSyncTimeThread[mculist.size()];
            	int	i = 0;
            
            	for (McuModemList mcuMeter : mculist) {
            		log.info(cnt++ + ": MCU[" + mcuMeter.mcuId + "] sync time");

            		threads[i] = new mcuSyncTimeThread(mcuMeter, _maxSynctimeModem);
            		pool.execute(threads[i]);
            		i++;
            	}

                log.info("ExecutorService for mcu shutdown.");
                pool.shutdown();
                log.info("ExecutorService for mcu awaitTermination. [" + _timeout + "]sec");
                pool.awaitTermination(_timeout, TimeUnit.SECONDS);
                
            	// INSERT START SP-640
            	}
            	// INSERT END SP-640
            }
            // UPDATE END SP-556
            
            log.info("Total Meter to need time sync ["+ meterlist.size() + "]");
            int cnt = 0;

            // INSERT START #300
            ExecutorService pool = Executors.newFixedThreadPool(_maxThreadWorker);
            syncTimeThread threads[] = new syncTimeThread[meterlist.size()];
            int	i = 0;
            // INSERT END #300
            
            for (Meter m : meterlist) {
            	log.info(cnt++ + ": Meter[" + m.getMdsId() + "] sync time");
            	// UPDATE START #300
            	//syncTime(m.getMdsId());
            	threads[i] = new syncTimeThread(m.getMdsId());
            	pool.execute(threads[i]);
            	i++;
            	// UPDATE END #300
            }
            
            // INSERT START #300
            log.info("ExecutorService shutdown.");
            pool.shutdown();
            log.info("ExecutorService awaitTermination. [" + _timeout + "]sec");
            pool.awaitTermination(_timeout, TimeUnit.SECONDS);
    		// INSERT END #300
        }
        catch (Exception ee) {}
        // INSERT START #300
        finally {
            if (txstatus != null && !txstatus.isCompleted())
                txmanager.commit(txstatus);
        }
        // INSERT END #300
    }
    
    // INSERT START #300
    protected class syncTimeThread extends Thread {
    	String meterId;
    	Class clazz = null;
    	AbstractMDSaver saver = null;
    	Meter meter = null;
    	CommandGW commandGw = DataUtil.getBean(CommandGW.class);	// INSERT SP-443
    	Modem modem = null;											// INSERT SP-443
    	
    	public syncTimeThread(String id) {
    		try {
	    		meterId = id;
	            meter = meterDao.get(meterId);
	            modem = meter.getModem();							// INSERT SP-443
	            clazz = Class.forName(meter.getModel().getDeviceConfig().getSaverName());
	            saver = (AbstractMDSaver)DataUtil.getBean(clazz);
            }
            catch (Exception ee) {}
    	}
    	public void run() {
            if (meter.getModel() != null && meter.getModem() != null) {
            	log.info("ThreadID[" + Thread.currentThread().getId() + "] syncTime() thread start. Meter[" + meterId + "]");

            	if (meter.getModem().getMcu() == null) {
            		// UPDATE START SP-443
//                    saver.syncTime(meter.getModem().getDeviceSerial(), meterId);
            		if (_mode.equals("Bypass")){ 
            			saver.syncTime(meter.getModem().getDeviceSerial(), meterId);
            		} else {
            			try {
            				if((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS)) {
            					saver.syncTime(meter.getModem().getDeviceSerial(), meterId);
            				} else {
            					commandGw.cmdDCUMeterTimeSync(meter.getModem().getDeviceSerial(), meterId);
            				}
            			} catch (Exception e){
            				log.info("ThreadID[" + Thread.currentThread().getId() + "] syncTime() thread end. Meter[" + meterId + "] is failed.");
            			}
            		}
            		// UPDATE END SP-443
            	}
                else {
                	// UPDATE START SP-443
//                    saver.syncTime(meter.getModem().getMcu().getSysID(), meterId);                   
            		if (_mode.equals("Bypass")){ 
                        saver.syncTime(meter.getModem().getMcu().getSysID(), meterId);                   
            		} else {
            			try {
            				if((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS)) {
            					saver.syncTime(meter.getModem().getMcu().getSysID(), meterId);  
            				} else {
            					commandGw.cmdDCUMeterTimeSync(meter.getModem().getMcu().getSysID(), meterId);
            				}
            			} catch (Exception e){
            				log.info("ThreadID[" + Thread.currentThread().getId() + "] syncTime() thread end. Meter[" + meterId + "] is failed.");
            			}
            		}
            		// UPDATE END SP-443
                }
            	log.info("ThreadID[" + Thread.currentThread().getId() + "] syncTime() thread end. Meter[" + meterId + "]");
            }
            else {
            	log.debug("Meter[" + meterId + "] getModel() and getModem() is null.");
            }
    	}
    }
    // INSERT END #300
    
    // INSERT START SP-556
    protected class mcuSyncTimeThread extends Thread {
    	McuModemList mcuModems;
    	MCU mcu = null;
    	CommandGW commandGw = DataUtil.getBean(CommandGW.class);
    	Modem modem = null;
    	int limitOfModems = 0;
    	
    	public mcuSyncTimeThread(McuModemList mcuModems, int limit) {
    		try {
	    		this.mcuModems = mcuModems;
	    		mcu = mcuDao.get(mcuModems.mcuId);
	    		limitOfModems = limit;
            }
            catch (Exception ee) {}
    	}
    	public void run() {
            if (mcu != null) {
            	log.info("ThreadID[" + Thread.currentThread().getId() + "] syncTime() thread start. MCU[" + mcuModems.mcuId + "]");

            	try {
            		List<Modem> modemList = mcuModems.modemList;
            		List<Modem> splitModems = null;
            		int k = modemList.size() / limitOfModems;
            		int cnt = 0;
            		
            		for (int i = 0; i < k + 1; i++) {
            			splitModems = new ArrayList<Modem>();
            			
            			for (int j = 0; j < limitOfModems; j++) {
            				if (cnt == modemList.size()) {
            					break;
            				}
            				splitModems.add((Modem)modemList.get(cnt++));
            			}
            			Modem[] modems = splitModems.toArray(new Modem[splitModems.size()]);
            			commandGw.cmdDCUModemTimeSync(modems);
            			splitModems = null;
            		}                      	
            	} catch (Exception e){
            		log.info("ThreadID[" + Thread.currentThread().getId() + "] syncTime() thread end. MCU[" + mcu.getId() + "] is failed.");
            	}	

            	log.info("ThreadID[" + Thread.currentThread().getId() + "] syncTime() thread end. MCU[" + mcu.getId() + "]");            	
            }
            else {
            	log.debug("MCU[" + mcuModems.mcuId + "] is null.");
            }
    	}
    }
    // INSERT END SP-556
    
    public ResponseMap syncTime(String meterId) throws Exception
    {
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            Meter meter = meterDao.get(meterId);
            ResponseMap map = new ResponseMap();
            if (meter.getModel() != null && meter.getModem() != null) {
                Class clazz = Class.forName(meter.getModel().getDeviceConfig().getSaverName());
                AbstractMDSaver saver = (AbstractMDSaver)DataUtil.getBean(clazz);
                
                Map<String, String> response = new HashMap<String, String>();
                if (meter.getModem().getMcu() == null)
                    response.put("Response", saver.syncTime(meter.getModem().getDeviceSerial(), meterId));
                else
                    response.put("Response", saver.syncTime(meter.getModem().getMcu().getSysID(), meterId));
                
                map.setResponse(response);
            }
            return map;
        }
        finally {
            if (txstatus != null && !txstatus.isCompleted())
                txmanager.commit(txstatus);
        }
    }
}
