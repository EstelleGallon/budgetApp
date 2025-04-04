package fr.enst.budgetapp.ui.budget;

import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import fr.enst.budgetapp.ExpenseLimit;
import fr.enst.budgetapp.ExpenseLimitAdapter;
import fr.enst.budgetapp.R;
import fr.enst.budgetapp.SavingGoal;
import fr.enst.budgetapp.SavingGoalAdapter;
import fr.enst.budgetapp.databinding.FragmentBudgetBinding;

public class budgetFragment extends Fragment {

    private FragmentBudgetBinding binding;
    private AnyChartView barChart;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        budgetViewModel budgetViewModel =
                new ViewModelProvider(this).get(budgetViewModel.class);

        binding = FragmentBudgetBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // ---- Navigation to New Saving Goal ----
        ImageView btnAddSavingGoal = root.findViewById(R.id.btnAddSavingGoal);
        btnAddSavingGoal.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_budgetFragment_to_newSavingGoalFragment);
        });

        // --- Navigation to New Expense Limit ----
        ImageView btnAddExpenseLimit = root.findViewById(R.id.btnAddExpenseLimit);
        btnAddExpenseLimit.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_budgetFragment_to_newExpenseLimitFragment);
        });


        // Sample data for saving goals TODO: use actual data
        List<SavingGoal> savingGoals = Arrays.asList(
                new SavingGoal("1","Vacation Fund", 50, "2023-12-31", 1000),
                new SavingGoal("2", "Emergency Fund", 30, "2024-06-30", 5000),
                new SavingGoal("3", "New Car", 70, "2025-01-01", 20000)
        );

        // Set up RecyclerView for saving goals
        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewSavingGoals);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SavingGoalAdapter adapter = new SavingGoalAdapter(savingGoals);
        recyclerView.setAdapter(adapter);


        // Sample data for expense limits TODO: use actual data
        List<ExpenseLimit> expenseLimits = Arrays.asList(
                new ExpenseLimit("Food", new boolean[]{true, false, false, true, false, false, true, false, false, true, false, false}),
                new ExpenseLimit("Transport", new boolean[]{false, false, true, false, false, true, false, false, true, false, false, true}),
                new ExpenseLimit("Entertainment", new boolean[]{false, true, false, false, true, false, false, true, false, false, true, false})
        );

        // Set up RecyclerView for expense limits
        RecyclerView recyclerViewExpenseLimits = root.findViewById(R.id.recyclerViewExpenseLimits);
        recyclerViewExpenseLimits.setLayoutManager(new LinearLayoutManager(getContext()));
        ExpenseLimitAdapter expenseLimitAdapter = new ExpenseLimitAdapter(expenseLimits, getContext());
        recyclerViewExpenseLimits.setAdapter(expenseLimitAdapter);

        recyclerViewExpenseLimits.setNestedScrollingEnabled(false); // Disable nested scrolling


        // ------- Expense Limit -------
        barChart = root.findViewById(R.id.chartExpenseLimit);
        APIlib.getInstance().setActiveAnyChartView(barChart);
        setupBarChart();

        Calendar calendar = Calendar.getInstance();
        TextView tvMonthYear = root.findViewById(R.id.tvMonthYear);
        ImageButton btnPrevMonth = root.findViewById(R.id.btnPrevMonth);
        ImageButton btnNextMonth = root.findViewById(R.id.btnNextMonth);

        btnPrevMonth.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1);
            updateMonthYear(tvMonthYear, calendar);
            APIlib.getInstance().setActiveAnyChartView(barChart);
            setupBarChart();
        });

        btnNextMonth.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1);
            updateMonthYear(tvMonthYear, calendar);
            APIlib.getInstance().setActiveAnyChartView(barChart);
            setupBarChart();
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

    private void setupBarChart() {
        // Create a Cartesian chart
        Cartesian cartesian = AnyChart.column();

        // Set the title of the chart
        cartesian.title("Expenses per Category");

        // Configure tooltips
        cartesian.tooltip()
                .positionMode(TooltipPositionMode.POINT)
                .anchor(Anchor.CENTER_BOTTOM)
                .position(Position.CENTER_BOTTOM)
                .format("Spent: {%Value}€");

        // Enable interactivity
        cartesian.interactivity().hoverMode(HoverMode.BY_X);
        cartesian.animation(true);

        // Set axis titles
        //cartesian.xAxis(0).title("Categories");
        //cartesian.yAxis(0).title("Amount (€)");
        cartesian.yAxis(0).labels().format("{%Value}€");

        // Create separate data lists for each series
        List<DataEntry> spendingLimitData = new ArrayList<>();
        List<DataEntry> actualSpentData = new ArrayList<>();

        // Add data for each category
        spendingLimitData.add(new ValueDataEntry("Food", 300));
        actualSpentData.add(new ValueDataEntry("Food", 250));

        spendingLimitData.add(new ValueDataEntry("Transport", 150));
        actualSpentData.add(new ValueDataEntry("Transport", 100));

        spendingLimitData.add(new ValueDataEntry("Entertainment", 200));
        actualSpentData.add(new ValueDataEntry("Entertainment", 180));

        // Add series for "Spending Limit"
        Column spendingLimitColumn = cartesian.column(spendingLimitData);
        spendingLimitColumn.name("Spending Limit").color("#FF5733");

        // Add series for "Actual Spent"
        Column actualSpentColumn = cartesian.column(actualSpentData);
        actualSpentColumn.name("Actual Spent").color("#33FF57");

        // Configure the legend
        cartesian.legend()
                .enabled(true)
                .position("top")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);

        // Attach the chart to the AnyChartView
        barChart.setChart(cartesian);
    }
}