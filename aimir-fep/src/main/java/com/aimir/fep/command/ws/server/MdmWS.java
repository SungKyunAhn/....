package com.aimir.fep.command.ws.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.MeterProgramKind;
import com.aimir.constants.CommonConstants.OTATargetType;
import com.aimir.dao.device.MeterDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.MeterData;
import com.aimir.fep.meter.data.Response;
import com.aimir.fep.meter.file.CSV;
import com.aimir.fep.meter.file.DisplayItemSettingCSV;
import com.aimir.fep.meter.parser.MX2Table.DisplayItemSettingBuilder;
import com.aimir.fep.meter.parser.MX2Table.SummerTimeBuilder;
import com.aimir.fep.meter.parser.MX2Table.TOUCalendarBuilder;
import com.aimir.fep.meter.parser.MX2Table.VOBuilder;
import com.aimir.fep.meter.parser.plc.PLCDataFrame;
import com.aimir.fep.modem.BatteryLog;
import com.aimir.fep.modem.EventLog;
import com.aimir.fep.modem.ModemROM;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.exception.FMPException;
import com.aimir.fep.protocol.fmp.exception.FMPMcuException;
import com.aimir.fep.protocol.fmp.frame.service.Entry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiBindingEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiDeviceEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiMemoryEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiNeighborEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.drLevelEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.endDeviceEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.ffdEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.gpioEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.idrEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.loadControlSchemeEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.loadLimitPropertyEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.loadLimitSchemeEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.loadShedSchemeEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.memEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.meterEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.mobileEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.pluginEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.procEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.sensorInfoNewEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.sysEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.timeEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.trInfoEntry;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FileInfo;
import com.aimir.fep.util.GroupInfo;
import com.aimir.fep.util.GroupTypeInfo;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.MeterProgram;

/**
 * Command Gateway WebService
 *
 * @author elevas
 * @version $Rev: 1 $, $Date: 2013-01-03 15:59:15 +0900 $,
 */
@WebService(serviceName="MdmWS")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
@Service(value="mdmWS")
public class MdmWS
{
    private static Log log = LogFactory.getLog(MdmWS.class);
    /**
     * MCU Diagnosis
     *
     * @param mcuId MCU Indentifier
     * @return status (MCU status, GSM status, Sink 1 status, Sink 2 status)
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public @WebResult(name="response") ResponseMap cmdMcuDiagnosis(@WebParam(name="McuId") String mcuId)
            throws Exception
    {
        ResponseMap response = new ResponseMap();
        Map<String, String> map = new HashMap<String, String>();
        map.put("mcuId", mcuId);
        response.setResponse(map);
        return response;
    }

}

