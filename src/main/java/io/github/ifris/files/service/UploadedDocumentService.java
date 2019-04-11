package io.github.ifris.files.service;

import io.github.ifris.files.service.dto.UploadedDocumentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing UploadedDocument.
 */
public interface UploadedDocumentService {

    /**
     * Save a uploadedDocument.
     *
     * @param uploadedDocumentDTO the entity to save
     * @return the persisted entity
     */
    UploadedDocumentDTO save(UploadedDocumentDTO uploadedDocumentDTO);

    /**
     * Get all the uploadedDocuments.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<UploadedDocumentDTO> findAll(Pageable pageable);


    /**
     * Get the "id" uploadedDocument.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<UploadedDocumentDTO> findOne(Long id);

    /**
     * Delete the "id" uploadedDocument.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the uploadedDocument corresponding to the query.
     *
     * @param query    the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<UploadedDocumentDTO> search(String query, Pageable pageable);
}
