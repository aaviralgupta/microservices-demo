package com.microservices.demo.twitter.to.kafka.service.transformer;

import com.microservices.demo.avro.model.TwitterAvroModel;
import org.springframework.stereotype.Component;
import twitter4j.Status;

@Component
public class TwitterStatusToAvroTransformer {

    public TwitterAvroModel transform(Status status){
        return TwitterAvroModel.newBuilder()
                .setId(status.getId())
                .setCreatedAt(status.getCreatedAt().toInstant())
                .setText(status.getText())
                .setUserId(status.getUser().getId())
                .build();
    }
}
