package com.soam.service.priority;

import com.soam.model.priority.PriorityType;
import com.soam.model.soamenum.SoamEnumRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class PriorityService {
    private final SoamEnumRepository soamEnumRepository;

    public PriorityService(SoamEnumRepository soamEnumRepository) {
        this.soamEnumRepository = soamEnumRepository;
    }

    public Collection<PriorityType> findAll() {
        return soamEnumRepository.findAllByOrderBySequence();
    }
}
