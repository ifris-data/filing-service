package io.github.ifris.files.web.rest;

import io.github.ifris.files.FilingServiceApp;

import io.github.ifris.files.config.SecurityBeanOverrideConfiguration;

import io.github.ifris.files.domain.UploadedDocument;
import io.github.ifris.files.repository.UploadedDocumentRepository;
import io.github.ifris.files.repository.search.UploadedDocumentSearchRepository;
import io.github.ifris.files.service.UploadedDocumentService;
import io.github.ifris.files.service.dto.UploadedDocumentDTO;
import io.github.ifris.files.service.mapper.UploadedDocumentMapper;
import io.github.ifris.files.web.rest.errors.ExceptionTranslator;
import io.github.ifris.files.service.dto.UploadedDocumentCriteria;
import io.github.ifris.files.service.UploadedDocumentQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;


import static io.github.ifris.files.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the UploadedDocumentResource REST controller.
 *
 * @see UploadedDocumentResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SecurityBeanOverrideConfiguration.class, FilingServiceApp.class})
public class UploadedDocumentResourceIntTest {

    private static final String DEFAULT_FILE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FILE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_YEAR = "AAAAAAAAAA";
    private static final String UPDATED_YEAR = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_PERIOD_START = "AAAAAAAAAA";
    private static final String UPDATED_PERIOD_START = "BBBBBBBBBB";

    private static final String DEFAULT_PERIOD_END = "AAAAAAAAAA";
    private static final String UPDATED_PERIOD_END = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_IFRIS_MODEL = "AAAAAAAAAA";
    private static final String UPDATED_IFRIS_MODEL = "BBBBBBBBBB";

    @Autowired
    private UploadedDocumentRepository uploadedDocumentRepository;

    @Autowired
    private UploadedDocumentMapper uploadedDocumentMapper;

    @Autowired
    private UploadedDocumentService uploadedDocumentService;

    /**
     * This repository is mocked in the io.github.ifris.files.repository.search test package.
     *
     * @see io.github.ifris.files.repository.search.UploadedDocumentSearchRepositoryMockConfiguration
     */
    @Autowired
    private UploadedDocumentSearchRepository mockUploadedDocumentSearchRepository;

    @Autowired
    private UploadedDocumentQueryService uploadedDocumentQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restUploadedDocumentMockMvc;

