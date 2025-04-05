package fr.enst.budgetapp;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.graphics.Color;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import java.io.OutputStreamWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;


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
            InputStream internalStream = context.openFileInput("transactions.json");
            InputStreamReader internalReader = new InputStreamReader(internalStream);
            return gson.fromJson(internalReader, transactionListType);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            InputStream assetStream = context.getAssets().open("transactions.json");
            InputStreamReader assetReader = new InputStreamReader(assetStream);
            return gson.fromJson(assetReader, transactionListType);

        } catch (Exception e) {
            e.printStackTrace();
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





    public static List<Category> loadCategories(Context context) {
        List<Category> categories = new ArrayList<>();
        File file = new File(context.getFilesDir(), "categories.json");

        try {
            InputStream is;
            if (file.exists()) {
                is = new FileInputStream(file);
            } else {
                is = context.getAssets().open("categories.json");
                FileOutputStream os = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.close();
                is = new FileInputStream(file);
            }

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String name = obj.getString("name");
                String colorString = obj.getString("color");
                String iconName = obj.getString("iconResId");

                int color = Color.parseColor(colorString);
                int iconResId = context.getResources().getIdentifier(iconName, "drawable", context.getPackageName());

                categories.add(new Category(name, color, iconResId));
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return categories;
    }


    public static boolean saveCategories(Context context, List<Category> categories) {
        JSONArray jsonArray = new JSONArray();

        for (Category category : categories) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("name", category.getName());
                obj.put("color", String.format("#%06X", (0xFFFFFF & category.getColor())));
                obj.put("iconResId", context.getResources().getResourceEntryName(category.getIconResId()));
                jsonArray.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        try (FileOutputStream fos = context.openFileOutput("categories.json", Context.MODE_PRIVATE)) {
            fos.write(jsonArray.toString().getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }






}
