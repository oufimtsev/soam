package com.soam.model.specification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SpecificationRepository extends CrudRepository<Specification, Integer> {
    Page<Specification> findByNameStartsWithIgnoreCase(String name, Pageable pageable);
    Optional<Specification> findByNameIgnoreCase(String name);
    Optional<Specification> findByName(String name );
}
