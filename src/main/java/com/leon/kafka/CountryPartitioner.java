package com.leon.kafka;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ntcon on 6/11/2017.
 */
public class CountryPartitioner implements Partitioner {

    private static Map<String, Integer> countryToPartitionMap;

    @Override
    public int partition(String s, Object o, byte[] bytes, Object o1, byte[] bytes1, Cluster cluster) {
        //List<PartitionInfo> partitions = cluster.availablePartitionsForTopic(s);
        String value = (String) o1;
        String countryName = ((String) o1).split(":")[0];
        if (countryToPartitionMap.containsKey(countryName)) {
            return countryToPartitionMap.get(countryName);
        } else {
            int noOfPartitions = cluster.topics().size();
            return value.hashCode() % noOfPartitions + countryToPartitionMap.size();
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {
        System.out.println("Inside CountryPartitioner.configure " + map);
        countryToPartitionMap = new HashMap<>();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            if (entry.getKey().startsWith("partition.")) {
                String keyName = entry.getKey();
                String value = (String) entry.getValue();
                System.out.println(keyName.substring(10));
                int partitionID = Integer.valueOf(keyName.substring(10));
                countryToPartitionMap.put(value, partitionID);
            }
        }
        //System.out.println("Partition Map: " + countryToPartitionMap);
    }

}

