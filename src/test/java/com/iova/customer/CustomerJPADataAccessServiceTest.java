package com.iova.customer;

import com.iova.AbstractTestcontainers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomerJPADataAccessServiceTest extends AbstractTestcontainers {

    @Mock
    private CustomerRepository customerRepository;
    private CustomerJPADataAccessService underTest;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception{
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {
        /*Customer customer = Customer.builder()
                .name(FAKER.name().fullName())
                .email(FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID())
                .age(20)
                .build();

        List<Customer> expectedList = List.of(customer);
        when(customerRepository.findAll()).thenReturn(expectedList);

        underTest.insertCustomer(customer);
        List<Customer> actual = underTest.selectAllCustomers();

        assertThat(actual)
                .isNotEmpty()
                .isEqualTo(expectedList);
        verify(customerRepository).findAll();*/

        underTest.selectAllCustomers();

        verify(customerRepository).findAll();
    }

    @Test
    void selectCustomerById() {
        int id = 1;
        underTest.selectCustomerById(id);
        verify(customerRepository).findById(id);
    }

    @Test
    void willReturnEmptyWhenSelectCustomerById() {
        int id = -1;
        var actual = underTest.selectCustomerById(id);
        assertThat(actual).isEmpty();
    }

    @Test
    void insertCustomer() {
    }

    @Test
    void existsPersonWithEmail() {
    }

    @Test
    void existsPersonWithEmailReturnsFalseWhenDoesNotExists() {
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var actual = underTest.existsPersonWithEmail(email);
        assertThat(actual).isFalse();
    }

    @Test
    void existsPersonWithId() {
    }

    @Test
    void deleteCustomerById() {
    }
}