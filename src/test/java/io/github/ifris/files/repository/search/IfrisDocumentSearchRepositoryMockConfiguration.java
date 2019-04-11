package io.github.ifris.files.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of IfrisDocumentSearchRepository to test the application without starting Elasticsearch.
 */
@Configuration
public class IfrisDocumentSearchRepositoryMockConfiguration {

    @MockBean
    private IfrisDocumentSearchRepository mockIfrisDocumentSearchRepository;

}
