package io.github.ifris.files.repository.search;

import io.github.ifris.files.domain.DocumentTemplate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the DocumentTemplate entity.
 */
public interface DocumentTemplateSearchRepository extends ElasticsearchRepository<DocumentTemplate, Long> {
}
