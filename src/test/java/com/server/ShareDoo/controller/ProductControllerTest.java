//package com.server.ShareDoo.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.server.ShareDoo.dto.request.productRequest.ProductDTO;
//import com.server.ShareDoo.dto.request.userRequest.LoginDTO;
//import com.server.ShareDoo.enums.Category;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//import java.math.BigDecimal;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@Transactional
//class ProductControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private String token;
//
//    @BeforeEach
//    void setUp() throws Exception {
//        LoginDTO loginDTO = new LoginDTO();
//        loginDTO.setUsername("admin");
//        loginDTO.setPassword("admin");
//        String response = mockMvc.perform(post("/api/login")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(loginDTO)))
//                .andReturn().getResponse().getContentAsString();
//        token = objectMapper.readTree(response).get("token").asText();
//    }
//
//    @Test
//    void testGetAllProducts() throws Exception {
//        mockMvc.perform(get("/api/products"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void testCreateProductWithAuth() throws Exception {
//        ProductDTO productDTO = new ProductDTO();
//        productDTO.setName("Test Product");
//        productDTO.setDescription("Test Description");
//        productDTO.setImageUrl("https://example.com/image.jpg");
//        productDTO.setLocation("Ho Chi Minh City");
//        productDTO.setCategory(Category.CAMPING);
//        productDTO.setPricePerDay(BigDecimal.valueOf(100.0));
//        productDTO.setAvailabilityStatus(ProductDTO.AvailabilityStatus.AVAILABLE);
//
//        mockMvc.perform(post("/api/products")
//                .header("Authorization", "Bearer " + token)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(productDTO)))
//                .andExpect(status().isCreated());
//    }
//}