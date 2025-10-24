package com.invoice_generator.invoice_generator.services;

import com.invoice_generator.invoice_generator.models.InvoiceSequence;
import com.invoice_generator.invoice_generator.repositories.InvoiceSequenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class InvoiceNumberGeneratorService {

    private final InvoiceSequenceRepository sequences;

    public InvoiceNumberGeneratorService(InvoiceSequenceRepository sequences) {
        this.sequences = sequences;
    }

    @Transactional
    public String nextNumber(LocalDate invoiceDate) {
        int year = (invoiceDate != null ? invoiceDate.getYear() : LocalDate.now().getYear());

        InvoiceSequence seq = sequences.findForUpdate(year)
                .orElseGet(() -> new InvoiceSequence(year, 0));

        int next = seq.getLastNumber() + 1;
        seq.setLastNumber(next);
        sequences.save(seq);

        return String.format("%d-%06d", year, next);
    }
}
