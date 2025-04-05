package fr.enst.budgetapp.ui.list;

import android.os.Bundle;
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

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import fr.enst.budgetapp.DateTransactionsAdapter;
import fr.enst.budgetapp.JsonLoader;
import fr.enst.budgetapp.R;
import fr.enst.budgetapp.Transaction;
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

    private List<Transaction> transactions;


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


        /*
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

        */

        //List<Transaction> transactions = Arrays.asList();

        // setup recyclerview
        outerRecyclerView = root.findViewById(R.id.outerRecyclerView);
        outerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        transactions = JsonLoader.loadTransactions(getContext());
        if (transactions == null) {
            transactions = new ArrayList<>();
        }
        JsonLoader.recomputeBalancesFromTransactions(getContext());


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




        //dateTransactionsAdapter = new DateTransactionsAdapter(dateTransactionPairs);
        //outerRecyclerView.setAdapter(dateTransactionsAdapter);

        // add transaction button
        ImageView btnAddTransaction = root.findViewById(R.id.btnAddTransaction);
        btnAddTransaction.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("PREVIOUS_MENU", "list");
            Navigation.findNavController(v).navigate(R.id.action_listFragment_to_newTransactionFragment, bundle);
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

        TextView tvIncomeMonth = binding.incomeSpendingsHeader.tvIncomeMonth;
        TextView tvSpendingsMonth = binding.incomeSpendingsHeader.tvSpendingsMonth;


        double incomeTotal = 0;
        double spendingTotal = 0;

        Date today = new Date();

        for (Transaction tx : currentTransactions) {
            double amount = tx.getMoneyAmountDouble();

            Date txDate = null;
            try {
                txDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(tx.getTransactionDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }


            if (tx.getTransactionType().equalsIgnoreCase("Income")) {
                incomeTotal += amount;
            } else if ((tx.getTransactionType().equalsIgnoreCase("Spending") || tx.getTransactionType().equalsIgnoreCase("Savings"))) {
                spendingTotal += amount;
            }
        }


        DecimalFormat format = new DecimalFormat("#,##0.00€");

        if (tvIncomeMonth != null) {
            tvIncomeMonth.setText(format.format(incomeTotal));
        }
        if (tvSpendingsMonth != null) {
            tvSpendingsMonth.setText(format.format(spendingTotal));
        }


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