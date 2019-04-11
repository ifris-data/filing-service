package io.github.ifris.files.web.rest;

import io.github.ifris.files.FilingServiceApp;
import io.github.ifris.files.config.SecurityBeanOverrideConfiguration;
import io.github.ifris.files.domain.IfrisDocument;
import io.github.ifris.files.domain.IfrisModel;
import io.github.ifris.files.repository.IfrisDocumentRepository;
import io.github.ifris.files.repository.search.IfrisDocumentSearchRepository;
import io.github.ifris.files.service.IfrisDocumentQueryService;
import io.github.ifris.files.service.IfrisDocumentService;
import io.github.ifris.files.service.dto.IfrisDocumentDTO;
import io.github.ifris.files.service.mapper.IfrisDocumentMapper;
import io.github.ifris.files.web.rest.errors.ExceptionTranslator;
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
import org.springframework.util.Base64Utils;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static io.github.ifris.files.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the IfrisDocumentResource REST controller.
 *
 * @see IfrisDocumentResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SecurityBeanOverrideConfiguration.class, FilingServiceApp.class})
public class IfrisDocumentResourceIntTest {

    private static final String DEFAULT_FILE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FILE_NAME = "BBBBBBBBBB";

    private static final Long DEFAULT_YEAR = 1L;
    private static final Long UPDATED_YEAR = 2L;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_PERIOD_START = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PERIOD_START = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_PERIOD_END = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PERIOD_END = LocalDate.now(ZoneId.systemDefault());

    private static final byte[] DEFAULT_CONTENT = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_CONTENT = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_CONTENT_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_CONTENT_CONTENT_TYPE = "image/png";

    @Autowired
    private IfrisDocumentRepository ifrisDocumentRepository;

    @Autowired
    private IfrisDocumentMapper ifrisDocumentMapper;

    @Autowired
    private IfrisDocumentService ifrisDocumentService;

    /**
     * This repository is mocked in the io.github.ifris.files.repository.search test package.
     *
     * @see io.github.ifris.files.repository.search.IfrisDocumentSearchRepositoryMockConfiguration
     */
    @Autowired
    private IfrisDocumentSearchRepository mockIfrisDocumentSearchRepository;

    @Autowired
    private IfrisDocumentQueryService ifrisDocumentQueryService;

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

    private MockMvc restIfrisDocumentMockMvc;

    private IfrisDocument ifrisDocument;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it, if they test an entity which requires the current entity.
     */
    public static IfrisDocument createEntity(EntityManager em) {
        IfrisDocument ifrisDocument = new IfrisDocument().fileName(DEFAULT_FILE_NAME)
                                                         .year(DEFAULT_YEAR)
                                                         .description(DEFAULT_DESCRIPTION)
                                                         .periodStart(DEFAULT_PERIOD_START)
                                                         .periodEnd(DEFAULT_PERIOD_END)
                                                         .content(DEFAULT_CONTENT)
                                                         .contentContentType(DEFAULT_CONTENT_CONTENT_TYPE);
        // Add required entity
        IfrisModel ifrisModel = IfrisModelResourceIntTest.createEntity(em);
        em.persist(ifrisModel);
        em.flush();
        ifrisDocument.setIfrisModel(ifrisModel);
        return ifrisDocument;
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final IfrisDocumentResource ifrisDocumentResource = new IfrisDocumentResource(ifrisDocumentService, ifrisDocumentQueryService);
        this.restIfrisDocumentMockMvc = MockMvcBuilders.standaloneSetup(ifrisDocumentResource)
                                                       .setCustomArgumentResolvers(pageableArgumentResolver)
                                                       .setControllerAdvice(exceptionTranslator)
                                                       .setConversionService(createFormattingConversionService())
                                                       .setMessageConverters(jacksonMessageConverter)
                                                       .setValidator(validator)
                                                       .build();
    }

    @Before
    public void initTest() {
        ifrisDocument = createEntity(em);
    }

