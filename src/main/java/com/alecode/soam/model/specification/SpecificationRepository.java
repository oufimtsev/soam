package com.alecode.soam.model.specification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SpecificationRepository extends CrudRepository<Specification, Integer> {

    Page<Specification> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Optional<Specification> findByName(String name );
}
