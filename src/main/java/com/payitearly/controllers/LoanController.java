package main.java.com.payitearly.controllers;

import main.java.com.payitearly.services.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoanController {

    @Autowired
    private LoanService loanService;

    @GetMapping("/")
    public String showForm() {
        return "index";
    }

    @PostMapping("/calculate")
    public String calculateLoan(
            @RequestParam("principal") double principal,
            @RequestParam("interestRate") double interestRate,
            @RequestParam("monthsLeft") int monthsLeft,
            @RequestParam("extraPayment") double extraPayment,
            Model model) {

        double monthlyPayment = loanService.calculateMonthlyPayment(principal, interestRate, monthsLeft);
        double newMonthlyPayment = monthlyPayment + extraPayment;
        double totalInterestSaved = loanService.calculateInterestSaved(principal, interestRate, monthsLeft, extraPayment);

        model.addAttribute("monthlyPayment", monthlyPayment);
        model.addAttribute("newMonthlyPayment", newMonthlyPayment);
        model.addAttribute("totalInterestSaved", totalInterestSaved);
        
        return "result";
    }
}