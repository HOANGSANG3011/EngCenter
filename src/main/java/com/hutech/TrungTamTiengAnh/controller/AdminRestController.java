package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.dto.AIRestResponseDTO;
import com.hutech.TrungTamTiengAnh.dto.QuestionResponseDTO;
import com.hutech.TrungTamTiengAnh.service.GeminiAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/admin/api/ai")
public class AdminRestController {

    @Autowired
    private GeminiAIService geminiAIService;

    @PostMapping(value = "/generate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> generateQuestions(
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "image", required = false) String imageBase64,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        try {
            // 1. Nếu có file Word (.docx)
            if (file != null && !file.isEmpty()) {
                String extractedText = geminiAIService.extractTextFromWord(file);
                AIRestResponseDTO result = geminiAIService.analyzeText(extractedText);
                return ResponseEntity.ok(result);
            }

            // 2. Nếu có hình ảnh (Base64)
            if (imageBase64 != null && !imageBase64.isEmpty()) {
                AIRestResponseDTO result = geminiAIService.analyzeImage(imageBase64);
                return ResponseEntity.ok(result);
            }

            // 3. Nếu chỉ có văn bản thuần
            if (text != null && !text.isBlank()) {
                List<QuestionResponseDTO> questions = parseQuestionsFromText(text);
                AIRestResponseDTO result = new AIRestResponseDTO(questions, text);
                return ResponseEntity.ok(result);
            }

            return ResponseEntity.badRequest().body(Map.of("error", "Dữ liệu trống! Vui lòng nhập văn bản, chọn ảnh hoặc file Word."));

        } catch (HttpClientErrorException e) {
            System.err.println("[AI ERROR] HttpClientErrorException: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            Map<String, String> errorResponse = new HashMap<>();
            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                errorResponse.put("error", "Tình trạng quá tải hoặc hết hạn ngạch Google Gemini API.");
            } else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                errorResponse.put("error", "API Key Google Gemini không hợp lệ.");
            } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                errorResponse.put("error", "Không tìm thấy model Google Gemini. Vui lòng kiểm tra lại URL API.");
            } else {
                errorResponse.put("error", "Lỗi từ Google Gemini API: " + e.getResponseBodyAsString());
            }
            return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Đã xảy ra lỗi hệ thống: " + e.getMessage()));
        }
    }

    private List<QuestionResponseDTO> parseQuestionsFromText(String text) {
        List<QuestionResponseDTO> list = new ArrayList<>();
        String[] blocks = text.split("(?m)^\\s*(?:Câu\\s+)?\\d+[:.)]\\s*");
        
        for (String block : blocks) {
            if (block.trim().length() < 10) continue;

            QuestionResponseDTO dto = new QuestionResponseDTO();
            List<String> answers = new ArrayList<>();
            // Cải thiện Regex để bắt cả trường hợp xuống dòng và cùng một dòng
            Pattern optionPattern = Pattern.compile("([A-D])[:.)]\\s*(.*?)(?=\\s+[A-D][:.)]|\\s+Answer|\\s+Đáp án|\\s+Key|$)");
            Matcher matcher = optionPattern.matcher(block);
            
            String questionPart = block;
            int firstOptionIndex = -1;
            int correctIdx = -1;
            
            while (matcher.find()) {
                if (firstOptionIndex == -1) firstOptionIndex = matcher.start();
                String optionLabel = matcher.group(1);
                String optionText = matcher.group(2).trim();
                
                String fullOption = optionLabel + ". " + optionText;
                answers.add(fullOption);
                
                if (optionText.toLowerCase().contains("(correct)") || optionText.toLowerCase().contains("(đúng)")) {
                    correctIdx = answers.size() - 1;
                    answers.set(answers.size() - 1, (char)('A' + correctIdx) + ". " + optionText.replaceAll("(?i)\\s*\\(correct\\)|\\(đúng\\)", ""));
                }
            }
            
            if (firstOptionIndex != -1) {
                questionPart = block.substring(0, firstOptionIndex).trim();
            }

            if (correctIdx == -1) {
                // Hỗ trợ tìm Key/Answer cuối câu
                Pattern answerPattern = Pattern.compile("(?i)(?:Answer|Đáp án|Key)[:\\s]*([A-D])");
                Matcher ansMatcher = answerPattern.matcher(block);
                if (ansMatcher.find()) {
                    String label = ansMatcher.group(1).toUpperCase();
                    correctIdx = label.charAt(0) - 'A';
                }
            }

            dto.setQuestion(questionPart);
            dto.setAnswers(answers);
            dto.setCorrectIndex(correctIdx != -1 ? correctIdx : null);

            if (!answers.isEmpty() || !questionPart.isEmpty()) {
                list.add(dto);
            }
        }
        return list;
    }
}
