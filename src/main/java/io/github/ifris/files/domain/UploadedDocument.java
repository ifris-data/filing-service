package io.github.ifris.files.domain;


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A UploadedDocument.
 *
 * Created to track IfrisDocument items which have been uploaded in this or other instances.
 * That way the messanger end points can check if a given alert in the MQ contains a mesage
 * about a document already persisted by this instance or one that this instance needs to
 * query
 *
 */
@Entity
@Table(name = "uploaded_document")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "uploadeddocument")
public class UploadedDocument implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "file_name", nullable = false)
    private String fileName;

    @NotNull
    @Column(name = "jhi_year", nullable = false)
    private String year;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull
    @Column(name = "period_start", nullable = false)
    private String periodStart;

    @NotNull
    @Column(name = "period_end", nullable = false)
    private String periodEnd;

    @NotNull
    @Column(name = "content_type", nullable = false)
    private String contentType;

    @NotNull
    @Column(name = "ifris_model", nullable = false)
    private String ifrisModel;

    @NotNull
    @Column(name = "app_instance", nullable = false)
    private String appInstance;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public UploadedDocument fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public UploadedDocument year(String year) {
        this.year = year;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UploadedDocument description(String description) {
        this.description = description;
        return this;
    }

    public String getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(String periodStart) {
        this.periodStart = periodStart;
    }

    public UploadedDocument periodStart(String periodStart) {
        this.periodStart = periodStart;
        return this;
    }

    public String getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(String periodEnd) {
        this.periodEnd = periodEnd;
    }

    public UploadedDocument periodEnd(String periodEnd) {
        this.periodEnd = periodEnd;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public UploadedDocument contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public String getIfrisModel() {
        return ifrisModel;
    }

    public void setIfrisModel(String ifrisModel) {
        this.ifrisModel = ifrisModel;
    }

    public UploadedDocument ifrisModel(String ifrisModel) {
        this.ifrisModel = ifrisModel;
        return this;
    }

    public String getAppInstance() {
        return appInstance;
    }

    public void setAppInstance(String appInstance) {
        this.appInstance = appInstance;
    }

    public UploadedDocument appInstance(String appInstance) {
        this.appInstance = appInstance;
        return this;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UploadedDocument uploadedDocument = (UploadedDocument) o;
        if (uploadedDocument.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), uploadedDocument.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "UploadedDocument{" + "id=" + getId() + ", fileName='" + getFileName() + "'" + ", year='" + getYear() + "'" + ", description='" + getDescription() + "'" + ", periodStart='" +
            getPeriodStart() + "'" + ", periodEnd='" + getPeriodEnd() + "'" + ", contentType='" + getContentType() + "'" + ", ifrisModel='" + getIfrisModel() + "'" + ", appInstance='" +
            getAppInstance() + "'" + "}";
    }
}
