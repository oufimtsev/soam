package com.soam.model.specificationobjective;

import com.soam.model.specification.Specification;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SpecificationObjectiveRepository extends CrudRepository<SpecificationObjective, Integer> {
    Optional<SpecificationObjective> findBySpecificationAndNameIgnoreCase(Specification specification, String name);
}
