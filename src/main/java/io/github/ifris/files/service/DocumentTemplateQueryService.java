package io.github.ifris.files.service;

import io.github.ifris.files.domain.DocumentTemplate;
import io.github.ifris.files.domain.DocumentTemplate_;
import io.github.ifris.files.domain.IfrisModel_;
import io.github.ifris.files.repository.DocumentTemplateRepository;
import io.github.ifris.files.repository.search.DocumentTemplateSearchRepository;
import io.github.ifris.files.service.dto.DocumentTemplateCriteria;
import io.github.ifris.files.service.dto.DocumentTemplateDTO;
import io.github.ifris.files.service.mapper.DocumentTemplateMapper;
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
 * Service for executing complex queries for DocumentTemplate entities in the database. The main input is a {@link DocumentTemplateCriteria} which gets converted to {@link Specification}, in a way
 * that all the filters must apply. It returns a {@link List} of {@link DocumentTemplateDTO} or a {@link Page} of {@link DocumentTemplateDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DocumentTemplateQueryService extends QueryService<DocumentTemplate> {

    private final Logger log = LoggerFactory.getLogger(DocumentTemplateQueryService.class);

    private final DocumentTemplateRepository documentTemplateRepository;

    private final DocumentTemplateMapper documentTemplateMapper;

    private final DocumentTemplateSearchRepository documentTemplateSearchRepository;

    public DocumentTemplateQueryService(DocumentTemplateRepository documentTemplateRepository, DocumentTemplateMapper documentTemplateMapper,
                                        DocumentTemplateSearchRepository documentTemplateSearchRepository) {
        this.documentTemplateRepository = documentTemplateRepository;
        this.documentTemplateMapper = documentTemplateMapper;
        this.documentTemplateSearchRepository = documentTemplateSearchRepository;
    }

    /**
     * Return a {@link List} of {@link DocumentTemplateDTO} which matches the criteria from the database
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<DocumentTemplateDTO> findByCriteria(DocumentTemplateCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<DocumentTemplate> specification = createSpecification(criteria);
        return documentTemplateMapper.toDto(documentTemplateRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link DocumentTemplateDTO} which matches the criteria from the database
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DocumentTemplateDTO> findByCriteria(DocumentTemplateCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<DocumentTemplate> specification = createSpecification(criteria);
        return documentTemplateRepository.findAll(specification, page).map(documentTemplateMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DocumentTemplateCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<DocumentTemplate> specification = createSpecification(criteria);
        return documentTemplateRepository.count(specification);
    }

    /**
     * Function to convert DocumentTemplateCriteria to a {@link Specification}
     */
    private Specification<DocumentTemplate> createSpecification(DocumentTemplateCriteria criteria) {
        Specification<DocumentTemplate> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), DocumentTemplate_.id));
            }
            if (criteria.getDateCreated() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDateCreated(), DocumentTemplate_.dateCreated));
            }
            if (criteria.getUpdateDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdateDate(), DocumentTemplate_.updateDate));
            }
            if (criteria.getIfrisModelId() != null) {
                specification = specification.and(buildSpecification(criteria.getIfrisModelId(), root -> root.join(DocumentTemplate_.ifrisModel, JoinType.LEFT).get(IfrisModel_.id)));
            }
        }
        return specification;
    }
}
