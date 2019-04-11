package io.github.ifris.files.service.dto;

import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Criteria class for the UploadedDocument entity. This class is used in UploadedDocumentResource to receive all the possible filtering options from the Http GET request parameters. For example the
 * following could be a valid requests:
 * <code> /uploaded-documents?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use fix type specific filters.
 */
public class UploadedDocumentCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter fileName;

    private StringFilter year;

    private StringFilter description;

    private StringFilter periodStart;

    private StringFilter periodEnd;

    private StringFilter contentType;

    private StringFilter ifrisModel;

    private StringFilter appInstance;

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getFileName() {
        return fileName;
    }

    public void setFileName(StringFilter fileName) {
        this.fileName = fileName;
    }

    public StringFilter getYear() {
        return year;
    }

    public void setYear(StringFilter year) {
        this.year = year;
    }

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public StringFilter getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(StringFilter periodStart) {
        this.periodStart = periodStart;
    }

    public StringFilter getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(StringFilter periodEnd) {
        this.periodEnd = periodEnd;
    }

    public StringFilter getContentType() {
        return contentType;
    }

    public void setContentType(StringFilter contentType) {
        this.contentType = contentType;
    }

    public StringFilter getIfrisModel() {
        return ifrisModel;
    }

    public void setIfrisModel(StringFilter ifrisModel) {
        this.ifrisModel = ifrisModel;
    }

    public StringFilter getAppInstance() {
        return appInstance;
    }

    public void setAppInstance(StringFilter appInstance) {
        this.appInstance = appInstance;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final UploadedDocumentCriteria that = (UploadedDocumentCriteria) o;
        return Objects.equals(id, that.id) && Objects.equals(fileName, that.fileName) && Objects.equals(year, that.year) && Objects.equals(description, that.description) &&
            Objects.equals(periodStart, that.periodStart) && Objects.equals(periodEnd, that.periodEnd) && Objects.equals(contentType, that.contentType) &&
            Objects.equals(ifrisModel, that.ifrisModel) && Objects.equals(appInstance, that.appInstance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fileName, year, description, periodStart, periodEnd, contentType, ifrisModel, appInstance);
    }

    @Override
    public String toString() {
        return "UploadedDocumentCriteria{" + (id != null ? "id=" + id + ", " : "") + (fileName != null ? "fileName=" + fileName + ", " : "") + (year != null ? "year=" + year + ", " : "") +
            (description != null ? "description=" + description + ", " : "") + (periodStart != null ? "periodStart=" + periodStart + ", " : "") +
            (periodEnd != null ? "periodEnd=" + periodEnd + ", " : "") + (contentType != null ? "contentType=" + contentType + ", " : "") +
            (ifrisModel != null ? "ifrisModel=" + ifrisModel + ", " : "") + (appInstance != null ? "appInstance=" + appInstance + ", " : "") + "}";
    }

}
