package com.soam.model.specification;

import com.soam.model.SoamEntity;
import com.soam.model.templatelink.TemplateLink;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.List;

@Entity
@Table( name="specification_templates", uniqueConstraints = {@UniqueConstraint(columnNames = { "name"})})
public class SpecificationTemplate extends SoamEntity {
    @OneToMany(mappedBy = "specificationTemplate")
    private List<TemplateLink> templateLinks;

    public List<TemplateLink> getTemplateLinks() {
        return templateLinks;
    }

    public void setTemplateLinks(List<TemplateLink> templateLinks) {
        this.templateLinks = templateLinks;
    }
}
