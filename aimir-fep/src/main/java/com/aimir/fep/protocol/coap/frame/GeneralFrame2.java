package com.aimir.fep.protocol.coap.frame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.californium.core.CoapResponse;

import com.aimir.fep.util.DataUtil;
//MBB ETH *데이터 파싱하는 부분 문서에 맞게 수정필요
public class GeneralFrame2 {
    private static Log log = LogFactory.getLog(GeneralFrame.class);
    
    public static final byte Version = (byte)0x01;
    public enum URI_Addr {
        CommIFMainType("/6/main_type"),
        CommIFSubType("/6/sub_type"),
        DataCCHwVersion("/dcu_info/hw_version"),
        DataCCId("/dcu_info/id"),
        DataCCModelName("/dcu_info/model_name"),
        DataCCSwVersion("/dcu_info/sw_version"),
        EMInfoCt("/3/ct"),
        EMInfoDispMultiplier("/3/disp_multiplier"),
        EMInfoDispScalar("/3/disp_scalar"),
        EMInfoFrequency("/3/frequency"),
        EMInfoPhaseConfiguration("/3/phase_configuration"),
        EMInfoPt("/3/pt"),
        EMInfoTransFormerRatio("/3/transformer_ratio"),
        EMInfoVahSf("/3/vah_sf"),
        EMInfoVaSf("/3/va_sf"),
        EthIFMainIpAddress("/7/main_ip_address"),
        EthIFMainPortNumber("/7/main_port_number"),
        LogLogType("/10/log_type"),
        LowInfoBandWidth("/9/bandwidth"),
        LowInfoFrequency("/9/frequency"),
        LowInfoHopeToBaseStation("/9/hops_to_base_station"),
        LowInfoIpv6Address("/9/ipv6_address"),
        LowInfoListenPort("/9/listen_port"),
        MeterInfoCummulativeActivePower("/2/cummulative_active_power"),
        MeterInfoCummulativeActivePowerTime("/2/cummulative_active_power_time"),
        MeterInfoCustomerNumber("/2/customer_number"),
        MeterInfoHwVersion("/2/hw_version"),
        MeterInfoLastCommTime("/4/l_comm_t"),
        MeterInfoLastUpdateTime("/2/last_update_time"),
        MeterInfoLpChannelCount("/2/lp_channel_count"),
        MeterInfoLpInterval("/2/lp_interval"),
        MeterInfoMeterMenufacture("/2/meter_manufacture"),
        MeterInfoMeterSerial("/2/meter_serial"),
        MeterInfoMeterStatus("/2/meter_status"),
        MeterInfoModelName("/2/model_name"),
        MeterInfoSwVersion("/2/sw_version"),
        MobileIFID("/8/id"),
        MobileIFImei("/8/imei"),
        MobileIFMobileApn("/8/apn"),
        MobileIFMobileId("/8/mobile_id"),
        MobileIFMobileMode("/8/mobile_mode"),
        MobileIFMobileNumber("/8/mobile_number"),
        MobileIFMobileType("/8/mobile_type"),
        MobileIFPassword("/8/password"),
        MobileIFMisisdn("/8/misisdn"),
        MobileIFIpAddress("/8/ip_address"),
        MobileIFAccessTechnologyUsed("/8/access_technology_used"),
        MobileIFRacLacCellId("/8/rac_lac_cell_id"),
        MobileIFOperator("/8/operator"),
        MobileIFConnectionStatus("/8/connection_status"),
        MTInfoOperationTime("/4/operation_time"),
        MTInfoNetworkManagementSystem("/6/n_m_s"),
        MTInfoPrimaryPowerSourceType("/4/primary_power_source_type"),
        MTInfoResetCount("/4/reset_count"),
        MTInfoResetReason("/4/reset_reason"),
        MTInfoSecondaryPowerSourceType("/4/secondary_power_source_type"),
        TimeTimeZone("/1/time_zone"),
        TimeUtcTime("/1/utc_time"),
        WellKnownCore("/.well-known/core"),
    	ModemReset("/1/reset");
    		
