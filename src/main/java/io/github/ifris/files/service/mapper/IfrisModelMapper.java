package io.github.ifris.files.service.mapper;

import io.github.ifris.files.domain.IfrisModel;
import io.github.ifris.files.service.dto.IfrisModelDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity IfrisModel and its DTO IfrisModelDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface IfrisModelMapper extends EntityMapper<IfrisModelDTO, IfrisModel> {


    @Mapping(target = "ifrisDocuments", ignore = true)
    @Mapping(target = "documentTemplates", ignore = true)
    IfrisModel toEntity(IfrisModelDTO ifrisModelDTO);

    default IfrisModel fromId(Long id) {
        if (id == null) {
            return null;
        }
        IfrisModel ifrisModel = new IfrisModel();
        ifrisModel.setId(id);
        return ifrisModel;
    }
}
