package com.AI_Powered.Resume.Reviewer.controller;

import com.AI_Powered.Resume.Reviewer.entity.ResumeHistory;
import com.AI_Powered.Resume.Reviewer.repository.ResumeHistoryRepository;
import com.AI_Powered.Resume.Reviewer.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private ResumeHistoryRepository resumeHistoryRepository;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobDescription") String jobDescription,
            Authentication authentication) {

        System.out.println("File received: " + file.getOriginalFilename());

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a file");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.endsWith(".pdf")) {
            return ResponseEntity.badRequest().body("Only PDF files allowed");
        }

        try {
            String feedback = resumeService.extractTextFromPDF(file, jobDescription);

            // Extract score from feedback
            int score = extractScore(feedback);

            // Save to history
            if (authentication != null) {
                ResumeHistory history = new ResumeHistory();
                history.setUserEmail(authentication.getName());
                history.setResumeFileName(fileName);
                history.setScore(score);
                history.setFeedback(feedback);
                history.setAnalyzedAt(LocalDateTime.now());
                resumeHistoryRepository.save(history);
            }

            return ResponseEntity.ok(feedback);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Failed to process PDF");
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<ResumeHistory>> getHistory(Authentication authentication) {
        String email = authentication.getName();
        List<ResumeHistory> history = resumeHistoryRepository
                .findByUserEmailOrderByAnalyzedAtDesc(email);
        return ResponseEntity.ok(history);
    }

    private int extractScore(String feedback) {
        Pattern pattern = Pattern.compile("JD MATCH SCORE[:\\s]*(\\d+)");
        Matcher matcher = pattern.matcher(feedback);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }



    @PostMapping("/interview-prep")
    public ResponseEntity<String> interviewPrep(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobDescription") String jobDescription,
            Authentication authentication) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a file");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.endsWith(".pdf")) {
            return ResponseEntity.badRequest().body("Only PDF files allowed");
        }

        try {
            String questions = resumeService.generateInterviewQuestions(file, jobDescription);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Failed to generate interview questions");
        }
    }
}