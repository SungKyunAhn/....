package com.aimir.fep.meter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DataSVC;
import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.constants.CommonConstants.ThresholdName;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.mvm.MeasurementHistoryDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.fep.meter.data.AMUMDHistoryData;
import com.aimir.fep.meter.data.MDHistoryData;
import com.aimir.fep.meter.entry.AMUMeasurementDataEntry;
import com.aimir.fep.meter.entry.AMUMeasurementDataEntryList;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.entry.IMeasurementDataEntry;
import com.aimir.fep.meter.entry.MeasurementDataEntry;
import com.aimir.fep.meter.entry.MeasurementDataEntryList;
import com.aimir.fep.meter.parser.rdata.InventoryRData;
import com.aimir.fep.meter.parser.rdata.MeterConfigurationRData;
import com.aimir.fep.meter.parser.rdata.MeteringDataRData;
import com.aimir.fep.meter.parser.rdata.RData;
import com.aimir.fep.meter.parser.rdata.RDataConstant;
import com.aimir.fep.meter.parser.rdata.RDataList;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.threshold.CheckThreshold;
import com.aimir.model.device.ACD;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.GasMeter;
import com.aimir.model.device.HMU;
import com.aimir.model.device.HeatMeter;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MeasurementHistory;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.VolumeCorrector;
import com.aimir.model.device.WaterMeter;
import com.aimir.model.device.ZBRepeater;
import com.aimir.model.device.ZEUMBus;
import com.aimir.model.device.ZEUPLS;
import com.aimir.model.device.ZRU;
import com.aimir.model.system.DeviceConfig;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.MeterConfig;
import com.aimir.model.system.ModemConfig;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

/**
 * 검침데이타 저장
 *
 * @author 박종성 elevas@nuritelecom.com
 */
@Service
public class MeterDataSaverMain
{
    protected static Log log = LogFactory.getLog(MeterDataSaverMain.class);
    /*
    private static List<String> isProcessMeter = null;
    
    static {
        if (isProcessMeter == null)
            isProcessMeter = new ArrayList<String>();
    }
    */

    @Autowired
    private MCUDao mcuDao;
    
    @Autowired
    private ModemDao modemDao;
    
    @Autowired
    private MeterDao meterDao;
    
    @Autowired
    private MeasurementHistoryDao measurementHistoryDao;
    
    @Autowired
    private DeviceModelDao deviceModelDao;
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    /**
     * 검침데이타 저장
     * @param mdHistoryData EMDataList 하나
     * @throws Exception
     */
    @SuppressWarnings("finally")
    public LinkedHashMap<String, ArrayList<String>> save(MDHistoryData mdHistoryData, boolean isOndemand) throws Exception
    {
        LinkedHashMap<String, ArrayList<String>> oneMeasurementDataResult = new LinkedHashMap<String, ArrayList<String>>();
        ArrayList<String> succModem = new ArrayList<String>();
        ArrayList<String> failModem = new ArrayList<String>();
        ArrayList<String> isOndemandArray = new ArrayList<String>();
        try {
            saveMeasurementHistory(mdHistoryData);

            String mcuId = mdHistoryData.getMcuId();
            log.debug("MCU ID["+mcuId+"] isOndemand["+isOndemand+"] NameSpace = [" + mdHistoryData.getNameSpace() + "]");

            MeasurementDataEntry[] mde = getMeasurementDataEntry(mdHistoryData, isOndemand);

            log.debug("saveMeasurementData length["+mde.length+"]");
            int[] result = new int[]{0, 0};


            for(int i = 0 ; i < mde.length ; i++)
            {
                if(mde[i].getModemType()==CommonConstants.ModemType.ZRU) {
                    failModem.add(mde[i].getModemId());
                }
                log.debug("mde["+i+"]=["+mde[i].toString()+"]");
                try {
                    result = save(mcuId, mde[i]);
                }
                catch (Exception e) {
                    log.error(e, e);
                    result = new int[]{0, 1};
                }
                
                if(result!=null && result.length>0) {
                    if(result[0]>=1) {
                        failModem.remove(mde[i].getModemId());
                        succModem.add(mde[i].getModemId());
                    }
                }
                log.info("mde["+i+"]=[Success(" + result[0]+ ")][Fail(" + result[1]+")]");
            }
        }
        catch(Exception ex)
        {
            log.error("saveMeasurementData failed : "+ex, ex);
            throw ex;
        }finally {
            oneMeasurementDataResult.put("succ", succModem);
            oneMeasurementDataResult.put("fail", failModem);
            isOndemandArray.add(isOndemand+"");
            oneMeasurementDataResult.put("isOndemand", isOndemandArray);
            return oneMeasurementDataResult;
        }
    }

