package com.leon.kafka;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import java.util.*;

/**
 * Created by ntcon on 6/11/2017.
 */
public class Consumer {

    private static Scanner in;

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.printf("Usage: %s <topicName> <groupId>\n", Consumer.class.getSimpleName());
            System.exit(-1);
        }

        String topicName = args[0];
        String groupID = args[1];
        in = new Scanner(System.in);

        ConsumerThread consumers = new ConsumerThread(topicName, groupID, -1);
        consumers.start();

        String line = "";
        while (!"exit".equals(line)) {
            line = in.next();
        }

        in.close();
        consumers.getKafkaConsumer().wakeup();
        consumers.join();
    }

    private static class ConsumerThread extends Thread {

        private String topicName;

        private String groupID;

        private long startingOffset;

        private KafkaConsumer<String, String> kafkaConsumer;

        ConsumerThread(String topicName, String groupID, long startingOffset) {
            this.topicName = topicName;
            this.groupID = groupID;
            this.startingOffset = startingOffset;
        }

        @Override
        public void run() {
            Properties configProperties = new Properties();
            configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupID);
            configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "simple");
            configProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

            kafkaConsumer = new KafkaConsumer<>(configProperties);
            kafkaConsumer.subscribe(Collections.singletonList(topicName), new ConsumerRebalanceListener() {
                @Override
                public void onPartitionsRevoked(Collection<TopicPartition> collection) {
                    System.out.printf("%s topic-partitions are revoked from this consumer\n", Arrays.toString(collection.toArray()));
                }

                @Override
                public void onPartitionsAssigned(Collection<TopicPartition> collection) {
                    System.out.printf("%s topic-partitions are assigned to this consumer\n", Arrays.toString(collection.toArray()));
                    if (startingOffset == 0) {
                        System.out.println("Setting offset to beginning");
                        kafkaConsumer.seekToBeginning(collection);
                    } else if (startingOffset == -1) {
                        System.out.println("Setting it to the end");
                        kafkaConsumer.seekToEnd(collection);
                    } else {
                        for (TopicPartition topicPartition : collection) {
                            System.out.println("Current offset is " + kafkaConsumer.position(topicPartition)
                                    + " ,committed offset is -> " + kafkaConsumer.committed(topicPartition));
                            System.out.println("Resetting offset to " + startingOffset);
                            kafkaConsumer.seek(topicPartition, startingOffset);
                        }
                    }
                }
            });

            try {
                while (true) {
                    ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
                    for (ConsumerRecord record : records) {
                        System.out.println(record.value());
                    }
                }
            } catch (WakeupException ex) {
                System.out.println("Exception caught " + ex.getMessage());
            } finally {
                kafkaConsumer.close();
                System.out.println("After closing KafkaConsumer");
            }
        }

        KafkaConsumer<String, String> getKafkaConsumer() {
            return this.kafkaConsumer;
        }

    }

}
