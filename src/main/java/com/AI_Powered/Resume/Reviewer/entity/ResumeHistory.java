package com.AI_Powered.Resume.Reviewer.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "resume_history")
public class ResumeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;
    private String resumeFileName;
    private int score;

    @Column(length = 5000)
    private String feedback;

    private LocalDateTime analyzedAt;
}
