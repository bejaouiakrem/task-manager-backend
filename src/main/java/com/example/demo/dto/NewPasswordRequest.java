// dto/NewPasswordRequest.java
package com.example.demo.dto;

import lombok.Data;

@Data
public class NewPasswordRequest {
    private String token;
    private String newPassword;
}
