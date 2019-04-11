package io.github.ifris.files.service.dto;

import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Criteria class for the IfrisModel entity. This class is used in IfrisModelResource to receive all the possible filtering options from the Http GET request parameters. For example the following
 * could be a valid requests:
 * <code> /ifris-models?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use fix type specific filters.
 */
public class IfrisModelCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter modelName;

    private StringFilter description;

    private StringFilter serviceName;

    private StringFilter servicePort;

    private LongFilter ifrisDocumentId;

    private LongFilter documentTemplateId;

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getModelName() {
        return modelName;
    }

    public void setModelName(StringFilter modelName) {
        this.modelName = modelName;
    }

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public StringFilter getServiceName() {
        return serviceName;
    }

    public void setServiceName(StringFilter serviceName) {
        this.serviceName = serviceName;
    }

    public StringFilter getServicePort() {
        return servicePort;
    }

    public void setServicePort(StringFilter servicePort) {
        this.servicePort = servicePort;
    }

    public LongFilter getIfrisDocumentId() {
        return ifrisDocumentId;
    }

    public void setIfrisDocumentId(LongFilter ifrisDocumentId) {
        this.ifrisDocumentId = ifrisDocumentId;
    }

    public LongFilter getDocumentTemplateId() {
        return documentTemplateId;
    }

    public void setDocumentTemplateId(LongFilter documentTemplateId) {
        this.documentTemplateId = documentTemplateId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final IfrisModelCriteria that = (IfrisModelCriteria) o;
        return Objects.equals(id, that.id) && Objects.equals(modelName, that.modelName) && Objects.equals(description, that.description) && Objects.equals(serviceName, that.serviceName) &&
            Objects.equals(servicePort, that.servicePort) && Objects.equals(ifrisDocumentId, that.ifrisDocumentId) && Objects.equals(documentTemplateId, that.documentTemplateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, modelName, description, serviceName, servicePort, ifrisDocumentId, documentTemplateId);
    }

    @Override
    public String toString() {
        return "IfrisModelCriteria{" + (id != null ? "id=" + id + ", " : "") + (modelName != null ? "modelName=" + modelName + ", " : "") +
            (description != null ? "description=" + description + ", " : "") + (serviceName != null ? "serviceName=" + serviceName + ", " : "") +
            (servicePort != null ? "servicePort=" + servicePort + ", " : "") + (ifrisDocumentId != null ? "ifrisDocumentId=" + ifrisDocumentId + ", " : "") +
            (documentTemplateId != null ? "documentTemplateId=" + documentTemplateId + ", " : "") + "}";
    }

}
