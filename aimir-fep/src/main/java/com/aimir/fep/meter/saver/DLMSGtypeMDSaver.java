package com.aimir.fep.meter.saver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.ElectricityChannel;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.constants.CommonConstants.PeakType;
import com.aimir.dao.mvm.DaesungMeteringDataDao;
import com.aimir.dao.mvm.LpEMDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.mvm.RealTimeBillingEMDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.fep.command.conf.DLMSMeta.LOAD_CONTROL_STATUS;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.command.mbean.CommandGW.OnDemandOption;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.DLMSGtype;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.DaesungMeteringData;
import com.aimir.model.mvm.LpEM;
import com.aimir.model.mvm.MonthEM;
import com.aimir.model.mvm.RealTimeBillingEM;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.TariffEM;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.Condition.Restriction;

@Service
public class DLMSGtypeMDSaver extends AbstractMDSaver {
    private static Log log = LogFactory.getLog(DLMSGtypeMDSaver.class);
	
    @Autowired
    LpEMDao lpEMDao;
    
    @Autowired
    MonthEMDao monthEMDao;
    
    @Autowired
    TariffEMDao tariffDao;
    
    @Autowired
    RealTimeBillingEMDao billingEmDao;
    
    @Autowired
    DaesungMeteringDataDao dmDao;
    
