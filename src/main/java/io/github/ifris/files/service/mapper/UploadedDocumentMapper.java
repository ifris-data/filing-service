package io.github.ifris.files.service.mapper;

import io.github.ifris.files.domain.*;
import io.github.ifris.files.service.dto.UploadedDocumentDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity UploadedDocument and its DTO UploadedDocumentDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface UploadedDocumentMapper extends EntityMapper<UploadedDocumentDTO, UploadedDocument> {



    default UploadedDocument fromId(Long id) {
        if (id == null) {
            return null;
        }
        UploadedDocument uploadedDocument = new UploadedDocument();
        uploadedDocument.setId(id);
        return uploadedDocument;
    }
}
