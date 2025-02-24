package com.payitearly.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.payitearly.services.LoanService;

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
            @RequestParam(value = "extraPayment", defaultValue = "0") double extraPayment,
            @RequestParam(value = "customMinPayment", defaultValue = "0") double customMinPayment,
            Model model) {

        if (extraPayment == 0) {
            extraPayment = principal * 0.05;
        }

        double monthlyPayment = loanService.calculateMonthlyPayment(principal, interestRate, monthsLeft);
        double effectiveMinPayment = customMinPayment > 0 ? customMinPayment : monthlyPayment;
        double newMonthlyPayment = effectiveMinPayment + extraPayment;
        int originalPayoffMonths = loanService.calculatePayoffMonths(principal, interestRate, monthsLeft, 0, customMinPayment);
        int payoffMonths = loanService.calculatePayoffMonths(principal, interestRate, monthsLeft, extraPayment, customMinPayment);
        double totalInterestSaved = loanService.calculateInterestSaved(principal, interestRate, monthsLeft, extraPayment, customMinPayment);
        double originalTotalCost = principal + loanService.computeTotalInterest(principal, interestRate, originalPayoffMonths, 0, customMinPayment);
        double newTotalCost = principal + loanService.computeTotalInterest(principal, interestRate, payoffMonths, extraPayment, customMinPayment);
        double totalMoneySaved = originalTotalCost - newTotalCost;
        double[] savingsBreakdown = loanService.calculatePaymentFrequencySavings(principal, interestRate, monthsLeft, extraPayment, customMinPayment);
        List<double[]> incrementalSavings = loanService.calculateIncrementalSavings(principal, interestRate, monthsLeft, monthlyPayment, customMinPayment);

        model.addAttribute("monthlyPayment", monthlyPayment);
        model.addAttribute("effectiveMinPayment", effectiveMinPayment);
        model.addAttribute("newMonthlyPayment", newMonthlyPayment);
        model.addAttribute("originalPayoffMonths", originalPayoffMonths);
        model.addAttribute("payoffMonths", payoffMonths);
        model.addAttribute("monthsSaved", originalPayoffMonths - payoffMonths);
        model.addAttribute("totalInterestSaved", totalInterestSaved);
        model.addAttribute("totalMoneySaved", totalMoneySaved);
        model.addAttribute("monthlySavings", savingsBreakdown[0]);
        model.addAttribute("biweeklySavings", savingsBreakdown[1]);
        model.addAttribute("weeklySavings", savingsBreakdown[2]);
        model.addAttribute("incrementalSavings", incrementalSavings);

        return "result";
    }
}