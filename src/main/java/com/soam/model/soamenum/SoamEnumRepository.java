package com.soam.model.soamenum;

import com.soam.model.priority.PriorityType;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface SoamEnumRepository extends CrudRepository<SoamEnum, Integer> {
    Collection<SoamEnum> findAll(Sort sort);
    Collection<SoamEnum> findByEnumId(String enumId, Sort sort);
    Collection<PriorityType> findAllByOrderBySequence();
}
