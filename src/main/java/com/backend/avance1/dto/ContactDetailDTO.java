package com.backend.avance1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactDetailDTO {

    private String messageCode;
    private String name;
    private String email;
    private String message;
    private LocalDateTime createdAt;

    private List<ReplyInfo> replies;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReplyInfo {
        private String replyCode;
        private String replyMessage;
        private LocalDateTime repliedAt;
    }
}