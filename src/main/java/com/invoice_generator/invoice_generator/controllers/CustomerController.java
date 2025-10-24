package com.invoice_generator.invoice_generator.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.invoice_generator.invoice_generator.models.Customer;
import com.invoice_generator.invoice_generator.services.CustomerService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("customers", customerService.findAll());
        return "customers";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("customer", new Customer());
        model.addAttribute("isEdit", false);
        return "customer-form";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Customer existing = customerService.findByIdOrThrow(id);
        model.addAttribute("customer", existing);
        model.addAttribute("isEdit", true);
        return "customer-form";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            customerService.delete(id);
            ra.addFlashAttribute("toast", "Customer deleted.");
            return "redirect:/customers?deleted=1";
        } catch (IllegalStateException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/customers?deleteError=1";
        }
    }

    @PostMapping
    public String create(@ModelAttribute("customer") Customer customer) {
        customerService.save(customer);
        return "redirect:/customers";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
            @ModelAttribute("customer") Customer formCustomer) {
        customerService.update(id, formCustomer);
        return "redirect:/customers";
    }
}
