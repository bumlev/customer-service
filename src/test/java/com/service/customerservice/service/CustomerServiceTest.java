package com.service.customerservice.service;

import com.service.customerservice.dto.request.CustomerRequest;
import com.service.customerservice.dto.response.CustomerResponse;
import com.service.customerservice.entity.CustomerEntity;
import com.service.customerservice.exception.CustomerException;
import com.service.customerservice.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCustomer_Success() {
        CustomerRequest request = new CustomerRequest("John Doe", "john.doe@example.com", "1234567890");
        CustomerEntity savedEntity = CustomerEntity.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .build();

        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(savedEntity);
        CustomerEntity result = customerService.createCustomer(request);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("1234567890", result.getPhone());
        verify(customerRepository, times(1)).save(any(CustomerEntity.class));
    }

    @Test
    void createCustomer_NullRequest_ThrowsException() {
        CustomerRequest request = null;

        assertThrows(NullPointerException.class, () -> customerService.createCustomer(request));
        verify(customerRepository, never()).save(any(CustomerEntity.class));
    }



    @Test
    void getAllCustomers_Success() {
        CustomerEntity customer1 = CustomerEntity.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .build();

        CustomerEntity customer2 = CustomerEntity.builder()
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .phone("0987654321")
                .build();

        when(customerRepository.findAll()).thenReturn(List.of(customer1, customer2));
        List<CustomerResponse> result = customerService.getAllCustomers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("Jane Smith", result.get(1).getName());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void getAllCustomers_NoCustomers_ReturnsEmptyList() {
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());

        List<CustomerResponse> result = customerService.getAllCustomers();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void updateCustomer_Success() {
        Long customerId = 1L;
        CustomerRequest request = new CustomerRequest("Updated Name", "updated.email@example.com", "9876543210");
        CustomerEntity existingCustomer = CustomerEntity.builder()
                .id(customerId)
                .name("Old Name")
                .email("old.email@example.com")
                .phone("1234567890")
                .build();

        CustomerEntity updatedCustomer = CustomerEntity.builder()
                .id(customerId)
                .name("Updated Name")
                .email("updated.email@example.com")
                .phone("9876543210")
                .build();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(updatedCustomer);

        CustomerEntity result = customerService.updateCustomer(customerId, request);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("updated.email@example.com", result.getEmail());
        assertEquals("9876543210", result.getPhone());
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, times(1)).save(existingCustomer);
    }

    @Test
    void updateCustomer_CustomerNotFound_ThrowsException() {
        Long customerId = 1L;
        CustomerRequest request = new CustomerRequest("Updated Name", "updated.email@example.com", "9876543210");

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        CustomerException exception = assertThrows(CustomerException.class, () -> customerService.updateCustomer(customerId, request));
        assertEquals("Customer not found with id: " + customerId, exception.getMessage());
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, never()).save(any(CustomerEntity.class));
    }


    @Test
    void getCustomerById_Success() {
        Long customerId = 1L;
        CustomerEntity customerEntity = CustomerEntity.builder()
                .id(customerId)
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .build();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customerEntity));
        CustomerResponse result = customerService.getCustomerById(customerId);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("1234567890", result.getPhone());
        verify(customerRepository, times(1)).findById(customerId);
    }


    @Test
    void getCustomerById_CustomerNotFound_ThrowsException() {
        Long customerId = 1L;

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        CustomerException exception = assertThrows(CustomerException.class, () -> customerService.getCustomerById(customerId));
        assertEquals("Customer not found with id: " + customerId, exception.getMessage());
        verify(customerRepository, times(1)).findById(customerId);
    }

}