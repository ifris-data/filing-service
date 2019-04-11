package io.github.ifris.files.service.impl;

import io.github.ifris.files.service.UploadedDocumentService;
import io.github.ifris.files.domain.UploadedDocument;
import io.github.ifris.files.repository.UploadedDocumentRepository;
import io.github.ifris.files.repository.search.UploadedDocumentSearchRepository;
import io.github.ifris.files.service.dto.UploadedDocumentDTO;
import io.github.ifris.files.service.mapper.UploadedDocumentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing UploadedDocument.
 */
@Service
@Transactional
public class UploadedDocumentServiceImpl implements UploadedDocumentService {

    private final Logger log = LoggerFactory.getLogger(UploadedDocumentServiceImpl.class);

    private final UploadedDocumentRepository uploadedDocumentRepository;

    private final UploadedDocumentMapper uploadedDocumentMapper;

    private final UploadedDocumentSearchRepository uploadedDocumentSearchRepository;

    public UploadedDocumentServiceImpl(UploadedDocumentRepository uploadedDocumentRepository, UploadedDocumentMapper uploadedDocumentMapper, UploadedDocumentSearchRepository uploadedDocumentSearchRepository) {
        this.uploadedDocumentRepository = uploadedDocumentRepository;
        this.uploadedDocumentMapper = uploadedDocumentMapper;
        this.uploadedDocumentSearchRepository = uploadedDocumentSearchRepository;
    }

    /**
     * Save a uploadedDocument.
     *
     * @param uploadedDocumentDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public UploadedDocumentDTO save(UploadedDocumentDTO uploadedDocumentDTO) {
        log.debug("Request to save UploadedDocument : {}", uploadedDocumentDTO);
        UploadedDocument uploadedDocument = uploadedDocumentMapper.toEntity(uploadedDocumentDTO);
        uploadedDocument = uploadedDocumentRepository.save(uploadedDocument);
        UploadedDocumentDTO result = uploadedDocumentMapper.toDto(uploadedDocument);
        uploadedDocumentSearchRepository.save(uploadedDocument);
        return result;
    }

    /**
     * Get all the uploadedDocuments.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UploadedDocumentDTO> findAll(Pageable pageable) {
        log.debug("Request to get all UploadedDocuments");
        return uploadedDocumentRepository.findAll(pageable)
            .map(uploadedDocumentMapper::toDto);
    }


    /**
     * Get one uploadedDocument by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UploadedDocumentDTO> findOne(Long id) {
        log.debug("Request to get UploadedDocument : {}", id);
        return uploadedDocumentRepository.findById(id)
            .map(uploadedDocumentMapper::toDto);
    }

    /**
     * Delete the uploadedDocument by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete UploadedDocument : {}", id);
        uploadedDocumentRepository.deleteById(id);
        uploadedDocumentSearchRepository.deleteById(id);
    }

    /**
     * Search for the uploadedDocument corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UploadedDocumentDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of UploadedDocuments for query {}", query);
        return uploadedDocumentSearchRepository.search(queryStringQuery(query), pageable)
            .map(uploadedDocumentMapper::toDto);
    }
}
