package io.github.ifris.files.service;

import io.github.ifris.files.service.dto.DocumentTemplateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing DocumentTemplate.
 */
public interface DocumentTemplateService {

    /**
     * Save a documentTemplate.
     *
     * @param documentTemplateDTO the entity to save
     * @return the persisted entity
     */
    DocumentTemplateDTO save(DocumentTemplateDTO documentTemplateDTO);

    /**
     * Get all the documentTemplates.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<DocumentTemplateDTO> findAll(Pageable pageable);


    /**
     * Get the "id" documentTemplate.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<DocumentTemplateDTO> findOne(Long id);

    /**
     * Delete the "id" documentTemplate.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the documentTemplate corresponding to the query.
     *
     * @param query    the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<DocumentTemplateDTO> search(String query, Pageable pageable);
}
