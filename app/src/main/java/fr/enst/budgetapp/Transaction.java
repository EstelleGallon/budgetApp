package fr.enst.budgetapp;

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
}
