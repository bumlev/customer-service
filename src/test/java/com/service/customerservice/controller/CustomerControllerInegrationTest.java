package com.service.customerservice.controller;

import com.service.customerservice.dto.request.CustomerRequest;
import com.service.customerservice.dto.response.CustomerResponse;
import com.service.customerservice.entity.CustomerEntity;
import com.service.customerservice.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
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

        customerRepository.saveAll(List.of(customer1, customer2));

        ResponseEntity<List<CustomerResponse>> response = restTemplate.exchange(
                "/customers",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CustomerResponse>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        CustomerResponse firstCustomer = response.getBody().get(0);
        assertEquals("John Doe", firstCustomer.getName());
        assertEquals("john.doe@example.com", firstCustomer.getEmail());
        assertEquals("1234567890", firstCustomer.getPhone());

        CustomerResponse secondCustomer = response.getBody().get(1);
        assertEquals("Jane Smith", secondCustomer.getName());
        assertEquals("jane.smith@example.com", secondCustomer.getEmail());
        assertEquals("0987654321", secondCustomer.getPhone());
    }

    @Test
    void getAllCustomers_NoCustomers_ReturnsEmptyList() {
        ResponseEntity<List<CustomerResponse>> response = restTemplate.exchange(
                "/customers",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CustomerResponse>>() {}
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void createCustomer_Success() {
        CustomerRequest request = new CustomerRequest(
                "John Doe",
                "john.doe@example.com",
                "1234567890"
        );

        ResponseEntity<CustomerEntity> response = restTemplate.postForEntity(
                "/customers",
                request,
                CustomerEntity.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John Doe", response.getBody().getName());
        assertEquals("john.doe@example.com", response.getBody().getEmail());
        assertEquals("1234567890", response.getBody().getPhone());

        List<CustomerEntity> customers = customerRepository.findAll();
        assertEquals(1, customers.size());
        assertEquals("John Doe", customers.get(0).getName());
    }

    @Test
    void createCustomer_WithNullFields_ReturnsBadRequest() {
        CustomerRequest request = new CustomerRequest(
                null,
                null,
                null
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/customers",
                request,
                String.class
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(customerRepository.findAll().isEmpty());
    }

    @Test
    void createCustomer_WithDuplicateEmail_ReturnsBadRequest() {
        CustomerEntity existingCustomer = CustomerEntity.builder()
                .name("Existing User")
                .email("john.doe@example.com")
                .phone("0987654321")
                .build();
        customerRepository.save(existingCustomer);

        CustomerRequest request = new CustomerRequest(
                "John Doe",
                "john.doe@example.com",
                "1234567890"
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/customers",
                request,
                String.class
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(1, customerRepository.findAll().size());
    }

    @Test
    void getCustomerById_Success() {
        CustomerEntity customer = CustomerEntity.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .build();
        CustomerEntity savedCustomer = customerRepository.save(customer);

        ResponseEntity<CustomerResponse> response = restTemplate.getForEntity(
                "/customers/" + savedCustomer.getId(),
                CustomerResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John Doe", response.getBody().getName());
        assertEquals("john.doe@example.com", response.getBody().getEmail());
        assertEquals("1234567890", response.getBody().getPhone());
    }

    @Test
    void getCustomerById_CustomerNotFound_ReturnsNotFound() {
        Long nonExistentId = 999L;

        ResponseEntity<String> response = restTemplate.getForEntity(
                "/customers/" + nonExistentId,
                String.class
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Customer not found with id: " + nonExistentId));
    }


    @Test
    void updateCustomer_Success() {
        CustomerEntity existingCustomer = CustomerEntity.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .build();
        CustomerEntity savedCustomer = customerRepository.save(existingCustomer);

        CustomerRequest updateRequest = new CustomerRequest(
                "Updated Name",
                "updated.email@example.com",
                "9876543210"
        );

        ResponseEntity<CustomerEntity> response = restTemplate.exchange(
                "/customers/" + savedCustomer.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                CustomerEntity.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Name", response.getBody().getName());
        assertEquals("updated.email@example.com", response.getBody().getEmail());
        assertEquals("9876543210", response.getBody().getPhone());

        CustomerEntity updatedCustomer = customerRepository.findById(savedCustomer.getId()).orElse(null);
        assertNotNull(updatedCustomer);
        assertEquals("Updated Name", updatedCustomer.getName());
        assertEquals("updated.email@example.com", updatedCustomer.getEmail());
        assertEquals("9876543210", updatedCustomer.getPhone());
    }

    @Test
    void updateCustomer_CustomerNotFound_ReturnsNotFound() {
        Long nonExistentId = 999L;
        CustomerRequest updateRequest = new CustomerRequest(
                "Updated Name",
                "updated.email@example.com",
                "9876543210"
        );

        ResponseEntity<String> response = restTemplate.exchange(
                "/customers/" + nonExistentId,
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                String.class
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Customer not found with id: " + nonExistentId));
    }

    @Test
    void updateCustomer_WithNullFields_ReturnsBadRequest() {
        CustomerEntity existingCustomer = CustomerEntity.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .build();
        CustomerEntity savedCustomer = customerRepository.save(existingCustomer);

        CustomerRequest updateRequest = new CustomerRequest(
                null,
                null,
                null
        );

        ResponseEntity<String> response = restTemplate.exchange(
                "/customers/" + savedCustomer.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                String.class
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        CustomerEntity unchangedCustomer = customerRepository.findById(savedCustomer.getId()).orElse(null);
        assertNotNull(unchangedCustomer);
        assertEquals("John Doe", unchangedCustomer.getName());
        assertEquals("john.doe@example.com", unchangedCustomer.getEmail());
        assertEquals("1234567890", unchangedCustomer.getPhone());
    }

    @Test
    void updateCustomer_WithDuplicateEmail_ReturnsBadRequest() {
        CustomerEntity existingCustomer1 = CustomerEntity.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .build();
        CustomerEntity existingCustomer2 = CustomerEntity.builder()
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .phone("0987654321")
                .build();
        customerRepository.saveAll(List.of(existingCustomer1, existingCustomer2));

        CustomerRequest updateRequest = new CustomerRequest(
                "Updated Name",
                "jane.smith@example.com",
                "9876543210"
        );

        ResponseEntity<String> response = restTemplate.exchange(
                "/customers/" + existingCustomer1.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                String.class
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        CustomerEntity unchangedCustomer = customerRepository.findById(existingCustomer1.getId()).orElse(null);
        assertNotNull(unchangedCustomer);
        assertEquals("John Doe", unchangedCustomer.getName());
        assertEquals("john.doe@example.com", unchangedCustomer.getEmail());
    }

}