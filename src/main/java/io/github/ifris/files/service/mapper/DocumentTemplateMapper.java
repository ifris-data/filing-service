package io.github.ifris.files.service.mapper;

import io.github.ifris.files.domain.DocumentTemplate;
import io.github.ifris.files.service.dto.DocumentTemplateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity DocumentTemplate and its DTO DocumentTemplateDTO.
 */
@Mapper(componentModel = "spring", uses = {IfrisModelMapper.class})
public interface DocumentTemplateMapper extends EntityMapper<DocumentTemplateDTO, DocumentTemplate> {

    @Mapping(source = "ifrisModel.id", target = "ifrisModelId")
    @Mapping(source = "ifrisModel.modelName", target = "ifrisModelModelName")
    DocumentTemplateDTO toDto(DocumentTemplate documentTemplate);

    @Mapping(source = "ifrisModelId", target = "ifrisModel")
    DocumentTemplate toEntity(DocumentTemplateDTO documentTemplateDTO);

    default DocumentTemplate fromId(Long id) {
        if (id == null) {
            return null;
        }
        DocumentTemplate documentTemplate = new DocumentTemplate();
        documentTemplate.setId(id);
        return documentTemplate;
    }
}
