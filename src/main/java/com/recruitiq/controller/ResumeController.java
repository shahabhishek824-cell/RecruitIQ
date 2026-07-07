package com.recruitiq.controller;

import com.recruitiq.model.CandidateResult;
import com.recruitiq.service.ResumeAnalyzerService;
import com.recruitiq.service.SkillDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Controller
public class ResumeController {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String ALLOWED_TYPE = "application/pdf";

    @Autowired
    private ResumeAnalyzerService analyzerService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("categories", SkillDatabase.CATEGORIES);
        return "index";
    }

    private String validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            return "File is empty: " + file.getOriginalFilename();
        }
        if (!ALLOWED_TYPE.equals(file.getContentType())) {
            return "Unsupported file type (PDF only): " + file.getOriginalFilename();
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            return "File exceeds 5MB limit: " + file.getOriginalFilename();
        }
        return null;
    }

    @PostMapping("/screen")
    public String screen(
            @RequestParam("resumes") List<MultipartFile> files,
            @RequestParam(value = "skills", required = false) List<String> skills,
            Model model) {

        List<String> req = (skills != null) ? skills : new ArrayList<String>();
        List<CandidateResult> results = new ArrayList<CandidateResult>();
        List<String> errors = new ArrayList<String>();

        for (MultipartFile f : files) {
            String validationError = validateFile(f);
            if (validationError != null) {
                errors.add(validationError);
                continue;
            }
            try {
                results.add(analyzerService.analyze(f, req));
            } catch (Exception e) {
                errors.add("Failed to process: " + f.getOriginalFilename());
                System.err.println("Error processing: " + f.getOriginalFilename());
            }
        }

        Collections.sort(results, new Comparator<CandidateResult>() {
            public int compare(CandidateResult a, CandidateResult b) {
                return Integer.compare(b.getScore(), a.getScore());
            }
        });

        model.addAttribute("results", results);
        model.addAttribute("errors", errors);
        model.addAttribute("requiredSkills", req);
        model.addAttribute("categories", SkillDatabase.CATEGORIES);
        return "results";
    }
}