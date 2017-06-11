package com.leon.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.Scanner;

/**
 * Created by ntcon on 6/11/2017.
 */
public class Producer {

    private static Scanner in;

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Please specify the name of topic.");
            System.exit(-1);
        }

        String topicName = args[0];
        in = new Scanner(System.in);
        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer");
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        configProperties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, CountryPartitioner.class.getCanonicalName());
        configProperties.put("partition.0", "USA");
        configProperties.put("partition.1", "India");

        org.apache.kafka.clients.producer.Producer producer = new KafkaProducer<String, String>(configProperties);
        String line = in.nextLine();
        while (!"exit".equals(line)) {
            ProducerRecord<String, String> record = new ProducerRecord<>(topicName, line);
            producer.send(record, (recordMetadata, e) -> {
                System.out.println("Message is sent to topic -> " + recordMetadata.topic()
                        + " ,partition -> " + recordMetadata.partition()
                        + " ,stored at offset -> " + recordMetadata.offset());
            });
            line = in.nextLine();
        }

        in.close();
        producer.close();
    }

}
