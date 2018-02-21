package com.aimir.fep.tool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.dao.device.MeterDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.meter.data.MeterData;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

/**
 * Running Ondemand to DCU
 * @author nurisj
 * arg[0]=={startDate} arg[1]=={searchPeriod} arg[2]=={meterSerial}
 */
public class OndemandRecoveryByDcu 
{
	private static Log log = LogFactory.getLog(OndemandRecoveryByDcu.class);
	
	
	
	private void dcuOndemandStart(String startDate, int searchPeriod, String meterSerial) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "/config/spring-fep-schedule.xml" });
		DataUtil.setApplicationContext(ctx);
		
		JpaTransactionManager txmanager = null;
        TransactionStatus txstatus = null;
		
		MeterDao meterDao = ctx.getBean(MeterDao.class);
		CommandGW cgw = DataUtil.getBean(CommandGW.class);
		
		List<Meter> meters = new ArrayList<Meter>();
		
		try {
			txmanager = (JpaTransactionManager)ctx.getBean("transactionManager");
            txmanager.setDefaultTimeout(120);
            txstatus = txmanager.getTransaction(null);
            
            if(!"".equals(meterSerial) && meterSerial != null){
            	Set<Condition> condition = new HashSet<Condition>();
                condition.add(new Condition("mdsId", new Object[]{meterSerial}, null, Restriction.EQ));
                meters = meterDao.findByConditions(condition);
                if(meters.size() <= 0) {
                	log.info("No Meter Object for input args");
                }
            }else {
            	meters = meterDao.getAll();
            	Set<Condition> condition = new HashSet<Condition>();
            	condition.add(new Condition("mcu_id", null, null, Restriction.NOTNULL));
            	meters = meterDao.findByConditions(condition);
            	//안되면.. MeterDaoImpl 구현
            	log.info("Load Meter list.."+meters.size());
            }                        
            
            txmanager.commit(txstatus);
            
		}catch(Exception be){
			if(txstatus!=null){
				txmanager.rollback(txstatus);
			}
			log.error(be,be);			
		}
		
		
		log.info("DCU ondemand start...");
		
		if(meters != null && meters.size() > 0){
			for(Meter me : meters) {
				try {
					//get MODEM
					Modem mod = me.getModem();
					String modemId = mod.getId().toString();
					
					//get MCU
					MCU mc = mod.getMcu();
					String mcuId = mc.getSysID();
					
					//set Date
					for(int i=0; i<searchPeriod; i++){
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			            Calendar fromCal = Calendar.getInstance();
						fromCal.setTime(sdf.parse(startDate));
						Calendar toCal = Calendar.getInstance();
						toCal = (Calendar) fromCal.clone();
						toCal.add(Calendar.DAY_OF_MONTH, +1);
						String toDate = sdf.format(toCal.getTime());
						
						MeterData emd = null;
						emd = cgw.cmdGetMeteringData(mcuId, me.getMdsId(), modemId, "", startDate, toDate, null);
						//result = cmdOperationUtil.cmdGetMeteringData(meter, 0, "admin", nOption, fromDate, toDate);
					
						
						if(emd != null){
							log.info("DCU ondemad ["+me.getMdsId()+"],["+ toDate+"] is Done.");
						}else {
							log.info("FAIL: DCU ondemad ["+me.getMdsId()+"],["+ toDate+"] ");
						}
					}					
					
				}catch(Exception ce){
					log.info("Error on Meter.." + me.getMdsId().toString() + "  go to next meter");
					log.error(ce,ce);
				}
			}
		}
		
		
		
		log.info("All job is finish");
		
	}
	

	
	public static void main(String[] args) {
		log.info("ARG_0[" + args[0] + "] ARG_1[" + args[1] + "] ARG_2[" + args[2] + "] ARG_3[" + args[3] + "]");		
		
		if ((args.length != 2 && args.length != 3) || (args[0].equals("${startDate}") || args[1].equals("${searchPeriod}")) ||
                (args[0] == null || args[1] == null) || ("".equals(args[0]) || "".equals(args[1]))) {
            args = new String[3];
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Calendar cal = Calendar.getInstance();            
            args[1] = sdf.format(cal.getTime());  //current time
            cal.add(Calendar.DAY_OF_MONTH, -2);
            args[0] = sdf.format(cal.getTime());  //two days before (default)
        }
		
		String startDate = args[0];
		String searchPeriod = args[1].trim();
		int days = Integer.parseInt(searchPeriod);
		String meterSerial = null;
		
		if(!"".equals(args[2])){
			meterSerial = args[2];
			log.info("DCU Ondemand.. from " + startDate + " + " + searchPeriod + "days , meterSerial..[" + meterSerial+"]");
		}else{
			log.info("DCU Ondemand.. to All meter..  +[" + startDate + " + " + searchPeriod + "days]");
		}
		
		
		OndemandRecoveryByDcu forJob = new OndemandRecoveryByDcu();
		forJob.dcuOndemandStart(startDate, days, meterSerial);
								
	}		
	
}
