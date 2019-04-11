package io.github.ifris.files.repository;

import io.github.ifris.files.domain.UploadedDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the UploadedDocument entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UploadedDocumentRepository extends JpaRepository<UploadedDocument, Long>, JpaSpecificationExecutor<UploadedDocument> {

}
