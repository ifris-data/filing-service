package io.github.ifris.files.service;

import io.github.ifris.files.mq.UploadedDocumentConsumerChannel;
import io.github.ifris.files.service.dto.UploadedDocumentDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

/**
 * This is a service that receives messages when a document has been uploaded in the system
 */
@Slf4j
@Service
public class UploadedDocumentConsumerService {

    @Value("${eureka.instance.instanceId:JhipsterService}")
    private String instanceName;

    private final UploadedDocumentService uploadedDocumentService;

    public UploadedDocumentConsumerService(final UploadedDocumentService uploadedDocumentService) {
        this.uploadedDocumentService = uploadedDocumentService;
    }


    @StreamListener(UploadedDocumentConsumerChannel.CHANNEL)
    public void consume(UploadedDocumentDTO uploadedDocument){

        log.info("Received uploadedDocument alert : {}, uploaded by app : {}, of the type : {}",
                 uploadedDocument.getFileName(), uploadedDocument.getAppInstance(), uploadedDocument.getContentType());

        if (!uploadedDocument.getAppInstance().equalsIgnoreCase(instanceName)){

            uploadedDocumentService.save(uploadedDocument);

            // trigger endpoint to receive IfrisDocument with the same specs as the UploadedDocument.
        }
    }

}
