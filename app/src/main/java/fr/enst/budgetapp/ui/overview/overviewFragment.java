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
import androidx.viewpager2.widget.ViewPager2;

import java.util.Arrays;
import java.util.List;

import fr.enst.budgetapp.AccountBalanceAdapter;
import fr.enst.budgetapp.R;
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