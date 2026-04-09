package com.hutech.TrungTamTiengAnh.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponseDTO {
    private String question;
    private List<String> answers;
    private Integer correctIndex;
}
