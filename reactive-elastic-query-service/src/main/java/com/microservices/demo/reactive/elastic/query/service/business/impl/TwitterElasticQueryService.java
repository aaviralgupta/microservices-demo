package com.microservices.demo.reactive.elastic.query.service.business.impl;

import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import com.microservices.demo.elastic.query.service.common.transformer.ElasticToResponseModelTransformer;
import com.microservices.demo.reactive.elastic.query.service.business.ElasticQueryService;
import com.microservices.demo.reactive.elastic.query.service.business.ReactiveElasticQueryClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class TwitterElasticQueryService implements ElasticQueryService {

    private final ReactiveElasticQueryClient<TwitterIndexModel> reactiveElasticQueryClient;
    private final ElasticToResponseModelTransformer transformer;

    public TwitterElasticQueryService(ReactiveElasticQueryClient<TwitterIndexModel> reactiveElasticQueryClient, ElasticToResponseModelTransformer transformer) {
        this.reactiveElasticQueryClient = reactiveElasticQueryClient;
        this.transformer = transformer;
    }


    @Override
    public Flux<ElasticQueryServiceResponseModel> getDocumentByText(String text) {
        log.info("Querying elasticsearch for documents by text: {}", text);
        return reactiveElasticQueryClient.getIndexModelByText(text)
                .map(transformer::getResponseModel);
    }
}
