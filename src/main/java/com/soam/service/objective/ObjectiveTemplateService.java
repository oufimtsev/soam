package com.soam.service.objective;

import com.soam.model.objective.ObjectiveTemplate;
import com.soam.model.objective.ObjectiveTemplateRepository;
import com.soam.service.EntityNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ObjectiveTemplateService {
    private static final Sort SORT_ORDER = Sort.by(Sort.Order.by("name").ignoreCase());

    private final ObjectiveTemplateRepository objectiveTemplateRepository;

    public ObjectiveTemplateService(
            ObjectiveTemplateRepository objectiveTemplateRepository) {
        this.objectiveTemplateRepository = objectiveTemplateRepository;
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

    public ObjectiveTemplate save(ObjectiveTemplate objectiveTemplate) {
        return objectiveTemplateRepository.save(objectiveTemplate);
    }

    public void delete(ObjectiveTemplate objectiveTemplate) {
        objectiveTemplateRepository.delete(objectiveTemplate);
    }
}