        private String code;
	
        URI_Addr(String code){
            this.code = code;
        }
		
        public String getCode(){
            return this.code;
        }
    }
	
    public enum Type {
        Confirmable((int)0),
        Non_confirmable((int)1),
        Acknowledgement((int)2),
        Reset((int)3);
        
        private int code;
        
        Type(int code) {
            this.code = code;
        }
        
        public int getCode() {
            return this.code;
        }
    }
  
    public enum Code {
        GET("0.01"),
        POST("0.02"),
        PUT("0.03"),
        DELETE("0.04"),
        Created("2.01"),
        Deleted("2.02"),
        Valid("2.03"),
        Changed("2.04"),
        Content("2.05"),
        Bad_Request("4.00"),
        Unauthorized("4.01"),
        Bad_Option("4.02"),
        Forbidden("4.03"),
        Not_Found("4.04"),
        Method_Now_Allowed("4.05"),
        Not_Acceptable("4.06"),
        Precondition_Failed("4.12"),
        Request_Entity_Too_Large("4.13"),
        Unsupported_Content_Format("4.15"),
        Internal_Server_Error("5.00"),
        Not_Implemented("5.01"),
        Bad_Gateway("5.02"),
        Service_Unavailable("5.03"),
        Gateway_Timeout("5.04"),
        Proxying_Not_Supported("5.05");
      
        private String code;
      
        Code(String code) {
            this.code = code;
        }
      
        public String getCode() {
            return this.code;
        }
    }
  
    public enum OptionDelta {
        If_Match((byte)0x01),
        Uri_Host((byte)0x03),
        ETag((byte)0x04),
        If_None_Match((byte)0x05),
        Uri_Port((byte)0x07),
        Location_Path((byte)0x08),
        Uri_Path((byte)0x11),
        Content_Format((byte)0x12),
        Max_Age((byte)0x14),
        Uri_Query((byte)0x15),
        Accept((byte)0x17),
        Location_Query((byte)0x20),
        Proxy_Uri((byte)0x35),
        Proxy_Scheme((byte)0x39),
        Size((byte)0x60);
      
        private byte code;
      
        OptionDelta(byte code) {
            this.code = code;
        }
      
        public byte getCode() {
            return this.code;
        }
    }
  
    public enum ContentFormat {
        Text_plain((byte)0x00),
        link_format((byte)0x40),
        xml((byte)0x41),
        octet_stream((byte)0x42),
        exi((byte)0x47),
        json((byte)0x50);
  
        private byte code;
      
        ContentFormat(byte code) {
            this.code = code;
        }
      
        public byte getCode() {
            return this.code;
        }
    }
  
    public enum Reason {
        Pop_PdrOrBor((byte)0x01),
        Pin((byte)0x02),
        Por_Pdr((byte)0x04),
        Software((byte)0x08),
        Iwdog((byte)0x10),
        Wwdog((byte)0x20),
        LowPower((byte)0x40);
	      
        private byte code;
	      
        Reason(byte code) {
            this.code = code;
        }
	     
        public byte getCode() {
            return this.code;
        }
    }
  
  
    public enum AccessThechnology {
	  	Gsm((byte)0x00),
	  	GsmCompact((byte)0x01),
	  	Utran((byte)0x02),
	  	GsmWEgprs((byte)0x03),
	  	UtranWHsdpa((byte)0x04),
	  	UtranWHsupa((byte)0x05),
	  	UtranWHsdpaAndHsupa((byte)0x06),
	  	EUtran((byte)0x07);
	    private byte code;
	      
        AccessThechnology(byte code) {
          this.code = code;
        }
        public byte getCode() {
          return this.code;
        }
    }
  
    public enum Status {
	  	Disconnected((byte)0x00),
	  	Connected((byte)0x01);
	    private byte code;
	      
	    Status(byte code) {
	         this.code = code;
	    }
	    public byte getCode() {
	         return this.code;
	    }
    }

