package com.AI_Powered.Resume.Reviewer.service;

import lombok.Data;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Data
public class ResumeService {

    @Autowired
    private GeminiService geminiService;

    public String extractTextFromPDF(MultipartFile file,String jobdescription) throws IOException{

        PDDocument document= Loader.loadPDF(file.getBytes());
        PDFTextStripper stripper = new PDFTextStripper();
        String text=stripper.getText(document);
        document.close();
        return  geminiService.analyze(text,jobdescription);
    }

    public String generateInterviewQuestions(MultipartFile file, String jobDescription) throws IOException {
        PDDocument document = Loader.loadPDF(file.getBytes());
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();
        return geminiService.generateInterviewQuestions(text, jobDescription);
    }
}
