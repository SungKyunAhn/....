package com.aimir.fep.protocol.fmp.log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.SenderReceiverType;
import com.aimir.dao.device.CommLogDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.util.threshold.CheckThreshold;
import com.aimir.model.device.CommLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.SubGiga;
import com.aimir.model.system.Location;
import com.aimir.util.TimeUtil;
import com.aimir.util.DateTimeUtil;
/**
 * Common Logger
 *
 * @author J.S Park (elevas@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2009-11-21 15:59:15 +0900 $,
 */
@Component
public class CommLogger extends MessageLogger
{
    @Autowired
    private MCUDao mcuDao;

    @Autowired
    private ModemDao modemDao;
    
    @Autowired
    private MeterDao meterDao;

	@Autowired
	CommLogDao commLogDao = null;

	/**
     * constructor
     *
     * @throws Exception
     */
    public CommLogger() throws Exception
    {
        super();
    }

    @Override
    public String writeObject(Serializable obj) {

        try
        {
            File f = null;
            if( obj instanceof CommLog)
            {
                CommLog fl= (CommLog)obj;
                f = new File(logDirName,"FMPCommLog-"
                    + fl.getReceiverTypeCode().getName()+"-"
                    + fl.getReceiverId()
                    + fl.getOperationCode()+"-"
                    +System.currentTimeMillis()+".log");
            } else {
                f = new File(logDirName,"GeneralLog-"
                    +System.currentTimeMillis()+".log");
            }
            ObjectOutputStream os = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(f)));
            os.writeObject(obj);
            os.close();
        } catch (Exception e) {
            log.error("********" + getClass().getName()
                    + " write() Failed *********",e);
        }
        return null;
    }

    @Override
    public void backupObject(Serializable obj) {

        try
        {
            File f = null;
            if( obj instanceof CommLog)
            {
                CommLog fl= (CommLog)obj;
                f = new File(getBackupDir(),"FMPCommLog-"
                    + fl.getReceiverTypeCode().getName()+"-"
                    + fl.getReceiverId()
                    + fl.getOperationCode()+"-"
                    +System.currentTimeMillis()+".log");
            } else {
                f = new File(getBackupDir(),"GeneralLog-"
                    +System.currentTimeMillis()+".log");
            }
            ObjectOutputStream os = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(f)));
            os.writeObject(obj);
            os.close();
        } catch (Exception e) {
            log.error("********" + getClass().getName()
                    + " write() Failed *********",e);
        }
    }

    @Override
    @Transactional
    public void sendLog(final Serializable data)
    {
        CommLog commLog = (CommLog)data;
        try {
        	//log.info("commLog.getSenderTypeCode().getCode()	="+commLog.getSenderTypeCode().getCode());
        	//log.info("TargetClass.MCU.getCode()				="+TargetClass.MCU.getCode());
        	//log.info("TargetClass.Modem.getCode()			="+TargetClass.Modem.getCode());

        	SenderReceiverType senderType = SenderReceiverType.DCU;
        	Protocol protocolType = Protocol.GPRS;
        	String supplierId = commLog.getSuppliedId();
        	Location location = null;
        	
        	List<String> mculist = mcuDao.getMcuByIp(commLog.getSenderId());
            
        	MCU mcu = null;
            if (mculist != null && mculist.size() == 1) {
                commLog.setSenderId(mculist.get(0));
            }
            mcu = mcuDao.get(commLog.getSenderId());
            
            List<Object[]> modemlist = modemDao.getModemByIp(commLog.getSenderId());
            
            Modem modem = null;
            if (modemlist != null && modemlist.size() == 1) {
                commLog.setSenderId((String)modemlist.get(0)[1]);
            }
            modem = modemDao.get(commLog.getSenderId());
            
        	// MCU, Modem, Meter 어떤 것인지 확인한다.
        	Meter meter = meterDao.get(commLog.getSenderId());
        	
        	//통신 시간을 로그에서 가져온다.
            String endTime = commLog.getEndTime();
            if(endTime == null){
                //시간이 없으면 현재 시간으로 한다.
                endTime = DateTimeUtil.getDateString(new Date());
            }
            
            if(mcu !=null) {
                // commLog.setSuppliedId((mcu.getSupplier()!=null ? mcu.getSupplier().getId()+"":"null"));
                // commLog.setLocation(mcu.getLocation());
                // commLog.setSenderLocation(mcu.getLocation());
                log.debug("mcu : "+mcu.getSysID());
                log.debug("supplierId: "+(mcu.getSupplier()!=null ? mcu.getSupplier().getId()+"":"null"));
                // log.debug("location: "+ mcu.getLocation() != null ? mcu.getLocation().getName():"");
                log.debug("svcType: "+commLog.getSvcTypeCode());
                protocolType = Protocol.valueOf(mcu.getProtocolType().getName());
                supplierId = mcu.getSupplier().getId().toString();
                location = mcu.getLocation();
                
                // mcu 최종통신시간을 변경한다.
                String lastCommDate = mcu.getLastCommDate();
                if (lastCommDate == null || 
                        DateTimeUtil.getDateFromYYYYMMDDHHMMSS(commLog.getStartDateTime()).getTime() - DateTimeUtil.getDateFromYYYYMMDDHHMMSS(lastCommDate).getTime() > 3600000) {
                	mcu.setLastCommDate(commLog.getStartDateTime());
                	mcuDao.update(mcu);
                }
            }
            else if(modem != null) {
                for (SenderReceiverType s : SenderReceiverType.values()) {
                    if (s.name().equalsIgnoreCase(modem.getModemType().name())) {
                        senderType = s;
                        break;
                    }
                }
                protocolType = modem.getProtocolType();
                supplierId = modem.getSupplier().getId().toString();
                location = modem.getLocation();
                
                // modem 최종통신시간을 변경한다.
                // modem.setLastLinkTime(endTime);
                String lastCommDate = modem.getLastLinkTime();
                if (lastCommDate == null || 
                        DateTimeUtil.getDateFromYYYYMMDDHHMMSS(commLog.getStartDateTime()).getTime() - DateTimeUtil.getDateFromYYYYMMDDHHMMSS(lastCommDate).getTime() > 3600000) {
                    modem.setLastLinkTime(commLog.getStartDateTime());
                }
            }else if(meter != null){
            	// meter 의 Last Comm 항목 갱신
            	
            	//마지막 통신시간 갱신
            	// meter.setLastReadDate(endTime);
            }
    		commLog.setSuppliedId(supplierId);
            commLog.setLocation(location);
            commLog.setSenderLocation(location);
            if (protocolType != null)
                commLog.setProtocolCode(CommonConstants.getProtocolByName(protocolType.name()));
            else
                commLog.setProtocolCode(CommonConstants.getProtocolByName(Protocol.GPRS.name()));
            commLog.setSenderTypeCode(CommonConstants.getSenderReceiver(senderType.getLcode()));
            // commLog.setId(TimeUtil.getCurrentLongTime());
            commLog.setTime(TimeUtil.getCurrentTime());
        	commLogDao.add(commLog);
        	
        	// INSERT START SP-193
        	int totalCommTime = 1;
    		// UPDATE START SP-322
        	//if (commLog.getTotalCommTime() != null && commLog.getTotalCommTime() > 0)
        	//    totalCommTime = commLog.getTotalCommTime();
        	//Integer throughput = (commLog.getSendBytes() + commLog.getRcvBytes())/totalCommTime;
        	Integer throughput = 0;
        	if (commLog.getTotalCommTime() != null && commLog.getTotalCommTime() > 0){
        	    totalCommTime = commLog.getTotalCommTime();
            	throughput = (int)(((commLog.getSendBytes() + commLog.getRcvBytes())*1000)/totalCommTime);
            	log.debug("Throughput [" + throughput + "]");
        	} else {
        		log.debug("TOTAL_COMM_TIME is 0. Can not calculate throughput.");
        		return;
        	}
    		// UPDATE END SP-322
        	
        	String ip="";
        	if(mcu !=null) {
        	    if (mcu.getIpv6Addr() != null && !"".equals(mcu.getIpv6Addr()))
        	        ip = mcu.getIpv6Addr();
        	    else
        	        ip = mcu.getIpAddr();
        	}
        	else if(modem != null) {
        	    if (modem.getModemType() == ModemType.SubGiga) {
        	        SubGiga subgiga = (SubGiga)modem;
        	        if (subgiga.getIpv6Address() != null && !"".equals(subgiga.getIpv6Address()))
                        ip = subgiga.getIpv6Address();
                    else
                        ip = subgiga.getIpAddr();
        	    }
        	    else if (modem.getModemType() == ModemType.MMIU) {
        	        MMIU mmiu = (MMIU)modem;
                    if (mmiu.getIpv6Address() != null && !"".equals(mmiu.getIpv6Address()))
                        ip = mmiu.getIpv6Address();
                    else
                        ip = mmiu.getIpAddr();
        	    }
        	}
        	else if(meter != null){

        	}
            if ((ip != null) && (CheckThreshold.isUnderThroughput(throughput))) {
            	// UPDATE START SP-285
//            	CheckThreshold.addThroughputWarning(ip, throughput); 
            	CheckThreshold.addThroughputWarning(ip, throughput, false); 
            	// UPDATE START SP-285
            }
        	// INSERT START SP-193
        }
        catch (Exception e) {
            writeObject(commLog);
            log.error(e,e);
        }
    }
}
