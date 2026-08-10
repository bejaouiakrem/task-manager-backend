package com.example.demo.dto;


import com.example.demo.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
        private String username;
        private String password;
        private String email;


}
