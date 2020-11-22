package com.bbte.styoudent.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "course")
@Data
@EqualsAndHashCode(callSuper = true)
public class Course extends BaseEntity{

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;
}
