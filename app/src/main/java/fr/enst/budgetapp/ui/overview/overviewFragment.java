package fr.enst.budgetapp.ui.overview;

import android.os.Bundle;
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
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.scales.Linear;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import fr.enst.budgetapp.AccountBalanceAdapter;
import fr.enst.budgetapp.Balances;
import fr.enst.budgetapp.JsonLoader;
import fr.enst.budgetapp.R;
import fr.enst.budgetapp.Transaction;
import fr.enst.budgetapp.TransactionAdapter;
import fr.enst.budgetapp.databinding.FragmentOverviewBinding;

public class overviewFragment extends Fragment {

    private FragmentOverviewBinding binding;
    private Calendar calendarSpendingsPerCategory;
    private Calendar calendarExpensesVsIncome;

    private TextView tvMonthYearSpendingsPerCategory;
    private TextView tvMonthYearExpensesVsIncome;

    private AnyChartView barChart;
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


        /*
        // Sample data for account balances (TODO: replace with actual data)
        List<Pair<String, String>> balances = Arrays.asList(
                new Pair<>("Checking Account", "1 200,00€"),
                new Pair<>("Savings Account", "5 000,00€")
        );

        */

        // Set up the ViewPager2 adapter
        AccountBalanceAdapter adapter = new AccountBalanceAdapter(balances);
        viewPager.setAdapter(adapter);

        /*
        // Initialize the RecyclerView for last transactions
        List<Transaction> transactions = Arrays.asList(
                new Transaction("Groceries", "50,00€", "2023-10-01"),
                new Transaction("Transport", "20,00€", "2023-10-02"),
                new Transaction("Entertainment", "30,00€", "2023-10-03")
        );

         */

        //List<Transaction> transactions = Arrays.asList();


        //The transactions JSON file in the assets folder is the initial setup. Once transactions
        // are added, they are stored on the device's internal storage
        // at: data/data/<your.app.package>/files/transactions.json" that can be accessed from
        //View -> Tool Windows -> Device Explorer
        List<Transaction> transactions = JsonLoader.loadTransactions(getContext());
        if (transactions == null) transactions = new ArrayList<>();

        Collections.sort(transactions, (t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()));

        //Take the 3 latest transactions to display in the overview page
        List<Transaction> recent = transactions.subList(0, Math.min(3, transactions.size()));

        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        TransactionAdapter transactionAdapter = new TransactionAdapter(recent);
        recyclerView.setAdapter(transactionAdapter);

        // Initialize the bar chart
        tvMonthYearSpendingsPerCategory= root.findViewById(R.id.tvMonthYear);
        barChart = root.findViewById(R.id.chartSpendingPerCategory);
        APIlib.getInstance().setActiveAnyChartView(barChart);

        ImageButton btnPrevMonth = root.findViewById(R.id.btnPrevMonth);
        ImageButton btnNextMonth = root.findViewById(R.id.btnNextMonth);

        calendarSpendingsPerCategory = Calendar.getInstance();
        updateMonthYear(tvMonthYearSpendingsPerCategory, calendarSpendingsPerCategory);

        btnPrevMonth.setOnClickListener(v -> {
            calendarSpendingsPerCategory.add(Calendar.MONTH, -1);
            updateMonthYear(tvMonthYearSpendingsPerCategory, calendarSpendingsPerCategory);
            APIlib.getInstance().setActiveAnyChartView(barChart);
            updateBarChart();
        });

        btnNextMonth.setOnClickListener(v -> {
            calendarSpendingsPerCategory.add(Calendar.MONTH, 1);
            updateMonthYear(tvMonthYearSpendingsPerCategory, calendarSpendingsPerCategory);
            APIlib.getInstance().setActiveAnyChartView(barChart);
            updateBarChart();
        });

        // Set up the bar chart with hardcoded values
        updateBarChart();


        // Initialize the line chart
        tvMonthYearExpensesVsIncome = root.findViewById(R.id.tvMonthYear2);
        lineChart = root.findViewById(R.id.chartExpensesVsIncome);
        APIlib.getInstance().setActiveAnyChartView(lineChart);
        ImageButton btnPrevMonthExpensesVsIncome = root.findViewById(R.id.btnPrevMonth2);
        ImageButton btnNextMonthExpensesVsIncome = root.findViewById(R.id.btnNextMonth2);
        calendarExpensesVsIncome = Calendar.getInstance();

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

        updateLineChart();


        return root;
    }

    private void updateMonthYear(TextView textView,  Calendar calendar) {
        String monthYear = android.text.format.DateFormat.format("MMMM yyyy", calendar).toString();
        textView.setText(monthYear);
    }

    private void updateLineChart(){
        // Create a line chart
        Cartesian line = AnyChart.line();

        // Set the title of the chart
        line.title("Cumulative Expenses vs. Income");

        // Configure tooltips
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

        // Hardcoded data for testing
        List<DataEntry> expensesData = new ArrayList<>();
        expensesData.add(new ValueDataEntry("1", 50));
        expensesData.add(new ValueDataEntry("2", 100));
        expensesData.add(new ValueDataEntry("3", 150));
        expensesData.add(new ValueDataEntry("4", 200));

        List<DataEntry> incomeData = new ArrayList<>();
        incomeData.add(new ValueDataEntry("1", 100));
        incomeData.add(new ValueDataEntry("2", 200));
        incomeData.add(new ValueDataEntry("3", 300));
        incomeData.add(new ValueDataEntry("4", 400));


        // Add series for expenses and income
        line.line(expensesData).name("Expenses").color("#FF5733");
        line.line(incomeData).name("Income").color("#33FF57");

        // Attach the chart to the AnyChartView
        lineChart.setChart(line);
    }

    private void updateBarChart() {
        // Create a bar chart
        Cartesian bar = AnyChart.column();

        // Set the title of the chart
        bar.title("Spendings per category");

        // Configure tooltips
        bar.tooltip()
                .positionMode(TooltipPositionMode.POINT)
                .anchor(Anchor.CENTER_BOTTOM)
                .position(Position.CENTER_BOTTOM)
                .format("{%Value}€");


        bar.animation(true);
        // Enable interactivity
        bar.interactivity().hoverMode(HoverMode.BY_X);

        // Hardcoded data for testing
        List<DataEntry> data = new ArrayList<>();
        data.add(new CustomDataEntry("Food", 250, "#FF5733")); // Category: Food, Amount: 250, Color: Red
        data.add(new CustomDataEntry("Transport", 150, "#33FF57")); // Category: Transport, Amount: 150, Color: Green
        data.add(new CustomDataEntry("Entertainment", 100, "#3357FF")); // Category: Entertainment, Amount: 100, Color: Blue
        data.add(new CustomDataEntry("Utilities", 200, "#FFC300")); // Category: Utilities, Amount: 200, Color: Yellow

        // Set the data to the chart
        bar.data(data);
        bar.yAxis(0).labels().format("{%Value}€");
        // Attach the chart to the AnyChartView
        barChart.setChart(bar);
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