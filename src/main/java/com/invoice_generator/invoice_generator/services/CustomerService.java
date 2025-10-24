package com.invoice_generator.invoice_generator.services;

import org.springframework.stereotype.Service;

import com.invoice_generator.invoice_generator.models.Customer;
import com.invoice_generator.invoice_generator.repositories.CustomerRepository;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository repo;

    public CustomerService(CustomerRepository repo) {
        this.repo = repo;
    }

    public Customer save(Customer c) {
        return repo.save(c);
    }

    public List<Customer> findAll() {
        return repo.findAll();
    }

    public Customer findByIdOrThrow(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));
    }

    public Customer update(Long id, Customer fromForm) {
        Customer existing = findByIdOrThrow(id);

        existing.setCompanyName(fromForm.getCompanyName());
        existing.setPhoneNumber(fromForm.getPhoneNumber());

        if (existing.getAddress() == null) {
            existing.setAddress(fromForm.getAddress());
        } else if (fromForm.getAddress() != null) {
            existing.getAddress().setLine1(fromForm.getAddress().getLine1());
            existing.getAddress().setLine2(fromForm.getAddress().getLine2());
            existing.getAddress().setPostalCode(fromForm.getAddress().getPostalCode());
            existing.getAddress().setCity(fromForm.getAddress().getCity());
            existing.getAddress().setCountryCode(fromForm.getAddress().getCountryCode());
        }

        return repo.save(existing);
    }
}
