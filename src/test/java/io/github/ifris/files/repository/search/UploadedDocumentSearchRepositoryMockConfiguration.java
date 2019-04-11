package io.github.ifris.files.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of UploadedDocumentSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class UploadedDocumentSearchRepositoryMockConfiguration {

    @MockBean
    private UploadedDocumentSearchRepository mockUploadedDocumentSearchRepository;

}
