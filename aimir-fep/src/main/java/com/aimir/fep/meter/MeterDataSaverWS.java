package com.aimir.fep.meter;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.fep.meter.data.MDHistoryData;
import com.aimir.fep.util.Hex;

/**
 * 검침데이타 저장
 *
 * @author 박종성 elevas@nuritelecom.com
 */
@WebService(serviceName="MeterDataSaverWS")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
@Service
public class MeterDataSaverWS
{
    protected static Log log = LogFactory.getLog(MeterDataSaverWS.class);

    @Autowired
    private MeterDataSaverMain saver;

    @WebMethod
    public void save(
              @WebParam(name="McuId")String mcuId,
              @WebParam(name="MeteringDataCount") int cnt,
              @WebParam(name="MeteringDataBinary") byte[] data,
              @WebParam(name="IpAddr") String ipAddr,
              @WebParam(name="ProtocolType") String protocolType)
    throws Exception
    {
         save(mcuId, cnt, data, ipAddr, protocolType, null);
    }
    
    @WebMethod
    public void save(
              @WebParam(name="McuId")String mcuId,
              @WebParam(name="MeteringDataCount") int cnt,
              @WebParam(name="MeteringDataBinary") byte[] data,
              @WebParam(name="IpAddr") String ipAddr,
              @WebParam(name="ProtocolType") String protocolType,
              @WebParam(name="NameSpace") String nameSpace)
    throws Exception
    {
        log.debug("MCU_ID[" + mcuId + "] MeteringDataCnt[" + cnt + "] MeteringDataBinary[" + Hex.decode(data) + "]");
        MDHistoryData mdHistoryData = new MDHistoryData();
        /*
         * 테스트 용으로 잠시 주석 하였음. 테스트 완료 후 주석 풀겠슴
         * */
        if (checkMcuId(mcuId)) {
            mdHistoryData.setMcuId(mcuId);
            mdHistoryData.setEntryCount(cnt);
            mdHistoryData.setMdData(data);
            mdHistoryData.setIpAddr(ipAddr);
            mdHistoryData.setProtocolType(protocolType);
            mdHistoryData.setNameSpace(nameSpace);

            saver.save(mdHistoryData, false);

            log.debug("saveMeasurementData");
        }
        else {
            log.error("MCU_ID[" + mcuId + "] invalid!");
        }
    }
    
    @WebMethod
    public void saveRData(@WebParam(name="McuId")String mcuId, @WebParam(name="MeteringDataCount") int cnt,
            @WebParam(name="MeteringDataBinary") byte[] data)
    throws Exception
    {
        log.info("McuId[" + mcuId + "] MeteringDataCnt[" + cnt + "] MeteringDataBinary[" + Hex.decode(data) + "]");
        if (checkMcuId(mcuId)) {
            saver.saveRData(mcuId, cnt, data);

            log.debug("saveMeasurementData");
        }
        else {
            log.error("MCUID[" + mcuId + "] invalid!");
        }
    }

    private boolean checkMcuId(String mcuId) {
        return true;
        /*
        if (mcuId != null) {
            mcuId=mcuId.trim();
            for(int i = 0; i < mcuId.length(); i++){
                if(mcuId.charAt(i) < '0' || mcuId.charAt(i) > '9'){
                    return false;
                }
            }
        }
        else
            return false;
        return true;
        */
    }
}
