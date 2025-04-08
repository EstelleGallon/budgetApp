package fr.enst.budgetapp;


public class ExpenseLimit {
    private String id;
    private String categoryName;
    private double amount;
    private String startDate;
    private String endDate;
    private String repeatFrequency;
    private String notes;
    private Boolean[] exceededMonths;


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


    public void setId(String id){this.id = id;}

    public void setCategoryName(String categoryName){this.categoryName = categoryName;}

    public void setAmount(double amount){this.amount = amount;}

    public void setEndDate(String endDate){this.endDate =endDate;}

    public void setRepeatFrequency(String repeatFrequency){this.repeatFrequency =repeatFrequency;}

    public void setNotes(String notes){this.notes = notes;}
    public void setExceededMonths(Boolean[] exceededMonths) {
        this.exceededMonths = exceededMonths;
    }

    public Boolean isExceeded(int monthIndex) {
        if (exceededMonths == null || monthIndex < 0 || monthIndex >= exceededMonths.length)
            return null;

        return exceededMonths[monthIndex];
    }



}
