
package com.aimir.fep.adapter;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.MeteringFlag;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.SupplierType;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.PeakDemandLogDao;
import com.aimir.dao.system.PeakDemandScenarioDao;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.rdata.BPList;
import com.aimir.fep.meter.parser.rdata.LPList;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.Meter;
import com.aimir.model.system.ContractCapacity;
import com.aimir.model.system.Location;
import com.aimir.model.system.PeakDemandLog;
import com.aimir.model.system.PeakDemandScenario;
import com.aimir.model.system.PeakDemandSetting;
import com.aimir.model.system.SupplyTypeLocation;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;

/**
 * BEMS 제주 컨벤션 센터 검침 데이타 처리를 위한 프로세스
 *
 * 2012.07.13
 */
@Transactional
public class BemsProcessor extends AbstractMDSaver implements MessageListener {
    private static Log log = LogFactory.getLog(BemsProcessor.class);

    private String fepName;
    
    @Autowired
    private MeterDao meterDao;
    
    @Autowired
    private PeakDemandLogDao pdlDao;
    
    @Autowired
    private PeakDemandScenarioDao pdsDao;
    
    private String jmxUrl;
    
    public void init()
    {
        fepName = System.getProperty("name");
        System.setProperty("fepName", fepName);
        this.jmxUrl = FMPProperty.getProperty("fep.jmxrmi");
        
        log.info("\t" + fepName + " FEPd is Ready for Service...\n");
    }

    private void processing(Object data) throws Exception {
        if (!(data instanceof MeteringData))
            return;
        
        MeteringData md = (MeteringData)data;
        Meter meter = meterDao.get(md.getMeterId());
        
        if (meter == null)
            throw new Exception("Invalid Meter[" + md.getMeterId() + "]");
        
        // BT Time과 첫번째 LP Time이 같지 않으면 예외처리
        String baseTime = md.getBpLists().get(0).getBaseTime();
        String lpTime = md.getLpLists().get(0).getLpTime();
        if (!baseTime.equals(lpTime))
            throw new Exception("BaseTime[" + baseTime + "] is not equals to LPTime[" + lpTime + "]");
        
        Long[] _baseValue = md.getBpLists().get(0).getBaseValues().toArray(new Long[0]);
        double[] baseValue = new double[_baseValue.length];
        // 단위 적용해야 함.
        if (meter.getPulseConstant() == null) {
            throw new Exception("Meter[" + md.getMeterId() + "] does not have pulse constant");
        }
        
        for (int i = 0; i < _baseValue.length; i++) {
            baseValue[i] = (double)_baseValue[i] * meter.getPulseConstant();

			// (-) 값이 들어오면 0으로 저장.
			if ( baseValue[i] < 0 ) baseValue[i] = 0.0;
        }
        
        // BP Count 만큼 마지막 검침값을 저장한다.
        // TODO lastValue를 계산하기 위해서 meterconfiguration의 sigExp를 가져와서 계산해야 되나
        // Wh, mA 단위로 오기 때문에 1000, 100으로 나눈다.
        Map<String, Double> baseMap = new HashMap<String, Double>();
        for (BPList bplist : md.getBpLists().toArray(new BPList[0])) {

			// 2012.09.16 요구사항 처리
			// (-) 값이 들어오면 0으로 저장한다.
			double mv =  bplist.getLastValues().get(0).doubleValue()*meter.getPulseConstant();
			if ( mv < 0 ){
 				mv = 0.0;	
			}

            saveMeteringData(MeteringType.Normal, bplist.getLastTime().substring(0, 8),
                    bplist.getLastTime().substring(8)+"00", mv,
                    meter, DeviceType.Meter, md.getMeterId(),
                    DeviceType.Meter, md.getMeterId(), null);
        }
        
        // flaglist를 생성한다.
        int[] flaglist = new int[md.getLpLists().size()];
        // 채널 배열을 생성한다. 각 채널별 단위를 확인하여 계산한다.
        double[][] chlp = new double[md.getChCount()][flaglist.length];
        LPList _lplist = null;
        for (int lpcnt=0; lpcnt < md.getLpLists().size(); lpcnt++) {
            _lplist = md.getLpLists().get(lpcnt);
            flaglist[lpcnt] = MeteringFlag.Correct.getFlag();
            if (_lplist.getLpValues() != null) {
                for (int chcnt = 0; chcnt < _lplist.getLpValues().size(); chcnt++) {
                    if (chcnt == 0 || chcnt == 1)
                        chlp[chcnt][lpcnt] = _lplist.getLpValues().get(chcnt) * meter.getPulseConstant();
                    else chlp[chcnt][lpcnt] = _lplist.getLpValues().get(chcnt);

					// (-) 값이 들어오면 0으로 저장한다.
					if ( chlp[chcnt][lpcnt] < 0 ) chlp[chcnt][lpcnt] = 0.0;
                }
            }
        }
        
        saveLPData(MeteringType.Normal, baseTime.substring(0,8), baseTime.substring(8, 12),
                chlp, flaglist, baseValue, meter, DeviceType.Meter, md.getMeterId(),
                DeviceType.Meter, md.getMeterId());
     
 
        // 미터가 공급유형이 전기이면서 계약용량 정보가 있으면 메인전원으로 판단한다.
        Location loc = meter.getLocation();
        // chlp의 마지막 lp값과 60분을 lp 주기로 나눈 값을 곱하여 수요전력을 구한다.
        // ch[0]이 active energy 값인지 확인해야 한다. 채널 정보를 가져와야 할 지도 모른다.
        double demand = chlp[0][chlp[0].length-1] * (60 / meter.getLpInterval());

        log.debug("DEMAND[" + demand + "]");
        
        // 지역의 공급타입 중 전기에 해당하는 공급용량을 가져온다.
        Set<SupplyTypeLocation> set = loc.getSupplyTypeLocations();
        SupplyTypeLocation stl = null;
        ContractCapacity cc = null;
        int level = 0;
        double peak = 0;
        for (Iterator<SupplyTypeLocation> i = set.iterator(); i.hasNext(); ) {
            stl = i.next();
            if (SupplierType.valueOf(stl.getSupplyType().getTypeCode().getName()) == SupplierType.Electricity) {
                cc = stl.getContractCapacity();
                
                // 전기에 대한 공급지역을 가져왔는데 계약용량 정보가 없으면 종료
                if (cc == null)
                    return;
                
                // peak 계산
                peak = demand / cc.getCapacity() * 100.0;
                log.debug("PEAK[" + peak + "]");
                
                // level 결정
                if (peak >= cc.getThreshold1() && peak < cc.getThreshold2()) level = 1;
                else if (peak >= cc.getThreshold2() && peak < cc.getThreshold3()) level = 2;
                else if (peak >= cc.getThreshold3()) level = 3;
                log.debug("LEVEL[" + level + "]");
                
                Set<Condition> condition = new LinkedHashSet<Condition>();
                // ContraceCapacity와 PeakDemandScenario 관계가 1:n에서 1:1로 변경 예정
                condition.add(new Condition("contractCapacity.id", new Object[]{ cc.getId() }, null, Restriction.EQ));
                List<PeakDemandScenario> pdslist = pdsDao.findByConditions(condition);
                PeakDemandSetting pdset = null;
                for (PeakDemandScenario pds : pdslist.toArray(new PeakDemandScenario[0])) {
                    for (Iterator<PeakDemandSetting> pdsets = pds.getPeakDemandSettings().iterator(); pdsets.hasNext();) {
                        pdset = pdsets.next();
                        // 임계치 이벤트와 DR 전송
                        if (pdset.getThresholdLevel() == level) {
                            // PeakDemandLog 생성
                            PeakDemandLog pdlog = new PeakDemandLog();
                            pdlog.setScenario(pds);
                            pdlog.setThresholdLevel(level);
                            pdlog.setRunTime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddhhmmss"));
                            
                            // DR 전송 후 성공하면 이벤트 생성
                            // DR 성공 여부 로그 생성
                            log.debug("SCENARIO[" + pds.getName() + "] TARGET[" + pds.getTarget() + "]");
                            if (cmdDR(pds.getTarget())) {
                                pdlog.setResult(ResultStatus.SUCCESS.name());
                                EventUtil.sendEvent("Threshold Warning",
                                        TargetClass.EnergyMeter,
                                        meter.getMdsId(),
                                        new String[][]{{"message", "Scenario Name[" + pds.getName() + "] Level[" + level + "] Peak[" + peak + "%]"}});
                            }
                            else pdlog.setResult(ResultStatus.FAIL.name());
                            
                            pdlDao.add(pdlog);
                            
                            break;
                        }
                    }
                }
                break;
            }
        }
    }
    
