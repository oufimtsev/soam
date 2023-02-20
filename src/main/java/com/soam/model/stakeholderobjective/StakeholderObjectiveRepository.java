package com.soam.model.stakeholderobjective;

import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.model.stakeholder.Stakeholder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StakeholderObjectiveRepository extends CrudRepository<StakeholderObjective, Integer> {
    boolean existsByStakeholderAndSpecificationObjective(
            Stakeholder stakeholder, SpecificationObjective specificationObjective);
}
