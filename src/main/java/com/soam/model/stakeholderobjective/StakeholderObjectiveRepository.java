package com.soam.model.stakeholderobjective;

import com.soam.model.stakeholder.Stakeholder;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StakeholderObjectiveRepository extends CrudRepository<StakeholderObjective, Integer> {
    Optional<StakeholderObjective> findByStakeholderAndSpecificationObjectiveId(Stakeholder stakeholder, int specificationObjectiveId);
}
