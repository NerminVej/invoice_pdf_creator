package com.invoice_generator.invoice_generator.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoice", indexes = {
        @Index(name = "idx_invoice_number", columnList = "invoice_number", unique = true)
})
public class Invoice {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Optional reference to the live Customer entity */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    /* -------- Snapshot of customer data at the time of invoicing -------- */
    @NotBlank @Size(max = 200)
    @Column(name = "bill_company_name", nullable = false)
    private String billCompanyName;

    @Size(max = 50)
    @Column(name = "bill_phone_number")
    private String billPhoneNumber;

    @NotBlank @Size(max = 200)
    @Column(name = "bill_line1", nullable = false)
    private String billLine1;

    @Size(max = 200)
    @Column(name = "bill_line2")
    private String billLine2;

    @NotBlank @Size(max = 20)
    @Column(name = "bill_postal_code", nullable = false)
    private String billPostalCode;

    @NotBlank @Size(max = 100)
    @Column(name = "bill_city", nullable = false)
    private String billCity;

    @NotBlank @Size(min = 2, max = 2)
    @Column(name = "bill_country_code", length = 2, nullable = false)
    private String billCountryCode;

    /* -------------------- Invoice metadata -------------------- */
    @NotBlank
    @Column(name = "invoice_number", nullable = false, unique = true)
    private String invoiceNumber;            // Rechnungsnummer

    @NotNull
    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;           // Leistungsdatum / Service Datum

    @NotNull
    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;           // Rechnungsdatum

    @Size(max = 4000)
    @Column(name = "entry_text", length = 4000)
    private String entryText;                // Einleitungstext

    /* -------------------- Line items -------------------- */
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<LineItem> items = new ArrayList<>();

    /* -------------------- Helpers -------------------- */

    public void addItem(LineItem item) {
        item.setInvoice(this);
        if (item.getPosition() == null) {
            item.setPosition(items.size() + 1);
        }
        items.add(item);
    }

    public void removeItem(LineItem item) {
        item.setInvoice(null);
        items.remove(item);
    }

    /** Sum of line totals (quantity * unitNetAmount). */
    @Transient
    public BigDecimal getTotalNet() {
        return items.stream()
                .map(LineItem::getLineNetTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /* -------------------- Getters/Setters -------------------- */

    public Long getId() { return id; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public String getBillCompanyName() { return billCompanyName; }
    public void setBillCompanyName(String billCompanyName) { this.billCompanyName = billCompanyName; }

    public String getBillPhoneNumber() { return billPhoneNumber; }
    public void setBillPhoneNumber(String billPhoneNumber) { this.billPhoneNumber = billPhoneNumber; }

    public String getBillLine1() { return billLine1; }
    public void setBillLine1(String billLine1) { this.billLine1 = billLine1; }

    public String getBillLine2() { return billLine2; }
    public void setBillLine2(String billLine2) { this.billLine2 = billLine2; }

    public String getBillPostalCode() { return billPostalCode; }
    public void setBillPostalCode(String billPostalCode) { this.billPostalCode = billPostalCode; }

    public String getBillCity() { return billCity; }
    public void setBillCity(String billCity) { this.billCity = billCity; }

    public String getBillCountryCode() { return billCountryCode; }
    public void setBillCountryCode(String billCountryCode) { this.billCountryCode = billCountryCode; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public LocalDate getServiceDate() { return serviceDate; }
    public void setServiceDate(LocalDate serviceDate) { this.serviceDate = serviceDate; }

    public LocalDate getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }

    public String getEntryText() { return entryText; }
    public void setEntryText(String entryText) { this.entryText = entryText; }

    public List<LineItem> getItems() { return items; }
    public void setItems(List<LineItem> items) {
        this.items.clear();
        if (items != null) {
            items.forEach(this::addItem);
        }
    }
}
