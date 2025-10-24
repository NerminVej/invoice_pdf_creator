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
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + id));
    }

    @Transactional
    public void delete(Long id) {
        Invoice inv = findByIdOrThrow(id);
        repo.delete(inv);
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

    @Transactional
    public Invoice update(Long id, Invoice cmd) {
        Invoice existing = findByIdOrThrow(id);

        existing.setInvoiceDate(cmd.getInvoiceDate());
        existing.setServiceDate(cmd.getServiceDate());
        existing.setEntryText(cmd.getEntryText());

        existing.setBillCompanyName(cmd.getBillCompanyName());
        existing.setBillPhoneNumber(cmd.getBillPhoneNumber());
        existing.setBillLine1(cmd.getBillLine1());
        existing.setBillLine2(cmd.getBillLine2());
        existing.setBillPostalCode(cmd.getBillPostalCode());
        existing.setBillCity(cmd.getBillCity());
        existing.setBillCountryCode(cmd.getBillCountryCode());

        existing.getItems().clear();
        if (cmd.getItems() != null) {
            for (int i = 0; i < cmd.getItems().size(); i++) {
                var item = cmd.getItems().get(i);
                item.setInvoice(existing);
                item.setPosition(i + 1);
                existing.getItems().add(item);
            }
        }

        return repo.save(existing);
    }

}
