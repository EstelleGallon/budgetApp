package fr.enst.budgetapp;

public class SavingGoal {
    private String id;
    private String title;
    private int progressPercentage;
    private String deadline;
    private int totalAmount;

    private final String notes;

    public SavingGoal(String id, String title, int progressPercentage, String deadline, int totalAmount, String notes) {
        this.id = id;
        this.title = title;
        this.progressPercentage = progressPercentage;
        this.deadline = deadline;
        this.totalAmount = totalAmount;
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public String getDeadline() {
        return deadline;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public String getNotes(){ return notes;}

    public void setProgressPercentage(int progressPercentage){this.progressPercentage = progressPercentage;}

    public void setId(String id){this.id = id;}


}