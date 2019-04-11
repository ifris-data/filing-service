package io.github.ifris.files.web.rest;

import io.github.ifris.files.service.UploadedDocumentQueryService;
import io.github.ifris.files.service.UploadedDocumentService;
import io.github.ifris.files.service.dto.UploadedDocumentCriteria;
import io.github.ifris.files.service.dto.UploadedDocumentDTO;
import io.github.ifris.files.web.rest.errors.BadRequestAlertException;
import io.github.ifris.files.web.rest.util.HeaderUtil;
import io.github.ifris.files.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing UploadedDocument.
 */
@RestController
@RequestMapping("/api")
public class UploadedDocumentResource {

    private static final String ENTITY_NAME = "filingServiceUploadedDocument";
    private final Logger log = LoggerFactory.getLogger(UploadedDocumentResource.class);
    private final UploadedDocumentService uploadedDocumentService;

    private final UploadedDocumentQueryService uploadedDocumentQueryService;

    public UploadedDocumentResource(UploadedDocumentService uploadedDocumentService, UploadedDocumentQueryService uploadedDocumentQueryService) {
        this.uploadedDocumentService = uploadedDocumentService;
        this.uploadedDocumentQueryService = uploadedDocumentQueryService;
    }

    /**
     * POST  /uploaded-documents : Create a new uploadedDocument.
     *
     * @param uploadedDocumentDTO the uploadedDocumentDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new uploadedDocumentDTO, or with status 400 (Bad Request) if the uploadedDocument has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/uploaded-documents")
    public ResponseEntity<UploadedDocumentDTO> createUploadedDocument(@Valid @RequestBody UploadedDocumentDTO uploadedDocumentDTO) throws URISyntaxException {
        log.debug("REST request to save UploadedDocument : {}", uploadedDocumentDTO);
        if (uploadedDocumentDTO.getId() != null) {
            throw new BadRequestAlertException("A new uploadedDocument cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UploadedDocumentDTO result = uploadedDocumentService.save(uploadedDocumentDTO);
        return ResponseEntity.created(new URI("/api/uploaded-documents/" + result.getId())).headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString())).body(result);
    }

    /**
     * PUT  /uploaded-documents : Updates an existing uploadedDocument.
     *
     * @param uploadedDocumentDTO the uploadedDocumentDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated uploadedDocumentDTO, or with status 400 (Bad Request) if the uploadedDocumentDTO is not valid, or with status 500
     * (Internal Server Error) if the uploadedDocumentDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/uploaded-documents")
    public ResponseEntity<UploadedDocumentDTO> updateUploadedDocument(@Valid @RequestBody UploadedDocumentDTO uploadedDocumentDTO) throws URISyntaxException {
        log.debug("REST request to update UploadedDocument : {}", uploadedDocumentDTO);
        if (uploadedDocumentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        UploadedDocumentDTO result = uploadedDocumentService.save(uploadedDocumentDTO);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, uploadedDocumentDTO.getId().toString())).body(result);
    }

    /**
     * GET  /uploaded-documents : get all the uploadedDocuments.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of uploadedDocuments in body
     */
    @GetMapping("/uploaded-documents")
    public ResponseEntity<List<UploadedDocumentDTO>> getAllUploadedDocuments(UploadedDocumentCriteria criteria, Pageable pageable) {
        log.debug("REST request to get UploadedDocuments by criteria: {}", criteria);
        Page<UploadedDocumentDTO> page = uploadedDocumentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/uploaded-documents");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /uploaded-documents/count : count all the uploadedDocuments.
     *
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the count in body
     */
    @GetMapping("/uploaded-documents/count")
    public ResponseEntity<Long> countUploadedDocuments(UploadedDocumentCriteria criteria) {
        log.debug("REST request to count UploadedDocuments by criteria: {}", criteria);
        return ResponseEntity.ok().body(uploadedDocumentQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /uploaded-documents/:id : get the "id" uploadedDocument.
     *
     * @param id the id of the uploadedDocumentDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the uploadedDocumentDTO, or with status 404 (Not Found)
     */
    @GetMapping("/uploaded-documents/{id}")
    public ResponseEntity<UploadedDocumentDTO> getUploadedDocument(@PathVariable Long id) {
        log.debug("REST request to get UploadedDocument : {}", id);
        Optional<UploadedDocumentDTO> uploadedDocumentDTO = uploadedDocumentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(uploadedDocumentDTO);
    }

    /**
     * DELETE  /uploaded-documents/:id : delete the "id" uploadedDocument.
     *
     * @param id the id of the uploadedDocumentDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/uploaded-documents/{id}")
    public ResponseEntity<Void> deleteUploadedDocument(@PathVariable Long id) {
        log.debug("REST request to delete UploadedDocument : {}", id);
        uploadedDocumentService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/uploaded-documents?query=:query : search for the uploadedDocument corresponding to the query.
     *
     * @param query    the query of the uploadedDocument search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/uploaded-documents")
    public ResponseEntity<List<UploadedDocumentDTO>> searchUploadedDocuments(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of UploadedDocuments for query {}", query);
        Page<UploadedDocumentDTO> page = uploadedDocumentService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/uploaded-documents");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}
