package com.soam.model.specification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SpecificationTemplateRepository extends CrudRepository<SpecificationTemplate, Integer> {

    Page<SpecificationTemplate> findAll(Pageable pageable);
    Optional<SpecificationTemplate> findByName(String name );
    Page<SpecificationTemplate> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
