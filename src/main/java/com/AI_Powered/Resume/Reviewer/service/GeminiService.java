package com.AI_Powered.Resume.Reviewer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class GeminiService {

    @Value("${groq.api.key}")
    private String apiKey;

//    private static final String OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions";

    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
    public String analyze(String resumeText,String jobDescription) {

        RestTemplate restTemplate = new RestTemplate();

        String prompt = "You are a strict HR reviewer in 2026. " +
                "Carefully analyze this resume against the job description provided. " +
                "Be very strict and honest. Do not give high scores easily. " +
                "Most freshers score between 3 to 6. Only exceptional resumes score above 7. " +
                "Give feedback in EXACTLY this format: " +
                "STRONG POINTS: (list each point on new line starting with -) " +
                "WEAK POINTS: (list each point on new line starting with -) " +
                "MISSING SKILLS: (skills mentioned in JD but completely missing in resume, list starting with -) " +
                "JD MATCH SCORE: (give x/10 strictly based on how well resume matches this specific job. " +
                "Be harsh. Justify the score in one line.) " +
                "WHAT TO IMPROVE: (specific actionable steps to get this job, list starting with -) " +
                "Job Description: " + jobDescription + " " +
                "Resume: " + resumeText;

        // Clean all special characters properly
        String cleanPrompt = prompt
                .replace("\\", "\\\\")
                .replace("\"", "'")
                .replace("\r\n", " ")
                .replace("\r", " ")
                .replace("\n", " ")
                .replace("\t", " ")
                .replaceAll("[\\x00-\\x1F\\x7F]", "");

        String requestBody = "{" +
                "\"model\": \"llama-3.3-70b-versatile\"," +
                "\"messages\": [{" +
                "\"role\": \"user\"," +
                "\"content\": \"" + cleanPrompt + "\"" +
                "}]}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(GROQ_URL, entity, String.class);

//        return response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(response.getBody());
        String feedback = root.path("choices").get(0).path("message").path("content").asText();

        return feedback;
    }

    public String generateInterviewQuestions(String resumeText, String jobDescription) {

        RestTemplate restTemplate = new RestTemplate();

        String prompt = "You are an expert technical interviewer in 2026. " +
                "Based on the resume and job description provided, generate relevant interview questions. " +
                "Give questions in EXACTLY this format: " +
                "TECHNICAL QUESTIONS: (list 15  questions starting with -) " +
                "HR QUESTIONS: (list 10 behavioral questions starting with -) " +
                "CODING QUESTIONS: (list 10 expected coding/DSA questions starting with -) " +
                "Job Description: " + jobDescription + " " +
                "Resume: " + resumeText;

        String cleanPrompt = prompt
                .replace("\\", "\\\\")
                .replace("\"", "'")
                .replace("\r\n", " ")
                .replace("\r", " ")
                .replace("\n", " ")
                .replace("\t", " ")
                .replaceAll("[\\x00-\\x1F\\x7F]", "");

        String requestBody = "{" +
                "\"model\": \"llama-3.3-70b-versatile\"," +
                "\"messages\": [{" +
                "\"role\": \"user\"," +
                "\"content\": \"" + cleanPrompt + "\"" +
                "}]}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(GROQ_URL, entity, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(response.getBody());
        String questions = root.path("choices").get(0).path("message").path("content").asText();

        return questions;
    }

}