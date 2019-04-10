package io.github.ifris.files.repository.search;

import io.github.ifris.files.domain.IfrisDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the IfrisDocument entity.
 */
public interface IfrisDocumentSearchRepository extends ElasticsearchRepository<IfrisDocument, Long> {
}
