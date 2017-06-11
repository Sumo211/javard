package com.leon.kafka;

import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;

/**
 * Created by ntcon on 6/11/2017.
 */
public class ZookeeperUtils {

    public static void main(String[] args) throws Exception {
        ZooKeeper zooKeeper = new ZooKeeper("localhost:2181", 10000, null);
        ZkClient zkClient = new ZkClient("localhost:2181", 10000);

        List<String> topics = zooKeeper.getChildren("/brokers/topics", false);
        for (String topic : topics) {
            zkClient.deleteRecursive(ZkUtils.getTopicPath(topic));
        }
    }

}
