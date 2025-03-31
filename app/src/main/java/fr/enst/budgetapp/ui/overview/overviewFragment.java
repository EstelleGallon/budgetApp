package fr.enst.budgetapp.ui.overview;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import fr.enst.budgetapp.AccountBalanceAdapter;
import fr.enst.budgetapp.R;
import fr.enst.budgetapp.Transaction;
import fr.enst.budgetapp.TransactionAdapter;
import fr.enst.budgetapp.databinding.FragmentOverviewBinding;

public class overviewFragment extends Fragment {

    private FragmentOverviewBinding binding;
    private Calendar calendar;
    private TextView tvMonthYear;
    private AnyChartView barChart;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        overviewViewModel overviewViewModel =
                new ViewModelProvider(this).get(overviewViewModel.class);

        binding = FragmentOverviewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize the ViewPager2 for account balances
        ViewPager2 viewPager = root.findViewById(R.id.vpAccountBalances);

        // Sample data for account balances (TODO: replace with actual data)
        List<Pair<String, String>> balances = Arrays.asList(
                new Pair<>("Checking Account", "1 200,00€"),
                new Pair<>("Savings Account", "5 000,00€")
        );

        // Set up the ViewPager2 adapter
        AccountBalanceAdapter adapter = new AccountBalanceAdapter(balances);
        viewPager.setAdapter(adapter);

        // Initialize the RecyclerView for last transactions
        List<Transaction> transactions = Arrays.asList(
                new Transaction("Groceries", "50,00€", "2023-10-01"),
                new Transaction("Transport", "20,00€", "2023-10-02"),
                new Transaction("Entertainment", "30,00€", "2023-10-03")
        );

        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        TransactionAdapter transactionAdapter = new TransactionAdapter(transactions);
        recyclerView.setAdapter(transactionAdapter);

        // Initialize the bar chart
        tvMonthYear = root.findViewById(R.id.tvMonthYear);
        barChart = root.findViewById(R.id.chartSpendingPerCategory);
        ImageButton btnPrevMonth = root.findViewById(R.id.btnPrevMonth);
        ImageButton btnNextMonth = root.findViewById(R.id.btnNextMonth);

        calendar = Calendar.getInstance();
        updateMonthYear();

        btnPrevMonth.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1);
            updateMonthYear();
            updateChart();
        });

        btnNextMonth.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1);
            updateMonthYear();
            updateChart();
        });

        // Set up the bar chart with hardcoded values
        updateChart();

        return root;
    }

    private void updateMonthYear() {
        String monthYear = android.text.format.DateFormat.format("MMMM yyyy", calendar).toString();
        tvMonthYear.setText(monthYear);
    }

    private void updateChart() {
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