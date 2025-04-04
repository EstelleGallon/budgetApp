package fr.enst.budgetapp.ui.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import fr.enst.budgetapp.R;

public class EditExpenseLimitFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_expense_limit, container, false);

        // Initialize UI elements
        EditText etEditExpenseLimitAmount = root.findViewById(R.id.etEditExpenseLimitAmount);
        EditText etEditExpenseLimitDate = root.findViewById(R.id.etEditExpenseLimitDate);
        EditText etEditExpenseLimitNotes = root.findViewById(R.id.etEditExpenseLimitNotes);

        Button btnBack = root.findViewById(R.id.btnBackBudget);
        Button btnDelete = root.findViewById(R.id.btnDeleteExpenseLimit);
        Button btnSave = root.findViewById(R.id.btnSaveExpenseLimit);

        // --- Handle navigation to Category page ---
        ImageView ivChooseCategory = root.findViewById(R.id.ivEditChooseCategory);
        ivChooseCategory.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("PREVIOUS_FRAGMENT", "editExpenseLimit");
            Navigation.findNavController(v).navigate(R.id.action_editExpenseLimitFragment_to_categoriesFragment, bundle);
        });

        TextView tvCategoryName = root.findViewById(R.id.tvEditChooseCategory);
        tvCategoryName.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("PREVIOUS_FRAGMENT", "editExpenseLimit");
            Navigation.findNavController(v).navigate(R.id.action_editExpenseLimitFragment_to_categoriesFragment, bundle);
        });


        // --- Handle Back button ---
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // --- Handle Delete button ---
        btnDelete.setOnClickListener(v -> {
            // TODO: Implement delete functionality
            Navigation.findNavController(v).navigate(R.id.action_editExpenseLimitFragment_to_budgetFragment);
        });

        // --- Handle Save button ---
        btnSave.setOnClickListener(v -> {
            // TODO: Save the edited expense limit
            Navigation.findNavController(v).navigate(R.id.action_editExpenseLimitFragment_to_budgetFragment);
        });

        // --- Category Selection ---

        if (getArguments() != null) {
            String categoryName = getArguments().getString("CATEGORY_NAME");
            if (categoryName != null)
                tvCategoryName.setText(categoryName);
        }

        return root;
    }
}