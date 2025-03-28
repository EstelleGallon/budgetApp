package fr.enst.budgetapp.ui.overview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import fr.enst.budgetapp.databinding.FragmentOverviewBinding;

public class overviewFragment extends Fragment {

    private FragmentOverviewBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        overviewViewModel overviewViewModel =
                new ViewModelProvider(this).get(overviewViewModel.class);

        binding = FragmentOverviewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textOverview;
        overviewViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}