package fr.enst.budgetapp;

public class Category {
    private String name;
    private int color;
    private int iconResId; // resource ID for the icon

    public Category(String name, int color, int iconResId) {
        this.name = name;
        this.color = color;
        this.iconResId = iconResId;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public int getIconResId() {
        return iconResId;
    }
}

