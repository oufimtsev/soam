/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.soam.model.stakeholder;

import com.soam.model.SoamEntity;
import com.soam.model.specification.Specification;
import com.soam.model.stakeholderobjective.StakeholderObjective;
import com.soam.model.stakeholderobjective.StakeholderObjectiveComparator;
import jakarta.persistence.*;
import org.hibernate.annotations.SortComparator;
import org.springframework.core.style.ToStringCreator;

import java.util.SortedSet;

/**
 * Simple JavaBean domain object representing a Stakeholder.
 */
@Entity
@Table(name = "stakeholders")
public class Stakeholder extends SoamEntity {
	@ManyToOne
	@JoinColumn(name = "specification_id")
	private Specification specification;

	@OneToMany(mappedBy = "stakeholder")
	@SortComparator(StakeholderObjectiveComparator.class)
	private SortedSet<StakeholderObjective> stakeholderObjectives;

	public SortedSet<StakeholderObjective> getStakeholderObjectives() {
		return stakeholderObjectives;
	}

	public void setStakeholderObjectives(SortedSet<StakeholderObjective> stakeholderObjectives) {
		this.stakeholderObjectives = stakeholderObjectives;
	}

	public Specification getSpecification() {
		return specification;
	}

	public void setSpecification(Specification specification) {
		this.specification = specification;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("id", getId()).append("new", isNew())
				.append("name", getName())
				.append("description", getDescription()).append("notes", getNotes())
				.toString();
	}
}
