package com.soam.model.stakeholderobjective;

import com.soam.model.stakeholder.Stakeholder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StakeholderObjectiveRepository extends CrudRepository<StakeholderObjective, Integer> {
    Optional<StakeholderObjective> findByStakeholderAndSpecificationObjectiveId(
            Stakeholder stakeholder, int specificationObjectiveId);
}
