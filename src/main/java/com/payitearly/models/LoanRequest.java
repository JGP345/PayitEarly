package com.payitearly.models;

public class LoanRequest {
    private double principal;
    private double interestRate;
    private int monthsLeft;
    private double extraPayment;

    public LoanRequest() {}

    public LoanRequest(double principal, double interestRate, int monthsLeft, double extraPayment) {
        this.principal = principal;
        this.interestRate = interestRate;
        this.monthsLeft = monthsLeft;
        this.extraPayment = extraPayment;
    }

    public double getPrincipal() {
        return principal;
    }

    public void setPrincipal(double principal) {
        this.principal = principal;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public int getMonthsLeft() {
        return monthsLeft;
    }

    public void setMonthsLeft(int monthsLeft) {
        this.monthsLeft = monthsLeft;
    }

    public double getExtraPayment() {
        return extraPayment;
    }

    public void setExtraPayment(double extraPayment) {
        this.extraPayment = extraPayment;
    }
}
