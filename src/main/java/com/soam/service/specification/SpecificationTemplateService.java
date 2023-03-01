package com.soam.service.specification;

import com.soam.config.SoamProperties;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.specification.SpecificationTemplateRepository;
import com.soam.model.templatelink.TemplateLink;
import com.soam.model.templatelink.TemplateLinkRepository;
import com.soam.service.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class SpecificationTemplateService {
    private static final Sort SORT_ORDER = Sort.by(Sort.Order.by("name").ignoreCase());

    private final SpecificationTemplateRepository specificationTemplateRepository;
    private final TemplateLinkRepository templateLinkRepository;
    private final SoamProperties soamProperties;

    public SpecificationTemplateService(
            SpecificationTemplateRepository specificationTemplateRepository,
            TemplateLinkRepository templateLinkRepository, SoamProperties soamProperties) {
        this.specificationTemplateRepository = specificationTemplateRepository;
        this.templateLinkRepository = templateLinkRepository;
        this.soamProperties = soamProperties;
    }

    public SpecificationTemplate getById(int specificationTemplateId) {
        return specificationTemplateRepository.findById(specificationTemplateId)
                .orElseThrow(() -> new EntityNotFoundException("Specification Template", specificationTemplateId));
    }

    public Page<SpecificationTemplate> findByPrefix(String name, int page) {
        Pageable pageable = PageRequest.of(page, soamProperties.getPageSize(), SORT_ORDER);
        return specificationTemplateRepository.findByNameStartsWithIgnoreCase(name, pageable);
    }

    public Optional<SpecificationTemplate> findByName(String name) {
        return specificationTemplateRepository.findByNameIgnoreCase(name);
    }

    public List<SpecificationTemplate> findAll() {
        return specificationTemplateRepository.findAll(SORT_ORDER);
    }

    public SpecificationTemplate save(SpecificationTemplate specificationTemplate) {
        return specificationTemplateRepository.save(specificationTemplate);
    }

    @Transactional
    public SpecificationTemplate saveDeepCopy(SpecificationTemplate srcSpecificationTemplate, SpecificationTemplate dstSpecificationTemplate) {
        SpecificationTemplate savedDstSpecificationTemplate = specificationTemplateRepository.save(dstSpecificationTemplate);

        savedDstSpecificationTemplate.setTemplateLinks(new LinkedList<>());

        Collection<TemplateLink> templateLinks = srcSpecificationTemplate.getTemplateLinks();
        templateLinks.forEach(templateLink -> {
            TemplateLink newTemplateLink = new TemplateLink();
            newTemplateLink.setSpecificationTemplate(savedDstSpecificationTemplate);
            newTemplateLink.setStakeholderTemplate(templateLink.getStakeholderTemplate());
            newTemplateLink.setObjectiveTemplate(templateLink.getObjectiveTemplate());
            savedDstSpecificationTemplate.getTemplateLinks().add(newTemplateLink);
            templateLinkRepository.save(newTemplateLink);
        });

        return savedDstSpecificationTemplate;
    }

    public void delete(SpecificationTemplate specificationTemplate) {
        specificationTemplateRepository.delete(specificationTemplate);
    }
}
