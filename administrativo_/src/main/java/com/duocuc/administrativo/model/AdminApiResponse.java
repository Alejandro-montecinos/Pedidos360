package com.duocuc.administrativo.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminApiResponse {

    private boolean success;
    private String message;
    private Object data;
    private Map<String, Object> details;

    public static AdminApiResponse ok(String message) {
        return AdminApiResponse.builder()
                .success(true)
                .message(message)
                .build();
    }

    public static AdminApiResponse ok(String message, Map<String, Object> details) {
        return AdminApiResponse.builder()
                .success(true)
                .message(message)
                .details(details)
                .build();
    }

    public static AdminApiResponse error(String message) {
        return AdminApiResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}
