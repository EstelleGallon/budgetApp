package fr.enst.budgetapp;

public class ExpenseLimit {
    private final String categoryName;
    private final boolean[] exceededMonths;

    public ExpenseLimit(String categoryName, boolean[] exceededMonths) {
        this.categoryName = categoryName;
        this.exceededMonths = exceededMonths;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public boolean isExceeded(int monthIndex) {
        return exceededMonths[monthIndex];
    }
}