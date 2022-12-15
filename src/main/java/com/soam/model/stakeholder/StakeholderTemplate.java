package com.soam.model.stakeholder;

import com.soam.model.SoamEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table( name="stakeholder_templates", uniqueConstraints = {@UniqueConstraint(columnNames = { "name"})})
public class StakeholderTemplate extends SoamEntity {

}
