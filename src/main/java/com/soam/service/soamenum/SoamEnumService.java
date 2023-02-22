package com.soam.service.soamenum;

import com.soam.model.soamenum.SoamEnum;
import com.soam.model.soamenum.SoamEnumRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

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

    public Collection<SoamEnum> findBySoamEnumId(Class<? extends SoamEnum> clazz) {
        return soamEnumRepository.findByEnumId(clazz.getSimpleName(), SORT_ORDER);
    }

    public Collection<SoamEnum> findAll() {
        return soamEnumRepository.findAll(SORT_ORDER);
    }
}
