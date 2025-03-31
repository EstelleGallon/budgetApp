package fr.enst.budgetapp.ui.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import fr.enst.budgetapp.R;
import fr.enst.budgetapp.SavingGoal;
import fr.enst.budgetapp.SavingGoalAdapter;
import fr.enst.budgetapp.databinding.FragmentBudgetBinding;

public class budgetFragment extends Fragment {

    private FragmentBudgetBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        budgetViewModel budgetViewModel =
                new ViewModelProvider(this).get(budgetViewModel.class);

        binding = FragmentBudgetBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        // Sample data for saving goals TODO: use actual data
        List<SavingGoal> savingGoals = Arrays.asList(
                new SavingGoal("Vacation Fund", 50, "2023-12-31", 1000),
                new SavingGoal("Emergency Fund", 30, "2024-06-30", 5000),
                new SavingGoal("New Car", 70, "2025-01-01", 20000)
        );

        // Set up RecyclerView
        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewSavingGoals);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SavingGoalAdapter adapter = new SavingGoalAdapter(savingGoals);
        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}