    @Test
    @Transactional
    public void createIfrisDocument() throws Exception {
        int databaseSizeBeforeCreate = ifrisDocumentRepository.findAll().size();

        // Create the IfrisDocument
        IfrisDocumentDTO ifrisDocumentDTO = ifrisDocumentMapper.toDto(ifrisDocument);
        restIfrisDocumentMockMvc.perform(post("/api/ifris-documents").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(ifrisDocumentDTO)))
                                .andExpect(status().isCreated());

        // Validate the IfrisDocument in the database
        List<IfrisDocument> ifrisDocumentList = ifrisDocumentRepository.findAll();
        assertThat(ifrisDocumentList).hasSize(databaseSizeBeforeCreate + 1);
        IfrisDocument testIfrisDocument = ifrisDocumentList.get(ifrisDocumentList.size() - 1);
        assertThat(testIfrisDocument.getFileName()).isEqualTo(DEFAULT_FILE_NAME);
        assertThat(testIfrisDocument.getYear()).isEqualTo(DEFAULT_YEAR);
        assertThat(testIfrisDocument.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testIfrisDocument.getPeriodStart()).isEqualTo(DEFAULT_PERIOD_START);
        assertThat(testIfrisDocument.getPeriodEnd()).isEqualTo(DEFAULT_PERIOD_END);
        assertThat(testIfrisDocument.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testIfrisDocument.getContentContentType()).isEqualTo(DEFAULT_CONTENT_CONTENT_TYPE);

