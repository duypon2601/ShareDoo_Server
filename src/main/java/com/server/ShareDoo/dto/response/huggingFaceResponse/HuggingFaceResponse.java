package com.server.ShareDoo.dto.response.huggingFaceResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor // Thêm constructor mặc định
@AllArgsConstructor
public class HuggingFaceResponse {
    private List<String> labels;
    private List<Double> scores;
}