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
            Sort.Order.by("enumId"),
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

    public Optional<SoamEnum> findBySoamEnumIdAndName(Class<? extends SoamEnum> clazz, String name) {
        return soamEnumRepository.findByEnumIdAndName(clazz.getSimpleName(), name);
    }

    public Optional<SoamEnum> findBySoamEnumIdAndSequence(Class<? extends SoamEnum> clazz, int sequence) {
        return soamEnumRepository.findByEnumIdAndSequence(clazz.getSimpleName(), sequence);
    }

    public Collection<SoamEnum> findBySoamEnumId(Class<? extends SoamEnum> clazz) {
        return soamEnumRepository.findByEnumId(clazz.getSimpleName(), SORT_ORDER);
    }

    public Collection<SoamEnum> findAll() {
        return soamEnumRepository.findAll(SORT_ORDER);
    }

    public SoamEnum save(SoamEnum soamEnum) {
        return soamEnumRepository.save(soamEnum);
    }
}
