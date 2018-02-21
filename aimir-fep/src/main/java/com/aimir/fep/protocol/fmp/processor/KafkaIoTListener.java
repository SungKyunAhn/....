package com.aimir.fep.protocol.fmp.processor;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;

import com.aimir.fep.iot.domain.resources.Resource;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
//import com.aimir.fep.util.iot.ResourceDeSerializer;

public class KafkaIoTListener
{
    private static Log log = LogFactory.getLog(KafkaIoTListener.class);
      
    class KafkaIoTListenerThread extends Thread {
        private String topicName;
        
        KafkaIoTListenerThread(String topicName) {
            this.topicName = topicName;
        }
        
        @Override
        public void run() {
        	log.debug("");
        	log.debug("");
        	log.debug("");
        	log.debug("######### KafkaIoTListener start ~~~ ["+topicName+"] ############");
        	
            MessageListenerContainer jcontainer = messageListenerContainer(topicName);
            jcontainer.setupMessageListener(new MessageListener<String, Resource>() {
            	
                @Override
                public void onMessage(ConsumerRecord<String, Resource> record) {
                	try {
                        /*convert json message to message*/
                        //StringJsonMessageConverter converter = new StringJsonMessageConverter();
                        //Message msg = (Message)converter.toMessage(record, null, Message.class).getPayload();
                        //CommLog commLog = makeCommLog(msg);
                        try {
                        	log.debug("### KafkaIoTListener Message [" + record.value() +"]");
                        	send(topicName, record.value());
                            //commLog.setResult(CommonConstants.DefaultCmdResult.SUCCESS.getMessage());
                        	
                        	/*try (KafkaConsumer<String, User> consumer = new KafkaConsumer<>(props)) {
                        	    consumer.subscribe(Collections.singletonList(topic));
                        	    while (true) {
                        	        ConsumerRecords<String, User> messages = consumer.poll(100);
                        	        for (ConsumerRecord<String, User> message : messages) {
                        	          System.out.println("Message received " + message.value().toString());
                        	        }
                        	    }
                        	} catch (Exception e) {
                        	    e.printStackTrace();
                        	}*/
                        }
                        catch (Exception e) {
                            //commLog.setResult(CommonConstants.DefaultCmdResult.FAILURE.getMessage());
                            //commLog.setErrorReason(e.getMessage());
                            log.error(e, e);
                        }
                        //CommLogger commLogger = DataUtil.getBean(CommLogger.class);
                        //commLogger.sendLog(commLog);
                    }
                    catch (Exception e) {
                        log.error(e, e);
                    }
                }
            });
            jcontainer.start();
        }
    }
    
    /**
     * constructor
     *
     * @throws Exception
     */
    public KafkaIoTListener() throws Exception
    {
        if (Boolean.parseBoolean(FMPProperty.getProperty("kafka.enable"))) {
            //new KafkaIoTListenerThread(ProcessorHandler.SERVICE_EVENT_1_2).start();
            //new KafkaIoTListenerThread(ProcessorHandler.SERVICE_EVENT).start();
            //new KafkaIoTListenerThread(ProcessorHandler.SERVICE_MEASUREMENTDATA).start();
            //new KafkaIoTListenerThread(ProcessorHandler.SERVICE_DATAFILEDATA).start();
            new KafkaIoTListenerThread(ProcessorHandler.SERVICE_IOT_MDDATA).start();
        }
    }

    public MessageListenerContainer messageListenerContainer(String groupName) {
        ConcurrentMessageListenerContainer<Integer, String> container = 
                new ConcurrentMessageListenerContainer<Integer, String>(consumerFactory(groupName), new ContainerProperties(groupName));
        int concurrency = 1;
        if (groupName.equals(ProcessorHandler.SERVICE_IOT_MDDATA))
            concurrency = Integer.parseInt(
                    FMPProperty.getProperty("IoTProcessor.thread.poolSize"));
        
        /*if (groupName.equals(ProcessorHandler.SERVICE_MEASUREMENTDATA))
            concurrency = Integer.parseInt(
                    FMPProperty.getProperty("MDProcessor.thread.poolSize"));
        else if (groupName.equals(ProcessorHandler.SERVICE_EVENT_1_2)
                || groupName.equals(ProcessorHandler.SERVICE_EVENT))
            concurrency = Integer.parseInt(
                    FMPProperty.getProperty("EventProcessor.thread.poolSize"));
        else if (groupName.equals(ProcessorHandler.SERVICE_DATAFILEDATA))
            concurrency = Integer.parseInt(
                    FMPProperty.getProperty("DFProcessor.thread.poolSize"));*/
        
        container.setConcurrency(concurrency);
        return container;
    }
    
