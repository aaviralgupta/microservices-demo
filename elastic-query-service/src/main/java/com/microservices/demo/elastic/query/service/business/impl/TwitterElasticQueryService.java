package com.microservices.demo.elastic.query.service.business.impl;

import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.demo.elastic.query.client.service.ElasticQueryClient;
import com.microservices.demo.elastic.query.service.business.ElasticQueryService;
import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import com.microservices.demo.elastic.query.service.model.assembler.ElasticQueryServiceResponseModelAssembler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TwitterElasticQueryService implements ElasticQueryService {

    private final ElasticQueryServiceResponseModelAssembler assembler;
    private final ElasticQueryClient<TwitterIndexModel> elasticQueryClient;

    public TwitterElasticQueryService(ElasticQueryServiceResponseModelAssembler assembler,
                                      ElasticQueryClient<TwitterIndexModel> elasticQueryClient) {
        this.assembler = assembler;
        this.elasticQueryClient = elasticQueryClient;
    }

    @Override
    public ElasticQueryServiceResponseModel getDocumentById(String id) {
        log.info("Querying elasticsearch y id : {}",id);
        return assembler.toModel(elasticQueryClient.getIndexModelById(id));
    }

    @Override
    public List<ElasticQueryServiceResponseModel> getDocumentByText(String text) {
        log.info("Querying elasticsearch y text : {}",text);
        return assembler.toModels(elasticQueryClient.getIndexModelByText(text));
    }

    @Override
    public List<ElasticQueryServiceResponseModel> getAllDocuments() {
        log.info("Querying elasticsearch getting all models ");
        return assembler.toModels(elasticQueryClient.getAllIndexModels());
    }
}
