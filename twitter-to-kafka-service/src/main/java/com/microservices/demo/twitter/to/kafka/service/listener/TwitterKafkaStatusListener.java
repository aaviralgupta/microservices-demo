package com.microservices.demo.twitter.to.kafka.service.listener;

import com.microservices.demo.avro.model.TwitterAvroModel;
import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.kafka.producer.service.KafkaProducer;
import com.microservices.demo.twitter.to.kafka.service.transformer.TwitterStatusToAvroTransformer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.StatusAdapter;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class TwitterKafkaStatusListener extends StatusAdapter {

    private final KafkaConfigData kafkaConfigData;
    private final KafkaProducer<Long, TwitterAvroModel> kafkaProducer;
    private final TwitterStatusToAvroTransformer twitterStatusToAvroTransformer;

    public TwitterKafkaStatusListener(KafkaConfigData kafkaConfigData,
                                      KafkaProducer<Long, TwitterAvroModel> kafkaProducer,
                                      TwitterStatusToAvroTransformer twitterStatusToAvroTransformer) {
        this.kafkaConfigData = kafkaConfigData;
        this.kafkaProducer = kafkaProducer;
        this.twitterStatusToAvroTransformer = twitterStatusToAvroTransformer;
    }

    @Override
    public void onStatus(Status status) {
        log.info("Twitter status with text : {}", status.getText());
        TwitterAvroModel twitterAvroModel = twitterStatusToAvroTransformer.transform(status);
        kafkaProducer.send(kafkaConfigData.getTopicName(), status.getId(), twitterAvroModel, getKafkaCallback());
    }

    private BiConsumer<SendResult<Long, TwitterAvroModel>, Throwable> getKafkaCallback() {
        return (result, ex)-> {
            if(ex == null){
                RecordMetadata metadata = result.getRecordMetadata();
                log.info("Received successfully response from kafka for Topic : {} " +
                                "Partition :{} Offset: {} Timestamp:{}",
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        metadata.partition());
            }
            else{
                log.error("Error while sending message to kafka fot twitter!", ex);
            }
        };
    }
}
