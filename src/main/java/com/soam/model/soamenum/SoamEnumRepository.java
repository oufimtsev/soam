package com.soam.model.soamenum;

import com.soam.model.priority.PriorityType;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface SoamEnumRepository extends CrudRepository<SoamEnum, Integer> {
    Optional<SoamEnum> findByEnumIdAndName(String enumId, String name);
    Optional<SoamEnum> findByEnumIdAndSequence(String enumId, int sequence);
    Collection<SoamEnum> findAll(Sort sort);
    Collection<SoamEnum> findByEnumId(String enumId, Sort sort);
    Collection<PriorityType> findAllByOrderBySequence();
}
