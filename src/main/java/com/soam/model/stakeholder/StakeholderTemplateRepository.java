package com.soam.model.stakeholder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StakeholderTemplateRepository extends CrudRepository<StakeholderTemplate, Integer> {

    Page<StakeholderTemplate> findAll(Pageable pageable);
    Optional<StakeholderTemplate> findByName(String name );
    Optional<StakeholderTemplate> findByNameIgnoreCase(String name);
    Page<StakeholderTemplate> findByNameStartsWithIgnoreCase(String name, Pageable pageable);
}
