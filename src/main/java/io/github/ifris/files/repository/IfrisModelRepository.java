package io.github.ifris.files.repository;

import io.github.ifris.files.domain.IfrisModel;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the IfrisModel entity.
 */
@SuppressWarnings("unused")
@Repository
public interface IfrisModelRepository extends JpaRepository<IfrisModel, Long>, JpaSpecificationExecutor<IfrisModel> {

}
