package io.github.ifris.files.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DocumentTemplate.
 */
@Entity
@Table(name = "document_template")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "documenttemplate")
public class DocumentTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_created")
    private LocalDate dateCreated;

    @Column(name = "update_date")
    private LocalDate updateDate;


    @Lob
    @Column(name = "template_file", nullable = false)
    private byte[] templateFile;

    @Column(name = "template_file_content_type", nullable = false)
    private String templateFileContentType;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("documentTemplates")
    private IfrisModel ifrisModel;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public DocumentTemplate dateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }

    public LocalDate getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    public DocumentTemplate updateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
        return this;
    }

    public byte[] getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(byte[] templateFile) {
        this.templateFile = templateFile;
    }

    public DocumentTemplate templateFile(byte[] templateFile) {
        this.templateFile = templateFile;
        return this;
    }

    public String getTemplateFileContentType() {
        return templateFileContentType;
    }

    public void setTemplateFileContentType(String templateFileContentType) {
        this.templateFileContentType = templateFileContentType;
    }

    public DocumentTemplate templateFileContentType(String templateFileContentType) {
        this.templateFileContentType = templateFileContentType;
        return this;
    }

    public IfrisModel getIfrisModel() {
        return ifrisModel;
    }

    public void setIfrisModel(IfrisModel ifrisModel) {
        this.ifrisModel = ifrisModel;
    }

    public DocumentTemplate ifrisModel(IfrisModel ifrisModel) {
        this.ifrisModel = ifrisModel;
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
        DocumentTemplate documentTemplate = (DocumentTemplate) o;
        if (documentTemplate.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), documentTemplate.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "DocumentTemplate{" + "id=" + getId() + ", dateCreated='" + getDateCreated() + "'" + ", updateDate='" + getUpdateDate() + "'" + ", templateFile='" + getTemplateFile() + "'" +
            ", templateFileContentType='" + getTemplateFileContentType() + "'" + "}";
    }
}
