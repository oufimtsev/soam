package com.soam.model.objective;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ObjectiveRepository extends CrudRepository<Objective, Integer> {
    Page<Objective> findByNameStartsWithIgnoreCase(String name, Pageable pageable);
    Optional<Objective> findByNameIgnoreCase(String name);
    Optional<Objective> findByName(String name );
}
