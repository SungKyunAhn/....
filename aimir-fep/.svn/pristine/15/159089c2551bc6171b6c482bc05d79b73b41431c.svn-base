package com.aimir.fep.trap.actions;

import java.math.BigDecimal;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.mvm.BillingDayEMDao;
import com.aimir.dao.mvm.BillingDayWMDao;
import com.aimir.dao.mvm.BillingMonthEMDao;
import com.aimir.dao.mvm.BillingMonthWMDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.DayWMDao;
import com.aimir.dao.mvm.RealTimeBillingEMDao;
import com.aimir.dao.mvm.SeasonDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.GroupDao;
import com.aimir.dao.system.GroupMemberDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.TOURateDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.dao.system.TariffWMDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.trap.data.IHD_ReceiveDataFrame;
import com.aimir.fep.trap.data.IHD_RequestDataFrame;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.MIBUtil;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.WaterMeter;
import com.aimir.model.mvm.BillingMonthEM;
import com.aimir.model.mvm.BillingMonthWM;
import com.aimir.model.mvm.Season;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.GroupMember;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.TOURate;
import com.aimir.model.system.TariffEM;
import com.aimir.model.system.TariffWM;
import com.aimir.notification.FMPTrap;
import com.aimir.util.CalendarUtil;
import com.aimir.util.Condition;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.Condition.Restriction;

/**
 * 
 * Event ID : 217.1 Processing Class
 * IHDEvent
 * @author jihoon
 *
 */
@Component
public class EV_217_1_0_Action implements EV_Action{
	
	private static Log log = LogFactory.getLog(EV_217_1_0_Action.class);
	@Autowired
	MCUDao mcuDao;
	@Autowired
    MeterDao meterDao;
	@Autowired
    ModemDao modemDao;
	@Autowired
	CodeDao codeDao;
	@Autowired
	DayEMDao dayEMDao;
	@Autowired
	DayWMDao dayWMDao;
	@Autowired
	ContractDao contractDao;
	@Autowired
	RealTimeBillingEMDao realTimeBillingEMDao;
	@Autowired
	BillingDayEMDao billingDayEMDao;
	@Autowired
	BillingDayWMDao billingDayWMDao;
	@Autowired
	BillingMonthEMDao billingMonthEMDao;
	@Autowired
	BillingMonthWMDao billingMonthWMDao;
	@Autowired
	PrepaymentLogDao prepaymentLogDao;
	@Autowired
	TariffEMDao tariffEMDao;
	@Autowired
	TariffWMDao tariffWMDao;
	@Autowired
	TOURateDao touRateDao;
	@Autowired
	CommandGW commandGW;
	@Autowired
	GroupDao groupDao;
	@Autowired
	GroupMemberDao groupMemberDao;
	@Autowired
	SeasonDao seasonDao;
	
	Modem IHD = new Modem();
	Meter meter_EM = null;
	Meter meter_WM = null;
	Supplier supplier = new Supplier();

	
	@Override
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		log.debug("***** 217.1.0 event start *****");
		String mcuId = trap.getMcuId();		
		String sensorId = event.getEventAttrValue("sensorID");
//		String ihdFrame = event.getEventAttrValue("streamEntry");
		
		MIBUtil mibUtil = MIBUtil.getInstance();
        String oid = mibUtil.getOid("streamEntry").toString();
        byte[] bx = ((OCTET)trap.getVarBinds().get(oid)).getValue();
		String strIhdFrame = Hex.decode(bx);
		
		log.debug("\n mcuId : " + mcuId + ", sensorId : " + sensorId + "\n IHDFrame : " + Hex.getHexDump(bx));
		IHD 		= modemDao.get(sensorId);
		supplier 	= IHD.getSupplier();

		//IHD가 속한 그룹을 구한다.
		GroupMember gm = groupMemberDao.findByCondition("member", IHD.getDeviceSerial().trim());
		
		//검색한 그룹의 멤버를 구한다. & 멤버들의 타입을 지정한다.
		Set<GroupMember> gmList = groupMemberDao.getGroupMemberById(gm.getGroupId());
		Iterator<GroupMember> lt = gmList.iterator();
		
		meter_EM = null;
		meter_WM = null;
		
		while(lt.hasNext()) {
			GroupMember g = lt.next();
			Meter tmpMeter = meterDao.get(g.getMember().trim());
			
			log.debug("GroupMember : [" + g.getMember().trim()+"]");
			if(tmpMeter == null) {
				continue;
			} else if(tmpMeter.getMeterType().getCode().equals("1.3.1.1")){ // EnergyMeter
				meter_EM = new EnergyMeter();
				meter_EM = tmpMeter;	
				log.debug("meter_EM["+tmpMeter.getMdsId()+"]");
			} else if(tmpMeter.getMeterType().getCode().equals("1.3.1.2")){ // WaterMeter
				meter_WM = new WaterMeter();
				meter_WM = tmpMeter;
				log.debug("meter_WM["+tmpMeter.getMdsId()+"]");
			}
		}

	   /**
		* 미터 객체 생성 로직 추가 필요
		* meter_EM
		* meter_WM
		*/
		
