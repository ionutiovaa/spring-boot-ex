package com.iova.customer;

import com.iova.exception.DuplicateResourceException;
import com.iova.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerDAO customerDAO;

    public List<Customer> getAllCustomers() {
        return customerDAO.selectAllCustomers();
    }

    public Customer getCustomer(Integer id) {
        return customerDAO.selectCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id: %s not found".formatted(id)));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest){
        if (customerDAO.existsPersonWithEmail(customerRegistrationRequest.email())) {
            throw new DuplicateResourceException("email already taken");
        }

        Customer customer = new Customer();
        customer.setName(customerRegistrationRequest.name());
        customer.setEmail(customerRegistrationRequest.email());
        customer.setAge(customerRegistrationRequest.age());
        customer.setCountry(customerRegistrationRequest.country());
        customerDAO.insertCustomer(customer);
    }

    public void deleteCustomerById(Integer id) {
        if (!customerDAO.existsPersonWithId(id)) {
            throw new ResourceNotFoundException("Not found");
        }
        customerDAO.deleteCustomerById(id);
    }

    public void updateCustomer(Integer id, CustomerRegistrationRequest request) {
        Customer customer = getCustomer(id);

        boolean hasChanges = false;

        if (request.name() != null && !request.name().isEmpty() && !request.name().equals(customer.getName())) {
            customer.setName(request.name());
            hasChanges = true;
        }

        if (request.age() != null && !request.age().equals(customer.getAge())) {
            customer.setAge(request.age());
            hasChanges = true;
        }

        if (request.email() != null && !request.email().isEmpty() && !request.email().equals(customer.getEmail())) {
            if (customerDAO.existsPersonWithEmail(request.email())) {
                throw new DuplicateResourceException("email taken");
            }
            customer.setEmail(request.email());
            hasChanges = true;
        }

        if (request.country() != null && !request.country().equals(customer.getCountry())) {
            customer.setCountry(request.country());
            hasChanges = true;
        }

        if (hasChanges) {
            customerDAO.insertCustomer(customer);
        }
    }
}
