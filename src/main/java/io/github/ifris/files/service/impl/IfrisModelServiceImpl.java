package io.github.ifris.files.service.impl;

import io.github.ifris.files.domain.IfrisModel;
import io.github.ifris.files.repository.IfrisModelRepository;
import io.github.ifris.files.repository.search.IfrisModelSearchRepository;
import io.github.ifris.files.service.IfrisModelService;
import io.github.ifris.files.service.dto.IfrisModelDTO;
import io.github.ifris.files.service.mapper.IfrisModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Service Implementation for managing IfrisModel.
 */
@Service
@Transactional
public class IfrisModelServiceImpl implements IfrisModelService {

    private final Logger log = LoggerFactory.getLogger(IfrisModelServiceImpl.class);

    private final IfrisModelRepository ifrisModelRepository;

    private final IfrisModelMapper ifrisModelMapper;

    private final IfrisModelSearchRepository ifrisModelSearchRepository;

    public IfrisModelServiceImpl(IfrisModelRepository ifrisModelRepository, IfrisModelMapper ifrisModelMapper, IfrisModelSearchRepository ifrisModelSearchRepository) {
        this.ifrisModelRepository = ifrisModelRepository;
        this.ifrisModelMapper = ifrisModelMapper;
        this.ifrisModelSearchRepository = ifrisModelSearchRepository;
    }

    /**
     * Save a ifrisModel.
     *
     * @param ifrisModelDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public IfrisModelDTO save(IfrisModelDTO ifrisModelDTO) {
        log.debug("Request to save IfrisModel : {}", ifrisModelDTO);
        IfrisModel ifrisModel = ifrisModelMapper.toEntity(ifrisModelDTO);
        ifrisModel = ifrisModelRepository.save(ifrisModel);
        IfrisModelDTO result = ifrisModelMapper.toDto(ifrisModel);
        ifrisModelSearchRepository.save(ifrisModel);
        return result;
    }

    /**
     * Get all the ifrisModels.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<IfrisModelDTO> findAll(Pageable pageable) {
        log.debug("Request to get all IfrisModels");
        return ifrisModelRepository.findAll(pageable).map(ifrisModelMapper::toDto);
    }


    /**
     * Get one ifrisModel by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<IfrisModelDTO> findOne(Long id) {
        log.debug("Request to get IfrisModel : {}", id);
        return ifrisModelRepository.findById(id).map(ifrisModelMapper::toDto);
    }

    /**
     * Delete the ifrisModel by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete IfrisModel : {}", id);
        ifrisModelRepository.deleteById(id);
        ifrisModelSearchRepository.deleteById(id);
    }

    /**
     * Search for the ifrisModel corresponding to the query.
     *
     * @param query    the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<IfrisModelDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of IfrisModels for query {}", query);
        return ifrisModelSearchRepository.search(queryStringQuery(query), pageable).map(ifrisModelMapper::toDto);
    }
}
