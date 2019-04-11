package io.github.ifris.files.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of DocumentTemplateSearchRepository to test the application without starting Elasticsearch.
 */
@Configuration
public class DocumentTemplateSearchRepositoryMockConfiguration {

    @MockBean
    private DocumentTemplateSearchRepository mockDocumentTemplateSearchRepository;

}
