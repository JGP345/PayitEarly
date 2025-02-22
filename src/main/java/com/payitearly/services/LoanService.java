package com.payitearly.services;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class LoanService {
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public double calculateMonthlyPayment(double principal, double interestRate, int monthsLeft) {
        double monthlyRate = interestRate / 100 / 12;
        return Double.parseDouble(df.format((principal * monthlyRate) / (1 - Math.pow(1 + monthlyRate, -monthsLeft))));
    }

    public double calculateInterestSaved(double principal, double interestRate, int monthsLeft, double extraPayment, double customMinPayment) {
        double originalTotalInterest = computeTotalInterest(principal, interestRate, monthsLeft, 0, customMinPayment);
        double newTotalInterest = computeTotalInterest(principal, interestRate, monthsLeft, extraPayment, customMinPayment);
        return Double.parseDouble(df.format(originalTotalInterest - newTotalInterest));
    }

    public int calculatePayoffMonths(double principal, double interestRate, int monthsLeft, double extraPayment, double customMinPayment) {
        double monthlyRate = interestRate / 100 / 12;
        double payment = (customMinPayment > 0 ? customMinPayment : calculateMonthlyPayment(principal, interestRate, monthsLeft)) + extraPayment;
        int months = 0;
        while (principal > 0 && months < monthsLeft * 2) { // Safety cap
            double interest = principal * monthlyRate;
            principal = principal + interest - payment;
            months++;
        }
        return principal <= 0 ? months : monthsLeft;
    }

    public double[] calculateWeeklyBiweeklySavings(double principal, double interestRate, int monthsLeft, double extraPayment, double customMinPayment) {
        double monthlyRate = interestRate / 100 / 12;
        double basePayment = customMinPayment > 0 ? customMinPayment : calculateMonthlyPayment(principal, interestRate, monthsLeft);

        // Monthly: 1 payment per month
        double monthlyInterestSaved = calculateInterestSaved(principal, interestRate, monthsLeft, extraPayment, customMinPayment);

        // Biweekly: 26 payments per year (approx 2 per month)
        double biweeklyPayment = (basePayment + extraPayment) / 2;
        double biweeklyTotalInterest = computeTotalInterestWithFrequency(principal, monthlyRate, monthsLeft * 12 / 26, biweeklyPayment, 26.0 / 12);
        double originalBiweeklyInterest = computeTotalInterestWithFrequency(principal, monthlyRate, monthsLeft * 12 / 26, basePayment / 2, 26.0 / 12);
        double biweeklyInterestSaved = originalBiweeklyInterest - biweeklyTotalInterest;

        // Weekly: 52 payments per year (approx 4 per month)
        double weeklyPayment = (basePayment + extraPayment) / 4;
        double weeklyTotalInterest = computeTotalInterestWithFrequency(principal, monthlyRate, monthsLeft * 12 / 52, weeklyPayment, 52.0 / 12);
        double originalWeeklyInterest = computeTotalInterestWithFrequency(principal, monthlyRate, monthsLeft * 12 / 52, basePayment / 4, 52.0 / 12);
        double weeklyInterestSaved = originalWeeklyInterest - weeklyTotalInterest;

        return new double[] { monthlyInterestSaved, biweeklyInterestSaved, weeklyInterestSaved };
    }

    public List<double[]> calculateIncrementalSavings(double principal, double interestRate, int monthsLeft, double basePayment, double customMinPayment) {
        List<double[]> savings = new ArrayList<>();
        double increment = basePayment < 50 ? 10 : 100;
        for (int i = 1; i <= 5; i++) {
            double extra = increment * i;
            double interestSaved = calculateInterestSaved(principal, interestRate, monthsLeft, extra, customMinPayment);
            savings.add(new double[] { extra, interestSaved });
        }
        return savings;
    }

    private double computeTotalInterest(double principal, double interestRate, int monthsLeft, double extraPayment, double customMinPayment) {
        double totalInterest = 0;
        double monthlyRate = interestRate / 100 / 12;
        double payment = (customMinPayment > 0 ? customMinPayment : calculateMonthlyPayment(principal, interestRate, monthsLeft)) + extraPayment;

        for (int i = 0; i < monthsLeft && principal > 0; i++) {
            double interest = principal * monthlyRate;
            totalInterest += interest;
            double principalPayment = payment - interest;
            principal -= principalPayment;
        }
        return totalInterest > 0 ? totalInterest : 0;
    }

    private double computeTotalInterestWithFrequency(double principal, double monthlyRate, int periods, double paymentPerPeriod, double periodsPerMonth) {
        double totalInterest = 0;
        double periodRate = monthlyRate / periodsPerMonth; // Adjust interest rate for payment frequency

        for (int i = 0; i < periods && principal > 0; i++) {
            double interest = principal * periodRate;
            totalInterest += interest;
            principal = principal + interest - paymentPerPeriod;
        }
        return totalInterest > 0 ? totalInterest : 0;
    }
}