package com.aimir.fep.bems.sender;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import com.aimir.fep.BaseTestCase;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.saver.DLMSMDSaver;
import com.aimir.util.DateTimeUtil;

public class MeteringDataSenderTest extends BaseTestCase {
//	static {
//        DataUtil.appCtx = new ClassPathXmlApplicationContext(new String[]{"/config/spring.xml"});
//    }

	
	@Test
	public void threadTest(){
		
		final ApplicationContext ac = this.applicationContext;
		
		Thread t1 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				MeteringDataSender sender = (MeteringDataSender) ac.getBean("meteringDataSender");
				synchronized(sender){
//						sender.synchSend(1);
				}
			}
		});
		t1.start();
		
		Thread t2 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				MeteringDataSender sender = (MeteringDataSender) ac.getBean("meteringDataSender");
				synchronized(sender){
//						sender.synchSend(2);
					
				}
			}
		});
		t2.start();
		
		
		Thread t3 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				MeteringDataSender sender = (MeteringDataSender) ac.getBean("meteringDataSender");
				synchronized(sender){
//						sender.synchSend(3);
				}
			}
		});
		t3.start();
		
		try {
			while(true){
				Thread.sleep(10000);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Ignore
	public void t() throws Exception{
		DLMSMDSaver d = this.applicationContext.getBean(DLMSMDSaver.class);
		
		AbstractMDSaver s = d;
		
		
		s.addMeteringData(null, null, null, null);
		
		
		
	}
	
	@Ignore
	public void sendTest(){
		MeteringDTO dto = new MeteringDTO();
		
		
		String yyyyMMddHHmmss = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
		
		dto.setYyyymmdd(yyyyMMddHHmmss.substring(0, 8).getBytes());
		dto.setHh(yyyyMMddHHmmss.substring(8, 10).getBytes());
		dto.setMm(yyyyMMddHHmmss.substring(10, 12).getBytes());
		dto.setDst((byte)'1');
//		dto.setEnergyType((byte)'1');
		dto.setMeteringType((byte)'0');
		dto.setMeterSerial("12345678901234567890".getBytes());
		dto.setMeterChannel("12".getBytes());
		dto.setMeteringData("666666".getBytes());
//		dto.setProcessTime(yyyyMMddHHmmss.getBytes());
		
		MeteringDataSender sender = this.applicationContext.getBean(MeteringDataSender.class);
		sender.connect();
		sender.send(dto);
		sender.disconnect();
	}
}
