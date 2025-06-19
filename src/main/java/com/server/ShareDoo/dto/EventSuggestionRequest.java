package com.server.ShareDoo.dto;

import lombok.Data;

@Data
public class EventSuggestionRequest {
    private String eventDescription;
    private String budget;
    private int guestCount;
} 