package io.github.ifris.files.web.rest;

import io.github.ifris.files.FilingServiceApp;
import io.github.ifris.files.config.SecurityBeanOverrideConfiguration;
import io.github.ifris.files.domain.DocumentTemplate;
import io.github.ifris.files.domain.IfrisModel;
import io.github.ifris.files.repository.DocumentTemplateRepository;
import io.github.ifris.files.repository.search.DocumentTemplateSearchRepository;
import io.github.ifris.files.service.DocumentTemplateQueryService;
import io.github.ifris.files.service.DocumentTemplateService;
import io.github.ifris.files.service.dto.DocumentTemplateDTO;
import io.github.ifris.files.service.mapper.DocumentTemplateMapper;
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
 * Test class for the DocumentTemplateResource REST controller.
 *
 * @see DocumentTemplateResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SecurityBeanOverrideConfiguration.class, FilingServiceApp.class})
public class DocumentTemplateResourceIntTest {

    private static final LocalDate DEFAULT_DATE_CREATED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_CREATED = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_UPDATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_UPDATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final byte[] DEFAULT_TEMPLATE_FILE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_TEMPLATE_FILE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_TEMPLATE_FILE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_TEMPLATE_FILE_CONTENT_TYPE = "image/png";

    @Autowired
    private DocumentTemplateRepository documentTemplateRepository;

    @Autowired
    private DocumentTemplateMapper documentTemplateMapper;

    @Autowired
    private DocumentTemplateService documentTemplateService;

    /**
     * This repository is mocked in the io.github.ifris.files.repository.search test package.
     *
     * @see io.github.ifris.files.repository.search.DocumentTemplateSearchRepositoryMockConfiguration
     */
    @Autowired
    private DocumentTemplateSearchRepository mockDocumentTemplateSearchRepository;

    @Autowired
    private DocumentTemplateQueryService documentTemplateQueryService;

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

    private MockMvc restDocumentTemplateMockMvc;

    private DocumentTemplate documentTemplate;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it, if they test an entity which requires the current entity.
     */
    public static DocumentTemplate createEntity(EntityManager em) {
        DocumentTemplate documentTemplate =
            new DocumentTemplate().dateCreated(DEFAULT_DATE_CREATED).updateDate(DEFAULT_UPDATE_DATE).templateFile(DEFAULT_TEMPLATE_FILE).templateFileContentType(DEFAULT_TEMPLATE_FILE_CONTENT_TYPE);
        // Add required entity
        IfrisModel ifrisModel = IfrisModelResourceIntTest.createEntity(em);
        em.persist(ifrisModel);
        em.flush();
        documentTemplate.setIfrisModel(ifrisModel);
        return documentTemplate;
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final DocumentTemplateResource documentTemplateResource = new DocumentTemplateResource(documentTemplateService, documentTemplateQueryService);
        this.restDocumentTemplateMockMvc = MockMvcBuilders.standaloneSetup(documentTemplateResource)
                                                          .setCustomArgumentResolvers(pageableArgumentResolver)
                                                          .setControllerAdvice(exceptionTranslator)
                                                          .setConversionService(createFormattingConversionService())
                                                          .setMessageConverters(jacksonMessageConverter)
                                                          .setValidator(validator)
                                                          .build();
    }

    @Before
    public void initTest() {
        documentTemplate = createEntity(em);
    }

    @Test
    @Transactional
    public void createDocumentTemplate() throws Exception {
        int databaseSizeBeforeCreate = documentTemplateRepository.findAll().size();

        // Create the DocumentTemplate
        DocumentTemplateDTO documentTemplateDTO = documentTemplateMapper.toDto(documentTemplate);
        restDocumentTemplateMockMvc.perform(post("/api/document-templates").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(documentTemplateDTO)))
                                   .andExpect(status().isCreated());

