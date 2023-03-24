package com.soam.service.objective;

import com.soam.config.SoamProperties;
import com.soam.model.objective.ObjectiveTemplate;
import com.soam.model.objective.ObjectiveTemplateRepository;
import com.soam.service.EntityNotFoundException;
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
    private final SoamProperties soamProperties;

    public ObjectiveTemplateService(
            ObjectiveTemplateRepository objectiveTemplateRepository, SoamProperties soamProperties) {
        this.objectiveTemplateRepository = objectiveTemplateRepository;
        this.soamProperties = soamProperties;
    }

    public ObjectiveTemplate getById(int objectiveTemplateId) {
        return objectiveTemplateRepository.findById(objectiveTemplateId)
                .orElseThrow(() -> new EntityNotFoundException("Objective Template", objectiveTemplateId));
    }

    public List<ObjectiveTemplate> findByPrefix(String name) {
        return objectiveTemplateRepository.findByNameStartsWithIgnoreCase(name, SORT_ORDER);
    }

    public Optional<ObjectiveTemplate> findByName(String name) {
        return objectiveTemplateRepository.findByNameIgnoreCase(name);
    }

    public List<ObjectiveTemplate> findAll() {
        return objectiveTemplateRepository.findAll(SORT_ORDER);
    }

    public Page<ObjectiveTemplate> findAll(int page) {
        Pageable pageable = PageRequest.of(page, soamProperties.getPageSize(), SORT_ORDER);
        return objectiveTemplateRepository.findAll(pageable);
    }

    public ObjectiveTemplate save(ObjectiveTemplate objectiveTemplate) {
        return objectiveTemplateRepository.save(objectiveTemplate);
    }

    public void delete(ObjectiveTemplate objectiveTemplate) {
        objectiveTemplateRepository.delete(objectiveTemplate);
    }
}
