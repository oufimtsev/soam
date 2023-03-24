package com.soam.model.specification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpecificationTemplateRepository extends CrudRepository<SpecificationTemplate, Integer> {
    List<SpecificationTemplate> findAll(Sort sort);
    Page<SpecificationTemplate> findAll(Pageable pageable);
    Optional<SpecificationTemplate> findByName(String name);
    Optional<SpecificationTemplate> findByNameIgnoreCase(String name);
    List<SpecificationTemplate> findByNameStartsWithIgnoreCase(String name, Sort sort);
}