    /** 
     * SP-494
     */
    public MeasurementDataEntry[] getMeasurementDataEntry(MDHistoryData mdHistoryData, boolean isOndemand) {
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
            MeasurementDataEntryList mdel = new MeasurementDataEntryList();
            mdel.setOnDemand(isOndemand);
            mdel.setEmDataCnt(mdHistoryData.getEntryCount());
            mdel.setMcuId(mdHistoryData.getMcuId());
            mdel.decode(mdHistoryData.getMdData(), mdHistoryData.getNameSpace(),
                    mdHistoryData.getIpAddr(), mdHistoryData.getProtocolType());
            
            txmanager.commit(txstatus);
            return mdel.getMeasurementDataEntry();
        }
        catch (Exception e) {
            if (txstatus != null) txmanager.rollback(txstatus);
        }
        
        return null;
    }
    
    /** 
     * SP-494
     */
    public void saveMeasurementHistory(MDHistoryData mdHistoryData) {
        boolean rawdataSaved = Boolean.parseBoolean(FMPProperty.getProperty("rawdata.save.enable", "false"));

        if (rawdataSaved) {
            TransactionStatus txstatus = null;
            try {
                txstatus = txmanager.getTransaction(
                        new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
                MeasurementHistory mh = new MeasurementHistory();
    
                //mh.setDeviceType(mdHistoryData.get);
                mh.setId(TimeUtil.getCurrentLongTime());
                mh.setDeviceId(mdHistoryData.getMcuId());
                mh.setDataType(Integer.parseInt(FMPProperty.getProperty("hdm.data.type.md", "1")));
                mh.setDataCount(mdHistoryData.getEntryCount());
                mh.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                mh.setYyyymmdd(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd"));
                mh.setHhmmss(DateTimeUtil.getCurrentDateTimeByFormat("HHmmss"));
                mh.setRawData(mdHistoryData.getMdData());
                
                measurementHistoryDao.add(mh);
                
                txmanager.commit(txstatus);
            }
            catch (Exception e) {
                log.error(e, e);
                if (txstatus != null) txmanager.rollback(txstatus);
            }
        }
    }
    
    /**
     * 검침데이타 메인 로직
     * SP-494
     * @param mcuId 집중기의 sysID
     * @param mde   IF4-5문서중 EMDATAList 하나를 의미함
     * @throws Exception
     */
    public int[] save(String mcuId, MeasurementDataEntry mde) throws Exception
    {
        // 파서가 있는지 체크한다.
        long stime = System.currentTimeMillis();
        //테스트 후 수정
        String modemId = mde.getModemId();
        String meterId = mde.getMeterId();
        ModemType modemType = mde.getModemType();
        DataSVC svcType = mde.getSvcType();

        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(
                    new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
            MCU mcu = mcuDao.get(mcuId);
            McuType mcuType = null;
            if(mcu != null){
                int mcuCode = 0;
    
                if(mcu.getMcuType() != null){
                    String mcuTypeCode = mcu.getMcuType().getCode();
                    mcuCode = Integer.parseInt(mcuTypeCode.substring(mcuTypeCode.lastIndexOf(".")+1));
                }
                mcuType = CommonConstants.getMCUType(mcuCode);
            }
    
            txmanager.commit(txstatus);
    
            /*
             * add by js in Oct-8, 2008
             * 모뎀이 리피터인 경우 리피터 정보만 저장하고 종료한다.
             
            if (modemType == ModemType.ZBRepeater)
            {
                log.debug("Save Repeater Information");
                return new int[]{1, 0};
            }
            */
            
            log.debug("modemId="+modemId + ", meterId="+meterId + ", svcType="+svcType + ", modemType="+modemType);
        
            // 처리중인지 검사한다.
            /*
            if (isProcessMeter.contains(modemId+":"+meterId)) {
                log.info("modemId="+modemId + ", meterId="+meterId + " on process. SKIP!!");
                return new int[]{0,0};
            }
            else {
                isProcessMeter.add(modemId+":"+meterId);
            }
            */
            
            txstatus = txmanager.getTransaction(
                    new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
            int[] result = save(mde);
            
            long etime = System.currentTimeMillis();
            log.info("### finished Saving MD modemId[" + modemId +"] meterId[" + meterId +"] "
                    + "measurementdata count[" +mde.getMeasurementData().length+"] time["+(etime-stime)+"]");
            
            txmanager.commit(txstatus);
            
            return result;
        }
        catch (Exception e) {
            log.error(e, e);
            if (txstatus != null && !txstatus.isCompleted()) txmanager.rollback(txstatus);
            // success_cnt(0), fail_cnt(1)
            return new int[]{0, 1};
        }
        finally {
            // 리스트에서 처리된 것을 삭제한다.
            // isProcessMeter.remove(modemId+":"+meterId);
        }
    }

    /**
     * @param mde IF4-5문서중 EMDATAList 하나를 의미함
     * @throws Exception
     */
    private int[] save(IMeasurementDataEntry mde)
    throws Exception
    {
        int suc = 0;
        int fail = 0;
        int[] result = new int[]{0, 0};
        for (IMeasurementData data : mde.getSortedMeasurementData()) {
            result = save(data);
            suc += result[0];
            fail += result[1];
        }

        return new int[] {suc, fail};
    }
    
    /**
     * @param mde IF4-5문서중 EMDATA 하나를 의미함
     * @throws Exception
     */
    private int[] save(IMeasurementData md)
    throws Exception
    {
        int suc = 0;
        int fail = 0;
        boolean result = false;
        DeviceConfig config = null;
        AbstractMDSaver saver = null;
        if (md.getMeterDataParser() == null) {
            log.warn("Parser is not exist");
            fail++;

            return new int[]{suc, fail};
        }
        
        // lazyexception
        Meter meter = meterDao.get(md.getMeterDataParser().getMDevId());
        md.getMeterDataParser().setMeter(meter);
        
        if (md.getMeterDataParser().getMeter().getModel() != null)
            config = md.getMeterDataParser().getMeter().getModel().getDeviceConfig();
        
        if (config == null || (config != null && (config.getSaverName() == null || "".equals(config.getSaverName()))))
            config = md.getMeterDataParser().getMeter().getModem().getModel().getDeviceConfig();
        
        log.debug("Saver[" + config.getSaverName() + "]");
        // 저장 객체를 생성한다.

        // 2012.12.13 ondemand 에 대한 저장 로직 추가
        if(md.getMeterDataParser().isOnDemand() && config.getOndemandSaverName() != null && !"".equals(config.getOndemandSaverName())) {
            saver = (AbstractMDSaver)DataUtil.getBean(Class.forName(config.getOndemandSaverName()));
        }
        else saver = (AbstractMDSaver)DataUtil.getBean(Class.forName(config.getSaverName()));

        try {               
            result = saver.save(md);                
            if (result)
                suc++;
            else
                fail++;
        }
        catch (Exception e) {
            //TODO throw가 없으면 Exception을 던지지 못해서 저장 실패시 파일로 저장하는 로직이 돌아가지 않을 텐데 왜 throw 하지 않는지 체크 필요
            // e.printStackTrace();
            log.error(e,e);
            fail++;
            if ( md.getMeterDataParser().getMeter().getModem().getIpAddr() != null ) {
                // UPDATE START SP-285
//                  CheckThreshold.updateCount(data.getMeterDataParser().getMeter().getModem().getIpAddr(), ThresholdName.INVALID_PACKET); // INSERT SP-193
                CheckThreshold.updateCount(md.getMeterDataParser().getMeter().getModem().getIpAddr(), ThresholdName.INVALID_PACKET, false); // INSERT SP-193
                // UPDATE END SP-285
            } else if ( md.getMeterDataParser().getMeter().getModem() instanceof MMIU ) {
                MMIU mmiu = (MMIU)md.getMeterDataParser().getMeter().getModem();
                // if (mmiu.getIpv6Address() != null)
                    // UPDATE START SP-285
//                        CheckThreshold.updateCount(mmiu.getIpv6Address(), ThresholdName.INVALID_PACKET); // INSERT SP-193
                    CheckThreshold.updateCount(mmiu.getIpv6Address(), ThresholdName.INVALID_PACKET, false); // INSERT SP-193
                    // UPDATE END SP-285
            }
        }

        return new int[] {suc, fail};
    }

    public void save(AMUMDHistoryData mdHistoryData)
    throws Exception
    {

        try {
            boolean rawdataSaved = Boolean.parseBoolean(FMPProperty.getProperty("rawdata.save.enable", "false"));

            if (rawdataSaved) {
                MeasurementHistory mh = new MeasurementHistory();
                mh.setId(TimeUtil.getCurrentLongTime());
                mh.setDeviceId(mdHistoryData.getSourceAddr());
                mh.setDataType(Integer.parseInt(FMPProperty.getProperty("hdm.data.type.amu","4" )));
                mh.setDataCount(mdHistoryData.getEntryCount());
                mh.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                mh.setYyyymmdd(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd"));
                mh.setHhmmss(DateTimeUtil.getCurrentDateTimeByFormat("HHmmss"));
                mh.setRawData(mdHistoryData.getMdData());
                
                measurementHistoryDao.add(mh);
            }

            String sourceAddr = mdHistoryData.getSourceAddr();
            String destAddr = mdHistoryData.getDestAddr();
            log.debug("SourceAdr["+sourceAddr+"] DestAddr[" + destAddr + "]");

            AMUMeasurementDataEntryList mdel
                = new AMUMeasurementDataEntryList();
            mdel.setEmDataCnt(mdHistoryData.getEntryCount());
            mdel.setSourceAddr(sourceAddr);
            mdel.setDestAddr(destAddr);

            // TODO source address를 이용해서 계량기를 찾아야 한다.
            mdel.decode(mdHistoryData.getMdData());
            AMUMeasurementDataEntry[] mde =
                mdel.getMeasurementDataEntry();

            log.debug("saveMeasurementData length["+mde.length+"]");
            int[] result;

            for(int i = 0 ; i < mde.length ; i++)
            {
                log.debug("mde["+i+"]=["+mde[i].toString()+"]");
                result = save(mde[i]);
                log.info("mde["+i+"]=[Success(" + result[0]+ ")][Fail(" + result[1]+")]");
            }
        }
        catch(Exception ex)
        {
            log.error("saveMeasurementData failed : "+ex, ex);
            // if (ut != null) ut.rollback();
            throw ex;
        }
    }
    
    public void saveRData(String mcuId, int cnt, byte[] data) throws Exception {
        boolean rawdataSaved = Boolean.parseBoolean(FMPProperty.getProperty("rawdata.save.enable", "false"));

        try {
            if (rawdataSaved) {
                MeasurementHistory mh = new MeasurementHistory();
    
                //mh.setDeviceType(mdHistoryData.get);
                mh.setId(TimeUtil.getCurrentLongTime());
                mh.setDeviceId(mcuId);
                mh.setDataType(Integer.parseInt(FMPProperty.getProperty("hdm.data.type.rmd", "5")));
                mh.setDataCount(cnt);
                mh.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                mh.setYyyymmdd(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd"));
                mh.setHhmmss(DateTimeUtil.getCurrentDateTimeByFormat("HHmmss"));
                mh.setRawData(data);
                
                measurementHistoryDao.add(mh);
            }
        }
        catch (Exception e) {
            log.warn(e);
        }
        
        RDataList rdataList = new RDataList();
        rdataList.setCnt(cnt);
        rdataList.setMcuId(mcuId);
        rdataList.setrDataRaw(data);
        rdataList.decode();
        
        for (RData rdata : rdataList.getrDatas().toArray(new RData[0])) {
            try {
                if (rdata instanceof MeterConfigurationRData) {
                    saveMeterConfiguration(mcuId, (MeterConfigurationRData)rdata);
                }
                else if (rdata instanceof InventoryRData) {
                    saveInventory(mcuId, (InventoryRData)rdata);
                }
                else if (rdata instanceof MeteringDataRData) {
                    // MeasurementDataEntry 포맷으로 변환한다.
                    saveMeteringData(mcuId, (MeteringDataRData)rdata);
                }
            }
            catch (Exception e) {
                // 정상적으로 처리되는 것이 있기 때문에 예외를 받아내야 함.
                log.warn(e, e);
            }
        }
    }
    
    public void saveMeteringData(String mcuId, MeteringDataRData data) throws Exception {
        /*
        Set<Condition> condition = new LinkedHashSet<Condition>();
        condition.add(new Condition("modem.mcu.sysID", new Object[]{mcuId}, null, Restriction.EQ));
        condition.add(new Condition("shortId", new Object[]{data.getShortId()}, null, Restriction.EQ));
        List<Meter> meterList = meterDao.findByConditions(condition);
        */
        List<Meter> meterList = meterDao.getMeters(mcuId, data.getShortId());
        
        if (meterList.size() != 1) {
            throw new Exception("It must be one meter for shortId[" + data.getShortId() + "] mcuId[" + mcuId + "]");
        }
        
        Meter meter = meterList.get(0);
        Modem modem = meter.getModem();
        
        if (modem == null) {
            throw new Exception("Modem for meter[" + meter.getMdsId() + "] dosen't exist");
        }
        
        SMIValue smiValue = DataUtil.getSMIValueByObject("mdID", modem.getDeviceSerial());
        byte[] eui64 = smiValue.getVariable().encode();
        smiValue = DataUtil.getSMIValueByObject("mdSerial", meter.getMdsId());
        byte[] meterSerial = DataUtil.fillCopy(smiValue.getVariable().encode(), (byte)0x00, 20);
        byte[] stype = new byte[]{modem.getModemType().getCode().byteValue()};
        byte[] svc = null;
        if (meter instanceof EnergyMeter) {
            svc = new byte[]{RDataConstant.ServiceType.Electricity.getCode().byteValue()};
        }
        else if (meter instanceof GasMeter) {
            svc = new byte[]{RDataConstant.ServiceType.Gas.getCode().byteValue()};
        }
        else if (meter instanceof WaterMeter) {
            svc = new byte[]{RDataConstant.ServiceType.Water.getCode().byteValue()};
        }
        else if (meter instanceof HeatMeter) {
            svc = new byte[]{RDataConstant.ServiceType.Heat.getCode().byteValue()};
        }
        else if (meter instanceof VolumeCorrector) {
            svc = new byte[]{RDataConstant.ServiceType.VolumeCorrector.getCode().byteValue()};
        }
        byte[] vendor = new byte[]{0};
        byte[] dataCnt = DataUtil.get2ByteToInt(1);
        DataUtil.convertEndian(dataCnt);
        byte[] len = DataUtil.get2ByteToInt(data.getPayload().length + 7);
        DataUtil.convertEndian(len);
        smiValue = DataUtil.getSMIValueByObject("mdTime", DateTimeUtil.getDateString(new Date()));
        byte[] timestamp = smiValue.getVariable().encode();
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.write(eui64);
        bos.write(meterSerial);
        bos.write(stype);
        bos.write(svc);
        bos.write(vendor);
        bos.write(dataCnt);
        bos.write(len);
        bos.write(timestamp);
        bos.write(data.getPayload());
        
        MeasurementDataEntry mde = new MeasurementDataEntry();
        mde.decode(bos.toByteArray(), 0, mcuId, null, null, null);
        save(mcuId, mde);
    }
    
    public void saveMeterConfiguration(String mcuid, MeterConfigurationRData data) {
        
    }
    
    private Modem validateModem(MCU mcu, InventoryRData data) throws Exception {
        // ID로 모뎀을 찾는다.
        Modem modem = modemDao.get(data.getId());
        DeviceModel model = null;
        
        // modem이 없으면 생성한다.
        if (modem == null) {
            // modem 유형을 구분한다. 파서와 svc를 이용한다.
            switch (data.getParserType()) {
            case Pulse :
                modem = new ZEUPLS();
                break;
            case Aidon :
            case Ansi :
            case Kamstrup :
            case DLMS :
            case I210 :
            case SX :
            case Osaki :
                modem = new ZRU();
                modem.setModemType(ModemType.ZRU.name());
                break;
            case Repeater :
                modem = new ZBRepeater();
                modem.setModemType(ModemType.ZBRepeater.name());
                break;
            case MBus :
                modem = new ZEUMBus();
                modem.setModemType(ModemType.ZEUMBus.name());
                break;
            case Acd :
                modem = new ACD();
                modem.setModemType(ModemType.ACD.name());
                break;
            case SmokeSensor :
                break;
            case Ihd :
                break;
            case Hmu :
                modem = new HMU();
                modem.setModemType(ModemType.HMU.name());
                break;
            case FireAlarm :
                break;
            }
            
            if (data.getModel() != null) {
                List<DeviceModel> list = deviceModelDao.getDeviceModelByName(mcu.getSupplierId(), data.getModel());
                if (list != null && list.size() == 1 && list.get(0).getDeviceConfig() instanceof ModemConfig)
                    modem.setModel(list.get(0));
            }
            
            modem.setDeviceSerial(data.getId());
            modem.setIdType(data.getIdType().getCode());
            modem.setInstallDate(data.getInstallDateTime());
            modem.setHwVer(data.getHwVersion());
            modem.setSwVer(data.getSwVersion());
            modem.setFwVer(data.getSwVersion());
            modem.setFwRevision(data.getSwBuild());
            modem.setLpPeriod(data.getLpPeriod());
            modem.setSupplier(mcu.getSupplier());
            modem.setProtocolType(Protocol.ZigBee.name());
            modem.setMcu(mcu);
            modemDao.add(modem);
        }
        
        // modem의 mcu와 mcuId가 같은지 확인한다.
        MCU modemMcu = modem.getMcu();
        // modem의 mcu가 널인지 확인 후 널이거나 mcuId가 다르면 mcu를 매핑한다.
        if (modemMcu == null || !modemMcu.getSysID().equals(mcu.getSysID())) {
            modem.setMcu(mcu);
        }
        
        return modem;
    }
    
    private void validateMeter(MCU mcu, Modem modem, InventoryRData data) throws Exception {
        // mcuId와 shortId로 계량기를 찾아서 meterId를 비교하여 다르면 shortId를 널로 변경한다.
        /*
        Set<Condition> condition = new LinkedHashSet<Condition>();
        condition.add(new Condition("modem.mcu.sysID", new Object[]{mcu.getSysID()}, null, Restriction.EQ));
        condition.add(new Condition("shortId", new Object[]{data.getShortId()}, null, Restriction.EQ));
        List<Meter> meterList = meterDao.findByConditions(condition);
        */
        // 미터 번호가 널이거나 공백이면
        if (data.getMeterId() == null || "".equals(data.getMeterId()))
            return;
        
        List<Meter> meterList = meterDao.getMeters(mcu.getSysID(), data.getShortId());
        
        for (Meter meter : meterList.toArray(new Meter[0])) {
            if (!meter.getMdsId().equals(data.getMeterId()))
                meter.setShortId(null);
        }
        
        // DB에 매핑되어 있는 계량기를 가져온다.
        Meter[] dbMeter = modem.getMeter().toArray(new Meter[0]);
        
        // Modem 타입이 펄스식, 전자식, MBus인 경우만 계량기 검사를 진행한다.
        if (modem instanceof ZEUPLS || modem instanceof ZRU) {
            
            // 모뎀과 매핑된 계량기가 없거나 아이디가 다르면 새로운 것으로 매핑한다.
            if (dbMeter.length == 0 || !dbMeter[0].getMdsId().equals(data.getMeterId())) {
                // 기존 계량기의 모뎀 정보 매핑을 없앤다.
                for (int i = 0; i < dbMeter.length; i++) {
                    dbMeter[i].setModem(null);
                }
                
                Meter meter = meterDao.get(data.getMeterId());
                
                // DB에 저장된 계량기가 없으면 생성한다.
                if (meter == null) {
                    // service type의 constant가 RDataConstant에 있어서 사용할 수 없음. 2:가스, 3:수도
                    if (modem instanceof ZEUPLS) {
                        if (data.getServiceType().getCode() == 2) {
                            meter = new GasMeter();
                            meter.setPulseConstant(100.0); // default
                            meter.setMeterType(CommonConstants.getMeterTypeByName(MeterType.GasMeter.name()));
                        }
                        else if (data.getServiceType().getCode() == 3) {
                            meter = new WaterMeter();
                            meter.setPulseConstant(100.0);
                            meter.setMeterType(CommonConstants.getMeterTypeByName(MeterType.WaterMeter.name()));
                        }
                    }
                    else if (modem instanceof ZRU) {
                        meter = new EnergyMeter();
                        meter.setPulseConstant(0.01); // default
                        meter.setMeterType(CommonConstants.getMeterTypeByName(MeterType.EnergyMeter.name()));
                    }
                    meter.setShortId(data.getShortId());
                    meter.setMdsId(data.getMeterId());
                    meter.setModem(modem);
                    meter.setInstallDate(data.getInstallDateTime());
                    meter.setSupplier(modem.getSupplier());
                    meter.setLpInterval(60 / modem.getLpPeriod());
                    
                    // 모델 정보 가져오기
                    if (data.getModel() != null) {
                        List<DeviceModel> list = deviceModelDao.getDeviceModelByName(mcu.getSupplierId(), data.getModel());
                        if (list != null && list.size() == 1 && list.get(0).getDeviceConfig() instanceof MeterConfig)
                            meter.setModel(list.get(0));
                    }
                    
                    meterDao.add(meter);
                    
                    EventUtil.sendEvent("Equipment Registration",
                            TargetClass.valueOf(meter.getMeterType().getName()),
                            meter.getMdsId(),
                            new String[][] {{"message",
                                "MeterType[" + meter.getMeterType().getName() +
                                "] MCU[" + mcu.getSysID()+
                                "] MODEM[" + meter.getModem().getDeviceSerial()+ "] on saving metering value"}}
                            );
                }
                else {
                    meter.setShortId(data.getShortId());
                    meter.setModem(modem);
                }
            }
            else {
                // shortId를 비교하여 다르면 변경한다.
                if (dbMeter[0].getShortId() == null || dbMeter[0].getShortId() != data.getShortId())
                    dbMeter[0].setShortId(data.getShortId());
            }
            
        }
        else if (modem instanceof ZEUMBus) {
            // 모뎀과 매핑된 계량기를 가져와서 같은 meterid가 있는 지 확인 후 
            // 포트가 다르면 변경하고 
            boolean contains = false;
            for (int i = 0; i < dbMeter.length; i++) {
                if (dbMeter[i].getMdsId().equals(data.getMeterId())) {
                    contains = true;
                    if (dbMeter[i].getModemPort() != data.getPortNum())
                        dbMeter[i].setModemPort(data.getPortNum());
                    if (dbMeter[i].getShortId() == null || dbMeter[i].getShortId() != data.getShortId())
                        dbMeter[i].setShortId(data.getShortId());
                    
                    break;
                }
            }
            
            if (!contains) {
                Meter meter = meterDao.get(data.getMeterId());
                
                if (meter == null) {
                    switch (data.getServiceType()) {
                    case Electricity :
                        meter = new EnergyMeter();
                        meter.setPulseConstant(0.01); // default
                        meter.setMeterType(CommonConstants.getMeterTypeByName(MeterType.EnergyMeter.name()));
                        break;
                    case Gas :
                        meter = new GasMeter();
                        meter.setPulseConstant(100.0); // default
                        meter.setMeterType(CommonConstants.getMeterTypeByName(MeterType.GasMeter.name()));
                        break;
                    case Water :
                    case WarmWater :
                    case Cooling :
                        meter = new WaterMeter();
                        meter.setPulseConstant(100.0); // default
                        meter.setMeterType(CommonConstants.getMeterTypeByName(MeterType.WaterMeter.name()));
                        break;
                    case Heat :
                        meter = new HeatMeter();
                        meter.setMeterType(CommonConstants.getMeterTypeByName(MeterType.HeatMeter.name()));
                        break;
                    case VolumeCorrector :
                        meter = new VolumeCorrector();
                        meter.setMeterType(CommonConstants.getMeterTypeByName(MeterType.VolumeCorrector.name()));
                        break;
                    }
                    
                    meter.setShortId(data.getShortId());
                    meter.setMdsId(data.getMeterId());
                    meter.setModem(modem);
                    meter.setModemPort(data.getPortNum());
                    meter.setInstallDate(data.getInstallDateTime());
                    
                    meterDao.add(meter);
                    
                    EventUtil.sendEvent("Equipment Registration",
                            TargetClass.valueOf(meter.getMeterType().getName()),
                            meter.getMdsId(),
                            new String[][] {{"message",
                                "MeterType[" + meter.getMeterType().getName() +
                                "] MCU[" + mcu.getSysID()+
                                "] MODEM[" + meter.getModem().getDeviceSerial()+ "] on saving metering value"}}
                            );
                }
                else {
                    meter.setShortId(data.getShortId());
                    meter.setModem(modem);
                    meter.setModemPort(data.getPortNum());
                }
            }
        }
    }
    
    public void saveInventory(String mcuId, InventoryRData data) throws Exception {
        MCU mcu = mcuDao.get(mcuId);
        if (mcu == null) {
            throw new Exception("MCU[" + mcuId + "] doesn't exist");
        }
    
        Modem modem = validateModem(mcu, data);
        
        validateMeter(mcu, modem, data);
    }
}