    @Override
    public boolean save(IMeasurementData md) throws Exception {
        try {
            DLMSGtype parser = (DLMSGtype) md.getMeterDataParser();
    
            // 마지막 LP 데이타를 가져와서 LP를 다시 구성한다.
            LPData lastLp = null; // getLastLp(parser.getMeterID(), parser.getMeter().getInstallProperty(), 
                    // parser.getMeter().getPulseConstant());
            parser.setLPData(lastLp);
            parser.setMeteringValue();
            LPData[] lplist = parser.getLPData();
            
            // log.debug("active pulse constant:" +
            // parser.getActivePulseConstant());
            // log.debug("currentDemand:" + currentDemand);
            
            if (lplist == null || lplist.length <= 1) {
                log.debug("LPSIZE => 0");
            } else {
                log.info("lplist[0]:"+lplist[1]);
                log.info("lplist[0].getDatetime():"+lplist[1].getDatetime());
    			
                String startlpdate = lplist[1].getDatetime();
                String lpdatetime = startlpdate;
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
                cal.setTime(sdf.parse(startlpdate));
                List<Double>[] chValue = new ArrayList[lplist[1].getCh().length];
                List<Integer> flag = new ArrayList<Integer>();
                double baseValue = lplist[1].getCh()[1];
    	        
                for (int i = 1; i < lplist.length; i++) {
                    if (!lpdatetime.equals(lplist[i].getDatetime())) {
                        saveLPData(chValue, flag, startlpdate, baseValue, parser);
    	        		
                        startlpdate = lplist[i].getDatetime();
                        lpdatetime = startlpdate;
                        baseValue = lplist[i].getLpValue();
                        flag = new ArrayList<Integer>();
                        chValue = new ArrayList[lplist[i].getCh().length];
                    }
                    flag.add(lplist[i].getFlag());
            		
                    for (int ch = 0; ch < chValue.length; ch++) {
                        if (chValue[ch] == null) chValue[ch] = new ArrayList<Double>();
            			
                        if (ch+1 <= lplist[i].getCh().length)
                            chValue[ch].add(lplist[i].getCh()[ch]);
                        else
                            chValue[ch].add(0.0);
                    }
                    cal.add(Calendar.MINUTE, parser.getMeter().getLpInterval());
                    lpdatetime = sdf.format(cal.getTime());
                }
                saveLPData(chValue, flag, startlpdate, baseValue, parser);
                BillingData billData = saveBill(parser);
                saveMeterEventLog(parser);
    	        
                if (billData != null && parser.getMeter().getContract() != null)
                    saveDaesungMeteringData(parser.getMeter().getContract().getContractNumber(),
                           parser.getMDevId(), billData, lplist[lplist.length-1]);
                
                Instrument pq = parser.getInstrument();
                if (pq != null) {
                    savePowerQuality(parser.getMeter(), parser.getMeteringTime().substring(0, 12), 
                            new Instrument[]{pq}, parser.getDeviceType(), parser.getDeviceId(),
                            parser.getMDevType(), parser.getMDevId());
                }
                
                try {
                    /*saveMeteringData(MeteringType.Normal, md.getTimeStamp().substring(0,8),
                             md.getTimeStamp().substring(8, 14), parser.getMeteringValue(),
                             parser.getMeter(), DeviceType.MCU, parser.getMeter().getMcu().getSysID(),
                             DeviceType.Meter, parser.getMeterID(), parser.getMeterTime());*/
                 // 미터와 모뎀 최종 통신 시간과 값을 갱신한다.
                    // if (meter.getLastMeteringValue() == null || meter.getLastMeteringValue() < meteringValue)
                    //    meter.setLastMeteringValue(meteringValue);
                    Meter meter = parser.getMeter();
                    String dsttime = DateTimeUtil.getDST(null, md.getTimeStamp());
                    if (meter.getLastReadDate() == null || dsttime.compareTo(meter.getLastReadDate()) > 0) {
                        meter.setLastReadDate(dsttime);
                        Code meterStatus = CommonConstants.getMeterStatusByName(MeterStatus.Normal.name());
                        log.debug("METER_STATUS[" + (meterStatus==null? "NULL":meterStatus.getName()) + "]");
                        meter.setMeterStatus(meterStatus);
                        meter.setLastMeteringValue(parser.getMeteringValue());
                        
                        String meterTime = parser.getMeterTime();
                        if (meterTime != null && !"".equals(meterTime)) {
                            long diff = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(md.getTimeStamp()).getTime() - 
                                    DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meterTime).getTime();
                            meter.setTimeDiff(diff / 1000);
                        }
                        
                     // 수검침과 같이 모뎀과 관련이 없는 경우는 예외로 처리한다.
                        if (meter.getModem() != null) {
                            meter.getModem().setLastLinkTime(dsttime);
                        }
                    }
                }
                catch (Exception ignore) {}
            }
	    log.info(parser.getMDevId() + " Metering END......!!!!");	
        }
        catch (Exception e) {
            log.error(e, e);
            throw e;
        }
        return true;
    }
	
    private void saveDaesungMeteringData(String contractNo, String mdsId, BillingData billData, LPData lpData) {
        /*
        Set<Condition> cond = new LinkedHashSet<Condition>();
        cond.add(new Condition("id.contractNumber", new Object[]{contractNo}, null, Restriction.EQ));
        List<DaesungMeteringData> mdDataList = dmDao.findByConditions(cond);
         */
		
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		log.info("ContractNo[" + contractNo + "] MDS_ID[" + mdsId + "]");
        try {
            Set<Condition> cond = new LinkedHashSet<Condition>();
            cond.add(new Condition("id.mdevId", new Object[]{mdsId}, null, Restriction.EQ));
            cond.add(new Condition("id.yyyymm", new Object[]{lpData.getDatetime().substring(0, 6)}, null, Restriction.EQ));
            cond.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
            cond.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
            
            List<MonthEM> monthList = monthEMDao.findByConditions(cond);
            mdsId = mdsId.substring(5);
            
            if (monthList != null && monthList.size() > 0) {
                double activeEnergy = 0.0;
                double exportActiveEnergy = 0.0;
                double reactiveEnergy = 0.0;
                double reactiveLagEnergy = 0.0;
                double reactiveLeadEnergy = 0.0;
                
                for (MonthEM m : monthList.toArray(new MonthEM[0])) {
                    if (m.getChannel() == 2) {
                        activeEnergy = m.getTotal();
                    }
                    else if (m.getChannel() == 3) {
                        reactiveLagEnergy = m.getTotal();
                    }
                    else if (m.getChannel() == 4) {
                        reactiveLeadEnergy = m.getTotal();
                    }
                    else if (m.getChannel() == 5) {
                        reactiveEnergy = m.getTotal();
                    }
                    else if (m.getChannel() == 13) {
                        exportActiveEnergy = m.getTotal();
                    }
                }
                
                Date lpDate = sdf.parse(lpData.getDatetime()+"00");
    
                DaesungMeteringData mdData = new DaesungMeteringData();
                mdData.setContractNumber(contractNo);
                mdData.setYyyymmddhhmmss(lpDate);
                mdData.setMdsId(mdsId);
                mdData.setResultCd("N");
    				
                // 유효전력량
                mdData.setWdvFlag("W");
                mdData.setMidValue((int)(activeEnergy - exportActiveEnergy));
                mdData.setMidImportValue((int)activeEnergy);
                mdData.setMidExportValue((int)exportActiveEnergy);
                dmDao.saveOrUpdate(mdData);
    				
                // 최대수요전
                mdData = new DaesungMeteringData();
                mdData.setContractNumber(contractNo);
                mdData.setYyyymmddhhmmss(lpDate);
                mdData.setMdsId(mdsId);
                mdData.setResultCd("N");
                mdData.setWdvFlag("D");
                mdData.setMidValue(billData.getActivePowerMaxDemandRateTotal().intValue());
                dmDao.saveOrUpdate(mdData);
    				
                // 무효전력량
                mdData = new DaesungMeteringData();
                mdData.setContractNumber(contractNo);
                mdData.setYyyymmddhhmmss(lpDate);
                mdData.setMdsId(mdsId);
                mdData.setResultCd("N");
                mdData.setWdvFlag("V");
                mdData.setMidValue((int)reactiveLagEnergy);
                mdData.setMidReactiveLeadValue((int)reactiveLeadEnergy);
                dmDao.saveOrUpdate(mdData);
            }
        }
        catch (Exception e) {
            log.warn(e, e);
        }
    }
	
    private LPData getLastLp(String meterId, String status, double activeContant) {
        List<LpEM> list = lpEMDao.getLastData(meterId);
        if (list == null || list.size() == 0) return null;
        else {
            LPData lpData = new LPData();
            lpData.setStatus(status);
            Double[] ch = new Double[5];
            for (LpEM l : list.toArray(new LpEM[0])) {
                if (lpData.getLp() == null && l.getValue() != null) {
                    lpData.setLp(l.getValue().doubleValue()*activeContant);
                    lpData.setLpValue(l.getValue().doubleValue());
                }
			
                if (l.getValue_45() != null) {
                    lpData.setDatetime(l.getYyyymmddhh() + "45");
                    if (l.getChannel() == 2) {
                        ch[0] = l.getValue_45();
                    }
                    else if (l.getChannel() == 3) {
                        ch[1] = l.getValue_45();
                    }
                    else if (l.getChannel() == 4) {
                        ch[2] = l.getValue_45();
                    }
                    else if (l.getChannel() == 5) {
                        ch[3] = l.getValue_45();
                    }
                    else if (l.getChannel() == 13) {
                        ch[4] = l.getValue_45();
                    }
                }
                else if (l.getValue_30() != null) {
                    lpData.setDatetime(l.getYyyymmddhh() + "30");
                    if (l.getChannel() == 2) {
                        ch[0] = l.getValue_30();
                    }
                    else if (l.getChannel() == 3) {
                        ch[1] = l.getValue_30();
                    }
                    else if (l.getChannel() == 4) {
                        ch[2] = l.getValue_30();
                    }
                    else if (l.getChannel() == 5) {
                        ch[3] = l.getValue_30();
                    }
                    else if (l.getChannel() == 13) {
                        ch[4] = l.getValue_30();
                    }
                }
                else if (l.getValue_15() != null) {
                    lpData.setDatetime(l.getYyyymmddhh() + "15");
                    if (l.getChannel() == 2) {
                        ch[0] = l.getValue_15();
                    }
                    else if (l.getChannel() == 3) {
                        ch[1] = l.getValue_15();
                    }
                    else if (l.getChannel() == 4) {
                        ch[2] = l.getValue_15();
                    }
                    else if (l.getChannel() == 5) {
                        ch[3] = l.getValue_15();
                    }
                    else if (l.getChannel() == 13) {
                        ch[4] = l.getValue_15();
                    }
                }
                else if (l.getValue_00() != null) {
                    lpData.setDatetime(l.getYyyymmddhh() + "00");
                    if (l.getChannel() == 2) {
                        ch[0] = l.getValue_00();
                    }
                    else if (l.getChannel() == 3) {
                        ch[1] = l.getValue_00();
                    }
                    else if (l.getChannel() == 4) {
                        ch[2] = l.getValue_00();
                    }
                    else if (l.getChannel() == 5) {
                        ch[3] = l.getValue_00();
                    }
                    else if (l.getChannel() == 13) {
                        if (l.getValue_00() == null)
                            ch[4] = 0.0;
                        else
                            ch[4] = l.getValue_00();
                    }
                }
            }
            lpData.setCh(ch);
            return lpData;
        }
    }
	
    public void saveLPData(List<Double>[] chValue, List<Integer> flag, String startlpdate, double baseValue, DLMSGtype parser)
            throws Exception {
        double[][] _lplist = new double[chValue.length][chValue[0].size()];
        for (int ch = 0; ch < _lplist.length; ch++) {
            for (int j = 0; j < _lplist[ch].length; j++) {
                if (chValue[ch].get(j) != null)
                    _lplist[ch][j] = chValue[ch].get(j);
                else
                    _lplist[ch][j] = 0.0;
            }
        }
        int[] _flag = new int[chValue[0].size()];
        for (int j = 0; j < _flag.length; j++) {
            _flag[j] = flag.get(j);
        }
        super.saveLPData(MeteringType.Normal, startlpdate.substring(0, 8), startlpdate.substring(8)+"00",
                _lplist, _flag, baseValue, parser.getMeter(),
                DeviceType.MCU, parser.getMeter().getMcu().getSysID(),
                DeviceType.Meter, parser.getMeterID());
    }
	
    private void saveMeterEventLog(DLMSGtype parser) throws Exception {
        Meter meter = parser.getMeter();
        if (meter.getInstallProperty() != null && meter.getInstallProperty().length() != "000001000000000000000000".length())
            meter.setInstallProperty(null);
        
        LPData[] lpData = parser.getLPData();
		
        for (LPData l : lpData) {
            if (meter.getInstallProperty() == null || !meter.getInstallProperty().equals(l.getStatus())) {
                // 미터 이벤트 상태가 다르면 발생한다.
                List<EventLogData> events = new ArrayList<EventLogData>();
                int prevState = 0;
                int curState = 0;
                if (l.getStatus() != null) {
                    for (int flag = 0; flag < l.getStatus().length(); flag++) {
                        // Sag/Swell 과 정복전 처리는 이벤트 로그를 읽은 것으로 처리한다.
                        if (flag == 8 || flag == 17) continue;
                        
                        if (meter.getInstallProperty() == null)
                            prevState = 0;
                        else
                            prevState = Integer.parseInt(meter.getInstallProperty().substring(flag, flag+1));
						
                        curState = Integer.parseInt(l.getStatus().substring(flag, flag+1));
                        if (prevState == 0 && curState == 1) {
                            EventLogData e = new EventLogData();
                            e.setDate(l.getDatetime().substring(0, 8));
                            e.setTime(l.getDatetime().substring(8)+"00");
                            e.setFlag(flag);
                            log.debug(e.toString());
                            events.add(e);
                        }
                        else if (prevState == 1 && curState == 0) {
                            // 13:전류제한차단해제, 17:복전, 18:시간변경후
                            if (flag == 13 || flag == 17 || flag == 18) {
                                EventLogData e = new EventLogData();
                                e.setDate(l.getDatetime().substring(0, 8));
                                e.setTime(l.getDatetime().substring(8)+"00");
                                e.setFlag(flag+100);
                                log.debug(e.toString());
                                events.add(e);
                            }
                        }
                    }
                    saveMeterEventLog(meter, events.toArray(new EventLogData[0]));
                    meter.setInstallProperty(l.getStatus());
                }
            }
        }
        // sag, swell, 정복전은 로그로 처리한다.
        saveMeterEventLog(meter, parser.getMeterEvent());
    }
	
    private void saveBill(DLMSGtype parser, RealTimeBillingEM billing) throws Exception {
        Set<Condition> cond = new LinkedHashSet<Condition>();
        cond.add(new Condition("id.mdevId", new Object[]{parser.getMDevId()}, null, Restriction.EQ));
        cond.add(new Condition("id.mdevType", new Object[]{parser.getMDevType()}, null, Restriction.EQ));
        cond.add(new Condition("id.yyyymmdd", new Object[]{billing.getYyyymmdd()}, null, Restriction.EQ));
        cond.add(new Condition("id.hhmmss", new Object[]{billing.getHhmmss()}, null, Restriction.EQ));
        List<RealTimeBillingEM> billings = billingEmDao.findByConditions(cond); 
        if (billings == null || billings.size() == 0)
            billingEmDao.add(billing); 
    }
	
    private RealTimeBillingEM saveBill(RealTimeBillingEM baseBilling, LPData[] lpData, DLMSGtype parser, List<TariffEM> tous)
    throws Exception {
        if (baseBilling == null) {
            baseBilling = makeBaseBilling(lpData[1], parser, tous);
            // billingEmDao.saveOrUpdate(baseBilling);
            saveBill(parser, baseBilling);
    
            for (int i = 2; i < lpData.length; i++) {
                if (!(baseBilling.getYyyymmdd()+baseBilling.getHhmmss()).substring(0, 12).equals(lpData[i].getDatetime().substring(0, 12))) {
                    // 월이 변경되면 새로 생성한다.
                    if (baseBilling.getYyyymmdd().substring(4,6).equals(lpData[i].getDatetime().substring(4,6))) {
                        baseBilling = makeRealTimeBilling(baseBilling, lpData[i], parser, tous);
                    }
                    else {
                        baseBilling = makeBaseBilling(lpData[i], parser, tous);
                    }
                    // billingEmDao.saveOrUpdate(baseBilling);
                    saveBill(parser, baseBilling);
                }
            }
        }
        else {
            for (int i = 1; i < lpData.length; i++) {
                if (!(baseBilling.getYyyymmdd()+baseBilling.getHhmmss()).substring(0, 12).equals(lpData[i].getDatetime().substring(0, 12))) {
                    if (baseBilling.getYyyymmdd().substring(4,6).equals(lpData[i].getDatetime().substring(4,6))) {
                        baseBilling = makeRealTimeBilling(baseBilling, lpData[i], parser, tous);
                    }
                    else {
                        baseBilling = makeBaseBilling(lpData[i], parser, tous);
                    }
                    // billingEmDao.saveOrUpdate(baseBilling);
                    saveBill(parser, baseBilling);
                }
            }
        }
        return baseBilling;
    }
    
    private BillingData saveBill(DLMSGtype parser) {
        try {
            // 계약 Tariff 정보를 가져온다.
            Contract contract = parser.getMeter().getContract();
            LPData[] lpData = parser.getLPData();
            RealTimeBillingEM baseBilling = null;
            if (contract != null && lpData.length > 1) {
                List<TariffEM> tous = tariffDao.getNewestTariff(contract, lpData[1].getDatetime().substring(0, 12));
                if (tous != null && tous.size() > 0) {
                    // 적용일이 가장 최근 것을 가져온다.
    				
                    // LP의 첫번째 realtime_billing 데이타를 가져온다.
                    // 없으면 마지막 realtime_billing 데이타를 가져온다.
                    // 없으면 생성한다.
                    // 매월 1일 새로 생성하기 때문에 LP의 날짜가 1일 0000 데이타의 경우는 무조건 새로 생성한다.
                    if (lpData[1].getDatetime().substring(6, 12).equals("010000")) {
                        baseBilling = saveBill(null, lpData, parser, tous);
                    }
                    else {
                        Set<Condition> cond = new LinkedHashSet<Condition>();
                        cond.add(new Condition("id.mdevId", new Object[]{parser.getMDevId()}, null, Restriction.EQ));
                        cond.add(new Condition("id.mdevType", new Object[]{parser.getMDevType()}, null, Restriction.EQ));
                        cond.add(new Condition("id.yyyymmdd", new Object[]{lpData[1].getDatetime().substring(0, 8)}, null, Restriction.EQ));
                        cond.add(new Condition("id.hhmmss", new Object[]{lpData[1].getDatetime().substring(8, 12)+"00"}, null, Restriction.EQ));
                        List<RealTimeBillingEM> billing = billingEmDao.findByConditions(cond);
                        if (billing != null && billing.size() == 1) {
                            baseBilling = billing.get(0);
                        }
                        else {
                            // 동월 마지막 realtime_billing을 가져온다.
                            // 없으면 첫번째 LP로 새로 생성
                            baseBilling = billingEmDao.getNewestRealTimeBilling(parser.getMDevId(), 
                                    parser.getMDevType(), lpData[1].getDatetime().substring(0, 6));
                        }
                        baseBilling = saveBill(baseBilling, lpData, parser, tous);
                    }
                }	
            }
            BillingData[] billData = parser.getCurrentMonthly();
    		
            // if (billData[0] != null)
            // saveCurrentBilling(billData[0], parser.getMeter(), null, null, parser.getMDevType(), parser.getMDevId());
            if (billData[1] != null && baseBilling != null) {
                billData[1].setActiveEnergyImportRateTotal(baseBilling.getActiveEnergyImportRateTotal());
                billData[1].setActiveEnergyExportRateTotal(baseBilling.getActiveEnergyExportRateTotal());
                billData[1].setActiveEnergyRateTotal(baseBilling.getActiveEnergyImportRateTotal());
                // 지상 무효전력량
                billData[1].setReactiveEnergyLagImportRateTotal(baseBilling.getReactiveEnergyLagImportRateTotal());
                // 진상 무효전력량
                billData[1].setReactiveEnergyLeadImportRateTotal(baseBilling.getReactiveEnergyLeadImportRateTotal());
                // 피상 전력량
                billData[1].setReactiveEnergyRateTotal(baseBilling.getReactiveEnergyRateTotal());
                // 최대수요
                billData[1].setActivePowerMaxDemandRateTotal(baseBilling.getActivePowerMaxDemandRateTotal());
                billData[1].setActivePowerDemandMaxTimeRateTotal(baseBilling.getActivePowerDemandMaxTimeRateTotal());
    
                billData[1].setActivePwrDmdMaxImportRateTotal(baseBilling.getActivePwrDmdMaxImportRateTotal());
                billData[1].setActivePwrDmdMaxTimeImportRateTotal(baseBilling.getActivePwrDmdMaxTimeImportRateTotal());
    
                billData[1].setPf(baseBilling.getPf());
    
                // tariff1
                billData[1].setActiveEnergyImportRate1(baseBilling.getActiveEnergyImportRate1());
                billData[1].setActiveEnergyExportRate1(baseBilling.getActiveEnergyExportRate1());
                // 지상 무효전력량
                billData[1].setReactiveEnergyLagImportRate1(baseBilling.getReactiveEnergyLagImportRate1());
                // 진상 무효전력량
                billData[1].setReactiveEnergyLeadImportRate1(baseBilling.getReactiveEnergyLeadImportRate1());
                // 피상 전력량
                billData[1].setReactiveEnergyRate1(baseBilling.getReactiveEnergyRate1());
                // 최대수요
                billData[1].setActivePowerMaxDemandRate1(baseBilling.getActivePowerMaxDemandRate1());
                billData[1].setActivePowerDemandMaxTimeRate1(baseBilling.getActivePowerDemandMaxTimeRate1());
                billData[1].setActivePwrDmdMaxImportRate1(baseBilling.getActivePwrDmdMaxImportRate1());
                billData[1].setActivePwrDmdMaxTimeImportRate1(baseBilling.getActivePwrDmdMaxTimeImportRate1());
    
                // 역률
                billData[1].setCummkVah1Rate1(baseBilling.getCummkVah1Rate1());
    
                // tariff2
                billData[1].setActiveEnergyImportRate2(baseBilling.getActiveEnergyImportRate2());
                billData[1].setActiveEnergyExportRate2(baseBilling.getActiveEnergyExportRate2());
                // 지상 무효전력량
                billData[1].setReactiveEnergyLagImportRate2(baseBilling.getReactiveEnergyLagImportRate2());
                // 진상 무효전력량
                billData[1].setReactiveEnergyLeadImportRate2(baseBilling.getReactiveEnergyLeadImportRate2());
                // 피상 전력량
                billData[1].setReactiveEnergyRate2(baseBilling.getReactiveEnergyRate2());
                // 최대수요
                billData[1].setActivePowerMaxDemandRate2(baseBilling.getActivePowerMaxDemandRate2());
                billData[1].setActivePowerDemandMaxTimeRate2(baseBilling.getActivePowerDemandMaxTimeRate2());
                billData[1].setActivePwrDmdMaxImportRate2(baseBilling.getActivePwrDmdMaxImportRate2());
                billData[1].setActivePwrDmdMaxTimeImportRate2(baseBilling.getActivePwrDmdMaxTimeImportRate2());
    
                // 역률
                billData[1].setCummkVah1Rate2(baseBilling.getCummkVah1Rate2());
    
                // tariff3
                billData[1].setActiveEnergyImportRate3(baseBilling.getActiveEnergyImportRate3());
                billData[1].setActiveEnergyExportRate3(baseBilling.getActiveEnergyExportRate3());
                // 지상 무효전력량
                billData[1].setReactiveEnergyLagImportRate3(baseBilling.getReactiveEnergyLagImportRate3());
                // 진상 무효전력량
                billData[1].setReactiveEnergyLeadImportRate3(baseBilling.getReactiveEnergyLeadImportRate3());
                // 피상 전력량
                billData[1].setReactiveEnergyRate3(baseBilling.getReactiveEnergyRate3());
                // 최대수요
                billData[1].setActivePowerMaxDemandRate3(baseBilling.getActivePowerMaxDemandRate3());
                billData[1].setActivePowerDemandMaxTimeRate3(baseBilling.getActivePowerDemandMaxTimeRate3());
                billData[1].setActivePwrDmdMaxImportRate3(baseBilling.getActivePwrDmdMaxImportRate3());
                billData[1].setActivePwrDmdMaxTimeImportRate3(baseBilling.getActivePwrDmdMaxTimeImportRate3());
    
                // 역률
                billData[1].setCummkVah1Rate3(baseBilling.getCummkVah1Rate3());
                saveMonthlyBilling(billData[1], parser.getMeter(), null, null, parser.getMDevType(), parser.getMDevId());
    
                return billData[1];
            }
        }
        catch (Exception ignore) {
            log.warn(ignore, ignore);
        }
        return null;
    }

    private RealTimeBillingEM makeBaseBilling(LPData lpData, DLMSGtype parser, List<TariffEM> tous) {
        RealTimeBillingEM baseBilling = new RealTimeBillingEM();
        baseBilling.setMeter(parser.getMeter());
        baseBilling.setMDevId(parser.getMDevId());
        baseBilling.setMDevType(parser.getMDevType().name());
        baseBilling.setContract(parser.getMeter().getContract());
        baseBilling.setLocation(parser.getMeter().getLocation());
        baseBilling.setSupplier(parser.getMeter().getSupplier());
        baseBilling.setYyyymmdd(lpData.getDatetime().substring(0, 8));
        baseBilling.setHhmmss(lpData.getDatetime().substring(8, 12)+"00");
        baseBilling.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
		
        // 유효전력량
        baseBilling.setActiveEnergyImportRateTotal(lpData.getCh()[0]);
        if (lpData.getCh().length == 13)
            baseBilling.setActiveEnergyExportRateTotal(lpData.getCh()[11]);
        else
            baseBilling.setActiveEnergyExportRateTotal(0.0);
        baseBilling.setActiveEnergyRateTotal(baseBilling.getActiveEnergyImportRateTotal());
        // 지상 무효전력량
        baseBilling.setReactiveEnergyLagImportRateTotal(lpData.getCh()[8]);
        // 진상 무효전력량
        baseBilling.setReactiveEnergyLeadImportRateTotal(lpData.getCh()[9]);
        // 피상 전력량
        baseBilling.setReactiveEnergyRateTotal(lpData.getCh()[10]);
        // 최대수요
        baseBilling.setActivePowerMaxDemandRateTotal(lpData.getCh()[0]*4);
        baseBilling.setActivePowerDemandMaxTimeRateTotal(lpData.getDatetime());

        baseBilling.setActivePwrDmdMaxImportRateTotal(baseBilling.getActivePowerMaxDemandRateTotal());
        baseBilling.setActivePwrDmdMaxTimeImportRateTotal(baseBilling.getActivePowerDemandMaxTimeRateTotal());
        
        double pf_p = Math.sqrt(Math.pow(baseBilling.getActiveEnergyImportRateTotal(),2)
                + Math.pow(baseBilling.getReactiveEnergyLagImportRateTotal()
                        + baseBilling.getReactiveEnergyLeadImportRateTotal(),2));
        if (pf_p == 0)
            baseBilling.setPf(1.0);
        else
            baseBilling.setPf(baseBilling.getActiveEnergyImportRateTotal() / pf_p);
		
        log.info("PF[" + baseBilling.getPf() + "]");
        int itariff = checkTariff(tous, lpData.getDatetime());
        if (itariff == 1) {
            baseBilling.setActiveEnergyImportRate1(lpData.getCh()[0]);
            if (lpData.getCh().length == 13)
                baseBilling.setActiveEnergyExportRate1(lpData.getCh()[11]);
            else
                baseBilling.setActiveEnergyExportRate1(0.0);
            // 지상 무효전력량
            baseBilling.setReactiveEnergyLagImportRate1(lpData.getCh()[8]);
            // 진상 무효전력량
            baseBilling.setReactiveEnergyLeadImportRate1(lpData.getCh()[9]);
            // 피상 전력량
            baseBilling.setReactiveEnergyRate1(lpData.getCh()[10]);
            // 최대수요
            baseBilling.setActivePowerMaxDemandRate1(lpData.getCh()[0]*4);
            baseBilling.setActivePowerDemandMaxTimeRate1(lpData.getDatetime());

            baseBilling.setActivePwrDmdMaxImportRate1(baseBilling.getActivePowerMaxDemandRate1());
            baseBilling.setActivePwrDmdMaxTimeImportRate1(baseBilling.getActivePowerDemandMaxTimeRate1());

            pf_p = Math.sqrt(Math.pow(baseBilling.getActiveEnergyImportRate1(), 2)
                    + Math.pow(baseBilling.getReactiveEnergyLagImportRate1() + baseBilling.getReactiveEnergyLeadImportRate1(), 2));
			
            if (pf_p == 0)
                baseBilling.setCummkVah1Rate1(1.0);
            else
                baseBilling.setCummkVah1Rate1(baseBilling.getActiveEnergyImportRate1() / pf_p);
			
            baseBilling.setActiveEnergyImportRate2(0.0);
            baseBilling.setActiveEnergyExportRate2(0.0);
            // 지상 무효전력량
            baseBilling.setReactiveEnergyLagImportRate2(0.0);
            // 진상 무효전력량
            baseBilling.setReactiveEnergyLeadImportRate2(0.0);
            // 피상 전력량
            baseBilling.setReactiveEnergyRate2(0.0);
            baseBilling.setActivePowerMaxDemandRate2(0.0);
            baseBilling.setActivePowerDemandMaxTimeRate2("");
			
            baseBilling.setActivePwrDmdMaxImportRate2(0.0);
            baseBilling.setActivePwrDmdMaxTimeImportRate2("");
            baseBilling.setCummkVah1Rate2(1.0); 
			
            baseBilling.setActiveEnergyImportRate3(0.0);
            baseBilling.setActiveEnergyExportRate3(0.0);
            // 지상 무효전력량
            baseBilling.setReactiveEnergyLagImportRate3(0.0);
            // 진상 무효전력량
            baseBilling.setReactiveEnergyLeadImportRate3(0.0);
            // 피상 전력량
            baseBilling.setReactiveEnergyRate3(0.0);
            baseBilling.setActivePowerMaxDemandRate3(0.0);
            baseBilling.setActivePowerDemandMaxTimeRate3("");
		
            baseBilling.setActivePwrDmdMaxImportRate3(0.0);
            baseBilling.setActivePwrDmdMaxTimeImportRate3("");
            baseBilling.setCummkVah1Rate3(1.0); 
			
            log.info("Tariff1 PF[" + baseBilling.getCummkVah1Rate1() + "]");
        }
        else if (itariff == 2) {
            baseBilling.setActiveEnergyImportRate2(lpData.getCh()[0]);
            if (lpData.getCh().length == 13)
                baseBilling.setActiveEnergyExportRate2(lpData.getCh()[11]);
            else
                baseBilling.setActiveEnergyExportRate2(0.0);
            // 지상 무효전력량
            baseBilling.setReactiveEnergyLagImportRate2(lpData.getCh()[8]);
            // 진상 무효전력량
            baseBilling.setReactiveEnergyLeadImportRate2(lpData.getCh()[9]);
            // 피상 전력량
            baseBilling.setReactiveEnergyRate2(lpData.getCh()[10]);
            // 최대수요
            baseBilling.setActivePowerMaxDemandRate2(lpData.getCh()[0]*4);
            baseBilling.setActivePowerDemandMaxTimeRate2(lpData.getDatetime());

            baseBilling.setActivePwrDmdMaxImportRate2(baseBilling.getActivePowerMaxDemandRate2());
            baseBilling.setActivePwrDmdMaxTimeImportRate2(baseBilling.getActivePowerDemandMaxTimeRate2());

            // 역률
            pf_p = Math.sqrt(Math.pow(baseBilling.getActiveEnergyImportRate2(), 2)
                    + Math.pow(baseBilling.getReactiveEnergyLagImportRate2() + baseBilling.getReactiveEnergyLeadImportRate2(), 2));

            if (pf_p == 0)
                baseBilling.setCummkVah1Rate2(1.0);
            else
                baseBilling.setCummkVah1Rate2(baseBilling.getActiveEnergyImportRate2() / pf_p);

            baseBilling.setActiveEnergyImportRate1(0.0);
            baseBilling.setActiveEnergyExportRate1(0.0);
            // 지상 무효전력량
            baseBilling.setReactiveEnergyLagImportRate1(0.0);
            // 진상 무효전력량
            baseBilling.setReactiveEnergyLeadImportRate1(0.0);
            // 피상 전력량
            baseBilling.setReactiveEnergyRate1(0.0);
            baseBilling.setActivePowerMaxDemandRate1(0.0);
            baseBilling.setActivePowerDemandMaxTimeRate1("");

            baseBilling.setActivePwrDmdMaxImportRate1(0.0);
            baseBilling.setActivePwrDmdMaxTimeImportRate1("");
            baseBilling.setCummkVah1Rate1(1.0); 

            baseBilling.setActiveEnergyImportRate3(0.0);
            baseBilling.setActiveEnergyExportRate3(0.0);
            // 지상 무효전력량
            baseBilling.setReactiveEnergyLagImportRate3(0.0);
            // 진상 무효전력량
            baseBilling.setReactiveEnergyLeadImportRate3(0.0);
            // 피상 전력량
            baseBilling.setReactiveEnergyRate3(0.0);
            baseBilling.setActivePowerMaxDemandRate3(0.0);
            baseBilling.setActivePowerDemandMaxTimeRate3("");

            baseBilling.setActivePwrDmdMaxImportRate3(0.0);
            baseBilling.setActivePwrDmdMaxTimeImportRate3("");
            baseBilling.setCummkVah1Rate3(1.0); 

            log.info("Tariff2 PF[" + baseBilling.getCummkVah1Rate2() + "]");
        }
        else if (itariff == 3) {
            baseBilling.setActiveEnergyImportRate3(lpData.getCh()[0]);
            if (lpData.getCh().length == 13)
                baseBilling.setActiveEnergyExportRate3(lpData.getCh()[11]);
            else
                baseBilling.setActiveEnergyExportRate3(0.0);
            // 지상 무효전력량
            baseBilling.setReactiveEnergyLagImportRate3(lpData.getCh()[8]);
            // 진상 무효전력량
            baseBilling.setReactiveEnergyLeadImportRate3(lpData.getCh()[9]);
            // 피상 전력량
            baseBilling.setReactiveEnergyRate3(lpData.getCh()[10]);
            // 최대수요
            baseBilling.setActivePowerMaxDemandRate3(lpData.getCh()[0]*4);
            baseBilling.setActivePowerDemandMaxTimeRate3(lpData.getDatetime());

            baseBilling.setActivePwrDmdMaxImportRate3(baseBilling.getActivePowerMaxDemandRate3());
            baseBilling.setActivePwrDmdMaxTimeImportRate3(baseBilling.getActivePowerDemandMaxTimeRate3());

            // 역률
            pf_p = Math.sqrt(Math.pow(baseBilling.getActiveEnergyImportRate3(), 2)
                    + Math.pow(baseBilling.getReactiveEnergyLagImportRate3() + baseBilling.getReactiveEnergyLeadImportRate3(), 2));

            if (pf_p == 0)
                baseBilling.setCummkVah1Rate3(1.0);
            else
                baseBilling.setCummkVah1Rate3(baseBilling.getActiveEnergyImportRate3() / pf_p);

            baseBilling.setActiveEnergyImportRate1(0.0);
            baseBilling.setActiveEnergyExportRate1(0.0);
            // 지상 무효전력량
            baseBilling.setReactiveEnergyLagImportRate1(0.0);
            // 진상 무효전력량
            baseBilling.setReactiveEnergyLeadImportRate1(0.0);
            // 피상 전력량
            baseBilling.setReactiveEnergyRate1(0.0);
            baseBilling.setActivePowerMaxDemandRate1(0.0);
            baseBilling.setActivePowerDemandMaxTimeRate1("");

            baseBilling.setActivePwrDmdMaxImportRate1(0.0);
            baseBilling.setActivePwrDmdMaxTimeImportRate1("");
            baseBilling.setCummkVah1Rate1(1.0); 

            baseBilling.setActiveEnergyImportRate2(0.0);
            baseBilling.setActiveEnergyExportRate2(0.0);
            // 지상 무효전력량
            baseBilling.setReactiveEnergyLagImportRate2(0.0);
            // 진상 무효전력량
            baseBilling.setReactiveEnergyLeadImportRate2(0.0);
            // 피상 전력량
            baseBilling.setReactiveEnergyRate2(0.0);
            baseBilling.setActivePowerMaxDemandRate2(0.0);
            baseBilling.setActivePowerDemandMaxTimeRate2("");

            baseBilling.setActivePwrDmdMaxImportRate2(0.0);
            baseBilling.setActivePwrDmdMaxTimeImportRate2("");
            baseBilling.setCummkVah1Rate2(1.0); 

            log.info("Tariff3 PF[" + baseBilling.getCummkVah1Rate3() + "]");
        }
		
        return baseBilling;
    }
	
    private RealTimeBillingEM makeRealTimeBilling(RealTimeBillingEM baseBill, LPData lpData, 
            DLMSGtype parser, List<TariffEM> tous) {
		
        RealTimeBillingEM billing = new RealTimeBillingEM();
        billing.setMeter(parser.getMeter());
        billing.setMDevId(parser.getMDevId());
        billing.setMDevType(parser.getMDevType().name());
        billing.setContract(parser.getMeter().getContract());
        billing.setLocation(parser.getMeter().getLocation());
        billing.setSupplier(parser.getMeter().getSupplier());
        billing.setYyyymmdd(lpData.getDatetime().substring(0, 8));
        billing.setHhmmss(lpData.getDatetime().substring(8, 12)+"00");
        billing.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
		
        // 유효전력량
        billing.setActiveEnergyImportRateTotal(baseBill.getActiveEnergyImportRateTotal() + lpData.getCh()[0]);
        if (baseBill.getActiveEnergyExportRateTotal() != null) {
            if (lpData.getCh().length == 13)
                billing.setActiveEnergyExportRateTotal(baseBill.getActiveEnergyExportRateTotal() + lpData.getCh()[11]);
            else
                billing.setActiveEnergyExportRateTotal(baseBill.getActiveEnergyExportRateTotal());
        }
        else {
            if (lpData.getCh().length == 13)
                billing.setActiveEnergyExportRateTotal(lpData.getCh()[11]);
            else
                billing.setActiveEnergyExportRateTotal(0.0);
        }
        billing.setActiveEnergyRateTotal(billing.getActiveEnergyImportRateTotal());
        // 지상 무효전력량
        billing.setReactiveEnergyLagImportRateTotal(baseBill.getReactiveEnergyLagImportRateTotal()+lpData.getCh()[8]);
        // 진상 무효전력량
        billing.setReactiveEnergyLeadImportRateTotal(baseBill.getReactiveEnergyLeadImportRateTotal()+lpData.getCh()[9]);
        // 피상 전력량
        billing.setReactiveEnergyRateTotal(baseBill.getReactiveEnergyRateTotal()+lpData.getCh()[10]);
        double maxdemand = lpData.getCh()[0]*4;
        if (baseBill.getActivePowerMaxDemandRateTotal() == null || maxdemand > baseBill.getActivePowerMaxDemandRateTotal()) {
            billing.setActivePowerMaxDemandRateTotal(maxdemand);
            billing.setActivePowerDemandMaxTimeRateTotal(lpData.getDatetime());
			
            billing.setActivePwrDmdMaxImportRateTotal(billing.getActivePowerMaxDemandRateTotal());
            billing.setActivePwrDmdMaxTimeImportRateTotal(billing.getActivePowerDemandMaxTimeRateTotal());
        }
        else {
            billing.setActivePowerMaxDemandRateTotal(baseBill.getActivePowerMaxDemandRateTotal());
            billing.setActivePowerDemandMaxTimeRateTotal(baseBill.getActivePowerDemandMaxTimeRateTotal());

            billing.setActivePwrDmdMaxImportRateTotal(baseBill.getActivePwrDmdMaxImportRateTotal());
            billing.setActivePwrDmdMaxTimeImportRateTotal(baseBill.getActivePwrDmdMaxTimeImportRateTotal());
        }
		
        double pf_p = Math.sqrt(Math.pow(billing.getActiveEnergyImportRateTotal(), 2)
                + Math.pow(billing.getReactiveEnergyLagImportRateTotal() + billing.getReactiveEnergyLeadImportRateTotal(), 2));
        if (pf_p == 0)
            billing.setPf(1.0);
        else
            billing.setPf(billing.getActiveEnergyImportRateTotal() / pf_p); 
        log.debug("PF[" + billing.getPf() + "]");
		
        int itariff = checkTariff(tous, lpData.getDatetime());
        if (itariff == 1) {
            billing.setActiveEnergyImportRate1(baseBill.getActiveEnergyImportRate1() + lpData.getCh()[0]);
            if (baseBill.getActiveEnergyExportRate1() != null) {
                if (lpData.getCh().length == 13)
                    billing.setActiveEnergyExportRate1(baseBill.getActiveEnergyExportRate1() + lpData.getCh()[11]);
                else
                    billing.setActiveEnergyExportRate1(baseBill.getActiveEnergyExportRate1());
            }
            else {
                if (lpData.getCh().length == 13)
                    billing.setActiveEnergyExportRate1(lpData.getCh()[11]);
                else
                    billing.setActiveEnergyExportRate1(0.0);
            }
            // 지상 무효전력량
            billing.setReactiveEnergyLagImportRate1(baseBill.getReactiveEnergyLagImportRate1() + lpData.getCh()[8]);
            // 진상 무효전력량
            billing.setReactiveEnergyLeadImportRate1(baseBill.getReactiveEnergyLeadImportRate1() + lpData.getCh()[9]);
            // 피상 전력량
            billing.setReactiveEnergyRate1(baseBill.getReactiveEnergyRate1() + lpData.getCh()[10]);
            maxdemand = lpData.getCh()[0]*4;
            if (baseBill.getActivePowerMaxDemandRate1() == null || maxdemand > baseBill.getActivePowerMaxDemandRate1()) {
                billing.setActivePowerMaxDemandRate1(maxdemand);
                billing.setActivePowerDemandMaxTimeRate1(lpData.getDatetime());
    			
                billing.setActivePwrDmdMaxImportRate1(billing.getActivePowerMaxDemandRate1());
                billing.setActivePwrDmdMaxTimeImportRate1(billing.getActivePowerDemandMaxTimeRate1());
            }
            else {
                billing.setActivePowerMaxDemandRate1(baseBill.getActivePowerMaxDemandRate1());
                billing.setActivePowerDemandMaxTimeRate1(baseBill.getActivePowerDemandMaxTimeRate1());

                billing.setActivePwrDmdMaxImportRate1(baseBill.getActivePwrDmdMaxImportRate1());
                billing.setActivePwrDmdMaxTimeImportRate1(baseBill.getActivePwrDmdMaxTimeImportRate1());
            }
			
            pf_p = Math.sqrt(Math.pow(billing.getActiveEnergyImportRate1(), 2)
                    + Math.pow(billing.getReactiveEnergyLagImportRate1() + billing.getReactiveEnergyLeadImportRate1(), 2));
    		
            // 역률
            if (pf_p == 0)
                billing.setCummkVah1Rate1(1.0);
            else
                billing.setCummkVah1Rate1(billing.getActiveEnergyImportRate1() / pf_p); 
    		
            billing.setActiveEnergyImportRate2(baseBill.getActiveEnergyImportRate2());
            billing.setActiveEnergyExportRate2(baseBill.getActiveEnergyExportRate2());
            // 지상 무효전력량
            billing.setReactiveEnergyLagImportRate2(baseBill.getReactiveEnergyLagImportRate2());
            // 진상 무효전력량
            billing.setReactiveEnergyLeadImportRate2(baseBill.getReactiveEnergyLeadImportRate2());
            // 피상 전력량
            billing.setReactiveEnergyRate2(baseBill.getReactiveEnergyRate2());
            billing.setActivePowerMaxDemandRate2(baseBill.getActivePowerMaxDemandRate2());
            billing.setActivePowerDemandMaxTimeRate2(baseBill.getActivePowerDemandMaxTimeRate2());

            billing.setActivePwrDmdMaxImportRate2(baseBill.getActivePwrDmdMaxImportRate2());
            billing.setActivePwrDmdMaxTimeImportRate2(baseBill.getActivePwrDmdMaxTimeImportRate2());
            billing.setCummkVah1Rate2(billing.getCummkVah1Rate2()); 

            billing.setActiveEnergyImportRate3(baseBill.getActiveEnergyImportRate3());
            billing.setActiveEnergyExportRate3(baseBill.getActiveEnergyExportRate3());
            // 지상 무효전력량
            billing.setReactiveEnergyLagImportRate3(baseBill.getReactiveEnergyLagImportRate3());
            // 진상 무효전력량
            billing.setReactiveEnergyLeadImportRate3(baseBill.getReactiveEnergyLeadImportRate3());
            // 피상 전력량
            billing.setReactiveEnergyRate3(baseBill.getReactiveEnergyRate3());
            billing.setActivePowerMaxDemandRate3(baseBill.getActivePowerMaxDemandRate3());
            billing.setActivePowerDemandMaxTimeRate3(baseBill.getActivePowerDemandMaxTimeRate3());

            billing.setActivePwrDmdMaxImportRate3(baseBill.getActivePwrDmdMaxImportRate3());
            billing.setActivePwrDmdMaxTimeImportRate3(baseBill.getActivePwrDmdMaxTimeImportRate3());
            billing.setCummkVah1Rate3(billing.getCummkVah1Rate3()); 

            log.debug("Tariff1 PF[" + billing.getCummkVah1Rate1() + "]");
        }
        else if (itariff == 2) {
            billing.setActiveEnergyImportRate2(baseBill.getActiveEnergyImportRate2() + lpData.getCh()[0]);
            if (baseBill.getActiveEnergyExportRate2() != null) {
                if (lpData.getCh().length == 13)
                    billing.setActiveEnergyExportRate2(baseBill.getActiveEnergyExportRate2() + lpData.getCh()[11]);
                else
                    billing.setActiveEnergyExportRate2(baseBill.getActiveEnergyExportRate2());
            }
            else {
                if (lpData.getCh().length == 13)
                    billing.setActiveEnergyExportRate2(lpData.getCh()[11]);
                else
                    billing.setActiveEnergyExportRate2(0.0);
            }
            
            // 지상 무효전력량
            billing.setReactiveEnergyLagImportRate2(baseBill.getReactiveEnergyLagImportRate2() + lpData.getCh()[8]);
            // 진상 무효전력량
            billing.setReactiveEnergyLeadImportRate2(baseBill.getReactiveEnergyLeadImportRate2() + lpData.getCh()[9]);
            // 피상 전력량
            billing.setReactiveEnergyRate2(baseBill.getReactiveEnergyRate2() + lpData.getCh()[10]);
            maxdemand = lpData.getCh()[0]*4;
            if (baseBill.getActivePowerMaxDemandRate2() == null || maxdemand > baseBill.getActivePowerMaxDemandRate2()) {
                billing.setActivePowerMaxDemandRate2(maxdemand);
                billing.setActivePowerDemandMaxTimeRate2(lpData.getDatetime());
        		
                billing.setActivePwrDmdMaxImportRate2(billing.getActivePowerMaxDemandRate2());
                billing.setActivePwrDmdMaxTimeImportRate2(billing.getActivePowerDemandMaxTimeRate2());
            }
            else {
                billing.setActivePowerMaxDemandRate2(baseBill.getActivePowerMaxDemandRate2());
                billing.setActivePowerDemandMaxTimeRate2(baseBill.getActivePowerDemandMaxTimeRate2());

                billing.setActivePwrDmdMaxImportRate2(baseBill.getActivePwrDmdMaxImportRate2());
                billing.setActivePwrDmdMaxTimeImportRate2(baseBill.getActivePwrDmdMaxTimeImportRate2());
            }
        	
            pf_p = Math.sqrt(Math.pow(billing.getActiveEnergyImportRate2(), 2)
                    + Math.pow(billing.getReactiveEnergyLagImportRate2() + billing.getReactiveEnergyLeadImportRate2(), 2));
        	
            // 역률
            if (pf_p == 0)
                billing.setCummkVah1Rate2(1.0);
            else
                billing.setCummkVah1Rate2(billing.getActiveEnergyImportRate2() / pf_p); 
        	
            billing.setActiveEnergyImportRate1(baseBill.getActiveEnergyImportRate1());
            billing.setActiveEnergyExportRate1(baseBill.getActiveEnergyExportRate1());
            // 지상 무효전력량
            billing.setReactiveEnergyLagImportRate1(baseBill.getReactiveEnergyLagImportRate1());
            // 진상 무효전력량
            billing.setReactiveEnergyLeadImportRate1(baseBill.getReactiveEnergyLeadImportRate1());
            // 피상 전력량
            billing.setReactiveEnergyRate1(baseBill.getReactiveEnergyRate1());
            billing.setActivePowerMaxDemandRate1(baseBill.getActivePowerMaxDemandRate1());
            billing.setActivePowerDemandMaxTimeRate1(baseBill.getActivePowerDemandMaxTimeRate1());

            billing.setActivePwrDmdMaxImportRate1(baseBill.getActivePwrDmdMaxImportRate1());
            billing.setActivePwrDmdMaxTimeImportRate1(baseBill.getActivePwrDmdMaxTimeImportRate1());
            billing.setCummkVah1Rate1(billing.getCummkVah1Rate1()); 

            billing.setActiveEnergyImportRate3(baseBill.getActiveEnergyImportRate3());
            billing.setActiveEnergyExportRate3(baseBill.getActiveEnergyExportRate3());
            // 지상 무효전력량
            billing.setReactiveEnergyLagImportRate3(baseBill.getReactiveEnergyLagImportRate3());
            // 진상 무효전력량
            billing.setReactiveEnergyLeadImportRate3(baseBill.getReactiveEnergyLeadImportRate3());
            // 피상 전력량
            billing.setReactiveEnergyRate3(baseBill.getReactiveEnergyRate3());
            billing.setActivePowerMaxDemandRate3(baseBill.getActivePowerMaxDemandRate3());
            billing.setActivePowerDemandMaxTimeRate3(baseBill.getActivePowerDemandMaxTimeRate3());

            billing.setActivePwrDmdMaxImportRate3(baseBill.getActivePwrDmdMaxImportRate3());
            billing.setActivePwrDmdMaxTimeImportRate3(baseBill.getActivePwrDmdMaxTimeImportRate3());
            billing.setCummkVah1Rate3(billing.getCummkVah1Rate3()); 

            log.debug("Tariff2 PF[" + billing.getCummkVah1Rate2() + "]");
        }
        else if (itariff == 3) {
            billing.setActiveEnergyImportRate3(baseBill.getActiveEnergyImportRate3() + lpData.getCh()[0]);
            if (baseBill.getActiveEnergyExportRate3() != null) {
                if (lpData.getCh().length == 13)
                    billing.setActiveEnergyExportRate3(baseBill.getActiveEnergyExportRate3() + lpData.getCh()[11]);
                else
                    billing.setActiveEnergyExportRate3(baseBill.getActiveEnergyExportRate3());
            }
            else {
                if (lpData.getCh().length == 13)
                    billing.setActiveEnergyExportRate3(lpData.getCh()[11]);
                else
                    billing.setActiveEnergyExportRate3(0.0);
            }
            // 지상 무효전력량
            billing.setReactiveEnergyLagImportRate3(baseBill.getReactiveEnergyLagImportRate3() + lpData.getCh()[8]);
            // 진상 무효전력량
            billing.setReactiveEnergyLeadImportRate3(baseBill.getReactiveEnergyLeadImportRate3() + lpData.getCh()[9]);
            // 피상 전력량
            billing.setReactiveEnergyRate3(baseBill.getReactiveEnergyRate3() + lpData.getCh()[10]);
            maxdemand = lpData.getCh()[0]*4;
            if (baseBill.getActivePowerMaxDemandRate3() == null || maxdemand > baseBill.getActivePowerMaxDemandRate3()) {
                billing.setActivePowerMaxDemandRate3(maxdemand);
                billing.setActivePowerDemandMaxTimeRate3(lpData.getDatetime());

                billing.setActivePwrDmdMaxImportRate3(billing.getActivePowerMaxDemandRate3());
                billing.setActivePwrDmdMaxTimeImportRate3(billing.getActivePowerDemandMaxTimeRate3());
            }
            else {
                billing.setActivePowerMaxDemandRate3(baseBill.getActivePowerMaxDemandRate3());
                billing.setActivePowerDemandMaxTimeRate3(baseBill.getActivePowerDemandMaxTimeRate3());

                billing.setActivePwrDmdMaxImportRate3(baseBill.getActivePwrDmdMaxImportRate3());
                billing.setActivePwrDmdMaxTimeImportRate3(baseBill.getActivePwrDmdMaxTimeImportRate3());
            }
        	
            pf_p = Math.sqrt(Math.pow(billing.getActiveEnergyImportRate3(), 2)
                    + Math.pow(billing.getReactiveEnergyLagImportRate3() + billing.getReactiveEnergyLeadImportRate3(), 2));
        	
            // 역률
            if (pf_p == 0)
                billing.setCummkVah1Rate3(1.0);
            else
                billing.setCummkVah1Rate3(billing.getActiveEnergyImportRate3() / pf_p); 
        	
            billing.setActiveEnergyImportRate1(baseBill.getActiveEnergyImportRate1());
            billing.setActiveEnergyExportRate1(baseBill.getActiveEnergyExportRate1());
            // 지상 무효전력량
            billing.setReactiveEnergyLagImportRate1(baseBill.getReactiveEnergyLagImportRate1());
            // 진상 무효전력량
            billing.setReactiveEnergyLeadImportRate1(baseBill.getReactiveEnergyLeadImportRate1());
            // 피상 전력량
            billing.setReactiveEnergyRate1(baseBill.getReactiveEnergyRate1());
            billing.setActivePowerMaxDemandRate1(baseBill.getActivePowerMaxDemandRate1());
            billing.setActivePowerDemandMaxTimeRate1(baseBill.getActivePowerDemandMaxTimeRate1());

            billing.setActivePwrDmdMaxImportRate1(baseBill.getActivePwrDmdMaxImportRate1());
            billing.setActivePwrDmdMaxTimeImportRate1(baseBill.getActivePwrDmdMaxTimeImportRate1());
            billing.setCummkVah1Rate1(billing.getCummkVah1Rate1()); 

            billing.setActiveEnergyImportRate2(baseBill.getActiveEnergyImportRate2());
            billing.setActiveEnergyExportRate2(baseBill.getActiveEnergyExportRate2());
            // 지상 무효전력량
            billing.setReactiveEnergyLagImportRate2(baseBill.getReactiveEnergyLagImportRate2());
            // 진상 무효전력량
            billing.setReactiveEnergyLeadImportRate2(baseBill.getReactiveEnergyLeadImportRate2());
            // 피상 전력량
            billing.setReactiveEnergyRate2(baseBill.getReactiveEnergyRate2());
            billing.setActivePowerMaxDemandRate2(baseBill.getActivePowerMaxDemandRate2());
            billing.setActivePowerDemandMaxTimeRate2(baseBill.getActivePowerDemandMaxTimeRate2());

            billing.setActivePwrDmdMaxImportRate2(baseBill.getActivePwrDmdMaxImportRate2());
            billing.setActivePwrDmdMaxTimeImportRate2(baseBill.getActivePwrDmdMaxTimeImportRate2());
            billing.setCummkVah1Rate2(billing.getCummkVah1Rate2()); 

            log.debug("Tariff3 PF[" + billing.getCummkVah1Rate3() + "]");
        }
        return billing;
    }
	
    private int checkTariff(List<TariffEM> tous, String lpdate) {
        String startDate = "";
        String endDate = "";
        String lp_mmdd = lpdate.substring(4, 8);
        int lp_hour = Integer.parseInt(lpdate.substring(8, 10));
        for (TariffEM t : tous) {
            startDate = t.getSeason().getSmonth() + t.getSeason().getSday();
            endDate = t.getSeason().getEmonth() + t.getSeason().getEday();
            if (startDate.compareTo(endDate) < 0) {
                if (startDate.compareTo(lp_mmdd) <= 0 && endDate.compareTo(lp_mmdd) > 0) {
                    if (Integer.parseInt(t.getStartHour()) < Integer.parseInt(t.getEndHour())) {
                        if (Integer.parseInt(t.getStartHour()) <= lp_hour && 
                                Integer.parseInt(t.getEndHour()) > lp_hour) {
                            if (t.getPeakType() == PeakType.CRITICAL_PEAK)
                                return 1;
                            else if (t.getPeakType() == PeakType.PEAK)
                                return 2;
                            else
                                return 3;
                        }
                    }
                    else {
                        if (Integer.parseInt(t.getStartHour()) <= lp_hour || Integer.parseInt(t.getEndHour()) > lp_hour){
                            if (t.getPeakType() == PeakType.CRITICAL_PEAK)
                                return 1;
                            else if (t.getPeakType() == PeakType.PEAK)
                                return 2;
                            else
                                return 3;
                        }
                    }
                }
            }
            else {
                if (startDate.compareTo(lp_mmdd) <= 0 || endDate.compareTo(lp_mmdd) > 0) {
                    if (Integer.parseInt(t.getStartHour()) < Integer.parseInt(t.getEndHour())) {
                        if (Integer.parseInt(t.getStartHour()) <= lp_hour && 
                                Integer.parseInt(t.getEndHour()) > lp_hour) {
                            if (t.getPeakType() == PeakType.CRITICAL_PEAK)
                                return 1;
                            else if (t.getPeakType() == PeakType.PEAK)
                                return 2;
                            else
                                return 3;
                        }
                    }
                    else {
                        if (Integer.parseInt(t.getStartHour()) <= lp_hour || Integer.parseInt(t.getEndHour()) > lp_hour){
                            if (t.getPeakType() == PeakType.CRITICAL_PEAK)
                                return 1;
                            else if (t.getPeakType() == PeakType.PEAK)
                                return 2;
                            else
                                return 3;
                        }
                    }
                }
            }
        }
        return 1;
    }
	
    private Double retValue(String mm,Double value_00,Double value_15,Double value_30,Double value_45){
	
        Double retVal_00 = value_00 == null ? 0d:value_00;
        Double retVal_15 = value_15 == null ? 0d:value_15;
        Double retVal_30 = value_30 == null ? 0d:value_30;
        Double retVal_45 = value_45 == null ? 0d:value_45;
        if("15".equals(mm)){
            return retVal_00;
        }
		
        if("30".equals(mm)){
            return retVal_00+retVal_15;
        }
		
        if("45".equals(mm)){
            return retVal_00+retVal_15+retVal_30;
        }
		
        if("00".equals(mm)){
            return retVal_00+retVal_15+retVal_30+retVal_45;
        }
		
        return retVal_00+retVal_15;

    }
	
    private boolean saveLP(IMeasurementData md, LPData[] validlplist,
            DLMSGtype parser, double base,double addBasePulse) throws Exception {
        LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
        String yyyymmdd = validlplist[0].getDatetime().substring(0, 8);
		String yyyymmddhh = validlplist[0].getDatetime().substring(0, 10);
		String hhmm = validlplist[0].getDatetime().substring(8, 12);
		String mm = validlplist[0].getDatetime().substring(10, 12);

		condition.add(new Condition("id.mdevType", new Object[] { parser.getMDevType() }, null, Restriction.EQ));
		condition.add(new Condition("id.mdevId", new Object[] { parser.getMDevId() }, null, Restriction.EQ));
		// log.debug("parser.getMDevType():"+parser.getMDevType()+":parser.getMDevId():"+parser.getMDevId()+":dst:"+DateTimeUtil
		// .inDST(null, parser.getMeterDate())+":yyyymmddhh:"+yyyymmddhh );
		condition.add(new Condition("id.dst", new Object[] { DateTimeUtil.inDST(null, parser.getMeterTime()) }, null, Restriction.EQ));
		condition.add(new Condition("id.channel",new Object[] { ElectricityChannel.Usage.getChannel() },null, Restriction.EQ));
		condition.add(new Condition("id.yyyymmddhh", new Object[] { yyyymmddhh }, null, Restriction.EQ));

		List<LpEM> lpEM = lpEMDao.findByConditions(condition);

		// String firstDate = lplist[0].getDatetime().substring(0,8);
		double basePulse = -1;

		try {
		    if (lpEM != null && !lpEM.isEmpty()) {
		        // 동시간대의 값을 가져올 경우 00분을 제외한 lp값을 더하여 base값을 구해야 한다.
		        if (!mm.equals("00"))
		            basePulse = lpEM.get(0).getValue()+retValue(mm,lpEM.get(0).getValue_00(),lpEM.get(0).getValue_15(),lpEM.get(0).getValue_30(),lpEM.get(0).getValue_45());
		        else
		            basePulse = lpEM.get(0).getValue();
		    } 
		    else{
		        LinkedHashSet<Condition> condition2 = new LinkedHashSet<Condition>();
		        condition2.add(new Condition("id.mdevType",
		                new Object[] { parser.getMDevType() }, null,
		                Restriction.EQ));
		        condition2.add(new Condition("id.mdevId",
		                new Object[] { parser.getMDevId() }, null,
		                Restriction.EQ));
		        // log.debug("parser.getMDevType():"+parser.getMDevType()+":parser.getMDevId():"+parser.getMDevId()+":dst:"+DateTimeUtil
		        // .inDST(null,
		        // parser.getMeterDate())+":yyyymmddhh:"+yyyymmddhh );
		        condition2.add(new Condition("id.dst",
		                new Object[] { DateTimeUtil.inDST(null, parser
		                        .getMeterTime()) }, null, Restriction.EQ));

		        condition2.add(new Condition("id.channel",
		                new Object[] { ElectricityChannel.Usage
		                .getChannel() }, null, Restriction.EQ));
					
		        Calendar cal = Calendar.getInstance();
		        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHH");
		        cal.setTime(dateFormatter.parse(yyyymmddhh));

		        cal.add(cal.HOUR, -1);

		        condition2.add(new Condition("id.yyyymmddhh",
		                new Object[] { dateFormatter.format(cal
		                        .getTime()) }, null, Restriction.EQ));
		        List<LpEM> subLpEM = lpEMDao.findByConditions(condition2);
		        if (subLpEM != null && !subLpEM.isEmpty()) {
		            // 전 시간 값을 가져온 것이기 때문에 전부 다 합산해야 한다.
		            basePulse = subLpEM.get(0).getValue()+retValue("00",subLpEM.get(0).getValue_00(),subLpEM.get(0).getValue_15(),subLpEM.get(0).getValue_30(),subLpEM.get(0).getValue_45());
		        }
		    }

		} catch (Exception e) {
		    // TODO Auto-generated catch block
		    log.error(e, e);
		}
		
		if (lpEM != null && !lpEM.isEmpty()) {	
		    //basePulse = lpEM.get(0).getValue();
		}
		else {
		    basePulse = base;
		}

		log.info("#########save base value:" + basePulse+":mdevId:"+parser.getMDevId()+":yyyymmddhh:"+yyyymmddhh);
		double[][] lpValues = new double[validlplist[0].getCh().length][validlplist.length];
		int[] flaglist = new int[validlplist.length];
		double[] pflist = new double[validlplist.length];

		for (int ch = 0; ch < lpValues.length; ch++) {
		    for (int lpcnt = 0; lpcnt < lpValues[ch].length; lpcnt++) {
		        // lpValues[ch][lpcnt] = validlplist[lpcnt].getLpValue();
		        // Kw/h 단위로 저장
		        lpValues[ch][lpcnt] = validlplist[lpcnt].getCh()[ch];
		    }
		}

		for (int i = 0; i < flaglist.length; i++) {
		    flaglist[i] = validlplist[i].getFlag();
		    pflist[i] = validlplist[i].getPF();
		}

		parser.getMeter().setLpInterval(parser.getInterval());
		// TODO Flag, PF 처리해야 함.
		saveLPData(MeteringType.Normal, yyyymmdd, hhmm, lpValues, flaglist,
		        basePulse, parser.getMeter(), parser.getDeviceType(),
		        parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
		return true;
    }
    
    @Override
    public String relayValveOn(String mcuId, String meterId) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        
        try {
            Meter meter = meterDao.get(meterId);
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
            resultMap = commandGw.cmdOnDemandMeter( mcuId, meterId, OnDemandOption.WRITE_OPTION_RELAYON.getCode());
            
            
            if (resultMap != null && resultMap.get("LoadControlStatus") != null) {
                LOAD_CONTROL_STATUS ctrlStatus =  (LOAD_CONTROL_STATUS)resultMap.get("LoadControlStatus");
                
                if (ctrlStatus == LOAD_CONTROL_STATUS.CLOSE) {
                    updateMeterStatusNormal(meter);
                }
            }
        }
        catch (Exception e) {
            resultMap.put("failReason", e.getMessage());
        }
        
        return MapToJSON(resultMap);
    }

    @Override
    public String relayValveOff(String mcuId, String meterId) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            Meter meter = meterDao.get(meterId);
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
            resultMap = commandGw.cmdOnDemandMeter( mcuId, meterId, OnDemandOption.WRITE_OPTION_RELAYOFF.getCode());
            
            
            if (resultMap != null && resultMap.get("LoadControlStatus") != null) {
                LOAD_CONTROL_STATUS ctrlStatus =  (LOAD_CONTROL_STATUS)resultMap.get("LoadControlStatus");
                
                if (ctrlStatus == LOAD_CONTROL_STATUS.OPEN) {
                    updateMeterStatusCutOff(meter);
                }
            }
        }
        catch (Exception e) {
            resultMap.put("failReason", e.getMessage());
        }
        
        return MapToJSON(resultMap);
    }

    @Override
    public String relayValveStatus(String mcuId, String meterId) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            Meter meter = meterDao.get(meterId);
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
            int nOption = OnDemandOption.READ_OPTION_RELAY.getCode(); //read table
            resultMap = commandGw.cmdOnDemandMeter( mcuId, meterId, nOption);
            
            if (resultMap != null) {
                if ((LOAD_CONTROL_STATUS)resultMap.get("LoadControlStatus") == LOAD_CONTROL_STATUS.OPEN) {
                    updateMeterStatusCutOff(meter);
                }
                else if ((LOAD_CONTROL_STATUS)resultMap.get("LoadControlStatus") == LOAD_CONTROL_STATUS.CLOSE) {
                    updateMeterStatusNormal(meter);
                }
            }
        }
        catch (Exception e) {
            log.error(e, e);
            resultMap.put("failReason", e.getMessage());
        }
        
        return MapToJSON(resultMap);
    }

    @Override
    public String syncTime(String mcuId, String meterId) {
        Map<String, String> resultMap = new HashMap<String, String>();
        int result = 0;
        try {
            Meter meter = meterDao.get(meterId);
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
            resultMap = commandGw.cmdMeterTimeSyncByGtype(mcuId,meter.getMdsId());
            if(resultMap != null) {
                String before = (String) resultMap.get("beforeTime");
                String after = (String) resultMap.get("afterTime");
                String diff = String.valueOf((TimeUtil.getLongTime(after) - TimeUtil.getLongTime(before))/1000);
                resultMap.put("diff", diff);
                
                saveMeterTimeSyncLog(meter, before, after, result);
            }
        }
        catch (Exception e) {
            result = 1;
            resultMap.put("failReason", e.getMessage());
        }
        return MapToJSON(resultMap);
    }
}
