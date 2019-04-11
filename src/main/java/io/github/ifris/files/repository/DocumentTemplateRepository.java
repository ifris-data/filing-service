package io.github.ifris.files.repository;

import io.github.ifris.files.domain.DocumentTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the DocumentTemplate entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DocumentTemplateRepository extends JpaRepository<DocumentTemplate, Long>, JpaSpecificationExecutor<DocumentTemplate> {

}
