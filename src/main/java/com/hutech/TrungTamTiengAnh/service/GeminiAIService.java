package com.hutech.TrungTamTiengAnh.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hutech.TrungTamTiengAnh.dto.AIRestResponseDTO;
import com.hutech.TrungTamTiengAnh.dto.QuestionResponseDTO;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Service
public class GeminiAIService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private static final String DEFAULT_PROMPT_IMAGE = "Bạn là chuyên gia phân tích đề thi tiếng Anh. Hãy đọc hình ảnh đề thi và trích xuất TẤT CẢ câu hỏi trắc nghiệm.\n\n" +
            "Yêu cầu:\n" +
            "- Mỗi câu hỏi có ĐÚNG 4 lựa chọn A, B, C, D.\n" +
            "- Xác định đáp án đúng dựa vào dấu hiệu trong đề (in đậm, khoanh tròn, hoặc key). Nếu không thấy, đặt correctIndex = 0.\n" +
            "- Trả về kết quả dưới dạng JSON.\n\n" +
            "JSON Structure:\n" +
            "{\"questions\": [{\"question\": \"...\", \"answers\": [\"A. ...\", \"B. ...\", \"C. ...\", \"D. ...\"], \"correctIndex\": 0}], \"ocrText\": \"toàn bộ nội dung văn bản trích xuất được\"}";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String extractTextFromWord(MultipartFile file) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (InputStream is = file.getInputStream(); XWPFDocument doc = new XWPFDocument(is)) {
            for (XWPFParagraph para : doc.getParagraphs()) {
                sb.append(para.getText()).append("\n");
            }
        }
        return sb.toString();
    }

    public AIRestResponseDTO analyzeText(String fullText) throws HttpClientErrorException {
        return callGemini(fullText, null);
    }

    public AIRestResponseDTO analyzeImage(String base64Image) throws HttpClientErrorException {
        return callGemini(DEFAULT_PROMPT_IMAGE, base64Image);
    }

    private AIRestResponseDTO callGemini(String promptText, String base64Image) throws HttpClientErrorException {
        try {
            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", promptText);

            List<Map<String, Object>> parts = new ArrayList<>();
            parts.add(textPart);

            if (base64Image != null && !base64Image.isEmpty()) {
                // Detect MIME type từ Base64 header (PNG, JPEG, GIF, WebP)
                String mimeType = "image/jpeg";
                if (base64Image.startsWith("/9j/")) mimeType = "image/jpeg";
                else if (base64Image.startsWith("iVBORw")) mimeType = "image/png";
                else if (base64Image.startsWith("R0lGOD")) mimeType = "image/gif";
                else if (base64Image.startsWith("UklGR")) mimeType = "image/webp";
                
                Map<String, Object> imagePart = new HashMap<>();
                Map<String, String> inlineData = new HashMap<>();
                inlineData.put("mime_type", mimeType);
                inlineData.put("data", base64Image);
                imagePart.put("inline_data", inlineData);
                // ✅ Image TRƯỚC text để Gemini Vision xử lý tốt hơn
                parts.add(0, imagePart);
            }

            Map<String, Object> contentPart = new HashMap<>();
            contentPart.put("parts", parts);

            Map<String, Object> payload = new HashMap<>();
            payload.put("contents", Collections.singletonList(contentPart));

            // ✅ JSON Mode Configuration
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("response_mime_type", "application/json");
            payload.put("generationConfig", generationConfig);

            // ✅ Safety Settings (Disable blocking for educational content)
            List<Map<String, String>> safetySettings = new ArrayList<>();
            String[] categories = {"HARM_CATEGORY_HARASSMENT", "HARM_CATEGORY_HATE_SPEECH", "HARM_CATEGORY_SEXUALLY_EXPLICIT", "HARM_CATEGORY_DANGEROUS_CONTENT"};
            for (String cat : categories) {
                Map<String, String> s = new HashMap<>();
                s.put("category", cat);
                s.put("threshold", "BLOCK_NONE");
                safetySettings.add(s);
            }
            payload.put("safetySettings", safetySettings);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            // Thử URL chính (từ config) với cơ chế Retry cho lỗi 429
            String fullUrl = apiUrl + apiKey;
            int maxRetries = 2; // Số lần thử lại tối đa
            ResponseEntity<String> response = null;
            
            for (int attempt = 0; attempt <= maxRetries; attempt++) {
                try {
                    System.out.println("[GEMINI AI] Attempt " + (attempt + 1) + " sending request to: " + apiUrl + "***");
                    response = restTemplate.postForEntity(fullUrl, entity, String.class);
                    break; // Thành công thì thoát vòng lặp
                } catch (HttpClientErrorException e) {
                    if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS && attempt < maxRetries) {
                        System.err.println("[GEMINI AI] 429 Too Many Requests. Waiting 3s to retry...");
                        try { Thread.sleep(3000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                        continue; // Thử lại
                    }
                    
                    if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                        // Fallback sang gemini-2.0-flash nếu model chính không tìm thấy
                        System.err.println("[GEMINI AI] Model not found, trying gemini-2.0-flash...");
                        String fallbackUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;
                        response = restTemplate.postForEntity(fallbackUrl, entity, String.class);
                        break;
                    } else {
                        throw e; // Lỗi khác thì ném ra ngoài
                    }
                }
            }

            if (response != null && response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode candidates = root.path("candidates");
                if (candidates.isEmpty()) {
                    System.err.println("[GEMINI AI] No candidates returned. Possible safety block.");
                    return new AIRestResponseDTO(Collections.emptyList(), "Gemini không trả về kết quả. Có thể nội dung bị chặn bởi bộ lọc an toàn.");
                }

                String jsonContent = candidates.get(0).path("content").path("parts").get(0).path("text").asText();
                
                // Xử lý Markdown (dù đã có JSON mode nhưng AI vẫn có thể bọc ```)
                jsonContent = jsonContent.trim();
                if (jsonContent.contains("```")) {
                    jsonContent = jsonContent.replaceAll("(?s)^.*?```(?:json)?", "").replaceAll("```.*?$", "").trim();
                }
                
                System.out.println("[DEBUG GEMINI CONTENT]: " + (jsonContent.length() > 200 ? jsonContent.substring(0, 200) : jsonContent));
                
                JsonNode decodedNode = objectMapper.readTree(jsonContent);
                String ocrText = decodedNode.has("ocrText") ? decodedNode.get("ocrText").asText() : promptText;
                JsonNode questionsNode = decodedNode.get("questions");

                List<QuestionResponseDTO> questions = new ArrayList<>();
                if (questionsNode != null && questionsNode.isArray()) {
                    for (JsonNode node : questionsNode) {
                        QuestionResponseDTO dto = new QuestionResponseDTO();
                        dto.setQuestion(node.path("question").asText(""));
                        List<String> answers = new ArrayList<>();
                        if (node.has("answers") && node.get("answers").isArray()) {
                            for (JsonNode ans : node.get("answers")) {
                                answers.add(ans.asText());
                            }
                        }
                        // Fallback nêú JSON mode làm mất label A,B,C,D (thường Gemini trích xuất dạng list chuỗi)
                        for (int i = 0; i < answers.size(); i++) {
                            String a = answers.get(i).trim();
                            String prefix = (char)('A' + i) + ". ";
                            if (!a.toUpperCase().startsWith(prefix)) {
                                answers.set(i, prefix + a);
                            }
                        }
                        
                        dto.setAnswers(answers);
                        dto.setCorrectIndex(node.has("correctIndex") && !node.get("correctIndex").isNull() ? node.get("correctIndex").asInt() : null);
                        if (!dto.getQuestion().isEmpty()) questions.add(dto);
                    }
                }
                return new AIRestResponseDTO(questions, ocrText);
            }
        } catch (HttpClientErrorException e) {
            System.err.println("[GEMINI AI] API Error: " + e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            System.err.println("[GEMINI AI] Error: " + e.getMessage());
            throw new RuntimeException("Lỗi phân tích kết quả từ AI: " + e.getMessage());
        }
        return new AIRestResponseDTO(Collections.emptyList(), "");
    }
}
