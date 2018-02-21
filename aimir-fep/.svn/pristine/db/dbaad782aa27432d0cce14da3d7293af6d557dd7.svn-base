package com.aimir.fep.test;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.aimir.fep.command.mbean.CommandGWMBean;

public class drTest {

	public static void main(String[] args) { 
		onOff();
	}
	
    public static void onOff() {
    	JMXConnector jmxc = null;
        try {
        	String jmxUrl = "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi";
            JMXServiceURL url = new JMXServiceURL(jmxUrl);
            jmxc = JMXConnectorFactory.connect(url);
            MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
            ObjectName objectName = new ObjectName("Service:name=FMPCommandGW");
            CommandGWMBean mbeanProxy = JMX.newMBeanProxy(mbsc, objectName, CommandGWMBean.class, true);
        //    List list = mbeanProxy.cmdGetDRAssetInfo("4", null, null);
       //     drAssetEntry dr = new drAssetEntry();
            
       //     for(int i = 0; i < list.size(); i++) {
       //     	dr = (drAssetEntry) list.get(i);
       //     	log.debug(dr.getId() + ", " + dr.getbDRAsset() + ", " + dr.getnCurrentLevel() + ", " + dr.getLastUpdate() + ", " + dr.getnLevelCount() + ", " + dr.getaLevelArray() );
       //     }
       //     System.out.println(DataUtil.getSMIValue("streamEntry",a+""));
         //   mbeanProxy.cmdSetEnergyLevel("4", "000D6F0000D69CD2", "1"); //ACD
            String xmlTmp = "<?xml version='1.0' encoding='utf-8' standalone='yes'?> <Pricings xmlns='http://www.kepco.or.kr/smartplace/profiles/events/Pricings#'> <Pricing> <CustomerAccount> <mRID>00803404143</mRID> </CustomerAccount> <MeterAsset> <mRID>01150724156</mRID> </MeterAsset> <priceUnit>WON</priceUnit> <CBLPUnit>kWh</CBLPUnit> <priceType>RTP</priceType> <pricingDate>2012-02-01+09:00</pricingDate> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T01:00:00.000+09:00</end> <start>2012-02-01T00:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>131.4</highPrice> </Price> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T02:00:00.000+09:00</end> <start>2012-02-01T01:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>131.4</highPrice> </Price> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T03:00:00.000+09:00</end> <start>2012-02-01T02:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>131.4</highPrice> </Price> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T04:00:00.000+09:00</end> <start>2012-02-01T03:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>131.4</highPrice> </Price> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T05:00:00.000+09:00</end> <start>2012-02-01T04:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>131.4</highPrice> </Price> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T06:00:00.000+09:00</end> <start>2012-02-01T05:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>131.4</highPrice> </Price> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T07:00:00.000+09:00</end> <start>2012-02-01T06:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>131.4</highPrice> </Price> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T08:00:00.000+09:00</end> <start>2012-02-01T07:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>131.4</highPrice> </Price> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T09:00:00.000+09:00</end> <start>2012-02-01T08:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>131.4</highPrice> </Price> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T10:00:00.000+09:00</end> <start>2012-02-01T09:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>180.5</highPrice> </Price> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T11:00:00.000+09:00</end> <start>2012-02-01T10:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>209.5</highPrice> </Price> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T12:00:00.000+09:00</end> <start>2012-02-01T11:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>209.5</highPrice> </Price> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T13:00:00.000+09:00</end> <start>2012-02-01T12:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>180.5</highPrice> </Price> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T14:00:00.000+09:00</end> <start>2012-02-01T13:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>180.5</highPrice> </Price> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T15:00:00.000+09:00</end> <start>2012-02-01T14:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>180.5</highPrice> </Price> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T16:00:00.000+09:00</end> <start>2012-02-01T15:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>180.5</highPrice> </Price> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T17:00:00.000+09:00</end> <start>2012-02-01T16:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>180.5</highPrice> </Price> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T18:00:00.000+09:00</end> <start>2012-02-01T17:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>209.5</highPrice> </Price> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T19:00:00.000+09:00</end> <start>2012-02-01T18:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>209.5</highPrice> </Price> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T20:00:00.000+09:00</end> <start>2012-02-01T19:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>209.5</highPrice> </Price> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T21:00:00.000+09:00</end> <start>2012-02-01T20:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>180.5</highPrice> </Price> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T22:00:00.000+09:00</end> <start>2012-02-01T21:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>180.5</highPrice> </Price> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T23:00:00.000+09:00</end> <start>2012-02-01T22:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>209.5</highPrice> </Price> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T23:59:00.000+09:00</end> <start>2012-02-01T23:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>131.4</highPrice> </Price> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T23:59:00.000+09:00</end> <start>2012-02-01T23:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>131.4</highPrice> </Price> </Pricing> </Pricings>";
        //    String xmlTmp = "<?xml version='1.0' encoding='utf-8' standalone='yes'?> <Pricings xmlns='http://www.kepco.or.kr/smartplace/profiles/events/Pricings#'> <Pricing> <CustomerAccount> <mRID>00803404143</mRID> </CustomerAccount> <MeterAsset> <mRID>01150724156</mRID> </MeterAsset> <priceUnit>WON</priceUnit> <CBLPUnit>kWh</CBLPUnit> <priceType>RTP</priceType> <pricingDate>2012-02-01+09:00</pricingDate> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T18:00:00.000+09:00</end> <start>2012-02-01T17:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>209.5</highPrice> </Price> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T19:00:00.000+09:00</end> <start>2012-02-01T18:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>209.5</highPrice> </Price> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T20:00:00.000+09:00</end> <start>2012-02-01T19:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>209.5</highPrice> </Price> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T21:00:00.000+09:00</end> <start>2012-02-01T20:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>180.5</highPrice> </Price> <Price> <timeStamp> <scheduleInterval> <end>2012-02-01T22:00:00.000+09:00</end> <start>2012-02-01T21:00:00.000+09:00</start> </scheduleInterval> </timeStamp> <unit>WON</unit> <CBLP>0.0</CBLP> <lowPrice>0.0</lowPrice> <highPrice>180.5</highPrice> </Price> </Pricing> </Pricings>";			
            mbeanProxy.cmdSendMessage("4", "000D6F0000E4AEC2", 0, 3, 6000, 0, 0, 0, 1, xmlTmp); //IHD
         //   mbeanProxy.cmdSetEnergyLevel("4", "000D6F0000D69CD2", "1"); //ACD
        }
        catch (Exception e) {
        	System.out.println("11"+e);
        	e.printStackTrace();
//            log.error(e,e);
        }
        finally {
            try {
                if (jmxc != null)
                    jmxc.close();
            }
            catch (Exception e){
            	System.out.println("22"+e);
//            	log.error(e,e);
            }
        }
    }
}


