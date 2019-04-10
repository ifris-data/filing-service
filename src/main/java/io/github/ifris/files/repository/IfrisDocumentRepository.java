package io.github.ifris.files.repository;

import io.github.ifris.files.domain.IfrisDocument;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the IfrisDocument entity.
 */
@SuppressWarnings("unused")
@Repository
public interface IfrisDocumentRepository extends JpaRepository<IfrisDocument, Long>, JpaSpecificationExecutor<IfrisDocument> {

}
