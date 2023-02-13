package com.soam.service.objective;

import com.soam.model.objective.ObjectiveTemplate;
import com.soam.model.objective.ObjectiveTemplateRepository;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.service.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ObjectiveTemplateService {
    private static final Sort SORT_ORDER = Sort.by(Sort.Order.by("name").ignoreCase());

    private final ObjectiveTemplateRepository objectiveTemplateRepository;

    @Value("${soam.pageSize}")
    private int pageSize;

    public ObjectiveTemplateService(ObjectiveTemplateRepository objectiveTemplateRepository) {
        this.objectiveTemplateRepository = objectiveTemplateRepository;
    }

    public ObjectiveTemplate getById(int objectiveTemplateId) {
        return objectiveTemplateRepository.findById(objectiveTemplateId)
                .orElseThrow(() -> new EntityNotFoundException("Objective Template", objectiveTemplateId));
    }

    public Page<ObjectiveTemplate> findByPrefix(String name, int page) {
        Pageable pageable = PageRequest.of(page, pageSize, SORT_ORDER);
        return objectiveTemplateRepository.findByNameStartsWithIgnoreCase(name, pageable);
    }

    public Optional<ObjectiveTemplate> findByName(String name) {
        return objectiveTemplateRepository.findByNameIgnoreCase(name);
    }

    public List<ObjectiveTemplate> findAll() {
        return objectiveTemplateRepository.findAll(SORT_ORDER);
    }

    public ObjectiveTemplate save(ObjectiveTemplate objectiveTemplate) {
        return objectiveTemplateRepository.save(objectiveTemplate);
    }

    public void delete(ObjectiveTemplate objectiveTemplate) {
        objectiveTemplateRepository.delete(objectiveTemplate);
    }
}
