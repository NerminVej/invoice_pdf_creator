package com.invoice_generator.invoice_generator.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.invoice_generator.invoice_generator.models.Customer;
import com.invoice_generator.invoice_generator.services.CustomerService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService; // or a JpaRepository

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // GET /customers -> list page (you already mapped via MvcConfig; either keep
    // that or use this)
    @GetMapping
    public String list(Model model) {
        model.addAttribute("customers", customerService.findAll());
        return "customers"; // renders templates/customers.html
    }

    // GET /customers/new -> show empty form
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("customer", new Customer()); // backing object for th:object
        model.addAttribute("isEdit", false);
        return "customer-form"; // renders templates/customer-form.html
    }

    // EDIT FORM
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Customer existing = customerService.findByIdOrThrow(id);
        model.addAttribute("customer", existing); // pre-fills the form
        model.addAttribute("isEdit", true);
        return "customer-form";
    }

    // CREATE SUBMIT -> POST /customers
    @PostMapping
    public String create(@ModelAttribute("customer") Customer customer) {
        customerService.save(customer);
        return "redirect:/customers";
    }

    // UPDATE SUBMIT -> POST /customers/{id}
    // (stays POST, so you don't need _method=put)
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
            @ModelAttribute("customer") Customer formCustomer) {
        customerService.update(id, formCustomer);
        return "redirect:/customers";
    }
}