    private UploadedDocument uploadedDocument;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final UploadedDocumentResource uploadedDocumentResource = new UploadedDocumentResource(uploadedDocumentService, uploadedDocumentQueryService);
        this.restUploadedDocumentMockMvc = MockMvcBuilders.standaloneSetup(uploadedDocumentResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UploadedDocument createEntity(EntityManager em) {
        UploadedDocument uploadedDocument = new UploadedDocument()
            .fileName(DEFAULT_FILE_NAME)
            .year(DEFAULT_YEAR)
            .description(DEFAULT_DESCRIPTION)
            .periodStart(DEFAULT_PERIOD_START)
            .periodEnd(DEFAULT_PERIOD_END)
            .contentType(DEFAULT_CONTENT_TYPE)
            .ifrisModel(DEFAULT_IFRIS_MODEL);
        return uploadedDocument;
    }

    @Before
    public void initTest() {
        uploadedDocument = createEntity(em);
    }

    @Test
    @Transactional
    public void createUploadedDocument() throws Exception {
        int databaseSizeBeforeCreate = uploadedDocumentRepository.findAll().size();

        // Create the UploadedDocument
        UploadedDocumentDTO uploadedDocumentDTO = uploadedDocumentMapper.toDto(uploadedDocument);
        restUploadedDocumentMockMvc.perform(post("/api/uploaded-documents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(uploadedDocumentDTO)))
            .andExpect(status().isCreated());

        // Validate the UploadedDocument in the database
        List<UploadedDocument> uploadedDocumentList = uploadedDocumentRepository.findAll();
        assertThat(uploadedDocumentList).hasSize(databaseSizeBeforeCreate + 1);
        UploadedDocument testUploadedDocument = uploadedDocumentList.get(uploadedDocumentList.size() - 1);
        assertThat(testUploadedDocument.getFileName()).isEqualTo(DEFAULT_FILE_NAME);
        assertThat(testUploadedDocument.getYear()).isEqualTo(DEFAULT_YEAR);
        assertThat(testUploadedDocument.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testUploadedDocument.getPeriodStart()).isEqualTo(DEFAULT_PERIOD_START);
        assertThat(testUploadedDocument.getPeriodEnd()).isEqualTo(DEFAULT_PERIOD_END);
        assertThat(testUploadedDocument.getContentType()).isEqualTo(DEFAULT_CONTENT_TYPE);
        assertThat(testUploadedDocument.getIfrisModel()).isEqualTo(DEFAULT_IFRIS_MODEL);

        // Validate the UploadedDocument in Elasticsearch
        verify(mockUploadedDocumentSearchRepository, times(1)).save(testUploadedDocument);
    }

    @Test
    @Transactional
    public void createUploadedDocumentWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = uploadedDocumentRepository.findAll().size();

        // Create the UploadedDocument with an existing ID
        uploadedDocument.setId(1L);
        UploadedDocumentDTO uploadedDocumentDTO = uploadedDocumentMapper.toDto(uploadedDocument);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUploadedDocumentMockMvc.perform(post("/api/uploaded-documents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(uploadedDocumentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UploadedDocument in the database
        List<UploadedDocument> uploadedDocumentList = uploadedDocumentRepository.findAll();
        assertThat(uploadedDocumentList).hasSize(databaseSizeBeforeCreate);

        // Validate the UploadedDocument in Elasticsearch
        verify(mockUploadedDocumentSearchRepository, times(0)).save(uploadedDocument);
    }

    @Test
    @Transactional
    public void checkFileNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = uploadedDocumentRepository.findAll().size();
        // set the field null
        uploadedDocument.setFileName(null);

        // Create the UploadedDocument, which fails.
        UploadedDocumentDTO uploadedDocumentDTO = uploadedDocumentMapper.toDto(uploadedDocument);

        restUploadedDocumentMockMvc.perform(post("/api/uploaded-documents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(uploadedDocumentDTO)))
            .andExpect(status().isBadRequest());

        List<UploadedDocument> uploadedDocumentList = uploadedDocumentRepository.findAll();
        assertThat(uploadedDocumentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkYearIsRequired() throws Exception {
        int databaseSizeBeforeTest = uploadedDocumentRepository.findAll().size();
        // set the field null
        uploadedDocument.setYear(null);

        // Create the UploadedDocument, which fails.
        UploadedDocumentDTO uploadedDocumentDTO = uploadedDocumentMapper.toDto(uploadedDocument);

        restUploadedDocumentMockMvc.perform(post("/api/uploaded-documents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(uploadedDocumentDTO)))
            .andExpect(status().isBadRequest());

        List<UploadedDocument> uploadedDocumentList = uploadedDocumentRepository.findAll();
        assertThat(uploadedDocumentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = uploadedDocumentRepository.findAll().size();
        // set the field null
        uploadedDocument.setDescription(null);

        // Create the UploadedDocument, which fails.
        UploadedDocumentDTO uploadedDocumentDTO = uploadedDocumentMapper.toDto(uploadedDocument);

        restUploadedDocumentMockMvc.perform(post("/api/uploaded-documents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(uploadedDocumentDTO)))
            .andExpect(status().isBadRequest());

        List<UploadedDocument> uploadedDocumentList = uploadedDocumentRepository.findAll();
        assertThat(uploadedDocumentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPeriodStartIsRequired() throws Exception {
        int databaseSizeBeforeTest = uploadedDocumentRepository.findAll().size();
        // set the field null
        uploadedDocument.setPeriodStart(null);

        // Create the UploadedDocument, which fails.
        UploadedDocumentDTO uploadedDocumentDTO = uploadedDocumentMapper.toDto(uploadedDocument);

        restUploadedDocumentMockMvc.perform(post("/api/uploaded-documents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(uploadedDocumentDTO)))
            .andExpect(status().isBadRequest());

        List<UploadedDocument> uploadedDocumentList = uploadedDocumentRepository.findAll();
        assertThat(uploadedDocumentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPeriodEndIsRequired() throws Exception {
        int databaseSizeBeforeTest = uploadedDocumentRepository.findAll().size();
        // set the field null
        uploadedDocument.setPeriodEnd(null);

        // Create the UploadedDocument, which fails.
        UploadedDocumentDTO uploadedDocumentDTO = uploadedDocumentMapper.toDto(uploadedDocument);

        restUploadedDocumentMockMvc.perform(post("/api/uploaded-documents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(uploadedDocumentDTO)))
            .andExpect(status().isBadRequest());

        List<UploadedDocument> uploadedDocumentList = uploadedDocumentRepository.findAll();
        assertThat(uploadedDocumentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkContentTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = uploadedDocumentRepository.findAll().size();
        // set the field null
        uploadedDocument.setContentType(null);

        // Create the UploadedDocument, which fails.
        UploadedDocumentDTO uploadedDocumentDTO = uploadedDocumentMapper.toDto(uploadedDocument);

        restUploadedDocumentMockMvc.perform(post("/api/uploaded-documents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(uploadedDocumentDTO)))
            .andExpect(status().isBadRequest());

        List<UploadedDocument> uploadedDocumentList = uploadedDocumentRepository.findAll();
        assertThat(uploadedDocumentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkIfrisModelIsRequired() throws Exception {
        int databaseSizeBeforeTest = uploadedDocumentRepository.findAll().size();
        // set the field null
        uploadedDocument.setIfrisModel(null);

        // Create the UploadedDocument, which fails.
        UploadedDocumentDTO uploadedDocumentDTO = uploadedDocumentMapper.toDto(uploadedDocument);

        restUploadedDocumentMockMvc.perform(post("/api/uploaded-documents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(uploadedDocumentDTO)))
            .andExpect(status().isBadRequest());

        List<UploadedDocument> uploadedDocumentList = uploadedDocumentRepository.findAll();
        assertThat(uploadedDocumentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllUploadedDocuments() throws Exception {
        // Initialize the database
        uploadedDocumentRepository.saveAndFlush(uploadedDocument);

        // Get all the uploadedDocumentList
        restUploadedDocumentMockMvc.perform(get("/api/uploaded-documents?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(uploadedDocument.getId().intValue())))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME.toString())))
            .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].periodStart").value(hasItem(DEFAULT_PERIOD_START.toString())))
            .andExpect(jsonPath("$.[*].periodEnd").value(hasItem(DEFAULT_PERIOD_END.toString())))
            .andExpect(jsonPath("$.[*].contentType").value(hasItem(DEFAULT_CONTENT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].ifrisModel").value(hasItem(DEFAULT_IFRIS_MODEL.toString())));
    }
    
    @Test
    @Transactional
    public void getUploadedDocument() throws Exception {
        // Initialize the database
        uploadedDocumentRepository.saveAndFlush(uploadedDocument);

        // Get the uploadedDocument
        restUploadedDocumentMockMvc.perform(get("/api/uploaded-documents/{id}", uploadedDocument.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(uploadedDocument.getId().intValue()))
            .andExpect(jsonPath("$.fileName").value(DEFAULT_FILE_NAME.toString()))
            .andExpect(jsonPath("$.year").value(DEFAULT_YEAR.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.periodStart").value(DEFAULT_PERIOD_START.toString()))
            .andExpect(jsonPath("$.periodEnd").value(DEFAULT_PERIOD_END.toString()))
            .andExpect(jsonPath("$.contentType").value(DEFAULT_CONTENT_TYPE.toString()))
            .andExpect(jsonPath("$.ifrisModel").value(DEFAULT_IFRIS_MODEL.toString()));
    }

    @Test
    @Transactional
    public void getAllUploadedDocumentsByFileNameIsEqualToSomething() throws Exception {
        // Initialize the database
        uploadedDocumentRepository.saveAndFlush(uploadedDocument);

        // Get all the uploadedDocumentList where fileName equals to DEFAULT_FILE_NAME
        defaultUploadedDocumentShouldBeFound("fileName.equals=" + DEFAULT_FILE_NAME);

        // Get all the uploadedDocumentList where fileName equals to UPDATED_FILE_NAME
        defaultUploadedDocumentShouldNotBeFound("fileName.equals=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    public void getAllUploadedDocumentsByFileNameIsInShouldWork() throws Exception {
        // Initialize the database
        uploadedDocumentRepository.saveAndFlush(uploadedDocument);

        // Get all the uploadedDocumentList where fileName in DEFAULT_FILE_NAME or UPDATED_FILE_NAME
        defaultUploadedDocumentShouldBeFound("fileName.in=" + DEFAULT_FILE_NAME + "," + UPDATED_FILE_NAME);

        // Get all the uploadedDocumentList where fileName equals to UPDATED_FILE_NAME
        defaultUploadedDocumentShouldNotBeFound("fileName.in=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    public void getAllUploadedDocumentsByFileNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        uploadedDocumentRepository.saveAndFlush(uploadedDocument);

        // Get all the uploadedDocumentList where fileName is not null
        defaultUploadedDocumentShouldBeFound("fileName.specified=true");

        // Get all the uploadedDocumentList where fileName is null
        defaultUploadedDocumentShouldNotBeFound("fileName.specified=false");
    }

    @Test
    @Transactional
    public void getAllUploadedDocumentsByYearIsEqualToSomething() throws Exception {
        // Initialize the database
        uploadedDocumentRepository.saveAndFlush(uploadedDocument);

        // Get all the uploadedDocumentList where year equals to DEFAULT_YEAR
        defaultUploadedDocumentShouldBeFound("year.equals=" + DEFAULT_YEAR);

        // Get all the uploadedDocumentList where year equals to UPDATED_YEAR
        defaultUploadedDocumentShouldNotBeFound("year.equals=" + UPDATED_YEAR);
    }

    @Test
    @Transactional
    public void getAllUploadedDocumentsByYearIsInShouldWork() throws Exception {
        // Initialize the database
        uploadedDocumentRepository.saveAndFlush(uploadedDocument);

        // Get all the uploadedDocumentList where year in DEFAULT_YEAR or UPDATED_YEAR
        defaultUploadedDocumentShouldBeFound("year.in=" + DEFAULT_YEAR + "," + UPDATED_YEAR);

        // Get all the uploadedDocumentList where year equals to UPDATED_YEAR
        defaultUploadedDocumentShouldNotBeFound("year.in=" + UPDATED_YEAR);
    }

    @Test
    @Transactional
    public void getAllUploadedDocumentsByYearIsNullOrNotNull() throws Exception {
        // Initialize the database
        uploadedDocumentRepository.saveAndFlush(uploadedDocument);

        // Get all the uploadedDocumentList where year is not null
        defaultUploadedDocumentShouldBeFound("year.specified=true");

        // Get all the uploadedDocumentList where year is null
        defaultUploadedDocumentShouldNotBeFound("year.specified=false");
    }

    @Test
    @Transactional
    public void getAllUploadedDocumentsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        uploadedDocumentRepository.saveAndFlush(uploadedDocument);

        // Get all the uploadedDocumentList where description equals to DEFAULT_DESCRIPTION
        defaultUploadedDocumentShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the uploadedDocumentList where description equals to UPDATED_DESCRIPTION
        defaultUploadedDocumentShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllUploadedDocumentsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        uploadedDocumentRepository.saveAndFlush(uploadedDocument);

        // Get all the uploadedDocumentList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultUploadedDocumentShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the uploadedDocumentList where description equals to UPDATED_DESCRIPTION
        defaultUploadedDocumentShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllUploadedDocumentsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        uploadedDocumentRepository.saveAndFlush(uploadedDocument);

        // Get all the uploadedDocumentList where description is not null
        defaultUploadedDocumentShouldBeFound("description.specified=true");

        // Get all the uploadedDocumentList where description is null
        defaultUploadedDocumentShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    public void getAllUploadedDocumentsByPeriodStartIsEqualToSomething() throws Exception {
        // Initialize the database
        uploadedDocumentRepository.saveAndFlush(uploadedDocument);

        // Get all the uploadedDocumentList where periodStart equals to DEFAULT_PERIOD_START
        defaultUploadedDocumentShouldBeFound("periodStart.equals=" + DEFAULT_PERIOD_START);

        // Get all the uploadedDocumentList where periodStart equals to UPDATED_PERIOD_START
        defaultUploadedDocumentShouldNotBeFound("periodStart.equals=" + UPDATED_PERIOD_START);
    }

    @Test
    @Transactional
    public void getAllUploadedDocumentsByPeriodStartIsInShouldWork() throws Exception {
        // Initialize the database
        uploadedDocumentRepository.saveAndFlush(uploadedDocument);

        // Get all the uploadedDocumentList where periodStart in DEFAULT_PERIOD_START or UPDATED_PERIOD_START
        defaultUploadedDocumentShouldBeFound("periodStart.in=" + DEFAULT_PERIOD_START + "," + UPDATED_PERIOD_START);

        // Get all the uploadedDocumentList where periodStart equals to UPDATED_PERIOD_START
        defaultUploadedDocumentShouldNotBeFound("periodStart.in=" + UPDATED_PERIOD_START);
    }

    @Test
    @Transactional
    public void getAllUploadedDocumentsByPeriodStartIsNullOrNotNull() throws Exception {
        // Initialize the database
        uploadedDocumentRepository.saveAndFlush(uploadedDocument);

        // Get all the uploadedDocumentList where periodStart is not null
        defaultUploadedDocumentShouldBeFound("periodStart.specified=true");

        // Get all the uploadedDocumentList where periodStart is null
        defaultUploadedDocumentShouldNotBeFound("periodStart.specified=false");
    }

    @Test
    @Transactional
    public void getAllUploadedDocumentsByPeriodEndIsEqualToSomething() throws Exception {
        // Initialize the database
        uploadedDocumentRepository.saveAndFlush(uploadedDocument);

        // Get all the uploadedDocumentList where periodEnd equals to DEFAULT_PERIOD_END
        defaultUploadedDocumentShouldBeFound("periodEnd.equals=" + DEFAULT_PERIOD_END);

        // Get all the uploadedDocumentList where periodEnd equals to UPDATED_PERIOD_END
        defaultUploadedDocumentShouldNotBeFound("periodEnd.equals=" + UPDATED_PERIOD_END);
    }

    @Test
    @Transactional
    public void getAllUploadedDocumentsByPeriodEndIsInShouldWork() throws Exception {
        // Initialize the database
        uploadedDocumentRepository.saveAndFlush(uploadedDocument);

        // Get all the uploadedDocumentList where periodEnd in DEFAULT_PERIOD_END or UPDATED_PERIOD_END
        defaultUploadedDocumentShouldBeFound("periodEnd.in=" + DEFAULT_PERIOD_END + "," + UPDATED_PERIOD_END);

        // Get all the uploadedDocumentList where periodEnd equals to UPDATED_PERIOD_END
        defaultUploadedDocumentShouldNotBeFound("periodEnd.in=" + UPDATED_PERIOD_END);
    }

    @Test
    @Transactional
    public void getAllUploadedDocumentsByPeriodEndIsNullOrNotNull() throws Exception {
        // Initialize the database
        uploadedDocumentRepository.saveAndFlush(uploadedDocument);

        // Get all the uploadedDocumentList where periodEnd is not null
        defaultUploadedDocumentShouldBeFound("periodEnd.specified=true");

        // Get all the uploadedDocumentList where periodEnd is null
        defaultUploadedDocumentShouldNotBeFound("periodEnd.specified=false");
    }

    @Test
    @Transactional
    public void getAllUploadedDocumentsByContentTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        uploadedDocumentRepository.saveAndFlush(uploadedDocument);

        // Get all the uploadedDocumentList where contentType equals to DEFAULT_CONTENT_TYPE
        defaultUploadedDocumentShouldBeFound("contentType.equals=" + DEFAULT_CONTENT_TYPE);

        // Get all the uploadedDocumentList where contentType equals to UPDATED_CONTENT_TYPE
        defaultUploadedDocumentShouldNotBeFound("contentType.equals=" + UPDATED_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void getAllUploadedDocumentsByContentTypeIsInShouldWork() throws Exception {
        // Initialize the database
        uploadedDocumentRepository.saveAndFlush(uploadedDocument);

        // Get all the uploadedDocumentList where contentType in DEFAULT_CONTENT_TYPE or UPDATED_CONTENT_TYPE
        defaultUploadedDocumentShouldBeFound("contentType.in=" + DEFAULT_CONTENT_TYPE + "," + UPDATED_CONTENT_TYPE);

        // Get all the uploadedDocumentList where contentType equals to UPDATED_CONTENT_TYPE
        defaultUploadedDocumentShouldNotBeFound("contentType.in=" + UPDATED_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void getAllUploadedDocumentsByContentTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        uploadedDocumentRepository.saveAndFlush(uploadedDocument);

        // Get all the uploadedDocumentList where contentType is not null
        defaultUploadedDocumentShouldBeFound("contentType.specified=true");

        // Get all the uploadedDocumentList where contentType is null
        defaultUploadedDocumentShouldNotBeFound("contentType.specified=false");
    }

    @Test
    @Transactional
    public void getAllUploadedDocumentsByIfrisModelIsEqualToSomething() throws Exception {
        // Initialize the database
        uploadedDocumentRepository.saveAndFlush(uploadedDocument);

        // Get all the uploadedDocumentList where ifrisModel equals to DEFAULT_IFRIS_MODEL
        defaultUploadedDocumentShouldBeFound("ifrisModel.equals=" + DEFAULT_IFRIS_MODEL);

        // Get all the uploadedDocumentList where ifrisModel equals to UPDATED_IFRIS_MODEL
        defaultUploadedDocumentShouldNotBeFound("ifrisModel.equals=" + UPDATED_IFRIS_MODEL);
    }

    @Test
    @Transactional
    public void getAllUploadedDocumentsByIfrisModelIsInShouldWork() throws Exception {
        // Initialize the database
        uploadedDocumentRepository.saveAndFlush(uploadedDocument);

        // Get all the uploadedDocumentList where ifrisModel in DEFAULT_IFRIS_MODEL or UPDATED_IFRIS_MODEL
        defaultUploadedDocumentShouldBeFound("ifrisModel.in=" + DEFAULT_IFRIS_MODEL + "," + UPDATED_IFRIS_MODEL);

        // Get all the uploadedDocumentList where ifrisModel equals to UPDATED_IFRIS_MODEL
        defaultUploadedDocumentShouldNotBeFound("ifrisModel.in=" + UPDATED_IFRIS_MODEL);
    }

    @Test
    @Transactional
    public void getAllUploadedDocumentsByIfrisModelIsNullOrNotNull() throws Exception {
        // Initialize the database
        uploadedDocumentRepository.saveAndFlush(uploadedDocument);

        // Get all the uploadedDocumentList where ifrisModel is not null
        defaultUploadedDocumentShouldBeFound("ifrisModel.specified=true");

        // Get all the uploadedDocumentList where ifrisModel is null
        defaultUploadedDocumentShouldNotBeFound("ifrisModel.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultUploadedDocumentShouldBeFound(String filter) throws Exception {
        restUploadedDocumentMockMvc.perform(get("/api/uploaded-documents?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(uploadedDocument.getId().intValue())))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].periodStart").value(hasItem(DEFAULT_PERIOD_START)))
            .andExpect(jsonPath("$.[*].periodEnd").value(hasItem(DEFAULT_PERIOD_END)))
            .andExpect(jsonPath("$.[*].contentType").value(hasItem(DEFAULT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].ifrisModel").value(hasItem(DEFAULT_IFRIS_MODEL)));

        // Check, that the count call also returns 1
        restUploadedDocumentMockMvc.perform(get("/api/uploaded-documents/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultUploadedDocumentShouldNotBeFound(String filter) throws Exception {
        restUploadedDocumentMockMvc.perform(get("/api/uploaded-documents?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUploadedDocumentMockMvc.perform(get("/api/uploaded-documents/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingUploadedDocument() throws Exception {
        // Get the uploadedDocument
        restUploadedDocumentMockMvc.perform(get("/api/uploaded-documents/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUploadedDocument() throws Exception {
        // Initialize the database
        uploadedDocumentRepository.saveAndFlush(uploadedDocument);

        int databaseSizeBeforeUpdate = uploadedDocumentRepository.findAll().size();

        // Update the uploadedDocument
        UploadedDocument updatedUploadedDocument = uploadedDocumentRepository.findById(uploadedDocument.getId()).get();
        // Disconnect from session so that the updates on updatedUploadedDocument are not directly saved in db
        em.detach(updatedUploadedDocument);
        updatedUploadedDocument
            .fileName(UPDATED_FILE_NAME)
            .year(UPDATED_YEAR)
            .description(UPDATED_DESCRIPTION)
            .periodStart(UPDATED_PERIOD_START)
            .periodEnd(UPDATED_PERIOD_END)
            .contentType(UPDATED_CONTENT_TYPE)
            .ifrisModel(UPDATED_IFRIS_MODEL);
        UploadedDocumentDTO uploadedDocumentDTO = uploadedDocumentMapper.toDto(updatedUploadedDocument);

        restUploadedDocumentMockMvc.perform(put("/api/uploaded-documents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(uploadedDocumentDTO)))
            .andExpect(status().isOk());

        // Validate the UploadedDocument in the database
        List<UploadedDocument> uploadedDocumentList = uploadedDocumentRepository.findAll();
        assertThat(uploadedDocumentList).hasSize(databaseSizeBeforeUpdate);
        UploadedDocument testUploadedDocument = uploadedDocumentList.get(uploadedDocumentList.size() - 1);
        assertThat(testUploadedDocument.getFileName()).isEqualTo(UPDATED_FILE_NAME);
        assertThat(testUploadedDocument.getYear()).isEqualTo(UPDATED_YEAR);
        assertThat(testUploadedDocument.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testUploadedDocument.getPeriodStart()).isEqualTo(UPDATED_PERIOD_START);
        assertThat(testUploadedDocument.getPeriodEnd()).isEqualTo(UPDATED_PERIOD_END);
        assertThat(testUploadedDocument.getContentType()).isEqualTo(UPDATED_CONTENT_TYPE);
        assertThat(testUploadedDocument.getIfrisModel()).isEqualTo(UPDATED_IFRIS_MODEL);

        // Validate the UploadedDocument in Elasticsearch
        verify(mockUploadedDocumentSearchRepository, times(1)).save(testUploadedDocument);
    }

    @Test
    @Transactional
    public void updateNonExistingUploadedDocument() throws Exception {
        int databaseSizeBeforeUpdate = uploadedDocumentRepository.findAll().size();

        // Create the UploadedDocument
        UploadedDocumentDTO uploadedDocumentDTO = uploadedDocumentMapper.toDto(uploadedDocument);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUploadedDocumentMockMvc.perform(put("/api/uploaded-documents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(uploadedDocumentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UploadedDocument in the database
        List<UploadedDocument> uploadedDocumentList = uploadedDocumentRepository.findAll();
        assertThat(uploadedDocumentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UploadedDocument in Elasticsearch
        verify(mockUploadedDocumentSearchRepository, times(0)).save(uploadedDocument);
    }

    @Test
    @Transactional
    public void deleteUploadedDocument() throws Exception {
        // Initialize the database
        uploadedDocumentRepository.saveAndFlush(uploadedDocument);

        int databaseSizeBeforeDelete = uploadedDocumentRepository.findAll().size();

        // Delete the uploadedDocument
        restUploadedDocumentMockMvc.perform(delete("/api/uploaded-documents/{id}", uploadedDocument.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<UploadedDocument> uploadedDocumentList = uploadedDocumentRepository.findAll();
        assertThat(uploadedDocumentList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the UploadedDocument in Elasticsearch
        verify(mockUploadedDocumentSearchRepository, times(1)).deleteById(uploadedDocument.getId());
    }

    @Test
    @Transactional
    public void searchUploadedDocument() throws Exception {
        // Initialize the database
        uploadedDocumentRepository.saveAndFlush(uploadedDocument);
        when(mockUploadedDocumentSearchRepository.search(queryStringQuery("id:" + uploadedDocument.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(uploadedDocument), PageRequest.of(0, 1), 1));
        // Search the uploadedDocument
        restUploadedDocumentMockMvc.perform(get("/api/_search/uploaded-documents?query=id:" + uploadedDocument.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(uploadedDocument.getId().intValue())))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].periodStart").value(hasItem(DEFAULT_PERIOD_START)))
            .andExpect(jsonPath("$.[*].periodEnd").value(hasItem(DEFAULT_PERIOD_END)))
            .andExpect(jsonPath("$.[*].contentType").value(hasItem(DEFAULT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].ifrisModel").value(hasItem(DEFAULT_IFRIS_MODEL)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UploadedDocument.class);
        UploadedDocument uploadedDocument1 = new UploadedDocument();
        uploadedDocument1.setId(1L);
        UploadedDocument uploadedDocument2 = new UploadedDocument();
        uploadedDocument2.setId(uploadedDocument1.getId());
        assertThat(uploadedDocument1).isEqualTo(uploadedDocument2);
        uploadedDocument2.setId(2L);
        assertThat(uploadedDocument1).isNotEqualTo(uploadedDocument2);
        uploadedDocument1.setId(null);
        assertThat(uploadedDocument1).isNotEqualTo(uploadedDocument2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UploadedDocumentDTO.class);
        UploadedDocumentDTO uploadedDocumentDTO1 = new UploadedDocumentDTO();
        uploadedDocumentDTO1.setId(1L);
        UploadedDocumentDTO uploadedDocumentDTO2 = new UploadedDocumentDTO();
        assertThat(uploadedDocumentDTO1).isNotEqualTo(uploadedDocumentDTO2);
        uploadedDocumentDTO2.setId(uploadedDocumentDTO1.getId());
        assertThat(uploadedDocumentDTO1).isEqualTo(uploadedDocumentDTO2);
        uploadedDocumentDTO2.setId(2L);
        assertThat(uploadedDocumentDTO1).isNotEqualTo(uploadedDocumentDTO2);
        uploadedDocumentDTO1.setId(null);
        assertThat(uploadedDocumentDTO1).isNotEqualTo(uploadedDocumentDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(uploadedDocumentMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(uploadedDocumentMapper.fromId(null)).isNull();
    }
}
