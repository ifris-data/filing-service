package io.github.ifris.files.service.impl;

import io.github.ifris.files.service.DocumentTemplateService;
import io.github.ifris.files.domain.DocumentTemplate;
import io.github.ifris.files.repository.DocumentTemplateRepository;
import io.github.ifris.files.repository.search.DocumentTemplateSearchRepository;
import io.github.ifris.files.service.dto.DocumentTemplateDTO;
import io.github.ifris.files.service.mapper.DocumentTemplateMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing DocumentTemplate.
 */
@Service
@Transactional
public class DocumentTemplateServiceImpl implements DocumentTemplateService {

    private final Logger log = LoggerFactory.getLogger(DocumentTemplateServiceImpl.class);

    private final DocumentTemplateRepository documentTemplateRepository;

    private final DocumentTemplateMapper documentTemplateMapper;

    private final DocumentTemplateSearchRepository documentTemplateSearchRepository;

    public DocumentTemplateServiceImpl(DocumentTemplateRepository documentTemplateRepository, DocumentTemplateMapper documentTemplateMapper, DocumentTemplateSearchRepository documentTemplateSearchRepository) {
        this.documentTemplateRepository = documentTemplateRepository;
        this.documentTemplateMapper = documentTemplateMapper;
        this.documentTemplateSearchRepository = documentTemplateSearchRepository;
    }

    /**
     * Save a documentTemplate.
     *
     * @param documentTemplateDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public DocumentTemplateDTO save(DocumentTemplateDTO documentTemplateDTO) {
        log.debug("Request to save DocumentTemplate : {}", documentTemplateDTO);
        DocumentTemplate documentTemplate = documentTemplateMapper.toEntity(documentTemplateDTO);
        documentTemplate = documentTemplateRepository.save(documentTemplate);
        DocumentTemplateDTO result = documentTemplateMapper.toDto(documentTemplate);
        documentTemplateSearchRepository.save(documentTemplate);
        return result;
    }

    /**
     * Get all the documentTemplates.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<DocumentTemplateDTO> findAll(Pageable pageable) {
        log.debug("Request to get all DocumentTemplates");
        return documentTemplateRepository.findAll(pageable)
            .map(documentTemplateMapper::toDto);
    }


    /**
     * Get one documentTemplate by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<DocumentTemplateDTO> findOne(Long id) {
        log.debug("Request to get DocumentTemplate : {}", id);
        return documentTemplateRepository.findById(id)
            .map(documentTemplateMapper::toDto);
    }

    /**
     * Delete the documentTemplate by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete DocumentTemplate : {}", id);
        documentTemplateRepository.deleteById(id);
        documentTemplateSearchRepository.deleteById(id);
    }

    /**
     * Search for the documentTemplate corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<DocumentTemplateDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of DocumentTemplates for query {}", query);
        return documentTemplateSearchRepository.search(queryStringQuery(query), pageable)
            .map(documentTemplateMapper::toDto);
    }
}
