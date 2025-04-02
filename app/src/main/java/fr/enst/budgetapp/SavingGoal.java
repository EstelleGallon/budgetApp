package fr.enst.budgetapp;

public class SavingGoal {
    private final String id;
    private final String title;
    private final int progressPercentage;
    private final String deadline;
    private final int totalAmount;

    public SavingGoal(String id, String title, int progressPercentage, String deadline, int totalAmount) {
        this.id = id;
        this.title = title;
        this.progressPercentage = progressPercentage;
        this.deadline = deadline;
        this.totalAmount = totalAmount;
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
}