    public ConsumerFactory<Integer, String> consumerFactory(String groupName) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupName);
        // props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, 
        		FMPProperty.getProperty("kafka.iot.broker.list"));
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, 
                Boolean.parseBoolean(FMPProperty.getProperty("kafka.consumer.auto.commit")));
        // props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 
        //         Integer.parseInt(FMPProperty.getProperty("kafka.consumer.auto.commit.interval")));
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 4*1024*1024);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        //props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ResourceDeSerializer.class);
        // props.put("isolation.level", "read_commited");
        // props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, "org.apache.kafka.clients.consumer.RoundRobinAssignor");

        return new DefaultKafkaConsumerFactory<>(props);
    }
    
    private void send(String dataType, Object obj) {
    	try {
    		if (dataType.equals(ProcessorHandler.SERVICE_IOT_MDDATA)) {
    			IoTSensorDataProcessor iot = DataUtil.getBean(IoTSensorDataProcessor.class);
                iot.processing(obj);
    		}
    	} catch (Exception e) {
    		log.error(e, e);
    	}
    }
    
    
    //private void processing(Message msg, CommLog commLog)
    /*private void processing(Object obj)
    throws Exception
    {
        if (msg.getDataType().equals(ProcessorHandler.SERVICE_DATA)) {
            receivedServiceDataFrame(msg, commLog);
            if (msg.getFilename() != null && !"".equals(msg.getFilename())) {
                log.debug("DELETE File[" + msg.getFilename() + "]");
                new File(msg.getFilename()).delete();
            }
        }
        else if (msg.getDataType().equals(ProcessorHandler.SERVICE_MEASUREMENTDATA)) {
            MDProcessor mp = DataUtil.getBean(MDProcessor.class);
            MDData sd = new MDData();
            sd.setCnt(new WORD(1));
            // byte[] b = new byte[msg.getData().length - 2];
            // System.arraycopy(msg.getData(), 2, b, 0, b.length);
            sd.setMdData(msg.getData());
            sd.setNS(msg.getNameSpace());
            sd.setIpAddr(msg.getSenderIp());
            sd.setMcuId(msg.getReceiverId());
            mp.processing(sd, commLog);
            if (msg.getFilename() != null && !"".equals(msg.getFilename())) {
                log.debug("DELETE File[" + msg.getFilename() + "]");
                new File(msg.getFilename()).delete();
            }
        }
        else if (msg.getDataType().equals(ProcessorHandler.SERVICE_PLC)) {
            receivedPLCDataFrame(msg, commLog);
        }
        else if (msg.getDataType().equals(ProcessorHandler.SERVICE_DATAFILEDATA)) {
            DFProcessor dp = DataUtil.getBean(DFProcessor.class);
            dp.processing(msg.getFilename(), commLog);
        }
        else if (msg.getDataType().equals(ProcessorHandler.SERVICE_IOT_MDDATA)) {
    	}
    	log.debug("### processing : " + obj);
    	IoTSensorDataProcessor iot = DataUtil.getBean(IoTSensorDataProcessor.class);
    	iot.processing(obj);
    }*/
    
    /*private void receivedServiceDataFrame(Message msg, CommLog commLog) throws Exception
    {       
        String nameSpace = msg.getNameSpace();
        String ipaddr = msg.getSenderIp();
        IoBuffer buf = IoBuffer.wrap(msg.getData());
        ServiceDataFrame frame = (ServiceDataFrame)GeneralDataFrame.decode(nameSpace, buf);
        
        ServiceData sd = ServiceData.decode(nameSpace, frame, ipaddr);
        
        if(sd != null)
        {
            log.debug("\nServiceData :" + sd.getType() );
            log.debug("\nRECEIVED SERVICE DATA \n"+sd);
            // log.debug(sd);
            if (sd instanceof EventData) {
                EventProcessor ep = DataUtil.getBean(EventProcessor.class);
                ep.processing(sd, commLog);
            }
            else if (sd instanceof EventData_1_2) {
                EventProcessor_1_2 ep_1_2 = DataUtil.getBean(EventProcessor_1_2.class);
                ep_1_2.processing(sd, commLog);
            }
            else if (sd instanceof DFData) {
                DFProcessor dp = DataUtil.getBean(DFProcessor.class);
                dp.processing(sd, commLog);
            }
            else if (sd instanceof MDData) {
                MDProcessor mp = DataUtil.getBean(MDProcessor.class);
                mp.processing(sd, commLog);
            }
            else //if (sd instanceof MDData) {
            if (sd != null){
            	IoTSensorDataProcessor iot = DataUtil.getBean(IoTSensorDataProcessor.class);
                iot.processing(sd);
            }
        }
        else {
            log.debug("ServiceData is null");
        }
    }*/

    /*	Log by ask
    private CommLog makeCommLog(Message msg) {
        CommLog commLog = new CommLog();
        
        commLog.setSenderIp(msg.getSenderIp());
        commLog.setSenderId(msg.getSenderId());
        commLog.setReceiverId(msg.getReceiverId());
        commLog.setReceiverTypeCode(CommonConstants.getSenderReceiver("1")); // FEP
        commLog.setSendBytes((int)msg.getSendBytes()); //ENQ+ACK
        commLog.setRcvBytes((int)msg.getRcvBytes());//included EOT that received
        commLog.setStartDateTime(msg.getStartDateTime());
        commLog.setStartDate(msg.getStartDateTime().substring(0,8));
        commLog.setStartTime(msg.getStartDateTime().substring(8,14));
        commLog.setEndTime(msg.getEndDateTime());
        commLog.setInterfaceCode(CommonConstants.getInterface(Interface.IF4.name()));
        commLog.setReceiver(System.getProperty("fepName"));
        // Communication Success
        commLog.setCommResult(1);
        log.debug("startTime["+commLog.getStartTime()+"] endTime["+commLog.getEndTime()+"]");
        try {
            long startLongTime = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(commLog.getStartDateTime()).getTime();
            long endLongTime = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(commLog.getEndTime()).getTime();
            
            if(endLongTime - startLongTime > 0) {
                commLog.setTotalCommTime((int)(endLongTime - startLongTime));
            }
            else {
                commLog.setTotalCommTime(0);
            }
        }
        catch (Exception e) {
            log.warn(e);
        }
        return commLog;
    }*/
}
