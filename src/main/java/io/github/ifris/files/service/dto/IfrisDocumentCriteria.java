package io.github.ifris.files.service.dto;

import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.LocalDateFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Criteria class for the IfrisDocument entity. This class is used in IfrisDocumentResource to receive all the possible filtering options from the Http GET request parameters. For example the
 * following could be a valid requests:
 * <code> /ifris-documents?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use fix type specific filters.
 */
public class IfrisDocumentCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter fileName;

    private LongFilter year;

    private StringFilter description;

    private LocalDateFilter periodStart;

    private LocalDateFilter periodEnd;

    private LongFilter ifrisModelId;

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

    public LongFilter getYear() {
        return year;
    }

    public void setYear(LongFilter year) {
        this.year = year;
    }

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public LocalDateFilter getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDateFilter periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDateFilter getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDateFilter periodEnd) {
        this.periodEnd = periodEnd;
    }

    public LongFilter getIfrisModelId() {
        return ifrisModelId;
    }

    public void setIfrisModelId(LongFilter ifrisModelId) {
        this.ifrisModelId = ifrisModelId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final IfrisDocumentCriteria that = (IfrisDocumentCriteria) o;
        return Objects.equals(id, that.id) && Objects.equals(fileName, that.fileName) && Objects.equals(year, that.year) && Objects.equals(description, that.description) &&
            Objects.equals(periodStart, that.periodStart) && Objects.equals(periodEnd, that.periodEnd) && Objects.equals(ifrisModelId, that.ifrisModelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fileName, year, description, periodStart, periodEnd, ifrisModelId);
    }

    @Override
    public String toString() {
        return "IfrisDocumentCriteria{" + (id != null ? "id=" + id + ", " : "") + (fileName != null ? "fileName=" + fileName + ", " : "") + (year != null ? "year=" + year + ", " : "") +
            (description != null ? "description=" + description + ", " : "") + (periodStart != null ? "periodStart=" + periodStart + ", " : "") +
            (periodEnd != null ? "periodEnd=" + periodEnd + ", " : "") + (ifrisModelId != null ? "ifrisModelId=" + ifrisModelId + ", " : "") + "}";
    }

}