    private boolean cmdDR(String tagName) {
        JMXConnector jmxc = null;
        try {
            JMXServiceURL url = new JMXServiceURL(jmxUrl);
    
            log.info("About to jmxcnect to : " + url.toString());
    
            jmxc = JMXConnectorFactory.connect(url);
            
            MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
            ObjectName objectName = new ObjectName(BemsAdapter.ADAPTER_DOMAIN+":name="+fepName);
            BemsAdapterMBean mbean = JMX.newMBeanProxy(mbsc, objectName, BemsAdapterMBean.class, true);
            mbean.cmdDR(tagName, 0);
            return true;
        }
        catch (Exception e) {
            log.error(e);
            return false;
        }
    }
    
    @Override
    public void onMessage(Message msg) {
        if (msg instanceof ObjectMessage) {
            try {
                processing(((ObjectMessage) msg).getObject());
            }
            catch (Exception e) {
		log.error("BEMS Processing Exception!! ["+e.getMessage()+"]");
                //log.error(e,e);
            }
        }
        else {
            log.warn("Message is not object, check it!!");
        }
    }
    
    public static void main(String[] args) {
        DataUtil.setApplicationContext(new ClassPathXmlApplicationContext(new String[]{"/config/spring-bems-listener.xml"}));
        // BemsProcessor fep = DataUtil.getBean(BemsProcessor.class);
        // fep.init();
    }

    @Override
    protected boolean save(IMeasurementData md) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }
}
