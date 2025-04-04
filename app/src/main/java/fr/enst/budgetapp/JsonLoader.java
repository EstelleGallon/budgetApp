package fr.enst.budgetapp;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import java.io.OutputStreamWriter;

public class JsonLoader {


    /*
    public static List<Transaction> loadTransactions(Context context) {
        try {
            InputStream inputStream = context.getAssets().open("transactions.json");
            InputStreamReader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();

            Type transactionListType = new TypeToken<List<Transaction>>() {}.getType();
            return gson.fromJson(reader, transactionListType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

     */


    public static List<Transaction> loadTransactions(Context context) {
        Gson gson = new Gson();
        Type transactionListType = new TypeToken<List<Transaction>>() {}.getType();

        try {
            // First try loading from internal storage
            InputStream internalStream = context.openFileInput("transactions.json");
            InputStreamReader internalReader = new InputStreamReader(internalStream);
            return gson.fromJson(internalReader, transactionListType);

        } catch (Exception e) {
            e.printStackTrace(); // Optional: log that internal file wasn't found
        }

        try {
            // If no saved file, fall back to assets
            InputStream assetStream = context.getAssets().open("transactions.json");
            InputStreamReader assetReader = new InputStreamReader(assetStream);
            return gson.fromJson(assetReader, transactionListType);

        } catch (Exception e) {
            e.printStackTrace(); // Log asset load failure
            return null;
        }
    }



    public static boolean saveTransactions(Context context, List<Transaction> transactions) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(
                    context.openFileOutput("transactions.json", Context.MODE_PRIVATE)
            );

            Gson gson = new Gson();
            String json = gson.toJson(transactions);
            writer.write(json);
            writer.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }






    public static Balances loadBalances(Context context) {
        Gson gson = new Gson();
        try {
            InputStream internalStream = context.openFileInput("balances.json");
            InputStreamReader reader = new InputStreamReader(internalStream);
            return gson.fromJson(reader, Balances.class);
        } catch (Exception e) {

        }

        try {
            InputStream assetStream = context.getAssets().open("balances.json");
            InputStreamReader reader = new InputStreamReader(assetStream);
            return gson.fromJson(reader, Balances.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new Balances(0, 0);
        }
    }


    public static boolean saveBalances(Context context, Balances balances) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(
                    context.openFileOutput("balances.json", Context.MODE_PRIVATE)
            );
            Gson gson = new Gson();
            writer.write(gson.toJson(balances));
            writer.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }






    public static void recomputeBalancesFromTransactions(Context context) {
        List<Transaction> transactions = loadTransactions(context);
        if (transactions == null) return;

        Balances balances = new Balances(0, 0); // start from 0

        for (Transaction tx : transactions) {
            try {
                double amount = Double.parseDouble(tx.getMoneyAmount().replace("€", "").replace(",", ".").trim());

                if (tx.getTransactionType().equalsIgnoreCase("Spending")) {
                    balances.checking -= amount;
                } else if (tx.getTransactionType().equalsIgnoreCase("Income")) {
                    balances.checking += amount;
                } else if (tx.getTransactionType().equalsIgnoreCase("Savings")) {
                    balances.checking -= amount;
                    balances.savings += amount;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        saveBalances(context, balances);
    }







}
