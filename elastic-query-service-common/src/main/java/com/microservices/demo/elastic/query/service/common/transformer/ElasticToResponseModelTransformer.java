package com.microservices.demo.elastic.query.service.common.transformer;

import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ElasticToResponseModelTransformer {
    public ElasticQueryServiceResponseModel getResponseModel(TwitterIndexModel indexModel) {
        return ElasticQueryServiceResponseModel.builder()
                .id(indexModel.getId())
                .text(indexModel.getText())
                .createdAt(indexModel.getCreatedAt())
                .userId(indexModel.getUserId())
                .build();
    }

    public List<ElasticQueryServiceResponseModel> getResponseModels(List<TwitterIndexModel> indexModels){
        return indexModels.stream().map(this::getResponseModel).toList();
    }
}
