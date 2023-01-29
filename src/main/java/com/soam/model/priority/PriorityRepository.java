package com.soam.model.priority;

import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface PriorityRepository extends CrudRepository<PriorityType, Integer> {
    Collection<PriorityType> findAll();
}
