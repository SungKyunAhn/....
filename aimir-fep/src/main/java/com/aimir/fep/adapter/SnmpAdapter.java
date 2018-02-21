package com.aimir.fep.adapter;

import java.lang.management.ManagementFactory;
import java.util.Date;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ImportResource;

import com.aimir.fep.protocol.snmp.SnmpManagerAdapter;
import com.aimir.fep.util.DataUtil;

/**
 * @author Hansejin
 */
@SpringBootApplication
@EntityScan(basePackages={"com.aimir.model"})
@ComponentScan(basePackages={"com.aimir"}, excludeFilters= @ComponentScan.Filter(
        classes={FepAdapter.class, FepAdapter_IF4.class, FepProcessor.class, SMCPAdapter.class, FEPNewAdapter.class},
        type=FilterType.ASSIGNABLE_TYPE))
@ImportResource(value={"classpath:config/spring-snmp.xml"})
public class SnmpAdapter {
    private static Logger logger = LoggerFactory.getLogger(SnmpAdapter.class);

    public final static String SERVICE_DOMAIN = "Service";
    public final static String ADAPTER_DOMAIN = "Adapter";

    private String fepName;
    private String community;
    private String trapPort;
    private String subPort;

    public void init(String _community, String _trapPort, String _subPort) throws Exception {
        fepName = System.getProperty("name");
        System.setProperty("fepName", fepName);

        this.community = _community;
        this.trapPort = _trapPort;
        this.subPort = _subPort;

        logger.info("init::name={} community={} trapPort={} subPort={}",
                new Object[] { fepName, this.community, this.trapPort, this.subPort });
    }

    public void startService() throws Exception {
        logger.info("Register SnAdapter");

        registerSnAdapter();

        logger.info("[{}] Manager is Ready for Service...", fepName);
    }

    public void registerSnAdapter() throws Exception {
        String name = "SNMPAdapter";
        if (trapPort != null && !"".equals(trapPort)) {
            logger.info("Register SnAdapter.class");

            SnmpManagerAdapter adapter = new SnmpManagerAdapter();
            adapter.setPort(Integer.parseInt(trapPort));

            ObjectName adapterName = new ObjectName(ADAPTER_DOMAIN + ":name=" + name + ", port=" + trapPort);

            registerMBean(adapter, adapterName);
            adapter.start();

            logger.info("SNMPAdapter ready.");
        }
    }

    private void registerMBean(Object obj, ObjectName objName) throws Exception {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        mbs.registerMBean(obj, objName);
    }


    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        SpringApplication.run(SnmpAdapter.class, args);
    }
    
    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            BasicConfigurator.configure();
            Date startDate = new Date();
            long startTime = startDate.getTime();

            String community = "asm";
            String trapPort = "162";
            String subPort = "163";

            for (int i = 0; i < args.length; i += 2) {
                String nextArg = args[i];
                if (nextArg.startsWith("-community") ){
                    community = new String(args[i+1]);
                } else if (nextArg.startsWith("-trapPort")) {
                    trapPort = new String(args[i + 1]);
                } else if (nextArg.startsWith("-subPort")) {
                    subPort = new String(args[i + 1]);
                }
            }

            try{
                //Audit Target Name
                String fepName = System.getProperty("name");
                if (fepName == null || fepName.equals("")) {
                    System.setProperty("aimir.auditTargetName", "SNMP-Trapd");
                } else {
                    System.setProperty("aimir.auditTargetName", fepName);
                }

                //ctx
                DataUtil.setApplicationContext(ctx);
            } catch (Exception e){
                logger.error(e.getMessage(), e);
            }

            SnmpAdapter fep = new SnmpAdapter();
            try {
                fep.init(community,trapPort, subPort);
                fep.startService();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                System.exit(1);
            }

            long endTime = System.currentTimeMillis();
            logger.info("######## SNMPAdapter-Starting Elapse Time : {}", (endTime - startTime) / 1000.0f + "s \n");
        };
    }

}
