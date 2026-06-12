package com.recruitiq.service;

import com.recruitiq.model.CandidateResult;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.regex.*;

@Service
public class ResumeAnalyzerService {

    public CandidateResult analyze(MultipartFile file, List<String> requiredSkills) throws IOException {
        String text = extractText(file);
        String low  = text.toLowerCase();

        // Skill Matching
        List<String> allSkills = SkillDatabase.getAllSkills();
        Set<String> found = new LinkedHashSet<String>();
        for (String sk : allSkills) {
            Pattern p = Pattern.compile("\\b" + Pattern.quote(sk.toLowerCase()) + "\\b");
            if (p.matcher(low).find()) {
                found.add(sk);
            }
        }

        Set<String> req = new LinkedHashSet<String>(requiredSkills);
        List<String> matched = new ArrayList<String>();
        List<String> missing = new ArrayList<String>();
        List<String> bonus   = new ArrayList<String>();

        for (String s : req) {
            if (found.contains(s)) matched.add(s);
            else missing.add(s);
        }
        for (String s : found) {
            if (!req.contains(s)) bonus.add(s);
        }

        // Scoring
        int base;
        if (requiredSkills.isEmpty()) {
            base = 50;
        } else {
            base = (int) Math.round(matched.size() * 80.0 / requiredSkills.size());
        }
        int extra = Math.min(bonus.size() * 2, 20);
        int score = Math.min(base + extra, 100);
        String tier = score >= 75 ? "Strong" : score >= 50 ? "Moderate" : "Weak";

        // AI Detection
        int    aiScore   = detectAI(text);
        String aiVerdict = aiScore >= 70 ? "Likely AI-Generated"
                         : aiScore >= 40 ? "Possibly AI-Assisted"
                         : "Likely Human-Written";

        CandidateResult r = new CandidateResult();
        r.setFilename(file.getOriginalFilename());
        r.setScore(score);
        r.setMatchedSkills(matched);
        r.setMissingSkills(missing);
        r.setBonusSkills(bonus);
        r.setAiScore(aiScore);
        r.setAiVerdict(aiVerdict);
        r.setTier(tier);
        return r;
    }

    private String extractText(MultipartFile file) throws IOException {
        PDDocument doc = PDDocument.load(file.getInputStream());
        try {
            return new PDFTextStripper().getText(doc);
        } finally {
            doc.close();
        }
    }

    private int detectAI(String text) {
        int score = 0;
        String low = text.toLowerCase();

        // Signal 1: Buzzword density
        String[] buzz = {
            "results-driven","dynamic","synergy","leverage","proactive","passionate",
            "innovative","detail-oriented","team player","fast-paced","thought leader",
            "motivated","enthusiastic","hardworking","go-getter","value-added"
        };
        int bc = 0;
        for (String b : buzz) {
            if (low.contains(b)) bc++;
        }
        score += Math.min(bc * 6, 40);

        // Signal 2: Sentence length uniformity
        String[] sents = text.split("[.!?]+");
        if (sents.length > 3) {
            double sum = 0;
            for (String s : sents) sum += s.length();
            double mean = sum / sents.length;
            double varSum = 0;
            for (String s : sents) varSum += Math.pow(s.length() - mean, 2);
            double sd = Math.sqrt(varSum / sents.length);
            if (sd < 15) score += 20;
        }

        // Signal 3: Unique word ratio
        String[] words = low.split("\\W+");
        if (words.length > 0) {
            Set<String> unique = new HashSet<String>(Arrays.asList(words));
            double ratio = (double) unique.size() / words.length;
            if (ratio < 0.45) score += 25;
            else if (ratio < 0.55) score += 10;
        }

        // Signal 4: Passive voice
        Matcher m = Pattern.compile(
            "\\b(is|are|was|were|be|been|being) (\\w+ed)\\b",
            Pattern.CASE_INSENSITIVE).matcher(text);
        int pc = 0;
        while (m.find()) pc++;
        score += Math.min(pc * 3, 15);

        return Math.min(score, 100);
    }
}
