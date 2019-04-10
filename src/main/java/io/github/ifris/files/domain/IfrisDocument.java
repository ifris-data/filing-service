package io.github.ifris.files.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A IfrisDocument.
 */
@Entity
@Table(name = "ifris_document")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "ifrisdocument")
public class IfrisDocument implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "file_name", nullable = false)
    private String fileName;

    @NotNull
    @Column(name = "jhi_year", nullable = false)
    private Long year;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @NotNull
    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    
    @Lob
    @Column(name = "content", nullable = false)
    private byte[] content;

    @Column(name = "content_content_type", nullable = false)
    private String contentContentType;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("ifrisDocuments")
    private IfrisModel ifrisModel;

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

    public IfrisDocument fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getYear() {
        return year;
    }

    public IfrisDocument year(Long year) {
        this.year = year;
        return this;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    public String getDescription() {
        return description;
    }

    public IfrisDocument description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public IfrisDocument periodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
        return this;
    }

    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public IfrisDocument periodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
        return this;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    public byte[] getContent() {
        return content;
    }

    public IfrisDocument content(byte[] content) {
        this.content = content;
        return this;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getContentContentType() {
        return contentContentType;
    }

    public IfrisDocument contentContentType(String contentContentType) {
        this.contentContentType = contentContentType;
        return this;
    }

    public void setContentContentType(String contentContentType) {
        this.contentContentType = contentContentType;
    }

    public IfrisModel getIfrisModel() {
        return ifrisModel;
    }

    public IfrisDocument ifrisModel(IfrisModel ifrisModel) {
        this.ifrisModel = ifrisModel;
        return this;
    }

    public void setIfrisModel(IfrisModel ifrisModel) {
        this.ifrisModel = ifrisModel;
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
        IfrisDocument ifrisDocument = (IfrisDocument) o;
        if (ifrisDocument.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), ifrisDocument.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "IfrisDocument{" +
            "id=" + getId() +
            ", fileName='" + getFileName() + "'" +
            ", year=" + getYear() +
            ", description='" + getDescription() + "'" +
            ", periodStart='" + getPeriodStart() + "'" +
            ", periodEnd='" + getPeriodEnd() + "'" +
            ", content='" + getContent() + "'" +
            ", contentContentType='" + getContentContentType() + "'" +
            "}";
    }
}
