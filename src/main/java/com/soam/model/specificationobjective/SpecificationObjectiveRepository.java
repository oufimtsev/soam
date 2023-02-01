package com.soam.model.specificationobjective;

import com.soam.model.specification.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpecificationObjectiveRepository extends CrudRepository<SpecificationObjective, Integer> {
    Optional<SpecificationObjective> findBySpecificationAndNameIgnoreCase(Specification specification, String name);
}
