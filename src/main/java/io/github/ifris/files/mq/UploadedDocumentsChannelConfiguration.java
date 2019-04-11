package io.github.ifris.files.mq;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.support.GenericMessage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Configures cloud stream to send alerts on uploaded dcuments in the IFRIS system
 *
 */
@EnableBinding(value = {Source.class,
    UploadedDocumentProducerChannel.class,
    UploadedDocumentConsumerChannel.class
})
public class UploadedDocumentsChannelConfiguration {

    @Value("${spring.application.name:JhipsterService}")
    private String applicationName;

    @Value("${eureka.instance.instanceId:JhipsterService}")
    private String instanceName;


}
