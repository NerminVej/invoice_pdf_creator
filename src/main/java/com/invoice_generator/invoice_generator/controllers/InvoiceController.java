package com.invoice_generator.invoice_generator.controllers;

import com.invoice_generator.invoice_generator.services.PdfService;
import java.net.URL;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.invoice_generator.invoice_generator.models.Invoice;
import com.invoice_generator.invoice_generator.services.CustomerService;
import com.invoice_generator.invoice_generator.services.InvoiceService;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoices;
    private final CustomerService customers;
    private final PdfService pdfService;

    public InvoiceController(InvoiceService invoices, CustomerService customers, PdfService pdfService) {
        this.invoices = invoices;
        this.customers = customers;
        this.pdfService = pdfService;
    }

    @GetMapping("/select-customer")
    public String selectCustomer(Model model) {
        model.addAttribute("customers", customers.findAll());
        return "invoices/select-customer";
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("invoices", invoices.findAll());
        return "invoices";
    }

    @GetMapping("/new")
    public String newInvoice(@RequestParam(required = false) Long customerId, Model model) {
        Invoice invoice = (customerId == null)
                ? new Invoice()
                : invoices.createDraftFromCustomer(customerId);

        model.addAttribute("invoice", invoice);
        model.addAttribute("customers", customers.findAll());
        return "invoices/invoice-form";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        model.addAttribute("invoice", invoices.findByIdOrThrow(id));
        return "invoices/invoice-detail";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("invoice", invoices.findByIdOrThrow(id));
        model.addAttribute("customers", customers.findAll());
        return "invoices/invoice-form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute("invoice") Invoice invoice) {
        Invoice saved = invoices.update(id, invoice);
        return "redirect:/invoices/" + saved.getId() + "?updated=1";
    }

    @PostMapping
    public String create(@ModelAttribute("invoice") Invoice invoice) {
        Invoice saved = invoices.save(invoice);
        return "redirect:/invoices/" + saved.getId() + "?saved=1";
    }

    @GetMapping("/{id}/print")
    public ResponseEntity<byte[]> print(@PathVariable Long id) {
        var inv = invoices.findByIdOrThrow(id);

        byte[] pdf = pdfService.renderTemplateToPdf(
                "invoices/invoice-pdf",
                Map.of("invoice", inv),
                "");

        String filename = "Invoice-" + (inv.getInvoiceNumber() != null ? inv.getInvoiceNumber() : inv.getId()) + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + filename + "\"")
                .body(pdf);
    }

}
