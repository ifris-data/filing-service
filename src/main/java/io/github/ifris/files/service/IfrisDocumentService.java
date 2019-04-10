package io.github.ifris.files.service;

import io.github.ifris.files.service.dto.IfrisDocumentDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing IfrisDocument.
 */
public interface IfrisDocumentService {

    /**
     * Save a ifrisDocument.
     *
     * @param ifrisDocumentDTO the entity to save
     * @return the persisted entity
     */
    IfrisDocumentDTO save(IfrisDocumentDTO ifrisDocumentDTO);

    /**
     * Get all the ifrisDocuments.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<IfrisDocumentDTO> findAll(Pageable pageable);


    /**
     * Get the "id" ifrisDocument.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<IfrisDocumentDTO> findOne(Long id);

    /**
     * Delete the "id" ifrisDocument.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the ifrisDocument corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<IfrisDocumentDTO> search(String query, Pageable pageable);
}
