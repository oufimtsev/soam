package com.soam.model.specification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpecificationRepository extends CrudRepository<Specification, Integer> {
    List<Specification> findAll(Sort sort);
    Page<Specification> findByNameStartsWithIgnoreCase(String name, Pageable pageable);
    Optional<Specification> findByNameIgnoreCase(String name);
}
