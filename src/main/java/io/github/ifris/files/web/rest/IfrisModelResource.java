package io.github.ifris.files.web.rest;

import io.github.ifris.files.service.IfrisModelQueryService;
import io.github.ifris.files.service.IfrisModelService;
import io.github.ifris.files.service.dto.IfrisModelCriteria;
import io.github.ifris.files.service.dto.IfrisModelDTO;
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
 * REST controller for managing IfrisModel.
 */
@RestController
@RequestMapping("/api")
public class IfrisModelResource {

    private static final String ENTITY_NAME = "filingServiceIfrisModel";
    private final Logger log = LoggerFactory.getLogger(IfrisModelResource.class);
    private final IfrisModelService ifrisModelService;

    private final IfrisModelQueryService ifrisModelQueryService;

    public IfrisModelResource(IfrisModelService ifrisModelService, IfrisModelQueryService ifrisModelQueryService) {
        this.ifrisModelService = ifrisModelService;
        this.ifrisModelQueryService = ifrisModelQueryService;
    }

    /**
     * POST  /ifris-models : Create a new ifrisModel.
     *
     * @param ifrisModelDTO the ifrisModelDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new ifrisModelDTO, or with status 400 (Bad Request) if the ifrisModel has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/ifris-models")
    public ResponseEntity<IfrisModelDTO> createIfrisModel(@Valid @RequestBody IfrisModelDTO ifrisModelDTO) throws URISyntaxException {
        log.debug("REST request to save IfrisModel : {}", ifrisModelDTO);
        if (ifrisModelDTO.getId() != null) {
            throw new BadRequestAlertException("A new ifrisModel cannot already have an ID", ENTITY_NAME, "idexists");
        }
        IfrisModelDTO result = ifrisModelService.save(ifrisModelDTO);
        return ResponseEntity.created(new URI("/api/ifris-models/" + result.getId())).headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString())).body(result);
    }

    /**
     * PUT  /ifris-models : Updates an existing ifrisModel.
     *
     * @param ifrisModelDTO the ifrisModelDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated ifrisModelDTO, or with status 400 (Bad Request) if the ifrisModelDTO is not valid, or with status 500 (Internal Server
     * Error) if the ifrisModelDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/ifris-models")
    public ResponseEntity<IfrisModelDTO> updateIfrisModel(@Valid @RequestBody IfrisModelDTO ifrisModelDTO) throws URISyntaxException {
        log.debug("REST request to update IfrisModel : {}", ifrisModelDTO);
        if (ifrisModelDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        IfrisModelDTO result = ifrisModelService.save(ifrisModelDTO);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, ifrisModelDTO.getId().toString())).body(result);
    }

    /**
     * GET  /ifris-models : get all the ifrisModels.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of ifrisModels in body
     */
    @GetMapping("/ifris-models")
    public ResponseEntity<List<IfrisModelDTO>> getAllIfrisModels(IfrisModelCriteria criteria, Pageable pageable) {
        log.debug("REST request to get IfrisModels by criteria: {}", criteria);
        Page<IfrisModelDTO> page = ifrisModelQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/ifris-models");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /ifris-models/count : count all the ifrisModels.
     *
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the count in body
     */
    @GetMapping("/ifris-models/count")
    public ResponseEntity<Long> countIfrisModels(IfrisModelCriteria criteria) {
        log.debug("REST request to count IfrisModels by criteria: {}", criteria);
        return ResponseEntity.ok().body(ifrisModelQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /ifris-models/:id : get the "id" ifrisModel.
     *
     * @param id the id of the ifrisModelDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the ifrisModelDTO, or with status 404 (Not Found)
     */
    @GetMapping("/ifris-models/{id}")
    public ResponseEntity<IfrisModelDTO> getIfrisModel(@PathVariable Long id) {
        log.debug("REST request to get IfrisModel : {}", id);
        Optional<IfrisModelDTO> ifrisModelDTO = ifrisModelService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ifrisModelDTO);
    }

    /**
     * DELETE  /ifris-models/:id : delete the "id" ifrisModel.
     *
     * @param id the id of the ifrisModelDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/ifris-models/{id}")
    public ResponseEntity<Void> deleteIfrisModel(@PathVariable Long id) {
        log.debug("REST request to delete IfrisModel : {}", id);
        ifrisModelService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/ifris-models?query=:query : search for the ifrisModel corresponding to the query.
     *
     * @param query    the query of the ifrisModel search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/ifris-models")
    public ResponseEntity<List<IfrisModelDTO>> searchIfrisModels(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of IfrisModels for query {}", query);
        Page<IfrisModelDTO> page = ifrisModelService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/ifris-models");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}
