package io.github.ifris.files.web.rest;
import io.github.ifris.files.service.DocumentTemplateService;
import io.github.ifris.files.web.rest.errors.BadRequestAlertException;
import io.github.ifris.files.web.rest.util.HeaderUtil;
import io.github.ifris.files.web.rest.util.PaginationUtil;
import io.github.ifris.files.service.dto.DocumentTemplateDTO;
import io.github.ifris.files.service.dto.DocumentTemplateCriteria;
import io.github.ifris.files.service.DocumentTemplateQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing DocumentTemplate.
 */
@RestController
@RequestMapping("/api")
public class DocumentTemplateResource {

    private final Logger log = LoggerFactory.getLogger(DocumentTemplateResource.class);

    private static final String ENTITY_NAME = "filingServiceDocumentTemplate";

    private final DocumentTemplateService documentTemplateService;

    private final DocumentTemplateQueryService documentTemplateQueryService;

    public DocumentTemplateResource(DocumentTemplateService documentTemplateService, DocumentTemplateQueryService documentTemplateQueryService) {
        this.documentTemplateService = documentTemplateService;
        this.documentTemplateQueryService = documentTemplateQueryService;
    }

    /**
     * POST  /document-templates : Create a new documentTemplate.
     *
     * @param documentTemplateDTO the documentTemplateDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new documentTemplateDTO, or with status 400 (Bad Request) if the documentTemplate has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/document-templates")
    public ResponseEntity<DocumentTemplateDTO> createDocumentTemplate(@Valid @RequestBody DocumentTemplateDTO documentTemplateDTO) throws URISyntaxException {
        log.debug("REST request to save DocumentTemplate : {}", documentTemplateDTO);
        if (documentTemplateDTO.getId() != null) {
            throw new BadRequestAlertException("A new documentTemplate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DocumentTemplateDTO result = documentTemplateService.save(documentTemplateDTO);
        return ResponseEntity.created(new URI("/api/document-templates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /document-templates : Updates an existing documentTemplate.
     *
     * @param documentTemplateDTO the documentTemplateDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated documentTemplateDTO,
     * or with status 400 (Bad Request) if the documentTemplateDTO is not valid,
     * or with status 500 (Internal Server Error) if the documentTemplateDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/document-templates")
    public ResponseEntity<DocumentTemplateDTO> updateDocumentTemplate(@Valid @RequestBody DocumentTemplateDTO documentTemplateDTO) throws URISyntaxException {
        log.debug("REST request to update DocumentTemplate : {}", documentTemplateDTO);
        if (documentTemplateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        DocumentTemplateDTO result = documentTemplateService.save(documentTemplateDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, documentTemplateDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /document-templates : get all the documentTemplates.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of documentTemplates in body
     */
    @GetMapping("/document-templates")
    public ResponseEntity<List<DocumentTemplateDTO>> getAllDocumentTemplates(DocumentTemplateCriteria criteria, Pageable pageable) {
        log.debug("REST request to get DocumentTemplates by criteria: {}", criteria);
        Page<DocumentTemplateDTO> page = documentTemplateQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/document-templates");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
    * GET  /document-templates/count : count all the documentTemplates.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/document-templates/count")
    public ResponseEntity<Long> countDocumentTemplates(DocumentTemplateCriteria criteria) {
        log.debug("REST request to count DocumentTemplates by criteria: {}", criteria);
        return ResponseEntity.ok().body(documentTemplateQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /document-templates/:id : get the "id" documentTemplate.
     *
     * @param id the id of the documentTemplateDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the documentTemplateDTO, or with status 404 (Not Found)
     */
    @GetMapping("/document-templates/{id}")
    public ResponseEntity<DocumentTemplateDTO> getDocumentTemplate(@PathVariable Long id) {
        log.debug("REST request to get DocumentTemplate : {}", id);
        Optional<DocumentTemplateDTO> documentTemplateDTO = documentTemplateService.findOne(id);
        return ResponseUtil.wrapOrNotFound(documentTemplateDTO);
    }

    /**
     * DELETE  /document-templates/:id : delete the "id" documentTemplate.
     *
     * @param id the id of the documentTemplateDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/document-templates/{id}")
    public ResponseEntity<Void> deleteDocumentTemplate(@PathVariable Long id) {
        log.debug("REST request to delete DocumentTemplate : {}", id);
        documentTemplateService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/document-templates?query=:query : search for the documentTemplate corresponding
     * to the query.
     *
     * @param query the query of the documentTemplate search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/document-templates")
    public ResponseEntity<List<DocumentTemplateDTO>> searchDocumentTemplates(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of DocumentTemplates for query {}", query);
        Page<DocumentTemplateDTO> page = documentTemplateService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/document-templates");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}
