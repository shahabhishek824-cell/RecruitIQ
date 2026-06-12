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

    @Autowired
    private ResumeAnalyzerService analyzerService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("categories", SkillDatabase.CATEGORIES);
        return "index";
    }

    @PostMapping("/screen")
    public String screen(
            @RequestParam("resumes") List<MultipartFile> files,
            @RequestParam(value = "skills", required = false) List<String> skills,
            Model model) {

        List<String> req = (skills != null) ? skills : new ArrayList<String>();
        List<CandidateResult> results = new ArrayList<CandidateResult>();

        for (MultipartFile f : files) {
            if (!f.isEmpty()) {
                try {
                    results.add(analyzerService.analyze(f, req));
                } catch (Exception e) {
                    System.err.println("Error processing: " + f.getOriginalFilename());
                }
            }
        }

        Collections.sort(results, new Comparator<CandidateResult>() {
            public int compare(CandidateResult a, CandidateResult b) {
                return Integer.compare(b.getScore(), a.getScore());
            }
        });

        model.addAttribute("results", results);
        model.addAttribute("requiredSkills", req);
        model.addAttribute("categories", SkillDatabase.CATEGORIES);
        return "results";
    }
}
