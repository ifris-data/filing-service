package io.github.ifris.files.service;

import io.github.ifris.files.service.dto.IfrisModelDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing IfrisModel.
 */
public interface IfrisModelService {

    /**
     * Save a ifrisModel.
     *
     * @param ifrisModelDTO the entity to save
     * @return the persisted entity
     */
    IfrisModelDTO save(IfrisModelDTO ifrisModelDTO);

    /**
     * Get all the ifrisModels.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<IfrisModelDTO> findAll(Pageable pageable);


    /**
     * Get the "id" ifrisModel.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<IfrisModelDTO> findOne(Long id);

    /**
     * Delete the "id" ifrisModel.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the ifrisModel corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<IfrisModelDTO> search(String query, Pageable pageable);
}
