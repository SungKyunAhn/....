package com.aimir.fep.bems.sender;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.aimir.fep.protocol.fmp.exception.FMPEncodeException;
import com.aimir.fep.protocol.fmp.frame.service.EventData;
import com.aimir.model.mvm.ChannelConfig;
import com.aimir.model.mvm.MeteringLP;
import com.aimir.model.system.MeterConfig;
import com.aimir.util.DateTimeUtil;

/**
 * (BEMS) MeteringData 를 지정된 주소로 전송(TPC/IP)하는 기능. <br>
 * <br>
 * <b>Application context example</b> 
 * <xmp> 
 * <?xml version="1.0" encoding="UTF-8"?> 
 * <beans xmlns="http://www.springframework.org/schema/beans"
 * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 * xsi:schemaLocation="http://www.springframework.org/schema/beans
 * http://www.springframework.org/schema/beans/spring-beans-2.5.xsd">
 * 
 * <bean id="meteringDataSender" class="com.aimir.fep.bems.sender.MeteringDataSender" >
 * <property name="hostname" value="localhost"/>
 * <property name="port" value="4000"/>
 * </bean>
 * </beans> 
 * </xmp>
 * 
 * @author kskim
 */
public class MeteringDataSender extends DataSender {
    protected static Log log = LogFactory.getLog("meteringDataSender");
	
    private boolean isMeteringDTOList(Object obj){
		if (obj != null && obj instanceof List) {
			if (((List) obj).get(0) instanceof MeteringDTO) {
				return true;
			}
		}
		return false;
    }
    
    private boolean isMeteringLPList(Object obj){
		if (obj != null && obj instanceof List) {
			if (((List) obj).get(0) instanceof MeteringLP) {
				return true;
			}
		}
		return false;
    }
    
    @Override
	public boolean send(Object obj){
		if(obj instanceof MeteringDTO){
			MeteringDTO md = (MeteringDTO)obj;
			return send(md);
		}else if(isMeteringDTOList(obj)){
			List<MeteringDTO> list = (List<MeteringDTO>) obj;
			return send(list);
		}else if(obj instanceof List<?>[] ){
			List<?>[] oobj = (List<?>[]) obj;
			if(isMeteringLPList(oobj[0])){
				List<MeteringLP>[] lps = (List<MeteringLP>[]) obj;
				return send(lps);
			}
		}else{
			log.error(new IllegalArgumentException());
		}
		return false;
	}
    
    /**
     * 데이터 전송.
     * @param data
     * @return
     */
    private boolean send(MeteringDTO data){
        byte[] bdata = data.getBytes();
        IoBuffer buf = IoBuffer.allocate(bdata.length);
        buf.put(bdata);
        buf.flip();
		return this.send(buf);
    }
	
    /**
     * 데이터 전송
     * @param datas
     * @return
	 */
	private boolean send(List<MeteringDTO> datas){
		for (MeteringDTO meteringDTO : datas) {
			if(!this.send(meteringDTO))
				return false;
		}
		return true;
	}
	
