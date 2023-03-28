package com.soam.model.stakeholder;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StakeholderTemplateRepository extends CrudRepository<StakeholderTemplate, Integer> {
    List<StakeholderTemplate> findAll(Sort sort);
    Optional<StakeholderTemplate> findByName(String name);
    Optional<StakeholderTemplate> findByNameIgnoreCase(String name);
    List<StakeholderTemplate> findByNameStartsWithIgnoreCase(String name, Sort sort);
}
