package fr.enst.budgetapp;


public class ExpenseLimit {
    private final String id;
    private final String categoryName;
    private final double amount;
    private final String startDate;
    private final String endDate;
    private final String repeatFrequency;
    private final String notes;
    private final Boolean[] exceededMonths;


    public ExpenseLimit(String id, String categoryName, double amount, String startDate, String endDate, String repeatFrequency, String notes, Boolean[] exceededMonths) {
        this.id = id;
        this.categoryName = categoryName;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.repeatFrequency = repeatFrequency;
        this.notes = notes;
        this.exceededMonths = exceededMonths;
    }

    public String getId() {
        return id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public double getAmount() {
        return amount;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getRepeatFrequency() {
        return repeatFrequency;
    }

    public String getNotes() {
        return notes;
    }

    public Boolean[] getExceededMonths() {
        return exceededMonths;
    }


    public boolean isExceeded(int monthIndex) {
        if (exceededMonths == null || monthIndex < 0 || monthIndex >= exceededMonths.length)
            return false;

        Boolean value = exceededMonths[monthIndex];
        return value != null && value;
    }


}
