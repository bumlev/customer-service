package com.service.customerservice.service;

import com.service.customerservice.dto.request.CustomerRequest;
import com.service.customerservice.dto.response.CustomerResponse;
import com.service.customerservice.entity.CustomerEntity;
import com.service.customerservice.exception.CustomerException;
import com.service.customerservice.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    public List<CustomerResponse> getAllCustomers(){

        List<CustomerEntity> customerEntity = customerRepository.findAll();
        return customerEntity.stream().map(customer -> CustomerResponse.builder()
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .build()).toList();
    }

    public CustomerEntity createCustomer(CustomerRequest customerRequest){

        CustomerEntity savedCustomer = CustomerEntity.builder()
                .name(customerRequest.getName())
                .email(customerRequest.getEmail())
                .phone(customerRequest.getPhone())
                .build();
        return  customerRepository.save(savedCustomer);
    }

    public CustomerEntity updateCustomer(Long customerId, CustomerRequest customerRequest) {
        CustomerEntity existingCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerException("Customer not found with id: " + customerId));

        existingCustomer.setName(customerRequest.getName());
        existingCustomer.setEmail(customerRequest.getEmail());
        existingCustomer.setPhone(customerRequest.getPhone());

        return customerRepository.save(existingCustomer);
    }

    public CustomerResponse getCustomerById(Long customerId){
        CustomerEntity customerEntity = customerRepository.findById(customerId)
                .orElseThrow(()-> new CustomerException("Customer not found with id: " + customerId));

        return CustomerResponse.builder()
                .name(customerEntity.getName())
                .email(customerEntity.getEmail())
                .phone(customerEntity.getPhone())
                .build();
    }
}
