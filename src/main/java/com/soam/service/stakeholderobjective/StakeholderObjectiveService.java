package com.soam.service.stakeholderobjective;

import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.model.stakeholder.Stakeholder;
import com.soam.model.stakeholderobjective.StakeholderObjective;
import com.soam.model.stakeholderobjective.StakeholderObjectiveRepository;
import com.soam.service.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class StakeholderObjectiveService {
    private final StakeholderObjectiveRepository stakeholderObjectiveRepository;

    public StakeholderObjectiveService(StakeholderObjectiveRepository stakeholderObjectiveRepository) {
        this.stakeholderObjectiveRepository = stakeholderObjectiveRepository;
    }

    public StakeholderObjective getById(int stakeholderObjectiveId) {
        return stakeholderObjectiveRepository.findById(stakeholderObjectiveId)
                .orElseThrow(() -> new EntityNotFoundException("Stakeholder Objective", stakeholderObjectiveId));
    }

    public boolean existsForStakeholderAndSpecificationObjective(
            Stakeholder stakeholder, SpecificationObjective specificationObjective) {
        return stakeholderObjectiveRepository.existsByStakeholderAndSpecificationObjective(
                stakeholder, specificationObjective);
    }

    public StakeholderObjective save(StakeholderObjective stakeholderObjective) {
        return stakeholderObjectiveRepository.save(stakeholderObjective);
    }

    public void delete(StakeholderObjective stakeholderObjective) {
        stakeholderObjectiveRepository.delete(stakeholderObjective);
    }
}
