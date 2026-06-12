package com.recruitiq.model;

import java.util.List;

public class CandidateResult {
    private String filename;
    private int score;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private List<String> bonusSkills;
    private int aiScore;
    private String aiVerdict;
    private String tier;

    public String getFilename() { return filename; }
    public void setFilename(String v) { this.filename = v; }
    public int getScore() { return score; }
    public void setScore(int v) { this.score = v; }
    public List<String> getMatchedSkills() { return matchedSkills; }
    public void setMatchedSkills(List<String> v) { this.matchedSkills = v; }
    public List<String> getMissingSkills() { return missingSkills; }
    public void setMissingSkills(List<String> v) { this.missingSkills = v; }
    public List<String> getBonusSkills() { return bonusSkills; }
    public void setBonusSkills(List<String> v) { this.bonusSkills = v; }
    public int getAiScore() { return aiScore; }
    public void setAiScore(int v) { this.aiScore = v; }
    public String getAiVerdict() { return aiVerdict; }
    public void setAiVerdict(String v) { this.aiVerdict = v; }
    public String getTier() { return tier; }
    public void setTier(String v) { this.tier = v; }
}
