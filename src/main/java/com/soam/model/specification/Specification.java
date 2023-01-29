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
package com.soam.model.specification;

import com.soam.model.SoamEntity;
import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.model.stakeholder.Stakeholder;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import org.springframework.core.style.ToStringCreator;

import java.util.List;

/**
 * Simple JavaBean domain object representing a specification.
 */
@Entity
@Table(name = "specifications")
public class Specification extends SoamEntity {
	@OneToMany(mappedBy = "specification")
	@OrderBy("LOWER(name)")
	private List<Stakeholder> stakeholders;

	@OneToMany(mappedBy = "specification")
	@OrderBy("LOWER(name)")
	private List<SpecificationObjective> specificationObjectives;

	public List<Stakeholder> getStakeholders() {
		return stakeholders;
	}

	public void setStakeholders(List<Stakeholder> stakeholders) {
		this.stakeholders = stakeholders;
	}

	public List<SpecificationObjective> getSpecificationObjectives() {
		return specificationObjectives;
	}

	public void setSpecificationObjectives(List<SpecificationObjective> specificationObjectives) {
		this.specificationObjectives = specificationObjectives;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("id", getId()).append("new", isNew())
				.append("name", getName())
				.append("description", getDescription()).append("notes", getNotes())
				.toString();
	}
}