	/**
	 * 데이터 전송
	 * @param addLPs
	 */
	private boolean send(List<MeteringLP>[] addLPs) {
	    try{
	        // 채널 매핑 맵을 생성한다. 아래 채널 매핑시 반복적인 처리를 피하기 위한 것이다.
	        Map<Integer, Integer> channelMap = new HashMap<Integer, Integer>();
	        
	        // 채널 정보를 가져온다.
		    if (addLPs != null && addLPs.length > 0 && addLPs[0].size() > 0) {
		        MeteringLP lp = addLPs[0].get(0);
		        
		        if (lp.getMeter() != null) {
		            Set<ChannelConfig> chset = ((MeterConfig)lp.getMeter().getModel().getDeviceConfig()).getChannels();
		            
		            ChannelConfig cc = null;
		            for (Iterator<ChannelConfig> i = chset.iterator(); i.hasNext();) {
		                cc = i.next();
		                channelMap.put(cc.getChannelIndex(), Integer.parseInt(cc.getChannel().getChannelValue()));
		            }
		        }
		    }
	        //소수점 자릿수 설정.
	        Method[] methods = MeteringLP.class.getDeclaredMethods();
		
	        List<MeteringDTO> mtrDatas = new ArrayList<MeteringDTO>();
	        String mm = null;
	        for (int i=0;i<addLPs.length;i++) {
	            List<MeteringLP> lpList = addLPs[i];
	            if(lpList == null){
	                log.debug("lp is null.");
	                continue;
	            }
	            for (MeteringLP lp : lpList) {

	                // meter serial
	                StringBuilder sb = new StringBuilder();
	                sb.append(lp.getMDevId());
	                sb.setLength(20);
	                String fixedSerial = sb.toString();

	                Double baseValue = lp.getValue()==null ? 0:lp.getValue();
				
                    /**
                     * 해당 분(minute) 까지의 합
                     * ex> 30분까지의 값 = baseValue + ( ... 15분값 + 30분 )
                     */
	                Number nCumul = baseValue;
	                log.debug(String.format("device id : [%s]",lp.getMDevId()));
	                for (Method method : methods) {
	                    if (method.getName().startsWith("getValue_")) {
                            try {
                                // 분 정보를 읽는다.
                                mm = method.getName().replaceAll("[^0-9]", "");

                                Object obj = method.invoke(lp);

                                // 값이 없으면 건너뛴다.
                                if (obj == null) {
                                    continue;
                                }

                                if (obj instanceof Number) {
								
                                    Number value = (Number) obj;
								
                                    nCumul = nCumul.doubleValue() + value.doubleValue();
                                    String sCumul = String.format("%12.5f", nCumul);
                                    byte[] bCumul = sCumul.getBytes();

                                    // 데이터 설정
                                    MeteringDTO mtrData = new MeteringDTO();

                                    // 0:정기검침, 1:온디맨드, 2:실패검침
                                    mtrData.setMeteringType((byte) lp
                                            .getMeteringType().toString()
                                            .charAt(0));

                                    mtrData.setYyyymmdd(lp.getYyyymmdd()
                                            .getBytes());
                                    mtrData.setHh(lp.getYyyymmddhh().substring(8,10).getBytes());
                                    mtrData.setMm(mm.getBytes());
                                    mtrData.setDst((byte) (lp.getDst() == 1 ? '1': '0'));
//                                    mtrData.setEnergyType((byte) '1');
                                    if (lp.getChannel() != null && lp.getChannel() < 1000) {
                                        mtrData.setMeterChannel(String.format(
                                                "%04d",
                                                channelMap.get(lp.getChannel()) != null? channelMap.get(lp.getChannel()):lp.getChannel())
													.getBytes());
                                    }
                                    else {
                                        log.debug("cannot found channel - " + lp.getChannel());
                                    }

                                    //log.debug(String.format("channel - [%s]", new String(mtrData.getMeterChannel())));
								
                                    String strValue = String.format("%9.5f", value);
                                    mtrData.setMeteringData(strValue.getBytes());
                                    mtrData.setCumulativeData(bCumul);
                                    mtrData.setMeterSerial(fixedSerial.getBytes());
//                                    mtrData.setProcessTime(DateTimeUtil
//                                            .getCurrentDateTimeByFormat(
//                                                    "yyyyMMddHHmmss").getBytes());

                                    log.debug(String.format(
                                            "%s %s,ch=%d, baseValue=%f, value=%s, cumul=%s", lp
                                            .getYyyymmddhh(), mm, lp
                                            .getChannel(),lp.getValue(), strValue,sCumul));
                                    mtrDatas.add(mtrData);
                                }
                            } catch (Exception e) {
                                log.error(e, e);
                                continue;
                            }
	                    }
	                }
	            }
	        }
		
	        // 데이터 전송
	        if(this.connect()){
	            return this.send(mtrDatas);
	        }
	    }catch(Exception e){
	        log.error(e,e);
	    }
	    return false;
	}
}
