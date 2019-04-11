package io.github.ifris.files.service;

import io.github.ifris.files.domain.IfrisDocument;
import io.github.ifris.files.domain.IfrisDocument_;
import io.github.ifris.files.domain.IfrisModel_;
import io.github.ifris.files.repository.IfrisDocumentRepository;
import io.github.ifris.files.repository.search.IfrisDocumentSearchRepository;
import io.github.ifris.files.service.dto.IfrisDocumentCriteria;
import io.github.ifris.files.service.dto.IfrisDocumentDTO;
import io.github.ifris.files.service.mapper.IfrisDocumentMapper;
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
 * Service for executing complex queries for IfrisDocument entities in the database. The main input is a {@link IfrisDocumentCriteria} which gets converted to {@link Specification}, in a way that all
 * the filters must apply. It returns a {@link List} of {@link IfrisDocumentDTO} or a {@link Page} of {@link IfrisDocumentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class IfrisDocumentQueryService extends QueryService<IfrisDocument> {

    private final Logger log = LoggerFactory.getLogger(IfrisDocumentQueryService.class);

    private final IfrisDocumentRepository ifrisDocumentRepository;

    private final IfrisDocumentMapper ifrisDocumentMapper;

    private final IfrisDocumentSearchRepository ifrisDocumentSearchRepository;

    public IfrisDocumentQueryService(IfrisDocumentRepository ifrisDocumentRepository, IfrisDocumentMapper ifrisDocumentMapper, IfrisDocumentSearchRepository ifrisDocumentSearchRepository) {
        this.ifrisDocumentRepository = ifrisDocumentRepository;
        this.ifrisDocumentMapper = ifrisDocumentMapper;
        this.ifrisDocumentSearchRepository = ifrisDocumentSearchRepository;
    }

    /**
     * Return a {@link List} of {@link IfrisDocumentDTO} which matches the criteria from the database
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<IfrisDocumentDTO> findByCriteria(IfrisDocumentCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<IfrisDocument> specification = createSpecification(criteria);
        return ifrisDocumentMapper.toDto(ifrisDocumentRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link IfrisDocumentDTO} which matches the criteria from the database
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<IfrisDocumentDTO> findByCriteria(IfrisDocumentCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<IfrisDocument> specification = createSpecification(criteria);
        return ifrisDocumentRepository.findAll(specification, page).map(ifrisDocumentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(IfrisDocumentCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<IfrisDocument> specification = createSpecification(criteria);
        return ifrisDocumentRepository.count(specification);
    }

    /**
     * Function to convert IfrisDocumentCriteria to a {@link Specification}
     */
    private Specification<IfrisDocument> createSpecification(IfrisDocumentCriteria criteria) {
        Specification<IfrisDocument> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), IfrisDocument_.id));
            }
            if (criteria.getFileName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFileName(), IfrisDocument_.fileName));
            }
            if (criteria.getYear() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getYear(), IfrisDocument_.year));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), IfrisDocument_.description));
            }
            if (criteria.getPeriodStart() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPeriodStart(), IfrisDocument_.periodStart));
            }
            if (criteria.getPeriodEnd() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPeriodEnd(), IfrisDocument_.periodEnd));
            }
            if (criteria.getIfrisModelId() != null) {
                specification = specification.and(buildSpecification(criteria.getIfrisModelId(), root -> root.join(IfrisDocument_.ifrisModel, JoinType.LEFT).get(IfrisModel_.id)));
            }
        }
        return specification;
    }
}
