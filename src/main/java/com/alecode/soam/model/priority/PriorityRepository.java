package com.alecode.soam.model.priority;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface PriorityRepository extends CrudRepository<PriorityType, Integer> {

    @Cacheable("priorityTypes")
    Collection<PriorityType> findAll();
}
