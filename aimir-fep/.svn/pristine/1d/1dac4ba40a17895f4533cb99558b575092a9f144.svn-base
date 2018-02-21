package com.aimir.fep.protocol.fmp.processor;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

public class RoundRobinPartitioner implements Partitioner {
    private static final Log log = LogFactory.getLog(RoundRobinPartitioner.class);

    final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void configure(Map<String, ?> configs) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        int partitions = cluster.partitionCountForTopic(topic);
        
        int partitionId = counter.incrementAndGet() % partitions;
        if (counter.get() > 65536) {
           counter.set(0);
        }
        log.debug("TOPIC[" + topic + "] PARTITION_ID[" + partitionId + "]");
        return partitionId; 
    }
    
    @Override
    public void close() {
        // TODO Auto-generated method stub
        
    }
}
