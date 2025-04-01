package com.microservices.demo.kafka.admin.client;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.config.RetryConfigData;
import com.microservices.demo.kafka.admin.exception.KafkaClientException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class KafkaAdminClient {

    private final KafkaConfigData kafkaConfigData;
    private final RetryConfigData retryConfigData;
    private final AdminClient adminClient;
    private final RetryTemplate retryTemplate;
    private final WebClient webClient;

    public KafkaAdminClient(KafkaConfigData kafkaConfigData, RetryConfigData retryConfigData,
                            AdminClient adminClient, RetryTemplate retryTemplate, WebClient webClient) {
        this.kafkaConfigData = kafkaConfigData;
        this.retryConfigData = retryConfigData;
        this.adminClient = adminClient;
        this.retryTemplate = retryTemplate;
        this.webClient = webClient;
    }

    public void createTopics() {
        CreateTopicsResult createTopicsResult;
        try {
            createTopicsResult = retryTemplate.execute(this::doTopicsCreate);
        }
        catch (Exception e) {
            throw new KafkaClientException("Reached max number of retries to create Kafka topic(s)!", e);
        }
        checkCreatedTopics();
    }



    public void checkSchemaRegistry(){
        int retryCount = 1;
        Integer maxRetry = retryConfigData.getMaxAttempts();
        int multiplier = retryConfigData.getMultiplier().intValue();
        Long sleepTimeMs = retryConfigData.getSleepTimeMs();
        while (!getSchemaRegistryStatus().is2xxSuccessful()){
            checkMaxRetry(retryCount++,maxRetry);
            sleep(sleepTimeMs);
            sleepTimeMs*=multiplier;
        }
    }

    private void checkCreatedTopics() {
        Collection<TopicListing> topics = getTopics();
        int retryCount = 1;
        Integer maxRetry = retryConfigData.getMaxAttempts();
        int multiplier = retryConfigData.getMultiplier().intValue();
        Long sleepTimeMs = retryConfigData.getSleepTimeMs();
        for(String topic : kafkaConfigData.getTopicNamesToCreate()){
            while(!isTopicCreated(topic, topics)) {
                checkMaxRetry(retryCount++,maxRetry);
                sleep(sleepTimeMs);
                sleepTimeMs*=multiplier;
                topics=getTopics();
            }
        }
    }

    private HttpStatusCode getSchemaRegistryStatus() {
        try {
            return webClient
                    .method(HttpMethod.GET)
                    .uri(kafkaConfigData.getSchemaRegistryUrl())
                    .exchangeToMono(res -> Mono.just(res.statusCode()))
                    .block();
        } catch (Exception e) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }
    }

    private void sleep(Long sleepTimeMs) {
        try {
            Thread.sleep(sleepTimeMs);
        } catch (InterruptedException e) {
            throw new KafkaClientException("Error while sleeping while creating new kafka topics", e);
        }
    }

    private void checkMaxRetry(int retryCount, Integer maxRetry) {
        if(retryCount> maxRetry){
            throw new KafkaClientException("Reached max number of retries for creating Kafka topics!");
        }
    }

    private boolean isTopicCreated(String topic, Collection<TopicListing> topics) {
        if(topics == null){
            return false;
        }
        return topics.stream().anyMatch(topicListing -> topicListing.name().equals(topic));
    }

    private CreateTopicsResult doTopicsCreate(RetryContext retryContext) {
        List<String> topicNames = kafkaConfigData.getTopicNamesToCreate();
        log.info("Creating {} topic(s), attempt {}", topicNames.size(), retryContext.getRetryCount());
        List<NewTopic> kafkaTopics = topicNames.stream().map(topicName -> new NewTopic(topicName,
                        kafkaConfigData.getNumOfPartitions(),
                        kafkaConfigData.getReplicationFactor()))
                .toList();
        return adminClient.createTopics(kafkaTopics);
    }

    private Collection<TopicListing> getTopics(){
        Collection<TopicListing> topics;
        try {
            topics = retryTemplate.execute(this::getTopicNames);
        } catch (Exception e) {
            throw new KafkaClientException("Reached max number of retries to fetch Kafka topic(s)!" , e);
        }
        return topics;
    }

    private Collection<TopicListing> getTopicNames(RetryContext retryContext)
            throws ExecutionException, InterruptedException {
        log.info("Reading kafka topic {}, attempt {}",
                kafkaConfigData.getTopicNamesToCreate().toArray(), retryContext.getRetryCount());
        Collection<TopicListing> kafkaTopics = adminClient.listTopics().listings().get();
        if(kafkaTopics!=null) {
            kafkaTopics.forEach(topic -> log.debug("Topic with name : {}",topic.name()));
        }
        return kafkaTopics;
    }
}
