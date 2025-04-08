package fr.enst.budgetapp.ui.overview;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.core.cartesian.series.Line;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.scales.Linear;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

import fr.enst.budgetapp.AccountBalanceAdapter;
import fr.enst.budgetapp.Balances;
import fr.enst.budgetapp.JsonLoader;
import fr.enst.budgetapp.R;
import fr.enst.budgetapp.Transaction;
import fr.enst.budgetapp.TransactionAdapter;
import fr.enst.budgetapp.databinding.FragmentOverviewBinding;
import fr.enst.budgetapp.Category;




public class overviewFragment extends Fragment {

    private FragmentOverviewBinding binding;

    //private Calendar calendarSpendingsPerCategory;
    private Calendar calendarSpendingsPerCategory = Calendar.getInstance();

    private Calendar calendarExpensesVsIncome;

    private TextView tvMonthYearSpendingsPerCategory;
    private TextView tvMonthYearExpensesVsIncome;

    private AnyChartView barChart;
    private Column series1;

    private Cartesian bar;


    private Cartesian line;

    private Line series2;

    private Line series3;
    private AnyChartView lineChart;




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        overviewViewModel overviewViewModel =
                new ViewModelProvider(this).get(overviewViewModel.class);


        binding = FragmentOverviewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize the ViewPager2 for account balances
        ViewPager2 viewPager = root.findViewById(R.id.vpAccountBalances);


        Balances balancesObj = JsonLoader.loadBalances(getContext());

        String checkingFormatted = String.format("%,.2f€", balancesObj.checking);
        String savingsFormatted = String.format("%,.2f€", balancesObj.savings);

        List<Pair<String, String>> balances = Arrays.asList(
                new Pair<>("Checking Account", checkingFormatted),
                new Pair<>("Savings Account", savingsFormatted)
        );




        //The transactions JSON file in the assets folder is the initial setup. Once transactions
        // are added, they are stored on the device's internal storage
        // at: data/data/<your.app.package>/files/transactions.json" that can be accessed from
        //View -> Tool Windows -> Device Explorer
        List<Transaction> transactions = JsonLoader.loadTransactions(getContext());
        if (transactions == null) transactions = new ArrayList<>();




        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar today = Calendar.getInstance();

