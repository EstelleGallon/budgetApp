package fr.enst.budgetapp;

import static java.security.AccessController.getContext;

import static fr.enst.budgetapp.ui.budget.budgetFragment.generateRepeatedWindows;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import java.io.OutputStreamWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Locale;


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


    /*
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

     */

    public static List<Transaction> loadTransactions(Context context) {
        Gson gson = new Gson();
        Type transactionListType = new TypeToken<List<Transaction>>() {}.getType();
        File file = new File(context.getFilesDir(), "transactions.json");

        try {
            // Step 1: If file doesn't exist in internal storage, copy from assets
            if (!file.exists()) {
                InputStream assetStream = context.getAssets().open("transactions.json");
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = assetStream.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
                fos.close();
                assetStream.close();
                Log.d("JsonLoader", "transactions.json copied from assets to internal storage.");
            }

            // Step 2: Load from internal storage
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            return gson.fromJson(reader, transactionListType);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
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





    public static void saveSavingGoals(Context context, List<SavingGoal> goals) {
        Gson gson = new Gson();
        String json = gson.toJson(goals);

        try (FileOutputStream fos = context.openFileOutput("savings_goals.json", Context.MODE_PRIVATE)) {
            fos.write(json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<SavingGoal> loadSavingGoals(Context context) {
        File file = new File(context.getFilesDir(), "savings_goals.json");

        if (file.exists()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                String json = baos.toString(StandardCharsets.UTF_8.name());
                Type listType = new TypeToken<List<SavingGoal>>() {}.getType();
                return new Gson().fromJson(json, listType);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            InputStream is = context.getAssets().open("savings_goals.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, StandardCharsets.UTF_8);
            Type listType = new TypeToken<List<SavingGoal>>() {}.getType();
            return new Gson().fromJson(json, listType);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }









    public static List<ExpenseLimit> loadExpenseLimits(Context context) {
        File file = new File(context.getFilesDir(), "expense_limits.json");

        if (file.exists()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                String json = baos.toString(StandardCharsets.UTF_8.name());
                Type listType = new TypeToken<List<ExpenseLimit>>() {}.getType();
                return new Gson().fromJson(json, listType);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            InputStream is = context.getAssets().open("expense_limits.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, StandardCharsets.UTF_8);
            Type listType = new TypeToken<List<ExpenseLimit>>() {}.getType();
            return new Gson().fromJson(json, listType);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }




    public static void saveExpenseLimits(Context context, List<ExpenseLimit> limits) {
        File file = new File(context.getFilesDir(), "expense_limits.json");
        try {
            FileWriter writer = new FileWriter(file);
            new Gson().toJson(limits, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getNextExpenseLimitId(Context context) {
        List<ExpenseLimit> existing = loadExpenseLimits(context);
        return existing.size() + 1;
    }




    public static void evaluateExpenseLimits(Context context, List<ExpenseLimit> limits, List<Transaction> transactions) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date today = new Date();

        for (ExpenseLimit limit : limits) {
            Boolean[] exceeded = new Boolean[12];
            Arrays.fill(exceeded, null);

            List<Pair<Calendar, Calendar>> periods;
            try {
                periods = getApplicablePeriods(limit, today);
            } catch (ParseException e) {
                e.printStackTrace();
                continue;
            }

            for (Pair<Calendar, Calendar> period : periods) {
                Calendar start = period.first;
                Calendar end = period.second;
                Log.d("START DATE", String.valueOf(start));
                Log.d("END DATE", String.valueOf(end));

                if (start.getTime().after(today)) continue;

                double spent = 0.0;

                for (Transaction tx : transactions) {
                    if (!tx.getTransactionType().equalsIgnoreCase("Spending")) continue;
                    if (!tx.getCategoryName().equalsIgnoreCase(limit.getCategoryName())) continue;

                    Log.d("VERIFIES CONDITION", "True");
                    Date txDate = tx.getDate();
                    Log.d("DATE TRANSACTION", String.valueOf(txDate));
                    if (txDate == null) continue;

                    if (!txDate.before(start.getTime()) && !txDate.after(end.getTime())){
                        Log.d("INCLUDED TX", String.valueOf(tx.getMoneyAmountDouble()));
                        spent += tx.getMoneyAmountDouble();
                        Log.d("SPENT", String.valueOf(spent));
                    }
                }

                int month = start.get(Calendar.MONTH);
                exceeded[month] = spent > limit.getAmount();

                Log.d("LIMIT_DEBUG", "Window: " + sdf.format(start.getTime()) + " to " + sdf.format(end.getTime()));
                Log.d("LIMIT_DEBUG", "Spent: " + spent + ", Limit: " + limit.getAmount());
            }

            limit.setExceededMonths(exceeded);
        }

        JsonLoader.saveExpenseLimits(context, limits);
    }




    public static List<Pair<Calendar, Calendar>> getApplicablePeriods(ExpenseLimit limit, Date today) throws ParseException {
        List<Pair<Calendar, Calendar>> periods = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        Calendar start = Calendar.getInstance();
        start.setTime(sdf.parse(limit.getStartDate()));

        Calendar endDate = Calendar.getInstance();
        endDate.setTime(sdf.parse(limit.getEndDate()));

        if (start == null || endDate == null) return periods;

        Calendar pointer = (Calendar) start.clone();

        switch (limit.getRepeatFrequency()) {
            case "Never repeat":
                if (!pointer.after(today)) {
                    Calendar periodEnd = (Calendar) endDate.clone();
                    periods.add(new Pair<>((Calendar) pointer.clone(), periodEnd));
                    Log.d("NEVER REPEAT TX", limit.getCategoryName());
                }
                break;

            case "Monthly":
                while (!pointer.after(endDate) && !pointer.after(today)) {
                    Calendar periodStart = (Calendar) pointer.clone();
                    Calendar periodEnd = (Calendar) pointer.clone();
                    periodEnd.add(Calendar.MONTH, 1);
                    periods.add(new Pair<>(periodStart, periodEnd));
                    pointer.add(Calendar.MONTH, 1);
                    Log.d("MONTHLY REPEAT TX", limit.getCategoryName());
                }
                break;

            case "Weekly":
                while (!pointer.after(endDate) && !pointer.after(today)) {
                    Calendar periodStart = (Calendar) pointer.clone();
                    Calendar periodEnd = (Calendar) pointer.clone();
                    periodEnd.add(Calendar.WEEK_OF_YEAR, 1);
                    periods.add(new Pair<>(periodStart, periodEnd));
                    pointer.add(Calendar.WEEK_OF_YEAR, 1);
                    Log.d("WEEKLY REPEAT TX", limit.getCategoryName());
                }
                break;
        }

        return periods;
    }



    public static void refreshExpenseLimitStatus(Context context) {
        List<Transaction> transactions = JsonLoader.loadTransactions(context);
        List<ExpenseLimit> limits = JsonLoader.loadExpenseLimits(context);
        evaluateExpenseLimits(context, limits, transactions);
    }








}
