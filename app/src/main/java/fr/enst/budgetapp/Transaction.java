package fr.enst.budgetapp;

import static java.lang.Double.parseDouble;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Transaction {
    private final String categoryName;
    private final String moneyAmount;
    private final String transactionDate;

    public Transaction(String categoryName, String moneyAmount, String transactionDate) {
        this.categoryName = categoryName;
        this.moneyAmount = moneyAmount;
        this.transactionDate = transactionDate;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getMoneyAmount() {
        return moneyAmount;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public double getMoneyAmountDouble() {
        String digitAmount = moneyAmount.replaceAll("[^\\d,]", "");
        return parseDouble(digitAmount.replace(",", "."));
    }

    public static String sumMoneyAmount(List<Transaction> transactions) {
        double amount = 0;
        for (Transaction transaction : transactions) {
            amount += transaction.getMoneyAmountDouble();
        }
        return Double.toString(amount).replace(".", ",") + "€";
    }

    public static HashMap<String, List<Transaction>> groupTransactionsByDate(List<Transaction> transactions) {
        HashMap<String, List<Transaction>> transactionsByDate = new HashMap<>();
        for (Transaction transaction : transactions) {
            String date = transaction.getTransactionDate();
            if (!transactionsByDate.containsKey(date)) {
                transactionsByDate.put(date, new ArrayList<>());
            }
            transactionsByDate.get(date).add(transaction);
        }
        return transactionsByDate;
    }

    public String getYearAndMonth() {
        if (transactionDate != null && transactionDate.length() >= 7) {
            return transactionDate.substring(0, 7);
        } else {
            return null;
        }
    }

    public static List<Transaction> filterYearAndMonth(List<Transaction> allTransactions, String yearAndMonth) {
        System.out.println("current month: " + yearAndMonth);
        List<Transaction> filteredTransactions = new ArrayList<>();
        for (Transaction transaction : allTransactions) {

            System.out.println("ym: " + transaction.getYearAndMonth());
            if (Objects.equals(transaction.getYearAndMonth(), yearAndMonth)) {
                filteredTransactions.add(transaction);
                System.out.println("ym: " + transaction.getMoneyAmount());
            }
        }
        return filteredTransactions;
    }
}
