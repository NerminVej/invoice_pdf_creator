package com.invoice_generator.invoice_generator.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoice_generator.invoice_generator.models.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
