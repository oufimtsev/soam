package com.soam.model.specification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SpecificationTemplateRepository extends CrudRepository<SpecificationTemplate, Integer> {

    List<SpecificationTemplate> findAll();
    Page<SpecificationTemplate> findAll(Pageable pageable);
    Optional<SpecificationTemplate> findByName(String name);
    Optional<SpecificationTemplate> findByNameIgnoreCase(String name);
    Page<SpecificationTemplate> findByNameStartsWithIgnoreCase(String name, Pageable pageable);
}
