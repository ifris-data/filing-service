package io.github.ifris.files.mq;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * This is a message object for use by the channel to alert when a file has been uploaded to
 * the system, inorder to alert listeners.
 * This however can only be used for uploaded documents not the templates themselves
 */
@Data
@NoArgsConstructor
@Builder
public class UploadedDocumentDTO implements Serializable {

    private static final long serialVersionUID = -2043243084547247009L;
    private String fileName;
    private String year;
    private String description;
    private String periodStart;
    private String periodEnd;
    private String ContentType;
    private String ifrisModel;
}
