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
import com.soam.model.objective.Objective;
import com.soam.model.specification.Specification;
import jakarta.persistence.*;
import org.springframework.core.style.ToStringCreator;

import java.util.List;

/**
 * Simple JavaBean domain object representing a Stakeholder.
 */
@Entity
@Table(name = "stakeholders")
public class Stakeholder extends SoamEntity {
	@ManyToOne
	@JoinColumn(name = "specification_id")
	private Specification specification;

	@OneToMany( mappedBy = "stakeholder" )
	@OrderBy("name")
	private List<Objective> objectives;

	public List<Objective> getObjectives() {
		return this.objectives;
	}

	public void setObjectives(List<Objective> objectives) {
		this.objectives = objectives;
	}

	public Specification getSpecification() {
		return specification;
	}

	public void setSpecification(Specification specification) {
		this.specification = specification;
	}


	@Override
	public String toString() {
		return new ToStringCreator(this).append("id", this.getId()).append("new", this.isNew())
				.append("name", this.getName())
				.append("description", this.getDescription()).append("notes", this.getNotes())
				.toString();
	}
}
