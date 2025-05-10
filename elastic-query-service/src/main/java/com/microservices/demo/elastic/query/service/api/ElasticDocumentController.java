package com.microservices.demo.elastic.query.service.api;

import com.microservices.demo.elastic.query.service.business.ElasticQueryService;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceRequestModel;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceResponseModel;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceResponseModelV2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/documents", produces = "application/vnd.api.v1+json")
public class ElasticDocumentController {

    private final ElasticQueryService elasticQueryService;


    public ElasticDocumentController(ElasticQueryService elasticQueryService) {
        this.elasticQueryService = elasticQueryService;
    }

    @Operation(summary = "Get all elastic documents")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response.", content = {
                    @Content(mediaType = "application/vnd.api.v1+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModel.class))
            }),
            @ApiResponse(responseCode = "400", description = "Not Found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @GetMapping("/")
    public ResponseEntity<List<ElasticQueryServiceResponseModel>> getAllDocuments(){
        List<ElasticQueryServiceResponseModel> response = elasticQueryService.getAllDocuments();
        log.info("Elasticsearch returned {} documents",response.size());
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Get elastic document by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response.", content = {
                    @Content(mediaType = "application/vnd.api.v1+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModel.class))
            }),
            @ApiResponse(responseCode = "400", description = "Not Found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ElasticQueryServiceResponseModel> getDocumentById(@PathVariable @NotEmpty String id){
        ElasticQueryServiceResponseModel response = elasticQueryService.getDocumentById(id);
        log.info("Elasticsearch returned document with id {}",response.getId());
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Get elastic document by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response.", content = {
                    @Content(mediaType = "application/vnd.api.v2+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModelV2.class))
            }),
            @ApiResponse(responseCode = "400", description = "Not Found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @GetMapping(value = "/{id}", produces = "application/vnd.api.v2+json")
    public ResponseEntity<ElasticQueryServiceResponseModelV2> getDocumentByIdV2(@PathVariable @NotEmpty String id){
        ElasticQueryServiceResponseModel response = elasticQueryService.getDocumentById(id);
        log.info("Elasticsearch returned documentV2 with id  {}",response.getId());
        return ResponseEntity.ok(getV2Model(response));
    }

    @Operation(summary = "Get elastic document by text")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response.", content = {
                    @Content(mediaType = "application/vnd.api.v1+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModel.class))
            }),
            @ApiResponse(responseCode = "400", description = "Not Found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @PostMapping("/get-document-by-text")
    public ResponseEntity<List<ElasticQueryServiceResponseModel>> getDocumentWithText(
            @RequestBody @Valid ElasticQueryServiceRequestModel request) {
        List<ElasticQueryServiceResponseModel> response = elasticQueryService.getDocumentByText(request.getText());
        log.info("Elasticsearch returned {} documents for text {}",response.size(), request.getText());
        return ResponseEntity.ok(response);
    }

    private ElasticQueryServiceResponseModelV2 getV2Model(ElasticQueryServiceResponseModel modelV1) {
        ElasticQueryServiceResponseModelV2 responseModelV2 = ElasticQueryServiceResponseModelV2.builder()
                .id(Long.parseLong(modelV1.getId()))
                .userId(modelV1.getUserId())
                .text(modelV1.getText())
                .textV2("Version 2 text")
                .build();
        responseModelV2.add(modelV1.getLinks());
        return responseModelV2;
    }
}
