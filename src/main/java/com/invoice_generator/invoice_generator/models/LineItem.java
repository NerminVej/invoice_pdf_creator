package com.invoice_generator.invoice_generator.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "invoice_line_item")
public class LineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Owning invoice */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    /** 1-based position in the invoice */
    @Min(1)
    @Column(name = "position_index")
    private Integer position;

    @NotBlank
    @Size(max = 1000)
    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = false)
    @Column(name = "quantity", precision = 18, scale = 3, nullable = false)
    private BigDecimal quantity;

    /** Net unit price (EUR without VAT) */
    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    @Column(name = "unit_net_amount", precision = 18, scale = 2, nullable = false)
    private BigDecimal unitNetAmount;

    /** quantity * unitNetAmount */
    @Transient
    public BigDecimal getLineNetTotal() {
        if (quantity == null || unitNetAmount == null)
            return BigDecimal.ZERO;
        return unitNetAmount.multiply(quantity);
    }

    /* -------------------- Getters/Setters -------------------- */

    public Long getId() {
        return id;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitNetAmount() {
        return unitNetAmount;
    }

    public void setUnitNetAmount(BigDecimal unitNetAmount) {
        this.unitNetAmount = unitNetAmount;
    }
}
