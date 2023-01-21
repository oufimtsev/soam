package com.soam.model.objective;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ObjectiveTemplateRepository extends CrudRepository<ObjectiveTemplate, Integer> {

    List<ObjectiveTemplate> findAll();
    Page<ObjectiveTemplate> findAll(Pageable pageable);
    Optional<ObjectiveTemplate> findByName(String name );
    Optional<ObjectiveTemplate> findByNameIgnoreCase(String name);
    Page<ObjectiveTemplate> findByNameStartsWithIgnoreCase(String name, Pageable pageable);
}
