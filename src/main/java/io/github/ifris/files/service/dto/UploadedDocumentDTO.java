package io.github.ifris.files.service.dto;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the UploadedDocument entity.
 */
public class UploadedDocumentDTO implements Serializable {

    private Long id;

    @NotNull
    private String fileName;

    @NotNull
    private String year;

    @NotNull
    private String description;

    @NotNull
    private String periodStart;

    @NotNull
    private String periodEnd;

    @NotNull
    private String contentType;

    @NotNull
    private String ifrisModel;


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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(String periodStart) {
        this.periodStart = periodStart;
    }

    public String getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(String periodEnd) {
        this.periodEnd = periodEnd;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getIfrisModel() {
        return ifrisModel;
    }

    public void setIfrisModel(String ifrisModel) {
        this.ifrisModel = ifrisModel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UploadedDocumentDTO uploadedDocumentDTO = (UploadedDocumentDTO) o;
        if (uploadedDocumentDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), uploadedDocumentDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "UploadedDocumentDTO{" +
            "id=" + getId() +
            ", fileName='" + getFileName() + "'" +
            ", year='" + getYear() + "'" +
            ", description='" + getDescription() + "'" +
            ", periodStart='" + getPeriodStart() + "'" +
            ", periodEnd='" + getPeriodEnd() + "'" +
            ", contentType='" + getContentType() + "'" +
            ", ifrisModel='" + getIfrisModel() + "'" +
            "}";
    }
}
