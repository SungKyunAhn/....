package com.aimir.fep.protocol.coap.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;

import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.security.DtlsConnector;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.protocol.coap.frame.GeneralFrame;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.fep.protocol.coap.client.SyncCoapCommand;

public class CoapClient {
    private static Log log = LogFactory.getLog(CoapClient.class);
    public static void main(String[] args) throws Exception {
        GeneralFrame frm =  new GeneralFrame();
        Target tag =  new Target();
//      tag.setIpv6Addr("fd00:544a:16af:f699::1");
        tag.setIpv6Addr("FD00:544A:16AF:F699:AABB:CCDD:EEFF:0101");
        tag.setPort(5683);
        
        /**
         * request
         * /log/log_type
         * 
         * prameter setting
         * frm.setStartIndex(1);
         * frm.setEndIndex(10);
         * ex)uri:/log/log_type?s=1&e=10
         * 
         * response
         * ex)frm.getYyyymmddhhmmss()
         ***************************************
         * request
         * WellKnownCore("/.well-known/core")
         * 
         * response
         * server 에서 사용 중인 모든 Url,Content-Format를 보내줌.
         * ex)frm.getWellKnownCore()
         * 
         */
        
        frm.setUriAddr(frm._uriAddr.TimeUtcTime.getCode());
        frm.setURI_Addr();
        SyncCoapCommand client = new SyncCoapCommand();
        if (Boolean.parseBoolean(FMPProperty.getProperty("protocol.ssl.coap.enable", "false"))) {
            client.setEndpoint(new CoapEndpoint(DtlsConnector.newDtlsClientConnector(false, Protocol.GPRS, 5683, true), NetworkConfig.getStandard()));
        }
        client.sendCommand(tag, frm);
        
        /**
         * response result
         */
        switch (frm._uriAddr.getCode()) {
            case "/2/customer_number":
            case "/2/hw_version":
            case "/2/model_name":
            case "/2/sw_version":
            case "/8/id":
            case "/8/imei":
            case "/8/apn":
            case "/8/mobile_id":
            case "/8/mobile_number":
            case "/8/password":
            case "/dcu_info/hw_version":
            case "/dcu_info/id":
            case "/dcu_info/model_name":
            case "/dcu_info/sw_version":
            case "/2/meter_manufacture":
            case "/2/meter_serial":
                  log.info("[RESPONSE]"+frm.getResult());
                  break;
            case "/6/main_type":
            case "/6/sub_type":
            case "/8/mobile_mode":
            case "/8/mobile_type":
            case "/4/primary_power_source_type":
            case "/4/secondary_power_source_type":
                  log.info("[RESPONSE]"+frm.getType());
                  break;
            case "/3/disp_multiplier":
            case "/3/disp_scalar":
            case "/3/frequency":
            case "/3/phase_configuration":
            case "/3/vah_sf":
            case "/3/va_sf":
                  log.info("[RESPONSE]"+frm.getValue());
                  break;
            case "/3/ct":
            case "/3/pt":
            case "/3/transformer_ratio":
            case "/2/cummulative_active_power":
                 log.info("[RESPONSE]"+frm.getValue());
                 break;
            case "/2/meter_status":
                 log.info("[RESPONSE]"+frm.getValue());
                 break;
                 
            case "/7/main_port_number":     
            case "/9/listen_port":
                 log.info("[RESPONSE]"+frm.getPort());
                 break;
            case "/2/cummulative_active_power_time":
            case "/2/last_comm_time":
            case "/2/last_update_time":
            case "/1/utc_time":
                 log.info("[RESPONSE]"+frm.getYyyymmddhhmmss());
                 break;
            case "/7/main_ip_address":
                 log.info("[RESPONSE]"+frm.getAddress());
                 break;
            case "/9/bandwidth":
                 log.info("[RESPONSE]"+frm.getBandwidth());
                 break;
            case "/9/frequency":
                 log.info("[RESPONSE][startFreq:"+frm.getStartFreq()+",endFreq:"+frm.getEndFreq()+"]");
                 break;
            case "/9/hops_to_base_station":
                 log.info("[RESPONSE]"+frm.getHops());
                 break;
            case "/9/ipv6_address":
                 log.info("[RESPONSE]"+frm.getPassword());
                 break;
            case "/2/lp_channel_count":
                 log.info("[RESPONSE]"+frm.getCount());
                 break;
            case "/2/lp_interval":
                 log.info("[RESPONSE]"+frm.getInterval());
                 break;
            case "/4/operation_time":
                 log.info("[RESPONSE]"+frm.getCount());
                 break;
            case "/4/network_management_system":
                 log.info("[RESPONSE][parentNodeId:"+frm.getParentNodeId()
                 +",rssi:"+frm.getRssi()
                 +",etx:"+frm.getEtx()
                 +",cpuUsage:"+frm.getCpuUsage()
                 +",memoryUsage:"+frm.getMemoryUsage()
                 +",totalTxSize:"+frm.getTotalTxSize()
                 +"]");
                 break; 
            case "/4/reset_count":
                 log.info("[RESPONSE]"+frm.getCount());
                 break;
            case "/4/reset_reason":
                 log.info("[RESPONSE]"+frm.getReason().name());
                 break;
            case "/1/time_zone":
                 log.info("[RESPONSE]"+frm.getOffSet());
                 break;
            case "/8/misisdn":
                 log.info("[RESPONSE]"+frm.getMisisdn());
                 break;
            case "/8/ip_address":
                 log.info("[RESPONSE]"+frm.getIpAddress());
                 break;
            case "/8/access_technology_used":
                 log.info("[RESPONSE]"+frm.get_accessThechnology().name());
                 break;
            case "/8/rac_lac_cell_id":
                log.info("[RESPONSE][rac:"+frm.getRac()
                 +",lac:"+frm.getLac()
                 +",cid:"+frm.getCid()
                 +"]");
                break;
            case "/8/operator":
                log.info("[RESPONSE]"+frm.getOperator());
                break;
            case "/8/connection_status":
                log.info("[RESPONSE]"+frm.get_status().name());
                break;
            case "/.well-known/core":
                log.info("[RESPONSE]"+frm.getWellKnownCore());
                break;
            default:
                 //get시 파라미터 존재 하므로....
                 if(frm._uriAddr.getCode().indexOf("/10/log_type") >= 0){
                      log.info("[RESPONSE][logCount:"+frm.getLogCount()
                             +",index:"+frm.getIndex()
                             +",time:"+frm.getTime()
                             +",logCode:"+frm.getLogCode()
                             +",logValue:"+frm.getLogValue()
                             +"]");
                }
                break;
         }
        //log.info("getYyyymmddhhmmss:"+frm.getYyyymmddhhmmss());
//      AsyncCoapCommand client2 = new AsyncCoapCommand();
//      GeneralFrame frm2 =  new GeneralFrame();
//      Target tag2 =  new Target();
//      tag2.setIpv6Addr("2002:bb01:1ed9::bb01:1ed9");
//      frm2.setUriAddr("/time_zone");
//      frm2.setURI_Addr();
//      log.info("result:"+client2.sendCommand(tag2, frm2));
//      log.info("getYyyymmddhhmmss:"+frm.getYyyymmddhhmmss());
    }
}