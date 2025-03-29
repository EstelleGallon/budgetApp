package fr.enst.budgetapp.ui.overview;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.Arrays;
import java.util.List;

import fr.enst.budgetapp.AccountBalanceAdapter;
import fr.enst.budgetapp.R;
import fr.enst.budgetapp.Transaction;
import fr.enst.budgetapp.TransactionAdapter;
import fr.enst.budgetapp.databinding.FragmentOverviewBinding;

public class overviewFragment extends Fragment {

    private FragmentOverviewBinding binding;

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

        // Sample data for last transactions (TODO: replace with actual data)
        List<Transaction> transactions = Arrays.asList(
                new Transaction("Grocery Store", "50,00€", "03/01/2023"),
                new Transaction("Online Shopping", "120,00€", "02/01/2023"),
                new Transaction("Coffee Shop", "5,00€", "01/01/2023")
        );


        // Sample data for upcoming bills (TODO: replace with actual data)
        List<Transaction> upcomingBills = Arrays.asList(
                new Transaction("Rent", "800,00€", "01/02/2023"),
                new Transaction("Netflix Subscription", "15,00€", "05/02/2023"),
                new Transaction("Electricity Bill", "120,00€", "10/02/2023")
        );

        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new TransactionAdapter(transactions));

        // Initialize RecyclerView for upcoming bills
        RecyclerView recyclerViewUpcomingBills = root.findViewById(R.id.recyclerViewUpcomingBills);
        recyclerViewUpcomingBills.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewUpcomingBills.setAdapter(new TransactionAdapter(upcomingBills));

        AccountBalanceAdapter adapter = new AccountBalanceAdapter(balances);
        viewPager.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}