    //internal variable
    private byte Token;
    private byte[] subHead = new byte[1] ;
    private byte[] subHeadArray = new byte[3];
    private byte vtype;
    private Type _type;
    private byte tokenLength;
    private double doubleCode = 0.00;
    private byte OptionLength;
    private byte OptionValue;
      
    private AccessThechnology _accessThechnology;
      
    //request
    private int startIndex;
    private int endIndex;
      
    private int ipAddress;
      
    public int getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(int ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }
    
    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    //response
    private Code _code;
    private int messageId;
    private String yyyymmddhhmmss;
    private int offSet;
    private String result;
    private int value;
    private int count;
    private int interval;
    private int type;
    private String uriAddr;
    private Reason reason;
    private String address;
    private int port;
    
    private String misisdn;
    
    private String password;
    private String hops;
    private String startFreq;
    private String endFreq;
    private String bandwidth;
    
    private int logCount;
    private int index;
    private String time;
    private String logCode;
    private String logValue;
    
    private int rac;
    private int lac;
    private int cid;
    private String operator;
    private Status _status;
      
    private String parentNodeId;
    private int rssi;
    private int lqi;
    private int etx;
    private int cpuUsage;
    private int memoryUsage;
    private int totalTxSize;
      
    private String wellKnownCore;
  	
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;
    
    public Status get_status() {
        return _status;
    }
    
    public void set_status(Status _status) {
        this._status = _status;
    }
    
    public AccessThechnology get_accessThechnology() {
        return _accessThechnology;
    }
    
    public void set_accessThechnology(AccessThechnology _accessThechnology) {
        this._accessThechnology = _accessThechnology;
    }
    
    public String getParentNodeId() {
        return parentNodeId;
    }
    
    public void setParentNodeId(String parentNodeId) {
        this.parentNodeId = parentNodeId;
    }
    
    public int getRssi() {
        return rssi;
    }
    
    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
    
    public int getLqi() {
        return lqi;
    }
    
    public void setLqi(int lqi) {
        this.lqi = lqi;
    }
    
    public int getEtx() {
        return etx;
    }
    
    public void setEtx(int etx) {
        this.etx = etx;
    }
    
    public int getCpuUsage() {
        return cpuUsage;
    }
    
    public void setCpuUsage(int cpuUsage) {
        this.cpuUsage = cpuUsage;
    }
    
    public int getMemoryUsage() {
        return memoryUsage;
    }
    
    public void setMemoryUsage(int memoryUsage) {
        this.memoryUsage = memoryUsage;
    }
    
    public int getTotalTxSize() {
        return totalTxSize;
    }
    
    public void setTotalTxSize(int totalTxSize) {
        this.totalTxSize = totalTxSize;
    }
    
    public String getWellKnownCore() {
        return wellKnownCore;
    }
    
    public void setWellKnownCore(String wellKnownCore) {
        this.wellKnownCore = wellKnownCore;
    }
    
    public String getOperator() {
        return operator;
    }
    
    public void setOperator(String operator) {
        this.operator = operator;
    }
    
    public int getRac() {
        return rac;
    }
     
    public void setRac(int rac) {
        this.rac = rac;
    }
    
    public int getLac() {
        return lac;
    }
    
    public void setLac(int lac) {
        this.lac = lac;
    }
    
    public int getCid() {
        return cid;
    }
    
    public void setCid(int cid) {
        this.cid = cid;
    }
    
    public String getMisisdn() {
        return misisdn;
    }
    	
    public void setMisisdn(String misisdn) {
        this.misisdn = misisdn;
    }
      
    public Reason getReason() {
        return reason;
    }
    
    public void setReason(Reason reason) {
        this.reason = reason;
    }
  
