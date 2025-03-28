package fr.enst.budgetapp.ui.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import fr.enst.budgetapp.databinding.FragmentBudgetBinding;

public class budgetFragment extends Fragment {

    private FragmentBudgetBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        budgetViewModel budgetViewModel =
                new ViewModelProvider(this).get(budgetViewModel.class);

        binding = FragmentBudgetBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textBudget;
        budgetViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}