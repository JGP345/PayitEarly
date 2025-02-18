package com.payitearly.services;
import org.springframework.stereotype.Service;

@Service
public class LoanService {

    public double calculateMonthlyPayment(double principal, double interestRate, int monthsLeft) {
        double monthlyRate = interestRate / 100 / 12;
        return (principal * monthlyRate) / (1 - Math.pow(1 + monthlyRate, -monthsLeft));
    }

    public double calculateInterestSaved(double principal, double interestRate, int monthsLeft, double extraPayment) {
        double originalTotalInterest = computeTotalInterest(principal, interestRate, monthsLeft, 0);
        double newTotalInterest = computeTotalInterest(principal, interestRate, monthsLeft, extraPayment);
        return originalTotalInterest - newTotalInterest;
    }

    private double computeTotalInterest(double principal, double interestRate, int monthsLeft, double extraPayment) {
        double totalInterest = 0;
        double monthlyRate = interestRate / 100 / 12;
        
        for (int i = 0; i < monthsLeft && principal > 0; i++) {
            double interest = principal * monthlyRate;
            totalInterest += interest;
            double principalPayment = (calculateMonthlyPayment(principal, interestRate, monthsLeft) + extraPayment) - interest;
            principal -= principalPayment;
        }
        return totalInterest;
    }
}