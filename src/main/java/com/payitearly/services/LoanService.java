package com.payitearly.services;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class LoanService {
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public double calculateMonthlyPayment(double principal, double interestRate, int monthsLeft) {
        if (principal <= 0 || interestRate < 0 || monthsLeft <= 0) {
            throw new IllegalArgumentException("Principal, interest rate, and months must be positive.");
        }
        double monthlyRate = interestRate / 100 / 12;
        double payment = (principal * monthlyRate) / (1 - Math.pow(1 + monthlyRate, -monthsLeft));
        return Double.parseDouble(df.format(payment));
    }

    public double calculateInterestSaved(double principal, double interestRate, int monthsLeft, double extraPayment, double customMinPayment) {
        if (principal <= 0 || interestRate < 0 || monthsLeft <= 0) {
            throw new IllegalArgumentException("Principal, interest rate, and months must be positive.");
        }
        double originalTotalInterest = computeTotalInterest(principal, interestRate, monthsLeft, 0, customMinPayment);
        double newTotalInterest = computeTotalInterest(principal, interestRate, monthsLeft, extraPayment, customMinPayment);
        return Double.parseDouble(df.format(originalTotalInterest - newTotalInterest));
    }

    public int calculatePayoffMonths(double principal, double interestRate, int monthsLeft, double extraPayment, double customMinPayment) {
        if (principal <= 0 || interestRate < 0) {
            throw new IllegalArgumentException("Principal and interest rate must be positive.");
        }
        double dailyRate = interestRate / 100 / 365;
        double payment = (customMinPayment > 0 ? customMinPayment : calculateMonthlyPayment(principal, interestRate, monthsLeft)) + extraPayment;
        if (payment <= 0) {
            throw new IllegalArgumentException("Payment must be positive.");
        }
        int days = 0;
        double balance = principal;
        while (balance > 0 && days < monthsLeft * 31 * 2) { // Safety cap
            double interest = balance * dailyRate;
            balance += interest;
            if (days % 30 == 0) { // Monthly payment
                balance -= payment;
            }
            days++;
        }
        if (balance > 0) {
            return monthsLeft; // Fallback if loan never pays off within safety cap
        }
        return (int) Math.ceil(days / 30.0); // Convert days to months
    }

    public double[] calculatePaymentFrequencySavings(double principal, double interestRate, int monthsLeft, double extraPayment, double customMinPayment) {
        if (principal <= 0 || interestRate < 0 || monthsLeft <= 0) {
            throw new IllegalArgumentException("Principal, interest rate, and months must be positive.");
        }
        double dailyRate = interestRate / 100 / 365;
        double basePayment = customMinPayment > 0 ? customMinPayment : calculateMonthlyPayment(principal, interestRate, monthsLeft);
        double totalPayment = basePayment + extraPayment;

        // Monthly: 1 payment per month
        double monthlyInterest =.computeTotalInterest(principal, interestRate, monthsLeft, extraPayment, customMinPayment);
        double originalMonthlyInterest = computeTotalInterest(principal, interestRate, monthsLeft, 0, customMinPayment);
        double monthlyInterestSaved = originalMonthlyInterest - monthlyInterest;

        // Biweekly: Split monthly payment into 2 biweekly payments
        double biweeklyPayment = totalPayment / 2;
        double biweeklyInterest = computeTotalInterestWithFrequency(principal, dailyRate, monthsLeft * 2, biweeklyPayment, 14); // 14 days
        double originalBiweeklyInterest = computeTotalInterestWithFrequency(principal, dailyRate, monthsLeft * 2, basePayment / 2, 14);
        double biweeklyInterestSaved = originalBiweeklyInterest - biweeklyInterest;

        // Weekly: Split monthly payment into 4 weekly payments
        double weeklyPayment = totalPayment / 4;
        double weeklyInterest = computeTotalInterestWithFrequency(principal, dailyRate, monthsLeft * 4, weeklyPayment, 7); // 7 days
        double originalWeeklyInterest = computeTotalInterestWithFrequency(principal, dailyRate, monthsLeft * 4, basePayment / 4, 7);
        double weeklyInterestSaved = originalWeeklyInterest - weeklyInterest;

        return new double[] { monthlyInterestSaved, biweeklyInterestSaved, weeklyInterestSaved };
    }

    public List<double[]> calculateIncrementalSavings(double principal, double interestRate, int monthsLeft, double basePayment, double customMinPayment) {
        if (principal <= 0 || interestRate < 0 || monthsLeft <= 0) {
            throw new IllegalArgumentException("Principal, interest rate, and months must be positive.");
        }
        List<double[]> savings = new ArrayList<>();
        double increment = basePayment < 50 ? 10 : 100;
        for (int i = 1; i <= 5; i++) {
            double extra = increment * i;
            double interestSaved = calculateInterestSaved(principal, interestRate, monthsLeft, extra, customMinPayment);
            savings.add(new double[] { extra, interestSaved });
        }
        return savings;
    }

    public double computeTotalInterest(double principal, double interestRate, int monthsLeft, double extraPayment, double customMinPayment) {
        if (principal <= 0 || interestRate < 0 || monthsLeft <= 0) {
            throw new IllegalArgumentException("Principal, interest rate, and months must be positive.");
        }
        double totalInterest = 0;
        double dailyRate = interestRate / 100 / 365;
        double payment = (customMinPayment > 0 ? customMinPayment : calculateMonthlyPayment(principal, interestRate, monthsLeft)) + extraPayment;
        double balance = principal;

        for (int day = 0; day < monthsLeft * 30 && balance > 0; day++) {
            double interest = balance * dailyRate;
            totalInterest += interest;
            balance += interest;
            if (day % 30 == 0) { // Monthly payment
                balance -= payment;
            }
        }
        return balance < 0 ? totalInterest + balance : totalInterest; // Adjust for overpayment
    }

    public double computeTotalInterestWithFrequency(double principal, double dailyRate, int periods, double paymentPerPeriod, int daysBetweenPayments) {
        if (principal <= 0 || dailyRate < 0 || periods <= 0) {
            throw new IllegalArgumentException("Principal, rate, and periods must be positive.");
        }
        double totalInterest = 0;
        double balance = principal;

        for (int period = 0; period < periods && balance > 0; period++) {
            for (int day = 0; day < daysBetweenPayments && balance > 0; day++) {
                double interest = balance * dailyRate;
                totalInterest += interest;
                balance += interest;
            }
            balance -= paymentPerPeriod;
        }
        return balance < 0 ? totalInterest + balance : totalInterest; // Adjust for overpayment
    }
}