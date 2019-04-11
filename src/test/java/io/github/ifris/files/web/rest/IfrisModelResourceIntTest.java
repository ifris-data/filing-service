package io.github.ifris.files.web.rest;

import io.github.ifris.files.FilingServiceApp;
import io.github.ifris.files.config.SecurityBeanOverrideConfiguration;
import io.github.ifris.files.domain.DocumentTemplate;
import io.github.ifris.files.domain.IfrisDocument;
import io.github.ifris.files.domain.IfrisModel;
import io.github.ifris.files.repository.IfrisModelRepository;
import io.github.ifris.files.repository.search.IfrisModelSearchRepository;
import io.github.ifris.files.service.IfrisModelQueryService;
import io.github.ifris.files.service.IfrisModelService;
import io.github.ifris.files.service.dto.IfrisModelDTO;
import io.github.ifris.files.service.mapper.IfrisModelMapper;
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
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
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
 * Test class for the IfrisModelResource REST controller.
 *
 * @see IfrisModelResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SecurityBeanOverrideConfiguration.class, FilingServiceApp.class})
public class IfrisModelResourceIntTest {

    private static final String DEFAULT_MODEL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_MODEL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_SERVICE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SERVICE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SERVICE_PORT = "AAAAAAAAAA";
    private static final String UPDATED_SERVICE_PORT = "BBBBBBBBBB";

    @Autowired
    private IfrisModelRepository ifrisModelRepository;

    @Autowired
    private IfrisModelMapper ifrisModelMapper;

    @Autowired
    private IfrisModelService ifrisModelService;

    /**
     * This repository is mocked in the io.github.ifris.files.repository.search test package.
     *
     * @see io.github.ifris.files.repository.search.IfrisModelSearchRepositoryMockConfiguration
     */
    @Autowired
    private IfrisModelSearchRepository mockIfrisModelSearchRepository;

    @Autowired
    private IfrisModelQueryService ifrisModelQueryService;

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

    private MockMvc restIfrisModelMockMvc;

    private IfrisModel ifrisModel;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it, if they test an entity which requires the current entity.
     */
    public static IfrisModel createEntity(EntityManager em) {
        IfrisModel ifrisModel = new IfrisModel().modelName(DEFAULT_MODEL_NAME).description(DEFAULT_DESCRIPTION).serviceName(DEFAULT_SERVICE_NAME).servicePort(DEFAULT_SERVICE_PORT);
        return ifrisModel;
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final IfrisModelResource ifrisModelResource = new IfrisModelResource(ifrisModelService, ifrisModelQueryService);
        this.restIfrisModelMockMvc = MockMvcBuilders.standaloneSetup(ifrisModelResource)
                                                    .setCustomArgumentResolvers(pageableArgumentResolver)
                                                    .setControllerAdvice(exceptionTranslator)
                                                    .setConversionService(createFormattingConversionService())
                                                    .setMessageConverters(jacksonMessageConverter)
                                                    .setValidator(validator)
                                                    .build();
    }

    @Before
    public void initTest() {
        ifrisModel = createEntity(em);
    }

    @Test
    @Transactional
    public void createIfrisModel() throws Exception {
        int databaseSizeBeforeCreate = ifrisModelRepository.findAll().size();

        // Create the IfrisModel
        IfrisModelDTO ifrisModelDTO = ifrisModelMapper.toDto(ifrisModel);
        restIfrisModelMockMvc.perform(post("/api/ifris-models").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(ifrisModelDTO))).andExpect(status().isCreated());

        // Validate the IfrisModel in the database
        List<IfrisModel> ifrisModelList = ifrisModelRepository.findAll();
        assertThat(ifrisModelList).hasSize(databaseSizeBeforeCreate + 1);
        IfrisModel testIfrisModel = ifrisModelList.get(ifrisModelList.size() - 1);
        assertThat(testIfrisModel.getModelName()).isEqualTo(DEFAULT_MODEL_NAME);
        assertThat(testIfrisModel.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testIfrisModel.getServiceName()).isEqualTo(DEFAULT_SERVICE_NAME);
        assertThat(testIfrisModel.getServicePort()).isEqualTo(DEFAULT_SERVICE_PORT);