        List<Transaction> pastOrToday = new ArrayList<>();
        for (Transaction tx : transactions) {
            try {
                Date txDate = sdf.parse(tx.getTransactionDate());
                if (!txDate.after(today.getTime())) {
                    pastOrToday.add(tx);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        Collections.sort(pastOrToday, (t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()));
        List<Transaction> recent = pastOrToday.subList(0, Math.min(3, pastOrToday.size()));





        //Collections.sort(transactions, (t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()));

        //List<Transaction> recent = transactions.subList(0, Math.min(3, transactions.size()));

        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        TransactionAdapter transactionAdapter = new TransactionAdapter(recent);
        recyclerView.setAdapter(transactionAdapter);


        //UPCOMING BILLS

        List<Transaction> upcomingBills = new ArrayList<>();

        double upcomingTotal = balancesObj.checking;


        Calendar nextWeek = Calendar.getInstance();
        nextWeek.add(Calendar.DAY_OF_YEAR, 7);

        for (Transaction tx : transactions) {
            if ("Spending".equalsIgnoreCase(tx.getTransactionType())) {
                try {
                    Date txDate = sdf.parse(tx.getTransactionDate());
                    Calendar txCal = Calendar.getInstance();
                    txCal.setTime(txDate);

                    if (!txCal.before(today) && txCal.before(nextWeek)) {
                        upcomingBills.add(tx);
                        upcomingTotal -= tx.getMoneyAmountDouble();
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        if (upcomingBills == null) upcomingBills = new ArrayList<>();
        Log.d("UPCOMING", String.valueOf(upcomingTotal));



        RecyclerView upcomingRecycler = root.findViewById(R.id.recyclerViewUpcomingBills);
        upcomingRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        TransactionAdapter upcomingAdapter = new TransactionAdapter(upcomingBills);
        upcomingRecycler.setAdapter(upcomingAdapter);


        AccountBalanceAdapter adapter = new AccountBalanceAdapter(balances, upcomingTotal);
        viewPager.setAdapter(adapter);


        tvMonthYearSpendingsPerCategory= root.findViewById(R.id.tvMonthYear);



        //SPENDINGS PER CATEGORY INITIAL SETUP
        barChart = root.findViewById(R.id.chartSpendingPerCategory);

        APIlib.getInstance().setActiveAnyChartView(barChart);

        bar = AnyChart.column();

        bar.title("Spendings per category");
        bar.tooltip()
                .positionMode(TooltipPositionMode.POINT)
                .anchor(Anchor.CENTER_BOTTOM)
                .position(Position.CENTER_BOTTOM)
                .format("{%Value}€");
        bar.animation(true);
        bar.interactivity().hoverMode(HoverMode.BY_X);




        Map<String, Double> spendingsPerCategory = new HashMap<>();
        List<Transaction> allTransactions = JsonLoader.loadTransactions(getContext());
        sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (Transaction tx : allTransactions) {
            if (!"Spending".equalsIgnoreCase(tx.getTransactionType()) &&
                    !"Savings".equalsIgnoreCase(tx.getTransactionType())) continue;

            try {
                Date txDate = sdf.parse(tx.getTransactionDate());
                Calendar txCal = Calendar.getInstance();
                txCal.setTime(txDate);

                Log.d("MONTHCAL", "Current chart month: " + calendarSpendingsPerCategory.get(Calendar.MONTH));
                if(txCal.get(Calendar.MONTH) == calendarSpendingsPerCategory.get(Calendar.MONTH) &&
                        txCal.get(Calendar.YEAR) == calendarSpendingsPerCategory.get(Calendar.YEAR)){


                    spendingsPerCategory.merge(tx.getCategoryName(), tx.getMoneyAmountDouble(), Double::sum);
                    Log.d("TRANSACT_INSIDE", tx.getCategoryName() + ": " + tx.getMoneyAmount());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Log.d("BAR_CHART", "Filtered entries: " + spendingsPerCategory.size());

        Map<String, String> categoryColorMap = new HashMap<>();
        List<Category> allCategories = JsonLoader.loadCategories(getContext());
        for (Category cat : allCategories) {
            if (spendingsPerCategory.containsKey(cat.getName())) {
                categoryColorMap.put(cat.getName(), String.format("#%06X", (0xFFFFFF & cat.getColor())));
            }
        }

        List<DataEntry> data = new ArrayList<>();
        for (Map.Entry<String, Double> entry : spendingsPerCategory.entrySet()) {
            String category = entry.getKey();
            double amount = entry.getValue();
            String color = categoryColorMap.getOrDefault(category, "#888888");
            Log.d("ENTRY", category + ": " + amount);
            data.add(new CustomDataEntry(category, amount, color));
        }

        Log.d("TRYTRY", "I was here");
        bar.yAxis(0).labels().format("{%Value}€");

        series1 = bar.column(data);
        series1.stroke(null);
        barChart.setChart(bar);




        ImageButton btnPrevMonth = root.findViewById(R.id.btnPrevMonth);
        ImageButton btnNextMonth = root.findViewById(R.id.btnNextMonth);

        //calendarSpendingsPerCategory = Calendar.getInstance();
        updateMonthYear(tvMonthYearSpendingsPerCategory, calendarSpendingsPerCategory);



        //Buttons for Bar chart for expenses per category
        btnPrevMonth.setOnClickListener(v -> {
            calendarSpendingsPerCategory.add(Calendar.MONTH, -1);
            updateMonthYear(tvMonthYearSpendingsPerCategory, calendarSpendingsPerCategory);
            APIlib.getInstance().setActiveAnyChartView(barChart);
            Log.d("UPDATE STATUS","to be updated");
            updateBarChart();
            Log.d("UPDATE STATUS","updated!!!");
        });

        btnNextMonth.setOnClickListener(v -> {
            calendarSpendingsPerCategory.add(Calendar.MONTH, 1);
            updateMonthYear(tvMonthYearSpendingsPerCategory, calendarSpendingsPerCategory);
            APIlib.getInstance().setActiveAnyChartView(barChart);
            updateBarChart();
        });





        // Initialize the line chart
        tvMonthYearExpensesVsIncome = root.findViewById(R.id.tvMonthYear2);

        lineChart = root.findViewById(R.id.chartExpensesVsIncome);
        APIlib.getInstance().setActiveAnyChartView(lineChart);

        ImageButton btnPrevMonthExpensesVsIncome = root.findViewById(R.id.btnPrevMonth2);
        ImageButton btnNextMonthExpensesVsIncome = root.findViewById(R.id.btnNextMonth2);
        calendarExpensesVsIncome = Calendar.getInstance();

        line = AnyChart.line();
        line.title("Cumulative Expenses vs. Income");

        line.tooltip()
                .positionMode(TooltipPositionMode.POINT)
                .anchor(Anchor.CENTER_BOTTOM)
                .position(Position.CENTER_BOTTOM)
                .format("{%Value}€");

        line.interactivity().hoverMode(HoverMode.BY_X);
        line.animation(true);
        line.yAxis(0).labels().format("{%Value}€");

        Linear xScale = Linear.instantiate();
        xScale.minimum(1);
        xScale.maximum(31);
        line.xScale(xScale);
        line.xAxis(0).title("Days").labels().format("{%Value}");



        String currentMonth = android.text.format.DateFormat.format("yyyy-MM", calendarExpensesVsIncome).toString();


        Map<Integer, Double> incomePerDay = new HashMap<>();
        Map<Integer, Double> expensePerDay = new HashMap<>();

        for (Transaction tx : allTransactions) {
            try {
                Date txDate = sdf.parse(tx.getTransactionDate());
                Calendar txCal = Calendar.getInstance();
                txCal.setTime(txDate);

                String txMonth = android.text.format.DateFormat.format("yyyy-MM", txCal).toString();
                if (!txMonth.equals(currentMonth)) continue;

                int day = txCal.get(Calendar.DAY_OF_MONTH);
                double amount = tx.getMoneyAmountDouble();

                if (tx.getTransactionType().equalsIgnoreCase("Income")) {
                    incomePerDay.merge(day, amount, Double::sum);
                } else {
                    expensePerDay.merge(day, amount, Double::sum);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        Calendar now = Calendar.getInstance();
        boolean isCurrentMonth = (
                calendarExpensesVsIncome.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                        calendarExpensesVsIncome.get(Calendar.YEAR) == now.get(Calendar.YEAR)
        );
        int maxDay = isCurrentMonth
                ? now.get(Calendar.DAY_OF_MONTH)
                : calendarExpensesVsIncome.getActualMaximum(Calendar.DAY_OF_MONTH);


        List<DataEntry> incomeData = new ArrayList<>();
        List<DataEntry> expenseData = new ArrayList<>();

        double cumulativeIncome = 0;
        double cumulativeExpenses = 0;

        for (int day = 1; day <= maxDay; day++) {
            cumulativeIncome += incomePerDay.getOrDefault(day, 0.0);
            cumulativeExpenses += expensePerDay.getOrDefault(day, 0.0);

            incomeData.add(new ValueDataEntry(String.valueOf(day), cumulativeIncome));
            expenseData.add(new ValueDataEntry(String.valueOf(day), cumulativeExpenses));
        }

        line.legend().enabled(true);
        line.legend()
                .fontSize(14)
                .padding(10, 10, 10, 10);


        series2 = (Line) line.line(expenseData).name("Expenses").color("#FF5733");
        series3 = (Line) line.line(incomeData).name("Income").color("#33FF57");


        lineChart.setChart(line);






        updateMonthYear(tvMonthYearExpensesVsIncome, calendarExpensesVsIncome);
        btnPrevMonthExpensesVsIncome.setOnClickListener(v -> {
            calendarExpensesVsIncome.add(Calendar.MONTH, -1);
            updateMonthYear(tvMonthYearExpensesVsIncome, calendarExpensesVsIncome);
            APIlib.getInstance().setActiveAnyChartView(lineChart);
            updateLineChart();
        });
        btnNextMonthExpensesVsIncome.setOnClickListener(v -> {
            calendarExpensesVsIncome.add(Calendar.MONTH, 1);
            updateMonthYear(tvMonthYearExpensesVsIncome, calendarExpensesVsIncome);
            APIlib.getInstance().setActiveAnyChartView(lineChart);
            updateLineChart();
        });

        return root;
    }

    private void updateMonthYear(TextView textView,  Calendar calendar) {
        String monthYear = android.text.format.DateFormat.format("MMMM yyyy", calendar).toString();
        textView.setText(monthYear);
    }




    private void updateBarChart() {

        Log.d("CHART_DEBUG", "updateBarChart() called for month: " +
                calendarSpendingsPerCategory.get(Calendar.MONTH));


            Map<String, Double> spendingsPerCategory = new HashMap<>();
            List<Transaction> allTransactions = JsonLoader.loadTransactions(getContext());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            for (Transaction tx : allTransactions) {
                if (!"Spending".equalsIgnoreCase(tx.getTransactionType()) &&
                        !"Savings".equalsIgnoreCase(tx.getTransactionType())) continue;

                try {
                    Date txDate = sdf.parse(tx.getTransactionDate());
                    Calendar txCal = Calendar.getInstance();
                    txCal.setTime(txDate);

                    Log.d("MONTHCAL", "Current chart month: " + calendarSpendingsPerCategory.get(Calendar.MONTH));
                    if( txCal.get(Calendar.MONTH) == calendarSpendingsPerCategory.get(Calendar.MONTH) &&
                            txCal.get(Calendar.YEAR) == calendarSpendingsPerCategory.get(Calendar.YEAR)){
                        spendingsPerCategory.merge(tx.getCategoryName(), tx.getMoneyAmountDouble(), Double::sum);
                        Log.d("TRANSACT_INSIDE", tx.getCategoryName() + ": " + tx.getMoneyAmount());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            Log.d("BAR_CHART", "Filtered entries: " + spendingsPerCategory.size());

            Map<String, String> categoryColorMap = new HashMap<>();
            List<Category> allCategories = JsonLoader.loadCategories(getContext());
            for (Category cat : allCategories) {
                if (spendingsPerCategory.containsKey(cat.getName())) {
                    categoryColorMap.put(cat.getName(), String.format("#%06X", (0xFFFFFF & cat.getColor())));
                }
            }

            List<DataEntry> data = new ArrayList<>();
            for (Map.Entry<String, Double> entry : spendingsPerCategory.entrySet()) {
                String category = entry.getKey();
                double amount = entry.getValue();
                String color = categoryColorMap.getOrDefault(category, "#888888");
                Log.d("ENTRY", category + ": " + amount);
                data.add(new CustomDataEntry(category, amount, color));
            }

            Log.d("GENERATE NUMBER", String.valueOf(Math.random()));

            APIlib.getInstance().setActiveAnyChartView(barChart);


        if (data.isEmpty()) {;
            data.add(new CustomDataEntry("", 0, "#FFFFFF")); // needed or else will load the last bar
        }
        series1.data(data);




    }


    private void updateLineChart() {
        if (line == null) return;

        String currentMonth = android.text.format.DateFormat.format("yyyy-MM", calendarExpensesVsIncome).toString();
        List<Transaction> allTransactions = JsonLoader.loadTransactions(getContext());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        Map<Integer, Double> incomePerDay = new HashMap<>();
        Map<Integer, Double> expensePerDay = new HashMap<>();

        for (Transaction tx : allTransactions) {
            try {
                Date txDate = sdf.parse(tx.getTransactionDate());
                Calendar txCal = Calendar.getInstance();
                txCal.setTime(txDate);

                String txMonth = android.text.format.DateFormat.format("yyyy-MM", txCal).toString();
                if (!txMonth.equals(currentMonth)) continue;

                int day = txCal.get(Calendar.DAY_OF_MONTH);
                double amount = tx.getMoneyAmountDouble();

                if (tx.getTransactionType().equalsIgnoreCase("Income")) {
                    incomePerDay.merge(day, amount, Double::sum);
                } else  {
                    expensePerDay.merge(day, amount, Double::sum);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        Calendar now = Calendar.getInstance();
        boolean isCurrentMonth = (
                calendarExpensesVsIncome.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                        calendarExpensesVsIncome.get(Calendar.YEAR) == now.get(Calendar.YEAR)
        );
        int maxDay = isCurrentMonth
                ? now.get(Calendar.DAY_OF_MONTH)
                : calendarExpensesVsIncome.getActualMaximum(Calendar.DAY_OF_MONTH);


        List<DataEntry> incomeData = new ArrayList<>();
        List<DataEntry> expenseData = new ArrayList<>();

        double cumulativeIncome = 0;
        double cumulativeExpenses = 0;

        for (int day = 1; day <= maxDay; day++) {
            cumulativeIncome += incomePerDay.getOrDefault(day, 0.0);
            cumulativeExpenses += expensePerDay.getOrDefault(day, 0.0);

            incomeData.add(new ValueDataEntry(String.valueOf(day), cumulativeIncome));
            expenseData.add(new ValueDataEntry(String.valueOf(day), cumulativeExpenses));
        }

        // Update the chart
        series2.data(expenseData);
        series3.data(incomeData);
    }


    @Override
    public void onResume() {
        super.onResume();
        APIlib.getInstance().setActiveAnyChartView(barChart);
        updateBarChart();
        APIlib.getInstance().setActiveAnyChartView(lineChart);
        updateLineChart();
    }





    private static class CustomDataEntry extends ValueDataEntry {
        CustomDataEntry(String x, Number value, String color) {
            super(x, value);
            setValue("fill", color); // Set the bar color
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}