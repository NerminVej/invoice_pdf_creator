package com.invoice_generator.invoice_generator.models;

import jakarta.persistence.*;

@Entity
@Table(name = "invoice_sequence")
public class InvoiceSequence {

    @Id
    @Column(name = "year_key")
    private Integer year; // e.g., 2025

    @Column(name = "last_number", nullable = false)
    private Integer lastNumber = 0;

    protected InvoiceSequence() {
    }

    public InvoiceSequence(Integer year, Integer lastNumber) {
        this.year = year;
        this.lastNumber = lastNumber;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getLastNumber() {
        return lastNumber;
    }

    public void setLastNumber(Integer lastNumber) {
        this.lastNumber = lastNumber;
    }
}