        // Validate the IfrisModel in Elasticsearch
        verify(mockIfrisModelSearchRepository, times(1)).save(testIfrisModel);
    }

    @Test
    @Transactional
    public void createIfrisModelWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = ifrisModelRepository.findAll().size();

        // Create the IfrisModel with an existing ID
        ifrisModel.setId(1L);
        IfrisModelDTO ifrisModelDTO = ifrisModelMapper.toDto(ifrisModel);

        // An entity with an existing ID cannot be created, so this API call must fail
        restIfrisModelMockMvc.perform(post("/api/ifris-models").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(ifrisModelDTO)))
                             .andExpect(status().isBadRequest());

        // Validate the IfrisModel in the database
        List<IfrisModel> ifrisModelList = ifrisModelRepository.findAll();
        assertThat(ifrisModelList).hasSize(databaseSizeBeforeCreate);

        // Validate the IfrisModel in Elasticsearch
        verify(mockIfrisModelSearchRepository, times(0)).save(ifrisModel);
    }

    @Test
    @Transactional
    public void checkModelNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = ifrisModelRepository.findAll().size();
        // set the field null
        ifrisModel.setModelName(null);

        // Create the IfrisModel, which fails.
        IfrisModelDTO ifrisModelDTO = ifrisModelMapper.toDto(ifrisModel);

        restIfrisModelMockMvc.perform(post("/api/ifris-models").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(ifrisModelDTO)))
                             .andExpect(status().isBadRequest());

        List<IfrisModel> ifrisModelList = ifrisModelRepository.findAll();
        assertThat(ifrisModelList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = ifrisModelRepository.findAll().size();
        // set the field null
        ifrisModel.setDescription(null);

        // Create the IfrisModel, which fails.
        IfrisModelDTO ifrisModelDTO = ifrisModelMapper.toDto(ifrisModel);

        restIfrisModelMockMvc.perform(post("/api/ifris-models").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(ifrisModelDTO)))
                             .andExpect(status().isBadRequest());

        List<IfrisModel> ifrisModelList = ifrisModelRepository.findAll();
        assertThat(ifrisModelList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllIfrisModels() throws Exception {
        // Initialize the database
        ifrisModelRepository.saveAndFlush(ifrisModel);

        // Get all the ifrisModelList
        restIfrisModelMockMvc.perform(get("/api/ifris-models?sort=id,desc"))
                             .andExpect(status().isOk())
                             .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                             .andExpect(jsonPath("$.[*].id").value(hasItem(ifrisModel.getId().intValue())))
                             .andExpect(jsonPath("$.[*].modelName").value(hasItem(DEFAULT_MODEL_NAME.toString())))
                             .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                             .andExpect(jsonPath("$.[*].serviceName").value(hasItem(DEFAULT_SERVICE_NAME.toString())))
                             .andExpect(jsonPath("$.[*].servicePort").value(hasItem(DEFAULT_SERVICE_PORT.toString())));
    }

    @Test
    @Transactional
    public void getIfrisModel() throws Exception {
        // Initialize the database
        ifrisModelRepository.saveAndFlush(ifrisModel);

        // Get the ifrisModel
        restIfrisModelMockMvc.perform(get("/api/ifris-models/{id}", ifrisModel.getId()))
                             .andExpect(status().isOk())
                             .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                             .andExpect(jsonPath("$.id").value(ifrisModel.getId().intValue()))
                             .andExpect(jsonPath("$.modelName").value(DEFAULT_MODEL_NAME.toString()))
                             .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
                             .andExpect(jsonPath("$.serviceName").value(DEFAULT_SERVICE_NAME.toString()))
                             .andExpect(jsonPath("$.servicePort").value(DEFAULT_SERVICE_PORT.toString()));
    }

    @Test
    @Transactional
    public void getAllIfrisModelsByModelNameIsEqualToSomething() throws Exception {
        // Initialize the database
        ifrisModelRepository.saveAndFlush(ifrisModel);

        // Get all the ifrisModelList where modelName equals to DEFAULT_MODEL_NAME
        defaultIfrisModelShouldBeFound("modelName.equals=" + DEFAULT_MODEL_NAME);

        // Get all the ifrisModelList where modelName equals to UPDATED_MODEL_NAME
        defaultIfrisModelShouldNotBeFound("modelName.equals=" + UPDATED_MODEL_NAME);
    }

    @Test
    @Transactional
    public void getAllIfrisModelsByModelNameIsInShouldWork() throws Exception {
        // Initialize the database
        ifrisModelRepository.saveAndFlush(ifrisModel);

        // Get all the ifrisModelList where modelName in DEFAULT_MODEL_NAME or UPDATED_MODEL_NAME
        defaultIfrisModelShouldBeFound("modelName.in=" + DEFAULT_MODEL_NAME + "," + UPDATED_MODEL_NAME);

        // Get all the ifrisModelList where modelName equals to UPDATED_MODEL_NAME
        defaultIfrisModelShouldNotBeFound("modelName.in=" + UPDATED_MODEL_NAME);
    }

    @Test
    @Transactional
    public void getAllIfrisModelsByModelNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        ifrisModelRepository.saveAndFlush(ifrisModel);

        // Get all the ifrisModelList where modelName is not null
        defaultIfrisModelShouldBeFound("modelName.specified=true");

        // Get all the ifrisModelList where modelName is null
        defaultIfrisModelShouldNotBeFound("modelName.specified=false");
    }

    @Test
    @Transactional
    public void getAllIfrisModelsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        ifrisModelRepository.saveAndFlush(ifrisModel);

        // Get all the ifrisModelList where description equals to DEFAULT_DESCRIPTION
        defaultIfrisModelShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the ifrisModelList where description equals to UPDATED_DESCRIPTION
        defaultIfrisModelShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllIfrisModelsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        ifrisModelRepository.saveAndFlush(ifrisModel);

        // Get all the ifrisModelList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultIfrisModelShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the ifrisModelList where description equals to UPDATED_DESCRIPTION
        defaultIfrisModelShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllIfrisModelsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        ifrisModelRepository.saveAndFlush(ifrisModel);

        // Get all the ifrisModelList where description is not null
        defaultIfrisModelShouldBeFound("description.specified=true");

        // Get all the ifrisModelList where description is null
        defaultIfrisModelShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    public void getAllIfrisModelsByServiceNameIsEqualToSomething() throws Exception {
        // Initialize the database
        ifrisModelRepository.saveAndFlush(ifrisModel);

        // Get all the ifrisModelList where serviceName equals to DEFAULT_SERVICE_NAME
        defaultIfrisModelShouldBeFound("serviceName.equals=" + DEFAULT_SERVICE_NAME);

        // Get all the ifrisModelList where serviceName equals to UPDATED_SERVICE_NAME
        defaultIfrisModelShouldNotBeFound("serviceName.equals=" + UPDATED_SERVICE_NAME);
    }

    @Test
    @Transactional
    public void getAllIfrisModelsByServiceNameIsInShouldWork() throws Exception {
        // Initialize the database
        ifrisModelRepository.saveAndFlush(ifrisModel);

        // Get all the ifrisModelList where serviceName in DEFAULT_SERVICE_NAME or UPDATED_SERVICE_NAME
        defaultIfrisModelShouldBeFound("serviceName.in=" + DEFAULT_SERVICE_NAME + "," + UPDATED_SERVICE_NAME);

        // Get all the ifrisModelList where serviceName equals to UPDATED_SERVICE_NAME
        defaultIfrisModelShouldNotBeFound("serviceName.in=" + UPDATED_SERVICE_NAME);
    }

    @Test
    @Transactional
    public void getAllIfrisModelsByServiceNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        ifrisModelRepository.saveAndFlush(ifrisModel);

        // Get all the ifrisModelList where serviceName is not null
        defaultIfrisModelShouldBeFound("serviceName.specified=true");

        // Get all the ifrisModelList where serviceName is null
        defaultIfrisModelShouldNotBeFound("serviceName.specified=false");
    }

    @Test
    @Transactional
    public void getAllIfrisModelsByServicePortIsEqualToSomething() throws Exception {
        // Initialize the database
        ifrisModelRepository.saveAndFlush(ifrisModel);

        // Get all the ifrisModelList where servicePort equals to DEFAULT_SERVICE_PORT
        defaultIfrisModelShouldBeFound("servicePort.equals=" + DEFAULT_SERVICE_PORT);

        // Get all the ifrisModelList where servicePort equals to UPDATED_SERVICE_PORT
        defaultIfrisModelShouldNotBeFound("servicePort.equals=" + UPDATED_SERVICE_PORT);
    }

    @Test
    @Transactional
    public void getAllIfrisModelsByServicePortIsInShouldWork() throws Exception {
        // Initialize the database
        ifrisModelRepository.saveAndFlush(ifrisModel);

        // Get all the ifrisModelList where servicePort in DEFAULT_SERVICE_PORT or UPDATED_SERVICE_PORT
        defaultIfrisModelShouldBeFound("servicePort.in=" + DEFAULT_SERVICE_PORT + "," + UPDATED_SERVICE_PORT);

        // Get all the ifrisModelList where servicePort equals to UPDATED_SERVICE_PORT
        defaultIfrisModelShouldNotBeFound("servicePort.in=" + UPDATED_SERVICE_PORT);
    }

    @Test
    @Transactional
    public void getAllIfrisModelsByServicePortIsNullOrNotNull() throws Exception {
        // Initialize the database
        ifrisModelRepository.saveAndFlush(ifrisModel);

        // Get all the ifrisModelList where servicePort is not null
        defaultIfrisModelShouldBeFound("servicePort.specified=true");

        // Get all the ifrisModelList where servicePort is null
        defaultIfrisModelShouldNotBeFound("servicePort.specified=false");
    }

    @Test
    @Transactional
    public void getAllIfrisModelsByIfrisDocumentIsEqualToSomething() throws Exception {
        // Initialize the database
        IfrisDocument ifrisDocument = IfrisDocumentResourceIntTest.createEntity(em);
        em.persist(ifrisDocument);
        em.flush();
        ifrisModel.addIfrisDocument(ifrisDocument);
        ifrisModelRepository.saveAndFlush(ifrisModel);
        Long ifrisDocumentId = ifrisDocument.getId();

        // Get all the ifrisModelList where ifrisDocument equals to ifrisDocumentId
        defaultIfrisModelShouldBeFound("ifrisDocumentId.equals=" + ifrisDocumentId);

        // Get all the ifrisModelList where ifrisDocument equals to ifrisDocumentId + 1
        defaultIfrisModelShouldNotBeFound("ifrisDocumentId.equals=" + (ifrisDocumentId + 1));
    }


    @Test
    @Transactional
    public void getAllIfrisModelsByDocumentTemplateIsEqualToSomething() throws Exception {
        // Initialize the database
        DocumentTemplate documentTemplate = DocumentTemplateResourceIntTest.createEntity(em);
        em.persist(documentTemplate);
        em.flush();
        ifrisModel.addDocumentTemplate(documentTemplate);
        ifrisModelRepository.saveAndFlush(ifrisModel);
        Long documentTemplateId = documentTemplate.getId();

        // Get all the ifrisModelList where documentTemplate equals to documentTemplateId
        defaultIfrisModelShouldBeFound("documentTemplateId.equals=" + documentTemplateId);

        // Get all the ifrisModelList where documentTemplate equals to documentTemplateId + 1
        defaultIfrisModelShouldNotBeFound("documentTemplateId.equals=" + (documentTemplateId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultIfrisModelShouldBeFound(String filter) throws Exception {
        restIfrisModelMockMvc.perform(get("/api/ifris-models?sort=id,desc&" + filter))
                             .andExpect(status().isOk())
                             .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                             .andExpect(jsonPath("$.[*].id").value(hasItem(ifrisModel.getId().intValue())))
                             .andExpect(jsonPath("$.[*].modelName").value(hasItem(DEFAULT_MODEL_NAME)))
                             .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
                             .andExpect(jsonPath("$.[*].serviceName").value(hasItem(DEFAULT_SERVICE_NAME)))
                             .andExpect(jsonPath("$.[*].servicePort").value(hasItem(DEFAULT_SERVICE_PORT)));

        // Check, that the count call also returns 1
        restIfrisModelMockMvc.perform(get("/api/ifris-models/count?sort=id,desc&" + filter))
                             .andExpect(status().isOk())
                             .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                             .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultIfrisModelShouldNotBeFound(String filter) throws Exception {
        restIfrisModelMockMvc.perform(get("/api/ifris-models?sort=id,desc&" + filter))
                             .andExpect(status().isOk())
                             .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                             .andExpect(jsonPath("$").isArray())
                             .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restIfrisModelMockMvc.perform(get("/api/ifris-models/count?sort=id,desc&" + filter))
                             .andExpect(status().isOk())
                             .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                             .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingIfrisModel() throws Exception {
        // Get the ifrisModel
        restIfrisModelMockMvc.perform(get("/api/ifris-models/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateIfrisModel() throws Exception {
        // Initialize the database
        ifrisModelRepository.saveAndFlush(ifrisModel);

        int databaseSizeBeforeUpdate = ifrisModelRepository.findAll().size();

        // Update the ifrisModel
        IfrisModel updatedIfrisModel = ifrisModelRepository.findById(ifrisModel.getId()).get();
        // Disconnect from session so that the updates on updatedIfrisModel are not directly saved in db
        em.detach(updatedIfrisModel);
        updatedIfrisModel.modelName(UPDATED_MODEL_NAME).description(UPDATED_DESCRIPTION).serviceName(UPDATED_SERVICE_NAME).servicePort(UPDATED_SERVICE_PORT);
        IfrisModelDTO ifrisModelDTO = ifrisModelMapper.toDto(updatedIfrisModel);

        restIfrisModelMockMvc.perform(put("/api/ifris-models").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(ifrisModelDTO))).andExpect(status().isOk());

        // Validate the IfrisModel in the database
        List<IfrisModel> ifrisModelList = ifrisModelRepository.findAll();
        assertThat(ifrisModelList).hasSize(databaseSizeBeforeUpdate);
        IfrisModel testIfrisModel = ifrisModelList.get(ifrisModelList.size() - 1);
        assertThat(testIfrisModel.getModelName()).isEqualTo(UPDATED_MODEL_NAME);
        assertThat(testIfrisModel.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testIfrisModel.getServiceName()).isEqualTo(UPDATED_SERVICE_NAME);
        assertThat(testIfrisModel.getServicePort()).isEqualTo(UPDATED_SERVICE_PORT);

        // Validate the IfrisModel in Elasticsearch
        verify(mockIfrisModelSearchRepository, times(1)).save(testIfrisModel);
    }

    @Test
    @Transactional
    public void updateNonExistingIfrisModel() throws Exception {
        int databaseSizeBeforeUpdate = ifrisModelRepository.findAll().size();

        // Create the IfrisModel
        IfrisModelDTO ifrisModelDTO = ifrisModelMapper.toDto(ifrisModel);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIfrisModelMockMvc.perform(put("/api/ifris-models").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(ifrisModelDTO)))
                             .andExpect(status().isBadRequest());

        // Validate the IfrisModel in the database
        List<IfrisModel> ifrisModelList = ifrisModelRepository.findAll();
        assertThat(ifrisModelList).hasSize(databaseSizeBeforeUpdate);

        // Validate the IfrisModel in Elasticsearch
        verify(mockIfrisModelSearchRepository, times(0)).save(ifrisModel);
    }

    @Test
    @Transactional
    public void deleteIfrisModel() throws Exception {
        // Initialize the database
        ifrisModelRepository.saveAndFlush(ifrisModel);

        int databaseSizeBeforeDelete = ifrisModelRepository.findAll().size();

        // Delete the ifrisModel
        restIfrisModelMockMvc.perform(delete("/api/ifris-models/{id}", ifrisModel.getId()).accept(TestUtil.APPLICATION_JSON_UTF8)).andExpect(status().isOk());

        // Validate the database is empty
        List<IfrisModel> ifrisModelList = ifrisModelRepository.findAll();
        assertThat(ifrisModelList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the IfrisModel in Elasticsearch
        verify(mockIfrisModelSearchRepository, times(1)).deleteById(ifrisModel.getId());
    }

    @Test
    @Transactional
    public void searchIfrisModel() throws Exception {
        // Initialize the database
        ifrisModelRepository.saveAndFlush(ifrisModel);
        when(mockIfrisModelSearchRepository.search(queryStringQuery("id:" + ifrisModel.getId()), PageRequest.of(0, 20))).thenReturn(
            new PageImpl<>(Collections.singletonList(ifrisModel), PageRequest.of(0, 1), 1));
        // Search the ifrisModel
        restIfrisModelMockMvc.perform(get("/api/_search/ifris-models?query=id:" + ifrisModel.getId()))
                             .andExpect(status().isOk())
                             .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                             .andExpect(jsonPath("$.[*].id").value(hasItem(ifrisModel.getId().intValue())))
                             .andExpect(jsonPath("$.[*].modelName").value(hasItem(DEFAULT_MODEL_NAME)))
                             .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
                             .andExpect(jsonPath("$.[*].serviceName").value(hasItem(DEFAULT_SERVICE_NAME)))
                             .andExpect(jsonPath("$.[*].servicePort").value(hasItem(DEFAULT_SERVICE_PORT)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(IfrisModel.class);
        IfrisModel ifrisModel1 = new IfrisModel();
        ifrisModel1.setId(1L);
        IfrisModel ifrisModel2 = new IfrisModel();
        ifrisModel2.setId(ifrisModel1.getId());
        assertThat(ifrisModel1).isEqualTo(ifrisModel2);
        ifrisModel2.setId(2L);
        assertThat(ifrisModel1).isNotEqualTo(ifrisModel2);
        ifrisModel1.setId(null);
        assertThat(ifrisModel1).isNotEqualTo(ifrisModel2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(IfrisModelDTO.class);
        IfrisModelDTO ifrisModelDTO1 = new IfrisModelDTO();
        ifrisModelDTO1.setId(1L);
        IfrisModelDTO ifrisModelDTO2 = new IfrisModelDTO();
        assertThat(ifrisModelDTO1).isNotEqualTo(ifrisModelDTO2);
        ifrisModelDTO2.setId(ifrisModelDTO1.getId());
        assertThat(ifrisModelDTO1).isEqualTo(ifrisModelDTO2);
        ifrisModelDTO2.setId(2L);
        assertThat(ifrisModelDTO1).isNotEqualTo(ifrisModelDTO2);
        ifrisModelDTO1.setId(null);
        assertThat(ifrisModelDTO1).isNotEqualTo(ifrisModelDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(ifrisModelMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(ifrisModelMapper.fromId(null)).isNull();
    }
}
