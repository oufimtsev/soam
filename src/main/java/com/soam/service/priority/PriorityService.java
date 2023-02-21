package com.soam.service.priority;

import com.soam.model.priority.PriorityRepository;
import com.soam.model.priority.PriorityType;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class PriorityService {
    private final PriorityRepository priorityRepository;

    public PriorityService(PriorityRepository priorityRepository) {
        this.priorityRepository = priorityRepository;
    }

    public Collection<PriorityType> findAll() {
        return priorityRepository.findAllByOrderBySequence();
    }
}
