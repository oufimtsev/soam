package com.soam.service.stakeholder;

import com.soam.model.stakeholder.StakeholderTemplate;
import com.soam.model.stakeholder.StakeholderTemplateRepository;
import com.soam.service.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StakeholderTemplateService {
    private static final Sort SORT_ORDER = Sort.by(Sort.Order.by("name").ignoreCase());

    private final StakeholderTemplateRepository stakeholderTemplateRepository;

    @Value("${soam.pageSize}")
    private int pageSize;

    public StakeholderTemplateService(StakeholderTemplateRepository stakeholderTemplateRepository) {
        this.stakeholderTemplateRepository = stakeholderTemplateRepository;
    }

    public StakeholderTemplate getById(int stakeholderTemplateId) {
        return stakeholderTemplateRepository.findById(stakeholderTemplateId)
                .orElseThrow(() -> new EntityNotFoundException("Stakeholder Template", stakeholderTemplateId));
    }

    public Page<StakeholderTemplate> findByPrefix(String name, int page) {
        Pageable pageable = PageRequest.of(page, pageSize, SORT_ORDER);
        return stakeholderTemplateRepository.findByNameStartsWithIgnoreCase(name, pageable);
    }

    public Optional<StakeholderTemplate> findByName(String name) {
        return stakeholderTemplateRepository.findByNameIgnoreCase(name);
    }

    public List<StakeholderTemplate> findAll() {
        return stakeholderTemplateRepository.findAll(SORT_ORDER);
    }

    public StakeholderTemplate save(StakeholderTemplate stakeholderTemplate) {
        return stakeholderTemplateRepository.save(stakeholderTemplate);
    }

    public void delete(StakeholderTemplate stakeholderTemplate) {
        stakeholderTemplateRepository.delete(stakeholderTemplate);
    }
}
