package com.service.customerservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerRequest {
    private String name;
    private String email;
    private String phone;
}
