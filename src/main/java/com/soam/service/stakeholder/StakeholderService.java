package com.soam.service.stakeholder;

import com.soam.model.specification.Specification;
import com.soam.model.stakeholder.Stakeholder;
import com.soam.model.stakeholder.StakeholderRepository;
import com.soam.service.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StakeholderService {
    private final StakeholderRepository stakeholderRepository;

    public StakeholderService(StakeholderRepository stakeholderRepository) {
        this.stakeholderRepository = stakeholderRepository;
    }

    public Stakeholder getById(int stakeholderId) {
        return stakeholderRepository.findById(stakeholderId)
                .orElseThrow(() -> new EntityNotFoundException("Stakeholder", stakeholderId));
    }

    public Optional<Stakeholder> findBySpecificationAndName(Specification specification, String name) {
        return stakeholderRepository.findBySpecificationAndNameIgnoreCase(specification, name);
    }

    public Stakeholder save(Stakeholder stakeholder) {
        return stakeholderRepository.save(stakeholder);
    }

    public void delete(Stakeholder stakeholder) {
        stakeholderRepository.delete(stakeholder);
    }
}
