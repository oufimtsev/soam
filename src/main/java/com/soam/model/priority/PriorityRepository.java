package com.soam.model.priority;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface PriorityRepository extends CrudRepository<PriorityType, Integer> {
    Collection<PriorityType> findAllByOrderBySequence();
}