        // Validate the DocumentTemplate in the database
        List<DocumentTemplate> documentTemplateList = documentTemplateRepository.findAll();
        assertThat(documentTemplateList).hasSize(databaseSizeBeforeCreate + 1);
        DocumentTemplate testDocumentTemplate = documentTemplateList.get(documentTemplateList.size() - 1);
        assertThat(testDocumentTemplate.getDateCreated()).isEqualTo(DEFAULT_DATE_CREATED);
        assertThat(testDocumentTemplate.getUpdateDate()).isEqualTo(DEFAULT_UPDATE_DATE);
        assertThat(testDocumentTemplate.getTemplateFile()).isEqualTo(DEFAULT_TEMPLATE_FILE);
        assertThat(testDocumentTemplate.getTemplateFileContentType()).isEqualTo(DEFAULT_TEMPLATE_FILE_CONTENT_TYPE);

        // Validate the DocumentTemplate in Elasticsearch
        verify(mockDocumentTemplateSearchRepository, times(1)).save(testDocumentTemplate);
    }

    @Test
    @Transactional
    public void createDocumentTemplateWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = documentTemplateRepository.findAll().size();

        // Create the DocumentTemplate with an existing ID
        documentTemplate.setId(1L);
        DocumentTemplateDTO documentTemplateDTO = documentTemplateMapper.toDto(documentTemplate);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDocumentTemplateMockMvc.perform(post("/api/document-templates").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(documentTemplateDTO)))
                                   .andExpect(status().isBadRequest());

        // Validate the DocumentTemplate in the database
        List<DocumentTemplate> documentTemplateList = documentTemplateRepository.findAll();
        assertThat(documentTemplateList).hasSize(databaseSizeBeforeCreate);

        // Validate the DocumentTemplate in Elasticsearch
        verify(mockDocumentTemplateSearchRepository, times(0)).save(documentTemplate);
    }

    @Test
    @Transactional
    public void getAllDocumentTemplates() throws Exception {
        // Initialize the database
        documentTemplateRepository.saveAndFlush(documentTemplate);

        // Get all the documentTemplateList
        restDocumentTemplateMockMvc.perform(get("/api/document-templates?sort=id,desc"))
                                   .andExpect(status().isOk())
                                   .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                                   .andExpect(jsonPath("$.[*].id").value(hasItem(documentTemplate.getId().intValue())))
                                   .andExpect(jsonPath("$.[*].dateCreated").value(hasItem(DEFAULT_DATE_CREATED.toString())))
                                   .andExpect(jsonPath("$.[*].updateDate").value(hasItem(DEFAULT_UPDATE_DATE.toString())))
                                   .andExpect(jsonPath("$.[*].templateFileContentType").value(hasItem(DEFAULT_TEMPLATE_FILE_CONTENT_TYPE)))
                                   .andExpect(jsonPath("$.[*].templateFile").value(hasItem(Base64Utils.encodeToString(DEFAULT_TEMPLATE_FILE))));
    }

    @Test
    @Transactional
    public void getDocumentTemplate() throws Exception {
        // Initialize the database
        documentTemplateRepository.saveAndFlush(documentTemplate);

        // Get the documentTemplate
        restDocumentTemplateMockMvc.perform(get("/api/document-templates/{id}", documentTemplate.getId()))
                                   .andExpect(status().isOk())
                                   .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                                   .andExpect(jsonPath("$.id").value(documentTemplate.getId().intValue()))
                                   .andExpect(jsonPath("$.dateCreated").value(DEFAULT_DATE_CREATED.toString()))
                                   .andExpect(jsonPath("$.updateDate").value(DEFAULT_UPDATE_DATE.toString()))
                                   .andExpect(jsonPath("$.templateFileContentType").value(DEFAULT_TEMPLATE_FILE_CONTENT_TYPE))
                                   .andExpect(jsonPath("$.templateFile").value(Base64Utils.encodeToString(DEFAULT_TEMPLATE_FILE)));
    }

    @Test
    @Transactional
    public void getAllDocumentTemplatesByDateCreatedIsEqualToSomething() throws Exception {
        // Initialize the database
        documentTemplateRepository.saveAndFlush(documentTemplate);

        // Get all the documentTemplateList where dateCreated equals to DEFAULT_DATE_CREATED
        defaultDocumentTemplateShouldBeFound("dateCreated.equals=" + DEFAULT_DATE_CREATED);

        // Get all the documentTemplateList where dateCreated equals to UPDATED_DATE_CREATED
        defaultDocumentTemplateShouldNotBeFound("dateCreated.equals=" + UPDATED_DATE_CREATED);
    }

    @Test
    @Transactional
    public void getAllDocumentTemplatesByDateCreatedIsInShouldWork() throws Exception {
        // Initialize the database
        documentTemplateRepository.saveAndFlush(documentTemplate);

        // Get all the documentTemplateList where dateCreated in DEFAULT_DATE_CREATED or UPDATED_DATE_CREATED
        defaultDocumentTemplateShouldBeFound("dateCreated.in=" + DEFAULT_DATE_CREATED + "," + UPDATED_DATE_CREATED);

        // Get all the documentTemplateList where dateCreated equals to UPDATED_DATE_CREATED
        defaultDocumentTemplateShouldNotBeFound("dateCreated.in=" + UPDATED_DATE_CREATED);
    }

    @Test
    @Transactional
    public void getAllDocumentTemplatesByDateCreatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        documentTemplateRepository.saveAndFlush(documentTemplate);

        // Get all the documentTemplateList where dateCreated is not null
        defaultDocumentTemplateShouldBeFound("dateCreated.specified=true");

        // Get all the documentTemplateList where dateCreated is null
        defaultDocumentTemplateShouldNotBeFound("dateCreated.specified=false");
    }

    @Test
    @Transactional
    public void getAllDocumentTemplatesByDateCreatedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        documentTemplateRepository.saveAndFlush(documentTemplate);

        // Get all the documentTemplateList where dateCreated greater than or equals to DEFAULT_DATE_CREATED
        defaultDocumentTemplateShouldBeFound("dateCreated.greaterOrEqualThan=" + DEFAULT_DATE_CREATED);

        // Get all the documentTemplateList where dateCreated greater than or equals to UPDATED_DATE_CREATED
        defaultDocumentTemplateShouldNotBeFound("dateCreated.greaterOrEqualThan=" + UPDATED_DATE_CREATED);
    }

    @Test
    @Transactional
    public void getAllDocumentTemplatesByDateCreatedIsLessThanSomething() throws Exception {
        // Initialize the database
        documentTemplateRepository.saveAndFlush(documentTemplate);

        // Get all the documentTemplateList where dateCreated less than or equals to DEFAULT_DATE_CREATED
        defaultDocumentTemplateShouldNotBeFound("dateCreated.lessThan=" + DEFAULT_DATE_CREATED);

        // Get all the documentTemplateList where dateCreated less than or equals to UPDATED_DATE_CREATED
        defaultDocumentTemplateShouldBeFound("dateCreated.lessThan=" + UPDATED_DATE_CREATED);
    }


    @Test
    @Transactional
    public void getAllDocumentTemplatesByUpdateDateIsEqualToSomething() throws Exception {
        // Initialize the database
        documentTemplateRepository.saveAndFlush(documentTemplate);

        // Get all the documentTemplateList where updateDate equals to DEFAULT_UPDATE_DATE
        defaultDocumentTemplateShouldBeFound("updateDate.equals=" + DEFAULT_UPDATE_DATE);

        // Get all the documentTemplateList where updateDate equals to UPDATED_UPDATE_DATE
        defaultDocumentTemplateShouldNotBeFound("updateDate.equals=" + UPDATED_UPDATE_DATE);
    }

    @Test
    @Transactional
    public void getAllDocumentTemplatesByUpdateDateIsInShouldWork() throws Exception {
        // Initialize the database
        documentTemplateRepository.saveAndFlush(documentTemplate);

        // Get all the documentTemplateList where updateDate in DEFAULT_UPDATE_DATE or UPDATED_UPDATE_DATE
        defaultDocumentTemplateShouldBeFound("updateDate.in=" + DEFAULT_UPDATE_DATE + "," + UPDATED_UPDATE_DATE);

        // Get all the documentTemplateList where updateDate equals to UPDATED_UPDATE_DATE
        defaultDocumentTemplateShouldNotBeFound("updateDate.in=" + UPDATED_UPDATE_DATE);
    }

    @Test
    @Transactional
    public void getAllDocumentTemplatesByUpdateDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        documentTemplateRepository.saveAndFlush(documentTemplate);

        // Get all the documentTemplateList where updateDate is not null
        defaultDocumentTemplateShouldBeFound("updateDate.specified=true");

        // Get all the documentTemplateList where updateDate is null
        defaultDocumentTemplateShouldNotBeFound("updateDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllDocumentTemplatesByUpdateDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        documentTemplateRepository.saveAndFlush(documentTemplate);

        // Get all the documentTemplateList where updateDate greater than or equals to DEFAULT_UPDATE_DATE
        defaultDocumentTemplateShouldBeFound("updateDate.greaterOrEqualThan=" + DEFAULT_UPDATE_DATE);

        // Get all the documentTemplateList where updateDate greater than or equals to UPDATED_UPDATE_DATE
        defaultDocumentTemplateShouldNotBeFound("updateDate.greaterOrEqualThan=" + UPDATED_UPDATE_DATE);
    }

    @Test
    @Transactional
    public void getAllDocumentTemplatesByUpdateDateIsLessThanSomething() throws Exception {
        // Initialize the database
        documentTemplateRepository.saveAndFlush(documentTemplate);

        // Get all the documentTemplateList where updateDate less than or equals to DEFAULT_UPDATE_DATE
        defaultDocumentTemplateShouldNotBeFound("updateDate.lessThan=" + DEFAULT_UPDATE_DATE);

        // Get all the documentTemplateList where updateDate less than or equals to UPDATED_UPDATE_DATE
        defaultDocumentTemplateShouldBeFound("updateDate.lessThan=" + UPDATED_UPDATE_DATE);
    }


    @Test
    @Transactional
    public void getAllDocumentTemplatesByIfrisModelIsEqualToSomething() throws Exception {
        // Initialize the database
        IfrisModel ifrisModel = IfrisModelResourceIntTest.createEntity(em);
        em.persist(ifrisModel);
        em.flush();
        documentTemplate.setIfrisModel(ifrisModel);
        documentTemplateRepository.saveAndFlush(documentTemplate);
        Long ifrisModelId = ifrisModel.getId();

        // Get all the documentTemplateList where ifrisModel equals to ifrisModelId
        defaultDocumentTemplateShouldBeFound("ifrisModelId.equals=" + ifrisModelId);

        // Get all the documentTemplateList where ifrisModel equals to ifrisModelId + 1
        defaultDocumentTemplateShouldNotBeFound("ifrisModelId.equals=" + (ifrisModelId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultDocumentTemplateShouldBeFound(String filter) throws Exception {
        restDocumentTemplateMockMvc.perform(get("/api/document-templates?sort=id,desc&" + filter))
                                   .andExpect(status().isOk())
                                   .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                                   .andExpect(jsonPath("$.[*].id").value(hasItem(documentTemplate.getId().intValue())))
                                   .andExpect(jsonPath("$.[*].dateCreated").value(hasItem(DEFAULT_DATE_CREATED.toString())))
                                   .andExpect(jsonPath("$.[*].updateDate").value(hasItem(DEFAULT_UPDATE_DATE.toString())))
                                   .andExpect(jsonPath("$.[*].templateFileContentType").value(hasItem(DEFAULT_TEMPLATE_FILE_CONTENT_TYPE)))
                                   .andExpect(jsonPath("$.[*].templateFile").value(hasItem(Base64Utils.encodeToString(DEFAULT_TEMPLATE_FILE))));

        // Check, that the count call also returns 1
        restDocumentTemplateMockMvc.perform(get("/api/document-templates/count?sort=id,desc&" + filter))
                                   .andExpect(status().isOk())
                                   .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                                   .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultDocumentTemplateShouldNotBeFound(String filter) throws Exception {
        restDocumentTemplateMockMvc.perform(get("/api/document-templates?sort=id,desc&" + filter))
                                   .andExpect(status().isOk())
                                   .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                                   .andExpect(jsonPath("$").isArray())
                                   .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDocumentTemplateMockMvc.perform(get("/api/document-templates/count?sort=id,desc&" + filter))
                                   .andExpect(status().isOk())
                                   .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                                   .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingDocumentTemplate() throws Exception {
        // Get the documentTemplate
        restDocumentTemplateMockMvc.perform(get("/api/document-templates/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDocumentTemplate() throws Exception {
        // Initialize the database
        documentTemplateRepository.saveAndFlush(documentTemplate);

        int databaseSizeBeforeUpdate = documentTemplateRepository.findAll().size();

        // Update the documentTemplate
        DocumentTemplate updatedDocumentTemplate = documentTemplateRepository.findById(documentTemplate.getId()).get();
        // Disconnect from session so that the updates on updatedDocumentTemplate are not directly saved in db
        em.detach(updatedDocumentTemplate);
        updatedDocumentTemplate.dateCreated(UPDATED_DATE_CREATED).updateDate(UPDATED_UPDATE_DATE).templateFile(UPDATED_TEMPLATE_FILE).templateFileContentType(UPDATED_TEMPLATE_FILE_CONTENT_TYPE);
        DocumentTemplateDTO documentTemplateDTO = documentTemplateMapper.toDto(updatedDocumentTemplate);

        restDocumentTemplateMockMvc.perform(put("/api/document-templates").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(documentTemplateDTO)))
                                   .andExpect(status().isOk());

        // Validate the DocumentTemplate in the database
        List<DocumentTemplate> documentTemplateList = documentTemplateRepository.findAll();
        assertThat(documentTemplateList).hasSize(databaseSizeBeforeUpdate);
        DocumentTemplate testDocumentTemplate = documentTemplateList.get(documentTemplateList.size() - 1);
        assertThat(testDocumentTemplate.getDateCreated()).isEqualTo(UPDATED_DATE_CREATED);
        assertThat(testDocumentTemplate.getUpdateDate()).isEqualTo(UPDATED_UPDATE_DATE);
        assertThat(testDocumentTemplate.getTemplateFile()).isEqualTo(UPDATED_TEMPLATE_FILE);
        assertThat(testDocumentTemplate.getTemplateFileContentType()).isEqualTo(UPDATED_TEMPLATE_FILE_CONTENT_TYPE);

        // Validate the DocumentTemplate in Elasticsearch
        verify(mockDocumentTemplateSearchRepository, times(1)).save(testDocumentTemplate);
    }

    @Test
    @Transactional
    public void updateNonExistingDocumentTemplate() throws Exception {
        int databaseSizeBeforeUpdate = documentTemplateRepository.findAll().size();

        // Create the DocumentTemplate
        DocumentTemplateDTO documentTemplateDTO = documentTemplateMapper.toDto(documentTemplate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDocumentTemplateMockMvc.perform(put("/api/document-templates").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(documentTemplateDTO)))
                                   .andExpect(status().isBadRequest());

        // Validate the DocumentTemplate in the database
        List<DocumentTemplate> documentTemplateList = documentTemplateRepository.findAll();
        assertThat(documentTemplateList).hasSize(databaseSizeBeforeUpdate);

        // Validate the DocumentTemplate in Elasticsearch
        verify(mockDocumentTemplateSearchRepository, times(0)).save(documentTemplate);
    }

    @Test
    @Transactional
    public void deleteDocumentTemplate() throws Exception {
        // Initialize the database
        documentTemplateRepository.saveAndFlush(documentTemplate);

        int databaseSizeBeforeDelete = documentTemplateRepository.findAll().size();

        // Delete the documentTemplate
        restDocumentTemplateMockMvc.perform(delete("/api/document-templates/{id}", documentTemplate.getId()).accept(TestUtil.APPLICATION_JSON_UTF8)).andExpect(status().isOk());

        // Validate the database is empty
        List<DocumentTemplate> documentTemplateList = documentTemplateRepository.findAll();
        assertThat(documentTemplateList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the DocumentTemplate in Elasticsearch
        verify(mockDocumentTemplateSearchRepository, times(1)).deleteById(documentTemplate.getId());
    }

    @Test
    @Transactional
    public void searchDocumentTemplate() throws Exception {
        // Initialize the database
        documentTemplateRepository.saveAndFlush(documentTemplate);
        when(mockDocumentTemplateSearchRepository.search(queryStringQuery("id:" + documentTemplate.getId()), PageRequest.of(0, 20))).thenReturn(
            new PageImpl<>(Collections.singletonList(documentTemplate), PageRequest.of(0, 1), 1));
        // Search the documentTemplate
        restDocumentTemplateMockMvc.perform(get("/api/_search/document-templates?query=id:" + documentTemplate.getId()))
                                   .andExpect(status().isOk())
                                   .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                                   .andExpect(jsonPath("$.[*].id").value(hasItem(documentTemplate.getId().intValue())))
                                   .andExpect(jsonPath("$.[*].dateCreated").value(hasItem(DEFAULT_DATE_CREATED.toString())))
                                   .andExpect(jsonPath("$.[*].updateDate").value(hasItem(DEFAULT_UPDATE_DATE.toString())))
                                   .andExpect(jsonPath("$.[*].templateFileContentType").value(hasItem(DEFAULT_TEMPLATE_FILE_CONTENT_TYPE)))
                                   .andExpect(jsonPath("$.[*].templateFile").value(hasItem(Base64Utils.encodeToString(DEFAULT_TEMPLATE_FILE))));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DocumentTemplate.class);
        DocumentTemplate documentTemplate1 = new DocumentTemplate();
        documentTemplate1.setId(1L);
        DocumentTemplate documentTemplate2 = new DocumentTemplate();
        documentTemplate2.setId(documentTemplate1.getId());
        assertThat(documentTemplate1).isEqualTo(documentTemplate2);
        documentTemplate2.setId(2L);
        assertThat(documentTemplate1).isNotEqualTo(documentTemplate2);
        documentTemplate1.setId(null);
        assertThat(documentTemplate1).isNotEqualTo(documentTemplate2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DocumentTemplateDTO.class);
        DocumentTemplateDTO documentTemplateDTO1 = new DocumentTemplateDTO();
        documentTemplateDTO1.setId(1L);
        DocumentTemplateDTO documentTemplateDTO2 = new DocumentTemplateDTO();
        assertThat(documentTemplateDTO1).isNotEqualTo(documentTemplateDTO2);
        documentTemplateDTO2.setId(documentTemplateDTO1.getId());
        assertThat(documentTemplateDTO1).isEqualTo(documentTemplateDTO2);
        documentTemplateDTO2.setId(2L);
        assertThat(documentTemplateDTO1).isNotEqualTo(documentTemplateDTO2);
        documentTemplateDTO1.setId(null);
        assertThat(documentTemplateDTO1).isNotEqualTo(documentTemplateDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(documentTemplateMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(documentTemplateMapper.fromId(null)).isNull();
    }
}
