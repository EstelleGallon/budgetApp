package fr.enst.budgetapp.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import fr.enst.budgetapp.databinding.FragmentListBinding;

public class listFragment extends Fragment {

    private FragmentListBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listViewModel listViewModel =
                new ViewModelProvider(this).get(listViewModel.class);

        binding = FragmentListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textList;
        listViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}