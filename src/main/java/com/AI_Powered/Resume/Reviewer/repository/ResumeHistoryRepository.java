package com.AI_Powered.Resume.Reviewer.repository;

import com.AI_Powered.Resume.Reviewer.entity.ResumeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResumeHistoryRepository extends JpaRepository<ResumeHistory, Long> {
    List<ResumeHistory> findByUserEmailOrderByAnalyzedAtDesc(String userEmail);
}