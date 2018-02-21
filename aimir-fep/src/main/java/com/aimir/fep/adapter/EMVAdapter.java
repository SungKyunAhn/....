/**
 *
 * (관련용어)<br>
 * <p>FEP (Front End Processor)</p>
 * <p>FMP (FEP and MCU Protocol)</p>
 * <p>MRP (Meter Read Protocol)</p>
 *
 */

/**
 * (@)# EMVAdapter.java
 *
 * 2015. 4. 14.
 *
 * Copyright (c) 2013 NURITELECOM, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * NURITELECOM, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with NURITELECOM, Inc.
 *
 * For more information on this product, please see
 * http://www.nuritelecom.co.kr
 *
 */
package com.aimir.fep.adapter;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.Date;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.aimir.fep.protocol.emnv.exception.EMnVSystemException;
import com.aimir.fep.protocol.emnv.exception.EMnVSystemException.EMnVExceptionReason;
import com.aimir.fep.protocol.emnv.server.EMnVAdapter;
import com.aimir.fep.util.DataUtil;

/**
 * @author simhanger
 */
public class EMVAdapter {
	private static Logger logger = LoggerFactory.getLogger(EMVAdapter.class);

	public final static String SERVICE_DOMAIN = "Service";
	public final static String ADAPTER_DOMAIN = "Adapter";

	private String fepName;
	private String commPort;
	private String bypassPort;
	private String mnvPort;
	private static Class<?> clazz;
	private static Object obj;

	public void init(String commPort, String bypassPort, String mnvPort) throws Exception {
		fepName = System.getProperty("name");
		System.setProperty("fepName", fepName);

		this.commPort = commPort;
		this.bypassPort = bypassPort;
		this.mnvPort = mnvPort;

		logger.info("name={} commPort={} bypassPort={} mnvPort={}", new Object[] { fepName, this.commPort, this.bypassPort, mnvPort });

		/**
		 * 보안 모듈 초기화.
		 * 보안 모듈 사용시 aimir-fep-exec의 pom-emnv-h.xml 파일에아래처럼 dependency를 추가하고
		 * .m2 repository에 보안모듈라이브러리를 위치시켜야 한다.
		 * 
			    <!-- MagicJCrypto 라이브러리는 Maven 라이브러리 폴더에 수동으로 넣어줘야함. -->
			    <!-- ex) ..\.m2\repository\com\dreamsecurity\MagicJCrypto\v1.0.0.0 -->
			    <dependency>
				  <groupId>com.dreamsecurity</groupId>
				  <artifactId>MagicJCrypto</artifactId>
				  <version>v1.0.0.0</version>
				</dependency>   
		 * 
		 */
		try {
			clazz = Class.forName("com.dreamsecurity.kcmv.jce.provider.MagicJCryptoProvider");
			obj = clazz.newInstance();

			Method method = clazz.getDeclaredMethod("installProvider");
			method.invoke(obj);

			logger.info("JCE Provider install ok..");
		} catch (Exception e) {
			logger.info("JCE Provider install fail..");
			throw new EMnVSystemException(EMnVExceptionReason.INIT_ERROR);
		}
	}

	public void startService() throws Exception {
		logger.info("Register EMVAdapter");
		registerEMVAdapter();

		logger.info("[{}] FEPh is Ready for Service...", fepName);
	}

	private void registerEMVAdapter() throws Exception {
		String name = "EMnVAdapter";
		if (mnvPort != null && !"".equals(mnvPort)) {
			logger.info("Register MnVAdapter");

			EMnVAdapter adapter = new EMnVAdapter();
			adapter.setPort(Integer.parseInt(mnvPort));

			ObjectName adapterName = new ObjectName(ADAPTER_DOMAIN + ":name=" + name + ", port=" + mnvPort);

			registerMBean(adapter, adapterName);
			adapter.start();

			logger.info("EMVAdapter ready.");
		}
	}

	private void registerMBean(Object obj, ObjectName objName) throws Exception {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		mbs.registerMBean(obj, objName);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Date startDate = new Date();
		long startTime = startDate.getTime();

		String commPort = "8000";
		String bypassPort = "8900";
		String mnvPort = "8198";

		for (int i = 0; i < args.length; i++) {
			logger.debug("### args[{}] => {}", i, args[i]);
		}

		for (int i = 0; i < args.length; i += 2) {
			String nextArg = args[i];
			if (nextArg.startsWith("-commPort")) {
				commPort = new String(args[i + 1]);
			} else if (nextArg.startsWith("-bypassPort")) {
				bypassPort = new String(args[i + 1]);
			} else if (nextArg.startsWith("-mnvPort")) {
				mnvPort = new String(args[i + 1]);
			}
		}

		try {
			/**
			 * Audit Target Name 설정.
			 */
			String fepName = System.getProperty("name");
			if (fepName == null || fepName.equals("")) {
				System.setProperty("aimir.auditTargetName", "EMnVA-FEPh");
			} else {
				System.setProperty("aimir.auditTargetName", fepName);
			}

			ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[] { "/config/spring-feph.xml" });
			DataUtil.setApplicationContext(applicationContext);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		EMVAdapter fep = new EMVAdapter();
		try {
			fep.init(commPort, bypassPort, mnvPort);
			fep.startService();
		} catch (Exception e) {
			try {
				Method method = clazz.getDeclaredMethod("removeProvider");
				method.invoke(obj);
				logger.info("JCE Provider remove ok..");
			} catch (Exception e1) {
				logger.error("Exception-", e1);
			}

			logger.error(e.getMessage(), e);
			System.exit(1);
		}

		long endTime = System.currentTimeMillis();
		logger.info("######## EMVAdapter Starting Elapse Time : {}", (endTime - startTime) / 1000.0f + "s");
	}

}