    public int getMessageId() {
        return messageId;
    }
    	
    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }
    	
    public String getYyyymmddhhmmss() {
        return yyyymmddhhmmss;
    }
    	
    public void setYyyymmddhhmmss(String yyyymmddhhmmss) {
        this.yyyymmddhhmmss = yyyymmddhhmmss;
    }
    	
    public int getOffSet() {
        return offSet;
    }
    	
    public void setOffSet(int offSet) {
        this.offSet = offSet;
    }
    	
    public String getResult() {
        return result;
    }
    	
    public void setResult(String result) {
        this.result = result;
    }
    	
    public int getValue() {
        return value;
    }
    	
    public void setValue(int value) {
        this.value = value;
    }
    	
    public int getCount() {
        return count;
    }
    	
    public void setCount(int count) {
        this.count = count;
    }
    	
    public int getInterval() {
        return interval;
    }
    	
    public void setInterval(int interval) {
        this.interval = interval;
    }
    	
    public int getType() {
        return type;
    }
    	
    public String getAddress() {
        return address;
    }
    	
    public void setAddress(String address) {
        this.address = address;
    }
    	
    public int getPort() {
        return port;
    }
    	
    public void setPort(int port) {
        this.port = port;
    }
    	
    public String getPassword() {
        return password;
    }
    	
    public void setPassword(String password) {
        this.password = password;
    }
    	
    public String getHops() {
        return hops;
    }
    	
    public void setHops(String hops) {
        this.hops = hops;
    }
    	
    public String getStartFreq() {
        return startFreq;
    }
    	
    public void setStartFreq(String startFreq) {
        this.startFreq = startFreq;
    }
    	
    public String getEndFreq() {
        return endFreq;
    }
    	
    public void setEndFreq(String endFreq) {
        this.endFreq = endFreq;
    }
    	
    public String getBandwidth() {
        return bandwidth;
    }
    	
    public void setBandwidth(String bandwidth) {
        this.bandwidth = bandwidth;
    }
    	
    public int getLogCount() {
        return logCount;
    }
    	
    public void setLogCount(int logCount) {
        this.logCount = logCount;
    }
    	
    public int getIndex() {
        return index;
    }
    	
    public void setIndex(int index) {
        this.index = index;
    }
    	
    public String getTime() {
        return time;
    }
    	
    public void setTime(String time) {
        this.time = time;
    }
    	
    public String getLogCode() {
        return logCode;
    }
    	
    public void setLogCode(String logCode) {
        this.logCode = logCode;
    }
    	
    public String getLogValue() {
        return logValue;
    }
    	
    public void setLogValue(String logValue) {
        this.logValue = logValue;
    }
  
    public String getUriAddr() {
        return uriAddr;
    }

    public void setUriAddr(String uriAddr) {
        this.uriAddr = uriAddr;
    }
    
    public int getYearValue() {
        return year;
    }
    	
    public void setYearValue(int year) {
        this.year = year;
    }
    
    public int getMonthValue() {
        return month;
    }
    	
    public void setMonthValue(int month) {
        this.month = month;
    }
    
    public int getDayValue() {
        return day;
    }
    	
    public void setDayValue(int month) {
        this.day = day;
    }
    
    public int getHourValue() {
        return hour;
    }
    	
    public void setHourValue(int hour) {
        this.hour = hour;
    }
    
    public int getMinuteValue() {
        return minute;
    }
    	
    public void setMinuteValue(int minute) {
        this.minute = minute;
    }
    
    public int getSecondValue() {
        return second;
    }
    	
    public void setSecondValue(int second) {
        this.second = second;
    }
    
    public URI_Addr _uriAddr;
  
    public URI_Addr getURI() {
        return _uriAddr;
    }

    public void setURI(URI_Addr _uriAddr) {
        this._uriAddr = _uriAddr;
    }
  
  
    //Status 셋팅
    private void setStatus(byte code) {
        for (Status n : Status.values()) {
            if (code == n.getCode()) {
                _status = n;
                break;
            }
        }
    }
  
    //AccessThechnology 셋팅
    private void setAccessThechnology(byte code) {
        for (AccessThechnology n : AccessThechnology.values()) {
            if (code == n.getCode()) {
                _accessThechnology = n;
                break;
            }
        }
    }

  
    //Reason 셋팅
    private void setReason(byte code) {
        for (Reason n : Reason.values()) {
            if (code == n.getCode()) {
                reason = n;
                break;
            }
        }
    }
  
    //URI Addr 셋팅
    public void setURI_Addr() {
        for (URI_Addr n : URI_Addr.values()) {
            if (uriAddr == n.getCode()) {
                _uriAddr = n;
                break;
            }
        }
    }
    
    //subHead Type 셋팅
    private void setType(int code) {
        for (Type n : Type.values()) {
            if (code == n.getCode()) {
                _type = n;
                break;
            }
        }
    }
  
    //subHead Code 셋팅
    private void setCode(String code) {
        //System.out.println("setCode:"+String.valueOf(doubleCode));
        for (Code n : Code.values()) {
            if (code == n.getCode()) {
                _code = n;
                //System.out.println("n:"+String.valueOf(n.getCode()));
                break;
            }
        }
    }
  
    //response Decode
    public void decode(CoapResponse response) throws Exception {
        int pos = 0;
        byte[] b = new byte[1];
        setCode(response.getCode().toString());
        setType(response.advanced().getType().value);
        setMessageId(response.advanced().getMID());
      
        byte[] data = response.getPayload();
        //Payload 종류별
        switch (_uriAddr.getCode()) {
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
    			  b = new byte[data.length];
    		      System.arraycopy(data, pos, b, 0, b.length);
    		      result = DataUtil.getString(b);
          		  break;
    		case "/6/main_type":
    		case "/6/sub_type":
    		case "/8/mobile_mode":
    		case "/8/mobile_type":
    		case "/4/primary_power_source_type":
    		case "/4/secondary_power_source_type":
    			  b = new byte[1];
    	          System.arraycopy(data, pos, b, 0, b.length);
    	          type = DataUtil.getIntToByte(b[0]);
    	          break;
      	    // value:1byte
    		case "/3/disp_multiplier":
    		case "/3/disp_scalar":
    		case "/3/frequency":
    		case "/3/phase_configuration":
    		case "/3/vah_sf":
    		case "/3/va_sf":
    			  /**
    			   * Data Format(/3/phase_configuration)
    	    	   * 0 : Other, 1 : 3P4W,2 : 3P3W,3 : 3P2W,
    			   * 4 : 2P3W,5 : 2P2W,6 : 1P3W,7 : 1P2W
    	    	   */
    			  b = new byte[1];
    	          System.arraycopy(data, pos, b, 0, b.length);
    	          value = DataUtil.getIntToByte(b[0]);
    	          break;
      	    // value:4byte
    		case "/3/ct":
    		case "/3/pt":
    		case "/3/transformer_ratio":
    		case "/2/cummulative_active_power":
    			 b = new byte[4];
    	         System.arraycopy(data, pos, b, 0, b.length);
    	         value = DataUtil.getIntTo4Byte(b);
    	         break;
    		case "/2/meter_status":
    			 /**   
    	           * 	Data Format(/2/meter_status)
    	           *    byte0(MsbLsb)
    	           *    Clock,invalid,Replacebattery,Powerup,L1error,L2error,L3error,-,-
    	           *	Byte1(MsbLsb)
    			   *	Program memory error,RAM error,NV memory error,Watchdog error,Fraud attempt,-,-,-
    			   *	Byte2(MsbLsb)
    			   *	Communication error M-BUS,New M-BUS device discovered,-,-,-,-,-,-
    			   *	Byte3(MsbLsb)
    			   *	-,-,-,-,-,-,-,-
    	           */
    			 b = new byte[4];
    	         System.arraycopy(data, pos, b, 0, b.length);
    //	         value = Integer.parseInt(DataUtil.getBit(b[0])+DataUtil.getBit(b[1])+DataUtil.getBit(b[2])+DataUtil.getBit(b[3]));
    	         value = DataUtil.getIntTo4Byte(b);
    	         break;
    	         
    		case "/7/main_port_number":     
    		case "/9/listen_port":
    			 b = new byte[2];
    	         System.arraycopy(data, pos, b, 0, b.length);
    	         port = DataUtil.getIntTo2Byte(b);
    	         break;
    		case "/2/cummulative_active_power_time":
    		case "/4/l_comm_t":
    			 b = new byte[7];
    	     	 System.arraycopy(data, pos, b, 0, b.length);
    	     	 yyyymmddhhmmss=String.format("%04d%02d%02d%02d%02d%02d", 
                        DataUtil.getIntTo2Byte(new byte[]{b[0], b[1]}),
                        DataUtil.getIntToByte(b[2]),
                        DataUtil.getIntToByte(b[3]),
                        DataUtil.getIntToByte(b[4]),
                        DataUtil.getIntToByte(b[5]),
                        DataUtil.getIntToByte(b[6]));
    			 break;
    		case "/2/last_update_time":
    		case "/1/utc_time":
    			 b = new byte[7];
    	     	 System.arraycopy(data, pos, b, 0, b.length);
    	     	 yyyymmddhhmmss=String.format("%04d%02d%02d%02d%02d%02d", 
                        DataUtil.getIntTo2Byte(new byte[]{b[0], b[1]}),
                        DataUtil.getIntToByte(b[2]),
                        DataUtil.getIntToByte(b[3]),
                        DataUtil.getIntToByte(b[4]),
                        DataUtil.getIntToByte(b[5]),
                        DataUtil.getIntToByte(b[6]));
    			 break;
    		case "/7/main_ip_address":
    			 b = new byte[4];
    	         System.arraycopy(data, pos, b, 0, b.length);
    	         address = DataUtil.decodeIpAddr(b);
    	         
    			break;
    		case "/9/bandwidth":
    			 b = new byte[2];
    	         System.arraycopy(data, pos, b, 0, b.length);
    	         bandwidth = String.valueOf(DataUtil.getIntTo2Byte(b));
    			break;
    		case "/9/frequency":
    			 b = new byte[3];
    	         System.arraycopy(data, pos, b, 0, b.length);
    	         startFreq = String.valueOf(DataUtil.getIntTo3Byte(b));
    	         pos += b.length;
    	          
    	         b = new byte[3];
    	         System.arraycopy(data, pos, b, 0, b.length);
    	         endFreq = String.valueOf(DataUtil.getIntTo3Byte(b));
    			break;
    		case "/9/hops_to_base_station":
    			b = new byte[2];
    	        System.arraycopy(data, pos, b, 0, b.length);
    	        hops = String.valueOf(DataUtil.getIntTo2Byte(b));
    			break;
    		case "/9/ipv6_address":
    			 b = new byte[16];
    	         System.arraycopy(data, pos, b, 0, b.length);
    	         password = String.valueOf(DataUtil.getIntToBytes(b));
    			break;
    		
    		case "/2/lp_channel_count":
    			 b = new byte[1];
    	         System.arraycopy(data, pos, b, 0, b.length);
    	         count = DataUtil.getIntToByte(b[0]);
    			break;
    		case "/2/lp_interval":
    			b = new byte[1];
    	        System.arraycopy(data, pos, b, 0, b.length);
    	        interval = DataUtil.getIntToByte(b[0]);
    			break;
    		case "/4/operation_time":
    			b = new byte[4];
    	        System.arraycopy(data, pos, b, 0, b.length);
    	        count = DataUtil.getIntTo4Byte(b);
    			break;
    		case "/6/n_m_s":
    	        b = new byte[1];
    	        System.arraycopy(data, pos, b, 0, b.length);
    	        cpuUsage = DataUtil.getIntToByte(b[0]);
    	        pos+=b.length;
    
    	        b = new byte[1];
    	        System.arraycopy(data, pos, b, 0, b.length);
    	        memoryUsage = DataUtil.getIntToByte(b[0]);
    	        pos+=b.length;
    	        
    	        b = new byte[4];
    	        System.arraycopy(data, pos, b, 0, b.length);
    	        totalTxSize = DataUtil.getIntTo4Byte(b);
    	        pos+=b.length;
    			break;	
    		case "/4/reset_count":
    			b = new byte[2];
    	        System.arraycopy(data, pos, b, 0, b.length);
    	        count = DataUtil.getIntTo2Byte(b);
    			break;
    		case "/4/reset_reason":
    			 /**
    	    	   * Data Format
    	    	   * Field,Reserved,Low power,WWDOG,IWDOG,Software,POR/PDR,Pin,POR/PDR or BOR
    	    	   */
    	    	 b = new byte[1];
    	         System.arraycopy(data, pos, b, 0, b.length);
    	         setReason(b[0]);
    	         //reason = getBit(b[0]);
    			break;
    		case "/1/time_zone":
    			b = new byte[1];
    	        System.arraycopy(data, pos, b, 0, b.length);
    	        offSet = DataUtil.getIntToByte(b[0]);
    			break;
    			
    		case "/8/misisdn":
    			b = new byte[16];
    	         System.arraycopy(data, pos, b, 0, b.length);
    	         misisdn = String.valueOf(DataUtil.getIntToBytes(b));
    			break;
    		case "/8/ip_address":
    			b = new byte[4];
    	         System.arraycopy(data, pos, b, 0, b.length);
    	         ipAddress = DataUtil.getIntToBytes(b);
    			break;
    		case "/8/access_technology_used":
    			b = new byte[1];
    	         System.arraycopy(data, pos, b, 0, b.length);
    	         setAccessThechnology(b[0]);
    			break;
    		case "/8/rac_lac_cell_id":
    			b = new byte[2];
    	        System.arraycopy(data, pos, b, 0, b.length);
    	        rac = DataUtil.getIntTo2Byte(b);
    	        pos+=b.length;
    	        
    	        
    	        b = new byte[1];
    	        System.arraycopy(data, pos, b, 0, b.length);
    	        lac = DataUtil.getIntToByte(b[0]);
    	        pos+=b.length;
    	        
    	        
    	        b = new byte[4];
    	        System.arraycopy(data, pos, b, 0, b.length);
    	        cid = DataUtil.getIntTo4Byte(b);
    	        pos+=b.length;
    	        
    			break;
    		case "/8/operator":
    			b = new byte[16];
    	        System.arraycopy(data, pos, b, 0, b.length);
    	        operator = String.valueOf(DataUtil.getString(b));
    			break;
    		case "/8/connection_status":
    			b = new byte[1];
    	        System.arraycopy(data, pos, b, 0, b.length);
    	        setStatus(b[0]);
    			break;
    		case "/.well-known/core":
    			b = new byte[data.length];
    	        System.arraycopy(data, pos, b, 0, b.length);
    	        setWellKnownCore(DataUtil.getString(b));
    			break;
    		case "/1/reset":
    			break;
    		default:
    			 //get시 파라미터 존재 하므로....
    			 if(_uriAddr.getCode().indexOf("/10/log_type") >= 0){
    				  b = new byte[2];
    		          System.arraycopy(data, pos, b, 0, b.length);
    		          logCount = DataUtil.getIntTo2Byte(b);
    		          pos += b.length;
    		          
    		          b = new byte[2];
    		          System.arraycopy(data, pos, b, 0, b.length);
    		          index = DataUtil.getIntTo2Byte(b);
    		          pos += b.length;
    		          
    		          b = new byte[7];
    		          System.arraycopy(data, pos, b, 0, b.length);
    		          time = String.format("%04d%02d%02d%02d%02d%02d", 
    		                  DataUtil.getIntTo2Byte(new byte[]{b[0], b[1]}),
    		                  DataUtil.getIntToByte(b[2]),
    		                  DataUtil.getIntToByte(b[3]),
    		                  DataUtil.getIntToByte(b[4]),
    		                  DataUtil.getIntToByte(b[5]),
    		                  DataUtil.getIntToByte(b[6]));
    		          pos += b.length;
    		          
    		          b = new byte[2];
    		          System.arraycopy(data, pos, b, 0, b.length);
    		          logCode = String.valueOf(DataUtil.getIntTo2Byte(b));
    		          pos += b.length;
    		          
    		          b = new byte[4];
    		          System.arraycopy(data, pos, b, 0, b.length);
    		          logValue = String.valueOf(DataUtil.getIntTo4Byte(b));
    		    }
    			break;
        }
    }
}
