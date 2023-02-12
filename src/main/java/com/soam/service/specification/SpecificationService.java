package com.soam.service.specification;

import com.soam.model.objective.ObjectiveTemplate;
import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationRepository;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.model.specificationobjective.SpecificationObjectiveRepository;
import com.soam.model.stakeholder.Stakeholder;
import com.soam.model.stakeholder.StakeholderRepository;
import com.soam.model.stakeholder.StakeholderTemplate;
import com.soam.model.stakeholderobjective.StakeholderObjective;
import com.soam.model.stakeholderobjective.StakeholderObjectiveComparator;
import com.soam.model.stakeholderobjective.StakeholderObjectiveRepository;
import com.soam.model.templatelink.TemplateLink;
import com.soam.service.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

@Service
public class SpecificationService {
    private static final Sort SORT_ORDER = Sort.by(Sort.Order.by("name").ignoreCase());

    private final SpecificationRepository specificationRepository;
    private final SpecificationObjectiveRepository specificationObjectiveRepository;
    private final StakeholderRepository stakeholderRepository;
    private final StakeholderObjectiveRepository stakeholderObjectiveRepository;

    @Value("${soam.pageSize}")
    private int pageSize;

    public SpecificationService(
            SpecificationRepository specificationRepository,
            SpecificationObjectiveRepository specificationObjectiveRepository,
            StakeholderRepository stakeholderRepository, StakeholderObjectiveRepository stakeholderObjectiveRepository) {
        this.specificationRepository = specificationRepository;
        this.specificationObjectiveRepository = specificationObjectiveRepository;
        this.stakeholderRepository = stakeholderRepository;
        this.stakeholderObjectiveRepository = stakeholderObjectiveRepository;
    }

    public Specification getById(int specificationId) {
        return specificationRepository.findById(specificationId)
                .orElseThrow(() -> new EntityNotFoundException("Specification", specificationId));
    }

    public Page<Specification> findByPrefix(String name, int page) {
        Pageable pageable = PageRequest.of(page, pageSize, SORT_ORDER);
        return specificationRepository.findByNameStartsWithIgnoreCase(name, pageable);
    }

    public Optional<Specification> findByName(String name) {
        return specificationRepository.findByNameIgnoreCase(name);
    }

    public List<Specification> findAll() {
        return specificationRepository.findAll(SORT_ORDER);
    }

    public Specification save(Specification specification) {
        return specificationRepository.save(specification);
    }

    public Specification saveDeepCopy(Specification srcSpecification, Specification dstSpecification) {
        specificationRepository.save(dstSpecification);

        dstSpecification.setStakeholders(new ArrayList<>());
        dstSpecification.setSpecificationObjectives(new ArrayList<>());

        //in the loop below we sacrifice the performance somewhat in favour of predictable memory consumption.
        srcSpecification.getSpecificationObjectives().forEach(srcSpecificationObjective -> {
            SpecificationObjective dstSpecificationObjective = new SpecificationObjective();
            dstSpecificationObjective.setName(srcSpecificationObjective.getName());
            dstSpecificationObjective.setDescription(srcSpecificationObjective.getDescription());
            dstSpecificationObjective.setNotes(srcSpecificationObjective.getNotes());
            dstSpecificationObjective.setPriority(srcSpecificationObjective.getPriority());
            dstSpecificationObjective.setSpecification(dstSpecification);
            dstSpecification.getSpecificationObjectives().add(dstSpecificationObjective);
            specificationObjectiveRepository.save(dstSpecificationObjective);
        });
        srcSpecification.getStakeholders().forEach(srcStakeholder -> {
            Stakeholder dstStakeholder = new Stakeholder();
            dstStakeholder.setName(srcStakeholder.getName());
            dstStakeholder.setDescription(srcStakeholder.getDescription());
            dstStakeholder.setNotes(srcStakeholder.getNotes());
            dstStakeholder.setPriority(srcStakeholder.getPriority());
            dstStakeholder.setSpecification(dstSpecification);
            dstStakeholder.setStakeholderObjectives(new TreeSet<>(new StakeholderObjectiveComparator()));
            dstSpecification.getStakeholders().add(dstStakeholder);
            stakeholderRepository.save(dstStakeholder);

            srcStakeholder.getStakeholderObjectives().forEach(srcStakeholderObjective ->
                    specificationObjectiveRepository.findBySpecificationAndNameIgnoreCase(dstSpecification, srcStakeholderObjective.getSpecificationObjective().getName())
                            .ifPresent(so -> {
                                StakeholderObjective dstStakeholderObjective = new StakeholderObjective();
                                dstStakeholderObjective.setStakeholder(dstStakeholder);
                                dstStakeholderObjective.setSpecificationObjective(so);
                                dstStakeholderObjective.setNotes(srcStakeholderObjective.getNotes());
                                dstStakeholderObjective.setPriority(srcStakeholderObjective.getPriority());
                                dstStakeholder.getStakeholderObjectives().add(dstStakeholderObjective);
                                stakeholderObjectiveRepository.save(dstStakeholderObjective);
                            }));
        });

        return dstSpecification;
    }

