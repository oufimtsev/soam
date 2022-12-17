package com.soam.model.objective;

import com.soam.model.SoamEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table( name="objective_templates", uniqueConstraints = {@UniqueConstraint(columnNames = { "name"})})
public class ObjectiveTemplate extends SoamEntity {

}
