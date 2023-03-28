package com.soam.web.templatelink;

import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.stakeholder.StakeholderTemplate;

public class TemplateLinkFormDto {
    private SpecificationTemplate filterSpecificationTemplate;
    private StakeholderTemplate filterStakeholderTemplate;

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
}
