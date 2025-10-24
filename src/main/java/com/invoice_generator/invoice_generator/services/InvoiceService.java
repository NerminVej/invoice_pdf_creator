package com.invoice_generator.invoice_generator.services;

import com.invoice_generator.invoice_generator.models.Customer;
import com.invoice_generator.invoice_generator.models.Invoice;
import com.invoice_generator.invoice_generator.repositories.InvoiceRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InvoiceService {

    private final InvoiceRepository repo;
    private final CustomerService customers;
    private final InvoiceNumberGeneratorService generator;

    public InvoiceService(InvoiceRepository repo, CustomerService customers, InvoiceNumberGeneratorService generator) {
        this.repo = repo;
        this.customers = customers;
        this.generator = generator;
    }

    public List<Invoice> findAll() {
        return repo.findAll();
    }

    public Invoice findByIdOrThrow(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));
    }

    @Transactional
    public Invoice save(Invoice inv) {
        if (inv.getInvoiceDate() == null) {
            inv.setInvoiceDate(LocalDate.now());
        }
        if (inv.getServiceDate() == null) {
            inv.setServiceDate(inv.getInvoiceDate());
        }

        if (inv.getInvoiceNumber() == null || inv.getInvoiceNumber().isBlank()) {
            String num = generator.nextNumber(inv.getInvoiceDate());
            inv.setInvoiceNumber(num);
        }
        if (inv.getItems() != null) {
            for (int i = 0; i < inv.getItems().size(); i++) {
                var item = inv.getItems().get(i);
                item.setInvoice(inv);
                item.setPosition(i + 1);
            }
        }
        return repo.save(inv);
    }

    public Invoice createDraftFromCustomer(Long customerId) {
        Customer c = customers.findByIdOrThrow(customerId);

        Invoice inv = new Invoice();
        inv.setCustomer(c);

        inv.setBillCompanyName(c.getCompanyName());
        inv.setBillPhoneNumber(c.getPhoneNumber());
        if (c.getAddress() != null) {
            inv.setBillLine1(c.getAddress().getLine1());
            inv.setBillLine2(c.getAddress().getLine2());
            inv.setBillPostalCode(c.getAddress().getPostalCode());
            inv.setBillCity(c.getAddress().getCity());
            inv.setBillCountryCode(c.getAddress().getCountryCode());
        }

        inv.setInvoiceDate(LocalDate.now());
        inv.setServiceDate(LocalDate.now());

        return inv;
    }
}
