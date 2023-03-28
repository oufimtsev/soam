package com.soam.model.objective;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ObjectiveTemplateRepository extends CrudRepository<ObjectiveTemplate, Integer> {
    List<ObjectiveTemplate> findAll(Sort sort);
    Optional<ObjectiveTemplate> findByName(String name);
    Optional<ObjectiveTemplate> findByNameIgnoreCase(String name);
    List<ObjectiveTemplate> findByNameStartsWithIgnoreCase(String name, Sort sort);
}
