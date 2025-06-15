package com.server.ShareDoo.service.aiSuggestionService;

import com.server.ShareDoo.service.productService.ProductService;
import com.server.ShareDoo.service.rentalService.RentalService;
import com.server.ShareDoo.service.userService.UserService;
import org.springframework.stereotype.Service;

@Service
public class AiSuggestionService {

    private final HuggingFaceService huggingFaceService;
    private final ProductService productService;
    private final RentalService rentalService;
    private final UserService userService;

    public AiSuggestionService(HuggingFaceService huggingFaceService,
                               ProductService productService,
                               RentalService rentalService,
                               UserService userService) {
        this.huggingFaceService = huggingFaceService;
        this.productService = productService;
        this.rentalService = rentalService;
        this.userService = userService;
    }

    public String suggestProductsForEvent(String eventDescription,
                                          String budget,
                                          Integer guestCount) {
        String availableProducts = productService.getAvailableProductsAsString();
        String prompt = String.format(
            "Bạn là một chuyên gia tư vấn cho thuê đồ dùng cho sự kiện.\n" +
            "Thông tin sự kiện:\n" +
            "- Mô tả: %s\n" +
            "- Ngân sách: %s\n" +
            "- Số khách: %s\n" +
            "Danh sách sản phẩm có sẵn:\n%s\n" +
            "Hãy gợi ý 5-7 sản phẩm phù hợp nhất cho sự kiện này, bao gồm lý do tại sao nên chọn từng sản phẩm. Trả lời bằng tiếng Việt.",
            eventDescription,
            budget != null ? budget : "Chưa xác định",
            guestCount != null ? guestCount.toString() : "Chưa xác định",
            availableProducts
        );
        return huggingFaceService.generateText(prompt);
    }

    public String analyzeUserRentalHistory(Long userId) {
        String userRentalHistory = rentalService.getUserRentalHistory(userId);
        String prompt = String.format(
            "Bạn là một chuyên gia phân tích hành vi người dùng.\n" +
            "Thông tin lịch sử thuê đồ của người dùng:\n%s\n" +
            "Hãy phân tích và đưa ra các gợi ý sản phẩm phù hợp dựa trên lịch sử thuê đồ. Trả lời bằng tiếng Việt.",
            userRentalHistory
        );
        return huggingFaceService.generateText(prompt);
    }

    public String predictRentalTrends() {
        String rentalData = rentalService.getRentalTrendsData();
        String prompt = String.format(
            "Bạn là một chuyên gia phân tích xu hướng thị trường.\n" +
            "Dữ liệu thuê đồ:\n%s\n" +
            "Hãy phân tích và dự đoán xu hướng thuê đồ trong thời gian tới. Trả lời bằng tiếng Việt.",
            rentalData
        );
        return huggingFaceService.generateText(prompt);
    }
}