    public Specification saveFromTemplate(SpecificationTemplate srcSpecificationTemplate, Specification dstSpecification) {
        specificationRepository.save(dstSpecification);

        dstSpecification.setStakeholders(new ArrayList<>());
        dstSpecification.setSpecificationObjectives(new ArrayList<>());

        Collection<TemplateLink> templateLinks = srcSpecificationTemplate.getTemplateLinks();

        //in the loop below we sacrifice the performance somewhat in favour of predictable memory consumption.
        templateLinks.forEach(templateLink -> {
            Stakeholder stakeholder = stakeholderRepository.findBySpecificationAndNameIgnoreCase(
                    dstSpecification,
                    templateLink.getStakeholderTemplate().getName()
            ).orElseGet(() -> {
                StakeholderTemplate stakeholderTemplate = templateLink.getStakeholderTemplate();
                Stakeholder newStakeholder = new Stakeholder();
                newStakeholder.setName(stakeholderTemplate.getName());
                newStakeholder.setDescription(stakeholderTemplate.getDescription());
                newStakeholder.setNotes(stakeholderTemplate.getNotes());
                newStakeholder.setPriority(stakeholderTemplate.getPriority());
                newStakeholder.setSpecification(dstSpecification);
                newStakeholder.setStakeholderObjectives(new TreeSet<>(new StakeholderObjectiveComparator()));
                stakeholderRepository.save(newStakeholder);
                dstSpecification.getStakeholders().add(newStakeholder);
                return newStakeholder;
            });

            SpecificationObjective specificationObjective = specificationObjectiveRepository.findBySpecificationAndNameIgnoreCase(
                    dstSpecification,
                    templateLink.getObjectiveTemplate().getName()
            ).orElseGet(() -> {
                ObjectiveTemplate objectiveTemplate = templateLink.getObjectiveTemplate();
                SpecificationObjective newSpecificationObjective = new SpecificationObjective();
                newSpecificationObjective.setName(objectiveTemplate.getName());
                newSpecificationObjective.setDescription(objectiveTemplate.getDescription());
                newSpecificationObjective.setNotes(objectiveTemplate.getNotes());
                newSpecificationObjective.setPriority(objectiveTemplate.getPriority());
                newSpecificationObjective.setSpecification(dstSpecification);
                specificationObjectiveRepository.save(newSpecificationObjective);
                dstSpecification.getSpecificationObjectives().add(newSpecificationObjective);
                return newSpecificationObjective;
            });

            StakeholderObjective stakeholderObjective = new StakeholderObjective();
            stakeholderObjective.setStakeholder(stakeholder);
            stakeholderObjective.setSpecificationObjective(specificationObjective);
            stakeholderObjective.setNotes(specificationObjective.getNotes());
            stakeholderObjective.setPriority(specificationObjective.getPriority());
            stakeholder.getStakeholderObjectives().add(stakeholderObjective);
            stakeholderObjectiveRepository.save(stakeholderObjective);
        });

        return dstSpecification;
    }

    public void delete(Specification specification) {
        specificationRepository.delete(specification);
    }
}
