package com.iova.customer;

import com.iova.exception.DuplicateResourceException;
import com.iova.exception.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDAO customerDAO;
    private CustomerService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDAO);
    }

    @Test
    void getAllCustomers() {
        underTest.getAllCustomers();
        verify(customerDAO).selectAllCustomers();
    }

    @Test
    void getCustomer() {
        int id = 10;
        Customer customer = new Customer(id, "Alex", "email", 20, "USA");
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));
        Customer actual = underTest.getCustomer(id);
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willThrowWhenGetCustomerReturnsEmptyOptional() {
        int id = 10;
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id: %s not found".formatted(id));
    }

    @Test
    void addCustomer() {
        String email = "email";
        when(customerDAO.existsPersonWithEmail(email)).thenReturn(false);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest("Alex", email, 19, "USA");
        underTest.addCustomer(request);
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDAO).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getCountry()).isEqualTo(request.country());
    }

    @Test
    void addCustomerThrowAnErrorWhenEmailAlreadyExists() {
        String email = "email";
        when(customerDAO.existsPersonWithEmail(email)).thenReturn(true);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest("Alex", email, 19, "USA");
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");
    }

    @Test
    void deleteCustomerById() {
        int id = 1;
        when(customerDAO.existsPersonWithId(id)).thenReturn(true);
        underTest.deleteCustomerById(id);
        verify(customerDAO).deleteCustomerById(id);
    }

    @Test
    void deleteCustomerByIdThrowWhenCustomerNotExists() {
        int id = 1;
        when(customerDAO.existsPersonWithId(id)).thenReturn(false);
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Not found");
        verify(customerDAO, never()).deleteCustomerById(id);
    }

    @Test
    void updateCustomer() {
        int id = 1;
        String email = "email";
        Customer customer = new Customer(id, "Alex", email, 20, "USA");
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "newEmail";
        CustomerRegistrationRequest request = new CustomerRegistrationRequest("Alexia", newEmail, 20, "RO");

        underTest.updateCustomer(id, request);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDAO).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getCountry()).isEqualTo(request.country());
    }
}