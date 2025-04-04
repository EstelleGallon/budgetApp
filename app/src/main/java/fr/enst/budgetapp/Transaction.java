package fr.enst.budgetapp;

import static java.lang.Double.parseDouble;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Transaction {

    private final int id;

    private final String transactionType;
    private final String categoryName;
    private final String moneyAmount;
    private final String transactionDate;

    public Transaction(int id, String transactionType, String categoryName, String moneyAmount, String transactionDate) {
        this.id = id;
        this.transactionType = transactionType;
        this.categoryName = categoryName;
        this.moneyAmount = moneyAmount;
        this.transactionDate = transactionDate;
    }

    public int getId(){return id;}

    public String getTransactionType(){return transactionType;}
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

            if (transaction.getTransactionType().equalsIgnoreCase("Income")) {
                amount += transaction.getMoneyAmountDouble();
            } else {
                amount -= transaction.getMoneyAmountDouble();
            }

        }
        //return Double.toString(amount).replace(".", ",") + "€";
        return String.format(java.util.Locale.FRANCE, "%.2f€", amount).replace(".", ",");

    }

    public static HashMap<String, List<Transaction>> groupTransactionsByDate(List<Transaction> transactions) {
        HashMap<String, List<Transaction>> transactionsByDate = new HashMap<>();

        for (Transaction transaction : transactions)
        {
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

            if (Objects.equals(transaction.getYearAndMonth(), yearAndMonth)) {
                filteredTransactions.add(transaction);
            }
        }
        return filteredTransactions;
    }

    public static String getWeekday(String initialDate) throws ParseException {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = originalFormat.parse(initialDate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        //int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String weekdayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, java.util.Locale.getDefault());

        // Get the day of the month
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        System.out.println(weekdayName + ", " + dayOfMonth);

        return weekdayName + ", " + dayOfMonth;
    }
}
