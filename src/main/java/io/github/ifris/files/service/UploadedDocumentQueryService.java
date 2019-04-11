package io.github.ifris.files.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import io.github.ifris.files.domain.UploadedDocument;
import io.github.ifris.files.domain.*; // for static metamodels
import io.github.ifris.files.repository.UploadedDocumentRepository;
import io.github.ifris.files.repository.search.UploadedDocumentSearchRepository;
import io.github.ifris.files.service.dto.UploadedDocumentCriteria;
import io.github.ifris.files.service.dto.UploadedDocumentDTO;
import io.github.ifris.files.service.mapper.UploadedDocumentMapper;

/**
 * Service for executing complex queries for UploadedDocument entities in the database.
 * The main input is a {@link UploadedDocumentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link UploadedDocumentDTO} or a {@link Page} of {@link UploadedDocumentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UploadedDocumentQueryService extends QueryService<UploadedDocument> {

    private final Logger log = LoggerFactory.getLogger(UploadedDocumentQueryService.class);

    private final UploadedDocumentRepository uploadedDocumentRepository;

    private final UploadedDocumentMapper uploadedDocumentMapper;

    private final UploadedDocumentSearchRepository uploadedDocumentSearchRepository;

    public UploadedDocumentQueryService(UploadedDocumentRepository uploadedDocumentRepository, UploadedDocumentMapper uploadedDocumentMapper, UploadedDocumentSearchRepository uploadedDocumentSearchRepository) {
        this.uploadedDocumentRepository = uploadedDocumentRepository;
        this.uploadedDocumentMapper = uploadedDocumentMapper;
        this.uploadedDocumentSearchRepository = uploadedDocumentSearchRepository;
    }

    /**
     * Return a {@link List} of {@link UploadedDocumentDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<UploadedDocumentDTO> findByCriteria(UploadedDocumentCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<UploadedDocument> specification = createSpecification(criteria);
        return uploadedDocumentMapper.toDto(uploadedDocumentRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link UploadedDocumentDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<UploadedDocumentDTO> findByCriteria(UploadedDocumentCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<UploadedDocument> specification = createSpecification(criteria);
        return uploadedDocumentRepository.findAll(specification, page)
            .map(uploadedDocumentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UploadedDocumentCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<UploadedDocument> specification = createSpecification(criteria);
        return uploadedDocumentRepository.count(specification);
    }

    /**
     * Function to convert UploadedDocumentCriteria to a {@link Specification}
     */
    private Specification<UploadedDocument> createSpecification(UploadedDocumentCriteria criteria) {
        Specification<UploadedDocument> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), UploadedDocument_.id));
            }
            if (criteria.getFileName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFileName(), UploadedDocument_.fileName));
            }
            if (criteria.getYear() != null) {
                specification = specification.and(buildStringSpecification(criteria.getYear(), UploadedDocument_.year));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), UploadedDocument_.description));
            }
            if (criteria.getPeriodStart() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPeriodStart(), UploadedDocument_.periodStart));
            }
            if (criteria.getPeriodEnd() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPeriodEnd(), UploadedDocument_.periodEnd));
            }
            if (criteria.getContentType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getContentType(), UploadedDocument_.contentType));
            }
            if (criteria.getIfrisModel() != null) {
                specification = specification.and(buildStringSpecification(criteria.getIfrisModel(), UploadedDocument_.ifrisModel));
            }
            if (criteria.getAppInstance() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAppInstance(), UploadedDocument_.appInstance));
            }
        }
        return specification;
    }
}
