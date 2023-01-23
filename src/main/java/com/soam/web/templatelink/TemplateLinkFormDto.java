package com.soam.web.templatelink;

import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.stakeholder.StakeholderTemplate;
import com.soam.model.templatelink.TemplateLink;
import jakarta.validation.Valid;

public class TemplateLinkFormDto {
    private SpecificationTemplate filterSpecificationTemplate;
    private StakeholderTemplate filterStakeholderTemplate;
    @Valid
    private TemplateLink newTemplateLink;
    private int deleteTemplateLinkId;

    public SpecificationTemplate getFilterSpecificationTemplate() {
        return filterSpecificationTemplate;
    }

    public void setFilterSpecificationTemplate(SpecificationTemplate filterSpecificationTemplate) {
        this.filterSpecificationTemplate = filterSpecificationTemplate;
    }

    public StakeholderTemplate getFilterStakeholderTemplate() {
        return filterStakeholderTemplate;
    }

    public void setFilterStakeholderTemplate(StakeholderTemplate filterStakeholderTemplate) {
        this.filterStakeholderTemplate = filterStakeholderTemplate;
    }

    public TemplateLink getNewTemplateLink() {
        return newTemplateLink;
    }

    public void setNewTemplateLink(TemplateLink newTemplateLink) {
        this.newTemplateLink = newTemplateLink;
    }

    public int getDeleteTemplateLinkId() {
        return deleteTemplateLinkId;
    }

    public void setDeleteTemplateLinkId(int deleteTemplateLinkId) {
        this.deleteTemplateLinkId = deleteTemplateLinkId;
    }
}
