package io.github.ifris.files.mq;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface UploadedDocumentConsumerChannel {

    String CHANNEL = "uploadedDocumentSubscribableChannel";

    @Input
    SubscribableChannel uploadedDocumentSubscribableChannel();
}
