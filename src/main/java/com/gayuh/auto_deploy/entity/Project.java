package com.gayuh.auto_deploy.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "projects")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Project {
    @Id
    private String id;
    private String name;
    private String path;
    private String language;
    private String description;
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<BuildHistory> buildHistories;
}
