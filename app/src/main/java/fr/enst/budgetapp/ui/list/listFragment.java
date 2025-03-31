package fr.enst.budgetapp.ui.list;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import fr.enst.budgetapp.AccountBalanceAdapter;
import fr.enst.budgetapp.R;
import fr.enst.budgetapp.Transaction;
import fr.enst.budgetapp.TransactionAdapter;
import fr.enst.budgetapp.databinding.FragmentListBinding;

public class listFragment extends Fragment {

    private FragmentListBinding binding;
    private TextView tvMonthYear;
    private Calendar calendar;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listViewModel listViewModel =
                new ViewModelProvider(this).get(listViewModel.class);

        binding = FragmentListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textList;
        listViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        tvMonthYear = root.findViewById(R.id.tvMonthYearList);
        ImageButton btnPrevMonth = root.findViewById(R.id.btnPrevMonthList);
        ImageButton btnNextMonth = root.findViewById(R.id.btnNextMonthList);

        // initialize calendar and update the month/year text
        calendar = Calendar.getInstance();
        updateMonthYear();

        btnPrevMonth.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1);
            updateMonthYear();
            //updateChart();
        });

        btnNextMonth.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1);
            updateMonthYear();
            //updateChart();
        });

        // sample data TODO: replace with actual data
        List<Transaction> transactions = Arrays.asList(
                new Transaction("Groceries", "45,00€", "2023-03-31"),
                new Transaction("Groceries", "50,00€", "2023-03-03"),
                new Transaction("Transport", "5,00€", "2023-03-31"),
                new Transaction("Transport", "20,00€", "2023-03-15"),
                new Transaction("Entertainment", "30,00€", "2023-03-10")
        );

        // grouping transactions by date
        HashMap<String, List<Transaction>> transactionsByDate = new HashMap<>();
        for (Transaction transaction : transactions) {
            String date = transaction.getTransactionDate();
            if (!transactionsByDate.containsKey(date)) {
                transactionsByDate.put(date, new ArrayList<>());
            }
            transactionsByDate.get(date).add(transaction);
        }

        // Sort the dates in descending order
        List<String> sortedDates = new ArrayList<>(transactionsByDate.keySet());
        sortedDates.sort(Collections.reverseOrder());

        // parent container
        LinearLayout containerForTransactions = root.findViewById(R.id.containerForTransactions);
        containerForTransactions.removeAllViews();

        // Get today's date as a String
        LocalDate today = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            today = LocalDate.now();
        }
        String todayDate = today.toString();

        for (String date : sortedDates) {
            List<Transaction> dailyTransactions = transactionsByDate.get(date);

            CardView cardView = new CardView(getContext());
            cardView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            cardView.setCardElevation(4);
            cardView.setRadius(10);

            LinearLayout dailyLayout = new LinearLayout(getContext());
            dailyLayout.setOrientation(LinearLayout.VERTICAL);
            dailyLayout.setPadding(20, 20, 20, 20);

            TextView dateHeader = new TextView(getContext());
            if (date.equals(todayDate)) {
                dateHeader.setText("Today");
            } else {
                dateHeader.setText(date);
            }
            dateHeader.setTextSize(18);
            dateHeader.setTextColor(Color.BLACK);

            int backgroundColor = ContextCompat.getColor(getContext(), R.color.light_blueish_gray);
            dateHeader.setBackgroundColor(backgroundColor);

            dateHeader.setPadding(0, 0, 0, 10);
            dailyLayout.addView(dateHeader);

            // Add transactions for this day
            RecyclerView recyclerView = new RecyclerView(getContext());
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            // Adapter for RecyclerView with transactions for that day
            TransactionAdapter transactionAdapter = new TransactionAdapter(dailyTransactions);
            recyclerView.setAdapter(transactionAdapter);


            dailyLayout.addView(recyclerView);
            cardView.addView(dailyLayout);
            containerForTransactions.addView(cardView);
        }

        /*RecyclerView recyclerView = root.findViewById(R.id.recyclerViewToday);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        TransactionAdapter transactionAdapter = new TransactionAdapter(transactions);
        recyclerView.setAdapter(transactionAdapter);*/

        return root;
    }

    private void updateMonthYear() {
        String monthYear = android.text.format.DateFormat.format("MMMM yyyy", calendar).toString();
        tvMonthYear.setText(monthYear);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}