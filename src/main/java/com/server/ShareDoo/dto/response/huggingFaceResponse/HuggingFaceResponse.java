package com.server.ShareDoo.dto.response.huggingFaceResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@Data
@NoArgsConstructor // Thêm constructor mặc định
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HuggingFaceResponse {
    private List<String> labels;
    private List<Double> scores;
}