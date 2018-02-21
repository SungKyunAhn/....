package com.aimir.fep.bypass.actions;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;

import com.aimir.fep.bypass.BypassDevice;
import com.aimir.fep.bypass.actions.moe.DayProfileTable;
import com.aimir.fep.bypass.actions.moe.DayProfileTableFactory;
import com.aimir.fep.bypass.actions.moe.SeasonProfileTable;
import com.aimir.fep.bypass.actions.moe.SeasonProfileTableFactory;
import com.aimir.fep.bypass.actions.moe.WeekProfileTable;
import com.aimir.fep.bypass.actions.moe.WeekProfileTableFactory;
import com.aimir.fep.bypass.decofactory.protocolfactory.BypassFrameFactory;
import com.aimir.fep.bypass.decofactory.protocolfactory.BypassFrameResult;
import com.aimir.fep.bypass.decofactory.protocolfactory.BypassGDFactory;
import com.aimir.fep.bypass.decofactory.protocolfactory.BypassFrameFactory.Procedure;
import com.aimir.fep.protocol.fmp.client.Client;
import com.aimir.fep.protocol.fmp.client.bypass.BYPASSClient;
import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.FMPVariable;
import com.aimir.fep.protocol.fmp.datatype.OID;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.util.CodecUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class CommandAction_NG extends CommandAction {
	private static Log log = LogFactory.getLog(CommandAction_NG.class);

	@Override
	public void execute(String cmd, SMIValue[] smiValues, IoSession session) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void executeBypass(byte[] frame, IoSession session) throws Exception {
		log.debug("#### Receive Bypass Frame ==> " + Hex.decode(frame));

	}

	@Override
	public CommandData executeBypassClient(byte[] frame, IoSession session) throws Exception {

		log.debug("#### Receive Bypass Frame ===> " + Hex.decode(frame));

		Object ns_obj = session.getAttribute("nameSpace");
		String ns = ns_obj != null ? (String) ns_obj : null;

		BypassDevice bd = (BypassDevice) session.getAttribute(session.getRemoteAddress());
		BypassFrameResult bypassFrameResult = bd.getFrameFactory().receiveBypass(session, frame);

		log.debug("###[" + bd.getCommand() + "] Execute Bypass ===> " + bypassFrameResult.toString());

		try {
			if (!bypassFrameResult.isResultState()) {
				log.debug("FAIL : " + bd.getCommand());
				return null;
			}

			if (bypassFrameResult.isResultState()) {
				if (bypassFrameResult.isFinished()) {
					CommandData cd = (CommandData) CodecUtil.unpack(ns, CommandData.class.getName(), frame);
					cd.setNS(ns);

					OID oid = new OID("1.11.0");
					SMIValue smiValue = new SMIValue();
					smiValue.setOid(oid);

					FMPVariable mFMPVariable = DataUtil.getFMPVariableObject(ns, oid);

					if ("cmdGetMeterFWVersion".equals(bd.getCommand()) || "cmdSetBillingCycle".equals(bd.getCommand()) || "cmdAlarmReset".equals(bd.getCommand()) || "cmdDemandPeriod".equals(bd.getCommand()) || "cmdTOUSet".equals(bd.getCommand()) || "cmdGetMeterFWVersion".equals(bd.getCommand()) || "cmdSetCurrentLoadLimit".equals(bd.getCommand())) {
						String fwVersion = (String) bypassFrameResult.getResultValue();
						mFMPVariable.decode(ns, fwVersion.getBytes(), 0, fwVersion.getBytes().length);

						smiValue.setVariable(mFMPVariable);
						cd.setErrCode(new BYTE(0));
						cd.append(smiValue);

						return cd;
					} else if ("cmdGetBillingCycle".equals(bd.getCommand())) {
						@SuppressWarnings("unchecked")
						HashMap<Object, Object> resultMap = (HashMap<Object, Object>) bypassFrameResult.getResultValue();

						StringBuilder builder = new StringBuilder();
						builder.append(resultMap.get("time").toString());
						builder.append(",");
						builder.append(resultMap.get("day").toString());

						String result = builder.toString();

						mFMPVariable.decode(ns, result.getBytes(), 0, result.getBytes().length);
						smiValue.setVariable(mFMPVariable);
						cd.setErrCode(new BYTE(0));
						cd.append(smiValue);

						return cd;
					} else if ("cmdGetDemandPeriod".equals(bd.getCommand())) {
						String args = session.getAttribute(bd.getCommand()).toString();
						String lastArgs = getDemandPeriodString(bypassFrameResult);
						log.debug("### CmdGetDemandPeriod RESULT[" + args + lastArgs + "]");

						StringBuilder builder = new StringBuilder();
						builder.append(args);
						builder.append(lastArgs);

						String result = builder.toString();

						mFMPVariable.decode(ns, result.getBytes(), 0, result.getBytes().length);
						smiValue.setVariable(mFMPVariable);
						cd.setErrCode(new BYTE(0));
						cd.append(smiValue);

						return cd;
					} else if ("cmdGetCurrentLoadLimit".equals(bd.getCommand())) {
						String args = session.getAttribute(bd.getCommand()).toString();
						String lastArgs = getCurrentLoadLimit(bypassFrameResult);
						log.debug("### cmdGetCurrentLoadLimit RESULT[" + args + lastArgs + "]");

						StringBuilder builder = new StringBuilder();
						builder.append(args);
						builder.append(lastArgs);

						String result = builder.toString();

						mFMPVariable.decode(ns, result.getBytes(), 0, result.getBytes().length);
						smiValue.setVariable(mFMPVariable);
						cd.setErrCode(new BYTE(0));
						cd.append(smiValue);

						return cd;
					}
				} else {
					if ("cmdGetDemandPeriod".equals(bd.getCommand())) {

						if (session.getAttribute(bd.getCommand()) == null) {
							session.setAttribute(bd.getCommand(), getDemandPeriodString(bypassFrameResult));
						} else {
							String args = session.getAttribute(bd.getCommand()).toString();
							session.setAttribute(bd.getCommand(), args + getDemandPeriodString(bypassFrameResult));
						}

					} else if ("cmdGetCurrentLoadLimit".equals(bd.getCommand())) {

						if (session.getAttribute(bd.getCommand()) == null) {
							session.setAttribute(bd.getCommand(), getCurrentLoadLimit(bypassFrameResult));
						} else {
							String args = session.getAttribute(bd.getCommand()).toString();
							session.setAttribute(bd.getCommand(), args + getCurrentLoadLimit(bypassFrameResult));
						}

					} else {
						return null;
					}

				}
			}

		} catch (Exception ex) {
			log.error(ex, ex);
		}

		return null;
	}

	public String getDemandPeriodString(BypassFrameResult bypassFrameResult) {
		StringBuilder builder = new StringBuilder();

		if (bypassFrameResult.getResultValue(Procedure.GET_DEMAND_PLUS_A_PERIOD.name()) != null) {
			builder.append("demand +A Period : ");
			builder.append(bypassFrameResult.getResultValue(Procedure.GET_DEMAND_PLUS_A_PERIOD.name()));
			builder.append(" , ");
		}
		if (bypassFrameResult.getResultValue(Procedure.GET_DEMAND_PLUS_A_NUMBER.name()) != null) {
			builder.append("demand +A Number : ");
			builder.append(bypassFrameResult.getResultValue(Procedure.GET_DEMAND_PLUS_A_NUMBER.name()));
			builder.append("\n");
		}
		if (bypassFrameResult.getResultValue(Procedure.GET_DEMAND_MINUS_A_PERIOD.name()) != null) {
			builder.append("demand -A Period : ");
			builder.append(bypassFrameResult.getResultValue(Procedure.GET_DEMAND_MINUS_A_PERIOD.name()));
			builder.append(" , ");
		}
		if (bypassFrameResult.getResultValue(Procedure.GET_DEMAND_MINUS_A_NUMBER.name()) != null) {
			builder.append("demand -A Number : ");
			builder.append(bypassFrameResult.getResultValue(Procedure.GET_DEMAND_MINUS_A_NUMBER.name()));
			builder.append("\n");
		}
		if (bypassFrameResult.getResultValue(Procedure.GET_DEMAND_PLUS_R_PERIOD.name()) != null) {
			builder.append("demand +R Period : ");
			builder.append(bypassFrameResult.getResultValue(Procedure.GET_DEMAND_PLUS_R_PERIOD.name()));
			builder.append(" , ");
		}
		if (bypassFrameResult.getResultValue(Procedure.GET_DEMAND_PLUS_R_NUMBER.name()) != null) {
			builder.append("demand +R Number : ");
			builder.append(bypassFrameResult.getResultValue(Procedure.GET_DEMAND_PLUS_R_NUMBER.name()));
			builder.append("\n");
		}
		if (bypassFrameResult.getResultValue(Procedure.GET_DEMAND_MINUS_R_PERIOD.name()) != null) {
			builder.append("demand -R Period : ");
			builder.append(bypassFrameResult.getResultValue(Procedure.GET_DEMAND_MINUS_R_PERIOD.name()));
			builder.append(" , ");
		}
		if (bypassFrameResult.getResultValue(Procedure.GET_DEMAND_MINUS_R_NUMBER.name()) != null) {
			builder.append("demand -R Number : ");
			builder.append(bypassFrameResult.getResultValue(Procedure.GET_DEMAND_MINUS_R_NUMBER.name()));
			builder.append("\n");
		}
		if (bypassFrameResult.getResultValue(Procedure.GET_DEMAND_R_QI_PERIOD.name()) != null) {
			builder.append("demand R(QI) Period : ");
			builder.append(bypassFrameResult.getResultValue(Procedure.GET_DEMAND_R_QI_PERIOD.name()));
			builder.append(" , ");
		}
		if (bypassFrameResult.getResultValue(Procedure.GET_DEMAND_R_QI_NUMBER.name()) != null) {
			builder.append("demand R(QI) Number : ");
			builder.append(bypassFrameResult.getResultValue(Procedure.GET_DEMAND_R_QI_NUMBER.name()));
			builder.append("\n");
		}
		if (bypassFrameResult.getResultValue(Procedure.GET_DEMAND_R_QIV_PERIOD.name()) != null) {
			builder.append("demand R(QIV) Period : ");
			builder.append(bypassFrameResult.getResultValue(Procedure.GET_DEMAND_R_QIV_PERIOD.name()));
			builder.append(" , ");
		}
		if (bypassFrameResult.getResultValue(Procedure.GET_DEMAND_R_QIV_NUMBER.name()) != null) {
			builder.append("demand R(QIV) Number : ");
			builder.append(bypassFrameResult.getResultValue(Procedure.GET_DEMAND_R_QIV_NUMBER.name()));
			builder.append("\n");
		}
		if (bypassFrameResult.getResultValue(Procedure.GET_DEMAND_PLUS_PERIOD.name()) != null) {
			builder.append("demand+ Period : ");
			builder.append(bypassFrameResult.getResultValue(Procedure.GET_DEMAND_PLUS_PERIOD.name()));
			builder.append(" , ");
		}
		if (bypassFrameResult.getResultValue(Procedure.GET_DEMAND_PLUS_NUMBER.name()) != null) {
			builder.append("demand+ Number : ");
			builder.append(bypassFrameResult.getResultValue(Procedure.GET_DEMAND_PLUS_NUMBER.name()));
			builder.append("\n");
		}
		if (bypassFrameResult.getResultValue(Procedure.GET_DEMAND_MINUS_PERIOD.name()) != null) {
			builder.append("demand- Period : ");
			builder.append(bypassFrameResult.getResultValue(Procedure.GET_DEMAND_MINUS_PERIOD.name()));
			builder.append(" , ");
		}
		if (bypassFrameResult.getResultValue(Procedure.GET_DEMAND_MINUS_NUMBER.name()) != null) {
			builder.append("demand- Number : ");
			builder.append(bypassFrameResult.getResultValue(Procedure.GET_DEMAND_MINUS_NUMBER.name()));
			builder.append("\n");
		}

		return builder.toString();
	}

	public String getCurrentLoadLimit(BypassFrameResult bypassFrameResult) {
		StringBuilder builder = new StringBuilder();

		if (bypassFrameResult.getResultValue(Procedure.GET_CURRENT_LOAD_LIMIT_DURATION_JUDGE_TIME.name()) != null) {
			builder.append("Duration judge time : ");
			builder.append(bypassFrameResult.getResultValue(Procedure.GET_CURRENT_LOAD_LIMIT_DURATION_JUDGE_TIME.name()));
			builder.append("s, ");
		}
		if (bypassFrameResult.getResultValue(Procedure.GET_CURRENT_LOAD_LIMIT_THRESHOLD.name()) != null) {
			String threshold = String.valueOf(bypassFrameResult.getResultValue(Procedure.GET_CURRENT_LOAD_LIMIT_THRESHOLD.name()));
			builder.append("Threshold : ");
			builder.append(threshold.substring(0, 3) + "." + threshold.substring(3, 5) + "%");
			builder.append("\n");
		}

		return builder.toString();
	}

	@Override
	public CommandData execute(HashMap<String, String> params, IoSession session, Client client) throws Exception {
		
		CommandData cd = null;
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		HashMap<String, Object> argMap = new HashMap<String, Object>();    		
		
		BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
		String cmd = params.get("cmd").toString();		
		
		bd.setCommand(cmd);    
		bd.setClient(client);
		
		if("cmdGetMeterFWVersion".equals(cmd)) 
		{	
			BypassFrameFactory gdFactory = new BypassGDFactory("cmdGetMeterFWVersion");
			bd.setFrameFactory(gdFactory);					
			
			session.setAttribute(session.getRemoteAddress(), bd);
			cd = ((BYPASSClient)client).sendBypass(session, gdFactory);
		}
		else if("cmdDemandPeriod".equals(cmd))
		{
			paramMap.clear();
			paramMap.put("period", params.get("period").toString());
			paramMap.put("number", params.get("number").toString());
			
			BypassFrameFactory gdFactory = new BypassGDFactory("cmdDemandPeriod");
			gdFactory.setParam(paramMap);
			bd.setFrameFactory(gdFactory);
			
			session.setAttribute(session.getRemoteAddress(), bd);
			cd = ((BYPASSClient)client).sendBypass(session, gdFactory);
		}
		else if("cmdGetDemandPeriod".equals(cmd)) {
			BypassFrameFactory gdFactory = new BypassGDFactory("cmdGetDemandPeriod");
			bd.setFrameFactory(gdFactory);
			
			session.setAttribute(session.getRemoteAddress(), bd);
			cd = ((BYPASSClient)client).sendBypass(session, gdFactory);
		}					
		else if("cmdGetBillingCycle".equals(cmd))
		{					
			argMap.put("timeout", "60");
			bd.setArgMap(argMap);
			
			BypassFrameFactory gdFactory = new BypassGDFactory("cmdGetBillingCycle");
			bd.setFrameFactory(gdFactory);
			
			session.setAttribute(session.getRemoteAddress(), bd);
			cd = ((BYPASSClient)client).sendBypass(session, gdFactory);
		}
		else if("cmdSetBillingCycle".equals(cmd))
		{
			argMap.put("timeout", "60");
			bd.setArgMap(argMap);
			
			paramMap.clear();
			paramMap.put("time", params.get("time").toString());
			paramMap.put("day", params.get("day").toString());

			BypassFrameFactory gdFactory = new BypassGDFactory("cmdSetBillingCycle");
			gdFactory.setParam(paramMap);
			bd.setFrameFactory(gdFactory);
			
			session.setAttribute(session.getRemoteAddress(), bd);
			cd = ((BYPASSClient)client).sendBypass(session, gdFactory);
		}			
		else if("cmdAlarmReset".equals(cmd))
		{
			argMap.put("timeout", "60");
			bd.setArgMap(argMap);
			
			BypassFrameFactory gdFactory = new BypassGDFactory("cmdAlarmReset");
			bd.setFrameFactory(gdFactory);
			
			session.setAttribute(session.getRemoteAddress(), bd);
			cd = ((BYPASSClient)client).sendBypass(session, gdFactory);
		}
		else if("cmdGetMeterFWVersion".equals(cmd)) 
		{
			argMap.put("timeout", "60");
			bd.setArgMap(argMap);
			
			BypassFrameFactory gdFactory = new BypassGDFactory("cmdGetMeterFWVersion");
			bd.setFrameFactory(gdFactory);
			
			session.setAttribute(session.getRemoteAddress(), bd);
			cd = ((BYPASSClient)client).sendBypass(session, gdFactory);
		}
		else if("cmdTOUSet".equals(cmd))
		{
			paramMap.clear();
			String calendarNamePassive =params.get("calendarNamePassive");
			String startingDate = params.get("startingDate");
			String seasonFilePath = params.get("seasonFile");
			String weekFilePath = params.get("weekFile");
			String dayFilePath = params.get("dayFile");
			
			/*
			 * Calendar Name Passive
			 */
			if(calendarNamePassive != null && !calendarNamePassive.equals("")){
				paramMap.put("calendarNamePassive", calendarNamePassive);
				log.debug("[Calendar Name Passive] ==> " + calendarNamePassive);
			}
			
			/*
			 * TOU Starting Date
			 */
			if(startingDate != null && !startingDate.equals("")){
				paramMap.put("startingDate", startingDate);
				log.debug("[Starting Date] ==> " + startingDate);
			}
			
			/*
			 * Seasson Profile
			 */
			if(seasonFilePath != null && !seasonFilePath.equals("")){
				try {
					Path path = Paths.get(seasonFilePath);
					Source xmlSource = new StreamSource(path.toFile());
					
					JAXBContext jaxbContext = JAXBContext.newInstance(SeasonProfileTableFactory.class);
					Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
					JAXBElement<SeasonProfileTable> unmarshalledObject = (JAXBElement<SeasonProfileTable>) unmarshaller.unmarshal(xmlSource, SeasonProfileTable.class);
					
					SeasonProfileTable seasonProfileTable = unmarshalledObject.getValue();
					log.debug("[Season Profile Table] ==> " + seasonProfileTable.getRecordList().toString());
					paramMap.put("seasonProfileTable", seasonProfileTable);				
				} catch (JAXBException e) {
					log.error("Season Profile Table Parsing Error - " + e);
				}
			}
			
			/*
			 * Week Profile
			 */
			if(weekFilePath != null && !weekFilePath.equals("")){
				try {
					Path path = Paths.get(weekFilePath);
					Source xmlSource = new StreamSource(path.toFile());
					
					JAXBContext jaxbContext = JAXBContext.newInstance(WeekProfileTableFactory.class);
					Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
					JAXBElement<WeekProfileTable> unmarshalledObject = (JAXBElement<WeekProfileTable>) unmarshaller.unmarshal(xmlSource, WeekProfileTable.class);
					
					WeekProfileTable weekProfileTable = unmarshalledObject.getValue();
					log.debug("Week Profile Table] ==> " + weekProfileTable.getRecordList().toString());
					paramMap.put("weekProfileTable", weekProfileTable);
				} catch (JAXBException e) {
					log.error("Week Profile Table Parsing Error - " + e);
				}
			}
			
			/*
			 * Day Profile
			 */
			if(dayFilePath != null && !dayFilePath.equals("")){
				try {
					Path path = Paths.get(dayFilePath);
					Source xmlSource = new StreamSource(path.toFile());
					
					JAXBContext jaxbContext = JAXBContext.newInstance(DayProfileTableFactory.class);
					Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
					JAXBElement<DayProfileTable> unmarshalledObject = (JAXBElement<DayProfileTable>) unmarshaller.unmarshal(xmlSource, DayProfileTable.class);
					
					DayProfileTable dayProfileTable = unmarshalledObject.getValue();
					log.debug("[Day Profile Table] ==> " + dayProfileTable.getRecordList().toString());				
					paramMap.put("dayProfileTable", dayProfileTable);
				} catch (JAXBException e) {
					log.error("Day Profile Table Parsing Error - " + e);
				}
			}
			
			if(0 < params.size()) {						
				BypassFrameFactory gdFactory = new BypassGDFactory("cmdTOUSet");
				gdFactory.setParam(paramMap);
				bd.setFrameFactory(gdFactory);
										
				session.setAttribute(session.getRemoteAddress(), bd);
				cd = ((BYPASSClient)client).sendBypass(session, gdFactory);
			} else {
				throw new Exception("TOU Parameter is null...");
			}
		}
		else if("cmdGetCurrentLoadLimit".equals(cmd))
		{					
			argMap.put("timeout", "60");
			bd.setArgMap(argMap);
			
			BypassFrameFactory gdFactory = new BypassGDFactory("cmdGetCurrentLoadLimit");
			bd.setFrameFactory(gdFactory);
			
			session.setAttribute(session.getRemoteAddress(), bd);
			cd = ((BYPASSClient)client).sendBypass(session, gdFactory);
		}
		else if("cmdSetCurrentLoadLimit".equals(cmd))
		{
			argMap.put("timeout", "60");
			bd.setArgMap(argMap);
			paramMap.clear();
			paramMap.put("judgeTime", params.get("judgeTime").toString());
			paramMap.put("threshold", params.get("threshold").toString());

			BypassFrameFactory gdFactory = new BypassGDFactory("cmdSetCurrentLoadLimit");
			gdFactory.setParam(paramMap);
			bd.setFrameFactory(gdFactory);
			
			session.setAttribute(session.getRemoteAddress(), bd);
			cd = ((BYPASSClient)client).sendBypass(session, gdFactory);
		}
		return cd;
	}
}
