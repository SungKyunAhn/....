package com.aimir.fep.tool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.dao.device.MeterDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.command.ws.client.ResponseMap;
import com.aimir.fep.meter.data.MeterData;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;


public class SetCloneOnOffWithTarget 
{
	private static Log log = LogFactory.getLog(SetCloneOnOffWithTarget.class);
	
	
	
	private void execCommand(String modemId, String code, int count, String version, int euiCount, List<String> euiList) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "/config/spring-fep-schedule.xml" });
		DataUtil.setApplicationContext(ctx);
		
		CommandGW gw = DataUtil.getBean(CommandGW.class);
					
		log.info("Execute start...");

		try {		
			Map<String, Object> result = new HashMap<String, Object>();
			result = gw.setCloneOnOffWithTarget(modemId, code, count, version, euiCount, euiList);
	
	        for (Map.Entry<String, Object> e : result.entrySet()) {
	            log.debug("[MODEM ID:" + modemId + "]  key["+e.getKey()+"], value["+ e.getValue()+"]");
	        }		
		}catch(Exception e){
			log.error(e,e);
		}		
		log.info("All job is finish");
		
	}
	

	
	public static void main(String[] args) {
		log.info("ARG_0[" + args[0] + "] ARG_1[" + args[1] + "] ARG_2[" + args[2] + "] ARG_3[" + args[3] + "] ARG_4[" + args[4] +"] ARG_5[" + args[5] +"]");
				

		String modemId = args[0];
		String code = args[1];
		String count = args[2];
		String version = args[3];
		String euiCount = args[4];        
		String euiString = args[5];      		
	
        List<String> euiList = new ArrayList<String>();
        StringTokenizer tokenizer = new  StringTokenizer(euiString, "|");
        while (tokenizer.hasMoreTokens()) {
        	euiList.add(tokenizer.nextToken());
        }
		
		SetCloneOnOffWithTarget forJob = new SetCloneOnOffWithTarget();
		forJob.execCommand(modemId, 
				code,
				Integer.parseInt(count),
				version, 
				Integer.parseInt(euiCount), 
				euiList);
		
		System.exit(0);						
	}		
	
}
