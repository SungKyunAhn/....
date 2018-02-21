package com.aimir.fep.tool;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.NIAttributeId;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.Modem;

/**
 * @author sunghan
 *
 */
@Service
public class SetCloneOnOffBatch {
	private static Log log = LogFactory.getLog(SetCloneOnOffBatch.class);
	private static String fileName = "setCloneOnOffList.txt";
	private List<String> targetList;
	private int paramCount;
	ApplicationContext ctx;
	
	public static void main(String[] args) {
		log.debug("SetCloneOnOffBatch (S)");		
				
		// TODO SP-560 파일로부터 모뎀 번호, clone on/off 기간을 입력받는 로직 구현 필요 
		// TODO count변수는 TEST용 파라메터
		String param = "";
		for (int i=0; i< args.length; i += 2) {
			String nextArg = args[i];
			
			log.debug("arg[i]=" + args[i] + ", arg[i+1]=" + args[i+1]);
			
			if (nextArg.startsWith("-param")) {
				if ( !"${param}".equals(args[i + 1]))
					param = new String(args[i + 1]);
			}
		}
		
		if(param.length() < 1){
			log.info("SetCloneOnOffBatch -Dparam=CloneCount");
			System.exit(1);
		}
		
		int count = Integer.parseInt(param);
		if(count > 96){
			log.info("PARAM=0 | 20<=PARAM<=96");
			System.exit(1);
		}
		
		SetCloneOnOffBatch forJob = new SetCloneOnOffBatch();
		// retrieve the modem list from an external-file
		forJob.setTargetList();
		// execute
		forJob.setClone(count);
		
		log.debug("SetCloneOnOffBatch (E)");
	}

	private void setClone(int count) {
		try {
			springInit();
			
			CommandGW commandGW = (CommandGW)DataUtil.getBean(CommandGW.class);
			ModemDao modemDao = DataUtil.getBean(ModemDao.class);

			String[] deviceSerialArr = targetList.toArray(new String[targetList.size()]);
			List<Integer> mdsId_list = new ArrayList<Integer>();
			Modem modem = null;
	        
			long time = System.currentTimeMillis();
			SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = dayTime.format(new Date(time));
			String resultStr = "------------------------------------" + "\n" + 
								"Execute date: " + date + "\n" + 
								"------------------------------------";
			
			for (String device_serial : deviceSerialArr) {
				modem = modemDao.get(device_serial);
				
				if (modem == null) {
					log.warn("------------------------------------" +
							"\n" + "Invalid Modem Serial: " + device_serial + "\n" + 
							"------------------------------------");
				} else if (modem.getProtocolType() == Protocol.SMS) {
					log.warn("------------------------------------" +
							"\n" + "MBB Modem is not available" + "\n" + 
							"------------------------------------");
				} else {
					mdsId_list.add(modem.getId());
				}
			}

			//Map<String, Object> result = new HashMap<String, Object>();
			Map<String, String> result = new HashMap<String, String>();
			String deviceSerial = null;
			int index = 0;
	        String attrParam="";
	        
			for (Integer mdsid : mdsId_list) {
				deviceSerial = deviceSerialArr[index];
				resultStr += "\n Target: " + deviceSerial + "\n";
				log.info("Target:" + deviceSerial);

				//result = commandGW.setCloneOnOff(mdsid.toString(), count);
				
                attrParam = Hex.decode(DataUtil.get2ByteToInt(count));
				
				result = commandGW.cmdExecDmdNiCommand(mdsid.toString(), 
						"SET", 
						Hex.decode(NIAttributeId.CloneOnOff.getCode()),
						attrParam);
				
		        for (Map.Entry<String, String> e : result.entrySet()) {
		            log.debug("[MODEM ID:" + deviceSerial + "] REQUEST TYPE: " + NIAttributeId.CloneOnOff.name() + "] key["+e.getKey()+"], value["+ e.getValue()+"]");
		        }
		        
				if (result.containsKey("cmdResult")) {
					resultStr += result.get("cmdResult") + "\n";
					log.info(result.get("cmdResult"));
				} else {
					resultStr += "[Fail] communication error \n";
					log.info("[Fail][" + deviceSerial + "] communication error");
				}

				index++;
			}

			makeResultFile(resultStr + "--------------------------------------------------------------------------------------------\n\n");
		} catch (Exception e) {
			log.error(e, e);
		}
	}
	
	// file에 기재된 device_serial 정보를 읽어온다.
	private void setTargetList() {
		InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream(fileName);
		if (fileInputStream != null) {
			targetList = new LinkedList<String>();

			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(fileInputStream);
			while (scanner.hasNextLine()) {
				// TODO SP-560 파일로부터 모뎀 번호, clone on/off 기간을 입력받는 로직 구현 필요 
				
				String target = scanner.nextLine().trim();
				if (!target.equals("")) {
					targetList.add(target);
				}
			}
			log.info("Target List:" + "(" + targetList.size() + ")" + targetList.toString());
		} else {
			log.info("File not found");
		}
	}
	
	private void springInit() throws Exception {
		ctx = new ClassPathXmlApplicationContext(new String[] { "/config/spring-setCloneOnOff.xml" });
		DataUtil.setApplicationContext(ctx);
	}

	private void makeResultFile(String resultStr){
		String fileName = "./log/setCloneOnOffResult.txt";
		
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
