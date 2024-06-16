package com.gayuh.auto_deploy.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "build_history_logs")
public class BuildHistoryLog {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "build_hiistory_logs_generator")
    @SequenceGenerator(name = "build_hiistory_logs_generator", sequenceName = "build_history_logs_id_seq", allocationSize = 1)
    private Long id;
    @Column(name = "line_number")
    private Integer lineNumber;
    private String line;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "build_history_id")
    private BuildHistory buildHistory;
}
