package io.github.ifris.files.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of IfrisModelSearchRepository to test the application without starting Elasticsearch.
 */
@Configuration
public class IfrisModelSearchRepositoryMockConfiguration {

    @MockBean
    private IfrisModelSearchRepository mockIfrisModelSearchRepository;

}
