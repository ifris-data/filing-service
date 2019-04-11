package io.github.ifris.files.service;

import io.github.ifris.files.domain.DocumentTemplate_;
import io.github.ifris.files.domain.IfrisDocument_;
import io.github.ifris.files.domain.IfrisModel;
import io.github.ifris.files.domain.IfrisModel_;
import io.github.ifris.files.repository.IfrisModelRepository;
import io.github.ifris.files.repository.search.IfrisModelSearchRepository;
import io.github.ifris.files.service.dto.IfrisModelCriteria;
import io.github.ifris.files.service.dto.IfrisModelDTO;
import io.github.ifris.files.service.mapper.IfrisModelMapper;
import io.github.jhipster.service.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.JoinType;
import java.util.List;

/**
 * Service for executing complex queries for IfrisModel entities in the database. The main input is a {@link IfrisModelCriteria} which gets converted to {@link Specification}, in a way that all the
 * filters must apply. It returns a {@link List} of {@link IfrisModelDTO} or a {@link Page} of {@link IfrisModelDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class IfrisModelQueryService extends QueryService<IfrisModel> {

    private final Logger log = LoggerFactory.getLogger(IfrisModelQueryService.class);

    private final IfrisModelRepository ifrisModelRepository;

    private final IfrisModelMapper ifrisModelMapper;

    private final IfrisModelSearchRepository ifrisModelSearchRepository;

    public IfrisModelQueryService(IfrisModelRepository ifrisModelRepository, IfrisModelMapper ifrisModelMapper, IfrisModelSearchRepository ifrisModelSearchRepository) {
        this.ifrisModelRepository = ifrisModelRepository;
        this.ifrisModelMapper = ifrisModelMapper;
        this.ifrisModelSearchRepository = ifrisModelSearchRepository;
    }

    /**
     * Return a {@link List} of {@link IfrisModelDTO} which matches the criteria from the database
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<IfrisModelDTO> findByCriteria(IfrisModelCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<IfrisModel> specification = createSpecification(criteria);
        return ifrisModelMapper.toDto(ifrisModelRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link IfrisModelDTO} which matches the criteria from the database
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<IfrisModelDTO> findByCriteria(IfrisModelCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<IfrisModel> specification = createSpecification(criteria);
        return ifrisModelRepository.findAll(specification, page).map(ifrisModelMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(IfrisModelCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<IfrisModel> specification = createSpecification(criteria);
        return ifrisModelRepository.count(specification);
    }

    /**
     * Function to convert IfrisModelCriteria to a {@link Specification}
     */
    private Specification<IfrisModel> createSpecification(IfrisModelCriteria criteria) {
        Specification<IfrisModel> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), IfrisModel_.id));
            }
            if (criteria.getModelName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getModelName(), IfrisModel_.modelName));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), IfrisModel_.description));
            }
            if (criteria.getServiceName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getServiceName(), IfrisModel_.serviceName));
            }
            if (criteria.getServicePort() != null) {
                specification = specification.and(buildStringSpecification(criteria.getServicePort(), IfrisModel_.servicePort));
            }
            if (criteria.getIfrisDocumentId() != null) {
                specification = specification.and(buildSpecification(criteria.getIfrisDocumentId(), root -> root.join(IfrisModel_.ifrisDocuments, JoinType.LEFT).get(IfrisDocument_.id)));
            }
            if (criteria.getDocumentTemplateId() != null) {
                specification = specification.and(buildSpecification(criteria.getDocumentTemplateId(), root -> root.join(IfrisModel_.documentTemplates, JoinType.LEFT).get(DocumentTemplate_.id)));
            }
        }
        return specification;
    }
}
