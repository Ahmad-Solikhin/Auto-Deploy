package com.gayuh.auto_deploy.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "build_histories")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BuildHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "build_hiistories_generator")
    @SequenceGenerator(name = "build_hiistories_generator", sequenceName = "build_histories_id_seq", allocationSize = 1)
    private Long id;
    @Column(name = "execution_time")
    private Long executionTime;
    @Column(name = "execute_at")
    private Long executeAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
    @OneToOne(mappedBy = "buildHistory", fetch = FetchType.LAZY)
    private BuildHistoryLog buildHistoryLog;
}
