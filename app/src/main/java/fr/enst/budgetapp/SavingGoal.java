package fr.enst.budgetapp;

public class SavingGoal {
    private final String title;
    private final int progressPercentage;
    private final String deadline;
    private final int totalAmount;

    public SavingGoal(String title, int progressPercentage, String deadline, int totalAmount) {
        this.title = title;
        this.progressPercentage = progressPercentage;
        this.deadline = deadline;
        this.totalAmount = totalAmount;
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