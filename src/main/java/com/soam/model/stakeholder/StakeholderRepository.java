package com.soam.model.stakeholder;

import com.soam.model.specification.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StakeholderRepository extends CrudRepository<Stakeholder, Integer> {
    Page<Stakeholder> findByNameStartsWithIgnoreCase(String name, Pageable pageable);
    Optional<Stakeholder> findBySpecificationAndNameIgnoreCase(Specification specification, String name);
    Optional<Stakeholder> findByNameIgnoreCase(String name);
    Optional<Stakeholder> findByName(String name);
}
