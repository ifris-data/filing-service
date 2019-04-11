package io.github.ifris.files.mq;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * Output channel for uploaded documents
 */
public interface UploadedDocumentProducerChannel {

    String CHANNEL = "uploadedDocumentChannel";

    @Output
    MessageChannel uploadedDocumentChannel();
}
