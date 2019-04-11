package io.github.ifris.files.web.rest;

import io.github.ifris.files.mq.UploadedDocumentProducerChannel;
import io.github.ifris.files.service.IfrisDocumentQueryService;
import io.github.ifris.files.service.IfrisDocumentService;
import io.github.ifris.files.service.UploadedDocumentService;
import io.github.ifris.files.service.dto.IfrisDocumentCriteria;
import io.github.ifris.files.service.dto.IfrisDocumentDTO;
import io.github.ifris.files.service.dto.UploadedDocumentDTO;
import io.github.ifris.files.web.rest.errors.BadRequestAlertException;
import io.github.ifris.files.web.rest.util.HeaderUtil;
import io.github.ifris.files.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing IfrisDocument.
 */
@RestController
@RequestMapping("/api")
public class IfrisDocumentResource {

    @Value("${eureka.instance.instanceId:JhipsterService}")
    private String instanceName;

    private final UploadedDocumentService uploadedDocumentService;
    private static final String ENTITY_NAME = "filingServiceIfrisDocument";
    private final Logger log = LoggerFactory.getLogger(IfrisDocumentResource.class);
    private final IfrisDocumentService ifrisDocumentService;
    private final IfrisDocumentQueryService ifrisDocumentQueryService;
    private MessageChannel uploadedDocumentProducerChannel;

    @Autowired
    public IfrisDocumentResource(IfrisDocumentService ifrisDocumentService, IfrisDocumentQueryService ifrisDocumentQueryService,
                                 final UploadedDocumentService uploadedDocumentService, UploadedDocumentProducerChannel producerChannel) {
        this.ifrisDocumentService = ifrisDocumentService;
        this.ifrisDocumentQueryService = ifrisDocumentQueryService;
        this.uploadedDocumentProducerChannel = producerChannel.uploadedDocumentChannel();
        this.uploadedDocumentService = uploadedDocumentService;
    }

    /**
     * POST  /ifris-documents : Create a new ifrisDocument.
     *
     * @param ifrisDocumentDTO the ifrisDocumentDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new ifrisDocumentDTO, or with status 400 (Bad Request) if the ifrisDocument has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/ifris-documents")
    public ResponseEntity<IfrisDocumentDTO> createIfrisDocument(@Valid @RequestBody IfrisDocumentDTO ifrisDocumentDTO) throws URISyntaxException {
        log.debug("REST request to save IfrisDocument : {}", ifrisDocumentDTO);
        if (ifrisDocumentDTO.getId() != null) {
            throw new BadRequestAlertException("A new ifrisDocument cannot already have an ID", ENTITY_NAME, "idexists");
        }
        IfrisDocumentDTO result = ifrisDocumentService.save(ifrisDocumentDTO);

        trackDocument(ifrisDocumentDTO);

        return ResponseEntity.created(new URI("/api/ifris-documents/" + result.getId())).headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString())).body(result);
    }

    /**
     * Keep track of ifrisDocuments handled by this instance
     *
     * @param ifrisDocumentDTO
     */
    private synchronized void trackDocument(final @Valid @RequestBody IfrisDocumentDTO ifrisDocumentDTO) {
        UploadedDocumentDTO documentDTO = new UploadedDocumentDTO();
        documentDTO.setFileName(ifrisDocumentDTO.getFileName());
        documentDTO.setContentType(ifrisDocumentDTO.getContentContentType());
        documentDTO.setDescription(ifrisDocumentDTO.getDescription());
        documentDTO.setYear(ifrisDocumentDTO.getYear().toString());
        documentDTO.setPeriodStart(ifrisDocumentDTO.getPeriodStart().format(DateTimeFormatter.ISO_DATE));
        documentDTO.setPeriodEnd(ifrisDocumentDTO.getPeriodEnd().format(DateTimeFormatter.ISO_DATE));
        documentDTO.setContentType(ifrisDocumentDTO.getContentContentType());
        documentDTO.setAppInstance(instanceName);
        // send message to channel
        uploadedDocumentProducerChannel.send(
            MessageBuilder.withPayload(documentDTO).build());
        // persist the UploadedDocument
        uploadedDocumentService.save(documentDTO);
    }

    /**
     * PUT  /ifris-documents : Updates an existing ifrisDocument.
     *
     * @param ifrisDocumentDTO the ifrisDocumentDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated ifrisDocumentDTO, or with status 400 (Bad Request) if the ifrisDocumentDTO is not valid, or with status 500 (Internal
     * Server Error) if the ifrisDocumentDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/ifris-documents")
    public ResponseEntity<IfrisDocumentDTO> updateIfrisDocument(@Valid @RequestBody IfrisDocumentDTO ifrisDocumentDTO) throws URISyntaxException {
        log.debug("REST request to update IfrisDocument : {}", ifrisDocumentDTO);
        if (ifrisDocumentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        IfrisDocumentDTO result = ifrisDocumentService.save(ifrisDocumentDTO);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, ifrisDocumentDTO.getId().toString())).body(result);
    }

    /**
     * GET  /ifris-documents : get all the ifrisDocuments.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of ifrisDocuments in body
     */
    @GetMapping("/ifris-documents")
    public ResponseEntity<List<IfrisDocumentDTO>> getAllIfrisDocuments(IfrisDocumentCriteria criteria, Pageable pageable) {
        log.debug("REST request to get IfrisDocuments by criteria: {}", criteria);
        Page<IfrisDocumentDTO> page = ifrisDocumentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/ifris-documents");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /ifris-documents/count : count all the ifrisDocuments.
     *
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the count in body
     */
    @GetMapping("/ifris-documents/count")
    public ResponseEntity<Long> countIfrisDocuments(IfrisDocumentCriteria criteria) {
        log.debug("REST request to count IfrisDocuments by criteria: {}", criteria);
        return ResponseEntity.ok().body(ifrisDocumentQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /ifris-documents/:id : get the "id" ifrisDocument.
     *
     * @param id the id of the ifrisDocumentDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the ifrisDocumentDTO, or with status 404 (Not Found)
     */
    @GetMapping("/ifris-documents/{id}")
    public ResponseEntity<IfrisDocumentDTO> getIfrisDocument(@PathVariable Long id) {
        log.debug("REST request to get IfrisDocument : {}", id);
        Optional<IfrisDocumentDTO> ifrisDocumentDTO = ifrisDocumentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ifrisDocumentDTO);
    }

    /**
     * DELETE  /ifris-documents/:id : delete the "id" ifrisDocument.
     *
     * @param id the id of the ifrisDocumentDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/ifris-documents/{id}")
    public ResponseEntity<Void> deleteIfrisDocument(@PathVariable Long id) {
        log.debug("REST request to delete IfrisDocument : {}", id);
        ifrisDocumentService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/ifris-documents?query=:query : search for the ifrisDocument corresponding to the query.
     *
     * @param query    the query of the ifrisDocument search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/ifris-documents")
    public ResponseEntity<List<IfrisDocumentDTO>> searchIfrisDocuments(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of IfrisDocuments for query {}", query);
        Page<IfrisDocumentDTO> page = ifrisDocumentService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/ifris-documents");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}
