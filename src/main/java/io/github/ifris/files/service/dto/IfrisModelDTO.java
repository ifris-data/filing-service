package io.github.ifris.files.service.dto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the IfrisModel entity.
 */
public class IfrisModelDTO implements Serializable {

    private Long id;

    @NotNull
    private String modelName;

    @NotNull
    private String description;

    private String serviceName;

    private String servicePort;


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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServicePort() {
        return servicePort;
    }

    public void setServicePort(String servicePort) {
        this.servicePort = servicePort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        IfrisModelDTO ifrisModelDTO = (IfrisModelDTO) o;
        if (ifrisModelDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), ifrisModelDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "IfrisModelDTO{" + "id=" + getId() + ", modelName='" + getModelName() + "'" + ", description='" + getDescription() + "'" + ", serviceName='" + getServiceName() + "'" +
            ", servicePort='" + getServicePort() + "'" + "}";
    }
}
