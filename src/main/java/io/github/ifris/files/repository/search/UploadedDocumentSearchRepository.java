package io.github.ifris.files.repository.search;

import io.github.ifris.files.domain.UploadedDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the UploadedDocument entity.
 */
public interface UploadedDocumentSearchRepository extends ElasticsearchRepository<UploadedDocument, Long> {
}
