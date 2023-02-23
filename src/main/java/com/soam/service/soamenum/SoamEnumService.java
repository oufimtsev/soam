package com.soam.service.soamenum;

import com.soam.model.soamenum.SoamEnum;
import com.soam.model.soamenum.SoamEnumRepository;
import com.soam.service.EntityNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class SoamEnumService {
    private static final Sort SORT_ORDER = Sort.by(List.of(
            Sort.Order.by("type"),
            Sort.Order.by("sequence")
    ));

    private final SoamEnumRepository soamEnumRepository;

    public SoamEnumService(SoamEnumRepository soamEnumRepository) {
        this.soamEnumRepository = soamEnumRepository;
    }

    public SoamEnum getById(int soamEnumId) {
        return soamEnumRepository.findById(soamEnumId)
                .orElseThrow(() -> new EntityNotFoundException("Soam Enum", soamEnumId));
    }

    public Optional<SoamEnum> findByTypeAndName(Class<? extends SoamEnum> clazz, String name) {
        return soamEnumRepository.findByTypeAndName(clazz.getSimpleName(), name);
    }

    public Optional<SoamEnum> findByTypeAndSequence(Class<? extends SoamEnum> clazz, int sequence) {
        return soamEnumRepository.findByTypeAndSequence(clazz.getSimpleName(), sequence);
    }

    public Collection<SoamEnum> findByType(Class<? extends SoamEnum> clazz) {
        return soamEnumRepository.findByType(clazz.getSimpleName(), SORT_ORDER);
    }

    public Collection<SoamEnum> findAll() {
        return soamEnumRepository.findAll(SORT_ORDER);
    }

    public SoamEnum save(SoamEnum soamEnum) {
        return soamEnumRepository.save(soamEnum);
    }
}
