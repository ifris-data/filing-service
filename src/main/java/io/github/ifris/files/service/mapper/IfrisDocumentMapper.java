package io.github.ifris.files.service.mapper;

import io.github.ifris.files.domain.IfrisDocument;
import io.github.ifris.files.service.dto.IfrisDocumentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity IfrisDocument and its DTO IfrisDocumentDTO.
 */
@Mapper(componentModel = "spring", uses = {IfrisModelMapper.class})
public interface IfrisDocumentMapper extends EntityMapper<IfrisDocumentDTO, IfrisDocument> {

    @Mapping(source = "ifrisModel.id", target = "ifrisModelId")
    @Mapping(source = "ifrisModel.modelName", target = "ifrisModelModelName")
    IfrisDocumentDTO toDto(IfrisDocument ifrisDocument);

    @Mapping(source = "ifrisModelId", target = "ifrisModel")
    IfrisDocument toEntity(IfrisDocumentDTO ifrisDocumentDTO);

    default IfrisDocument fromId(Long id) {
        if (id == null) {
            return null;
        }
        IfrisDocument ifrisDocument = new IfrisDocument();
        ifrisDocument.setId(id);
        return ifrisDocument;
    }
}
