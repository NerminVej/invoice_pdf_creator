package com.invoice_generator.invoice_generator.models;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Embeddable
public class Address {

    @NotBlank
    @Size(max = 200)
    private String line1; // Street + number

    @Size(max = 200)
    private String line2; // Apt/Floor/etc. (optional)

    @NotBlank
    @Size(max = 20)
    private String postalCode; // PLZ / ZIP

    @NotBlank
    @Size(max = 100)
    private String city;

    /**
     * ISO 3166-1 alpha-2 code (e.g., "DE", "US").
     * Use a String for flexibility; validate in service or with a custom validator.
     */
    @NotBlank
    @Size(min = 2, max = 2)
    private String countryCode;

    // getters/setters
    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
