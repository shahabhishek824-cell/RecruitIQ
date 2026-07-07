package com.recruitiq.service;

import com.recruitiq.model.CandidateResult;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResumeAnalyzerServiceTest {

    private final ResumeAnalyzerService service = new ResumeAnalyzerService();

    // Helper: builds a real in-memory PDF containing the given text,
    // so we test the actual PDFBox extraction path, not a mock.
    private MockMultipartFile buildPdf(String filename, String text) throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);

        PDPageContentStream cs = new PDPageContentStream(doc, page);
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA, 11);
        cs.newLineAtOffset(50, 700);

        String[] lines = text.split("\n");
        for (String line : lines) {
            cs.showText(line);
            cs.newLineAtOffset(0, -15);
        }
        cs.endText();
        cs.close();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        doc.save(out);
        doc.close();

        return new MockMultipartFile(
                "resumes", filename, "application/pdf", out.toByteArray());
    }

    @Test
    void allRequiredSkillsPresent_scoresHighAndTierStrong() throws IOException {
        MockMultipartFile pdf = buildPdf("candidate1.pdf",
                "Experienced engineer skilled in Java and React with Docker knowledge.");

        List<String> required = Arrays.asList("Java", "React", "Docker");
        CandidateResult r = service.analyze(pdf, required);

        assertEquals(3, r.getMatchedSkills().size());
        assertTrue(r.getMissingSkills().isEmpty());
        assertTrue(r.getScore() >= 75, "Expected Strong-tier score, got " + r.getScore());
        assertEquals("Strong", r.getTier());
    }

    @Test
    void noRequiredSkillsFound_scoresLowAndTierWeak() throws IOException {
        MockMultipartFile pdf = buildPdf("candidate2.pdf",
                "Marketing coordinator with experience in social media campaigns.");

        List<String> required = Arrays.asList("Java", "Kubernetes", "AWS");
        CandidateResult r = service.analyze(pdf, required);

        assertTrue(r.getMatchedSkills().isEmpty());
        assertEquals(3, r.getMissingSkills().size());
        assertEquals(0, r.getScore());
        assertEquals("Weak", r.getTier());
    }

    @Test
    void emptyRequiredSkillsList_usesBaseScoreOfFifty() throws IOException {
        MockMultipartFile pdf = buildPdf("candidate3.pdf",
                "General resume with no specific technical claims.");

        CandidateResult r = service.analyze(pdf, Arrays.asList());

        // base=50 when requiredSkills is empty, no bonus skills detected
        assertEquals(50, r.getScore());
        assertEquals("Moderate", r.getTier());
    }

    @Test
    void bonusSkillsAreDetectedSeparatelyFromRequired() throws IOException {
        MockMultipartFile pdf = buildPdf("candidate4.pdf",
                "Backend developer using Java and also familiar with MongoDB and Kafka.");

        List<String> required = Arrays.asList("Java");
        CandidateResult r = service.analyze(pdf, required);

        assertEquals(1, r.getMatchedSkills().size());
        assertTrue(r.getBonusSkills().contains("MongoDB"));
        assertTrue(r.getBonusSkills().contains("Kafka"));
        assertFalse(r.getBonusSkills().contains("Java"), "Required skill should not double as bonus");
    }

    @Test
    void partialMatch_correctlySplitsMatchedAndMissing() throws IOException {
        MockMultipartFile pdf = buildPdf("candidate5.pdf",
                "Frontend developer skilled in React and CSS.");

        List<String> required = Arrays.asList("React", "Angular", "Vue");
        CandidateResult r = service.analyze(pdf, required);

        assertEquals(1, r.getMatchedSkills().size());
        assertEquals(2, r.getMissingSkills().size());
        assertTrue(r.getMissingSkills().containsAll(Arrays.asList("Angular", "Vue")));
    }

    @Test
    void filenameIsPreservedInResult() throws IOException {
        MockMultipartFile pdf = buildPdf("my_special_resume.pdf", "Java developer.");
        CandidateResult r = service.analyze(pdf, Arrays.asList("Java"));

        assertEquals("my_special_resume.pdf", r.getFilename());
    }
}