package com.aimir.fep.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Message;
import com.aimir.util.TimeUtil;

public class MQSend implements Runnable {
    private static Log log = LogFactory.getLog(MQSend.class);
    private String fileName = "";

    
    public String getFileName() {
		return fileName;
	}

	public void setFilePath(String fileName) {
		this.fileName = fileName;
	}

	public static void main(String[] args) {
    	String activemq = "localhost";
    	String filePath = "";
        int threadCount = 10;

    	if(args.length > 2 && !"".equals(args[0]) && args[0] != null) {
    		log.info("args["+args[0]+"]");
    		activemq = args[0];
    		filePath = args[1];
    		threadCount = Integer.parseInt(args[2]);
    	}    	
    	String amqDomain = "org.apache.activemq";
    	String runningQueue = null;
    	
    	String queueName = "ServiceData.DFData";
    	String mqIp = null;
    	
        String filename = null;
        int i = 1;
		ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "/config/spring-fep-schedule.xml" });
		DataUtil.setApplicationContext(ctx);

		File dir = new File(filePath);
        if (!dir.exists()) {
            log.error("["+filePath+"]Directory not found");
            return;
        }
        File[] files = dir.listFiles();
        
        List<File> fileList = new ArrayList<File>();
        for (File f : files) {
            fileList.add(f);
        }
        
        Collections.sort(fileList, (f1, f2) -> {return (int)(f1.lastModified() - f2.lastModified());});
        
		ExecutorService pool = Executors.newFixedThreadPool(threadCount);
		try
		{
		    for (File f : fileList.toArray(new File[0])) {
		        log.info("[" + (i++) + "/" + files.length +"] filename[" + f.getName() + "]");
		        MQSend mqsend = new MQSend();
		        mqsend.setFilePath(f.getAbsolutePath());
		        pool.execute(mqsend);
		    }
		    
		    pool.awaitTermination(120, TimeUnit.SECONDS);
		    
		    // pool.shutdown();
		} catch (Exception ex)
        {
        	pool.shutdown();
            log.error("failed ", ex);
        }
    }

	@Override
	public void run() {

	    ProcessorHandler handler = DataUtil.getBean(ProcessorHandler.class);        
	    boolean kafkaEnable = Boolean.parseBoolean(FMPProperty.getProperty("kafka.enable"));
	    //FileChannel inChannel = null;
    	log.info("Filename[" + fileName + "]");
	    try {
	        if(kafkaEnable){
	            if (fileName.lastIndexOf("dat") != -1 || fileName.lastIndexOf("zlib") != -1){
                	
                    //inChannel = new RandomAccessFile(fileName, "r").getChannel();
                    //MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
                    //byte[] data = new byte[(byte)inChannel.size()];
                    //buffer.get(data, 0, data.length);
                	Message message = new Message();
                    message.setFilename(fileName);                    
                    message.setSenderIp("localhost");
                    message.setReceiverIp("localhost");
                    message.setSenderId("1234");
                    message.setReceiverId("4321");
                    message.setSendBytes(0L);
                    message.setRcvBytes(0L);
                    message.setStartDateTime(TimeUtil.getCurrentTime());
                    message.setEndDateTime(TimeUtil.getCurrentTime());
                    message.setTotalCommTime(0);
                    message.setDataType(ProcessorHandler.SERVICE_DATAFILEDATA);
                    message.setNameSpace("SP");
                    //message.setData(data);
                    
                    handler.putServiceData(ProcessorHandler.SERVICE_DATAFILEDATA, message);
                }
                
        	}else{
                if (fileName.lastIndexOf("dat") != -1 || fileName.lastIndexOf("zlib") != -1)
                    handler.putServiceData(ProcessorHandler.SERVICE_DATAFILEDATA, fileName);
        	}

        }
        catch (Exception e) {
            log.error(e, e);
        } 
		
	}
}
