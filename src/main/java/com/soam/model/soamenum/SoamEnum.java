package com.soam.model.soamenum;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@DiscriminatorColumn(name = "enum_id", discriminatorType = DiscriminatorType.STRING)
@Table(
        name = "enums",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"enum_id", "name"}),
                @UniqueConstraint(columnNames = {"enum_id", "sequence"})
        }
)
public abstract class SoamEnum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "enum_id", insertable = false, updatable = false)
    private String enumId;
    private String name;
    private Integer sequence;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEnumId() {
        return enumId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
}
