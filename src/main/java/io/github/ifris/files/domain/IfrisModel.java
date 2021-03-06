package io.github.ifris.files.domain;


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A IfrisModel.
 */
@Entity
@Table(name = "ifris_model")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "ifrismodel")
public class IfrisModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "model_name", nullable = false)
    private String modelName;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "service_port")
    private String servicePort;

    @OneToMany(mappedBy = "ifrisModel")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<IfrisDocument> ifrisDocuments = new HashSet<>();
    @OneToMany(mappedBy = "ifrisModel")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<DocumentTemplate> documentTemplates = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public IfrisModel modelName(String modelName) {
        this.modelName = modelName;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IfrisModel description(String description) {
        this.description = description;
        return this;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public IfrisModel serviceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public String getServicePort() {
        return servicePort;
    }

    public void setServicePort(String servicePort) {
        this.servicePort = servicePort;
    }

    public IfrisModel servicePort(String servicePort) {
        this.servicePort = servicePort;
        return this;
    }

    public Set<IfrisDocument> getIfrisDocuments() {
        return ifrisDocuments;
    }

    public void setIfrisDocuments(Set<IfrisDocument> ifrisDocuments) {
        this.ifrisDocuments = ifrisDocuments;
    }

    public IfrisModel ifrisDocuments(Set<IfrisDocument> ifrisDocuments) {
        this.ifrisDocuments = ifrisDocuments;
        return this;
    }

    public IfrisModel addIfrisDocument(IfrisDocument ifrisDocument) {
        this.ifrisDocuments.add(ifrisDocument);
        ifrisDocument.setIfrisModel(this);
        return this;
    }

    public IfrisModel removeIfrisDocument(IfrisDocument ifrisDocument) {
        this.ifrisDocuments.remove(ifrisDocument);
        ifrisDocument.setIfrisModel(null);
        return this;
    }

    public Set<DocumentTemplate> getDocumentTemplates() {
        return documentTemplates;
    }

    public void setDocumentTemplates(Set<DocumentTemplate> documentTemplates) {
        this.documentTemplates = documentTemplates;
    }

    public IfrisModel documentTemplates(Set<DocumentTemplate> documentTemplates) {
        this.documentTemplates = documentTemplates;
        return this;
    }

    public IfrisModel addDocumentTemplate(DocumentTemplate documentTemplate) {
        this.documentTemplates.add(documentTemplate);
        documentTemplate.setIfrisModel(this);
        return this;
    }

    public IfrisModel removeDocumentTemplate(DocumentTemplate documentTemplate) {
        this.documentTemplates.remove(documentTemplate);
        documentTemplate.setIfrisModel(null);
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
        IfrisModel ifrisModel = (IfrisModel) o;
        if (ifrisModel.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), ifrisModel.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "IfrisModel{" + "id=" + getId() + ", modelName='" + getModelName() + "'" + ", description='" + getDescription() + "'" + ", serviceName='" + getServiceName() + "'" + ", servicePort='" +
            getServicePort() + "'" + "}";
    }
}
