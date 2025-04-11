package fr.enst.budgetapp.ui.budget;

import static fr.enst.budgetapp.JsonLoader.evaluateExpenseLimits;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.CategoryValueDataEntry;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Align;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.LegendLayout;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import fr.enst.budgetapp.Balances;
import fr.enst.budgetapp.ExpenseLimit;
import fr.enst.budgetapp.ExpenseLimitAdapter;
import fr.enst.budgetapp.JsonLoader;
import fr.enst.budgetapp.R;
import fr.enst.budgetapp.SavingGoal;
import fr.enst.budgetapp.SavingGoalAdapter;
import fr.enst.budgetapp.Transaction;
import fr.enst.budgetapp.databinding.FragmentBudgetBinding;

public class budgetFragment extends Fragment {

    private FragmentBudgetBinding binding;
    private AnyChartView barChart;

    private Cartesian cartesian;
    private Column col1;
    private Column col2;


    private Calendar calendar = Calendar.getInstance();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        budgetViewModel budgetViewModel =
                new ViewModelProvider(this).get(budgetViewModel.class);

        binding = FragmentBudgetBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        ImageView btnAddSavingGoal = root.findViewById(R.id.btnAddSavingGoal);
        btnAddSavingGoal.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_budgetFragment_to_newSavingGoalFragment);
        });

        ImageView btnAddExpenseLimit = root.findViewById(R.id.btnAddExpenseLimit);
        btnAddExpenseLimit.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_budgetFragment_to_newExpenseLimitFragment);
        });



        /*
        // Sample data for saving goals TODO: use actual data
        List<SavingGoal> savingGoals = Arrays.asList(
                new SavingGoal("1","Vacation Fund", 50, "2023-12-31", 1000),
                new SavingGoal("2", "Emergency Fund", 30, "2024-06-30", 5000),
                new SavingGoal("3", "New Car", 70, "2025-01-01", 20000)
        );

         */



        Balances balances = JsonLoader.loadBalances(getContext());
        double savingsBalance = balances.savings;

        List<SavingGoal> savingGoals = JsonLoader.loadSavingGoals(getContext());

        for (SavingGoal goal : savingGoals) {
            int percentage = (int) Math.min((savingsBalance / goal.getTotalAmount()) * 100, 100);
            goal.setProgressPercentage(percentage);
        }


        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewSavingGoals);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        SavingGoalAdapter adapter = new SavingGoalAdapter(savingGoals);
        recyclerView.setAdapter(adapter);


        /*
        // Sample data for expense limits TODO: use actual data
        List<ExpenseLimit> expenseLimits = Arrays.asList(
                new ExpenseLimit("Food", new boolean[]{true, false, false, true, false, false, true, false, false, true, false, false}),
                new ExpenseLimit("Transport", new boolean[]{false, false, true, false, false, true, false, false, true, false, false, true}),
                new ExpenseLimit("Entertainment", new boolean[]{false, true, false, false, true, false, false, true, false, false, true, false})
        );

         */


        List<Transaction> transactions = JsonLoader.loadTransactions(getContext());
        List<ExpenseLimit> expenseLimits = JsonLoader.loadExpenseLimits(getContext());
        Log.d("EXPENSE LIMIT", String.valueOf(expenseLimits.size()));

        evaluateExpenseLimits(getContext(), expenseLimits, transactions);


        RecyclerView recyclerViewExpenseLimits = root.findViewById(R.id.recyclerViewExpenseLimits);
        recyclerViewExpenseLimits.setLayoutManager(new LinearLayoutManager(getContext()));
        ExpenseLimitAdapter expenseLimitAdapter = new ExpenseLimitAdapter(expenseLimits, getContext());
        recyclerViewExpenseLimits.setAdapter(expenseLimitAdapter);


        recyclerViewExpenseLimits.setNestedScrollingEnabled(false);

        barChart = root.findViewById(R.id.chartExpenseLimit);

        APIlib.getInstance().setActiveAnyChartView(barChart);

        TextView tvMonthYear = root.findViewById(R.id.tvMonthYear);
        updateMonthYear(tvMonthYear, calendar);


        List<DataEntry> spendingLimitData = new ArrayList<>();
        List<DataEntry> actualSpentData = new ArrayList<>();

        List<ExpenseLimit> limits = JsonLoader.loadExpenseLimits(getContext());

        Date selectedMonthStart = getMonthStart(calendar);
        Date selectedMonthEnd = getMonthEnd(calendar);

        Map<String, Double> spentPerCategory = new HashMap<>();
        Map<String, Double> limitPerCategory = new HashMap<>();

        for (ExpenseLimit limit : limits) {
            List<Pair<Calendar, Calendar>> periods;
            try {
                periods = JsonLoader.getApplicablePeriods(limit, calendar.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
                continue;
            }

            for (Pair<Calendar, Calendar> window : periods) {
                Date windowStart = window.first.getTime();
                Date windowEnd = window.second.getTime();

                Date today = normalizeDate(new Date());
                Calendar currentMonth = Calendar.getInstance();
                currentMonth.setTime(today);

                boolean isCurrentMonth = currentMonth.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                        currentMonth.get(Calendar.MONTH) == calendar.get(Calendar.MONTH);


                if (!(windowEnd.before(selectedMonthStart) || windowStart.after(selectedMonthEnd)) &&
                         (!isCurrentMonth || !windowStart.after(today)))
                {

                    double totalSpent = 0.0;

                    for (Transaction tx : transactions) {
                        Date txDate = tx.getDate();
                        if (isCurrentMonth && txDate.after(today)) {
                            Log.d("DEBUG_SKIP_TX", "Skipping future transaction in current month: " + txDate);
                            continue;
                        }
                        if (txDate.before(selectedMonthStart) || txDate.after(selectedMonthEnd)) continue;
                        if (!"Spending".equalsIgnoreCase(tx.getTransactionType())) continue;
                        if (!tx.getCategoryName().equalsIgnoreCase(limit.getCategoryName())) continue;


                        if (txDate != null && !txDate.before(windowStart) && !txDate.after(windowEnd)) {
                            totalSpent += tx.getMoneyAmountDouble();
                            Log.d("TOTAL SPENT", String.valueOf(totalSpent));
                        }
                    }

                    String category = limit.getCategoryName();
                    spentPerCategory.put(category,
                            spentPerCategory.getOrDefault(category, 0.0) + totalSpent);
                    limitPerCategory.put(category,
                            limitPerCategory.getOrDefault(category, 0.0) + limit.getAmount());
                }
            }
        }

        Set<String> allCategories = new HashSet<>();
        allCategories.addAll(limitPerCategory.keySet());
        allCategories.addAll(spentPerCategory.keySet());

        for (String category : allCategories) {
            double spent = spentPerCategory.getOrDefault(category, 0.0);
            double limit = limitPerCategory.getOrDefault(category, 0.0);

            spendingLimitData.add(new CustomDataEntry(category, limit,"#FF5733"));
            actualSpentData.add(new CustomDataEntry(category, spent, "#33FF57"));

        }


        if (spendingLimitData.isEmpty()) {
            spendingLimitData.add(new ValueDataEntry("No Data", 0));
            actualSpentData.add(new CustomDataEntry("No Data", 0, "#FFFFFF"));
        }


        cartesian = AnyChart.column();
        col1 = cartesian.column(spendingLimitData);
        col1.name("Spending Limit").color("#FF5733");

        col2 = cartesian.column(actualSpentData);
        col2.name("Actual Spent").color("#33FF57");

        col1.legendItem()
                .iconFill()
                ;

        col2.legendItem()
                .iconFill()
                ;

        barChart.setChart(cartesian);

        initializeBarChart();

        ImageButton btnPrevMonth = root.findViewById(R.id.btnPrevMonth);
        ImageButton btnNextMonth = root.findViewById(R.id.btnNextMonth);

        btnPrevMonth.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1);
            updateMonthYear(tvMonthYear, calendar);
            APIlib.getInstance().setActiveAnyChartView(barChart);
            updateBarChartData();
        });

        btnNextMonth.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1);
            updateMonthYear(tvMonthYear, calendar);
            APIlib.getInstance().setActiveAnyChartView(barChart);
            updateBarChartData();
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateMonthYear(TextView textView, Calendar calendar) {
        String monthYear = android.text.format.DateFormat.format("MMMM yyyy", calendar).toString();
        textView.setText(monthYear);
    }

    private void updateBarChartData() {
        List<DataEntry> spendingLimitData = new ArrayList<>();
        List<DataEntry> actualSpentData = new ArrayList<>();

        col1.data(new ArrayList<>());
        col2.data(new ArrayList<>());


        List<ExpenseLimit> limits = JsonLoader.loadExpenseLimits(getContext());
        List<Transaction> transactions = JsonLoader.loadTransactions(getContext());

        Date selectedMonthStart = getMonthStart(calendar);
        Date selectedMonthEnd = getMonthEnd(calendar);

        Map<String, Double> spentPerCategory = new HashMap<>();
        Map<String, Double> limitPerCategory = new HashMap<>();

        for (ExpenseLimit limit : limits) {
            List<Pair<Calendar, Calendar>> periods;
            try {
                periods = JsonLoader.getApplicablePeriods(limit, calendar.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
                continue;
            }

            for (Pair<Calendar, Calendar> window : periods) {
                Date windowStart = window.first.getTime();
                Date windowEnd = window.second.getTime();

                Log.d("DEBUG_WINDOW", "Limit " + limit.getCategoryName() +
                        " window: " + windowStart + " to " + windowEnd +
                        " | Selected month: " + selectedMonthStart + " to " + selectedMonthEnd);


                Calendar nextMonthStart = Calendar.getInstance();
                nextMonthStart.setTime(selectedMonthStart);
                nextMonthStart.add(Calendar.MONTH, 1);

                Date today = normalizeDate(new Date());

                Calendar currentMonth = Calendar.getInstance();
                currentMonth.setTime(today);

                boolean isCurrentMonth = currentMonth.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                        && currentMonth.get(Calendar.MONTH) == calendar.get(Calendar.MONTH);

                //if (!windowEnd.before(selectedMonthStart) && windowEnd.before(nextMonthStart.getTime())){
                if (!(windowEnd.before(selectedMonthStart) || windowStart.after(selectedMonthEnd))){

                    double totalSpent = 0.0;



                    for (Transaction tx : transactions) {
                        Date txDate = tx.getDate();

                        if (txDate.before(selectedMonthStart) || txDate.after(selectedMonthEnd)) continue;


                        if (!"Spending".equalsIgnoreCase(tx.getTransactionType())) continue;
                        if (!tx.getCategoryName().equalsIgnoreCase(limit.getCategoryName())) continue;

                        Log.d("STAYED IN THE LOOP", "STAYED");


                        if (txDate != null) {
                            txDate = normalizeDate(txDate);
                            Date normStart = normalizeDate(windowStart);
                            Date normEnd = normalizeDate(windowEnd);

                            if (!windowEnd.before(selectedMonthStart) && windowEnd.compareTo(nextMonthStart.getTime()) <= 0) {
                                if (isCurrentMonth && windowEnd.after(today)) {
                                    Log.d("DEBUG_SKIP", "Skipping window ending after today in current month: " + windowEnd);
                                    continue;
                                    }

                                if (isCurrentMonth && txDate.after(today)) {
                                    Log.d("DEBUG_SKIP_TX", "Skipping future transaction in current month: " + txDate);
                                    continue;
                                }

                                totalSpent += tx.getMoneyAmountDouble();
                                Log.d("TX CONTRIBUTION", String.valueOf(tx.getMoneyAmountDouble()));
                                Log.d("TOTAL SPENT", String.valueOf(totalSpent));
                                Log.d("DEBUG_TX", "txDate: " + txDate + ", windowStart: " + normStart + ", windowEnd: " + normEnd);
                            }
                        }


                    }

                    String category = limit.getCategoryName();
                    spentPerCategory.put(category,
                            spentPerCategory.getOrDefault(category, 0.0) + totalSpent);
                    limitPerCategory.put(category,
                            limitPerCategory.getOrDefault(category, 0.0) + limit.getAmount());
                }
            }
        }

        /*
        for (String category : limitPerCategory.keySet()) {
            double spent = spentPerCategory.getOrDefault(category, 0.0);
            double limit = limitPerCategory.get(category);
            String barColor = (spent > limit) ?"#FF5733" : "#33FF57";



            Log.d("DEBUG_DATA", "Category: " + category + " -> spent: " + spent + ", limit: " + limit);
            spendingLimitData.add(new CustomDataEntry(category, limit,"#FF5733"));
            actualSpentData.add(new CustomDataEntry(category, spent, "#33FF57"));

        }

         */

        Set<String> allCategories = new HashSet<>();
        allCategories.addAll(limitPerCategory.keySet());
        allCategories.addAll(spentPerCategory.keySet());

        for (String category : allCategories) {
            double spent = spentPerCategory.getOrDefault(category, 0.0);
            double limit = limitPerCategory.getOrDefault(category, 0.0);

            spendingLimitData.add(new CustomDataEntry(category, limit,"#FF5733"));
            actualSpentData.add(new CustomDataEntry(category, spent, "#33FF57"));

        }


        if (spendingLimitData.isEmpty()) {
            spendingLimitData.add(new ValueDataEntry("No Data", 0));
            actualSpentData.add(new CustomDataEntry("No Data", 0, "#FFFFFF"));
        }


        col1.data(spendingLimitData);
        col2.data(actualSpentData);
    }




    public static List<Pair<Date, Date>> generateRepeatedWindows(String startDateStr, String endDateStr, String frequency) {
        List<Pair<Date, Date>> windows = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            Date originalStart = sdf.parse(startDateStr);
            Date originalEnd = sdf.parse(endDateStr);

            long durationMs = originalEnd.getTime() - originalStart.getTime();

            Calendar currentStart = Calendar.getInstance();
            currentStart.setTime(originalStart);


            Calendar oneYearLater = Calendar.getInstance();
            oneYearLater.setTime(originalStart);
            oneYearLater.add(Calendar.YEAR, 1);

            while (currentStart.getTime().before(oneYearLater.getTime())) {
                Date start = currentStart.getTime();
                Date end = new Date(start.getTime() + durationMs);
                windows.add(new Pair<>(start, end));

                if (frequency.equalsIgnoreCase("Monthly")) {
                    currentStart.add(Calendar.MONTH, 1);
                } else if (frequency.equalsIgnoreCase("Weekly")) {
                    currentStart.add(Calendar.WEEK_OF_YEAR, 1);
                } else {
                    break;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return windows;
    }


    private Date getMonthStart(Calendar cal) {
        Calendar copy = (Calendar) cal.clone();
        copy.set(Calendar.DAY_OF_MONTH, 1);
        copy.set(Calendar.HOUR_OF_DAY, 0);
        copy.set(Calendar.MINUTE, 0);
        copy.set(Calendar.SECOND, 0);
        copy.set(Calendar.MILLISECOND, 0);
        return copy.getTime();
    }

    private Date getMonthEnd(Calendar cal) {
        Calendar copy = (Calendar) cal.clone();
        copy.set(Calendar.DAY_OF_MONTH, copy.getActualMaximum(Calendar.DAY_OF_MONTH));
        copy.set(Calendar.HOUR_OF_DAY, 23);
        copy.set(Calendar.MINUTE, 59);
        copy.set(Calendar.SECOND, 59);
        copy.set(Calendar.MILLISECOND, 999);
        return copy.getTime();
    }

    private int getMonthIndex(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(dateString);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal.get(Calendar.MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }




    private static class CustomDataEntry extends ValueDataEntry {
        CustomDataEntry(String x, Number value, String color) {
            super(x, value);
            setValue("fill", color);
        }

    }


    private void initializeBarChart() {
        cartesian.title("Expenses per Category");
        cartesian.tooltip()
                .positionMode(TooltipPositionMode.POINT)
                .anchor(Anchor.CENTER_BOTTOM)
                .position(Position.CENTER_BOTTOM)
                .format("€{%Value}");
        cartesian.yAxis(0).labels().format("€{%Value}");
        cartesian.interactivity().hoverMode(HoverMode.BY_X);
        cartesian.animation(true);
        cartesian.legend()
                .enabled(true)
                .position("top")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);
    }


    private Date normalizeDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }








}