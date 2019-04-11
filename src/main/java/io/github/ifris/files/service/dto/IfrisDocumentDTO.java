package io.github.ifris.files.service.dto;

import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the IfrisDocument entity.
 */
public class IfrisDocumentDTO implements Serializable {

    private Long id;

    @NotNull
    private String fileName;

    @NotNull
    private Long year;

    private String description;

    @NotNull
    private LocalDate periodStart;

    @NotNull
    private LocalDate periodEnd;


    @Lob
    private byte[] content;

    private String contentContentType;

    private Long ifrisModelId;

    private String ifrisModelModelName;

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

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getContentContentType() {
        return contentContentType;
    }

    public void setContentContentType(String contentContentType) {
        this.contentContentType = contentContentType;
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

        IfrisDocumentDTO ifrisDocumentDTO = (IfrisDocumentDTO) o;
        if (ifrisDocumentDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), ifrisDocumentDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "IfrisDocumentDTO{" + "id=" + getId() + ", fileName='" + getFileName() + "'" + ", year=" + getYear() + ", description='" + getDescription() + "'" + ", periodStart='" +
            getPeriodStart() + "'" + ", periodEnd='" + getPeriodEnd() + "'" + ", content='" + getContent() + "'" + ", ifrisModel=" + getIfrisModelId() + ", ifrisModel='" + getIfrisModelModelName() +
            "'" + "}";
    }
}
