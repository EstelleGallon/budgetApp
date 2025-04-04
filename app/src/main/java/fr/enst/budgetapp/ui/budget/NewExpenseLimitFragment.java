package fr.enst.budgetapp.ui.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import fr.enst.budgetapp.R;

public class NewExpenseLimitFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_expense_limit, container, false);


        // --- Handle Back button ---
        Button btnBack = root.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());


        // --- Handle Save button ---
        Button btnSave = root.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            // TODO: Save the new expense limit
            Navigation.findNavController(v).navigate(R.id.action_newExpenseLimitFragment_to_budgetFragment);
        });

        // --- Handle Category navigation
        ImageView ivCategory = root.findViewById(R.id.ivChooseCategory);
        ivCategory.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("PREVIOUS_FRAGMENT", "newExpenseLimit");
            Navigation.findNavController(v).navigate(R.id.action_newExpenseLimitFragment_to_categoriesFragment, bundle);
        });

        TextView tvCategory = root.findViewById(R.id.tvChooseCategory);
        tvCategory.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("PREVIOUS_FRAGMENT", "newExpenseLimit");
            Navigation.findNavController(v).navigate(R.id.action_newExpenseLimitFragment_to_categoriesFragment, bundle);
        });
        // --- Category Selection ---
        //TextView tvCategory = root.findViewById(R.id.tvChooseCategory);

        if (getArguments() != null) {
            String categoryName = getArguments().getString("CATEGORY_NAME");
            if (categoryName != null)
                tvCategory.setText(categoryName);
        }


        return root;
    }
}