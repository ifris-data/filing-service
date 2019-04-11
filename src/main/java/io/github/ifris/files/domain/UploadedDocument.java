package io.github.ifris.files.domain;


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Objects;

/**
 * A UploadedDocument.
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

    public UploadedDocument fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getYear() {
        return year;
    }

    public UploadedDocument year(String year) {
        this.year = year;
        return this;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDescription() {
        return description;
    }

    public UploadedDocument description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPeriodStart() {
        return periodStart;
    }

    public UploadedDocument periodStart(String periodStart) {
        this.periodStart = periodStart;
        return this;
    }

    public void setPeriodStart(String periodStart) {
        this.periodStart = periodStart;
    }

    public String getPeriodEnd() {
        return periodEnd;
    }

    public UploadedDocument periodEnd(String periodEnd) {
        this.periodEnd = periodEnd;
        return this;
    }

    public void setPeriodEnd(String periodEnd) {
        this.periodEnd = periodEnd;
    }

    public String getContentType() {
        return contentType;
    }

    public UploadedDocument contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getIfrisModel() {
        return ifrisModel;
    }

    public UploadedDocument ifrisModel(String ifrisModel) {
        this.ifrisModel = ifrisModel;
        return this;
    }

    public void setIfrisModel(String ifrisModel) {
        this.ifrisModel = ifrisModel;
    }

    public String getAppInstance() {
        return appInstance;
    }

    public UploadedDocument appInstance(String appInstance) {
        this.appInstance = appInstance;
        return this;
    }

    public void setAppInstance(String appInstance) {
        this.appInstance = appInstance;
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
        return "UploadedDocument{" +
            "id=" + getId() +
            ", fileName='" + getFileName() + "'" +
            ", year='" + getYear() + "'" +
            ", description='" + getDescription() + "'" +
            ", periodStart='" + getPeriodStart() + "'" +
            ", periodEnd='" + getPeriodEnd() + "'" +
            ", contentType='" + getContentType() + "'" +
            ", ifrisModel='" + getIfrisModel() + "'" +
            ", appInstance='" + getAppInstance() + "'" +
            "}";
    }
}