        // Validate the IfrisDocument in Elasticsearch
        verify(mockIfrisDocumentSearchRepository, times(1)).save(testIfrisDocument);
    }

    @Test
    @Transactional
    public void createIfrisDocumentWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = ifrisDocumentRepository.findAll().size();

        // Create the IfrisDocument with an existing ID
        ifrisDocument.setId(1L);
        IfrisDocumentDTO ifrisDocumentDTO = ifrisDocumentMapper.toDto(ifrisDocument);

        // An entity with an existing ID cannot be created, so this API call must fail
        restIfrisDocumentMockMvc.perform(post("/api/ifris-documents").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(ifrisDocumentDTO)))
                                .andExpect(status().isBadRequest());

        // Validate the IfrisDocument in the database
        List<IfrisDocument> ifrisDocumentList = ifrisDocumentRepository.findAll();
        assertThat(ifrisDocumentList).hasSize(databaseSizeBeforeCreate);

        // Validate the IfrisDocument in Elasticsearch
        verify(mockIfrisDocumentSearchRepository, times(0)).save(ifrisDocument);
    }

    @Test
    @Transactional
    public void checkFileNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = ifrisDocumentRepository.findAll().size();
        // set the field null
        ifrisDocument.setFileName(null);

        // Create the IfrisDocument, which fails.
        IfrisDocumentDTO ifrisDocumentDTO = ifrisDocumentMapper.toDto(ifrisDocument);

        restIfrisDocumentMockMvc.perform(post("/api/ifris-documents").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(ifrisDocumentDTO)))
                                .andExpect(status().isBadRequest());

        List<IfrisDocument> ifrisDocumentList = ifrisDocumentRepository.findAll();
        assertThat(ifrisDocumentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkYearIsRequired() throws Exception {
        int databaseSizeBeforeTest = ifrisDocumentRepository.findAll().size();
        // set the field null
        ifrisDocument.setYear(null);

        // Create the IfrisDocument, which fails.
        IfrisDocumentDTO ifrisDocumentDTO = ifrisDocumentMapper.toDto(ifrisDocument);

        restIfrisDocumentMockMvc.perform(post("/api/ifris-documents").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(ifrisDocumentDTO)))
                                .andExpect(status().isBadRequest());

        List<IfrisDocument> ifrisDocumentList = ifrisDocumentRepository.findAll();
        assertThat(ifrisDocumentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPeriodStartIsRequired() throws Exception {
        int databaseSizeBeforeTest = ifrisDocumentRepository.findAll().size();
        // set the field null
        ifrisDocument.setPeriodStart(null);

        // Create the IfrisDocument, which fails.
        IfrisDocumentDTO ifrisDocumentDTO = ifrisDocumentMapper.toDto(ifrisDocument);

        restIfrisDocumentMockMvc.perform(post("/api/ifris-documents").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(ifrisDocumentDTO)))
                                .andExpect(status().isBadRequest());

        List<IfrisDocument> ifrisDocumentList = ifrisDocumentRepository.findAll();
        assertThat(ifrisDocumentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPeriodEndIsRequired() throws Exception {
        int databaseSizeBeforeTest = ifrisDocumentRepository.findAll().size();
        // set the field null
        ifrisDocument.setPeriodEnd(null);

        // Create the IfrisDocument, which fails.
        IfrisDocumentDTO ifrisDocumentDTO = ifrisDocumentMapper.toDto(ifrisDocument);

        restIfrisDocumentMockMvc.perform(post("/api/ifris-documents").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(ifrisDocumentDTO)))
                                .andExpect(status().isBadRequest());

        List<IfrisDocument> ifrisDocumentList = ifrisDocumentRepository.findAll();
        assertThat(ifrisDocumentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllIfrisDocuments() throws Exception {
        // Initialize the database
        ifrisDocumentRepository.saveAndFlush(ifrisDocument);

        // Get all the ifrisDocumentList
        restIfrisDocumentMockMvc.perform(get("/api/ifris-documents?sort=id,desc"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                                .andExpect(jsonPath("$.[*].id").value(hasItem(ifrisDocument.getId().intValue())))
                                .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME.toString())))
                                .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR.intValue())))
                                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                                .andExpect(jsonPath("$.[*].periodStart").value(hasItem(DEFAULT_PERIOD_START.toString())))
                                .andExpect(jsonPath("$.[*].periodEnd").value(hasItem(DEFAULT_PERIOD_END.toString())))
                                .andExpect(jsonPath("$.[*].contentContentType").value(hasItem(DEFAULT_CONTENT_CONTENT_TYPE)))
                                .andExpect(jsonPath("$.[*].content").value(hasItem(Base64Utils.encodeToString(DEFAULT_CONTENT))));
    }

    @Test
    @Transactional
    public void getIfrisDocument() throws Exception {
        // Initialize the database
        ifrisDocumentRepository.saveAndFlush(ifrisDocument);

        // Get the ifrisDocument
        restIfrisDocumentMockMvc.perform(get("/api/ifris-documents/{id}", ifrisDocument.getId()))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                                .andExpect(jsonPath("$.id").value(ifrisDocument.getId().intValue()))
                                .andExpect(jsonPath("$.fileName").value(DEFAULT_FILE_NAME.toString()))
                                .andExpect(jsonPath("$.year").value(DEFAULT_YEAR.intValue()))
                                .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
                                .andExpect(jsonPath("$.periodStart").value(DEFAULT_PERIOD_START.toString()))
                                .andExpect(jsonPath("$.periodEnd").value(DEFAULT_PERIOD_END.toString()))
                                .andExpect(jsonPath("$.contentContentType").value(DEFAULT_CONTENT_CONTENT_TYPE))
                                .andExpect(jsonPath("$.content").value(Base64Utils.encodeToString(DEFAULT_CONTENT)));
    }

    @Test
    @Transactional
    public void getAllIfrisDocumentsByFileNameIsEqualToSomething() throws Exception {
        // Initialize the database
        ifrisDocumentRepository.saveAndFlush(ifrisDocument);

        // Get all the ifrisDocumentList where fileName equals to DEFAULT_FILE_NAME
        defaultIfrisDocumentShouldBeFound("fileName.equals=" + DEFAULT_FILE_NAME);

        // Get all the ifrisDocumentList where fileName equals to UPDATED_FILE_NAME
        defaultIfrisDocumentShouldNotBeFound("fileName.equals=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    public void getAllIfrisDocumentsByFileNameIsInShouldWork() throws Exception {
        // Initialize the database
        ifrisDocumentRepository.saveAndFlush(ifrisDocument);

        // Get all the ifrisDocumentList where fileName in DEFAULT_FILE_NAME or UPDATED_FILE_NAME
        defaultIfrisDocumentShouldBeFound("fileName.in=" + DEFAULT_FILE_NAME + "," + UPDATED_FILE_NAME);

        // Get all the ifrisDocumentList where fileName equals to UPDATED_FILE_NAME
        defaultIfrisDocumentShouldNotBeFound("fileName.in=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    public void getAllIfrisDocumentsByFileNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        ifrisDocumentRepository.saveAndFlush(ifrisDocument);

        // Get all the ifrisDocumentList where fileName is not null
        defaultIfrisDocumentShouldBeFound("fileName.specified=true");

        // Get all the ifrisDocumentList where fileName is null
        defaultIfrisDocumentShouldNotBeFound("fileName.specified=false");
    }

    @Test
    @Transactional
    public void getAllIfrisDocumentsByYearIsEqualToSomething() throws Exception {
        // Initialize the database
        ifrisDocumentRepository.saveAndFlush(ifrisDocument);

        // Get all the ifrisDocumentList where year equals to DEFAULT_YEAR
        defaultIfrisDocumentShouldBeFound("year.equals=" + DEFAULT_YEAR);

        // Get all the ifrisDocumentList where year equals to UPDATED_YEAR
        defaultIfrisDocumentShouldNotBeFound("year.equals=" + UPDATED_YEAR);
    }

    @Test
    @Transactional
    public void getAllIfrisDocumentsByYearIsInShouldWork() throws Exception {
        // Initialize the database
        ifrisDocumentRepository.saveAndFlush(ifrisDocument);

        // Get all the ifrisDocumentList where year in DEFAULT_YEAR or UPDATED_YEAR
        defaultIfrisDocumentShouldBeFound("year.in=" + DEFAULT_YEAR + "," + UPDATED_YEAR);

        // Get all the ifrisDocumentList where year equals to UPDATED_YEAR
        defaultIfrisDocumentShouldNotBeFound("year.in=" + UPDATED_YEAR);
    }

    @Test
    @Transactional
    public void getAllIfrisDocumentsByYearIsNullOrNotNull() throws Exception {
        // Initialize the database
        ifrisDocumentRepository.saveAndFlush(ifrisDocument);

        // Get all the ifrisDocumentList where year is not null
        defaultIfrisDocumentShouldBeFound("year.specified=true");

        // Get all the ifrisDocumentList where year is null
        defaultIfrisDocumentShouldNotBeFound("year.specified=false");
    }

    @Test
    @Transactional
    public void getAllIfrisDocumentsByYearIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ifrisDocumentRepository.saveAndFlush(ifrisDocument);

        // Get all the ifrisDocumentList where year greater than or equals to DEFAULT_YEAR
        defaultIfrisDocumentShouldBeFound("year.greaterOrEqualThan=" + DEFAULT_YEAR);

        // Get all the ifrisDocumentList where year greater than or equals to UPDATED_YEAR
        defaultIfrisDocumentShouldNotBeFound("year.greaterOrEqualThan=" + UPDATED_YEAR);
    }

    @Test
    @Transactional
    public void getAllIfrisDocumentsByYearIsLessThanSomething() throws Exception {
        // Initialize the database
        ifrisDocumentRepository.saveAndFlush(ifrisDocument);

        // Get all the ifrisDocumentList where year less than or equals to DEFAULT_YEAR
        defaultIfrisDocumentShouldNotBeFound("year.lessThan=" + DEFAULT_YEAR);

        // Get all the ifrisDocumentList where year less than or equals to UPDATED_YEAR
        defaultIfrisDocumentShouldBeFound("year.lessThan=" + UPDATED_YEAR);
    }


    @Test
    @Transactional
    public void getAllIfrisDocumentsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        ifrisDocumentRepository.saveAndFlush(ifrisDocument);

        // Get all the ifrisDocumentList where description equals to DEFAULT_DESCRIPTION
        defaultIfrisDocumentShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the ifrisDocumentList where description equals to UPDATED_DESCRIPTION
        defaultIfrisDocumentShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllIfrisDocumentsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        ifrisDocumentRepository.saveAndFlush(ifrisDocument);

        // Get all the ifrisDocumentList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultIfrisDocumentShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the ifrisDocumentList where description equals to UPDATED_DESCRIPTION
        defaultIfrisDocumentShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllIfrisDocumentsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        ifrisDocumentRepository.saveAndFlush(ifrisDocument);

        // Get all the ifrisDocumentList where description is not null
        defaultIfrisDocumentShouldBeFound("description.specified=true");

        // Get all the ifrisDocumentList where description is null
        defaultIfrisDocumentShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    public void getAllIfrisDocumentsByPeriodStartIsEqualToSomething() throws Exception {
        // Initialize the database
        ifrisDocumentRepository.saveAndFlush(ifrisDocument);

        // Get all the ifrisDocumentList where periodStart equals to DEFAULT_PERIOD_START
        defaultIfrisDocumentShouldBeFound("periodStart.equals=" + DEFAULT_PERIOD_START);

        // Get all the ifrisDocumentList where periodStart equals to UPDATED_PERIOD_START
        defaultIfrisDocumentShouldNotBeFound("periodStart.equals=" + UPDATED_PERIOD_START);
    }

    @Test
    @Transactional
    public void getAllIfrisDocumentsByPeriodStartIsInShouldWork() throws Exception {
        // Initialize the database
        ifrisDocumentRepository.saveAndFlush(ifrisDocument);

        // Get all the ifrisDocumentList where periodStart in DEFAULT_PERIOD_START or UPDATED_PERIOD_START
        defaultIfrisDocumentShouldBeFound("periodStart.in=" + DEFAULT_PERIOD_START + "," + UPDATED_PERIOD_START);

        // Get all the ifrisDocumentList where periodStart equals to UPDATED_PERIOD_START
        defaultIfrisDocumentShouldNotBeFound("periodStart.in=" + UPDATED_PERIOD_START);
    }

    @Test
    @Transactional
    public void getAllIfrisDocumentsByPeriodStartIsNullOrNotNull() throws Exception {
        // Initialize the database
        ifrisDocumentRepository.saveAndFlush(ifrisDocument);

        // Get all the ifrisDocumentList where periodStart is not null
        defaultIfrisDocumentShouldBeFound("periodStart.specified=true");

        // Get all the ifrisDocumentList where periodStart is null
        defaultIfrisDocumentShouldNotBeFound("periodStart.specified=false");
    }

    @Test
    @Transactional
    public void getAllIfrisDocumentsByPeriodStartIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ifrisDocumentRepository.saveAndFlush(ifrisDocument);

        // Get all the ifrisDocumentList where periodStart greater than or equals to DEFAULT_PERIOD_START
        defaultIfrisDocumentShouldBeFound("periodStart.greaterOrEqualThan=" + DEFAULT_PERIOD_START);

        // Get all the ifrisDocumentList where periodStart greater than or equals to UPDATED_PERIOD_START
        defaultIfrisDocumentShouldNotBeFound("periodStart.greaterOrEqualThan=" + UPDATED_PERIOD_START);
    }

    @Test
    @Transactional
    public void getAllIfrisDocumentsByPeriodStartIsLessThanSomething() throws Exception {
        // Initialize the database
        ifrisDocumentRepository.saveAndFlush(ifrisDocument);

        // Get all the ifrisDocumentList where periodStart less than or equals to DEFAULT_PERIOD_START
        defaultIfrisDocumentShouldNotBeFound("periodStart.lessThan=" + DEFAULT_PERIOD_START);

        // Get all the ifrisDocumentList where periodStart less than or equals to UPDATED_PERIOD_START
        defaultIfrisDocumentShouldBeFound("periodStart.lessThan=" + UPDATED_PERIOD_START);
    }


    @Test
    @Transactional
    public void getAllIfrisDocumentsByPeriodEndIsEqualToSomething() throws Exception {
        // Initialize the database
        ifrisDocumentRepository.saveAndFlush(ifrisDocument);

        // Get all the ifrisDocumentList where periodEnd equals to DEFAULT_PERIOD_END
        defaultIfrisDocumentShouldBeFound("periodEnd.equals=" + DEFAULT_PERIOD_END);

        // Get all the ifrisDocumentList where periodEnd equals to UPDATED_PERIOD_END
        defaultIfrisDocumentShouldNotBeFound("periodEnd.equals=" + UPDATED_PERIOD_END);
    }

    @Test
    @Transactional
    public void getAllIfrisDocumentsByPeriodEndIsInShouldWork() throws Exception {
        // Initialize the database
        ifrisDocumentRepository.saveAndFlush(ifrisDocument);

        // Get all the ifrisDocumentList where periodEnd in DEFAULT_PERIOD_END or UPDATED_PERIOD_END
        defaultIfrisDocumentShouldBeFound("periodEnd.in=" + DEFAULT_PERIOD_END + "," + UPDATED_PERIOD_END);

        // Get all the ifrisDocumentList where periodEnd equals to UPDATED_PERIOD_END
        defaultIfrisDocumentShouldNotBeFound("periodEnd.in=" + UPDATED_PERIOD_END);
    }

    @Test
    @Transactional
    public void getAllIfrisDocumentsByPeriodEndIsNullOrNotNull() throws Exception {
        // Initialize the database
        ifrisDocumentRepository.saveAndFlush(ifrisDocument);

        // Get all the ifrisDocumentList where periodEnd is not null
        defaultIfrisDocumentShouldBeFound("periodEnd.specified=true");

        // Get all the ifrisDocumentList where periodEnd is null
        defaultIfrisDocumentShouldNotBeFound("periodEnd.specified=false");
    }

    @Test
    @Transactional
    public void getAllIfrisDocumentsByPeriodEndIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ifrisDocumentRepository.saveAndFlush(ifrisDocument);

        // Get all the ifrisDocumentList where periodEnd greater than or equals to DEFAULT_PERIOD_END
        defaultIfrisDocumentShouldBeFound("periodEnd.greaterOrEqualThan=" + DEFAULT_PERIOD_END);

        // Get all the ifrisDocumentList where periodEnd greater than or equals to UPDATED_PERIOD_END
        defaultIfrisDocumentShouldNotBeFound("periodEnd.greaterOrEqualThan=" + UPDATED_PERIOD_END);
    }

    @Test
    @Transactional
    public void getAllIfrisDocumentsByPeriodEndIsLessThanSomething() throws Exception {
        // Initialize the database
        ifrisDocumentRepository.saveAndFlush(ifrisDocument);

        // Get all the ifrisDocumentList where periodEnd less than or equals to DEFAULT_PERIOD_END
        defaultIfrisDocumentShouldNotBeFound("periodEnd.lessThan=" + DEFAULT_PERIOD_END);

        // Get all the ifrisDocumentList where periodEnd less than or equals to UPDATED_PERIOD_END
        defaultIfrisDocumentShouldBeFound("periodEnd.lessThan=" + UPDATED_PERIOD_END);
    }


    @Test
    @Transactional
    public void getAllIfrisDocumentsByIfrisModelIsEqualToSomething() throws Exception {
        // Initialize the database
        IfrisModel ifrisModel = IfrisModelResourceIntTest.createEntity(em);
        em.persist(ifrisModel);
        em.flush();
        ifrisDocument.setIfrisModel(ifrisModel);
        ifrisDocumentRepository.saveAndFlush(ifrisDocument);
        Long ifrisModelId = ifrisModel.getId();

        // Get all the ifrisDocumentList where ifrisModel equals to ifrisModelId
        defaultIfrisDocumentShouldBeFound("ifrisModelId.equals=" + ifrisModelId);

        // Get all the ifrisDocumentList where ifrisModel equals to ifrisModelId + 1
        defaultIfrisDocumentShouldNotBeFound("ifrisModelId.equals=" + (ifrisModelId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultIfrisDocumentShouldBeFound(String filter) throws Exception {
        restIfrisDocumentMockMvc.perform(get("/api/ifris-documents?sort=id,desc&" + filter))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                                .andExpect(jsonPath("$.[*].id").value(hasItem(ifrisDocument.getId().intValue())))
                                .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
                                .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR.intValue())))
                                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
                                .andExpect(jsonPath("$.[*].periodStart").value(hasItem(DEFAULT_PERIOD_START.toString())))
                                .andExpect(jsonPath("$.[*].periodEnd").value(hasItem(DEFAULT_PERIOD_END.toString())))
                                .andExpect(jsonPath("$.[*].contentContentType").value(hasItem(DEFAULT_CONTENT_CONTENT_TYPE)))
                                .andExpect(jsonPath("$.[*].content").value(hasItem(Base64Utils.encodeToString(DEFAULT_CONTENT))));

        // Check, that the count call also returns 1
        restIfrisDocumentMockMvc.perform(get("/api/ifris-documents/count?sort=id,desc&" + filter))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                                .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultIfrisDocumentShouldNotBeFound(String filter) throws Exception {
        restIfrisDocumentMockMvc.perform(get("/api/ifris-documents?sort=id,desc&" + filter))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restIfrisDocumentMockMvc.perform(get("/api/ifris-documents/count?sort=id,desc&" + filter))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                                .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingIfrisDocument() throws Exception {
        // Get the ifrisDocument
        restIfrisDocumentMockMvc.perform(get("/api/ifris-documents/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateIfrisDocument() throws Exception {
        // Initialize the database
        ifrisDocumentRepository.saveAndFlush(ifrisDocument);

        int databaseSizeBeforeUpdate = ifrisDocumentRepository.findAll().size();

        // Update the ifrisDocument
        IfrisDocument updatedIfrisDocument = ifrisDocumentRepository.findById(ifrisDocument.getId()).get();
        // Disconnect from session so that the updates on updatedIfrisDocument are not directly saved in db
        em.detach(updatedIfrisDocument);
        updatedIfrisDocument.fileName(UPDATED_FILE_NAME)
                            .year(UPDATED_YEAR)
                            .description(UPDATED_DESCRIPTION)
                            .periodStart(UPDATED_PERIOD_START)
                            .periodEnd(UPDATED_PERIOD_END)
                            .content(UPDATED_CONTENT)
                            .contentContentType(UPDATED_CONTENT_CONTENT_TYPE);
        IfrisDocumentDTO ifrisDocumentDTO = ifrisDocumentMapper.toDto(updatedIfrisDocument);

        restIfrisDocumentMockMvc.perform(put("/api/ifris-documents").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(ifrisDocumentDTO)))
                                .andExpect(status().isOk());

        // Validate the IfrisDocument in the database
        List<IfrisDocument> ifrisDocumentList = ifrisDocumentRepository.findAll();
        assertThat(ifrisDocumentList).hasSize(databaseSizeBeforeUpdate);
        IfrisDocument testIfrisDocument = ifrisDocumentList.get(ifrisDocumentList.size() - 1);
        assertThat(testIfrisDocument.getFileName()).isEqualTo(UPDATED_FILE_NAME);
        assertThat(testIfrisDocument.getYear()).isEqualTo(UPDATED_YEAR);
        assertThat(testIfrisDocument.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testIfrisDocument.getPeriodStart()).isEqualTo(UPDATED_PERIOD_START);
        assertThat(testIfrisDocument.getPeriodEnd()).isEqualTo(UPDATED_PERIOD_END);
        assertThat(testIfrisDocument.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testIfrisDocument.getContentContentType()).isEqualTo(UPDATED_CONTENT_CONTENT_TYPE);

        // Validate the IfrisDocument in Elasticsearch
        verify(mockIfrisDocumentSearchRepository, times(1)).save(testIfrisDocument);
    }

    @Test
    @Transactional
    public void updateNonExistingIfrisDocument() throws Exception {
        int databaseSizeBeforeUpdate = ifrisDocumentRepository.findAll().size();

        // Create the IfrisDocument
        IfrisDocumentDTO ifrisDocumentDTO = ifrisDocumentMapper.toDto(ifrisDocument);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIfrisDocumentMockMvc.perform(put("/api/ifris-documents").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(ifrisDocumentDTO)))
                                .andExpect(status().isBadRequest());

        // Validate the IfrisDocument in the database
        List<IfrisDocument> ifrisDocumentList = ifrisDocumentRepository.findAll();
        assertThat(ifrisDocumentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the IfrisDocument in Elasticsearch
        verify(mockIfrisDocumentSearchRepository, times(0)).save(ifrisDocument);
    }

    @Test
    @Transactional
    public void deleteIfrisDocument() throws Exception {
        // Initialize the database
        ifrisDocumentRepository.saveAndFlush(ifrisDocument);

        int databaseSizeBeforeDelete = ifrisDocumentRepository.findAll().size();

        // Delete the ifrisDocument
        restIfrisDocumentMockMvc.perform(delete("/api/ifris-documents/{id}", ifrisDocument.getId()).accept(TestUtil.APPLICATION_JSON_UTF8)).andExpect(status().isOk());

        // Validate the database is empty
        List<IfrisDocument> ifrisDocumentList = ifrisDocumentRepository.findAll();
        assertThat(ifrisDocumentList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the IfrisDocument in Elasticsearch
        verify(mockIfrisDocumentSearchRepository, times(1)).deleteById(ifrisDocument.getId());
    }

    @Test
    @Transactional
    public void searchIfrisDocument() throws Exception {
        // Initialize the database
        ifrisDocumentRepository.saveAndFlush(ifrisDocument);
        when(mockIfrisDocumentSearchRepository.search(queryStringQuery("id:" + ifrisDocument.getId()), PageRequest.of(0, 20))).thenReturn(
            new PageImpl<>(Collections.singletonList(ifrisDocument), PageRequest.of(0, 1), 1));
        // Search the ifrisDocument
        restIfrisDocumentMockMvc.perform(get("/api/_search/ifris-documents?query=id:" + ifrisDocument.getId()))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                                .andExpect(jsonPath("$.[*].id").value(hasItem(ifrisDocument.getId().intValue())))
                                .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
                                .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR.intValue())))
                                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
                                .andExpect(jsonPath("$.[*].periodStart").value(hasItem(DEFAULT_PERIOD_START.toString())))
                                .andExpect(jsonPath("$.[*].periodEnd").value(hasItem(DEFAULT_PERIOD_END.toString())))
                                .andExpect(jsonPath("$.[*].contentContentType").value(hasItem(DEFAULT_CONTENT_CONTENT_TYPE)))
                                .andExpect(jsonPath("$.[*].content").value(hasItem(Base64Utils.encodeToString(DEFAULT_CONTENT))));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(IfrisDocument.class);
        IfrisDocument ifrisDocument1 = new IfrisDocument();
        ifrisDocument1.setId(1L);
        IfrisDocument ifrisDocument2 = new IfrisDocument();
        ifrisDocument2.setId(ifrisDocument1.getId());
        assertThat(ifrisDocument1).isEqualTo(ifrisDocument2);
        ifrisDocument2.setId(2L);
        assertThat(ifrisDocument1).isNotEqualTo(ifrisDocument2);
        ifrisDocument1.setId(null);
        assertThat(ifrisDocument1).isNotEqualTo(ifrisDocument2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(IfrisDocumentDTO.class);
        IfrisDocumentDTO ifrisDocumentDTO1 = new IfrisDocumentDTO();
        ifrisDocumentDTO1.setId(1L);
        IfrisDocumentDTO ifrisDocumentDTO2 = new IfrisDocumentDTO();
        assertThat(ifrisDocumentDTO1).isNotEqualTo(ifrisDocumentDTO2);
        ifrisDocumentDTO2.setId(ifrisDocumentDTO1.getId());
        assertThat(ifrisDocumentDTO1).isEqualTo(ifrisDocumentDTO2);
        ifrisDocumentDTO2.setId(2L);
        assertThat(ifrisDocumentDTO1).isNotEqualTo(ifrisDocumentDTO2);
        ifrisDocumentDTO1.setId(null);
        assertThat(ifrisDocumentDTO1).isNotEqualTo(ifrisDocumentDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(ifrisDocumentMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(ifrisDocumentMapper.fromId(null)).isNull();
    }
}
