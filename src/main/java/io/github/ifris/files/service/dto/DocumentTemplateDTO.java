package io.github.ifris.files.service.dto;
import java.time.LocalDate;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Lob;

/**
 * A DTO for the DocumentTemplate entity.
 */
public class DocumentTemplateDTO implements Serializable {

    private Long id;

    private LocalDate dateCreated;

    private LocalDate updateDate;

    
    @Lob
    private byte[] templateFile;

    private String templateFileContentType;

    private Long ifrisModelId;

    private String ifrisModelModelName;

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

    public LocalDate getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    public byte[] getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(byte[] templateFile) {
        this.templateFile = templateFile;
    }

    public String getTemplateFileContentType() {
        return templateFileContentType;
    }

    public void setTemplateFileContentType(String templateFileContentType) {
        this.templateFileContentType = templateFileContentType;
    }

    public Long getIfrisModelId() {
        return ifrisModelId;
    }

    public void setIfrisModelId(Long ifrisModelId) {
        this.ifrisModelId = ifrisModelId;
    }

    public String getIfrisModelModelName() {
        return ifrisModelModelName;
    }

    public void setIfrisModelModelName(String ifrisModelModelName) {
        this.ifrisModelModelName = ifrisModelModelName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DocumentTemplateDTO documentTemplateDTO = (DocumentTemplateDTO) o;
        if (documentTemplateDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), documentTemplateDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "DocumentTemplateDTO{" +
            "id=" + getId() +
            ", dateCreated='" + getDateCreated() + "'" +
            ", updateDate='" + getUpdateDate() + "'" +
            ", templateFile='" + getTemplateFile() + "'" +
            ", ifrisModel=" + getIfrisModelId() +
            ", ifrisModel='" + getIfrisModelModelName() + "'" +
            "}";
    }
}
