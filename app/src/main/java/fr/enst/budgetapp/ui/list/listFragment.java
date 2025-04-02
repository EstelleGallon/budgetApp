package fr.enst.budgetapp.ui.list;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
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
import fr.enst.budgetapp.DateTransactionsAdapter;
import fr.enst.budgetapp.R;
import fr.enst.budgetapp.Transaction;
import fr.enst.budgetapp.TransactionAdapter;
import fr.enst.budgetapp.databinding.FragmentListBinding;

public class listFragment extends Fragment {

    private FragmentListBinding binding;
    //private TextView tvMonthYear;
    private TextView tvDisplayedMonth;
    private Calendar calendar;
    private String currentMonthYear;
    private List<Pair<String, List<Transaction>>> dateTransactionPairs;

    private RecyclerView outerRecyclerView;
    private DateTransactionsAdapter dateTransactionsAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listViewModel listViewModel =
                new ViewModelProvider(this).get(listViewModel.class);

        binding = FragmentListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        //tvMonthYear = root.findViewById(R.id.tvMonthYearList);
        tvDisplayedMonth = root.findViewById(R.id.tvDisplayedMonth);
        ImageButton btnPrevMonth = root.findViewById(R.id.btnPrevMonthList);
        ImageButton btnNextMonth = root.findViewById(R.id.btnNextMonthList);


        // sample data TODO: replace with actual data
        List<Transaction> transactions = Arrays.asList(
                new Transaction("Groceries", "45,00€", "2025-03-31"),
                new Transaction("Groceries", "50,00€", "2025-03-03"),
                new Transaction("Transport", "5,00€", "2025-03-31"),
                new Transaction("Transport", "20,00€", "2025-03-15"),
                new Transaction("Entertainment", "30,00€", "2025-03-10"),
                new Transaction("Holidays", "1000,00€", "2025-04-10"),
                new Transaction("Entertainment", "30,00€", "2025-04-15")
        );

        // initialize calendar and update the month/year text
        calendar = Calendar.getInstance();
        updateMonthYear();
        updateTransactions(transactions);

        btnPrevMonth.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1);
            updateMonthYear();
            updateTransactions(transactions);
        });

        btnNextMonth.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1);
            updateMonthYear();
            updateTransactions(transactions);
        });


        // setup recyclerview
        outerRecyclerView = root.findViewById(R.id.outerRecyclerView);
        outerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dateTransactionsAdapter = new DateTransactionsAdapter(dateTransactionPairs);
        outerRecyclerView.setAdapter(dateTransactionsAdapter);

        // add transaction button
        ImageView btnAddTransaction = root.findViewById(R.id.btnAddTransaction);
        btnAddTransaction.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_listFragment_to_newTransactionFragment);
        });


        return root;
    }

    private void updateMonthYear() {
        String monthYear = android.text.format.DateFormat.format("MMMM yyyy", calendar).toString();
        //tvMonthYear.setText(monthYear);
        tvDisplayedMonth.setText(monthYear);
        currentMonthYear = android.text.format.DateFormat.format("yyyy-MM", calendar).toString();

    }

    private void updateTransactions(List<Transaction> transactions) {
        // prepare data:

        // 1) only keep transactions displayed month
        List<Transaction> currentTransactions = Transaction.filterYearAndMonth(transactions, currentMonthYear);

        // 2) grouping transactions by date
        HashMap<String, List<Transaction>> transactionsByDate = Transaction.groupTransactionsByDate(currentTransactions);

        // 3) sort dates by descending order
        List<String> sortedDates = new ArrayList<>(transactionsByDate.keySet());
        sortedDates.sort(Collections.reverseOrder());

        // 4) create list for recycler view
        dateTransactionPairs = new ArrayList<>();
        for (String date : sortedDates) {
            dateTransactionPairs.add(new Pair<>(date, transactionsByDate.get(date)));
        }

        // 5) update view
        dateTransactionsAdapter = new DateTransactionsAdapter(dateTransactionPairs);
        if (outerRecyclerView != null)
            outerRecyclerView.setAdapter(dateTransactionsAdapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}