		log.debug("\n streamEntry to Hex : " + Hex.getHexDump(bx));
		IHD_ReceiveDataFrame receiveFrame = new IHD_ReceiveDataFrame(bx);
		byte[] data = receiveFrame.getData();
		String sendTarget 		= strIhdFrame.substring(4, 6);
		String receiveTarget  	= strIhdFrame.substring(2, 4);
		String cmd 				= strIhdFrame.substring(10, 12);
		log.debug("sendTarget : "+ sendTarget + ", receiveTarget : " + receiveTarget + ", cmd : " + cmd);
		
		byte[] cmdCode = Hex.encode("303132333435");
		byte CMD = DataUtil.select(data, 0, 1)[0];
		log.debug("start");
		//CMD 분기
		if(CMD == cmdCode[0]){			//0x30 CustomerInfosMessage
			//기초데이터
			log.debug("0x30 CustomerInfosMessage");
			IHD_RequestDataFrame rf = new IHD_RequestDataFrame();
			sendDCU(rf.getBytes(sendTarget, receiveTarget, cmd, getCustomerInfosMessage()), mcuId, sensorId);
		} else if(CMD == cmdCode[1]){	//0x31 TariffsMessage
			//기초데이터
			log.debug("0x31 TariffsMessage");
			IHD_RequestDataFrame rf = new IHD_RequestDataFrame();
			sendDCU(rf.getBytes(sendTarget, receiveTarget, cmd, getTariffsMessage()), mcuId, sensorId);
		} else if(CMD == cmdCode[2]){	//0x32 IHDMainInfosMessage
			log.debug("0x32 IHDMainInfosMessage");
			IHD_RequestDataFrame rf = new IHD_RequestDataFrame();
			sendDCU(rf.getBytes(sendTarget, receiveTarget, cmd, getIHDMainInfosMessage()), mcuId, sensorId);
		} else if(CMD == cmdCode[3]){	//0x33 EventMessage
			//기초데이터
			//초기인스톨 메세지 리턴
			IHD_RequestDataFrame rf = new IHD_RequestDataFrame();
			sendDCU(rf.getBytes(sendTarget, receiveTarget, cmd, getEventMessage()), mcuId, sensorId);
		} else if(CMD == cmdCode[4]){	//0x34 CustomerUpdateInfosMessage
			log.debug("0x34 CustomerUpdateInfosMessage");
			//기초데이터에서 빠졌기때문에 UI에서 해당 값이 변경시에만 IHD로 값을 보냄
//			IHD_RequestDataFrame rf = new IHD_RequestDataFrame();
//			sendDCU(rf.getBytes(sendTarget, receiveTarget, cmd, getCustomerUpdateInfosMessage()), mcuId, sensorId);
		} else if(CMD == cmdCode[5]){	//0x35 BillingInfosMessage
			log.debug("0x35 BillingInfosMessage");
			IHD_RequestDataFrame rf = new IHD_RequestDataFrame();
			String from = "";
			String to	= "";
			int offset 	= 1;
			int len = 3;
			if(data.length > 1) {
				log.debug("datautil : "+DataUtil.select(data, offset, 1)[0]);
				if(DataUtil.select(data, offset, 1)[0] == (byte)0x10){
					offset++;
					from = Hex.decode(DataUtil.select(data, offset, len));
					offset += len;
				}
				
				if(DataUtil.select(data, offset, 1)[0] == (byte)0x11){
					offset++;
					to = Hex.decode(DataUtil.select(data, offset, len));
					offset += len;
				}
			}
			log.debug("from : [" + from + "], to : [" +to + "]");
			sendDCU(rf.getBytes(sendTarget, receiveTarget, cmd, getBillingInfosMessage(from, to)), mcuId, sensorId);
		} 
		log.debug("***** 217.1.0 event end *****");
	}
	
	public void sendDCU(byte[] data, String mcuId, String sensorId){
		try {			
//			MCU mcu = mcuDao.get(mcuId);
//			log.debug("sendDCU mcuId : " + mcuId + ", mcuSysId : " + mcu.getSysID());
			log.debug("HexDump : \n"+Hex.getHexDump(data));
			commandGW.cmdSendIHDData(mcuId, sensorId, data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.debug(e.getMessage());
		}
	}
	
	/**
	 * getCustomerInfosMessage
	 * 
	 * @return String type Data
	 */
	public String getCustomerInfosMessage(){
		log.debug("   *** getCustomerInfosMessage Start ***");
		String rtnStr 	= "";
		
		//noun				0x01
		//createdDateTime	0x02
		//priveUnit			0x03
		//language			0x04
		//mdFormat			0x05
		//cdFormat			0x06
		//customerInfo_serviceType_E					0x10
		//customerInfo_energyUnit_E						0x11
		//customerInfo_phaseType_E						0x12
		//customerInfo_customerAccount_creditMode_E		0x13
		//customerInfo_customerAccountbillDate_E		0x14
		//customerInfo_customerAccountcontractDemand_E	0x15
		//customerInfo_customerAccounttariffName_E		0x16
		//customerInfo_serviceType_W					0x20
		//customerInfo_energyUnit_W						0x21
		//customerInfo_customerAccount_creditMode_W		0x22
		//customerInfo_customerAccountbillDate_W		0x23
		//customerInfo_customerAccountcontractDemand_W	0x24
		//customerInfo_customerAccounttariffName_W		0x25
		
		/*
		 * 공통 	
		 */
		Contract contract = new Contract();
		rtnStr += getTypeFrame("01", "BillingDeterminants");	 //고정값
		log.debug("30x01 : "+ "BillingDeterminants");
		
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
		rtnStr += getTypeFrame("02", formatter.format(date));
		log.debug("30x02 : "+ formatter.format(date));
		
		rtnStr += getTypeFrame("03", "Rand");
		log.debug("30x03 : "+ "Rand");
		
		if(supplier != null && supplier.getLang() != null) {
			rtnStr += getTypeFrame("04", supplier.getLang().getName());
			log.debug("30x04 : "+supplier.getLang().getName());
		}
		if(supplier != null && supplier.getMd() != null) {
			rtnStr += getTypeFrame("05", supplier.getMd().getPattern());
			log.debug("30x05 : "+supplier.getMd().getPattern());
		}
		if(supplier != null && supplier.getCd() != null) {
			rtnStr += getTypeFrame("06", supplier.getCd().getPattern());
			log.debug("30x06 : "+supplier.getCd().getPattern());
		}
		/*
		 * EM 	
		 */		
		if(meter_EM != null){
		
			rtnStr += getTypeFrame("10", "Electricity");
			log.debug("30x10 : "+ "Electricity");
			
			rtnStr += getTypeFrame("11", "kWh");
			log.debug("30x11 : "+ "kWh");
			
  		if(((EnergyMeter)meter_EM).getMeterElementCodeId() != null) {
				rtnStr += getTypeFrame("12", codeDao.get(((EnergyMeter)meter_EM).getMeterElementCodeId()).getName());
				log.debug("30x12 : "+codeDao.get(((EnergyMeter)meter_EM).getMeterElementCodeId()).getName());
			}
			if(meter_EM.getContract() != null ){
				contract = contractDao.get(meter_EM.getContract().getId());
				if(meter_EM.getContract().getCreditType() != null) {
					rtnStr += getTypeFrame("13", StringUtil.nullToBlank(contract.getCreditType().getCode()));
					log.debug("30X13 : " + StringUtil.nullToBlank(contract.getCreditType().getCode()));
				}
				if(meter_EM.getContract().getBillDate() != null) {
					rtnStr += getTypeFrame("14", StringUtil.nullToBlank(meter_EM.getContract().getBillDate()));
					log.debug("30X14 : " + StringUtil.nullToBlank(meter_EM.getContract().getBillDate()));
				}
				if(meter_EM.getContract().getContractDemand() != null) {
					rtnStr += getTypeFrame("15", StringUtil.nullToBlank(meter_EM.getContract().getContractDemand()));
					log.debug("30X15 : " + StringUtil.nullToBlank(new BigDecimal(meter_EM.getContract().getContractDemand())));
				}
				if(meter_EM.getContract().getTariffIndex() != null) {
					rtnStr += getTypeFrame("16", StringUtil.nullToBlank(contract.getTariffIndex().getCode()));
					log.debug("30X16 : " + StringUtil.nullToBlank(contract.getTariffIndex().getCode()));
				}
			}
		}
		
		/*
		 * WM 	
		 */
		if(meter_WM != null){
			rtnStr += getTypeFrame("20", "Water");
			log.debug("30x20 : "+ "Water");
			
			rtnStr += getTypeFrame("21", "m3");
			log.debug("30x21 : "+ "m3");
			
	  		if(meter_WM.getContract() != null){
	  			contract = contractDao.get(meter_WM.getContract().getId());
				if(meter_WM.getContract().getCreditType() != null) {
					rtnStr += getTypeFrame("22", StringUtil.nullToBlank(contract.getCreditType().getCode()));
					log.debug("30X22 : " + StringUtil.nullToBlank(contract.getCreditType().getCode()));
				}
				
				if(meter_WM.getContract().getBillDate() != null) {
					rtnStr += getTypeFrame("23", StringUtil.nullToBlank(meter_WM.getContract().getBillDate()));
					log.debug("30X23 : " + StringUtil.nullToBlank(meter_WM.getContract().getBillDate()));
				}
				
				if(meter_WM.getContract().getContractDemand() != null) {
					rtnStr += getTypeFrame("24", StringUtil.nullToBlank(meter_WM.getContract().getContractDemand()));
					log.debug("30X24 : " + StringUtil.nullToBlank(meter_WM.getContract().getContractDemand()));
				}
				
				if(meter_WM.getContract().getTariffIndex() != null) {
					rtnStr += getTypeFrame("25", StringUtil.nullToBlank(contract.getTariffIndex().getCode()));
					log.debug("30X25 : " + StringUtil.nullToBlank(contract.getTariffIndex().getCode()));
				}
			}
		}
		
		return rtnStr;
	}
	
	/**
	 * getTariffsMessage
	 * 
	 * @return String type Data
	 */
	public String getTariffsMessage(){
		log.debug("   *** getTariffsMessage Start ***");
		
		List<TariffEM> tariffEMList = null;
		int tariffEMSize = 0;
		List<TariffWM> tariffWMList = null;
		int tariffWMSize = 0;
		Contract contract = new Contract();
			
		if(meter_EM != null && meter_EM.getContract() != null){
			tariffEMList = tariffEMDao.getNewestTariff(meter_EM.getContract());
			tariffEMSize = tariffEMList.size();
			contract = contractDao.get(meter_EM.getContract().getId());
		}
		
		if(meter_WM != null && meter_WM.getContract() != null){
			tariffWMList = tariffWMDao.getNewestTariff(meter_WM.getContract());
			tariffWMSize = tariffWMList.size();
		}
		
		String rtnStr 	= "";
		
		
//		createdDateTime				0x01		
//		tariff_tariffName			0x02		
//		tariff_phaseType			0x03
//		tariff_energyCharge			0x04
//		tariff_energyLevy			0x05
//		tariff_tou_season			0x06
//		tariff_tou_peakType			0x07
//		tariff_tou_startTime		0x08
//		tariff_tou_endTime			0x09
//		tariff_season_name			0x10
//		tariff_season_startDate		0x11
//		tariff_season_endDate		0x12
//		energy_Type					0x13
//		supplySizeMin_E				0x15  //Block요금제_선불
//		supplySizeMax_E				0x16  //Block요금제_선불
		
		/*
		 * 공통 	
		 */
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
		rtnStr += getTypeFrame("01", formatter.format(date));
		log.debug("31X01 : " + formatter.format(date));

		
        /*
		 * EM 	
		 */
		if(tariffEMSize > 0){
			//후불일때
			if(Code.POSTPAY.equals(contract.getCreditType().getCode())) {
				log.debug("POSTPAY_EM");
				if(tariffEMList.get(0) != null && tariffEMList.get(0).getTariffType() != null){
					rtnStr += getTypeFrame("02", tariffEMList.get(0).getTariffType().getName());
					log.debug("31X02 : " + tariffEMList.get(0).getTariffType().getName());
					
					if(((EnergyMeter)meter_EM).getMeterElementCodeId() != null) {
						rtnStr += getTypeFrame("03", codeDao.get(((EnergyMeter)meter_EM).getMeterElementCodeId()).getName());
						log.debug("31X03 : " + codeDao.get(((EnergyMeter)meter_EM).getMeterElementCodeId()).getName());
					}
					if(tariffEMList.get(0).getActiveEnergyCharge() != null && !"".equals(tariffEMList.get(0).getActiveEnergyCharge())) {
						rtnStr += getTypeFrame("04", StringUtil.nullToBlank(tariffEMList.get(0).getActiveEnergyCharge()));
						log.debug("31X04 : " + StringUtil.nullToBlank(tariffEMList.get(0).getActiveEnergyCharge()));
					}
					if(tariffEMList.get(0).getRateRebalancingLevy() != null && !"".equals(tariffEMList.get(0).getRateRebalancingLevy())) {
						rtnStr += getTypeFrame("05", StringUtil.nullToBlank(tariffEMList.get(0).getRateRebalancingLevy()));
						log.debug("31X05 : " + StringUtil.nullToBlank(tariffEMList.get(0).getRateRebalancingLevy()));
					}
				}
				rtnStr += getTypeFrame("13", "Electricity");
				log.debug("31X13 : " + "Electricity");
				
				for(int cnt=0; cnt<tariffEMSize; cnt++){
					if(tariffEMList.get(cnt) != null && tariffEMList.get(cnt).getTariffType() != null){
						if(tariffEMList.get(cnt).getSeason() != null){
							if(tariffEMList.get(cnt).getSeason() != null) {
								rtnStr += getTypeFrame("06", StringUtil.nullToBlank(tariffEMList.get(cnt).getSeason().getName()));
								log.debug("31X06 : " + StringUtil.nullToBlank(tariffEMList.get(cnt).getSeason().getName()));
							}
							if(tariffEMList.get(cnt).getPeakType() != null) {
								rtnStr += getTypeFrame("07", StringUtil.nullToBlank(tariffEMList.get(cnt).getPeakType().name()));						
								log.debug("31X07 : " + StringUtil.nullToBlank(tariffEMList.get(cnt).getPeakType().name()));
							}
							//TOU 생성
							TOURate touRateEM = touRateDao.getTOURate(tariffEMList.get(cnt).getTariffType().getId(), tariffEMList.get(cnt).getSeason().getId(), tariffEMList.get(cnt).getPeakType());
							if(!(touRateEM == null || "".equals(touRateEM))) {
								rtnStr += getTypeFrame("08", StringUtil.nullToBlank(touRateEM.getStartTime()));
								log.debug("31X08 : " + StringUtil.nullToBlank(touRateEM.getStartTime()));
								rtnStr += getTypeFrame("09", StringUtil.nullToBlank(touRateEM.getEndTime()));
								log.debug("31X09 : " + StringUtil.nullToBlank(touRateEM.getEndTime()));
							}
						}
					}
					
				}
				
				//4계절 정보를 모두 보냄
				List<Season> season = seasonDao.getSeasons();
				for (int i = 0; i < season.size(); i++) {
					rtnStr += getTypeFrame("10", season.get(i).getName().toString());
					log.debug("31X10 : " + season.get(i).getName().toString());
					rtnStr += getTypeFrame("11", season.get(i).getSmonth().toString()+season.get(i).getSday().toString());
					log.debug("31X11 : " + season.get(i).getSmonth().toString()+season.get(i).getSday().toString());
					rtnStr += getTypeFrame("12", season.get(i).getEmonth().toString()+season.get(i).getEday().toString());
					log.debug("31X12 : " + season.get(i).getEmonth().toString()+season.get(i).getEday().toString());
				}
			}
			//선불고객일때
			else if(Code.PREPAYMENT.equals(contract.getCreditType().getCode()) || Code.EMERGENCY_CREDIT.equals(contract.getCreditType().getCode())) {
				log.debug("PREPAYMENT_EM");
				for(int cnt=0; cnt<tariffEMSize; cnt++){
					rtnStr += getTypeFrame("02", tariffEMList.get(0).getTariffType().getName());
					log.debug("31X02 : " + tariffEMList.get(0).getTariffType().getName());
					
					if(((EnergyMeter)meter_EM).getMeterElementCodeId() != null) {
						rtnStr += getTypeFrame("03", codeDao.get(((EnergyMeter)meter_EM).getMeterElementCodeId()).getName());
						log.debug("31X03 : " + codeDao.get(((EnergyMeter)meter_EM).getMeterElementCodeId()).getName());
					}
					if(tariffEMList.get(cnt).getActiveEnergyCharge() != null && !"".equals(tariffEMList.get(cnt).getActiveEnergyCharge())) {
						rtnStr += getTypeFrame("04", StringUtil.nullToBlank(tariffEMList.get(0).getActiveEnergyCharge()));
						log.debug("31X04 : " + StringUtil.nullToBlank(tariffEMList.get(0).getActiveEnergyCharge()));
					}
					if(tariffEMList.get(cnt).getRateRebalancingLevy() != null && !"".equals(tariffEMList.get(cnt).getRateRebalancingLevy())) {
						rtnStr += getTypeFrame("05", StringUtil.nullToBlank(tariffEMList.get(0).getRateRebalancingLevy()));
						log.debug("31X05 : " + StringUtil.nullToBlank(tariffEMList.get(0).getRateRebalancingLevy()));
					}
					
					rtnStr += getTypeFrame("13", "Electricity");
					log.debug("31x13 : Electricity");
//					//Block요금제 내려보냄
					if(tariffEMList.get(cnt).getSupplySizeMin() == null) {
						rtnStr += getTypeFrame("15", "0");
						log.debug("31x15 : 0");
					} else {
						rtnStr += getTypeFrame("15", tariffEMList.get(cnt).getSupplySizeMin().toString());
						log.debug("31x15 : " + tariffEMList.get(cnt).getSupplySizeMin().toString());
					}
					if(tariffEMList.get(cnt).getSupplySizeMax() == null) {
						//tariffEMList의 마지막이 null일경우 0으로 보냄
						rtnStr += getTypeFrame("16", "0");
						log.debug("31x16 : 0");
					} else {
						rtnStr += getTypeFrame("16", tariffEMList.get(cnt).getSupplySizeMax().toString());
						log.debug("31x16 : " + tariffEMList.get(cnt).getSupplySizeMax().toString());
					}
					
					
				}
			}
		}
		
		
		/*
		 * WM 	
		 */
		if(Code.PREPAYMENT.equals(contract.getCreditType().getCode()) || Code.EMERGENCY_CREDIT.equals(contract.getCreditType().getCode())) {
			if(tariffWMSize > 0){
				for(int cnt=0; cnt<tariffWMSize; cnt++){
					if(tariffWMList.get(cnt) != null && tariffWMList.get(cnt).getTariffType() != null){
						rtnStr += getTypeFrame("02", tariffWMList.get(cnt).getTariffType().getName());
						log.debug("31X02 : " + tariffWMList.get(cnt).getTariffType().getName());
						
						if(tariffWMList.get(cnt).getUsageUnitPrice() != null) {
							rtnStr += getTypeFrame("04", StringUtil.nullToBlank(tariffWMList.get(cnt).getUsageUnitPrice()));
							log.debug("31X04 : " + StringUtil.nullToBlank(tariffWMList.get(cnt).getUsageUnitPrice()));
						} 
						if(tariffWMList.get(cnt).getShareCost() != null) {
							rtnStr += getTypeFrame("05", StringUtil.nullToBlank(tariffWMList.get(cnt).getShareCost()));
							log.debug("31X05 : " + StringUtil.nullToBlank(tariffWMList.get(cnt).getShareCost()));
						}
						rtnStr += getTypeFrame("13", "Water");
						log.debug("31X13 : Water");
						
						//Block요금제 내려보냄
						if(tariffWMList.get(cnt).getSupplySizeMin() == null) {
							rtnStr += getTypeFrame("15", "0");
							log.debug("31x15 : 0");
						} else {
							rtnStr += getTypeFrame("15", tariffWMList.get(cnt).getSupplySizeMin().toString());
							log.debug("31x15 : " + tariffWMList.get(cnt).getSupplySizeMin().toString());
						}
						if(tariffWMList.get(cnt).getSupplySizeMax() == null) {
							//tariffEMList의 마지막이 null일경우 0으로 보냄
							rtnStr += getTypeFrame("16", "0");
							log.debug("31x16 : 0");
						} else {
							rtnStr += getTypeFrame("16", tariffWMList.get(cnt).getSupplySizeMax().toString());
							log.debug("31x16 : " + tariffWMList.get(cnt).getSupplySizeMax().toString());
						}
					}
				}
			}
		}

		return rtnStr;
	}
	
	/**
	 * getIHDMainInfosMessage
	 * 
	 * @return String type Data
	 * @throws ParseException 
	 */
	public String getIHDMainInfosMessage() throws ParseException{
		log.debug("   *** getIHDMainInfosMessage Start ***");
		String rtnStr 	= "";
		//createdDateTime						0x01
		//ihdMainInfo_serviceType_E			0x02
		//ihdMainInfo_cummulativeTotal_E	0x03
		//ihdMainInfo_meterDateTime_E		0x04
		//ihdMainInfo_availableCredit_E		0x05
		//ihdMainInfo_lastCreditDateTime_E	0x06
		//ihdMainInfo_lastestCredit_E		0x07
//		ihdMainInfo_serviceType_W			0x10
//		ihdMainInfo_cummulativeTotal_W		0x11
//		ihdMainInfo_meterDateTime_W			0x12
//		ihdMainInfo_availableCredit_W		0x13
//		ihdMainInfo_lastCreditDateTime_W	0x14
//		ihdMainInfo_lastestCredit_W			0x15
		
		/*
		 * 공통	
		 */
			Date date = new Date();
			Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
			rtnStr += getTypeFrame("01", formatter.format(date));
			log.debug("31X01 : " + formatter.format(date));
			/*
			 * EM
			 */
	  		if(meter_EM != null && meter_EM.getContract() != null)  {
	  			Contract contractEM = contractDao.get(meter_EM.getContract().getId());
	  			
				rtnStr += getTypeFrame("02", "Electricity");	
				log.debug("31X02 : Electricity");
				
				Map<String, Object> cummulativeTotalMap = null;
				cummulativeTotalMap = billingDayEMDao.getSelDate(meter_EM.getContract().getId(), TimeUtil.getCurrentDay());
				
				if(cummulativeTotalMap != null) {
					if(cummulativeTotalMap.get("usage") != null) {
						rtnStr += getTypeFrame("03", cummulativeTotalMap.get("usage").toString()) ;
						log.debug("32X03 : " + cummulativeTotalMap.get("usage"));
					}
				}
				
				if(meter_EM.getLastReadDate() != null) {
					rtnStr += getTypeFrame("04", TimeUtil.formatDateTime(meter_EM.getLastReadDate()));
					log.debug("32X04 : " + TimeUtil.formatDateTime(meter_EM.getLastReadDate()));
				}
				if(Code.PREPAYMENT.equals(contractEM.getCreditType().getCode())  || Code.EMERGENCY_CREDIT.equals(contractEM.getCreditType().getCode())) {	
					if(meter_EM.getContract().getCurrentCredit() != null) {
						rtnStr += getTypeFrame("05", new BigDecimal(meter_EM.getContract().getCurrentCredit()).toString());
						log.debug("32X05 : " + new BigDecimal(meter_EM.getContract().getCurrentCredit()).toString());
					}
					if(meter_EM.getContract().getLastTokenDate() != null) {
						rtnStr += getTypeFrame("06", TimeUtil.formatDateTime(meter_EM.getContract().getLastTokenDate()));
						log.debug("32X06 : " + TimeUtil.formatDateTime(meter_EM.getContract().getLastTokenDate()));
					}
					if(meter_EM.getContract().getContractNumber() != null && meter_EM.getContract().getLastTokenDate() != null){
						Map<String, Object> conditionMap = new HashMap<String, Object>();
						conditionMap.put("contractNumber", meter_EM.getContract().getContractNumber());
						conditionMap.put("lastTokenDate", meter_EM.getContract().getLastTokenDate());
						
						List<Map<String, Object>> proPayListEM = prepaymentLogDao.getChargeHistoryByLastTokenDate(conditionMap);
						log.debug("proPayListEM : " + proPayListEM);
						if(!proPayListEM.isEmpty()) {
							Map<String, Object> proPayMap = prepaymentLogDao.getChargeHistoryByLastTokenDate(conditionMap).get(0);
							rtnStr += getTypeFrame("07", StringUtil.nullToBlank(proPayMap.get("chargedCredit")));
							log.debug("32X07 : " + StringUtil.nullToBlank(proPayMap.get("chargedCredit")));
						}
					}
					
				}		
	  		}
	  		/*
			 * WM	
			 */ 	
			if(meter_WM!=null && meter_WM.getContract() != null){
				Contract contractWM = contractDao.get(meter_WM.getContract().getId());
				
				rtnStr += getTypeFrame("10", "Water");
				log.debug("31X10 : Water");
				
				Map<String, Object> cummulativeTotalMap_W = null;
				cummulativeTotalMap_W = billingDayWMDao.getSelDate(meter_WM.getContract().getId(), TimeUtil.getCurrentDay());
				
				if(cummulativeTotalMap_W != null) {
					if(cummulativeTotalMap_W.get("usage")!=null) {
						rtnStr += getTypeFrame("11", cummulativeTotalMap_W.get("usage").toString()) ;
						log.debug("32X11 : " + getTypeFrame("11",cummulativeTotalMap_W.get("usage").toString()));
					}
				}
		
				if(meter_WM.getLastReadDate() != null) {
					rtnStr += getTypeFrame("12", TimeUtil.formatDateTime(meter_WM.getLastReadDate()));
					log.debug("32X12 : " + TimeUtil.formatDateTime(meter_WM.getLastReadDate()));
				}
				if(Code.PREPAYMENT.equals(contractWM.getCreditType().getCode())  || Code.EMERGENCY_CREDIT.equals(contractWM.getCreditType().getCode())) {
					if(meter_WM.getContract().getCurrentCredit() != null) {
						rtnStr += getTypeFrame("13", meter_WM.getContract().getCurrentCredit().toString());
						log.debug("32X13 : " + meter_WM.getContract().getCurrentCredit().toString());
					}
			
					if(meter_WM.getContract().getLastTokenDate() != null) {
						rtnStr += getTypeFrame("14", TimeUtil.formatDateTime(meter_WM.getContract().getLastTokenDate()));
						log.debug("32X14 : " + TimeUtil.formatDateTime(meter_WM.getContract().getLastTokenDate()));
					}
			
					if(meter_WM.getContract().getContractNumber() != null && meter_WM.getContract().getLastTokenDate() != null){
						Map<String, Object> conditionMap_WM = new HashMap<String, Object>();
						conditionMap_WM.put("contractNumber", meter_WM.getContract().getContractNumber());
						conditionMap_WM.put("lastTokenDate", meter_WM.getContract().getLastTokenDate());
						List<Map<String, Object>> proPayListWM = prepaymentLogDao.getChargeHistoryByLastTokenDate(conditionMap_WM);
	
						if(!proPayListWM.isEmpty()) {
							Map<String, Object> proPayMap_WM = prepaymentLogDao.getChargeHistoryByLastTokenDate(conditionMap_WM).get(0);
							rtnStr += getTypeFrame("15", StringUtil.nullToBlank(proPayMap_WM.get("chargedCredit")));
							log.debug("32X15 : " + StringUtil.nullToBlank(proPayMap_WM.get("chargedCredit")));
						}
					}
				}
			}
		return rtnStr;
		
	}
	
	/**
	 * getEventMessage
	 * 초기 Install 성공 메세지 전송
	 * 
	 * @return String type Data
	 */
	public String getEventMessage(){
		Date date = new Date();
		String rtnStr = "";			
		
		//초기설치 시 이벤트 메세지			
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
		rtnStr += getTypeFrame("01", formatter.format(date));
		log.debug("33X01 : " + formatter.format(date));
		rtnStr += getTypeFrame("02", "Welcome to IHD");
		log.debug("33X02 : " + "Welcome to IHD");
		rtnStr += getTypeFrame("03", "IHD install is completed successfully. You can access your consumption information through IHD.");
		log.debug("33X03 : " + "IHD install is completed successfully. You can access your consumption information through IHD.");
		
		return rtnStr;
	}

	/**
	 * getCustomerUpdateInfosMessage
	 * 초기 설계시 기초데이터 였으나 동일값이 CMD[30]번으로 가고 있으므로 나중에 빠짐 - 따라서 주석처리 해놓음 
	 * 기초데이터에서 빠진 이유 : CustomerInfosMessage에 해당 값이 포함되어 있어 빠짐.
	 * @return String type Data
	 */
	/*
	public String getCustomerUpdateInfosMessage(){ 
		log.debug("   *** getCustomerUpdateInfosMessage Start ***");
		String rtnStr 	= "";
		
//		  공통 	
//		createDateTime								0x01
//		customerUpdateInfo_supplyCapacityLimit	0x02
		
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
		rtnStr += getTypeFrame("01", formatter.format(date));

		if(meter_EM !=null && meter_EM.getContract() != null && 
		        meter_EM.getContract().getContractDemand() != null &&
				!meter_EM.getContract().getContractDemand().equals("")) {
			rtnStr += getTypeFrame("02", meter_EM.getContract().getContractDemand().toString());
			log.debug("34X02 : " + meter_EM.getContract().getContractDemand().toString());
		}				
				
		return rtnStr;
	}
	*/	
	
	/**
	 * getBillingInfosMessage
	 * @return String type Data
	 * 
	 * @date : 2013.01.10
	 * 11월의 사용량은 11월의 DB에 저장되도록 되어있음
	 * 현재 정책 : 사용량 - 11월 01일 ~ 11월 30일 까지의 사용량
	 *         Billing Date - 12월 20일에 나옴
	 *         납입기간 - 01월 12일전까지 
	 *         
	 *         BillingMonthEM에 저장시 11월의 사용량에 해당하는 Bill갑은 yyyymmdd값이 12월01인 레코드의 bill을 저장시킨다. 
	 */
	public String getBillingInfosMessage(String from, String to){
		log.debug("   *** getBillingInfosMessage Start ***");
		String rtnStr 	= "";
		
//		createdDateTime			0x01
//		billingYYYYMM_E			0x02
//		billingCycle_E			0x03
//		bill_E					0x04
//		billingYYYYMM_W			0x06
//		billingCycle_W			0x07
//		bill_W					0x08
//		bill_from_yyyymm		0x10
//		bill_to_yyyymm			0x20

		SimpleDateFormat sd = new SimpleDateFormat("yyyyMM");
		if(from.isEmpty()) {
			from = sd.format(new Date());
		}
		if(to.isEmpty()) {
			to = sd.format(new Date());
		}
		
		//저장된 날짜 : 20121101~ 20121131 까지의 경우 12월 1일에 저장되므로 savebillDate는 20121201 이 된다.
		String savebillDate = null;
		String usageStartDate = null;
		String usageEndDate = null;

		
		/*
		 * 공통 
		 */
		Contract contract = new Contract();
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
		rtnStr += getTypeFrame("01", formatter.format(date));
		log.debug("0x01 : " + formatter.format(date));
		
		try {
			for (int i=Integer.parseInt(from); i<=Integer.parseInt(to); i=Integer.parseInt(TimeUtil.getPreMonth(i+"01000000",-1).substring(0,6))) {
				
				savebillDate = String.valueOf(i)+"01";
				usageStartDate = TimeUtil.getPreMonth(savebillDate+"01000000").substring(0,8) ; 
				usageEndDate = usageStartDate.substring(0,6)+CalendarUtil.getMonthLastDate(usageStartDate.substring(0,4), usageStartDate.substring(4,6));	
				/*
				 * EM
				 */
				if(meter_EM != null) {
					rtnStr += getTypeFrame("02", savebillDate.substring(0,6));
					log.debug("35X02 : " + savebillDate.substring(0,6));
					rtnStr += getTypeFrame("03", usageStartDate + "~" + usageEndDate);
					log.debug("35X03 : " + usageStartDate + "~" + usageEndDate);
					
					if(meter_EM.getContract() != null && meter_EM.getContract().getCreditType() != null) {
						contract = contractDao.get(meter_EM.getContract().getId());
						//후불일 경우  
						if(Code.POSTPAY.equals(contract.getCreditType().getCode())) {
							log.debug("POSTPAY_EM");
							Set<Condition> condition = new HashSet<Condition>();
							condition.add(new Condition("id.mdevId", new Object[]{ meter_EM.getMdsId() }, null, Restriction.EQ));
							condition.add(new Condition("id.yyyymmdd", new Object[]{ savebillDate }, null, Restriction.EQ));
							condition.add(new Condition("id.mdevType", new Object[]{ DeviceType.Meter }, null, Restriction.EQ));
							List<BillingMonthEM> billingMonthEM = billingMonthEMDao.findByConditions(condition);
							if(billingMonthEM.size() > 0 && (billingMonthEM.get(0).getBill() != null)) {
								rtnStr += getTypeFrame("04", billingMonthEM.get(0).getBill().toString());
								log.debug("35X04 : " + billingMonthEM.get(0).getBill().toString());
							} else {
								log.debug("35x04 : Not exist bill Data");
							}
						}
					}
				}
				/*
				 * WM
				 */
				if(meter_WM != null) {
					rtnStr += getTypeFrame("06", savebillDate.substring(0,6));
					log.debug("35X06 : " + savebillDate.substring(0,6));
					rtnStr += getTypeFrame("07", usageStartDate + "~" + usageEndDate);
					log.debug("35X07 : " + usageStartDate + "~" + usageEndDate);
					if(meter_WM.getContract() != null && meter_WM.getContract().getCreditType() != null){
						contract = contractDao.get(meter_WM.getContract().getId());
			            if(Code.POSTPAY.equals(contract.getCreditType().getCode())) {
			            	log.debug("POSTPAY_WM"); 
			            	Set<Condition> condition = new HashSet<Condition>();
			            	condition.add(new Condition("id.mdevId", new Object[]{ meter_WM.getMdsId() }, null, Restriction.EQ));
							condition.add(new Condition("id.yyyymmdd", new Object[]{ savebillDate }, null, Restriction.EQ));
							condition.add(new Condition("id.mdevType", new Object[]{ DeviceType.Meter }, null, Restriction.EQ));
							List<BillingMonthWM> billingMonthWM = billingMonthWMDao.findByConditions(condition);
							if(billingMonthWM.size() > 0 && (billingMonthWM.get(0).getBill() != null)) {
					            rtnStr += getTypeFrame("08", billingMonthWM.get(0).getBill().toString());
					            log.debug("35X08 : " + billingMonthWM.get(0).getBill());
							} else {
								log.debug("35x08 : Not exist bill Data");
							}
			            }
					}
				}
			} 
		} catch (Exception e) {
			// TODO: handle exception
			log.error(e,e);
		}

		rtnStr += getTypeFrame("10", from);
		rtnStr += getTypeFrame("20", to);
		log.debug("35X10 : " + from);
		log.debug("35X11 : " + to);
		
		
		return rtnStr;
		
	}
    
	/**
	 * getTypeFrame
	 * 
	 * @param type
	 * @param data
	 * @return DATA필드의 Type 프레임 리턴(Type(1), TypeLength(1), Data(가변))
	 */
	public String getTypeFrame(String type, String data){
		
		if(data.length()<1){
			return "";
		}
		String returnStr 	= "";
		byte[] dataBytes 	= data.getBytes();
		String dataSize		= String.format("%02X", DataUtil.getByteToInt(dataBytes.length));
		
		returnStr += type;
		returnStr += dataSize;
		returnStr += Hex.decode(dataBytes);
		
		return getRemoveSpaces(returnStr);
	}
	
	/**
	 * getRemoveSpaces
	 * 
	 * @param str
	 * @return 공배제거하여 String 타입으로 리턴
	 */
	public String getRemoveSpaces(String str){
		return str.replaceAll(" ", "");
	}
	
}
