package io.github.ifris.files.repository.search;

import io.github.ifris.files.domain.IfrisModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the IfrisModel entity.
 */
public interface IfrisModelSearchRepository extends ElasticsearchRepository<IfrisModel, Long> {
}
