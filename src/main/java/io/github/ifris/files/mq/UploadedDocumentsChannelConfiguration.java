package io.github.ifris.files.mq;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;

/**
 * Configures cloud stream to send alerts on uploaded dcuments in the IFRIS system
 */
@EnableBinding(value = {Source.class, UploadedDocumentProducerChannel.class, UploadedDocumentConsumerChannel.class})
public class UploadedDocumentsChannelConfiguration {

    @Value("${spring.application.name:JhipsterService}")
    private String applicationName;

    @Value("${eureka.instance.instanceId:JhipsterService}")
    private String instanceName;


}
