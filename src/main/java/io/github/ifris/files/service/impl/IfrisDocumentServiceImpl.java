package io.github.ifris.files.service.impl;

import io.github.ifris.files.service.IfrisDocumentService;
import io.github.ifris.files.domain.IfrisDocument;
import io.github.ifris.files.repository.IfrisDocumentRepository;
import io.github.ifris.files.repository.search.IfrisDocumentSearchRepository;
import io.github.ifris.files.service.dto.IfrisDocumentDTO;
import io.github.ifris.files.service.mapper.IfrisDocumentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing IfrisDocument.
 */
@Service
@Transactional
public class IfrisDocumentServiceImpl implements IfrisDocumentService {

    private final Logger log = LoggerFactory.getLogger(IfrisDocumentServiceImpl.class);

    private final IfrisDocumentRepository ifrisDocumentRepository;

    private final IfrisDocumentMapper ifrisDocumentMapper;

    private final IfrisDocumentSearchRepository ifrisDocumentSearchRepository;

    public IfrisDocumentServiceImpl(IfrisDocumentRepository ifrisDocumentRepository, IfrisDocumentMapper ifrisDocumentMapper, IfrisDocumentSearchRepository ifrisDocumentSearchRepository) {
        this.ifrisDocumentRepository = ifrisDocumentRepository;
        this.ifrisDocumentMapper = ifrisDocumentMapper;
        this.ifrisDocumentSearchRepository = ifrisDocumentSearchRepository;
    }

    /**
     * Save a ifrisDocument.
     *
     * @param ifrisDocumentDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public IfrisDocumentDTO save(IfrisDocumentDTO ifrisDocumentDTO) {
        log.debug("Request to save IfrisDocument : {}", ifrisDocumentDTO);
        IfrisDocument ifrisDocument = ifrisDocumentMapper.toEntity(ifrisDocumentDTO);
        ifrisDocument = ifrisDocumentRepository.save(ifrisDocument);
        IfrisDocumentDTO result = ifrisDocumentMapper.toDto(ifrisDocument);
        ifrisDocumentSearchRepository.save(ifrisDocument);
        return result;
    }

    /**
     * Get all the ifrisDocuments.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<IfrisDocumentDTO> findAll(Pageable pageable) {
        log.debug("Request to get all IfrisDocuments");
        return ifrisDocumentRepository.findAll(pageable)
            .map(ifrisDocumentMapper::toDto);
    }


    /**
     * Get one ifrisDocument by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<IfrisDocumentDTO> findOne(Long id) {
        log.debug("Request to get IfrisDocument : {}", id);
        return ifrisDocumentRepository.findById(id)
            .map(ifrisDocumentMapper::toDto);
    }

    /**
     * Delete the ifrisDocument by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete IfrisDocument : {}", id);
        ifrisDocumentRepository.deleteById(id);
        ifrisDocumentSearchRepository.deleteById(id);
    }

    /**
     * Search for the ifrisDocument corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<IfrisDocumentDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of IfrisDocuments for query {}", query);
        return ifrisDocumentSearchRepository.search(queryStringQuery(query), pageable)
            .map(ifrisDocumentMapper::toDto);
    }
}
