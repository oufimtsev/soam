package com.soam.service.specificationobjective;

import com.soam.model.specification.Specification;
import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.model.specificationobjective.SpecificationObjectiveRepository;
import com.soam.service.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SpecificationObjectiveService {
    private final SpecificationObjectiveRepository specificationObjectiveRepository;

    public SpecificationObjectiveService(SpecificationObjectiveRepository specificationObjectiveRepository) {
        this.specificationObjectiveRepository = specificationObjectiveRepository;
    }

    public SpecificationObjective getById(int specificationObjectiveId) {
        return specificationObjectiveRepository.findById(specificationObjectiveId)
                .orElseThrow(() -> new EntityNotFoundException("Specification Objective", specificationObjectiveId));
    }

    public Optional<SpecificationObjective> findBySpecificationAndName(Specification specification, String name) {
        return specificationObjectiveRepository.findBySpecificationAndNameIgnoreCase(specification, name);
    }

    public SpecificationObjective save(SpecificationObjective specificationObjective) {
        return specificationObjectiveRepository.save(specificationObjective);
    }

    public void delete(SpecificationObjective specificationObjective) {
        specificationObjectiveRepository.delete(specificationObjective);
    }
}
