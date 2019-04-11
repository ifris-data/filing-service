package io.github.ifris.files.service.dto;

import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.LocalDateFilter;
import io.github.jhipster.service.filter.LongFilter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Criteria class for the DocumentTemplate entity. This class is used in DocumentTemplateResource to receive all the possible filtering options from the Http GET request parameters. For example the
 * following could be a valid requests:
 * <code> /document-templates?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use fix type specific filters.
 */
public class DocumentTemplateCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LocalDateFilter dateCreated;

    private LocalDateFilter updateDate;

    private LongFilter ifrisModelId;

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LocalDateFilter getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateFilter dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateFilter getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateFilter updateDate) {
        this.updateDate = updateDate;
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
        final DocumentTemplateCriteria that = (DocumentTemplateCriteria) o;
        return Objects.equals(id, that.id) && Objects.equals(dateCreated, that.dateCreated) && Objects.equals(updateDate, that.updateDate) && Objects.equals(ifrisModelId, that.ifrisModelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dateCreated, updateDate, ifrisModelId);
    }

    @Override
    public String toString() {
        return "DocumentTemplateCriteria{" + (id != null ? "id=" + id + ", " : "") + (dateCreated != null ? "dateCreated=" + dateCreated + ", " : "") +
            (updateDate != null ? "updateDate=" + updateDate + ", " : "") + (ifrisModelId != null ? "ifrisModelId=" + ifrisModelId + ", " : "") + "}";
    }

}
